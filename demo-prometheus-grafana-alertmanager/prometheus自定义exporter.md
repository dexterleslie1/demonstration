# 自定义`exporter`

## 使用`simpleclient`编写自定义`exporter（又称为metrics endpoint）`

参考[`demo-simpleclient`](https://gitee.com/dexterleslie/demonstration/tree/master/demo-prometheus-grafana-alertmanager/demo-prometheus-simpleclient)编写自定`exporter`

在`prometheus.yml`配置文件中配置自定义`exporter`为拉取目标

```yml
scrape_configs:
  - job_name: 'prometheus'
    # 覆盖全局默认值，每15秒从该作业中刮取一次目标
    scrape_interval: 15s
    static_configs:
    # `prometheus`会自动添加`http://.../metrics`拉取`metrics`数据
    - targets: ['192.168.1.181:8081']
```

通过`prometheus`目标列表`http://localhost:9090/targets`查看新配置的`exporter`是否`UP`状态