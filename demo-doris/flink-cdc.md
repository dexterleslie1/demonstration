## 注意：数据库不能写DEC数据类型

```sql
# 错误写法，flink不支持dec数据类型错误
alter table auth add column col1 dec(20,2);

# 正确写法
alter table auth add column col1 decimal(20,2);
```

## flink cdc数据同步出错处理步骤

详细用法请参考本站示例：https://gitee.com/dexterleslie/demonstration/tree/main/demo-doris

查看jobmanager日志

```sh
docker compose logs -f --tail 10 flink-jobmanager
```

重新启动flink cdc jobmanager以删除所有同步job

```sh
docker compose restart flink-jobmanager
```

删除Doris中报错的表（其他没有报错的表可以不删除）和删除flink cdc checkpoint数据，重新启动flink cdc submit提交新任务重新同步数据

```sh
docker compose up -d flink-cdc-submit
```

## flink是什么呢？

Apache Flink 是一个开源的分布式流式计算引擎，也是目前大数据实时计算领域的主流框架。简单来说，它就是一个能够以**高性能、高吞吐、低延迟**的方式，对海量数据进行实时处理和分析的计算平台。

为了让你更全面地了解 Flink，我们可以从以下几个核心维度来看：

### 核心亮点：流批一体
Flink 最大的特点是实现了**“流批一体”**。
*   **流处理（Streaming）：** 数据源源不断地产生，Flink 可以做到“来一条处理一条”，实现毫秒级的低延迟处理。非常适合实时大屏、实时监控等场景。
*   **批处理（Batch）：** 处理有明确开始和结束的固定数据集。
*   **统一框架：** 在 Flink 中，批处理被视为流处理的一种特殊情况（有界流）。这意味着你可以用同一套代码和框架，同时搞定实时和离线两种业务场景。

### Flink 的核心优势
*   **极低延迟与高吞吐：** 专为实时而生，能够提供毫秒级的处理时延，同时保持极高的数据吞吐量。
*   **精准的时间语义：** 能够完美处理数据乱序、延迟到达等问题。它通过“事件时间（Event-time）”和“水位线（Watermark）”机制，确保计算结果的准确性。
*   **强大的状态管理：** 可以在计算过程中存储中间数据（状态），即使面对海量数据也能高效维护。
*   **高可靠与容错：** 拥有强大的容错机制（如 Checkpoint 和 Savepoint），即使任务宕机也能自动恢复，并保证数据**“精准一次（Exactly-Once）”**被处理，绝不丢失或重复。

### 常见的编程方式
Flink 提供了多种抽象层级的 API，你可以根据自己的需求和技术栈选择：
*   **Flink SQL：** 上手最简单，只需要写标准的 SQL 语句就能完成实时统计、报表和数据同步。目前是企业数仓和云平台中最主流的用法。
*   **DataStream API（Java/Python）：** 也就是常说的 Java Flink 或 PyFlink。适合开发稍微复杂的自定义逻辑，灵活性更高。
*   **底层 API：** 适合极度硬核的底层性能优化和复杂业务逻辑开发。

### 典型应用场景
*   **实时数仓与报表：** 比如电商大促时的实时成交额统计、实时大屏展示。
*   **实时风控与监控：** 比如支付宝的实时交易反欺诈检测、服务器日志的实时异常报警。
*   **物联网（IoT）数据处理：** 对海量传感器数据进行实时清洗和分析。
*   **数据管道（ETL）：** 持续不断地将数据从一个存储系统同步并转换到另一个系统。

目前，包括阿里巴巴、华为、小米等在内的众多大厂，都在大规模使用 Flink 来支撑其核心数据业务。如果你正准备踏入实时计算领域，Flink 绝对是首选的核心技术。

## flink CDC是什么呢？

Flink CDC 是 Apache Flink 生态中的一个**实时数据集成工具**。

为了让你更好理解，可以把它看作是 Flink 的“超级数据搬运工”。它的核心作用是**实时捕获业务数据库（如 MySQL、PostgreSQL）中的数据变化，并将这些变化自动、准确地同步到下游系统（如 Kafka、数据仓库、数据湖等）**。

### 它和普通的数据同步工具有什么不同？
传统的数据同步（比如每天凌晨跑一次的定时 ETL）通常有延迟，且对数据库压力大。Flink CDC 采用了**基于日志（Log-based）**的机制，它的工作原理非常巧妙：

