package com.xy.demo.redis;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.util.*;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {Application.class})
public class Tests {

    @Autowired
    StringRedisTemplate redisTemplate = null;
    @Resource
    ObjectMapper objectMapper;

    @Test
    public void testHashPutAllAndMulGet() throws JsonProcessingException {
        int size = 10;
        Map<String, String> idToJSONMap = new HashMap<>();
        List<Object> ids = new ArrayList<>();
        for (int i = 1; i <= size; i++) {
            Map<String, Object> mapZD = new HashMap<String, Object>();
            mapZD.put("id", i);
            mapZD.put("field1", i);
            mapZD.put("field2", i * 10);
            idToJSONMap.put(String.valueOf(i), this.objectMapper.writeValueAsString(mapZD));
            ids.add(String.valueOf(i));
        }
        redisTemplate.opsForHash().putAll("key1", idToJSONMap);

        List<Object> listReturn = redisTemplate.opsForHash().multiGet("key1", ids);
        Assert.assertEquals(size, listReturn.size());
    }
}
