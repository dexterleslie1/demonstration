# `docker`命令



## `docker builder`

删除`docker build`缓存

>`https://docs.docker.com/reference/cli/docker/builder/prune/`

```bash
docker builder prune
```



## `docker build`

```shell
# 编译最新版本镜像并指定使用Dockerfile-base
docker build --tag 192.168.235.138:80/library/my-image:latest -f Dockerfile-base .

# 编译指定版本镜像并指定使用Dockerfile-base
docker build --tag 192.168.235.138:80/library/my-image:1.0.0 -f Dockerfile-base .
```



## `docker rm`

```bash
# 删除所有容器
docker rm $(docker ps -aq)
```



## `docker stats`

```bash
# 查看容器cpu状态
docker stats

# 查看指定容器的运行状态
docker stats mariadb-all-in-one
```



## `docker search`

```bash
# 搜索镜像
docker search nexus
```



## `docker cp`

> [docker cp命令复制、拷贝](https://www.runoob.com/docker/docker-cp-command.html)

```bash
# 从容器复制文件到宿主机
docker cp containername:/test.sql .

# 从宿主机复制文件到容器
docker cp ./test.sql containername:/
```



## `docker image`

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



## `docker login/logout`

> [登陆和登出](https://www.runoob.com/docker/docker-login-command.html)

```bash
# 登陆hub.docker.com，根据提示输入用户名和密码
docker login localhost:8082

# 登出hub.docker.com
docker logout localhost:8082
```



## `docker push`

> [推送本地镜像到远程仓库](https://www.runoob.com/docker/docker-push-command.html)

```bash
# 推送镜像dexterleslie/maven-ci-cd-demo到docker.io
docker push dexterleslie/maven-ci-cd-demo
```



## 网络管理

> [docker出现 could not find an available, non-overlapping IPv4...错误解决方案](https://blog.csdn.net/epitomizelu/article/details/124989596)

```bash
# 查询当前创建所有网络
docker network ls

# 删除没有被引用的网络
docker network prune
```



## `docker run`

```bash
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



## `docker inspect`

```bash
# 使用inspect命令查看容器信息
docker inspect mariadb-all-in-one
```



## `docker tag`给镜像打标签或者删除标签

> https://blog.csdn.net/K_520_W/article/details/116570680

```bash
# 删除标签
docker rmi 192.168.1.xxx:50003/library/hello-world:1.0.0

# 给hello-world镜像打标签
docker tag hello-world 192.168.1.181:50003/library/hello-world:1.0.0
```



## `docker compose logs`查看容器日志

从容器的最后10条开始查看日志并且滚动最新日志

```sh
docker-compose logs --tail 10 -f
```

查询指定的容器最后10条开始并滚动最新日志

```sh
docker-compose logs --tail 10 -f yyd-websocket-service
```



## 以列表方式显示`docker`容器已经启动时间`uptime`

通过查看`docker ps`命令输出的`STATUS`字段查看容器已经启动的时间

```sh
docker ps
```





## `docker system`

- 删除 Docker 系统中未使用的数据

  `docker system prune -a` 命令是 Docker 中的一个非常有用的命令，用于删除 Docker 系统中未使用的数据，包括悬空（dangling）镜像、容器、网络、卷（volume）和构建缓存（build cache）。加上 `-a` 或 `--all` 选项时，这个命令会删除所有未使用的资源，而不仅仅是悬空（dangling）的资源。

  具体来说：

  - **悬空镜像**：这些是没有被任何容器引用的镜像。它们可能是因为某些容器被删除而遗留下来的，或者是因为拉取了新的镜像版本而不再需要的旧版本。
  - **容器**：默认情况下，`docker system prune` 会删除所有已停止的容器。使用 `-a` 选项后，这个命令会删除所有容器，无论它们是否正在运行。因此，请在使用此命令时格外小心，以免不小心删除了重要的容器。
  - **网络**：Docker 创建的网络如果不再被任何容器使用，也会被删除。
  - **卷**：通常，`docker system prune` 不会删除卷，因为卷可能包含重要数据。但是，有些特定类型的卷（如未挂载的本地卷）可能会被删除，具体取决于 Docker 的版本和配置。不过，`-a` 选项主要影响的是容器和镜像，对卷的影响有限。
  - **构建缓存**：这个命令还会删除构建镜像时产生的缓存。这对于清理旧的构建数据非常有用。

  **使用注意**：

  - 在执行 `docker system prune -a` 之前，请确保你不再需要被删除的容器、镜像等资源。这个命令一旦执行，被删除的资源将无法恢复。
  - 如果你想先查看哪些资源会被删除，可以使用 `docker system prune -a --dry-run` 命令。这个命令会模拟执行过程，但不会真正删除任何资源。
  - 对于重要的数据，如数据库或配置文件，应该使用 Docker 卷（volumes）来存储，并确保定期备份这些数据。这样，即使执行了 `docker system prune -a`，也不会丢失重要数据。
