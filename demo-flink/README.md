## Flink是什么呢？

### Apache Flink 简介

Apache Flink 是由 Apache 软件基金会开发的一个**开源分布式流处理框架**，其核心是用 Java 和 Scala 编写的分布式流数据引擎。它既能处理**无界的实时数据流**（流处理），也能处理**有界的数据集**（批处理），是目前业界最主流的实时计算引擎之一。

---

### 起源与发展

Flink 诞生于柏林工业大学的学术研究项目 **StratoSphere**，早期以批处理为主。2014 年，核心成员将其重新定位为**流式计算**引擎，并捐赠给 Apache 基金会，成为 Apache 顶级项目。2019 年阿里巴巴收购了 Flink 的创始公司 Data Artisans，进一步推动了 Flink 在国内外的广泛应用。

---

### 核心特性

- **批流一体**：Flink 将批处理视为流处理的一种特例（有界流），用统一的引擎和 API 同时支持两种场景。
- **精确一次语义（Exactly-Once）**：通过 Checkpoint 和 Savepoint 机制，即使在发生故障时也能保证数据不丢失、不重复处理。
- **事件时间处理**：支持 Event-time、Processing-time 和 Ingestion-time 三种时间语义，配合 Watermark 机制优雅处理乱序和延迟数据。
- **丰富的状态管理**：提供 ValueState、ListState、MapState 等多种状态类型，支持内存和 RocksDB 等后端存储。
- **高吞吐、低延迟**：可在上千个节点上运行，实现毫秒级延迟和每秒数百万条事件的处理能力。

---

### 架构概览

Flink 的运行时架构主要包含三个角色：

- **Client**：负责向集群提交用户作业。
- **JobManager**：管理节点，负责调度任务、协调 Checkpoint，在 HA 模式下可有多个但只有一个主节点。
- **TaskManager**：工作节点，负责执行具体的计算任务，每个 TaskManager 包含多个 Slot（任务槽）。

---

### API 体系

Flink 提供了多层级的编程接口：

- **DataStream API**：核心流处理 API，支持 map、filter、window、keyBy 等丰富的转换操作。
- **Table API & SQL**：声明式编程接口，支持用类 SQL 语法处理流和批数据，降低了使用门槛。
- **CEP Library**：复杂事件处理库，用于模式匹配和事件流分析。
- **连接器（Connectors）**：与 Kafka、Kinesis、HDFS、Elasticsearch、JDBC 等外部系统无缝集成。

---

### 典型应用场景

- **实时数据分析与监控**：如实时仪表盘、网络流量异常检测
- **金融风控**：实时检测信用卡欺诈交易
- **电商实时推荐**：基于用户行为数据实时生成个性化推荐
- **实时 ETL 与数据集成**：替代传统批处理 ETL，实现低延迟的数据管道
- **物联网（IoT）**：处理海量传感器数据流

---

简单来说，Flink 就是一个**以流处理为核心、兼顾批处理**的大数据计算引擎，特别适合需要**低延迟、高可靠**的实时数据处理场景。如果你正在学习大数据技术栈，Flink 是目前实时计算领域最值得关注的框架之一。

## Flink有状态计算是什么呢？

### Flink 有状态计算

在 Flink 中，**"状态"（State）** 指的是在流处理过程中，算子（Operator）为了完成计算而需要**跨多条数据保存的中间结果或历史信息**。所谓"有状态计算"，就是计算当前数据时，不仅依赖当前这条数据本身，还需要参考之前处理过的数据所累积下来的信息。

---

### 通俗理解

举个简单的例子：

> 假设你要统计某个用户的**累计消费金额**。当第 1 笔订单到来时，你记住了"张三消费了 100 元"；当第 2 笔订单到来时，你需要知道之前已经累计了多少，才能算出新的总额。这个"之前累计的金额"就是**状态**。

如果不需要记住之前的信息，每条数据独立处理就能得到结果（比如简单的 `map` 转换），那就是**无状态计算**。

---

### 有状态 vs 无状态

