# `docker-compose`运行`prometheus`

> 功能介绍：自动运行`prometheus`、`grafana`、`alertmanager`、`pushgateway`、`node_exporter`（监控宿主机）、`cAdvisor`服务（实时收集、聚合、处理和导出运行中容器的信息，包括CPU使用情况、内存使用情况、网络吞吐量及文件系统使用情况等）；自动导入`dashboards`、`datasources`。
>
> 参考`gitee`别人开源的仓库编写`docker-compose`运行`prometheus+grafana+alertmanager https://gitee.com/linge365/docker-prometheus/tree/master`

使用`docker-compose`运行`prometheus+grafana+alertmanager+pushgateway`的详细配置文件请参考`https://gitee.com/dexterleslie/demonstration/tree/master/demo-prometheus-grafana-alertmanager/demo-docker-compose-prometheus-grafana-alertmanager`

运行`prometheus+grafana+alertmanager+pushgateway`服务

```bash
docker compose up -d
```

检查相关服务是否运行正常

- 打开浏览器访问`http://localhost:9090/`检查`prometheus`是否运行正常（有界面显示表示正常）
- 打开浏览器访问`http://localhost:9090/targets`通过`prometheus`查看其他服务器是否`UP`状态，`UP`状态表示运行正常
- 打开浏览器访问`http://localhost:3000/`检查`grafana`是否运行正常（能够登录表示正常），帐号和密码都是：admin
- 打开浏览器访问`http://localhost:9093/`检查`alertmanager`是否运行正常（有界面显示表示正常）
- 打开浏览器访问`http://localhost:9100/metrics`检查`node_exporter`是否运行正常（有数据显示表示正常）
- 打开浏览器访问`http://localhost:8080`检查`cAdvisor`是否运行正常（有界面显示表示正常，注意：因为访问`Google`才能打开`cAdvisor`界面，所以使用能够访问`Google`的浏览器访问）

关闭服务

```bash
docker compose down -v
```

如果需要测试使用`node_exporter`监控其他主机操作系统，请参考 <a href="/prometheus-grafana-alertmanager/exporter使用.html#使用docker-compose运行node-exporter" target="_blank">链接</a> 在系统上使用`docker compose`运行`node_exporter`服务。

如果需要测试使用`process_exporter`监控其他主机操作系统进程，请参考 <a href="/prometheus-grafana-alertmanager/exporter使用.html#使用docker-compose运行process-exporter" target="_blank">链接</a> 在系统上使用`docker compose`运行`process_exporter`服务。