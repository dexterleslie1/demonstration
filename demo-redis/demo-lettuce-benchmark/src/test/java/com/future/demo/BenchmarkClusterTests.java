package com.future.demo;

import io.lettuce.core.RedisFuture;
import io.lettuce.core.RedisURI;
import io.lettuce.core.cluster.RedisClusterClient;
import io.lettuce.core.cluster.api.StatefulRedisClusterConnection;
import io.lettuce.core.cluster.api.async.RedisClusterAsyncCommands;
import io.lettuce.core.cluster.api.sync.RedisClusterCommands;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.StopWatch;

import java.util.*;
import java.util.concurrent.*;

@SpringBootTest(classes = {Application.class})
@Slf4j
public class BenchmarkClusterTests {

    RedisClusterClient clusterClient;
    StatefulRedisClusterConnection<String, String> clusterConnection;

    @BeforeEach
    public void before() {
        // 集群版 Redis 连接
        RedisURI node1 = RedisURI.create("localhost", 6380);
        RedisURI node2 = RedisURI.create("localhost", 6381);
        RedisURI node3 = RedisURI.create("localhost", 6382);
        clusterClient = RedisClusterClient.create(Arrays.asList(node1, node2, node3));
        clusterConnection = clusterClient.connect();
    }

    @AfterEach
    public void after() {
        if (this.clusterConnection != null) {
            this.clusterConnection.close();
            this.clusterConnection = null;
        }
    }

    /**
     * 使用异步 api 基准测试 Redis
     *
     * @throws Exception
     */
    @Test
    public void testBenchmarkByUsingAsyncApi() throws Exception {
        RedisClusterCommands<String, String> sync = clusterConnection.sync();
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
        RedisClusterAsyncCommands<String, String> async = clusterConnection.async();
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
        RedisClusterCommands<String, String> sync = clusterConnection.sync();
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
