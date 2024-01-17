## redis cluster 管理

> 使用 docker compose 建立 redis cluster
> https://blog.yowko.com/docker-compose-redis-cluster/

编辑.env文件填写主机ip地址，.env 内容如下

```properties
# 宿主机ip
varHostIp=192.168.1.181
```

启动集群

```sh
docker compose up -d
```

停止集群

```sh
docker compose down
```

查看集群状态信息

```sh
docker exec -it demo-redis-cluster-node1 redis-cli -p 6380 cluster info
```

查看集群节点信息

```sh
docker exec -it demo-redis-cluster-node1 redis-cli -p 6380 cluster nodes
```

