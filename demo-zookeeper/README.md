## 使用 docker 运行

>zookeeper jvm 内存设置，使用环境变量设置 JVMFLAGS=-Xmx512m -Xms512m -server，参考 `https://www.cnblogs.com/zqllove/p/13724195.html`

```yaml
version: "3.0"

services:
  demo-zookeeper:
    image: zookeeper:3.4.9
    environment:
      - TZ=Asia/Shanghai
      - JVMFLAGS=-Xmx512m -Xms512m -server
#    ports:
#      - 2181:2181
    network_mode: host
```



## 禁用 AdminServer

```yaml
version: "3.0"

services:
  demo-zookeeper:
#    image: zookeeper:3.4.9
    image: zookeeper:3.8.4
    environment:
      - TZ=Asia/Shanghai
      - JVMFLAGS=-Xmx512m -Xms512m -server
      # 禁用 zookeeper AdminServer
      # https://hub.docker.com/_/zookeeper
      - ZOO_ADMINSERVER_ENABLED=false
#    ports:
#      - 2181:2181
    network_mode: host
```



## 客户端工具



### ZooKeeper Assistant

特点：

- 支持 ubuntu
- 缺点：每次刷新后节点数自动闭合需要手动重新展开



安装

下载 ZA-2.0.0.4-linux-x64.tar.gz，地址 `https://www.redisant.cn/za`

解压 ZA-2.0.0.4-linux-x64.tar.gz

```bash
tar -xvzf ZA-2.0.0.4-linux-x64.tar.gz
```

进入解压后的目录并授予 ZooKeeperAssistant 执行权限

```bash
sudo chmod +x ZooKeeperAssistant
```

运行 ZooKeeperAssistant

```bash
./ZooKeeperAssistant
```



## 持久化容器数据



### `zookeeper` 镜像持久化

>[参考资料](https://hub.docker.com/_/zookeeper#where-to-store-data)

该映像在 /data 和 /datalog 配置了卷，分别用于保存 Zookeeper 内存数据库快照和数据库更新的事务日志。

`docker-compose.yaml`：

```yaml
version: "3.1"

services:
  zookeeper:
    image: zookeeper:3.8.4
    environment:
      - TZ=Asia/Shanghai
      - JVMFLAGS=-Xmx512m -Xms512m -server
      # 禁用 zookeeper AdminServer
      # https://hub.docker.com/_/zookeeper
      - ZOO_ADMINSERVER_ENABLED=false
    restart: unless-stopped
    volumes:
      - data-demo-flash-sale-zookeeper-data:/data
      - data-demo-flash-sale-zookeeper-datalog:/datalog
    network_mode: host
    
volumes:
  data-demo-flash-sale-zookeeper-data:
  data-demo-flash-sale-zookeeper-datalog:
```



### `confluentinc/cp-zookeeper` 镜像持久化

`docker-compose.yaml`：

```yaml
version: "3.8"

services:
  zookeeper:
    image: confluentinc/cp-zookeeper:7.3.0
    environment:
      ZOOKEEPER_CLIENT_PORT: 2181
      ZOOKEEPER_TICK_TIME: 2000
    ports:
      - "2181:2181"
    volumes:
      - data-demo-flash-sale-kafka-zookeeper:/var/lib/zookeeper
    restart: unless-stopped
    
volumes:
  data-demo-flash-sale-kafka-zookeeper:
```



## 命令行



### 查看 `zookeeper` 版本

镜像 `zookeeper:3.8.4` 中查看

```sh
zkServer.sh version
```



镜像 `confluentinc/cp-zookeeper:7.3.0` 中查看

>提示：不同通过命令行查看，通过官方兼容表 `https://docs.confluent.io/platform/current/installation/versions-interoperability.html#zk` 查看为 `3.8.3`。

## 自定义zoo.cfg配置

>详细用法请参考本站示例：https://gitee.com/dexterleslie/demonstration/tree/main/demo-zookeeper/demo-curator-framework

zoo.cfg

```
# The number of milliseconds of each tick
tickTime=2000
# The number of ticks that the initial
# synchronization phase can take
initLimit=10
# The number of ticks that can pass between
# sending a request and getting an acknowledgement
syncLimit=5
# the directory where the snapshot is stored.
# do not use /tmp for storage, /tmp here is just
# example sakes.
dataDir=/data
dataLogDir=/datalog
# the port at which the clients will connect
clientPort=2181
# the maximum number of client connections.
# increase this if you need to handle more clients
#maxClientCnxns=60
#
# Be sure to read the maintenance section of the
# administrator guide before turning on autopurge.
#
# https://zookeeper.apache.org/doc/current/zookeeperAdmin.html#sc_maintenance
#
# The number of snapshots to retain in dataDir
#autopurge.snapRetainCount=3
# Purge task interval in hours
# Set to "0" to disable auto purge feature
#autopurge.purgeInterval=1

## Metrics Providers
#
# https://prometheus.io Metrics Exporter
#metricsProvider.className=org.apache.zookeeper.metrics.prometheus.PrometheusMetricsProvider
#metricsProvider.httpHost=0.0.0.0
#metricsProvider.httpPort=7000
#metricsProvider.exportJvmInfo=true


```

docker-compose.yaml

```yaml
version: "3.0"

services:
  demo-zookeeper:
#    image: zookeeper:3.4.9
    image: zookeeper:3.8.4
    volumes:
      - ./zoo.cfg:/conf/zoo.cfg
  	...
```

