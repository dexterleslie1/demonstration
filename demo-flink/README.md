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

## Flink算子是什么呢？

### Flink 算子（Operator）

在 Flink 中，**算子（Operator）** 就是数据流处理管道中的**一个处理节点**，它接收输入数据、执行某种计算逻辑、然后输出结果。你可以把它理解成流水线上的一个"工位"——数据从上游流进来，经过这个工位的加工处理后，再流向下游。

---

### 算子在数据流中的位置

```
数据源(Source) → [算子1] → [算子2] → [算子3] → 数据汇(Sink)
                   ↑          ↑          ↑
                 每个方框就是一个算子
```

当你调用 `map()`、`filter()`、`flatMap()` 等方法时，Flink 内部就会创建对应的算子，串联成一个**算子链（Operator Chain）**，也就是执行计划中的 DAG（有向无环图）。

---

### 算子的分类

Flink 中的算子主要分为以下几类：

#### 数据源算子（Source Operator）

负责从外部系统读取数据，是数据流的起点。

```java
// 从集合创建
env.fromElements("hello", "world");

// 从 Kafka 读取
env.addSource(new FlinkKafkaConsumer<>(...));

// 从文件读取
env.readTextFile("hdfs:///data.txt");
```

#### 转换算子（Transformation Operator）

对数据进行加工处理，是最常用的算子类型。

| 算子        | 作用                                         | 是否有状态 |
| ----------- | -------------------------------------------- | ---------- |
| `map`       | 一对一转换                                   | 无状态     |
| `flatMap`   | 一对多转换 + 拍平                            | 无状态     |
| `filter`    | 过滤数据                                     | 无状态     |
| `keyBy`     | 按 key 分区（逻辑分组）                      | 无状态     |
| `reduce`    | 聚合（两两归并）                             | 有状态     |
| `aggregate` | 聚合（增量计算）                             | 有状态     |
| `process`   | 最底层的通用算子，可访问状态、定时器、侧输出 | 可有状态   |
| `window`    | 窗口聚合                                     | 有状态     |
| `union`     | 合并多个流                                   | 无状态     |
| `connect`   | 连接两个不同类型的流                         | 无状态     |

#### 数据汇算子（Sink Operator）

负责将处理结果输出到外部系统，是数据流的终点。

```java
// 打印到控制台
stream.print();

// 写入 Kafka
stream.addSink(new FlinkKafkaProducer<>(...));

// 写入文件
stream.writeAsText("hdfs:///output/result.txt");

// 写入 Elasticsearch
stream.addSink(new ElasticsearchSink<>(...));
```

---

### 算子链（Operator Chaining）

Flink 有一个重要的优化机制叫**算子链**：将多个满足条件的算子合并成一个任务（Task），在同一个线程中执行，避免不必要的序列化和网络传输。

```
未优化（每个算子一个 Task）：
  [Source] → 网络传输 → [Map] → 网络传输 → [Filter] → 网络传输 → [Sink]

算子链优化后（合并为一个 Task）：
  [Source → Map → Filter → Sink]  在一个线程中直接执行
```

#### 算子链合并的条件

- 两个算子的并行度相同
- 两个算子在同一个 slot sharing group 中
- 没有被 `disableChaining()` 或 `startNewChain()` 打断
- 算子之间没有发生数据重分区（如 `keyBy`、`rebalance`）

---

### 算子与 Task、Slot 的关系

```
TaskManager（JVM 进程）
├── Slot 1
│   └── Task（一个算子链 = 一个 Task）
├── Slot 2
│   └── Task
└── Slot 3
    └── Task
```

- **算子（Operator）**：逻辑概念，代表一个处理步骤
- **算子链（Operator Chain）**：多个算子合并后的逻辑单元
- **Task**：算子链的物理执行单元，运行在 Slot 中
- **Slot**：TaskManager 中的资源槽位，一个 Slot 可以运行一个 Task

---

### 自定义算子

当内置算子不能满足需求时，可以通过继承富函数（Rich Function）来自定义算子：

```java
stream.map(new RichMapFunction<String, Integer>() {

    // open() 在算子启动时调用一次，用于初始化
    @Override
    public void open(Configuration parameters) throws Exception {
        // 初始化数据库连接、状态等
    }

    // map() 每条数据调用一次
    @Override
    public Integer map(String value) throws Exception {
        return value.length();
    }

    // close() 在算子关闭时调用一次，用于释放资源
    @Override
    public void close() throws Exception {
        // 关闭数据库连接等
    }
});
```

---

### 总结

> - **算子 = 数据流中的一个处理节点**，负责接收、处理、输出数据
> - 分为 **Source（数据源）→ Transformation（转换）→ Sink（数据汇）** 三大类
> - Flink 通过**算子链优化**将多个算子合并执行，减少开销
> - 可以通过 Rich Function 自定义算子逻辑，访问状态、定时器等高级功能

## Flink执行环境有哪些呢？

### Flink 执行环境（Execution Environment）

在 Flink 中，**执行环境（Execution Environment）** 是所有 Flink 程序的**入口和上下文**。它负责管理程序的执行、配置运行参数、注册数据源和转换操作，最终触发作业的执行。

你可以把它理解为 Flink 程序的"舞台"——所有的算子、数据源、数据汇都要在这个舞台上搭建和运行。

---

### 三大执行环境

#### StreamExecutionEnvironment（流处理环境）

这是目前**最核心、最常用**的执行环境，用于处理无界流（实时流）和有界流（批数据）。

```java
// 创建流处理执行环境
StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

// 定义数据流
DataStream<String> stream = env.fromElements("hello", "world");

// 转换处理
stream.map(String::toUpperCase).print();

// 触发执行（必须调用，否则程序不会运行）
env.execute("My Stream Job");
```

**常用工厂方法：**

| 方法                                            | 说明                                                         |
| ----------------------------------------------- | ------------------------------------------------------------ |
| `getExecutionEnvironment()`                     | 自动判断运行上下文（IDE 中本地执行，提交到集群则分布式执行） |
| `createLocalEnvironment()`                      | 强制创建本地执行环境                                         |
| `createRemoteEnvironment(host, port, jarFiles)` | 连接到远程集群执行                                           |

---

#### ExecutionEnvironment（批处理环境，已废弃）

这是早期 DataSet API 的执行环境，专门用于处理有界数据集（批数据）。

```java
// 创建批处理执行环境
ExecutionEnvironment env = ExecutionEnvironment.getExecutionEnvironment();

// 定义数据集
DataSet<String> dataSet = env.readTextFile("hdfs:///data.txt");

// 转换处理
dataSet.map(String::toUpperCase).print();

// 触发执行
env.execute("My Batch Job");
```

> ⚠️ 如前面提到的，DataSet API 在 Flink 1.12 被标记为过时，Flink 2.0 已完全移除。批处理场景现在统一使用 `StreamExecutionEnvironment` + `RuntimeExecutionMode.BATCH`。

---

#### TableEnvironment（表/SQL 环境）

这是 Table API 和 Flink SQL 的执行环境，提供声明式的编程方式，可以用 SQL 语句处理数据。

```java
// 创建流式表环境
StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
StreamTableEnvironment tableEnv = StreamTableEnvironment.create(env);

// 用 SQL 查询
tableEnv.executeSql("SELECT * FROM my_table WHERE age > 18");
```

**常用工厂方法：**

| 方法                                 | 说明                             |
| ------------------------------------ | -------------------------------- |
| `StreamTableEnvironment.create(env)` | 基于流环境创建，支持流批两种模式 |
| `TableEnvironment.create(settings)`  | 独立创建，通过配置指定批/流模式  |

---

### 三者的关系

```
┌─────────────────────────────────────────────┐
│           StreamExecutionEnvironment        │  ← 最底层，核心运行时
│  ┌───────────────────────────────────────┐  │
│  │        StreamTableEnvironment         │  │  ← 建立在流环境之上
│  │   （Table API / Flink SQL 编程接口）    │  │
│  └───────────────────────────────────────┘  │
└─────────────────────────────────────────────┘

┌─────────────────────────────────────────────┐
│         ExecutionEnvironment（已废弃）        │  ← 早期批处理环境
└─────────────────────────────────────────────┘
```

---

### 执行模式（RuntimeExecutionMode）

`StreamExecutionEnvironment` 支持三种执行模式，通过设置来决定如何处理数据：

| 模式        | 说明                                   | 适用场景           |
| ----------- | -------------------------------------- | ------------------ |
| `STREAMING` | 流式执行，逐条处理，持续运行           | 实时流处理（默认） |
| `BATCH`     | 批式执行，数据全部就绪后统一处理       | 离线批处理         |
| `AUTOMATIC` | 自动判断，根据数据源类型选择流或批模式 | 不确定数据源类型时 |

```java
StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

// 设置为批处理模式
env.setRuntimeMode(RuntimeExecutionMode.BATCH);
```

---

### 环境配置

执行环境还提供了丰富的配置方法：

```java
StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

// 设置并行度（全局）
env.setParallelism(4);

// 设置 Checkpoint 间隔（毫秒）
env.enableCheckpointing(60000);

// 设置 Checkpoint 模式
env.getCheckpointConfig().setCheckpointingMode(CheckpointingMode.EXACTLY_ONCE);

// 设置最大并行度
env.setMaxParallelism(128);
```

---

### 总结

> - **`StreamExecutionEnvironment`**：当前唯一推荐的执行环境，流批通用
> - **`ExecutionEnvironment`**：已废弃，不要再使用
> - **`TableEnvironment`**：基于流环境构建，提供 SQL 声明式编程能力
> - 所有 Flink 程序的第一步都是**创建执行环境**，最后一步都是调用 **`execute()`** 触发执行

## Flink源算子有哪些呢？

### Flink 源算子（Source Operator）

源算子是 Flink 数据流的**起点**，负责从外部系统或数据源读取数据，构建出 `DataStream`，供后续的转换算子处理。根据数据来源的不同，Flink 提供了多种源算子。

---

### 新旧 API 对比

从 Flink 1.12 开始，Source API 发生了重大变化：

| 版本                  | API 方式                                            | 说明                                      |
| --------------------- | --------------------------------------------------- | ----------------------------------------- |
| Flink 1.12 之前（旧） | `env.addSource(new MySource())`                     | 基于 `SourceFunction` 接口，不支持并行    |
| Flink 1.12 之后（新） | `env.fromSource(source, watermarkStrategy, "name")` | 基于 FLIP-27 新 Source 架构，支持流批一体 |

---

### 内置源算子分类

#### 基于集合的 Source

用于学习和测试场景，将内存中的数据转为数据流，**并行度为 1**。

```java
// 从可变参数创建
env.fromElements(1, 2, 3, 4, 5);

// 从 Collection 创建
env.fromCollection(Arrays.asList("a", "b", "c"));

// 从 Iterator 创建（需指定类型信息）
env.fromCollection(new CustomIterator(), BasicTypeInfo.INT_TYPE_INFO);

// 生成序列
env.generateSequence(1, 100);

// 并行集合（支持并行度 > 1）
env.fromParallelCollection(new SplittableIterator(), BasicTypeInfo.LONG_TYPE_INFO);
```

#### 基于文件的 Source

从本地文件系统或 HDFS 读取文件数据。

```java
// 新 API（推荐）
FileSource<String> source = FileSource.forRecordStreamFormat(
        new TextLineInputFormat(),
        new Path("input/data.txt"))
    .build();
env.fromSource(source, WatermarkStrategy.noWatermarks(), "file-source");

// 旧 API
env.readTextFile("hdfs:///data/logs.txt");
```

文件 Source 支持两种模式：
- **PROCESS_ONCE**：读取一次后结束（有界流）
- **PROCESS_CONTINUOUSLY**：周期性监控文件变化（无界流）

#### 基于 Socket 的 Source

从网络套接字读取实时数据流，常用于本地调试，**并行度为 1**。

```java
// 参数：主机名、端口、分隔符、最大重试次数
env.socketTextStream("localhost", 9999, "\n", 3);
```

可以用 `nc -l 9999` 启动一个本地 TCP 服务来测试。

#### 基于 Kafka 的 Source

生产环境中最常用的数据源，支持并行读取。

```java
KafkaSource<String> kafkaSource = KafkaSource.<String>builder()
    .setBootstrapServers("localhost:9092")
    .setTopics("my-topic")
    .setGroupId("my-group")
    .setStartingOffsets(OffsetsInitializer.latest())
    .setValueOnlyDeserializer(new SimpleStringSchema())
    .build();

env.fromSource(kafkaSource, WatermarkStrategy.noWatermarks(), "kafka-source");
```

#### 基于数据生成器的 Source

>数据生成器的基本用法参考本站示例：https://gitee.com/dexterleslie/demonstration/tree/main/demo-flink/demo-flink-tutorial 中的DataGeneratorSourceTests

用于压力测试和性能基准测试，自动生成指定数量的数据。

```java
DataGeneratorSource<String> generatorSource = new DataGeneratorSource<>(
    index -> "message-" + index,
    1000,                    // 总共生成 1000 条
    RateLimiterStrategy.perSecond(100),  // 每秒生成 100 条
    Types.STRING
);
env.fromSource(generatorSource, WatermarkStrategy.noWatermarks(), "generator-source");
```

---

### 自定义 Source

当内置 Source 无法满足需求时，可以自定义实现。

