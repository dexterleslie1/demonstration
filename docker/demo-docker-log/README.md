# 演示log配置

## docker-compose配置每个容器的日志

> https://github.com/umputun/docker-logger/blob/master/docker-compose.yml
>

```
# 查看容器日志路径
docker inspect --format='' yyd-ecommerce-logstash | grep LogPath

# 切换到容器日志路径再查看当前日志大小

# docker-compose.yml中每个容器配置如下：
logging:
      driver: json-file
      options:
        max-size: "5k"
        max-file: "100"
        
driver: json-file表示使用json-file驱动记录容器日志，容器日志输出位置在/var/lib/docker/containers/[container id]/[container id]-json.log
max-size: 表示日志达到20m大小就滚动一次
max-file: 表示最多保留5个滚动日志
```

