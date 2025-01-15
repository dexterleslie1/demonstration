package com.future.demo;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@RunWith(SpringRunner.class)
@SpringBootTest
// https://blog.csdn.net/m0_62946761/article/details/126688793
public class ReentrantLockTests {

    @Resource
    RedissonClient redisson = null;

    // 同一个线程能够多次上锁
    @Test
    public void test() throws InterruptedException {
        String key = UUID.randomUUID().toString();

        RLock rLock = this.redisson.getLock(key);
        try {
            boolean acquired = rLock.tryLock(10, 10000, TimeUnit.MILLISECONDS);
            Assert.assertTrue(acquired);
            acquired = rLock.tryLock(10, 10000, TimeUnit.MILLISECONDS);
            Assert.assertTrue(acquired);
        } finally {
            // 锁计数器等于2
            Assert.assertEquals(2, rLock.getHoldCount());
            rLock.unlock();
            // 锁计数器等于1
            Assert.assertEquals(1, rLock.getHoldCount());
            rLock.unlock();
            // 锁计数器等于0
            Assert.assertEquals(0, rLock.getHoldCount());
        }
    }
}
