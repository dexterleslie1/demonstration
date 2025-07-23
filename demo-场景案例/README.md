# 场景案例



## 秒杀场景

### 需求

如下：

- 下单接口 `10+` 万 `qps`（下单成功和下单不成功的总 `qps`）
- 每秒发放 `32` 个普通商品，`96` 个秒杀商品，每秒发放商品总数 `38400` 个（`(32+96)*300=38400` 个）。
- 支持高并发根据用户 `ID` 、根据商家 `ID` 查询订单列表。
- 支持高并发根据订单 `ID`、根据订单 `ID` 列表、根据商品 `ID`、根据商品 `ID` 列表查询订单或者商品列表。



### `todo`

- 升级 `JDK` 使用虚拟线程以提升性能。
- 使用 `dubbo` 提升性能。
- 限制流量保护系统稳定运行。
- 数据库和 `Cassandra` 海量数据快速自检核对。
- 商品推荐算法实现。
- 热点数据缓存以提升性能。
- 使用 `TiDB` 简化目前的架构。
- 支持系统所有数据的读取性能横向扩展。
- 支持 `k8s` 平台部署。
- 系统掉电数据一致性解决。



### 测试



#### 阿里云测试

1. 创建管理实例的镜像

   >提醒：配置为 `1C4G`，普通硬盘 `40G`（不需要 `ssd` 硬盘，因为创建实例镜像）

   克隆 `demonstration` 项目到实例中

   ```sh
   git clone --depth 1 --branch main https://gitee.com/dexterleslie/demonstration.git
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

   >提醒：配置为 `1C4G`，普通硬盘 `40G`（不需要 `ssd` 硬盘，因为创建实例镜像）

   安装 `Docker`

   ```sh
   # 安装 dcli 工具
   sudo rm -f /usr/bin/dcli && sudo curl https://fut001.oss-cn-hangzhou.aliyuncs.com/dcli/dcli-linux-x86_64 --output /usr/bin/dcli && sudo chmod +x /usr/bin/dcli
   
   # 同步最新源
   sudo apt update
   
   # 安装 Docker
   sudo dcli docker install
   ```

   配置测试节点的系统参数

   - 使用 `rdp` 客户端远程登录 `bm-management` 管理主机，切换到 `demonstration/demo-场景案例/demo-flash-sale` 目录

   - 修改 `inventory.ini` 配置中的 `[test-node]` 配置指向测试节点实例

     ```properties
     [test-node]
     10.0.1.x
     ```

   - 执行下面命令配置测试节点：

     ```sh
     ansible-playbook playbook-os-test-node-config.yml --inventory inventory.ini
     ```

   配置完毕后，使用 `节省停机模式` 关闭实例并创建名为 `bm-template-node` 的自定义镜像。

3. 使用 `ROS` 创建相关测试实例

   复制 `ros-aliyun.yml` 内容到阿里云控制创建资源栈并等待资源创建完毕。

4. 配置测试节点的 `deployer`

   根据实例的配置修改 `inventory.ini` 如下的配置：

   ```properties
   # 数据库服务变量
   [db:vars]
   # 数据库 innodb_buffer_pool_size 配置
   innodb_buffer_pool_size=4g
   
   # crond 服务变量
   [crond:vars]
   java_opts=-Xmx6g
   
   # api 服务变量
   [api:vars]
   java_opts=-Xmx4g
   
   # rocketmq 服务变量
   [rocketmq:vars]
   kafka_heap_opts=-Xms2g -Xmx2g
   ```

   在管理主机中执行下面命令配置测试节点的 `deployer`：

   ```sh
   ansible-playbook playbook-deployer-config.yml --inventory inventory.ini
   ```

5. 启动所有测试服务

   在管理主机中执行下面命令启动所有测试服务：

   ```sh
   ansible-playbook playbook-service-start.yml --inventory inventory.ini
   ```

   测试服务是否正常运行：

   ```sh
   curl 10.0.1.40/api/v1/order/create
   ```

6. 访问应用界面测试功能 `http://10.0.1.10`

7. 实施压力测试

   访问 `Prometheus` 监控 `http://10.0.1.10:3000`，帐号 `admin`，密码 `admin`

   普通方式下单测试：

   ```sh
   wrk -t8 -c2048 -d60s --latency --timeout 30 http://10.0.1.40/api/v1/order/create
   ```

   秒杀方式下单测试：

   ```sh
   wrk -t8 -c2048 -d60s --latency --timeout 30 http://10.0.1.40/api/v1/order/createFlashSale
   ```

8. 销毁所有测试服务

   在管理主机中执行下面命令销毁所有测试服务：

   ```sh
   ansible-playbook playbook-service-destroy.yml --inventory inventory.ini
   ```




## 随机 `ID` 选择器

>背景：在海量订单或者商品数据场景中，需要模拟根据订单 `ID` 或者商品 `ID` 列表查询订单或者商品信息。此时需要借助随机 `ID` 选择器按照一定的频率随机抽取 `ID` 列表。
>
>详细用法请参考本站 [示例](https://gitee.com/dexterleslie/demonstration/tree/main/demo-%E5%9C%BA%E6%99%AF%E6%A1%88%E4%BE%8B/demo-random-id-picker)
>
>生产环境使用的随机 `ID` 选择器请参考本站 <a href="/future/README.html#random-id-picker-服务" target="_blank">链接</a>

### 运行并测试示例

编译示例：

```sh
./build.sh && ./push.sh
```

复制示例配置：

```sh
ansible-playbook playbook-deployer-config.yml --inventory inventory.ini
```

启用示例：

```sh
ansible-playbook playbook-service-start.yml --inventory inventory.ini
```

初始化示例：

```sh
curl -X POST http://192.168.1.185/api/v1/id/picker/init?flag=order
```

使用 `wrk` 测试 `id` 列表新增性能：

```sh
wrk -t8 -c16 -d30000000s --latency --timeout 30 http://192.168.1.185/api/v1/id/picker/testAddIdList?flag=order
```

销毁示例：

```sh
ansible-playbook playbook-service-destroy.yml --inventory inventory.ini
```



### 结论

经过测试，在单表数据量达到 `13` 亿时，插入性能几乎没有损失，依旧保持在每秒插入 `18` 万条记录水平。随机抽取 `ID` 列表接口性能依旧没有明显损失，保持在 `10ms` 到 `60ms` 之间。

