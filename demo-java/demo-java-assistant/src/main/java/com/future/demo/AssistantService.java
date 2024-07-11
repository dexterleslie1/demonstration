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
        for (int i = 0; i < 256; i++) {
            executorService.submit(new Runnable() {
                @Override
                public void run() {
                    int maxDepth = 1024; // 你可以调整这个值来测试不同的栈大小
                    try {
                        investigateXssRecursion(maxDepth);
                    } catch (StackOverflowError e) {
                        System.out.println("Stack overflow occurred in thread " + Thread.currentThread().getName());
                    }
                }
            });
        }
        executorService.shutdown();
        while (!executorService.awaitTermination(3600, TimeUnit.SECONDS)) ;
    }

    private void investigateXssRecursion(int depth) {
        if (depth > 0) {
            // 增加局部变量以消耗更多栈空间（可选）
            byte[] largeArray = new byte[1024]; // 分配一个较大的数组作为局部变量
            RANDOM.nextBytes(largeArray);

            // 递归调用
            investigateXssRecursion(depth - 1);

            // 需要引用变量largeArray，否则变量在年轻代gc就被回收无法查看变量占用内存空间
            System.out.println(largeArray.length);
        } else {
            // 达到递归深度后不退出函数，以便测试分析
            try {
                Thread.sleep(3600 * 1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
