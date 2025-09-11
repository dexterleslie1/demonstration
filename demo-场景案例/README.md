# 场景案例



## 秒杀场景

### 需求

如下：

- 下单接口 `10+` 万 `qps`（下单成功和下单不成功的总 `qps`）
- 每秒发放 `32` 个普通商品，`96` 个秒杀商品，每秒发放商品总数 `38400` 个（`(32+96)*300=38400` 个）。
- 支持高并发根据用户 `ID` 、根据商家 `ID` 查询订单列表。
- 支持高并发根据订单 `ID`、根据订单 `ID` 列表、根据商品 `ID`、根据商品 `ID` 列表查询订单或者商品列表。
- 支持存储 `50` 亿订单数据性能依旧保持。



### `todo`

- 升级 `JDK` 使用虚拟线程以提升性能。
- 使用 `dubbo` 提升性能。
- 限制流量保护系统稳定运行。
- 数据库和 `Cassandra` 海量数据快速自检核对。
- 商品推荐算法实现。
- 高并发关键字搜索商品。
- 模拟热点商品、订单数据读取机制实现。
- 热点数据缓存以提升性能。
- 使用 `TiDB` 简化目前的架构。
- 支持系统所有数据的读取性能横向扩展。
- 支持 `k8s` 平台部署。
- 系统掉电数据一致性解决。



### 阿里云测试

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

7. 访问 `Nacos` 控制面板 `http://192.168.1.198:8848/nacos/` 调整普通和秒杀下单的限流规则 `qps` 为 `10000`（`3` 个 `api` 服务），帐号：`nacos`，密码：`nacos`。

8. 实施压力测试

   访问 `Prometheus` 监控 `http://10.0.1.10:3000`，帐号 `admin`，密码 `admin`

   普通方式下单测试：

   ```sh
   wrk -t8 -c2048 -d600000000000s --latency --timeout 30 http://10.0.1.40/api/v1/order/create
   ```

   秒杀方式下单测试：

   ```sh
   wrk -t8 -c2048 -d600000000000s --latency --timeout 30 http://10.0.1.40/api/v1/order/createFlashSale
   ```

9. 销毁所有测试服务

   在管理主机中执行下面命令销毁所有测试服务：

   ```sh
   ansible-playbook playbook-service-destroy.yml --inventory inventory.ini
   ```



### 单机部署后组件分离测试

>说明：单击部署应用后，因为业务规模变大，单机负载增加，组件之间资源相互竞争，影响应用的稳定性。面对此场景模拟应用支持在线扩容并且不影响在线业务。

单机部署：

- 修改 `inventory.ini` 各个 `host` 指向同一个实例取消 `单实例8c16g部署配置` 注释，注释 `scaleout=true` 全局配置。

- 复制 `Anisble` 配置：

  ```sh
  ansible-playbook playbook-deployer-config.yml --inventory inventory.ini
  ```

- 编译并推送镜像：

  ```sh
  ./build.sh && ./push.sh
  ```

- 运行应用：

  ```sh
  ansible-playbook playbook-service-start.yml --inventory inventory.ini
  ```

登录 `Nacos http://192.168.1.19:8848/nacos` 修改秒杀业务限流阈值为 `100`。

使用 `wrk` 协助持续秒杀业务进行中：

```sh
wrk -t8 -c2048 -d30000000s --latency --timeout 30 http://192.168.1.19/api/v1/order/createFlashSale
```

查看 `api`、`crond` 服务日志是否会报告异常：

```sh
# 查看 api 服务日志
cd deployer-flash-sale/service
docker compose logs -f --tail 10

# 查看 crond 服务日志
cd deployer-flash-sale/crond
docker compose logs -f --tail 10
```

复制分离 `Ansible` 配置：

- `inventory.ini` 配置中 `[redis]` 使用多实例部署配置

- `inventory.ini` 配置中 `[rocketmq]` 使用多实例部署配置

- `inventory.ini` 配置中 `[cassandra]` 使用多实例部署配置

- `inventory.ini` 配置中 `[api]` 使用多实例部署配置

