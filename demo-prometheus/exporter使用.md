# `exporter`的使用



## `node_exporter`使用

`docker-compose.yaml`内容如下：

```yaml
version: '3.3'

services:
  node_exporter:
    image: prom/node-exporter:v1.5.0
    restart: always
    volumes:
      - /etc/localtime:/etc/localtime:ro
      - /proc:/host/proc:ro
      - /sys:/host/sys:ro
      - /:/rootfs:ro
    command:
      - '--path.procfs=/host/proc'
      - '--path.rootfs=/rootfs'
      - '--path.sysfs=/host/sys'
      - '--collector.filesystem.mount-points-exclude=^/(sys|proc|dev|host|etc)($$|/)'
    network_mode: host
            
```

启动`node_exporter`

```bash
docker compose up -d
```

访问`http://192.168.1.205:9100/metrics`确认`node_exporter`正常运行

配置`prometheus prometheus.yml`拉取`node_exporter`指标

```yaml
scrape_configs:
  - job_name: 'node-exporter'
    scrape_interval: 15s
    static_configs:
    - targets: ['192.168.1.205:9100']
      labels:
        instance: centOS8测试服务器
```

访问`http://localhost:9090/targets`查看目标是否`UP`状态

登录`grafana`导入`id`为`1860`的`node_exporter dashboard`



## `process_exporter`使用

> process-Exporter是一个开源的Prometheus指标导出器，主要用于监控Linux或Unix系统上的进程状态和资源使用情况。

`docker-compose.yaml`内容如下：

```yaml
version: '3.3'

services:
  process-exporter:  
    image: ncabatoff/process-exporter:latest
    # image: ncabatoff/process-exporter:0.7.6  
    # ports:  
    #   - "9256:9256"  # 将容器的9256端口映射到宿主机的9256端口  
    volumes:  
      - /proc:/host/proc:ro  # 挂载宿主机的 /proc 目录到容器内，以便 process-exporter 可以访问
      - ./process-exporter.yml:/config/process-exporter.yml  # 挂载你的配置文件  
    command: ["-config.path", "/config/process-exporter.yml", '-procfs', '/host/proc']  # 指定配置文件的路径
    restart: always
    # privileged: true需要设置，否则无法监控i/o数据
    privileged: true
    network_mode: 'host'
      
```

`process-exporter`的配置文件`process-exporter.yml`内容如下：

```yaml
# 进程名称和对应的命令行/PID文件配置  
process_names:
  # 监控所有进程并使用其进程完整命令行（包括进程所有参数）作为显示进程名
  - name: "{{.Matches}}"
    cmdline: 
    - '.+'
  
# 其他可选配置项  
metrics:  
  # 这里可以定义额外的metrics，但process-exporter默认已经包含了许多常见的metrics  
  # 例如: namedprocess_namegroup_cpu_seconds_total, namedprocess_namegroup_memory_rss_bytes 等  
  
# 模板变量（可选，用于更复杂的命名或匹配）  
# name选项有四个可用的模板变量:  
#   - {{.Comm}} 包含原始可执行文件的基本名称  
#   - {{.ExeBase}} 包含可执行文件的基名  
#   - {{.ExeFull}} 包含可执行文件的完全限定路径  
#   - {{.Username}} 包含有效用户的用户名  
#   - {{.Matches}} map包含应用cmdline regexps产生的所有匹配项  
  
# 注意：如果一个进程符合多个匹配项，只会归属于第一个匹配的groupname组  
  
# 其他Process Exporter的全局配置项（可选）  
# 这些配置项可能包括日志级别、监听地址等，但通常这些都有默认值  
# 可以在process-exporter的官方文档中找到更多关于这些配置项的信息  
  
# 例如：  
# log.level: info  
# web.listen-address: :9256
```

启动`process_exporter`

```bash
docker compose up -d
```

访问`http://192.168.1.205:9256/metrics`确认`process_exporter`正常运行

配置`prometheus prometheus.yml`拉取`node_exporter`指标

```yaml
scrape_configs:
  - job_name: 'process-exporter'
    scrape_interval: 15s
    static_configs:
    - targets: ['192.168.1.205:9256']
      labels:
        instance: centOS8测试服务器
```

访问`http://localhost:9090/targets`查看目标是否`UP`状态

