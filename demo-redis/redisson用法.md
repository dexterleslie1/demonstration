# `redisson`用法



## `SpringBoot`应用集成`redisson`

详细用法请参考示例`https://gitee.com/dexterleslie/demonstration/tree/master/demo-redis/spring-boot-redisson-integration`

项目`maven`的`pom.xml`配置

```xml
<dependency>
    <groupId>org.redisson</groupId>
    <artifactId>redisson</artifactId>
    <version>3.19.1</version>
</dependency>
```

**Redis Standalone 模式配置**

**Redis Replication 模式配置**

**Redis Sentinel 模式配置**

**Redis Cluster 模式配置**

`spring-boot`项目的`application.properties`配置

```properties
spring.redis.host=localhost
spring.redis.port=6379
spring.redis.password=123456
```

创建`RedissonClient`

```java
@Configuration
public class ConfigRedis {
    @Value("${spring.redis.host}")
    private String redisHost = null;
    @Value("${spring.redis.port}")
    private int redisPort = 0;
    @Value("${spring.redis.password}")
    private String redisPassword = null;

    @Bean
    RedissonClient redissonClient(){
        Config config = new Config();
        SingleServerConfig singleServerConfig = config
                .useSingleServer()
                .setAddress("redis://"+redisHost+":"+redisPort);
        if(!StringUtils.isEmpty(redisPassword)) {
            singleServerConfig.setPassword(redisPassword);
        }
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



## 锁的标准用法

详细代码请参考 [链接](https://gitee.com/dexterleslie/demonstration/blob/master/demo-redis/redisson/redisson-lock/src/test/java/com/xy/demo/redisson/lock/RedissonLockTests.java#L38)

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

