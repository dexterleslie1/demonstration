package com.future.demo.awaitility;

import org.awaitility.Awaitility;
import org.awaitility.core.ConditionTimeoutException;
import org.junit.Assert;
import org.junit.Test;

import java.time.Duration;
import java.util.Date;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class Tests {
    @Test
    public void test() {
        long milliseconds = 5000;

        //region 测试等待成功

        AtomicInteger atomicInteger = new AtomicInteger();
        AtomicInteger finalAtomicInteger1 = atomicInteger;
        Thread thread = new Thread(() -> {
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            finalAtomicInteger1.incrementAndGet();
        });
        thread.start();

        // 最长等待5秒
        // 每1秒检查一次条件是否满足
        Awaitility.await().atMost(milliseconds, TimeUnit.MILLISECONDS).pollInterval(Duration.ofSeconds(1)).until(() -> {
            System.out.println(new Date());
            return finalAtomicInteger1.get() >= 1;
        });

        //endregion


        //region 测试等待失败

        atomicInteger = new AtomicInteger();
        AtomicInteger finalAtomicInteger = atomicInteger;
        thread = new Thread(() -> {
            try {
                Thread.sleep(10000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            finalAtomicInteger.incrementAndGet();
        });
        thread.start();

        try {
            Awaitility.await().atMost(milliseconds, TimeUnit.MILLISECONDS).pollInterval(Duration.ofSeconds(1)).until(() -> {
                return finalAtomicInteger.get() >= 1;
            });
            Assert.fail("预期异常没有抛出");
        } catch (ConditionTimeoutException ex) {

        }

        //endregion

    }
}