| 对比维度 | 无状态计算                   | 有状态计算                              |
| -------- | ---------------------------- | --------------------------------------- |
| 特点     | 每条数据独立处理，不依赖历史 | 需要依赖历史数据或中间结果              |
| 典型算子 | `map`、`filter`、`print`     | `keyBy + reduce`、`window`、`aggregate` |
| 示例     | 将每条日志转为大写           | 统计每分钟的交易总额                    |

---

### 常见的状态类型

Flink 提供了多种状态结构来应对不同的业务场景：

- **ValueState\**：存储单个值，比如一个计数器、一个最新温度值。
- **ListState\**：存储一个列表，比如收集一段时间内的所有事件。
- **MapState\**：存储键值对映射，比如按商品 ID 统计各自的销量。
- **ReducingState\** / **AggregatingState\**：每次写入时自动进行聚合计算，无需手动取出再写回。

---

### 为什么状态管理很重要

有状态计算带来了很多能力，但也引入了几个核心挑战，Flink 对此提供了完善的解决方案：

#### 容错与一致性

Flink 通过 **Checkpoint 机制** 定期对状态做快照，保存到持久化存储（如 HDFS）。一旦任务失败，可以从最近的快照恢复，保证 **Exactly-Once（精确一次）** 语义——数据既不丢失也不重复处理。

#### 状态后端

状态可以存储在不同的后端中：

- **HashMapStateBackend**：状态存在 TaskManager 的 JVM 堆内存中，访问速度快，但受内存限制。
- **EmbeddedRocksDBStateBackend**：状态存在 RocksDB（本地磁盘）中，适合超大状态场景，支持增量 Checkpoint。

#### 状态 TTL（过期清理）

可以配置状态的存活时间，过期的状态会被自动清理，避免无限膨胀。

---

### 一个代码示例

```java
// 统计每个用户的访问次数
DataStream<String> stream = ...;

stream.keyBy(user -> user)
      .map(new RichMapFunction<String, Integer>() {
          // 声明一个 ValueState 来保存计数
          private ValueState<Integer> countState;

          @Override
          public void open(Configuration parameters) {
              countState = getRuntimeContext()
                  .getState(new ValueStateDescriptor<>("user-count", Integer.class));
          }

          @Override
          public Integer map(String user) throws Exception {
              Integer current = countState.value();
              if (current == null) current = 0;
              current += 1;
              countState.update(current);  // 更新状态
              return current;
          }
      });
```

---

### 总结

> **有状态计算 = 计算当前数据时，需要"记住"之前处理过的数据信息。**

这是 Flink 区别于简单流处理框架的核心优势之一。正是因为强大的状态管理能力，Flink 才能实现复杂窗口聚合、实时去重、实时风控等高级场景，同时通过 Checkpoint 保证故障恢复时的数据一致性。

## Flink有界流和无界流是什么呢？

### Flink 有界流与无界流

在 Flink 中，所有的数据都被抽象为**流（Stream）**，而根据数据是否有"尽头"，流被分为两种类型：**有界流（Bounded Stream）** 和 **无界流（Unbounded Stream）**。

---

### 无界流（Unbounded Stream）

无界流是指**有明确起点、但没有明确终点**的数据流。数据会持续不断地产生，理论上永远不会结束。

#### 特征

- 数据持续到达，没有终止信号
- 处理过程不能"等数据全部到齐再算"
- 必须边接收边处理

#### 典型场景

- Kafka 中的实时消息流
- 传感器持续上报的 IoT 数据
- 用户行为日志（点击、浏览、下单）
- 金融交易流水

#### 处理方式

通常依赖**时间窗口（Window）**或**计数窗口**来切分数据，在窗口内进行聚合计算。例如"每 5 分钟统计一次交易额"。

---

### 有界流（Bounded Stream）

有界流是指**有明确起点、也有明确终点**的数据集。数据总量是固定的，可以被完整读取。

#### 特征

