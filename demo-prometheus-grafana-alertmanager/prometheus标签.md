# 标签的用法



## 标签的作用

labels（标签）是定义和区分指标（metrics）维度的关键部分。它们允许用户按特定的属性对指标进行分组、过滤和聚合，从而提供丰富的上下文信息来分析和监控系统的性能、状态和行为。

以下是 labels 的主要作用：

1. 标识和区分指标实例：
   - 在 Prometheus 中，一个指标可能由多个具有不同标签值的实例组成。例如，一个名为 `http_requests_total` 的指标可能有一个 `method` 标签来区分 GET、POST 等不同的 HTTP 请求方法。
2. 数据聚合：
   - 通过选择特定的标签值，用户可以聚合来自不同实例的指标数据。例如，可以计算所有 GET 请求的总数，或者仅计算来自特定服务的 POST 请求的数量。
3. 数据过滤：
   - 在查询时，labels 可以作为过滤条件，帮助用户仅检索他们感兴趣的指标数据。例如，可以只查询特定环境（如 `production` 或 `staging`）的指标。
4. 数据切片：
   - 使用 labels，用户可以根据不同的维度来“切片”他们的指标数据，从而在不同的上下文中比较和分析数据。例如，可以比较不同服务、不同环境或不同版本之间的性能指标。
5. 动态定义：
   - Prometheus 支持动态标签，这意味着可以在运行时根据监控的目标或环境动态地添加或修改标签。这提供了极大的灵活性，允许用户根据需要自定义他们的监控解决方案。
6. 告警条件：
   - 在设置告警规则时，labels 可以作为触发告警的条件之一。例如，可以设置一个告警规则，当某个服务的错误率（由 `error` 标签标识）超过某个阈值时触发告警。
7. 可视化：
   - Prometheus 提供了多种可视化工具（如 Grafana），这些工具利用 labels 来帮助用户以图形化方式展示和分析他们的指标数据。通过选择不同的标签组合，用户可以创建各种仪表板（dashboards）来监控他们的系统。
8. 数据保留策略：
   - 在一些场景中，用户可能希望根据标签值来定义不同的数据保留策略。例如，他们可能希望保留更长时间的关键业务指标的历史数据，而仅保留较短时间的非关键指标的数据。

总之，Prometheus 的 labels 提供了强大的功能来组织、分析和查询指标数据，帮助用户更好地理解和监控他们的系统。



## 标签使用场景

>标签的详细用法请参考 [demo-simpleclient](https://gitee.com/dexterleslie/demonstration/tree/master/demo-prometheus-grafana-alertmanager/demo-prometheus-simpleclient)

定义`Counter`指标时指定标签的类型`instance`（用于根据主机实例分类指标）

```java
Counter myCounter = Counter.build()
            .name("my_custom_counter")
            .help("这是自定义counter指标类型")
            // 标签的分类，例如：此例子中标签的分类为实例
            .labelNames("instance")
            .register();
```

操作`Counter`指标时指定标签类型`instance`的值为`gce0`、`gce1`、`gce2`分别代表实例`0`、`1`、`2`

```java
public static List<String> instanceList = new ArrayList<>(Arrays.asList("gce0", "gce1", "gce2"));

Random random = new Random();
String instance = instanceList.get(random.nextInt(instanceList.size()));
// 操作Counter指标数据时指定instance标签的值为gce0、gce1、gce2
this.myCounter.labels(instance).inc();
```

