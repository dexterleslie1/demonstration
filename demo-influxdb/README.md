## influxdb用法

### 使用docker运行influxdb

> https://medium.com/geekculture/deploying-influxdb-2-0-using-docker-6334ced65b6c
> https://hub.docker.com/_/influxdb

```shell
# 如果需要自定义配置influxdb可以先导出配置
docker run --rm influxdb:2.0.7 influxd print-config > config.yml

# 使用自定义配置启动influxdb，influxdb数据挂载到data-influxdb2目录下
docker run --rm --name influxdb \
-p 8086:8086 \
--volume `pwd`/data-influxdb2:/var/lib/influxdb2 \
--volume `pwd`/config.yml:/etc/influxdb2/config.yml \
influxdb:2.0.7

# 初始化influxdb
# bucket相当于数据库
# username和password用于登录dashboard
docker exec influxdb influx setup \
  --bucket my-bucket \
  --org my-org \
  --password xxx123456 \
  --username root \
  --force
  
# 或者在启动容器时初始化influxdb
docker run --rm --name influxdb \
-p 8086:8086 \
--volume `pwd`/data-influxdb2:/var/lib/influxdb2 \
--volume `pwd`/config.yml:/etc/influxdb2/config.yml \
-e DOCKER_INFLUXDB_INIT_MODE=setup \
-e DOCKER_INFLUXDB_INIT_USERNAME=root \
-e DOCKER_INFLUXDB_INIT_PASSWORD=xxx123456 \
-e DOCKER_INFLUXDB_INIT_ORG=my-org \
-e DOCKER_INFLUXDB_INIT_BUCKET=my-bucket \
influxdb:2.0.7
  
# 登录dashboard测试influxdb是否正常启动
http://localhost:8086

# 获取influxdb token
docker exec influxdb influx auth list | awk '/root/ {print $4 " "}'
```

