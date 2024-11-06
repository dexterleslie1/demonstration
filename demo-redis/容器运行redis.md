# 容器运行`redis`

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

>注意：在使用`docker swarm`运行`redis`集群并且没有使用`nfs`存储时，需要绑定`redis`节点到指定的`swarm`节点中，否则在集群重启后会导致集群`down`。在使用`nfs`存储时则理论上不需要绑定`redis`节点到`swarm`节点中（未做实验）。

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

获取通过示例`https://gitee.com/dexterleslie/demonstration/blob/master/spring-cloud/demo-spring-cloud-assistant/deployer/docker-stack.yaml`测试`swarm`中的`redis`集群是否会丢失数据。



### 基于`k8s`集群

#### 参考资料

参考以下资料基于`k8s`运行各种模式的`redis`

- `https://github.com/bitnami/charts/tree/main/bitnami/redis-cluster`
- `https://github.com/bitnami/charts/tree/main/bitnami/redis`
- `https://github.com/bitnami/containers/tree/main/bitnami/redis-cluster`
- `https://github.com/bitnami/containers/tree/main/bitnami/redis`



#### 运行`redis cluser`模式

> 注意：为了实现`redis-cluster-0`等待其他所有节点准备好并执行`redis-cli --cluster create`逻辑，需要借助`helm`生成`REDIS_NODES`(格式：`redis-cluster-0:6379 redis-cluster-1:6379 redis-cluster-2:6379`)。所以暂时放弃自动创建`redis cluster`方法转而使用人工介入的方式创建`redis cluster`。
>
> 注意：使用`k8s`运行`redis7`（`redis7`在初始化集群时支持不使用节点`ip`）集群，使用`--cluster-announce-ip`参数指定`redis`节点的无头服务名称，即使`k8s`节点重启后`redis`节点`ip`地址变化也不会导致`redis`集群失败。

下载`redis cluster`配置到本地，链接`https://gitee.com/dexterleslie/demonstration/tree/master/demo-redis/redis-server/kubernetes-based/mode-cluster`

给`redis`集群性能测试专用节点加污点和标签

> 注意：在性能测试时，避免`jmeter slave`和`redis`集群在同一个`k8s`节点上运行导致相互干扰，所以需要给`redis`集群专用节点添加污点，标签是用于`redis`集群定向调度到指定`k8s`节点中。

```bash
kubectl taint node k8s-node-openresty support-only-perf-target:NoSchedule
kubectl label node k8s-node-openresty support-only-perf-target=
```

创建并启动所有`redis cluster`节点

```bash
./create-k8s.sh
```

查看`redis statefulset`是否已经启动

```bash
kubectl get statefulset
```

等待所有`redis cluster`节点`running`状态后手动创建`redis`集群

>`https://medium.com/geekculture/redis-cluster-on-kubernetes-c9839f1c14b6`

```bash
# 注意：最新版本redis集群创建可以通过下面的 <pod-name>.<headless-service> 方式创建，不需要使用ip地址
export REDIS_NODES=$(kubectl get pods -l app.kubernetes.io/name=redis-cluster -o json | jq -r '.items | map(.status.podIP) | join(":6379 ")'):6379

kubectl exec -it redis-cluster-0 -- redis-cli --cluster create --cluster-replicas 1 ${REDIS_NODES}

# 或者针对redis7使用下面方式创建
# 注意：最新版本`redis`通过`<pod-name>.<headless-service>`方式创建集群
kubectl exec -it redis-cluster-0 -- redis-cli --cluster create --cluster-replicas 1 redis-cluster-0.redis-cluster-headless:6379 redis-cluster-1.redis-cluster-headless:6379 redis-cluster-2.redis-cluster-headless:6379 redis-cluster-3.redis-cluster-headless:6379 redis-cluster-4.redis-cluster-headless:6379 redis-cluster-5.redis-cluster-headless:6379

# 或者
kubectl exec -it redis-cluster-0 -- redis-cli --cluster create redis-cluster-0.redis-cluster-headless:6379 redis-cluster-1.redis-cluster-headless:6379 redis-cluster-2.redis-cluster-headless:6379 redis-cluster-3.redis-cluster-headless:6379 redis-cluster-4.redis-cluster-headless:6379 redis-cluster-5.redis-cluster-headless:6379
```

删除`redis`集群

```bash
./destroy-k8s.sh
```



#### 测试`redis`集群是否正常

查看集群状态是否正常

```bash
kubectl exec -it redis-cluster-0 -- redis-cli -c cluster info
kubectl exec -it redis-cluster-0 -- redis-cli -c cluster nodes
```

通过`set/get`指令测试集群读写是否正常

```bash
# 测试写
kubectl exec -it redis-cluster-0 -- bash -c "for i in {1..15}; do redis-cli -c set key\$i value\$i 1>/dev/null && echo \"成功set key\$i value\$i\"; done"

# 测试读，能够读取所有key*值说明读正常
kubectl exec -it redis-cluster-0 -- bash -c "for i in {1..15}; do echo \"key\$i=\$(redis-cli -c get key\$i)\"; done"
```

删除若干`redis`集群节点，再次查看数据是否会丢失

```bash
# 删除若干节点
kubectl delete pod redis-cluster-0
kubectl delete pod redis-cluster-1
kubectl delete pod redis-cluster-2

kubectl exec -it redis-cluster-0 -- redis-cli -c cluster info

# 测试读，能够读取所有key*值说明没有丢失数据
kubectl exec -it redis-cluster-0 -- bash -c "for i in {1..15}; do echo \"key\$i=\$(redis-cli -c get key\$i)\"; done"
```

