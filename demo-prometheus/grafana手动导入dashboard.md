# 手动导入`node_exporter dashboard`

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

