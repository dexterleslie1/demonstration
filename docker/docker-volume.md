# `docker volume`的用法

## `volume`相关命令

显示所有`volume`列表

```bash
docker volume ls
```

回收删除不再使用的匿名卷

```bash
docker volume prune
```



## `volume`存储位置

> `volume`以`volume`名称为目录名存储于`/var/lib/docker/volumes docker`的数据目录中。

如下命令创建命名卷`vol1`并挂载到容器的`/data`目录，然后输出内容到`1.txt`，`1.txt`存储在宿主机的 `/var/lib/docker/volumes/vol1/_data/1.txt`路径中。

```bash
docker run -it --rm -v vol1:/data centos /bin/sh -c "echo 'Test content11' > /data/1.txt"
```

## `volume`类型

### 命名`volume`

- 使用容器测试命名卷

  自动创建`vol1 volume`（在宿主机目录`/var/lib/docker/volumes`中）并挂载到容器`/data`目录下，执行`echo SHELL`命令输出`“Test content11”`到`/data/1.txt`文件中

  ```bash
  docker run -it --rm -v vol1:/data centos /bin/sh -c "echo 'Test content11' > /data/1.txt"
  ```

  创建vol2并输出内容到其中

  ```bash
  docker volume create vol2
  
  docker run -it --rm -v vol2:/data centos /bin/sh -c "echo 'Test content11' > /data/1.txt"
  ```

- 使用`docker compose`测试命名卷

  > 备注： `docker compose down -v`不会自动删除`external=true`的命名卷，但是会自动删除`external=false`的命名卷。

  `docker-compose.yaml`内容如下：

  ```yaml
  version: "3.0"
  
  services:
    demo-test:
      image: centos
      volumes:
        - vol1:/data
      command: /bin/sh -c "echo 'Test content11' > /data/1.txt"
  volumes:
    vol1:
      # external: true
  ```

  启动服务

  ```bash
  # external=true 时候需要手动创建命名卷，否则报告 vol1 卷不存在导致服务无法启动
  docker volume create vol1
  
  docker compose up -d
  ```

  关闭服务并且删除`external=false`自动创建的命名卷

  ```bash
  docker compose down -v
  ```

### 绑定`volume`

创建容器`b2`

```bash
docker run --name b2 -it -v ~/demo-docker-volume-b2:/data --rm busybox
```

在宿主机的存储卷上进行简单操作

```bash
cd ~/demo-docker-volume-b2
echo "hello" > 1.txt

# 在容器中查看1.txt
cat /data/1.txt
```

### 匿名`volume`

> `docker`自动创建匿名卷，如 `/var/lib/docker/volumes/ecc6395e6ce53a06f5b4cba01ab5b6e22b857f11319668268d0ab4374b344c53/_data/1.txt`

手动删除容器后匿名卷不会被自动删除

```bash
docker run --name test2 -v /data centos /bin/sh -c "echo 'anonymous volume' > /data/1.txt"

docker rm test2
```

通过下面命令回收删除不再使用的匿名卷

```bash
docker volume prune
```

## 使用`--volumes-from`复制数据

创建数据容器

```bash
docker run --name demo-docker-datum -it -v vol-demo-docker-datum:/data centos /bin/sh -c "echo '`date`' > /data/1.txt"
```

从数据容器创建新的容器并且复制数据

```bash
docker run --rm --name demo-docker-datum-cloner -it --volumes-from demo-docker-datum -v vol-demo-docker-datum-clone:/data1 centos /bin/sh -c "cp -rp /data/* /data1/"
```

使用复制数据启动新的容器，切换到/data目录查看数据

```bash
docker run --rm -it -v vol-demo-docker-datum-clone:/data centos /bin/bash
```

