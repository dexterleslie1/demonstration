package com.xy.demo.redisson.lock;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.redisson.Redisson;
import org.redisson.api.RSemaphore;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;

import java.util.Date;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class RedissonSemaphoreTests {
    private RedissonClient redisson = null;

    @Before
    public void setup(){
        String host = MyConfig.Host;
        int port = MyConfig.Port;
        String password = MyConfig.Password;

        Config config = new Config();
        config.useSingleServer().setAddress("redis://" + host + ":" + port).setPassword(password);
        redisson = Redisson.create(config);
    }

    @After
    public void teardown(){
        if(redisson != null){
            redisson.shutdown();
        }
    }

    /**
     * redis redisson 信号量示例（RSemaphore）
     * https://blog.csdn.net/weixin_43931625/article/details/103232670
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
        for(int i=0; i<10; i++) {
            executorService.submit(() -> {
                boolean acquired = false;
                try {
                    acquired = semaphore.tryAcquire(10, TimeUnit.MILLISECONDS);
                    if(acquired) {
                        successAcquire.incrementAndGet();
                    } else {
                        failAcquire.incrementAndGet();
                    }

                    TimeUnit.SECONDS.sleep(1);
                } catch (Exception ex) {
                    ex.printStackTrace();
                } finally {
                    if(acquired) {
                        semaphore.release();
                    }
                }
            });
        }

        executorService.shutdown();
        while(!executorService.awaitTermination(1000, TimeUnit.MILLISECONDS));

        Assert.assertEquals(5, successAcquire.get());
        Assert.assertEquals(5, failAcquire.get());
    }
}
