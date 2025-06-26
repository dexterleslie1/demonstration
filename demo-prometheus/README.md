# `prometheus + grafana + pushgateway + alertmanager`使用



## `todo`

- `prometheus`监控`k8s`
- `prometheus`监控`spring-cloud（自动发现）`
- 使用`k8s`运行`prometheus`



## 什么是`prometheus`？

Prometheus是一个开源的服务监控系统和时序数据库。其主要功能包括收集和存储时间序列数据，如从各种来源（包括应用程序、服务、操作系统和网络设备等）获取的数据，并使用自己的数据模型和查询语言PromQL（Prometheus Query Language）来存储和处理这些数据。Prometheus能够实时监控各种指标，并根据预定义的规则进行警报和通知。

Prometheus的核心组件Prometheus server会定期从静态配置的监控目标或者基于服务发现自动配置的目标中拉取数据。当新拉取到的数据大于配置的内存缓存区时，数据就会持久化到存储设备当中。Prometheus能够直接把API Server作为服务发现系统使用，进而动态发现和监控集群中的所有可被监控的对象。

Prometheus还支持高可用性和分布式架构，可以在大规模环境中提供稳定的监控服务。它使用了一种名为“联邦”的架构，将数据存储和查询功能分布在多个节点上，这种架构允许Prometheus处理大量的监控数据，同时保持高性能和低延迟。

Prometheus被广泛应用于各种领域，如监控和警报系统、分布式系统的性能监控、自动化运维、数据分析、物联网监控、日志分析和安全监控等。



## `prometheus`数据采集的方式有哪些呢？

Prometheus数据采集的方式主要有两种：

1. 推送方式：推送模式是进程主动将指标推送给prometheus服务器，但是在架构设计上并不是直接推送的，如下，是prometheus官网的架构图。推送的指标是推送给了pushgateway，然后prometheus server 从推送网关上面拉取指标信息。像短时定时任务我们可以采用推送模式，推送定时任务相关的指标。
2. 拉去方式：这是Prometheus更常用的数据采集方式。在Pull方式中，Prometheus服务器主动从被监控的应用程序拉取指标数据。同样，被监控的应用程序需要将指标数据暴露为一个HTTP端点，Prometheus服务器定期请求这个端点以获取最新的指标数据。



## `pushgateway`是什么呢？

Pushgateway是Prometheus整体监控方案的一个功能组件，作为一个独立的工具存在。它主要用于Prometheus无法直接拿到监控指标的场景，例如监控源位于防火墙之后，Prometheus无法穿透防火墙；或者目标服务没有可抓取监控数据的端点等多种情况。在这些场景中，可以通过部署Pushgateway来解决问题。

当部署该组件后，监控源通过主动发送监控数据到Pushgateway，再由Prometheus定时获取信息，实现资源的状态监控。Pushgateway的工作流程大致是：监控源通过Post方式，发送数据到Pushgateway（路径为/metrics）。然后Prometheus服务端设置任务，定时获取Pushgateway上面的监控指标。

Pushgateway支持两种数据推送方式：Prometheus Client SDK推送和API推送。它主要适用于临时性的任务，各个目标主机可以上报数据到Pushgateway，然后Prometheus server统一从Pushgateway拉取数据。同时，Pushgateway也可以作为数据中转站，支持数据生产者随时将数据推送过来，尤其是那些瞬时生成的数据需要一个中转站临时存放。此外，Pushgateway还可以统一收集并持久化推送给它的所有监控数据，起到给Prometheus减压的作用。



## `Prometheus` 设置

### 设置自动删除过期数据

>[参考链接](https://stackoverflow.com/questions/59298811/increasing-prometheus-storage-retention)

详细设置请参考 [链接](https://gitee.com/dexterleslie/demonstration/blob/main/demo-prometheus/demo-docker-compose-prometheus/docker-compose.yaml#L21)

通过命令行参数`--storage.tsdb.retention.time=30d`设置历史数据最大保留时间，默认15天，此示例中保留30天内的数据。

```yaml
version: '3.3'

services:
  prometheus:
    image: prom/prometheus:v2.37.6
    restart: always
    deploy:
      resources:
        limits:
          memory: 512M
    volumes:
      - /etc/localtime:/etc/localtime:ro
      - ./prometheus.yml:/etc/prometheus/prometheus.yml
      - ./alert-rule.yml:/etc/prometheus/alert-rule.yml
      - data-demo-prometheus:/prometheus
    command:
      - '--config.file=/etc/prometheus/prometheus.yml'
      - '--storage.tsdb.path=/prometheus'
      - '--web.console.libraries=/usr/share/prometheus/console_libraries'
      - '--web.console.templates=/usr/share/prometheus/consoles'
      #热加载配置
      - '--web.enable-lifecycle'
      #api配置
      #- '--web.enable-admin-api'
      #历史数据最大保留时间，默认15天
      - '--storage.tsdb.retention.time=30d'  
    ports:
      - '9090:9090'
```

