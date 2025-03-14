package com.future.demo;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.redisson.api.RSemaphore;
import org.redisson.api.RedissonClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

@RunWith(SpringRunner.class)
@SpringBootTest
public class SemaphoreTests {
    @Resource
    RedissonClient redisson = null;

    /**
     * redis redisson 信号量示例（RSemaphore）
     * https://blog.csdn.net/weixin_43931625/article/details/103232670
     *
     * @throws InterruptedException
     */
    @Test
    public void test() throws InterruptedException {
        String key = "demo-redis-lock-semaphore";
        final RSemaphore semaphore = redisson.getSemaphore(key);
        semaphore.delete();
        boolean b = semaphore.trySetPermits(5);
        Assert.assertTrue(b);

        AtomicInteger successAcquire = new AtomicInteger();
        AtomicInteger failAcquire = new AtomicInteger();
        ExecutorService executorService = Executors.newCachedThreadPool();
        for (int i = 0; i < 10; i++) {
            executorService.submit(() -> {
                boolean acquired = false;
                try {
                    acquired = semaphore.tryAcquire(10, TimeUnit.MILLISECONDS);
                    if (acquired) {
                        successAcquire.incrementAndGet();
                    } else {
                        failAcquire.incrementAndGet();
                    }

                    TimeUnit.SECONDS.sleep(1);
                } catch (Exception ex) {
                    ex.printStackTrace();
                } finally {
                    if (acquired) {
                        semaphore.release();
                    }
                }
            });
        }

        executorService.shutdown();
        while (!executorService.awaitTermination(1000, TimeUnit.MILLISECONDS)) ;

        Assert.assertEquals(5, successAcquire.get());
        Assert.assertEquals(5, failAcquire.get());
    }
}
