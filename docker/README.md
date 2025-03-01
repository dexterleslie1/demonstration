# 演示docker相关用法

## run command on host from docker container

> [how-to-run-shell-script-on-host-from-docker-container](https://stackoverflow.com/questions/32163955/how-to-run-shell-script-on-host-from-docker-container)

```shell
# 命令启动容器后能够在容器内运行任何命令操作宿主机
docker run --privileged --pid=host -it alpine:3.8 nsenter -t 1 -m -u -n -i sh
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





## docker 容器占用存储空间分析

### 备注

下面命令会自动删除 /var/lib/docker/containers 下对应的容器日志卷，但是不会自动删除容器对应的匿名卷和命名卷

```sh
docker compose down
```

下面命令会自动删除 /var/lib/docker/containers 下对应的容器日志卷，也会自动删除容器对应的匿名卷和命名卷

```sh
docker compose down -v
```



### 分析容器的 overlay2 存储空间使用情况

备注： 删除容器会自动删除 overlay2 对应的存储。



创建使用 overlay2 存储空间容器

```sh
docker run --rm --name test1 centos /bin/sh -c "for i in {1..10000000}; do echo \$i >> /1.txt; done; sleep 3600;"
```

分析 overlay2 存储空间使用大小

```sh
docker inspect -f '{{ .Name }} {{ .GraphDriver }}' test1
cd /var/lib/docker
du -d 1 -h a9acff3198a9e707e92c8db0728e2a83ab17dafafdeffc844acdf710247c38a1/
```

删除容器

```sh
docker rm -f test1
```



### 分析容器的日志存储空间使用情况

查看指定或者所有容器的日志路径

```sh
docker inspect -f '{{ .Name }} {{ .LogPath }}' $(docker ps -qa)
```

创建容器实例模拟占用大量日志存储空间

```sh
docker run --name test2 centos /bin/sh -c "for i in {1..10000000}; do echo \$i; done;"
```

显示所有容器实例日志存储存储使用情况，再通过显示的日志路径中id部分找出对应的容器实例。https://stackoverflow.com/questions/59765204/how-to-list-docker-logs-size-for-all-containers

```sh
sudo du -ch $(docker inspect --format='{{.LogPath}}' $(docker ps -qa)) | sort -h
```



### 分析容器的卷存储空间使用情况

> NOTE： 绑定卷无法通过下面方法查看存储空间使用情况，但可以通过查看宿主机卷挂载点硬盘存储使用情况间接分析容器绑定卷存储空间使用情况。

创建命名卷模拟占用大量存储空间

```sh
docker run --name b2 -v vol1:/data centos /bin/sh -c "for i in {1..10000000}; do echo \$i >> /data/1.txt; done;"
```

针对匿名卷需要通过下面命令(列出所有容器实例对应的卷，包括命名卷、绑定卷、匿名卷)配合查到匿名卷对应的容器，https://stackoverflow.com/questions/30133664/how-do-you-list-volumes-in-docker-containers

```sh
docker inspect -f '{{ .Name }} {{ .Mounts }}' $(docker ps -qa)
```

显示 images、containers、volumes、build cache 存储空间使用情况汇总，images 表示镜像存储的使用空间，containers 表示容器实例存储的使用空间，volumes 表示卷存储的使用空间。

```sh
docker system df
```

显示 images、containers、volumes、build cahce 存储空间使用情况明细

```sh
docker system df -v
```





## 环境变量用法

### dockerfile 中声明环境变量

https://www.baeldung.com/ops/dockerfile-env-variable

dockerfile 内容如下：

```dockerfile
FROM busybox

ENV myV=v2

ENTRYPOINT [ "/bin/sh", "-c", "sleep 36000000" ]
```

编译镜像

```sh
docker build --rm -t test .
```

运行容器

```sh
docker run -d --rm --name test1 test
```

查看环境变量

```sh
docker exec -it test1 /bin/sh
echo $myV
```

删除容器

```sh
docker rm -f test1
```





## dockerfile USER 指令用法

使用 USER 指令指定的用户名将用于运行 Dockerfile 中的所有后续 RUN、CMD 和 ENTRYPOINT 指令。

参考

> https://subscription.packtpub.com/book/cloud-and-networking/9781838983444/2/ch02lvl1sec14/other-dockerfile-directives

dockerfile 内容如下：

```dockerfile
FROM ubuntu
# RUN apt-get update && apt-get install apache2 -y 
USER www-data
CMD ["whoami"]
```

编译镜像

```sh
docker build -t user .
```

运行容器

```sh
docker run -it user
```

容器输出 www-data 表示当前运行用户为 www-data



## 运行容器的用户和卷、目录、文件权限

备注： 如果不使用 dockerfile USER 指令或者 docker run --user 参数指定运行容器用户，则默认使用 root 用户运行容器，对所有目录都有写权限。如果使用 USER 指令或者 --user 参数指定运行容器用户，则需要使用 chmod 或者 chown 授予当前用户或者修改目录、文件的属主以获得对目录、文件写入权限。

dockerfile 内容如下：

```dockerfile
FROM ubuntu
RUN mkdir /data

# 必须授予 www-data 用户对 /data 目录有写入权限，
# 否则会报告 /bin/sh: 1: cannot create /data/1.txt: Permission denied 错误
RUN chmod -R o+w /data

USER www-data
ENTRYPOINT [ "/bin/sh", "-c", "date > /data/1.txt" ]
```

docker-compose.yaml 内容

```yaml
version: "3.0"

services:
  demo-test:
    build:
      context: .
    container_name: demo-test
    image: my-demo-test

```

编译镜像

```sh
docker compose build
```

启动服务，没有错误信息输出表示 www-data 用户成功写入输入到 /data/1.txt 文件中。

```sh
docker compose up
```

