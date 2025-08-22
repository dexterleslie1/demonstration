# `kafka`



## 概念

### `Topic` 和 `Partition`

**Kafka Topic 是什么？**

Kafka Topic 是 Kafka 消息系统的核心概念之一，用于对消息进行分类和存储。以下是 Kafka Topic 的详细解释：

------

**1. Topic 的基本定义**

- **Topic 是消息的逻辑分类**：
  Kafka 中的消息（数据）按照 Topic 进行分类。例如，电商系统可以有 `orders`（订单）、`payments`（支付）、`shipments`（发货）等 Topic。
- **Topic 是消息的存储单元**：
  每个 Topic 是一个逻辑上的容器，消息会被持久化存储在 Kafka 集群中，直到被消费者消费或达到配置的保留时间（Retention Time）。

------

**2. Topic 的核心特性**

- 多生产者、多消费者：
  - 生产者（Producer）可以向同一个 Topic 发送消息。
  - 消费者（Consumer）可以从同一个 Topic 订阅消息。
- 分区（Partition）机制：
  - 一个 Topic 可以划分为多个分区（Partition），分区是消息的实际存储单元。
  - 分区可以分布在不同的 Broker 上，实现消息的并行处理和扩展性。
- 消息有序性：
  - 在单个分区内，消息是有序的（按写入顺序存储）。
  - 不同分区之间的消息是无序的。
- 消息持久化：
  - 消息会被持久化存储在磁盘上，直到达到配置的保留时间或磁盘空间不足。

------

**3. Topic 的结构**

- 分区（Partition）：
  - 一个 Topic 由一个或多个分区组成。
  - 每个分区是一个有序的、不可变的消息序列。
  - 分区可以分布在不同的 Broker 上，实现负载均衡和容错。
- 副本（Replica）：
  - 每个分区可以有多个副本（Replica），其中一个副本是 Leader，其他副本是 Follower。
  - Leader 负责处理读写请求，Follower 同步 Leader 的数据。
  - 副本机制提高了 Kafka 的容错性。
- 偏移量（Offset）：
  - 每个消息在分区中有一个唯一的偏移量（Offset），用于标识消息的位置。
  - 消费者通过偏移量来跟踪已消费的消息。

------

**4. Topic 的使用场景**

- **日志收集**：
  将应用程序的日志发送到 Kafka Topic，供日志分析系统消费。
- **消息队列**：
  作为消息队列，解耦生产者和消费者。
- **流处理**：
  Kafka Streams 或 Flink 等流处理框架可以从 Topic 读取数据，进行处理后再写入其他 Topic。
- **事件溯源**：
  将系统中的事件（如订单创建、支付完成）发送到 Kafka Topic，供其他系统消费。

------

**5. Topic 的创建与管理**

- 创建 Topic：

  使用 Kafka 命令行工具或 API 创建 Topic，可以指定分区数、副本数等参数。

  ```bash
  /usr/bin/kafka-topics --bootstrap-server localhost:9092 --create --topic my-topic --partitions 3 --replication-factor 1
  ```

- 查看 Topic：

  ```bash
  /usr/bin/kafka-topics --bootstrap-server localhost:9092 --list
  /usr/bin/kafka-topics --bootstrap-server localhost:9092 --describe --topic my-topic
  ```

- 删除 Topic：

  ```bash
  /usr/bin/kafka-topics --bootstrap-server localhost:9092 --delete --topic my-topic
  ```

------

**6. Topic 的配置参数**

- 分区数（Partitions）：
  - 决定 Topic 的并行处理能力。
  - 分区数越多，吞吐量越高，但也会增加系统复杂度。
- 副本数（Replication Factor）：
  - 决定 Topic 的容错性。
  - 副本数越多，容错性越高，但也会增加存储开销。
- 保留时间（Retention Time）：
  - 消息在 Kafka 中保留的时间（默认 7 天）。
- 保留大小（Retention Size）：
  - 消息在 Kafka 中保留的总大小（默认无限制）。

------

**7. Topic 的示例**

假设有一个电商系统，可以创建以下 Topic：

- orders：存储订单数据。
  - 分区数：3
  - 副本数：2
- payments：存储支付数据。
  - 分区数：2
  - 副本数：2
- shipments：存储发货数据。
  - 分区数：1
  - 副本数：1

生产者将订单、支付、发货数据分别发送到对应的 Topic，消费者（如订单处理系统、支付系统、物流系统）从对应的 Topic 订阅消息。

------

**8. Topic 的优势**

- **高吞吐量**：
  通过分区和副本机制，Kafka 可以实现高吞吐量的消息处理。
- **可扩展性**：
  可以通过增加分区或 Broker 来扩展 Topic 的处理能力。
- **容错性**：
  通过副本机制，Kafka 可以保证消息的可靠性。
- **持久化**：
  消息会被持久化存储，适合需要长期存储数据的场景。

------

**总结**

Kafka Topic 是 Kafka 消息系统的核心概念，用于对消息进行分类和存储。它具有多生产者、多消费者、分区、副本、持久化等特性，适用于日志收集、消息队列、流处理、事件溯源等多种场景。通过合理配置 Topic 的分区数、副本数等参数，可以实现高吞吐量、可扩展性和容错性。



### 消费者组

Kafka **消费者组（Consumer Group）** 是 Kafka 实现**高吞吐、高容错并行消费**的核心机制。它允许多个消费者实例（Consumer）组成一个逻辑组，共同消费一个或多个主题（Topic）的消息，通过分区分配、重平衡（Rebalance）和偏移量管理，实现负载均衡与故障容错。以下从核心机制、工作流程、关键组件、分配策略及最佳实践等方面深入解析。


#### **一、消费者组的核心目标**
消费者组的设计主要为了解决两个问题：  
1. **并行消费**：通过多消费者实例分担负载，提升消息处理吞吐量（每个分区可被独立消费）。  
2. **容错与弹性**：当消费者故障或新增时，自动重新分配分区（重平衡），确保消费不中断。  


#### **二、消费者组的核心机制**

##### **1. 组协调器（Group Coordinator）**  
消费者组的“管理者”，负责协调组内消费者的生命周期、分区分配和偏移量提交。  
- **角色**：每个 Kafka 集群中，一个 Broker 会充当组协调器（由 `group.initial.rebalance.delay.ms` 等参数控制）。  
- **职责**：  
  - 管理消费者组的元数据（如成员列表、分区分配策略）。  
  - 触发并协调重平衡（Rebalance）。  
  - 接收消费者的心跳（Heartbeat），判断成员是否存活。  


##### **2. 消费者组的状态**  
消费者组有三种状态，反映其生命周期：  
- **空组（Empty）**：组内无消费者成员。  
- **准备中（PreparingRebalance）**：组内有成员加入/退出，即将触发重平衡。  
- **稳定（Stable）**：所有成员已加入，分区分配完成，正常消费。  


##### **3. 重平衡（Rebalance）**  
当消费者组的成员发生变化（如新增/退出消费者、主题分区数变化）时，组协调器会触发**重平衡**，重新分配分区。  

**触发条件**：  
- 消费者加入或退出组（主动/被动）。  
- 主题的分区数增加（消费者组需要感知新分区）。  
- 组协调器故障转移（新的协调器需要重新初始化组状态）。  

**重平衡过程**：  

1. **通知阶段**：组协调器向所有消费者发送 `JoinGroup` 请求，收集成员信息（如支持的协议、实例ID）。  
2. **领导者选举**：其中一个消费者被选为“组领导者”（通常是第一个响应的成员），负责制定分区分配方案。  
3. **分配方案提交**：组领导者根据分配策略（如 `RangeAssignor`）生成分区分配结果，发送给组协调器。  
4. **同步阶段**：组协调器将分配结果广播给所有消费者，成员确认后开始消费新分配的分区。  

**重平衡的影响**：  
- **优点**：确保故障消费者释放的分区被其他消费者接管，避免消息丢失。  
- **缺点**：重平衡期间所有消费者暂停消费（直到分配完成），频繁重平衡会降低吞吐量（需优化配置减少触发次数）。  


##### **4. 偏移量管理**  
消费者组通过**组偏移量（Group Offset）**记录每个分区的消费进度，确保消息“至少一次”或“恰好一次”消费。  

- **存储位置**：组偏移量存储在 Kafka 的内部主题 `__consumer_offsets` 中（默认保留 7 天）。  
- **提交方式**：  
  - **同步提交（`commitSync()`）**：阻塞等待 Broker 确认，确保偏移量成功写入（强一致性，可能影响吞吐量）。  
  - **异步提交（`commitAsync()`）**：非阻塞提交，通过回调处理失败（高吞吐，可能丢数据）。  
  - **批量提交**：结合时间间隔（`enable.auto.commit=true`，默认 5 秒）或手动触发，平衡一致性与性能。  


#### **三、消费者组的工作流程**  
以一个包含 3 个分区（P0、P1、P2）的主题 `topic`，和一个包含 2 个消费者（C1、C2）的组 `group1` 为例：  

1. **消费者加入组**：  
   C1 和 C2 启动后，向组协调器发送 `JoinGroup` 请求，声明自己加入 `group1`。  

2. **组协调器分配分区**：  
   组协调器根据分配策略（如 `RangeAssignor`）分配分区：C1 消费 P0、P1，C2 消费 P2。  

3. **消费与提交偏移量**：  
   C1 从 P0、P1 拉取消息处理，C2 从 P2 拉取消息处理。处理完成后，消费者通过 `commitSync()` 或 `commitAsync()` 提交当前消费的偏移量（如 P0 偏移到 100，P1 偏移到 200）。  

4. **消费者故障或退出**：  
   若 C1 故障，组协调器检测到其心跳超时（`session.timeout.ms` 默认 10 秒），触发重平衡。C2 被重新选举为领导者，接管 P0、P1 的分区，从上次提交的偏移量（P0:100，P1:200）继续消费。  

5. **消费者退出组**：  
   若 C2 主动退出（调用 `close()`），组协调器将其从成员列表移除，触发重平衡。此时组内只剩 C1，C1 接管所有分区（P0、P1、P2）。  


#### **四、分区分配策略**  
组协调器根据配置的**分区分配策略**，将主题的分区分配给组内消费者。Kafka 支持以下内置策略（可通过 `partition.assignment.strategy` 参数配置）：  

| **策略**                    | **特点**                                                     | **适用场景**                                     |
| --------------------------- | ------------------------------------------------------------ | ------------------------------------------------ |
| `RangeAssignor`（默认）     | 按主题的分区范围分配（如主题有 5 分区，2 消费者则 C1:P0-P2，C2:P3-P4）。 | 分区连续，追求简单高效。                         |
| `RoundRobinAssignor`        | 轮询分配（所有主题的分区被打乱，按顺序轮询给消费者）。       | 多主题消费，希望分区均匀分布。                   |
| `StickyAssignor`            | 粘性分配（尽量保持原有分配，仅调整变化的部分，减少重平衡开销）。 | 频繁重平衡场景（如消费者频繁上下线），优化性能。 |
| `CooperativeStickyAssignor` | 协作式粘性分配（重平衡时协商调整，减少数据移动）。           | Kafka 2.4+，支持更平滑的重平衡。                 |


#### **五、消费者组的关键配置**  
合理配置消费者组参数可提升性能与稳定性，常见参数如下：  

| **参数**                        | **说明**                                                     | **默认值**      |
| ------------------------------- | ------------------------------------------------------------ | --------------- |
| `group.id`                      | 消费者组的唯一标识（必须设置，否则无法加入组）。             | 无（必填）      |
| `session.timeout.ms`            | 消费者与组协调器的心跳超时时间（超时则认为消费者故障，触发重平衡）。 | 10 秒           |
| `heartbeat.interval.ms`         | 消费者发送心跳的间隔（需小于 `session.timeout.ms`）。        | 3 秒            |
| `max.poll.interval.ms`          | 消费者两次拉取消息的最大间隔（超时则认为处理过慢，触发重平衡）。 | 5 分钟          |
| `enable.auto.commit`            | 是否自动提交偏移量（`true` 为自动，`false` 需手动提交）。    | `true`          |
| `auto.commit.interval.ms`       | 自动提交的时间间隔（仅当 `enable.auto.commit=true` 时生效）。 | 5 秒            |
| `partition.assignment.strategy` | 分区分配策略（如 `RangeAssignor`、`RoundRobinAssignor`）。   | `RangeAssignor` |


#### **六、消费者组的最佳实践**  
1. **消费者数量与分区数匹配**：  
   消费者组的吞吐量受限于分区数（每个分区只能被一个消费者消费）。建议消费者数量 ≤ 分区数（否则多余消费者空闲）。  

2. **避免频繁重平衡**：  
   - 调整 `session.timeout.ms`（如设为 30 秒）和 `max.poll.interval.ms`（如设为 10 分钟），减少因网络波动触发的误判。  
   - 优化消息处理逻辑，避免 `max.poll.interval.ms` 超时（如拆分大任务，异步处理）。  

3. **合理选择分配策略**：  
   - 多主题消费时用 `RoundRobinAssignor`；  
   - 频繁重平衡场景用 `StickyAssignor` 减少数据移动。  

4. **手动提交偏移量**：  
   对一致性要求高的场景（如金融交易），使用手动提交（`commitSync()`），确保消息处理完成后再提交偏移量。  

5. **监控消费者组状态**：  
   通过 Kafka 工具（如 `kafka-consumer-groups.sh`）或监控平台（如 Prometheus + Grafana）监控消费者组的偏移量滞后（Lag）、成员状态等，及时发现消费延迟或故障。  


#### **七、总结**  
Kafka 消费者组是实现**高吞吐、高容错并行消费**的核心机制，通过组协调器、分区分配策略和偏移量管理，解决了多消费者协同消费的问题。合理配置消费者组参数、选择分配策略，并结合业务场景优化，可充分发挥 Kafka 的实时处理能力。  

**适用场景**：实时日志分析、订单处理、实时推荐、监控告警等需要并行消费和高可靠性的实时数据处理场景。



### 两种核心消费模型

Kafka 除了支持**发布-订阅模型（Pub/Sub）**外，还天然支持**队列模型（Queue Model）**。这两种模型是 Kafka 消费者的两种核心消费方式，通过**消费者组（Consumer Group）**的灵活配置实现，本质上是通过分区和消费者的绑定关系来区分的。


#### **一、两种模型的核心区别**
| **模型**          | **核心特点**                                                 | **适用场景**                                               |
| ----------------- | ------------------------------------------------------------ | ---------------------------------------------------------- |
| **发布-订阅模型** | 消息被所有订阅该主题的消费者组消费（每个消费者组独立消费全量消息）。 | 数据多副本处理、跨业务线广播（如实时监控告警、日志同步）。 |
| **队列模型**      | 消息被消费者组内的消费者负载均衡消费（每条消息仅被组内一个消费者处理）。 | 高吞吐并行处理、流量削峰（如订单处理、实时计算）。         |


#### **二、模型实现原理：消费者组的分区分配**
Kafka 通过**消费者组的分区分配策略**实现两种模型的切换，核心依赖“消费者组内消费者数量”与“主题分区数”的关系：  

##### **1. 发布-订阅模型（多消费者组）**  
当**多个消费者组订阅同一主题**时，每个消费者组独立消费主题的所有分区。此时，每个消费者组的行为等同于“订阅”了全量消息，不同组之间无竞争，消息会被每个组完整消费一次。  

**示例**：  
主题 `log_topic` 有 3 个分区（P0、P1、P2），消费者组 `group_a` 和 `group_b` 均订阅该主题：  
- `group_a` 内的消费者 C1、C2 分配 P0、P1（假设 2 消费者），消费全量消息。  
- `group_b` 内的消费者 C3 独占所有分区（P0、P1、P2），同样消费全量消息。  


##### **2. 队列模型（单消费者组）**  
当**只有一个消费者组订阅主题**时，组内的消费者通过分区分配策略（如 `RangeAssignor`）“瓜分”分区，实现**负载均衡**。此时，每条消息仅被组内一个消费者处理（分区与消费者一一对应），消息不会被重复消费（同一分区仅由一个消费者读取）。  

**示例**：  
主题 `order_topic` 有 4 个分区（P0-P3），消费者组 `group_c` 包含 2 个消费者（C4、C5）：  
- 按 `RangeAssignor` 策略，C4 消费 P0、P1，C5 消费 P2、P3。  
- 每条消息仅被 C4 或 C5 处理一次（分区与消费者绑定）。  


