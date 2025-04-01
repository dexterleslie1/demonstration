package com.future.demo;

import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.util.UUID;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

@RunWith(SpringRunner.class)
@SpringBootTest
@Slf4j
public class LockTests {
    @Resource
    RedissonClient redisson = null;

    /**
     * 测试标准用法
     *
     * @throws InterruptedException
     */
    @Test
    public void test() throws InterruptedException {
        final String key = UUID.randomUUID().toString();

        AtomicInteger atomicIntegerAcquired = new AtomicInteger();
        AtomicInteger atomicIntegerNotAcquired = new AtomicInteger();
        ExecutorService service = Executors.newCachedThreadPool();
        int totalConcurrent = 100;
        for (int i = 0; i < totalConcurrent; i++) {
            service.submit(new Runnable() {
                public void run() {
                    RLock lock = null;
                    boolean acquired = false;
                    try {
                        lock = redisson.getLock(key);

                        acquired = lock.tryLock(10, 30000, TimeUnit.MILLISECONDS);
                        if (!acquired) {
                            atomicIntegerNotAcquired.incrementAndGet();
                            return;
                        }

                        // 锁定2秒
                        Thread.sleep(500);
                        atomicIntegerAcquired.incrementAndGet();
                    } catch (Exception ex) {
                        throw new RuntimeException(ex);
                    } finally {
                        if (acquired) {
                            try {
                                lock.unlock();
                            } catch (Exception ex) {
                                //
                            }
                        }
                    }
                }
            });
        }
        service.shutdown();
        while (!service.awaitTermination(100, TimeUnit.MILLISECONDS)) ;

        // 只有一个并发请求能够取得锁
        Assert.assertEquals(1, atomicIntegerAcquired.get());
        Assert.assertEquals(totalConcurrent - 1, atomicIntegerNotAcquired.get());
    }

    /**
     * 演示使用isLocked函数判断key是否被锁定
     *
     * @throws InterruptedException
     */
    @Test
    public void testIsLocked() throws InterruptedException {
        final String key = UUID.randomUUID().toString();
        // 成功获取锁
        final AtomicInteger atomicIntegerAquiredLock = new AtomicInteger();
        // 未成功获取锁
        final AtomicInteger atomicIntegerNotAquiredLock = new AtomicInteger();
        // 已上锁
        final AtomicInteger atomicIntegerLocked = new AtomicInteger();
        int totalThreads = 100;
        ExecutorService service = Executors.newCachedThreadPool();
        for (int i = 0; i < totalThreads; i++) {
            service.submit(() -> {
                RLock lock = redisson.getLock(key);
                boolean isAquireLock = false;
                try {
                    isAquireLock = lock.tryLock(10, 30000, TimeUnit.MILLISECONDS);
                    if (isAquireLock) {
                        atomicIntegerAquiredLock.incrementAndGet();
                        // 模拟耗时操作
                        Thread.sleep(2000);
                    } else {
                        atomicIntegerNotAquiredLock.incrementAndGet();
                    }

                    boolean isLocked = lock.isLocked();
                    if (isLocked) {
                        atomicIntegerLocked.incrementAndGet();
                    }
                } catch (Exception ex) {
                    throw new RuntimeException(ex);
                } finally {
                    if (isAquireLock) {
                        try {
                            lock.unlock();
                        } catch (Exception ex) {
                            //
                        }
                    }
                }
            });
        }
        service.shutdown();
        while (!service.awaitTermination(100, TimeUnit.MILLISECONDS)) ;

        Assert.assertEquals(1, atomicIntegerAquiredLock.get());
        Assert.assertEquals(totalThreads - 1, atomicIntegerNotAquiredLock.get());
        Assert.assertEquals(totalThreads, atomicIntegerLocked.get());
        Assert.assertFalse(redisson.getLock(key).isLocked());
    }

