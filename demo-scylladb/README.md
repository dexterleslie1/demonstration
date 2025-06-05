# ScyllaDB

>提示：ScyllaDB 兼容 Cassandra，相关操作可以参考 Cassandra。



## 部署

>注意：scylladb 内存参数 `--memory` 不能设置小于1G，否则服务无法启动。

### Docker 部署单机

>注意：客户端 `com.datastax.cassandra:cassandra-driver-core:3.11.4` 需要使用 `192.168.1.181 ip` 地址连接 scylladb 服务。

详细用法请参考本站 [示例](https://gitee.com/dexterleslie/demonstration/tree/main/demo-scylladb/demo-client-datastax)

参考本站 <a href="/linux/README.html#fs-aio-max-nr" target="_blank">链接</a> 配置 `fs.aio-max-nr` 内核参数。

.env 内容如下：

```properties
listen_address=192.168.1.181

```

docker-compose.yaml 配置文件如下：

```yaml
version: "3.1"

services:
    scylladb:
      image: scylladb/scylla:4.4.8
      environment:
        - TZ=Asia/Shanghai
      # 设置本机监听网卡ip地址，否则外部无法连接
      command: '--listen-address=${listen_address}'
      volumes:
        - ./init.cql:/scripts/data.cql:ro
        - ./scylla.yaml:/etc/scylla/scylla.yaml:ro
      network_mode: host

```

init.cql 内容如下：

```CQL
CREATE KEYSPACE IF NOT EXISTS demo WITH REPLICATION ={'class' : 'SimpleStrategy','replication_factor' : '1'};

USE demo;

CREATE TABLE IF NOT EXISTS t_order
(
    id            bigint PRIMARY KEY,
    user_id       bigint,
    status        text,      -- 使用text代替ENUM，Cassandra不支持ENUM类型
    pay_time      timestamp, -- 使用timestamp代替datetime
    delivery_time timestamp,
    received_time timestamp,
    cancel_time   timestamp,
    delete_status text,
    create_time   timestamp
);

CREATE TABLE IF NOT EXISTS t_order_detail
(
    id          bigint,
    order_id    bigint,
    user_id     bigint,
    product_id  bigint,
    merchant_id bigint,
    amount      int,
    PRIMARY KEY ((order_id), id) -- 复合主键，order_id为分区键，detail_id为聚类键
) WITH CLUSTERING ORDER BY (id ASC);
```

scylla.yaml 配置文件制作步骤参考本站 <a href="/cassandra/README.html#服务配置" target="_blank">链接</a>，配置文件添加如下内容：

```yaml
# 添加超时设置，否则在select count(id) from xxx时候会报告超时错误。提醒：客户端 cqlsh 在连接时同样需要提供 timeout 参数，否则会报告客户端超时，cqlsh --request-timeout=300000
read_request_timeout_in_ms: 300000
range_request_timeout_in_ms: 300000

# 修改下面设置为25，否则在批量插入时显示超出批量处理大小警告信息
# https://stackoverflow.com/questions/50385262/cassandra-batch-prepared-statement-size-warning
batch_size_warn_threshold_in_kb: 25
```

启动服务

```bash
docker compose up -d
```

进入 cqlsh

```bash
docker compose exec -it scylladb cqlsh --request-timeout=300000
```

初始化数据表

```CQL
source '/scripts/data.cql';
```



### Docker 部署集群

>注意：客户端 `com.datastax.cassandra:cassandra-driver-core:3.11.4` 需要使用 `192.168.1.90、192.168.1.91、192.168.1.92 ip` 地址连接 scylladb 服务。
>
>[参考链接1](https://github.com/garovu/scylladb-compose/blob/main/compose.yaml)
>
>[参考链接2](https://github.com/scylladb/scylla-code-samples/blob/master/scylla-and-spark/introduction/docker-compose.yaml)
>
>[参考链接3](https://hub.docker.com/r/scylladb/scylla)

详细用法请参考本站 [示例](https://gitee.com/dexterleslie/demonstration/tree/main/demo-scylladb/demo-order-management-app)

参考本站 <a href="/linux/README.html#fs-aio-max-nr" target="_blank">链接</a> 配置 `fs.aio-max-nr` 内核参数。

init.cql 内容如下：

```CQL
CREATE KEYSPACE IF NOT EXISTS demo WITH REPLICATION ={'class' : 'SimpleStrategy','replication_factor' : '3'};

USE demo;

drop table if exists t_order;
CREATE TABLE IF NOT EXISTS t_order
(
    id            bigint primary key,
    user_id       bigint,
    status        text,      -- 使用text代替ENUM，Cassandra不支持ENUM类型
    pay_time      timestamp, -- 使用timestamp代替datetime
    delivery_time timestamp,
    received_time timestamp,
    cancel_time   timestamp,
    delete_status text,
    create_time   timestamp
);

drop table if exists t_order_list_by_userId;
CREATE TABLE IF NOT EXISTS t_order_list_by_userId
(
    id            bigint,
    user_id       bigint,
    status        text,
    delete_status text,
    create_time   timestamp,
    order_id      bigint,
    primary key ((user_id),status,delete_status,create_time,order_id)
);

/*似乎order by很多限制不容易实现业务逻辑*/
/*CREATE MATERIALIZED VIEW mv_list_by_user_id AS
SELECT * FROM t_order
where user_id is not null and create_time is not null
    and status is not null and delete_status is not null and id is not null
PRIMARY KEY ((user_id),status,delete_status,create_time,id);*/

drop table if exists t_order_detail;
CREATE TABLE IF NOT EXISTS t_order_detail
(
    id          bigint,
    order_id    bigint,
    user_id     bigint,
    product_id  bigint,
    merchant_id bigint,
    amount      int,
    PRIMARY KEY ((order_id), id) -- 复合主键，order_id为分区键，detail_id为聚类键
) WITH CLUSTERING ORDER BY (id ASC);

```

用于编译容器镜像的 Dockerfile-scylladb 内容如下：

```dockerfile
FROM scylladb/scylla:4.4.8

COPY scylla.yaml /etc/scylla/scylla.yaml
COPY init.cql /scripts/data.cql

```

scylla.yaml 配置文件制作步骤参考本站 <a href="/cassandra/README.html#服务配置" target="_blank">链接</a>，配置文件添加如下内容：

```yaml
# 添加超时设置，否则在select count(id) from xxx时候会报告超时错误。提醒：客户端 cqlsh 在连接时同样需要提供 timeout 参数，否则会报告客户端超时，cqlsh --request-timeout=300000
read_request_timeout_in_ms: 300000
range_request_timeout_in_ms: 300000

# 修改下面设置为25，否则在批量插入时显示超出批量处理大小警告信息
# https://stackoverflow.com/questions/50385262/cassandra-batch-prepared-statement-size-warning
batch_size_warn_threshold_in_kb: 25
```

docker-compose.yaml 内容如下：

```yaml
version: "3.1"

services:
  node0:
    image: registry.cn-hangzhou.aliyuncs.com/future-public/demo-order-management-app-scylladb
    environment:
      - TZ=Asia/Shanghai
    command: "--seeds ${cassandra_seeds} --listen-address=${listen_address} --rpc-address=${listen_address}"
    restart: unless-stopped
    network_mode: host
```

修改各个节点的 deployer/node/.env 配置

```yaml
cassandra_seeds=192.168.1.90,192.168.1.91,192.168.1.92
# listen-address 和 rpc-address 的配置
listen_address=192.168.1.90
```

```yaml
cassandra_seeds=192.168.1.90,192.168.1.91,192.168.1.92
# listen-address 和 rpc-address 的配置
listen_address=192.168.1.91
```

```yaml
cassandra_seeds=192.168.1.90,192.168.1.91,192.168.1.92
# listen-address 和 rpc-address 的配置
listen_address=192.168.1.92
```

登录 192.168.1.90 查看初始化表

```sh
# 进入 cassandra 容器
docker compose exec -it node0 bash

# 进入 cqlsh
cqlsh --request-timeout=300000

# 初始化表
source '/scripts/data.cql';
```

登录 192.168.1.90 查看集群状态

```sh
# 进入 cassandra 容器
docker compose exec -it node0 bash

# 查看集群状态
nodetool status
```





## 配置参数

配置参数参数 [链接](https://hub.docker.com/r/scylladb/scylla)

配置参数通过 command 命令提供给容器，例如：

```yaml
version: "3.1"

services:
  node0:
    image: registry.cn-hangzhou.aliyuncs.com/future-public/demo-order-management-app-scylladb
    environment:
      - TZ=Asia/Shanghai
    command: "--seeds ${cassandra_seeds} --listen-address=${listen_address} --rpc-address=${listen_address}"
    restart: unless-stopped
    network_mode: host
```

