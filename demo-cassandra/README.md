# `Cassandra`



## 介绍

**Cassandra软件是一款开源的分布式NoSQL数据库管理系统**，专为处理大规模数据而设计，具有高可用性、可扩展性和容错性等特点。以下是对Cassandra软件的详细介绍：

### 一、核心特性

1. 分布式架构：
   - Cassandra采用无中心节点的对等架构（P2P），所有节点功能相同，数据自动在集群中复制和分布。
   - 这种设计消除了单点故障风险，允许在不停机的情况下动态扩展集群规模。
2. 高可用性与容错性：
   - 通过多数据中心复制和可调一致性级别，Cassandra能在节点或数据中心故障时持续提供服务。
   - 即使部分节点离线，系统仍能保持完整功能，确保数据零丢失（通过适当配置）。
3. 线性可扩展性：
   - 横向扩展能力支持从单节点扩展到数千节点集群，处理能力随节点数量线性增长。
   - 适用于需要处理PB级数据或每秒数十万次请求的场景。
4. 灵活的数据模型：
   - 采用宽列存储模型，支持动态模式（无需预定义表结构），允许同一表中存在不同列的记录。
   - 这种灵活性特别适合存储半结构化数据（如JSON文档、时间序列数据）。

### 二、技术优势

1. 最终一致性模型：
   - 提供可配置的一致性级别（从ONE到ALL），允许在强一致性与高性能间灵活选择。
   - 默认的QUORUM级别在多数节点响应时即确认写入，平衡了速度与数据一致性。
2. 高性能写入：
   - 写入操作直接提交到内存中的MemTable，定期刷新到磁盘形成SSTable，显著提升写入吞吐量。
   - 写入路径的优化使其成为日志收集、物联网数据等写入密集型场景的理想选择。
3. 跨数据中心支持：
   - 内置多数据中心复制功能，支持地理分布式部署。
   - 通过策略配置（如NetworkTopologyStrategy）可实现跨区域数据同步，满足全球业务需求。

### 三、典型应用场景

1. 时间序列数据：
   - 物联网设备生成的海量传感器数据、金融市场的实时交易记录等场景，Cassandra的分布式写入能力可高效处理高频数据流。
2. 用户画像与个性化推荐：
   - 电商平台存储用户行为日志（点击、浏览、购买记录），通过Cassandra的快速查询能力支持实时推荐系统。
3. 消息队列与日志存储：
   - 替代传统消息中间件（如Kafka），Cassandra可持久化存储日志数据，同时提供高吞吐量的写入与查询能力。

### 四、与其他数据库的对比

- 与传统关系型数据库（如MySQL）：
  - Cassandra放弃JOIN操作和事务支持，换取更高的水平扩展性，适合处理海量非结构化数据。
  - 关系型数据库更适合需要复杂事务和强一致性的OLTP场景。
- 与其他NoSQL数据库（如MongoDB）：
  - MongoDB采用文档模型，支持更丰富的查询操作，但Cassandra在分布式环境下的容错性和可扩展性更优。
  - Cassandra的线性扩展能力使其在处理超大规模数据时更具成本效益。

### 五、生态与社区

- 活跃的开源社区：
  - Cassandra由Apache软件基金会维护，拥有全球开发者贡献代码和文档，社区支持包括邮件列表、Slack频道和定期用户大会。
- 企业级支持：
  - DataStax等公司提供商业版Cassandra（如DataStax Enterprise），集成分析、搜索和图数据库功能，并提供技术支持与培训服务。

### 六、总结

Cassandra通过其分布式架构、高可用性和灵活的数据模型，成为处理大规模、高吞吐量数据场景的理想选择。无论是需要存储时间序列数据的物联网应用，还是构建用户画像系统的电商平台，Cassandra都能提供可靠的解决方案。其开源属性和活跃的社区支持，进一步降低了企业的技术门槛和运维成本。



## 一致性级别

Cassandra 是一款高可用的分布式 NoSQL 数据库，其一致性模型允许用户根据业务需求灵活选择**一致性级别**（Consistency Level），在**强一致性**和**高可用性**之间取得平衡。以下是对 Cassandra 一致性级别的详细解析：


### **一、核心概念**
在分布式系统中，一致性级别定义了**写入操作需要多少副本确认成功**，以及**读取操作需要从多少副本获取数据**才能返回结果。Cassandra 的一致性级别基于**复制因子（Replication Factor, RF）**，即数据在集群中被复制的副本数量（例如 RF=3 表示数据存储在 3 个不同节点）。


### **二、关键分类：写入一致性 vs 读取一致性**
Cassandra 的一致性级别分为两类，分别控制写入和读取的行为：

#### 1. **写入一致性（Write Consistency）**  
定义写入操作需要多少个副本成功确认，协调器（Coordinator）才会认为写入成功。未达到一致性级别时，操作会超时或失败。

#### 2. **读取一致性（Read Consistency）**  
定义读取操作需要从多少个副本获取数据，并返回结果。若读取的副本数据不一致（存在版本差异），Cassandra 会通过**读取修复（Read Repair）**异步同步最新数据到旧副本（默认概率触发，可配置为同步修复）。


### **三、主要一致性级别详解**
Cassandra 支持以下一致性级别（按一致性从弱到强排序）：

#### 1. **ONE**  
- **写入**：仅需 1 个副本（任意副本）确认写入成功。  
- **读取**：仅需从 1 个副本读取数据（可能不是最新版本，后续通过读取修复同步）。  
- **特点**：最低一致性，最高可用性和最低延迟，适用于对一致性要求极低的场景（如日志收集）。

#### 2. **TWO / THREE**  
- **写入**：需要 2 或 3 个副本确认（取决于 RF，若 RF=3，THREE 等价于 ALL）。  
- **读取**：需要从 2 或 3 个副本读取数据。  
- **适用场景**：比 ONE 更强的一致性，但仍允许少量副本未响应。

#### 3. **QUORUM（法定人数）**  
- **计算方式**：`QUORUM = (RF // 2) + 1`（向下取整）。例如 RF=3 时，QUORUM=2；RF=4 时，QUORUM=3。  
- **写入**：需要超过半数的副本确认（强一致性的基础）。  
- **读取**：同样需要超过半数的副本响应。  
- **特点**：平衡一致性与可用性的经典选择。当读写均使用 QUORUM 时（如 RF=3，读写均为 2），可保证**强一致性**（因至少有一个副本被同时读写，数据一致）。

#### 4. **ALL**  
- **写入**：需要所有 RF 个副本确认成功（任何一个副本故障会导致写入失败）。  
- **读取**：需要从所有 RF 个副本读取数据（确保获取绝对最新版本）。  
- **特点**：最高一致性，但可用性最低（单副本故障即不可用），适用于对一致性要求极高的场景（如金融交易）。

#### 5. **ANY**  
- **写入**：允许写入到任意副本（包括“提示式移交”（Hinted Handoff）的副本）。若所有副本不可用，协调器会将写入暂存为“提示”（后续副本恢复后补全）。  
- **读取**：需至少 1 个存活副本响应（可能返回旧数据，依赖读取修复）。  
- **特点**：极端情况下（如多数副本故障）仍能写入，但一致性最弱。

#### 6. **LOCAL_QUORUM（本地法定人数）**  
- **写入/读取**：仅在**当前数据中心（Data Center）**内满足 QUORUM 级别的副本确认（跨数据中心部署时，减少跨机房延迟）。  
- **示例**：若集群有 3 个数据中心，每个 DC 的 RF=3，则 LOCAL_QUORUM 仅需当前 DC 内 2 个副本确认。  
- **适用场景**：多数据中心部署时，优先保证本地数据一致性，降低跨机房网络开销。

#### 7. **EACH_QUORUM（每个数据中心法定人数）**  
- **写入/读取**：要求**每个数据中心**内均满足 QUORUM 级别的副本确认。  
- **示例**：3 个 DC，每个 DC RF=3，则 EACH_QUORUM 需要每个 DC 内 2 个副本确认。  
- **适用场景**：强一致性的跨数据中心需求（如全局配置同步）。

#### 8. **LOCAL_ONE**  
- **写入/读取**：仅在当前数据中心内至少 1 个副本确认。  
- **特点**：最低本地一致性，适用于对延迟敏感且本地一致性要求极低的场景（如内部监控数据）。


### **四、一致性与可用性的权衡**
Cassandra 允许通过调整读写一致性级别实现**强一致性**。例如：  
- 若读写均使用 `QUORUM`（假设 RF=3），则写操作需 2 个副本确认，读操作也需 2 个副本响应。由于写操作已同步到 2 个副本，读操作必然从这 2 个副本中获取最新数据（时间戳最新的版本），从而保证强一致性。  
- 数学上，当满足 `写一致性级别 (W) + 读一致性级别 (R) > RF` 时，可保证强一致性（因至少有一个副本被同时读写）。


### **五、总结**
Cassandra 的一致性级别提供了高度灵活性，用户可根据业务需求选择：  
- **高吞吐/低延迟**：ONE、LOCAL_ONE（牺牲一致性）。  
- **平衡场景**：QUORUM、LOCAL_QUORUM（最常用）。  
- **强一致性**：QUORUM（读写均用）或 ALL（牺牲可用性）。  
- **跨数据中心**：EACH_QUORUM（强一致跨 DC）、LOCAL_QUORUM（本地优先）。  

理解这些级别有助于在设计数据库架构时，合理配置复制因子（RF）和一致性级别，实现性能与一致性的最优平衡。



## `CQL`

### 什么是 `CQL` 呢？

Cassandra CQL（Cassandra Query Language）是Apache Cassandra数据库中用于与数据库交互的一种查询语言，以下是详细介绍：

**一、基本概念**

- **定义**：CQL是一种类似于SQL的查询语言，用于执行数据查询、插入、更新和删除等操作，是Cassandra数据库的原生查询语言。
- **设计目的**：CQL的设计目的是简化开发者对Cassandra数据库的操作，使其更接近于传统的SQL语言，但同时又针对NoSQL数据库的特性进行了优化。

**二、功能特性**

- **支持常见操作**：CQL支持常见的关系数据库操作，如INSERT、SELECT、UPDATE和DELETE，同时也具有一些特定于Cassandra的功能，如分区键、复合主键和集合数据类型。
- **数据类型丰富**：CQL支持基本数据类型、复合数据类型以及用户定义类型（UDT），可以满足不同场景下的数据存储需求。
- **高级特性**：CQL还提供了索引的使用、视图的创建与管理等高级特性，有助于提升查询效率和数据管理能力。

**三、语法结构**

- **数据定义语言（DDL）**：用于定义和修改数据库结构，例如创建和删除表。在Cassandra中，创建表的CQL语句通常需要指定表名、分区键（PRIMARY KEY），以及可能的其他列和数据类型。
- **数据操作语言（DML）**：用于插入、查询、更新和删除表中的数据。

**四、应用场景**

- **数据操作**：通过CQL，用户可以方便地操作Cassandra数据库并执行各种数据操作，满足其数据存储和检索需求。
- **分页遍历**：CQL方法可以用于分页遍历所有行的数据，适用于处理大规模数据集。

**五、与SQL的区别**

- **不支持特性**：CQL不支持JOIN操作和子查询，原因在于Cassandra是为大数据存储设计的，部署模式基于分区方式，不支持低效的查询语句。
- **范围查询限制**：Cassandra基于partition key的hash分布数据，不支持在partition key字段上的范围查询（除非使用特定的分区器，但一般不推荐）。范围查询通常只允许在clustering key的列上进行，且需满足特定条件。



### 显示所有 keyspace

```CQL
describe keyspaces;
```



### 显示当前 keyspace 中的所有表

```CQL
describe tables
```



### `cqlsh` 客户端无交互执行命令

```sh
cqlsh -e "source '/scripts/data.cql'"
```



## 部署

