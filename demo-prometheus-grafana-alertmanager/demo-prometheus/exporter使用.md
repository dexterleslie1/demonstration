# exporter的使用

## 使用`docker-compose`运行`node_exporter`

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
    ports:
      - '9100:9100'
            
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

## 使用`docker-compose`运行`process_exporter`

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
    network_mode: 'host'
      
```

`process-exporter`的配置文件`process-exporter.yml`内容如下：

```yaml
# 进程名称和对应的命令行/PID文件配置  
process_names:
  # 监控所有进程并使用其进程完全限定路径作为指标名称
  - name: "{{.ExeFull}}"
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