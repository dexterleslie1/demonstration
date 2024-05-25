# promql的用法

> PromQL（Prometheus Query Language）是Prometheus监控系统内置的一种查询语言，用于查询、聚合和转换时间序列数据。PromQL被广泛应用于Prometheus的日常应用中，包括数据查询、可视化以及告警处理等方面。

> 启动`promql`测试实验环境：
>
> - 启动`prometheus`服务，参考<a href='/prometheus-grafana-alertmanager/使用docker-compose运行prometheus-grafana-alertmanager/' target='_blank'>链接</a>
> - 启动自定义`exporter`服务，参考<a href='/prometheus/自定义exporter/' target='_blank'>链接</a>

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

