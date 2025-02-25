# Curator Framework



## 和 Zookeeper 兼容性

org.apache.curator:curator-recipes 2.13.0 以下版本和 zookeeper:3.4.9 兼容

org.apache.curator:curator-recipes 3.0.0 到 5.7.1 版本和 zookeeper:3.8.4 兼容



## SpringBoot 集成

pom 配置引用 curator framework 依赖：

```xml
<dependency>
    <groupId>org.apache.curator</groupId>
    <artifactId>curator-recipes</artifactId>
    <version>2.4.2</version>
</dependency>
```

application.properties 配置如下：

```properties
zookeeper.url=localhost:2181
```

ConfigCuratorFramework Java 配置如下：

```java
@Configuration
public class ConfigCuratorFramework {
    @Value("${zookeeper.url}")
    String url;

    @Bean(destroyMethod = "close")
    CuratorFramework curatorFramework() {
        RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000, 3);
        CuratorFramework curatorFramework =
                CuratorFrameworkFactory.newClient(
                        url,
                        5000,
                        3000,
                        retryPolicy);
        curatorFramework.start();
        return curatorFramework;
    }
}
```

引用 Curator Framework

```java
@Autowired
CuratorFramework curatorFramework;
```



## Barrier 栅栏

>`https://www.cnblogs.com/LiZhiW/p/4937547.html`
>
>栅栏分为手动模式和自动模式，手动模式（使用 DistributedBarrier 实现）需要主控角色设置栅栏（调用 setBarrier 方法）、移除栅栏（调用 removeBarrier 方法），被控角色等待（调用 waitOnBarrier 方法）栅栏移除才能够继续执行。自动模式（使用 DistributedDoubleBarrier 实现）在创建 DistributedDoubleBarrier 时需要定义 memberQty，各个线程调用 enter 方法等待直到所有 member 都到达栅栏点后才能够继续执行，各个线程调用 leave 方法等待直到所有 member 都到达栅栏点后才能够退出。

注意：DistributedDoubleBarrier 在结束时必须要调用 leave 方法，否则同一个 path 下的 DistributedDoubleBarrier 在后续调用栅栏效果会不生效

```java
// region 测试 barrier
// https://www.cnblogs.com/LiZhiW/p/4937547.html

// 手动模式
{
    ExecutorService threadPool = Executors.newCachedThreadPool();

    String uuid = "/" + UUID.randomUUID();

    DistributedBarrier controllerBarrier = new DistributedBarrier(curatorFramework, uuid);
    // 主控设置栅栏
    controllerBarrier.setBarrier();

    for (int i = 0; i < 5; i++) {
        threadPool.submit(() -> {
            DistributedBarrier distributedBarrier = new DistributedBarrier(curatorFramework, uuid);
            try {
                log.info("线程 {} 等待 ...", Thread.currentThread().getName());
                // 等到移除栅栏才继续执行
                distributedBarrier.waitOnBarrier();
            } catch (Exception e) {
                e.printStackTrace();
            }

            log.info("线程 {} 结束等待", Thread.currentThread().getName());
        });
    }

    TimeUnit.SECONDS.sleep(RandomUtils.nextInt(1, 5));

    // 主控移除栅栏
    controllerBarrier.removeBarrier();

    threadPool.shutdown();
    while (!threadPool.awaitTermination(10, TimeUnit.MILLISECONDS)) ;
}

// 自动模式
{
    ExecutorService threadPool = Executors.newCachedThreadPool();

    String uuid = "/" + UUID.randomUUID();

    for (int i = 0; i < 5; i++) {
        threadPool.submit(() -> {
            try {
                TimeUnit.SECONDS.sleep(RandomUtils.nextInt(0, 6));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            DistributedDoubleBarrier distributedDoubleBarrier = new DistributedDoubleBarrier(curatorFramework, uuid, 5);
            try {
                // 等到 memberQty=5 才继续执行
                distributedDoubleBarrier.enter();

                log.info("线程 {} 继续执行", Thread.currentThread().getName());

                try {
                    TimeUnit.SECONDS.sleep(RandomUtils.nextInt(0, 6));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    // 等到 memberQty=5 才继续执行
                    distributedDoubleBarrier.leave();
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    log.info("线程 {} 结束执行", Thread.currentThread().getName());
                }
            }
        });
    }

    threadPool.shutdown();
    while (!threadPool.awaitTermination(10, TimeUnit.MILLISECONDS)) ;
}

// endregion
```

