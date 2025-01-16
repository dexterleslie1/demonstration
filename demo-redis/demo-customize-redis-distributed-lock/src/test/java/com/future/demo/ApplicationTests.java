package com.future.demo;

import org.junit.Assert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

@SpringBootTest
class ApplicationTests {
    @Autowired
    StringRedisTemplate redisTemplate;

    @Test
    void contextLoads() throws InterruptedException {
        // region 测试是否会错误释放锁
        AtomicInteger acquiredCounter = new AtomicInteger();
        AtomicInteger notAcquiredCounter = new AtomicInteger();

        String key = UUID.randomUUID().toString();

        ExecutorService executorService = Executors.newCachedThreadPool();

        // 持有锁的线程
        executorService.submit(() -> {
            MyLock lock = null;
            boolean acquired = false;
            try {
                lock = new MyLock(key, redisTemplate);
                acquired = lock.lock(60);

                TimeUnit.SECONDS.sleep(3);

                if (acquired) {
                    acquiredCounter.incrementAndGet();
                } else {
                    notAcquiredCounter.incrementAndGet();
                }

            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (lock != null && acquired) {
                    lock.unlock();
                }
            }
        });

        TimeUnit.SECONDS.sleep(1);

        int concurrentThreads = 256;
        int loopInner = 10;
        for (int i = 0; i < concurrentThreads; i++) {
            executorService.submit(() -> {
                for (int j = 0; j < loopInner; j++) {
                    MyLock lock = null;
                    try {
                        lock = new MyLock(key, redisTemplate);
                        boolean acquired = lock.lock(60);

                        if (acquired) {
                            acquiredCounter.incrementAndGet();
                        } else {
                            notAcquiredCounter.incrementAndGet();
                        }

                    } finally {
                        if (lock != null) {
                            lock.unlock();
                        }
                    }
                }
            });
        }

        executorService.shutdown();
        while (!executorService.awaitTermination(10, TimeUnit.MILLISECONDS)) {
        }

        Assert.assertEquals(1, acquiredCounter.get());
        Assert.assertEquals(concurrentThreads * loopInner, notAcquiredCounter.get());

        // endregion

        // region 测试锁释放线程安全问题，期望结果为：线程1成功获取锁，线程2成功获取锁，线程3获取锁失败（但是线程3成功获取锁）

        // 删除之前的 key 以避免干扰测试
        this.redisTemplate.delete(key);

        AtomicInteger thread1Counter = new AtomicInteger();
        AtomicInteger thread2Counter = new AtomicInteger();
        AtomicInteger thread3Counter = new AtomicInteger();

        executorService = Executors.newCachedThreadPool();

        executorService.submit(() -> {
            MyLock lock = null;
            boolean acquired = false;
            try {
                lock = new MyLock(key, redisTemplate);
                acquired = lock.lock(1);

                if (acquired) {
                    // 线程1成功获取锁
                    thread1Counter.incrementAndGet();
                }
            } finally {
                if (lock != null && acquired) {
                    // 在释放锁前模拟 JVM GC 卡顿 2 秒，2秒内锁的键已经超时过期
                    lock.unlockWithThreadSafeProblem(2);
                }
            }
        });

        // 等待线程1锁超时过期
        TimeUnit.MILLISECONDS.sleep(1500);

        executorService.submit(() -> {
            MyLock lock = null;
            try {
                lock = new MyLock(key, redisTemplate);
                boolean acquired = lock.lock(60);

                if (acquired) {
                    // 线程2成功获取锁，因为线程1锁超时过期
                    thread2Counter.incrementAndGet();
                }
            } finally {

            }
        });

        // 等待线程1错误删除线程2的锁，此时线程3成功获取锁
        TimeUnit.MILLISECONDS.sleep(600);

        executorService.submit(() -> {
            MyLock lock = null;
            try {
                lock = new MyLock(key, redisTemplate);
                boolean acquired = lock.lock(60);

                if (acquired) {
                    // 线程3成功获取锁
                    thread3Counter.incrementAndGet();
                }
            } finally {

            }
        });

        executorService.shutdown();
        while (!executorService.awaitTermination(10, TimeUnit.MILLISECONDS)) {

        }

        Assert.assertEquals(1, thread1Counter.get());
        Assert.assertEquals(1, thread2Counter.get());
        Assert.assertEquals(1, thread3Counter.get());

        // endregion

        // region 测试超时自动释放锁

        // 删除之前的 key 以避免干扰测试
        this.redisTemplate.delete(key);

        MyLock lock = null;
        try {
            lock = new MyLock(key, redisTemplate);
            lock.lock(1);
        } finally {

        }

        TimeUnit.MILLISECONDS.sleep(1100);

        Assertions.assertNull(this.redisTemplate.opsForValue().get(key));

        // endregion

        // region todo 测试锁自动续命
        // endregion

        // region todo 测试锁重入
        // endregion

        // region todo 测试锁阻塞等待
        // endregion

        // region todo 测试锁阻塞等待超时
        // endregion
    }

}
