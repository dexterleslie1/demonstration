## 演示docker volume用法

### dockerfile VOLUME 用法

备注： VOLUME 声明的挂载点支持 -v 挂载到宿主机目录中。



Dockerfile 内容如下：

```dockerfile
FROM busybox

VOLUME [ "/data" ]

ENTRYPOINT [ "/bin/sleep", "3600" ]

```

docker-compose.yaml 内容如下：

```yaml
version: "3.0"

services:
  demo-test1:
    build:
      context: .
      dockerfile: Dockerfile
    container_name: demo-test1
    image: demo-test1

```

启动容器后会自动创建匿名卷并且挂载到 /data 挂载点

```sh
docker compose up -d
```



### volume 相关命令

显示所有 volume 列表

```sh
docker volume ls
```

回收删除不再使用的匿名卷

```sh
docker volume prune
```



### volume 存储位置

> volume 以 volume 名称为目录名存储于 /var/lib/docker/volumes docker的数据目录中。

如下命令创建命名卷 vol1 并挂载到容器的 /data 目录，然后输出内容到 1.txt，1.txt 存储在宿主机的 /var/lib/docker/volumes/vol1/_data/1.txt 路径中。

```sh
docker run -it --rm -v vol1:/data centos /bin/sh -c "echo 'Test content11' > /data/1.txt"
```



### volume 类型



#### 命名volume

```sh
# 自动创建vol1 volume （在宿主机目录/var/lib/docker/volumes 中）并挂载到容器/data目录下，执行echo SHELL命令输出 “Test content11” 到 /data/1.txt 文件中
docker run -it --rm -v vol1:/data centos /bin/sh -c "echo 'Test content11' > /data/1.txt"

# 创建vol2并输出内容到其中
docker volume create vol2
docker run -it --rm -v vol2:/data centos /bin/sh -c "echo 'Test content11' > /data/1.txt"
```



#### 绑定volume

```sh
# 创建容器b2
docker run --name b2 -it -v ~/demo-docker-volume-b2:/data --rm busybox

# 在宿主机的存储卷上进行简单操作
cd ~/demo-docker-volume-b2
echo "hello" > 1.txt

# 在容器中查看1.txt
cat /data/1.txt
```



#### 匿名volume

docker 自动创建匿名卷，例如 /var/lib/docker/volumes/ecc6395e6ce53a06f5b4cba01ab5b6e22b857f11319668268d0ab4374b344c53/_data/1.txt。

手动删除容器后匿名卷不会被自动删除

```sh
docker run --name test2 -v /data centos /bin/sh -c "echo 'anonymous volume' > /data/1.txt"

docker rm test2
```

通过下面命令回收删除不再使用的匿名卷

```sh
docker volume prune
```



### 演示使用--volumes-from复制数据

```
# 创建数据容器
docker run --name demo-docker-datum -it -v vol-demo-docker-datum:/data centos /bin/sh -c "echo '`date`' > /data/1.txt"

# 从数据容器创建新的容器并且复制数据
docker run --rm --name demo-docker-datum-cloner -it --volumes-from demo-docker-datum -v vol-demo-docker-datum-clone:/data1 centos /bin/sh -c "cp -rp /data/* /data1/"

# 使用复制数据启动新的容器，切换到/data目录查看数据
docker run --rm -it -v vol-demo-docker-datum-clone:/data centos /bin/bash
```

