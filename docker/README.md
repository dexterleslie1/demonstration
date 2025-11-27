## `run command on host from docker container`

> [how-to-run-shell-script-on-host-from-docker-container](https://stackoverflow.com/questions/32163955/how-to-run-shell-script-on-host-from-docker-container)

```shell
# 命令启动容器后能够在容器内运行任何命令操作宿主机
docker run --privileged --pid=host -it alpine:3.8 nsenter -t 1 -m -u -n -i sh
```



## `docker` 最佳安全实践

> https://blog.aquasec.com/docker-security-best-practices
>
> - 使用非root模式运行docker daemon
>
> todo 配置过程繁琐，暂时不研究



## `docker` 多阶段构建

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



## 容器占用存储空间分析

### 备注

下面命令会自动删除 `/var/lib/docker/containers` 下对应的容器日志卷，但是不会自动删除容器对应的匿名卷和命名卷

```sh
docker compose down
```

下面命令会自动删除 `/var/lib/docker/containers` 下对应的容器日志卷，也会自动删除容器对应的匿名卷和命名卷

```sh
docker compose down -v
```



### 分析容器的 `overlay2` 存储空间使用情况

>提示： 删除容器会自动删除 `overlay2` 对应的存储。

在 Docker 中，`overlay2` 是最常用的存储驱动（尤其在 Linux 系统上），用于管理容器的**层叠文件系统**（Union File System）。`overlay2` 占用磁盘空间的核心原因是 **镜像层、容器可写层及其他相关数据的持久化存储**。以下是具体场景和占用原因的详细说明：


#### 一、`overlay2` 的存储结构基础
`overlay2` 基于 **联合文件系统（UnionFS）**，将镜像拆分为多个**只读层（Image Layers）**，容器运行时在其上叠加一个**可写层（Container Layer）**。所有这些层的数据均存储在宿主机的 `/var/lib/docker/overlay2` 目录下（默认路径）。


#### 二、`overlay2` 占用空间的核心场景

##### 1. **镜像层的累积（最常见原因）**
每个 Docker 镜像由多个**只读层**叠加而成（由 `Dockerfile` 中的 `RUN`、`COPY`、`ADD` 等指令生成）。即使镜像未被容器使用，这些层仍会永久存储在 `overlay2` 中。具体场景包括：
- **拉取公共镜像**：如 `docker pull nginx:latest` 会下载 Nginx 镜像的所有层到 `overlay2`。
- **构建自定义镜像**：`docker build` 时每一步指令生成一个新层，即使后续指令修改了文件，旧层也不会被删除（仅通过硬链接复用未修改部分）。
- **未被清理的悬空镜像（Dangling Images）**：当重新构建同名镜像时，旧镜像的层若未被其他镜像引用，会成为“悬空层”（标记为 `<none>`），但仍占用空间。

**示例**：一个包含 10 层的镜像，即使仅运行一次容器后删除，这 10 层仍会保留在 `overlay2` 中。


##### 2. **容器可写层的写入**
每个运行中的容器会在 `overlay2` 中生成一个**可写层**（位于 `diff` 目录下），用于存储容器内文件的**修改、新增或删除操作**。即使容器未主动写入数据，某些进程也可能隐式生成写入：
- **日志输出**：应用日志直接写入容器文件系统（未挂载卷时），会累积在可写层。
- **临时文件**：如 `/tmp` 目录下的临时文件（未被清理时）。
- **文件修改**：应用更新配置文件、生成缓存（如 `node_modules`、`~/.m2`）等。

**示例**：一个运行 7 天的日志服务容器，每天生成 1GB 日志，其可写层会占用 7GB 空间。


##### 3. **未清理的停止容器**
即使容器已停止（`docker stop`），其关联的**可写层**和**镜像层引用**仍会保留在 `overlay2` 中。若大量停止容器未清理，会间接占用空间（因为镜像层可能被多个容器共享，但可写层是独立的）。


##### 4. **镜像构建过程中的中间层**
`docker build` 时，每个 `RUN` 指令生成的临时文件（如编译产物、下载的中间包）会被打包到镜像层中。若构建过程中未优化（如未清理临时文件），这些中间文件会被永久保留在 `overlay2` 的镜像层中。  
**示例**：  
```dockerfile
# 未优化的 Dockerfile（残留中间文件）
RUN wget https://example.com/big-file.tar.gz && tar -xzf big-file.tar.gz
# 优化后（清理中间文件）
RUN wget https://example.com/big-file.tar.gz && \
    tar -xzf big-file.tar.gz && \
    rm big-file.tar.gz  # 删除临时文件
```


##### 5. **卷（Volumes）的间接占用（需区分）**
严格来说，**命名卷（Named Volumes）** 的数据存储在 `/var/lib/docker/volumes` 目录（而非 `overlay2`），但如果容器通过**绑定挂载（Bind Mount）**将宿主机目录挂载到容器内，或使用 `tmpfs` 挂载，这些数据不会占用 `overlay2`。  
**例外**：若容器内的应用将数据写入未挂载的路径（如 `/app/data`），且未通过卷持久化，这些数据会被写入容器的可写层（属于 `overlay2`）。


##### 6. **日志驱动的日志存储**
若 Docker 守护进程配置了 `json-file` 日志驱动（默认），容器的标准输出/错误日志会写入宿主机的 `/var/lib/docker/containers/<container-id>/<container-id>-json.log` 文件。虽然日志文件本身不在 `overlay2` 中，但如果容器内应用将日志写入文件（而非标准输出），这些文件会存储在容器的可写层（属于 `overlay2`）。


#### 三、如何定位 `overlay2` 空间占用来源？
可通过以下命令排查具体占用：

##### 1. 查看 Docker 磁盘使用概览
```bash
docker system df  # 显示镜像、容器、卷的空间占用
```
输出示例：
```
TYPE            TOTAL     ACTIVE    SIZE      RECLAIMABLE
Images          50        10        20.5GB    15.2GB (74%)
Containers      20        2         1.2GB     1.0GB (83%)
Local Volumes   10        3         5.0GB     4.5GB (90%)
Build Cache     100       0         50.0GB    50.0GB (100%)
```
- 若 `Images` 或 `Local Volumes` 的 `SIZE` 很大，可能是 `overlay2` 占用的主因。


##### 2. 查看镜像层的详细占用
```bash
# 列出所有镜像及其层大小（按大小排序）
docker images --format "{{.Repository}}:{{.Tag}} {{.Size}}" | sort -hr

# 查看某个镜像的具体层（替换 <image-id>）
docker history <image-id> --no-trunc  # 显示每层的大小和指令
```


##### 3. 定位 `overlay2` 目录下的具体文件
`overlay2` 的目录结构为 `/var/lib/docker/overlay2/<layer-id>/diff`（镜像层）或 `/var/lib/docker/overlay2/<container-id>/diff`（容器可写层）。可通过以下命令查找大文件：

```bash
# 进入 overlay2 目录（需 root 权限）
cd /var/lib/docker/overlay2

# 查找最大的目录（镜像层或容器层）
du -sh * | sort -hr | head -n 10

# 进入具体层目录，进一步定位大文件
du -sh diff/* | sort -hr | head -n 10
```


#### 四、优化 `overlay2` 空间占用的建议
1. **清理无用镜像和容器**：  
   ```bash
   docker image prune -a  # 清理未被使用的镜像（包括悬空层）
   docker container prune  # 清理停止的容器
   ```

2. **优化 Dockerfile 减少层数**：  
   - 合并 `RUN` 指令（如 `RUN apt-get update && apt-get install -y pkg`）。  
   - 清理临时文件（如 `rm -rf /tmp/*`）。  
   - 使用多阶段构建（Multi-stage Build）分离构建环境和运行环境。

3. **限制容器日志大小**：  
   在 `docker run` 时通过 `--log-opt` 限制日志文件大小和数量：  
   ```bash
   docker run --log-opt max-size=10m --log-opt max-file=3 ...
   ```

4. **使用卷存储大文件**：  
   将应用的日志、缓存等大文件存储到 Docker 卷（`docker volume create`）或宿主机目录（绑定挂载），避免写入容器的可写层。

5. **定期监控和清理**：  
   结合 `cron` 或监控工具（如 Prometheus + Grafana）定期检查 `overlay2` 空间使用情况，自动清理过期数据。


#### 总结
`overlay2` 占用空间的核心原因是 **镜像层、容器可写层及相关数据的持久化存储**。通过优化镜像构建、清理无用资源、合理使用卷和日志策略，可以有效控制其空间占用。



#### 实验测试

创建使用 `overlay2` 存储空间容器

```sh
docker run --rm --name test1 busybox /bin/sh -c "for i in {1..10000000}; do echo \$i >> /1.txt; done; sleep 3600;"
```

分析 `overlay2` 存储空间使用大小

```sh
# 获取 overlay2 路径
docker inspect -f '{{ .Name }} {{ .GraphDriver }}' test1

cd /var/lib/docker/overlay2
# 查看 overlay2 占用的空间
du -d 1 -h a9acff3198a9e707e92c8db0728e2a83ab17dafafdeffc844acdf710247c38a1/
```

删除容器

```sh
docker rm -f test1
```



### 分析容器的日志存储空间使用情况

>提示：容器的标准输出/错误日志会写入宿主机的 `/var/lib/docker/containers/<container-id>/<container-id>-json.log` 文件。

查看指定或者所有容器的日志路径

```sh
docker inspect -f '{{ .Name }} {{ .LogPath }}' $(docker ps -qa)
```

创建容器实例模拟占用大量日志存储空间

```sh
docker run --name test2 busybox /bin/sh -c "for i in {1..10000000}; do echo \$i; done;"
```

显示所有容器实例日志存储存储使用情况，再通过显示的日志路径中id部分找出对应的容器实例。`https://stackoverflow.com/questions/59765204/how-to-list-docker-logs-size-for-all-containers`

```sh
sudo du -ch $(docker inspect --format='{{.LogPath}}' $(docker ps -qa)) | sort -h
```



### 分析容器的卷存储空间使用情况

> 注意：绑定卷无法通过下面方法查看存储空间使用情况，但可以通过查看宿主机卷挂载点硬盘存储使用情况间接分析容器绑定卷存储空间使用情况。

创建命名卷模拟占用大量存储空间

```sh
docker run --name b2 -v vol1:/data busybox /bin/sh -c "for i in {1..10000000}; do echo \$i >> /data/1.txt; done;"
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



### 实践

>说明：在测试本站 <a href="/场景案例/README.html#阿里云测试" target="_blank">秒杀</a> 案例时，发现硬盘容量占用异常，下面解析排查过程。

显式 `docker` 存储空间使用明细情况，通过明细仔细排查异常占用空间所在位置

```sh
docker system df -v
```

如果有很多 `none tag` 的镜像则使用下面命令删除

```sh
# 删除 none tag 镜像
docker rmi $(docker images -f "dangling=true" -q)
# 或者
docker image prune
```

如果发现有 `Volume` 空间占用异常：

- 记录下 `Volume` 对应的 `id`

- 通过命令查看 `Volume` 对应的容器，`70958edce96f204784653008643132974246701e35d09281bc10c494c0474e5f` 为 `Volume` 的 `id`

  ```sh
  docker inspect -f '{{ .Name }} {{ .Mounts }}' $(docker ps -qa) | grep 70958edce96f204784653008643132974246701e35d09281bc10c494c0474e5f
  ```

- 接下来步骤就是分析容器内部为何会异常占用空间。



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



## 工具镜像 - `curl`

>镜像中已经包含 `curl` 工具，`image: amd64/buildpack-deps:buster-curl`。

## 网络类型有哪些？

Docker 的网络类型主要有五种，可以分为两大类：**默认网络**和**自定义网络**。理解这些网络类型对于部署和管理容器化应用至关重要。

下面我将详细解释每种网络类型的特点和用途。

### 一、Docker 的默认网络驱动

当你安装 Docker 后，它会自动创建三个默认网络。你可以使用 `docker network ls` 命令查看它们。

```bash
$ docker network ls
NETWORK ID     NAME      DRIVER    SCOPE
a123b456c789   bridge    bridge    local
d789e012f345   host      host      local
f345g678h901   none      null      local
```

---

#### 1. **bridge（桥接网络）**
这是 **默认** 的网络模式。如果你在运行容器时不指定 `--network` 参数，容器就会连接到这个默认的 `bridge` 网络。

*   **工作原理**：Docker 会在宿主机上创建一个名为 `docker0` 的虚拟网桥。所有连接到这个网络的容器都会通过这个网桥进行通信，并分配一个私有 IP 地址。
*   **网络隔离**：容器之间可以 **相互通信**（通过 IP 地址），但与宿主机以外的网络是隔离的。
*   **端口映射**：如果要从外部访问容器内的服务，必须使用 `-p` 参数将容器的端口**映射**到宿主机的端口上（例如：`-p 8080:80`）。
*   **适用场景**：适用于大多数独立的容器，或者需要通过网络隔离的单个 Docker 应用。

---

#### 2. **host（主机网络）**
使用 `--network=host` 可以让容器共享宿主机的网络命名空间。

*   **工作原理**：容器不会拥有自己独立的网络栈，而是直接使用宿主机的 IP 地址和端口。例如，容器内运行在 80 端口，那么外部就可以直接通过宿主机的 IP 的 80 端口访问，**无需端口映射**。
*   **性能**：因为绕过了网络地址转换（NAT），所以网络性能比 `bridge` 模式更高。
*   **缺点**：容易引发端口冲突，因为容器使用的端口不能与宿主机上已有的端口冲突。
*   **适用场景**：对网络性能要求极高的场景，如高性能网络应用。但需要注意安全性，因为它放弃了网络隔离。

---

#### 3. **none（无网络）**
使用 `--network=none` 的容器将不会配置任何网络。

*   **工作原理**：容器只有一个本地回环地址 `127.0.0.1`，没有网卡、IP 地址，也无法与外界（包括宿主机和其他容器）通信。
*   **适用场景**：适用于需要完全离线、对安全性要求极高、或者需要自定义网络栈的场景。例如，运行一个只处理本地磁盘文件的批处理任务。

---

### 二、自定义网络驱动

除了使用默认网络，你还可以创建自己的自定义网络，这提供了更强大和灵活的网络管理能力。

---

#### 4. **自定义 bridge 网络**
虽然 Docker 提供了默认的 `bridge` 网络，但最佳实践是**创建并使用自定义的 bridge 网络**。

与默认 bridge 网络相比，自定义 bridge 网络提供了以下优势：
*   **自动 DNS 解析**：在自定义网络中，容器之间不仅可以通过 IP 地址通信，还可以直接使用**容器名**作为主机名进行解析。而在默认的 bridge 网络中，你只能使用 `--link`（已废弃）来链接容器。
*   **更好的隔离性**：你可以为不同的应用组创建不同的自定义 bridge 网络，实现网络层面的隔离。
*   **可配置性**：可以自定义子网、IP 地址范围、网关等网络配置。

**创建和使用示例：**
```bash
# 1. 创建一个自定义的bridge网络，名为 my-net
docker network create my-net

# 2. 运行一个 Nginx 容器并连接到 my-net
docker run -d --name web-server --network my-net nginx

# 3. 运行一个 busybox 容器并连接到同一个网络，然后通过容器名 ping web-server
docker run -it --network my-net busybox
/ # ping web-server # 这可以成功，因为DNS自动解析
```

---

#### 5. **overlay（覆盖网络）**
用于 **Docker Swarm 集群** 或其它支持 Swarm 模式的编排工具（如 Kubernetes 在某些配置下），实现跨多个 Docker 宿主机之间的容器通信。

*   **工作原理**：它创建一个分布式网络，覆盖在（overlay）宿主机的物理网络之上，使得不同主机上的容器看起来像是在同一个虚拟网络中。
*   **适用场景**：**集群环境**。当你的服务需要扩展到多台主机时，必须使用 `overlay` 网络来保证服务间的通信。

---

#### 6. **macvlan（Macvlan 网络）**
这种模式允许你为容器分配一个物理网络（如你的家庭或公司局域网）上的 **MAC 地址**，使得容器在网络上看起来就像一台**独立的物理设备**。

*   **工作原理**：它绕过宿主机上的网络栈，让容器直接连接到物理网络接口。容器会从你的物理路由器 DHCP 服务器获取 IP 地址，或者你可以设置静态 IP。
*   **优点**：性能极佳，几乎没有开销。
*   **缺点**：需要网络设备的支持（某些网络/云服务商可能不允许混杂模式），并且会消耗大量的 MAC 地址。
*   **适用场景**：需要容器应用像虚拟机或物理机一样直接暴露在物理网络中的场景。例如，迁移传统网络应用至容器。

---

### 总结与对比

| 网络模式           | 驱动名称  | 隔离性       | 性能                     | 关键特性                | 适用场景                     |
| :----------------- | :-------- | :----------- | :----------------------- | :---------------------- | :--------------------------- |
| **bridge（默认）** | `bridge`  | 容器间隔离   | 较好（有NAT开销）        | 需要端口映射            | 单机、简单的容器应用         |
| **自定义 bridge**  | `bridge`  | 网络级隔离   | 较好（有NAT开销）        | **自动DNS**，可配置性强 | **单机最佳实践**，多容器应用 |
| **host**           | `host`    | 无隔离       | **最佳**（无NAT）        | 直接使用宿主机网络      | 极致性能需求，不关心端口冲突 |
| **none**           | `null`    | 完全隔离     | -                        | 无任何网络              | 离线任务，高度安全           |
| **overlay**        | `overlay` | 集群网络隔离 | 较好                     | **跨主机通信**          | **Docker Swarm 集群**        |
| **macvlan**        | `macvlan` | 直接暴露     | **极佳**（直接物理网络） | 容器拥有独立MAC/IP      | 容器需作为物理设备存在于网络 |

### 最佳实践建议

1.  **避免使用默认的 `bridge` 网络**：对于任何需要相互通信的多容器应用，都应该创建自定义的 bridge 网络，以利用其自动服务发现的优势。
2.  **单机应用**：优先使用 **自定义 bridge 网络**。
3.  **集群应用**：必须使用 **overlay 网络**。
4.  **特殊需求**：根据对性能和网络暴露程度的要求，谨慎选择 `host` 或 `macvlan` 网络。

## 自定义bridge网络

Docker Compose自定义bridge网络

```yaml
version: '3'

networks:
  # 定义了一个名为 "my-net" 的网络
  my-net:
    # IP地址管理配置
    # IPAM​ 是 "IP Address Management" 的缩写，负责管理网络的IP地址分配。
    ipam:
      # 没有显式指定 driver，所以默认使用 bridge 驱动
      driver: default
      config:
        # 子网范围
        - subnet: "172.25.0.0/24"
          # 网关地址
          gateway: 172.25.0.1

services:
  my_busybox1:
    image: busybox
    command: sh -c "sleep infinity"
    networks:
      my-net:
        # 指定分配ip地址
        ipv4_address: 172.25.0.2

  my_busybox2:
    image: busybox
    command: sh -c "sleep infinity"
    networks:
      my-net:
        ipv4_address: 172.25.0.3
  
  my_busybox3:
    image: busybox
    command: sh -c "sleep infinity"
    networks:
      # 自动分配IP 172.25.0.4
      my-net:
```

