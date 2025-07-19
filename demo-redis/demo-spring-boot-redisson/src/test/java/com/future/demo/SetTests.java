package com.future.demo;

import cn.hutool.core.util.RandomUtil;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.redisson.api.RSet;
import org.redisson.api.RedissonClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import javax.annotation.Resource;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

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
}
