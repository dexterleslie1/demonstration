package com.future.demo.appropriate.datatype;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.future.demo.Application;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@SpringBootTest(classes = {Application.class})
public class IncrementalKeyValueStorageTests {

    @Autowired
    StringRedisTemplate redisTemplate;
    @Autowired
    ObjectMapper objectMapper;

    // 使用 string 类型存储 10w 个增量键值
    @Test
    public void testStoringByUsingStringDataType() {
        Objects.requireNonNull(this.redisTemplate.getConnectionFactory()).getConnection().flushDb();

        for (int i = 0; i < 100000; i++) {
            this.redisTemplate.opsForValue().set("id:" + i, "value" + i);
        }
    }

    // 使用 hash 类型存储 10w 个增量键值
    @Test
    public void testStoringByUsingHashDataType() {
        Objects.requireNonNull(this.redisTemplate.getConnectionFactory()).getConnection().flushDb();

        String hashKey = "test:hash";
        for (int i = 0; i < 100000; i++) {
            this.redisTemplate.opsForHash().put(hashKey, "id:" + i, "value" + i);
        }
    }

    // 使用 hash分片 存储 10w 个增量键值
    @Test
    public void testStoringByUsingHashDataTypeWithSlicing() throws JsonProcessingException {
        Objects.requireNonNull(this.redisTemplate.getConnectionFactory()).getConnection().flushDb();

        Map<String, String> map = new HashMap<>();
        for (int i = 0; i < 100000; i++) {
            map.put("id:" + (i % 100), "value" + i);

            if ((i + 1) % 100 == 0) {
                String hashKey = "test:hash:" + (i / 100);
                this.redisTemplate.opsForHash().putAll(hashKey, map);
                map = new HashMap<>();
            }
        }
    }
}