#### **三、两种模型的典型场景**
##### **发布-订阅模型（多组消费）**  
- **数据广播**：一条消息需要被多个业务线处理（如用户注册事件，需同时触发短信通知、积分发放、用户画像更新）。  
- **跨集群同步**：将 Kafka 主题的数据复制到其他集群（如从生产集群到灾备集群），通过独立的消费者组拉取全量数据。  
- **多级处理流水线**：消息先被日志分析组消费（生成统计报表），再被机器学习组消费（训练模型）。  


##### **队列模型（单组消费）**  
- **流量削峰**：突发高并发请求（如秒杀活动）通过消费者组内的多个消费者并行处理，避免单节点压力过大。  
- **实时计算**：订单消息被消费者组内的多个消费者并行处理（如计算实时 GMV、库存扣减），提升吞吐量。  
- **任务分发**：将任务队列中的消息（如图片压缩、文件转码）分配给多个 worker 节点并行执行。  


#### **四、总结**
Kafka 的两种核心消费模型：  
- **发布-订阅模型**：通过**多个消费者组**实现消息广播，每个组独立消费全量消息。  
- **队列模型**：通过**单个消费者组**实现负载均衡，组内消费者分摊分区消费，每条消息仅被处理一次。  

两种模型通过消费者组的灵活配置（数量、分区分配策略）无缝切换，共同支撑了 Kafka 在实时数据处理、日志收集、事件驱动架构等场景中的广泛应用。



### 消费者组会重复消费消息吗？

Kafka 消费者组**可能会重复消费消息**，但这是由其设计机制（如偏移量管理、重平衡）和消息处理逻辑共同决定的。是否重复取决于**偏移量提交时机**、**消息处理逻辑的健壮性**以及**消费者故障恢复策略**。以下从原理、场景和解决方案三个维度详细解析：


#### **一、重复消费的核心原因**
消费者组的重复消费本质上是**“消息处理完成”与“偏移量提交”之间的时序问题**。Kafka 保证消息“至少一次”（At-Least-Once）消费，即消息可能被多次处理，但不会丢失。具体触发重复的场景包括：

##### **1. 消息处理完成前，偏移量未提交**
消费者组的偏移量提交（`commit`）是**异步操作**（默认自动提交），若消息处理逻辑耗时较长，或在处理完成前发生以下情况，会导致偏移量未及时提交，从而触发重复消费：  
- **自动提交超时**：若 `enable.auto.commit=true`（默认开启），消费者会定期（`auto.commit.interval.ms`，默认 5 秒）提交偏移量。若消息在两次提交之间处理完成，但偏移量未提交时消费者故障，重启后会从上一次提交的偏移量重新消费未处理的消息。  
- **手动提交延迟**：若手动调用 `commitSync()` 或 `commitAsync()`，但在提交前消费者崩溃（如进程终止），偏移量未持久化，重启后需重新消费未提交的消息。  


##### **2. 消费者组重平衡（Rebalance）**
当消费者组发生重平衡（如消费者加入/退出、分区数变化）时，组协调器会重新分配分区。若原消费者在**处理消息但未提交偏移量**时被移出组，其负责的分区会被其他消费者接管，新消费者会从**上次提交的偏移量**开始消费，导致原消费者已处理但未提交的消息被重复消费。  

**示例**：  
- 消费者 C1 处理分区 P0 的消息（偏移量 0→100），但未提交偏移量时故障退出。  
- 组协调器触发重平衡，消费者 C2 接管 P0，从偏移量 100 开始消费（实际 C1 已处理到 100，但未提交）。  
- 若 C1 未提交的偏移量是 90（即已处理到 100 但未提交），则 C2 会从 90 开始消费，导致 90→100 的消息被重复处理。  


##### **3. 消息处理逻辑异常**
即使偏移量已提交，若消息处理逻辑本身存在错误（如抛异常未捕获），可能导致消息未被正确处理，但偏移量已提交，从而丢失处理结果。此时需结合**幂等性设计**或**事务补偿**来解决，而非 Kafka 原生的重复消费问题。  


#### **二、Kafka 的消息传递语义**
Kafka 消费者组的消息传递语义由**偏移量提交策略**和**业务逻辑**共同决定，主要有三种模式：  

| **语义类型**                  | **定义**                           | **实现方式**                                                 |
| ----------------------------- | ---------------------------------- | ------------------------------------------------------------ |
| **至少一次（At-Least-Once）** | 消息可能被多次消费，但不会丢失。   | 默认模式：自动提交偏移量（`enable.auto.commit=true`），或在处理完成后手动提交。 |
| **至多一次（At-Most-Once）**  | 消息最多被消费一次，可能丢失。     | 手动提交偏移量在处理消息**前**（如先提交偏移量，再处理消息）。 |
| **恰好一次（Exactly-Once）**  | 消息被精确消费一次，无重复或丢失。 | 结合 Kafka 事务（Producer 与 Consumer 共同使用事务）或幂等处理逻辑。 |


#### **三、如何避免或减少重复消费？**
尽管 Kafka 消费者组默认可能重复消费，但通过合理配置和业务逻辑设计，可以**最小化重复**或**安全处理重复**。以下是关键方法：

##### **1. 优化偏移量提交策略**
- **手动提交偏移量**：关闭自动提交（`enable.auto.commit=false`），在消息**处理完成且确认无误后**手动提交（`commitSync()` 或 `commitAsync()`）。  
  ```java
  while (true) {
      ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(100));
      for (ConsumerRecord<String, String> record : records) {
          processRecord(record); // 业务处理逻辑
      }
      consumer.commitSync(); // 处理完成后提交偏移量
  }
  ```
- **异步提交+回调**：使用 `commitAsync()` 并在回调中处理异常（如重试提交），避免提交失败导致重复。  


##### **2. 控制重平衡的影响**
- **缩短 `session.timeout.ms`**：减少消费者故障检测的超时时间（默认 10 秒），避免因网络波动误触发重平衡。  
- **优化 `max.poll.interval.ms`**：增大消费者处理消息的最大间隔（默认 5 分钟），避免因处理耗时过长被误判为故障（触发重平衡）。  
- **使用粘性分配策略（StickyAssignor）**：重平衡时尽量保持原有分区分配，减少数据移动（Kafka 0.11+ 支持）。  


##### **3. 设计幂等的业务逻辑**
即使消息被重复消费，业务逻辑也能正确处理（如根据唯一 ID 去重、数据库唯一约束等）。  
**示例**：  
- 数据库插入时使用 `INSERT IGNORE` 或 `ON DUPLICATE KEY UPDATE`。  
- 缓存操作时检查键是否存在，避免重复更新。  


##### **4. 使用恰好一次语义（Exactly-Once）**
对于需要严格不重复的场景（如金融交易），可结合 Kafka 事务实现端到端的恰好一次处理：  
- **生产者事务**：确保消息生产时不重复（通过 `KafkaProducer` 的 `initTransactions()`、`beginTransaction()`、`commitTransaction()`）。  
- **消费者事务**：消费者作为事务的一部分，提交偏移量与处理结果绑定（需 Kafka 0.11+ 支持）。  


#### **四、总结**
Kafka 消费者组**默认可能重复消费消息**（至少一次语义），但重复的根本原因是“消息处理完成”与“偏移量提交”的时序问题，或重平衡导致的偏移量未及时提交。通过以下方式可有效控制重复：  
- 手动提交偏移量（处理完成后提交）。  
- 设计幂等的业务逻辑。  
- 优化重平衡参数（如 `session.timeout.ms`）。  
- 使用恰好一次语义（结合事务）。  

实际应用中，需根据业务对一致性的要求（如日志统计可接受重复，支付交易需严格不重复）选择合适的策略。



### 不同的消费者组消费同一个 `topic`，同一个消息会被多次消费吗？

在 Kafka 中，**不同的消费者组（Consumer Group）消费同一个主题（Topic）时，同一条消息会被每个消费者组各自消费一次**。这是 Kafka“发布-订阅”模型的核心特性之一，其根本原因是**不同消费者组的偏移量（Offset）独立维护**，彼此之间互不影响。


#### **一、核心机制：消费者组的独立偏移量**
Kafka 的主题（Topic）会被划分为多个分区（Partition），每个分区可以被多个消费者组独立消费。每个消费者组会为**每个分区维护自己的消费偏移量**（记录该组在该分区上已消费到的位置），这些偏移量存储在 Kafka 的内部主题 `__consumer_offsets` 中，键为 `(group.id, topic, partition)`。  

因此：  
- 同一消费者组内的消费者通过**分区分配**（如 `RangeAssignor`）避免重复消费同一分区的消息（组内消费者共同消费所有分区，但每个分区仅被组内一个消费者处理）。  
- 不同消费者组的偏移量是**完全独立**的：消费者组 A 消费到分区 P0 的偏移量 100，不影响消费者组 B 从分区 P0 的偏移量 0 开始消费。  


#### **二、具体场景示例**
假设主题 `order_topic` 有 2 个分区（P0、P1），消息按顺序写入：  
- 消息 M1 → P0（偏移量 0）  
- 消息 M2 → P1（偏移量 0）  
- 消息 M3 → P0（偏移量 1）  


##### **消费者组 A（Group A）的消费行为**  
Group A 包含 2 个消费者（C1、C2），通过分区分配策略（如 `RangeAssignor`）分配为：  
- C1 消费 P0（偏移量从 0 开始）。  
- C2 消费 P1（偏移量从 0 开始）。  

消费过程：  
1. C1 读取 P0 的 M1（偏移量 0），处理后提交偏移量 1（表示下次从 1 开始）。  
2. C2 读取 P1 的 M2（偏移量 0），处理后提交偏移量 1。  
3. 后续 M3 写入 P0（偏移量 1），C1 读取并处理，提交偏移量 2。  


##### **消费者组 B（Group B）的消费行为**  
Group B 包含 1 个消费者（C3），未分配分区（自动分配所有分区）：  
- C3 消费 P0（偏移量从 0 开始）和 P1（偏移量从 0 开始）。  

消费过程：  
1. C3 读取 P0 的 M1（偏移量 0），处理后提交偏移量 1（Group B 的 P0 偏移量）。  
2. C3 读取 P0 的 M3（偏移量 1），处理后提交偏移量 2。  
3. C3 读取 P1 的 M2（偏移量 0），处理后提交偏移量 1（Group B 的 P1 偏移量）。  


**结论**：  
- Group A 消费了 M1、M2、M3（各一次）。  
- Group B 也消费了 M1、M2、M3（各一次）。  
同一条消息（如 M1）被两个不同的消费者组各自消费了一次。  


#### **三、为什么 Kafka 设计为多消费者组重复消费？**
这一设计是为了支持 Kafka 的**多订阅者模式**（Pub/Sub），满足以下场景需求：  
1. **数据多副本处理**：不同业务线需要基于同一批原始数据做不同的处理（如一条订单消息，需要同时触发库存扣减、物流通知、用户积分发放）。  
2. **流量隔离**：高优先级业务（如实时告警）和低优先级业务（如日志归档）可以独立消费同一主题，避免相互影响（如低优先级消费慢不会阻塞高优先级）。  
3. **容灾与备份**：同一数据的多个副本可以被不同消费者组消费，用于备份到其他系统（如从 Kafka 复制到 Elasticsearch、HBase）。  


#### **四、如何验证不同消费者组的重复消费？**
可以通过 Kafka 自带的工具 `kafka-console-consumer.sh` 验证：  
1. 启动消费者组 A：  
   ```bash
   kafka-console-consumer.sh --bootstrap-server localhost:9092 --topic order_topic --group group_a
   ```
2. 启动消费者组 B：  
   ```bash
   kafka-console-consumer.sh --bootstrap-server localhost:9092 --topic order_topic --group group_b
   ```
3. 向 `order_topic` 发送一条消息（如 `{"order_id":1, "amount":100}`）。  
4. 观察两个消费者组的输出：**两条消费者都会打印该消息**，证明同一消息被不同组重复消费。  


#### **五、总结**
Kafka 中**不同的消费者组消费同一个主题时，同一条消息会被每个消费者组各自消费一次**。这是因为：  
- 每个消费者组独立维护偏移量（`__consumer_offsets` 中以 `(group.id, topic, partition)` 为键）。  
- 不同消费者组的消费进度互不影响，各自从自己的偏移量位置开始消费。  

这一特性是 Kafka 支持“多订阅者”场景的核心设计，适用于需要数据多副本处理、流量隔离或容灾备份的业务场景。如果需要避免重复消费（如仅允许一个组处理），则应使用**单个消费者组**（组内消费者通过分区分配实现并行消费）。



### `replication-factor`

在 Kafka 中，**`replication-factor`（副本因子）** 是一个核心参数，用于控制**主题分区的冗余程度和数据可靠性**。它决定了每个分区的数据会被复制到多少个 Broker（Kafka 服务节点）上存储。简单来说，副本因子越高，数据的冗余性和容错能力越强，但集群资源消耗也会相应增加。


#### **一、核心定义**
每个 Kafka 主题（Topic）会被划分为多个**分区（Partition）**，而每个分区可以有多个**副本（Replica）**。`replication-factor` 表示每个分区需要创建的副本总数（包括一个 Leader 副本和若干 Follower 副本）。  

例如：  
- 若 `replication-factor=1`，则每个分区只有 1 个副本（即没有 Follower，仅 Leader 自身）。  
- 若 `replication-factor=3`，则每个分区会有 3 个副本（1 个 Leader + 2 个 Follower），这 3 个副本会分布在不同的 Broker 上（Kafka 会尽量均匀分布）。  


#### **二、副本的角色分工**
每个分区的副本中，存在两种角色：  
1. **Leader 副本**：  
   - 负责处理该分区的所有读写请求（生产者发送的消息会写入 Leader，消费者从 Leader 读取消息）。  
   - 是分区的“主”副本，其他副本（Follower）会同步 Leader 的数据。  

2. **Follower 副本**：  
   - 仅负责**被动同步 Leader 的数据**（通过拉取 Leader 的日志并应用到本地）。  
   - 不处理读写请求，但在 Leader 宕机时，会被选举为新的 Leader（保证分区的高可用）。  


#### **三、副本因子的核心作用**
##### 1. **数据冗余与容错**  
副本因子越高，分区的冗余副本越多。当某个 Broker 宕机时，该 Broker 上的分区副本会丢失，但其他 Broker 上的 Follower 副本可以顶替 Leader，避免数据丢失和服务中断。  

- 示例：若 `replication-factor=3`，且集群中有 3 个 Broker，每个分区的 3 个副本分别分布在 3 个 Broker 上。此时若 1 个 Broker 宕机，剩下的 2 个 Broker 上的副本仍可提供服务（其中一个 Follower 会被提升为 Leader）。  
- 若 `replication-factor=1`，且 Broker 宕机，则该 Broker 上的所有分区数据将无法访问（无冗余），需手动恢复。  


##### 2. **提高读取性能（可选）**  
虽然 Follower 副本默认不处理读写请求，但 Kafka 支持通过配置 `replica.lag.time.max.ms` 等参数，允许 Follower 副本提供读服务（需谨慎使用，可能导致数据不一致）。此时，更高的副本因子可以分担 Leader 的读压力。  


##### 3. **平衡集群负载**  
Kafka 会尽量将同一分区的副本均匀分布在不同 Broker 上（例如，若集群有 5 个 Broker，`replication-factor=3`，则每个分区的 3 个副本可能分布在 Broker 1、2、3 或 2、4、5 等组合）。这有助于避免单个 Broker 负载过高（如磁盘、网络压力）。  


#### **四、副本因子 vs 分区数**
需要注意区分两个关键参数：  
- **分区数（Partitions）**：决定主题的并行度（生产者可并行写入不同分区，消费者可通过多实例并行消费不同分区）。  
- **副本因子（Replication Factor）**：决定每个分区的冗余程度（数据可靠性和容错能力）。  

**总副本数** = 分区数 × 副本因子。例如：  
- 若主题有 3 个分区，副本因子为 2，则总共有 3×2=6 个副本（每个分区 2 个副本，分布在不同 Broker 上）。  


