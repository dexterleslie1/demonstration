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

显示所有容器实例日志存储存储使用情况，再通过显示的日志路径中id部分找出对应的容器实例。`https://stackoverflow.com/questions/59765204/how-to-list-docker-logs-size-for-all-containers`

```sh
sudo du -ch $(docker inspect --format='{{.LogPath}}' $(docker ps -qa)) | sort -h
```



### 分析容器的卷存储空间使用情况

> 注意：绑定卷无法通过下面方法查看存储空间使用情况，但可以通过查看宿主机卷挂载点硬盘存储使用情况间接分析容器绑定卷存储空间使用情况。

创建命名卷模拟占用大量存储空间

```sh
docker run --name b2 -v vol1:/data centos /bin/sh -c "for i in {1..10000000}; do echo \$i >> /data/1.txt; done;"
```

针对匿名卷需要通过下面命令(列出所有容器实例对应的卷，包括命名卷、绑定卷、匿名卷)配合查到匿名卷对应的容器，`https://stackoverflow.com/questions/30133664/how-do-you-list-volumes-in-docker-containers`

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





## `dockerfile` 指令 - `USER`

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



## `dockerfile` 指令 - `ADD`



### 复制并解压本地压缩包

```dockerfile
# ADD 自动解压（生成 /tmp/app/...）
ADD app.tar.gz /tmp

# 若用 COPY，需手动解压（更可控）
COPY app.tar.gz /tmp
RUN tar -xzf /tmp/app.tar.gz -C /tmp && rm /tmp/app.tar.gz  # 清理压缩包
```



### 下载远程配置文件

```dockerfile
# 不推荐：ADD 直接下载（依赖远程 URL）
ADD https://example.com/config.ini /etc/config/

# 推荐：用 RUN + curl（可添加错误处理、清理临时文件）
RUN curl -fSL https://example.com/config.ini -o /etc/config/config.ini && \
    chmod 644 /etc/config/config.ini
```



## `dockerfile` 指令 - `COPY`



### 复制本地代码目录

```dockerfile
# 推荐：用 COPY，行为明确
COPY ./src /app/src

# 不推荐：虽然 ADD 也能实现，但无必要
ADD ./src /app/src

# 自动递归复制 dist 目录中的文件和目录到 /usr/local/openresty/nginx/html/ 目录中
COPY dist/ /usr/local/openresty/nginx/html/
```



## `dockerfile` 指令 - `ADD` 和 `COPY` 区别

在 Dockerfile 中，`ADD` 和 `COPY` 都是用于将文件/目录复制到镜像中的指令，但它们的设计目标和功能有明显差异。以下是核心区别的详细对比：


### **1. 支持的源类型不同**
- **`COPY`**：仅支持**本地文件或目录**（必须存在于构建上下文中）。  
  构建镜像时，Docker 客户端会将构建上下文（通常是 `docker build` 命令所在目录）中的文件打包发送给 Docker 守护进程，`COPY` 只能从该上下文中复制文件。  

- **`ADD`**：支持三种源类型：  
  - 本地文件/目录（同 `COPY`）；  
  - **本地压缩包**（如 `.tar`、`.tar.gz`、`.tar.bz2`、`.tar.xz`、`.zip`）；  
  - **远程 URL**（如 `http://example.com/file.txt`、`https://github.com/xxx.zip`）。  


### **2. 对压缩包的处理行为不同**
- **`COPY`**：无论源是否是压缩包，均直接复制到镜像中，**不会自动解压**。  
  例如：`COPY app.tar.gz /tmp` 会将 `app.tar.gz` 文件直接复制到镜像的 `/tmp` 目录，不会解压。  

- **`ADD`**：若源是**本地压缩包**（且格式受支持），会**自动解压到目标路径**（类似 `tar -xzf`）。  
  例如：`ADD app.tar.gz /tmp` 会将 `app.tar.gz` 解压到 `/tmp` 目录（生成 `app/` 等子文件），但 `.zip` 压缩包解压后无顶层目录（需注意路径问题）。  


