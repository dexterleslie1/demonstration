package com.xy.demo.redisson.lock;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.redisson.Redisson;
import org.redisson.api.RLock;
import org.redisson.api.RReadWriteLock;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 *
 */
public class RedissonReadWriteLockTests {
    private final static Logger logger = LoggerFactory.getLogger(RedissonReadWriteLockTests.class);
    private final static java.util.Random Random = new Random();

    private RedissonClient redisson = null;

    // 测试支持并发读
    @Test
    public void testConcurrentRead() throws InterruptedException {
        final String lockname = "test-readwrite-lock";

        AtomicInteger counter = new AtomicInteger();
        List<Integer> concurrentCountSampleList = new ArrayList<>();
        Random random = new Random();
        ExecutorService executorService = Executors.newCachedThreadPool();
        for(int i=0; i<50; i++) {
            executorService.submit(() -> {
                RReadWriteLock readWriteLock = redisson.getReadWriteLock(lockname);
                RLock rLock = readWriteLock.readLock();
                try {
                    rLock.lock();

                    int count = counter.incrementAndGet();
                    if(count > 1) {
                        concurrentCountSampleList.add(count);
                    }

                    int randomInt = random.nextInt(1000);
                    if(randomInt > 0) {
                        try {
                            TimeUnit.MILLISECONDS.sleep(randomInt);
                        } catch (InterruptedException e) {
                            //
                        }
                    }
                } finally {
                    if(rLock.isHeldByCurrentThread()) {
                        counter.decrementAndGet();
                        rLock.unlock();
                    }
                }
            });
        }
        executorService.shutdown();
        while(!executorService.awaitTermination(1, TimeUnit.SECONDS));

        Assert.assertTrue(concurrentCountSampleList.size() > 0);
    }

    // 测试不支持并发写
    @Test
    public void testNotSupportConcurrentWrite() throws InterruptedException {
        final String lockname = "test-readwrite-lock";

        AtomicInteger counter = new AtomicInteger();
        List<Integer> concurrentCountSampleList = new ArrayList<>();
        Random random = new Random();
        ExecutorService executorService = Executors.newCachedThreadPool();
        for(int i=0; i<50; i++) {
            executorService.submit(() -> {
                RReadWriteLock readWriteLock = redisson.getReadWriteLock(lockname);
                RLock rLock = readWriteLock.writeLock();
                try {
                    rLock.lock();

                    int count = counter.incrementAndGet();
                    if(count > 1) {
                        concurrentCountSampleList.add(count);
                    }

                    int randomInt = random.nextInt(100);
                    if(randomInt > 0) {
                        try {
                            TimeUnit.MILLISECONDS.sleep(randomInt);
                        } catch (InterruptedException e) {
                            //
                        }
                    }
                } finally {
                    if(rLock.isHeldByCurrentThread()) {
                        counter.decrementAndGet();
                        rLock.unlock();
                    }
                }
            });
        }
        executorService.shutdown();
        while(!executorService.awaitTermination(1, TimeUnit.SECONDS));

        Assert.assertEquals(0, concurrentCountSampleList.size());
    }

    /**
     * 测试读写锁
     * 演示使用读写锁控制不可能出现同时出现读写情况
     * https://blog.csdn.net/qq_43750656/article/details/108634781
     */
    @Test
    public void testReadWriteLock() throws InterruptedException {
        final String lockname = "test-readwrite-lock";

        final Map<String, Boolean> readingMapper  = new ConcurrentHashMap<>();
        final Map<String, Boolean> writingMapper = new ConcurrentHashMap<>();
        List<Boolean> statusList = new ArrayList<>();
        ExecutorService executorService = Executors.newCachedThreadPool();
        for(int i=0; i<50; i++) {
            executorService.submit(() -> {
                RReadWriteLock readWriteLock = redisson.getReadWriteLock(lockname);
                RLock rLock = null;
                String uuid = UUID.randomUUID().toString();
                try {
                    rLock = readWriteLock.readLock();
                    rLock.lock();

                    int randomInt = Random.nextInt(100);
                    if (randomInt <= 0) {
                        randomInt = 1;
                    }

                    readingMapper.put(uuid, true);

                    displayInfo(readingMapper, writingMapper, statusList);

                    try {
                        Thread.sleep(randomInt);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    displayInfo(readingMapper, writingMapper, statusList);
                } catch (Exception ex) {
                    logger.error(ex.getMessage(), ex);
                } finally {
                    readingMapper.remove(uuid);
                    if (rLock != null && rLock.isHeldByCurrentThread()) {
                        rLock.unlock();
                    }
                }
            });
        }

        for(int i=0; i<50; i++) {
            executorService.submit(() -> {
                RReadWriteLock readWriteLock = redisson.getReadWriteLock(lockname);
                RLock rLock = null;
                String uuid = UUID.randomUUID().toString();
                try {
                    rLock = readWriteLock.writeLock();
                    rLock.lock();

                    int randomInt = Random.nextInt(100);
                    if (randomInt <= 0) {
                        randomInt = 1;
                    }

                    writingMapper.put(uuid, true);

                    displayInfo(readingMapper, writingMapper, statusList);

                    try {
                        Thread.sleep(randomInt);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    displayInfo(readingMapper, writingMapper, statusList);
                } catch (Exception ex) {
                    logger.error(ex.getMessage(), ex);
                } finally {
                    writingMapper.remove(uuid);
                    if (rLock != null && rLock.isHeldByCurrentThread()) {
                        rLock.unlock();
                    }
                }
            });
        }

        executorService.shutdown();
        while(!executorService.awaitTermination(1, TimeUnit.SECONDS));

        Assert.assertEquals(0, statusList.size());
    }

    private void displayInfo(Map<String, Boolean> readingMapper, Map<String, Boolean> writingMapper, List<Boolean> statusList) {
        if(readingMapper.size() > 0 && writingMapper.size() > 0) {
//            logger.info("有读写并发reading={},writing={}", readingMapper.size(), writingMapper.size());
            statusList.add(true);
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
