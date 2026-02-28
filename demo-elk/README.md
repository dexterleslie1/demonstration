## Elasticsearch熔断机制原理

### 一、什么是熔断

熔断（Circuit Breaker）是一种**保护机制**，当系统检测到异常（如内存压力、请求过多、响应过慢等）时，会**主动拒绝部分或全部请求**，防止系统崩溃，给系统恢复的时间窗口。

类比电路保险丝：电流过大 → 保险丝熔断 → 断电保护 → 修复后重新合闸。

------

### 二、Elasticsearch 中熔断的作用场景

Elasticsearch 在处理大规模数据时，容易出现以下问题：

- **内存压力过大**（尤其是 JVM Heap）
- **聚合查询占用大量内存**
- **批量写入请求堆积**
- **节点负载过高导致 GC 频繁**

熔断器的作用是：**提前拦截可能耗尽资源的请求**，而不是等 OOM 或节点崩溃再处理。

------

### 三、主要熔断类型及原理

Elasticsearch 的熔断由 `CircuitBreaker`模块实现，主要类型如下：

#### 1. Parent Circuit Breaker（总熔断器）

- **作用**：监控所有子熔断器的内存使用总和，确保不会超过 JVM 堆内存的某个比例。
- **配置参数**：
  - `indices.breaker.total.limit`：总内存限制，默认是 JVM 堆的 70%。
- **原理**：
  - 每次分配内存时，先检查总使用量 + 新申请量是否超过 `total.limit`。
  - 如果超过，直接抛 `CircuitBreakingException`，请求被拒绝。

------

#### 2. Field Data Circuit Breaker（字段数据熔断器）

- **作用**：保护 **fielddata cache** 的内存，用于 `terms`聚合、排序等需要加载字段值到内存的操作。
- **配置参数**：
  - `indices.breaker.fielddata.limit`：默认是 JVM 堆的 40%。
- **原理**：
  - 在加载 fielddata 到堆内存前，检查当前 fielddata 已用内存 + 新申请量是否超过 limit。
  - 超限则拒绝该操作，返回错误，避免 OOM。

------

#### 3. Request Circuit Breaker（请求熔断器）

- **作用**：保护 **单个请求** 的内存使用，比如聚合、脚本计算等。
- **配置参数**：
  - `indices.breaker.request.limit`：默认是 JVM 堆的 60%。
- **原理**：
  - 每个请求执行前估算所需内存，如果加上当前已用内存超过 limit，则拒绝该请求。
  - 防止一个复杂查询拖垮整个节点。

------

#### 4. In Flight Requests Circuit Breaker（传输中请求熔断器）

- **作用**：保护 **网络层** 的内存，防止同时接收过多大请求。
- **配置参数**：
  - `network.breaker.inflight_requests.limit`：默认是 JVM 堆的 100%（但受 `max_content_length`等限制）。
- **原理**：
  - 统计正在传输的请求占用的内存，如果超过 limit，新请求会被拒绝。
  - 避免网络请求堆积导致内存溢出。

------

#### 5. Accounting Circuit Breaker（统计/记账熔断器）

- **作用**：保护 **非请求相关** 的内存，如段合并、存储等后台任务。
- **配置参数**：
  - `indices.breaker.accounting.limit`：默认是 JVM 堆的 100%。
- **原理**：
  - 监控段合并、translog 等后台操作的内存，防止它们无限制占用堆。

------

### 四、熔断触发流程

1. **请求进入**：如搜索、聚合、写入。
2. **内存预估**：ES 根据请求类型和参数估算所需内存。
3. **逐级检查**：
   - 先检查 **Request / Fielddata / Inflight / Accounting** 等子熔断器。
   - 再检查 **Parent 总熔断器**。
4. **决定是否放行**：
   - 未超限 → 正常执行。
   - 超限 → 抛出 `CircuitBreakingException`，请求失败。

------

### 五、熔断后的表现

- 客户端收到类似错误：

  ```
  {
    "error": {
      "type": "circuit_breaking_exception",
      "reason": "[parent] Data too large, data for [<request>] would be larger than limit [...]"
    }
  }
  ```

- 日志中会记录类似：

  ```
  circuit_breaking_exception: [fielddata] Data too large...
  ```

------

### 六、配置建议