#### 旧方式（SourceFunction，已不推荐）

```java
public class MySource implements SourceFunction<String> {
    private volatile boolean running = true;

    @Override
    public void run(SourceContext<String> ctx) throws Exception {
        while (running) {
            ctx.collect("data-" + System.currentTimeMillis());
            Thread.sleep(1000);
        }
    }

    @Override
    public void cancel() {
        running = false;
    }
}

// 使用
env.addSource(new MySource());
```

#### 新方式（Source 接口，推荐）

实现 FLIP-27 的 `Source` 接口，支持并行、分片和 Watermark 生成。

---

### 并行度对比

| Source 类型                       | 是否支持并行 | 说明              |
| --------------------------------- | ------------ | ----------------- |
| `fromElements` / `fromCollection` | ❌ 并行度 = 1 | 适合测试          |
| `socketTextStream`                | ❌ 并行度 = 1 | 适合调试          |
| `generateSequence`                | ✅ 支持并行   | 可设置并行度      |
| `FileSource`                      | ✅ 支持并行   | 按文件分片        |
| `KafkaSource`                     | ✅ 支持并行   | 按 Partition 分片 |
| `DataGeneratorSource`             | ✅ 支持并行   | 适合压测          |

---

### 总结

> - 源算子是 Flink 数据流的**入口**，决定了数据从哪里来
> - 从 Flink 1.12 开始，推荐使用 `env.fromSource()` 新 API，支持流批一体
> - 测试场景用集合 Source 或 Socket Source；生产环境用 Kafka Source 或 File Source
> - 内置 Source 不够用时，通过实现 `Source` 接口自定义

## Flink转换算子有哪些呢？

### Flink 转换算子（Transformation Operator）

转换算子负责将一个或多个 DataStream **转换**为新的 DataStream，是 Flink 数据流处理的核心。按功能可以分为以下几大类：

---

### 单流转换算子

#### Map

一对一转换，输入一条数据，输出一条数据。

```java
stream.map(value -> value.toUpperCase());
```

#### FlatMap

一对多转换，输入一条数据，可以输出零条、一条或多条数据。

```java
stream.flatMap((String line, Collector<String> out) -> {
    for (String word : line.split(" ")) {
        out.collect(word);
    }
}).returns(Types.STRING);
```

#### Filter

过滤，保留满足条件的数据。

```java
stream.filter(value -> value > 10);
```

#### KeyBy

逻辑分区，将相同 key 的数据分到同一个分区中（类似 Group By）。

```java
stream.keyBy(value -> value.getCategory());
```

**注意**：以下类型不能作为 keyBy 的 key：
- 数组类型（没有实现 `hashCode`）
- 全局聚合（如 `keyBy(0)` 对所有数据用同一个 key）

#### Reduce

在 KeyedStream 上做增量聚合，每来一条数据就与之前的聚合结果合并。

```java
keyedStream.reduce((a, b) -> {
    a.setCount(a.getCount() + b.getCount());
    return a;
});
```

#### Aggregations（聚合算子）

KeyedStream 上的快捷聚合方法。

```java
keyedStream.sum("price");          // 按字段名求和
keyedStream.min("price");          // 取最小值
keyedStream.max("price");          // 取最大值
keyedStream.minBy("price");        // 取最小值对应的完整记录
keyedStream.maxBy("price");        // 取最大值对应的完整记录
```

| 方法              | 说明                            |
| ----------------- | ------------------------------- |
| `sum`             | 数值累加                        |
| `min` / `max`     | 取最小/最大值                   |
| `minBy` / `maxBy` | 取最小/最大值对应的**完整记录** |

---

### 窗口算子

#### Window

在 KeyedStream 上开窗，将数据按时间或数量分组。

```java
keyedStream
    .window(TumblingEventTimeWindows.of(Time.seconds(10)))  // 滚动窗口
    .apply(new MyWindowFunction());
```

常用窗口类型：

| 窗口类型                   | 说明                             |
| -------------------------- | -------------------------------- |
| `TumblingEventTimeWindows` | 滚动窗口（固定大小，无重叠）     |
| `SlidingEventTimeWindows`  | 滑动窗口（固定大小，有重叠）     |
| `SessionEventTimeWindows`  | 会话窗口（按活动间隔划分）       |
| `CountWindow`              | 计数窗口（按数据条数划分）       |
| `GlobalWindows`            | 全局窗口（所有数据归为一个窗口） |

#### WindowAll

非 Keyed 的全局窗口，**并行度为 1**，慎用。

```java
stream.windowAll(TumblingEventTimeWindows.of(Time.seconds(10)))
      .sum(0);
```

---

### 双流转换算子

#### Union

合并多个同类型的数据流，数据**不去重**。

```java
stream1.union(stream2, stream3);
```

#### Connect

连接两个**不同类型**的数据流，连接后可以用 CoMap/CoFlatMap 分别处理。

```java
ConnectedStreams connected = stream1.connect(stream2);

connected.map(
    new CoMapFunction<String, Integer, String>() {
        @Override
        public String map1(String value) { return "from stream1: " + value; }
        @Override
        public String map2(Integer value) { return "from stream2: " + value; }
    }
);
```

#### CoMap / CoFlatMap

对 Connect 后的双流分别定义不同的处理逻辑。

```java
connected.flatMap(new CoFlatMapFunction<String, Integer, String>() {
    @Override
    public void flatMap1(String value, Collector<String> out) {
        out.collect("String: " + value);
    }
    @Override
    public void flatMap2(Integer value, Collector<String> out) {
        out.collect("Integer: " + value);
    }
});
```

---

### 高级算子

#### ProcessFunction（最强大）

可以访问**事件时间、Watermark、定时器、状态**，是所有转换算子中最灵活的。

```java
stream.process(new ProcessFunction<String, String>() {
    // 注册定时器
    @Override
    public void processElement(String value, Context ctx, Collector<String> out) {
        ctx.timerService().registerEventTimeTimer(ctx.timestamp() + 5000);
        out.collect(value);
    }

    // 定时器触发
    @Override
    public void onTimer(long timestamp, OnTimerContext ctx, Collector<String> out) {
        out.collect("timer fired at: " + timestamp);
    }
});
```

**ProcessFunction 家族：**

| 类                         | 输入流            | 说明                        |
| -------------------------- | ----------------- | --------------------------- |
| `ProcessFunction`          | 单流              | 最基础的 Process 函数       |
| `KeyedProcessFunction`     | KeyedStream       | 支持 Key 级别的定时器和状态 |
| `CoProcessFunction`        | ConnectedStreams  | 双流处理，支持定时器和状态  |
| `ProcessJoinFunction`      | IntervalJoin      | Interval Join 的处理函数    |
| `BroadcastProcessFunction` | BroadcastStream   | 广播流 + 普通流关联         |
| `ProcessWindowFunction`    | WindowedStream    | 窗口处理，可访问窗口元信息  |
| `ProcessAllWindowFunction` | AllWindowedStream | 全局窗口处理                |

#### Side Output（侧输出流）

将不满足条件的数据分流到侧输出流，**不会丢失数据**。

```java
OutputTag<String> lateTag = new OutputTag<String>("late-data"){};

SingleOutputStreamOperator<String> result = stream.process(
    new ProcessFunction<String, String>() {
        @Override
        public void processElement(String value, Context ctx, Collector<String> out) {
            if (value.length() > 5) {
                out.collect(value);           // 主流
            } else {
                ctx.output(lateTag, value);   // 侧输出流
            }
        }
    }
);

// 获取侧输出流
DataStream<String> lateStream = result.getSideOutput(lateTag);
```

#### Iterate（迭代流）

将算子的输出重新反馈到输入端，形成循环，适用于机器学习等迭代算法。

```java
IterativeStream<Integer> iterate = stream.iterate();

DataStream<Integer> iteration = iterate.map(new MapFunction<Integer, Integer>() {
    @Override
    public Integer map(Integer value) { return value - 1; }
});

// 反馈条件：大于 0 的数据继续迭代
iterate.closeWith(iteration.filter(value -> value > 0));

// 输出条件：等于 0 的数据输出到主流
iteration.filter(value -> value == 0);
```

---

### 物理分区算子

控制数据在算子之间的**物理分发方式**：

| 算子          | 说明                             |
| ------------- | -------------------------------- |
| `forward()`   | 一对一转发，不跨线程             |
| `shuffle()`   | 随机分发                         |
| `rebalance()` | 轮询分发（Round-Robin）          |
| `rescale()`   | 局部轮询分发（不跨 TaskManager） |
| `broadcast()` | 广播到所有下游分区               |
| `global()`    | 所有数据发到一个分区（并行度=1） |
| `keyBy()`     | 按 key 哈希分发                  |

```java
stream.rebalance().map(...);
stream.broadcast().map(...);
```

---

### 总结

> - **单流算子**：Map、FlatMap、Filter、KeyBy、Reduce、Aggregation
> - **窗口算子**：Window、WindowAll（滚动/滑动/会话/计数）
> - **双流算子**：Union（同类型合并）、Connect + CoMap（不同类型关联）
> - **高级算子**：ProcessFunction 家族（最灵活）、Side Output（分流）、Iterate（迭代）
> - **物理分区**：forward、shuffle、rebalance、rescale、broadcast、global

## Flink输出算子有哪些呢？

### Flink 输出算子（Sink）

Flink 的输出算子（Sink）负责将处理后的数据流写入外部系统或存储介质。可以分为**内置简单输出**、**连接器 Sink** 和**自定义 Sink** 三大类。

---

### 内置简单输出（调试用）

这些 API 主要用于开发和调试，**不参与 Checkpoint，不保证 Exactly-Once**。

| 方法                       | 说明                         |
| -------------------------- | ---------------------------- |
| `print()`                  | 打印到标准输出（stdout）     |
| `printToErr()`             | 打印到标准错误输出（stderr） |
| `writeAsText()`            | 以文本格式写入文件           |
| `writeAsCsv()`             | 以 CSV 格式写入文件          |
| `writeToSocket()`          | 写入 Socket 端口             |
| `writeUsingOutputFormat()` | 使用自定义 OutputFormat 写入 |

```java
// 打印到控制台
stream.print("DEBUG");

// 写入文本文件
stream.writeAsText("/path/to/output.txt", FileSystem.WriteMode.OVERWRITE);

// 写入 CSV 文件
stream.writeAsCsv("/path/to/output.csv", WriteMode.OVERWRITE, "\n", ",");

// 写入 Socket
stream.writeToSocket("localhost", 9999, new SimpleStringSchema());
```

> ⚠️ 官方文档明确指出：`write*()` 方法主要用于调试，失败时数据可能丢失。生产环境应使用 FileSink 或连接器 Sink。

---

### 连接器 Sink（生产环境推荐）

Flink 官方提供了与多种外部系统集成的连接器，通过 `addSink()` 或 `sinkTo()` 方法调用。

#### Flink 官方支持的 Sink 连接器

| 连接器                       | 类型        | 说明                                 |
| ---------------------------- | ----------- | ------------------------------------ |
| Apache Kafka                 | source/sink | 消息队列                             |
| Apache Cassandra             | sink        | NoSQL 数据库                         |
| Elasticsearch                | sink        | 搜索引擎                             |
| OpenSearch                   | sink        | Elasticsearch 的开源分支             |
| JDBC                         | sink        | 关系型数据库（MySQL、PostgreSQL 等） |
| Amazon Kinesis Data Streams  | source/sink | AWS 流处理                           |
| Amazon Kinesis Data Firehose | sink        | AWS 数据投递                         |
| Amazon DynamoDB              | sink        | AWS NoSQL 数据库                     |
| RabbitMQ                     | source/sink | 消息队列                             |
| Google PubSub                | source/sink | Google 消息服务                      |
| FileSystem                   | source/sink | 文件系统（HDFS、S3、本地文件等）     |
| MongoDB                      | source/sink | 文档数据库                           |
| Apache Pulsar                | source      | 消息平台                             |

#### 第三方扩展（Apache Bahir）

| 连接器          | 类型        |
| --------------- | ----------- |
| Redis           | sink        |
| Apache ActiveMQ | source/sink |
| Apache Flume    | sink        |

---

### 常用连接器 Sink 示例

#### FileSink（文件系统）

生产环境写入文件推荐使用 `FileSink`，支持 Checkpoint，保证 Exactly-Once。

```java
FileSink<String> fileSink = FileSink
    .<String>forRowFormat(new Path("/output"), new SimpleStringEncoder<>("UTF-8"))
    .withOutputFileConfig(
        OutputFileConfig.builder()
            .withPartPrefix("data-")
            .withPartSuffix(".log")
            .build()
    )
    .withBucketAssigner(new DateTimeBucketAssigner<>("yyyy-MM-dd--HH"))
    .withRollingPolicy(
        DefaultRollingPolicy.builder()
            .withRolloverInterval(Duration.ofMinutes(1))
            .withMaxPartSize(new MemorySize(1024 * 1024))
            .build()
    )
    .build();

stream.sinkTo(fileSink);
```

#### KafkaSink

```java
KafkaSink<String> kafkaSink = KafkaSink.<String>builder()
    .setBootstrapServers("localhost:9092")
    .setRecordSerializer(
        KafkaRecordSerializationSchema.builder()
            .setTopic("output-topic")
            .setValueSerializationSchema(new SimpleStringSchema())
            .build()
    )
    .setDeliveryGuarantee(DeliveryGuarantee.EXACTLY_ONCE)
    .setTransactionalIdPrefix("flink-kafka-sink")
    .build();

stream.sinkTo(kafkaSink);
```

