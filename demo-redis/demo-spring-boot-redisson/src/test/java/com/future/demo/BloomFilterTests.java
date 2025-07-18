package com.future.demo;

import cn.hutool.core.util.RandomUtil;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.redisson.api.RBloomFilter;
import org.redisson.api.RedissonClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.StopWatch;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

@SpringBootTest
@Slf4j
public class BloomFilterTests {
    @Resource
    RedissonClient redissonClient;

    /**
     * 基本用法
     */
    @Test
    public void test() {
        String uuidStr = UUID.randomUUID().toString();
        RBloomFilter<Long> bloomFilter = redissonClient.getBloomFilter(uuidStr);
        bloomFilter.delete();
        // 存1万个元素，误判率为1%
        bloomFilter.tryInit(10000L, 0.01);

        int total = 1000;
        for (long i = 0; i < total; i++) {
            Assertions.assertFalse(bloomFilter.contains(i), i + " 本应该不存在，但是却判断为存在");
        }

        for (long i = 0; i < total; i++) {
            boolean b = bloomFilter.add(i);
            Assertions.assertTrue(b, i + " 添加失败，因为判断为元素已经存在");
        }

        for (long i = 0; i < total; i++) {
            Assertions.assertTrue(bloomFilter.contains(i), i + " 本应该存在，但是却判断不存在");
        }

        bloomFilter.delete();
    }

    /**
     * 测试误判
     */
    @Test
    public void testFalsePositive() {
        String uuidStr = UUID.randomUUID().toString();
        RBloomFilter<Long> bloomFilter = redissonClient.getBloomFilter(uuidStr);
        bloomFilter.delete();
        bloomFilter.tryInit(10000L, 0.1);

        int total = 1000;
        for (long i = 0; i < total; i++) {
            // 演示无假阴性，判断不存在则实际一定不存在
            Assertions.assertFalse(bloomFilter.contains(i), i + " 本应该不存在，但是却判断为存在");
        }

        for (long i = 0; i < total; i++) {
            try {
                // 演示可能有假阳性，判断存在则实际不一定存在
                boolean b = bloomFilter.add(i);
                Assertions.assertTrue(b, i + " 添加失败，因为判断为元素已经存在");
                if (i == 492)
                    // 本应该492是假阳性的
                    Assertions.fail();
            } catch (AssertionError error) {
                Assertions.assertEquals("492 添加失败，因为判断为元素已经存在", error.getMessage());
            }
        }
    }

    /**
     * 测试插入元素总数超过预期元素总数5倍时的误判率
     */
    @Test
    public void testExpectedInsertionsOverflow() {
        String uuidStr = UUID.randomUUID().toString();
        RBloomFilter<Long> bloomFilter = redissonClient.getBloomFilter(uuidStr);
        long expectedInsertions = 100000L;
        AtomicInteger falsePositiveCounter = new AtomicInteger();
        bloomFilter.tryInit(expectedInsertions, 0.001);
        int times = 5;

        for (long i = 0; i < expectedInsertions * times; i++) {
            boolean b = bloomFilter.add(i);
            if (!b)
                falsePositiveCounter.incrementAndGet();
        }

        log.info("插入元素总数超过预期元素总数5倍时的误判率为 {}%", ((double) falsePositiveCounter.get() / (double) (expectedInsertions * times)) * 100d);
    }

    /**
     * 统计误判率
     */
    @Test
    public void testStatisticalFalsePositiveRate() throws InterruptedException {
        String uuidStr = UUID.randomUUID().toString();
        int concurrentThreads = 32;
        ExecutorService threadPool = Executors.newCachedThreadPool();
        long total = 1000000L;
        AtomicInteger counter = new AtomicInteger();
        AtomicInteger falseCounter = new AtomicInteger();
        RBloomFilter<Long> bloomFilter = redissonClient.getBloomFilter(uuidStr);
        bloomFilter.delete();
        bloomFilter.tryInit(total, 0.01);
        for (int i = 0; i < concurrentThreads; i++) {
            threadPool.submit(() -> {
                while (counter.getAndIncrement() <= total) {
                    long randomLong = RandomUtil.randomLong(0, Long.MAX_VALUE);
                    boolean b = bloomFilter.add(randomLong);
                    if (!b)
                        falseCounter.incrementAndGet();
                }
            });
        }
        threadPool.shutdown();
        while (!threadPool.awaitTermination(5, TimeUnit.MILLISECONDS)) ;

        log.info("使用 {} 个样本统计误判率为 {}%", total, ((double) falseCounter.get() / (double) total) * 100d);
    }