- `inventory.ini` 配置中 `[openresty]` 使用多实例部署配置

- `inventory.ini` 配置中 `scaleout=true` 全局配置取消注释

- 复制 `Ansible` 配置

  ```sh
  ansible-playbook playbook-deployer-config.yml --inventory inventory.ini
  ```



启动应用相关组件空节点准备加入集群：

- `inventory.ini` 配置中 `[common]` 所有实例配置注释

- `inventory.ini` 配置中 `[db]` 所有实例配置注释

- `inventory.ini` 配置中 `[crond]` 所有实例配置注释

- 启动空节点

  ```sh
  ansible-playbook playbook-service-start.yml --inventory inventory.ini
  ```



`redis` 节点加入集群：

- 登录新节点所在的实例并登录容器

  ```sh
  cd deployer-flash-sale/redis
  docker compose exec -it node1 bash
  ```

- 添加新节点前查看集群状态

  ```sh
  $ redis-cli -h 192.168.1.19 -p 6380 cluster nodes
  025da042a31ff9dda4f4cf196387ce9ad50a50d6 192.168.1.198:6389@16389 slave 1bace5b5fdb4d973af22018ab6e423f8000ff2fe 0 1753867312000 1 connected
  2565a9258683148a0d5993e4c71f459ddee7fcac 192.168.1.198:6388@16388 slave 8fb4186bc7b1cbe6c34c48e43d09945f8ccff52a 0 1753867309000 2 connected
  45154545fca31f1434d7cac4b7f7012767e56d5a 192.168.1.198:6387@16387 slave cb356f4d477c48613e872b8736a56fff3924fce3 0 1753867308598 4 connected
  8acdb15a81ef8d4385be40299deb97a18a34884d 192.168.1.198:6384@16384 master - 0 1753867310000 5 connected 13107-16383
  8fb4186bc7b1cbe6c34c48e43d09945f8ccff52a 192.168.1.198:6381@16381 master - 0 1753867311725 2 connected 3277-6553
  78b03c0d9217d0c3c3afb3337a154fdc7e1f65ff 192.168.1.198:6386@16386 slave 8acdb15a81ef8d4385be40299deb97a18a34884d 0 1753867312731 5 connected
  149a0927d896f7b1beac0426a505eb5c65172b98 192.168.1.198:6382@16382 master - 0 1753867309000 3 connected 6554-9829
  8a1edd6e5b0fcd5f91e9520f1ecccd4537e0c057 192.168.1.198:6385@16385 slave 149a0927d896f7b1beac0426a505eb5c65172b98 0 1753867313736 3 connected
  1bace5b5fdb4d973af22018ab6e423f8000ff2fe 192.168.1.198:6380@16380 myself,master - 0 1753867307000 1 connected 0-3276
  cb356f4d477c48613e872b8736a56fff3924fce3 192.168.1.198:6383@16383 master - 0 1753867309707 4 connected 9830-13106
  ```

- 添加新节点到集群中，注意：此时新节点无法接收和处理请求，因为新节点还没有分配插槽

  ```sh
  # 添加 192.168.1.16:6380 新节点到现有集群，192.168.1.19:6380 为现有集群中任意一个节点
  redis-cli --cluster add-node 192.168.1.16:6380 192.168.1.19:6380
  redis-cli --cluster add-node 192.168.1.16:6381 192.168.1.19:6380
  redis-cli --cluster add-node 192.168.1.16:6382 192.168.1.19:6380
  redis-cli --cluster add-node 192.168.1.16:6383 192.168.1.19:6380
  redis-cli --cluster add-node 192.168.1.16:6384 192.168.1.19:6380
  redis-cli --cluster add-node 192.168.1.16:6385 192.168.1.19:6380
  redis-cli --cluster add-node 192.168.1.16:6386 192.168.1.19:6380
  redis-cli --cluster add-node 192.168.1.16:6387 192.168.1.19:6380
  redis-cli --cluster add-node 192.168.1.16:6388 192.168.1.19:6380
  redis-cli --cluster add-node 192.168.1.16:6389 192.168.1.19:6380
  ```

