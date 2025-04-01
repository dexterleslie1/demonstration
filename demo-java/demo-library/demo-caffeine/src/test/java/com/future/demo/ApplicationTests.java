package com.future.demo;

import com.github.benmanes.caffeine.cache.Cache;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

@SpringBootTest
class ApplicationTests {
    @Autowired
    Cache<String, String> caffeineInstance;
    @Autowired
    Cache<String, String> caffeineInstanceSizeBased;
    @Autowired
    Cache<String, String> caffeineInstanceTimeBased;

    @Test
    void contextLoads() throws InterruptedException {
        // region 测试 set 和 get

        String uuidStr = UUID.randomUUID().toString();
        this.caffeineInstance.put(uuidStr, uuidStr);
        String uuidStrResult = this.caffeineInstance.getIfPresent(uuidStr);
        Assertions.assertEquals(uuidStr, uuidStrResult);

        // 如果缓存中不存在会自动调用回调函数获取并自动存储到缓存中
        uuidStr = UUID.randomUUID().toString();
        uuidStrResult = this.caffeineInstance.get(uuidStr, key -> {
            // 如果缓存中不存在数据，这里的函数会被回调，在这里可以通过读取数据库加载数据，数据会被自动存储到 caffeine 缓存中
            return key + "-from-callback";
        });
        Assertions.assertEquals(uuidStr + "-from-callback", uuidStrResult);

        // endregion

        // region 测试驱逐策略
        // https://github.com/ben-manes/caffeine/wiki/Eviction-zh-CN

        // 基于容量
        String uuidStr1 = UUID.randomUUID().toString();
        String uuidStr2 = UUID.randomUUID().toString();
        this.caffeineInstanceSizeBased.put(uuidStr1, uuidStr1);
        this.caffeineInstanceSizeBased.put(uuidStr2, uuidStr2);
        TimeUnit.SECONDS.sleep(1);
        String uuidStrResult1 = this.caffeineInstanceSizeBased.getIfPresent(uuidStr1);
        String uuidStrResult2 = this.caffeineInstanceSizeBased.getIfPresent(uuidStr2);
        Assertions.assertNull(uuidStrResult1);
        Assertions.assertEquals(uuidStr2, uuidStrResult2);

        // 基于时间
        uuidStr1 = UUID.randomUUID().toString();
        uuidStr2 = UUID.randomUUID().toString();
        this.caffeineInstanceTimeBased.put(uuidStr1, uuidStr1);
        this.caffeineInstanceTimeBased.put(uuidStr2, uuidStr2);
        TimeUnit.MILLISECONDS.sleep(600);
        uuidStrResult1 = this.caffeineInstanceTimeBased.getIfPresent(uuidStr1);
        uuidStrResult2 = this.caffeineInstanceTimeBased.getIfPresent(uuidStr2);
        Assertions.assertEquals(uuidStr1, uuidStrResult1);
        Assertions.assertEquals(uuidStr2, uuidStrResult2);
        TimeUnit.MILLISECONDS.sleep(1100);
        uuidStrResult1 = this.caffeineInstanceTimeBased.getIfPresent(uuidStr1);
        uuidStrResult2 = this.caffeineInstanceTimeBased.getIfPresent(uuidStr2);
        Assertions.assertNull(uuidStrResult1);
        Assertions.assertNull(uuidStrResult2);

        // endregion
    }

}
