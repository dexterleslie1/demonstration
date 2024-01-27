# 演示docker相关用法

## run command on host from docker container

> [how-to-run-shell-script-on-host-from-docker-container](https://stackoverflow.com/questions/32163955/how-to-run-shell-script-on-host-from-docker-container)

```shell
# 命令启动容器后能够在容器内运行任何命令操作宿主机
docker run --privileged --pid=host -it alpine:3.8 nsenter -t 1 -m -u -n -i sh
```

## docker volume权限管理

> https://www.bbsmax.com/A/kjdwbNpA5N/



## 以列表方式显示 docker 容器已经启动时间 uptime

通过查看 docker ps 命令输出的 STATUS 字段查看容器已经启动的时间

```sh
docker ps
```



## 使用 docker compose 仅对单个服务 service 进行操作

仅更新 yyd-websocket-service

```sh
docker compose pull yyd-websocket-service
```

强制重建 yyd-websocket-service，--no-deps 表示依赖的相关容器不会被重建

```
docker compose up -d --no-deps --force-recreate yyd-websocket-service
```



## docker命令



### docker build

```shell
# 编译最新版本镜像并指定使用Dockerfile-base
docker build --tag docker.118899.net:10001/yyd-public/my-image:latest -f Dockerfile-base .

# 编译指定版本镜像并指定使用Dockerfile-base
docker build --tag docker.118899.net:10001/yyd-public/my-image:1.0.0 -f Dockerfile-base .
```



### docker rm

```
# 删除所有容器
docker rm $(docker ps -aq)
```



### docker stats

```
# 查看容器cpu状态
docker stats

# 查看指定容器的运行状态
docker stats mariadb-all-in-one
```



### docker search

```
# 搜索镜像
docker search nexus
```



### docker cp

> docker cp命令复制、拷贝
> https://www.runoob.com/docker/docker-cp-command.html

```
# 从容器复制文件到宿主机
docker cp containername:/test.sql .

# 从宿主机复制文件到容器
docker cp ./test.sql containername:/
```



### docker image

```shell
# 删除未被使用的镜像
# https://www.baeldung.com/ops/docker-remove-dangling-unused-images
docker image prune -a

# 强制删除本地所有容器和镜像
docker rmi -f $(docker images -aq)

# 显示本地所有镜像
# https://www.runoob.com/docker/docker-images-command.html
docker images

# 删除 none tag 镜像
docker rmi $(docker images -f "dangling=true" -q)
# 或者
docker image prune
```



### docker login/logout

> 登陆和登出
> https://www.runoob.com/docker/docker-login-command.html

```
# 登陆hub.docker.com，根据提示输入用户名和密码
docker login localhost:8082

# 登出hub.docker.com
docker logout localhost:8082
```



### docker push

> 推送本地镜像到远程仓库
> https://www.runoob.com/docker/docker-push-command.html

```
# 推送镜像dexterleslie/maven-ci-cd-demo到docker.io
docker push dexterleslie/maven-ci-cd-demo
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



### docker run

```
# 重写entrypoint并带参数
docker run --rm --entrypoint /bin/sh untergeek/curator:8.0.4 -c "while true; do date; sleep 1; done;"

# docker run命令传递额外启动参数
docker run --rm --name mariadb-demo mariadb:10.4.19  --character-set-server=utf8mb4 --collation-server=utf8mb4_general_ci --skip-character-set-client-handshake

# 运行容器时启动一个终端使它不退出运行
# https://blog.csdn.net/qq_19381989/article/details/102781663
# 前台运行
docker run -it --name=demo-jdk8 --rm openjdk:8-jre

# 后台运行
docker run -itd --name=demo-jdk8 --rm openjdk:8-jre
```



### docker inspect

```
# 使用inspect命令查看容器信息
docker inspect mariadb-all-in-one
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



## docker 多阶段构建

Dockerfile 多阶段构建是一种优化 Docker 镜像构建的方法，它可以减少 Docker 镜像的大小和运行时的资源消耗。
Dockerfile 多阶段构建的基本思路是利用多个阶段构建镜像，每个阶段都有一个基础镜像，并在这个基础镜像上进行构建。在每个阶段的最后，通过 COPY 或者 FROM 语句将需要的文件或者库复制到最终的镜像中。这种方法可以减少最终镜像的大小，同时也可以避免在运行时不必要的资源消耗。

参考 https://zhuanlan.zhihu.com/p/612292168

示例：

main.go 内容如下：

```go
package main

import (
	"fmt"
	"time"
)

func main() {
	fmt.Println("Hello world!")
	time.Sleep(time.Second * 3600)
}
```

Dockerfile 内容如下：

```dockerfile
FROM golang:1.16-alpine AS builder
WORKDIR /app
COPY main.go .
RUN go build -o myapp main.go

FROM alpine:latest
RUN apk --no-cache add ca-certificates
WORKDIR /root/
COPY --from=builder /app/myapp .
CMD ["./myapp"]
```

编译镜像

```shell
docker build --tag my-hello-world .
```

运行镜像，输出 "Hello world" 表示成功运行

```shell
docker run --rm my-hello-world
```

