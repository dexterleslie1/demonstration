package com.future.demo;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.StopWatch;
import redis.clients.jedis.Jedis;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@SpringBootTest(classes = {Application.class})
@Slf4j
public class BenchmarkTests {
    @Autowired(required = false)
    JedisStandaloneUtil jedisStandaloneUtil;
    @Autowired(required = false)
    JedisClusterUtil jedisClusterUtil;

    /**
     * 基准测试 Jedis
     *
     * @throws ExecutionException
     * @throws InterruptedException
     */
    @Test
    public void testBenchmarkJedis() throws ExecutionException, InterruptedException {
        Jedis jedisTemporary = null;
        try {
            jedisTemporary = this.jedisStandaloneUtil.getJedis();
            jedisTemporary.flushDB();
        } finally {
            this.jedisStandaloneUtil.returnJedis(jedisTemporary);
        }

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
                Jedis jedis = null;
                try {
                    jedis = this.jedisStandaloneUtil.getJedis();
                    for (int j = 0; j < eachThreadRunLoop; j++) {
                        String key = keyList.get(random.nextInt(totalKeySize));
                        jedis.set(key, key);
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                } finally {
                    this.jedisStandaloneUtil.returnJedis(jedis);
                }
            }, threadPool));
        }

        CompletableFuture.allOf(completableFutureList.toArray(new CompletableFuture[]{})).get();

        stopWatch.stop();
        System.out.println(totalRequests + " 次 set 耗时 " + stopWatch.getTotalTimeMillis() + " 毫秒");
    }

    /**
     * 基准测试 Jedis 集群
     *
     * @throws ExecutionException
     * @throws InterruptedException
     */
    @Test
    public void testBenchmarkJedisCluster() throws ExecutionException, InterruptedException {
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
                    this.jedisClusterUtil.getJedisCluster().set(key, key);
                }
            }, threadPool));
        }

        CompletableFuture.allOf(completableFutureList.toArray(new CompletableFuture[]{})).get();

        stopWatch.stop();
        System.out.println(totalRequests + " 次 set 耗时 " + stopWatch.getTotalTimeMillis() + " 毫秒");
    }
}
