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

## 主从复制

> 使用redis集群代替主从复制。所以不研究