1. **监听数据库日志**：它不去频繁查询你的业务数据库，而是直接读取数据库的“操作流水账”（例如 MySQL 的 Binlog）。
2. **捕获变更事件**：只要数据库里有数据发生插入（INSERT）、更新（UPDATE）或删除（DELETE），Flink CDC 就能在毫秒级内感知到。
3. **流式同步**：将这些变化转化为数据流，交给 Flink 引擎进行后续的处理或直接写入目标系统。

### Flink CDC 的核心优势
* **极低延迟**：能够实现秒级甚至毫秒级的数据同步，让下游的实时报表、风控系统拿到最新鲜的数据。
* **全量增量一体化**：这是它的一大亮点。在第一次同步时，它会自动先读取数据库的历史全量数据，读取完成后无缝切换到实时监听增量日志，整个过程无需人工干预，且**不需要锁表**，不影响线上业务。
* **数据精准不丢失**：依托 Flink 强大的容错机制，它保证了数据在整个链路中**“精准一次（Exactly-Once）”**被处理，绝对不丢数据，也不重复处理。
* **表结构自动同步（Schema Evolution）**：如果你的业务数据库加了新字段或改了表结构，Flink CDC 可以自动感知并将这些变更同步到下游，无需手动修改同步任务。

### 两种主流的使用方式
Flink CDC 提供了非常灵活的使用门槛，无论你是写代码的开发者，还是偏运维的数据工程师都能轻松上手：

1. **YAML 配置化（零代码）**：你只需要写一个类似下面的配置文件，告诉它源数据库和目标数据库的信息，就能拉起一个实时同步任务。
```yaml
source:
  type: mysql
  hostname: localhost
  port: 3306
  username: root
  password: 123456
  tables: app_db.*  # 监控整个库的表
sink:
  type: doris       # 同步到 Doris 数据仓库
  fenodes: 127.0.0.1:8030
pipeline:
  name: MySQL to Doris Sync
  parallelism: 2
```
2. **SQL / DataStream API**：如果你有更复杂的清洗、关联需求，可以直接在 Flink SQL 中使用 `flink-connector-mysql-cdc` 等连接器，像查普通表一样查询数据库的实时变更流。

### 典型应用场景
* **实时数仓构建**：将业务库（MySQL/Oracle）的数据实时同步到 ClickHouse、Doris 或 StarRocks 中，供 BI 报表实时查询。
* **缓存实时更新**：当数据库中的商品价格或库存发生变化时，实时同步更新到 Redis 缓存中。
* **跨系统数据打通**：比如将订单系统的实时数据同步到推荐系统或物流追踪系统，实现业务的实时联动。

简单来说，如果你需要把数据库里的数据**又快、又准、又稳**地搬运到其他地方做实时分析，Flink CDC 就是目前业界最主流、最好用的解决方案之一。

## Flink支持配置式指定表实时同步MariaDB数据到Doris吗？

**完全支持。** 你可以通过 Flink CDC 的 YAML 配置方式，轻松实现从 MariaDB 到 Doris 的实时数据同步。

Flink CDC 完美兼容 MariaDB，并且在最新的 Pipeline 模式下，支持通过简单的配置文件来指定同步特定的表或整个数据库。

### 核心配置方法
在配置文件中，你只需要关注两个核心部分：
1. **Source（数据源）**：将 `type` 设置为 `mysql`（Flink CDC 底层通过 MySQL 协议兼容 MariaDB），并在 `tables` 参数中通过正则表达式指定你要同步的特定表。
2. **Sink（数据写入端）**：将 `type` 设置为 `doris`，并填入 Doris 集群的连接信息。

### 配置示例（YAML）
假设你想将 MariaDB 中 `order_db` 库下的 `users` 表和所有以 `order_` 开头的表实时同步到 Doris，配置文件（如 `mariadb-to-doris.yaml`）可以这样写：

```yaml
source:
  type: mysql              # MariaDB 使用 mysql 类型即可
  hostname: your_mariadb_ip
  port: 3306
  username: your_username
  password: your_password
  tables: order_db.users, order_db.order_.*  # 指定单表或多表（支持正则）
  server-id: 5400-5404     # 必须配置，模拟 MySQL/MariaDB 的从节点 ID

sink:
  type: doris
  fenodes: your_doris_fe_ip:8030
  username: root
  password: your_doris_password
  # 自动建表相关属性（可选）
  table.create.properties.replication_num: 1 

pipeline:
  name: MariaDB to Doris Sync
  parallelism: 2
```

