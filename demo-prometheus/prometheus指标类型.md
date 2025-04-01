# 指标类型

> 指标类型的使用示例请参考 [simpleclient](https://gitee.com/dexterleslie/demonstration/tree/master/demo-prometheus-grafana-alertmanager/demo-prometheus-simpleclient)

## Counter（计数器）

**Counter是一个累计类型的数据指标，代表单调递增的计数器**。Counter类型的指标用于累计值，例如记录请求次数、任务完成数、错误发生次数等。Counter的值只能在重新启动时增加或重置为0，它只对外暴露了增加接口（如Inc()和Add(float64)），以确保其值只增不减。但是，若Exporter重启了，则Counter类型的Metric的值会重新从0开始。

在使用Counter指标时，通常会结合rate()方法获取该指标在某个时间段的变化率。

下面是`Counter`指标一个样板数据

```
# HELP my_custom_counter_total 这是自定义counter指标类型
# TYPE my_custom_counter_total counter
my_custom_counter_total{instance="gce0",} 80.0
my_custom_counter_total{instance="gce1",} 75.0
my_custom_counter_total{instance="gce2",} 89.0
```

上面的样板数据解析如下：

- `# HELP my_custom_counter_total 这是自定义counter指标类型`是指标的描述
- `# TYPE my_custom_counter_total counter`是指标的名称和类型
- `my_custom_counter_total{instance="gce0",} 80.0`指标标签为`gce0`的`counter`值为`80.0`

## Gauge（仪表盘）

Gauge（仪表盘）是一种度量指标类型，用于描述某个指标当前的状态或值。与Counter（计数器）不同，Gauge的值可以任意变化，即可以增长也可以减少。它适用于描述如系统内存余量、CPU使用率、队列数量等实时变化的指标。

应用场景：

- 系统资源监控：如CPU使用率、内存余量等。
- 业务队列监控：实时观察队列数量，及时发现堆积情况。
- 其他实时变化的指标：如网络带宽使用情况、磁盘I/O等。

## Histogram（直方图）

Histogram（直方图）是一种度量类型，它用于对观察结果（通常是请求的持续时间或响应大小）进行采样，并在预定义的桶（bucket）中进行累积。这些桶定义了度量的不同范围，允许您高效地计算聚合数据（如平均值、中位数、分位数等），而无需原始数据。

Histogram的主要特点如下：

1. **桶（Buckets）**：Histogram预先定义了一系列的桶，每个桶代表一个数值范围。当新的观察结果出现时，它会被分配到最接近且不超过其值的桶中，并累加到该桶的计数中。同时，所有比当前最大桶值还要大的观察结果都会被累加到“+Inf”桶中。
2. **累积分布**：Histogram的桶是累积的，即每个桶的计数都包含了比它小的所有桶的计数。这允许您快速计算给定观察结果的比例，例如计算小于或等于某个特定值的观察结果的比例。
3. **原始数据丢失**：由于Histogram只存储桶的计数，而不存储原始的观察结果，因此它不会消耗过多的存储空间。但是，这也意味着您无法从Histogram中检索原始的观察结果。
4. **使用场景**：Histogram特别适用于描述如请求持续时间、响应大小等具有连续数值范围的度量。它允许您高效地计算这些度量的分布情况，并识别出可能的性能瓶颈或异常值。
5. **查询函数**：PromQL（Prometheus Query Language）提供了许多用于查询Histogram数据的函数，如`histogram_quantile()`用于计算分位数，`sum()`用于计算总和，`avg()`用于计算平均值等。这些函数可以帮助您从Histogram数据中提取有价值的信息。

总之，Prometheus的Histogram是一种用于描述连续数值范围度量分布情况的度量类型。它通过预定义的桶来累积观察结果，并允许您高效地计算聚合数据，而无需存储原始的观察结果。

下面是模拟`http`请求响应时间的`histogram`数据

```
# HELP http_request_duration_milliseconds Request latency in milliseconds.
# TYPE http_request_duration_milliseconds histogram
http_request_duration_milliseconds_bucket{method="get",path="/api/v1/data",le="10.0",} 0.0
http_request_duration_milliseconds_bucket{method="get",path="/api/v1/data",le="100.0",} 0.0
http_request_duration_milliseconds_bucket{method="get",path="/api/v1/data",le="200.0",} 1.0
http_request_duration_milliseconds_bucket{method="get",path="/api/v1/data",le="300.0",} 2.0
http_request_duration_milliseconds_bucket{method="get",path="/api/v1/data",le="500.0",} 3.0
http_request_duration_milliseconds_bucket{method="get",path="/api/v1/data",le="1000.0",} 3.0
http_request_duration_milliseconds_bucket{method="get",path="/api/v1/data",le="5000.0",} 16.0
http_request_duration_milliseconds_bucket{method="get",path="/api/v1/data",le="10000.0",} 37.0
http_request_duration_milliseconds_bucket{method="get",path="/api/v1/data",le="30000.0",} 125.0
http_request_duration_milliseconds_bucket{method="get",path="/api/v1/data",le="+Inf",} 244.0
http_request_duration_milliseconds_count{method="get",path="/api/v1/data",} 244.0
http_request_duration_milliseconds_sum{method="get",path="/api/v1/data",} 7469203.0
```

上面的样板数据解析如下：

- `http_request_duration_milliseconds_count{method="get",path="/api/v1/data",} 244.0`表示样本总数为`244`个
- `http_request_duration_milliseconds_sum{method="get",path="/api/v1/data",} 7469203.0`表示`244`个样本的毫秒总和为`7469203`
- `http_request_duration_milliseconds_bucket{method="get",path="/api/v1/data",le="30000.0",} 125.0`表示有`125`个样本小于`30000`毫秒
- `http_request_duration_milliseconds_bucket{method="get",path="/api/v1/data",le="200.0",} 1.0`表示有`1`个样本小于`200`毫秒

## Summary（摘要）

Summary是一种度量类型，用于表示一段时间内的样本数据的统计摘要。与Histogram不同，Summary在客户端进行样本数据的聚合计算，然后将聚合结果（如count、sum、avg、quantile等）发送到Prometheus服务器。

Summary的主要特点包括：

1. **客户端聚合**：Summary在客户端（即被监控的应用程序或服务）中收集样本数据，并计算各种统计信息（如平均值、分位数等）。这些聚合结果随后被发送到Prometheus服务器进行存储和查询。
2. **统计信息**：Summary可以提供以下统计信息：
   - `count`：表示观察值的总数。
   - `sum`：表示观察值的总和。
   - `avg`：表示观察值的平均值（通常通过`sum / count`计算得出）。
   - `quantile`：表示观察值的分位数，例如中位数（p50）、90%分位数（p90）等。这些分位数是通过客户端的滑动时间窗口算法（如HDRHistogram）计算得出的近似值。
3. **存储和传输效率**：由于Summary在客户端进行聚合计算，因此发送到Prometheus服务器的数据量相对较少。这有助于提高存储和传输效率，特别是在高负载场景下。
4. **适用场景**：Summary适用于那些需要在客户端进行复杂计算或需要近似分位数的场景。例如，当您希望监控某个服务的请求延迟时，可以使用Summary来收集延迟样本数据，并在客户端计算平均值和分位数。

需要注意的是，由于Summary的分位数是通过近似算法计算得出的，因此其精度可能不如Histogram高。此外，由于Summary在客户端进行聚合计算，因此可能会增加客户端的负载。在选择使用Summary还是Histogram时，需要根据具体的应用场景和需求进行权衡。

下面是模拟`http`请求响应时间的`summary`数据

```
# HELP http_request_duration_milliseconds_summary HTTP request latencies in milliseconds.
# TYPE http_request_duration_milliseconds_summary summary
http_request_duration_milliseconds_summary{quantile="0.1",} 11184.0
http_request_duration_milliseconds_summary{quantile="0.5",} 33534.0
http_request_duration_milliseconds_summary{quantile="0.9",} 55452.0
http_request_duration_milliseconds_summary{quantile="0.99",} 59251.0
http_request_duration_milliseconds_summary_count 75.0
http_request_duration_milliseconds_summary_sum 2479366.0
```

上面的样板数据解析如下：

- `http_request_duration_milliseconds_summary_count 75.0`表示样本总数
- `http_request_duration_milliseconds_summary_sum 2479366.0`表示样本值总和，当前值为`2479366.0`毫秒
- `http_request_duration_milliseconds_summary{quantile="0.99",} 59251.0`表示`99%`分位为`59251.0`（小于`59251.0`毫秒的请求有`99%`）
- `http_request_duration_milliseconds_summary{quantile="0.1",} 11184.0`表示小于`11184.0`毫秒的请求有`10%`