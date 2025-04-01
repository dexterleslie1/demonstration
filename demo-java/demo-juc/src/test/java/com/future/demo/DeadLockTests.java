package com.future.demo;

import org.junit.Test;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class DeadLockTests {
    /**
     * 演示三条线程死锁情况
     */
    @Test
    public void test() {
        ExecutorService executor = Executors.newCachedThreadPool();

        Object object1 = new Object();
        Object object2 = new Object();
        Object object3 = new Object();

        CompletableFuture<Void> completableFuture1 = CompletableFuture.runAsync(() -> {
            synchronized (object1) {
                try {
                    TimeUnit.SECONDS.sleep(1);
                } catch (InterruptedException ignored) {
                }
                synchronized (object2) {
                    try {
                        TimeUnit.SECONDS.sleep(1);
                    } catch (InterruptedException ignored) {
                    }
                    synchronized (object3) {
                        try {
                            TimeUnit.SECONDS.sleep(1);
                        } catch (InterruptedException ignored) {
                        }
                    }
                }
            }
        }, executor);
        CompletableFuture<Void> completableFuture2 = CompletableFuture.runAsync(() -> {
            synchronized (object2) {
                try {
                    TimeUnit.SECONDS.sleep(1);
                } catch (InterruptedException ignored) {
                }
                synchronized (object1) {
                    try {
                        TimeUnit.SECONDS.sleep(1);
                    } catch (InterruptedException ignored) {
                    }
                    synchronized (object3) {
                        try {
                            TimeUnit.SECONDS.sleep(1);
                        } catch (InterruptedException ignored) {
                        }
                    }
                }
            }
        }, executor);
        CompletableFuture<Void> completableFuture3 = CompletableFuture.runAsync(() -> {
            synchronized (object3) {
                try {
                    TimeUnit.SECONDS.sleep(1);
                } catch (InterruptedException ignored) {
                }
                synchronized (object2) {
                    try {
                        TimeUnit.SECONDS.sleep(1);
                    } catch (InterruptedException ignored) {
                    }
                    synchronized (object1) {
                        try {
                            TimeUnit.SECONDS.sleep(1);
                        } catch (InterruptedException ignored) {
                        }
                    }
                }
            }
        }, executor);
        CompletableFuture.allOf(completableFuture1, completableFuture2, completableFuture3).join();

        executor.shutdown();
    }
}
