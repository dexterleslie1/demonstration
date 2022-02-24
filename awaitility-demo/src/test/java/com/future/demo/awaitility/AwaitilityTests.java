package com.future.demo.awaitility;

import org.awaitility.Awaitility;
import org.junit.Test;

import java.time.Duration;
import java.util.Date;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class AwaitilityTests {
    @Test
    public void test() {
        long milliseconds = 5000;

        AtomicInteger atomicInteger = new AtomicInteger();
        Thread thread = new Thread(() -> {
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            atomicInteger.incrementAndGet();
        });
        thread.start();

        Awaitility.await().atMost(milliseconds, TimeUnit.MILLISECONDS).pollInterval(Duration.ofSeconds(1)).until(() -> {
            System.out.println(new Date());
            return atomicInteger.get() >= 1;
        });
    }
}