| 参数                                      | 默认值 | 说明                       |
| ----------------------------------------- | ------ | -------------------------- |
| `indices.breaker.total.limit`             | 70%    | 总内存上限，建议不超过 70% |
| `indices.breaker.fielddata.limit`         | 40%    | fielddata 上限             |
| `indices.breaker.request.limit`           | 60%    | 单请求上限                 |
| `network.breaker.inflight_requests.limit` | 100%   | 传输中请求上限             |

**调优原则**：

- 堆内存越大，可适当提高 limit。
- 若频繁熔断，考虑优化查询（减少聚合、分页深度）、增加节点、升级硬件。
- 避免将 limit 设置过高，否则失去保护作用。

------

### 七、总结

Elasticsearch 熔断的核心思想是 **预防内存耗尽**，通过多级熔断器监控不同类型的内存使用，在危险发生前阻断请求。合理配置熔断参数，结合查询优化和资源扩容，是保证集群稳定性的关键。

## Data too large错误熔断机制实践

使用本站示例 https://gitee.com/dexterleslie/demonstration/tree/main/elasticsearch/demo-benchmark-es7 中的 testCreate60ClothGoodsIndices 协助测试

创建大量索引后会报告错误如下：

```

java.lang.IllegalStateException: Failed to load ApplicationContext

	at org.springframework.test.context.cache.DefaultCacheAwareContextLoaderDelegate.loadContext(DefaultCacheAwareContextLoaderDelegate.java:98)
	at org.springframework.test.context.support.DefaultTestContext.getApplicationContext(DefaultTestContext.java:124)
	at org.springframework.test.context.support.DependencyInjectionTestExecutionListener.injectDependencies(DependencyInjectionTestExecutionListener.java:118)
	at org.springframework.test.context.support.DependencyInjectionTestExecutionListener.prepareTestInstance(DependencyInjectionTestExecutionListener.java:83)
	at org.springframework.boot.test.autoconfigure.SpringBootDependencyInjectionTestExecutionListener.prepareTestInstance(SpringBootDependencyInjectionTestExecutionListener.java:43)
	at org.springframework.test.context.TestContextManager.prepareTestInstance(TestContextManager.java:248)
	at org.springframework.test.context.junit.jupiter.SpringExtension.postProcessTestInstance(SpringExtension.java:138)
	at org.junit.jupiter.engine.descriptor.ClassBasedTestDescriptor.lambda$invokeTestInstancePostProcessors$8(ClassBasedTestDescriptor.java:363)
	at org.junit.jupiter.engine.descriptor.ClassBasedTestDescriptor.executeAndMaskThrowable(ClassBasedTestDescriptor.java:368)
	at org.junit.jupiter.engine.descriptor.ClassBasedTestDescriptor.lambda$invokeTestInstancePostProcessors$9(ClassBasedTestDescriptor.java:363)
	at java.base/java.util.stream.ReferencePipeline$3$1.accept(ReferencePipeline.java:197)
	at java.base/java.util.stream.ReferencePipeline$2$1.accept(ReferencePipeline.java:179)
	at java.base/java.util.ArrayList$ArrayListSpliterator.forEachRemaining(ArrayList.java:1625)
	at java.base/java.util.stream.AbstractPipeline.copyInto(AbstractPipeline.java:509)
	at java.base/java.util.stream.AbstractPipeline.wrapAndCopyInto(AbstractPipeline.java:499)
	at java.base/java.util.stream.StreamSpliterators$WrappingSpliterator.forEachRemaining(StreamSpliterators.java:310)
	at java.base/java.util.stream.Streams$ConcatSpliterator.forEachRemaining(Streams.java:735)
	at java.base/java.util.stream.Streams$ConcatSpliterator.forEachRemaining(Streams.java:734)
	at java.base/java.util.stream.ReferencePipeline$Head.forEach(ReferencePipeline.java:762)
	at org.junit.jupiter.engine.descriptor.ClassBasedTestDescriptor.invokeTestInstancePostProcessors(ClassBasedTestDescriptor.java:362)
	at org.junit.jupiter.engine.descriptor.ClassBasedTestDescriptor.lambda$instantiateAndPostProcessTestInstance$6(ClassBasedTestDescriptor.java:283)
	at org.junit.platform.engine.support.hierarchical.ThrowableCollector.execute(ThrowableCollector.java:73)
	at org.junit.jupiter.engine.descriptor.ClassBasedTestDescriptor.instantiateAndPostProcessTestInstance(ClassBasedTestDescriptor.java:282)
	at org.junit.jupiter.engine.descriptor.ClassBasedTestDescriptor.lambda$testInstancesProvider$4(ClassBasedTestDescriptor.java:272)
	at java.base/java.util.Optional.orElseGet(Optional.java:364)
	at org.junit.jupiter.engine.descriptor.ClassBasedTestDescriptor.lambda$testInstancesProvider$5(ClassBasedTestDescriptor.java:271)
	at org.junit.jupiter.engine.execution.TestInstancesProvider.getTestInstances(TestInstancesProvider.java:31)
	at org.junit.jupiter.engine.descriptor.TestMethodTestDescriptor.lambda$prepare$0(TestMethodTestDescriptor.java:102)
	at org.junit.platform.engine.support.hierarchical.ThrowableCollector.execute(ThrowableCollector.java:73)
	at org.junit.jupiter.engine.descriptor.TestMethodTestDescriptor.prepare(TestMethodTestDescriptor.java:101)
	at org.junit.jupiter.engine.descriptor.TestMethodTestDescriptor.prepare(TestMethodTestDescriptor.java:66)
	at org.junit.platform.engine.support.hierarchical.NodeTestTask.lambda$prepare$2(NodeTestTask.java:123)
	at org.junit.platform.engine.support.hierarchical.ThrowableCollector.execute(ThrowableCollector.java:73)
	at org.junit.platform.engine.support.hierarchical.NodeTestTask.prepare(NodeTestTask.java:123)
	at org.junit.platform.engine.support.hierarchical.NodeTestTask.execute(NodeTestTask.java:90)
	at java.base/java.util.ArrayList.forEach(ArrayList.java:1511)
	at org.junit.platform.engine.support.hierarchical.SameThreadHierarchicalTestExecutorService.invokeAll(SameThreadHierarchicalTestExecutorService.java:41)
	at org.junit.platform.engine.support.hierarchical.NodeTestTask.lambda$executeRecursively$6(NodeTestTask.java:155)
	at org.junit.platform.engine.support.hierarchical.ThrowableCollector.execute(ThrowableCollector.java:73)
	at org.junit.platform.engine.support.hierarchical.NodeTestTask.lambda$executeRecursively$8(NodeTestTask.java:141)
	at org.junit.platform.engine.support.hierarchical.Node.around(Node.java:137)
	at org.junit.platform.engine.support.hierarchical.NodeTestTask.lambda$executeRecursively$9(NodeTestTask.java:139)
	at org.junit.platform.engine.support.hierarchical.ThrowableCollector.execute(ThrowableCollector.java:73)
	at org.junit.platform.engine.support.hierarchical.NodeTestTask.executeRecursively(NodeTestTask.java:138)
	at org.junit.platform.engine.support.hierarchical.NodeTestTask.execute(NodeTestTask.java:95)
	at java.base/java.util.ArrayList.forEach(ArrayList.java:1511)
	at org.junit.platform.engine.support.hierarchical.SameThreadHierarchicalTestExecutorService.invokeAll(SameThreadHierarchicalTestExecutorService.java:41)
	at org.junit.platform.engine.support.hierarchical.NodeTestTask.lambda$executeRecursively$6(NodeTestTask.java:155)
	at org.junit.platform.engine.support.hierarchical.ThrowableCollector.execute(ThrowableCollector.java:73)
	at org.junit.platform.engine.support.hierarchical.NodeTestTask.lambda$executeRecursively$8(NodeTestTask.java:141)
	at org.junit.platform.engine.support.hierarchical.Node.around(Node.java:137)
	at org.junit.platform.engine.support.hierarchical.NodeTestTask.lambda$executeRecursively$9(NodeTestTask.java:139)
	at org.junit.platform.engine.support.hierarchical.ThrowableCollector.execute(ThrowableCollector.java:73)
	at org.junit.platform.engine.support.hierarchical.NodeTestTask.executeRecursively(NodeTestTask.java:138)
	at org.junit.platform.engine.support.hierarchical.NodeTestTask.execute(NodeTestTask.java:95)
	at org.junit.platform.engine.support.hierarchical.SameThreadHierarchicalTestExecutorService.submit(SameThreadHierarchicalTestExecutorService.java:35)
	at org.junit.platform.engine.support.hierarchical.HierarchicalTestExecutor.execute(HierarchicalTestExecutor.java:57)
	at org.junit.platform.engine.support.hierarchical.HierarchicalTestEngine.execute(HierarchicalTestEngine.java:54)
	at org.junit.platform.launcher.core.EngineExecutionOrchestrator.execute(EngineExecutionOrchestrator.java:107)
	at org.junit.platform.launcher.core.EngineExecutionOrchestrator.execute(EngineExecutionOrchestrator.java:88)
	at org.junit.platform.launcher.core.EngineExecutionOrchestrator.lambda$execute$0(EngineExecutionOrchestrator.java:54)
	at org.junit.platform.launcher.core.EngineExecutionOrchestrator.withInterceptedStreams(EngineExecutionOrchestrator.java:67)
	at org.junit.platform.launcher.core.EngineExecutionOrchestrator.execute(EngineExecutionOrchestrator.java:52)
	at org.junit.platform.launcher.core.DefaultLauncher.execute(DefaultLauncher.java:114)
	at org.junit.platform.launcher.core.DefaultLauncher.execute(DefaultLauncher.java:86)
	at org.junit.platform.launcher.core.DefaultLauncherSession$DelegatingLauncher.execute(DefaultLauncherSession.java:86)
	at org.junit.platform.launcher.core.SessionPerRequestLauncher.execute(SessionPerRequestLauncher.java:53)
	at com.intellij.junit5.JUnit5IdeaTestRunner.startRunnerWithArgs(JUnit5IdeaTestRunner.java:57)
	at com.intellij.rt.junit.IdeaTestRunner$Repeater$1.execute(IdeaTestRunner.java:38)
	at com.intellij.rt.execution.junit.TestsRepeater.repeat(TestsRepeater.java:11)
	at com.intellij.rt.junit.IdeaTestRunner$Repeater.startRunnerWithArgs(IdeaTestRunner.java:35)
	at com.intellij.rt.junit.JUnitStarter.prepareStreamsAndStart(JUnitStarter.java:232)
	at com.intellij.rt.junit.JUnitStarter.main(JUnitStarter.java:55)
Caused by: org.springframework.beans.factory.BeanCreationException: Error creating bean with name 'configElasticsearchInit': Invocation of init method failed; nested exception is RestStatusException{status=429} org.springframework.data.elasticsearch.RestStatusException: Elasticsearch exception [type=circuit_breaking_exception, reason=[parent] Data too large, data for [<http_request>] would be [511827536/488.1mb], which is larger than the limit of [510027366/486.3mb], real usage: [511827256/488.1mb], new bytes reserved: [280/280b], usages [request=0/0b, fielddata=0/0b, in_flight_requests=280/280b, accounting=30448/29.7kb]]; nested exception is ElasticsearchStatusException[Elasticsearch exception [type=circuit_breaking_exception, reason=[parent] Data too large, data for [<http_request>] would be [511827536/488.1mb], which is larger than the limit of [510027366/486.3mb], real usage: [511827256/488.1mb], new bytes reserved: [280/280b], usages [request=0/0b, fielddata=0/0b, in_flight_requests=280/280b, accounting=30448/29.7kb]]]
	at org.springframework.beans.factory.annotation.InitDestroyAnnotationBeanPostProcessor.postProcessBeforeInitialization(InitDestroyAnnotationBeanPostProcessor.java:160)
	at org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory.applyBeanPostProcessorsBeforeInitialization(AbstractAutowireCapableBeanFactory.java:440)
	at org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory.initializeBean(AbstractAutowireCapableBeanFactory.java:1796)
	at org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory.doCreateBean(AbstractAutowireCapableBeanFactory.java:620)
	at org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory.createBean(AbstractAutowireCapableBeanFactory.java:542)
	at org.springframework.beans.factory.support.AbstractBeanFactory.lambda$doGetBean$0(AbstractBeanFactory.java:335)
	at org.springframework.beans.factory.support.DefaultSingletonBeanRegistry.getSingleton(DefaultSingletonBeanRegistry.java:234)
	at org.springframework.beans.factory.support.AbstractBeanFactory.doGetBean(AbstractBeanFactory.java:333)
	at org.springframework.beans.factory.support.AbstractBeanFactory.getBean(AbstractBeanFactory.java:208)
	at org.springframework.beans.factory.support.DefaultListableBeanFactory.preInstantiateSingletons(DefaultListableBeanFactory.java:955)
	at org.springframework.context.support.AbstractApplicationContext.finishBeanFactoryInitialization(AbstractApplicationContext.java:929)
	at org.springframework.context.support.AbstractApplicationContext.refresh(AbstractApplicationContext.java:591)
	at org.springframework.boot.SpringApplication.refresh(SpringApplication.java:732)
	at org.springframework.boot.SpringApplication.refreshContext(SpringApplication.java:409)
	at org.springframework.boot.SpringApplication.run(SpringApplication.java:308)
	at org.springframework.boot.test.context.SpringBootContextLoader.loadContext(SpringBootContextLoader.java:136)
	at org.springframework.test.context.cache.DefaultCacheAwareContextLoaderDelegate.loadContextInternal(DefaultCacheAwareContextLoaderDelegate.java:141)
	at org.springframework.test.context.cache.DefaultCacheAwareContextLoaderDelegate.loadContext(DefaultCacheAwareContextLoaderDelegate.java:90)
	... 72 more
Caused by: RestStatusException{status=429} org.springframework.data.elasticsearch.RestStatusException: Elasticsearch exception [type=circuit_breaking_exception, reason=[parent] Data too large, data for [<http_request>] would be [511827536/488.1mb], which is larger than the limit of [510027366/486.3mb], real usage: [511827256/488.1mb], new bytes reserved: [280/280b], usages [request=0/0b, fielddata=0/0b, in_flight_requests=280/280b, accounting=30448/29.7kb]]; nested exception is ElasticsearchStatusException[Elasticsearch exception [type=circuit_breaking_exception, reason=[parent] Data too large, data for [<http_request>] would be [511827536/488.1mb], which is larger than the limit of [510027366/486.3mb], real usage: [511827256/488.1mb], new bytes reserved: [280/280b], usages [request=0/0b, fielddata=0/0b, in_flight_requests=280/280b, accounting=30448/29.7kb]]]
	at org.springframework.data.elasticsearch.core.ElasticsearchExceptionTranslator.translateExceptionIfPossible(ElasticsearchExceptionTranslator.java:69)
	at org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate.translateException(ElasticsearchRestTemplate.java:601)
	at org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate.execute(ElasticsearchRestTemplate.java:584)
	at org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate.search(ElasticsearchRestTemplate.java:399)
	at org.springframework.data.elasticsearch.core.AbstractElasticsearchTemplate.search(AbstractElasticsearchTemplate.java:418)
	at com.future.demo.ConfigElasticsearchInit.init(ConfigElasticsearchInit.java:38)
	at java.base/jdk.internal.reflect.NativeMethodAccessorImpl.invoke0(Native Method)
	at java.base/jdk.internal.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:77)
	at java.base/jdk.internal.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:43)
	at java.base/java.lang.reflect.Method.invoke(Method.java:568)
	at org.springframework.beans.factory.annotation.InitDestroyAnnotationBeanPostProcessor$LifecycleElement.invoke(InitDestroyAnnotationBeanPostProcessor.java:389)
	at org.springframework.beans.factory.annotation.InitDestroyAnnotationBeanPostProcessor$LifecycleMetadata.invokeInitMethods(InitDestroyAnnotationBeanPostProcessor.java:333)
	at org.springframework.beans.factory.annotation.InitDestroyAnnotationBeanPostProcessor.postProcessBeforeInitialization(InitDestroyAnnotationBeanPostProcessor.java:157)
	... 89 more
Caused by: ElasticsearchStatusException[Elasticsearch exception [type=circuit_breaking_exception, reason=[parent] Data too large, data for [<http_request>] would be [511827536/488.1mb], which is larger than the limit of [510027366/486.3mb], real usage: [511827256/488.1mb], new bytes reserved: [280/280b], usages [request=0/0b, fielddata=0/0b, in_flight_requests=280/280b, accounting=30448/29.7kb]]]
	at org.elasticsearch.rest.BytesRestResponse.errorFromXContent(BytesRestResponse.java:178)
	at org.elasticsearch.client.RestHighLevelClient.parseEntity(RestHighLevelClient.java:2484)
	at org.elasticsearch.client.RestHighLevelClient.parseResponseException(RestHighLevelClient.java:2461)
	at org.elasticsearch.client.RestHighLevelClient.internalPerformRequest(RestHighLevelClient.java:2184)
	at org.elasticsearch.client.RestHighLevelClient.performRequest(RestHighLevelClient.java:2137)
	at org.elasticsearch.client.RestHighLevelClient.performRequestAndParseEntity(RestHighLevelClient.java:2105)
	at org.elasticsearch.client.RestHighLevelClient.search(RestHighLevelClient.java:1367)
	at org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate.lambda$search$14(ElasticsearchRestTemplate.java:399)
	at org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate.execute(ElasticsearchRestTemplate.java:582)
	... 99 more
	Suppressed: org.elasticsearch.client.ResponseException: method [POST], host [http://localhost:9200], URI [/cloth_goods/_search?typed_keys=true&max_concurrent_shard_requests=5&search_type=query_then_fetch&batched_reduce_size=512], status line [HTTP/1.1 429 Too Many Requests]
{"error":{"root_cause":[{"type":"circuit_breaking_exception","reason":"[parent] Data too large, data for [<http_request>] would be [511827536/488.1mb], which is larger than the limit of [510027366/486.3mb], real usage: [511827256/488.1mb], new bytes reserved: [280/280b], usages [request=0/0b, fielddata=0/0b, in_flight_requests=280/280b, accounting=30448/29.7kb]","bytes_wanted":511827536,"bytes_limit":510027366,"durability":"PERMANENT"}],"type":"circuit_breaking_exception","reason":"[parent] Data too large, data for [<http_request>] would be [511827536/488.1mb], which is larger than the limit of [510027366/486.3mb], real usage: [511827256/488.1mb], new bytes reserved: [280/280b], usages [request=0/0b, fielddata=0/0b, in_flight_requests=280/280b, accounting=30448/29.7kb]","bytes_wanted":511827536,"bytes_limit":510027366,"durability":"PERMANENT"},"status":429}
		at org.elasticsearch.client.RestClient.convertResponse(RestClient.java:347)
		at org.elasticsearch.client.RestClient.performRequest(RestClient.java:313)
		at org.elasticsearch.client.RestClient.performRequest(RestClient.java:288)
		at org.elasticsearch.client.RestHighLevelClient.performClientRequest(RestHighLevelClient.java:2699)
		at org.elasticsearch.client.RestHighLevelClient.internalPerformRequest(RestHighLevelClient.java:2171)
		... 104 more


```

