# promql的用法



## 什么是`promql`呢？

PromQL（Prometheus Query Language）是Prometheus监控系统内置的一种查询语言，用于查询、聚合和转换时间序列数据。PromQL被广泛应用于Prometheus的日常应用中，包括数据查询、可视化以及告警处理等方面。



## 启动`promql`测试实验环境

1. 启动`prometheus`服务，参考 <a href='/prometheus-grafana-alertmanager/使用docker-compose运行prometheus-grafana-alertmanager/' target='_blank'>链接</a>
2. 启动自定义`exporter`服务，参考 <a href='/prometheus-grafana-alertmanager/prometheus自定义exporter.html' target='_blank'>链接</a>
3. 启动`openresty prometheus`服务（协助测试`promql`创建测试数据），参考 <a href="/openresty/监控.html#基于prometheus监控" target="_blank">链接</a>



## 选择器

### 即时向量选择器

> 选择特定时间点的样本数据，格式为`metric_name`。

查询`my_custom_counter_total Counter`即时指标值，指标关联的多个标签也会全部显示

```
my_custom_counter_total
```

### 区间向量选择器

> 选择一段时间范围内的样本数据，格式为`metric_name[time_range]`，其中`time_range`可以是如`5m`（表示最近5分钟）这样的时间范围。

查询`my_custom_counter_total Counter`指标最近`1m`的所有值

```
my_custom_counter_total[1m]
```

### 标签选择器

> 使用`{}`来指定过滤条件，例如`{label_name="label_value"}`。

查询标签为`instance=gce1`的`my_custom_counter_total Counter`指标最近`1m`的所有值

```
my_custom_counter_total{exported_instance="gce1"}[1m]
```



## `offset`用法

查询1分钟前指标值

```bash
nginx_http_requests_total{instance="target-1", host!="127.0.0.1"} offset 1m
```

查询1分钟前的2分钟内指标值列表

```bash
nginx_http_requests_total{instance="target-1", host!="127.0.0.1"}[2m] offset 1m
```



## `rate`和`irate`函数用法

使用`rate`算法计算1分钟前的2分钟内的请求速率

```bash
rate(nginx_http_requests_total{instance="target-1", host!="127.0.0.1"}[2m] offset 1m)
```

使用`irate`算法计算1分钟前的2分钟内的请求速率

```bash
irate(nginx_http_requests_total{instance="target-1", host!="127.0.0.1"}[2m] offset 1m)
```

检测`CC`攻击`alertmanager`规则

```bash
irate(nginx_http_requests_total{host!="127.0.0.1"}[5m]) > 50 and irate(nginx_http_requests_total{host!="127.0.0.1"}[5m] offset 30m) > 0 and irate(nginx_http_requests_total{host!="127.0.0.1"}[5m])/irate(nginx_http_requests_total{host!="127.0.0.1"}[5m] offset 30m) >= 10
```

- `irate(nginx_http_requests_total{host!="127.0.0.1"}[5m]) > 50`表示当前`QPS`大于`50`
- `irate(nginx_http_requests_total{host!="127.0.0.1"}[5m] offset 30m) > 0`表示`30`分钟前`QPS`大于`0`
- `irate(nginx_http_requests_total{host!="127.0.0.1"}[5m])/irate(nginx_http_requests_total{host!="127.0.0.1"}[5m] offset 30m) >= 10`表示当前`QPS`是`30`分钟前`QPS`的`10`倍



### `rate`和`irate`区别

假设我们有一个时间序列`http_requests_total`，它记录了某个API服务器在一段时间内接收到的HTTP请求总数。以下是该时间序列的部分数据点，这些数据点以时间戳（假设以秒为单位）和对应的HTTP请求总数表示：

| 时间戳 | HTTP请求总数 |
| ------ | ------------ |
| 160000 | 100          |
| 160060 | 250          |
| 160120 | 450          |
| 160180 | 600          |
| 160240 | 750          |

现在，我们想要计算在过去一段时间内的HTTP请求平均增长率和瞬时增长率。

**使用rate函数**

假设我们想要计算过去2分钟（即120秒）内的平均增长率。

- **查询**：`rate(http_requests_total[120s])`
- 计算：我们需要考虑时间戳从160120到160240的数据点。计算这些点之间的增量并求平均值，但实际上`rate`函数计算的是每秒的平均增长率。所以，我们直接看从450增长到750的总增量，然后除以时间范围（120秒）：(750−450)/120=2.5请求/秒
- **结果**：过去120秒内，每秒2.5个HTTP请求。

**使用irate函数**

接下来，我们计算同一时间范围内的瞬时增长率。

- **查询**：`irate(http_requests_total[120s])`
- 计算：`irate`函数只关注时间范围内的最后两个数据点，即160180和160240。计算这两个点之间的增量，并尝试给出每秒的瞬时变化率。但实际上，由于我们只有一个60秒的时间间隔（从160180到160240），`irate`会基于这个间隔来计算：（750-600)/60=2.5i请求/秒，但请注意，PromQL中的`irate`函数会自动处理时间间隔，并给出每秒的瞬时变化率，因此你不需要手动计算时间差。这里的5请求/秒只是一个基于给定数据点的近似值，用于说明`irate`的计算方法。
- **结果**：在过去120秒内的某个时间段（实际上是最后60秒），瞬时变化率表明每秒2.5个HTTP请求。然而，重要的是要理解`irate`给出的是基于最后两个数据点的瞬时估算，它可能无法反映整个时间范围内的平均情况。

通过这个例子，我们可以看到`rate`和`irate`在计算时间序列变化率时的不同。`rate`提供了更平滑、更稳定的平均增长率，而`irate`则能够更快地响应数据中的最新变化。



## 分组

按`exported_instance`分组求指标的`irate`，注意：不能删除`sum`函数，如果删除`sum`函数，`irate`返回关于多个标签的指标，此时指定`by (exported_instance)`时`promql`不知道如何聚合被`group by (exported_instance)`后重复的指标。

```bash
sum(irate(my_custom_counter_total[1m])) by (exported_instance)
```

