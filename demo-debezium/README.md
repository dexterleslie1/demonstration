# `debezium`

> `debezium` 支持三种模式：服务器、`SpringBoot` 嵌入式、`Kafka Connector` 模式。
>
> `todo`：完成服务器、`Kafka Connector` 模式 `demo`。



## 为什么消费应用程序必须期待重复事件？

>[官方 `FAQ` 参考](https://debezium.io/documentation/faq)

当所有系统正常运行，或者部分或全部系统正常关闭时，消费应用程序可以预期每个事件只会被看到一次。但是，当出现问题时，消费应用程序始终有可能至少看到一次事件。

当 Debezium 的系统崩溃时，它们并不总是能够记录其最后的位置/偏移量。当它们重新启动时，它们会从上次已知的位置开始恢复，因此消费应用程序始终可以看到每个事件，但在恢复期间可能会看到至少一些重复的消息。

此外，网络故障可能导致 Debezium 连接器无法收到写入确认，从而导致同一事件被记录一次或多次（直到收到确认为止）。



## 当受监控的数据库停止或崩溃时会发生什么？

>[官方 `FAQ` 参考](https://debezium.io/documentation/faq)

当 Debezium 监控的数据库服务器停止或崩溃时，Debezium 连接器可能会尝试重新建立通信。Debezium 会定期在 Kafka 中记录连接器的位置和偏移量，因此一旦连接器建立通信，连接器应该继续从上次记录的位置和偏移量读取数据。



## 当应用程序停止或崩溃时会发生什么？

>[官方 `FAQ` 参考](https://debezium.io/documentation/faq)

为了消费数据库的变更事件，应用程序会创建一个 Kafka 消费者，该消费者将连接到 Kafka 代理服务器，并消费与该数据库关联的主题的所有事件。消费者被配置为定期记录其在每个主题中的位置（也称为偏移量）。当应用程序正常停止并关闭消费者时，消费者将记录每个主题中最后一个事件的偏移量。当应用程序稍后重新启动时，消费者会查找这些偏移量，并开始读取每个主题中的下一个事件。因此，在正常运行情况下，应用程序只会看到每个事件一次。

如果应用程序意外崩溃，则在重新启动时，应用程序的消费者将查找每个主题最后记录的偏移量，并从每个主题的最后一个偏移量开始消费事件。在大多数情况下，应用程序会看到一些在崩溃之前（但在记录偏移量之后）看到的相同事件，然后是它尚未看到的事件。因此，应用程序至少会看到每个事件一次。应用程序可以通过更频繁地记录偏移量来减少多次看到的事件数量，尽管这样做会对客户端的性能和吞吐量产生负面影响。

请注意，可以将 Kafka 消费者配置为连接并从每个主题中的最新偏移量开始读取。这可能会导致事件丢失，但在某些情况下这是完全可以接受的。



## `offset` 是什么？

Debezium 中的 **Offset（偏移量）** 是一个核心概念，用于**精确记录连接器（Connector）已经读取和处理到源数据库变更日志（如 binlog、WAL 等）的哪个位置**。

你可以把它想象成一个**“书签”**或者**“进度指针”**：

1.  **记录读取位置：** 当 Debezium 连接到 MySQL、PostgreSQL、MongoDB 等数据源时，它会读取数据库的事务日志（变更数据捕获的基础）。Offset 就是用来记录这个连接器当前已经处理到日志的哪个具体位置了。例如：
    *   对于 MySQL，Offset 可能包含 `binlog 文件名` 和 `binlog 位置（position）`。
    *   对于 PostgreSQL，Offset 可能包含 `LSN（Log Sequence Number）`。
    *   对于 MongoDB，Offset 可能包含 `oplog 的时间戳（timestamp）` 和 `递增的序号（incrementing sequence）`。
    *   对于 SQL Server，Offset 可能包含 `LSN`。

2.  **容错与恢复：** Offset 的关键作用是提供**容错能力**。假设 Debezium 连接器所在的服务器宕机了，或者需要重启、重新部署，当它再次启动时，它可以从之前保存的 Offset 位置继续读取日志，而不会遗漏已经处理过的变更，也不会重复处理（理想情况下）。

3.  **与 Kafka 集成：** 当 Debezium 将捕获的变更事件发送到 Kafka 主题时，它通常会将这个 Offset 信息**一同写入 Kafka 的特殊主题（通常是 `<connector-name>-offsets>`）**中。Kafka 消费者（下游系统）在读取消息时，会根据 Kafka 主题中的 Offset 来决定从哪里开始消费新的事件。这确保了整个数据管道的可靠性和顺序性。

4.  **提交 Offset：** Debezium 连接器在成功处理完一批变更事件（通常是事务提交后）后，会**提交（Commit）**当前的 Offset 到 Kafka 的 offsets 主题。这个提交操作是幂等的，并且保证了“至少一次”的传递语义（在正确配置的情况下，配合消费者的幂等消费和事务，也可以实现“恰好一次”）。

**总结一下 Offset 的核心要点：**

*   **本质：** 一个结构化的数据对象，描述了连接器在源数据库变更日志中的读取位置。
*   **目的：** 记录处理进度，确保在故障恢复、重启或重新平衡时，能够从正确的位置继续处理，避免数据丢失或重复。
*   **存储：** 主要存储在 Kafka 的连接器 offsets 主题中（当使用 Kafka Connect 时），也可能存储在其他地方（如文件系统，取决于配置）。
*   **关键性：** 是 Debezium 实现可靠、精确一次（Exactly-Once）或至少一次（At-Least-Once）数据处理的基础机制之一。

简单来说，Debezium 的 Offset 就是告诉系统：“我已经处理到源数据库变更日志的这个地点了，下次请从这个地点之后开始处理新的变更。”



`Offset` 文件样例：

```
��srjava.util.HashMap���`�F
loadFactorI	thresholdxp?@
                             ur[B��T�xp7["customer-mysql-connector",{"server":"demo-debezium"}]uq~o{"transaction_id":null,"ts_sec":1754040496,"file":"my-binlog.000002","pos":940,"row":1,"server_id":1,"event":3}x%  
```



## `Schema History` 的作用

Debezium 的 **Schema History（模式历史）** 是用于**跟踪和管理源数据库表结构（Schema）变更历史**的核心机制，主要解决数据库表结构动态变化时，变更事件（Change Event）的模式兼容性与可解析性问题。它是 Debezium 实现可靠数据同步的关键组件之一，尤其在处理生产环境中频繁的表结构变更（如字段增删、类型修改等）时至关重要。


### **核心作用详解**

#### 1. **记录模式演进的全生命周期**
数据库的表结构（Schema）并非静态，可能因业务需求发生变更（例如：添加新字段、修改字段类型、删除列、调整约束等）。Schema History 会**完整记录每一次模式变更的时间点、变更内容（DDL语句）及对应的模式版本**，形成一条可追溯的模式变更时间线。  
例如：  
- 时间点 T1：表 `orders` 新增字段 `delivery_time`（类型为 `TIMESTAMP`）。  
- 时间点 T2：字段 `amount` 的类型从 `INT` 改为 `DECIMAL(10,2)`。  
- 时间点 T3：删除字段 `old_status`。  

Schema History 会记录这三个时间点对应的模式版本（如 V1 → V2 → V3），并关联每个变更事件（Change Event）生成时所使用的模式版本。


#### 2. **保证变更事件的可解析性**
Debezium 输出的变更事件（如 MySQL 的 Binlog 转换后的 JSON/Avro 事件）包含数据的**内容**和**模式信息**。当下游系统（如 Kafka 消费者、数据仓库）处理这些事件时，需要明确知道事件中每个字段的含义、类型及结构，否则会解析失败。  

Schema History 的作用是：**为每个变更事件关联其生成时的模式版本**。即使后续表结构发生变更，下游系统仍可根据事件对应的模式版本，正确解析历史事件的数据内容。  

例如：  
- 消费者在 T4 处理一个 T2 时刻生成的事件（对应模式 V2），此时即使表已演进到模式 V3（字段 `amount` 是 `DECIMAL`），消费者仍可通过 Schema History 找到模式 V2 的定义（`amount` 是 `INT`），避免解析错误。  


#### 3. **支持模式的动态适配与兼容**
在生产环境中，下游系统可能无法实时同步上游模式变更（例如：消费者服务未升级，无法处理新模式）。Schema History 允许：  
- **向后兼容**：下游系统可以按需选择使用旧版本模式解析历史事件，避免因模式突变导致的解析失败。  
- **向前兼容**：当消费者升级后，可以切换到最新模式解析新事件，同时仍能处理旧事件（通过历史模式）。  


#### 4. **故障恢复与一致性保障**
当 Debezium 连接器重启或发生故障转移时，Schema History 能帮助快速恢复状态：  
- 连接器重启后，可通过 Schema History 确认当前最新的模式版本，确保后续变更事件使用正确的模式序列化/反序列化。  
- 结合 Offset（偏移量），可准确定位到故障前最后处理的事件及其对应的模式版本，避免因模式不一致导致的数据错位或丢失。  


#### 5. **与外部系统的模式协同**
Debezium 通常与模式注册中心（如 Confluent Schema Registry）集成，将模式信息存储在注册中心中。Schema History 在此场景下的作用是：  
- 记录每个事件对应的模式 ID 或版本号，确保消费者可以从注册中心拉取正确的模式进行解析。  
- 当模式发生变更时（如通过 DDL 触发 Avro 模式的演化），Schema History 会同步更新模式版本，并关联到后续的变更事件，保证注册中心中模式的版本与事件一一对应。  


### **Schema History 的存储与实现**
在不同集成场景下，Schema History 的存储方式有所不同：  
- **Kafka Connect 集成**：默认存储在 Kafka 的内部主题 `__debezium_schema_history` 中（名称格式为 `<connector-name>-schema-history`），主题的分区数和副本数可通过配置调整。该主题存储的内容包括模式版本 ID、时间戳、关联的变更事件偏移量（Offset）、DDL 语句等元数据。  
- **独立模式存储**：对于非 Kafka 场景（如直接输出到文件或消息队列），Schema History 可能存储在本地文件系统或外部数据库中，具体取决于连接器配置。  


### **总结**
Schema History 是 Debezium 支持**动态模式变更**的核心机制，通过完整记录表结构的演进历史，确保变更事件在不同时间点的可解析性、下游系统的兼容性，以及连接器故障恢复时的一致性。它是 Debezium 实现“可靠数据同步”的关键基石之一，尤其在处理生产环境中频繁的表结构变更时不可或缺。



### `Schema History` 文件样例

```json
{"source":{"server":"demo-debezium"},"position":{"ts_sec":1754040493,"file":"my-binlog.000002","pos":342,"snapshot":true},"ts_ms":1754040494326,"databaseName":"","ddl":"SET character_set_server=utf8mb4, collation_server=utf8mb4_general_ci","tableChanges":[]}
{"source":{"server":"demo-debezium"},"position":{"ts_sec":1754040494,"file":"my-binlog.000002","pos":342,"snapshot":true},"ts_ms":1754040494340,"databaseName":"demo","ddl":"DROP TABLE IF EXISTS `demo`.`t_user`","tableChanges":[]}
{"source":{"server":"demo-debezium"},"position":{"ts_sec":1754040494,"file":"my-binlog.000002","pos":342,"snapshot":true},"ts_ms":1754040494345,"databaseName":"demo","ddl":"DROP DATABASE IF EXISTS `demo`","tableChanges":[]}
{"source":{"server":"demo-debezium"},"position":{"ts_sec":1754040494,"file":"my-binlog.000002","pos":342,"snapshot":true},"ts_ms":1754040494350,"databaseName":"demo","ddl":"CREATE DATABASE `demo` CHARSET utf8mb4 COLLATE utf8mb4_unicode_ci","tableChanges":[]}
{"source":{"server":"demo-debezium"},"position":{"ts_sec":1754040494,"file":"my-binlog.000002","pos":342,"snapshot":true},"ts_ms":1754040494351,"databaseName":"demo","ddl":"USE `demo`","tableChanges":[]}
{"source":{"server":"demo-debezium"},"position":{"ts_sec":1754040494,"file":"my-binlog.000002","pos":342,"snapshot":true},"ts_ms":1754040494395,"databaseName":"demo","ddl":"CREATE TABLE `t_user` (\n  `id` bigint(20) NOT NULL AUTO_INCREMENT,\n  `username` varchar(64) NOT NULL COMMENT '名称',\n  `password` varchar(64) NOT NULL COMMENT '密码',\n  `createTime` datetime NOT NULL COMMENT '创建时间',\n  PRIMARY KEY (`id`)\n) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci","tableChanges":[{"type":"CREATE","id":"\"demo\".\"t_user\"","table":{"defaultCharsetName":"utf8mb4","primaryKeyColumnNames":["id"],"columns":[{"name":"id","jdbcType":-5,"typeName":"BIGINT","typeExpression":"BIGINT","charsetName":null,"length":20,"position":1,"optional":false,"autoIncremented":true,"generated":true,"comment":null,"hasDefaultValue":false,"enumValues":[]},{"name":"username","jdbcType":12,"typeName":"VARCHAR","typeExpression":"VARCHAR","charsetName":"utf8mb4","length":64,"position":2,"optional":false,"autoIncremented":false,"generated":false,"comment":null,"hasDefaultValue":false,"enumValues":[]},{"name":"password","jdbcType":12,"typeName":"VARCHAR","typeExpression":"VARCHAR","charsetName":"utf8mb4","length":64,"position":3,"optional":false,"autoIncremented":false,"generated":false,"comment":null,"hasDefaultValue":false,"enumValues":[]},{"name":"createTime","jdbcType":93,"typeName":"DATETIME","typeExpression":"DATETIME","charsetName":null,"position":4,"optional":false,"autoIncremented":false,"generated":false,"comment":null,"hasDefaultValue":false,"enumValues":[]}],"attributes":[]},"comment":null}]}

```



## 有哪些部署模式呢？

Debezium 是一款用于捕获数据库变更数据（Change Data Capture, CDC）的工具，其部署模式主要围绕**如何集成到应用或数据管道**展开。根据使用场景和架构需求，Debezium 主要有以下三种典型部署模式：


### **一、嵌入式模式（Embedded Mode）**
**定义**：Debezium 引擎直接嵌入到用户的应用程序进程中运行，作为应用的一部分，与应用共享生命周期。  
**核心特点**：  
- **紧耦合**：Debezium 引擎与应用代码强关联，需在应用中显式初始化、启动和关闭引擎。  
- **自定义处理**：支持直接在应用中编写事件监听逻辑（如将变更数据写入缓存、调用下游服务）。  
- **轻量级**：无需额外部署独立服务，适合小规模或需要高度定制化的场景。  


#### **适用场景**  
- 应用需要**实时消费自身数据库的变更**（如微服务间同步数据）。  
- 需要将变更数据**直接集成到应用业务逻辑**（如触发缓存更新、发送通知）。  


#### **配置与示例**  
1. **添加依赖**（以 Maven 为例）：  
   ```xml
   <dependency>
       <groupId>io.debezium</groupId>
       <artifactId>debezium-api</artifactId>
       <version>2.4.0.Final</version>
   </dependency>
   <dependency>
       <groupId>io.debezium</groupId>
       <artifactId>debezium-embedded</artifactId>
       <version>2.4.0.Final</version>
   </dependency>
   <dependency>
       <groupId>io.debezium</groupId>
       <artifactId>debezium-connector-mysql</artifactId>
       <version>2.4.0.Final</version>
   </dependency>
   ```

2. **应用代码初始化**：  
   ```java
   import io.debezium.engine.ChangeEvent;
   import io.debezium.engine.DebeziumEngine;
   import io.debezium.engine.format.Json;
   
   public class EmbeddedDebeziumExample {
       public static void main(String[] args) throws Exception {
           // 配置 Debezium 引擎
           DebeziumEngine<ChangeEvent<String, String>> engine = DebeziumEngine.create(Json.class)
               .using("mysql", "localhost:3306", "my_db", "user", "password")
               .notifying(record -> {
                   // 处理变更事件（如打印或写入缓存）
                   System.out.println("Received change: " + record);
               })
               .build();
   
           // 启动引擎
           engine.start();
   
           // 阻塞主线程（实际应用中需优雅关闭）
           Thread.sleep(Long.MAX_VALUE);
       }
   }
   ```


### **二、Kafka Connect 模式（Kafka Connect Mode）**
**定义**：Debezium 作为 **Kafka Connect 源连接器（Source Connector）** 运行，利用 Kafka Connect 框架管理任务分发、容错、偏移量存储等。变更数据会被写入 Kafka 主题，供下游系统消费。  
**核心特点**：  
- **分布式**：支持多 Worker 节点并行处理，水平扩展能力强。  
- **容错性**：Kafka Connect 自动管理任务重试、故障转移（如节点宕机后重新分配任务）。  
- **解耦**：Debezium 仅负责捕获变更并写入 Kafka，下游系统通过消费 Kafka 主题获取数据，架构更清晰。  


#### **适用场景**  
- 构建**实时数据管道**（如将 MySQL 变更同步到 Elasticsearch、数据仓库）。  
- 多系统间**事件驱动集成**（如订单变更触发库存扣减、物流派单）。  
- 需要利用 Kafka 的**高吞吐、持久化、多消费者**特性。  


#### **配置与示例**  
1. **部署 Kafka Connect Worker**：  
   需先部署 Kafka 集群，并启动 Kafka Connect Worker（独立进程或容器）。Worker 负责管理 Debezium 连接器任务。

2. **配置 Debezium 连接器**（以 MySQL 为例）：  
   在 Kafka Connect 的 `connect-config` 目录下创建 `mysql-source.properties`：  
   ```properties
   name=mysql-source-connector  # 连接器名称
   connector.class=io.debezium.connector.mysql.MySqlConnector  # Debezium MySQL 连接器类
   tasks.max=1  # 任务数（根据并行度调整）
   database.hostname=mysql-host  # 数据库地址
   database.port=3306
   database.user=user
   database.password=password
   database.server.id=184054  # 唯一 ID（需与其他 Debezium 实例不同）
   database.server.name=mysql-server  # Kafka 主题前缀（生成的 topic 为 mysql-server.*）
   database.include.list=my_db  # 需要捕获的数据库
   table.include.list=my_db.orders  # 需要捕获的表
   ```

3. **启动连接器**：  
   通过 Kafka Connect REST API 启动连接器：  
   ```bash
   curl -X POST -H "Content-Type: application/json" http://kafka-connect-host:8083/connectors \
        -d '{"name": "mysql-source-connector", "config": { ... }}'  # 替换为上述配置
   ```


### **三、Debezium Server 模式（独立模式，已逐渐被替代）**
**定义**：Debezium 以独立进程（Standalone Server）运行，不嵌入应用也不依赖 Kafka Connect，通过配置文件和 REST API 管理捕获任务。  
**核心特点**：  
- **独立部署**：作为单独服务运行，与应用、Kafka 解耦。  
- **简单易用**：通过配置文件定义数据源和输出（如写入 Kafka、Elasticsearch 等）。  
- **维护成本高**：需手动管理进程生命周期、日志、监控，社区已逐渐推荐使用 Kafka Connect 模式替代。  


#### **适用场景**  
- 轻量级 CDC 需求（如测试环境、小规模数据同步）。  
- 不依赖 Kafka 的场景（需将变更数据写入其他系统，如文件、HTTP 接口）。  


#### **配置与示例**  
1. **下载 Debezium Server**：  
   从 https://debezium.io/downloads/ 下载独立二进制包（如 `debezium-server-2.4.0.Final.tar.gz`）。

2. **配置 `debezium.properties`**：  
   编辑 `config/debezium.properties`，指定数据源和输出目标（以 MySQL 到 Kafka 为例）：  
   ```properties
   debezium.source.connector.class=io.debezium.connector.mysql.MySqlConnector
   debezium.source.database.hostname=mysql-host
   debezium.source.database.port=3306
   debezium.source.database.user=user
   debezium.source.database.password=password
   debezium.source.database.server.id=184054
   debezium.source.database.server.name=mysql-server
   debezium.source.database.include.list=my_db
   debezium.source.table.include.list=my_db.orders
   
   # 输出到 Kafka
   debezium.sink.kafka.broker.list=kafka-host:9092
   debezium.sink.kafka.topic.prefix=mysql-server
   ```

3. **启动 Debezium Server**：  
   ```bash
   bin/debezium run  # 启动服务
   ```


### **模式对比与选择建议**
| 模式                   | 核心优势                        | 适用场景                         | 维护复杂度            |
| ---------------------- | ------------------------------- | -------------------------------- | --------------------- |
| **嵌入式模式**         | 灵活定制、轻量级、与应用强耦合  | 小规模应用、需自定义事件处理逻辑 | 中（需管理引擎）      |
| **Kafka Connect 模式** | 分布式、容错、与 Kafka 深度集成 | 大规模数据管道、多系统事件驱动   | 低（依赖 Kafka 框架） |
| **Debezium Server**    | 独立部署、简单易用              | 轻量级需求、测试/临时场景        | 高（需手动管理进程）  |


### **总结**
Debezium 的部署模式选择需结合具体场景：  
- 若需**高度定制化事件处理**且应用规模较小，选**嵌入式模式**。  
- 若需构建**大规模实时数据管道**并与 Kafka 集成，选**Kafka Connect 模式**（推荐）。  
- 若仅需**轻量级 CDC 功能**且不依赖 Kafka，可考虑**Debezium Server**（但社区已逐步淡化此模式）。



## 配置项



### `offset.storage.file.filename`

在 Debezium 中，`offset.storage.file.filename` 是用于**自定义偏移量存储文件路径和名称**的配置参数，主要在通过文件系统（而非 Kafka 主题）存储连接器偏移量时使用。以下是关于该配置的详细说明：

#### **1. 核心作用**

Debezium 通过记录数据库变更的**偏移量（Offset）**来跟踪已处理的数据位置，确保连接器重启后能从断点继续消费，避免数据重复或丢失。`offset.storage.file.filename` 用于指定**存储这些偏移量的文件路径和名称**，仅在偏移量存储方式为文件系统时生效（对应 `offset.storage=filesystem`）。


#### **2. 使用前提**
要使用 `offset.storage.file.filename`，需先配置偏移量存储方式为文件系统：
```properties
offset.storage=filesystem  # 必须设置为文件系统存储
offset.storage.file.filename=/path/to/your/offset-file.offsets  # 自定义文件路径
```


#### **3. 配置细节**
##### （1）路径类型
- **绝对路径**：推荐使用绝对路径（如 `/var/lib/debezium/offsets/mysql-connector.offsets`），避免因工作目录变化导致文件找不到。
- **相对路径**：若使用相对路径（如 `./offsets/connector.offsets`），则相对于 Debezium 进程的工作目录（通常是启动命令的执行目录）。

##### （2）多实例隔离
若同一台机器运行多个 Debezium 连接器实例（如不同数据库或不同任务的连接器），需为每个实例设置**唯一的文件名**，避免偏移量文件被覆盖。例如：
```properties
# 实例 1（MySQL）
offset.storage.file.filename=/data/debezium/mysql-connector.offsets

# 实例 2（PostgreSQL）
offset.storage.file.filename=/data/debezium/postgres-connector.offsets
```

##### （3）文件权限
确保运行 Debezium 的用户对目标路径有**读写权限**。否则，启动时会抛出 `Permission denied` 错误。例如：
```bash
# 若 Debezium 以用户 "debezium" 运行，需授权：
chown debezium:debezium /data/debezium/
chmod 755 /data/debezium/
```


#### **4. 偏移量文件内容**
偏移量文件是 Debezium 内部维护的二进制或结构化文件（具体格式由 Debezium 版本决定），**无需手动编辑**。其内容包含：
- 各分区的最后提交偏移量（如数据库日志的 LSN、事务 ID 等）。
- 连接器的状态信息（如检查点、错误恢复标记）。


#### **5. 注意事项**
##### （1）与 Kafka 主题存储的对比
- **默认方式（Kafka 主题）**：更推荐使用默认的 `offset.storage=kafka`，偏移量存储在 Kafka 的内部主题（如 `debezium-offset-storage`），支持分布式、高可用，适合生产环境。
- **文件系统存储**：仅适用于单实例、测试或轻量级场景，无法支持多实例共享偏移量，且文件丢失会导致偏移量重置（可能重复消费数据）。

##### （2）故障恢复风险
若偏移量文件损坏或丢失，Debezium 会**从初始位置重新消费数据**（具体行为取决于连接器的 `snapshot.mode` 配置）。因此：
- 建议将偏移量文件存储在可靠的存储介质（如 RAID、云存储）。
- 定期备份偏移量文件（尤其是关键业务场景）。

##### （3）版本兼容性
不同 Debezium 版本的偏移量文件格式可能不兼容。升级 Debezium 时，需参考官方文档确认是否需要迁移或保留旧偏移量文件。


#### **6. 配置示例**
以下是一个完整的 MySQL 连接器配置示例，使用文件系统存储偏移量：
```properties
name=mysql-connector  # 连接器名称（用于生成文件名）
connector.class=io.debezium.connector.mysql.MySqlConnector
database.hostname=localhost
database.port=3306
database.user=debezium
database.password=dbz
database.server.id=184054
database.server.name=mysql-server
database.include.list=inventory

# 偏移量存储配置
offset.storage=filesystem
offset.storage.file.filename=/var/lib/debezium/offsets/mysql-connector.offsets

# 其他可选配置
snapshot.mode=when_needed
table.include.list=inventory.orders,inventory.products
```


#### **总结**
`offset.storage.file.filename` 是 Debezium 文件系统偏移量存储的核心配置，用于指定偏移量文件的路径。它适用于单实例、测试场景，但生产环境更推荐使用 Kafka 主题存储以保证高可用和可靠性。使用时需注意路径权限、多实例隔离及故障恢复风险。



### `schema.history.internal.file.filename`

在 Debezium 中，`schema.history.internal.file.filename` 是用于**自定义模式历史存储文件路径和名称**的配置参数，主要用于控制 Debezium 记录数据库表结构变更历史的存储位置。以下是关于该配置的详细说明：


#### **1. 核心作用**
Debezium 在捕获数据库变更（如 DDL 操作修改表结构）时，需要记录**模式历史（Schema History）**，以便下游消费者（如 Kafka Connect 的 Sink 连接器）能够根据历史模式正确解析变更数据（如 Avro、JSON 格式的消息）。  
`schema.history.internal.file.filename` 用于指定当模式历史存储方式为文件系统时，存储模式的文件的**路径和名称**（仅在 `schema.history.internal=filesystem` 时生效）。


#### **2. 使用前提**
要使用 `schema.history.internal.file.filename`，需先配置模式历史的存储方式为文件系统：  
```properties
schema.history.internal=filesystem  # 必须设置为文件系统存储
schema.history.internal.file.filename=/path/to/schema-history.file  # 自定义文件路径
```


#### **3. 配置细节**
##### （1）路径类型
- **绝对路径**：推荐使用绝对路径（如 `/var/lib/debezium/schema-history/mysql-connector.schema`），避免因 Debezium 进程工作目录变化导致文件找不到。  
- **相对路径**：若使用相对路径（如 `./schema-history/connector.schema`），则相对于 Debezium 进程的工作目录（通常是启动命令的执行目录）。

##### （2）多实例隔离
若同一台机器运行多个 Debezium 连接器实例（如不同数据库或不同任务的连接器），需为每个实例设置**唯一的文件名**，避免模式历史文件被覆盖。例如：  
```properties
# 实例 1（MySQL 连接器）
schema.history.internal=filesystem
schema.history.internal.file.filename=/data/debezium/mysql-schema-history.schema

# 实例 2（PostgreSQL 连接器）
schema.history.internal=filesystem
schema.history.internal.file.filename=/data/debezium/postgres-schema-history.schema
```

##### （3）文件权限
确保运行 Debezium 的用户对目标路径有**读写权限**。否则，启动时会抛出 `Permission denied` 错误。例如：  
```bash
# 若 Debezium 以用户 "debezium" 运行，需授权：
chown debezium:debezium /data/debezium/
chmod 755 /data/debezium/
```


#### **4. 模式历史文件内容**
模式历史文件是 Debezium 内部维护的结构化文件（通常为二进制或 JSON 格式，具体取决于版本），**无需手动编辑**。其内容包含：  
- 表的唯一标识（如 `server_name.database_name.table_name`）。  
- 模式的版本号（随每次 DDL 变更递增）。  
- 每个版本对应的完整模式信息（如列名、类型、约束等）。  
- 时间戳（记录模式变更的时间）。  


#### **5. 注意事项**
##### （1）与默认存储（Kafka 主题）的对比
- **默认方式（Kafka 主题）**：更推荐使用默认的 `schema.history.internal=kafka`，模式历史存储在 Kafka 的内部主题（如 `debezium-schema-history`），支持分布式、高可用，适合生产环境。  
- **文件系统存储**：仅适用于单实例、测试或轻量级场景，无法支持多实例共享模式历史，且文件丢失会导致模式历史重置（可能引发下游解析错误）。

##### （2）故障恢复风险
若模式历史文件损坏或丢失，Debezium 会**从初始状态重新记录模式变更**（即仅保留当前最新的模式），可能导致下游消费者因缺少历史模式而无法正确解析早期变更数据。因此：  
- 建议将模式历史文件存储在可靠的存储介质（如 RAID、云存储）。  
- 定期备份模式历史文件（尤其是关键业务场景）。

##### （3）版本兼容性
不同 Debezium 版本的**模式历史文件格式可能不兼容**。升级 Debezium 时，需参考官方文档确认是否需要迁移或保留旧模式历史文件（部分版本可能要求清空旧文件以避免解析错误）。

##### （4）与偏移量存储的区别
`schema.history.internal.file.filename` 控制的是**模式历史**的存储路径，而 `offset.storage.file.filename` 控制的是**偏移量**（已处理数据的进度）的存储路径，两者用途完全不同，需分别配置。


#### **6. 配置示例**
以下是一个完整的 MySQL 连接器配置示例，使用文件系统存储模式历史：  
```properties
name=mysql-orders-connector  # 连接器名称（用于生成唯一文件名）
connector.class=io.debezium.connector.mysql.MySqlConnector
database.hostname=localhost
database.port=3306
database.user=debezium
database.password=dbz
database.server.id=184054
database.server.name=mysql-orders
database.include.list=orders_db

# 模式历史存储配置（文件系统）
schema.history.internal=filesystem
schema.history.internal.file.filename=/var/lib/debezium/schema-history/mysql-orders-connector.schema

# 偏移量存储配置（可选，此处示例仍用默认 Kafka 存储）
offset.storage=kafka
offset.storage.topic=debezium-offsets
offset.storage.partitions=24

# 其他配置
snapshot.mode=when_needed
table.include.list=orders_db.orders,orders_db.customers
```


#### **总结**
`schema.history.internal.file.filename` 是 Debezium 文件系统模式历史存储的核心配置，用于指定模式历史文件的路径。它适用于单实例、测试场景，但生产环境强烈建议使用默认的 Kafka 主题存储，以保证模式历史的高可用性和可靠性。使用时需注意路径权限、多实例隔离及故障恢复风险。



## 配置项 - `max.batch.size`

>提示：
>
>- 控制连接器每次从 `MySQL Binlog` 中读取并处理的事件数（记录数）。`2048`（`Debezium` 官方默认）。
>- 借助本站 [示例](https://gitee.com/dexterleslie/demonstration/tree/main/demo-debezium/demo-debezium-embedded) 可以测试此参数的效果。

在 Debezium MySQL 连接器中，**批量获取的最大记录数**可以通过配置参数 `max.batch.size` 控制。该参数用于指定连接器从 MySQL 二进制日志（Binlog）中单次读取的最大事件数（即记录数）。以下是具体的配置方法和说明：


### **1. 核心参数：`max.batch.size`**
- **作用**：控制连接器每次从 MySQL Binlog 中读取并处理的事件数（记录数）。  
- **默认值**：`2048`（Debezium 官方默认）。  
- **取值范围**：建议根据业务需求调整，通常范围为 `100~8192`（需结合内存和吞吐量优化）。  


### **2. 配置到你的连接器中**
在你的 `debezium-connect-init` 服务的 `curl` 命令中，需要将 `max.batch.size` 添加到连接器的 `config` 配置对象中。修改后的 JSON 如下：

```json
{
  "name": "demo-connector",
  "config": {
    "connector.class": "io.debezium.connector.mysql.MySqlConnector",
    "tasks.max": "1",
    "database.hostname": "db",
    "database.port": "3306",
    "database.user": "root",
    "database.password": "123456",
    "database.server.id": "184054",
    "topic.prefix": "demo-debezium",  // 注意修正原拼写错误（原 "demo-debezum"）
    "database.include.list": "demo",
    "schema.history.internal.kafka.bootstrap.servers": "kafka:9092",
    "schema.history.internal.kafka.topic": "schemahistory.demo",
    "max.batch.size": 1000  // 新增：设置批量最大记录数为 1000
  }
}
```


### **3. 其他相关参数（可选优化）**
除了 `max.batch.size`，以下几个参数也与批量处理性能相关，可根据实际场景调整：

| 参数名                | 作用                                                         | 默认值  | 建议场景                                                     |
| --------------------- | ------------------------------------------------------------ | ------- | ------------------------------------------------------------ |
| `max.queue.size`      | 存储待发送到 Kafka 的事件队列大小（超过此大小会阻塞读取 Binlog） | `8192`  | 若 `max.batch.size` 较小但吞吐量高，可增大此值避免阻塞。     |
| `poll.interval.ms`    | 轮询 Binlog 的间隔时间（若当前批次未填满 `max.batch.size`，等待此时间后继续） | `500ms` | 若希望降低轮询频率（减少 CPU 消耗），可增大此值（如 `1000ms`）。 |
| `snapshot.fetch.size` | 全量快照时单次读取的记录数（仅在 `snapshot.mode=initial` 或 `schema_only_recovery` 时生效） | `10000` | 初始全量同步大表时，可减小此值降低内存压力（如 `1000`）。    |


### **4. 完整配置示例（含批量参数）**
如果你需要同时优化批量处理和全量快照，配置可能如下：

```json
{
  "name": "demo-connector",
  "config": {
    "connector.class": "io.debezium.connector.mysql.MySqlConnector",
    "tasks.max": "1",
    "database.hostname": "db",
    "database.port": "3306",
    "database.user": "root",
    "database.password": "123456",
    "database.server.id": "184054",
    "topic.prefix": "demo-debezium",
    "database.include.list": "demo",
    "schema.history.internal.kafka.bootstrap.servers": "kafka:9092",
    "schema.history.internal.kafka.topic": "schemahistory.demo",
    "max.batch.size": 1000,         // 单次读取最大记录数
    "max.queue.size": 20000,        // 队列容量（根据内存调整）
    "poll.interval.ms": 500,        // 轮询间隔（保持默认即可）
    "snapshot.mode": "initial",     // 初始全量快照（默认）
    "snapshot.fetch.size": 1000     // 全量快照单次读取记录数
  }
}
```


### **5. 验证配置是否生效**
部署后，可通过以下方式验证配置是否生效：
1. 查看 Debezium 连接器状态：  
   ```bash
   curl http://debezium-connect:8083/connectors/demo-connector/status
   ```
   输出中的 `config` 字段应包含 `max.batch.size`。

2. 观察 Kafka 消息批量大小：  
   通过 Kafka 消费者工具（如 `kafkacat`）订阅连接器输出的 Topic（如 `demo-debezium.demo`），统计每批消息的记录数是否符合预期。


### **总结**
通过在连接器配置中添加 `max.batch.size` 参数，即可控制 Debezium 批量获取的最大记录数。根据业务需求（如吞吐量、内存限制）调整该值，同时可结合 `max.queue.size` 等参数优化整体性能。



## `SpringBoot` 嵌入式模式

>提示：
>
>- 第一次启动 `SpringBoot` 应用没有 `offset` 信息时会全量读取一次数据库并回调 `SpringBoot CDC` 接口。官方这种模式已经废弃，但是 `future` 项目还是暂时采用这种模式。
>- `Debezium Engine` 回调接口抛出异常后不会再重试消息，导致消息丢失，所以生产系统中不采用此模式。



### 集成配置

>  `https://www.baeldung.com/debezium-intro`
>
>  `https://debezium.io/documentation/reference/stable/development/engine.html`
>
>  详细用法请参考本站 [示例](https://gitee.com/dexterleslie/demonstration/tree/main/demo-debezium/demo-debezium-embedded)

`POM` 配置：

```xml
<debezium.version>2.0.1.Final</debezium.version>

<dependency>
    <groupId>io.debezium</groupId>
    <artifactId>debezium-embedded</artifactId>
    <version>${debezium.version}</version>
</dependency>
<dependency>
    <groupId>io.debezium</groupId>
    <artifactId>debezium-connector-mysql</artifactId>
    <version>${debezium.version}</version>
</dependency>
<dependency>
    <groupId>mysql</groupId>
    <artifactId>mysql-connector-java</artifactId>
    <version>8.0.27</version>
</dependency>
```

启动相关容器

```bash
docker compose up -d
```

启动 `SpringBoot` 服务

在数据库中执行以下 `SQL` 后，观察 `Debezium` 在 `SpringBoot` 应用输出的日志

```sql
insert into t_user(username,`password`,createTime) values('user2','123456',now());

update t_user set username='userx' where username='user2';

delete from t_user where username='userx';
```

应用日志输出如下：

```
table=t_user,payload={password=123456, createTime=1752076266000, id=3, username=user2},operation=CREATE
table=t_user,payload={password=123456, createTime=1752076266000, id=3, username=userx},operation=UPDATE
table=t_user,payload={password=123456, createTime=1752076266000, id=3, username=userx},operation=DELETE
```



## `Connector` 模式



### 部署

>[参考官方文档](https://debezium.io/documentation//reference/stable/tutorial.html)
>
>详细用法请参考本站 [示例](https://gitee.com/dexterleslie/demonstration/tree/main/demo-debezium/demo-debezium-kafka)

总体流程：

1. 启动 `Zookeeper` 服务
2. 启动 `Kafka` 服务
3. 启动 `MySQL` 或者 `MariaDB` 服务
4. 启动 `Connector` 服务
5. 测试增、删、改触发的 `CDC` 事件。



启动 `Zookeeper` 服务：

>提示：
>
>- 如果使用 `Debezium` 镜像 `quay.io/debezium/kafka:3.2` 运行 `Kafka` 服务，则不需要启动 `Zookeeper` 服务。`Debezium` 镜像用法参考 `https://hub.docker.com/r/debezium/zookeeper`。
>- 如果使用 `Confluent` 镜像运行 `Kafka` 服务，则需要启动 `Zookeeper` 服务。

```yaml
version: '3.0'

services:
  # 使用 confluent kafka 的配置
  zookeeper:
    image: confluentinc/cp-zookeeper:7.3.0
    environment:
      TZ: Asia/Shanghai
      ZOOKEEPER_CLIENT_PORT: 2181
      ZOOKEEPER_TICK_TIME: 2000
    ports:
      - "2181:2181"
```



启动 `Kafka` 服务：

>提示：`Debezium` 镜像用法参考 `https://hub.docker.com/r/debezium/kafka`

- 可以通过 `Debezium` 或者 `Confluent` 镜像运行 `Kafka` 服务。

- 使用 `Debezium` 镜像

  ```yaml
  version: '3.0'
  
  services:
    # 使用 debezium kafka 的配置
    kafka:
      image: quay.io/debezium/kafka:3.2
      environment:
        TZ: Asia/Shanghai
      ports:
        - "9092:9092"
  ```

- 使用 `Confluent` 镜像

  `.env`：

  ```ini
  # 设置为 broker 所在实例的 ip 地址
  kafka_advertised_listeners=192.168.1.181
  
  ```

  `docker-compose.yaml`：

  ```yaml
  version: '3.0'
  
  services:
    kafka:
      image: confluentinc/cp-kafka:7.3.0
      depends_on:
        - zookeeper
      ports:
        - "9092:9092"
        # 映射 JMX 端口到主机
        # - "9997:9997"
      environment:
        TZ: Asia/Shanghai
        KAFKA_BROKER_ID: 1
        KAFKA_ZOOKEEPER_CONNECT: zookeeper:2181
        KAFKA_ADVERTISED_LISTENERS: PLAINTEXT://${kafka_advertised_listeners}:9092
        KAFKA_OFFSETS_TOPIC_REPLICATION_FACTOR: 1
        # 设置 Kafka 的 JVM 堆内存
        KAFKA_HEAP_OPTS: "-Xms1g -Xmx1g"
        # 配置 JMX 端口和认证，配置了 jmx 端口才能够被外部工具监控
        # KAFKA_JMX_OPTS: "-Dcom.sun.management.jmxremote -Dcom.sun.management.jmxremote.authenticate=false -Dcom.sun.management.jmxremote.ssl=false -Dcom.sun.management.jmxremote.local.only=false -Dcom.sun.management.jmxremote.port=9997 -Dcom.sun.management.jmxremote.rmi.port=9997"
        # 禁用自动创建 Topic，否则 Spring Boot 会自动创建 partitions=0 的 topic
        # KAFKA_AUTO_CREATE_TOPICS_ENABLE: "false"
  ```



启动 `MySQL` 或者 `MariaDB` 服务：

`db.sql`：

```sql
CREATE DATABASE IF NOT EXISTS demo CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE demo;

CREATE TABLE IF NOT EXISTS t_user(
    id                 BIGINT NOT NULL PRIMARY KEY AUTO_INCREMENT,
    username           VARCHAR(64) NOT NULL COMMENT '名称',
    `password`         VARCHAR(64) NOT NULL COMMENT '密码',
    createTime         DATETIME NOT NULL COMMENT '创建时间'
) ENGINE=INNODB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

insert into t_user(username,`password`,createTime) values('user1','123456',now());
update t_user set username='userx' where username='user1';



```

`my-customize.cnf`：

```ini
[mysqld]
max_connections=250
slow_query_log=1
long_query_time=1
slow_query_log_file=slow-query.log
innodb_flush_log_at_trx_commit=0
max_allowed_packet=10m
key_buffer_size=512m
innodb_log_file_size=512m
innodb_log_buffer_size=256m
innodb_file_per_table=1

log_bin=my-binlog
expire_logs_days=10
binlog_format=row
max_binlog_size=512m
```

`docker-compose.yaml`：

```yaml
version: '3.0'

services:
  db:
    image: mariadb:11.4
    environment:
      - MYSQL_ROOT_PASSWORD=123456
      - TZ=Asia/Shanghai
    volumes:
      - ./db.sql:/docker-entrypoint-initdb.d/db.sql:ro
      - ./my-customize.cnf:/etc/mysql/conf.d/my-customize.cnf:ro
    command:
      - --character-set-server=utf8mb4
      - --collation-server=utf8mb4_general_ci
      - --skip-character-set-client-handshake
      - --innodb-buffer-pool-size=256m
    ports:
      - "3306:3306"
```



启动 `Kafka Connector` 服务：

>提示：`Debezium` 镜像用法参考 `https://hub.docker.com/r/debezium/connect`。

- 启动 `Kafka Connector`

  ```yaml
  version: '3.0'
  
  services:
    debezium-connect:
      image: quay.io/debezium/connect:3.2
      environment:
        TZ: Asia/Shanghai
        GROUP_ID: 1
        CONFIG_STORAGE_TOPIC: my_connect_configs
        OFFSET_STORAGE_TOPIC: my_connect_offsets
        STATUS_STORAGE_TOPIC: my_connect_statuses
        # kafka 集群
        BOOTSTRAP_SERVERS: "kafka:9092"
      ports:
        - "8083:8083"
  ```

- 检查 `Connector` 状态

  ```sh
  $ curl -H "Accept:application/json" localhost:8083/
  {"version":"4.0.0","commit":"985bc99521dd22bb","kafka_cluster_id":"FURdxQsVSYmS2rqEds1uFA"}
  ```

- 检查已注册的 `connector` 列表

  ```sh
  $ curl -H "Accept:application/json" localhost:8083/connectors/
  []
  ```



部署 `MySQL Connector`：

- 调用 `Connector` 的接口配置 `MySQL Connector`

  ```sh
  curl -i -X POST -H "Accept:application/json" -H "Content-Type:application/json" localhost:8083/connectors/ -d '{ "name": "demo-connector", "config": { "connector.class": "io.debezium.connector.mysql.MySqlConnector", "tasks.max": "1", "database.hostname": "db", "database.port": "3306", "database.user": "root", "database.password": "123456", "database.server.id": "184054", "topic.prefix": "demo-debezium", "database.include.list": "demo", "schema.history.internal.kafka.bootstrap.servers": "kafka:9092", "schema.history.internal.kafka.topic": "schemahistory.demo" } }'
  ```

- 再次检查已注册的 `connector` 列表，此时 `demo-connector` 已注册

  ```sh
  $ curl -H "Accept:application/json" localhost:8083/connectors/
  ["demo-connector"]
  ```

- 审查 `demo-connector` 的配置

  ```sh
  $ curl -i -X GET -H "Accept:application/json" localhost:8083/connectors/demo-connector
  HTTP/1.1 200 OK
  Server: Jetty(12.0.15)
  Date: Sun, 03 Aug 2025 04:42:10 GMT
  Content-Type: application/json
  Content-Length: 519
  
  {"name":"demo-connector","config":{"connector.class":"io.debezium.connector.mysql.MySqlConnector","database.user":"root","topic.prefix":"demo-debezium","schema.history.internal.kafka.topic":"schemahistory.demo","database.server.id":"184054","tasks.max":"1","database.hostname":"db","database.password":"123456","name":"demo-connector","schema.history.internal.kafka.bootstrap.servers":"kafka:9092","database.port":"3306","database.include.list":"demo"},"tasks":[{"connector":"demo-connector","task":0}],"type":"source"}
  ```



查看 `Kafka` 主题和 `CDC` 事件：

- 列出所有 `Kafka` 主题

  ```sh
  $ KAFKA_JMX_OPTS="" /usr/bin/kafka-topics --bootstrap-server localhost:9092 --list
  __consumer_offsets
  demo-debezium
  demo-debezium.demo.t_user
  my_connect_configs
  my_connect_offsets
  my_connect_statuses
  schemahistory.demo
  ```

  - `demo-debezium`：所有 `DDL` 语句都写入的 [架构变更主题](https://debezium.io/documentation//reference/stable/connectors/mysql.html#mysql-schema-change-topic)。
  - `demo-debezium.demo.t_user`：捕获 `demo` 数据库中 `t_user` 表的变化事件。

- 查看 `CDC` 创建事件

  >提示：
  >
  >- 使用命令 `KAFKA_JMX_OPTS="" /usr/bin/kafka-console-consumer --topic demo-debezium.demo.t_user --from-beginning --bootstrap-server localhost:9092` 查看 `CDC` 事件。
  >- 使用命令 `KAFKA_JMX_OPTS="" /usr/bin/kafka-topics --describe --bootstrap-server localhost:9092 --topic demo-debezium.demo.t_user` 查看主题信息。

  示例：

  ```json
  {
    "schema": {
      "type": "struct",
      "fields": [
        {
          "type": "struct",
          "fields": [
            {
              "type": "int32",
              "optional": false,
              "field": "id"
            },
            {
              "type": "string",
              "optional": false,
              "field": "first_name"
            },
            {
              "type": "string",
              "optional": false,
              "field": "last_name"
            },
            {
              "type": "string",
              "optional": false,
              "field": "email"
            }
          ],
          "optional": true,
          "name": "dbserver1.inventory.customers.Value",
          "field": "before"
        },
        {
          "type": "struct",
          "fields": [
            {
              "type": "int32",
              "optional": false,
              "field": "id"
            },
            {
              "type": "string",
              "optional": false,
              "field": "first_name"
            },
            {
              "type": "string",
              "optional": false,
              "field": "last_name"
            },
            {
              "type": "string",
              "optional": false,
              "field": "email"
            }
          ],
          "optional": true,
          "name": "dbserver1.inventory.customers.Value",
          "field": "after"
        },
        {
          "type": "struct",
          "fields": [
            {
              "type": "string",
              "optional": true,
              "field": "version"
            },
            {
              "type": "string",
              "optional": false,
              "field": "name"
            },
            {
              "type": "int64",
              "optional": false,
              "field": "server_id"
            },
            {
              "type": "int64",
              "optional": false,
              "field": "ts_sec"
            },
            {
              "type": "string",
              "optional": true,
              "field": "gtid"
            },
            {
              "type": "string",
              "optional": false,
              "field": "file"
            },
            {
              "type": "int64",
              "optional": false,
              "field": "pos"
            },
            {
              "type": "int32",
              "optional": false,
              "field": "row"
            },
            {
              "type": "boolean",
              "optional": true,
              "field": "snapshot"
            },
            {
              "type": "int64",
              "optional": true,
              "field": "thread"
            },
            {
              "type": "string",
              "optional": true,
              "field": "db"
            },
            {
              "type": "string",
              "optional": true,
              "field": "table"
            }
          ],
          "optional": false,
          "name": "io.debezium.connector.mysql.Source",
          "field": "source"
        },
        {
          "type": "string",
          "optional": false,
          "field": "op"
        },
        {
          "type": "int64",
          "optional": true,
          "field": "ts_ms"
        },
        {
          "type": "int64",
          "optional": true,
          "field": "ts_us"
        },
        {
          "type": "int64",
          "optional": true,
          "field": "ts_ns"
        }
      ],
      "optional": false,
      "name": "dbserver1.inventory.customers.Envelope",
      "version": 1
    },
    "payload": {
      "before": null,
      "after": {
        "id": 1004,
        "first_name": "Anne",
        "last_name": "Kretchmar",
        "email": "annek@noanswer.org"
      },
      "source": {
        "version": "3.2.0.Final",
        "name": "dbserver1",
        "server_id": 0,
        "ts_sec": 0,
        "gtid": null,
        "file": "mysql-bin.000003",
        "pos": 154,
        "row": 0,
        "snapshot": true,
        "thread": null,
        "db": "inventory",
        "table": "customers"
      },
      "op": "r",
      "ts_ms": 1486500577691,
      "ts_us": 1486500577691547,
      "ts_ns": 1486500577691547930
    }
  }
  ```

- 查看 `CDC` 更新事件

  示例：

  ```json
  {
    "schema": {...},
    "payload": {
      "before": {  
        "id": 1004,
        "first_name": "Anne",
        "last_name": "Kretchmar",
        "email": "annek@noanswer.org"
      },
      "after": {  
        "id": 1004,
        "first_name": "Anne Marie",
        "last_name": "Kretchmar",
        "email": "annek@noanswer.org"
      },
      "source": {  
        "name": "3.2.0.Final",
        "name": "dbserver1",
        "server_id": 223344,
        "ts_sec": 1486501486,
        "gtid": null,
        "file": "mysql-bin.000003",
        "pos": 364,
        "row": 0,
        "snapshot": null,
        "thread": 3,
        "db": "inventory",
        "table": "customers"
      },
      "op": "u",  
      "ts_ms": 1486501486308,  
      "ts_us": 1486501486308910,  
      "ts_ns": 1486501486308910814  
    }
  }
  ```

- 查看 `CDC` 删除事件

  示例：

  ```json
  {
    "schema": {...},
    "payload": {
      "before": {  
        "id": 1004,
        "first_name": "Anne Marie",
        "last_name": "Kretchmar",
        "email": "annek@noanswer.org"
      },
      "after": null,  
      "source": {  
        "name": "3.2.0.Final",
        "name": "dbserver1",
        "server_id": 223344,
        "ts_sec": 1486501558,
        "gtid": null,
        "file": "mysql-bin.000003",
        "pos": 725,
        "row": 0,
        "snapshot": null,
        "thread": 3,
        "db": "inventory",
        "table": "customers"
      },
      "op": "d",  
      "ts_ms": 1486501558315,  
      "ts_us": 1486501558315901,  
      "ts_ns": 1486501558315901687  
    }
  }
  ```

- 查看 `CDC READ` 事件

  示例：

  ```json
  {
    "schema": {
      "type": "struct",
      "fields": [
        {
          "type": "struct",
          "fields": [
            {
              "type": "int64",
              "optional": false,
              "field": "id"
            },
            {
              "type": "string",
              "optional": false,
              "field": "username"
            },
            {
              "type": "string",
              "optional": false,
              "field": "password"
            },
            {
              "type": "int64",
              "optional": false,
              "name": "io.debezium.time.Timestamp",
              "version": 1,
              "field": "createTime"
            }
          ],
          "optional": true,
          "name": "demo-debezium.demo.t_user.Value",
          "field": "before"
        },
        {
          "type": "struct",
          "fields": [
            {
              "type": "int64",
              "optional": false,
              "field": "id"
            },
            {
              "type": "string",
              "optional": false,
              "field": "username"
            },
            {
              "type": "string",
              "optional": false,
              "field": "password"
            },
            {
              "type": "int64",
              "optional": false,
              "name": "io.debezium.time.Timestamp",
              "version": 1,
              "field": "createTime"
            }
          ],
          "optional": true,
          "name": "demo-debezium.demo.t_user.Value",
          "field": "after"
        },
        {
          "type": "struct",
          "fields": [
            {
              "type": "string",
              "optional": false,
              "field": "version"
            },
            {
              "type": "string",
              "optional": false,
              "field": "connector"
            },
            {
              "type": "string",
              "optional": false,
              "field": "name"
            },
            {
              "type": "int64",
              "optional": false,
              "field": "ts_ms"
            },
            {
              "type": "string",
              "optional": true,
              "name": "io.debezium.data.Enum",
              "version": 1,
              "parameters": {
                "allowed": "true,first,first_in_data_collection,last_in_data_collection,last,false,incremental"
              },
              "default": "false",
              "field": "snapshot"
            },
            {
              "type": "string",
              "optional": false,
              "field": "db"
            },
            {
              "type": "string",
              "optional": true,
              "field": "sequence"
            },
            {
              "type": "int64",
              "optional": true,
              "field": "ts_us"
            },
            {
              "type": "int64",
              "optional": true,
              "field": "ts_ns"
            },
            {
              "type": "string",
              "optional": true,
              "field": "table"
            },
            {
              "type": "int64",
              "optional": false,
              "field": "server_id"
            },
            {
              "type": "string",
              "optional": true,
              "field": "gtid"
            },
            {
              "type": "string",
              "optional": false,
              "field": "file"
            },
            {
              "type": "int64",
              "optional": false,
              "field": "pos"
            },
            {
              "type": "int32",
              "optional": false,
              "field": "row"
            },
            {
              "type": "int64",
              "optional": true,
              "field": "thread"
            },
            {
              "type": "string",
              "optional": true,
              "field": "query"
            }
          ],
          "optional": false,
          "name": "io.debezium.connector.mysql.Source",
          "version": 1,
          "field": "source"
        },
        {
          "type": "struct",
          "fields": [
            {
              "type": "string",
              "optional": false,
              "field": "id"
            },
            {
              "type": "int64",
              "optional": false,
              "field": "total_order"
            },
            {
              "type": "int64",
              "optional": false,
              "field": "data_collection_order"
            }
          ],
          "optional": true,
          "name": "event.block",
          "version": 1,
          "field": "transaction"
        },
        {
          "type": "string",
          "optional": false,
          "field": "op"
        },
        {
          "type": "int64",
          "optional": true,
          "field": "ts_ms"
        },
        {
          "type": "int64",
          "optional": true,
          "field": "ts_us"
        },
        {
          "type": "int64",
          "optional": true,
          "field": "ts_ns"
        }
      ],
      "optional": false,
      "name": "demo-debezium.demo.t_user.Envelope",
      "version": 2
    },
    "payload": {
      "before": null,
      "after": {
        "id": 1,
        "username": "userx",
        "password": "123456",
        "createTime": 1754225860000
      },
      "source": {
        "version": "3.2.0.Final",
        "connector": "mysql",
        "name": "demo-debezium",
        "ts_ms": 1754243877000,
        "snapshot": "last",
        "db": "demo",
        "sequence": null,
        "ts_us": 1754243877000000,
        "ts_ns": 1754243877000000000,
        "table": "t_user",
        "server_id": 0,
        "gtid": null,
        "file": "my-binlog.000002",
        "pos": 342,
        "row": 0,
        "thread": null,
        "query": null
      },
      "transaction": null,
      "op": "r",
      "ts_ms": 1754197077798,
      "ts_us": 1754197077798719,
      "ts_ns": 1754197077798719200
    }
  }
  ```



### `SpringBoot` 应用处理 `Connector` 事件

>详细用法请参考本站 [示例](https://gitee.com/dexterleslie/demonstration/tree/main/demo-debezium/demo-debezium-kafka)