#### JDBC Sink

```java
JdbcSink.sink(
    "INSERT INTO orders (id, product, amount) VALUES (?, ?, ?)",
    (statement, order) -> {
        statement.setLong(1, order.id);
        statement.setString(2, order.product);
        statement.setDouble(3, order.amount);
    },
    JdbcExecutionOptions.builder()
        .withBatchSize(1000)
        .withBatchIntervalMs(200)
        .build(),
    new JdbcConnectionOptions.JdbcConnectionOptionsBuilder()
        .withUrl("jdbc:mysql://localhost:3306/mydb")
        .withDriverName("com.mysql.cj.jdbc.Driver")
        .withUsername("root")
        .withPassword("password")
        .build()
);
```

#### Elasticsearch Sink

```java
ElasticsearchSink.Builder<String> esSinkBuilder = new ElasticsearchSink.Builder<>(
    httpHosts,
    new ElasticsearchSinkFunction<String>() {
        public void process(String element, RuntimeContext ctx, RequestIndexer indexer) {
            indexer.add(Requests.indexRequest("index-name").source(element, XContentType.JSON));
        }
    }
);
stream.addSink(esSinkBuilder.build());
```

---

### 自定义 Sink

当官方连接器不满足需求时，可以自定义 Sink。

#### 方式一：实现 SinkFunction（老版本）

```java
stream.addSink(new RichSinkFunction<String>() {
    private Connection connection;

    @Override
    public void open(Configuration parameters) throws Exception {
        connection = DriverManager.getConnection("jdbc:mysql://...");
    }

    @Override
    public void invoke(String value, Context context) throws Exception {
        PreparedStatement ps = connection.prepareStatement("INSERT INTO ...");
        ps.setString(1, value);
        ps.executeUpdate();
    }

    @Override
    public void close() throws Exception {
        if (connection != null) connection.close();
    }
});
```

#### 方式二：实现 Sink + SinkWriter（新版本，推荐）

Flink 1.12+ 推荐使用新的 Sink API（`sinkTo()` 方法）：

```java
public class MyCustomSink implements Sink<String> {
    @Override
    public SinkWriter<String> createWriter(InitContext context) throws IOException {
        return new SinkWriter<String>() {
            @Override
            public void write(String element, Context context) throws IOException {
                // 写入逻辑
            }

            @Override
            public Collection<Committable> prepareCommit() throws IOException {
                // 提交逻辑（用于 Exactly-Once）
                return Collections.emptyList();
            }

            @Override
            public void close() throws Exception {
                // 关闭资源
            }
        };
    }
}

stream.sinkTo(new MyCustomSink());
```

---

### 新旧 API 对比

| 特性       | 旧版 `addSink()`                        | 新版 `sinkTo()`             |
| ---------- | --------------------------------------- | --------------------------- |
| 接口       | `SinkFunction`                          | `Sink` + `SinkWriter`       |
| 语义       | At-Least-Once                           | 支持 Exactly-Once           |
| 两阶段提交 | 需自己实现 `TwoPhaseCommitSinkFunction` | 内置 `prepareCommit()` 支持 |
| 适用版本   | 所有版本                                | Flink 1.12+                 |

---

### 总结

> - **调试用**：`print()`、`writeAsText()`、`writeAsCsv()`、`writeToSocket()` — 简单但不保证一致性
> - **生产用（连接器）**：KafkaSink、FileSink、JDBC Sink、Elasticsearch Sink 等 — 支持 Checkpoint 和 Exactly-Once
> - **自定义**：实现 `SinkFunction`（旧版）或 `Sink` + `SinkWriter`（新版）
> - 生产环境推荐使用 `sinkTo()` + 连接器，配合 Checkpoint 保证 Exactly-Once 语义

## Flink窗口是什么呢？

### Flink 窗口（Window）是什么

Flink 窗口是流处理中将**无限数据流切分为有限"桶"**的机制，使得我们可以在这些有限的数据集合上进行聚合计算（如 SUM、COUNT、AVG 等）。

> 流数据是无限的，永远算不完。窗口就是把这个无限的流"切"成一段一段有限的块，每块独立计算。

---

### 为什么需要窗口

举个例子：

> "统计过去 5 分钟内网站的点击量"

如果没有窗口，数据流是无限的，无法确定"过去 5 分钟"的边界。窗口的作用就是**划定时间/数量范围**，把无限流变成有限集合来处理。

---

### 窗口的核心组成

一个完整的窗口机制由以下几个部分组成：

| 组件                       | 作用                                                 |
| -------------------------- | ---------------------------------------------------- |
| **时间语义**               | 决定"时间"以什么为准（事件时间、处理时间、摄入时间） |
| **窗口分配器（Assigner）** | 决定每条数据属于哪个窗口                             |
| **窗口函数（Function）**   | 决定窗口内数据怎么计算                               |
| **触发器（Trigger）**      | 决定什么时候触发窗口计算                             |
| **驱逐器（Evictor）**      | （可选）在窗口函数执行前后移除某些数据               |

---

### 三种时间语义

| 时间类型                        | 含义                                   | 特点                                       |
| ------------------------------- | -------------------------------------- | ------------------------------------------ |
| **Event Time（事件时间）**      | 数据实际产生的时间（数据自带的时间戳） | 最准确，但需要处理延迟数据，依赖 Watermark |
| **Processing Time（处理时间）** | 数据被 Flink 处理时的系统时间          | 最简单，但结果不确定（受网络延迟等影响）   |
| **Ingestion Time（摄入时间）**  | 数据进入 Flink 数据源的时间            | 介于两者之间，Flink 1.12 后已不推荐使用    |

**Watermark（水位线）** 是事件时间模式下的关键机制，用于衡量事件时间的进展，解决乱序和延迟数据的问题。

---

### 窗口类型

#### 时间窗口

##### 滚动窗口（Tumbling Window）

窗口大小固定，窗口之间**不重叠**，首尾相接。

```
数据流：  ──────────────────────────────────→
窗口：    [----10s----][----10s----][----10s----]
```

```java
// DataStream API
stream
    .keyBy(e -> e.getKey())
    .window(TumblingEventTimeWindows.of(Time.seconds(10)))
    .sum("value");
```

```sql
-- Flink SQL (Window TVF)
SELECT window_start, window_end, SUM(price)
FROM TABLE(
    TUMBLE(TABLE orders, DESCRIPTOR(order_time), INTERVAL '10' SECOND)
)
GROUP BY window_start, window_end;
```

**适用场景**：每 5 分钟统计一次 PV、每小时统计订单总量。

---

##### 滑动窗口（Sliding/Hop Window）

窗口大小固定，窗口之间**有重叠**。有两个参数：窗口大小（Size）和滑动步长（Slide）。

```
数据流：  ──────────────────────────────────→
窗口：    [------20s------]
              [------20s------]
                  [------20s------]
        步长=5s ↑
```

```java
stream
    .keyBy(e -> e.getKey())
    .window(SlidingEventTimeWindows.of(Time.seconds(20), Time.seconds(5)))
    .sum("value");
```

```sql
-- 注意参数顺序：先 Slide（步长），后 Size（大小）
SELECT window_start, window_end, AVG(temperature)
FROM TABLE(
    HOP(TABLE sensors, DESCRIPTOR(ts), INTERVAL '5' SECOND, INTERVAL '20' SECOND)
)
GROUP BY window_start, window_end;
```

**适用场景**：每 5 分钟统计过去 1 小时的热门商品。

---

##### 会话窗口（Session Window）

没有固定大小，由**活动间隙（Gap）**决定窗口边界。如果两条数据之间的间隔超过 Gap，则分为两个窗口。

```
数据流：  ──●──●────●──────●──●──●──────────●──
窗口：    [──●──●────●──]  [──●──●──●──]   [──●──]
                    ↑ Gap                ↑ Gap
```

```java
stream
    .keyBy(e -> e.getUserId())
    .window(EventTimeSessionWindows.withGap(Time.minutes(10)))
    .sum("value");
```

```sql
SELECT window_start, window_end, COUNT(*)
FROM TABLE(
    SESSION(TABLE clicks, DESCRIPTOR(click_time), INTERVAL '10' MINUTE)
)
GROUP BY window_start, window_end;
```

**适用场景**：用户行为分析（用户活跃期间的所有操作归为一个会话）。

---

##### 累积窗口（Cumulate Window）

**Flink 特有**的窗口类型，窗口按步长不断增大，直到达到最大窗口大小。用于"每天 0 点至今的累计值"这类需求。

```
数据流：  ──────────────────────────────────→
窗口：    [---10min---]
          [------20min------]
          [---------30min---------]
          ...直到达到最大窗口（如 1 天）
```

```sql
SELECT window_start, window_end, SUM(amount)
FROM TABLE(
    CUMULATE(TABLE orders, DESCRIPTOR(order_time), 
             INTERVAL '10' MINUTE, INTERVAL '1' DAY)
)
GROUP BY window_start, window_end;
```

**适用场景**：每天的实时累计销售额（每 10 分钟更新一次当天的累计值）。

---

#### 计数窗口

基于**数据条数**而非时间来划分窗口。

```java
// 每 100 条数据触发一次计算，滑动步长 10 条
stream
    .keyBy(e -> e.getKey())
    .countWindow(100, 10)   // 滑动计数窗口
    .sum("value");

// 每 100 条数据触发一次计算（滚动计数窗口）
stream
    .keyBy(e -> e.getKey())
    .countWindow(100)
    .sum("value");
```

---

#### 全局窗口（Global Window）

所有数据归入同一个窗口，**不会自动触发计算**，必须自定义 Trigger 才有意义。

```java
stream
    .keyBy(e -> e.getKey())
    .window(GlobalWindows.create())
    .trigger(CountTrigger.of(100))  // 每 100 条触发一次
    .sum("value");
```

---

### 窗口函数

窗口函数决定了窗口内数据**怎么计算**，分为两大类：

#### 增量聚合函数

每来一条数据就更新结果，**状态小、性能高**。

| 函数                | 说明                             |
| ------------------- | -------------------------------- |
| `ReduceFunction`    | 两条数据合并为一条               |
| `AggregateFunction` | 支持自定义输入、累加器、输出类型 |

```java
stream
    .keyBy(e -> e.getKey())
    .window(TumblingEventTimeWindows.of(Time.minutes(5)))
    .reduce(new ReduceFunction<Event>() {
        @Override
        public Event reduce(Event a, Event b) {
            a.setCount(a.getCount() + b.getCount());
            return a;
        }
    });
```

#### 全量窗口函数

等窗口关闭后，**一次性处理所有数据**，灵活但状态大。

| 函数                    | 说明                             |
| ----------------------- | -------------------------------- |
| `WindowFunction`        | 可以获取窗口元信息（起止时间等） |
| `ProcessWindowFunction` | 最强大，支持状态、定时器、侧输出 |

```java
stream
    .keyBy(e -> e.getKey())
    .window(TumblingEventTimeWindows.of(Time.minutes(5)))
    .process(new ProcessWindowFunction<Event, String, String, TimeWindow>() {
        @Override
        public void process(String key, Context context, 
                           Iterable<Event> elements, Collector<String> out) {
            long count = 0;
            for (Event e : elements) count++;
            out.collect("窗口[" + context.window().getStart() + ", " 
                       + context.window().getEnd() + "] 数据量: " + count);
        }
    });
```

#### 增量 + 全量组合

用增量函数预聚合减少状态，用全量函数获取窗口元信息：

```java
stream
    .keyBy(e -> e.getKey())
    .window(TumblingEventTimeWindows.of(Time.minutes(5)))
    .reduce(new MyReduceFunction(), new MyProcessWindowFunction());
```

---

### 延迟数据处理

在事件时间模式下，数据可能乱序到达。Flink 提供了三种机制处理延迟数据：

#### Watermark（水位线）

衡量事件时间的进展，允许一定程度的乱序。

```java
// 允许 5 秒的乱序
stream.assignTimestampsAndWatermarks(
    WatermarkStrategy.<Event>forBoundedOutOfOrderness(Duration.ofSeconds(5))
        .withTimestampAssigner((event, timestamp) -> event.getTimestamp())
);
```

#### Allowed Lateness（允许延迟）

窗口关闭后，再等待一段时间，延迟数据仍可触发窗口重新计算。

```java
stream
    .keyBy(e -> e.getKey())
    .window(TumblingEventTimeWindows.of(Time.minutes(5)))
    .allowedLateness(Time.seconds(10))  // 窗口关闭后再等 10 秒
    .sum("value");
```

#### Side Output（侧输出流）

超过允许延迟的数据，收集到侧输出流，不会丢失。

```java
OutputTag<Event> lateTag = new OutputTag<Event>("late-data"){};

SingleOutputStreamOperator<Event> result = stream
    .keyBy(e -> e.getKey())
    .window(TumblingEventTimeWindows.of(Time.minutes(5)))
    .sideOutputLateData(lateTag)
    .sum("value");

// 获取延迟数据
DataStream<Event> lateStream = result.getSideOutput(lateTag);
```

---

### 窗口类型总结对比

