# `redis`管理



## 主从复制模式

> 使用场景：读多写少的情况
>
> 主从复制，是指将一台 Redis 服务器的数据，复制到其他的 Redis 服务器。前者称为主节点(Master)，后者称为从节点(Slave)；数据的复制是单向的，只能由主节点到从节点。
> 默认情况下，每台 Redis 服务器都是主节点；且一个主节点可以有多个从节点 (或没有从节点)，但一个从节点只能有一个主节点。
> 数据冗余：主从复制实现了数据的热备份，是持久化之外的一种数据冗余方式。
> 故障恢复：当主节点出现问题时，可以由从节点提供服务，实现快速的故障恢复；实际上是一种服务的冗余。
> 负载均衡：在主从复制的基础上，配合读写分离，可以由主节点提供写服务，由从节点提供读服务 (即写 Redis 数据时应用连接主节点，读 Redis 数据时应用连接从节点)，分担服务器负载；尤其是在写少读多的场景下，通过多个从节点分担读负载，可以大大提高Redis服务器的并发量。
>
> 参考 redis-server/mode-replication，NOTE: 使用redistemplate/redistemplate-replication调试mode-replication

### 主从切换

> NOTE: 此模式下只支持人工主从切换
>
> 人工主从切换后需要修改spring-data-redis配置指向到新master节点后重启应用。
>
> redis 主从切换命令
> https://blog.csdn.net/qq_36949713/article/details/106812171?app_version=6.1.9&csdn_share_tail=%7B%22type%22%3A%22blog%22%2C%22rType%22%3A%22article%22%2C%22rId%22%3A%22106812171%22%2C%22source%22%3A%22dexterchan%22%7D&utm_source=app

```shell
# 使用docker-compose启动上面的mode-replication主从模式

# 模拟master节点down机，NOTE: 此时集群支持读但不支持写
docker stop demo-redis-replication-node1

# 可以查看其中一个slave显示master是down状态
docker exec -it demo-redis-replication-node3 redis-cli info replication

# 切换 demo-redis-replication-node3 为master
docker exec -it demo-redis-replication-node3 redis-cli slaveof no one
# 持久化配置到redis.conf
docker exec -it demo-redis-replication-node3 redis-cli config rewrite
# 可以看到当前 demo-redis-replication-node3 已经切换为master，但还没有slave节点
docker exec -it demo-redis-replication-node3 redis-cli info replication

# 设置 demo-redis-replication-node2 指向新的master
docker exec -it demo-redis-replication-node2 redis-cli slaveof demo-redis-replication-node3 6379
docker exec -it demo-redis-replication-node2 redis-cli config rewrite
# 设置 demo-redis-replication-node4 指向新的master
docker exec -it demo-redis-replication-node4 redis-cli slaveof demo-redis-replication-node3 6379
docker exec -it demo-redis-replication-node4 redis-cli config rewrite

# 可以看到新的master下有3个slave节点了
docker exec -it demo-redis-replication-node3 redis-cli info replication

# 最后把应用配置 redistemplate/redistemplate-replication 修改新的master节点即可
```



### 新增slave节点

> 新增节点后redis会自动通知spring-data-redis更新slave读取节点，新节点会参与到读负载均衡中。

```shell
# 启动redis replication模式
docker-compose up -d

# 启动完毕后新增节点
docker-compose -f docker-compose-add-slave-node.yaml up -d

# 此时可以使用 redistemplate/redistemplate-replication 调试
```



### 删除slave节点

> 通过docker stop停止slave容器后，slave节点就能够自动从master slave节点列表中删除。
> 自动删除的slave节点不再参与spring-data-redis的读负载均衡。
> 自动删除的slave节点如果再次重新上线则又会重新参与到spring-data-redis的读负载均衡中。
> slave节点的删除和新增都不需要重启spring-data-redis应用就能够感知到slave的删除和新增。

```shell
# 停止容器表示删除节点
docker stop demo-redis-replication-node4

# 此时可以使用 redistemplate/redistemplate-replication 调试
```



## 哨兵模式