- 为指定的 `master` 节点配置 `slave` 节点，`2728a594a0498e98e4b83a537e19f9a0a3790f38` 为指定 `master` 节点的 `id`

  ```sh
  redis-cli -h 192.168.1.19 -p 6380 cluster nodes
  
  redis-cli -h 192.168.1.16 -p 6380 cluster replicate 1bace5b5fdb4d973af22018ab6e423f8000ff2fe
  redis-cli -h 192.168.1.16 -p 6381 cluster replicate 8fb4186bc7b1cbe6c34c48e43d09945f8ccff52a
  redis-cli -h 192.168.1.16 -p 6382 cluster replicate 149a0927d896f7b1beac0426a505eb5c65172b98
  redis-cli -h 192.168.1.16 -p 6383 cluster replicate cb356f4d477c48613e872b8736a56fff3924fce3
  redis-cli -h 192.168.1.16 -p 6384 cluster replicate 8acdb15a81ef8d4385be40299deb97a18a34884d
  ```

- 手动 `failover` 到新的节点实现主从切换

  ```sh
  redis-cli -h 192.168.1.16 -p 6380 cluster failover
  redis-cli -h 192.168.1.16 -p 6381 cluster failover
  redis-cli -h 192.168.1.16 -p 6382 cluster failover
  redis-cli -h 192.168.1.16 -p 6383 cluster failover
  redis-cli -h 192.168.1.16 -p 6384 cluster failover
  ```

- 再次查看集群状态，`192.168.1.16:6380` 变为 `master`，`192.168.1.19:6380` 变为 `slave` 节点

  ```sh
  redis-cli -h 192.168.1.16 -p 6380 cluster nodes
  ```



`Cassandra` 节点加入集群：

- 登录 `http://192.168.1.19:81/` 填写用户 `ID` 为 `1`，商家 `ID` 为 `2` 后创建普通商品并下单（大概 `10` 条单），记录用户订单信息在 `Cassandra` 迁移后进行数据比对。

- 分别登录新节点所在的实例，手动添加节点到集群中，下面以第一个节点为例子演示：

  ```sh
  cd deployer-flash-sale/cassandra
  
  # 先删除所有新节点容器，避免蜂拥加入集群导致混乱
  docker compose down -v
  
  # 编辑 .env 修改为下面内容，其中 192.168.1.19 为已经存在的 cassandra 集群节点
  cassandra_seeds=192.168.1.19
  # 重启 cassandra 节点，等待一个节点成功加入集群再启动下一个节点
  docker compose up -d
  # 进入容器
  docker compose exec -it node0 bash
  # 查看 cassandra 集群状态
  nodetool status
  # 等待新的节点状态由 UJ 变为 UN 才操作下一个节点加入集群
  ```



迁移 `Kafka` 分区：

- 登录新节点所在的实例

- 切换到 `kafka` 目录

  ```sh
  cd deployer-flash-sale/kafka
  ```

- 检查 `Kafka` 集群 `Broker` 是否正常响应

  ```sh
  # 进入分离 kafka 容器
  docker compose exec -it kafka bash
  
  KAFKA_JMX_OPTS="" /usr/bin/kafka-broker-api-versions --bootstrap-server localhost:9092
  ```

  - 显示 `192.168.1.19:9092` 和 `192.168.1.191:9092` 表示正常。