登录`grafana`导入`id`为`4202`的`process_exporter dashboard`，[4202 dashboard grafana介绍](https://grafana.com/grafana/dashboards/4202-named-processes-by-interval-processes-host/)



## `blackbox exporter`使用

### `blackbox exporter`原理

Blackbox Exporter是Prometheus社区提供的一个官方黑盒监控解决方案，主要用于监控网络服务的可用性和性能。其原理基于“黑盒子”测试的概念，即只关注服务的输入和输出，而不考虑服务的内部实现细节。以下是Blackbox Exporter的工作原理和步骤：

原理概述

Blackbox Exporter通过模拟请求来检测目标服务是否正常运行。它支持多种协议，如HTTP、HTTPS、TCP、ICMP、DNS以及gRPC等，能够对这些协议进行探测，并根据响应结果判断服务的状态。

一、配置阶段

1. 目标服务配置：
   - 用户需要在Blackbox Exporter的配置文件（如`blackbox.yml`）中定义多个探测模块（modules），每个模块都包含了对特定类型服务（如HTTP、TCP、ICMP等）的探测逻辑。
   - 这些模块中并不直接包含目标服务器的IP地址和端口，而是定义了如何执行探测（如请求类型、超时时间、验证规则等）。
2. Prometheus配置：
   - 在Prometheus的配置文件中，用户需要定义`scrape_job`，这些作业包含了要监控的目标列表。
   - 每个目标都指定了其IP地址、端口以及要使用的Blackbox Exporter模块。

二、探测阶段

1. 请求发送：
   - Prometheus根据配置定期向Blackbox Exporter发送HTTP请求，这些请求中包含了要探测的目标信息（如IP地址、端口）和要使用的模块。
   - Blackbox Exporter接收到请求后，根据请求中的参数（模块和目标信息）构建相应的探测请求（如HTTP GET请求、TCP连接请求等）。
2. 执行探测：
   - Blackbox Exporter向目标服务器发送探测请求，并等待响应。
   - 根据探测请求的类型（HTTP、TCP、ICMP等），Blackbox Exporter会执行相应的探测逻辑，如发送HTTP请求并等待HTTP响应、尝试建立TCP连接等。

三、响应处理与指标输出

1. 响应接收：
   - Blackbox Exporter接收到目标服务器的响应后，会根据预定义的验证规则对响应进行处理。
   - 这些验证规则可能包括检查HTTP状态码、TCP连接是否成功建立、ICMP回显请求是否收到响应等。
2. 状态判断：
   - 根据响应和验证规则的结果，Blackbox Exporter判断目标服务的状态（如正常、异常、超时等）。
3. 指标输出：
   - Blackbox Exporter将探测结果以Prometheus指标格式输出，这些指标包括目标服务的可用性、响应时间、响应状态码等。
   - Prometheus收集这些指标，并可以进一步用于生成监控报告、图表和警报。

四、总结

Blackbox Exporter的原理基于“黑盒子”测试的概念，即只关注服务的输入和输出，而不考虑服务的内部实现细节。通过模拟请求和检查响应，Blackbox Exporter能够快速、准确地判断服务的状态，从而帮助用户及时发现和解决问题。其工作流程清晰简单，可以方便地应用于各种复杂的监控场景。

### `blackbox exporter`配置

`blackbox.yml`配置文件如下：

```yaml
modules:
  # 配置http get
  # prometheus module参数指定的值
  http_get_2xx:  
    prober: http  
    timeout: 5s  
    http:  
      method: GET  
      valid_status_codes: [200, 201, 202, 203, 204, 205, 206, 207, 208, 226]

  # 配置http post JSON
  # prometheus module参数指定的值
  http_post_json:  
    prober: http  
    timeout: 10s  
    http:  
      method: POST  
      headers:  
        Content-Type: application/json  
      body: '{"key":"value"}'  
      valid_status_codes: [200, 201]  
      # 其他HTTP POST JSON请求的配置...  
  
  # 配置http自定义头
  # prometheus module参数指定的值
  http_get_custom_header:  
    prober: http  
    timeout: 5s  
    http:  
      method: GET
      headers:
        # 自定义http请求头
        mySecret: xxxxx 
      valid_status_codes: [200]  
      # 其他带有自定义HTTP请求头的配置...
```



### 使用`docker compose`运行`blackbox exporter`

>注意：`blackbox exporter`只需要运行在网络连同的环境即可，因为它代理`prometheus`的`probe`请求探测目标服务器是否在线。

示例的详细配置请参考 [链接](https://gitee.com/dexterleslie/demonstration/blob/master/demo-prometheus-grafana-alertmanager/demo-blackbox-exporter)

启动基于`spring-boot`的探测目标`demo-assistant`，用于协助`blackbox exporter`通过`http`探测目标是否在线 [代码参考](https://gitee.com/dexterleslie/demonstration/tree/master/demo-prometheus-grafana-alertmanager/demo-blackbox-exporter/demo-assistant)

启动`blackbox exporter`

```bash
docker compose up -d
```

访问`blackbox exporter web UI`，http://localhost:9115/

通过`demo-assistant`探测目标调试`blackbox exporter`，http://localhost:9115/probe?target=192.168.235.128:18080/api/v1/alive/check&module=http_get_custom_header&debug=true，其中`localhost:9115/probe`是`blackbox exporter`服务所在的主机`ip`地址，`taget=192.168.235.128:18080/api/v1/alive/check`是`demo-assistant`探测目标所在的主机`ip`地址，`module=http_get_custom_header`是`blackbox exporter`中`blackbox.yml`文件配置的`module`，`debug=true`表示返回`probe`的调试信息。

关闭`blackbox exporter`

```bash
docker compose down -v
```



### `blackbox exporter`的`prometheus`配置

提醒：

- `prometheus`查看`blackbox exporter dashboard id`为`7587`，这个`dashboard`已经自动导入到`prometheus`。
- `blackbox exporter`的`prometheus`设置主要是配置`prometheus.yml`文件。

`prometheus`的`blackbox exporter`详细配置请参考 [链接](https://gitee.com/dexterleslie/demonstration/blob/master/demo-prometheus-grafana-alertmanager/demo-blackbox-exporter/prometheus.yml)

`blackbox exporter`的`prometheus.yml`配置如下：

```yml
# 搜刮配置
scrape_configs:
  - job_name: 'blackbox-http'  
    metrics_path: /probe  
    params:  
      # 使用blackbox.yml中配置的http_get_custom_header模块  
      module: [http_get_custom_header]
    static_configs:
      # 因为没有多个不同的target，所以临时增加两个相同的target用于测试用途
      - targets:
        # 探测目标  
        - http://192.168.235.128:18080/api/v1/alive/check
        labels:
          instance: target-1
      - targets:
        # 探测目标  
        - http://192.168.235.128:18080/api/v1/alive/check
        labels:
          instance: target-2
    relabel_configs:
      # 重标签配置将__address__（Prometheus默认为每个目标提供的地址标签）的值复制到__param_target标签。这是必需的，因为Blackbox Exporter通过__param_target参数接收目标地址。
      - source_labels: [__address__]  
        target_label: __param_target
      # 注意：因为static_configs指定target时已经指定instance标签值，所以这里不需要relabel标签
      # 重标签配置将__param_target的值复制到instance标签。这通常用于在Prometheus的UI或Grafana等监控工具中更清晰地识别目标实例。
      #- source_labels: [__param_target]  
      #  target_label: instance
      # 重标签配置将__address__标签的值替换为Blackbox Exporter的地址（blackbox_exporter:9115）。这是因为实际的探测请求应该发送给Blackbox Exporter，而不是直接发送给目标服务。Blackbox Exporter会代理这些请求到目标服务，并根据响应生成度量指标。
      - target_label: __address__  
        # 指定blackbox exporter的ip地址和端口
        replacement: blackbox_exporter:9115
```

启动`prometheus`

```bash
docker compose up -d
```

访问`grafana`查看`blackbox exporter dashboard`，http://localhost:3000/

关闭`prometheus`

```bash
docker compose down -v
```



## `mysqld-exporter`使用

>[监控MySQL运行状态：MySQLD Exporter](https://yunlzheng.gitbook.io/prometheus-book/part-ii-prometheus-jin-jie/exporter/commonly-eporter-usage/use-promethues-monitor-mysql)

`mysqld-exporter`详细用法请参考 [链接](https://gitee.com/dexterleslie/demonstration/tree/master/demo-prometheus-grafana-alertmanager/demo-mysqld-exporter)

`docker-compose.yaml`运行`mysqld-exporter`

```yaml
version: '3'

services:  
  mysqld_exporter:  
    image: prom/mysqld-exporter:v0.12.1
    ports:  
      - "9104:9104"
    environment:
      - DATA_SOURCE_NAME=root:123456@(192.168.235.128:3306)/
```

`prometheus`配置收集`mysqld-exporter`数据

```yaml
scrape_configs:
  # prometheus收集mysqld exporter容器的运行数据
  - job_name: 'mysqld_exporter'
    scrape_interval: 15s
    static_configs:
    - targets: ['mysqld_exporter:9104']
```

运行`mysqld-exporter`容器后，访问`http://localhost:9104/`查看`mysqld-exporter`是否正常运行

`grafana`导入`7362 dashboards`