> 主从切换技术的方法是：当服务器宕机后，需要手动一台从机切换为主机，这需要人工干预，不仅费时费力，而且还会造成一段时间内服务不可用。为了解决主从复制的缺点，就有了哨兵模式。
> 哨兵(sentinel)：是一个分布式系统，用于对主从结构中的每台服务器进行监控，当出现故障时通过投票机制选择新的 Master 并将所有 Slave 连接到新的 Master。所以整个运行哨兵的集群的数量不得少于3个节点。
>
> 哨兵模式的作用
>
> - 监控：哨兵会不断地检查主节点和从节点是否运作正常。
> - 自动故障转移：当主节点不能正常工作时，哨兵会开始自动故障转移操作，它会将失效主节点的其中一个从节点升级为新的主节点，并让其他从节点改为复制新的主节点。
> - 通知（提醒）：哨兵可以将故障转移的结果发送给客户端。
>
> 哨兵模式的结构由两部分组成，哨兵节点和数据节点：
>
> - 哨兵节点：哨兵系统由一个或多个哨兵节点组成，哨兵节点是特殊的redis节点，不存储数据。
> - 数据节点：主节点和从节点都是数据节点。
>
> 参考 redis-server/mode-sentinel



### 模拟master节点down（删除节点）

> master节点down后大约过了30s，sentinel重新选举一个新的master节点。spring-data-redis会被通知新的master节点和slave读节点不需要重启应用。

```shell
# 关闭master节点，大约等待30秒左右，sentinel会从slave节点中选举一个节点作为新的master
docker stop demo-redis-sentinel-repl-node1

# redistemplate/redistemplate-sentinel会被通知新的master节点，不需要重启应用

# 关闭master节点后，查看各个节点的replication信息被sentinel更新了
docker exec -it demo-redis-sentinel-repl-node2 redis-cli info replication
docker exec -it demo-redis-sentinel-repl-node3 redis-cli info replication
docker exec -it demo-redis-sentinel-repl-node4 redis-cli info replication
# 查看sentinel master节点sentinel信息
docker exec -it demo-redis-sentinel-node1 redis-cli -p 26379 info sentinel
```





### 模拟slave节点down（删除节点）

> slave节点down后，spring-data-redis会被通知删除slave读节点不需要重启应用。

```shell
# 关闭 demo-redis-sentinel-repl-node3 slave节点
docker stop demo-redis-sentinel-repl-node3
```





### 新增slave节点

> slave节点新增后，spring-data-redis会被通知新增slave读节点不需要重启应用。

```shell
# 新增slave节点
docker-compose -f docker-compose-add-slave-node.yml up -d
```





## `cluster`模式管理

