package com.future.demo;

import com.future.demo.service.LargeTransactionService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {Application.class})
public class LargeTransactionServiceTests {
    @Resource
    LargeTransactionService largeTransactionService;

    /**
     * 执行大事务
     * 1、用于协助分析binlog_cache_size、binlog_cache_use、binlog_cache_disk_use值
     */
    @Test
    public void test() {
        largeTransactionService.execute(128);
    }

    /**
     * 用于协助分析大事务+binlog_cache_size设置值大对内存使用率的影响
     */
    @Test
    public void testMemoryUsage() throws InterruptedException {
        int concurrentThreads = 128;
        ExecutorService executorService = Executors.newCachedThreadPool();
        for (int i = 0; i < concurrentThreads; i++) {
            executorService.submit(() -> {
                try {
                    for (int j = 0; j <= 512; j++) {
                        this.largeTransactionService.execute(4096);
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            });
        }
        executorService.shutdown();
        while (!executorService.awaitTermination(1, TimeUnit.SECONDS)) ;
    }
}