- 数据量有限，可以全部读入
- 可以等所有数据到齐后再统一处理
- 本质上就是我们传统意义上的"批数据"

#### 典型场景

- HDFS / S3 上的历史日志文件
- 数据库中的存量表
- CSV、Parquet 等静态文件

#### 处理方式

可以对整个数据集做全局排序、全量聚合等操作，无需担心数据还在持续到达。

---

### 对比总结

| 对比维度     | 无界流                 | 有界流                    |
| ------------ | ---------------------- | ------------------------- |
| 数据终点     | 无终点，持续产生       | 有终点，数据有限          |
| 处理模式     | 流处理（Streaming）    | 批处理（Batch）           |
| 能否全量读取 | 不能，数据还在不断到来 | 可以，数据已全部就绪      |
| 典型数据源   | Kafka、Kinesis、Socket | 文件（HDFS/S3）、数据库表 |
| 延迟要求     | 低延迟，实时响应       | 对延迟要求相对宽松        |

---

### 批流一体的核心理念

Flink 最核心的设计理念就是：**有界流只是无界流的一种特例**。

> 传统大数据框架（如 Spark）是"批处理引擎 + 微批模拟流"，而 Flink 是"流处理引擎 + 原生支持批"。

这意味着：

- 你用同一套 API（DataStream API 或 Table API / SQL）编写的程序，既可以跑在无界流上（实时模式），也可以跑在有界流上（批模式）。
- Flink 会根据数据源的特性自动选择合适的执行策略，而开发者无需改代码。

用一个图来理解：

```
无界流：  ──▶ ──▶ ──▶ ──▶ ──▶ ──▶ ──▶ ──▶ ···（永不停止）

有界流：  ──▶ ──▶ ──▶ ──▶ ──▶ ──▶ ──▶ ──▶ ──▶ END（有终点）
```

---

### 实际例子

```java
// 无界流：从 Kafka 持续消费
DataStream<String> kafkaStream = env.addSource(
    new FlinkKafkaConsumer<>("topic", new SimpleStringSchema(), properties)
);

// 有界流：读取 HDFS 上的文件
DataStream<String> fileStream = env.readTextFile("hdfs:///data/logs/2026-06-24.log");
```

两段代码用的是同一套 DataStream API，但底层执行策略不同——前者是持续运行的流作业，后者读完文件就会结束。

---

### 总结

> - **无界流** = 数据永远在处理中，是真正的"实时流"
> - **有界流** = 数据有尽头，本质就是"批数据"
> - Flink 用**统一的流处理引擎**同时覆盖这两种场景，这就是"批流一体"

## Flink和Spark的区别，使用哪个好呢？

### Flink 与 Spark 的核心区别

Flink 和 Spark 都是当前最主流的分布式大数据处理框架，但它们的设计哲学截然不同：**Flink 是"为流而生"，Spark 是"从批出发"**。这个根本差异决定了它们在延迟、状态管理、容错等方面的表现差异。

---

### 处理模型（最根本的区别）

- **Flink：原生流处理（Native Streaming）**
  数据来一条处理一条（Event-by-Event），是真正的逐事件实时处理。批处理被当作"有界流"的特例来对待。

- **Spark：微批处理（Micro-batch）**
  将流数据切分成极小的时间片（如 1 秒），每个时间片当作一个小批次来执行。虽然 Spark 3.x 引入了 Continuous Processing 模式，但生产环境使用较少且功能受限。

---

### 关键维度对比

