package com.future.demo;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * 线程间通信使用 wait、notify、notifyAll 方法
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class)
@Slf4j
public class ThreadCommunicationByUsingObjectWaitAndNotifyTests {
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
        private final Object lock = new Object();
        private int value;

        void increment() throws InterruptedException {
            // 需要先给 lock 对象上锁才能够调用其 wait、notify、notifyAll 方法
            synchronized (lock) {
                // 注意：不能使用 if 判断以避免 wait() 虚假唤醒问题
                while (value == 1) {
                    lock.wait();
                }

                value++;
                log.debug("{} value++: {}", Thread.currentThread().getName(), value);
                lock.notifyAll();
            }
        }

        void decrement() throws InterruptedException {
            // 需要先给 lock 对象上锁才能够调用其 wait、notify、notifyAll 方法
            synchronized (lock) {
                // 注意：不能使用 if 判断以避免 wait() 虚假唤醒问题
                while (value == 0) {
                    lock.wait();
                }

                value--;
                log.debug("{} value--: {}", Thread.currentThread().getName(), value);
                lock.notifyAll();
            }
        }
    }
}