### 如何提交任务
配置好 YAML 文件后，你可以使用 Flink CDC 提供的命令行工具直接提交任务，无需编写任何 Java/Python 代码：
```bash
bin/flink-cdc.sh mariadb-to-doris.yaml
```

### 这种配置式同步的优势
* **零代码开发**：通过声明式的 YAML 配置即可完成复杂的实时同步任务。
* **自动建表与结构同步**：任务启动后，Flink CDC 会自动在 Doris 中创建对应的表结构。如果后续 MariaDB 的表增加了字段（DDL变更），也能自动同步到 Doris，无需人工干预。
* **全量与增量一体化**：首次运行会先自动同步历史存量数据，完成后无缝切换到实时增量同步，且全程不锁表，不影响线上业务。

如果你使用的是较老版本的 Flink Doris Connector（非 Pipeline 模式），也可以通过命令行传参的方式（`mysql-sync-database`）配合 `--including-tables` 参数来实现指定表的同步。

## Flink JobManager和TaskManager是什么呢？

在 Flink 的分布式架构中，**JobManager** 和 **TaskManager** 是最核心的两个进程，它们共同构成了 Flink 集群的“主从架构（Master-Worker）”。

为了让你更容易理解，我们可以用公司里的角色来打个比方：

### JobManager：集群的“大老板”（主节点 Master）
JobManager 是整个 Flink 集群的**协调者和控制中心**，它不负责具体的脏活累活，而是负责指挥和调度。它的主要职责包括：
*   **接收任务（作业管理）**：接收你（客户端）提交上来的 Flink 作业（Job），并将作业转换成内部的执行图（ExecutionGraph）。
*   **资源调度**：向资源管理器（ResourceManager）申请计算资源，并将具体的任务（Task）分配给合适的 TaskManager。
*   **容错与恢复**：它是 Flink 容错机制的核心。负责定期触发检查点（Checkpoint），如果某个任务执行失败了，JobManager 会负责从检查点恢复任务，保证数据不丢不重。
*   **状态监控**：实时监控整个集群和作业的运行状态，如果 TaskManager 挂了，它会第一时间知道并做出反应。

### TaskManager：集群的“打工人”（工作节点 Worker）
TaskManager 是 Flink 集群中的**实际执行者**，它负责干“苦力活”。它的主要职责包括：
*   **执行具体任务**：真正运行 JobManager 分配下来的具体计算任务（比如你写的 Map、Filter、聚合等算子逻辑）。
*   **数据交换**：负责在不同的任务之间进行数据的传输和交换（比如 Shuffle 过程），将处理完的数据传给下一个环节。
*   **资源上报**：启动时会向 JobManager 汇报自己有多少资源可用，并定期发送心跳，告诉老板“我还活着，正在干活”。

---

### 核心概念：Task Slot
在了解 TaskManager 时，你一定会遇到 **Task Slot（任务槽）** 这个概念。
*   **什么是 Slot？** 它是 TaskManager 中**资源调度的最小单位**。每个 TaskManager 是一个独立的 JVM 进程，你可以把它想象成一台电脑，而 Slot 就是这台电脑上的 CPU 核心。
*   **有什么作用？** 如果一个 TaskManager 有 3 个 Slot，就意味着它可以同时并行执行 3 个子任务。通过调整 Slot 的数量，你可以控制一个 TaskManager 的并行处理能力。
*   **槽共享（Slot Sharing）**：Flink 允许同一个作业的不同任务共享同一个 Slot（比如把 Source、Map、Sink 串在一起放在一个 Slot 里跑），这能极大提高资源的利用率。

### 总结对比

| 角色            | 类比          | 核心职责                                      | 数量                               |
| :-------------- | :------------ | :-------------------------------------------- | :--------------------------------- |
| **JobManager**  | 大老板 / 大脑 | 接收作业、调度任务、容错恢复、协调 Checkpoint | 至少 1 个（高可用模式下有多个）    |
| **TaskManager** | 打工人 / 身体 | 执行具体的算子逻辑、数据交换、汇报心跳        | 1 个或多个（干活的越多，处理越快） |

简单来说，当你提交一个 Flink 任务时，**JobManager 负责“怎么干、谁来干”，而 TaskManager 负责“埋头苦干”**。