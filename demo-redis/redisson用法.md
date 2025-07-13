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
