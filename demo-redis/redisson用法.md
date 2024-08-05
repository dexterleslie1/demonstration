# `redisson`用法

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

