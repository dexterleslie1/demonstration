## redis sentinel 管理

查看 sentinel 相关状态包括 redis master 节点的信息

```sh
docker exec -it demo-redis-sentinel-node1 redis-cli -p 26379 info sentinel
```

