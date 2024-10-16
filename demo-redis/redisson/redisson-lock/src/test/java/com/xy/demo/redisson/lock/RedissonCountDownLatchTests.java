package com.xy.demo.redisson.lock;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.redisson.Redisson;
import org.redisson.api.RCountDownLatch;
import org.redisson.api.RLock;
import org.redisson.api.RReadWriteLock;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

public class RedissonCountDownLatchTests {
    private final static java.util.Random Random = new Random();

    private RedissonClient redisson = null;

    @Test
    public void test() throws InterruptedException {
        String key = UUID.randomUUID().toString();
        RCountDownLatch countDownLatch = this.redisson.getCountDownLatch(key);
        countDownLatch.trySetCount(5);

        ExecutorService service = Executors.newCachedThreadPool();
        for(int i=0; i<6; i++) {
            service.submit(countDownLatch::countDown);
        }
        service.shutdown();

        // 等待countdown到0
        boolean b = countDownLatch.await(1, TimeUnit.SECONDS);
        Assert.assertTrue(b);
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