#### **五、副本因子的设置建议**
##### 1. **生产环境常用值**  
大多数生产环境会将 `replication-factor` 设置为 **3**（行业默认推荐）。这是因为：  
- 3 副本在容错（允许 1 个 Broker 宕机）和资源消耗（相比 5 副本更节省）之间取得了平衡。  
- 若集群规模较小（如 3 台 Broker），3 副本可覆盖所有 Broker，避免冗余浪费。  


##### 2. **根据集群规模调整**  
- 若集群 Broker 数量较少（如 2 台），副本因子最大只能设为 2（否则无法分布到不同 Broker）。但此时若 1 台 Broker 宕机，剩余 1 台需承载全部负载，仍有风险。  
- 若集群 Broker 数量较多（如 10 台），可适当降低副本因子（如 2），但仍需至少保留 2 副本以应对单 Broker 故障。  


##### 3. **避免过度设置**  
副本因子过高（如 5 或更多）会导致：  
- 集群存储资源消耗激增（总副本数 = 分区数 × 副本因子）。  
- Follower 同步 Leader 数据的网络开销增大（可能影响集群吞吐量）。  


##### 4. **最小可靠性要求**  
若要求“允许最多 N 个 Broker 宕机时仍可用”，则副本因子至少为 `N+1`。例如：  
- 允许 1 个 Broker 宕机 → 副本因子 ≥ 2。  
- 允许 2 个 Broker 宕机 → 副本因子 ≥ 3。  


#### **六、常见问题与注意事项**
1. **副本无法分布到所有 Broker**：  
   Kafka 会尽量将副本均匀分布，但如果 Broker 数量少于副本因子（例如集群只有 2 台 Broker，副本因子设为 3），则无法满足，Kafka 会报警告并仅使用可用 Broker（此时冗余性下降）。  


2. **Follower 同步延迟**：  
   Follower 需与 Leader 保持数据同步（通过 `ISR` 机制维护同步中的副本列表）。若 Follower 长期落后（超过 `replica.lag.time.max.ms`），会被移出 ISR，此时若 Leader 宕机，该 Follower 无法被提升为新 Leader（可能导致数据丢失）。  


3. **副本因子与删除策略**：  
   当删除主题或分区时，所有副本都会被删除（无论副本因子如何设置）。  


#### **总结**  
`replication-factor` 是 Kafka 控制数据可靠性的核心参数，通过增加副本数量提升容错能力，但需在冗余性、资源消耗和性能之间找到平衡。生产环境通常推荐设置为 3，具体需根据集群规模、可靠性要求和性能目标调整。



### `Connector` 连接器是什么呢？

Kafka Connect 连接器（Connector）是 **Apache Kafka 生态中用于实现不同系统与 Kafka 高效数据集成的标准化组件**，是 Kafka Connect 框架的核心模块。它的主要作用是将外部系统（如数据库、消息队列、文件存储、云服务等）的数据与 Kafka 主题进行双向同步，降低数据集成的复杂度。


#### **核心定位：数据管道的“适配器”**
Kafka Connect 本身是一个分布式数据集成框架，而连接器则是这个框架中的“适配器”。它封装了与特定外部系统交互的逻辑（如读取、写入、转换数据），使得用户无需手动编写复杂的客户端代码，即可快速实现 Kafka 与其他系统的数据流转。


#### **连接器的分类**
根据数据流向，连接器可分为两类：

##### 1. **源连接器（Source Connector）**  
从外部系统（如 MySQL、MongoDB、文件系统、Kafka 自身等）**拉取数据并写入 Kafka 主题**。  
**典型场景**：数据库变更捕获（CDC）、日志文件采集、IoT 设备数据上报等。  
**示例**：Debezium 的 MySQL 源连接器（捕获数据库 Binlog）、File Source Connector（读取本地文件）。

##### 2. **汇连接器（Sink Connector）**  
从 Kafka 主题**读取数据并写入外部系统**（如 Elasticsearch、HBase、S3、数据仓库等）。  
**典型场景**：实时数据同步到分析系统、日志归档到对象存储、数据入湖/入仓等。  
**示例**：Elasticsearch Sink Connector（写入 ES 索引）、JDBC Sink Connector（写入关系型数据库）。


#### **连接器的关键特性**
##### 1. **标准化接口，降低集成成本**  
连接器通过 Kafka Connect 定义的 API 实现，统一了数据读写的规范（如偏移量管理、错误重试、并行度控制）。开发者只需关注外部系统的特有逻辑（如数据库的分页查询、文件的格式解析），无需处理 Kafka 底层的主题分区、消费者组等细节。

##### 2. **分布式与可扩展**  
Kafka Connect 支持分布式部署，连接器实例（或其中的任务 Task）可动态扩展。例如，一个源连接器可配置多个任务并行读取不同分片的数据，提升吞吐量；汇连接器也可通过多任务并行写入外部系统，避免单点瓶颈。

##### 3. **配置驱动，声明式管理**  
连接器的行为通过 JSON 配置文件定义（如数据过滤规则、转换逻辑、连接参数），支持通过 REST API 动态创建、修改、删除连接器实例。例如，启动一个 MySQL 源连接器时，只需提交包含数据库 URL、表名、Kafka 主题名的配置，框架会自动管理任务的分配和运行。

##### 4. **容错与一致性**  
连接器内置偏移量跟踪（记录已处理数据的进度），支持故障恢复。例如，若源连接器在拉取数据时宕机，重启后会从上次记录的偏移量继续，避免数据丢失或重复。


#### **连接器的生态与扩展**
Kafka Connect 的灵活性体现在其**可扩展的连接器生态**：  
- **官方连接器**：Kafka 社区提供了一些基础连接器（如文件、JDBC、S3 等），但覆盖范围有限。  
- **第三方连接器**：Confluent Hub、Apache 软件基金会等平台提供了数百个预构建的连接器（如 Debezium 的 CDC 连接器、Salesforce Sink Connector 等），覆盖主流系统。  
- **自定义连接器**：若现有连接器无法满足需求，用户可通过 Kafka Connect API 自行开发，仅需实现 Source 或 Sink 接口即可集成到框架中。


#### **典型应用场景**
- **数据库与 Kafka 同步**：通过 Debezium 源连接器捕获 MySQL/PostgreSQL 的 Binlog，实时同步到 Kafka，供下游流处理（如 Flink）消费。  
- **日志采集到 Kafka**：使用 File Source Connector 读取服务器日志文件，写入 Kafka 后由日志分析系统（如 ELK）消费。  
- **数据入仓/入湖**：通过 JDBC Sink Connector 将 Kafka 中的实时数据写入 Hive 或 ClickHouse，支持离线分析。  


#### **总结**  
Kafka Connect 连接器是 Kafka 生态中“连接外部世界”的关键桥梁，通过标准化、分布式的设计，极大简化了不同系统与 Kafka 的数据集成过程，是企业构建实时数据管道的核心工具。



## 概念 - 什么是 `Confluent` 呢？

>官方网站 `https://www.confluent.io/`。

**Confluent** 是一家专注于**事件流（Event Streaming）**领域的软件公司，由 Apache Kafka 的联合创始人**Jay Kreps**、**Neha Narkhede** 和**Jun Rao**于2014年创立。其核心目标是通过构建**企业级事件流平台**，帮助企业更高效地处理实时数据流，释放数据的实时价值。  

Confluent 的核心产品是基于 Apache Kafka 的**增强型平台**（Confluent Platform）和**全托管云服务**（Confluent Cloud），两者均围绕 Kafka 的核心能力扩展，解决了企业在大规模使用 Kafka 时的痛点（如数据集成、运维复杂度、模式管理等）。


### **核心定位：Kafka 的“企业级增强平台”**
Confluent 并非替代 Apache Kafka，而是**基于 Kafka 构建的完整事件流生态**，通过提供工具、服务和优化，降低企业落地 Kafka 的门槛，覆盖从数据采集、传输、存储到处理、分析的全生命周期。


### **核心产品与服务**
Confluent 的核心产品矩阵包括 **Confluent Platform**（自托管）和 **Confluent Cloud**（云托管），两者共享相同的技术栈，但交付方式不同。


#### **1. Confluent Platform（自托管版）**
面向需要自主控制基础设施的企业，提供开箱即用的 Kafka 增强功能，核心组件包括：

##### （1）**Kafka Connect**  
- **作用**：简化 Kafka 与其他系统（数据库、消息队列、文件系统、云服务等）的**双向数据集成**。  
- **能力**：支持数百种预构建的连接器（Connector，如 MySQL、PostgreSQL、Elasticsearch、S3、Salesforce 等），无需编写代码即可实现数据的实时抽取（Extract）、加载（Load）和同步（Sync）。  
- **典型场景**：将关系型数据库的变更数据捕获（CDC）同步到 Kafka，或从 Kafka 实时写入数据仓库。

##### （2）**Schema Registry**  
- **作用**：管理流数据的**模式（Schema）**，确保生产者和消费者之间的模式兼容性（如 Avro、Protobuf、JSON Schema）。  
- **能力**：支持模式的版本控制、演进策略（如向前/向后兼容）、存储与验证，避免因模式变更导致的系统故障。  
- **典型场景**：微服务间通过 Kafka 传递结构化数据时，确保上下游服务的模式一致性。

##### （3）**ksqlDB（原 KSQL）**  
- **作用**：提供**流 SQL 引擎**，允许用户用 SQL 语句直接处理 Kafka 数据流，无需编写 Java/Scala 代码。  
- **能力**：支持实时聚合（如每分钟统计订单量）、窗口操作（滚动/滑动窗口）、连接（Stream-Stream Join、Stream-Table Join）等复杂流处理逻辑。  
- **典型场景**：实时计算用户行为指标（如页面访问量、跳出率）、监控系统告警规则等。

##### （4）**Confluent Control Center**  
- **作用**：统一的**可视化监控与管理平台**，覆盖 Kafka 集群、连接器、流处理作业的全生命周期管理。  
- **能力**：实时监控集群健康度（如分区负载、延迟、副本状态）、查看连接器运行状态、调试流处理作业、管理 Schema Registry 模式等。  
- **典型场景**：运维团队快速定位集群故障，开发团队调试流处理逻辑。

##### （5）**kafka-rest-proxy**  
- **作用**：提供 RESTful API 接口，允许非 Java 客户端（如 Python、Go、前端应用）通过 HTTP 与 Kafka 交互（生产/消费消息）。  
- **能力**：简化客户端集成，降低非 Java 技术栈团队的使用门槛。  


#### **2. Confluent Cloud（云托管版）**  
面向希望降低运维成本的企业，提供**全托管的 Kafka 云服务**，基于公有云（AWS、GCP、Azure）构建，核心优势包括：  

- **自动化运维**：自动管理集群扩缩容、故障恢复、软件升级，无需人工干预。  
- **无缝集成云服务**：与 AWS S3、GCP BigQuery、Azure Synapse 等云数据服务深度集成，简化数据入湖/入仓流程。  
- **企业级安全**：支持 VPC 网络隔离、加密传输（TLS）、角色权限管理（RBAC）、审计日志等。  
- **弹性计费**：按使用量付费（流量、存储、计算资源），避免资源浪费。  


### **Confluent 与 Apache Kafka 的关系**  
- **底层依赖**：Confluent Platform 基于 Apache Kafka 开源项目构建，完全兼容 Kafka 的 API（Producer/Consumer、Connect、Streams 等）。  
- **增强功能**：Confluent 提供的组件（如 Kafka Connect 企业版、Schema Registry、ksqlDB、Control Center）是对 Kafka 原生能力的补充，解决了开源版本在**企业级场景**中的不足（如大规模集群管理、多租户支持、高级监控）。  
- **技术贡献**：Confluent 团队是 Kafka 社区的核心贡献者，主导了 Kafka 许多功能的设计与优化（如 Kafka Streams、KSQL 的早期开发）。  


### **典型应用场景**  
Confluent 平台适用于需要**实时数据流处理**的企业级场景，常见用例包括：  

#### 1. **实时数据管道（Data Pipeline）**  
通过 Kafka Connect 连接数据库（如 MySQL CDC）、日志系统（如 Elasticsearch）、云存储（如 S3），将分散的数据实时同步到 Kafka，为下游分析、AI 模型训练提供实时数据源。  

#### 2. **事件驱动架构（Event-Driven Architecture）**  
通过 Kafka 作为事件总线，解耦微服务间的通信。例如：用户下单事件触发库存扣减、物流派单、积分发放等多个下游服务的异步处理。  

#### 3. **实时分析与决策**  
结合 ksqlDB 或 Kafka Streams 对实时数据流进行聚合（如实时销售额、用户在线数），将结果输出到 BI 工具（如 Tableau）或数据库，支持业务人员实时决策。  

#### 4. **物联网（IoT）数据处理**  
通过 Kafka 接收海量设备传感器数据（如温度、湿度），利用 Confluent 的高吞吐能力支撑实时流处理，实现设备状态监控、异常预警。  


### **选择 Confluent 的原因**  
企业在选择事件流平台时，通常面临**开源 Kafka 运维复杂**、**缺乏企业级功能**、**跨系统集成困难**等挑战。Confluent 的核心价值在于：  

- **降低复杂度**：通过预构建连接器、可视化控制台、托管服务，减少开发和运维成本。  
- **增强可靠性**：提供企业级 SLA（如 99.95% 可用性）、数据备份与恢复、多租户隔离。  
- **加速创新**：通过流 SQL、模式管理等工具，让企业更聚焦于业务逻辑，而非底层技术实现。  


### **总结**  
Confluent 是**企业级事件流平台的领导者**，通过基于 Kafka 构建的增强工具链（Confluent Platform）和全托管云服务（Confluent Cloud），帮助企业解决实时数据处理中的集成、运维、安全等痛点，是金融、零售、制造、物联网等行业落地实时数字化转型的关键基础设施。



## `Confluent` 镜像版本和 `Kafka`、`Zookeeper` 版本兼容表

`Kafka` 兼容表：

>`https://docs.confluent.io/platform/current/installation/versions-interoperability.html#cp-and-apache-ak-compatibility`

| Confluent Platform | Apache Kafka® | Release Date      | Confluent Community software End of Support | Confluent Enterprise Standard End of Support | Confluent Enterprise Platinum End of Support |
| ------------------ | ------------- | ----------------- | ------------------------------------------- | -------------------------------------------- | -------------------------------------------- |
| 8.0.x              | 4.0.x         | June 11, 2025     | June 11, 2026                               | June 11, 2027                                | June 11, 2028                                |
| 7.9.x              | 3.9.x         | February 19, 2025 | February 19, 2027                           | February 19, 2027                            | February 19, 2028                            |
| 7.8.x              | 3.8.x         | December 2, 2024  | December 2, 2026                            | December 2, 2026                             | December 2, 2027                             |
| 7.7.x              | 3.7.x         | July 26, 2024     | July 26, 2026                               | July 26, 2026                                | July 26, 2027                                |
| 7.6.x              | 3.6.x         | February 9, 2024  | February 9, 2026                            | February 9, 2026                             | February 9, 2027                             |
| 7.5.x              | 3.5.x         | August 25, 2023   | August 25, 2025                             | August 25, 2025                              | August 25, 2026                              |
| 7.4.x              | 3.4.x         | May 3, 2023       | May 3, 2025                                 | May 3, 2025                                  | May 3, 2026                                  |
| 7.3.x              | 3.3.x         | November 4, 2022  | November 4, 2024                            | November 4, 2024                             | November 4, 2025                             |
| 7.2.x              | 3.2.x         | July 6, 2022      | July 6, 2024                                | July 6, 2024                                 | July 6, 2025                                 |

`Zookeeper` 兼容表：

>`https://docs.confluent.io/platform/current/installation/versions-interoperability.html#zk`

| Confluent Platform | Apache ZooKeeper™                        |
| ------------------ | ---------------------------------------- |
| 8.0.x              | Not supported                            |
| 7.9.x              | 3.8.4                                    |
| 7.8.x              | 3.8.4                                    |
| 7.7.x              | 3.5.6 through 3.8.4 (3.8.4 recommended)  |
| 7.6.x              | 3.5.6 through 3.8.3 (3.8.3 recommended)  |
| 7.5.x              | 3.5.6 through 3.8.3 (3.8.3 recommended)  |
| 7.4.x              | 3.4.10 through 3.8.3 (3.8.3 recommended) |
| 7.3.x              | 3.4.10 through 3.8.3 (3.8.3 recommended) |
| 7.2.x              | 3.4.10 through 3.8.3 (3.8.3 recommended) |
| 7.1.x              | 3.4.10 through 3.8.3 (3.8.3 recommended) |



