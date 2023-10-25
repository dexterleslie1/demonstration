## influxdb用法

### 使用docker运行influxdb



#### 运行influxdb2

> https://medium.com/geekculture/deploying-influxdb-2-0-using-docker-6334ced65b6c
> https://hub.docker.com/_/influxdb
>
> NOTE: 暂时未用到不研究。



#### 运行influxdb1.8.10

```shell
# 如果需要自定义配置influxdb可以先导出默认配置
# https://docs.influxdata.com/influxdb/v1/administration/config/
docker run --rm influxdb:1.8.10 influxd config > config.yml

# 使用自定义配置启动influxdb，influxdb数据挂载到data-influxdb目录下
docker run --rm --name influxdb \
-p 8086:8086 \
--volume `pwd`/data-influxdb:/var/lib/influxdb \
--volume `pwd`/config.yml://etc/influxdb/influxdb.conf \
influxdb:1.8.10 -config /etc/influxdb/influxdb.conf
  
# NOTE: influxdb v1没有web UI

# 创建数据库
# 进入influxdb shell
docker exec -it influxdb sh
# 进入influxdb命令行
/ # influx
# 显示当前所有数据库
> show databases
# 创建数据库
> create database jmeter

# 获取直接执行influx命令
docker exec -it influxdb influx -execute 'show databases'
docker exec -it influxdb influx -execute 'create database jmeter'
```

