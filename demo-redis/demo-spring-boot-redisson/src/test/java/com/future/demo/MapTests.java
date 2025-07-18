package com.future.demo;

import cn.hutool.core.util.RandomUtil;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.redisson.api.LocalCachedMapOptions;
import org.redisson.api.RLocalCachedMap;
import org.redisson.api.RMap;
import org.redisson.api.RedissonClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.util.StopWatch;

import javax.annotation.Resource;
import java.util.Date;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

@SpringBootTest
@Slf4j
// 设置 active profile 为 test
@ActiveProfiles("test")
public class MapTests {
    private final static Random random = new Random();
    @Resource
    RedissonClient redisson;

    /**
     * 测试基本使用
     */
    @Test
    public void testBasicUsage() {
        // region 测试 RMap

        String uuidStr = UUID.randomUUID().toString();
        RMap<String, Long> map = redisson.getMap(uuidStr);
        Assertions.assertEquals(0, map.size());
        map.put("k1", RandomUtil.randomLong(0, Long.MAX_VALUE));
        map.put("k2", RandomUtil.randomLong(0, Long.MAX_VALUE));
        Assertions.assertEquals(2, map.size());

        // endregion

        // region 测试 RMapCache

        uuidStr = UUID.randomUUID().toString();
        RMap<String, Long> mapCache = redisson.getMapCache(uuidStr);
        Assertions.assertEquals(0, mapCache.size());
        mapCache.put("k1", RandomUtil.randomLong(0, Long.MAX_VALUE));
        mapCache.put("k2", RandomUtil.randomLong(0, Long.MAX_VALUE));
        Assertions.assertEquals(2, mapCache.size());

        // endregion

        // region 测试 RLocalCachedMap

        uuidStr = UUID.randomUUID().toString();
        RLocalCachedMap<String, Long> rLocalCachedMap = redisson.getLocalCachedMap(uuidStr, LocalCachedMapOptions.defaults());
        Assertions.assertEquals(0, rLocalCachedMap.size());
        rLocalCachedMap.put("k1", RandomUtil.randomLong(0, Long.MAX_VALUE));
        rLocalCachedMap.put("k2", RandomUtil.randomLong(0, Long.MAX_VALUE));
        Assertions.assertEquals(2, rLocalCachedMap.size());

        // endregion
    }

    /**
     * 测试 RMap 大量数据时 redis 的内存占用
     * 结论：10000000 个 long 类型的元素 redis 内存占用为 772mb
     */
    @Test
    public void testRMapMemoryUsage() throws InterruptedException {
        String key = "testRMapMemoryUsage";
        RMap<Long, Long> rMap = redisson.getMap(key);
        // 删除之前的数据
        rMap.delete();
        int totalElementCount = 10000000;
        AtomicInteger counter = new AtomicInteger();
        ExecutorService threadPool = Executors.newCachedThreadPool();
        int concurrentThreads = 32;
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        for (int i = 0; i < concurrentThreads; i++) {
            threadPool.submit(() -> {
                while (counter.getAndIncrement() < totalElementCount) {
                    Long randomLong = RandomUtil.randomLong(0, Long.MAX_VALUE);
                    rMap.put(randomLong, randomLong);
                }
            });
        }
        threadPool.shutdown();
        while (!threadPool.awaitTermination(10, TimeUnit.MILLISECONDS)) ;
        stopWatch.stop();
        log.info("向 map 设置 {} 个元素耗时 {} 毫秒", totalElementCount, stopWatch.getTotalTimeMillis());
    }

    @Test
    public void testPerf() throws InterruptedException {
        Date timeStart = new Date();
        int looperOutter = 200;
        final int looperInner = 2000;
        ExecutorService executorService = Executors.newCachedThreadPool();
        for (int i = 0; i < looperOutter; i++) {
            final int j = i;
            executorService.submit(new Runnable() {
                public void run() {
                    try {
                        for (int i = 0; i < looperInner; i++) {
                            String string1 = String.valueOf(j * looperInner + i);
                            RMap<String, String> rMap = redisson.getMap("redissonmap");
                            rMap.put(string1, string1);
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    } finally {
                    }
                }
            });
        }
        executorService.shutdown();
        while (!executorService.awaitTermination(1, TimeUnit.SECONDS)) ;
        System.out.println("redis缓存数据已准备好，准备进行读效率测试");

        final int max = looperOutter * looperInner;
        executorService = Executors.newCachedThreadPool();
        for (int i = 0; i < 250; i++) {
            final int j = i;
            executorService.submit(new Runnable() {
                public void run() {
                    try {
                        for (int i = 0; i < 10000; i++) {
                            int intTemp = random.nextInt(max);
                            RMap<String, String> rMap = redisson.getMap("redissonmap");
                            rMap.get(String.valueOf(intTemp));
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    } finally {
                    }
                }
            });
        }
        executorService.shutdown();
        while (!executorService.awaitTermination(1, TimeUnit.SECONDS)) ;
        Date timeEnd = new Date();
        long milliseconds = timeEnd.getTime() - timeStart.getTime();
        log.info("耗时" + milliseconds + "毫秒");
    }
}