| 窗口类型 | 是否重叠 | 窗口大小 | 触发条件       | 典型场景           |
| -------- | -------- | -------- | -------------- | ------------------ |
| 滚动窗口 | ❌        | 固定     | 时间/数量到达  | 定时统计报表       |
| 滑动窗口 | ✅        | 固定     | 按步长滑动     | 移动平均、实时监控 |
| 会话窗口 | ❌        | 不固定   | 活动间隙超时   | 用户行为分析       |
| 累积窗口 | ✅        | 递增     | 按步长递增     | 每日累计指标       |
| 计数窗口 | ❌/✅      | 按条数   | 数据条数到达   | 批量处理           |
| 全局窗口 | ❌        | 无限     | 自定义 Trigger | 高级自定义场景     |

---

### 几个重要的注意事项

- **空窗口不输出**：如果某个窗口内没有数据，该窗口不会创建，也不会有输出
- **滑动窗口通过复制实现**：一条数据可能被分配到多个重叠的窗口中，如果窗口大小远大于步长，会导致数据膨胀
- **时间窗口与时间对齐**：1 小时的滚动窗口不会从你启动应用的那一刻开始算，而是与整点对齐（如 12:05 启动，第一个窗口只有 55 分钟，在 13:00 关闭）
- **Window TVF 是推荐方式**：Flink 1.13 开始推荐使用 Window TVF 替代旧的 Group Window，语法更标准、功能更强大

---

### 总结

> - 窗口的本质：把无限流切成有限块，在块上做聚合计算
> - 时间语义：Event Time（最准确）、Processing Time（最简单）
> - 窗口类型：滚动（不重叠）、滑动（重叠）、会话（按活动间隙）、累积（Flink 特有）、计数、全局
> - 窗口函数：增量（Reduce/Aggregate）省状态，全量（ProcessWindowFunction）更灵活
> - 延迟处理：Watermark → Allowed Lateness → Side Output，层层兜底

## Flink水位线是什么呢？

### Flink 水位线（Watermark）是什么

Watermark（水位线）是 Flink 在**事件时间（Event Time）**模式下，用来衡量事件时间进展的一种特殊信号。它的核心作用是：**告诉系统"这个时间点之前的数据应该都到齐了"**，从而解决流处理中数据乱序和延迟到达的问题。

> 简单理解：水位线就像一条"时间分界线"，它说——"我认为时间 X 之前的数据都已经到齐了，可以触发窗口计算了。"

---

### 为什么需要 Watermark

在真实场景中，数据往往不是按时间顺序到达的：

```
事件时间：  12:01  12:02  12:03  12:04  12:05
到达顺序：  12:01  12:03  12:02  12:05  12:04
                 ↑乱序↑        ↑乱序↑
```

如果没有 Watermark，系统无法知道什么时候该关闭窗口、触发计算：
- 关太早 → 丢失迟到数据，结果不准
- 关太晚 → 延迟太高，实时性差

Watermark 就是在这两者之间找到**平衡点**。

---

### Watermark 的工作原理

#### 核心思想

Watermark 本质上是一个**带时间戳的流内信号**，表示："时间戳 ≤ W 的所有事件应该已经到达。"

```
数据流：  ──●──●──●──W(t=5s)──●──●──W(t=8s)──●──→
                          ↑                ↑
                     认为5s前的         认为8s前的
                     数据已到齐         数据已到齐
```

#### 触发窗口计算的规则

当 Watermark 到达某个窗口时：

- **Watermark ≥ 窗口结束时间** → 触发窗口计算（窗口关闭）
- **Watermark < 窗口结束时间** → 窗口保持打开，继续接收数据

```
窗口：[0s ---- 10s)

Watermark = 8s  → 8 < 10，窗口继续等待
Watermark = 10s → 10 ≥ 10，触发计算，窗口关闭
Watermark = 12s → 12 ≥ 10，窗口早已关闭
```

---

### Watermark 的生成策略

Flink 提供了多种 Watermark 生成策略：

#### 周期性生成（Periodic）

按固定时间间隔（默认 200ms）自动生成 Watermark，适合数据量大的场景。

```java
// 策略一：允许固定延迟（最常用）
// 含义：允许数据最大乱序 5 秒，Watermark = 当前最大事件时间 - 5秒
WatermarkStrategy.<Event>forBoundedOutOfOrderness(Duration.ofSeconds(5))
    .withTimestampAssigner((event, timestamp) -> event.getEventTime());

// 策略二：单调递增（数据严格有序时使用）
WatermarkStrategy.<Event>forMonotonousTimestamps()
    .withTimestampAssigner((event, timestamp) -> event.getEventTime());

// 策略三：自定义生成逻辑
WatermarkStrategy.<Event>forGenerator(context -> new WatermarkGenerator<Event>() {
    private long maxTimestamp = 0;

    @Override
    public void onEvent(Event event, long eventTimestamp, WatermarkOutput output) {
        maxTimestamp = Math.max(maxTimestamp, event.getEventTime());
    }

    @Override
    public void onPeriodicEmit(WatermarkOutput output) {
        // 每 200ms 调用一次，发出 Watermark
        output.emitWatermark(new Watermark(maxTimestamp - 5000));
    }
});
```

#### 断点式生成（Punctuated）

根据数据中的特殊标记来生成 Watermark，适合有明确"时间推进信号"的场景。

```java
WatermarkStrategy.<Event>forGenerator(context -> new WatermarkGenerator<Event>() {
    @Override
    public void onEvent(Event event, long eventTimestamp, WatermarkOutput output) {
        // 当收到特殊标记事件时，发出 Watermark
        if (event.isWatermarkSignal()) {
            output.emitWatermark(new Watermark(event.getEventTime()));
        }
    }

    @Override
    public void onPeriodicEmit(WatermarkOutput output) {
        // 不使用周期性生成
    }
});
```

---

### Watermark 在多并行度下的传播

Flink 是分布式系统，一个算子可能有多个并行子任务。Watermark 的传播规则是：

> **算子的 Watermark = 所有输入通道中 Watermark 的最小值**

```
子任务1 的 Watermark = 10s ─┐
                              ├→ 算子 Watermark = min(10, 8, 12) = 8s
子任务2 的 Watermark = 8s  ──┤
                              │
子任务3 的 Watermark = 12s ──┘
```

这意味着：**最慢的那个分区决定了整体进度**。如果某个分区长时间没有数据，Watermark 会停滞，导致下游窗口无法触发。

---

### Watermark 停滞问题及解决方案

#### 问题场景

某个 Key 的分区长时间没有新数据 → 该分区的 Watermark 不推进 → 下游窗口的 Watermark 被拖住 → 窗口永远不触发。

#### 解决方案一：Idle Source（空闲源检测）

```java
WatermarkStrategy.<Event>forBoundedOutOfOrderness(Duration.ofSeconds(5))
    .withTimestampAssigner((event, timestamp) -> event.getEventTime())
    .withIdleness(Duration.ofSeconds(10));  // 10秒无数据则标记为空闲
```

当某个分区超过 10 秒没有数据时，Flink 会将其标记为空闲（Idle），在计算 Watermark 时忽略它。

#### 解决方案二：定期发送 Watermark

在 Source 端定时发送 Watermark，即使没有数据也推进时间。

---

### Watermark 与窗口、延迟数据的关系

Flink 处理延迟数据有**三道防线**，Watermark 是第一道：

```
数据到达 → ① Watermark 判断 → ② Allowed Lateness → ③ Side Output
                ↑                    ↑                     ↑
           正常触发窗口          窗口延迟关闭            收集到侧输出流
```

| 防线                 | 机制                   | 作用                         |
| -------------------- | ---------------------- | ---------------------------- |
| **Watermark**        | 允许一定乱序           | 决定窗口何时"正常关闭"       |
| **Allowed Lateness** | 窗口关闭后再等一段时间 | 延迟数据仍可触发窗口重新计算 |
| **Side Output**      | 超过 Lateness 的数据   | 收集到侧输出流，不丢失       |

#### 完整示例

```java
OutputTag<Event> lateDataTag = new OutputTag<Event>("late-data"){};

SingleOutputStreamOperator<Result> result = stream
    // 分配时间戳和 Watermark（允许 5 秒乱序）
    .assignTimestampsAndWatermarks(
        WatermarkStrategy.<Event>forBoundedOutOfOrderness(Duration.ofSeconds(5))
            .withTimestampAssigner((event, ts) -> event.getEventTime())
    )
    .keyBy(e -> e.getKey())
    .window(TumblingEventTimeWindows.of(Time.minutes(1)))
    .allowedLateness(Time.seconds(10))       // 窗口关闭后再等 10 秒
    .sideOutputLateData(lateDataTag)         // 超过 10 秒的数据收集到侧输出
    .sum("value");

// 获取延迟数据
DataStream<Event> lateStream = result.getSideOutput(lateDataTag);
lateStream.print("LATE DATA:");
```

以上示例的时间线：

```
窗口：[0s ---- 60s)

Watermark ≥ 60s        → 窗口正常触发计算
Watermark ≥ 70s        → 窗口最终关闭（60s + 10s 允许延迟）
Watermark ≥ 70s 后到达  → 数据进入侧输出流（不会丢失）
```

---

### Watermark 的可视化理解

```
事件时间轴：
0s        10s       20s       30s       40s       50s       60s
|---------|---------|---------|---------|---------|---------|
●  ●  ●     ●  ●        ●     ●  ●        ●  ●     ●

Watermark（允许5秒乱序）：
                        W=5s              W=15s             W=25s
                         |                  |                 |
                         ▼                  ▼                 ▼

窗口 [0s--10s)：W=5s 时未触发 → W=15s 时触发（15≥10）→ 输出结果
窗口 [10s--20s)：W=15s 时未触发 → W=25s 时触发（25≥20）→ 输出结果
```

---

### 设置 Watermark 的要点

| 要点                         | 说明                                                         |
| ---------------------------- | ------------------------------------------------------------ |
| **乱序容忍度**               | 设太小 → 丢数据；设太大 → 延迟高、状态膨胀                   |
| **数据源特性**               | 如果数据源本身有序，用 `forMonotonousTimestamps()` 即可      |
| **空闲源**                   | 多并行度下一定要配置 `withIdleness()`，否则 Watermark 可能停滞 |
| **与 Allowed Lateness 配合** | Watermark 控制"正常关闭"，Lateness 控制"最终关闭"，两者配合使用 |
| **监控**                     | 生产环境应监控 Watermark 延迟指标，及时发现异常              |

---

### 总结

> - Watermark 是 Flink 事件时间模式下的核心机制，本质上是一个"时间进度信号"
> - 它告诉系统："这个时间之前的数据应该都到齐了"，从而决定窗口何时触发计算
> - 生成方式：周期性（Periodic，按时间间隔）和断点式（Punctuated，按特殊标记）
> - 多并行度下取所有通道 Watermark 的**最小值**，最慢的分区决定整体进度
> - 与 Allowed Lateness、Side Output 配合，构成完整的延迟数据处理方案
> - 实际使用中要合理设置乱序容忍度，并配置空闲源检测，避免 Watermark 停滞

## Flink SQL是什么呢？

>参考本站示例FlinkSQLDataGenTests使用Flink SQL实现DataGen：https://gitee.com/dexterleslie/demonstration/tree/main/demo-flink/demo-flink-table-api-n-sql

### Flink SQL 是什么

Flink SQL 是 Apache Flink 提供的**基于标准 SQL 的流批一体计算引擎**，允许用户使用熟悉的 SQL 语法来开发实时数据处理任务，而无需编写复杂的 Java/Scala/Python 代码。

> 简单理解：Flink SQL 让你用写 SQL 的方式来做实时流处理，把"写代码"变成"写语句"。

---

### 核心定位

Flink 提供了分层的 API 体系，从底层到高层依次为：

```
Stateful Stream Processing（底层状态处理）
        ↑
DataStream API / DataSet API（编程式 API）
        ↑
Table API（表式 API）
        ↑
Flink SQL（最高层抽象）  ← 你在这里
```

Flink SQL 是**最高层级的抽象**，底层基于 Apache Calcite 实现查询优化，最终会被翻译成 DataStream API 执行。

---

### Flink SQL 与普通 SQL 的区别

| 维度     | 普通 SQL（MySQL/Oracle） | Flink SQL（实时流）                          |
| -------- | ------------------------ | -------------------------------------------- |
| 数据模型 | 静态有限表               | 无限数据流（动态表）                         |
| 执行方式 | 执行一次，返回固定结果   | 持续运行，实时更新结果                       |
| 核心能力 | 查询、统计、关联         | 实时开窗、Watermark 容错、状态迭代、实时聚合 |
| 专属语法 | 无                       | WATERMARK、窗口 TVF、流式建表                |
| 结果     | 一次性快照               | 持续更新的变更日志（Changelog）              |



---

### 核心概念

#### 动态表（Dynamic Table）

Flink SQL 的核心概念。与传统数据库的静态表不同，动态表是**持续变化的**——随着新数据不断流入，表的内容会实时更新。

```
数据流（无限）  ←→  动态表（持续更新）
```

#### 连续查询（Continuous Query）

在动态表上执行的查询**永远不会终止**，它会持续消费新到达的数据，并不断更新输出结果。

```
输入动态表 → 连续查询（永不停止） → 输出动态表
```

#### 流表二元性（Stream-Table Duality）

