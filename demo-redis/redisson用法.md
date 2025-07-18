# `redisson`用法



## `SpringBoot`应用集成`redisson`

详细用法请参考示例`https://gitee.com/dexterleslie/demonstration/tree/master/demo-redis/demo-spring-boot-redisson`

项目`maven`的`pom.xml`配置

```xml
<dependency>
    <groupId>org.redisson</groupId>
    <artifactId>redisson</artifactId>
    <version>3.19.1</version>
</dependency>
```

**Redis Standalone 模式配置**

```java
@Configuration
public class RedissonConfig {
    @Bean
    RedissonClient redissonClient() {
        // Redis Standalone 模式配置
        Config config = new Config();
        config.useSingleServer()
                .setAddress("redis://localhost:6379")
                .setPassword("123456");

        return Redisson.create(config);
    }
}
```

**Redis Replication 模式配置**

```java
@Configuration
public class RedissonConfig {
    @Bean
    RedissonClient redissonClient() {
        // Redis Replication 模式配置
        Config config = new Config();
        config.useReplicatedServers()
                .addNodeAddress(
                        "redis://localhost:6479"
                        , "redis://localhost:6480"
                        , "redis://localhost:6481");

        return Redisson.create(config);
    }
}
```

**Redis Sentinel 模式配置**

```java
@Configuration
public class RedissonConfig {
    @Bean
    RedissonClient redissonClient() {
        // Redis Sentinel 模式配置
        Config config = new Config();
        config.useSentinelServers()
                .setMasterName("mymaster")
                // 下面配置 Sentinel 节点
                .addSentinelAddress(
                        "redis://localhost:26579",
                        "redis://localhost:26580",
                        "redis://localhost:26581");

        return Redisson.create(config);
    }
}
```

**Redis Cluster 模式配置**

```java
@Configuration
public class RedissonConfig {
    @Bean
    RedissonClient redissonClient() {
        // Redis Cluster 模式配置
        Config config = new Config();
        config.useClusterServers()
                .addNodeAddress(
                        "redis://localhost:6679",
                        "redis://localhost:6680",
                        "redis://localhost:6681",
                        "redis://localhost:6682",
                        "redis://localhost:6683",
                        "redis://localhost:6684");

        return Redisson.create(config);
    }
}
```

测试`RedissonClient`

```java
@RunWith(SpringRunner.class)
@SpringBootTest(classes={Application.class})
@Slf4j
public class Tests {
	@Autowired
	RedissonClient redissonClient;

	@Test
	public void test() {
		String key = UUID.randomUUID().toString();

		RLock rLock = this.redissonClient.getLock(key);
		boolean acquired = false;
		try {
			acquired = rLock.tryLock();
		} finally {
			if(acquired) {
				try {
					rLock.unlock();
				} catch (Exception ignored) {

				}
			}
		}

		Assert.assertTrue(acquired);
	}
}
```



## 自定义分布式锁

详细用法请参考示例`https://gitee.com/dexterleslie/demonstration/tree/master/demo-redis/demo-customize-redis-distributed-lock`



## 锁的标准用法

详细代码请参考`https://gitee.com/dexterleslie/demonstration/tree/master/demo-redis/demo-spring-boot-redisson`

示例代码如下：

```java
/**
 * 测试标准用法
 *
 * @throws InterruptedException
 */
@Test
public void test() throws InterruptedException {
    final String key = UUID.randomUUID().toString();

    AtomicInteger atomicIntegerAcquired = new AtomicInteger();
    AtomicInteger atomicIntegerNotAcquired = new AtomicInteger();
    ExecutorService service = Executors.newCachedThreadPool();
    int totalConcurrent = 100;
    for (int i = 0; i < totalConcurrent; i++) {
        service.submit(new Runnable() {
            public void run() {
                RLock lock = null;
                boolean acquired = false;
                try {
                    lock = redisson.getLock(key);

                    acquired = lock.tryLock(10, 30000, TimeUnit.MILLISECONDS);
                    if (!acquired) {
                        atomicIntegerNotAcquired.incrementAndGet();
                        return;
                    }

                    // 锁定2秒
                    Thread.sleep(500);
                    atomicIntegerAcquired.incrementAndGet();
                } catch (Exception ex) {
                    throw new RuntimeException(ex);
                } finally {
                    if (acquired) {
                        try {
                            lock.unlock();
                        } catch (Exception ex) {
                            //
                        }
                    }
                }
            }
        });
    }
    service.shutdown();
    while (!service.awaitTermination(100, TimeUnit.MILLISECONDS)) ;

    // 只有一个并发请求能够取得锁
    Assert.assertEquals(1, atomicIntegerAcquired.get());
    Assert.assertEquals(totalConcurrent - 1, atomicIntegerNotAcquired.get());
}
```



