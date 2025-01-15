//package com.xy.demo.redisson.lock;
//
//import org.junit.After;
//import org.junit.Assert;
//import org.junit.Before;
//import org.junit.Test;
//import org.redisson.Redisson;
//import org.redisson.api.RLock;
//import org.redisson.api.RedissonClient;
//import org.redisson.config.Config;
//
//import java.util.ArrayList;
//import java.util.List;
//import java.util.UUID;
//import java.util.concurrent.ExecutorService;
//import java.util.concurrent.Executors;
//import java.util.concurrent.TimeUnit;
//
//public class RedissonMultiLockTests {
//
//    private RedissonClient redisson = null;
//
//    @Test
//    public void testAcquireSuccessfully() throws InterruptedException {
//        String key1 = UUID.randomUUID().toString();
//        String key2 = UUID.randomUUID().toString();
//        String key3 = UUID.randomUUID().toString();
//
//        RLock rLock1 = this.redisson.getLock(key1);
//        RLock rLock2 = this.redisson.getLock(key2);
//        RLock rLock3 = this.redisson.getLock(key3);
//
//        RLock rLock = this.redisson.getMultiLock(rLock1, rLock2, rLock3);
//        boolean acquired;
//        try {
//            rLock.tryLock(100, TimeUnit.SECONDS);
//            acquired = true;
//        } finally {
//            try {
//                rLock.unlock();
//            } catch (Exception ignored) {
//
//            }
//        }
//        Assert.assertTrue(acquired);
//    }
//
//    @Test
//    public void testAcquireFailed() throws InterruptedException {
//        String key1 = UUID.randomUUID().toString();
//        String key2 = UUID.randomUUID().toString();
//        String key3 = UUID.randomUUID().toString();
//
//        ExecutorService executorService = Executors.newCachedThreadPool();
//        executorService.submit(()-> {
//            RLock rLock1 = this.redisson.getLock(key1);
//            RLock rLock2 = this.redisson.getLock(key2);
//            RLock rLock3 = this.redisson.getLock(key3);
//            RLock rLock = this.redisson.getMultiLock(rLock1, rLock2, rLock3);
//            // 模拟在另外一条线程中上锁导致其他线程不能成功上锁
//            rLock.tryLock();
//
//            // MultiLock不能使用lock方法上锁，否则下面tryLock会返回true
//            // rLock.lock(100, TimeUnit.SECONDS);
//        });
//        executorService.shutdown();
//        while(!executorService.awaitTermination(10, TimeUnit.MILLISECONDS));
//
//        RLock rLock1 = this.redisson.getLock(key1);
//        RLock rLock2 = this.redisson.getLock(key2);
//        RLock rLock3 = this.redisson.getLock(key3);
//        RLock rLock = this.redisson.getMultiLock(rLock1, rLock2, rLock3);
//        boolean acquired;
//        try {
//            acquired = rLock.tryLock(5, 10000, TimeUnit.SECONDS);
//        } finally {
//            try {
//                rLock.unlock();
//            } catch (Exception ignored) {
//
//            }
//        }
//        Assert.assertFalse(acquired);
//    }
//
//    @Before
//    public void setup(){
//        String host = MyConfig.Host;
//        int port = MyConfig.Port;
//        String password = MyConfig.Password;
//
//        Config config = new Config();
//        config.useSingleServer().setAddress("redis://" + host + ":" + port).setPassword(password);
//        redisson = Redisson.create(config);
//    }
//
//    @After
//    public void teardown(){
//        if(redisson != null){
//            redisson.shutdown();
//        }
//    }
//}