流和表可以互相转换：
- **流 → 表**：将数据流视为一张持续更新的表
- **表 → 流**：将表的变更（Changelog）视为一条数据流

变更日志有四种类型：

| 类型                  | 含义     | 示例           |
| --------------------- | -------- | -------------- |
| `+I`（Insert）        | 插入新行 | 新订单         |
| `-U`（Update Before） | 撤回旧值 | 股价变化前的值 |
| `+U`（Update After）  | 写入新值 | 股价变化后的值 |
| `-D`（Delete）        | 删除行   | 取消的订单     |

---

### 任务开发三步走

一个 Flink SQL 实时任务的基本结构非常固定：

```sql
-- 第一步：定义源表（数据从哪来）
CREATE TABLE source_table (
    user_id STRING,
    page_id STRING,
    click_time TIMESTAMP(3),
    WATERMARK FOR click_time AS click_time - INTERVAL '5' SECOND
) WITH (
    'connector' = 'kafka',
    'topic' = 'clicks',
    'properties.bootstrap.servers' = 'kafka:9092',
    'format' = 'json'
);

-- 第二步：定义结果表（数据写到哪去）
CREATE TABLE sink_table (
    page_id STRING,
    view_count BIGINT,
    PRIMARY KEY (page_id) NOT ENFORCED
) WITH (
    'connector' = 'jdbc',
    'url' = 'jdbc:mysql://localhost:3306/mydb',
    'table-name' = 'page_views'
);

-- 第三步：实时计算并写入
INSERT INTO sink_table
SELECT
    page_id,
    COUNT(*) AS view_count
FROM source_table
GROUP BY page_id;
```

---

### 支持的窗口类型

Flink SQL 通过 **Window TVF（表值函数）** 支持窗口操作：

```sql
-- 滚动窗口：每 10 秒统计一次
SELECT window_start, window_end, SUM(amount)
FROM TABLE(
    TUMBLE(TABLE orders, DESCRIPTOR(order_time), INTERVAL '10' SECOND)
)
GROUP BY window_start, window_end;

-- 滑动窗口：每 5 秒统计过去 20 秒的数据
SELECT window_start, window_end, AVG(temperature)
FROM TABLE(
    HOP(TABLE sensors, DESCRIPTOR(ts), INTERVAL '5' SECOND, INTERVAL '20' SECOND)
)
GROUP BY window_start, window_end;

-- 会话窗口：10 分钟无活动则断开
SELECT window_start, window_end, COUNT(*)
FROM TABLE(
    SESSION(TABLE clicks, DESCRIPTOR(click_time), INTERVAL '10' MINUTE)
)
GROUP BY window_start, window_end;

-- 累积窗口：每天累计，每 10 分钟更新
SELECT window_start, window_end, SUM(amount)
FROM TABLE(
    CUMULATE(TABLE orders, DESCRIPTOR(order_time), 
             INTERVAL '10' MINUTE, INTERVAL '1' DAY)
)
GROUP BY window_start, window_end;
```

---

### Flink SQL 与 DataStream API 对比

| 特性     | Flink SQL                  | DataStream API                |
| -------- | -------------------------- | ----------------------------- |
| 编程方式 | 声明式（描述"做什么"）     | 命令式（描述"怎么做"）        |
| 代码量   | 极少（减少 70%-90%）       | 较多                          |
| 学习曲线 | 平缓（会 SQL 即可）        | 陡峭（需理解 Flink 核心概念） |
| 优化     | 自动优化（Calcite 优化器） | 手动优化                      |
| 表达能力 | 覆盖常见场景，复杂逻辑受限 | 极高，可实现任意逻辑          |
| 调试     | 相对困难（黑盒）           | 容易（断点、日志）            |
| 适用场景 | ETL、实时报表、数据入湖    | CEP、复杂状态管理、AI 推理    |



---

### 适用场景

- **实时 ETL**：数据清洗、格式转换、字段过滤
- **实时报表/仪表盘**：聚合计算生成即时指标（如 PV/UV、订单总额）
- **数据入湖/入仓**：实时写入 Hudi、Iceberg、Elasticsearch、MySQL 等
- **实时数据同步**：跨系统的数据复制和转换
- **简单实时分析**：窗口聚合、去重、关联等

---

### 扩展能力

#### UDF（用户自定义函数）

当 SQL 内置函数不够用时，可以用 Java 或 Python 编写自定义函数：

| 类型                              | 说明                         | 示例                     |
| --------------------------------- | ---------------------------- | ------------------------ |
| **Scalar Function**               | 一进一出                     | 字符串格式化、加密       |
| **Table Function**                | 一进多出                     | JSON 展开、字符串拆分    |
| **Aggregate Function**            | 多进一出                     | 自定义聚合逻辑           |
| **Table Aggregate Function**      | 多进多出                     | TopN 等                  |
| **Process Table Function（PTF）** | 处理整个表，支持状态和定时器 | 自定义窗口（Flink 2.1+） |

#### 丰富的连接器生态

Flink SQL 支持通过 `WITH` 子句连接各种外部系统：

| 类别     | 连接器                              |
| -------- | ----------------------------------- |
| 消息队列 | Kafka、Pulsar、Kinesis              |
| 文件系统 | HDFS、S3、OSS、本地文件             |
| 数据库   | MySQL、PostgreSQL、HBase、Cassandra |
| 数据湖   | Hudi、Iceberg、Delta Lake、Paimon   |
| 搜索引擎 | Elasticsearch                       |
| 缓存     | Redis                               |

---

### 执行模式

Flink SQL 支持两种执行模式，体现了**流批一体**的特性：

| 模式                    | 说明                     | 场景                   |
| ----------------------- | ------------------------ | ---------------------- |
| **流模式（Streaming）** | 持续运行，处理无限数据流 | 实时计算、实时监控     |
| **批模式（Batch）**     | 运行一次，处理有限数据集 | 离线分析、历史数据回刷 |

```sql
-- 设置为流模式
SET 'execution.runtime-mode' = 'streaming';

-- 设置为批模式
SET 'execution.runtime-mode' = 'batch';
```

同一套 SQL 代码，只需切换模式，就能在流和批之间无缝切换。

---

### 总结

> - Flink SQL 是 Flink 最高层的抽象，用标准 SQL 实现流批一体的实时计算
> - 核心概念：动态表、连续查询、流表二元性、变更日志（+I/-U/+U/-D）
> - 开发三步走：建源表 → 建结果表 → INSERT INTO ... SELECT
> - 支持窗口 TVF（TUMBLE/HOP/SESSION/CUMULATE）、Watermark、多种 Join
> - 可通过 UDF 扩展，支持 Java/Python 自定义函数
> - 与 DataStream API 互补：SQL 做常规处理，API 做复杂逻辑，可混合使用
> - 流批一体：同一套 SQL，切换模式即可在实时流和离线批之间切换

## Flink Table API是什么呢？

>参考本站示例FlinkTableApiDataGenTests使用Table API实现DataGen：https://gitee.com/dexterleslie/demonstration/tree/main/demo-flink/demo-flink-table-api-n-sql

### Flink Table API 是什么

Flink Table API 是 Flink 提供的一套**面向表的关系式编程 API**，内嵌在 Java、Scala 和 Python 语言中，允许开发者以**链式方法调用**（而非字符串 SQL）的方式来处理流数据和批数据。

> 简单理解：Table API 是 Flink SQL 的"代码版"——用编程的方式写查询，语义和 SQL 完全对齐，但写起来更像代码而不是字符串。

---

### 在 Flink API 体系中的位置

```
Flink SQL（字符串 SQL 语句）
    ↑ 共用同一套引擎
Table API（链式方法调用）  ← 你在这里
    ↑
DataStream API（底层流处理）
    ↑
Stateful Stream Processing（最底层）
```

Table API 和 Flink SQL 共用同一套底层引擎（基于 Apache Calcite），两者可以**无缝混用**。

---

### Table API 与 Flink SQL 的关系

| 维度     | Table API                  | Flink SQL       |
| -------- | -------------------------- | --------------- |
| 表达方式 | Java/Scala/Python 链式调用 | SQL 字符串      |
| 语法检查 | 编译期检查（IDE 提示）     | 运行期检查      |
| 代码提示 | 有（IDE 自动补全）         | 无              |
| 可读性   | 复杂逻辑更清晰             | 简单逻辑更直观  |
| 底层引擎 | 相同（Calcite）            | 相同（Calcite） |
| 执行计划 | 相同                       | 相同            |

两者是**互补关系**，不是替代关系。官方定义：Table API 是 Flink SQL 的**超集**。

---

### 核心概念

#### 动态表（Dynamic Table）

与传统数据库的静态表不同，动态表是**持续变化的**，随着新数据流入不断更新。

#### 流表二元性（Stream-Table Duality）

流和表可以互相转换：
- **流 → 表**：将数据流视为一张持续更新的表
- **表 → 流**：将表的变更视为一条数据流

#### 连续查询（Continuous Query）

在动态表上执行的查询**永不停止**，持续消费新数据并更新输出结果。

---

### 开发五步走

#### 第一步：创建表环境

```java
// 创建流执行环境
StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
// 创建表环境（Table API 的入口）
StreamTableEnvironment tableEnv = StreamTableEnvironment.create(env);
```

#### 第二步：创建表

```java
// 方式一：从 DataStream 转换
DataStream<SensorReading> dataStream = env.fromCollection(...);
tableEnv.createTemporaryView("sensorTable", dataStream);

// 方式二：通过 DDL 连接外部系统
tableEnv.executeSql(
    "CREATE TABLE orders (" +
    "  user_id STRING," +
    "  amount DOUBLE," +
    "  order_time TIMESTAMP(3)," +
    "  WATERMARK FOR order_time AS order_time - INTERVAL '5' SECOND" +
    ") WITH (" +
    "  'connector' = 'kafka'," +
    "  'topic' = 'orders'," +
    "  'properties.bootstrap.servers' = 'localhost:9092'," +
    "  'format' = 'json'" +
    ")"
);
```

#### 第三步：查询表（Table API 方式）

```java
Table orders = tableEnv.from("orders");

// 过滤 + 选择字段
Table result = orders
    .filter($("amount").isGreater(100))
    .select($("user_id"), $("amount"));

// 分组聚合
Table aggResult = orders
    .groupBy($("user_id"))
    .select($("user_id"), $("amount").sum().as("total_amount"));
```

同样的逻辑，用 SQL 写：

```java
Table result = tableEnv.sqlQuery(
    "SELECT user_id, amount FROM orders WHERE amount > 100"
);

Table aggResult = tableEnv.sqlQuery(
    "SELECT user_id, SUM(amount) AS total_amount FROM orders GROUP BY user_id"
);
```

可以看到：**语义完全一致，只是表达方式不同**。

#### 第四步：输出表

```java
// 注册输出表
tableEnv.executeSql(
    "CREATE TABLE sink_table (" +
    "  user_id STRING," +
    "  total_amount DOUBLE," +
    "  PRIMARY KEY (user_id) NOT ENFORCED" +
    ") WITH (" +
    "  'connector' = 'jdbc'," +
    "  'url' = 'jdbc:mysql://localhost:3306/mydb'," +
    "  'table-name' = 'user_totals'" +
    ")"
);

// 将结果写入输出表
result.executeInsert("sink_table");
```

#### 第五步：流表互转

```java
// 流 → 表
Table table = tableEnv.fromDataStream(dataStream, $("id"), $("name"), $("ts").as("order_time"));

// 表 → 流（追加流）
DataStream<Row> appendStream = tableEnv.toDataStream(resultTable);

// 表 → 流（变更日志流，包含 +I/-U/+U/-D）
DataStream<Row> changelogStream = tableEnv.toChangelogStream(resultTable);
```

---

### 窗口操作

Table API 同样支持窗口，有两种写法：

#### 旧版写法（链式调用）

```java
Table result = orders
    .window(Tumble.over(lit(1).hours()).on($("order_time")).as("w"))
    .groupBy($("w"), $("user_id"))
    .select(
        $("user_id"),
        $("w").end().as("window_end"),
        $("amount").sum().as("total")
    );
```

#### 新版写法（Window TVF，推荐）

```java
Table result = tableEnv.sqlQuery(
    "SELECT user_id, window_end, SUM(amount) AS total " +
    "FROM TABLE(TUMBLE(TABLE orders, DESCRIPTOR(order_time), INTERVAL '1' HOUR)) " +
    "GROUP BY window_start, window_end, user_id"
);
```

---

### Table API 常用操作一览

| 操作     | Table API 写法                                    | 对应 SQL                      |
| -------- | ------------------------------------------------- | ----------------------------- |
| 选择字段 | `.select($("a"), $("b"))`                         | `SELECT a, b`                 |
| 过滤     | `.filter($("a").isGreater(10))`                   | `WHERE a > 10`                |
| 分组聚合 | `.groupBy($("a")).select($("a"), $("b").sum())`   | `SELECT a, SUM(b) GROUP BY a` |
| 去重     | `.distinct()`                                     | `SELECT DISTINCT`             |
| 排序     | `.orderBy($("a").desc)`                           | `ORDER BY a DESC`             |
| 关联     | `.join(otherTable).where($("a").isEqual($("c")))` | `JOIN ... ON a = c`           |
| 窗口     | `.window(Tumble.over(...))`                       | `TUMBLE(...)`                 |

---

### 适用场景

