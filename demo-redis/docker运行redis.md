# `docker`容器运行`redis`

> redis官方镜像 [链接](https://hub.docker.com/_/redis?tab=tags)

## `standalone`模式（单机模式）

复制 [redis standalone](https://gitee.com/dexterleslie/demonstration/tree/master/demo-redis/redis-server/docker-based/mode-standalone) 相关配置到本地

切换到`mode-standalone`目录内

```bash
cd mode-standalone
```

启动`redis`服务

```bash
docker compose up -d
```

删除`redis`服务

```bash
docker compose down -v
```

## `replication`模式（主从复制模式）

> 示例会启动`demo-redis-replication-node1`容器作为`redis master`节点，`demo-redis-replication-node2`、`demo-redis-replication-node3`、`demo-redis-replication-node4`容器作为`redis slave`节点。
>
> `docker-compose-add-slave-node.yml`用于添加`slave`节点到现有的`replication`集群中。

复制 [redis replication](https://gitee.com/dexterleslie/demonstration/tree/master/demo-redis/redis-server/docker-based/mode-replication) 相关配置到本地

切换到`mode-replication`目录内

```bash
cd mode-replication
```

启动`redis`服务

```bash
docker compose up -d
```

查看`redis master`的`replication`状态，显示`role: master`

```bash
docker compose exec -it demo-redis-replication-node1 redis-cli info replication
```

查看`redis slave`的`replication`状态，显示`role: slave`

```bash
docker compose exec -it demo-redis-replication-node2 redis-cli info replication
```

添加`redis slave`到现有的主从集群中，下面命令执行后会启动一个名为`demo-redis-replication-node-extra`容器并自动加入到`demo-redis-replication-node1`主从集群中

```bash
docker compose -f docker-compose-add-slave-node.yml up -d
```

从现有的主从集群中删除`redis slave`

```bash
docker compose -f docker-compose-add-slave-node.yml down -v
```



## `sentinel`模式（哨兵模式）

> `docker-compose-add-slave-node.yml`用于添加`slave`节点到现有的集群中。

复制 [redis sentinel](https://gitee.com/dexterleslie/demonstration/tree/master/demo-redis/redis-server/docker-based/mode-sentinel) 相关配置到本地

切换到`mode-sentinel`目录内

```bash
cd mode-sentinel
```

编辑`.env`文件填写主机ip地址，`.env`内容如下：

```properties
# 宿主机ip
varHostIp=192.168.1.181
```

启动`redis sentinel`服务

```bash
docker compose up -d
```

查看`sentinel`相关状态包括`redis master`节点的信息

```bash
docker compose exec -it demo-redis-sentinel-node1 redis-cli -p 26379 info sentinel
```

删除`redis sentinel`服务

```bash
docker compose down -v
```



## `cluster`模式（集群模式）



### 基于`docker compose`集群

> [使用`docker compose`建立`redis cluster`](https://blog.yowko.com/docker-compose-redis-cluster/)

复制`https://gitee.com/dexterleslie/demonstration/tree/master/demo-redis/redis-server/docker-based/mode-cluster`示例到本地

启动`redis cluster`服务

```bash
# 基于redis6的集群
docker compose -f docker-compose-redis6.yml up -d

# 基于redis7的集群
# 注意：经过多次测试重启集群后容器ip地址变动也不会影响集群ok状态。
docker compose -f docker-compose-redis7.yml up -d
```

查看集群状态信息

```bash
docker compose exec -it node1 redis-cli -p 6380 cluster info
```

查看集群节点信息

```bash
docker compose exec -it node1 redis-cli -p 6380 cluster nodes
```

删除`redis cluster`服务

```bash
docker compose -f docker-compose-redis6.yml down -v

docker compose -f docker-compose-redis7.yml down -v
```



### 基于`docker swarm`集群

>注意：在使用`docker swarm`运行`redis`集群并且没有使用`nfs`存储时，需要绑定`redis`节点到指定的`swarm`节点中，否则在集群重启后会导致集群`down`。

复制`https://gitee.com/dexterleslie/demonstration/tree/master/demo-redis/redis-server/docker-based/mode-cluster`示例到本地，其中`docker-stack.yml`文件是`redis`集群在`docker swarm`部署的核心配置。

在`swarm`管理节点上执行以下命令部署`redis`集群

```bash
docker stack deploy test1 -c docker-stack.yaml
```

在`swarm`管理节点上查看`redis`集群状态

```bash
docker exec -it `docker ps |grep node1 | awk '{print $1}'` redis-cli -c cluster info
docker exec -it `docker ps |grep node1 | awk '{print $1}'` redis-cli -c cluster nodes
```

测试`swarm`中的`redis`集群是否丢失数据

```bash
# 生成数据
for i in {1..15}; do docker exec -it `docker ps|grep node1|awk '{print $1}'` redis-cli -c set key$i value$i; done

# 经过多次重启或者关闭swarm节点后，执行以下命令依然能够读取redis集群中的数据，表示redis集群数据不丢失
for i in {1..15}; do v_val=$(docker exec -it $(docker ps|grep node1|awk '{print $1}') redis-cli -c get key$i); echo key$i=$v_val; done
```