>注意：本站秒杀 [示例](https://gitee.com/dexterleslie/demonstration/tree/main/demo-%E5%9C%BA%E6%99%AF%E6%A1%88%E4%BE%8B/demo-flash-sale) 在使用 `cassandra:3.11.4` 镜像压力测试过程中发生 `CMS` 垃圾回收器 `STW` 停顿时间过长导致应用写 `Cassandra` 慢问题。建议应用中使用 `cassandra:5.0.4` 镜像（使用 `G1` 垃圾回收器）。



### `Docker Compose` 部署单机 `Cassandra3`

>[官方参考文档](https://cassandra.apache.org/doc/latest/cassandra/installing/installing.html#install-with-docker)

详细用法请参考本站 [示例](https://gitee.com/dexterleslie/demonstration/tree/main/demo-cassandra)

docker-compose.yaml 配置文件如下：

```yaml
version: "3.0"

services:
  cassandra:
   image: cassandra:3.11.4
   environment:
     - MAX_HEAP_SIZE=1G
     # 是MAX_HEAP_SIZE的1/4
     - HEAP_NEWSIZE=256M
   volumes:
     - ./data.cql:/scripts/data.cql:ro
   network_mode: host
   # 或者
   ports:
     - "9042:9042"
    
```

启动 Cassandra 服务

```bash
docker compose up -d
```

进入 cqlsh

```bash
docker compose exec -it cassandra cqlsh --request-timeout=120000
```

初始化数据表

```CQL
source '/scripts/data.cql';
```



### `Docker Compose` 部署集群



#### 单实例部署集群 `Cassandra3`

>详细用法请参考本站 [示例](https://gitee.com/dexterleslie/demonstration/tree/main/demo-cassandra/demo-client-datastax)

`docker-compose.yaml`：

```yaml
version: "3.1"

services:
    node0:
      image: cassandra:3.11.4
      environment:
        - MAX_HEAP_SIZE=1G
        # 是MAX_HEAP_SIZE的1/4
        - HEAP_NEWSIZE=256M
        - CASSANDRA_SEEDS=node0,node1,node2
        # 节点向客户端广播的地址是宿主机可访问的 IP
        - CASSANDRA_BROADCAST_RPC_ADDRESS=${cassandra_broadcast_rpc_address}
      volumes:
        - ./init.cql:/scripts/data.cql:ro
#      network_mode: host
      ports:
        - "9042:9042"   # CQL 客户端端口
    node1:
      image: cassandra:3.11.4
      environment:
        - MAX_HEAP_SIZE=1G
        # 是MAX_HEAP_SIZE的1/4
        - HEAP_NEWSIZE=256M
        - CASSANDRA_SEEDS=node0,node1,node2
        # 节点向客户端广播的地址是宿主机可访问的 IP
        - CASSANDRA_BROADCAST_RPC_ADDRESS=${cassandra_broadcast_rpc_address}
      ports:
        - "9043:9042"   # CQL 客户端端口
    node2:
      image: cassandra:3.11.4
      environment:
        - MAX_HEAP_SIZE=1G
        # 是MAX_HEAP_SIZE的1/4
        - HEAP_NEWSIZE=256M
        - CASSANDRA_SEEDS=node0,node1,node2
        # 节点向客户端广播的地址是宿主机可访问的 IP
        - CASSANDRA_BROADCAST_RPC_ADDRESS=${cassandra_broadcast_rpc_address}
      ports:
        - "9044:9042"   # CQL 客户端端口

```



#### 多实例部署集群 `Cassandra3`

详细用法请参考本站 [示例](https://gitee.com/dexterleslie/demonstration/tree/main/demo-cassandra/demo-order-management-app)

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
    pay_time      timestamp, -- 使用timestamp代替datetime
    delivery_time timestamp,
    received_time timestamp,
    cancel_time   timestamp,
    delete_status text,
    create_time   timestamp,
    primary key ((user_id),status,delete_status,create_time,id)
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

用于编译容器镜像的 Dockerfile-cassandra 内容如下：

```dockerfile
FROM cassandra:3.11.4

COPY cassandra.yaml /etc/cassandra/cassandra.yaml
```

cassandra.yaml 配置文件制作步骤参考本站 <a href="/cassandra/README.html#服务配置" target="_blank">链接</a>，配置文件添加如下内容：

```yaml
# 提示：在大数据量时，下面 timeout 配置不需要配置，因为采用异步独立计数器方案。
# 添加超时设置，否则在select count(id) from xxx时候会报告超时错误。提醒：客户端 cqlsh 在连接时同样需要提供 timeout 参数，否则会报告客户端超时，cqlsh --request-timeout=300000
read_request_timeout_in_ms: 300000
range_request_timeout_in_ms: 300000

# 修改下面配置，否则在批量插入时显示超出批量处理大小警告信息
# https://stackoverflow.com/questions/50385262/cassandra-batch-prepared-statement-size-warning
# cassandra3 配置
batch_size_warn_threshold_in_kb: 64
batch_size_fail_threshold_in_kb: 128
# cassandra5 配置
batch_size_warn_threshold: 64KiB
batch_size_fail_threshold: 128KiB
```

192.168.1.90 docker-compose.yaml 内容如下：

```yaml
version: "3.1"

services:
  node1:
  	build:
      context: ./
      dockerfile: Dockerfile-cassandra
    image: registry.cn-hangzhou.aliyuncs.com/future-public/demo-order-management-app-cassandra
    environment:
      - CASSANDRA_SEEDS=192.168.1.90,192.168.1.91,192.168.1.92
    volumes:
      - ./init.cql:/scripts/data.cql:ro
    restart: unless-stopped
    network_mode: host
```

192.168.1.91 docker-compose.yaml 内容如下：

```yaml
version: "3.1"

services:
  node1:
    image: registry.cn-hangzhou.aliyuncs.com/future-public/demo-order-management-app-cassandra
    environment:
      - CASSANDRA_SEEDS=192.168.1.90,192.168.1.91,192.168.1.92
    restart: unless-stopped
    network_mode: host
```

192.168.1.92 docker-compose.yaml 内容如下：

```yaml
version: "3.1"

services:
  node1:
    image: registry.cn-hangzhou.aliyuncs.com/future-public/demo-order-management-app-cassandra
    environment:
      - CASSANDRA_SEEDS=192.168.1.90,192.168.1.91,192.168.1.92
    restart: unless-stopped
    network_mode: host
```

登录 192.168.1.90 查看初始化表

```sh
# 进入 cassandra 容器
docker compose exec -it node1 bash

# 进入 cqlsh
cqlsh --request-timeout=120000

# 初始化表
source '/scripts/data.cql';
```

登录 192.168.1.90 查看集群状态

```sh
# 进入 cassandra 容器
docker compose exec -it node1 bash

# 查看集群状态
nodetool status
```



#### 部署 `Cassandra5` 集群

>提示：不配置 `MAX_HEAP_SIZE` 和 `HEAP_NEWSIZE` 默认 `Cassandra5` 默认会使用 `50%` 内存设置 `-Xmx` 参数（例如：内存为 `8g`，则 `-Xmx4g`）。
>
>详细用法请参考本站 [示例](https://gitee.com/dexterleslie/demonstration/tree/main/demo-cassandra/demo-client-datastax-cassandra5)

`.env`：

```ini
cassandra_broadcast_rpc_address=127.0.0.1

```

`docker-compose.yaml`：

```yaml
version: "3.1"

services:
    node0:
      image: cassandra:5.0.4
      environment:
        - MAX_HEAP_SIZE=1G
        # 是MAX_HEAP_SIZE的1/4
        - HEAP_NEWSIZE=256M
        - CASSANDRA_SEEDS=node0,node1
        # 节点向客户端广播的地址是宿主机可访问的 IP
        - CASSANDRA_BROADCAST_RPC_ADDRESS=${cassandra_broadcast_rpc_address}
      volumes:
        - ./init.cql:/scripts/data.cql:ro
#      network_mode: host
      ports:
        - "9042:9042"   # CQL 客户端端口
    node1:
      image: cassandra:5.0.4
      environment:
        - MAX_HEAP_SIZE=1G
        # 是MAX_HEAP_SIZE的1/4
        - HEAP_NEWSIZE=256M
        - CASSANDRA_SEEDS=node0,node1
        # 节点向客户端广播的地址是宿主机可访问的 IP
        - CASSANDRA_BROADCAST_RPC_ADDRESS=${cassandra_broadcast_rpc_address}
      ports:
        - "9043:9042"   # CQL 客户端端口

```

`init.cql`：

```CQL
CREATE KEYSPACE IF NOT EXISTS demo WITH REPLICATION ={'class' : 'SimpleStrategy','replication_factor' : '1'};

USE demo;

drop table if exists t_order;
CREATE TABLE IF NOT EXISTS t_order
(
    id            decimal PRIMARY KEY,
    user_id       bigint,
    status        text,      -- 使用text代替ENUM，Cassandra不支持ENUM类型
    pay_time      timestamp, -- 使用timestamp代替datetime
    delivery_time timestamp,
    received_time timestamp,
    cancel_time   timestamp,
    delete_status text,
    create_time   timestamp
);

/* 用于协助分页查询 */
drop table if exists t_order_list_by_userId;
CREATE TABLE IF NOT EXISTS t_order_list_by_userId
(
    user_id       bigint,
    status        text,
    create_time   timestamp,
    order_id      bigint,
    primary key ((user_id,status),create_time,order_id)
) with clustering order by (create_time desc,order_id desc);

drop table if exists t_order_detail;
CREATE TABLE IF NOT EXISTS t_order_detail
(
    id          bigint,
    order_id    decimal,
    user_id     bigint,
    product_id  bigint,
    merchant_id bigint,
    amount      int,
    PRIMARY KEY ((order_id), id) -- 复合主键，order_id为分区键，detail_id为聚类键
) WITH CLUSTERING ORDER BY (id ASC);

drop table if exists t_count;
/* 用于协助并发update同一条数据 */
create table if not exists t_count (
    flag        text,
    count       counter,
    primary key (flag)
);

/* 初始化 count 的值，不能直接 set，否则报错 */
update t_count set count=count+0 where flag='order';

/* 用于协助测试 upsert */
drop table if exists t_upsert_test1;
create table if not exists t_upsert_test1 (
    key1 int,
    key2 text,
    value text,
    primary key ( key1 )
);
drop table if exists t_upsert_test2;
create table if not exists t_upsert_test2 (
  key1 int,
  key2 text,
  value text,
  primary key ( (key1), key2 )
);
drop table if exists t_upsert_test3;
create table if not exists t_upsert_test3 (
  key1 int,
  key2 text,
  value text,
  primary key ( key1, key2 )
);

```



## 初始化数据

>[参考官方文档](https://cassandra.apache.org/doc/latest/cassandra/getting-started/cassandra-quickstart.html)

创建 data.cql 脚本文件

```CQL
CREATE KEYSPACE IF NOT EXISTS store WITH REPLICATION =
{ 'class' : 'SimpleStrategy',
'replication_factor' : '1'
};

CREATE TABLE IF NOT EXISTS store.shopping_cart (
    userid text PRIMARY KEY,
    item_count int,
    last_update_timestamp timestamp
);

INSERT INTO store.shopping_cart
    (userid, item_count, last_update_timestamp)
    VALUES ('9876', 2, toTimeStamp(now()));
INSERT INTO store.shopping_cart
    (userid, item_count, last_update_timestamp)
    VALUES ('1234', 5, toTimeStamp(now()));
```

执行脚本文件

```sh
docker run --rm --network host \
-v "$(pwd)/data.cql:/scripts/data.cql" \
-e CQLSH_HOST=localhost -e CQLSH_PORT=9042 \
-e CQLVERSION=3.4.7 nuvo/docker-cqlsh
```

查询数据

```sh
# 进入 cqlsh
docker run --rm -it --network \
host nuvo/docker-cqlsh cqlsh localhost \
9042 --cqlversion='3.4.7'
```

```sql
# 查询数据
SELECT * FROM store.shopping_cart;
```



## 手动执行 CQL 脚本

登录 cqlsh 客户端

```sh
cqlsh --request-timeout=120000
```

手动执行脚本

```CQL
source '/scripts/data.cql';
```



## Java 主流客户端

### 主流客户端有哪些呢？

在Java生态中，Apache Cassandra的主流客户端驱动库提供了不同特性和适用场景，以下是几个主要的Java客户端及其核心特点分析：

------

**1. DataStax Java Driver**

**简介**
由DataStax公司（Cassandra的主要贡献者）维护，是Cassandra官方推荐的Java客户端，支持Cassandra 2.0及以上版本。

**核心特性**

- **异步与同步API**：提供`AsyncResultSet`和同步API，支持响应式编程（如通过RxJava或Reactor扩展）。
- **负载均衡与故障转移**：内置智能路由策略（如`TokenAwarePolicy`）和重试机制，确保高可用性。
- **对象映射（OCM）**：通过`@Table`、`@Column`等注解实现Java对象与Cassandra表的自动映射，简化CRUD操作。
- **批处理与预处理语句**：支持批量操作和预编译SQL，提升性能。
- **配置灵活**：通过YAML或编程方式配置连接池、超时、SSL等参数。

**适用场景**

- 需要官方支持与稳定性的企业级应用。
- 复杂查询或需要对象映射的场景。

**示例代码片段**

```java
CqlSession session = CqlSession.builder().build();
ResultSet rs = session.execute("SELECT * FROM t_order WHERE id = ?", 123L);
Row row = rs.one();
System.out.println(row.getString("status"));
```

------

**2. Astyanax**

**简介**
Netflix开源的Cassandra客户端，基于Thrift协议（较旧），适合遗留系统或对性能要求极高的场景。

**核心特性**

- **连接池管理**：内置高效的连接池，支持多数据中心部署。
- **异步API**：基于Guava的`ListenableFuture`实现异步操作。
- **键值模型**：简化Cassandra的键值操作，适合简单的数据访问模式。

**局限性**

- 仅支持Thrift协议，新版本Cassandra已转向CQL（需评估兼容性）。
- 社区活跃度较低，维护成本较高。

**适用场景**

- 遗留系统迁移或特定性能优化需求。

------

**3. Hector**

**简介**
早期流行的Cassandra Java客户端，基于Thrift协议，现已停止维护。

**核心特性**

- **轻量级设计**：简单易用，适合快速原型开发。
- **多数据中心支持**：提供基本的跨数据中心读写能力。

**局限性**

- 不再维护，缺乏新特性支持（如CQL 3.0+）。
- 性能和稳定性不如现代客户端。

**适用场景**

- 仅适用于学习或历史项目维护。

------

**4. 其他客户端**

- Spring Data Cassandra
  - **简介**：Spring生态的集成方案，提供基于Repository的抽象。
  - **适用场景**：Spring Boot项目快速集成Cassandra。
- Kundera
  - **简介**：支持多数据源的ORM框架，兼容Cassandra。
  - **适用场景**：需要统一管理多种NoSQL数据库的场景。

------

**对比与推荐**



| **客户端**            | **协议** | **异步支持**  | **对象映射** | **维护状态** | **推荐场景**         |
| --------------------- | -------- | ------------- | ------------ | ------------ | -------------------- |
| DataStax Driver       | CQL      | ✅（原生）     | ✅（OCM）     | ✅（活跃）    | 企业级应用、复杂查询 |
| Astyanax              | Thrift   | ✅（Guava）    | ❌            | ⚠️（低活跃）  | 遗留系统、性能优化   |
| Hector                | Thrift   | ❌             | ❌            | ❌（停止）    | 仅学习或历史项目     |
| Spring Data Cassandra | CQL      | ✅（Reactive） | ✅（Spring）  | ✅（活跃）    | Spring Boot项目      |



**推荐选择**

- **生产环境**：优先选择**DataStax Java Driver**，因其官方支持、功能全面且社区活跃。
- **Spring项目**：使用**Spring Data Cassandra**简化集成。
- **遗留系统**：若必须使用Thrift协议，可考虑**Astyanax**，但需评估迁移风险。

------

**注意事项**

1. **协议兼容性**：新版本Cassandra（4.0+）已弃用Thrift，建议使用CQL协议的客户端。
2. **性能调优**：根据业务需求调整连接池大小、超时时间和重试策略。
3. **监控**：集成客户端的监控指标（如延迟、错误率）以优化集群性能。

通过合理选择客户端驱动，可以充分发挥Cassandra的分布式优势，满足高并发、低延迟的应用需求。

### **DataStax Java Driver**

>提示：`Cassandra5` 驱动程序用法和 `Cassandra3` 驱动程序用法有区别：
>
>- `POM` 配置
>
>  ```java
>  <!-- cassandra5 驱动程序 -->
>  <dependency>
>      <groupId>com.datastax.oss</groupId>
>      <artifactId>java-driver-core</artifactId>
>      <version>4.17.0</version>
>  </dependency>
>  ```
>
>- `Java` 配置
>
>  ```java
>  @Configuration
>  public class ConfigCassandra {
>      // 以下是连接 cassandra5 驱动程序使用
>      @Bean(destroyMethod = "close")
>      public CqlSession cqlSession() {
>          return CqlSession.builder()
>                  .addContactPoint(new InetSocketAddress("localhost", 9042))
>                  .addContactPoint(new InetSocketAddress("localhost", 9043))
>                  // 指定本地数据中心名称
>                  .withLocalDatacenter("datacenter1")
>                  .withKeyspace("demo")
>                  .build();
>      }
>  }
>  
>  ```
>
>- `BatchStatement` 创建
>
>  ```java
>  // 创建批量语句
>  // cassandra3 驱动程序
>  /*BatchStatement batch = new BatchStatement(BatchStatement.Type.LOGGED);*/
>  // cassandra5 驱动程序
>  BatchStatement batch = BatchStatement.newInstance(BatchType.LOGGED);
>  ```
>
>- `ConsistencyLevel` 设置
>
>  ```java
>  String cql = "INSERT INTO t_order_list_by_userId(user_id,create_time,status,order_id) " +
>                  "VALUES (?, ?, ?, ?)";
>  preparedStatementInsert = session.prepare(cql);
>  // cassandra3 驱动程序
>  /*preparedStatementInsert.setConsistencyLevel(ConsistencyLevel.LOCAL_QUORUM);*/
>  
>  // 添加多个订单到批量语句
>  for (int i = 0; i < list.size(); i++) {
>      OrderIndexListByUserIdModel model = list.get(i);
>      BoundStatement bound = preparedStatementInsert.bind(
>              model.getUserId(),
>              model.getCreateTime().toInstant(ZoneOffset.ofHours(8)),
>              model.getStatus(),
>              model.getOrderId()
>      );
>  
>      // cassandra5 驱动程序
>      bound = bound.setConsistencyLevel(ConsistencyLevel.LOCAL_QUORUM);
>  
>      batch = batch.add(bound);
>  }
>  ```
>
>- 绑定参数时日期时间类型为 `Instant` 类型
>
>  ```java
>  // 添加多个订单到批量语句
>  for (int i = 0; i < list.size(); i++) {
>      OrderIndexListByUserIdModel model = list.get(i);
>      BoundStatement bound = preparedStatementInsert.bind(
>              model.getUserId(),
>              model.getCreateTime().toInstant(ZoneOffset.ofHours(8)),
>              model.getStatus(),
>              model.getOrderId()
>      );
>  
>      // cassandra5 驱动程序
>      bound = bound.setConsistencyLevel(ConsistencyLevel.LOCAL_QUORUM);
>  
>      batch = batch.add(bound);
>  }
>  ```
>
>详细用法请参考本站 [示例 `Cassandra3`](https://gitee.com/dexterleslie/demonstration/tree/main/demo-cassandra/demo-client-datastax) 或者 [示例 `Cassandra5`](https://gitee.com/dexterleslie/demonstration/tree/main/demo-cassandra/demo-client-datastax-cassandra5)，提醒：本示例演示连接 `Cassandra3.11.4`，客户端默认已经配置集群拓扑自动更新机制不需要手动配置。

POM 配置：

```xml
<dependency>
    <groupId>com.datastax.cassandra</groupId>
    <artifactId>cassandra-driver-core</artifactId>
    <version>3.11.4</version>
</dependency>
<dependency>
    <groupId>com.codahale.metrics</groupId>
    <artifactId>metrics-core</artifactId>
    <version>3.0.2</version>
</dependency>
```

Java 配置：

```java
@Configuration
public class ConfigCassandra {
    
    @Bean(destroyMethod = "close")
    public Cluster cluster() {
        return Cluster.builder()
                // 如果只有一个 localhost:9042 连接点，
                // 在执行 nodetool decommission 使节点删除后将无法获取到集群新的拓扑导致报错
                .addContactPoint("localhost").withPort(9042)
                .addContactPoint("localhost").withPort(9043)
                .build();
    }

    // session 是线程安全的，共用同一个 session
    @Bean(destroyMethod = "close")
    public Session session(Cluster cluster) {
        return cluster.connect("demo");
    }
}

```

单条插入和批量插入：

```java
@SpringBootTest
@Slf4j
public class ApplicationTests {

    @Autowired
    Session session;

    private PreparedStatement preparedStatementOrderInsertion;

    @PostConstruct
    public void init() {
        // cql 只需要 prepare 一次
        String cql = "INSERT INTO t_order (id, user_id, status, pay_time, delivery_time, received_time, cancel_time, delete_status, create_time) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        this.preparedStatementOrderInsertion = session.prepare(cql);
    }

    @Test
    public void test() {
        // region 测试单条插入

        BoundStatement bound = preparedStatementOrderInsertion.bind(
                BigDecimal.valueOf(1L), // id
                1001L,                  // user_id
                "Unpay",                // status
                // https://stackoverflow.com/questions/39926022/codec-not-found-for-requested-operation-timestamp-java-lang-long
                Date.from(Instant.now()),          // pay_time
                null,                   // delivery_time
                null,                   // received_time
                null,                   // cancel_time
                "Normal",               // delete_status
                Date.from(Instant.now())           // create_time
        );

        ResultSet result = session.execute(bound);
        Assertions.assertTrue(result.wasApplied());

        // endregion

        // region 测试批量插入

        // 准备批量插入语句

        // 创建批量语句
        BatchStatement batch = new BatchStatement(BatchStatement.Type.LOGGED);

        // 添加多个订单到批量语句
        for (int i = 0; i < 5; i++) {
            bound = preparedStatementOrderInsertion.bind(
                    BigDecimal.valueOf(100L + i), // id
                    1001L,                        // user_id
                    "Unpay",                      // status
                    Date.from(Instant.now()),                // pay_time
                    null,                         // delivery_time
                    null,                         // received_time
                    null,                         // cancel_time
                    "Normal",                     // delete_status
                    Date.from(Instant.now())                 // create_time
            );
            batch = batch.add(bound);
        }

        // 执行批量插入
        result = session.execute(batch);
        Assertions.assertTrue(result.wasApplied());

        // endregion
    }
}

```



## 主流的 GUI 客户端

>todo

### 主流客户端有哪些呢？

Cassandra的GUI客户端工具能够帮助用户更直观地管理、查询和监控Cassandra集群。以下是一些主流的Cassandra GUI客户端工具，它们各自具有不同的特点和优势：

------

**1. DataStax DevCenter**

- **简介**：DataStax官方推出的免费GUI工具，专为Cassandra设计。
- 功能特点：
  - 支持CQL查询执行和结果可视化。
  - 提供基本的Schema管理功能（如创建、修改表）。
  - 支持连接多个Cassandra集群。
- **适用场景**：适合开发人员和DBA进行日常查询和简单管理。
- **限制**：功能相对基础，复杂操作需结合命令行工具。

------

**2. NoSQLBooster for Cassandra**

- **简介**：第三方开发的商业GUI工具，提供免费试用版。
- 功能特点：
  - 智能CQL代码补全和语法高亮。
  - 查询结果导出为CSV、JSON等格式。
  - 支持连接多个Cassandra集群，并管理Keyspace和表。
- **适用场景**：适合需要高效编写和调试CQL的开发人员。
- **限制**：高级功能需购买许可证。

------

**3. DBeaver**

- **简介**：开源的通用数据库工具，支持多种数据库（包括Cassandra）。
- 功能特点：
  - 支持CQL查询执行和结果可视化。
  - 提供数据导出、导入功能。
  - 支持插件扩展，可与其他工具集成。
- **适用场景**：适合需要统一管理多种数据库的团队。
- **限制**：Cassandra特定功能可能不如专用工具丰富。

------

**4. TablePlus**

- **简介**：轻量级的现代数据库管理工具，支持Cassandra。
- 功能特点：
  - 简洁直观的用户界面。
  - 支持多标签查询和结果集比较。
  - 提供基本的Schema管理和数据编辑功能。
- **适用场景**：适合追求简洁高效的用户。
- **限制**：Cassandra高级功能支持有限。

------

**5. Cassandra GUI (Apache Cassandra官方工具包中的一部分)**

- **简介**：Apache Cassandra官方提供的一个简单的图形界面工具。
- 功能特点：
  - 提供基本的CQL查询和结果查看功能。
  - 适合快速查看和简单操作。
- **适用场景**：适合初学者或需要快速操作的用户。
- **限制**：功能较为基础，缺乏高级管理特性。

------

**6. RazorSQL**

- **简介**：支持多种数据库的商业GUI工具，包括Cassandra。
- 功能特点：
  - 提供CQL编辑器、查询构建器和结果浏览器。
  - 支持数据导入、导出和备份。
  - 提供SQL历史记录和书签功能。
- **适用场景**：适合需要跨数据库操作的用户。
- **限制**：需购买许可证。

------

**7. OpsCenter (DataStax)**

- **简介**：DataStax提供的企业级监控和管理工具。
- 功能特点：
  - 提供集群监控、性能调优和备份恢复功能。
  - 支持自动化任务和警报配置。
- **适用场景**：适合企业级Cassandra集群的管理。
- **限制**：功能强大但复杂，适合专业团队。

------

**8. CQLSH (命令行工具，但可通过GUI包装)**

- **简介**：Cassandra自带的命令行工具，可通过第三方GUI包装使用。
- 功能特点：
  - 原生支持所有CQL操作。
  - 结合GUI包装工具（如`cqlshgui`）可提供图形界面。
- **适用场景**：适合喜欢命令行但希望有图形化辅助的用户。
- **限制**：需额外配置GUI包装工具。

------

**选择建议**

- **开发人员**：推荐使用**NoSQLBooster**或**DBeaver**，提供高效的CQL开发和调试功能。
- **DBA**：推荐使用**OpsCenter**或**DataStax DevCenter**，提供全面的集群管理和监控功能。
- **初学者**：推荐使用**TablePlus**或**Cassandra GUI**，界面简洁易用。
- **企业用户**：推荐使用**OpsCenter**，提供企业级的管理和监控能力。

------

**总结**

Cassandra的GUI客户端工具种类繁多，用户可根据自身需求（如开发、管理、监控等）选择合适的工具。官方工具（如DataStax DevCenter）适合基础操作，而第三方工具（如NoSQLBooster、DBeaver）则提供更丰富的功能和更好的用户体验。



## 索引

### 主键索引

#### 介绍

在Apache Cassandra中，主键索引是数据模型和查询性能的核心。主键不仅决定了数据在集群中的分布方式，还直接影响了查询的效率和可行性。以下是关于Cassandra主键索引的全面介绍：

------

**1. 主键的组成**

Cassandra的主键由以下两部分组成：

- 分区键（Partition Key）：
  - **作用**：决定数据存储在哪个分区（节点）上。
  - 特点：
    - 可以是单个列，也可以是多个列的组合（称为复合分区键）。
    - 具有哈希特性，Cassandra使用分区键的哈希值来确定数据存储在哪个节点上。
    - 相同分区键的数据会存储在同一个分区内。
- 聚类键（Clustering Key）：
  - **作用**：决定分区内数据的排序方式。
  - 特点：
    - 可以是单个列，也可以是多个列的组合（称为复合聚类键）。
    - 定义了分区内数据的物理排序顺序，影响查询结果的顺序。
    - 聚类键的顺序可以通过`WITH CLUSTERING ORDER BY`子句指定（升序或降序）。

------

**2. 主键索引的特点**

- 自动创建：
  - Cassandra在创建表时，主键列会自动创建索引，无需手动干预。
  - 主键索引是Cassandra中最高效的索引类型，支持快速查询。
- 高效查询：
  - **点查询**：通过主键精确查询单条记录（如`WHERE partition_key = ? AND clustering_key = ?`）非常高效。
  - **范围查询**：通过聚类键进行范围查询（如`WHERE partition_key = ? AND clustering_key >= ?`）也很高效。
- 唯一性：
  - 主键的值在表中必须是唯一的，确保每条记录可以被唯一标识。

------

**3. 主键索引的查询支持**

- 分区键查询：

  - 查询必须包含分区键，否则Cassandra无法定位数据所在的分区。

  - 示例：

    ```cql
    SELECT * FROM users WHERE user_id = ?;
    ```

    - `user_id`是分区键，查询高效。

- 聚类键查询：

  - 在分区键的基础上，可以通过聚类键进行范围查询或排序。

  - 示例：

    ```cql
    SELECT * FROM orders WHERE user_id = ? AND order_date >= ? AND order_date <= ?;
    ```

    - `user_id`是分区键，`order_date`是聚类键，支持范围查询。

- 排序：

  - 查询结果可以按照聚类键的顺序返回（通过`ORDER BY`指定）。

  - 示例：

    ```cql
    SELECT * FROM orders WHERE user_id = ? ORDER BY order_date DESC;
    ```

    - 结果按照`order_date`降序排列。

------

**4. 主键设计的最佳实践**

- 选择合适的分区键：

  - 分区键的选择应尽量使数据均匀分布在集群中，避免热点问题。
  - 示例：
    - 如果按用户ID分区，确保用户ID的分布均匀。

- 合理使用聚类键：

  - 聚类键应反映查询模式，支持常用的范围查询或排序。
  - 示例：
    - 如果经常按时间范围查询订单，可以将`order_date`作为聚类键。

- 避免宽分区：

  - 单个分区的数据量不宜过大，否则可能影响性能。
  - 建议：
    - 限制分区的大小，避免单个分区存储过多数据。

- 复合主键：

  - 使用复合主键可以支持更复杂的查询模式。

  - 示例：

    ```cql
    CREATE TABLE user_orders (
        user_id UUID,
        order_date TIMESTAMP,
        order_id UUID,
        amount DECIMAL,
        PRIMARY KEY ((user_id), order_date, order_id)
    ) WITH CLUSTERING ORDER BY (order_date DESC, order_id ASC);
    ```

    - `user_id`是分区键，`order_date`和`order_id`是聚类键。
    - 支持按用户ID和时间范围查询订单，并按时间降序、订单ID升序排列。

------

**5. 主键索引的限制**

- 不支持非主键列的直接查询：
  - 除非为非主键列创建二级索引或物质化视图，否则无法直接通过非主键列查询。
  - 示例：
    - 无法直接查询`WHERE status = 'SHIPPED'`，除非为`status`列创建索引。
- 不支持跨分区查询：
  - Cassandra的查询通常局限于单个分区，跨分区查询需要特殊处理（如使用`ALLOW FILTERING`，但不推荐）。
- 不支持复杂的聚合查询：
  - Cassandra不支持类似SQL的`GROUP BY`、`JOIN`等复杂聚合操作。

------

**6. 示例：主键索引的实际应用**

假设有一个电商系统，需要存储用户订单信息，并支持以下查询：

1. 查询某个用户的所有订单。
2. 查询某个用户在特定时间范围内的订单。
3. 查询结果按订单时间降序排列。

**表设计**：

```cql
CREATE TABLE user_orders (
    user_id UUID,
    order_date TIMESTAMP,
    order_id UUID,
    amount DECIMAL,
    status TEXT,
    PRIMARY KEY ((user_id), order_date, order_id)
) WITH CLUSTERING ORDER BY (order_date DESC, order_id ASC);
```

**查询示例**：

1. 查询用户`123e4567-e89b-12d3-a456-426614174000`的所有订单：

   ```cql
   SELECT * FROM user_orders WHERE user_id = 123e4567-e89b-12d3-a456-426614174000;
   ```

2. 查询用户在`2023-01-01`到`2023-01-31`之间的订单：

   ```cql
   SELECT * FROM user_orders 
   WHERE user_id = 123e4567-e89b-12d3-a456-426614174000 
   AND order_date >= '2023-01-01' 
   AND order_date <= '2023-01-31';
   ```

3. 查询结果按订单时间降序排列（通过`WITH CLUSTERING ORDER BY`实现）：

   - 查询语句同上，结果自动按`order_date`降序排列。

------

**总结**

- **主键索引是Cassandra的核心**：决定了数据的存储和查询方式。
- **分区键和聚类键的合理设计**：支持高效的点查询和范围查询。
- **遵循最佳实践**：避免热点问题，确保数据均匀分布。
- **理解限制**：Cassandra的主键索引不支持非主键列的直接查询或跨分区查询，需通过其他机制（如二级索引、物质化视图）实现。

通过合理设计主键索引，可以充分发挥Cassandra的高性能和可扩展性，满足业务需求。



#### 实践

>详细用法请参考本站 [示例](https://gitee.com/dexterleslie/demonstration/tree/main/demo-cassandra/demo-order-management-app)

下面实现根据用户ID、订单状态、创建日期查询订单列表。

表定义：

```CQL
CREATE TABLE IF NOT EXISTS t_order_list_by_userId
(
    user_id       bigint,
    status        text,
    create_time   timestamp,
    order_id      bigint,
    /* 使用 user_id,status 作为分区键快速定位数据，使用 create_time,order_id 作为聚类键范围查询和排序 */
    primary key ((user_id,status),create_time,order_id)
) with clustering order by (create_time desc,order_id desc);
```

业务 CQL：

```java
StringBuilder builder = new StringBuilder("select order_id from t_order_list_by_userId where user_id=?");
builder.append(" and status=?");
builder.append(" and create_time>=? and create_time<=?");
builder.append(" limit ?");
cql = builder.toString();
preparedStatementListByUserIdAndStatus = session.prepare(cql);
preparedStatementListByUserIdAndStatus.setConsistencyLevel(ConsistencyLevel.LOCAL_ONE);
```



### 二级索引

#### 介绍

在Apache Cassandra中，**二级索引（Secondary Index）**是一种允许对非主键列进行查询的机制。由于Cassandra的主键设计决定了数据的存储和查询方式，默认情况下只能通过主键高效查询。二级索引的引入扩展了查询能力，但需要注意其适用场景和性能特点。

------

**1. 二级索引的作用**

- 扩展查询能力：
  - 默认情况下，Cassandra只能通过主键（分区键和聚类键）高效查询。
  - 二级索引允许对非主键列进行查询，例如`WHERE status = 'SHIPPED'`。
- 支持简单条件查询：
  - 二级索引适用于低基数的列（列中不同值的数量较少）或偶尔查询的场景。

------

**2. 二级索引的类型**

Cassandra支持以下两种二级索引类型：

- 内置二级索引（Built-in Secondary Index）：

  - 特点：

    - 简单易用，通过`CREATE INDEX`语句创建。
    - 适用于低基数的列（如状态、布尔值等）。
    - 不适合高基数列（如用户ID、时间戳等），可能导致性能问题。

  - 示例：

    ```cql
    CREATE TABLE orders (
        order_id UUID PRIMARY KEY,
        user_id UUID,
        status TEXT,
        order_date TIMESTAMP,
        amount DECIMAL
    );
     
    CREATE INDEX IF NOT EXISTS ON orders (status);
    ```

    - 创建后，可以查询`WHERE status = 'SHIPPED'`。

- 自定义二级索引（Custom Secondary Index）：

  - 特点：
    - 通过实现`org.apache.cassandra.db.indexes.SecondaryIndex`接口创建。
    - 适用于需要特殊查询逻辑的场景（如全文搜索、地理空间查询等）。
    - 需要自定义实现，复杂度较高。
  - 示例：
    - 使用第三方插件（如Solr或Elasticsearch）实现更复杂的查询。

------

**3. 二级索引的创建与使用**

- 创建二级索引：

  ```cql
  CREATE INDEX [IF NOT EXISTS] index_name ON table_name (column_name);
  ```

  - `index_name`：可选，索引名称。
  - `table_name`：表名。
  - `column_name`：要索引的列名。

- 使用二级索引查询：

  - 查询时，Cassandra会自动使用索引（如果优化器认为合适）。

  - 示例：

    ```cql
    SELECT * FROM orders WHERE status = 'SHIPPED';
    ```

    - 如果`status`列有索引，查询会利用索引。

------

**4. 二级索引的适用场景**

- 低基数列：
  - 列中不同值的数量较少（如状态、类型、布尔值等）。
  - 示例：`status`列只有`'PENDING'`、`'SHIPPED'`、`'DELIVERED'`等值。
- 偶尔查询的列：
  - 如果查询频率较低，二级索引可以提供便利。
- 快速原型开发：
  - 在开发初期，二级索引可以快速实现查询需求，后续再优化。

------

**5. 二级索引的限制与性能问题**

- 高基数列的性能问题：
  - 对高基数列（如用户ID、时间戳等）创建二级索引会导致性能下降。
  - 原因：
    - 每个节点的索引数据可能分散在多个分区中，查询需要跨节点聚合。
    - 索引的维护开销较大，写入性能可能受影响。
- 不支持复杂查询：
  - 二级索引不支持多列组合查询或范围查询。
  - 示例：
    - 无法直接查询`WHERE status = 'SHIPPED' AND order_date > '2023-01-01'`（除非为`order_date`也创建索引）。
- 查询效率较低：
  - 二级索引查询通常比主键查询慢，因为需要额外的网络通信和聚合操作。
- 不适合OLTP场景：
  - 在高吞吐量的在线事务处理（OLTP）场景中，二级索引可能成为瓶颈。

------

**6. 二级索引的替代方案**

由于二级索引的限制，在以下场景中可以考虑其他方案：

- 物质化视图（Materialized View）：

  - 特点：

    - 自动维护的冗余表，支持非主键列的查询。
    - 写入性能较高，查询效率优于二级索引。

  - 示例：

    ```cql
    CREATE MATERIALIZED VIEW orders_by_status AS
    SELECT * FROM orders
    WHERE status IS NOT NULL AND order_id IS NOT NULL
    PRIMARY KEY (status, order_id);
    ```

    - 现在可以高效查询`WHERE status = 'SHIPPED'`。

- 反规范化（Denormalization）：

  - 特点：
    - 创建多个表，每个表的主键设计满足特定查询需求。
    - 写入时需要维护多个表，但查询性能高。
  - 示例：
    - 表1：`orders_by_user`，主键为`(user_id, order_id)`。
    - 表2：`orders_by_status`，主键为`(status, order_id)`。

- 使用外部搜索引擎：

  - 特点：
    - 结合Elasticsearch或Solr等工具，支持全文搜索和复杂查询。
    - 适合需要高级搜索功能的场景。

------

**7. 二级索引的最佳实践**

- 避免对高基数列创建索引：
  - 如用户ID、时间戳、UUID等列不适合创建二级索引。
- 监控索引性能：
  - 使用Cassandra的系统表（如`system_views.indexes`）监控索引的使用情况和性能。
- 评估查询需求：
  - 在设计阶段评估查询需求，优先使用主键或物质化视图。
- 考虑替代方案：
  - 对于复杂查询或高吞吐量场景，优先选择物质化视图或反规范化。

------

**8. 示例：二级索引的实际应用**

假设有一个电商系统，需要存储订单信息，并支持以下查询：

1. 查询所有状态为`'SHIPPED'`的订单。
2. 查询所有用户ID为`123e4567-e89b-12d3-a456-426614174000`的订单（不推荐用二级索引，仅作对比）。

**表设计**：

```cql
CREATE TABLE orders (
    order_id UUID PRIMARY KEY,
    user_id UUID,
    status TEXT,
    order_date TIMESTAMP,
    amount DECIMAL
);
 
-- 为status列创建二级索引
CREATE INDEX IF NOT EXISTS ON orders (status);
```

**查询示例**：

1. 查询状态为

   ```
   'SHIPPED'
   ```

   的订单：

   ```cql
   SELECT * FROM orders WHERE status = 'SHIPPED';
   ```

   - 二级索引有效，查询效率较高（对于低基数列）。

2. 查询用户ID为

   ```
   123e4567-e89b-12d3-a456-426614174000
   ```

   的订单：

   ```cql
   SELECT * FROM orders WHERE user_id = 123e4567-e89b-12d3-a456-426614174000;
   ```

   - 不推荐使用二级索引，性能较差。

   - 更好的方案：

     - 使用物质化视图：

       ```cql
       CREATE MATERIALIZED VIEW orders_by_user AS
       SELECT * FROM orders
       WHERE user_id IS NOT NULL AND order_id IS NOT NULL
       PRIMARY KEY (user_id, order_id);
       ```

     - 或反规范化：创建另一个表`orders_by_user`。

------

**9. 总结**

- 二级索引的作用：
  - 扩展Cassandra的查询能力，支持对非主键列的查询。
  - 适用于低基数列或偶尔查询的场景。
- 二级索引的限制：
  - 不适合高基数列，可能导致性能问题。
  - 不支持复杂查询，查询效率较低。
- 替代方案：
  - 物质化视图：适合需要高效查询非主键列的场景。
  - 反规范化：适合需要支持多种查询模式的场景。
  - 外部搜索引擎：适合需要高级搜索功能的场景。
- 最佳实践：
  - 谨慎使用二级索引，优先评估查询需求和性能影响。
  - 对于关键查询，考虑使用物质化视图或反规范化。

通过合理选择查询机制，可以充分发挥Cassandra的高性能和可扩展性，满足业务需求。



### 物质化视图

>注意：默认配置文件中写着 `Materialized views are considered experimental and are not recommended for production use.`，所以在生产设计中不使用此特性。

#### 介绍

Cassandra 的物化视图（Materialized Views）是 Cassandra 3.0 版本引入的一项功能，旨在简化数据查询的复杂性，同时保持 Cassandra 的分布式特性和高可用性。物化视图通过自动维护冗余数据，允许用户从不同的查询角度访问数据，而无需手动创建和维护多个反规范化表。

------

**核心概念**

1. 物化视图的定义
   - 物化视图是 Cassandra 中基于基表（Base Table）的预计算数据副本，存储在物理表中。
   - 与普通视图（逻辑视图）不同，物化视图会实际存储数据，因此查询性能更高。
   - 物化视图的主键（Primary Key）可以与基表不同，从而支持不同的查询模式。
2. 工作原理
   - 当基表的数据发生变化（插入、更新、删除）时，Cassandra 会自动同步更新物化视图。
   - 物化视图的更新是异步的，因此基表的写入性能不会受到显著影响。
   - 物化视图的数据存储在集群的不同节点上，与基表的数据分布一致。

------

**物化视图的优点**

1. 简化查询
   - 用户可以通过物化视图直接查询数据，而无需编写复杂的 CQL 语句或手动维护多个表。
   - 例如，如果基表的主键是 `(partition_key, clustering_key)`，物化视图可以重新定义主键为 `(new_partition_key, new_clustering_key)`，从而支持新的查询模式。
2. 提高查询性能
   - 物化视图通过预计算和存储冗余数据，避免了查询时的实时计算，显著提高了查询速度。
   - 特别适用于需要频繁查询但不需要实时一致性的场景。
3. 自动维护
   - Cassandra 自动维护物化视图与基表的一致性，减少了开发人员的工作量。
   - 无需手动编写触发器或应用程序逻辑来同步数据。

------

**物化视图的限制**

1. 最终一致性
   - 物化视图的更新是异步的，因此基表和物化视图之间可能存在短暂的不一致。
   - 对于需要强一致性的场景，物化视图可能不适用。
2. 写入性能影响
   - 虽然物化视图的更新是异步的，但在高写入负载下，基表的写入性能仍可能受到轻微影响。
   - 这是因为 Cassandra 需要额外处理物化视图的更新逻辑。
3. 复杂性
   - 物化视图的设计需要谨慎，不当的主键选择可能导致数据分布不均或查询性能下降。
   - 不支持所有 CQL 特性（例如，物化视图不能作为其他物化视图的基表）。
4. 实验性功能
   - 在 Cassandra 4.0 之前，物化视图被标记为实验性功能，可能存在一些已知问题。
   - 在生产环境中使用前，需要进行充分的测试。

------

**使用场景**

- **多维度查询**：当数据需要从不同角度查询时（例如，按时间、按类别、按用户等），物化视图可以提供高效的查询路径。
- **报表和分析**：对于需要频繁聚合或过滤的查询，物化视图可以显著提高性能。
- **简化应用逻辑**：通过物化视图，可以减少应用程序中的复杂查询和数据转换逻辑。

------

**示例**

假设有一个基表 `user_activity`，主键为 `(user_id, activity_time)`，记录用户的活动日志。如果需要频繁按活动类型查询数据，可以创建一个物化视图：

```sql
CREATE MATERIALIZED VIEW user_activity_by_type AS
    SELECT * FROM user_activity
    WHERE user_id IS NOT NULL AND activity_time IS NOT NULL AND activity_type IS NOT NULL
    PRIMARY KEY (activity_type, user_id, activity_time);
```

- 基表的主键是 `(user_id, activity_time)`，支持按用户和时间查询。
- 物化视图的主键是 `(activity_type, user_id, activity_time)`，支持按活动类型查询。

------

**最佳实践**

1. 选择合适的主键
   - 物化视图的主键应基于查询需求设计，确保数据分布均匀。
   - 避免在物化视图中使用高基数列作为分区键（Partition Key），否则可能导致数据热点。
2. 监控性能
   - 使用 Cassandra 的监控工具（如 `nodetool`）监控物化视图的写入延迟和一致性。
   - 在高写入负载下，可能需要调整物化视图的更新策略。
3. 考虑替代方案
   - 如果物化视图无法满足需求，可以考虑使用 Cassandra 的二级索引（Secondary Index）或 SAI（SSTable Attached Indexes）。
   - 对于复杂查询，也可以考虑使用外部工具（如 Spark）进行离线分析。

------

**总结**

Cassandra 的物化视图是一项强大的功能，可以显著简化查询复杂性和提高性能，但需要谨慎设计和使用。在生产环境中，应根据实际需求评估物化视图的适用性，并进行充分的测试和监控。



### SAI 索引

#### 介绍

**存储附带索引（Storage-Attached Indexing，简称 SAI）** 是 Apache Cassandra 中的一种高级索引技术，旨在解决 Cassandra 在数据建模、查询灵活性和运维方面的挑战。以下是 SAI 索引的核心特性和优势：

------

**1. 核心特性**

- **全局分布式与高伸缩性**
  SAI 索引与 Cassandra 的存储引擎深度集成，支持全局分布式部署，能够随着集群规模扩展而自动调整。
- **支持任意列的索引**
  用户可以在表中的任意列上创建 SAI 索引，无需受主键或分区键的限制，从而极大提升了查询灵活性。
- **高效的查询性能**
  SAI 针对不同数据类型（如数字、字符串等）进行了优化，支持快速的范围查询、前缀查询和模糊查询。
- **零运维复杂性**
  SAI 索引与 Cassandra 的核心机制（如快照、模式管理、数据过期）紧密集成，无需额外的配置或复杂的运维操作。
- **与零拷贝串流兼容**
  在集群扩容或节点变更时，SAI 索引会与 SSTables 同步，无需序列化或重建索引，确保数据一致性。

------

**2. 优势分析**

- **简化数据建模**
  传统 Cassandra 数据建模需要为不同查询模式创建多个表，而 SAI 允许通过单一表结构支持多种查询需求，减少了冗余数据和开发复杂度。
- **降低运维成本**
  SAI 索引的维护与 Cassandra 核心功能无缝集成，无需单独管理索引表，降低了运维复杂性和成本。
- **高性能查询**
  SAI 索引在内存和磁盘上对多种数据结构进行过滤，智能返回结果，显著提升了查询效率，尤其适用于大规模数据集。
- **节省存储空间**
  与传统的 Cassandra 索引或外部扩展方案相比，SAI 所需的磁盘使用量更低，因为它不会为每个索引词创建额外的 ngram。

------

**3. 使用场景**

- **复杂查询需求**
  当应用程序需要基于非主键列进行频繁查询时，SAI 索引可以显著提升查询性能。
- **动态查询需求**
  对于查询模式不固定的场景，SAI 索引的灵活性使得用户无需预先设计多个表结构。
- **大规模数据集**
  在数据量庞大的情况下，SAI 索引的高效过滤能力可以减少查询延迟，提升系统响应速度。

------

**4. 示例代码**

以下是一个在 Cassandra 中创建 SAI 索引的示例：

```sql
-- 创建表
CREATE TABLE products (
    id INT,
    time DATE,
    name TEXT,
    price INT,
    PRIMARY KEY (id, time)
);
 
-- 在 name 列上创建 SAI 索引
CREATE CUSTOM INDEX products_name_idx ON products(name)
USING 'StorageAttachedIndex';
 
-- 在 price 列上创建 SAI 索引（支持范围查询）
CREATE CUSTOM INDEX products_price_idx ON products(price)
USING 'StorageAttachedIndex';
 
-- 查询示例
SELECT * FROM products WHERE name = 'example' AND price > 100;
```

------

**5. 注意事项**

- **索引列选择**
  虽然 SAI 支持任意列的索引，但应避免在高基数列（如 UUID）上创建索引，以免影响性能。
- **查询优化**
  SAI 索引的性能依赖于查询模式，建议结合 Cassandra 的分区键和聚类键设计，以实现最佳查询效率。
- **版本兼容性**
  SAI 索引在较新版本的 Cassandra 中支持较好，使用前需确认集群版本是否兼容。



## 反范式设计

### 介绍

#### **1. 什么是反范式设计？**

反范式设计（Denormalization）是一种与关系型数据库范式化（Normalization）相反的数据建模方法。在 Cassandra 中，反范式设计通过**冗余存储数据**或**合并相关数据**来优化查询性能，避免昂贵的表连接操作。

#### **2. 为什么 Cassandra 需要反范式设计？**

- **分布式架构**：Cassandra 是分布式数据库，表连接（JOIN）在分布式环境中效率极低，甚至不支持。
- **查询驱动设计**：Cassandra 的数据模型必须围绕**查询模式**设计，而非数据关系。
- **性能优化**：通过反范式化减少查询时的数据访问次数，提升读取性能。

------

#### **3. 反范式设计的核心原则**

**(1) 查询优先**

- **明确查询需求**：所有表的设计必须基于已知的查询模式。
- **示例**：
  如果需要查询“用户及其订单信息”，可以设计一个合并表，将用户信息和订单信息冗余存储在一起。

**(2) 冗余数据**

- **存储冗余字段**：避免查询时需要访问多个表。
- **示例**：
  在订单表中冗余存储用户姓名，而不是通过外键关联用户表。

**(3) 避免表连接**

- **完全避免 JOIN**：Cassandra 不支持 JOIN 操作，所有数据必须通过单表查询获取。
- **替代方案**：
  使用宽行（Wide Row）或嵌套结构（如 JSON、集合类型）存储相关数据。

**(4) 宽行设计**

- **分区键和聚类键**：
  利用 Cassandra 的分区键（Partition Key）和聚类键（Clustering Key）将相关数据存储在同一分区中。
- **示例**：
  在用户时间线表中，以用户 ID 为分区键，时间戳为聚类键，将用户发布的所有帖子按时间顺序存储在同一分区中。

------

#### **4. 反范式设计的常见模式**

**(1) 宽行模式（Wide Row）**

- **适用场景**：
  需要按某个键（如用户 ID）查询一组相关数据（如用户的时间线、订单历史）。

- 示例：

  ```cql
  CREATE TABLE user_timeline (
      user_id UUID,
      post_time TIMESTAMP,
      post_content TEXT,
      PRIMARY KEY ((user_id), post_time)
  ) WITH CLUSTERING ORDER BY (post_time DESC);
  ```

  - **分区键**：`user_id`，确保同一用户的所有帖子存储在同一分区。
  - **聚类键**：`post_time`，按时间排序。

**(2) 嵌套集合**

- **适用场景**：
  需要存储一对多或多对多关系，且不需要频繁更新。

- 示例：

  ```cql
  CREATE TABLE user_with_tags (
      user_id UUID,
      username TEXT,
      tags SET<TEXT>,  -- 使用集合类型存储标签
      PRIMARY KEY (user_id)
  );
  ```

**(3) 冗余字段**

- **适用场景**：
  避免查询时需要访问多个表。

- 示例：

  ```cql
  CREATE TABLE orders_with_user_info (
      order_id UUID,
      user_id UUID,
      user_name TEXT,  -- 冗余存储用户名
      order_amount DECIMAL,
      PRIMARY KEY (order_id)
  );
  ```

------

#### **5. 反范式设计的优缺点**

**优点**

- **查询性能高**：单表查询即可获取所有数据。
- **适合分布式**：无需表连接，适合 Cassandra 的分布式架构。
- **简单高效**：减少查询复杂度，提升系统吞吐量。

**缺点**

- **数据冗余**：增加存储空间占用。
- **更新复杂**：更新冗余数据时需要同步多个表或字段。
- **一致性维护**：需要确保冗余数据的一致性（可通过 Cassandra 的轻量级事务或应用层逻辑实现）。

------

#### **6. 何时使用反范式设计？**

- **读多写少**：查询频率远高于更新频率。
- **固定查询模式**：查询需求明确且稳定。
- **性能敏感**：需要极致的读取性能。

**何时避免反范式设计？**

- **写频繁**：频繁更新冗余数据会导致性能下降。
- **查询模式多变**：查询需求不确定或频繁变化。

------

#### **7. 反范式设计的最佳实践**

1. **明确查询需求**：
   所有表的设计必须基于已知的查询模式。
2. **合理冗余**：
   只冗余必要的字段，避免过度冗余。
3. **使用宽行和嵌套结构**：
   利用 Cassandra 的分区键和聚类键优化数据布局。
4. **批量更新**：
   更新冗余数据时，尽量使用批量操作（如 BATCH 语句）减少网络开销。
5. **监控一致性**：
   定期检查冗余数据的一致性，必要时通过应用层逻辑修复。

------

#### **8. 示例：用户订单系统**

**需求**：

- 查询用户订单列表。
- 查询订单详情（包括用户信息）。

**反范式设计**：

```cql
-- 订单表（冗余用户信息）
CREATE TABLE orders (
    order_id UUID,
    user_id UUID,
    user_name TEXT,  -- 冗余存储用户名
    order_date TIMESTAMP,
    amount DECIMAL,
    PRIMARY KEY (order_id)
);
 
-- 用户订单索引表（按用户 ID 查询订单）
CREATE TABLE user_orders (
    user_id UUID,
    order_date TIMESTAMP,
    order_id UUID,
    amount DECIMAL,
    PRIMARY KEY ((user_id), order_date, order_id)
) WITH CLUSTERING ORDER BY (order_date DESC);
```

- 查询用户订单列表：

  ```sql
  SELECT order_id, order_date, amount FROM user_orders WHERE user_id = ?;
  ```

- 查询订单详情：

  ```sql
  SELECT * FROM orders WHERE order_id = ?;
  ```

------

#### **总结**

Cassandra 的反范式设计是一种以查询为中心的数据建模方法，通过冗余数据和宽行设计优化读取性能。虽然会增加存储空间和维护复杂度，但在读多写少、查询模式固定的场景下，反范式设计是 Cassandra 数据建模的最佳实践。



### 实践

详细用法请参考本站 [示例](https://gitee.com/dexterleslie/demonstration/tree/main/demo-cassandra/demo-order-management-app)

```CQL
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
    primary key ((user_id,status),create_time)
) with clustering order by (create_time desc);
```

上面 CQL 分别设计 t_order 用于根据订单 id 查询订单信息，t_order_list_by_userId 用于 `select order_id from t_order_list_by_userId where user_id=? and status=? and create_time>=? and create_time<=?` 查询订单列表。



## 服务配置

>使用 cassandra.yaml 配置 Cassandra 服务。

使用 Docker Compose 启动对应版本的 Cassandra 后复制 cassandra.yaml 配置文件

```sh
docker compose cp node1:/etc/cassandra/cassandra.yaml .
```

添加如下配置到配置文件中：

```yaml
read_request_timeout_in_ms: 300000
range_request_timeout_in_ms: 300000
```

创建新的 Cassandra 镜像，注意：不能直接使用 volumes 挂在 cassandra.yaml 配置到容器中，因为 Cassandra 集群中多个节点使用同一个配置文件会冲突。

```dockerfile
FROM cassandra:3.11.4

COPY cassandra.yaml /etc/cassandra/cassandra.yaml
```



## 分页

>详细用法请参考本站 [示例](https://gitee.com/dexterleslie/demonstration/tree/main/demo-cassandra/demo-client-datastax)

表定义：

```CQL
/* 用于协助分页查询 */
CREATE TABLE IF NOT EXISTS t_order_list_by_userId
(
    user_id       bigint,
    status        text,
    create_time   timestamp,
    order_id      bigint,
    primary key ((user_id,status),create_time,order_id)
) with clustering order by (create_time desc,order_id desc);
```

测试分页：

```java
/**
 * 测试分页
 */
@Test
public void testPagination() throws BusinessException {
    // 删除旧数据
    this.commonMapper.truncate("t_order_list_by_userid");

    // 初始化数据
    long userId = 1L;
    String status = "Unpay";
    Instant now = Instant.now();
    int totalCount = 5;
    List<OrderIndexListByUserIdModel> modelList = new ArrayList<>();
    for (int i = 0; i < totalCount; i++) {
        OrderIndexListByUserIdModel model = new OrderIndexListByUserIdModel();
        model.setUserId(userId);
        model.setStatus(status);
        model.setCreateTime(now.atZone(ZoneId.of("Asia/Shanghai")).toLocalDateTime());

        long ordeId = i + 1;
        model.setOrderId(ordeId);
        modelList.add(model);
    }
    this.indexMapper.insertBatchOrderIndexListByUserId(modelList);

    // 获取第一页数据
    String cql = "select create_time,order_id from t_order_list_by_userid where user_id=?" +
            " and status=? and create_time>=? and create_time<=?" +
            " limit ?";
    PreparedStatement preparedStatement = session.prepare(cql);
    BoundStatement boundStatement = preparedStatement.bind(userId, "Unpay", Date.from(now), Date.from(now), 3);
    ResultSet resultSet = session.execute(boundStatement);
    List<Long> orderIdList = new ArrayList<>();
    LocalDateTime createTimeLast = null;
    for (Row row : resultSet) {
        Long orderId = row.getLong("order_id");
        orderIdList.add(orderId);

        if (!resultSet.iterator().hasNext()) {
            java.util.Date createTimeDate = row.getTimestamp("create_time");
            createTimeLast = createTimeDate.toInstant().atZone(ZoneId.of("Asia/Shanghai")).toLocalDateTime();
        }
    }
    Assertions.assertArrayEquals(new Long[]{5L, 4L, 3L}, orderIdList.toArray(new Long[]{}));

    // 获取第二页数据，使用第一页最后 order_id 记录作为边界查询
    cql = "select order_id from t_order_list_by_userid where user_id=?" +
            " and status=? and (create_time,order_id)<(?,?)" +
            " limit ?";
    preparedStatement = session.prepare(cql);
    boundStatement = preparedStatement.bind(userId, "Unpay",
            Date.from(createTimeLast.atZone(ZoneId.of("Asia/Shanghai")).toInstant()), orderIdList.get(orderIdList.size() - 1), 3);
    resultSet = session.execute(boundStatement);
    orderIdList = new ArrayList<>();
    for (Row row : resultSet) {
        Long orderId = row.getLong("order_id");
        orderIdList.add(orderId);
    }
    Assertions.assertArrayEquals(new Long[]{2L, 1L}, orderIdList.toArray(new Long[]{}));
}
```



## 全表记录扫描

### 原理

在 Cassandra 3.11.4 中，分页扫描全表数据可以通过使用 **CQL 的 `LIMIT` 和 `TOKEN` 函数**来实现。由于 Cassandra 是一个分布式数据库，没有直接的“全表扫描”命令，但你可以通过分页查询来模拟这一过程。以下是实现步骤和示例：

------

**实现步骤**

1. 使用 `TOKEN` 函数：
   - Cassandra 使用分区键（Partition Key）来分布数据。通过 `TOKEN` 函数，你可以获取分区键的哈希值，从而在分页查询中跟踪进度。
   - 每次查询时，记录最后一个分区的 `TOKEN` 值，并在下一次查询中使用它作为起始点。
2. 使用 `LIMIT`：
   - 使用 `LIMIT` 子句限制每次查询返回的行数，实现分页。
3. 迭代查询：
   - 通过循环或脚本，逐步增加 `TOKEN` 的起始值，直到所有数据被查询完毕。

------

**示例代码**

假设你有一个表 `t_order`，其分区键是 `order_id`，你想分页查询所有数据：

```cql
-- 第一次查询，获取前 N 条记录
SELECT * FROM t_order LIMIT 100;
 
-- 记录最后一个分区的 order_id 和 TOKEN 值
-- 假设最后一个记录的 order_id 是 'order_100'
-- 使用 cqlsh 的 `TOKEN` 函数获取哈希值
SELECT TOKEN(order_id) FROM t_order WHERE order_id = 'order_100';
 
-- 假设返回的 TOKEN 值是 123456789
-- 第二次查询，从该 TOKEN 值之后开始
SELECT * FROM t_order WHERE TOKEN(order_id) > 123456789 LIMIT 100;
 
-- 重复上述过程，直到没有更多数据返回
```

------

**注意事项**

1. 性能考虑：
   - 全表扫描在 Cassandra 中通常不是推荐的操作，因为它会对集群造成较大压力。尽量在非高峰期执行此类操作。
   - 如果表很大，分页查询可能需要较长时间。
2. 一致性级别：
   - 确保使用合适的一致性级别（如 `LOCAL_QUORUM` 或 `ONE`），以平衡性能和数据一致性。
3. 脚本化：
   - 由于需要多次查询并记录 `TOKEN` 值，建议使用脚本（如 Python、Java 等）自动化这一过程。
4. 替代方案：
   - 如果可能，考虑使用 Spark 或其他批处理工具来处理全表扫描任务，这些工具更适合大数据量的处理。

------

**Python 示例脚本**

以下是一个简单的 Python 脚本示例，使用 `cassandra-driver` 来实现分页查询：

```python
from cassandra.cluster import Cluster
from cassandra.auth import PlainTextAuthProvider
 
# 连接到 Cassandra 集群
auth_provider = PlainTextAuthProvider(username='cassandra', password='cassandra')
cluster = Cluster(['127.0.0.1'], auth_provider=auth_provider)
session = cluster.connect('demo')  # 替换为你的 keyspace
 
# 初始化变量
last_token = None
page_size = 100
 
while True:
    # 构建查询
    if last_token is None:
        query = "SELECT * FROM t_order LIMIT %s"
        params = (page_size,)
    else:
        query = "SELECT * FROM t_order WHERE TOKEN(order_id) > %s LIMIT %s"
        params = (last_token, page_size)
 
    # 执行查询
    rows = session.execute(query, params)
 
    # 处理结果
    for row in rows:
        print(row)  # 替换为你的处理逻辑
 
    # 检查是否还有更多数据
    if not rows or len(rows) < page_size:
        break
 
    # 获取最后一个分区的 TOKEN 值
    last_order_id = rows[-1].order_id
    last_token_row = session.execute("SELECT TOKEN(order_id) FROM t_order WHERE order_id = %s", (last_order_id,))
    last_token = last_token_row[0][0]  # 假设返回的是一个元组，取第一个元素
 
# 关闭连接
cluster.shutdown()
```

------

**总结**

- 在 Cassandra 3.11.4 中，分页扫描全表数据可以通过 `TOKEN` 函数和 `LIMIT` 子句实现。
- 需要记录最后一个分区的 `TOKEN` 值，并在下一次查询中使用它作为起始点。
- 考虑性能影响，尽量在非高峰期执行，或使用批处理工具替代。
- 使用脚本自动化分页查询过程。



### 实验

>详细用法请参考本站 [示例](https://gitee.com/dexterleslie/demonstration/tree/main/demo-cassandra/demo-client-datastax)

```java
/**
 * 测试全表记录扫描
 */
@Test
public void testTableDatumFullyScan() {
    long userId = 1L;
    Instant now = Instant.now();
    long totalCount = 100;

    // 清空 order 表数据
    String cql = "truncate table t_order";
    ResultSet resultSet = session.execute(cql);
    Assertions.assertTrue(resultSet.wasApplied());

    // 准备测试数据
    BatchStatement batch = new BatchStatement(BatchStatement.Type.LOGGED);
    for (int i = 0; i < totalCount; i++) {
        BoundStatement bound = preparedStatementOrderInsertion.bind(
                BigDecimal.valueOf(1L + i), // id
                userId,                        // user_id
                "Unpay",                      // status
                // https://stackoverflow.com/questions/39926022/codec-not-found-for-requested-operation-timestamp-java-lang-long
                Date.from(now),                // pay_time
                null,                         // delivery_time
                null,                         // received_time
                null,                         // cancel_time
                "Normal",                     // delete_status
                Date.from(now)                 // create_time
        );
        batch = batch.add(bound);
    }
    resultSet = session.execute(batch);
    Assertions.assertTrue(resultSet.wasApplied());

    List<Long> orderIdListFetched = new ArrayList<>();
    // 先获取第一条数据并记录订单id
    cql = "select * from t_order limit 1";
    resultSet = session.execute(cql);
    Row row = resultSet.one();
    long orderId = row.getDecimal("id").longValue();
    orderIdListFetched.add(orderId);

    while (true) {
        cql = "select token(id) from t_order where id=" + orderId;
        resultSet = session.execute(cql);
        row = resultSet.one();
        long token = row.getLong(0);
        cql = "select * from t_order where token(id)>" + token + " limit 100000";
        resultSet = session.execute(cql);

        if (!resultSet.iterator().hasNext()) {
            // 没有更多数据时退出
            break;
        }

        for (Row rowInternal : resultSet) {
            orderId = rowInternal.getDecimal("id").longValue();
            orderIdListFetched.add(orderId);
        }
    }

    Assertions.assertEquals(totalCount, orderIdListFetched.size());
    for (int i = 0; i < totalCount; i++) {
        orderId = 1L + i;
        Assertions.assertTrue(orderIdListFetched.contains(orderId));
    }
}
```



## `in` 查询



### 基本用法

>详细用法请参考本站 [示例](https://gitee.com/dexterleslie/demonstration/tree/main/demo-cassandra/demo-client-datastax)

```java
/**
 * 测试in查询
 */
@Test
public void testQueryWhereIn() {
    long userId = 1L;
    Instant now = Instant.now();
    long totalCount = 100;

    // 清空 order 表数据
    String cql = "truncate table t_order";
    ResultSet resultSet = session.execute(cql);
    Assertions.assertTrue(resultSet.wasApplied());

    // 准备测试数据
    BatchStatement batch = new BatchStatement(BatchStatement.Type.LOGGED);
    for (int i = 0; i < totalCount; i++) {
        BoundStatement bound = preparedStatementOrderInsertion.bind(
                BigDecimal.valueOf(1L + i), // id
                userId,                        // user_id
                "Unpay",                      // status
                // https://stackoverflow.com/questions/39926022/codec-not-found-for-requested-operation-timestamp-java-lang-long
                Date.from(now),                // pay_time
                null,                         // delivery_time
                null,                         // received_time
                null,                         // cancel_time
                "Normal",                     // delete_status
                Date.from(now)                 // create_time
        );
        batch = batch.add(bound);
    }
    resultSet = session.execute(batch);
    Assertions.assertTrue(resultSet.wasApplied());

    // 测试in查询
    cql = "select * from t_order where id in(?,?)";
    PreparedStatement preparedStatement = this.session.prepare(cql);
    BoundStatement boundStatement = preparedStatement.bind(Arrays.asList(BigDecimal.valueOf(1L), BigDecimal.valueOf(2L)).toArray(new BigDecimal[0]));
    resultSet = this.session.execute(boundStatement);
    List<Long> orderIdListFetched = new ArrayList<>();
    for (Row rowInternal : resultSet) {
        long orderId = rowInternal.getDecimal("id").longValue();
        orderIdListFetched.add(orderId);
    }
    Assertions.assertArrayEquals(new Long[]{1L, 2L}, orderIdListFetched.toArray(new Long[0]));
}
```



### `in` 查询多个分区键数据没有全局排序关系

>详细用法请参考本站 [示例](https://gitee.com/dexterleslie/demonstration/tree/main/demo-cassandra/demo-client-datastax)

```java
/**
 * 测试 order by 排序
 */
@Test
public void testOrderBy() throws BusinessException, InterruptedException {
    // region 测试因为 where in 查询导致全局排序不符合预期异常

    // Cassandra 的数据按分区键（Partition Key）分散存储在不同的节点上，
    // 每个分区内的数据按聚类列（Clustering Columns）的顺序排序（即表定义的 clustering order by）。
    // 但不同分区之间的数据没有全局排序关系。

    // 删除旧数据
    this.commonMapper.truncate("t_order_list_by_userid");

    // 初始化数据
    long userId = 1L;
    List<String> statusList = new ArrayList<String>() {{
        add("Unpay");
        add("Undelivery");
        add("Unreceive");
        add("Received");
        add("Canceled");
    }};
    int totalCount = 5;
    List<OrderIndexListByUserIdModel> modelList = new ArrayList<>();
    for (int i = 0; i < totalCount; i++) {
        OrderIndexListByUserIdModel model = new OrderIndexListByUserIdModel();
        model.setUserId(userId);

        int randomIndex = RandomUtil.randomInt(0, statusList.size());
        String status = statusList.get(randomIndex);
        model.setStatus(status);

        Instant now = Instant.now();
        model.setCreateTime(now.atZone(ZoneId.of("Asia/Shanghai")).toLocalDateTime());

        long ordeId = i + 1;
        model.setOrderId(ordeId);
        modelList.add(model);
        TimeUnit.MILLISECONDS.sleep(100);
    }
    this.indexMapper.insertBatchOrderIndexListByUserId(modelList);

    Instant now = Instant.now();
    Instant startTime = now.minusSeconds(60);
    Instant endTime = now.plusSeconds(60);
    String cql = "select create_time,order_id from t_order_list_by_userid where user_id=?" +
            " and status in('Unpay','Undelivery','Unreceive','Received','Canceled') and create_time>=? and create_time<=?" +
            " limit ?";
    PreparedStatement preparedStatement = session.prepare(cql);
    BoundStatement boundStatement = preparedStatement.bind(userId, Date.from(startTime), Date.from(endTime), 3);
    ResultSet resultSet = session.execute(boundStatement);
    List<Long> orderIdList = new ArrayList<>();
    for (Row row : resultSet) {
        Long orderId = row.getLong("order_id");
        orderIdList.add(orderId);
    }

    boolean notEquals = false;
    for (int i = 0; i < new Long[]{5L, 4L, 3L}.length; i++) {
        Long orderId = orderIdList.toArray(new Long[0])[i];
        if (!new Long[]{5L, 4L, 3L}[i].equals(orderId)) {
            notEquals = true;
            break;
        }
    }
    Assertions.assertTrue(notEquals);

    // endregion
}
```



## 集群管理



### 查看集群状态

登录集群中其中一个节点运行以下命令

```sh
nodetool status
```



### 删除节点

如果目标节点未宕机按照下面步骤删除节点：

- 登录到目标节点（`192.168.1.93`）上执行以下命令，让节点安全地退出集群并迁移数据：

  ```sh
  nodetool decommission
  ```



如果目标节点宕机按照下面步骤删除节点：

- 删除节点

  ```sh
  nodetool removenode <host_id>
  ```

  - `<host-id>` 使用 `nodetool status` 命令获取。



### 加入并退出集群

使用本站 [示例](https://gitee.com/dexterleslie/demonstration/tree/main/demo-cassandra/demo-client-datastax) 协助测试。

`.env`：

```ini
cassandra_broadcast_rpc_address=192.168.1.181

```

启动第一、二个集群节点 `docker-compose.yaml`：

```yaml
version: "3.1"

services:
    node0:
      image: cassandra:3.11.4
      environment:
        - MAX_HEAP_SIZE=1G
        # 是MAX_HEAP_SIZE的1/4
        - HEAP_NEWSIZE=256M
        - CASSANDRA_SEEDS=node0
        # 节点向客户端广播的地址是宿主机可访问的 IP
        - CASSANDRA_BROADCAST_RPC_ADDRESS=${cassandra_broadcast_rpc_address}
      volumes:
        - ./init.cql:/scripts/data.cql:ro
#      network_mode: host
      ports:
        - "9042:9042"   # CQL 客户端端口
    node1:
      image: cassandra:3.11.4
      environment:
        - MAX_HEAP_SIZE=1G
        # 是MAX_HEAP_SIZE的1/4
        - HEAP_NEWSIZE=256M
        - CASSANDRA_SEEDS=node0
        # 节点向客户端广播的地址是宿主机可访问的 IP
        - CASSANDRA_BROADCAST_RPC_ADDRESS=${cassandra_broadcast_rpc_address}
      ports:
        - "9043:9042"   # CQL 客户端端口
```

使用 `nodetool status` 查看集群状态只有一个节点。

启动应用并使用 `wrk` 协助模拟业务逻辑在持续进行中

```sh
ab -n 20000 -c 32 -k http://localhost:8080/api/v1/testAssistMigrationGenerateDatum
```

运行约 `1` 分钟后执行下面操作。

加入集群：

- 启动第三个集群节点：

  ```yaml
  version: "3.1"
  
  services:
      node0:
        image: cassandra:3.11.4
        environment:
          - MAX_HEAP_SIZE=1G
          # 是MAX_HEAP_SIZE的1/4
          - HEAP_NEWSIZE=256M
          - CASSANDRA_SEEDS=node0
          # 节点向客户端广播的地址是宿主机可访问的 IP
          - CASSANDRA_BROADCAST_RPC_ADDRESS=${cassandra_broadcast_rpc_address}
        volumes:
          - ./init.cql:/scripts/data.cql:ro
  #      network_mode: host
        ports:
          - "9042:9042"   # CQL 客户端端口
      node1:
        image: cassandra:3.11.4
        environment:
          - MAX_HEAP_SIZE=1G
          # 是MAX_HEAP_SIZE的1/4
          - HEAP_NEWSIZE=256M
          - CASSANDRA_SEEDS=node0
          # 节点向客户端广播的地址是宿主机可访问的 IP
          - CASSANDRA_BROADCAST_RPC_ADDRESS=${cassandra_broadcast_rpc_address}
        ports:
          - "9043:9042"   # CQL 客户端端口
      node2:
        image: cassandra:3.11.4
        environment:
          - MAX_HEAP_SIZE=1G
          # 是MAX_HEAP_SIZE的1/4
          - HEAP_NEWSIZE=256M
          - CASSANDRA_SEEDS=node0
          # 节点向客户端广播的地址是宿主机可访问的 IP
          - CASSANDRA_BROADCAST_RPC_ADDRESS=${cassandra_broadcast_rpc_address}
        ports:
          - "9044:9042"   # CQL 客户端端口
  
  ```

  使用 `nodetool status` 查看集群状态有三个节点。

  ```sh
  $ nodetool status
  Datacenter: datacenter1
  =======================
  Status=Up/Down
  |/ State=Normal/Leaving/Joining/Moving
  --  Address      Load       Tokens       Owns (effective)  Host ID                               Rack
  UN  172.20.29.4  70.97 KiB  256          33.0%             d561c19a-a62e-401c-b16b-b347eb79fe62  rack1
  UN  172.20.29.2  81.11 KiB  256          33.8%             c2ab771e-f5b9-42c8-9ab8-4dfdc86a3f7c  rack1
  UN  172.20.29.3  70.96 KiB  256          33.3%             97716519-7e1c-4ff4-8450-79383755414c  rack1
  
  ```



退出集群：

- 登录到第一个节点并退出集群：

  ```sh
  $ docker compose exec -it node0 bash
  $ nodetool decommission
  # 等待命令执行完毕
  ```

- 等待 `wrk` 执行完毕后调用接口 `http://localhost:8080/api/v1/testAssistMigrationGetTotalCount` 返回 `"data": 20000` 表示没有丢失数据。




## 数据类型

### `counter`

>详细用法请参考本站 [示例](https://gitee.com/dexterleslie/demonstration/tree/main/demo-cassandra/demo-client-datastax)

`counter` 类型是一种专门用于计数操作的特殊数据类型。它被设计用来处理高并发的计数器场景，例如页面浏览计数、用户登录次数统计等。与常规的列类型不同，`counter` 类型有一些特定的行为和限制。`counter` 类型的列支持原子性的增减操作，这意味着你可以安全地在多线程或多节点环境下进行并发更新，而不会出现竞争条件。`counter` 列必须存储在专用的表中，不能与其他普通列混合在同一表中。这意味着如果你需要在一个表中同时使用普通列和计数器列，你需要创建两个表。`counter` 列只能通过 `UPDATE` 语句进行增加或减少操作，不能直接设置具体的值。

定义 `counter` 类型 `CQL`

```CQL
/* 用于协助并发update同一条数据 */
create table if not exists t_count (
    flag        text,
    count       counter,
    primary key (flag)
);

/* 初始化 count 的值，不能直接 set，否则报错 */
update t_count set count=count+0 where flag='order';
```

代码中增加 `counter` 类型计数

```java
/**
 * 测试并发更新同一条数据时数据是否一致
 */
@Test
public void testUpdateConcurrently() throws InterruptedException {
    // 重置 t_count 计数
    String cql = "select count from t_count where flag='order'";
    ResultSet resultSet = session.execute(cql);
    Row row = resultSet.one();
    long count = row.getLong("count");
    cql = "update t_count set count=count-" + count + " where flag='order'";
    resultSet = session.execute(cql);
    Assertions.assertTrue(resultSet.wasApplied());

    cql = "update t_count set count=count+? where flag='order'";
    PreparedStatement preparedStatement = session.prepare(cql);
    preparedStatement.setConsistencyLevel(ConsistencyLevel.LOCAL_ONE);

    long totalCount = 10000;
    int concurrentThreads = 128;
    AtomicInteger counter = new AtomicInteger();
    ExecutorService threadPool = Executors.newCachedThreadPool();
    for (int i = 0; i < concurrentThreads; i++) {
        threadPool.submit(() -> {
            try {
                while (true) {
                    int countInternal = counter.getAndIncrement();
                    if (countInternal >= totalCount) {
                        break;
                    }

                    // 于 cassandra counter 类型列运算的数据类型只能为 long 类型
                    BoundStatement boundStatement = preparedStatement.bind(1L);
                    ResultSet resultSet1 = session.execute(boundStatement);
                    Assertions.assertTrue(resultSet1.wasApplied());
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });
    }
    threadPool.shutdown();
    while (!threadPool.awaitTermination(100, TimeUnit.MILLISECONDS)) ;

    cql = "select * from t_count where flag='order'";
    resultSet = session.execute(cql);
    row = resultSet.one();
    count = row.getLong("count");
    Assertions.assertEquals(totalCount, count);
}
```



### 时间类型 - `timestamp`

>提醒：`Cassandra` 会自动把 `java.util.Date` 类型的对象转换为 `GMT` 时区保存，所以在 `cqlsh` 中查询到的数据为 `GMT` 时区的数据格式，例如：`2017-02-02 18:23:07.000000+0000`。在插入和读取数据时需要转换读取到的 `timestamp` 到当前时区对应的时间。
>
>详细用法请参考本站 [示例](https://gitee.com/dexterleslie/demonstration/tree/main/demo-cassandra/demo-client-datastax)

表结构：

```CQL
CREATE TABLE IF NOT EXISTS t_order
(
    id            decimal PRIMARY KEY,
    user_id       bigint,
    status        text,      -- 使用text代替ENUM，Cassandra不支持ENUM类型
    pay_time      timestamp, -- 使用timestamp代替datetime
    delivery_time timestamp,
    received_time timestamp,
    cancel_time   timestamp,
    delete_status text,
    create_time   timestamp
);
```

插入数据时，转换为当前时区的 `java.util.Date` 对象：

```java
private PreparedStatement preparedStatementOrderInsertion;

@PostConstruct
public void init() {
    // cql 只需要 prepare 一次
    String cql = "INSERT INTO t_order (id, user_id, status, pay_time, delivery_time, received_time, cancel_time, delete_status, create_time) " +
            "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
    this.preparedStatementOrderInsertion = session.prepare(cql);
    this.preparedStatementOrderInsertion.setConsistencyLevel(ConsistencyLevel.LOCAL_QUORUM);
}

long userId = 1001L;
ZoneId zoneId = ZoneId.of("Asia/Shanghai");
LocalDateTime localDateTimeNow = LocalDateTime.now().truncatedTo(ChronoUnit.MILLIS);
BigDecimal orderId = BigDecimal.valueOf(1L);

Date datePayTime = Date.from(localDateTimeNow.atZone(zoneId).toInstant());
Date dateCreateTime = Date.from(localDateTimeNow.atZone(zoneId).toInstant());
BoundStatement bound = preparedStatementOrderInsertion.bind(
    orderId, // id
    userId,                  // user_id
    "Unpay",                // status
    datePayTime,          // pay_time
    null,                   // delivery_time
    null,                   // received_time
    null,                   // cancel_time
    "Normal",               // delete_status
    dateCreateTime           // create_time
);

ResultSet result = session.execute(bound);
Assertions.assertTrue(result.wasApplied());
```

读取数据时，转换为当前时区的 `LocalDateTime` 对象：

```java
private PreparedStatement preparedStatementOrderSelectById;

@PostConstruct
public void init() {
    String cql = "select * from t_order where id=?";
    this.preparedStatementOrderSelectById = session.prepare(cql);
    this.preparedStatementOrderSelectById.setConsistencyLevel(ConsistencyLevel.LOCAL_ONE);
}

bound = preparedStatementOrderSelectById.bind(orderId);
result = session.execute(bound);
List<Row> rowList = result.all();
Assertions.assertEquals(1, rowList.size());
Assertions.assertEquals(orderId, rowList.get(0).getDecimal("id"));
LocalDateTime actualCreateTime = rowList.get(0).getTimestamp("create_time").toInstant().atZone(zoneId).toLocalDateTime();
Assertions.assertEquals(localDateTimeNow, actualCreateTime);
```

根据时间条件查询数据时，转换为当前时区的 `java.util.Date` 对象：

```java
// 测试根据时间查询
String cql = "select * from t_order where create_time>=? and create_time<=? ALLOW FILTERING";
PreparedStatement preparedStatement = session.prepare(cql);
preparedStatement.setConsistencyLevel(ConsistencyLevel.LOCAL_ONE);
bound = preparedStatement.bind(dateCreateTime, dateCreateTime);
result = session.execute(bound);
rowList = result.all();
Assertions.assertEquals(1, rowList.size());
Assertions.assertEquals(orderId, rowList.get(0).getDecimal("id"));
```



## `upsert` 特性

### 介绍

在Cassandra中，**Upsert（更新或插入）** 是一种常见的操作模式，其核心逻辑是：**根据主键判断记录是否存在，若存在则更新，若不存在则插入**。Cassandra本身没有独立的“Upsert”语法，但通过`INSERT`语句结合主键的唯一性约束，天然实现了Upsert的能力。

**一、Upsert的核心原理**

Cassandra的表通过**主键（Primary Key）** 唯一标识一行数据（主键包括分区键和可选的聚类列）。当执行`INSERT`语句时：  
- 如果主键对应的行**不存在**，则插入新行；  
- 如果主键对应的行**已存在**，则更新该行中指定的列（未指定的列保持原有值）。  

**二、基础Upsert实现（无条件覆盖）**

最基础的Upsert操作直接使用`INSERT`语句，无需额外语法。Cassandra会根据主键自动判断是插入还是更新。

**示例表结构**

假设存在表`t_order`，主键为`id`（`DECIMAL`类型）：
```sql
CREATE TABLE IF NOT EXISTS t_order (
    id            DECIMAL PRIMARY KEY,  -- 主键（分区键）
    user_id       BIGINT,
    status        TEXT,
    pay_time      TIMESTAMP,
    create_time   TIMESTAMP
);
```

**Upsert操作示例**

当执行以下`INSERT`语句时：  
- 若`id=1`不存在，则插入新行；  
- 若`id=1`已存在，则更新`user_id`、`status`、`pay_time`、`create_time`字段（未指定的字段如`id`保持不变）。

```sql
INSERT INTO t_order (id, user_id, status, pay_time, create_time)
VALUES (1, 1001, 'Unpay', toTimestamp('2025-06-30 10:05:39'), toTimestamp('2025-06-30 10:05:39'));
```

**三、条件Upsert（使用Lightweight Transactions）**

默认的`INSERT`语句是无条件的Upsert（总是覆盖或插入）。若需要**仅在特定条件下执行更新**（例如“仅当某字段为特定值时更新”），需使用Cassandra的**轻量级事务（Lightweight Transactions, LWT）**，通过`IF`子句实现。

**适用场景**  

例如：仅当订单状态为`Unpay`时，才允许更新为`Paid`。

**语法示例**

```sql
UPDATE t_order 
SET status = 'Paid', pay_time = toTimestamp('2025-06-30 10:10:00') 
WHERE id = 1 
IF status = 'Unpay'; -- 仅当当前状态为'Unpay'时执行更新
```

**执行结果**  

- 若条件满足（`status='Unpay'`），则更新成功；  
- 若条件不满足（如`status='Paid'`），则返回`[applied] = false`，表示未执行更新。

**四、批量Upsert**

若需要批量执行多个Upsert操作，可使用Cassandra的`BATCH`语句。批量操作需注意：  
- 所有操作必须针对**同一分区键**（否则可能降低性能）；  
- 批量操作是原子性的（要么全部成功，要么全部失败）。

**示例：批量Upsert**

```sql
BEGIN BATCH
    INSERT INTO t_order (id, user_id, status, pay_time, create_time) 
    VALUES (1, 1001, 'Unpay', toTimestamp('2025-06-30 10:05:39'), toTimestamp('2025-06-30 10:05:39'));
    
    UPDATE t_order 
    SET status = 'Processing', delivery_time = toTimestamp('2025-06-30 14:00:00') 
    WHERE id = 1;
APPLY BATCH;
```

**五、注意事项**

1. **主键设计的重要性**：  
   Upsert的行为完全依赖主键的唯一性。需根据业务需求设计合理的主键（如订单ID、用户ID+时间戳等），避免主键冲突或数据分布不均。

2. **性能影响**：  
   - 无条件Upsert（普通`INSERT`）性能极高（O(1)复杂度）；  
   - 条件Upsert（LWT）需通过Paxos协议协商，性能较低（建议仅在必要时使用）；  
   - 批量Upsert需避免跨分区操作，否则可能引发性能问题。

3. **字段覆盖规则**：  
   `INSERT`或`UPDATE`语句中仅指定部分字段时，未指定的字段**保持原有值**（不会被置为`NULL`）。例如：  
   ```sql
   INSERT INTO t_order (id, user_id) VALUES (1, 1001); -- 仅更新user_id，其他字段（如status）保持不变
   ```

4. **时间戳的处理**：  
   Cassandra的`timestamp`类型存储UTC时间。若业务需要记录本地时间（如上海时区），需在应用层将本地时间转换为UTC后再存储（读取时再转换回本地时区）。

**六、Java代码示例（使用DataStax驱动）**

以下是使用Java DataStax驱动实现Upsert的示例代码：

**1. 无条件Upsert（插入或更新）**

```java
import com.datastax.driver.core.PreparedStatement;
import com.datastax.driver.core.Session;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

public class CassandraUpsertExample {

    private final Session session;

    public CassandraUpsertExample(Session session) {
        this.session = session;
    }

    /**
     * 执行Upsert（插入或更新）
     */
    public void upsertOrder(BigDecimal orderId, Long userId, String status, LocalDateTime payTime, LocalDateTime createTime) {
        String cql = "INSERT INTO t_order (id, user_id, status, pay_time, create_time) " +
                     "VALUES (?, ?, ?, ?, ?)";

        PreparedStatement prepared = session.prepare(cql);
        prepared.setConsistencyLevel(ConsistencyLevel.LOCAL_QUORUM); // 设置一致性级别

        Date payTimeDate = Date.from(payTime.atZone(ZoneId.of("Asia/Shanghai")).toInstant());
        Date createTimeDate = Date.from(createTime.atZone(ZoneId.of("Asia/Shanghai")).toInstant());

        session.execute(prepared.bind(
            orderId,
            userId,
            status,
            payTimeDate,
            createTimeDate
        ));
    }
}
```

**2. 条件Upsert（使用LWT）**

```java
public boolean conditionalUpdateStatus(Long orderId, String newStatus, String expectedStatus) {
    String cql = "UPDATE t_order " +
                 "SET status = ? " +
                 "WHERE id = ? " +
                 "IF status = ?";

    PreparedStatement prepared = session.prepare(cql);
    ResultSet result = session.execute(prepared.bind(
        newStatus,
        orderId,
        expectedStatus
    ));

    // 检查是否应用成功（[applied]为true表示条件满足并更新）
    Row row = result.one();
    return row != null && row.getBool("[applied]");
}
```

**总结**

Cassandra的Upsert通过`INSERT`语句的天然特性实现，核心是根据主键判断记录是否存在。对于需要条件更新的场景，可使用轻量级事务（LWT）。实际开发中需注意主键设计、性能优化和时区处理，以确保数据一致性和操作效率。



### 实验

>详细用法请参考本站 [示例](https://gitee.com/dexterleslie/demonstration/tree/main/demo-cassandra/demo-client-datastax)

示例测试的场景如下：

- `primary key(key1)` 为主键，插入新数据时会覆盖之前的数据，导致只有一条数据存在。
- `primary key((key1),key2)` 为主键（包含聚类键），插入新数据时不会覆盖之前的数据，导致有两条数据存在。
- `primary key(key1,key2)` 为主键（不包含聚类键），插入新数据时不会覆盖之前的数据，导致有两条数据存在。

表结构如下：

```CQL
/* 用于协助测试 upsert */
drop table if exists t_upsert_test1;
create table if not exists t_upsert_test1 (
    key1 int,
    key2 text,
    value text,
    primary key ( key1 )
);
drop table if exists t_upsert_test2;
create table if not exists t_upsert_test2 (
  key1 int,
  key2 text,
  value text,
  primary key ( (key1), key2 )
);
drop table if exists t_upsert_test3;
create table if not exists t_upsert_test3 (
  key1 int,
  key2 text,
  value text,
  primary key ( key1, key2 )
);
```

测试代码如下：

```java
/**
 * 测试 upsert 特性
 *
 * @throws BusinessException
 */
@Test
public void testUpsert() throws BusinessException {
    // 删除所有数据
    this.commonMapper.truncate("t_upsert_test1");
    this.commonMapper.truncate("t_upsert_test2");
    this.commonMapper.truncate("t_upsert_test3");

    // region 测试 primary key(key1) 为主键，插入新数据时会覆盖之前的数据，导致只有一条数据存在

    String cql = "insert into t_upsert_test1(key1,key2,value) values(?,?,?)";
    PreparedStatement preparedStatement = session.prepare(cql);
    BoundStatement boundStatement = preparedStatement.bind(1, "a", "v1");
    ResultSet resultSet = session.execute(boundStatement);
    Assertions.assertTrue(resultSet.wasApplied());

    cql = "insert into t_upsert_test1(key1,key2,value) values(?,?,?)";
    preparedStatement = session.prepare(cql);
    boundStatement = preparedStatement.bind(1, "b", "v2");
    resultSet = session.execute(boundStatement);
    Assertions.assertTrue(resultSet.wasApplied());

    cql = "select * from t_upsert_test1";
    preparedStatement = session.prepare(cql);
    boundStatement = preparedStatement.bind();
    resultSet = session.execute(boundStatement);
    List<Row> rowList = resultSet.all();
    Assertions.assertEquals(1, rowList.size());
    Assertions.assertEquals(1, rowList.get(0).getInt("key1"));
    Assertions.assertEquals("b", rowList.get(0).getString("key2"));
    Assertions.assertEquals("v2", rowList.get(0).getString("value"));

    // endregion

    // region 测试 primary key((key1),key2) 为主键（包含聚类键），插入新数据时不会覆盖之前的数据，导致有两条数据存在

    cql = "insert into t_upsert_test2(key1,key2,value) values(?,?,?)";
    preparedStatement = session.prepare(cql);
    boundStatement = preparedStatement.bind(1, "a", "v1");
    resultSet = session.execute(boundStatement);
    Assertions.assertTrue(resultSet.wasApplied());

    cql = "insert into t_upsert_test2(key1,key2,value) values(?,?,?)";
    preparedStatement = session.prepare(cql);
    boundStatement = preparedStatement.bind(1, "b", "v2");
    resultSet = session.execute(boundStatement);
    Assertions.assertTrue(resultSet.wasApplied());

    cql = "select * from t_upsert_test2";
    preparedStatement = session.prepare(cql);
    boundStatement = preparedStatement.bind();
    resultSet = session.execute(boundStatement);
    rowList = resultSet.all();
    Assertions.assertEquals(2, rowList.size());
    Assertions.assertEquals(1, rowList.get(0).getInt("key1"));
    Assertions.assertEquals("a", rowList.get(0).getString("key2"));
    Assertions.assertEquals("v1", rowList.get(0).getString("value"));
    Assertions.assertEquals(1, rowList.get(1).getInt("key1"));
    Assertions.assertEquals("b", rowList.get(1).getString("key2"));
    Assertions.assertEquals("v2", rowList.get(1).getString("value"));

    // endregion

    // region 测试 primary key(key1,key2) 为主键（不包含聚类键），插入新数据时不会覆盖之前的数据，导致有两条数据存在

    cql = "insert into t_upsert_test3(key1,key2,value) values(?,?,?)";
    preparedStatement = session.prepare(cql);
    boundStatement = preparedStatement.bind(1, "a", "v1");
    resultSet = session.execute(boundStatement);
    Assertions.assertTrue(resultSet.wasApplied());

    cql = "insert into t_upsert_test3(key1,key2,value) values(?,?,?)";
    preparedStatement = session.prepare(cql);
    boundStatement = preparedStatement.bind(1, "b", "v2");
    resultSet = session.execute(boundStatement);
    Assertions.assertTrue(resultSet.wasApplied());

    cql = "select * from t_upsert_test3";
    preparedStatement = session.prepare(cql);
    boundStatement = preparedStatement.bind();
    resultSet = session.execute(boundStatement);
    rowList = resultSet.all();
    Assertions.assertEquals(2, rowList.size());
    Assertions.assertEquals(1, rowList.get(0).getInt("key1"));
    Assertions.assertEquals("a", rowList.get(0).getString("key2"));
    Assertions.assertEquals("v1", rowList.get(0).getString("value"));
    Assertions.assertEquals(1, rowList.get(1).getInt("key1"));
    Assertions.assertEquals("b", rowList.get(1).getString("key2"));
    Assertions.assertEquals("v2", rowList.get(1).getString("value"));

    // endregion
}
```



## 更新主键

### 介绍

在Cassandra中，**主键（Primary Key）是不可直接更新的**，这是由其底层数据模型和存储设计决定的核心特性。以下是详细解释：

1. **主键的不可变性（Immutability）**

Cassandra的主键由**分区键（Partition Key）**和可选的**聚类列（Clustering Columns）**组成，其核心作用是：
- **唯一标识一行数据**（通过分区键+聚类列的组合）；
- **决定数据在集群中的分布**（数据按分区键哈希后存储到特定节点）；
- **定义同一分区内的排序规则**（聚类列决定数据在分区内的物理顺序）。

由于主键直接影响数据的存储位置和查询效率，Cassandra设计时强制要求主键字段在数据插入后**不可修改**。如果允许更新主键，会导致数据需要重新分布到新的分区节点，引发大量数据迁移，严重影响性能和一致性。

2. **尝试更新主键的结果**

如果通过`UPDATE`语句尝试修改主键字段（无论是分区键还是聚类列），Cassandra会直接返回错误（例如：`InvalidQueryException: Cannot update primary key column`）。这是因为主键字段在底层存储中被标记为不可变，不允许修改。

**3. 替代方案：插入新记录并删除旧记录**

如果业务场景需要“修改主键”（例如用户ID变更），正确的做法是：
1. **插入新记录**：使用新的主键值，复制原记录的其他字段；
2. **删除旧记录**：删除原主键对应的旧记录。

示例（假设原主键为`user_id`）：
```sql
-- 插入新记录（新主键为 new_user_id）
INSERT INTO users (user_id, name, email) VALUES ('new_user_id', 'Alice', 'alice@new.com');

-- 删除旧记录（原主键为 old_user_id）
DELETE FROM users WHERE user_id = 'old_user_id';
```

**注意事项：**

- **事务一致性**：Cassandra不支持跨行原子事务，因此需应用层确保“插入+删除”操作的原子性（例如通过业务逻辑保证，或使用轻量级事务LWT限制旧记录在删除前未被修改）。
- **关联数据处理**：若旧记录被其他表引用（如外键），需同步清理关联数据（Cassandra无原生外键约束，需应用层维护）。

**总结**

Cassandra**禁止直接更新主键**，因为主键的不可变性是其存储和查询优化的基础。若需变更主键，应通过“插入新记录+删除旧记录”的方式间接实现，并注意应用层的一致性保障。



### 实验

>提示：更新主键的方案为：先删除旧数据，再插入新数据。
>
>详细用法请参考本站 [示例](https://gitee.com/dexterleslie/demonstration/tree/main/demo-cassandra/demo-client-datastax)

测试代码如下：

```java
/**
 * 测试禁止更新主键
 */
@Test
public void testUpdatePrimaryKeyProhibited() throws BusinessException {
    // 删除所有数据
    this.commonMapper.truncate("t_upsert_test1");
    this.commonMapper.truncate("t_upsert_test2");
    this.commonMapper.truncate("t_upsert_test3");

    int key1 = 1;
    String key2 = "a";
    String cql = "insert into t_upsert_test2(key1,key2,value) values(?,?,?)";
    PreparedStatement preparedStatement = session.prepare(cql);
    BoundStatement boundStatement = preparedStatement.bind(1, "a", "v1");
    ResultSet resultSet = session.execute(boundStatement);
    Assertions.assertTrue(resultSet.wasApplied());

    // 测试更新非主键
    cql = "update t_upsert_test2 set value=? where key1=? and key2=?";
    preparedStatement = session.prepare(cql);
    boundStatement = preparedStatement.bind("v2", key1, key2);
    resultSet = session.execute(boundStatement);
    Assertions.assertTrue(resultSet.wasApplied());

    cql = "select * from t_upsert_test2";
    preparedStatement = session.prepare(cql);
    boundStatement = preparedStatement.bind();
    resultSet = session.execute(boundStatement);
    List<Row> rowList = resultSet.all();
    Assertions.assertEquals(1, rowList.size());
    Assertions.assertEquals("v2", rowList.get(0).getString("value"));

    // region 测试更新主键

    try {
        cql = "update t_upsert_test2 set key2=? where key1=? and key2=?";
        // 抛出 com.datastax.driver.core.exceptions.InvalidQueryException: PRIMARY KEY part key2 found in SET part
        session.prepare(cql);
        /*boundStatement = preparedStatement.bind("b", key1, key2);
        resultSet = session.execute(boundStatement);
        Assertions.assertTrue(resultSet.wasApplied());*/
        Assertions.fail();
    } catch (InvalidQueryException ex) {
        Assertions.assertEquals("PRIMARY KEY part key2 found in SET part", ex.getMessage());
    }

    // endregion

    // region 更新主键的方案为：先删除旧数据，再插入新数据

    cql = "delete from t_upsert_test2 where key1=? and key2=?";
    preparedStatement = session.prepare(cql);
    boundStatement = preparedStatement.bind(key1, key2);
    resultSet = session.execute(boundStatement);
    Assertions.assertTrue(resultSet.wasApplied());
    cql = "insert into t_upsert_test2(key1,key2,value) values(?,?,?)";
    preparedStatement = session.prepare(cql);
    boundStatement = preparedStatement.bind(1, "b", "v1");
    resultSet = session.execute(boundStatement);
    Assertions.assertTrue(resultSet.wasApplied());

    // 验证数据
    cql = "select * from t_upsert_test2";
    preparedStatement = session.prepare(cql);
    boundStatement = preparedStatement.bind();
    resultSet = session.execute(boundStatement);
    rowList = resultSet.all();
    Assertions.assertEquals(1, rowList.size());
    Assertions.assertEquals(1, rowList.get(0).getInt("key1"));
    Assertions.assertEquals("b", rowList.get(0).getString("key2"));
    Assertions.assertEquals("v1", rowList.get(0).getString("value"));

    // endregion
}
```



## 性能测试

>详细用法请参考本站 [示例](https://gitee.com/dexterleslie/demonstration/tree/main/demo-cassandra/demo-order-management-app)

提醒：除 `Cassandra` 节点外其他实例的内存和 `CPU` 都充足。三个 `Cassandra` 节点实例规格为 `2c8g`。

部署并运行服务：

```sh
 # 复制部署配置
 ansible-playbook playbook-deployer-config.yml --inventory inventory.ini
 
 # 编译并推送镜像
 ./build.sh && ./push.sh
 
 # 运行服务
 ansible-playbook playbook-service-start.yml --inventory inventory.ini
```

测试批量插入的性能：

```sh
wrk -t8 -c16 -d30s --latency --timeout 60 http://192.168.1.185/api/v1/order/
insertBatchOrderIndexListByUserId
Running 30s test @ http://192.168.1.185/api/v1/order/insertBatchOrderIndexListByUserId
  8 threads and 16 connections
  Thread Stats   Avg      Stdev     Max   +/- Stdev
    Latency   115.09ms   57.82ms 381.55ms   65.68%
    Req/Sec    18.29      8.53    60.00     78.59%
  Latency Distribution
     50%  110.97ms
     75%  151.94ms
     90%  191.97ms
     99%  268.18ms
  4209 requests in 30.04s, 1.10MB read
Requests/sec:    140.12
Transfer/sec:     37.63KB
```

销毁服务：

```sh
ansible-playbook playbook-service-destroy.yml --inventory inventory.ini
```

测试结论：在实例规格为 `2c8g` 三个 `Cassandra` 节点的集群时，`Cassandra` 三个节点的 `CPU` 使用率达到 `100%`，数据批量插入速度为 `140*256=35840` 条记录每秒。



## 持久化容器数据

>[参考文档](https://hub.docker.com/_/cassandra#where-to-store-data)

重要提示：Docker 容器中运行的应用程序可以通过多种方式存储数据。我们鼓励 Cassandra 镜像用户熟悉可用的选项，包括：

- 让 Docker 使用其内部的卷管理功能将数据库文件写入主机系统上的磁盘，从而管理数据库数据的存储。这是默认设置，操作简单，对用户来说也相当透明。缺点是，对于直接在主机系统上运行的工具和应用程序（即容器外部）来说，这些文件可能难以找到。
- 在主机系统（容器外部）创建一个数据目录，并将其挂载到容器内部可见的目录。这会将数据库文件放置在主机系统上的已知位置，方便主机系统上的工具和应用程序访问这些文件。缺点是用户需要确保该目录存在，并且主机系统上的目录权限和其他安全机制已正确设置。

Docker 文档是了解不同存储选项和变体的良好起点，并且有多个博客和论坛帖子讨论并提供这方面的建议。我们将在此简单展示上述后一种选项的基本步骤：

1. 在主机系统上合适的卷上创建数据目录，例如 /my/own/datadir。

2. 像这样启动你的 cassandra 容器：

   ```sh
   $ docker run --name some-cassandra -v /my/own/datadir:/var/lib/cassandra -d cassandra:tag
   ```

该命令的 -v /my/own/datadir:/var/lib/cassandra 部分将底层主机系统中的 /my/own/datadir 目录挂载为容器内的 /var/lib/cassandra，Cassandra 默认会将其数据文件写入其中。

