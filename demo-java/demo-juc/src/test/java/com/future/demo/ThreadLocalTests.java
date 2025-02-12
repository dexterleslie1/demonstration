package com.future.demo;

import org.apache.commons.lang3.RandomUtils;
import org.junit.Assert;
import org.junit.Test;

import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class ThreadLocalTests {
    @Test
    public void test() throws InterruptedException {
        // region 不使用 ThreadLocal 多线程访问同一变量会错乱

        AtomicInteger flag = new AtomicInteger();
        VariableContainer variableContainer = new VariableContainer();

        ExecutorService threadPool = Executors.newCachedThreadPool();

        for (int i = 0; i < 10; i++) {
            AtomicInteger finalFlag1 = flag;
            threadPool.submit(() -> {
                String uuid = UUID.randomUUID().toString();
                variableContainer.setValue(uuid);

                int milliseconds = RandomUtils.nextInt(0, 1000);
                if (milliseconds == 0) {
                    milliseconds = 1;
                }
                try {
                    TimeUnit.MILLISECONDS.sleep(milliseconds);
                } catch (InterruptedException ignored) {

                }

                String result = variableContainer.getValue();
                try {
                    Assert.assertEquals(uuid, result);
                } catch (Throwable throwable) {
                    finalFlag1.incrementAndGet();
                }
            });
        }

        threadPool.shutdown();
        while (!threadPool.awaitTermination(1, TimeUnit.SECONDS)) ;

        Assert.assertTrue(flag.get() > 0);

        // endregion

        // region 使用 ThreadLocal 多线程访问同一变量不会错乱

        flag = new AtomicInteger();
        VariableContainerByUsingThreadLocal variableContainerByUsingThreadLocal = new VariableContainerByUsingThreadLocal();

        threadPool = Executors.newCachedThreadPool();

        for (int i = 0; i < 10; i++) {
            AtomicInteger finalFlag = flag;
            threadPool.submit(() -> {
                String uuid = UUID.randomUUID().toString();
                variableContainerByUsingThreadLocal.setValue(uuid);

                int milliseconds = RandomUtils.nextInt(0, 1000);
                if (milliseconds == 0) {
                    milliseconds = 1;
                }
                try {
                    TimeUnit.MILLISECONDS.sleep(milliseconds);
                } catch (InterruptedException ignored) {

                }

                String result = variableContainerByUsingThreadLocal.getValue();
                try {
                    Assert.assertEquals(uuid, result);
                } catch (Throwable throwable) {
                    finalFlag.incrementAndGet();
                }
            });
        }

        threadPool.shutdown();
        while (!threadPool.awaitTermination(1, TimeUnit.SECONDS)) ;

        Assert.assertTrue(flag.get() == 0);

        // endregion
    }

    public static class VariableContainer {
        private String value;

        public void setValue(String value) {
            this.value = value;
        }

        public String getValue() {
            return this.value;
        }
    }

    public static class VariableContainerByUsingThreadLocal {
        private ThreadLocal<String> threadLocal = new ThreadLocal<>();

        public void setValue(String value) {
            this.threadLocal.set(value);
        }

        public String getValue() {
            return this.threadLocal.get();
        }
    }
}