查看熔断的相关设置

```
GET /_cluster/settings?include_defaults=true&flat_settings=true
# 总内存限制，默认是 JVM 堆的 95%。
"indices.breaker.total.limit" : "95%",
# 默认是 JVM 堆的 40%
"indices.breaker.fielddata.limit" : "40%",
# 默认是 JVM 堆的 60%
"indices.breaker.request.limit" : "60%",
# 默认是 JVM 堆的 100%（但受 `max_content_length`等限制）
"network.breaker.inflight_requests.limit" : "100%",
# 默认是 JVM 堆的 100%
indices.breaker.accounting.limit
```

查看熔断运行统计信息

```
GET /_nodes/stats/breaker
{
  "_nodes" : {
    "total" : 1,
    "successful" : 1,
    "failed" : 0
  },
  "cluster_name" : "docker-cluster",
  "nodes" : {
    "EM6kjOsTSxydo3wTCumrPA" : {
      "timestamp" : 1772253347399,
      "name" : "d1ebc7988ebf",
      "transport_address" : "172.20.40.3:9300",
      "host" : "172.20.40.3",
      "ip" : "172.20.40.3:9300",
      "roles" : [
        "data",
        "ingest",
        "master",
        "ml",
        "remote_cluster_client",
        "transform"
      ],
      "attributes" : {
        "ml.machine_memory" : "23094456320",
        "xpack.installed" : "true",
        "transform.node" : "true",
        "ml.max_open_jobs" : "20"
      },
      "breakers" : {
        "request" : {
          "limit_size_in_bytes" : 322122547,
          "limit_size" : "307.1mb",
          "estimated_size_in_bytes" : 0,
          "estimated_size" : "0b",
          "overhead" : 1.0,
          "tripped" : 0
        },
        "fielddata" : {
          "limit_size_in_bytes" : 214748364,
          "limit_size" : "204.7mb",
          "estimated_size_in_bytes" : 0,
          "estimated_size" : "0b",
          "overhead" : 1.03,
          "tripped" : 0
        },
        "in_flight_requests" : {
          "limit_size_in_bytes" : 536870912,
          "limit_size" : "512mb",
          "estimated_size_in_bytes" : 0,
          "estimated_size" : "0b",
          "overhead" : 2.0,
          "tripped" : 0
        },
        "accounting" : {
          "limit_size_in_bytes" : 536870912,
          "limit_size" : "512mb",
          "estimated_size_in_bytes" : 21320,
          "estimated_size" : "20.8kb",
          "overhead" : 1.0,
          "tripped" : 0
        },
        "parent" : {
          "limit_size_in_bytes" : 510027366,
          # 总内存限制熔断的阈值
          "limit_size" : "486.3mb",
          "estimated_size_in_bytes" : 482141216,
          # 总内存限制熔断的当前使用值
          "estimated_size" : "459.8mb",
          "overhead" : 1.0,
          # 表示触发熔断的次数
          "tripped" : 11
        }
      }
    }
  }
}

```

