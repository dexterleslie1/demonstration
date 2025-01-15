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
//import java.util.Arrays;
//import java.util.List;
//import java.util.UUID;
//import java.util.concurrent.ExecutorService;
//import java.util.concurrent.Executors;
//import java.util.concurrent.TimeUnit;
//
//public class RedissonFairLockTests {
//
//    private RedissonClient redisson = null;
//
//    @Test
//    public void test() throws InterruptedException {
//        String key = UUID.randomUUID().toString();
//
//        // 按照加锁的请求顺序公平地（先到先得）分配上锁机会
//        RLock rLock = this.redisson.getFairLock(key);
//        List<String> seqList = new ArrayList<>();
//        for(int i=0; i<5; i++) {
//            Thread thread = new Thread(()->{
//                seqList.add(Thread.currentThread().getName());
//                boolean acquired = false;
//                try {
//                    acquired = rLock.tryLock(100000, 10000, TimeUnit.MILLISECONDS);
//                    TimeUnit.MILLISECONDS.sleep(100);
//                } catch (Exception ex) {
//                    ex.printStackTrace();
//                } finally {
//                    if(acquired) {
//                        try {
//                            rLock.unlock();
//                        } catch (Exception ignored) {
//                            //
//                        }
//                    }
//                }
//            });
//            thread.setName("thread-" + (i+1));
//            thread.start();
//
//            TimeUnit.MILLISECONDS.sleep(10);
//        }
//
//        TimeUnit.SECONDS.sleep(2);
//
//        Assert.assertEquals(5, seqList.size());
//        Assert.assertEquals(seqList.get(0), "thread-1");
//        Assert.assertEquals(seqList.get(1), "thread-2");
//        Assert.assertEquals(seqList.get(2), "thread-3");
//        Assert.assertEquals(seqList.get(3), "thread-4");
//        Assert.assertEquals(seqList.get(4), "thread-5");
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