### **3. 对远程资源的处理不同**
- **`COPY`**：无法直接复制远程 URL 资源（因为构建上下文不包含远程文件）。  
  若需从远程下载文件，必须配合 `RUN` 指令使用 `curl`、`wget` 等工具（更可控）。  

- **`ADD`**：可以直接复制远程 URL 资源到镜像中（类似 `wget <url> -O <目标路径>`）。  
  例如：`ADD https://example.com/config.ini /etc/config/` 会下载 `config.ini` 并保存到镜像的 `/etc/config/` 目录。  


### **4. 最佳实践与透明度**
- **`COPY`**：设计目标是「纯本地文件复制」，行为简单透明，符合「镜像构建过程应清晰可追溯」的最佳实践。  
  推荐优先使用 `COPY`，尤其是当源是本地文件时，避免因 `ADD` 的额外功能（如解压、下载）引入不可预期的问题。  

- **`ADD`**：因支持远程 URL 和自动解压，容易导致构建逻辑隐含外部依赖（如远程资源失效会影响镜像构建），或压缩包解压后路径混乱（如未清理压缩包本身）。  
  官方文档建议：**仅在需要解压本地压缩包时使用 `ADD`，否则用 `COPY`**；远程资源下载应通过 `RUN` 指令完成（可结合 `curl -O` 或 `wget`，并清理临时文件）。  


### **5. 其他细节差异**
- **目标路径的覆盖行为**：两者均会覆盖目标路径的同名文件，但 `COPY` 更严格（仅复制明确指定的文件），而 `ADD` 可能因解压或下载引入额外文件（需注意清理）。  
- **元数据保留**：两者均支持 `--chown` 参数修改文件所有者（如 `COPY --chown=user:group file.txt /app`），但 `ADD` 早期版本不支持（现代 Docker 已支持）。  


### **总结：如何选择？**
| 场景                        | 推荐指令          | 原因                                                         |
| --------------------------- | ----------------- | ------------------------------------------------------------ |
| 复制本地文件/目录（无压缩） | `COPY`            | 行为透明，符合最佳实践                                       |
| 复制本地压缩包并自动解压    | `ADD`             | 唯一支持自动解压的指令（但需注意解压后的路径清理）           |
| 从远程 URL 下载文件         | `RUN`+`curl/wget` | `ADD` 下载远程资源会导致构建依赖外部 URL，不够健壮（推荐用 `RUN` 控制） |


### **示例对比**
#### 场景 1：复制本地代码目录
```dockerfile
# 推荐：用 COPY，行为明确
COPY ./src /app/src

# 不推荐：虽然 ADD 也能实现，但无必要
ADD ./src /app/src
```

#### 场景 2：复制并解压本地压缩包
```dockerfile
# ADD 自动解压（生成 /tmp/app/...）
ADD app.tar.gz /tmp

# 若用 COPY，需手动解压（更可控）
COPY app.tar.gz /tmp
RUN tar -xzf /tmp/app.tar.gz -C /tmp && rm /tmp/app.tar.gz  # 清理压缩包
```

#### 场景 3：下载远程配置文件
```dockerfile
# 不推荐：ADD 直接下载（依赖远程 URL）
ADD https://example.com/config.ini /etc/config/

# 推荐：用 RUN + curl（可添加错误处理、清理临时文件）
RUN curl -fSL https://example.com/config.ini -o /etc/config/config.ini && \
    chmod 644 /etc/config/config.ini
```

**结论**：`COPY` 是更安全、透明的基础复制指令，而 `ADD` 仅在需要解压本地压缩包时使用；远程资源下载应通过 `RUN` 指令完成，避免构建逻辑隐含外部依赖。



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



## 查看容器网络命名空间的连接状态（无需进入容器） 

>详细用法请参考本站 <a href="/linux/命令行工具列表.html#查看容器网络命名空间的连接状态-无需进入容器" target="_blank">链接</a>
