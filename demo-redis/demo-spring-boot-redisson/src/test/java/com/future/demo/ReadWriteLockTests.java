package com.future.demo;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.redisson.api.RLock;
import org.redisson.api.RReadWriteLock;
import org.redisson.api.RedissonClient;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.util.Date;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

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

        int concurrentThreads = 50;
        AtomicInteger flag = new AtomicInteger();
        // 并发线程达到 concurrentThreads 时 flag 自增表示读锁支持并发读
        CyclicBarrier cyclicBarrier = new CyclicBarrier(concurrentThreads, flag::incrementAndGet);
        ExecutorService executorService = Executors.newCachedThreadPool();
        for (int i = 0; i < concurrentThreads; i++) {
            executorService.submit(() -> {
                RLock rLock = null;
                try {
                    RReadWriteLock readWriteLock = redisson.getReadWriteLock(lockname);
                    rLock = readWriteLock.readLock();
                    rLock.lock();

                    // 所有线程达到屏障点，await 才继续运行
                    cyclicBarrier.await(1, TimeUnit.SECONDS);
                } catch (InterruptedException | TimeoutException | BrokenBarrierException ignored) {
                } finally {
                    if (rLock != null && rLock.isHeldByCurrentThread()) {
                        rLock.unlock();
                    }
                }
            });
        }
        executorService.shutdown();
        while (!executorService.awaitTermination(1, TimeUnit.SECONDS)) ;

        Assertions.assertEquals(1, flag.get());
    }

    // 测试不支持并发写
    @Test
    public void testNotSupportConcurrentWrite() throws InterruptedException {
        final String lockname = "test-readwrite-lock";

        int concurrentThreads = 50;
        AtomicInteger flag = new AtomicInteger();
        // 并发线程达到 2 时 flag 自增表示写锁不支持并发写
        CyclicBarrier cyclicBarrier = new CyclicBarrier(2, flag::incrementAndGet);

        ExecutorService executorService = Executors.newCachedThreadPool();
        for (int i = 0; i < concurrentThreads; i++) {
            executorService.submit(() -> {
                RReadWriteLock readWriteLock = redisson.getReadWriteLock(lockname);
                RLock rLock = null;
                try {
                    rLock = readWriteLock.writeLock();
                    rLock.lock();

                    cyclicBarrier.await(60, TimeUnit.MILLISECONDS);
                } catch (BrokenBarrierException | InterruptedException | TimeoutException ignored) {
                } finally {
                    if (rLock != null && rLock.isHeldByCurrentThread()) {
                        rLock.unlock();
                    }
                }
            });
        }
        executorService.shutdown();
        while (!executorService.awaitTermination(1, TimeUnit.SECONDS)) ;

        Assertions.assertEquals(0, flag.get());
    }

    /**
     * 测试读写锁
     * 演示使用读写锁控制不可能出现同时出现读写情况
     * https://blog.csdn.net/qq_43750656/article/details/108634781
     */
    @Test
    public void testReadWriteLock() throws InterruptedException {
        final String lockname = "test-readwrite-lock";

        AtomicInteger flag = new AtomicInteger();
        // 并发线程达到 2 时 flag 自增表示读写锁不支持并发读写
        CyclicBarrier cyclicBarrier = new CyclicBarrier(2, flag::incrementAndGet);

        ExecutorService executorService = Executors.newCachedThreadPool();
        executorService.submit(() -> {
            RReadWriteLock readWriteLock = redisson.getReadWriteLock(lockname);
            RLock rLock = null;
            try {
                rLock = readWriteLock.readLock();
                rLock.lock();

                cyclicBarrier.await(60, TimeUnit.MILLISECONDS);
            } catch (Exception ignored) {

            } finally {
                if (rLock != null && rLock.isHeldByCurrentThread()) {
                    rLock.unlock();
                }
            }
        });

        executorService.submit(() -> {
            RReadWriteLock readWriteLock = redisson.getReadWriteLock(lockname);
            RLock rLock = null;
            try {
                rLock = readWriteLock.writeLock();
                rLock.lock();

                cyclicBarrier.await(60, TimeUnit.MILLISECONDS);
            } catch (Exception ignored) {
            } finally {
                if (rLock != null && rLock.isHeldByCurrentThread()) {
                    rLock.unlock();
                }
            }
        });

        executorService.shutdown();
        while (!executorService.awaitTermination(1, TimeUnit.SECONDS)) ;

        Assertions.assertEquals(0, flag.get());
    }

    // 测试读写锁try lock
    @Test
    public void testReadWriteTryLock() {
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
            Assertions.assertTrue(acquired);
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
        Assertions.assertTrue(endTime.getTime() - startTime.getTime() >= 4800);
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

    /**
     * 演示使用读写锁解决并发读写问题
     */
    @Test
    public void testConcurrentReadWriteScenarioIssueSolvedByUsingReadWriteLock() throws InterruptedException {
        String key = UUID.randomUUID().toString();
        RReadWriteLock readWriteLock = redisson.getReadWriteLock(key);

        ShareResource shareResource = new ShareResource();

        AtomicInteger flag = new AtomicInteger();
        AtomicInteger flagRead = new AtomicInteger();
        AtomicInteger flagWrite = new AtomicInteger();
        CyclicBarrier cyclicBarrierRead = new CyclicBarrier(2, flagRead::incrementAndGet);
        CyclicBarrier cyclicBarrierWrite = new CyclicBarrier(2, flagWrite::incrementAndGet);

        ExecutorService executor = Executors.newFixedThreadPool(50);

        for (int i = 0; i < 10; i++) {
            executor.submit(() -> {

                // 特意让程序先获取写锁
                try {
                    TimeUnit.MILLISECONDS.sleep(20);
                } catch (InterruptedException ignored) {

                }

                RLock rLock = null;
                try {
                    rLock = readWriteLock.readLock();
                    rLock.lock();

                    int value = shareResource.getValue();
                    if (value > 0) {
                        flag.incrementAndGet();
                    }

                    try {
                        // 读写锁支持并发读
                        cyclicBarrierRead.await();
                    } catch (InterruptedException | BrokenBarrierException ignored) {

                    }
                } finally {
                    if (rLock != null) {
                        rLock.unlock();
                    }
                }
            });
        }

        for (int i = 0; i < 10; i++) {
            executor.submit(() -> {
                RLock rLock = null;
                try {
                    rLock = readWriteLock.writeLock();
                    rLock.lock();

                    // 模拟业务卡顿，所有读锁被阻塞
                    try {
                        TimeUnit.MILLISECONDS.sleep(20);
                    } catch (InterruptedException ignored) {

                    }

                    shareResource.decrement();

                    try {
                        // 读写锁不支持并发写
                        cyclicBarrierWrite.await(10, TimeUnit.MILLISECONDS);
                    } catch (InterruptedException | BrokenBarrierException | TimeoutException ignored) {
                    }
                } finally {
                    if (rLock != null) {
                        rLock.unlock();
                    }
                }
            });
        }

        executor.shutdown();
        while (!executor.awaitTermination(50, TimeUnit.MILLISECONDS)) ;

        // 读写锁支持并发读
        Assertions.assertEquals(5, flagRead.get());
        // 读写锁不支持并发写
        Assertions.assertEquals(0, flagWrite.get());
        // 读写锁不支持并发读写
        Assertions.assertEquals(0, shareResource.getValue());
        Assertions.assertEquals(0, flag.get());
    }

    static class ShareResource {
        private int value = 1;

        public int getValue() {
            return value;
        }

        public void decrement() {
            if (value == 0) {
                return;
            }

            // 如果 decrement 方法没有并发锁控制，则 value-- 会被多次执行
            try {
                TimeUnit.MILLISECONDS.sleep(5);
            } catch (InterruptedException ignored) {

            }

            value--;
        }
    }
}