| 对比维度       | Flink                                              | Spark                                             |
| -------------- | -------------------------------------------------- | ------------------------------------------------- |
| **延迟**       | 毫秒级（10ms ~ 100ms）                             | 秒级（1s ~ 几秒）                                 |
| **处理模型**   | 原生流处理，逐条处理                               | 微批处理，按批次处理                              |
| **时间语义**   | 原生支持事件时间（Event Time），Watermark 机制成熟 | 支持但较重，大规模乱序数据处理时延迟较高          |
| **状态管理**   | 轻量高效，专为长运行流任务设计，支持 RocksDB 后端  | 基于 RDD/DataFrame，长流任务中开销较大            |
| **容错语义**   | 端到端 Exactly-Once（精确一次）                    | 流处理通常为 At-Least-Once，批处理为 Exactly-Once |
| **反压机制**   | 自然反压，基于数据流图的自然阻塞，反应迅速         | 周期性反压，基于批次调度，有滞后                  |
| **批处理能力** | 有提升但生态丰富度略逊于 Spark                     | 非常成熟，大规模离线数仓场景表现优异              |
| **机器学习**   | FlinkML 相对薄弱                                   | MLlib 生态完善，算法库丰富                        |
| **图计算**     | Gelly 成熟度较低                                   | GraphX 功能强大                                   |
| **语言支持**   | Java、Scala、Python（Python 支持较弱）             | Java、Scala、Python、R（Python 支持好）           |
| **社区生态**   | 增长迅速，但相对较小                               | 社区更大、更成熟，文档和第三方集成丰富            |



---

### 各自的优势场景

#### 选 Flink 的场景

- **毫秒级低延迟要求**：如金融风控、高频交易监控，差一毫秒都可能出问题
- **复杂状态管理**：需要维护大量中间状态（如实时去重、累计统计）
- **严重乱序数据**：依赖事件时间和 Watermark 精确处理延迟数据
- **复杂事件处理（CEP）**：如"连续 5 次失败登录触发告警"
- **实时数据集成**：Flink CDC 已成为数据库到数据湖/仓库的首选方案



#### 选 Spark 的场景

- **大规模离线批处理**：每天/每周的报表、ETL 流水线
- **准实时场景（秒级可接受）**：如每分钟更新一次的数据大屏
- **重度依赖机器学习**：需要频繁调用 MLlib 进行模型训练
- **团队已精通 Spark**：存量代码多，迁移成本高
- **复杂 SQL 分析**：Spark SQL 生态更成熟，优化器（Catalyst）更完善



---

### 选型建议

选择 Flink 还是 Spark，核心看你的**业务需求**，而不是哪个"更先进"：

- **延迟要求 < 1 秒** → 选 Flink，只有原生流架构能稳定保证
- **延迟要求 > 5 秒（准实时）** → Spark 完全够用，且开发效率更高
- **批处理为主 + 偶尔流处理** → Spark 更合适
- **流处理为主 + 偶尔批处理** → Flink 更合适
- **团队技术栈** → 如果团队已经精通其中一个，优先考虑现有技能栈，降低维护成本



---

### 当前趋势

从 2025-2026 年的行业趋势来看：

- **Flink 在实时领域的统治力不可动摇**，随着 Flink CDC 的成熟，它正在成为实时数据集成的事实标准。
- **Spark 在批处理和机器学习领域依然是王者**，短期内不会被替代。
- 两者正在**互相渗透**：Flink 的批处理能力越来越强，Spark 也在努力改进流处理能力。但在实际生产中，很多公司会**同时使用两者**，各取所长。

> 一句话总结：**极致实时选 Flink，大规模批处理和 ML 选 Spark，准实时场景两者皆可，看团队熟悉度。**

## Flink DataSet API和DataStream API是什么呢？

>DataSet和DataStream API的基本用法参考本站示例：https://gitee.com/dexterleslie/demonstration/tree/main/demo-flink/demo-flink-tutorial

### Flink DataSet API 与 DataStream API

Flink 提供了多层级的编程 API，其中 **DataSet API** 和 **DataStream API** 属于核心 API 层。它们分别面向批处理和流处理场景，但 Flink 的核心理念是"批流一体"，两者共享底层运行时引擎。

> **重要提示**：从 Flink 1.12 版本开始，DataSet API 已被标记为**过时（Legacy）**，官方建议迁移到 DataStream API 或 Table API/SQL。在 Flink 2.0 中，DataSet API 已被**完全移除**。

---

### DataStream API（流处理核心）

