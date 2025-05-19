# Cassandra



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



## CQL

### 什么是 CQL 呢？

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



## 部署

### Docker 部署

>[官方参考文档](https://cassandra.apache.org/doc/latest/cassandra/installing/installing.html#install-with-docker)

详细用法请参考本站 [示例](https://gitee.com/dexterleslie/demonstration/tree/main/demo-cassandra)

docker-compose.yaml 配置文件如下：

```yaml
version: "3.0"

services:
  cassandra:
    image: cassandra:latest
    network_mode: host
    
```

启动 Cassandra 服务

```bash
docker compose up -d
```

进入 cqlsh

```bash
docker compose exec -it cassandra cqlsh
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

>详细用法请参考本站 [示例](https://gitee.com/dexterleslie/demonstration/tree/main/demo-cassandra/demo-client-datastax)

POM 配置：

```xml
<!-- DataStax Java Driver核心模块 -->
<dependency>
    <groupId>com.datastax.oss</groupId>
    <artifactId>java-driver-core</artifactId>
    <version>${cassandra.driver.version}</version>
</dependency>
<!-- 查询构建器模块 -->
<dependency>
    <groupId>com.datastax.oss</groupId>
    <artifactId>java-driver-query-builder</artifactId>
    <version>${cassandra.driver.version}</version>
</dependency>
<!-- 对象映射运行时依赖 -->
<dependency>
    <groupId>com.datastax.oss</groupId>
    <artifactId>java-driver-mapper-runtime</artifactId>
    <version>${cassandra.driver.version}</version>
</dependency>
```

Java 配置：

```java
@Configuration
public class ConfigCassandra {
    @Bean(destroyMethod = "close")
    public CqlSession cqlSession() {
        String host = "localhost";
        int port = 9042;
        return CqlSession.builder()
                .addContactPoint(new InetSocketAddress(host, port))
                // 指定本地数据中心名称
                .withLocalDatacenter("datacenter1")
                .withKeyspace("demo")
                .build();
    }
}
```

单条插入和批量插入：

```java
@SpringBootTest
@Slf4j
public class ApplicationTests {
    @Autowired
    CqlSession cqlSession;

    @Test
    public void test() {
        // region 测试单条插入

        String cql = "INSERT INTO t_order (id, user_id, status, pay_time, delivery_time, received_time, cancel_time, delete_status, create_time) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        PreparedStatement prepared = cqlSession.prepare(cql);
        BoundStatement bound = prepared.bind(
                BigDecimal.valueOf(1L), // id
                1001L,                  // user_id
                "Unpay",                // status
                Instant.now(),          // pay_time
                null,                   // delivery_time
                null,                   // received_time
                null,                   // cancel_time
                "Normal",               // delete_status
                Instant.now()           // create_time
        );

        ResultSet result = cqlSession.execute(bound);
        Assertions.assertTrue(result.wasApplied());

        // endregion

        // region 测试批量插入

        // 准备批量插入语句
        cql = "INSERT INTO t_order (id, user_id, status, pay_time, delivery_time, received_time, cancel_time, delete_status, create_time) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        prepared = cqlSession.prepare(cql);

        // 创建批量语句
        BatchStatement batch = BatchStatement.newInstance(BatchType.UNLOGGED);

        // 添加多个订单到批量语句
        for (int i = 0; i < 5; i++) {
            bound = prepared.bind(
                    BigDecimal.valueOf(100L + i), // id
                    1001L,                        // user_id
                    "Unpay",                      // status
                    Instant.now(),                // pay_time
                    null,                         // delivery_time
                    null,                         // received_time
                    null,                         // cancel_time
                    "Normal",                     // delete_status
                    Instant.now()                 // create_time
            );
            batch = batch.add(bound);
        }

        // 执行批量插入
        result = cqlSession.execute(batch);
        Assertions.assertTrue(result.wasApplied());

        // endregion
    }
}
```