    /**
     * 测试add性能
     */
    @Test
    public void testPerfAdd() throws InterruptedException {
        StopWatch stopWatch = new StopWatch();
        String key = UUID.randomUUID().toString();
        int concurrentThreads = 32;
        ExecutorService threadPool = Executors.newCachedThreadPool();
        int total = 100000;
        AtomicInteger counter = new AtomicInteger();
        RBloomFilter<Long> bloomFilter = redissonClient.getBloomFilter(key);
        bloomFilter.tryInit(total * 2, 0.001);
        stopWatch.start();
        for (int i = 0; i < concurrentThreads; i++) {
            AtomicInteger finalCounter1 = counter;
            int finalTotal1 = total;
            RBloomFilter<Long> finalBloomFilter1 = bloomFilter;
            threadPool.submit(() -> {
                while (finalCounter1.getAndIncrement() <= finalTotal1) {
                    long randomLong = RandomUtil.randomLong(0, Long.MAX_VALUE);
                    finalBloomFilter1.add(randomLong);
                }
            });
        }
        threadPool.shutdown();
        while (!threadPool.awaitTermination(5, TimeUnit.MILLISECONDS)) ;
        stopWatch.stop();
        log.info("调用 add {} 次耗时 {} 毫秒", total, stopWatch.getTotalTimeMillis());
    }

    /**
     * 测试contain性能
     */
    @Test
    public void testPerfContains() throws InterruptedException {
        StopWatch stopWatch = new StopWatch();
        String key = UUID.randomUUID().toString();
        int concurrentThreads = 32;
        ExecutorService threadPool = Executors.newCachedThreadPool();
        int total = 100000;
        AtomicInteger counter = new AtomicInteger();
        RBloomFilter<Long> bloomFilter = redissonClient.getBloomFilter(key);
        bloomFilter.tryInit(total * 2, 0.001);
        stopWatch.start();
        for (int i = 0; i < concurrentThreads; i++) {
            AtomicInteger finalCounter = counter;
            int finalTotal = total;
            RBloomFilter<Long> finalBloomFilter = bloomFilter;
            threadPool.submit(() -> {
                while (finalCounter.getAndIncrement() <= finalTotal) {
                    long randomLong = RandomUtil.randomLong(0, Long.MAX_VALUE);
                    finalBloomFilter.contains(randomLong);
                }
            });
        }
        threadPool.shutdown();
        while (!threadPool.awaitTermination(5, TimeUnit.MILLISECONDS)) ;
        stopWatch.stop();
        log.info("调用 contains {} 次耗时 {} 毫秒", total, stopWatch.getTotalTimeMillis());
    }

    /**
     * 测试多个key分片add性能
     */
    @Test
    public void testPerfAddWithKeySharding() throws InterruptedException {
        StopWatch stopWatch = new StopWatch();
        String keyPrefix = UUID.randomUUID().toString();
        int totalKeyShards = 64;
        int concurrentThreads = 32;
        ExecutorService threadPool = Executors.newCachedThreadPool();
        int expectedInsertions = 2000000;
        int totalElement = 100000;
        AtomicInteger counter = new AtomicInteger();
        Map<Integer, RBloomFilter<Long>> keyShardToBloomFilterMap = new HashMap<>();
        for (int i = 0; i < totalKeyShards; i++) {
            String keyInternal = keyPrefix + ":" + i;
            RBloomFilter<Long> bloomFilter = redissonClient.getBloomFilter(keyInternal);
            bloomFilter.tryInit(expectedInsertions, 0.001);
            keyShardToBloomFilterMap.put(i, bloomFilter);
        }
        stopWatch.start();
        for (int i = 0; i < concurrentThreads; i++) {
            threadPool.submit(() -> {
                int count;
                while ((count = counter.getAndIncrement()) <= totalElement) {
                    int keyShard = count % totalKeyShards;
                    long randomLong = RandomUtil.randomLong(0, Long.MAX_VALUE);
                    keyShardToBloomFilterMap.get(keyShard).add(randomLong);
                }
            });
        }
        threadPool.shutdown();
        while (!threadPool.awaitTermination(5, TimeUnit.MILLISECONDS)) ;
        stopWatch.stop();
        log.info("多个key分片调用 add {} 次耗时 {} 毫秒", totalElement, stopWatch.getTotalTimeMillis());
    }
}