| 场景                           | 推荐用 Table API 还是 SQL         |
| ------------------------------ | --------------------------------- |
| 简单 ETL、报表                 | 两者皆可，SQL 更简洁              |
| 复杂业务逻辑、条件分支多       | Table API（编译期检查、IDE 提示） |
| 需要动态拼接查询条件           | Table API（代码灵活性高）         |
| 数据分析师/运维人员使用        | SQL（门槛低）                     |
| 需要与 DataStream API 深度交互 | Table API（转换更方便）           |
| 混合使用                       | Table API + SQL 混写（完全支持）  |

---

### 总结

> - Table API 是 Flink SQL 的"代码版"，用链式方法调用替代 SQL 字符串，语义完全对齐
> - 与 Flink SQL 共用同一套底层引擎（Calcite），执行计划相同，可无缝混用
> - 核心优势：编译期类型检查、IDE 自动补全、动态拼接查询、与 DataStream API 深度交互
> - 开发流程：创建表环境 → 创建表 → 查询表 → 输出表 → 流表互转
> - 支持窗口、聚合、Join、Watermark 等全部关系型操作
> - 与 DataStream API 互补：Table API/SQL 做关系型处理，DataStream API 做复杂状态处理

## Flink Connector是什么呢？

### Flink Connector 是什么

Flink Connector（连接器）是 Flink 与**外部数据系统**之间进行数据交互的**桥梁组件**，负责将外部系统的数据读入 Flink 进行处理，再将处理结果写回外部系统。

> 简单理解：Connector 就是 Flink 的"数据进出口"——没有它，Flink 就无法与 Kafka、MySQL、Elasticsearch 等外部系统通信。

---

### 核心定位

```
外部系统（Kafka/MySQL/HDFS/ES...）
        ↕  ← Connector 负责这里
    ┌───────────────────────┐
    │     Flink 计算引擎     │
    │  Source → 算子 → Sink  │
    └───────────────────────┘
        ↕  ← Connector 负责这里
外部系统（Kafka/MySQL/HDFS/ES...）
```

Connector 在 Flink 中分为两大类：

| 类型                 | 方向         | 职责                                  | 对应接口                             |
| -------------------- | ------------ | ------------------------------------- | ------------------------------------ |
| **Source（数据源）** | 外部 → Flink | 读取外部数据，转换为 Flink 内部数据流 | `SourceFunction` / 新版 `Source` API |
| **Sink（接收器）**   | Flink → 外部 | 将处理结果写入外部系统                | `SinkFunction` / 新版 `Sink` API     |

---

### 连接器的核心职责

Connector 不仅仅是简单的"读"和"写"，它还承担以下关键职责：

- **数据解析与序列化**：将外部系统的二进制/列式数据解析为 Flink 可处理的格式，写入时再序列化回外部格式
- **Watermark 生成**：Source 端负责从数据中提取时间戳并生成 Watermark，驱动事件时间语义
- **Checkpoint 对齐**：与 Flink 的 Checkpoint 机制联动，保存读取/写入位置，实现故障恢复
- **负载均衡**：根据并行度合理分配数据分区，实现并行读取和写入

---

### 常见连接器生态

#### 消息队列类

| 连接器       | 用途                   | 特点                           |
| ------------ | ---------------------- | ------------------------------ |
| **Kafka**    | 实时数据采集、事件驱动 | 高吞吐、Exactly-Once、事务写入 |
| **Pulsar**   | 大规模分布式消息流     | 多租户、分层存储               |
| **Kinesis**  | AWS 云原生流数据       | 与 AWS 生态深度集成            |
| **RabbitMQ** | 轻量级消息分发         | 简单易用，适合中小规模         |

#### 数据库类

| 连接器        | 用途                       | 特点                          |
| ------------- | -------------------------- | ----------------------------- |
| **JDBC**      | MySQL/PostgreSQL/Oracle 等 | 批量写入、幂等性              |
| **Flink CDC** | 实时捕获数据库变更         | 低延迟、无侵入、支持全量+增量 |
| **Redis**     | 实时缓存、去重、计数       | 低延迟读写                    |
| **HBase**     | 大规模 NoSQL 存储          | 随机读写、高可用              |

#### 数据湖/文件系统类

| 连接器                      | 用途               | 特点                   |
| --------------------------- | ------------------ | ---------------------- |
| **HDFS**                    | 数据入湖、日志归档 | 批流一体               |
| **Iceberg/Hudi/Delta Lake** | 数据湖存储         | ACID 事务、Schema 演化 |
| **Paimon**                  | Flink 原生数据湖   | 与 Flink 深度集成      |
| **S3/OSS**                  | 云对象存储         | 弹性扩展               |

#### 分析/检索类

| 连接器            | 用途               | 特点               |
| ----------------- | ------------------ | ------------------ |
| **Elasticsearch** | 实时检索、日志分析 | 全文搜索、聚合分析 |
| **ClickHouse**    | 高性能 OLAP        | 列式存储、极速查询 |
| **Hive**          | 数据仓库           | 流式写入、兼容 SQL |

---

### Source API 演进

Flink 的 Source 接口经历了重要演进：

#### 旧版 API（Flink 1.10 之前）

```java
// 基于 SourceFunction
FlinkKafkaConsumer<String> consumer = new FlinkKafkaConsumer<>(
    "topic",
    new SimpleStringSchema(),
    properties
);
DataStream<String> stream = env.addSource(consumer);
```

**问题**：流和批的 Source 实现不一致，扩展性差。

#### 新版 API（Flink 1.12+，推荐）

```java
// 基于新的 Source 接口（统一流批）
KafkaSource<String> source = KafkaSource.<String>builder()
    .setBootstrapServers("localhost:9092")
    .setTopics("input-topic")
    .setGroupId("my-group")
    .setStartingOffsets(OffsetsInitializer.earliest())
    .setValueOnlyDeserializer(new SimpleStringSchema())
    .build();

DataStream<String> stream = env.fromSource(source, WatermarkStrategy.noWatermarks(), "Kafka Source");
```

**优势**：
- 流批一体（同一套 API 支持流和批）
- 更好的并行度管理（基于 Split 分片）
- 更清晰的职责划分（Source → SplitReader → SourceReader）

---

### Sink API 演进

#### 旧版 API

```java
// 基于 SinkFunction
stream.addSink(new RichSinkFunction<String>() {
    @Override
    public void invoke(String value, Context context) {
        // 写入逻辑
    }
});
```

#### 新版 API（Flink 1.14+，推荐）

```java
// 基于新的 Sink 接口（支持 Exactly-Once）
JdbcSink.sink(
    "INSERT INTO users (id, name) VALUES (?, ?)",
    (statement, row) -> {
        statement.setLong(1, row.getFieldAs(0));
        statement.setString(2, row.getFieldAs(1));
    },
    JdbcExecutionOptions.builder()
        .withBatchSize(1000)
        .withBatchIntervalMs(200)
        .build(),
    connectionOptions
);
```

**优势**：
- 支持两阶段提交（2PC），实现 Exactly-Once
- 批量写入优化
- 更灵活的生命周期管理

---

### 关键特性

#### Exactly-Once 语义

通过 Flink Checkpoint + 两阶段提交（2PC）协议，保证数据端到端精确一次处理：

```
Checkpoint 触发 → 预提交（Prepare） → 提交（Commit）
                     ↓ 失败时
                  回滚（Abort） → 从上次 Checkpoint 恢复
```

#### 容错与断点续传

- Source：Checkpoint 时保存读取位置（如 Kafka Offset、Binlog Position）
- Sink：Checkpoint 时提交已写入的数据，故障后从最近一致点恢复

#### 批流一体

部分连接器（如 Kafka、Hive、HDFS、Iceberg）支持统一的批处理和流处理接口，只需切换执行模式即可。

---

### Flink SQL 中的 Connector 使用

在 Flink SQL 中，Connector 通过 `CREATE TABLE` 的 `WITH` 子句声明：

```sql
-- Source：从 Kafka 读取
CREATE TABLE kafka_source (
    user_id STRING,
    amount DOUBLE,
    order_time TIMESTAMP(3),
    WATERMARK FOR order_time AS order_time - INTERVAL '5' SECOND
) WITH (
    'connector' = 'kafka',
    'topic' = 'orders',
    'properties.bootstrap.servers' = 'localhost:9092',
    'format' = 'json'
);

-- Sink：写入 MySQL
CREATE TABLE mysql_sink (
    user_id STRING,
    total_amount DOUBLE,
    PRIMARY KEY (user_id) NOT ENFORCED
) WITH (
    'connector' = 'jdbc',
    'url' = 'jdbc:mysql://localhost:3306/mydb',
    'table-name' = 'user_totals',
    'username' = 'root',
    'password' = '123456'
);

-- 计算并写入
INSERT INTO mysql_sink
SELECT user_id, SUM(amount) AS total_amount
FROM kafka_source
GROUP BY user_id;
```

---

### 自定义 Connector 开发

当官方连接器无法满足需求时，可以自行开发：

| 步骤        | 说明                                                  |
| ----------- | ----------------------------------------------------- |
| 实现 Source | 继承 `SourceFunction` 或实现新版 `Source` 接口        |
| 实现 Sink   | 继承 `RichSinkFunction` 或实现新版 `Sink` 接口        |
| 处理容错    | 实现 `CheckpointedFunction`，在 Checkpoint 时保存状态 |
| 支持并行    | Source 实现分片逻辑，Sink 支持并行写入                |
| 注册使用    | 通过 SPI 机制注册，或直接在代码中引用                 |

---

### 总结

> - Flink Connector 是 Flink 与外部系统交互的桥梁，分为 Source（读入）和 Sink（写出）
> - 支持丰富的生态系统：Kafka、JDBC、Flink CDC、Elasticsearch、Iceberg、HDFS 等
> - 核心职责：数据解析/序列化、Watermark 生成、Checkpoint 对齐、负载均衡
> - API 经历了从旧版（SourceFunction/SinkFunction）到新版（统一流批）的演进
> - 支持 Exactly-Once 语义，通过 Checkpoint + 两阶段提交保证端到端一致性
> - 在 Flink SQL 中通过 `WITH ('connector' = 'xxx')` 声明使用
> - 支持自定义开发，满足特殊数据源的接入需求

## Flink源表和输出表是什么呢？

>参考本站示例FlinkSQLSourceNSinkTableTests演示源表和输出表：https://gitee.com/dexterleslie/demonstration/tree/main/demo-flink/demo-flink-table-api-n-sql

### Flink 源表和输出表是什么

在 Flink SQL / Table API 中，**源表（Source Table）**和**输出表（Sink Table）**是实时计算任务的**起点和终点**，分别定义了"数据从哪来"和"数据写到哪去"。

> 简单理解：源表是 Flink 的"入口"，输出表是 Flink 的"出口"，中间是计算逻辑。

---

### 整体关系

```
源表（Source Table）          输出表（Sink Table）
    ↓                            ↑
┌─────────────────────────────────────┐
│         Flink 计算逻辑               │
│   SELECT / FILTER / GROUP BY / ...  │
└─────────────────────────────────────┘
```

---

### 源表（Source Table）

#### 定义

源表是 Flink 读取数据的**入口表**，它通过 Connector 与外部数据系统建立连接，将外部数据以"表"的形式暴露给 Flink 引擎进行查询和计算。

#### 本质

源表在底层对应的是一个**动态表（Dynamic Table）**——它不是一次性加载的静态数据，而是随着外部系统不断产生新数据，表的内容会**持续更新**。

#### 创建方式

```sql
CREATE TABLE kafka_orders (           -- 表名
    order_id STRING,                  -- 字段定义
    user_id STRING,
    amount DOUBLE,
    order_time TIMESTAMP(3),
    WATERMARK FOR order_time          -- 事件时间水位线
        AS order_time - INTERVAL '5' SECOND
) WITH (
    'connector' = 'kafka',            -- 指定连接器类型
    'topic' = 'orders',               -- Kafka 主题
    'properties.bootstrap.servers' = 'localhost:9092',
    'properties.group.id' = 'flink-group',
    'scan.startup.mode' = 'latest-offset',
    'format' = 'json'                 -- 数据格式
);
```

#### 核心要素

| 要素          | 说明                           | 示例                                           |
| ------------- | ------------------------------ | ---------------------------------------------- |
| **字段定义**  | 描述数据的列名和数据类型       | `user_id STRING`, `amount DOUBLE`              |
| **Watermark** | 定义事件时间语义，处理乱序数据 | `WATERMARK FOR ts AS ts - INTERVAL '5' SECOND` |
| **Connector** | 指定数据源类型                 | `'connector' = 'kafka'`                        |
| **连接参数**  | 连接外部系统所需的配置         | 地址、端口、认证信息等                         |
| **数据格式**  | 数据的序列化/反序列化方式      | `'format' = 'json'`、`'format' = 'csv'`        |

#### 常见源表类型

| 场景       | Connector                      | 数据特点                     |
| ---------- | ------------------------------ | ---------------------------- |
| 消息队列   | Kafka、Pulsar                  | 无限流，持续产生             |
| 数据库变更 | Flink CDC（MySQL、PostgreSQL） | 捕获 INSERT/UPDATE/DELETE    |
| 文件系统   | HDFS、S3、OSS                  | 可以是有限批数据，也可以是流 |
| Socket     | Socket                         | 常用于测试和学习             |

---

### 输出表（Sink Table）

