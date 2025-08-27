package com.future.demo;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.scheduling.concurrent.CustomizableThreadFactory;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.LocalDateTime;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class)
@Slf4j
public class ExecutorServiceTests {
    /**
     * 测试 ScheduledExecutorService
     */
    @Test
    public void testScheduledExecutorService() throws InterruptedException {
        ScheduledExecutorService executorService = Executors.newScheduledThreadPool(1);
        executorService.scheduleAtFixedRate(() -> {
            log.info("当前线程 {} 当前时间 {}", Thread.currentThread().getName(), LocalDateTime.now());
        }, 3, 1, TimeUnit.SECONDS);

        TimeUnit.SECONDS.sleep(5);

        executorService.shutdown();
        while (!executorService.awaitTermination(100, TimeUnit.MILLISECONDS)) ;

        // 自定义线程名称
        ThreadFactory threadFactory = new CustomizableThreadFactory("my-scheduled-task-");
        executorService = Executors.newScheduledThreadPool(1, threadFactory);
        executorService.scheduleAtFixedRate(() -> {
            log.info("任务 {} 当前线程 {} 当前时间 {}", 0, Thread.currentThread().getName(), LocalDateTime.now());
        }, 0, 1, TimeUnit.SECONDS);

        TimeUnit.SECONDS.sleep(5);

        executorService.shutdown();
        while (!executorService.awaitTermination(100, TimeUnit.MILLISECONDS)) ;

        // 同一个任务即使执行时间超过任务间隔时间也不会并发执行，只会并发执行不同的任务
        threadFactory = new CustomizableThreadFactory("my-scheduled-task-");
        executorService = Executors.newScheduledThreadPool(2, threadFactory);
        for (int i = 0; i < 2; i++) {
            int finalI = i;
            executorService.scheduleAtFixedRate(() -> {
                log.info("任务 {} 当前线程 {} 当前时间 {}", finalI, Thread.currentThread().getName(), LocalDateTime.now());
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    log.error(e.getMessage(), e);
                }
            }, 0, 1, TimeUnit.SECONDS);
        }

        TimeUnit.SECONDS.sleep(5);

        executorService.shutdown();
        while (!executorService.awaitTermination(100, TimeUnit.MILLISECONDS)) ;

        // 任务抛出 RuntimeException 后 ScheduledExecutorService 停止工作
        log.info("------------------------------ 任务抛出 RuntimeException 后 ScheduledExecutorService 停止工作 ------------------------------ ");
        threadFactory = new CustomizableThreadFactory("my-scheduled-task-");
        executorService = Executors.newScheduledThreadPool(1, threadFactory);
        executorService.scheduleAtFixedRate(() -> {
            log.info("任务 {} 当前线程 {} 当前时间 {}", 0, Thread.currentThread().getName(), LocalDateTime.now());
            boolean b = true;
            if (b) {
                throw new RuntimeException("xxx");
            }
        }, 0, 1, TimeUnit.SECONDS);

        TimeUnit.SECONDS.sleep(5);

        executorService.shutdown();
        while (!executorService.awaitTermination(100, TimeUnit.MILLISECONDS)) ;
    }
}
