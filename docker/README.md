# 演示docker相关用法

## run command on host from docker container

> [how-to-run-shell-script-on-host-from-docker-container](https://stackoverflow.com/questions/32163955/how-to-run-shell-script-on-host-from-docker-container)

```shell
# 命令启动容器后能够在容器内运行任何命令操作宿主机
docker run --privileged --pid=host -it alpine:3.8 nsenter -t 1 -m -u -n -i sh
```

## docker volume权限管理

> https://www.bbsmax.com/A/kjdwbNpA5N/





## docker命令

### 删除镜像

```
# 删除未被使用的镜像
# https://www.baeldung.com/ops/docker-remove-dangling-unused-images
docker image prune -a
```

### 网络管理

> docker出现 could not find an available, non-overlapping IPv4...错误解决方案
> https://blog.csdn.net/epitomizelu/article/details/124989596

```
# 查询当前创建所有网络
docker network ls

# 删除没有被引用的网络
docker network prune
```



### docker run重写entrypoint并带参数

```
docker run --rm --entrypoint /bin/sh untergeek/curator:8.0.4 -c "while true; do date; sleep 1; done;"
```



### docker  tag给镜像打标签或者删除标签

> https://blog.csdn.net/K_520_W/article/details/116570680

```
# 删除标签
docker rmi 192.168.1.xxx:50003/library/hello-world:1.0.0

# 给hello-world镜像打标签
docker tag hello-world 192.168.1.181:50003/library/hello-world:1.0.0
```





## docker-compose命令



### 获取docker-compose up返回状态值

> https://github.com/docker/compose/issues/10225

```
# 在当前目录执行以下命令，不使用--abort-on-container-exit时下面脚本不会执行echo
docker-compose up --abort-on-container-exit || { echo '执行失败'; }
```





## docker最佳安全实践

> https://blog.aquasec.com/docker-security-best-practices
>
> - 使用非root模式运行docker daemon
>
> todo 配置过程繁琐，暂时不研究