## 布隆过滤器



### 介绍

Redisson 的布隆过滤器（Bloom Filter）是基于 Redis 实现的分布式概率型数据结构，用于高效判断一个元素是否存在于集合中。它通过牺牲一定准确性（允许**假阳性**，但保证**无假阴性**）来换取空间和时间效率，非常适合分布式系统中处理大规模数据的存在性校验场景。


#### **一、布隆过滤器的核心原理**
布隆过滤器底层是一个**二进制位数组**（Bit Array），初始时所有位均为 `0`。当插入一个元素时，通过多个独立的哈希函数（如 MurmurHash3）将元素映射到位数组的多个位置，并将这些位置的位设为 `1`。  
查询元素时，只需检查所有哈希映射的位置是否均为 `1`：  
- 若任意一位为 `0`，则元素**一定不存在**（无假阴性）；  
- 若所有位均为 `1`，则元素**可能存在**（可能有假阳性）。  


#### **二、Redisson 布隆过滤器的特点**
1. **分布式支持**：基于 Redis 的共享存储，多个应用实例可共享同一个布隆过滤器，适合分布式系统。  
2. **可配置参数**：支持设置预期插入元素数量（`expectedInsertions`）和误判率（`falsePositiveProbability`），Redisson 会根据这两个参数自动计算所需的位数组大小（`m`）和哈希函数数量（`k`）。  
3. **内存高效**：位数组仅用 `0/1` 存储，空间复杂度远低于传统哈希表或集合。  
4. **持久化支持**：若 Redis 开启了 RDB 或 AOF 持久化，布隆过滤器的状态会被持久化，重启后数据不丢失（需注意 Redis 持久化的性能影响）。  


#### **三、关键参数与计算**
Redisson 布隆过滤器的核心参数由用户指定，内部会自动计算其他参数：  
- **`expectedInsertions`**：预期插入的元素总数（估算值，影响位数组大小）。  
- **`falsePositiveProbability`**：可接受的误判率（如 `0.01` 表示 1% 的误判概率）。  

内部计算逻辑：  
- 位数组大小 `m = - (n * ln(p)) / (ln(2))²`（`n` 为 `expectedInsertions`，`p` 为误判率）。  
- 哈希函数数量 `k = (m/n) * ln(2)`（取整）。  


#### **四、使用示例（Java）**
##### 1. 引入依赖
需先引入 Redisson 依赖（Maven）：  
```xml
<dependency>
    <groupId>org.redisson</groupId>
    <artifactId>redisson</artifactId>
    <version>3.23.0</version> <!-- 最新版本 -->
</dependency>
```

##### 2. 初始化 RedissonClient
```java
Config config = new Config();
config.useSingleServer().setAddress("redis://127.0.0.1:6379");
RedissonClient redisson = Redisson.create(config);
```

##### 3. 创建布隆过滤器
```java
// 预期插入 100 万元素，允许 1% 误判率
RBloomFilter<String> bloomFilter = redisson.getBloomFilter("myBloomFilter");
bloomFilter.tryInit(1_000_000L, 0.01); // 初始化（仅需执行一次）
```

##### 4. 添加与查询元素
```java
// 添加元素
bloomFilter.add("user:1001");
bloomFilter.add("order:2025");

// 查询元素（可能返回 true 即使元素不存在）
boolean exists1 = bloomFilter.contains("user:1001"); // true（存在）
boolean exists2 = bloomFilter.contains("product:999"); // 可能为 true（误判）或 false（不存在）
```

##### 5. 销毁布隆过滤器（可选）
```java
bloomFilter.delete(); // 从 Redis 删除底层数据
```


#### **五、适用场景**
- **缓存穿透防护**：防止大量不存在的 key 直接查询数据库（如判断商品 ID 是否存在，若布隆过滤器返回 `false` 则直接拒绝请求）。  
- **日志/数据去重**：统计 UV（独立访客）或记录已处理的日志 ID，避免重复处理。  
- **分布式限流**：快速判断某个 IP 或用户是否超过请求阈值（需结合其他限流策略）。  