JVM的内存使用统计信息

```
GET /_nodes/stats/jvm?human=true
{
  "_nodes" : {
    "total" : 1,
    "successful" : 1,
    "failed" : 0
  },
  "cluster_name" : "docker-cluster",
  "nodes" : {
    "EM6kjOsTSxydo3wTCumrPA" : {
      "timestamp" : 1772253490218,
      "name" : "d1ebc7988ebf",
      "transport_address" : "172.20.40.3:9300",
      "host" : "172.20.40.3",
      "ip" : "172.20.40.3:9300",
      "roles" : [
        "data",
        "ingest",
        "master",
        "ml",
        "remote_cluster_client",
        "transform"
      ],
      "attributes" : {
        "ml.machine_memory" : "23094456320",
        "xpack.installed" : "true",
        "transform.node" : "true",
        "ml.max_open_jobs" : "20"
      },
      "jvm" : {
        "timestamp" : 1772253490218,
        "uptime" : "1.2h",
        "uptime_in_millis" : 4559550,
        "mem" : {
          # 堆内存当前使用值
          "heap_used" : "447.2mb",
          "heap_used_in_bytes" : 468930552,
          "heap_used_percent" : 87,
          "heap_committed" : "512mb",
          "heap_committed_in_bytes" : 536870912,
          "heap_max" : "512mb",
          "heap_max_in_bytes" : 536870912,
          "non_heap_used" : "176.1mb",
          "non_heap_used_in_bytes" : 184685936,
          "non_heap_committed" : "189.1mb",
          "non_heap_committed_in_bytes" : 198303744,
          "pools" : {
            "young" : {
              "used" : "3mb",
              "used_in_bytes" : 3145728,
              "max" : "0b",
              "max_in_bytes" : 0,
              "peak_used" : "295mb",
              "peak_used_in_bytes" : 309329920,
              "peak_max" : "0b",
              "peak_max_in_bytes" : 0
            },
            "old" : {
              "used" : "443.2mb",
              "used_in_bytes" : 464736248,
              "max" : "512mb",
              "max_in_bytes" : 536870912,
              "peak_used" : "478.3mb",
              "peak_used_in_bytes" : 501606904,
              "peak_max" : "512mb",
              "peak_max_in_bytes" : 536870912
            },
            "survivor" : {
              "used" : "1mb",
              "used_in_bytes" : 1048576,
              "max" : "0b",
              "max_in_bytes" : 0,
              "peak_used" : "39mb",
              "peak_used_in_bytes" : 40894464,
              "peak_max" : "0b",
              "peak_max_in_bytes" : 0
            }
          }
        },
        "threads" : {
          "count" : 89,
          "peak_count" : 94
        },
        "gc" : {
          "collectors" : {
            "young" : {
              "collection_count" : 939,
              "collection_time" : "5s",
              "collection_time_in_millis" : 5071
            },
            "old" : {
              "collection_count" : 0,
              "collection_time" : "0s",
              "collection_time_in_millis" : 0
            }
          }
        },
        "buffer_pools" : {
          "mapped" : {
            "count" : 15,
            "used" : "60.1kb",
            "used_in_bytes" : 61572,
            "total_capacity" : "60.1kb",
            "total_capacity_in_bytes" : 61572
          },
          "direct" : {
            "count" : 57,
            "used" : "16.1mb",
            "used_in_bytes" : 16982528,
            "total_capacity" : "16.1mb",
            "total_capacity_in_bytes" : 16982527
          },
          "mapped - 'non-volatile memory'" : {
            "count" : 0,
            "used" : "0b",
            "used_in_bytes" : 0,
            "total_capacity" : "0b",
            "total_capacity_in_bytes" : 0
          }
        },
        "classes" : {
          "current_loaded_count" : 21095,
          "total_loaded_count" : 21197,
          "total_unloaded_count" : 102
        }
      }
    }
  }
}

```