>[redis集群原理详解](https://blog.csdn.net/a745233700/article/details/112691126)
>
>[redis插槽](https://baijiahao.baidu.com/s?id=1767053233302021116&wfr=spider&for=pc)

默认情况下，`redis`集群的读和写都是到`master`上去执行的，不支持`slave`节点读和写，跟`Redis`主从复制下读写分离不一样，因为`redis`集群的核心的理念，主要是使用`slave`做数据的热备，以及`master`故障时的主备切换，实现高可用的。`Redis`的读写分离，是为了横向任意扩展`slave`节点去支撑更大的读吞吐量。而`redis`集群架构下，本身`master`就是可以任意扩展的，如果想要支撑更大的读或写的吞吐量，都可以直接对`master`进行横向扩展。

使用以下示例辅助学习：

- `https://gitee.com/dexterleslie/demonstration/tree/master/demo-redis/redis-server/docker-based/mode-cluster`
- `https://gitee.com/dexterleslie/demonstration/tree/master/demo-redis/redis-jedis/jedis-cluster-load`
- `https://gitee.com/dexterleslie/demonstration/tree/master/demo-redis/redistemplate/redistemplate-cluster`

使用`redis-cli`创建`redis`集群

```bash
# redis7之前创建集群只支持ip地址，例如下面的xxx代表节点所在的ip地址
# 创建集群后依次为`6380`和`6383`互为主从、`6381`和`6384`互为主从、`6382`和`6385`互为主从
redis-cli --cluster create --cluster-replicas 1 xxx:6380 xxx:6381 xxx:6382 xxx:6383 xxx:6384 xxx:6385

# redis7之后创建集群支持使用节点DNS名称，例如下面的node1、node2
redis-cli --cluster create --cluster-replicas 1 node1:6379 node2:6379 node3:6379 node4:6379 node5:6379 node6:6379
```



查看集群中所有节点信息

```bash
cluster nodes
```

查看集群状态信息

```bash
cluster info
```



### failover测试

> 测试场景： 停止1、3、5节点集群依旧能够正常提供服务。
>
> TODO: 同时停止1、3、5节点会导致集群无法恢复，必须要停止一个节点failover后再停止另外一个。

```shell
# 连接节点2查询集群状态
docker exec -it demo-redis-cluster-node2 sh
redis-cli -p 6381
# 显示集群状态为ok
cluster info
# 显示集群节点状态
cluster nodes

# NOTE: 依次停止节点等待failover成功后再停止下一个节点
docker stop demo-redis-cluster-node1
docker stop demo-redis-cluster-node3
docker stop demo-redis-cluster-node5

# 最后集群依旧能够正常提供服务
```



### 在线扩容

> 新增节点 https://zhuanlan.zhihu.com/p/540573229?utm_id=0
>
> todo: 使用redistemplate/redistemplate-cluster创造数据后添加节点会报告错误 CROSSSLOT Keys in request don't hash to the same slot

```shell
### 添加新的master节点到集群中

# 启动新节点容器
docker-compose -f docker-compose-add-node.yaml up -d

# 登录新的redis节点，把新节点添加到redis cluster中
docker exec -it demo-redis-cluster-node-extra sh
# demo-redis-cluster-node-extra:6390 为新节点
# 192.168.1.181:6380 为已存在的任意一个集群节点
# NOTE: 下面已存在节点不能使用docker容器网络名称只能够使用192.168.1.181 ip地址，否则报错
redis-cli --cluster add-node demo-redis-cluster-node-extra:6390 192.168.1.181:6380

# 此时新节点无法接收和处理请求，因为新节点还没有分配插槽
# 为新节点分配插槽
docker exec -it demo-redis-cluster-node-extra sh
# 分配插槽
# 输入需要移动的插槽数1000，然后输入接收插槽数据的新节点id，输入all表示从所有现有的节点中抽取共1000个插槽并移动到新节点中
redis-cli --cluster reshard demo-redis-cluster-node1:6380

# 查看集群状态，此时新的节点能够提供服务了
docker exec -it demo-redis-cluster-node1 sh
redis-cli -p 6380 cluster nodes




### 为新的节点添加从节点
# 登录从节点
docker exec -it demo-redis-cluster-node-extra-slave sh
# 添加节点到集群中
redis-cli --cluster add-node demo-redis-cluster-node-extra-slave:6391 192.168.1.181:6380
# 指定当前从节点的主节点
redis-cli -p 6391
cluster replicate 2728a594a0498e98e4b83a537e19f9a0a3790f38

# 查看集群状态
docker exec -it demo-redis-cluster-node1 sh
redis-cli -p 6380 cluster nodes
```



### 在线缩容

> 新增节点 https://zhuanlan.zhihu.com/p/540573229?utm_id=0

```shell
### 删除从节点

# 登录从节点
docker exec -it demo-redis-cluster-node-extra-slave sh

# 删除从节点
# 192.168.1.181:6391 是通过cluster nodes获取的从节点ip:port
# 6b92155a7e8851b9842c4a3ec38ff9d2b230b33e 是通过cluster nodes获取的从节点id
redis-cli --cluster del-node 192.168.1.181:6391 6b92155a7e8851b9842c4a3ec38ff9d2b230b33e

# 查看集群状态
docker exec -it demo-redis-cluster-node1 sh
redis-cli -p 6380 cluster nodes




### 删除主节点

# 登录主节点
docker exec -it demo-redis-cluster-node-extra sh

# 迁移将要删除的主节点slot到其他节点中，根据提示将节点中所有slot迁移，NOTE: sources ID输入被删除的主节点id，不能输入all
redis-cli --cluster reshard demo-redis-cluster-node-extra:6390

# slot迁移成功后，删除主节点
redis-cli --cluster del-node demo-redis-cluster-node-extra:6390 65999fa8f6dc13726008c35dfd0e60cec7e2c7da
```





