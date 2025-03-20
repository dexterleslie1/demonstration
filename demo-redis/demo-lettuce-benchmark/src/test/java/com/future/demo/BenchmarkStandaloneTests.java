package com.future.demo;

import io.lettuce.core.RedisClient;
import io.lettuce.core.RedisFuture;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.async.RedisAsyncCommands;
import io.lettuce.core.api.sync.RedisCommands;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.StopWatch;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.*;

@SpringBootTest(classes = {Application.class})
@Slf4j
public class BenchmarkStandaloneTests {

    StatefulRedisConnection<String, String> connection;

    @BeforeEach
    public void before() {
        RedisClient client = RedisClient.create("redis://123456@localhost");
        connection = client.connect();
    }

    @AfterEach
    public void after() {
        if (this.connection != null) {
            this.connection.close();
            this.connection = null;
        }
    }

    /**
     * 使用异步 api 基准测试 Redis
     *
     * @throws Exception
     */
    @Test
    public void testBenchmarkByUsingAsyncApi() throws Exception {
        RedisCommands<String, String> sync = connection.sync();
        sync.flushdb();

        int totalKeySize = 100;
        List<String> keyList = new ArrayList<>();
        for (int i = 0; i < totalKeySize; i++) {
            keyList.add(UUID.randomUUID().toString());
        }

        Random random = new Random();

        StopWatch stopWatch = new StopWatch();
        stopWatch.start();

        int totalRequests = 10000 * 200;
        int futureBatchSize = 10000 * 5;
        Semaphore semaphore = new Semaphore(futureBatchSize);
        RedisAsyncCommands<String, String> async = connection.async();
        CountDownLatch latch = new CountDownLatch(totalRequests);
        for (int i = 0; i < totalRequests; i++) {
            semaphore.acquire();
            String key = keyList.get(random.nextInt(totalKeySize));
            RedisFuture<String> redisFuture = async.set(key, key);
            redisFuture.whenComplete((data, exception) -> {
                if ("OK".equals(data)) {
                    latch.countDown();
                    if (latch.getCount() % (futureBatchSize * 10) == 0) {
                        log.debug("{}", totalRequests - latch.getCount());
                    }
                }
                semaphore.release();
            });
        }
        Assertions.assertTrue(latch.await(5, TimeUnit.MINUTES));

        stopWatch.stop();
        log.debug("{} 次 set 耗时 {} 毫秒", totalRequests, stopWatch.getTotalTimeMillis());
    }

    /**
     * 使用同步 api 基准测试 Redis
     *
     * @throws Exception
     */
    @Test
    public void testBenchmarkByUsingSyncApi() throws Exception {
        RedisCommands<String, String> sync = connection.sync();
        sync.flushdb();

        int totalKeySize = 100;
        List<String> keyList = new ArrayList<>();
        for (int i = 0; i < totalKeySize; i++) {
            keyList.add(UUID.randomUUID().toString());
        }

        Random random = new Random();

        StopWatch stopWatch = new StopWatch();
        stopWatch.start();

        int totalRequests = 10000 * 100;
        int concurrentThreads = 64;
        int eachThreadRunLoop = totalRequests / concurrentThreads;
        ExecutorService threadPool = Executors.newFixedThreadPool(concurrentThreads);
        List<CompletableFuture> completableFutureList = new ArrayList<>();
        for (int i = 0; i < concurrentThreads; i++) {
            completableFutureList.add(CompletableFuture.runAsync(() -> {
                try {
                    for (int j = 0; j < eachThreadRunLoop; j++) {
                        String key = keyList.get(random.nextInt(totalKeySize));
                        sync.set(key, key);
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }, threadPool));
        }

        CompletableFuture.allOf(completableFutureList.toArray(new CompletableFuture[]{})).get();

        stopWatch.stop();
        log.debug("{} 次 set 耗时 {} 毫秒", totalRequests, stopWatch.getTotalTimeMillis());
    }
}