结论：

通过下面熔断运行统计信息可以看出总内存限制当前使用值接近阈值会随时触发熔断错误Data too large，并且已经触发11次熔断错误。

```
{
  ...
  
      "breakers" : {
        ...
        
        "parent" : {
          "limit_size_in_bytes" : 510027366,
          # 总内存限制熔断的阈值
          "limit_size" : "486.3mb",
          "estimated_size_in_bytes" : 482141216,
          # 总内存限制熔断的当前使用值
          "estimated_size" : "459.8mb",
          "overhead" : 1.0,
          # 表示触发熔断的次数
          "tripped" : 11
        }
      }
    }
  }
}

```

## ILM 定期删除 30 天前的过期索引

>具体用法请参考本站示例：https://gitee.com/dexterleslie/demonstration/tree/main/demo-elk/elk-es7

Elasticsearch 7 自带 **Index Lifecycle Management (ILM)**，可替代 Curator 实现按“索引年龄”自动删除。

### 原理

- **ILM 策略** `log-30d-delete`：仅包含 delete 阶段，`min_age: 30d` 后执行删除。
- **索引模板** `log-type-lifecycle`：匹配形如 `log_type-yyyy-MM-dd` 的索引（如 `ecommerce-2025-01-28`），为新索引自动挂上上述策略。
- 年龄按**索引创建时间**计算，与 logstash 的按日建索引一致，约等于“保留最近 30 天”。

