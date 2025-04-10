package com.future.demo;

import io.lettuce.core.RedisFuture;
import io.lettuce.core.ScriptOutputType;
import io.lettuce.core.cluster.api.async.RedisClusterAsyncCommands;
import io.lettuce.core.cluster.api.sync.RedisClusterCommands;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.StopWatch;

import javax.annotation.Resource;
import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

@SpringBootTest(classes = {Application.class})
@Slf4j
public class BenchmarkTests {
    final static int KeyLength = 300;

    @Resource
    RedisClusterCommands<String, String> sync;
    @Resource
    RedisClusterAsyncCommands<String, String> async;

    /**
     * 使用异步 api 基准测试 Redis
     *
     * @throws Exception
     */
    @Test
    public void testBenchmarkByUsingAsyncApi() throws Exception {
        sync.flushdb();

        Random random = new Random();

        StopWatch stopWatch = new StopWatch();
        stopWatch.start();

        int totalRequests = 10000 * 200;
        int futureBatchSize = 10000 * 5;
        Semaphore semaphore = new Semaphore(futureBatchSize);
        CountDownLatch latch = new CountDownLatch(totalRequests);
        for (int i = 0; i < totalRequests; i++) {
            semaphore.acquire();
            int randInt = random.nextInt(KeyLength);
            String key = String.valueOf(randInt);
            RedisFuture<String> redisFuture = async.set(key, key);
            redisFuture.whenComplete((data, exception) -> {
                if ("OK".equals(data)) {
                    latch.countDown();
                    if (latch.getCount() % (futureBatchSize * 10) == 0) {
                        log.debug("{}", totalRequests - latch.getCount());
                    }
                }
                semaphore.release();
            }).exceptionally(throwable -> {
                throwable.printStackTrace();
                return null;
            });
        }
        Assertions.assertTrue(latch.await(5, TimeUnit.MINUTES));

        stopWatch.stop();
        log.debug("{} 次 set 耗时 {} 毫秒", totalRequests, stopWatch.getTotalTimeMillis());
    }

    /**
     * 使用异步 api 基准测试 Redis Lua 脚本
     *
     * @throws Exception
     */
    @Test
    public void testBenchmarkLuaScriptByUsingAsyncApi() throws Exception {
        sync.flushdb();

        Random random = new Random();

        StopWatch stopWatch = new StopWatch();
        stopWatch.start();

        int totalRequests = 10000 * 200;
        int futureBatchSize = 10000 * 5;
        Semaphore semaphore = new Semaphore(futureBatchSize);
        CountDownLatch latch = new CountDownLatch(totalRequests);
        for (int i = 0; i < totalRequests; i++) {
            semaphore.acquire();
            int randInt = random.nextInt(KeyLength);
            String key = String.valueOf(randInt);
            RedisFuture<String> redisFuture = async.eval(Const.LuaScript, ScriptOutputType.STATUS, new String[]{key}, key);
            redisFuture.whenComplete((data, exception) -> {
                if (key.equals(data)) {
                    latch.countDown();
                    if (latch.getCount() % (futureBatchSize * 10) == 0) {
                        log.debug("{}", totalRequests - latch.getCount());
                    }
                }
                semaphore.release();
            }).exceptionally(throwable -> {
                throwable.printStackTrace();
                return null;
            });
        }
        Assertions.assertTrue(latch.await(5, TimeUnit.MINUTES));

        stopWatch.stop();
        log.debug("{} 次 set 耗时 {} 毫秒", totalRequests, stopWatch.getTotalTimeMillis());
    }
}
