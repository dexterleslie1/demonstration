## 可观测性是什么呢？

在监控领域里，“可观测性”（Observability）指的是：**通过系统输出的数据，能够推断出系统内部状态、行为及问题的原因的能力**。它不仅仅是“能看到指标”，而是能**理解并解释系统为什么这样运行**。

---

### 一、可观测性的三个核心支柱

业界常用三个维度的数据来衡量可观测性：

1. **日志（Logs）**
   - 记录系统中发生的事件和错误信息。
   - 特点：详细、有时间戳，适合排查具体故障点。
   - 例子：`ERROR: failed to connect to DB (timeout=30s)`

2. **指标（Metrics）**
   - 按时间序列采集的数值型数据，反映系统的整体状况。
   - 特点：便于聚合、绘图、告警。
   - 例子：CPU使用率、请求 QPS、接口平均响应时间、错误率等。

3. **链路追踪（Traces）**
   - 跟踪一个请求在多个服务/组件之间的调用路径。
   - 特点：能看出调用链哪里慢、哪里出错。
   - 例子：一次下单请求经过网关 → 订单服务 → 库存服务 → 支付服务，每个环节耗时多少。

这三者一起，构成“可观测性三角”。

---

### 二、可观测性和传统监控的区别

| 维度     | 传统监控                     | 可观测性                             |
| -------- | ---------------------------- | ------------------------------------ |
| 关注点   | “有没有出问题”“指标是否超标” | “为什么出问题”“系统内部发生了什么”   |
| 数据形态 | 主要是指标 + 简单告警        | 日志 + 指标 + 链路追踪 + 事件        |
| 目标     | 发现问题、触发告警           | 快速定位根因、优化系统、支持排障决策 |
| 使用方式 | 看仪表盘、等报警             | 结合多种数据做关联分析、下钻、诊断   |

可以简单理解为：  
- **监控 = 看表盘，发现红灯亮了**  
- **可观测性 = 知道红灯为什么亮，以及影响范围有多大**

---

### 三、可观测性解决什么问题

- **问题定位难**：一个接口变慢，是数据库、缓存、第三方服务还是代码问题？可观测性通过链路+指标+日志帮助快速锁定。
- **系统太复杂**：微服务、容器、多活、云原生环境下，单点监控不够，需要跨系统、跨服务观察。
- **性能优化缺乏依据**：通过历史指标、调用链耗时分布，找到瓶颈点，指导优化。
- **故障复盘和容量规划**：有完整可观测数据，才能分析故障根因，预测资源需求。

---

### 四、可观测性在实践中的关键点

- **数据要能关联起来**  
  同一个请求，在日志、指标、链路中都有统一标识（如 traceId、requestId），才能从“某次超时”一路查到“哪行代码、哪个SQL、哪台机器”。

- **工具与平台**  
  常见组合：Prometheus（指标）+ Grafana（可视化）+ ELK / Loki（日志）+ Jaeger / Zipkin（链路追踪）。

- **不是越多越好**  
  关键在于：采集的数据是否能帮助你回答“发生了什么、为什么发生、影响多大”。

---

一句话概括：**监控告诉你系统“不健康”，可观测性帮你弄清楚“为什么不健康以及如何治好它”。**

## Loki是什么呢？

Loki 是 **Grafana Labs 开源的日志聚合系统**，专门用来**高效存储和查询日志**，设计理念类似 Prometheus，但只关心“日志内容 + 标签”，不解析日志全文，因此**资源占用比 ELK 低很多**。

---

### 一、Loki 是什么

- 类型：日志系统（Log Aggregation System）
- 定位：对标 ELK 里的 Elasticsearch，但**不做全文检索**，而是用**标签索引**来加速查询。
- 架构思路：受 Prometheus 启发 ——  
  Prometheus 存时间序列指标（Metric + Labels），  
  Loki 存日志流（Log Lines + Labels）。

一句话：**Loki = 带标签的日志 + 类似 Prometheus 的查询方式 + 与 Grafana 无缝集成**。

