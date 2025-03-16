package com.future.demo;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.StopWatch;
import redis.clients.jedis.Jedis;

import javax.annotation.Resource;
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
    @Resource
    JedisUtil jedisUtil;

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
            jedisTemporary = this.jedisUtil.getJedis();
            jedisTemporary.flushDB();
        } finally {
            this.jedisUtil.returnJedis(jedisTemporary);
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
                    jedis = this.jedisUtil.getJedis();
                    for (int j = 0; j < eachThreadRunLoop; j++) {
                        String key = keyList.get(random.nextInt(totalKeySize));
                        jedis.set(key, key);
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                } finally {
                    this.jedisUtil.returnJedis(jedis);
                }
            }, threadPool));
        }

        CompletableFuture.allOf(completableFutureList.toArray(new CompletableFuture[]{})).get();

        stopWatch.stop();
        System.out.println(totalRequests + " 次 set 耗时 " + stopWatch.getTotalTimeMillis() + " 毫秒");
    }
}
