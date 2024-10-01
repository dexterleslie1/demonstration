package com.future.demo;

import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class AssistantService {
    private final static Random RANDOM = new Random();

    /**
     * 用于研究-Xss参数
     *
     * @throws InterruptedException
     */
    public void investigateXss() throws InterruptedException {
        System.out.println("开始调用investigateXss...");
        ExecutorService executorService = Executors.newCachedThreadPool();
        // 创建256个线程
        for (int i = 0; i < 256; i++) {
            executorService.submit(() -> {
                // 每个线程函数递归调用的深度
                // 207942这个值是通过测试-Xss10m时得出的
                int maxDepth = 207942;
                try {
                    investigateXssRecursion(maxDepth);
                } catch (StackOverflowError e) {
                    System.out.println("Stack overflow occurred in thread " + Thread.currentThread().getName());
                }
            });
        }
        executorService.shutdown();
        while (!executorService.awaitTermination(3600, TimeUnit.SECONDS)) ;
    }

    private void investigateXssRecursion(int depth) {
        if (depth > 0) {
            // 递归调用
            investigateXssRecursion(depth - 1);
        } else {
            // 达到递归深度后不退出函数，以便测试分析
            try {
                Thread.sleep(3600 * 1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    /**
     * 用于研究内存使用峰值
     *
     * @throws InterruptedException
     */
    public void investigateMemoryAllocationPeak() throws InterruptedException {
        System.out.println("开始调用investigateMemoryAllocation...");
        ExecutorService executorService = Executors.newCachedThreadPool();
        // 创建256个线程
        for (int i = 0; i < 256; i++) {
            executorService.submit(new Runnable() {
                @Override
                public void run() {
                    // 每个线程函数递归调用的深度为1024
                    int maximumDepth = 2 * 1024;
                    try {
                        investigateMemoryAllocationRecursionPeak(maximumDepth, 1);
                    } catch (StackOverflowError e) {
                        System.out.println("Stack overflow occurred in thread " + Thread.currentThread().getName());
                    }
                }
            });
        }
        executorService.shutdown();
        while (!executorService.awaitTermination(10, TimeUnit.SECONDS)) ;
    }

    private void investigateMemoryAllocationRecursionPeak(int maximumDepth, int currentDepth) {
        byte[] allocByteArray = new byte[1024];
        RANDOM.nextBytes(allocByteArray);

        if (currentDepth >= maximumDepth) {
            try {
                Thread.sleep(3600 * 1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        } else {
            investigateMemoryAllocationRecursionPeak(maximumDepth, currentDepth + 1);
        }

        // 这里需要引用allocByteArray，否则上面allocByteArray会被jvm优化在年轻代gc就被回收
        int length = allocByteArray.length;
    }

    /**
     * @throws InterruptedException
     */
    public void investigateMemoryAllocation() throws InterruptedException {
        System.out.println("开始调用investigateMemoryAllocation...");
        ExecutorService executorService = Executors.newCachedThreadPool();
        // 创建多个线程
        for (int i = 0; i < 128; i++) {
            executorService.submit(() -> {
                // 每个线程函数递归调用的深度
                int maximumDepth = 4096;
                try {
                    while (true)
                        investigateMemoryAllocationRecursion(maximumDepth, 1);
                } catch (StackOverflowError e) {
                    System.out.println("Stack overflow occurred in thread " + Thread.currentThread().getName());
                }
            });
        }
        executorService.shutdown();
        while (!executorService.awaitTermination(10, TimeUnit.SECONDS)) ;
    }

    private final static Integer INTEGER = 10;

    private void investigateMemoryAllocationRecursion(int maximumDepth, int currentDepth) {
        byte[] allocByteArray = new byte[1024];
        RANDOM.nextBytes(allocByteArray);

        int randomInt = RANDOM.nextInt(INTEGER);
        if (randomInt > 0) {
            try {
                Thread.sleep(randomInt);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

        if (currentDepth < maximumDepth)
            investigateMemoryAllocationRecursion(maximumDepth, currentDepth + 1);

        randomInt = RANDOM.nextInt(INTEGER);
        if (randomInt > 0) {
            try {
                Thread.sleep(randomInt);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

        // 这里需要引用allocByteArray，否则上面allocByteArray会被jvm优化在年轻代gc就被回收
        int length = allocByteArray.length;
    }
}
