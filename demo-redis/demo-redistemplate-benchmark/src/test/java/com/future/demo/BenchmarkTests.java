package com.future.demo;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.util.StopWatch;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@SpringBootTest(classes = {Application.class})
@Slf4j
public class BenchmarkTests {
    @Autowired
    StringRedisTemplate redisTemplate;

    /**
     * 基准测试 RedisTemplate
     *
     * @throws Exception
     */
    @Test
    public void testBenchmarkRedisTemplate() throws Exception {
        redisTemplate.getConnectionFactory().getConnection().flushDb();

        int totalKeySize = 100;
        List<String> keyList = new ArrayList<>();
        for (int i = 0; i < totalKeySize; i++) {
            keyList.add(UUID.randomUUID().toString());
        }

        Random random = new Random();

        StopWatch stopWatch = new StopWatch();
        stopWatch.start();

        int totalRequests = 1000000;
        int concurrentThreads = 64;
        int eachThreadRunLoop = totalRequests / concurrentThreads;
        ExecutorService threadPool = Executors.newFixedThreadPool(concurrentThreads);
        List<CompletableFuture> completableFutureList = new ArrayList<>();
        for (int i = 0; i < concurrentThreads; i++) {
            completableFutureList.add(CompletableFuture.runAsync(() -> {
                for (int j = 0; j < eachThreadRunLoop; j++) {
                    String key = keyList.get(random.nextInt(totalKeySize));
                    redisTemplate.opsForValue().set(key, key);
                }
            }, threadPool));
        }

        CompletableFuture.allOf(completableFutureList.toArray(new CompletableFuture[]{})).get();

        stopWatch.stop();
        log.debug("{} 次 set 耗时 {} 毫秒", totalRequests, stopWatch.getTotalTimeMillis());
    }
}
