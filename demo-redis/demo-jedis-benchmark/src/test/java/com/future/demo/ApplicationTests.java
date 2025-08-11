package com.future.demo;

import cn.hutool.core.util.RandomUtil;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.StopWatch;
import redis.clients.jedis.ConnectionPool;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisCluster;
import redis.clients.jedis.exceptions.JedisDataException;
import redis.clients.jedis.params.ScanParams;
import redis.clients.jedis.resps.ScanResult;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

@SpringBootTest
@Slf4j
public class ApplicationTests {

    @Autowired(required = false)
    JedisCluster jedisCluster;
    @Autowired
    PipelineTestingSupportService pipelineTestingSupportService;

    /**
     *
     */
    @Test
    public void contextLoads() throws InterruptedException {
        // region 测试 pipeline 没有异常情况

        String uuidStr1 = UUID.randomUUID().toString();
        String uuidStr2 = UUID.randomUUID().toString();
        try {
            PipelineContextHolder.setupPipeline(jedisCluster);

            PipelineContextHolder.set(uuidStr1, uuidStr1);
            PipelineContextHolder.hset(uuidStr2, uuidStr2, uuidStr2);
        } finally {
            PipelineContextHolder.closePipeline();
        }

        String value = jedisCluster.get(uuidStr1);
        Assertions.assertEquals(uuidStr1, value);
        value = jedisCluster.hget(uuidStr2, uuidStr2);
        Assertions.assertEquals(uuidStr2, value);

        // endregion

        // region 测试 pipeline 有异常情况

        uuidStr1 = UUID.randomUUID().toString();
        uuidStr2 = UUID.randomUUID().toString();
        try {
            PipelineContextHolder.setupPipeline(jedisCluster);

            PipelineContextHolder.set(uuidStr1, uuidStr1);
            PipelineContextHolder.hset(uuidStr1, uuidStr2, uuidStr2);
        } finally {
            try {
                PipelineContextHolder.closePipeline();
                Assertions.fail();
            } catch (JedisDataException ex) {
                Assertions.assertTrue(ex.getMessage().contains("hset"));
                Assertions.assertTrue(ex.getCause() instanceof JedisDataException);
                Assertions.assertEquals("WRONGTYPE Operation against a key holding the wrong kind of value", ex.getCause().getMessage());
            }
        }

        value = jedisCluster.get(uuidStr1);
        Assertions.assertEquals(uuidStr1, value);

        // endregion

        // region 测试 PipelineContextHolder 上下文工具并发线程间相互独立

        ConcurrentHashMap<Integer, String> countToUuidMap = new ConcurrentHashMap<>();
        AtomicInteger count = new AtomicInteger();
        int concurrentThreads = 32;
        int runLoop = 1000;
        ExecutorService threadPool = Executors.newCachedThreadPool();
        for (int i = 0; i < concurrentThreads; i++) {
            int finalRunLoop1 = runLoop;
            threadPool.submit(() -> {
                for (int j = 0; j < finalRunLoop1; j++) {
                    int countInternal = count.getAndIncrement();
                    String uuidStr = UUID.randomUUID().toString();
                    countToUuidMap.put(countInternal, uuidStr);

                    pipelineTestingSupportService
                            .testPipelineContextHolderThreadConcurrentIndependenceAll(
                                    countInternal, uuidStr);
                }
            });
        }
        threadPool.shutdown();
        while (!threadPool.awaitTermination(10, TimeUnit.MILLISECONDS)) ;

        Assertions.assertEquals(concurrentThreads * runLoop, countToUuidMap.size());
        for (int i = 0; i < count.get(); i++) {
            Assertions.assertTrue(countToUuidMap.containsKey(i), "不包含" + i);
        }
        for (Integer index : countToUuidMap.keySet()) {
            String key = "set" + index;
            Assertions.assertEquals(countToUuidMap.get(index), jedisCluster.get(key));
            key = "hset" + index;
            String field = "field" + index;
            Assertions.assertEquals(countToUuidMap.get(index), jedisCluster.hget(key, field));
        }

        // endregion

        // region 测试 pipeline 性能有多高

        // 非管道
        concurrentThreads = 8;
        runLoop = 20000;
        threadPool = Executors.newCachedThreadPool();
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        for (int i = 0; i < concurrentThreads; i++) {
            int finalRunLoop = runLoop;
            threadPool.submit(() -> {
                for (int j = 0; j < finalRunLoop; j++) {
                    String uuidStr = UUID.randomUUID().toString();
                    jedisCluster.set(uuidStr, uuidStr);
                }
            });
        }
        threadPool.shutdown();
        while (!threadPool.awaitTermination(10, TimeUnit.MILLISECONDS)) ;

        stopWatch.stop();
        log.info("使用非管道执行 {} 个 set 指令耗时 {} 毫秒", concurrentThreads * runLoop, stopWatch.getTotalTimeMillis());

        // 管道
        threadPool = Executors.newCachedThreadPool();
        stopWatch = new StopWatch();
        stopWatch.start();
        for (int i = 0; i < concurrentThreads; i++) {
            int finalRunLoop = runLoop;
            threadPool.submit(() -> {
                try {
                    PipelineContextHolder.setupPipeline(jedisCluster);
                    for (int j = 0; j < finalRunLoop; j++) {
                        String uuidStr = UUID.randomUUID().toString();
                        PipelineContextHolder.set(uuidStr, uuidStr);
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                    throw new RuntimeException(ex);
                } finally {
                    PipelineContextHolder.closePipeline();
                }
            });
        }
        threadPool.shutdown();
        while (!threadPool.awaitTermination(10, TimeUnit.MILLISECONDS)) ;

        stopWatch.stop();
        log.info("使用管道执行 {} 个 set 指令耗时 {} 毫秒", concurrentThreads * runLoop, stopWatch.getTotalTimeMillis());

        // endregion
    }

    /**
     * 测试 redis 集群中 keys 和 scan 方法
     */
    @Test
    public void testKeysAndScanAgainstRedisCluster() {
        jedisCluster.flushAll();

        int total = RandomUtil.randomInt(1, 20481);

        String keyPrefix = "testKeys#";
        for (int i = 0; i < total; i++) {
            String key = keyPrefix + i;
            jedisCluster.set(key, key);
        }

        // region 测试 keys 方法

        String pattern = keyPrefix + "*";
        Set<String> matchedKeys = new TreeSet<>();
        Map<String, ConnectionPool> clusterNodes = jedisCluster.getClusterNodes();
        for (String key : clusterNodes.keySet()) {
            Jedis jedis = null;
            try {
                jedis = new Jedis(clusterNodes.get(key).getResource());
                matchedKeys.addAll(jedis.keys(pattern));
            } finally {
                if (jedis != null)
                    jedis.close();
            }
        }
        Assertions.assertEquals(total, matchedKeys.size());
        for (int i = 0; i < total; i++) {
            String key = keyPrefix + i;
            Assertions.assertTrue(matchedKeys.contains(key));
        }

        // endregion

        // region 测试 scan 方法

        matchedKeys = new LinkedHashSet<>();
        // 每次SCAN获取的数量
        int scanBatchSize = 1000;
        clusterNodes = jedisCluster.getClusterNodes();

        for (Map.Entry<String, ConnectionPool> entry : clusterNodes.entrySet()) {
            // 初始化游标
            String cursor = ScanParams.SCAN_POINTER_START;
            ScanParams scanParams = new ScanParams().match(pattern).count(scanBatchSize);

            try (Jedis jedis = new Jedis(entry.getValue().getResource())) {
                do {
                    // 执行SCAN命令
                    ScanResult<String> scanResult = jedis.scan(cursor, scanParams);
                    matchedKeys.addAll(scanResult.getResult());
                    cursor = scanResult.getCursor();
                } while (!cursor.equals(ScanParams.SCAN_POINTER_START));
            }
        }
        Assertions.assertEquals(total, matchedKeys.size());
        for (int i = 0; i < total; i++) {
            String key = keyPrefix + i;
            Assertions.assertTrue(matchedKeys.contains(key));
        }

        // endregion
    }
}
