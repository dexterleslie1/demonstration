package com.future.demo;

import cn.hutool.core.util.RandomUtil;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.redisson.api.RSet;
import org.redisson.api.RedissonClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.util.StopWatch;

import javax.annotation.Resource;
import java.util.*;

@SpringBootTest
@Slf4j
// 设置 active profile 为 test
@ActiveProfiles("test")
public class SetTests {

    @Resource
    RedissonClient redissonClient;

    /**
     * 测试基本用法
     */
    @Test
    public void testBasicUsage() {
        // region 测试 RSet

        String key = UUID.randomUUID().toString();
        RSet<Long> rSet = redissonClient.getSet(key);

        // RSet 没有元素时返回 null
        Long randomLong = rSet.random();
        Assertions.assertNull(randomLong);
        Set<Long> randomLongs = rSet.random(1000);
        Assertions.assertTrue(randomLongs.isEmpty());

        Long randomLong1 = RandomUtil.randomLong(0, Long.MAX_VALUE);
        rSet.add(randomLong1);
        Long randomLong2 = RandomUtil.randomLong(0, Long.MAX_VALUE);
        rSet.add(randomLong2);
        Assertions.assertEquals(2, rSet.size());
        randomLong = rSet.random();
        Assertions.assertTrue(Objects.equals(randomLong, randomLong1) || Objects.equals(randomLong, randomLong2));
        randomLongs = rSet.random(1000);
        Assertions.assertEquals(2, randomLongs.size());
        Assertions.assertTrue(randomLongs.toArray(new Long[]{})[0].equals(randomLong1) || randomLongs.toArray(new Long[]{})[0].equals(randomLong2));
        Assertions.assertTrue(randomLongs.toArray(new Long[]{})[1].equals(randomLong1) || randomLongs.toArray(new Long[]{})[1].equals(randomLong2));

        // endregion

        // region 测试 RSetCache

        // 注意：下面代码不能通过测试，因为 RSetCache 的 random 函数报错。
        /*key = UUID.randomUUID().toString();
        RSetCache<Long> rSetCache = redissonClient.getSetCache(key);
        randomLong1 = RandomUtil.randomLong(0, Long.MAX_VALUE);
        rSetCache.add(randomLong1);
        randomLong2 = RandomUtil.randomLong(0, Long.MAX_VALUE);
        rSetCache.add(randomLong2);
        Assertions.assertEquals(2, rSetCache.size());
        randomLong = rSetCache.random();
        Assertions.assertTrue(Objects.equals(randomLong, randomLong1) || Objects.equals(randomLong, randomLong2));
        randomLongs = rSetCache.random(1000);
        Assertions.assertEquals(2, randomLongs.size());
        Assertions.assertTrue(randomLongs.toArray(new Long[]{})[0].equals(randomLong1) || randomLongs.toArray(new Long[]{})[0].equals(randomLong2));
        Assertions.assertTrue(randomLongs.toArray(new Long[]{})[1].equals(randomLong1) || randomLongs.toArray(new Long[]{})[1].equals(randomLong2));*/

        // endregion
    }

    /**
     * 测试大数据集时
     */
    @Test
    public void testLargeDataset() {
        String key = UUID.randomUUID().toString();
        RSet<Long> rSet = redissonClient.getSet(key);
        int total = 500000;
        List<Long> batchList = new ArrayList<>();
        for (int i = 0; i < total; i++) {
            batchList.add((long) i);

            if (batchList.size() >= 1000) {
                rSet.addAll(batchList);
                batchList = new ArrayList<>();
            }
        }

        // 不应该使用 RSet.readAll() 方法读取大数据集，提示：在读取超大数据集时报告超时错误。
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        Set<Long> set = new HashSet<>(rSet.readAll());
        stopWatch.stop();
        Assertions.assertEquals(total, set.size());
        log.info("使用 RSet.readAll() 方法读取 {} 个元素耗时 {} 毫秒", total, stopWatch.getLastTaskTimeMillis());

        // 应该使用 iterator 读取大数据集，提示：虽然遍历过程慢，但是遍历超大数据集时不会报告超时错误。
        set = new HashSet<>();
        stopWatch.start();
        for (Long element : rSet) {
            set.add(element);
        }
        stopWatch.stop();
        Assertions.assertEquals(total, set.size());
        log.info("使用 RSet.iterator() 方法便利 {} 个元素耗时 {} 毫秒", total, stopWatch.getLastTaskTimeMillis());
    }
}
