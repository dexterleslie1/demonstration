package com.future.demo;

import junit.framework.Assert;
import lombok.extern.slf4j.Slf4j;
import org.junit.After;
import org.junit.Test;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

@Slf4j
public class LockTests {
    ExecutorService executor = Executors.newCachedThreadPool();

    @After
    public void teardown() {
        if (this.executor != null) {
            this.executor.shutdown();
        }
    }

    @Test
    public void testSynchronized() {
        // region synchronized 锁几种情况测试

        SynchronizedTestingAssistantObject synchronizedTestingAssistantObject1 = new SynchronizedTestingAssistantObject();
        SynchronizedTestingAssistantObject synchronizedTestingAssistantObject2 = new SynchronizedTestingAssistantObject();

        // 两个 synchronized 并且同一个实例的方法，锁互斥
        CompletableFuture<Void> completableFuture1 = CompletableFuture.runAsync(() -> synchronizedTestingAssistantObject1.instanceMethod1WithSynchronized("c1"), this.executor);
        CompletableFuture<Void> completableFuture2 = CompletableFuture.runAsync(() -> synchronizedTestingAssistantObject1.instanceMethod2WithSynchronized("c2"), this.executor);
        CompletableFuture.allOf(completableFuture1, completableFuture2).join();
        Assert.assertTrue(MyCheckPoint.isBefore("c1", "c2"));
        MyCheckPoint.clear();

        // 一个 synchronized 实例方法，一个普通实例方法，同一个实例，锁不互斥
        completableFuture1 = CompletableFuture.runAsync(() -> synchronizedTestingAssistantObject1.instanceMethod1WithSynchronized("c1"), this.executor);
        completableFuture2 = CompletableFuture.runAsync(() -> synchronizedTestingAssistantObject1.instanceMethod3WithoutSynchronized("c2"), this.executor);
        CompletableFuture.allOf(completableFuture1, completableFuture2).join();
        Assert.assertTrue(MyCheckPoint.isBefore("c2", "c1"));
        MyCheckPoint.clear();

        // 两个 synchronized 并且不是同一个实例的方法，锁不互斥
        completableFuture1 = CompletableFuture.runAsync(() -> synchronizedTestingAssistantObject1.instanceMethod1WithSynchronized("c1"), this.executor);
        completableFuture2 = CompletableFuture.runAsync(() -> synchronizedTestingAssistantObject2.instanceMethod3WithoutSynchronized("c2"), this.executor);
        CompletableFuture.allOf(completableFuture1, completableFuture2).join();
        Assert.assertTrue(MyCheckPoint.isBefore("c2", "c1"));
        MyCheckPoint.clear();

        // 两个 synchronized 静态方法并且是同一个实例，锁互斥
        completableFuture1 = CompletableFuture.runAsync(() -> synchronizedTestingAssistantObject1.staticMethod1WithSynchronized("c1"), this.executor);
        completableFuture2 = CompletableFuture.runAsync(() -> synchronizedTestingAssistantObject1.staticMethod2WithSynchronized("c2"), this.executor);
        CompletableFuture.allOf(completableFuture1, completableFuture2).join();
        Assert.assertTrue(MyCheckPoint.isBefore("c1", "c2"));
        MyCheckPoint.clear();

        // 两个 synchronized 静态方法并且不是同一个实例，锁互斥
        completableFuture1 = CompletableFuture.runAsync(() -> synchronizedTestingAssistantObject1.staticMethod1WithSynchronized("c1"), this.executor);
        completableFuture2 = CompletableFuture.runAsync(() -> synchronizedTestingAssistantObject2.staticMethod2WithSynchronized("c2"), this.executor);
        CompletableFuture.allOf(completableFuture1, completableFuture2).join();
        Assert.assertTrue(MyCheckPoint.isBefore("c1", "c2"));
        MyCheckPoint.clear();

        // 一个 synchronized 静态方法，一个 synchronized 实例方法，同一个实例，锁不互斥
        completableFuture1 = CompletableFuture.runAsync(() -> synchronizedTestingAssistantObject1.staticMethod1WithSynchronized("c1"), this.executor);
        completableFuture2 = CompletableFuture.runAsync(() -> synchronizedTestingAssistantObject1.instanceMethod2WithSynchronized("c2"), this.executor);
        CompletableFuture.allOf(completableFuture1, completableFuture2).join();
        Assert.assertTrue(MyCheckPoint.isBefore("c2", "c1"));
        MyCheckPoint.clear();

        // 一个 synchronized 静态方法，一个 synchronized 实例方法，不是同一个实例，锁不互斥
        completableFuture1 = CompletableFuture.runAsync(() -> synchronizedTestingAssistantObject1.staticMethod1WithSynchronized("c1"), this.executor);
        completableFuture2 = CompletableFuture.runAsync(() -> synchronizedTestingAssistantObject2.instanceMethod2WithSynchronized("c2"), this.executor);
        CompletableFuture.allOf(completableFuture1, completableFuture2).join();
        Assert.assertTrue(MyCheckPoint.isBefore("c2", "c1"));
        MyCheckPoint.clear();

        // endregion
    }

