# `prometheus`的设置

## 设置自动删除过期数据

>[参考链接](https://stackoverflow.com/questions/59298811/increasing-prometheus-storage-retention)

详细设置请参考 [链接](https://gitee.com/dexterleslie/demonstration/blob/master/demo-prometheus-grafana-alertmanager/demo-docker-compose-prometheus-grafana-alertmanager/docker-compose.yaml#L21)

通过命令行参数`--storage.tsdb.retention.time=30d`设置历史数据最大保留时间，默认15天，此示例中保留30天内的数据。

