# `docker`日志



## `docker compose`配置每个容器的日志

> `https://github.com/umputun/docker-logger/blob/master/docker-compose.yml`

查看容器日志路径

```bash
docker inspect --format='' yyd-ecommerce-logstash | grep LogPath
```

- 切换到容器日志路径再查看当前日志大小

`docker-compose.yml`中每个容器配置如下：

```yaml
logging:
  driver: json-file
  options:
    max-size: "5k"
    max-file: "100"
```

- `driver`：`json-file`表示使用`json-file`驱动记录容器日志，容器日志输出位置在`/var/lib/docker/containers/[container id]/[container id]-json.log`
- `max-size`：表示日志达到`20m`大小就滚动一次
- `max-file`：表示最多保留`5`个滚动日志

