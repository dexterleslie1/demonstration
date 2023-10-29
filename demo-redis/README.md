## redis命令行

### 切换数据库

```shell
# 切换到0号库
select 0

# 切换到1号库
select 1
```

### 判断key是否存在

```shell
set key1 v1

# 判断key1是否存在
exists key1
```

### keys返回匹配指定模式的所有key

```shell
set key1 v1
set key2 v2

# 返回当前数据库所有key
keys *

# 返回匹配 k* 的所有key
keys k*
```

### type查看指定key的数据类型

```shell
set key1 v1

# 查看key1的数据类型
type key1
```

### del删除指定key

```shell
set key1 v1

# 删除指定key1
del key1
```

### unlink异步删除指定的key

```shell
set key1 v1

# 异步删除key1
unlink key1
```

### expire设置指定key过期时间

```shell
set key1 v1

# 设置 key1 过期时间5秒
expire key1 5

keys *
```

### ttl查看指定key剩余过期时间

> -1 永不过期，-2 已经过期

```shell
set key1 v1

expire key1

# 查看 key1 剩余过期秒数
ttl key1
```

### 查看key个数

```shell
dbsize
```

### flushdb清空当前数据库

```shell
set key1 v1

flushdb

keys *
```

### flushall清空所有数据库

```shell
set key1 v1
select 1
set key2 v2
select 0

flushall

keys *
```

## 数据类型

### string类型

> string类型value最多存储512M字符串

```shell
set key1 v1
get key1

keys *

# 在 key1 中追加v2值
append key1 v2

# 获取字符串长度
strlen key1

# key1不存在时才设置v3值
setnx key1 v3
# 获取 key1 值
get key1

# key1 增加1
incr key1

# key1 减少1
decr key1

# key1增加指定步长
incrby key1 2

# key1减少指定步长
decrby key1 2

# 一次设置多个key
mset k1 v1 k2 v2 k3 v3
keys *

# 一次获取多个key对应的值
mget k1 k2 k3

# 一次设置多个key对应的值，如果有任何一个key存在则设置全部不成功，当且仅当所有key都不存在时设置才成功
mset k1 v1 k2 v2 k3 v4
keys *
mset k5 v5 k1 v1
keys *

# 获取值片段
set key1 123456
# 返回索引位置1-3（左闭右闭）的值片段，结果为"234"
getrange key1 1 3

# 设置值片段
set key1 123456
# 覆盖索引位置为3开始的值片段，结果为"123abc"
setrange key1 3 abc

# 设置键值对时同时设置过期时间，设置键为key1，值为v1，过期时间为10秒的键值对
setex key1 10 v1
ttl key1

# 以新换旧，替换新值，返回旧值
set key1 v1
getset key1 v2
```

### list类型

```shell
# 从左边插入值
lpush k1 v3 v2 v1

# 从左边取出值
lpush k1 v3 v2 v1
# 从左边开始获取索引 0-最后一个元素 列表，-1 表示列表的最后一个元素，-2 表示列表的倒数第二个元素
# https://www.runoob.com/redis/lists-lrange.html
lrange k1 0 -1

# 从右边插入值
rpush k2 v1 v2 v3
lrange k2 0 -1

# 使用lpop/rpop取出一个元素
lpush k1 v3 v2 v1
# 从左边取出一个元素
lpop k1
del k1
lpush k1 v3 v2 v1
# 从右边取出一个元素
rpop k1

# rpoplpush用法
rpush k1 v1 v2 v3
rpush k2 1 2 3
rpoplpush k1 k2
lrange k1 0 -1
lrange k2 0 -1

# 获取指定索引的元素，不会删除元素
rpush k1 v1 v2 v3
# 结果为v1
lindex k1 0
# 结果为v2
lindex k2 1

# 获取列表长度
rpush k1 v1 v2 v3
llen k1

# 在指定元素前插入元素
rpush k1 v1 v2 v3
linsert k1 before v2 newv2
lrange k1 0 -1

# 在指定元素后插入元素
rpush k2 v1 v2 v3
linsert k2 after v2 newv2
lrange k2 0 -1

# 从左边起删除2个v1元素
rpush k1 v1 v2 v2 v3
lrem k1 2 v2
lrange k1 0 -1

# 替换指定索引元素
rpush k1 v1 v2 v3
lset k1 0 v11
lrange k1 0 -1
```