---

### 二、核心概念

1. **Log Stream（日志流）**
   - 具有相同一组标签（Labels）的一组日志行。
   - 例子：  
     `{app="order-service", env="prod", instance="10.0.0.5"}` 是一条日志流的标签集合。

2. **Labels（标签）**
   - 类似 Prometheus 的 metric labels，用于对日志分组和过滤。
   - 只在写入时指定，**不可变**，不适合高频变化的值（否则会产生大量流）。
   - 常见标签：`job`、`instance`、`app`、`env`、`filename` 等。

3. **Log Line（日志行）**
   - 实际的文本内容，通常是 JSON 或单行文本。
   - Loki **不会全文索引**，只在标签上建索引，日志内容本身压缩存储。

4. **Components（组件）**
   - **Distributor**：接收日志写入请求，按标签哈希分发到多个 Ingester。
   - **Ingester**：缓存最近的日志块（chunk），定期 flush 到对象存储（本地磁盘/S3 等）。
   - **Querier**：查询时从 Ingester 和对象存储拉取日志块，合并返回结果。
   - **Compactor**：压缩旧日志块，优化存储和查询性能。

---

### 三、Loki 的特点

1. **资源占用低**
   - 不做全文倒排索引，内存/磁盘占用远低于 Elasticsearch。
   - 适合大规模日志场景，尤其是已有 Prometheus 体系的团队。

2. **标签驱动查询**
   - 查询语法 LogQL 类似 PromQL：  
     ```logql
     {app="order-service", env="prod"} |= "error" |~ "timeout.*"
     ```
   - 先用标签缩小范围，再对日志内容进行过滤。

3. **与 Grafana 天然集成**
   - Grafana 内置 Loki 数据源，可以直接在 Dashboard 里展示日志、做 Explore 查询。
   - 可以和 Prometheus 指标一起展示，实现“指标 + 日志”联动。

4. **多租户**
   - 支持通过 `X-Scope-OrgID` 区分租户，适合 SaaS 或多团队共用一套 Loki 集群。

5. **存储灵活**
   - 日志块可存储在本地磁盘、NFS、S3、GCS 等对象存储中，方便水平扩展。

---

### 四、典型应用场景

- **云原生 / Kubernetes 日志收集**
  - 配合 Promtail/Fluent Bit/Fluentd，把 Pod 日志打到 Loki。
- **微服务日志集中查询**
  - 通过统一标签（`app`, `env`, `instance`）快速筛选某个服务的日志。
- **与 OpenTelemetry / Prometheus 联动**
  - 指标异常时，直接跳转到对应服务/实例的日志排查问题。
- **低成本日志归档**
  - 长期保存大量日志，但不希望像 ELK 那样占用大量内存和 CPU。

---

### 五、简单对比一下 ELK vs Loki

| 特性               | ELK（Elasticsearch）      | Loki                         |
| ------------------ | ------------------------- | ---------------------------- |
| 索引方式           | 全文倒排索引              | 仅标签索引                   |
| 查询语法           | Lucene / Kibana Query DSL | LogQL（类似 PromQL）         |
| 存储开销           | 较高                      | 较低                         |
| 与 Prometheus 联动 | 需要额外集成              | 原生友好                     |
| 学习成本           | 相对较高                  | 对熟悉 Prometheus 的用户很低 |

---

### 六、在你当前知识体系中的位置

结合你前面问过的 OpenTelemetry / SigNoz / Spring Boot：

- OpenTelemetry：负责**采集 Trace / Metrics / Logs**（日志只是其中一种信号）。
- Spring Boot：产生业务日志。
- Promtail / Fluent Bit：把日志文件变成带标签的日志流。
- **Loki：存储这些日志流，并提供高效查询接口**。
- Grafana / SigNoz：从 Loki 查询日志，用于排查问题或展示在 UI 中。

## Grafana可视化查询Loki

使用admin/admin登录Grafana控制台http://localhost:3000

左侧 Explore（或 “探索”）→ 选择数据源 Loki

