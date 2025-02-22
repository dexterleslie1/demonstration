package com.future.demo;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomUtils;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.locks.InterProcessSemaphoreMutex;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.StopWatch;

import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@SpringBootTest
@Slf4j
public class PerfTests {

    @Autowired
    private CuratorFramework curatorFramework;

    @Test
    public void test() throws InterruptedException {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();

        ExecutorService executorService = Executors.newCachedThreadPool();

        for (int i = 0; i < 200; i++) {
            executorService.submit(new Runnable() {
                public void run() {
                    for (int j = 0; j < 1000; j++) {
                        InterProcessSemaphoreMutex lock = null;
                        try {
                            String uuid = UUID.randomUUID().toString();
                            lock = new InterProcessSemaphoreMutex(curatorFramework, uuid);
                            lock.acquire(50, TimeUnit.MILLISECONDS);

                            int randomMilliseconds = RandomUtils.nextInt(0, 50);
                            if (randomMilliseconds == 0) {
                                randomMilliseconds = 2;
                            }
                            Thread.sleep(randomMilliseconds);
                        } catch (Exception ex) {
                            if (lock != null && lock.isAcquiredInThisProcess()) {
                                try {
                                    lock.release();
                                } catch (Exception e) {
                                }
                            }
                            lock = null;
                        }
                    }
                }
            });
        }
        executorService.shutdown();
        while (!executorService.awaitTermination(100, TimeUnit.MILLISECONDS)) ;

        stopWatch.stop();
        log.info("耗时{}毫秒", stopWatch.getTotalTimeMillis());
    }
}
