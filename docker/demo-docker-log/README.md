# 演示log配置

## docker-compose配置每个容器的日志

> https://github.com/umputun/docker-logger/blob/master/docker-compose.yml
>
> 
>
> docker-compose.yml中每个容器配置如下：
> logging:
>   driver: json-file
>      options:
>         max-size: "20m"
>         max-file: "5"
>
> **driver** json-file表示使用json-file驱动记录容器日志，容器日志输出位置在/var/lib/docker/containers/[container id]/[container id]-json.log
> **max-size** 表示日志达到20m大小就滚动一次
> **max-file** 表示最多保留5个滚动日志