    @Test
    public void testReentrantLock() {
        // region 公平锁和非公平锁

        // 非公平锁测试
        ReentrantLock reentrantLock = new ReentrantLock();
        AtomicLong counter = new AtomicLong(1000);
        AtomicLong lockCounter1 = new AtomicLong();
        AtomicLong lockCounter2 = new AtomicLong();
        AtomicLong lockCounter3 = new AtomicLong();
        ReentrantLock finalReentrantLock3 = reentrantLock;
        AtomicLong finalCounter3 = counter;
        AtomicLong finalLockCounter3 = lockCounter1;
        CompletableFuture<Void> completableFuture1 = CompletableFuture.runAsync(() -> {
            while (true) {
                try {
                    finalReentrantLock3.lock();

                    try {
                        TimeUnit.MILLISECONDS.sleep(1);
                    } catch (InterruptedException ignored) {

                    }

                    if (finalCounter3.get() <= 0) {
                        break;
                    }
                    finalCounter3.decrementAndGet();
                    finalLockCounter3.incrementAndGet();
                } finally {
                    finalReentrantLock3.unlock();
                }
            }
        }, this.executor);
        ReentrantLock finalReentrantLock4 = reentrantLock;
        AtomicLong finalCounter4 = counter;
        AtomicLong finalLockCounter4 = lockCounter2;
        CompletableFuture<Void> completableFuture2 = CompletableFuture.runAsync(() -> {
            while (true) {
                try {
                    finalReentrantLock4.lock();

                    try {
                        TimeUnit.MILLISECONDS.sleep(1);
                    } catch (InterruptedException ignored) {

                    }

                    if (finalCounter4.get() <= 0) {
                        break;
                    }
                    finalCounter4.decrementAndGet();
                    finalLockCounter4.incrementAndGet();
                } finally {
                    finalReentrantLock4.unlock();
                }
            }
        }, this.executor);
        ReentrantLock finalReentrantLock5 = reentrantLock;
        AtomicLong finalCounter5 = counter;
        AtomicLong finalLockCounter5 = lockCounter3;
        CompletableFuture<Void> completableFuture3 = CompletableFuture.runAsync(() -> {
            while (true) {
                try {
                    finalReentrantLock5.lock();

                    try {
                        TimeUnit.MILLISECONDS.sleep(1);
                    } catch (InterruptedException ignored) {

                    }

                    if (finalCounter5.get() <= 0) {
                        break;
                    }
                    finalCounter5.decrementAndGet();
                    finalLockCounter5.incrementAndGet();
                } finally {
                    finalReentrantLock5.unlock();
                }
            }
        }, this.executor);
        CompletableFuture.allOf(completableFuture1, completableFuture2, completableFuture3).join();
        // 从输出结果中可以看出各个线程获取到的锁的次数不平均
        log.debug("lockCounter1=" + lockCounter1.get() + ",lockCounter2=" + lockCounter2.get() + ",lockCounter3=" + lockCounter3.get());

        // 公平锁测试
        reentrantLock = new ReentrantLock(true);
        counter = new AtomicLong(1000);
        lockCounter1 = new AtomicLong();
        lockCounter2 = new AtomicLong();
        lockCounter3 = new AtomicLong();
        ReentrantLock finalReentrantLock = reentrantLock;
        AtomicLong finalCounter = counter;
        AtomicLong finalLockCounter = lockCounter1;
        completableFuture1 = CompletableFuture.runAsync(() -> {
            while (true) {
                try {
                    finalReentrantLock.lock();

                    try {
                        TimeUnit.MILLISECONDS.sleep(1);
                    } catch (InterruptedException ignored) {

                    }

                    if (finalCounter.get() <= 0) {
                        break;
                    }
                    finalCounter.decrementAndGet();
                    finalLockCounter.incrementAndGet();
                } finally {
                    finalReentrantLock.unlock();
                }
            }
        }, this.executor);
        ReentrantLock finalReentrantLock1 = reentrantLock;
        AtomicLong finalCounter1 = counter;
        AtomicLong finalLockCounter1 = lockCounter2;
        completableFuture2 = CompletableFuture.runAsync(() -> {
            while (true) {
                try {
                    finalReentrantLock1.lock();

                    try {
                        TimeUnit.MILLISECONDS.sleep(1);
                    } catch (InterruptedException ignored) {

                    }

                    if (finalCounter1.get() <= 0) {
                        break;
                    }
                    finalCounter1.decrementAndGet();
                    finalLockCounter1.incrementAndGet();
                } finally {
                    finalReentrantLock1.unlock();
                }
            }
        }, this.executor);
        ReentrantLock finalReentrantLock2 = reentrantLock;
        AtomicLong finalCounter2 = counter;
        AtomicLong finalLockCounter2 = lockCounter3;
        completableFuture3 = CompletableFuture.runAsync(() -> {
            while (true) {
                try {
                    finalReentrantLock2.lock();

                    try {
                        TimeUnit.MILLISECONDS.sleep(1);
                    } catch (InterruptedException ignored) {

                    }

                    if (finalCounter2.get() <= 0) {
                        break;
                    }
                    finalCounter2.decrementAndGet();
                    finalLockCounter2.incrementAndGet();
                } finally {
                    finalReentrantLock2.unlock();
                }
            }
        }, this.executor);
        CompletableFuture.allOf(completableFuture1, completableFuture2, completableFuture3).join();
        // 从输出结果中可以看出各个线程获取到的锁的次数平均
        log.debug("lockCounter1=" + lockCounter1.get() + ",lockCounter2=" + lockCounter2.get() + ",lockCounter3=" + lockCounter3.get());

        // endregion
    }

