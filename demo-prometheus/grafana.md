# `Grafana`



## 使用 `Docker` 运行

> 参考这个资料使用 `docker` 运行 `grafana` [链接](https://grafana.com/docs/grafana/latest/setup-grafana/installation/docker/)

运行 `grafana`

> 注意：需要手动授权 `data-grafana` 目录所有人有写权限 `sudo chmod -R a+w data-grafana`

```bash
docker run --rm -p 3000:3000 \
--name=grafana \
--volume "$PWD/data-grafana:/var/lib/grafana" \
grafana/grafana:6.6.2
```

使用浏览器访问 `grafana` 登录界面 `http://localhost:3000`，帐号：`admin`，密码：`admin`



## 手动导入 `node_exporter dashboard`

使用`docker-compose`运行`grafana`，`docker-compose.yaml`内容如下：

```yaml
version: '3.3'

services:
  grafana:
    image: grafana/grafana:9.4.3
    volumes:
      - /etc/localtime:/etc/localtime:ro
    ports:
      - '3000:3000'
```

启动`grafana`服务

```bash
docker compose up -d
```

使用浏览器访问`grafana`登录界面`http://localhost:3000`，帐号：`admin`，密码：`admin`

跳转到`http://localhost:3000/datasources/new`创建一个类型为`prometheus`的新的数据源。其中数据源`HTTP`>`URL`配置为：`http://promethues:9090`，其他使用默认值。

跳转到`http://localhost:3000/dashboard/import`，在`Import via grafana.com`输入`1860（node_exporter系统监控dashboard）`在线导入`node_exporter dashboard`



## 自动导入 `dashboards` 和 `datasources`

> `datasource`数据源`provisioning yaml`文件配置[链接](https://grafana.com/docs/grafana/latest/administration/provisioning/#example-data-source-config-file)

示例的详细代码请参考 [链接](https://gitee.com/dexterleslie/demonstration/tree/main/demo-prometheus/demo-grafana-provisioning-automatically)

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



## 为自定义指标创建 `Dashboard`



### 创建自定义指标并配置 `Prometheus` 抓取

参考本站 <a href="/prometheus/prometheus自定义exporter.html#实验" target="_blank">链接</a> 使用 `Micrometer` 创建自定义指标并配置 `Prometheus` 抓取自定义指标。



### 在 `Grafana` 中配置显示自定义指标

登录 `Grafana`，`http://localhost:3000`，帐号：`admin`，密码：`admin`。

#### 创建用于显示自定义指标的 `Panel`

步骤如下：

- 点击 `Dashboards` >  `Browse`，显示 `Dashboards` 列表，点击 `New Dashboard` 新增 `Dashboard`
- 点击 `Add a new panel` 新增自定指标显示 `panel`，配置信息如下：

  - `Panel options` > `Title` 填写 ` 订单创建速率`
  - `Axis` > `Label` 填写 `每秒创建订单数`
  - `Graph styles` > `Line Interpolation` 选择 `Smooth`
  - `Graph styles` > `Show points` 选择 `Never`
  - 添加第一个 `Query`，`PromQL` 为 `irate(my_order_request_count_total{order="master"}[$__rate_interval])`，`options` > `legend` 选择 `Custom` 并填写 `Order Master`
  - 添加第二个 `Query`，`PromQL` 为 `irate(my_order_request_count_total{order="detail"}[$__rate_interval])`，`options` > `legend` 选择 `Custom` 并填写 `Order Detail`
- 点击 `Save` 保存 `Panel`。



#### 修改 `Dashboard` 名称

步骤如下：

- 点击 `Dashboard` 面板中的 `Dashboard settings` 按钮，在 `General` > `Name` 中填写 `Dashboard` 名称为 `测试应用监控`



#### 为 `Dashboard` 添加变量 - `Query` 类型

>通过自定义 `PromQL` 表达式获取变量的值列表。

步骤如下：

- 点击 `Dashboard` 面板中的 `Dashboard settings` 按钮，点击左边导航栏中的 `Variables`，点击 `Add Variable` 新增变量，变量设置参数如下：

  - `Select variable type` 选择 `Query`（参数的值使用 `PromQL` 获取）

  - 参数名称填写 `orderType`

  - 参数显示标签填写 `Order Type`
  - `Sort` 选择 `Alphabetical（desc）`

  - `Query` 设置填写 `label_values(my_order_request_count_total, order)`


- 点击 `Run query` 按钮测试，在 `Preview of values` 中检查值列表是否为 `master`、`detail` 预期数据，如果数据正确点击 `Apply` 按钮。

- 在自定义指标的 `PromQL` 表达式中使用变量，如下：

  ```
  irate(my_order_request_count_total{order="$orderType"}[$__rate_interval])
  ```



#### 为 `Dashboard` 添加变量 - `Datasource` 类型

>提供用户选择不同的数据源。

步骤如下：

- 点击 `Dashboard` 面板中的 `Dashboard settings` 按钮，点击左边导航栏中的 `Variables`，点击 `Add Variable` 新增变量，变量设置参数如下：

  - `Select variable type` 选择 `Data source`

  - 参数名称填写 `datasource`

  - 参数显示标签填写 `Datasource`


- 点击 `Run query` 按钮测试，在 `Preview of values` 中检查值列表是否为 `Prometheus`、`default` 预期数据，如果数据正确点击 `Apply` 按钮。
- 不需要在自定义指标显示引用此类型变量，它会自动注入到当前查询的上下文中。

