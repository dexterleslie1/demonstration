# `docker`命令



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