### 使用步骤

#### 1. 执行一次初始化（创建策略 + 模板）

ES 启动后执行：

```bash
# 若 ES 在本地
export ES_URL=http://localhost:9200
./ilm/init-ilm.sh

# 若 ES 在 docker-compose 中
docker compose exec demo-elk-elasticsearch curl -s -X PUT "http://localhost:9200/_ilm/policy/log-30d-delete" -H "Content-Type: application/json" -d @/path/to/ilm/ilm-policy-30d-delete.json
docker compose exec demo-elk-elasticsearch curl -s -X PUT "http://localhost:9200/_template/log-type-lifecycle" -H "Content-Type: application/json" -d @/path/to/ilm/index-template-log-type-lifecycle.json
```

或在宿主机（ES 端口已映射）：

```bash
cd elk-es7
ES_URL=http://localhost:9200 ./ilm/init-ilm.sh
```

#### 2. 已有索引补挂策略（可选）

在创建模板之前已经存在的、符合命名规则的索引不会自动带上策略，需手动挂一次（每个索引只需一次）：

```bash
# 单个索引示例
curl -X PUT "http://localhost:9200/ecommerce-2025-01-01/_settings" \
  -H "Content-Type: application/json" \
  -d '{"index.lifecycle.name":"log-30d-delete"}'

# 或对多个索引用 _all / 通配（按需替换索引名）
curl -X PUT "http://localhost:9200/ecommerce-*/_settings" \
  -H "Content-Type: application/json" \
  -d '{"index.lifecycle.name":"log-30d-delete"}'
```

之后新创建的、匹配模板的索引会自动带策略，无需再配。

### 索引匹配规则

- 模板 `index_patterns` 为 `["*-*-*-*"]`，匹配如 `ecommerce-2025-01-28`（log_type + 日期）的索引。
- 若 logstash 的 `log_type` 固定为某几种，也可把模板改成更精确的 pattern，例如 `["ecommerce-*","unknown-*"]`，只需在 `index-template-log-type-lifecycle.json` 里改 `index_patterns`。