DataStream API 是 Flink 处理**无界流（实时流）**的核心编程接口，数据来一条处理一条，专为低延迟、高吞吐的实时场景设计。

#### 核心特点

- **执行环境**：`StreamExecutionEnvironment`
- **数据抽象**：`DataStream<T>`，代表一个持续不断的事件序列
- **执行模型**：持续增量计算，数据到达即处理
- **时间语义**：支持事件时间（Event Time）、处理时间（Processing Time）、摄入时间（Ingestion Time），配合 Watermark 机制处理乱序数据
- **状态管理**：完善的键控状态和算子状态机制，通过 Checkpoint 保证 Exactly-Once 语义

#### 代码示例

```java
// 创建流处理执行环境
StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

// 从 Kafka 读取数据流
DataStream<String> stream = env.addSource(new FlinkKafkaConsumer<>(...));

// 转换操作
stream.map(value -> value.toUpperCase())
      .filter(value -> value.length() > 3)
      .print();

// 触发执行
env.execute("Stream Job");
```

#### 适用场景

- 实时监控告警
- 实时推荐系统
- 金融欺诈检测
- IoT 传感器数据处理

---

### DataSet API（批处理，已废弃）

DataSet API 是 Flink 早期用于处理**有界数据集（批数据）**的编程接口，采用惰性求值（Lazy Evaluation）机制，等所有转换操作定义完毕后，调用 `execute()` 触发整个作业执行。

#### 核心特点

- **执行环境**：`ExecutionEnvironment`
- **数据抽象**：`DataSet<T>`，代表一个有限的数据集合
- **执行模型**：批处理执行，数据全部就绪后统一处理
- **时间语义**：主要关注处理时间，关注整体吞吐量
- **状态管理**：相对简单，基于算子状态的批量管理

#### 代码示例

```java
// 创建批处理执行环境
ExecutionEnvironment env = ExecutionEnvironment.getExecutionEnvironment();

// 从文件读取数据集
DataSet<String> dataSet = env.readTextFile("hdfs:///data/logs.txt");

// 转换操作
dataSet.map(value -> value.toUpperCase())
       .filter(value -> value.length() > 3)
       .print();

// 触发执行
env.execute("Batch Job");
```

#### 适用场景

- 历史数据分析
- 周期性报表生成
- 大规模 ETL 操作

---

### 对比总结

| 对比维度 | DataStream API                                | DataSet API（已废弃）    |
| -------- | --------------------------------------------- | ------------------------ |
| 执行环境 | `StreamExecutionEnvironment`                  | `ExecutionEnvironment`   |
| 数据抽象 | `DataStream<T>`（无界流）                     | `DataSet<T>`（有界集）   |
| 执行模型 | 持续增量计算，逐条处理                        | 惰性求值，批量执行       |
| 时间语义 | Event Time / Processing Time / Ingestion Time | 主要关注 Processing Time |
| 状态管理 | 完善（键控状态、算子状态、Checkpoint）        | 相对简单                 |
| 窗口机制 | 滚动窗口、滑动窗口、会话窗口                  | 不支持                   |
| 容错语义 | Exactly-Once                                  | 批处理天然保证           |
| 延迟特性 | 毫秒级                                        | 分钟到小时级             |
| 当前状态 | **活跃维护，推荐使用**                        | **Flink 2.0 已移除**     |

---

### 现在该怎么选

既然 DataSet API 已经被废弃，批处理场景有以下两种替代方案：

- **用 DataStream API + Batch 模式**：设置执行模式为 `RuntimeExecutionMode.BATCH`，用同一套 DataStream API 处理批数据，代码与流处理几乎一致。

```java
StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
env.setRuntimeMode(RuntimeExecutionMode.BATCH);  // 切换为批处理模式
```

- **用 Table API / SQL**：这是官方推荐的批处理方式，声明式编程，门槛更低，优化器会自动选择最优执行策略。

> 总结：DataStream API 是 Flink 的**统一核心 API**，既能处理流也能处理批；DataSet API 已成为历史，新项目不要再使用。
