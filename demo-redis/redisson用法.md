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