    /**
     * 演示使用读写锁解决并发读写问题
     */
    @Test
    public void testConcurrentReadWriteScenarioIssueSolvedByUsingReentrantReadWriteLock() throws InterruptedException {
        ReentrantReadWriteLock readWriteLock = new ReentrantReadWriteLock();

        ShareResource shareResource = new ShareResource();

        AtomicInteger flag = new AtomicInteger();
        AtomicInteger flagRead = new AtomicInteger();
        AtomicInteger flagWrite = new AtomicInteger();
        CyclicBarrier cyclicBarrierRead = new CyclicBarrier(2, flagRead::incrementAndGet);
        CyclicBarrier cyclicBarrierWrite = new CyclicBarrier(2, flagWrite::incrementAndGet);

        ExecutorService executor = Executors.newCachedThreadPool();

        for (int i = 0; i < 10; i++) {
            executor.submit(() -> {

                // 特意让程序先获取写锁
                try {
                    TimeUnit.MILLISECONDS.sleep(20);
                } catch (InterruptedException ignored) {

                }

                ReentrantReadWriteLock.ReadLock lock = null;
                try {
                    lock = readWriteLock.readLock();
                    lock.lock();

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
                    if (lock != null) {
                        lock.unlock();
                    }
                }
            });
        }

        for (int i = 0; i < 10; i++) {
            executor.submit(() -> {
                ReentrantReadWriteLock.WriteLock lock = null;
                try {
                    lock = readWriteLock.writeLock();
                    lock.lock();

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
                    if (lock != null) {
                        lock.unlock();
                    }
                }
            });
        }

        executor.shutdown();
        while (!executor.awaitTermination(50, TimeUnit.MILLISECONDS)) ;

        // 读写锁支持并发读
        org.junit.Assert.assertEquals(5, flagRead.get());
        // 读写锁不支持并发写
        org.junit.Assert.assertEquals(0, flagWrite.get());
        // 读写锁不支持并发读写
        org.junit.Assert.assertEquals(0, shareResource.getValue());
        org.junit.Assert.assertEquals(0, flag.get());
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
