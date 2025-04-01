# `grafana`自动导入`dashboards`和`datasources`

> `datasource`数据源`provisioning yaml`文件配置[链接](https://grafana.com/docs/grafana/latest/administration/provisioning/#example-data-source-config-file)

示例的详细代码请参考 [链接](https://gitee.com/dexterleslie/demonstration/tree/master/demo-prometheus-grafana-alertmanager/demo-grafana-provisioning-automatically)

`docker-compose.yaml`内容如下：

```yaml
version: "3.0"

services:
  demo-grafana:
    image: grafana/grafana:9.4.3
    environment:
      - TZ=Asia/Shanghai
      - GF_PATHS_PROVISIONING=/etc/grafana/provisioning
    volumes:
      - ./datasources.yaml:/etc/grafana/provisioning/datasources/default.yaml:ro
      - ./dashboards.yaml:/etc/grafana/provisioning/dashboards/default.yaml:ro
      - ./jmeter.json:/var/lib/grafana/dashboards/jmeter.json:ro
    ports:
      - 3000:3000
    restart: always
    logging:
      driver: json-file
      options:
        max-size: "64m"
        max-file: "5"
```

`jmeter.json`（用于创建`jmeter dashboard`）内容如下：

```json
{
    "annotations": {
      "list": [
        {
          "builtIn": 1,
          "datasource": "$data_source",
          "enable": true,
          "hide": false,
          "iconColor": "rgba(0, 211, 255, 1)",
          "limit": 100,
          "name": "Start/stop marker",
          "query": "select text from events where $timeFilter",
          "showIn": 0,
          "textColumn": "",
          "type": "dashboard"
        }
      ]
    },
    ...
    "timezone": "browser",
    "title": "Apache JMeter Dashboard using Core InfluxdbBackendListenerClient",
    "uid": "C3Aj2PnIz",
    "version": 2
  }
```

`dashboards.yaml`（指定创建`dashboard`的`json`文件）内容如下：

```yaml
apiVersion: 1

providers:
- name: Default # A uniquely identifiable name for the provider
  type: file
  options:
    path: /var/lib/grafana/dashboards/jmeter.json
```

`datasources.yaml`（用于创建`influxdb`数据源）内容如下：

```yaml
---
apiVersion: 1

datasources:
- name: InfluxDB
  type: influxdb
  typeLogoUrl: public/app/plugins/datasource/influxdb/img/influxdb_logo.svg
  access: proxy
  url: http://demo-influxdb:8086
  password: ''
  user: ''
  database: jmeter
  basicAuth: false
  isDefault: true
  jsonData: {}
```

启动服务

```bash
docker compose up -d
```

使用浏览器访问`grafana`登录界面`http://localhost:3000`，帐号：`admin`，密码：`admin`