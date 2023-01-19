# 演示docker相关用法

## run command on host from docker container

> [how-to-run-shell-script-on-host-from-docker-container](https://stackoverflow.com/questions/32163955/how-to-run-shell-script-on-host-from-docker-container)

```shell
# 命令启动容器后能够在容器内运行任何命令操作宿主机
docker run --privileged --pid=host -it alpine:3.8 nsenter -t 1 -m -u -n -i sh
```

## docker volume权限管理

> https://www.bbsmax.com/A/kjdwbNpA5N/
