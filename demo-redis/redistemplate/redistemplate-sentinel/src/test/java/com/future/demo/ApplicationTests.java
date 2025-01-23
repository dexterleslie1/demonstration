package com.future.demo;

import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.UUID;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {Application.class})
public class ApplicationTests {
    @Autowired
    StringRedisTemplate redisTemplate;
    @Autowired
    TestComponent testComponent;

    @Test
    public void test() throws Exception {
        this.testComponent.test();
    }

    // Redis Sentinel 模式正常情况下不会发生主和从节点复制延迟
    @Test
    public void testMasterAndSlaveReplicationDelay() {
        int loopCount = 50000;
        for (int i = 0; i < loopCount; i++) {
            String uuidStr = UUID.randomUUID().toString();
            this.redisTemplate.opsForValue().set(uuidStr, uuidStr);
            String uuidStrResult = this.redisTemplate.opsForValue().get(uuidStr);
            Assertions.assertEquals(uuidStr, uuidStrResult);
        }
    }
}