#### 定义

输出表是 Flink 写入数据的**出口表**，它通过 Connector 将计算结果写入外部数据系统。

#### 本质

输出表同样是一个**动态表**——Flink 会持续将计算产生的变更（Changelog）写入输出表对应的外部系统。

#### 创建方式

```sql
CREATE TABLE mysql_result (           -- 表名
    user_id STRING,                   -- 字段定义
    total_amount DOUBLE,
    order_count BIGINT,
    PRIMARY KEY (user_id) NOT ENFORCED -- 主键声明
) WITH (
    'connector' = 'jdbc',             -- 指定连接器类型
    'url' = 'jdbc:mysql://localhost:3306/mydb',
    'table-name' = 'user_order_stats',
    'username' = 'root',
    'password' = '123456',
    'sink.buffer-flush.max-rows' = '1000',   -- 批量写入优化
    'sink.buffer-flush.interval' = '2s'
);
```

#### 核心要素

| 要素          | 说明                           | 示例                                 |
| ------------- | ------------------------------ | ------------------------------------ |
| **字段定义**  | 与目标外部表的字段对应         | `user_id STRING`                     |
| **主键声明**  | 用于 Upsert 语义（更新或插入） | `PRIMARY KEY (user_id) NOT ENFORCED` |
| **Connector** | 指定目标系统类型               | `'connector' = 'jdbc'`               |
| **连接参数**  | 连接目标系统的配置             | URL、用户名、密码等                  |
| **写入策略**  | 控制写入行为                   | 批量大小、刷新间隔等                 |

#### 常见输出表类型

| 场景       | Connector               | 写入模式                     |
| ---------- | ----------------------- | ---------------------------- |
| 关系数据库 | JDBC（MySQL/PG）        | Upsert（基于主键更新或插入） |
| 搜索引擎   | Elasticsearch           | Upsert / Append              |
| 数据湖     | Iceberg / Hudi / Paimon | Append / Upsert              |
| 消息队列   | Kafka                   | Append（追加写入）           |
| 打印调试   | Print                   | 输出到控制台                 |

---

### 源表与输出表的对比

| 维度             | 源表（Source Table） | 输出表（Sink Table）         |
| ---------------- | -------------------- | ---------------------------- |
| **方向**         | 外部系统 → Flink     | Flink → 外部系统             |
| **角色**         | 数据入口             | 数据出口                     |
| **核心操作**     | 读取（Scan）         | 写入（Insert/Upsert）        |
| **Watermark**    | 通常需要定义         | 不需要定义                   |
| **主键**         | 可选                 | 通常需要（用于 Upsert）      |
| **写入策略**     | 不适用               | 需要配置（批量、刷新间隔等） |
| **SQL 中的位置** | `FROM` 子句          | `INSERT INTO` 目标           |

---

### 完整示例：源表 → 计算 → 输出表

```sql
-- ① 定义源表：从 Kafka 读取订单数据
CREATE TABLE order_source (
    order_id STRING,
    user_id STRING,
    amount DOUBLE,
    order_time TIMESTAMP(3),
    WATERMARK FOR order_time AS order_time - INTERVAL '5' SECOND
) WITH (
    'connector' = 'kafka',
    'topic' = 'orders',
    'properties.bootstrap.servers' = 'localhost:9092',
    'format' = 'json'
);

-- ② 定义输出表：写入 MySQL
CREATE TABLE user_stats_sink (
    user_id STRING,
    total_amount DOUBLE,
    order_count BIGINT,
    PRIMARY KEY (user_id) NOT ENFORCED
) WITH (
    'connector' = 'jdbc',
    'url' = 'jdbc:mysql://localhost:3306/mydb',
    'table-name' = 'user_stats',
    'username' = 'root',
    'password' = '123456'
);

-- ③ 计算逻辑：从源表读取 → 聚合计算 → 写入输出表
INSERT INTO user_stats_sink
SELECT
    user_id,
    SUM(amount) AS total_amount,
    COUNT(*) AS order_count
FROM order_source
GROUP BY user_id;
```

---

### 源表与输出表的写入语义

输出表的写入行为取决于外部系统的能力和 Connector 的实现：

| 语义                     | 说明                         | 适用场景                    |
| ------------------------ | ---------------------------- | --------------------------- |
| **Append（追加）**       | 只插入新数据，不修改已有数据 | Kafka、HDFS、日志系统       |
| **Upsert（更新或插入）** | 有主键则更新，无主键则插入   | MySQL、Elasticsearch、HBase |
| **Retract（撤回）**      | 先删除旧值，再插入新值       | 支持删除操作的存储系统      |

---

### 多源表与多输出表

一个 Flink 任务可以连接多个源表和多个输出表：

```sql
-- 多个源表
CREATE TABLE orders (...) WITH ('connector' = 'kafka', ...);
CREATE TABLE users (...) WITH ('connector' = 'jdbc', ...);

-- 多个输出表
CREATE TABLE result_mysql (...) WITH ('connector' = 'jdbc', ...);
CREATE TABLE result_es (...) WITH ('connector' = 'elasticsearch-7', ...);
CREATE TABLE result_kafka (...) WITH ('connector' = 'kafka', ...);

-- 关联查询 + 多路输出
INSERT INTO result_mysql
SELECT o.order_id, u.user_name, o.amount
FROM orders o JOIN users u ON o.user_id = u.user_id;

INSERT INTO result_es
SELECT o.order_id, u.user_name, o.amount
FROM orders o JOIN users u ON o.user_id = u.user_id;
```

---

### 总结

> - **源表（Source Table）**：定义数据从哪里来，通过 Connector 连接外部数据源，是 Flink 任务的入口
> - **输出表（Sink Table）**：定义数据写到哪里去，通过 Connector 连接外部目标系统，是 Flink 任务的出口
> - 两者都基于**动态表**模型，通过 `CREATE TABLE ... WITH (...)` 的 DDL 语句创建
> - 源表关注：字段定义、Watermark、读取配置；输出表关注：字段定义、主键、写入策略
> - 中间的计算逻辑通过 `INSERT INTO sink_table SELECT ... FROM source_table` 串联
> - 支持多源表关联、多输出表同时写入

## Flink CDC是什么呢？

### Flink CDC 是什么

Flink CDC（Change Data Capture，变更数据捕获）是 Apache Flink 生态中的一个**实时数据集成框架**，通过解析数据库的事务日志（如 MySQL Binlog、PostgreSQL WAL），实时捕获数据库中的 INSERT、UPDATE、DELETE 操作，并将其转化为 Flink 可处理的数据流。

> 简单理解：Flink CDC 就是数据库的"实时监听器"——它不需要轮询、不需要触发器，而是直接读取数据库日志，毫秒级捕获每一次数据变更。

---

### 核心定位

```
┌──────────────────────────────────────────────────────┐
│                    Flink CDC                          │
│                                                      │
│   源数据库                Flink CDC              目标系统
│  ┌─────────┐     ┌──────────────────┐     ┌──────────┐
│  │  MySQL   │────→│  捕获变更事件流   │────→│  Kafka   │
│  │PostgreSQL│     │  全量+增量一体化  │     │  Doris   │
│  │  Oracle  │     │  Schema Evolution │     │  ES      │
│  │ SQL Server│     │  整库同步        │     │  HBase   │
│  └─────────┘     └──────────────────┘     └──────────┘
└──────────────────────────────────────────────────────┘
```

---

### 工作原理

Flink CDC 的工作流程可以分为以下阶段：

#### 快照阶段（全量读取）

首次启动时，对源表执行 `SELECT * FROM table` 获取全量数据快照，同时记录快照结束时的 Binlog 位置（GTID 或文件+偏移量）。

#### 增量阶段（增量捕获）

从快照结束位置开始，持续监听数据库事务日志，解析变更事件：

- **WriteRowsEvent** → INSERT 操作
- **UpdateRowsEvent** → UPDATE 操作
- **DeleteRowsEvent** → DELETE 操作

#### 事件序列化

每个变更事件被序列化为包含以下关键字段的结构：

```json
{
  "before": {"id": 1, "name": "Alice"},        // 变更前数据（UPDATE/DELETE）
  "after":  {"id": 1, "name": "Alice_updated"}, // 变更后数据（INSERT/UPDATE）
  "source": {"db": "test_db", "table": "users"}, // 来源信息
  "op": "u"                                      // 操作类型：c=INSERT, u=UPDATE, d=DELETE
}
```

---

### 发展历程

| 版本    | 时间       | 核心特性                                                |
| ------- | ---------- | ------------------------------------------------------- |
| **1.0** | 2020年     | 首个版本，依赖锁定机制保证一致性，无法水平扩展          |
| **2.0** | 2021年     | 借鉴 DBLog 论文，实现无锁并发读取，全量+增量无缝衔接    |
| **3.0** | 2023年12月 | Schema Evolution、整库同步、分库分表同步、YAML 配置 API |



---

### Flink CDC 3.0 核心特性

#### 整库同步

一个作业即可同步整个数据库的所有表，无需为每张表单独创建作业，大幅减少数据库连接数和计算资源消耗。

#### Schema Evolution（表结构变更自动同步）

当上游数据库发生表结构变更（如加列、删列、修改列类型）时，Flink CDC 能自动将这些变更同步到下游，无需手动修改作业定义。

#### 分库分表同步

支持将多个分库分表的数据合并同步到同一张目标表。

#### YAML 配置 API

用户通过编写 YAML 文件即可定义完整的数据管道，无需编写 Java/SQL 代码。

---

### YAML 配置示例

以下是一个从 MySQL 同步到 Apache Doris 的完整 Pipeline 配置：

```yaml
source:
  type: mysql
  hostname: localhost
  port: 3306
  username: root
  password: 123456
  tables: app_db.\.*          # 正则匹配所有表
  server-id: 5400-5404
  server-time-zone: UTC

sink:
  type: doris
  fenodes: 127.0.0.1:8030
  username: root
  password: ""
  table.create.properties.light_schema_change: true
  table.create.properties.replication_num: 1

pipeline:
  name: Sync MySQL Database to Doris
  parallelism: 2
```

通过 `flink-cdc.sh` 提交该 YAML 文件，即可自动编译并部署 Flink 作业。

---

### Flink SQL 中使用 Flink CDC

除了 YAML 方式，也可以在 Flink SQL 中直接使用 CDC Connector：

```sql
-- 创建 MySQL CDC 源表
CREATE TABLE mysql_orders (
    order_id INT,
    user_id STRING,
    amount DOUBLE,
    order_time TIMESTAMP(3),
    PRIMARY KEY (order_id) NOT ENFORCED
) WITH (
    'connector' = 'mysql-cdc',
    'hostname' = 'localhost',
    'port' = '3306',
    'username' = 'flinkuser',
    'password' = 'password',
    'database-name' = 'test_db',
    'table-name' = 'orders'
);

-- 创建 Kafka 输出表
CREATE TABLE kafka_sink (
    user_id STRING,
    total_amount DOUBLE,
    PRIMARY KEY (user_id) NOT ENFORCED
) WITH (
    'connector' = 'kafka',
    'topic' = 'order_aggregates',
    'properties.bootstrap.servers' = 'kafka:9092',
    'format' = 'json'
);

-- 实时聚合计算
INSERT INTO kafka_sink
SELECT user_id, SUM(amount) AS total_amount
FROM mysql_orders
GROUP BY user_id;
```

---

### 支持的数据源

| 数据源         | 类型       | 日志机制               |
| -------------- | ---------- | ---------------------- |
| **MySQL**      | 关系数据库 | Binlog                 |
| **PostgreSQL** | 关系数据库 | WAL（Write-Ahead Log） |
| **Oracle**     | 关系数据库 | LogMiner / XStream     |
| **SQL Server** | 关系数据库 | CDC / Change Tracking  |
| **MongoDB**    | NoSQL      | Oplog                  |
| **Kafka**      | 消息队列   | 消费 Lag               |



---

### 与传统数据同步方案对比

| 维度            | Flink CDC          | 定时 ETL      | 触发器方案           |
| --------------- | ------------------ | ------------- | -------------------- |
| **延迟**        | 毫秒级             | 分钟/小时级   | 毫秒级               |
| **对源库影响**  | 无侵入（只读日志） | 需查询源表    | 侵入性强（写触发器） |
| **数据一致性**  | Exactly-Once       | 可能重复/丢失 | 影响事务性能         |
| **全量+增量**   | 自动无缝衔接       | 需手动处理    | 不支持全量           |
| **Schema 变更** | 自动同步（3.0）    | 需手动调整    | 需手动调整           |
| **扩展性**      | 水平扩展           | 受限于单机    | 受限于数据库         |

---

### 典型应用场景

| 场景              | 说明                                                         |
| ----------------- | ------------------------------------------------------------ |
| **实时数仓**      | 将业务数据库实时同步到数据仓库（如 Doris、StarRocks、ClickHouse） |
| **数据湖入湖**    | 将数据库变更实时写入数据湖（如 Iceberg、Hudi、Paimon）       |
| **搜索索引同步**  | 将数据库变更实时同步到 Elasticsearch                         |
| **缓存更新**      | 数据库变更时自动更新 Redis 缓存                              |
| **实时风控**      | 捕获交易数据变更，实时进行风险评估                           |
| **数据备份/迁移** | 实时将数据从一个数据库同步到另一个数据库                     |

---

### 总结