#### **六、注意事项**
1. **误判率不可忽视**：布隆过滤器无法保证绝对准确，关键业务（如支付、权限校验）需配合其他精确校验机制。  
2. **容量规划要合理**：`expectedInsertions` 需尽量接近实际数据量，过小会导致高误判率，过大则浪费内存。  
3. **不支持删除操作**：布隆过滤器的位数组是叠加标记的，删除元素可能导致其他元素的位被误清零（Redisson 未提供删除接口）。  
4. **持久化权衡**：若 Redis 持久化（如 RDB）频繁，可能影响性能；若不启用持久化，重启后布隆过滤器需重新初始化。  


#### **总结**
Redisson 布隆过滤器是分布式系统中解决大规模数据存在性校验的高效工具，通过 Redis 实现分布式共享，适用于对空间和性能敏感但允许一定误判的场景。使用时需合理规划参数，并注意其误判特性，避免关键业务误判。



### 基本用法

>详细用法请参考本站 [示例](https://gitee.com/dexterleslie/demonstration/tree/main/demo-redis/demo-spring-boot-redisson)

```java
/**
 * 基本用法
 */
@Test
public void test() {
    String uuidStr = UUID.randomUUID().toString();
    RBloomFilter<Long> bloomFilter = redissonClient.getBloomFilter(uuidStr);
    bloomFilter.delete();
    // 存1万个元素，误判率为1%
    bloomFilter.tryInit(10000L, 0.01);

    int total = 1000;
    for (long i = 0; i < total; i++) {
        Assert.assertFalse(i + " 本应该不存在，但是却判断为存在", bloomFilter.contains(i));
    }

    for (long i = 0; i < total; i++) {
        boolean b = bloomFilter.add(i);
        Assert.assertTrue(i + " 添加失败，因为判断为元素已经存在", b);
    }

    for (long i = 0; i < total; i++) {
        Assert.assertTrue(i + " 本应该存在，但是却判断不存在", bloomFilter.contains(i));
    }

    bloomFilter.delete();
}
```



### 无假阴性，可能有假阳性

>详细用法请参考本站 [示例](https://gitee.com/dexterleslie/demonstration/tree/main/demo-redis/demo-spring-boot-redisson)
>
>结论：会存在假阳性，即不存在的元素报告已经存在。

```java
/**
 * 测试误判
 */
@Test
public void testFalsePositive() {
    String uuidStr = UUID.randomUUID().toString();
    RBloomFilter<Long> bloomFilter = redissonClient.getBloomFilter(uuidStr);
    bloomFilter.delete();
    bloomFilter.tryInit(10000L, 0.1);

    int total = 1000;
    for (long i = 0; i < total; i++) {
        // 演示无假阴性，判断不存在则实际一定不存在
        Assert.assertFalse(i + " 本应该不存在，但是却判断为存在", bloomFilter.contains(i));
    }

    for (long i = 0; i < total; i++) {
        try {
            // 演示可能有假阳性，判断存在则实际不一定存在
            boolean b = bloomFilter.add(i);
            Assert.assertTrue(i + " 添加失败，因为判断为元素已经存在", b);
            if (i == 492)
                // 本应该492是假阳性的
                Assert.fail();
        } catch (AssertionError error) {
            Assert.assertEquals("492 添加失败，因为判断为元素已经存在", error.getMessage());
        }
    }
}
```



### 插入元素总数超过预期元素总数 `5` 倍时的误判率

>详细用法请参考本站 [示例](https://gitee.com/dexterleslie/demonstration/tree/main/demo-redis/demo-spring-boot-redisson)
>
>结论：插入元素总数超过预期元素总数 `5` 倍时的误判率为 26.4134%。

```java
/**
 * 测试插入元素总数超过预期元素总数5倍时的误判率
 */
@Test
public void testExpectedInsertionsOverflow() {
    String uuidStr = UUID.randomUUID().toString();
    RBloomFilter<Long> bloomFilter = redissonClient.getBloomFilter(uuidStr);
    long expectedInsertions = 100000L;
    AtomicInteger falsePositiveCounter = new AtomicInteger();
    bloomFilter.tryInit(expectedInsertions, 0.001);
    int times = 5;

    for (long i = 0; i < expectedInsertions * times; i++) {
        boolean b = bloomFilter.add(i);
        if (!b)
            falsePositiveCounter.incrementAndGet();
    }

    log.info("插入元素总数超过预期元素总数5倍时的误判率为 {}%", ((double) falsePositiveCounter.get() / (double) (expectedInsertions * times)) * 100d);
}
```



### 统计误判率

>详细用法请参考本站 [示例](https://gitee.com/dexterleslie/demonstration/tree/main/demo-redis/demo-spring-boot-redisson)
>
>结论：下面测试代码输出：使用 `1000000` 个样本统计误判率为 `0.4757%`，比设定的 `falseProbability` 还小。

```java
/**
 * 统计误判率
 */
@Test
public void testStatisticalFalsePositiveRate() throws InterruptedException {
    String uuidStr = UUID.randomUUID().toString();
    int concurrentThreads = 32;
    ExecutorService threadPool = Executors.newCachedThreadPool();
    long total = 1000000L;
    AtomicInteger counter = new AtomicInteger();
    AtomicInteger falseCounter = new AtomicInteger();
    RBloomFilter<Long> bloomFilter = redissonClient.getBloomFilter(uuidStr);
    bloomFilter.delete();
    bloomFilter.tryInit(total, 0.01);
    for (int i = 0; i < concurrentThreads; i++) {
        threadPool.submit(() -> {
            while (counter.getAndIncrement() <= total) {
                long randomLong = RandomUtil.randomLong(0, Long.MAX_VALUE);
                boolean b = bloomFilter.add(randomLong);
                if (!b)
                    falseCounter.incrementAndGet();
            }
        });
    }
    threadPool.shutdown();
    while (!threadPool.awaitTermination(5, TimeUnit.MILLISECONDS)) ;

    log.info("使用 {} 个样本统计误判率为 {}%", total, ((double) falseCounter.get() / (double) total) * 100d);
}
```



### `add` 的性能

>详细用法请参考本站 [示例](https://gitee.com/dexterleslie/demonstration/tree/main/demo-redis/demo-spring-boot-redisson)
>
>结论：调用 `100000` 次 `add` 耗时 `15210` 毫秒。

```java
/**
 * 测试add性能
 */
@Test
public void testPerfAdd() throws InterruptedException {
    StopWatch stopWatch = new StopWatch();
    String key = UUID.randomUUID().toString();
    int concurrentThreads = 32;
    ExecutorService threadPool = Executors.newCachedThreadPool();
    int total = 100000;
    AtomicInteger counter = new AtomicInteger();
    RBloomFilter<Long> bloomFilter = redissonClient.getBloomFilter(key);
    bloomFilter.tryInit(total * 2, 0.001);
    stopWatch.start();
    for (int i = 0; i < concurrentThreads; i++) {
        AtomicInteger finalCounter1 = counter;
        int finalTotal1 = total;
        RBloomFilter<Long> finalBloomFilter1 = bloomFilter;
        threadPool.submit(() -> {
            while (finalCounter1.getAndIncrement() <= finalTotal1) {
                long randomLong = RandomUtil.randomLong(0, Long.MAX_VALUE);
                finalBloomFilter1.add(randomLong);
            }
        });
    }
    threadPool.shutdown();
    while (!threadPool.awaitTermination(5, TimeUnit.MILLISECONDS)) ;
    stopWatch.stop();
    log.info("调用 add {} 次耗时 {} 毫秒", total, stopWatch.getTotalTimeMillis());
}
```



### `contains` 的性能

>详细用法请参考本站 [示例](https://gitee.com/dexterleslie/demonstration/tree/main/demo-redis/demo-spring-boot-redisson)
>
>结论：调用 `100000` 次 `add` 耗时 `12662` 毫秒。

```java
/**
 * 测试contain性能
 */
@Test
public void testPerfContains() throws InterruptedException {
    StopWatch stopWatch = new StopWatch();
    String key = UUID.randomUUID().toString();
    int concurrentThreads = 32;
    ExecutorService threadPool = Executors.newCachedThreadPool();
    int total = 100000;
    AtomicInteger counter = new AtomicInteger();
    RBloomFilter<Long> bloomFilter = redissonClient.getBloomFilter(key);
    bloomFilter.tryInit(total * 2, 0.001);
    stopWatch.start();
    for (int i = 0; i < concurrentThreads; i++) {
        AtomicInteger finalCounter = counter;
        int finalTotal = total;
        RBloomFilter<Long> finalBloomFilter = bloomFilter;
        threadPool.submit(() -> {
            while (finalCounter.getAndIncrement() <= finalTotal) {
                long randomLong = RandomUtil.randomLong(0, Long.MAX_VALUE);
                finalBloomFilter.contains(randomLong);
            }
        });
    }
    threadPool.shutdown();
    while (!threadPool.awaitTermination(5, TimeUnit.MILLISECONDS)) ;
    stopWatch.stop();
    log.info("调用 contains {} 次耗时 {} 毫秒", total, stopWatch.getTotalTimeMillis());
}
```



### 多个 `key` 分片 `add` 性能和占用内存大小

>详细用法请参考本站 [示例](https://gitee.com/dexterleslie/demonstration/tree/main/demo-redis/demo-spring-boot-redisson)
>
>结论：多个 `key` 分片 `add` `100000` 次耗时 `16414` 毫秒。预期插入元素总数为 `128,000,000` 个 `redis` 占用内存大小为 `229.7MiB`。

```java
/**
 * 测试多个key分片add性能
 */
@Test
public void testPerfAddWithKeySharding() throws InterruptedException {
    StopWatch stopWatch = new StopWatch();
    String keyPrefix = UUID.randomUUID().toString();
    int totalKeyShards = 64;
    int concurrentThreads = 32;
    ExecutorService threadPool = Executors.newCachedThreadPool();
    int expectedInsertions = 2000000;
    int totalElement = 100000;
    AtomicInteger counter = new AtomicInteger();
    Map<Integer, RBloomFilter<Long>> keyShardToBloomFilterMap = new HashMap<>();
    for (int i = 0; i < totalKeyShards; i++) {
        String keyInternal = keyPrefix + ":" + i;
        RBloomFilter<Long> bloomFilter = redissonClient.getBloomFilter(keyInternal);
        bloomFilter.tryInit(expectedInsertions, 0.001);
        keyShardToBloomFilterMap.put(i, bloomFilter);
    }
    stopWatch.start();
    for (int i = 0; i < concurrentThreads; i++) {
        threadPool.submit(() -> {
            int count;
            while ((count = counter.getAndIncrement()) <= totalElement) {
                int keyShard = count % totalKeyShards;
                long randomLong = RandomUtil.randomLong(0, Long.MAX_VALUE);
                keyShardToBloomFilterMap.get(keyShard).add(randomLong);
            }
        });
    }
    threadPool.shutdown();
    while (!threadPool.awaitTermination(5, TimeUnit.MILLISECONDS)) ;
    stopWatch.stop();
    log.info("多个key分片调用 add {} 次耗时 {} 毫秒", totalElement, stopWatch.getTotalTimeMillis());
}
```



## `Map`

>`todo`：
>
>- 如何查看 `RMapCache` 和 `RLocalCachedMap` 本地缓存命中率？

### 介绍

在 Redisson 中，`RMap`、`RMapCache` 和 `RLocalCachedMap` 是三种不同的分布式数据结构，主要用于解决分布式场景下的键值对存储问题，但它们的设计目标和适用场景有显著差异。以下是详细解析：


#### **一、RMap：纯分布式 Map**
##### **定义**  
`RMap`（`org.redisson.api.RMap`）是 Redisson 提供的**纯分布式 Map 实现**，基于 Redis 的 `Hash` 数据结构构建。它将数据存储在 Redis 服务器中，所有操作（如 `put`、`get`、`remove`）均通过网络与 Redis 交互完成。


##### **核心特性**  
- **数据存储位置**：仅存储在 Redis 服务器中，应用本地无缓存；  
- **分布式一致性**：支持多节点并发操作，通过 Redis 的原子命令（如 `HSET`、`HGET`）保证数据一致性；  
- **功能丰富**：支持 `Map` 接口的所有方法（如 `keySet()`、`values()`、`entrySet()`），并扩展了分布式锁（`RLock`）、原子操作（如 `putIfAbsent`）等高级特性；  
- **无本地缓存**：每次读取都需访问 Redis，性能受网络延迟影响（通常为毫秒级）。  


##### **适用场景**  
- 分布式系统中需要**全局共享**的键值对数据（如配置信息、全局计数器）；  
- 对数据一致性要求高，需多节点实时同步的场景；  
- 无需本地缓存的简单键值存储需求。  


#### **二、RMapCache：分布式 Map + 本地缓存**  
##### **定义**  

`RMapCache`（`org.redisson.api.RMapCache`）是 Redisson 提供的**分布式缓存增强版 Map**，在 `RMap` 的基础上增加了**本地缓存（内存缓存）**，用于加速高频数据的读取。本地缓存默认使用 `ConcurrentHashMap` 实现，数据存储在应用内存中。


##### **核心特性**  
- **双层存储**：  
  - **Redis 后端存储**：作为持久化存储，保证数据全局一致；  
  - **本地缓存（内存）**：存储高频访问的热点数据，减少对 Redis 的网络请求，提升读取速度（接近内存级）；  
- **自动加载**：本地缓存未命中时，自动从 Redis 加载数据并缓存到本地（支持同步/异步加载）；  
- **容量控制**：通过 `setMaxSize(int maxSize)` 限制本地缓存的最大容量，结合淘汰策略（如 LRU、LFU）清理旧数据；  
- **缓存更新**：支持写穿透（本地更新时同步更新 Redis）或异步更新（后台批量同步）；  
- **数据过期**：支持设置单个键的 TTL（存活时间），过期后自动从本地缓存和 Redis 清理。  


##### **适用场景**  
- 分布式系统中**读多写少**的高频数据访问（如用户会话信息、商品详情）；  
- 需要**低延迟读取**（接近内存级），同时要求数据全局一致的场景；  
- 网络延迟较高（如跨机房），需通过本地缓存减少 Redis 访问的场景。  


#### **三、RLocalCachedMap：本地缓存优先的分布式 Map**  
##### **定义**  
`RLocalCachedMap`（`org.redisson.api.RLocalCachedMap`）是 Redisson 提供的**本地缓存优先的分布式 Map**，与 `RMapCache` 类似，但设计更侧重**本地缓存的一致性同步**，适用于对本地缓存一致性要求更高的场景。


##### **核心特性**  
- **本地缓存为核心**：数据优先存储在应用本地内存中，仅在本地缓存未命中或需要同步时访问 Redis；  
- **双向同步**：  
  - **本地 → Redis**：通过异步任务或显式调用 `putToRedis` 同步到 Redis；  
  - **Redis → 本地**：通过 Redis 的 `Pub/Sub`（发布订阅）机制，监听 Redis 数据变更事件，实时更新本地缓存；  
- **强一致性优化**：相比 `RMapCache`，`RLocalCachedMap` 更注重本地缓存与 Redis 的一致性（如通过订阅 Redis 的 `KEYSPACE` 事件触发本地缓存更新）；  
- **容量控制**：同样支持 `maxSize` 和淘汰策略（如 LRU），但本地缓存的更新更依赖 Redis 的事件通知。  


##### **适用场景**  
- 分布式系统中**本地缓存一致性要求高**的场景（如实时计数的本地缓存）；  
- 需要**快速回写本地缓存**（避免频繁回源 Redis）的高频写场景；  
- 网络不稳定，需通过事件通知机制保证本地缓存与 Redis 最终一致的场景。  


#### **四、三者对比总结**
| 特性             | RMap                         | RMapCache                        | RLocalCachedMap                  |
| ---------------- | ---------------------------- | -------------------------------- | -------------------------------- |
| **数据存储位置** | 仅 Redis                     | Redis（后端） + 本地内存（缓存） | 本地内存（优先） + Redis（同步） |
| **核心目标**     | 纯分布式存储                 | 分布式存储 + 本地缓存加速        | 本地缓存优先 + 分布式同步        |
| **读取延迟**     | 毫秒级（网络往返）           | 微秒级（本地内存为主）           | 微秒级（本地内存为主）           |
| **一致性**       | 强一致（Redis 保证）         | 最终一致（本地缓存可能短暂脏）   | 强一致（通过 Pub/Sub 实时同步）  |
| **适用场景**     | 全局共享、一致性要求高的数据 | 读多写少、需低延迟读取的热点数据 | 本地缓存优先、一致性要求高的场景 |


#### **五、选择建议**  
- **选 RMap**：需要全局共享数据，且对一致性要求极高（如实时订单状态）；  
- **选 RMapCache**：读多写少，需低延迟读取，且本地缓存一致性要求不高（如商品详情页缓存）；  
- **选 RLocalCachedMap**：本地缓存优先，需强一致同步（如实时用户会话缓存，需快速感知 Redis 变更）。  


#### **总结**  
`RMap` 是纯分布式存储的基础实现；`RMapCache` 在此基础上增加了本地缓存以加速读取；`RLocalCachedMap` 则进一步优化本地缓存与 Redis 的同步机制，适用于对本地缓存一致性要求更高的场景。根据业务的读写比例、一致性要求和性能目标，选择合适的实现即可。



### 基本用法

>详细用法请参考本站 [示例](https://gitee.com/dexterleslie/demonstration/tree/main/demo-redis/demo-spring-boot-redisson)

```java
/**
 * 测试基本使用
 */
@Test
public void testBasicUsage() {
    // region 测试 RMap

    String uuidStr = UUID.randomUUID().toString();
    RMap<String, Long> map = redisson.getMap(uuidStr);
    Assertions.assertEquals(0, map.size());
    map.put("k1", RandomUtil.randomLong(0, Long.MAX_VALUE));
    map.put("k2", RandomUtil.randomLong(0, Long.MAX_VALUE));
    Assertions.assertEquals(2, map.size());

    // endregion

    // region 测试 RMapCache

    uuidStr = UUID.randomUUID().toString();
    RMap<String, Long> mapCache = redisson.getMapCache(uuidStr);
    Assertions.assertEquals(0, mapCache.size());
    mapCache.put("k1", RandomUtil.randomLong(0, Long.MAX_VALUE));
    mapCache.put("k2", RandomUtil.randomLong(0, Long.MAX_VALUE));
    Assertions.assertEquals(2, mapCache.size());

    // endregion

    // region 测试 RLocalCachedMap

    uuidStr = UUID.randomUUID().toString();
    RLocalCachedMap<String, Long> rLocalCachedMap = redisson.getLocalCachedMap(uuidStr, LocalCachedMapOptions.defaults());
    Assertions.assertEquals(0, rLocalCachedMap.size());
    rLocalCachedMap.put("k1", RandomUtil.randomLong(0, Long.MAX_VALUE));
    rLocalCachedMap.put("k2", RandomUtil.randomLong(0, Long.MAX_VALUE));
    Assertions.assertEquals(2, rLocalCachedMap.size());

    // endregion
}
```



### 性能测试

>详细用法请参考本站 [示例](https://gitee.com/dexterleslie/demonstration/tree/main/demo-redis/demo-spring-boot-redisson)

相关实例的内存和 `CPU` 都充足。

部署并运行应用

```sh
# 部署配置
ansible-playbook playbook-deployer-config.yml --inventory inventory.ini

# 编译并推送镜像
./build.sh && ./push.sh

# 运行应用
ansible-playbook playbook-service-start.yml --inventory inventory.ini
```

测试 `RMap` 性能

```sh
wrk -t8 -c2048 -d30s --latency --timeout 30 http://192.168.1.185/api/v1/map/testRMapGet
Running 30s test @ http://192.168.1.185/api/v1/map/testRMapGet
  8 threads and 2048 connections
  Thread Stats   Avg      Stdev     Max   +/- Stdev
    Latency    29.61ms   57.06ms 813.22ms   92.47%
    Req/Sec    16.73k     2.71k   25.38k    81.01%
  Latency Distribution
     50%   13.46ms
     75%   21.35ms
     90%   47.55ms
     99%  313.87ms
  3957358 requests in 30.09s, 1.00GB read
Requests/sec: 131520.21
Transfer/sec:     34.12MB
```

测试 `RMapCache` 性能

```sh
wrk -t8 -c2048 -d30s --latency --timeout 30 http://192.168.1.185/api/v1/map/testRMapCacheGet
Running 30s test @ http://192.168.1.185/api/v1/map/testRMapCacheGet
  8 threads and 2048 connections
  Thread Stats   Avg      Stdev     Max   +/- Stdev
    Latency    42.43ms   77.29ms 830.73ms   89.06%
    Req/Sec    14.95k     4.09k   25.35k    81.73%
  Latency Distribution
     50%   14.32ms
     75%   25.24ms
     90%  135.98ms
     99%  376.26ms
  3441244 requests in 30.10s, 0.87GB read
Requests/sec: 114334.36
Transfer/sec:     29.66MB
```

测试 `RLocalCachedMap` 性能

```sh
wrk -t8 -c2048 -d30s --latency --timeout 30 http://192.168.1.185/api/v1/map/testRLocalCachedMapGet
Running 30s test @ http://192.168.1.185/api/v1/map/testRLocalCachedMapGet
  8 threads and 2048 connections
  Thread Stats   Avg      Stdev     Max   +/- Stdev
    Latency    42.89ms   84.76ms   1.22s    88.56%
    Req/Sec    20.16k     7.82k   68.50k    81.52%
  Latency Distribution
     50%   10.11ms
     75%   17.16ms
     90%  155.69ms
     99%  388.38ms
  4417752 requests in 30.10s, 1.12GB read
Requests/sec: 146781.52
Transfer/sec:     38.08MB
```

测试结论：`RMap` 和 `RMapCache` 性能差不多，`RLocalCacheMap` 性能高于 `RMap` 和 `RMapCache`。



## `Set`

>提醒：`RSet` 支持 `random()` 和 `random(int count)` 函数，支持随机获取指定数量的元素。

### 基本用法

>详细用法请参考本站 [示例](https://gitee.com/dexterleslie/demonstration/tree/main/demo-redis/demo-spring-boot-redisson)

```java
@SpringBootTest
@Slf4j
// 设置 active profile 为 test
@ActiveProfiles("test")
public class SetTests {

    @Resource
    RedissonClient redissonClient;

    /**
     * 测试基本用法
     */
    @Test
    public void testBasicUsage() {
        // region 测试 RSet

        String key = UUID.randomUUID().toString();
        RSet<Long> rSet = redissonClient.getSet(key);
        Long randomLong1 = RandomUtil.randomLong(0, Long.MAX_VALUE);
        rSet.add(randomLong1);
        Long randomLong2 = RandomUtil.randomLong(0, Long.MAX_VALUE);
        rSet.add(randomLong2);
        Assertions.assertEquals(2, rSet.size());
        Long randomLong = rSet.random();
        Assertions.assertTrue(Objects.equals(randomLong, randomLong1) || Objects.equals(randomLong, randomLong2));
        Set<Long> randomLongs = rSet.random(1000);
        Assertions.assertEquals(2, randomLongs.size());
        Assertions.assertTrue(randomLongs.toArray(new Long[]{})[0].equals(randomLong1) || randomLongs.toArray(new Long[]{})[0].equals(randomLong2));
        Assertions.assertTrue(randomLongs.toArray(new Long[]{})[1].equals(randomLong1) || randomLongs.toArray(new Long[]{})[1].equals(randomLong2));

        // endregion

        // region 测试 RSetCache

        // 注意：下面代码不能通过测试，因为 RSetCache 的 random 函数报错。
        /*key = UUID.randomUUID().toString();
        RSetCache<Long> rSetCache = redissonClient.getSetCache(key);
        randomLong1 = RandomUtil.randomLong(0, Long.MAX_VALUE);
        rSetCache.add(randomLong1);
        randomLong2 = RandomUtil.randomLong(0, Long.MAX_VALUE);
        rSetCache.add(randomLong2);
        Assertions.assertEquals(2, rSetCache.size());
        randomLong = rSetCache.random();
        Assertions.assertTrue(Objects.equals(randomLong, randomLong1) || Objects.equals(randomLong, randomLong2));
        randomLongs = rSetCache.random(1000);
        Assertions.assertEquals(2, randomLongs.size());
        Assertions.assertTrue(randomLongs.toArray(new Long[]{})[0].equals(randomLong1) || randomLongs.toArray(new Long[]{})[0].equals(randomLong2));
        Assertions.assertTrue(randomLongs.toArray(new Long[]{})[1].equals(randomLong1) || randomLongs.toArray(new Long[]{})[1].equals(randomLong2));*/

        // endregion
    }
}
```



### 性能测试

>详细用法请参考本站 [示例](https://gitee.com/dexterleslie/demonstration/tree/main/demo-redis/demo-spring-boot-redisson)

相关实例的内存和 `CPU` 都充足。

部署并运行应用

```sh
# 部署配置
ansible-playbook playbook-deployer-config.yml --inventory inventory.ini

# 编译并推送镜像
./build.sh && ./push.sh

# 运行应用
ansible-playbook playbook-service-start.yml --inventory inventory.ini
```

测试 `RSet random()` 性能

```sh
wrk -t8 -c2048 -d30s --latency --timeout 30 http://192.168.1.185/api/v1/set/testRSetRandom1
Running 30s test @ http://192.168.1.185/api/v1/set/testRSetRandom1
  8 threads and 2048 connections
  Thread Stats   Avg      Stdev     Max   +/- Stdev
    Latency    28.23ms   46.18ms 545.86ms   93.40%
    Req/Sec    14.15k     3.81k   30.99k    61.62%
  Latency Distribution
     50%   14.95ms
     75%   28.11ms
     90%   42.94ms
     99%  262.28ms
  3379464 requests in 30.09s, 773.15MB read
Requests/sec: 112296.80
Transfer/sec:     25.69MB
```

测试 `RSet random(20)` 性能

```sh
wrk -t8 -c2048 -d30s --latency --timeout 30 http://192.168.1.185/api/v1/set/testRSetRandom2
Running 30s test @ http://192.168.1.185/api/v1/set/testRSetRandom2
  8 threads and 2048 connections
  Thread Stats   Avg      Stdev     Max   +/- Stdev
    Latency    34.28ms   38.02ms 480.73ms   96.25%
    Req/Sec     8.84k     1.16k   11.46k    61.28%
  Latency Distribution
     50%   25.65ms
     75%   35.27ms
     90%   48.49ms
     99%  247.64ms
  2109215 requests in 30.10s, 749.86MB read
Requests/sec:  70077.99
Transfer/sec:     24.91MB
```

测试结论：`RSet random()` 性能高于 `RSet random(20)`。