- 在线迁移所有主题到分离 `kafka`

  查看主题分区所在的 `Broker`

  ```sh
  KAFKA_JMX_OPTS="" /usr/bin/kafka-topics --bootstrap-server localhost:9092 --describe --topic topic-increase-count-fast
  ```

  创建 `topics.json`：

  ```json
  {
    "topics": [
      {
        "topic": "__consumer_offsets"
      },
      {
        "topic": "topic-order-in-cache-sync-to-db"
      },
      {
        "topic": "topic-increase-count-fast"
      },
      {
        "topic": "topic-increase-count-slow"
      },
      {
        "topic": "topic-create-order-cassandra-index-listByUserId"
      },
      {
        "topic": "topic-create-order-cassandra-index-listByMerchantId"
      },
      {
        "topic": "topic-setup-product-flashsale-cache"
      },
      {
        "topic": "topic-random-id-picker-add-id-list"
      },
      {
        "topic": "topic-add-product-to-cache-for-pickup-randomly-when-purchasing"
      }
    ],
    "version": 1
  }
  ```

  生成迁移配置，所有分区从 `broker 1` 迁移到 `broker 2`

  ```sh
  docker compose cp topics.json kafka:/home/appuser
  
  KAFKA_JMX_OPTS="" /usr/bin/kafka-reassign-partitions --bootstrap-server localhost:9092 \
    --generate \
    --topics-to-move-json-file topics.json \
    --broker-list "2" > reassign.json
  
  docker compose cp kafka:/home/appuser/reassign.json .
  ```

  编辑 `reassign.json` 删除其他配置只保留 `Proposed partition reassignment configuration` 配置。

  应用迁移配置：

  ```sh
  docker compose cp reassign.json kafka:/home/appuser
  
  KAFKA_JMX_OPTS="" /usr/bin/kafka-reassign-partitions --bootstrap-server localhost:9092 \
    --execute \
    --reassignment-json-file reassign.json
  ```

  查看迁移进度或者迁移结果状态

  ```sh
  KAFKA_JMX_OPTS="" /usr/bin/kafka-reassign-partitions --bootstrap-server localhost:9092 \
    --verify \
    --reassignment-json-file reassign.json
  ```

  使用 `docker stats` 查看之前的 `kafka` 服务 `CPU` 很低说明成功分离。



测试分离后的服务

```sh
curl http://192.168.1.185/api/v1/order/createFlashSale
```

切换流量到新的 `OpenResty`

```sh
wrk -t8 -c2048 -d30000000s --latency --timeout 30 http://192.168.1.185/api/v1/order/createFlashSale
```

重启 `Prometheus` 以监控新的节点

```sh
cd deployer-flash-sale/prometheus
docker compose restart
```



删除旧节点：

- 删除 `Kafka`

  ```sh
  cd deployer-flash-sale/kafka
  docker compose down -v
  ```

- 删除 `cassandra`

  ```sh
  cd deployer-flash-sale/cassandra
  docker compose exec -it node0 bash
  # 安全地删除节点
  nodetool decommission
  # 删除容器
  docker compose down -v
  ```

- 删除 `redis`

  ```sh
  cd deployer-flash-sale/redis
  docker compose exec -it node1 bash
  
  # 获取 slave 节点信息
  redis-cli -h 192.168.1.16 -p 6380 cluster nodes
  
  # 192.168.1.16:6390 是集群中任意一个节点
  # 6b92155a7e8851b9842c4a3ec38ff9d2b230b33e 是通过 cluster nodes 获取的从 192.168.1.19:6380 节点 id
  redis-cli --cluster del-node 192.168.1.19:6380 6b92155a7e8851b9842c4a3ec38ff9d2b230b33e
  
  docker compose down -v
  ```

- 删除 `api`

  ```sh
  cd deployer-flash-sale/service
  docker compose down -v
  ```

- 删除 `OpenResty`

  ```sh
  cd deployer-flash-sale/openresty
  docker compose down -v
  ```

  

更新应用到 `Ansible` 一致状态：

- `inventory.ini` 配置中 `[common]` 修改回当前实例

- `inventory.ini` 配置中 `[db]` 修改回当前实例

- `inventory.ini` 配置中 `[crond]` 修改回当前实例

- 复制 `Ansible` 配置

  ```sh
  ansible-playbook playbook-deployer-config.yml --inventory inventory.ini
  ```

- 启动应用为了和 `Ansible` 状态一致

  ```sh
  ansible-playbook playbook-service-start.yml --inventory inventory.ini
  ```

登录 `http://192.168.1.19:81/` 填写用户 `ID` 为 `1`，商家 `ID` 为 `2` 后对比查看是否丢失数据。




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

