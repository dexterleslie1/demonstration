package com.xy.demo.redisson.lock;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.redisson.Redisson;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class RedissonFairLockTests {

    private RedissonClient redisson = null;

    @Test
    public void test() throws InterruptedException {
        String key = UUID.randomUUID().toString();

        ExecutorService executorService = Executors.newCachedThreadPool();
        // 按照加锁的请求顺序公平地（先到先得）分配上锁机会
        RLock rLock = this.redisson.getFairLock(key);
        List<String> seqList = new ArrayList<>();
        for(int i=0; i<5; i++) {
            executorService.submit(()->{
                boolean acquired = false;
                try {
                    acquired = rLock.tryLock(100000, 10000, TimeUnit.MILLISECONDS);
                    seqList.add(Thread.currentThread().getName());
                    TimeUnit.MILLISECONDS.sleep(100);
                } catch (Exception ex) {
                    ex.printStackTrace();
                } finally {
                    if(acquired) {
                        try {
                            rLock.unlock();
                        } catch (Exception ignored) {
                            //
                        }
                    }
                }});

            TimeUnit.MILLISECONDS.sleep(10);
        }

        executorService.shutdown();
        while(!executorService.awaitTermination(1, TimeUnit.SECONDS));

        Assert.assertArrayEquals(new String[] {"pool-2-thread-1", "pool-2-thread-2", "pool-2-thread-3", "pool-2-thread-4", "pool-2-thread-5"}, seqList.toArray());
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