### set类型

> 元素不重复，无序排列

```shell
# 获取全部元素
sadd k1 v1 v2 v3
smembers k1

# 判断元素是否存在
sadd k1 v1 v2 v3
# 结果返回1
sismember k1 v2
# 结果返回0
sismember k1 888

# 返回集合元素个数
sadd k1 v1 v2 v3
scard k1

# 删除集合元素
sadd k1 v1 v2 v3
srem k1 v2
smembers k1

# 从集合中随机pop一个元素
sadd k1 v1 v2 v3
spop k1
smembers k1

# 把集合中的一个值移动到另外一个集合中
sadd k1 v1 v2 v3 v5
sadd k2 v1 v2
# 把k1中的v5移动到k2中
smove k1 k2 v5
smembers k1
smembers k2

# 求集合的交集
sadd k1 v1 v2 v3
sadd k2 v3 v5 v6
# 结果返回v3
sinter k1 k2

# 求集合的并集
sadd k1 v1 v2 v3
sadd k2 v3 v5 v6
# 结果返回 v1 v2 v3 v5 v6
sunion k1 k2

# 求集合 k1-k2 差集结果
sadd k1 v1 v2 v3
sadd k2 v3 v5 v6
# 结果返回 v1 v2
sdiff k1 k2
```

### hash类型

```shell
# 设置和读取hash值
hset user1001 id 1001
hset user1001 name zhangsan
hget user1001 id
hget user1001 name

# 一次设置和读取多个field值
hmset user1001 id 1001 name zhangsan
hmget user1001 id name

# 判断field是否存在
hmset user1001 id 1001 name zhangsan
# 结果返回1
hexists user1001 id
# 结果返回0
hexists user1001 id1

# 列出集合所有field
hmset user1001 id 1001 name zhangsan
hkeys user1001

# 列出集合所有value
hmset user1001 id 1001 name zhangsan
hvals user1001

# 为指定的field增加指定的值
hmset user1001 id 1001 name zhangsan
# id field增加10
hincrby user1001 id 10

# field不存在时才设置指定值
hmset user1001 id 1001 name zhangsan
# field id已经存在设置不成功
hsetnx user1001 id 1002
# field gender不存在设置成功
hsetnx user1001 gender female
```

### zset类型（有序集合）

```shell
# 创建集合
zadd topn 100 java 400 c++ 200 php 300 mysql

# 从小到大排序方式返回结果 java php mysql c++
zadd topn 100 java 400 c++ 200 php 300 mysql
zrange topn 0 -1

# 获取结果包含分数
zadd topn 100 java 400 c++ 200 php 300 mysql
zrange topn 0 -1 withscores

# 返回200-300之间（包括200和300），升序排列
zadd topn 100 java 400 c++ 200 php 300 mysql
zrangebyscore topn 200 300 withscores

# 返回200-300之间（包括200和300），降序排序
zadd topn 100 java 400 c++ 200 php 300 mysql
zrevrangebyscore topn 300 200 withscores

# 增加指定值的score
zadd topn 100 java 400 c++ 200 php 300 mysql
# java score增加50
zincrby topn 50 java

# 删除指定member
zadd topn 100 java 400 c++ 200 php 300 mysql
zrem topn java

# 统计指定分数区间内元素个数
zadd topn 100 java 400 c++ 200 php 300 mysql
zcount topn 200 300

# 查询指定member排名
zadd topn 100 java 400 c++ 200 php 300 mysql
# 返回2，表示排名为第3位
zrank topn mysql
```

### bitmaps类型

> 暂时未用到

## 发布订阅

```shell
# 订阅channel1
subscribe channel1

# 向channel1发布消息
publish channel1 hello
```

## 事务