## 配置项 - `KAFKA_ADVERTISED_LISTENERS`

>提示：不能设置为 `127.0.0.1`，否则无法客户端和 `/usr/bin/kafka-topics --create --bootstrap-server kafka1:9092 --replication-factor 1 --partitions 256 --topic my-topic-1` 命令无法连接 `Kafka` 服务。应当设置为宿主机的 `ip` 地址。

在 Apache Kafka 中，`KAFKA_ADVERTISED_LISTENERS` 是一个**关键的网络配置参数**，用于控制 Kafka Broker 向客户端（或其他 Broker）**公布的可连接地址**。它的核心作用是解决“Broker 实际监听地址”与“客户端可访问地址”不一致的问题，尤其在复杂网络环境（如容器化、NAT、多网卡）中至关重要。

### **核心背景：为什么需要 KAFKA_ADVERTISED_LISTENERS？**

Kafka 的通信流程中，客户端（生产者/消费者）需要知道 Broker 的地址才能连接并发送请求。Broker 启动时会绑定一个**实际监听地址**（由 `listeners` 配置），但由于网络环境的复杂性（例如 Broker 部署在容器内、NAT 网关后，或使用内网 IP 但客户端需通过公网访问），客户端可能无法直接访问 Broker 的实际监听地址。此时，`KAFKA_ADVERTISED_LISTENERS` 用于告诉客户端“应该使用哪个地址连接 Broker”，确保客户端能正确建立连接。


### **关键概念对比：listeners vs. advertised.listeners**
| 参数                   | 全称            | 作用                                                         | 默认值                       |
| ---------------------- | --------------- | ------------------------------------------------------------ | ---------------------------- |
| `listeners`            | Broker 监听地址 | Broker 实际绑定的网络地址（用于接收请求），格式为 `协议://主机:端口` | 无（必须显式配置）           |
| `advertised.listeners` | Broker 公布地址 | Broker 向客户端/其他 Broker 公布的连接地址（客户端实际使用的地址） | 等于 `listeners`（未配置时） |


### **核心作用详解**
`KAFKA_ADVERTISED_LISTENERS` 的核心价值是**解耦 Broker 实际监听地址与客户端可访问地址**，常见场景包括：

#### 1. **容器化部署（如 Docker/K8s）**
当 Broker 运行在容器中时，容器内部的网络可能与外部网络隔离（例如容器使用 `localhost` 监听，但外部客户端需通过宿主机的 IP 或域名访问）。此时：
- `listeners` 配置为容器内部的监听地址（如 `PLAINTEXT://0.0.0.0:9092`，`0.0.0.0` 表示监听所有网卡）。
- `advertised.listeners` 配置为宿主机的公网 IP 或域名（如 `PLAINTEXT://kafka-host.example.com:9092`），确保客户端使用该地址连接。

#### 2. **NAT 网络环境**
Broker 部署在内网（如私有云），但客户端位于公网。此时：
- `listeners` 绑定内网 IP（如 `PLAINTEXT://192.168.1.100:9092`）。
- `advertised.listeners` 配置为公网 IP 或域名（如 `PLAINTEXT://kafka-public.example.com:9092`），客户端通过公网地址连接。

#### 3. **多网卡或多地址场景**
Broker 有多个网卡（如内网卡、公网卡），需要根据客户端来源选择不同的暴露地址：
- `listeners` 绑定所有网卡（如 `PLAINTEXT://0.0.0.0:9092`）。
- `advertised.listeners` 按客户端类型区分（如内网客户端用内网 IP，公网客户端用公网 IP）。

#### 4. **集群间通信**
Kafka 集群中的 Broker 需要互相通信（如复制日志、协调分区）。每个 Broker 的 `advertised.listeners` 会被其他 Broker 用作连接地址，因此需确保集群内所有 Broker 能通过该地址互相访问。


### **配置格式与示例**
`KAFKA_ADVERTISED_LISTENERS` 支持**多协议、多地址**配置（逗号分隔），每个地址格式为 `[协议]://[主机名/IP]:[端口]`。常见配置方式如下：

#### 示例 1：单协议单地址（基础场景）
```properties
# Broker 实际监听所有网卡的 9092 端口（PLAINTEXT 协议）
listeners=PLAINTEXT://0.0.0.0:9092

# 向客户端公布公网可访问的地址（如宿主机域名）
advertised.listeners=PLAINTEXT://kafka-broker.example.com:9092
```

#### 示例 2：多协议多地址（混合场景）
```properties
# Broker 实际监听内网 IP 的 PLAINTEXT 和 SSL 端口
listeners=PLAINTEXT://192.168.1.100:9092,SSL://192.168.1.100:9093

# 向公网客户端公布公网地址（SSL 加密），向内网客户端公布内网地址（PLAINTEXT）
advertised.listeners=PLAINTEXT://internal-kafka.example.com:9092,SSL://public-kafka.example.com:9093
```

#### 示例 3：Docker 容器场景
```properties
# 容器内监听所有网卡的 9092 端口（PLAINTEXT）
listeners=PLAINTEXT://0.0.0.0:9092

# 向外部客户端公布宿主机的 IP 和映射端口（假设宿主机将容器 9092 映射到宿主机 9093）
advertised.listeners=PLAINTEXT://host-machine-ip:9093
```


### **注意事项**
1. **协议一致性**：`advertised.listeners` 中每个地址的协议（如 `PLAINTEXT`、`SSL`）必须与 `listeners` 中对应协议的配置一致，否则客户端会因协议不匹配无法连接。
   
2. **主机名解析**：公布的地址（如 `kafka-broker.example.com`）必须能被客户端正确解析为 IP 地址（通过 DNS 或 `/etc/hosts`），否则客户端会连接失败。

3. **端口可见性**：公布的端口需确保客户端网络可达（如防火墙开放该端口）。例如，若 Broker 运行在云服务器上，需在安全组中开放 `9092` 端口。

4. **集群内一致性**：集群中所有 Broker 的 `advertised.listeners` 需满足“客户端能访问任意 Broker”的要求（例如，客户端通过任意 Broker 的地址都能连接到整个集群）。

5. **动态环境适配**：在容器化或弹性伸缩场景中（如 K8s StatefulSet），需结合服务发现机制（如 Headless Service）动态更新 `advertised.listeners`，确保地址随 Pod 重建或扩缩容自动调整。


### **常见错误与排查**
- **客户端连接失败**：检查 `advertised.listeners` 的地址是否可被客户端解析和访问（如 `ping kafka-broker.example.com` 或 `telnet kafka-broker.example.com 9092`）。
- **集群无法同步**：检查 Broker 间是否能通过 `advertised.listeners` 互相访问（如 `telnet <broker2-advertised-ip> <broker2-port>`）。
- **协议不匹配**：查看日志是否有 `Unsupported protocol` 错误，确认 `advertised.listeners` 与 `listeners` 的协议一致。


### **总结**
`KAFKA_ADVERTISED_LISTENERS` 是 Kafka 解决网络寻址问题的核心配置，通过明确“Broker 向客户端公布的连接地址”，确保客户端在不同网络环境下能正确连接到 Broker。其本质是**桥接 Broker 实际监听地址与客户端可访问地址**，是部署 Kafka 集群（尤其是跨网络、容器化场景）时必须重点关注的参数。



## 配置项 - `auto.offset.reset`

在 Spring Kafka 中，配置 `auto.offset.reset`（偏移量重置策略）是通过消费者工厂（`ConsumerFactory`）或 Spring Boot 自动配置属性实现的。该参数控制消费者组在**无已提交偏移量**（如首次启动）时的消费起始位置。以下是详细的配置方法和场景说明：


### **一、核心概念回顾**
`auto.offset.reset` 的取值和含义：
- **`latest`（默认）**：消费者从**当前分区的最新消息之后**开始消费（仅消费启动后新产生的消息）。  
- **`earliest`**：消费者从**分区的最早消息（Offset=0）**开始消费（读取所有历史消息）。  
- **`none`**：若消费者组无已提交偏移量，抛出 `NoOffsetForPartitionException` 异常（需手动处理）。  


### **二、Spring Boot 自动配置（推荐）**
若使用 Spring Boot 自动配置 Kafka 消费者，可直接在 `application.properties` 或 `application.yml` 中设置全局或针对特定消费者组的 `auto.offset.reset`。


#### **1. 全局配置（所有消费者组生效）**
在 `application.properties` 中添加以下配置：
```properties
# application.properties
spring.kafka.consumer.auto-offset-reset=earliest  # 全局设置为 earliest
spring.kafka.consumer.enable-auto-commit=true     # 自动提交偏移量（默认 true）
spring.kafka.consumer.group-id=my-consumer-group  # 消费者组 ID（必填）
```


#### **2. 针对特定消费者组的配置（推荐）**
若需要为不同的消费者组设置不同的 `auto.offset.reset`，可通过 `@KafkaListener` 注解的 `properties` 属性覆盖全局配置。例如：

```java
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class MyKafkaConsumer {

    // 消费者组 ID 为 "specific-group"，并设置 auto-offset-reset=earliest
    @KafkaListener(
        topics = "my-topic",
        groupId = "specific-group",
        properties = {"auto.offset.reset=earliest"}  // 覆盖全局配置
    )
    public void listen(String message) {
        System.out.println("Received: " + message);
    }
}
```


### **三、手动配置 ConsumerFactory（高级场景）**
若需要更细粒度的控制（如动态配置、自定义消费者工厂），可通过 `ConsumerFactory` 手动配置 `auto.offset.reset`。


#### **1. 配置步骤**
1. **定义 `ConsumerFactory` Bean**：通过 `KafkaConsumerFactory` 构建消费者工厂，设置 `auto.offset.reset`。  
2. **关联 `KafkaListenerContainerFactory`**：将消费者工厂关联到监听器容器工厂，供 `@KafkaListener` 使用。  


#### **2. 示例代码**
```java
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaConsumerConfig {

    // 1. 定义 ConsumerFactory
    @Bean
    public ConsumerFactory<String, String> consumerFactory() {
        Map<String, Object> configs = new HashMap<>();
        // 必填配置：Bootstrap Servers、Key/Value 反序列化器
        configs.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        configs.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        configs.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        
        // 设置 auto.offset.reset（关键配置）
        configs.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");  // 或 "latest"
        
        // 可选：消费者组 ID（若不设置，需在 @KafkaListener 中指定）
        configs.put(ConsumerConfig.GROUP_ID_CONFIG, "manual-group");
        
        // 可选：自动提交偏移量（默认 true）
        configs.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, true);
        
        return new DefaultKafkaConsumerFactory<>(configs);
    }

    // 2. 关联 ConsumerFactory 到监听器容器工厂
    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, String> kafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, String> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory());
        // 可选：设置并发消费者数量（默认 1）
        factory.setConcurrency(3);
        return factory;
    }
}
```


### **四、验证配置是否生效**
部署后，可通过以下方式验证 `auto.offset.reset` 是否生效：


#### **1. 查看消费者组偏移量**
使用 `kafka-consumer-groups` 工具查看消费者组的偏移量：
```bash
# 查看消费者组 "my-consumer-group" 的偏移量
kafka-consumer-groups --bootstrap-server localhost:9092 --describe --group my-consumer-group
```
- 若 `CURRENT-OFFSET` 为 `-`（无提交记录），且 `LOG-END-OFFSET` 为历史消息的最大偏移量，说明 `auto.offset.reset=earliest` 生效（消费者从最早消息开始消费）。  


#### **2. 观察消费者日志**
启动消费者后，若日志中出现历史消息的内容，说明 `auto.offset.reset=earliest` 配置成功。  


### **五、注意事项**
1. **消费者组已存在的情况**：  
   若消费者组已提交过偏移量（如之前运行过消费者），`auto.offset.reset` 不会生效，消费者会从**最后一次提交的偏移量**继续消费。此时需手动重置偏移量（见下文）。  

2. **手动重置偏移量（可选）**：  
   若需要让已存在的消费者组重新从历史消息开始消费，可使用 `kafka-consumer-groups` 工具重置偏移量：
   ```bash
   # 将消费者组 "my-consumer-group" 的偏移量重置为最早（earliest）
   kafka-consumer-groups --bootstrap-server localhost:9092 \
     --group my-consumer-group \
     --reset-offsets --to-earliest \
     --execute --topic my-topic
   ```


### **总结**
在 Spring Kafka 中配置 `auto.offset.reset` 的方式包括：
- **Spring Boot 自动配置**：通过 `application.properties` 全局或针对消费者组设置。  
- **手动配置 `ConsumerFactory`**：通过代码显式定义消费者工厂，灵活控制配置。  

核心目标是控制消费者在无已提交偏移量时的消费起始位置，适用于首次启动或需要重置偏移量的场景。



## `Docker Compose` 运行 - `Apache` 镜像