> - Flink CDC 是基于数据库日志的**实时数据集成框架**，深度集成 Apache Flink
> - 通过解析事务日志（Binlog/WAL）实现**毫秒级**数据变更捕获，对源库**零侵入**
> - 支持**全量+增量一体化**：首次全量快照，之后无缝切换到增量捕获
> - 3.0 版本核心特性：Schema Evolution（表结构变更自动同步）、整库同步、分库分表同步、YAML 配置 API
> - 支持 MySQL、PostgreSQL、Oracle、MongoDB 等多种数据源
> - 提供两种使用方式：YAML Pipeline（推荐，适合数据集成）和 Flink SQL（适合复杂计算）
> - 基于 Flink Checkpoint 实现 Exactly-Once 语义，保证数据不丢不重

## Flink CDC yaml配置支持select join吗？

**Flink CDC YAML 配置不支持 SELECT JOIN 语法。**

Flink CDC YAML 中的 Transform 模块采用的是**类 SQL 的简化语法**，而非完整的 Flink SQL，其能力范围是有限的。具体来说：

### Transform 模块支持的语法

- **projection**：类似 SQL 的 `SELECT`，支持列裁剪、计算列、通配符（`*`）、内置函数调用等
- **filter**：类似 SQL 的 `WHERE`，支持条件过滤（如 `id > 10 AND name IS NOT NULL`）
- **primary-keys / partition-keys**：重定义下游主键和分区键
- **converter-after-transform**：如软删除（SOFT_DELETE）等后处理

示例：
```yaml
transform:
  - source-table: mydb.web_order
    projection: "id, order_id, UPPER(product_name) AS product_name, weight/(height*height) AS bmi"
    filter: "id > 10 AND order_id > 100"
```

### 不支持 JOIN 的原因

Flink CDC YAML API 的设计定位是**数据集成**（源端到目标端的数据同步），Transform 模块仅对**单张源表**进行行级过滤和列级投影，不涉及多表关联操作。

### 如果需要 JOIN 怎么办？

如果你的场景确实需要多表 JOIN，有以下替代方案：

- **Flink SQL API**：支持完整的 SQL 语法，包括 `SELECT ... JOIN ...`、`GROUP BY`、`Top-N`、`INSERT INTO` 等
- **Flink DataStream API**：基于 Java 编程，灵活性最高，可以实现任意复杂的处理逻辑（包括 JOIN），但学习曲线较陡

总结：YAML API 适合**单表同步 + 简单转换**的场景；需要多表 JOIN 等复杂逻辑时，应切换到 Flink SQL 或 DataStream API。

## Flink Streaming ETL是什么呢？

### Flink Streaming ETL 是什么

Flink Streaming ETL 是基于 Apache Flink 实现的**实时数据集成与加工方案**，它将传统 ETL（Extract-Transform-Load，抽取-转换-加载）从"定时批处理"升级为"持续流式处理"，实现数据从产生到可用的延迟从**天级降到秒级**。

> 简单理解：传统 ETL 像"每天早上集中分拣快递"，而 Flink Streaming ETL 像"快递到了立刻分拣派送"——数据产生即处理，无需等待。

---

### 传统 ETL vs 流式 ETL

| 维度           | 传统批处理 ETL               | Flink Streaming ETL           |
| -------------- | ---------------------------- | ----------------------------- |
| **处理方式**   | 定时批量（如每天凌晨跑一次） | 持续流式（数据产生即处理）    |
| **延迟**       | 小时级/天级（T+1）           | 秒级/毫秒级                   |
| **数据新鲜度** | 昨天的数据，今天才能看到     | 数据产生几秒后即可查询        |
| **架构复杂度** | 需要暂存表、多个处理层       | 架构简洁，消除中间存储        |
| **资源利用**   | 集中调度，空闲时资源浪费     | 持续运行，资源利用率高        |
| **容错机制**   | 失败需重跑整个批次           | Checkpoint 断点恢复，不丢不重 |
| **适用场景**   | 报表统计、离线分析           | 实时数仓、实时风控、实时推荐  |

---

### 核心架构

```
┌──────────────┐     ┌──────────────┐     ┌──────────────┐     ┌──────────────┐
│   数据源      │     │  抽取(Extract)│     │  转换(Transform)│   │  加载(Load)   │
│              │     │              │     │              │     │              │
│  MySQL CDC   │ ──→ │  Flink CDC   │ ──→ │  Flink SQL   │ ──→ │  目标系统     │
│  Kafka       │     │  实时捕获     │     │  清洗/关联    │     │  数据湖/仓库  │
│  日志文件     │     │  变更事件     │     │  聚合/过滤    │     │  ES/Redis    │
└──────────────┘     └──────────────┘     └──────────────┘     └──────────────┘
      持续流入               实时捕获              流式加工              持续写入
```

---

### E-T-L 三阶段详解

#### Extract（抽取）

从各种数据源实时捕获数据变更：

| 数据源             | 抽取方式                     | 说明                      |
| ------------------ | ---------------------------- | ------------------------- |
| MySQL / PostgreSQL | Flink CDC（解析 Binlog/WAL） | 捕获 INSERT/UPDATE/DELETE |
| Kafka / Pulsar     | Connector 消费消息           | 持续消费消息流            |
| 日志文件           | File Source                  | 监听文件新增内容          |
| API / Socket       | 自定义 Source                | 实时拉取或推送            |

#### Transform（转换）

在 Flink 引擎中对数据进行实时加工，常见操作包括：

- **数据清洗**：过滤脏数据、去重、补全缺失字段
- **格式转换**：JSON → 结构化字段、类型转换
- **数据关联**：流表与维表 JOIN（如订单关联用户信息）
- **聚合计算**：实时统计（如每分钟订单量、用户消费总额）
- **窗口计算**：滚动窗口、滑动窗口、会话窗口
- **数据拆分/合并**：按条件分流、多流合并

#### Load（加载）

将加工后的数据实时写入目标系统：

| 目标系统                               | 写入方式      | 适用场景           |
| -------------------------------------- | ------------- | ------------------ |
| 数据湖（Iceberg/Hudi/Paimon）          | Append/Upsert | 构建实时数据湖     |
| 数据仓库（Doris/StarRocks/ClickHouse） | Upsert        | 实时 OLAP 分析     |
| Elasticsearch                          | Upsert        | 实时检索、日志分析 |
| Kafka                                  | Append        | 下游消费、事件驱动 |
| Redis / HBase                          | Upsert        | 实时缓存、特征存储 |

---

### 典型场景：实时数仓分层

Flink Streaming ETL 最常见的应用是构建**实时数据仓库**，实现数据从 ODS 层到 DWD 层再到 ADS 层的实时流转：

```
┌─────────────┐     ┌─────────────┐     ┌─────────────┐     ┌─────────────┐
│   ODS 层     │     │   DWD 层     │     │   DWS 层     │     │   ADS 层     │
│  原始数据层   │ ──→ │  明细宽表层   │ ──→ │  汇总层      │ ──→ │  应用层      │
│             │     │             │     │             │     │             │
│ MySQL CDC   │     │ 多表关联     │     │ 聚合统计     │     │ 报表/大屏    │
│ Kafka       │     │ 数据清洗     │     │ 窗口计算     │     │ 实时推送     │
└─────────────┘     └─────────────┘     └─────────────┘     └─────────────┘
```

#### Flink SQL 实现示例

```sql
-- ① ODS 层：从 MySQL CDC 抽取订单数据
CREATE TABLE ods_orders (
    order_id BIGINT,
    user_id BIGINT,
    product_id BIGINT,
    amount DECIMAL(10, 2),
    order_time TIMESTAMP(3),
    PRIMARY KEY (order_id) NOT ENFORCED
) WITH (
    'connector' = 'mysql-cdc',
    'hostname' = 'mysql-host',
    'port' = '3306',
    'username' = 'flink',
    'password' = '***',
    'database-name' = 'ecommerce',
    'table-name' = 'orders'
);

-- ② 维表：用户信息
CREATE TABLE dim_users (
    user_id BIGINT,
    user_name STRING,
    user_level STRING,
    city STRING,
    PRIMARY KEY (user_id) NOT ENFORCED
) WITH (
    'connector' = 'jdbc',
    'url' = 'jdbc:mysql://mysql-host:3306/ecommerce',
    'table-name' = 'users'
);

-- ③ DWD 层：实时关联生成订单宽表，写入 Kafka
CREATE TABLE dwd_order_detail (
    order_id BIGINT,
    user_id BIGINT,
    user_name STRING,
    user_level STRING,
    city STRING,
    product_id BIGINT,
    amount DECIMAL(10, 2),
    order_time TIMESTAMP(3)
) WITH (
    'connector' = 'kafka',
    'topic' = 'dwd_order_detail',
    'properties.bootstrap.servers' = 'kafka:9092',
    'format' = 'json'
);

-- 实时关联：订单流 LEFT JOIN 用户维表
INSERT INTO dwd_order_detail
SELECT
    o.order_id, o.user_id,
    u.user_name, u.user_level, u.city,
    o.product_id, o.amount, o.order_time
FROM ods_orders o
LEFT JOIN dim_users FOR SYSTEM_TIME AS OF o.order_time AS u
ON o.user_id = u.user_id;

-- ④ DWS 层：实时聚合，按城市统计订单量和金额
CREATE TABLE dws_city_stats (
    city STRING,
    order_count BIGINT,
    total_amount DECIMAL(15, 2),
    PRIMARY KEY (city) NOT ENFORCED
) WITH (
    'connector' = 'jdbc',
    'url' = 'jdbc:mysql://mysql-host:3306/warehouse',
    'table-name' = 'city_order_stats'
);

INSERT INTO dws_city_stats
SELECT
    city,
    COUNT(*) AS order_count,
    SUM(amount) AS total_amount
FROM dwd_order_detail
GROUP BY city;
```

---

### Flink Streaming ETL 的核心优势

#### 低延迟

数据在源系统产生变更的瞬间就被捕获和处理，端到端延迟可控制在**秒级甚至毫秒级**。

#### Exactly-Once 语义

基于 Flink Checkpoint + 两阶段提交（2PC）协议，保证数据**不丢不重**，即使发生故障也能从最近一致点恢复。

#### 流批一体

同一套代码既可以处理实时流数据，也可以处理历史批数据，避免维护两套系统。

#### 灵活的状态管理

Flink 内置强大的状态后端（如 RocksDB），支持在流处理中维护中间状态（如聚合结果、用户画像），实现有状态计算。

#### 丰富的生态

通过 Connector 无缝对接 Kafka、MySQL、Elasticsearch、Iceberg、Doris 等数十种数据源和目标系统。

---

### 典型应用场景

| 场景             | 说明                              | 示例                               |
| ---------------- | --------------------------------- | ---------------------------------- |
| **实时数仓**     | 替代 T+1 批处理，构建秒级数据仓库 | 订单数据实时入湖，分钟级可查       |
| **实时风控**     | 交易发生时立即检测异常            | 信用卡欺诈实时识别                 |
| **实时推荐**     | 用户行为实时反馈到推荐模型        | 点击商品后立即更新推荐列表         |
| **实时大屏**     | 业务指标实时展示                  | 双十一实时成交额大屏               |
| **数据同步**     | 异构系统间数据实时同步            | MySQL → Elasticsearch 搜索索引同步 |
| **IoT 数据处理** | 传感器数据实时清洗和聚合          | 按设备类型组织数据，去重后写入 S3  |

---

### 总结

> - Flink Streaming ETL 是将传统批处理 ETL 升级为**持续流式处理**的方案，数据产生即处理
> - 核心流程：**Extract**（CDC/Connector 实时捕获）→ **Transform**（Flink SQL/DataStream 流式加工）→ **Load**（实时写入目标系统）
> - 相比传统 ETL，延迟从**天级降到秒级**，架构更简洁，资源利用率更高
> - 基于 Flink 的 Checkpoint 和 Exactly-Once 语义，保证数据不丢不重
> - 最典型的应用是构建**实时数仓**（ODS → DWD → DWS → ADS 实时分层）
> - 广泛应用于实时风控、实时推荐、实时大屏、IoT 数据处理等场景

## Flink SQL JOIN

>参考本站示例 https://gitee.com/dexterleslie/demonstration/tree/main/demo-flink/demo-flink-table-api-n-sql 中的FlinkSQLParentAndChildTableInnerJoinParentDatumDelayTests、FlinkSQLParentAndChildTableLeftJoinChildDatumDelayTests、FlinkSQLParentAndChildTableLeftJoinParentDatumDelayTests

## Flink Connector JDBC

>参考本站示例 https://gitee.com/dexterleslie/demonstration/tree/main/demo-flink/demo-flink-connector 中的FlinkSQLConnectorJdbcTests

## Flink Connector MySQL CDC

>参考本站示例 https://gitee.com/dexterleslie/demonstration/tree/main/demo-flink/demo-flink-connector 中的FlinkSQLConnectorMySQLCDCTests

## 主子表JOIN成宽表

>参考本站示例 https://gitee.com/dexterleslie/demonstration/tree/main/demo-flink/demo-flink-connector 中的FlinkSQLConnectorMySQLCDCParentNChildTableTests
>
>提醒：
>
>- 使用固定JobId
>- 表状态使用RocksDB存储以减少内存使用
>- checkpoint只保留最近一份以减少硬盘空间使用
