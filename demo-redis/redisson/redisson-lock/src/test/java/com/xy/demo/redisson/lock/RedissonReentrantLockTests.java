package com.xy.demo.redisson.lock;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.redisson.Redisson;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;

import java.awt.image.PackedColorModel;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

// https://blog.csdn.net/m0_62946761/article/details/126688793
public class RedissonReentrantLockTests {

    private RedissonClient redisson = null;

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
}
