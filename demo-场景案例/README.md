# 场景案例



## 秒杀场景

### 需求

如下：

- 100万个商品同时被秒杀。
- 下单接口10+万 qps（下单成功和下单不成功的总 qps）
- 下单接口10万 tps（下单成功的总 tps）



### 测试



#### 阿里云测试

1. 创建管理实例的镜像

   >提醒：配置为 `1C4G`，普通硬盘 `20G`

   克隆 `demonstration` 项目到实例中

   ```sh
   git clone --depth 1 --branch main https://github.com/dexterleslie1/demonstration.git
   ```

   安装 `sshpass` 程序

   ```sh
   sudo apt install sshpass
   ```

   参考本站 <a href="/ansible/README.html#安装" target="_blank">链接</a> 安装 `Ansible`

   使用 `Ansible` 自动配置管理实例，提醒：此过程需要耐心等待，因为安装 `xrdp、wrk` 过程有点长。

   ```sh
   ansible-playbook playbook-os-management-config.yml --inventory inventory.ini
   ```

   配置完毕后，使用 `节省停机模式` 关闭实例并创建名为 `bm-template-management` 的自定义镜像。

2. 创建测试节点实例的镜像

   >提醒：配置为 `1C4G`，普通硬盘 `20G`

   安装 `Docker`

   ```sh
   # 安装 dcli 工具
   sudo rm -f /usr/bin/dcli && sudo curl https://fut001.oss-cn-hangzhou.aliyuncs.com/dcli/dcli-linux-x86_64 --output /usr/bin/dcli && sudo chmod +x /usr/bin/dcli
   
   # 同步最新源
   sudo apt update
   
   # 安装 Docker
   sudo dcli docker install
   ```

   配置完毕后，使用 `节省停机模式` 关闭实例并创建名为 `bm-template-node` 的自定义镜像。

3. 使用 `ROS` 创建相关测试实例

   复制 `ros-aliyun.yml` 内容到阿里云控制创建资源栈并等待资源创建完毕。

4. 配置测试节点的系统参数

   使用 `rdp` 客户端远程登录 `bm-management` 管理主机，切换到 `demonstration/demo-场景案例/demo-flash-sale` 目录，执行下面命令配置测试节点的系统参数：

   ```sh
   ansible-playbook playbook-os-test-node-config.yml --inventory inventory.ini
   ```

5. 配置测试节点的 `deployer`

   在管理主机中执行下面命令配置测试节点的 `deployer`：

   ```sh
   ansible-playbook playbook-deployer-config.yml --inventory inventory.ini
   ```

6. 启动所有测试服务

   在管理主机中执行下面命令启动所有测试服务：

   ```sh
   ansible-playbook playbook-service-start.yml --inventory inventory.ini
   ```

   等待所有测试服务启动完毕后，初始化商品数据：

   ```sh
   curl 10.0.1.30:8080/api/v1/initProduct
   ```

   测试服务是否正常运行：

   ```sh
   curl 10.0.1.40/api/v1/order/create
   ```

7. 实施压力测试

   访问 `RocketMQ` 的监控 `http://10.0.1.12:8080`

   访问 `Prometheus` 监控 `http://10.0.1.10:3000`，帐号 `admin`，密码 `admin`

   测试：

   ```sh
   wrk -t8 -c4096 -d60s --latency --timeout 30 http://10.0.1.40/api/v1/order/create
   ```

8. 销毁所有测试服务

   在管理主机中执行下面命令销毁所有测试服务：

   ```sh
   ansible-playbook playbook-service-destroy.yml --inventory inventory.ini
   ```

   