> 参考 demo-redis/redis-jedis/demo-redis-transaction demo
>
> redis事务三大特性（https://blog.csdn.net/Mudrock__/article/details/127514693）：
>
> - **单独隔离**：事务中的所有命令都会被序列化，并依次执行。事务在执行时，不会被其他命令打断
> - **无隔离级别概念**：事务中的所有命令被提交执行前均不会执行，而是进入队列等待执行
> - **不保证原子性**：事务中的命令发生[运行时异常](https://so.csdn.net/so/search?q=运行时异常&spm=1001.2101.3001.7020)时（可编译通过），该命令不执行并抛出异常，但其他命令照常执行，事务并不发生回滚

## lua脚本

> 参考 demo-redis/redis-jedis/demo-jedis-lua-script demo

```shell
# 使用eval函数执行lua脚本，1表示有1个key参数
# https://www.jianshu.com/p/4558689c13be
eval 'return "hello " .. KEYS[1] .. " " .. ARGV[1]' 1 redis world

# 使用redis.call调用redis命令
eval 'return redis.call("set", "k1", "sv1")' 0
get "k1"

# 调用lua脚本文件
# 1.lua脚本文件内容如下：
return 'hello ' .. KEYS[1]
# 调用1.lua脚本文件，返回值hello redis
redis-cli -a 123456 --eval ./1.lua redis
```

## 持久化

> https://www.cnblogs.com/iceggboom/p/13749948.html
>
> **RDB：**Redis 将内存数据库快照保存在名字为 `dump.rdb` 的二进制文件中。
>
> 优点：
>
> - 只会生成一个文件，方便操作，文件小
> - 相对AOF来说，恢复大数据集的速度快
> - 在RDB持久化开始时，只会fork一个子线程处理所有的持久化工作，不会影响到父系线程
>
> 缺点：
>
> - 如果你想保证数据的高可用性，即最大限度的避免数据丢失，那么RDB将不是一个很好的选择。因为系统一旦在定时持久化之前出现宕机现象，此前没有来得及写入磁盘的数据都将丢失。
> - 由于RDB是通过fork子进程来协助完成数据持久化工作的，因此，如果当数据集较大时，可能会导致整个服务器停止服务几百毫秒，甚至是1秒钟。
>
> **AOF：**从现在开始， 每当 Redis 执行一个改变数据集的命令时（比如 SET）， 这个命令就会被追加到 AOF 文件的末尾。
>
> 这样的话， 当 Redis 重新启动时， 程序就可以通过重新执行 AOF 文件中的命令来达到重建数据集的目的。
>
> 优点：
>
> - AOF可以更好的保护数据不丢失，一般AOF会每隔1秒，通过一个后台线程执行一次fsync操作，最多丢失1秒钟的数据。
> - AOF日志文件没有任何磁盘寻址的开销，写入性能非常高，文件不容易破损。
> - AOF日志文件即使过大的时候，出现后台重写操作，也不会影响客户端的读写。
> - AOF日志文件的命令通过非常可读的方式进行记录，这个特性非常适合做灾难性的误删除的紧急恢复。
>
> 缺点：
>
> - AOF日志文件相对于RDB来说会更大
> - AOF开启后，支持的写QPS会比RDB支持的写QPS低
>
> **RDB+AOF混合持久化：**如果开启了混合持久化，aof在重写时，不再是单纯将内存数据转换为RESP命令写入aof文件，而是将重写这一刻之前的内存做rdb快照处理，并且将rdb快照内容和增量的aof修改内存数据的命令存在一起，都写入新的aof文件，新的aof文件一开始不叫appendonly.aof，等到重写完成后，新的aof文件才会进行改名，原子的覆盖原有的aof文件，完成新旧两个aof文件的替换。
> 于是在redis重启的时候，可以先加载rdb文件，然后再重放增量的aof日志就可以完全替代之前的aof全量文件重放，因此重启效率大幅得到提高。
>
> 简单说就是BGSAVE做镜像全量持久化，AOF做增量持久化。

### AOF

```shell
# 默认关闭若要开启将no改为yes
appendonly yes
# append文件的名字
appendfilename "appendonly.aof"
# AOF文件的写入方式
# always一旦缓存区内容发生变化就写入AOF文件中
appendfsync always
# everysec 每个一秒将缓存区内容写入文件 默认开启的写入方式
appendfsync everysec
# 将写入文件的操作交由操作系统决定
appendfsync no
# 当AOF文件大小的增长率大于该配置项时自动开启重写（这里指超过原大小的100%）。
auto-aof-rewrite-percentage 100
# 当AOF文件大小大于该配置项时自动开启重写
auto-aof-rewrite-min-size 64mb
```

### RDB

```shell
# 在几秒内改动了多少数据就触发持久化
# 想禁用的话不设置save   或者save ""
save 900 1
save 300 10
# 60秒内有10个key变动则在第60秒持久化到存储中
save 60 10000
# 备份进程出错主进程停止写入操作
stop-writes-on-bgsave-error yes
# 是否压缩rdb文件 推荐no 相对于硬盘成本cpu更值钱
rdbcompression yes
```

### RDB+AOF混合持久化

> 注意：混合持久化是在AOF重写阶段起作用，需要启用AOF持久化配置，可以不启用RDB持久化配置
>
> 参考 demo-redis/redis-server/standalone/redis.conf 配置

```shell
# 启用RDB+AOF
aof-user-rdb-preamble yes

# 进入redis-cli，使用bgrewriteaof触发AOF重写
bgwriteaof
```

## 





## 集群

> https://baijiahao.baidu.com/s?id=1768636453217086050&wfr=spider&for=pc





### 主从复制模式

> 使用场景：读多写少的情况
>
> 主从复制，是指将一台 Redis 服务器的数据，复制到其他的 Redis 服务器。前者称为主节点(Master)，后者称为从节点(Slave)；数据的复制是单向的，只能由主节点到从节点。
> 默认情况下，每台 Redis 服务器都是主节点；且一个主节点可以有多个从节点 (或没有从节点)，但一个从节点只能有一个主节点。
> 数据冗余：主从复制实现了数据的热备份，是持久化之外的一种数据冗余方式。
> 故障恢复：当主节点出现问题时，可以由从节点提供服务，实现快速的故障恢复；实际上是一种服务的冗余。
> 负载均衡：在主从复制的基础上，配合读写分离，可以由主节点提供写服务，由从节点提供读服务 (即写 Redis 数据时应用连接主节点，读 Redis 数据时应用连接从节点)，分担服务器负载；尤其是在写少读多的场景下，通过多个从节点分担读负载，可以大大提高Redis服务器的并发量。
>
> 参考 redis-server/mode-replication，NOTE: 使用redistemplate/redistemplate-replication调试mode-replication

#### 主从切换

> NOTE: 此模式下只支持人工主从切换
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



#### 新增slave节点

```shell
# 启动redis replication模式
docker-compose up -d

# 启动完毕后新增节点
docker-compose -f docker-compose-add-slave-node.yaml up -d

# 此时可以使用 redistemplate/redistemplate-replication 调试
```



#### 删除slave节点

> 通过docker stop停止slave容器后，slave节点就能够自动从master slave节点列表中删除。
> 自动删除的slave节点不再参与spring-data-redis的读负载均衡。
> 自动删除的slave节点如果再次重新上线则又会重新参与到spring-data-redis的读负载均衡中。
> slave节点的删除和新增都不需要重启spring-data-redis应用就能够感知到slave的删除和新增。

```shell
# 停止容器表示删除节点
docker stop demo-redis-replication-node4

# 此时可以使用 redistemplate/redistemplate-replication 调试
```



### 哨兵模式

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

### cluster模式

> 使用docker运行redis集群参考 demo-redis/redis-server/mode-cluster
>
> redis集群原理详解 https://blog.csdn.net/a745233700/article/details/112691126
>
> 默认情况下，redis集群的读和写都是到master上去执行的，不支持slave节点读和写，跟Redis主从复制下读写分离不一样，因为redis集群的核心的理念，主要是使用slave做数据的热备，以及master故障时的主备切换，实现高可用的。Redis的读写分离，是为了横向任意扩展slave节点去支撑更大的读吞吐量。而redis集群架构下，本身master就是可以任意扩展的，如果想要支撑更大的读或写的吞吐量，都可以直接对master进行横向扩展。
>
> jedis操作集群参考 demo-redis/redis-jedis/jedis-cluster-load
>
> redistemplate操作集群参考 demo-redis/redistemplate/redistemplate-cluster





## 秒杀场景实现

### 单机版秒杀

> 参考 demo-redis/demo-flash-sale-standalone

### 集群版秒杀

> 参考 demo-redis/demo-flash-sale-cluster（todo 未完成）

## 锁

### 读写锁ReadWrite lock

> https://blog.csdn.net/qq_43750656/article/details/108634781
>
> 一次只有一个线程可以占有写模式的读写锁, 但是可以有多个线程同时占有读模式的读写锁. 正是因为这个特性,
> 当读写锁是写加锁状态时, 在这个锁被解锁之前, 所有试图对这个锁加锁的线程都会被阻塞.
> 当读写锁在读加锁状态时, 所有试图以读模式对它进行加锁的线程都可以得到访问权, 但是如果线程希望以写模式对此锁进行加锁, 它必须直到所有的线程释放锁.
> 通常, 当读写锁处于读模式锁住状态时, 如果有另外线程试图以写模式加锁, 读写锁通常会阻塞随后的读模式锁请求, 这样可以避免读模式锁长期占用, 而等待的写模式锁请求长期阻塞.
> 读写锁适合于对数据结构的读次数比写次数多得多的情况. 因为, 读模式锁定时可以共享, 以写模式锁住时意味着独占, 所以读写锁又叫共享-独占锁.
>
> 参考 demo-redis/redisson/redisson-lock demo

### 可重入锁Reentrant lock

> https://blog.csdn.net/m0_62946761/article/details/126688793
>
> 同一个线程能够多次上锁，redisson.getLock返回的RLock对象是ReentraintLock
>
> 利用redis的hash结构存储锁，key值随意，field属性为线程标识，value为锁次数。当线程获取一次锁后，如果此时redis中没有这个锁，则创建并将锁次数置为1；接下来如果线程再次获取锁会进行一次判断。即对比线程标识是否是同一个线程的多次获取，如果是的话锁次数+1。同样的，如果是释放锁的话也需要对线程标识进行判断，然后让对应的锁次数-1，当锁的次数为0时，表示此时可以删除锁了。
>
> 参考 demo-redis/redisson/redisson-lock demo

### 公平锁FairLock

> 按照加锁的请求顺序公平地（先到先得）分配上锁机会
>
> 参考 demo-redis/redisson/redisson-lock demo

### 多锁MultiLock

> https://blog.csdn.net/qq_32979219/article/details/126784890
>
> 参考 demo-redis/redisson/redisson-lock demo

### 信号量Semaphore

> https://blog.csdn.net/m0_70651612/article/details/124901452
>
> 参考 demo-redis/redisson/redisson-lock demo

### 闭锁CountDownLatch

> https://blog.csdn.net/m0_70651612/article/details/124901452
>
> 参考 demo-redis/redisson/redisson-lock demo

## mysql redis缓存双写不一致

> 对于热点数据（经常被查询，但不经常被修改的数据），我们可以将其放入redis缓存中，以增加查询效率，但需要保证从redis中读取的数据与数据库中存储的数据最终是一致的。针对一致性的问题进行了汇总总结。
>
> 
>
> https://blog.csdn.net/sanylove/article/details/127849015
>
> MySQL和redis缓存不一致情况如下：
>
> - 先更新数据库，再更新缓存：「请求 A 」先把数据库的数据更新为1，然后在更新缓存之前，「请求 B 」再将数据库的数据更新为2，紧接着把缓存数据更新为2，然后「请求 A 」才更新缓存数据为1。
>
> - 先更新缓存，再更新数据库：「请求 A 」先将缓存的数据更新为 1，然后在更新数据库前，「请求 B 」将缓存的数据更新为 2，紧接着把数据库更新为 2，然后「请求 A 」才将数据库的数据为1。
>
> - 先删除缓存，再更新数据库：「请求 A 」先将缓存的数据删除，然后在更新数据库前，「请求 B 」来读取数据，但是没有在缓存中命中，所以「请求 B 」会去数据库读取数据，并更新到缓存中去，然后「请求 A 」才将数据库的数据。所以，先删除缓存，再更新数据库，在「读 + 写」并发的时候，还是会出现缓存和数据库的数据不一致的问题。
>
> - 先更新数据库，再删除缓存：
>
>   「请求 A 」去读取数据，但是未在缓存中命中，去数据库读取数据，但是在数据库读取数据之后还没有更新缓存数据之前，「请求 B 」去更新数据库数据，然后删除缓存数据，然后「请求 A 」才更新缓存数据。
>
>   从上面的理论上分析，先更新数据库，再删除缓存也是会出现数据不一致性的问题，但是在实际中，这个问题出现的概率并不高。因为缓存的写入通常要远远快于数据库的写入，所以在实际中很难出现请求 B 已经更新了数据库并且删除了缓存，请求 A 才更新完缓存的情况。所以，「先更新数据库 + 再删除缓存」的方案，是可以保证数据一致性的。
>
>   从上面我们也知道「先更新数据库，再删除缓存」这属于两个操作，那么就会出现更新数据库成功，删除缓存失败的状态。如果出现这种状态，修改的数据是要过一段时间才生效，这个还是在我们加入过期时间的前提下。
>
>   那么怎么确保两个操作都能成功呢？其实解决方案有两种：重试机制和订阅 MySQL binlog，再操作缓存。
>
>   
>
> 不一致情况总结： 
>
> - 对于「先删除缓存，再更新数据库」这种读 + 写」并发请求而造成缓存不一致的解决办法：延迟双删。
>
>   延迟双删实现的伪代码如下：
>
>   ```
>   #删除缓存
>   redis.delKey(X)
>   #更新数据库
>   db.update(X)
>   #睡眠
>   Thread.sleep(N)
>   #再删除缓存
>   redis.delKey(X)
>   ```
>
> - 所以，不管是「先更新数据库，再更新缓存」，还是「先更新缓存，再更新数据库」，这两个方案都存在并发问题。即当两个请求并发更新同一条数据的时候，可能会出现缓存和数据库中的数据不一致的现象。
>
>   我们通过分析可以知道「先更新数据库，再更新缓存」和「先更新缓存，再更新数据库」（即两个更新）在并发的时候，出现数据不一致问题。主要是因为更新数据库和更新缓存这两个操作是独立的，而我们又没有对操作做任何并发控制，那么当两个线程并发更新它们的话，就会因为写入顺序的不同造成数据的不一致。所以，我们可以对这两个操作进行控制，方法如下：
>
>   1、在更新缓存前先加个分布式锁，保证同一时间只运行一个请求更新缓存，就会不会产生并发问题了，当然引入了锁后，对于写入的性能就会带来影响。
>
>   2、在更新完缓存时，给缓存加上较短的过期时间，这样即时出现缓存不一致的情况，缓存的数据也会很快过期，对业务还是能接受的。
>
> - 「先更新数据库，再删除缓存」不一致问题可以在缓存中加入过期时间，这样就算出现了缓存和数据库不一致问题，但最终是一致的。

## 缓存穿透、击穿、雪崩

### 缓存穿透

> https://baijiahao.baidu.com/s?id=1730541502423010481&wfr=spider&for=pc
>
> 缓存穿透是指查询一个缓存中和数据库中都不存在的数据，导致每次查询这条数据都会透过缓存，直接查库，最后返回空。当用户使用这条不存在的数据疯狂发起查询请求的时候，对数据库造成的压力就非常大，甚至可能直接挂掉。
>
> 解决缓存穿透的方法一般有两种，第一种是缓存空对象，第二种是使用布隆过滤器。
>
> 参考 demo-redis/demo-redis-cache-penetration

### 缓存击穿

> https://baijiahao.baidu.com/s?id=1730541502423010481&wfr=spider&for=pc
>
> 缓存击穿是指当缓存中某个热点数据过期了，在该热点数据重新载入缓存之前，有大量的查询请求穿过缓存，直接查询数据库。这种情况会导致数据库压力瞬间骤增，造成大量请求阻塞，甚至直接挂掉。
>
> 解决缓存击穿的方法也有两种，第一种是设置key永不过期；第二种是使用分布式锁，保证同一时刻只能有一个查询请求重新加载热点数据到缓存中，这样，其他的线程只需等待该线程运行完毕，即可重新从Redis中获取数据。
>
> 参考 demo-redis/demo-redis-cache-breakdown

### 缓存雪崩

> https://baijiahao.baidu.com/s?id=1730541502423010481&wfr=spider&for=pc
>
> 缓存雪崩是指当缓存中有大量的key在同一时刻过期，或者Redis直接宕机了，导致大量的查询请求全部到达数据库，造成数据库查询压力骤增，甚至直接挂掉。
>
> 针对第一种大量key同时过期的情况，解决起来比较简单，只需要将每个key的过期时间打散即可，使它们的失效点尽可能均匀分布。
>
> 针对第二种redis发生故障的情况，部署redis时可以使用redis的几种高可用方案部署
>
> 因为这种情况基本不会遇到所有不写demo
