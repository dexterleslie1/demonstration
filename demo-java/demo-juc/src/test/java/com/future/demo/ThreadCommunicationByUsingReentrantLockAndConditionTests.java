package com.future.demo;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 线程间通信使用 ReentrantLock+Condition 方法
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class)
@Slf4j
public class ThreadCommunicationByUsingReentrantLockAndConditionTests {
    @Test
    public void test() throws InterruptedException {
        int runLoop = 5;
        ShareResource shareResource = new ShareResource();

        Thread thread1 = new Thread(() -> {
            for (int i = 0; i < runLoop; i++) {
                try {
                    shareResource.increment();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }, "A");
        Thread thread11 = new Thread(() -> {
            for (int i = 0; i < runLoop; i++) {
                try {
                    shareResource.increment();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }, "A1");
        Thread thread2 = new Thread(() -> {
            for (int i = 0; i < runLoop; i++) {
                try {
                    shareResource.decrement();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }, "B");
        Thread thread21 = new Thread(() -> {
            for (int i = 0; i < runLoop; i++) {
                try {
                    shareResource.decrement();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }, "B1");
        thread1.start();
        thread11.start();
        thread2.start();
        thread21.start();

        thread1.join();
        thread11.join();
        thread2.join();
        thread21.join();
    }

    static class ShareResource {
        private ReentrantLock lock = new ReentrantLock();
        private Condition condition = lock.newCondition();
        private int value;

        void increment() throws InterruptedException {
            // 需要先给 lock 对象上锁才能够调用 condition await、signal、signalAll 方法
            try {
                lock.lock();
                // 注意：不能使用 if 判断以避免 wait() 虚假唤醒问题
                while (value == 1) {
                    condition.await();
                }

                value++;
                log.debug("{} value++: {}", Thread.currentThread().getName(), value);
                condition.signalAll();
            } finally {
                lock.unlock();
            }
        }

        void decrement() throws InterruptedException {
            // 需要先给 lock 对象上锁才能够调用 condition await、signal、signalAll 方法
            try {
                lock.lock();
                // 注意：不能使用 if 判断以避免 wait() 虚假唤醒问题
                while (value == 0) {
                    condition.await();
                }

                value--;
                log.debug("{} value--: {}", Thread.currentThread().getName(), value);
                condition.signalAll();
            } finally {
                lock.unlock();
            }
        }
    }
}