>提示：
>
>- 脚本位于 `/opt/kafka/bin/kafka-topics.sh`
>- 使用 `apache/kafka:4.0.0` 镜像运行，模式为 `KRaft`（不再使用 `zookeeper`）。
>
>[参考官方文档](https://kafka.apache.org/quickstart)
>
>详细用法请参考本站 [示例](https://gitee.com/dexterleslie/demonstration/tree/main/demo-kafka/demo-kafka-connect)

`.env`：

```ini
# 设置为 broker 所在实例的 ip 地址
kafka_advertised_listeners=192.168.1.181

```

`docker-compose.yaml`：

```dockerfile
version: "3.0"

services:
  kafka:
    image: apache/kafka:4.0.0
    ports:
      # 客户端通信端口
      - "9092:9092"
      # 控制器通信端口（KRaft 模式可选，多节点时需要）
      - "9093:9093"
      # 映射 JMX 端口到主机
      # - "9997:9997"
    environment:
      TZ: Asia/Shanghai
      # 节点唯一 ID（KRaft 模式必需，整数）
      KAFKA_BROKER_ID: 1
      # 必需配置：KRaft 模式角色（单节点可设为 broker,controller）
      KAFKA_PROCESS_ROLES: broker,controller
      # 控制器集群投票节点列表（单节点时指向自己，格式：nodeId@host:port）
      KAFKA_CONTROLLER_QUORUM_VOTERS: 1@kafka:9093
      # 声明控制器监听器名称，和下面配置的 CONTROLLER 一致
      KAFKA_CONTROLLER_LISTENER_NAMES: CONTROLLER
      KAFKA_LISTENERS: PLAINTEXT://:9092,CONTROLLER://:9093
      # Kafka Broker 向客户端（或其他 Broker）公布的可连接地址
      KAFKA_ADVERTISED_LISTENERS: PLAINTEXT://${kafka_advertised_listeners}:9092,CONTROLLER://${kafka_advertised_listeners}:9092
      KAFKA_OFFSETS_TOPIC_REPLICATION_FACTOR: 1
      # 设置 Kafka 的 JVM 堆内存
      KAFKA_HEAP_OPTS: "-Xms1g -Xmx1g"
      # 配置 JMX 端口和认证，配置了 jmx 端口才能够被外部工具监控
      # KAFKA_JMX_OPTS: "-Dcom.sun.management.jmxremote -Dcom.sun.management.jmxremote.authenticate=false -Dcom.sun.management.jmxremote.ssl=false -Dcom.sun.management.jmxremote.local.only=false -Dcom.sun.management.jmxremote.port=9997 -Dcom.sun.management.jmxremote.rmi.port=9997"
      # 禁用自动创建 Topic，否则 Spring Boot 会自动创建 partitions=0 的 topic
      KAFKA_AUTO_CREATE_TOPICS_ENABLE: "false"
      # ------------------- 日志清理配置 -------------------
      # 清理策略：启用 delete（按时间/大小删除）
      KAFKA_LOG_CLEANUP_POLICY: "delete"
      # 单个分区最大日志大小：2G（根据磁盘容量调整，如 5GB、20GB）
      KAFKA_LOG_RETENTION_BYTES: "2147483648"  # 设为 -1 表示不限制大小（仅用时间策略）
      # 日志段大小：512MB（更小的段可提升清理精度，但增加文件数）
      KAFKA_LOG_SEGMENT_BYTES: "536870912"  #（原默认 1GB）

```

启动服务：

```sh
docker compose up -d
```

创建主题：

```sh
/opt/kafka/bin/kafka-topics.sh --create --topic quickstart-events --bootstrap-server localhost:9092

# 查看主题信息
/opt/kafka/bin/kafka-topics.sh --describe --topic quickstart-events --bootstrap-server localhost:9092
```

写数据到主题中：

```sh
$ /opt/kafka/bin/kafka-console-producer.sh --topic quickstart-events --bootstrap-server localhost:9092
>1
>2
# 停止 ctrl+c
```

读取主题中的消息：

```sh
$ /opt/kafka/bin/kafka-console-consumer.sh --topic quickstart-events --from-beginning --bootstrap-server localhost:9092
# 停止 ctrl+c
```



## `Docker Compose` 运行 - `Confluent` 镜像

> 提示：脚本位于 `/usr/bin/kafka-topics`。
>
> [参考链接](https://blog.csdn.net/yudaonihaha/article/details/130768061)
>
> 详细用法请参考本站 [示例](https://gitee.com/dexterleslie/demonstration/tree/main/demo-kafka/demo-kafka-benchmark)

`.env`：

```properties
# 设置为 broker 所在实例的 ip 地址
kafka_advertised_listeners=192.168.1.x

```

`docker-compose.yml`：

```yaml
version: "3.8"

services:
  zookeeper:
    image: confluentinc/cp-zookeeper:7.3.0
    environment:
      TZ: Asia/Shanghai
      ZOOKEEPER_CLIENT_PORT: 2181
      ZOOKEEPER_TICK_TIME: 2000
      # todo 设置 ZooKeeper 的 JVM 堆内存
      # ZOOKEEPER_HEAP_OPTS: "-Xms1g -Xmx1g"
    ports:
      - "2181:2181"
  kafka:
    image: confluentinc/cp-kafka:7.3.0
    depends_on:
      - zookeeper
    ports:
      - "9092:9092"
      # 映射 JMX 端口到主机
      - "9997:9997"
    environment:
      TZ: Asia/Shanghai
      KAFKA_BROKER_ID: 1
      KAFKA_ZOOKEEPER_CONNECT: zookeeper:2181
      # Kafka Broker 向客户端（或其他 Broker）公布的可连接地址
      KAFKA_ADVERTISED_LISTENERS: PLAINTEXT://${kafka_advertised_listeners}:9092
      KAFKA_OFFSETS_TOPIC_REPLICATION_FACTOR: 1
      # 设置 Kafka 的 JVM 堆内存
      KAFKA_HEAP_OPTS: "-Xms1g -Xmx1g"
      # 配置 JMX 端口和认证，配置了 jmx 端口才能够被外部工具监控
      KAFKA_JMX_OPTS: "-Dcom.sun.management.jmxremote -Dcom.sun.management.jmxremote.authenticate=false -Dcom.sun.management.jmxremote.ssl=false -Dcom.sun.management.jmxremote.local.only=false -Dcom.sun.management.jmxremote.port=9997 -Dcom.sun.management.jmxremote.rmi.port=9997"
      # 禁用自动创建 Topic，否则 Spring Boot 会自动创建 partitions=0 的 topic
      KAFKA_AUTO_CREATE_TOPICS_ENABLE: "false"
  # 在 kafka 服务成功启动后自动配置 kafka 服务
  kafka-topic-config:
    image: confluentinc/cp-kafka:7.3.0
    depends_on:
      - kafka
    environment:
      TZ: Asia/Shanghai
    # 自动创建 topic
    entrypoint: /usr/bin/kafka-topics --create --bootstrap-server kafka:9092 --replication-factor 1 --partitions 128 --topic my-topic-1
  kafka-manager:
    image: sheepkiller/kafka-manager:latest
    ports:
      - "9000:9000"
    environment:
      TZ: Asia/Shanghai
      ZK_HOSTS: zookeeper:2181
      APPLICATION_SECRET: "123456"

```

启动 `kafka` 服务

```sh
docker compose up -d
```

进入 `kafka` 容器查看服务是否正常运行，下面命令如果没有报错则服务正常运行。

```sh
KAFKA_JMX_OPTS="" /usr/bin/kafka-topics --bootstrap-server localhost:9092 --list
```

如果启动失败查看 `kafka` 启动日志

```sh
docker compose logs -f kafka
```

或者使用生产和消费测试服务是否正常：

-  启动消费端，`--from-beiginning`表示所有消息重新接收一次

  ```sh
  KAFKA_JMX_OPTS="" /usr/bin/kafka-console-consumer --topic topic1 --bootstrap-server localhost:9092 --from-beginning
  ```

- 启动生产端

  ```sh
  KAFKA_JMX_OPTS="" /usr/bin/kafka-console-producer --topic topic1 --bootstrap-server localhost:9092
  ```




## `Connector` 连接器

>[参考官方快速入门](https://kafka.apache.org/quickstart)
>
>详细用法请参考本站 [示例](https://gitee.com/dexterleslie/demonstration/tree/main/demo-kafka/demo-kafka-connect)

启动 `Kafka` 服务：

```sh
docker compose up -d
```

切换到 `/opt/kafka` 目录

```sh
cd /opt/kafka
```

配置 `connect file` 插件路径

```sh
echo "plugin.path=libs/connect-file-4.0.0.jar" >> config/connect-standalone.properties
```

创建测试数据：

```sh
echo -e "foo\nbar" > test.txt
```

启动 `Connector`

```sh
bin/connect-standalone.sh config/connect-standalone.properties config/connect-file-source.properties config/connect-file-sink.properties
```

查看 `Connector sink` 数据：

```sh
cat test.sink.txt
```

消息被存储到主题 `connect-test` 中，查看主题中的消息

```sh
bin/kafka-console-consumer.sh --bootstrap-server localhost:9092 --topic connect-test --from-beginning
```



## 日志保留策略

### 基于大小

`docker-compose.yaml`：

```yaml
version: "3.8"

services:
  zookeeper:
    image: confluentinc/cp-zookeeper:7.3.0
    environment:
      TZ: Asia/Shanghai
      ZOOKEEPER_CLIENT_PORT: 2181
      ZOOKEEPER_TICK_TIME: 2000
      # todo 设置 ZooKeeper 的 JVM 堆内存
      # ZOOKEEPER_HEAP_OPTS: "-Xms1g -Xmx1g"
    ports:
      - "2181:2181"
  kafka:
    image: confluentinc/cp-kafka:7.3.0
    depends_on:
      - zookeeper
    ports:
      - "9092:9092"
      # 映射 JMX 端口到主机
      - "9997:9997"
    environment:
      TZ: Asia/Shanghai
      KAFKA_BROKER_ID: 1
      KAFKA_ZOOKEEPER_CONNECT: zookeeper:2181
      KAFKA_ADVERTISED_LISTENERS: PLAINTEXT://${kafka_advertised_listeners}:9092
      KAFKA_OFFSETS_TOPIC_REPLICATION_FACTOR: 1
      # 设置 Kafka 的 JVM 堆内存
      KAFKA_HEAP_OPTS: "-Xms1g -Xmx1g"
      # 配置 JMX 端口和认证，配置了 jmx 端口才能够被外部工具监控
      KAFKA_JMX_OPTS: "-Dcom.sun.management.jmxremote -Dcom.sun.management.jmxremote.authenticate=false -Dcom.sun.management.jmxremote.ssl=false -Dcom.sun.management.jmxremote.local.only=false -Dcom.sun.management.jmxremote.port=9997 -Dcom.sun.management.jmxremote.rmi.port=9997"
      # 禁用自动创建 Topic，否则 Spring Boot 会自动创建 partitions=0 的 topic
      KAFKA_AUTO_CREATE_TOPICS_ENABLE: "false"
      # ------------------- 日志清理配置 -------------------
      # 清理策略：启用 delete（按时间/大小删除）
      KAFKA_LOG_CLEANUP_POLICY: "delete"
      # 单个分区最大日志大小：2G（根据磁盘容量调整，如 5GB、20GB）
      KAFKA_LOG_RETENTION_BYTES: "2147483648"  # 设为 -1 表示不限制大小（仅用时间策略）
      # 日志段大小：512MB（更小的段可提升清理精度，但增加文件数）
      KAFKA_LOG_SEGMENT_BYTES: "536870912"  #（原默认 1GB）
```

- 保留日志总大小为 `2g`，单个日志文件大小为 `512m`。



### 基于时间

>提醒：暂时没有需求所以不作研究。



## 监控

### `kafka-manager`

>通过 `kafka-manager` 可以查看消息的生成速度，但是不能直接查看消息的速度。

参考下面的 `Docker Compose` 片段配置并运行 `kafka-manager`

```yaml
kafka-manager:
    image: sheepkiller/kafka-manager:latest
    ports:
      - "9000:9000"
    environment:
      TZ: Asia/Shanghai
      ZK_HOSTS: zookeeper:2181
      APPLICATION_SECRET: "123456"
```

访问 `http://192.168.1.x:9000/` 访问并配置 `kafka-manager`：

- 新增 `kafka` 集群

  点击功能 `Cluster` > `Add Cluster`，配置信息如下：

  - `Cluster Name`：随意填写
  - `Cluster Zookeeper Hosts`：填写 `zookeeper` 的 `ip` 地址，例如：`zookeeper:2181`
  - `Kafka Verson`：选择 `0.9.0.1`
  - `Enable JMX Polling`：勾选
  - 其他默认值。

  点击 `Save` 保存。



## 命令

借助本站 [示例](https://gitee.com/dexterleslie/demonstration/tree/main/demo-kafka/demo-kafka-benchmark) 做下面的测试。

列出所有的消费者组

```sh
$ KAFKA_JMX_OPTS="" /usr/bin/kafka-consumer-groups --bootstrap-server localhost:9092 --list
group-topic-test-alter-partitions-online
group-topicBatchSend1
group-topic1
group-topic2
```



查看指定 `group` 的消费者信息

```sh
$ KAFKA_JMX_OPTS="" /usr/bin/kafka-consumer-groups --bootstrap-server localhost:9092 --describe --group group-topic1
# 没有启动消费者时
GROUP           TOPIC           PARTITION  CURRENT-OFFSET  LOG-END-OFFSET  LAG             CONSUMER-ID     HOST            CLIENT-ID
group-topic1    my-topic-1      196        134             153             19              -               -               -
group-topic1    my-topic-1      237        145             167             22              -               -               -
group-topic1    my-topic-1      127        142             159             17              -               -               -
group-topic1    my-topic-1      203        146             161             15              -               -               -
group-topic1    my-topic-1      166        136             150             14              -               -               -

# 启动消费者时
GROUP           TOPIC           PARTITION  CURRENT-OFFSET  LOG-END-OFFSET  LAG             CONSUMER-ID                                                    HOST            CLIENT-ID
group-topic1    my-topic-1      92         170             170             0               consumer-group-topic1-349-504dcafa-5e3d-4b7f-999f-238a2b350e81 /192.168.1.181  consumer-group-topic1-349
group-topic1    my-topic-1      202        178             178             0               consumer-group-topic1-459-5d7aadd1-5823-4122-8219-8dbfcd69f1a4 /192.168.1.181  consumer-group-topic1-459
group-topic1    my-topic-1      214        166             166             0               consumer-group-topic1-471-3316cfcb-2d31-436a-b1e2-bd61b52187f9 /192.168.1.181  consumer-group-topic1-471
group-topic1    my-topic-1      25         167             167             0               consumer-group-topic1-282-7fbb49a3-0755-476d-9c5c-01cd2c603c5b /192.168.1.181  consumer-group-topic1-282
group-topic1    my-topic-1      6          140             140             0               consumer-group-topic1-263-1e049b32-cd1d-453d-8b92-04dc3282821a /192.168.1.181  consumer-group-topic1-263
group-topic1    my-topic-1      192        192             192             0               consumer-group-topic1-449-c68d92ef-ed34-43a3-8a96-4b2fa1e02548 /192.168.1.181  consumer-group-topic1-449
group-topic1    my-topic-1      80         170             170             0               consumer-group-topic1-337-5f1a3ede-1168-496a-87c2-210b8423a3f7 /192.168.1.181  consumer-group-topic1-337
```

- `TOPIC & PARTITION`：主题和分区的编号。
- `CURRENT-OFFSET`：消费者组在这个分区上最后一次提交的偏移量。即消费者当前消费到的位置。
- `LOG-END-OFFSET`：这个分区中最后一条消息的偏移量（即生产的最新消息位置）。
- `LAG`：滞后值。这是最关键的一列，计算公式为：`LAG = LOG-END-OFFSET - CURRENT-OFFSET`。`LAG = 0` 表示消费者已经消费了所有生产的消息，处于“追上”的状态。`LAG > 0`表示消费者落后于生产者，还有消息未消费。这是正常情况。`LAG 持续增长` 可能表示消费者消费速度跟不上生产速度，可能出现了性能问题。
- `CONSUMER-ID, HOST, CLIENT-ID`：当前正在消费该分区的消费者实例信息。



创建名为 `topic1`，分区数为`1`，副本数为`1`的 `topic`：

```sh
# confluent 镜像
KAFKA_JMX_OPTS="" /usr/bin/kafka-topics --create --bootstrap-server localhost:9092 --replication-factor 1 --partitions 1 --topic topic1
```

显示 `topic1` 详情

```sh
# confluent 镜像
KAFKA_JMX_OPTS="" /usr/bin/kafka-topics --describe --bootstrap-server localhost:9092 --topic topic1
```

修改 `topic1` 分区数

```sh
# confluent 镜像
KAFKA_JMX_OPTS="" /usr/bin/kafka-topics --alter --bootstrap-server localhost:9092 --topic topic1 --partitions 128
```

查询所有 `topic` 列表

```sh
# confluent 镜像
KAFKA_JMX_OPTS="" /usr/bin/kafka-topics --bootstrap-server localhost:9092 --list
```

删除 `topic`

```sh
# confluent 镜像
KAFKA_JMX_OPTS="" /usr/bin/kafka-topics --delete --bootstrap-server localhost:9092 --topic topic1
```

读取主题中所有消息

```sh
KAFKA_JMX_OPTS="" /usr/bin/kafka-console-consumer.sh --topic quickstart-events --from-beginning --bootstrap-server localhost:9092
```



## `Spring Boot` 操作 `kafka`

### 集成

> 详细用法请参考本站 [示例](https://gitee.com/dexterleslie/demonstration/tree/main/demo-kafka/demo-kafka-benchmark)

`POM` 配置：

```xml
<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
    xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <parent>
       <groupId>org.springframework.boot</groupId>
       <artifactId>spring-boot-starter-parent</artifactId>
       <version>2.7.18</version>
    </parent>

    <groupId>com.future.demo</groupId>
    <artifactId>demo-kafka-benchmark-parent</artifactId>
    <version>1.0.0</version>
    <packaging>pom</packaging>

    <modules>
       <module>service</module>
       <module>crond</module>
       <module>common</module>
    </modules>

    <properties>
       <maven.compiler.target>1.8</maven.compiler.target>
       <maven.compiler.source>1.8</maven.compiler.source>
    </properties>

    <dependencies>
       <dependency>
          <groupId>org.springframework.boot</groupId>
          <artifactId>spring-boot-starter-web</artifactId>
       </dependency>
       <dependency>
          <groupId>org.springframework.boot</groupId>
          <artifactId>spring-boot-starter-test</artifactId>
          <scope>test</scope>
       </dependency>
       <dependency>
          <groupId>org.projectlombok</groupId>
          <artifactId>lombok</artifactId>
          <version>1.18.8</version>
       </dependency>

       <dependency>
          <groupId>org.springframework.kafka</groupId>
          <artifactId>spring-kafka</artifactId>
       </dependency>

       <dependency>
          <groupId>com.github.dexterleslie1</groupId>
          <artifactId>future-common</artifactId>
          <version>1.2.2</version>
       </dependency>
    </dependencies>

    <repositories>
       <repository>
          <id>aliyun</id>
          <url>https://maven.aliyun.com/repository/public</url>
       </repository>
       <repository>
          <id>jitpack.io</id>
          <url>https://jitpack.io</url>
       </repository>
    </repositories>
</project>
```

`application.properties`：

```properties
spring.kafka.bootstrap-servers=${kafka_bootstrap_servers:localhost}:9092
spring.kafka.consumer.group-id=my-group
# 设置 consumer 每次 poll 返回的最大记录数
spring.kafka.consumer.max-poll-records=1024
```

消费者配置：

```java
@Configuration
@Slf4j
public class Config {
//    @Bean
//    public ConsumerFactory<String, String> consumerFactory() {
//        Map<String, Object> props = new HashMap<>();
//        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092"); // 根据你的 Kafka 服务器地址设置
//        props.put(ConsumerConfig.GROUP_ID_CONFIG, "your-group-id"); // 设置消费者组 ID
//        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer");
//        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer");
//        props.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, 1000); // 设置每次 poll 返回的最大记录数
//        return new DefaultKafkaConsumerFactory<>(props);
//    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, String> kafkaListenerContainerFactory(
            ConsumerFactory consumerFactory) {
        ConcurrentKafkaListenerContainerFactory<String, String> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory);
        // 启用批量消费
        factory.setBatchListener(true);
        // 设置并发线程数，需要设置 topic 分区数不为 0 才能并发消费消息。
        factory.setConcurrency(256);
        return factory;
    }

    private AtomicInteger concurrentCounter = new AtomicInteger();
    private AtomicLong counter = new AtomicLong();

    @KafkaListener(topics = TopicName)
    public void receiveMessage(List<String> messages) throws InterruptedException {
        log.info("concurrent=" + this.concurrentCounter.incrementAndGet() + ",size=" + messages.size() + ",total=" + counter.addAndGet(messages.size()));

        TimeUnit.MILLISECONDS.sleep(500);

        this.concurrentCounter.decrementAndGet();
    }

}
```

生产者配置：

```java
@RestController
@RequestMapping("/api/v1")
public class ApiController {

    @Resource
    private KafkaTemplate<String, String> kafkaTemplate;

    @GetMapping("send")
    public ObjectResponse<String> send() throws ExecutionException, InterruptedException {
        String message = UUID.randomUUID().toString();
        kafkaTemplate.send(TopicName, message).get();
        return ResponseUtils.successObject("消息发送成功");
    }
}
```



### 使用 `KafKaAdmin` 修改 `Topic` 分区数

>提醒：使用 `KafkaAdmin` 修改分区数后 `SpringBoot` 应用没有马上重新平衡消费者线程，依旧使用之前的 `1` 条线程，所以在 `Kafka` 服务启动后，使用 `/usr/bin/kafka-topics --create --bootstrap-server kafka:9092 --replication-factor 1 --partitions 128 --topic my-topic-1` 命令修改 `Topic` 分区数。

```java
@Component
public class TopicInitializer implements CommandLineRunner {

    @Autowired
    private KafkaAdmin kafkaAdmin;

    @Override
    public void run(String... args) {
        NewTopic newTopic = new NewTopic(TopicName, 128, (short) 1);
        kafkaAdmin.createOrModifyTopics(newTopic);
    }
}
```

- 上面的代码修改 `Topic` 的分区数为 `128`，副本数为 `1`。



### 消费失败重试策略配置

>详细用法请参考本站 [示例](https://gitee.com/dexterleslie/demonstration/tree/main/demo-kafka/demo-kafka-benchmark)

#### 概念

要实现Kafka监听器的**无限重试且每次重试间隔5秒**，可以通过Spring Kafka的**RetryTopic**功能（推荐，适用于2.7+版本）或自定义重试策略实现。以下是详细解决方案：

**方案一：使用Spring Kafka RetryTopic（推荐）**

RetryTopic是Spring Kafka 2.7+引入的内置功能，支持自动创建重试主题（Retry Topic）和死信主题（DLQ），并可通过配置实现延迟重试。

1. 前提条件

- Spring Kafka版本 ≥ 2.7.0
- Kafka服务端版本 ≥ 2.4.0（支持延迟消息）

2. 配置步骤

（1）添加依赖

确保`pom.xml`包含Spring Kafka依赖（版本≥2.7.0）：
```xml
<dependency>
    <groupId>org.springframework.kafka</groupId>
    <artifactId>spring-kafka</artifactId>
    <version>2.9.11</version> <!-- 或更高版本 -->
</dependency>
```

（2）配置RetryTopic策略

通过`RetryTopicConfiguration`定义重试规则，包括无限重试、5秒间隔等。

```java
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.util.backoff.FixedBackOff;

import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableKafka
public class KafkaConfig {

    // Kafka基础配置（根据实际情况调整）
    @Bean
    public ConsumerFactory<String, String> consumerFactory() {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "your-consumer-group");
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest"); // 从最早偏移量开始消费
        return new DefaultKafkaConsumerFactory<>(props);
    }

    // 监听器容器工厂（关键配置）
    @Bean
    public ConcurrentKafkaListenerContainerFactory<?, ?> kafkaListenerContainerFactory(
            ConsumerFactory<Object, Object> consumerFactory,
            KafkaTemplate<Object, Object> kafkaTemplate) {

        ConcurrentKafkaListenerContainerFactory<Object, Object> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory);
        factory.setCommonErrorHandler(retryErrorHandler(kafkaTemplate)); // 绑定重试错误处理器
        return factory;
    }

    // 定义重试错误处理器（核心）
    @Bean
    public DefaultErrorHandler retryErrorHandler(KafkaTemplate<Object, Object> template) {
        // 配置重试策略：无限次重试，每次间隔5秒
        FixedBackOff fixedBackOff = new FixedBackOff(5000L, -1); // 5000ms间隔，-1表示无限次

        // 使用RetryTopic的ErrorHandler（自动处理重试和DLQ）
        return new DefaultErrorHandler(
                // 自定义恢复逻辑（可选，当重试耗尽时触发）
                (record, ex) -> {
                    System.err.println("重试耗尽，消息进入死信队列：" + record.value());
                },
                fixedBackOff
        );
    }
}
```

（3）监听器方法调整

确保监听器方法正确声明，并使用`@KafkaListener`绑定主题。  
**注意**：若使用批量消费（`List<String>`），需额外配置批量处理的重试逻辑（见下文补充说明）。

```java
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import java.util.List;

@Component
public class KafkaListenerDemo {

    private int concurrentCounter = 0;
    private int totalCounter = 0;

    @KafkaListener(topics = "your-topic-name") // 替换为实际主题
    public void receiveMessage(List<String> messages) throws Exception {
        concurrentCounter++;
        totalCounter += messages.size();
        log.info("concurrent={}, size={}, total={}", concurrentCounter, messages.size(), totalCounter);

        // 模拟耗时操作
        TimeUnit.MILLISECONDS.sleep(500);

        // 模拟业务异常（触发重试）
        boolean b = true;
        if (b) {
            throw new BusinessException("测试异常"); // 自定义异常
        }

        concurrentCounter--;
        // 手动提交偏移量（若使用MANUAL_ACK模式）
        // acknowledgment.acknowledge();
    }
}
```

3. 关键配置说明

- **无限重试**：通过`SimpleRetryPolicy(-1)`设置重试次数为-1（无限）。
- **5秒间隔**：通过`FixedBackOff(5000L)`设置每次重试间隔5秒。
- **偏移量提交**：推荐使用`MANUAL_IMMEDIATE`手动提交，避免因处理失败导致偏移量提前提交（需在消息处理成功后调用`acknowledgment.acknowledge()`）。
- **死信队列（DLQ）**：当重试耗尽（理论上无限重试不会触发），消息会被发送到`${原主题}.dlq`，可单独处理。

**方案二：自定义重试逻辑（旧版本兼容）**

若Spring Kafka版本低于2.7，可通过`SeekToCurrentErrorHandler`结合延迟任务实现重试。

1. 核心思路

- 捕获消费异常后，通过`SeekToCurrentErrorHandler`将偏移量重置到当前位置（实现重试）。
- 结合`ScheduledExecutorService`延迟5秒后重新消费。

2. 实现代码

```java
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.util.backoff.FixedBackOff;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Component
public class CustomErrorHandler extends DefaultErrorHandler {

    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    @Override
    public void handle(Exception thrownException, List<ConsumerRecord<?, ?>> records,
                       Consumer<?, ?> consumer, MessageListenerContainer<?, ?> container) {

        // 记录异常
        log.error("处理消息失败，触发重试", thrownException);

        // 延迟5秒后重新消费当前记录
        for (ConsumerRecord<?, ?> record : records) {
            scheduler.schedule(() -> {
                try {
                    // 重新分配分区并定位到当前偏移量（手动触发重试）
                    consumer.seek(new TopicPartition(record.topic(), record.partition()), record.offset());
                    // 提交当前批次的偏移量（避免重复消费）
                    ((Acknowledgment) container).acknowledge();
                } catch (Exception e) {
                    log.error("重试任务执行失败", e);
                }
            }, 5, TimeUnit.SECONDS);
        }
    }
}
```

3. 监听器绑定自定义错误处理器

```java
@KafkaListener(topics = "your-topic-name", errorHandler = "customErrorHandler")
public void receiveMessage(List<String> messages) throws Exception {
    // 业务逻辑...
}
```

**补充说明（批量消费场景）**

若监听器使用批量消费（`List<String>`），需注意：
1. **部分失败处理**：默认情况下，批量消费中一条消息失败会导致整个批次重试。若需单独处理失败消息，需：
   - 设置`ackMode=MANUAL`，手动确认每条消息。
   - 在异常时仅将失败消息加入重试队列，成功消息立即提交。
2. **重试间隔**：批量重试时，所有消息会一起延迟重试，无法单独设置每条消息的间隔（如需精细控制，需拆分批量为单条消费）。

**总结**

推荐使用**Spring Kafka RetryTopic**（版本≥2.7），配置简单且功能完善，支持无限重试和延迟间隔。若版本不兼容，可通过自定义`ErrorHandler`结合延迟任务实现，但需注意批量消费的边界条件。



#### 实验

`ConcurrentKafkaListenerContainerFactory` 配置如下：

```java
@Bean
public ConcurrentKafkaListenerContainerFactory<String, String> kafkaListenerContainerFactory(
        ConsumerFactory consumerFactory) {
    ConcurrentKafkaListenerContainerFactory<String, String> factory = new ConcurrentKafkaListenerContainerFactory<>();
    factory.setConsumerFactory(consumerFactory);
    // 启用批量消费
    factory.setBatchListener(true);
    // 设置并发线程数，需要设置 topic 分区数不为 0 才能并发消费消息。
    factory.setConcurrency(256);
    // 绑定重试错误处理器
    factory.setCommonErrorHandler(retryErrorHandler(kafkaTemplate));
    return factory;
}
```

定义重试错误处理器（核心）：

```java
// 定义重试错误处理器（核心）
@Bean
public DefaultErrorHandler retryErrorHandler(KafkaTemplate<Object, Object> template) {
    // 配置重试策略：无限次重试，每次间隔5秒
    // 5000ms间隔，FixedBackOff.UNLIMITED_ATTEMPTS 表示无限次
    FixedBackOff fixedBackOff = new FixedBackOff(5000L, /*FixedBackOff.UNLIMITED_ATTEMPTS*/ 180);

    // 使用RetryTopic的ErrorHandler（自动处理重试和DLQ）
    return new DefaultErrorHandler(
            // 自定义恢复逻辑（可选，当重试耗尽时触发）
            (record, ex) -> {
                log.error("重试耗尽，消息进入死信队列：{}", record.value());
            },
            fixedBackOff
    );
}
```



### 队列模型

>详细用法请参考本站 [示例](https://gitee.com/dexterleslie/demonstration/tree/main/demo-kafka/demo-kafka-benchmark)

通过在 `application.properties` 中配置同一个 `groupId` 即可实现队列模型。如下：

```properties
spring.kafka.consumer.group-id=my-group
```



### 发布订阅模型

>详细用法请参考本站 [示例](https://gitee.com/dexterleslie/demonstration/tree/main/demo-kafka/demo-kafka-benchmark)

通过在 `application.properties` 中配置不同的 `groupId` 即可实现发布订阅模型。如下：

```properties
spring.kafka.consumer.group-id=${random.uuid}
```



### 性能测试

>详细用法请参考本站 [示例](https://gitee.com/dexterleslie/demonstration/tree/main/demo-kafka/demo-kafka-benchmark)

提醒：实例的内存和 `CPU` 充足。

部署和运行应用：

```sh
# 复制配置
ansible-playbook playbook-deployer-config.yml --inventory inventory.ini

# 编译并推送镜像
./build.sh && ./push.sh

# 运行应用
ansible-playbook playbook-service-start.yml --inventory inventory.ini
```

测试 `kafka` 消息发送速度：

```sh
wrk -t8 -c2048 -d30s --latency --timeout 60 http://192.168.1.185/api/v1/sendToTopic1
Running 30s test @ http://192.168.1.185/api/v1/sendToTopic1
  8 threads and 2048 connections
  Thread Stats   Avg      Stdev     Max   +/- Stdev
    Latency    16.18ms    8.31ms 247.86ms   92.04%
    Req/Sec    16.32k     1.90k   30.68k    86.42%
  Latency Distribution
     50%   14.74ms
     75%   17.64ms
     90%   22.39ms
     99%   40.23ms
  3896189 requests in 30.09s, 0.92GB read
Requests/sec: 129496.55
Transfer/sec:     31.37MB
```

删除应用：

```sh
ansible-playbook playbook-service-destroy.yml --inventory inventory.ini
```



### 各个消费者配置独立

#### 介绍

要让不同的Kafka主题（Topic1和Topic2）使用不同的`max-poll-records`配置，需要为每个主题创建独立的**消费者工厂（ConsumerFactory）**和**监听器容器工厂（ConcurrentKafkaListenerContainerFactory）**，并通过`@KafkaListener`的`containerFactory`属性指定对应的容器工厂。以下是具体实现步骤：


##### 步骤1：显式定义两个独立的ConsumerFactory
在配置类中定义两个`ConsumerFactory` Bean，分别针对Topic1和Topic2设置不同的`max-poll-records`及其他必要配置（如Bootstrap Server、反序列化器等）。

```java
@Configuration
@Slf4j
public class Config {
    @Resource
    KafkaTemplate kafkaTemplate;

    // -------------------- 为Topic1定制的ConsumerFactory --------------------
    @Bean("topic1ConsumerFactory")
    public ConsumerFactory<String, String> topic1ConsumerFactory() {
        Map<String, Object> props = new HashMap<>();
        // 从application.properties中获取Bootstrap Server（兼容原有配置）
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "${kafka_bootstrap_servers:localhost}:9092");
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "group-topic1"); // 建议为不同主题设置不同Group ID（可选）
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        // 为Topic1单独设置max-poll-records（示例值200）
        props.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, 200); 
        
        // 其他通用配置（可选）
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        return new DefaultKafkaConsumerFactory<>(props);
    }

    // -------------------- 为Topic2定制的ConsumerFactory --------------------
    @Bean("topic2ConsumerFactory")
    public ConsumerFactory<String, String> topic2ConsumerFactory() {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "${kafka_bootstrap_servers:localhost}:9092");
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "group-topic2"); // 不同主题可设置不同Group ID
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        // 为Topic2单独设置max-poll-records（示例值500）
        props.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, 500); 
        
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        return new DefaultKafkaConsumerFactory<>(props);
    }

    // -------------------- 为Topic1定制的监听器容器工厂 --------------------
    @Bean("topic1KafkaListenerContainerFactory")
    public ConcurrentKafkaListenerContainerFactory<String, String> topic1KafkaListenerContainerFactory(
            @Qualifier("topic1ConsumerFactory") ConsumerFactory<String, String> consumerFactory) {
        ConcurrentKafkaListenerContainerFactory<String, String> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory);
        factory.setBatchListener(true); // 启用批量消费
        factory.setConcurrency(256); // 并发线程数（根据分区数调整）
        factory.setCommonErrorHandler(retryErrorHandler(kafkaTemplate)); // 复用错误处理器
        return factory;
    }

    // -------------------- 为Topic2定制的监听器容器工厂 --------------------
    @Bean("topic2KafkaListenerContainerFactory")
    public ConcurrentKafkaListenerContainerFactory<String, String> topic2KafkaListenerContainerFactory(
            @Qualifier("topic2ConsumerFactory") ConsumerFactory<String, String> consumerFactory) {
        ConcurrentKafkaListenerContainerFactory<String, String> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory);
        factory.setBatchListener(true);
        factory.setConcurrency(256);
        factory.setCommonErrorHandler(retryErrorHandler(kafkaTemplate));
        return factory;
    }

    // 错误处理器（保持原有逻辑）
    @Bean
    public DefaultErrorHandler retryErrorHandler(KafkaTemplate<Object, Object> template) {
        FixedBackOff fixedBackOff = new FixedBackOff(5000L, 180); // 5秒间隔，最多重试180次
        return new DefaultErrorHandler(
                (record, ex) -> log.error("重试耗尽，消息进入死信队列：{}", record.value()),
                fixedBackOff
        );
    }

    // 原有监听器方法（通过containerFactory指定对应工厂）
    private AtomicInteger concurrentCounter = new AtomicInteger();
    private AtomicLong counter = new AtomicLong();
    @KafkaListener(topics = Constant.Topic1, containerFactory = "topic1KafkaListenerContainerFactory")
    public void receiveMessageFromTopic1(List<String> messages) throws Exception {
        try {
            log.info("concurrent={},size={},total={}", 
                this.concurrentCounter.incrementAndGet(), 
                messages.size(), 
                counter.addAndGet(messages.size()));
            TimeUnit.MILLISECONDS.sleep(1500);
        } finally {
            this.concurrentCounter.decrementAndGet();
        }
    }

    private AtomicInteger concurrentCount2 = new AtomicInteger();
    private AtomicLong counter2 = new AtomicLong();
    @KafkaListener(topics = Constant.Topic2, containerFactory = "topic2KafkaListenerContainerFactory")
    public void receiveMessageFromTopic2(List<String> messages) throws Exception {
        try {
            log.info("concurrent={},size={},total={}", 
                this.concurrentCount2.incrementAndGet(), 
                messages.size(), 
                counter2.addAndGet(messages.size()));
            TimeUnit.MILLISECONDS.sleep(1500);
        } finally {
            this.concurrentCount2.decrementAndGet();
        }
    }
}
```


##### 关键说明
1. **独立ConsumerFactory**：  
   通过`@Bean("topic1ConsumerFactory")`和`@Bean("topic2ConsumerFactory")`定义两个不同的消费者工厂，分别设置各自主题的`MAX_POLL_RECORDS_CONFIG`（如Topic1设为200，Topic2设为500）。

2. **独立监听器容器工厂**：  
   每个监听器容器工厂（`topic1KafkaListenerContainerFactory`和`topic2KafkaListenerContainerFactory`）绑定对应的消费者工厂，确保使用独立的配置。

3. **通过`containerFactory`指定容器工厂**：  
   在`@KafkaListener`注解中，通过`containerFactory = "topic1KafkaListenerContainerFactory"`（或`topic2`）明确指定使用哪个容器工厂，从而应用对应的`max-poll-records`。


##### 验证效果
启动应用后，观察日志或通过Kafka监控工具（如Kafka Manager）验证：
- Topic1的消费者在`poll`时每次最多拉取200条消息（`max.poll.records=200`）。
- Topic2的消费者每次最多拉取500条消息（`max.poll.records=500`）。


##### 注意事项
- **消费者组ID（Group ID）**：  
  如果Topic1和Topic2需要独立消费（不共享偏移量），建议为它们设置不同的`GROUP_ID_CONFIG`（如示例中的`group-topic1`和`group-topic2`）。如果需要共享消费进度，可使用相同的Group ID。

- **并发数与分区数**：  
  `setConcurrency(256)`需确保主题的分区数不少于并发数，否则多余的线程会空闲。例如，若Topic1只有10个分区，`concurrency`应设置为≤10。

- **错误处理与重试**：  
  示例中复用了原有的`retryErrorHandler`，确保两个主题的消费者使用相同的重试和死信队列逻辑（可根据需要差异化配置）。

通过以上配置，Topic1和Topic2的消费者将使用独立的`max-poll-records`参数，满足差异化需求。



#### 实验

>详细用法请参考本站 [示例](https://gitee.com/dexterleslie/demonstration/tree/main/demo-kafka/demo-kafka-benchmark)

`ConfigKafkaListenerContainerFactory.java` 分别定义一个默认的配置和一个专门用于 `Topic1` 的配置：

```java
@Configuration
@Slf4j
public class ConfigKafkaListenerContainerFactory {
    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

    @Bean("defaultConsumerFactory")
    public ConsumerFactory<String, String> defaultConsumerFactory() {
        Map<String, Object> props = new HashMap<>();
        // 从application.properties中获取Bootstrap Server（兼容原有配置）
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "group-topic1"); // 建议为不同主题设置不同Group ID（可选）
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        // 为Topic2单独设置max-poll-records
        props.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, 1024);
        return new DefaultKafkaConsumerFactory<>(props);
    }

    @Bean("defaultKafkaListenerContainerFactory")
    public ConcurrentKafkaListenerContainerFactory<String, String> defaultKafkaListenerContainerFactory(
            @Qualifier("defaultConsumerFactory") ConsumerFactory<String, String> consumerFactory,
            @Autowired DefaultErrorHandler retryErrorHandler) {
        ConcurrentKafkaListenerContainerFactory<String, String> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory);
        factory.setBatchListener(true);
        factory.setConcurrency(256);
        factory.setCommonErrorHandler(retryErrorHandler);
        return factory;
    }

    @Bean("topic2ConsumerFactory")
    public ConsumerFactory<String, String> topic2ConsumerFactory() {
        Map<String, Object> props = new HashMap<>();
        // 从application.properties中获取Bootstrap Server（兼容原有配置）
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "group-topic2"); // 建议为不同主题设置不同Group ID（可选）
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        // 为Topic2单独设置max-poll-records
        props.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, 128);
        return new DefaultKafkaConsumerFactory<>(props);
    }

    @Bean("topic2KafkaListenerContainerFactory")
    public ConcurrentKafkaListenerContainerFactory<String, String> topic2KafkaListenerContainerFactory(
            @Qualifier("topic2ConsumerFactory") ConsumerFactory<String, String> consumerFactory,
            @Autowired DefaultErrorHandler retryErrorHandler) {
        ConcurrentKafkaListenerContainerFactory<String, String> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory);
        factory.setBatchListener(true);
        factory.setConcurrency(256);
        factory.setCommonErrorHandler(retryErrorHandler);
        return factory;
    }

    // 定义重试错误处理器（核心）
    @Bean
    public DefaultErrorHandler retryErrorHandler() {
        // 配置重试策略：无限次重试，每次间隔5秒
        // 5000ms间隔，FixedBackOff.UNLIMITED_ATTEMPTS 表示无限次
        FixedBackOff fixedBackOff = new FixedBackOff(5000L, /*FixedBackOff.UNLIMITED_ATTEMPTS*/ 180);

        // 使用RetryTopic的ErrorHandler（自动处理重试和DLQ）
        return new DefaultErrorHandler(
                // 自定义恢复逻辑（可选，当重试耗尽时触发）
                (record, ex) -> {
                    log.error("重试耗尽，消息进入死信队列：{}", record.value());
                },
                fixedBackOff
        );
    }
}

```

`ConfigKafkaListener.java` 分别引用不同的配置：

```java
@Configuration
@Slf4j
public class ConfigKafkaListener {

    /*@Bean
    public ConcurrentKafkaListenerContainerFactory<String, String> kafkaListenerContainerFactory(
            ConsumerFactory consumerFactory,
            @Autowired DefaultErrorHandler retryErrorHandler) {
        ConcurrentKafkaListenerContainerFactory<String, String> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory);
        // 启用批量消费
        factory.setBatchListener(true);
        // 设置并发线程数，需要设置 topic 分区数不为 0 才能并发消费消息。
        factory.setConcurrency(256);
        // 绑定重试错误处理器
        factory.setCommonErrorHandler(retryErrorHandler);
        return factory;
    }*/

    private AtomicInteger concurrentCounter = new AtomicInteger();
    private AtomicLong counter = new AtomicLong();

    @KafkaListener(topics = Constant.Topic1, containerFactory = "defaultKafkaListenerContainerFactory")
    public void receiveMessageFromTopic1(List<String> messages) throws Exception {
        try {
            log.info("concurrent=" + this.concurrentCounter.incrementAndGet()
                    + ",size=" + messages.size()
                    + ",total=" + counter.addAndGet(messages.size()));

            TimeUnit.MILLISECONDS.sleep(1500);

            // 辅助测试失败重试
            /*boolean b = true;
            if (b) {
                throw new BusinessException("测试异常");
            }*/
        } finally {
            this.concurrentCounter.decrementAndGet();
        }
    }

    private AtomicInteger concurrentCount2 = new AtomicInteger();
    private AtomicLong counter2 = new AtomicLong();

    @KafkaListener(topics = Constant.Topic2, concurrency = "2", containerFactory = "topic2KafkaListenerContainerFactory")
    public void receiveMessageFromTopic2(List<String> messages) throws Exception {
        try {
            log.info("concurrent=" + this.concurrentCount2.incrementAndGet()
                    + ",size=" + messages.size()
                    + ",total=" + counter2.addAndGet(messages.size()));
        } finally {
            this.concurrentCount2.decrementAndGet();
        }
    }
}

```

测试 `Topic1` 的配置是否生效：

```sh
wrk -t8 -c2048 -d30s --latency --timeout 60 http://192.168.1.185/api/v1/sendToTopic1

# 查看 crond 的日志输出如下表示配置已经生效
crond-service-1  | 2025-07-19 10:10:56.569  INFO 7 --- [ainer#0-247-C-1] c.f.demo.config.ConfigKafkaListener      : concurrent=245,size=1024,total=3093694
crond-service-1  | 2025-07-19 10:10:56.569  INFO 7 --- [tainer#0-22-C-1] c.f.demo.config.ConfigKafkaListener      : concurrent=246,size=1008,total=3094702
crond-service-1  | 2025-07-19 10:10:56.569  INFO 7 --- [ainer#0-158-C-1] c.f.demo.config.ConfigKafkaListener      : concurrent=247,size=353,total=3095055
crond-service-1  | 2025-07-19 10:10:56.569  INFO 7 --- [ainer#0-121-C-1] c.f.demo.config.ConfigKafkaListener      : concurrent=248,size=1024,total=3096079
crond-service-1  | 2025-07-19 10:10:56.570  INFO 7 --- [tainer#0-26-C-1] c.f.demo.config.ConfigKafkaListener      : concurrent=249,size=1024,total=3097103
crond-service-1  | 2025-07-19 10:10:56.570  INFO 7 --- [tainer#0-14-C-1] c.f.demo.config.ConfigKafkaListener      : concurrent=250,size=1024,total=3098127
crond-service-1  | 2025-07-19 10:10:56.570  INFO 7 --- [ntainer#0-8-C-1] c.f.demo.config.ConfigKafkaListener      : concurrent=251,size=1003,total=3099130
crond-service-1  | 2025-07-19 10:10:56.570  INFO 7 --- [ainer#0-177-C-1] c.f.demo.config.ConfigKafkaListener      : concurrent=252,size=1024,total=3100154
crond-service-1  | 2025-07-19 10:10:56.570  INFO 7 --- [tainer#0-48-C-1] c.f.demo.config.ConfigKafkaListener      : concurrent=253,size=616,total=3100770
crond-service-1  | 2025-07-19 10:10:56.570  INFO 7 --- [ainer#0-246-C-1] c.f.demo.config.ConfigKafkaListener      : concurrent=254,size=1024,total=3101794
crond-service-1  | 2025-07-19 10:10:56.570  INFO 7 --- [ainer#0-211-C-1] c.f.demo.config.ConfigKafkaListener      : concurrent=255,size=718,total=3102512
crond-service-1  | 2025-07-19 10:10:56.571  INFO 7 --- [ainer#0-244-C-1] c.f.demo.config.ConfigKafkaListener      : concurrent=256,size=1024,total=3103536
```

测试 `Topic2` 的配置是否生效：

```sh
wrk -t8 -c2048 -d3000000000s --latency --timeout 60 http://192.168.1.185/api/v1/sendToTopic2

# 查看 crond 的日志输出如下表示配置已经生效
crond-service-1  | 2025-07-19 10:12:04.501  INFO 7 --- [ntainer#1-0-C-1] c.f.demo.config.ConfigKafkaListener      : concurrent=1,size=104,total=2829264
crond-service-1  | 2025-07-19 10:12:04.501  INFO 7 --- [ntainer#1-1-C-1] c.f.demo.config.ConfigKafkaListener      : concurrent=1,size=36,total=2829300
crond-service-1  | 2025-07-19 10:12:04.502  INFO 7 --- [ntainer#1-0-C-1] c.f.demo.config.ConfigKafkaListener      : concurrent=1,size=128,total=2829428
crond-service-1  | 2025-07-19 10:12:04.502  INFO 7 --- [ntainer#1-1-C-1] c.f.demo.config.ConfigKafkaListener      : concurrent=1,size=48,total=2829476
crond-service-1  | 2025-07-19 10:12:04.502  INFO 7 --- [ntainer#1-0-C-1] c.f.demo.config.ConfigKafkaListener      : concurrent=2,size=49,total=2829525
crond-service-1  | 2025-07-19 10:12:04.503  INFO 7 --- [ntainer#1-1-C-1] c.f.demo.config.ConfigKafkaListener      : concurrent=1,size=11,total=2829536
crond-service-1  | 2025-07-19 10:12:04.503  INFO 7 --- [ntainer#1-0-C-1] c.f.demo.config.ConfigKafkaListener      : concurrent=1,size=11,total=2829547
crond-service-1  | 2025-07-19 10:12:04.503  INFO 7 --- [ntainer#1-1-C-1] c.f.demo.config.ConfigKafkaListener      : concurrent=1,size=31,total=2829578
crond-service-1  | 2025-07-19 10:12:04.503  INFO 7 --- [ntainer#1-0-C-1] c.f.demo.config.ConfigKafkaListener      : concurrent=1,size=5,total=2829583
crond-service-1  | 2025-07-19 10:12:04.504  INFO 7 --- [ntainer#1-1-C-1] c.f.demo.config.ConfigKafkaListener      : concurrent=1,size=5,total=2829588
```



### 高效率发送消息

>详细用法请参考本站 [示例](https://gitee.com/dexterleslie/demonstration/tree/main/demo-kafka/demo-kafka-benchmark)

```java
@SpringBootTest(classes = {ApplicationService.class})
@Slf4j
public class ApplicationTests {

    @Resource
    private KafkaTemplate<String, String> kafkaTemplate;
    @Resource
    ConfigKafkaListener configKafkaListener;

    @Test
    public void contextLoads() throws InterruptedException {
        // 测试异步发送消息以提高消息发送效率

        int totalMessageCount = 1000000;
        int concurrentThreads = 32;
        ExecutorService threadPool = Executors.newCachedThreadPool();
        AtomicInteger counter = new AtomicInteger();
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        for (int i = 0; i < concurrentThreads; i++) {
            threadPool.submit(() -> {
                int count;
                List<ListenableFuture<SendResult<String, String>>> futureList = new ArrayList<>();
                while ((count = counter.getAndIncrement()) <= totalMessageCount) {
                    ListenableFuture<SendResult<String, String>> future =
                            kafkaTemplate.send(Constant.TopicTestSendPerf, String.valueOf(count));

                    // 发送效率低，发送一个消息等待一个消息发送结果响应
                    /*try {
                        future.get();
                    } catch (Exception e) {
                        log.error(e.getMessage(), e);
                        throw new RuntimeException(e);
                    }*/

                    // 发送效率高，发送多个消息后异步等待多个消息发送结果响应
                    futureList.add(future);
                    if (futureList.size() >= 1024 || count >= totalMessageCount) {
                        for (ListenableFuture<SendResult<String, String>> futureInternal : futureList) {
                            try {
                                futureInternal.get();
                            } catch (Exception e) {
                                log.error(e.getMessage(), e);
                                throw new RuntimeException(e);
                            }
                        }
                        futureList = new ArrayList<>();
                    }
                }
            });
        }
        threadPool.shutdown();
        while (!threadPool.awaitTermination(10, TimeUnit.MILLISECONDS)) ;
        stopWatch.stop();
        log.info("发送 {} 个消息耗时 {} 毫秒", totalMessageCount, stopWatch.getTotalTimeMillis());

        // 等待完成消费所有消息
        TimeUnit.SECONDS.sleep(2);
        Assertions.assertTrue(configKafkaListener.List.size() >= totalMessageCount, "接收到的消息个数为 " + configKafkaListener.List.size());

        // endregion
    }

}

```

```java
@Configuration
@Slf4j
public class ConfigKafkaListener {

    public List<String> List = new ArrayList<>();

    @KafkaListener(topics = Constant.TopicTestSendPerf, containerFactory = "defaultKafkaListenerContainerFactory")
    public void receiveMessageFromTopicBatchSend1(List<String> messages) {
        for (String message : messages) {
            List.add(message + ":" + Constant.TopicTestSendPerf);
        }
    }
}
```

测试结果：发送 `1000000` 个消息耗时 `3077` 毫秒。



### 消费并发线程数和主题的分区数关系

借助本站 [示例](https://gitee.com/dexterleslie/demonstration/tree/main/demo-kafka/demo-kafka-benchmark) 协助测试。

使用下面命令协助生成测试数据：

```sh
ab -n 100000 -c 8 -k http://192.168.1.181:8080/api/v1/sendToTopic1
```



#### 当消费线程数大于主题分区数时

消费者端的最大消费线程数等于主题的分区数，不会漏消息。



#### 当消费线程数小于主题分区数时

消费者端的最大消费线程数等于配置的最大消费线程数，不会漏消息。



## 持久化容器数据



### `confluentinc/cp-kafka` 镜像持久化

`docker-compose.yaml`：

```yaml
version: "3.8"

services:
  kafka:
    image: confluentinc/cp-kafka:7.3.0
    depends_on:
      - zookeeper
    ports:
      - "9092:9092"
    environment:
      KAFKA_BROKER_ID: 1
      KAFKA_ZOOKEEPER_CONNECT: zookeeper:2181
      KAFKA_ADVERTISED_LISTENERS: PLAINTEXT://${kafka_advertised_listeners}:9092
      KAFKA_OFFSETS_TOPIC_REPLICATION_FACTOR: 1
      # 设置 Kafka 的 JVM 堆内存
      KAFKA_HEAP_OPTS: "${kafka_heap_opts}"
      # 禁用自动创建 Topic，否则 Spring Boot 会自动创建 partitions=0 的 topic
      KAFKA_AUTO_CREATE_TOPICS_ENABLE: "false"
    restart: unless-stopped
    volumes:
      - data-demo-flash-sale-kafka:/var/lib/kafka/data
      
volumes:
  data-demo-flash-sale-kafka:
```



## 未被消费所在的日志文件被日志保留策略删除

>说明：系统在高并发时，因为消息消费性能下降，导致大量的消息积压，此时如果配置 `kafka` 日志保留策略是基于大小的，在日志总大小超过阈值后即使日志中包含未被消费的消息也会被日志保留策略删除导致消息丢失。

问题重现：

1. 借助本站 [示例](https://gitee.com/dexterleslie/demonstration/tree/main/demo-kafka/demo-kafka-benchmark) 重现问题。

2. 调整示例中 `docker-compose.yaml kafka` 日志保留策略部分配置如下：

   ```yaml
   # ------------------- 日志清理配置 -------------------
   # 清理策略：启用 delete（按时间/大小删除）
   KAFKA_LOG_CLEANUP_POLICY: "delete"
   # 单个分区最大日志大小：1MB（根据磁盘容量调整，如 5GB、20GB）
   KAFKA_LOG_RETENTION_BYTES: "1048576"  # 设为 -1 表示不限制大小（仅用时间策略）
   # 日志段大小：256KB（更小的段可提升清理精度，但增加文件数）
   KAFKA_LOG_SEGMENT_BYTES: "262144"  #（原默认 1GB）
   ```

   - 日志总大小为 `1MB`，日志段大小为 `256KB`。

3. 使用开发模式启动应用

4. 通过 `wrk` 工具自动发送大量的消息以生成 `kafka` 日志数据

   ```sh
   wrk -t8 -c32 -d300000000s --latency --timeout 60 http://localhost:8080/api/v1/sendToTopic1
   ```

5. 进入 `kafka` 所在的容器并观察在日志总大小超过 `1MB` 阈值后会被自动删除，从而导致消息丢失。

   ```sh
   # 进入 kafka 所在的容器
   
   # 切换到 my-topic-1-0 并观察日志被自动删除的过程
   watch -n 1 "ls -alh"
   ```




## 分区迁移到另外一个 `Broker`

>把指定的主题所有分区从一个 `Broker` 迁移到另外一个 `Broker` 中，在分区迁移过程中不丢失消息。

使用本站 [示例](https://gitee.com/dexterleslie/demonstration/tree/main/demo-kafka/demo-kafka-benchmark) 协助实验。

启动示例：

- 启动 `Kafka` 服务

  ```sh
  docker compose up -d
  ```

- 启动 `service` 和 `crond` 应用

- 应用正常启动后，取消 `docker-compose.yaml` 中的 `kafka2` 注释并启动 `Kafka Broker 2`

  ```sh
  docker compose up -d
  ```

进入 `kafka2` 容器

```sh
docker compose exec -it kafka2 bash
```

检查所有 `Broker` 是否正常响应

```sh
KAFKA_JMX_OPTS="" /usr/bin/kafka-broker-api-versions --bootstrap-server localhost:9093
```

- 有 `192.168.1.181:9093` 和 `192.168.1.181:9092` 输出表示 `Broker` 正常。

确保目标 `Broker` 上的所有分区副本均有冗余（至少 1 个其他副本在其他 `Broker` 上），查看分区副本分布

```sh
KAFKA_JMX_OPTS="" /usr/bin/kafka-topics --bootstrap-server localhost:9093 --describe --topic my-topic-1
```

- 输出中的 `Replicas` 列若仅包含目标 `Broker ID`（如 `[1]`），说明该分区无冗余，删除会导致数据丢失！

持续 `6` 分钟测试以模拟业务逻辑持续进行中

```sh
ab -n 25000 -c 32 -k http://192.168.1.181:8080/api/v1/sendToTopic1PartitionReassignAssist
```

生成重分配方案，所有分区从 `broker 1` 迁移到 `broker 2`

```sh
KAFKA_JMX_OPTS="" /usr/bin/kafka-reassign-partitions --bootstrap-server localhost:9093 \
  --generate \
  --topics-to-move-json-file topics.json \
  --broker-list "2" > reassign.json
```

`topics.json`：

```json
{
  "topics": [
    {
      "topic": "__consumer_offsets"
    },
    {
      "topic": "my-topic-1"
    },
    {
      "topic": "my-topic-2"
    },
    {
      "topic": "topic-test-send-perf"
    }
  ],
  "version": 1
}
```

编辑 `reassign.json` 删除其他配置只保留 `Proposed partition reassignment configuration` 配置。

应用重分配方案（生成 `reassign.json` 文件后执行）

```sh
KAFKA_JMX_OPTS="" /usr/bin/kafka-reassign-partitions --bootstrap-server localhost:9093 \
  --execute \
  --reassignment-json-file reassign.json
```

验证进度

```sh
KAFKA_JMX_OPTS="" /usr/bin/kafka-reassign-partitions --bootstrap-server localhost:9093 \
  --verify \
  --reassignment-json-file reassign.json
```

再次查看主题的分区全部迁移到 `Broker 2` 上

```sh
KAFKA_JMX_OPTS="" /usr/bin/kafka-topics --bootstrap-server localhost:9093 --describe --topic my-topic-1
```

使用 `docker stats` 查看 `Kafka` 容器 `CPU` 使用率时，发现 `kafka1` 容器没有使用 `CPU`，而 `kafka2` 容器在使用 `CPU`。

查看 `crond` 服务日志 `total=25000` 表示没有丢失消息。

此时可以安全地关闭并删除 `kafka1` 了。



## 消费者崩溃或者重启是否会丢失消息呢？

>使用本站 [示例](https://gitee.com/dexterleslie/demonstration/tree/main/demo-kafka/demo-kafka-benchmark) 协助测试。

### 测试是否会丢失消费者运行前的消息

>提示：按照下面修改应用配置后就不会丢失应用首次运行前的消息了。

在开发环境中启动应用的相关容器

启动 `ApplicationService` 应用，但不启动 `ApplicationCrond` 应用

使用 `rest.http` 文件中的 `GET http://localhost:8080/api/v1/sendToTopic1` 向主题发送 `3` 条消息后再启动 `ApplicationCrond` 应用。

`ApplicationCrond` 运行后使用 `rest.http` 文件中的 `GET http://localhost:8080/api/v1/getConfigOptionAutoOffsetResetCounter` 获取计数器，发现计数器依旧为 `0`（并没有消费之前的 `3` 条消息）。

要解决上面的问题需要参考本站 <a href="/kafka/README.html#配置项-auto-offset-reset" target="_blank">链接</a> 调整消费者添加 `props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");` 配置：

```java
@Bean("defaultConsumerFactory")
public ConsumerFactory<String, String> defaultConsumerFactory() {
    Map<String, Object> props = new HashMap<>();
    // 从application.properties中获取Bootstrap Server（兼容原有配置）
    props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
    /*props.put(ConsumerConfig.GROUP_ID_CONFIG, "group-topic1"); // 建议为不同主题设置不同Group ID（可选）*/
    props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
    props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
    // 为Topic2单独设置max-poll-records
    props.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, 1024);
    // 无已提交偏移量（如首次启动）时的消费起始位置
    props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
    return new DefaultKafkaConsumerFactory<>(props);
}
```



### 测试崩溃或者重启消息者是否会丢失消息

>提示：不会丢失消息。

在开发环境中启动应用的相关容器

启动 `ApplicationService` 和 `ApplicationCrond` 应用

借助 `ab` 工具生产消息持续 `3` 分钟：

```sh
ab -n 12000 -c 32 -k http://localhost:8080/api/v1/sendToTopic1
```

在测试期间重启或者崩溃 `ApplicationCrond` 应用

```sh
# 模拟应用崩溃
ps aux|grep ApplicationCrond
# 获取上面输出的进程 id
kill -9 105091
```

使用 `rest.http` 文件中的 `GET http://localhost:8080/api/v1/getConfigOptionAutoOffsetResetCounter` 获取计数器返回 `"data": 12000` 表示没有丢失消息。



## 在线修改主题分区数

>提示：
>
>- 在线修改主题的分区数，稍等几分钟后，客户端消费者会自动触发分区重新平衡机制，不需要重启应用。
>
>- 不支持减少主题的分区数，否则会报告如下错误
>
>  ```sh
>  $ KAFKA_JMX_OPTS="" /usr/bin/kafka-topics --alter --bootstrap-server localhost:9092 --topic topic-test-alter-partitions-online --partitions 3
>  Error while executing topic command : Topic currently has 8 partitions, which is higher than the requested 3.
>  [2025-08-05 13:03:35,181] ERROR org.apache.kafka.common.errors.InvalidPartitionsException: Topic currently has 8 partitions, which is higher than the requested 3.
>   (kafka.admin.TopicCommand$)
>  ```
>
>使用本站 [示例](https://gitee.com/dexterleslie/demonstration/tree/main/demo-kafka/demo-kafka-benchmark) 协助测试。

在开发环境中启动应用的相关容器

启动 `ApplicationService` 和 `ApplicationCrond` 应用

使用 `wrk` 协助生成测试数据

```sh
wrk -t4 -c32 -d30000000s --latency --timeout 60 http://localhost:8080/api/v1/sendToTopicTestAlterPartitionsOnline
```

查看 `ApplicationCrond` 应用日志，可以看出主题当前分区数为 `1` 所以 `concurrent` 并发为 `1`

```
2025-08-05 12:53:31.195  INFO 179502 --- [ntainer#2-0-C-1] c.f.demo.config.ConfigKafkaListener      : concurrent=1,size=1024,total=107941
2025-08-05 12:53:31.203  INFO 179502 --- [ntainer#2-0-C-1] c.f.demo.config.ConfigKafkaListener      : concurrent=1,size=1024,total=108965
2025-08-05 12:53:31.234  INFO 179502 --- [ntainer#2-0-C-1] c.f.demo.config.ConfigKafkaListener      : concurrent=1,size=1024,total=109989
2025-08-05 12:53:31.302  INFO 179502 --- [ntainer#2-0-C-1] c.f.demo.config.ConfigKafkaListener      : concurrent=1,size=1024,total=111013
2025-08-05 12:53:32.227  INFO 179502 --- [ntainer#2-0-C-1] c.f.demo.config.ConfigKafkaListener      : concurrent=1,size=1024,total=112037
2025-08-05 12:53:32.989  INFO 179502 --- [ntainer#2-0-C-1] c.f.demo.config.ConfigKafkaListener      : concurrent=1,size=1024,total=113061
2025-08-05 12:53:33.222  INFO 179502 --- [ntainer#2-0-C-1] c.f.demo.config.ConfigKafkaListener      : concurrent=1,size=1024,total=114085
2025-08-05 12:53:34.021  INFO 179502 --- [ntainer#2-0-C-1] c.f.demo.config.ConfigKafkaListener      : concurrent=1,size=614,total=114699
```

进入 `Kafka` 服务所在的容器在线修改主题的分区数为 `8`

```sh
$ KAFKA_JMX_OPTS="" /usr/bin/kafka-topics --alter --bootstrap-server localhost:9092 --topic topic-test-alter-partitions-online --partitions 8

$ KAFKA_JMX_OPTS="" /usr/bin/kafka-topics --describe --bootstrap-server localhost:9092 --topic topic-test-alter-partitions-online
Topic: topic-test-alter-partitions-online       TopicId: td4Zf-iUT12cU19_wc_bUA PartitionCount: 8       ReplicationFactor: 1    Configs: cleanup.policy=delete,segment.bytes=536870912,retention.bytes=2147483648
        Topic: topic-test-alter-partitions-online       Partition: 0    Leader: 1       Replicas: 1     Isr: 1
        Topic: topic-test-alter-partitions-online       Partition: 1    Leader: 1       Replicas: 1     Isr: 1
        Topic: topic-test-alter-partitions-online       Partition: 2    Leader: 1       Replicas: 1     Isr: 1
        Topic: topic-test-alter-partitions-online       Partition: 3    Leader: 1       Replicas: 1     Isr: 1
        Topic: topic-test-alter-partitions-online       Partition: 4    Leader: 1       Replicas: 1     Isr: 1
        Topic: topic-test-alter-partitions-online       Partition: 5    Leader: 1       Replicas: 1     Isr: 1
        Topic: topic-test-alter-partitions-online       Partition: 6    Leader: 1       Replicas: 1     Isr: 1
        Topic: topic-test-alter-partitions-online       Partition: 7    Leader: 1       Replicas: 1     Isr: 1
```

稍等几分钟后客户端自动触发分区重新平衡机制后再使用上面的 `wrk` 命令协助生成测试数据

此时查看 `ApplicationCrond` 应用日志发现并发数变化为 `8` 和预期的一致

```
2025-08-05 12:59:58.177  INFO 179502 --- [ntainer#2-0-C-1] c.f.demo.config.ConfigKafkaListener      : concurrent=8,size=1024,total=212756
2025-08-05 12:59:58.303  INFO 179502 --- [ainer#2-104-C-1] c.f.demo.config.ConfigKafkaListener      : concurrent=8,size=807,total=213563
2025-08-05 12:59:58.325  INFO 179502 --- [ainer#2-101-C-1] c.f.demo.config.ConfigKafkaListener      : concurrent=8,size=361,total=213924
2025-08-05 12:59:58.407  INFO 179502 --- [ainer#2-102-C-1] c.f.demo.config.ConfigKafkaListener      : concurrent=8,size=1024,total=214948
2025-08-05 12:59:58.439  INFO 179502 --- [ainer#2-101-C-1] c.f.demo.config.ConfigKafkaListener      : concurrent=7,size=1024,total=215972
2025-08-05 12:59:58.441  INFO 179502 --- [ntainer#2-9-C-1] c.f.demo.config.ConfigKafkaListener      : concurrent=8,size=1024,total=216996
2025-08-05 12:59:58.458  INFO 179502 --- [ainer#2-103-C-1] c.f.demo.config.ConfigKafkaListener      : concurrent=8,size=1024,total=218020
2025-08-05 12:59:58.478  INFO 179502 --- [tainer#2-99-C-1] c.f.demo.config.ConfigKafkaListener      : concurrent=8,size=1024,total=219044
```

