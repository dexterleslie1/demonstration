package com.future.demo;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.util.StopWatch;
import redis.clients.jedis.Jedis;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@SpringBootTest(classes = {Application.class})
@Slf4j
public class BenchmarkTests {
    @Autowired
    StringRedisTemplate redisTemplate;
    @Resource
    JedisUtil jedisUtil;

    /**
     * 基准测试 RedisTemplate
     *
     * @throws Exception
     */
    @Test
    public void testBenchmarkRedisTemplate() throws Exception {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();

        String key = UUID.randomUUID().toString();
        int totalRequests = 1000000;
        int concurrentThreads = 16;
        int eachThreadRunLoop = totalRequests / concurrentThreads;
        ExecutorService threadPool = Executors.newFixedThreadPool(concurrentThreads);
        List<CompletableFuture> completableFutureList = new ArrayList<>();
        for (int i = 0; i < concurrentThreads; i++) {
            completableFutureList.add(CompletableFuture.runAsync(() -> {
                for (int j = 0; j < eachThreadRunLoop; j++) {
                    redisTemplate.opsForValue().set(key, key);
                }
            }, threadPool));
        }

        CompletableFuture.allOf(completableFutureList.toArray(new CompletableFuture[]{})).get();

        stopWatch.stop();
        log.debug("{} 次 set 耗时 {} 毫秒", totalRequests, stopWatch.getTotalTimeMillis());
    }

    /**
     * 基准测试 Jedis
     *
     * @throws ExecutionException
     * @throws InterruptedException
     */
    @Test
    public void testBenchmarkJedis() throws ExecutionException, InterruptedException {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();

        String key = UUID.randomUUID().toString();
        int totalRequests = 1000000;
        int concurrentThreads = 16;
        int eachThreadRunLoop = totalRequests / concurrentThreads;
        ExecutorService threadPool = Executors.newFixedThreadPool(concurrentThreads);
        List<CompletableFuture> completableFutureList = new ArrayList<>();
        for (int i = 0; i < concurrentThreads; i++) {
            completableFutureList.add(CompletableFuture.runAsync(() -> {
                Jedis jedis = null;
                try {
                    jedis = this.jedisUtil.getJedis();
                    for (int i1 = 0; i1 < eachThreadRunLoop; i1++) {
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
