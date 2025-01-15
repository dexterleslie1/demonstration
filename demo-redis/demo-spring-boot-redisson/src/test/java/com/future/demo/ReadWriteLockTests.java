package com.future.demo;

import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.redisson.api.RLock;
import org.redisson.api.RReadWriteLock;
import org.redisson.api.RedissonClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

@RunWith(SpringRunner.class)
@SpringBootTest
@Slf4j
public class ReadWriteLockTests {
    private final static java.util.Random Random = new Random();

    @Resource
    RedissonClient redisson = null;

    // 测试支持并发读
    @Test
    public void testConcurrentRead() throws InterruptedException {
        final String lockname = "test-readwrite-lock";

        AtomicInteger counter = new AtomicInteger();
        List<Integer> concurrentCountSampleList = new ArrayList<>();
        Random random = new Random();
        ExecutorService executorService = Executors.newCachedThreadPool();
        for (int i = 0; i < 50; i++) {
            executorService.submit(() -> {
                RReadWriteLock readWriteLock = redisson.getReadWriteLock(lockname);
                RLock rLock = readWriteLock.readLock();
                try {
                    rLock.lock();

                    int count = counter.incrementAndGet();
                    if (count > 1) {
                        concurrentCountSampleList.add(count);
                    }

                    int randomInt = random.nextInt(1000);
                    if (randomInt > 0) {
                        try {
                            TimeUnit.MILLISECONDS.sleep(randomInt);
                        } catch (InterruptedException e) {
                            //
                        }
                    }
                } finally {
                    if (rLock.isHeldByCurrentThread()) {
                        counter.decrementAndGet();
                        rLock.unlock();
                    }
                }
            });
        }
        executorService.shutdown();
        while (!executorService.awaitTermination(1, TimeUnit.SECONDS)) ;

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
        for (int i = 0; i < 50; i++) {
            executorService.submit(() -> {
                RReadWriteLock readWriteLock = redisson.getReadWriteLock(lockname);
                RLock rLock = readWriteLock.writeLock();
                try {
                    rLock.lock();

                    int count = counter.incrementAndGet();
                    if (count > 1) {
                        concurrentCountSampleList.add(count);
                    }

                    int randomInt = random.nextInt(100);
                    if (randomInt > 0) {
                        try {
                            TimeUnit.MILLISECONDS.sleep(randomInt);
                        } catch (InterruptedException e) {
                            //
                        }
                    }
                } finally {
                    if (rLock.isHeldByCurrentThread()) {
                        counter.decrementAndGet();
                        rLock.unlock();
                    }
                }
            });
        }
        executorService.shutdown();
        while (!executorService.awaitTermination(1, TimeUnit.SECONDS)) ;

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

        final Map<String, Boolean> readingMapper = new ConcurrentHashMap<>();
        final Map<String, Boolean> writingMapper = new ConcurrentHashMap<>();
        List<Boolean> statusList = new ArrayList<>();
        ExecutorService executorService = Executors.newCachedThreadPool();
        for (int i = 0; i < 50; i++) {
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
                    log.error(ex.getMessage(), ex);
                } finally {
                    readingMapper.remove(uuid);
                    if (rLock != null && rLock.isHeldByCurrentThread()) {
                        rLock.unlock();
                    }
                }
            });
        }

        for (int i = 0; i < 50; i++) {
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
                    log.error(ex.getMessage(), ex);
                } finally {
                    writingMapper.remove(uuid);
                    if (rLock != null && rLock.isHeldByCurrentThread()) {
                        rLock.unlock();
                    }
                }
            });
        }

        executorService.shutdown();
        while (!executorService.awaitTermination(1, TimeUnit.SECONDS)) ;

        Assert.assertEquals(0, statusList.size());
    }

    // 测试读写锁try lock
    @Test
    public void testReadWriteTryLock() throws InterruptedException {
        ExecutorService executorService = Executors.newCachedThreadPool();

        String key = UUID.randomUUID().toString();

        executorService.submit(() -> {
            RReadWriteLock readWriteLock = redisson.getReadWriteLock(key);
            RLock rLock = null;
            try {
                rLock = readWriteLock.writeLock();
                rLock.tryLock(3, 60, TimeUnit.SECONDS);

                TimeUnit.SECONDS.sleep(5);
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                if (rLock != null && rLock.isHeldByCurrentThread()) {
                    try {
                        rLock.unlock();
                    } catch (Exception ignored) {

                    }
                }
            }
        });

        Date startTime = new Date();
        RReadWriteLock readWriteLock = redisson.getReadWriteLock(key);
        RLock rLock = null;
        try {
            rLock = readWriteLock.readLock();
            boolean acquired = rLock.tryLock(60, 60, TimeUnit.SECONDS);
            Assert.assertTrue(acquired);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            if (rLock != null && rLock.isHeldByCurrentThread()) {
                try {
                    rLock.unlock();
                } catch (Exception ignored) {

                }
            }
        }
        Date endTime = new Date();
        Assert.assertTrue(endTime.getTime() - startTime.getTime() >= 4800);
    }

    private boolean isWriteLockExit = false;

    @Test
    public void testReadLockHigherPriorityThanWriteLock() throws InterruptedException {
        ExecutorService executorService = Executors.newCachedThreadPool();

        String key = UUID.randomUUID().toString();

        executorService.submit(() -> {
            try {
                TimeUnit.SECONDS.sleep(5);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            Date startTime = new Date();

            RReadWriteLock readWriteLock = redisson.getReadWriteLock(key);
            RLock rLock = null;
//            boolean isAcquired = false;
            try {
                rLock = readWriteLock.writeLock();
                /*isAcquired = */
                rLock.tryLock(30, 60, TimeUnit.SECONDS);

                Date endTime = new Date();
                System.out.println("写锁耗时：" + (endTime.getTime() - startTime.getTime()));

                isWriteLockExit = true;
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                if (rLock != null && rLock.isHeldByCurrentThread()) {
                    try {
                        rLock.unlock();
                    } catch (Exception ignored) {

                    }
                }
            }
        });

        for (int i = 0; i < 128; i++) {
            executorService.submit(new Runnable() {
                @Override
                public void run() {
                    while (!isWriteLockExit) {
                        RReadWriteLock readWriteLock = redisson.getReadWriteLock(key);
                        RLock rLock = null;
                        try {
                            rLock = readWriteLock.readLock();
                            boolean isAcquired = rLock.tryLock(5, 60, TimeUnit.SECONDS);

                            if (isAcquired) {
                                TimeUnit.SECONDS.sleep(5);
                            }

                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        } finally {
                            if (rLock != null && rLock.isHeldByCurrentThread()) {
                                try {
                                    rLock.unlock();
                                } catch (Exception ignored) {

                                }
                            }
                        }
                    }
                }
            });
        }

        executorService.shutdown();
        while (!executorService.awaitTermination(1, TimeUnit.SECONDS)) {

        }
    }

    private void displayInfo(Map<String, Boolean> readingMapper, Map<String, Boolean> writingMapper, List<Boolean> statusList) {
        if (readingMapper.size() > 0 && writingMapper.size() > 0) {
//            logger.info("有读写并发reading={},writing={}", readingMapper.size(), writingMapper.size());
            statusList.add(true);
        }
    }
}