把查询输入框从Builder模式切换到Code模式并输入下面其中之一查询表达式

```
# 按应用
{app="demo-spring-boot"}

# 包含 “x5” 关键词
{app="demo-spring-boot"} |= "x5"

# 按级别
{app="demo-spring-boot", level="ERROR"}
```

点击 Run query 即可看到日志并支持时间范围筛选。

## SpringBoot集成Loki

具体用法请参考本站示例：https://gitee.com/dexterleslie/demonstration/tree/main/demo-可观察性/demo-loki

## Loki性能测试

使用本站示例 https://gitee.com/dexterleslie/demonstration/tree/main/demo-可观察性/demo-loki 协助测试

使用下面命令生产大量日志数据

```sh
wrk -t8 -c32 -d300000s --latency --timeout 60 http://localhost:8080/api/v1/test1
```

测试结论：

>注意：暂时不能使用Loki方案替代ELK。

- 在大量日志时，使用`{app="demo-spring-boot", level="ERROR"}`查询时Loki容器内存使用飙升到600多MB并且查询超时。

- wrk运行时SpringBoot报告下面错误

  ```
  entry with timestamp 2026-03-01 14:42:46.119119015 +0000 UTC ignored, reason: 'Per stream rate limit exceeded (limit: 3MB/sec) while attempting to ingest for stream '{app="demo-spring-boot", level="ERROR"}' totaling 8238B, consider splitting a stream via additional labels or contact your Loki administrator to see if the limit can be increased',
  entry with timestamp 2026-03-01 14:42:46.119119021 +0000 UTC ignored, reason: 'Per stream rate limit exceeded (limit: 3MB/sec) while attempting to ingest for stream '{app="demo-spring-boot", level="ERROR"}' totaling 8237B, consider splitting a stream via additional labels or contact your Loki administrator to see if the limit can be increased',
  entry with timestamp 2026-03-01 14:42:46.119119022 +0000 UTC ignored, reason: 'Per stream rate limit exceeded (limit: 3MB/sec) while attempting to ingest for stream '{app="demo-spring-boot", level="ERROR"}' totaling 8237B, consider splitting a stream via additional labels or contact your Loki administrator to see if the limit can be increased',
  entry with timestamp 2026-03-01 14:42:46.119119023 +0000 UTC ignored, reason: 'Per stream rate limit exceeded (limit: 3MB/sec) while attempting to ingest for stream '{app="demo-spring-boot", level="ERROR"}' totaling 8237B, consider splitting a stream via additional labels or contact your Loki administrator to see if the limit can be increased',
  user 'fake', total ignored: 263 out of 501 for stream: {app="demo-spring-boot", level="ERROR"}
  
  22:43:30,741 |-ERROR in com.github.loki4j.client.pipeline.DefaultPipeline@6a2afd4d - Loki responded with non-success status 429 on batch #54ada85907d4d (4,232,753 bytes). Error: Ingestion rate limit exceeded for user fake (limit: 6291456 bytes/sec) while attempting to ingest '1000' lines totaling '4124763' bytes, reduce log volume or contact your Loki administrator to see if the limit can be increased
  
  22:43:30,804 |-ERROR in com.github.loki4j.client.pipeline.DefaultPipeline@6a2afd4d - Loki responded with non-success status 429 on batch #54ada8cb83518 (4,273,927 bytes). Error: Ingestion rate limit exceeded for user fake (limit: 6291456 bytes/sec) while attempting to ingest '995' lines totaling '4165257' bytes, reduce log volume or contact your Loki administrator to see if the limit can be increased
  
  22:43:30,889 |-ERROR in com.github.loki4j.client.pipeline.DefaultPipeline@6a2afd4d - Loki responded with non-success status 429 on batch #54ada91151abe (4,266,015 bytes). Error: Ingestion rate limit exceeded for user fake (limit: 6291456 bytes/sec) while attempting to ingest '1000' lines totaling '4157373' bytes, reduce log volume or contact your Loki administrator to see if the limit can be increased
  
  22:43:30,957 |-ERROR in com.github.loki4j.client.pipeline.DefaultPipeline@6a2afd4d - Loki responded with non-success status 429 on batch #54ada951145ec (4,266,046 bytes). Error: Ingestion rate limit exceeded for user fake (limit: 6291456 bytes/sec) while attempting to ingest '1000' lines totaling '4157404' bytes, reduce log volume or contact your Loki administrator to see if the limit can be increased
  
  22:43:31,019 |-ERROR in com.github.loki4j.client.pipeline.DefaultPipeline@6a2afd4d - Loki responded with non-success status 429 on batch #54ada995213d1 (4,232,822 bytes). Error: Ingestion rate limit exceeded for user fake (limit: 6291456 bytes/sec) while attempting to ingest '1000' lines totaling '4124832' bytes, reduce log volume or contact your Loki administrator to see if the limit can be increased
  
  22:43:31,099 |-ERROR in com.github.loki4j.client.pipeline.DefaultPipeline@6a2afd4d - Loki responded with non-success status 429 on batch #54ada9ce6ecaa (4,274,204 bytes). Error: Ingestion rate limit exceeded for user fake (limit: 6291456 bytes/sec) while attempting to ingest '998' lines totaling '4165453' bytes, reduce log volume or contact your Loki administrator to see if the limit can be increased
  
  22:43:31,164 |-ERROR in com.github.loki4j.client.pipeline.DefaultPipeline@6a2afd4d - Loki responded with non-success status 429 on batch #54adaa0da0ef5 (4,266,104 bytes). Error: Ingestion rate limit exceeded for user fake (limit: 6291456 bytes/sec) while attempting to ingest '1000' lines totaling '4157462' bytes, reduce log volume or contact your Loki administrator to see if the limit can be increased
  
  22:43:31,240 |-ERROR in com.github.loki4j.client.pipeline.DefaultPipeline@6a2afd4d - Loki responded with non-success status 429 on batch #54adaa32822a3 (4,249,425 bytes). Error: Ingestion rate limit exceeded for user fake (limit: 6291456 bytes/sec) while attempting to ingest '1000' lines totaling '4141109' bytes, reduce log volume or contact your Loki administrator to see if the limit can be increased
  
  22:43:31,333 |-ERROR in com.github.loki4j.client.pipeline.DefaultPipeline@6a2afd4d - Loki responded with non-success status 429 on batch #54adaa6da8c7e (4,273,862 bytes). Error: entry with timestamp 2026-03-01 14:42:46.516516021 +0000 UTC ignored, reason: 'Per stream rate limit exceeded (limit: 3MB/sec) while attempting to ingest for stream '{app="demo-spring-boot", level="ERROR"}' totaling 8237B, consider splitting a stream via additional labels or contact your Loki administrator to see if the limit can be increased',
  entry with timestamp 2026-03-01 14:42:46.507507011 +0000 UTC ignored, reason: 'Per stream rate limit exceeded (limit: 3MB/sec) while attempting to ingest for stream '{app="demo-spring-boot", level="ERROR"}' totaling 8237B, consider splitting a stream via additional labels or contact your Loki administrator to see if the limit can be increased',
  entry with timestamp 2026-03-01 14:42:46.517517 +0000 UTC ignored, reason: 'Per stream rate limit exceeded (limit: 3MB/sec) while attempting to ingest for stream '{app="demo-spring-boot", level="ERROR"}' totaling 8238B, consider splitting a stream via additional labels or contact your Loki administrator to see if the limit can be increased',
  entry with timestamp 2026-03-01 14:42:46.517517001 +0000 UTC ignored, reason: 'Per stream rate limit exceeded (limit: 3MB/sec) while attempting to ingest for stream '{app="demo-spring-boot", level="ERROR"}' totaling 8238B, consider splitting a stream via additional labels or contact your Loki administrator to see if the limit can be increased',
  entry with timestamp 2026-03-01 14:42:46.515515016 +0000 UTC ignored, reason: 'Per stream rate limit exceeded (limit: 3MB/sec) while attempting to ingest for stream '{app="demo-spring-boot", level="ERROR"}' totaling 8238B, consider splitting a stream via additional labels or contact your Loki administrator to see if the limit can be increased',
  entry with timestamp 2026-03-01 14:42:46.517517005 +0000 UTC ignored, reason: 'Per stream rate limit exceeded (limit: 3MB/sec) while attempting to ingest for stream '{app="demo-spring-boot", level="ERROR"}' totaling 8238B, consider splitting a stream via additional labels or contact your Loki administrator to see if the limit can be increased',
  entry with timestamp 2026-03-01 14:42:46.516516004 +0000 UTC ignored, reason: 'Per stream rate limit exceeded (limit: 3MB/sec) while attempting to ingest for stream '{app="demo-spring-boot", level="ERROR"}' totaling 8237B, consider splitting a stream via additional labels or contact your Loki administrator to see if the limit can be increased',
  entry with timestamp 2026-03-01 14:42:46.517517009 +0000 UTC ignored, reason: 'Per stream rate limit exceeded (limit: 3MB/sec) while attempting to ingest for stream '{app="demo-spring-boot", level="ERROR"}' totaling 8238B, consider splitting a stream via additional labels or contact your Loki administrator to see if the limit can be increased',
  entry with timestamp 2026-03-01 14:42:46.517517008 +0000 UTC ignored, reason: 'Per stream rate limit exceeded (limit: 3MB/sec) while attempting to ingest for stream '{app="demo-spring-boot", level="ERROR"}' totaling 8237B, consider splitting a stream via additional labels or contact your Loki administrator to see if the limit can be increased',
  entry with timestamp 2026-03-01 14:42:46.517517007 +0000 UTC ignored, reason: 'Per stream rate limit exceeded (limit: 3MB/sec) while attempting to ingest for stream '{app="demo-spring-boot", level="ERROR"}' totaling 8238B, consider splitting a stream via additional labels or contact your Loki administrator to see if the limit can be increased',
  user 'fake', total ignored: 252 out of 501 for stream: {app="demo-spring-boot", level="ERROR"}
  
  22:43:31,406 |-ERROR in com.github.loki4j.client.pipeline.DefaultPipeline@6a2afd4d - Loki responded with non-success status 429 on batch #54adaaadb4b24 (4,273,839 bytes). Error: Ingestion rate limit exceeded for user fake (limit: 6291456 bytes/sec) while attempting to ingest '995' lines totaling '4165169' bytes, reduce log volume or contact your Loki administrator to see if the limit can be increased
  
  22:43:31,472 |-ERROR in com.github.loki4j.client.pipeline.DefaultPipeline@6a2afd4d - Loki responded with non-success status 429 on batch #54adab0db3c9c (4,241,140 bytes). Error: Ingestion rate limit exceeded for user fake (limit: 6291456 bytes/sec) while attempting to ingest '1000' lines totaling '4132987' bytes, reduce log volume or contact your Loki administrator to see if the limit can be increased
  
  22:43:31,536 |-ERROR in com.github.loki4j.client.pipeline.DefaultPipeline@6a2afd4d - Loki responded with non-success status 429 on batch #54adab38fda5a (4,274,448 bytes). Error: Ingestion rate limit exceeded for user fake (limit: 6291456 bytes/sec) while attempting to ingest '1000' lines totaling '4165643' bytes, reduce log volume or contact your Loki administrator to see if the limit can be increased
  
  22:43:31,604 |-ERROR in com.github.loki4j.client.pipeline.DefaultPipeline@6a2afd4d - Loki responded with non-success status 429 on batch #54adab74286a2 (4,274,164 bytes). Error: Ingestion rate limit exceeded for user fake (limit: 6291456 bytes/sec) while attempting to ingest '998' lines totaling '4165413' bytes, reduce log volume or contact your Loki administrator to see if the limit can be increased
  
  ```

  