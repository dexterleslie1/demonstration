## redis replication 模式管理

查看 master 的 replication 状态，显示 role: master

```sh
docker exec -it demo-redis-replication-node1 redis-cli info replication
```

查看 slave 的 replication 状态，显示 role: slave

```sh
docker exec -it demo-redis-replication-node2 redis-cli info replication
```

