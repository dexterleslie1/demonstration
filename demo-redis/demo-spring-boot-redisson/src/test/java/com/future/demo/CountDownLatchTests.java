package com.future.demo;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.redisson.api.RCountDownLatch;
import org.redisson.api.RedissonClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@RunWith(SpringRunner.class)
@SpringBootTest
public class CountDownLatchTests {
    @Resource
    RedissonClient redisson = null;

    @Test
    public void test() throws InterruptedException {
        String key = UUID.randomUUID().toString();
        RCountDownLatch countDownLatch = this.redisson.getCountDownLatch(key);
        countDownLatch.trySetCount(5);

        ExecutorService service = Executors.newCachedThreadPool();
        for (int i = 0; i < 6; i++) {
            service.submit(countDownLatch::countDown);
        }
        service.shutdown();

        // 等待countdown到0
        boolean b = countDownLatch.await(1, TimeUnit.SECONDS);
        Assert.assertTrue(b);
    }
}