    /**
     * 测试tryLock函数leaseTime参数
     *
     * @throws InterruptedException
     */
    @Test
    public void testTryLockLeaseTime() throws InterruptedException {
        final AtomicInteger atomicInteger = new AtomicInteger();
        final String key = UUID.randomUUID().toString();
        ExecutorService executorService = Executors.newCachedThreadPool();
        executorService.submit(() -> {
            try {
                RLock rLock = redisson.getLock(key);
                rLock.tryLock(10, 500, TimeUnit.MILLISECONDS);
            } catch (Exception ignored) {

            }
        });

        executorService.submit(() -> {
            RLock rLock = redisson.getLock(key);
            try {
                while (!rLock.tryLock(10, 1000, TimeUnit.MILLISECONDS)) {
                    atomicInteger.incrementAndGet();
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            } finally {
                rLock.unlock();
            }
        });

        executorService.shutdown();
        while (!executorService.awaitTermination(100, TimeUnit.MILLISECONDS)) ;

        Assert.assertTrue(atomicInteger.get() > 0);
        Assert.assertFalse(redisson.getLock(key).isLocked());
    }

    /**
     * 非本线程unlock锁会抛出IllegalMonitorStateException
     *
     * @throws InterruptedException
     */
    @Test
    public void testUnlockByAnotherThreadWithExceptionExpected() throws InterruptedException, ExecutionException {
        final AtomicInteger atomicIntegerException = new AtomicInteger();
        final AtomicInteger atomicIntegerExceptionCounter = new AtomicInteger();
        final String keyTemp = UUID.randomUUID().toString();
        ExecutorService executorService = Executors.newCachedThreadPool();
        Future future = executorService.submit(() -> {
            try {
                RLock rLock = redisson.getLock(keyTemp);
                boolean acquireLock = rLock.tryLock(10, 50000, TimeUnit.MILLISECONDS);
                Assert.assertTrue(acquireLock);
                boolean isHeldByCurrentThread = rLock.isHeldByCurrentThread();
                Assert.assertTrue(isHeldByCurrentThread);
                boolean isLocked = rLock.isLocked();
                Assert.assertTrue(isLocked);
                Thread.sleep(3000);
            } catch (Exception ex) {
                atomicIntegerException.incrementAndGet();
            } finally {
                RLock rLock = redisson.getLock(keyTemp);
                rLock.unlock();
            }
        });

        Thread.sleep(100);

        Future future1 = executorService.submit(() -> {
            try {
                RLock rLock = redisson.getLock(keyTemp);
                boolean acquireLock = rLock.tryLock(10, 30000, TimeUnit.MILLISECONDS);
                Assert.assertFalse(acquireLock);
            } catch (Exception ex) {
                atomicIntegerException.incrementAndGet();
            } finally {
                RLock rLock = redisson.getLock(keyTemp);
                boolean isHeldByCurrentThread = rLock.isHeldByCurrentThread();
                Assert.assertFalse(isHeldByCurrentThread);
                boolean isLocked = rLock.isLocked();
                Assert.assertTrue(isLocked);
                try {
                    rLock.unlock();
                } catch (Exception ex) {
                    if (ex instanceof IllegalMonitorStateException) {
                        atomicIntegerExceptionCounter.incrementAndGet();
                    }
                }
            }
        });

        executorService.shutdown();
        while (!executorService.awaitTermination(10, TimeUnit.MILLISECONDS)) ;
        future.get();
        future1.get();

        Assert.assertTrue("Exception counter=" + atomicIntegerExceptionCounter.get(), atomicIntegerExceptionCounter.get() > 0);
        Assert.assertTrue("Exception count=" + atomicIntegerException.get(), atomicIntegerException.get() <= 0);
    }

    /**
     * 测试isHeldByCurrentThread方法
     */
    @Test
    public void testIsHeldByCurrentThread() throws InterruptedException {
        final AtomicInteger atomicIntegerOwner = new AtomicInteger();
        final AtomicInteger atomicIntegerNotOwner = new AtomicInteger();

        final String keyTemp = UUID.randomUUID().toString();
        ExecutorService executorService = Executors.newCachedThreadPool();
        executorService.submit(() -> {
            try {
                RLock rLock = redisson.getLock(keyTemp);
                rLock.tryLock(10, 5000, TimeUnit.MILLISECONDS);
                boolean isHeldByCurrentThread = rLock.isHeldByCurrentThread();
                if (isHeldByCurrentThread) {
                    atomicIntegerOwner.incrementAndGet();
                }

                // 暂时不释放当前线程
                TimeUnit.SECONDS.sleep(1);
            } catch (Exception ex) {
                log.error(ex.getMessage(), ex);
            }
        });

        TimeUnit.MILLISECONDS.sleep(100);

        int notOwnerCounter = 15;
        for (int i = 0; i < notOwnerCounter; i++) {
            executorService.submit(() -> {
                try {
                    RLock rLock = redisson.getLock(keyTemp);
                    rLock.tryLock(1, 5000, TimeUnit.MILLISECONDS);
                    boolean isHeldByCurrentThread = rLock.isHeldByCurrentThread();
                    if (!isHeldByCurrentThread) {
                        atomicIntegerNotOwner.incrementAndGet();
                    }
                } catch (Exception ignored) {

                }
            });
        }

        executorService.shutdown();
        while (!executorService.awaitTermination(100, TimeUnit.MILLISECONDS)) ;

        Assert.assertEquals(1, atomicIntegerOwner.get());
        Assert.assertEquals(notOwnerCounter, atomicIntegerNotOwner.get());
    }

    /**
     * 测试当线程结束是否自动释放锁
     */
    @Test
    public void testIfUnlockAutomaticallyWhenThreadEnd() throws InterruptedException {
        final String keyTemp = "keyTestUnlock";
        ExecutorService executorService = Executors.newCachedThreadPool();
        executorService.submit(new Runnable() {
            public void run() {
                try {
                    RLock rLock = redisson.getLock(keyTemp);
                    rLock.tryLock(10, 10000, TimeUnit.MILLISECONDS);
                } catch (Exception ex) {

                }
            }
        });

        executorService.shutdown();
        while (!executorService.awaitTermination(10, TimeUnit.MILLISECONDS)) ;

        RLock rLock = redisson.getLock(keyTemp);
        boolean isLocked = rLock.isLocked();
        // 验证不会自动释放锁
        Assert.assertTrue(isLocked);
    }

    @Test
    public void testLeaseAutomatically() throws Exception {
        String lockKey = UUID.randomUUID().toString();

        // 下面演示不够严谨用法，没有考虑到会自动lease
        RLock rLock = redisson.getLock(lockKey);
        boolean acquired;
        try {
            acquired = rLock.tryLock(10, 500, TimeUnit.MILLISECONDS);
            Assert.assertTrue(acquired);

            TimeUnit.SECONDS.sleep(1);
        } finally {
            try {
                // 锁已经超时自动释放，再调用unlock会抛出异常
                rLock.unlock();
                Assert.fail("预期异常没有抛出");
            } catch (Exception ignored) {
            }
        }
    }
}
