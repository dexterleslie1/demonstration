package com.future.demo;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@SpringBootTest(classes = {Application.class})
public class ApplicationTests {

    @Autowired
    private RedisTemplate redisTemplate = null;

    @Test
    public void test() {
        String key = "demo-key-set-type";
        this.redisTemplate.delete(key);

        int total = 100;
        // sadd：添加元素
        // smember：获取集合的所有元素，结果是无序的
        List<String> expectedList = new ArrayList<>();
        for (int i = 0; i < total; i++) {
            this.redisTemplate.opsForSet().add(key, String.valueOf(i));
            expectedList.add(String.valueOf(i));
        }
        List<String> actualList = new ArrayList<>(this.redisTemplate.opsForSet().members(key));
        actualList = actualList.stream().sorted(Comparator.comparingInt(Integer::parseInt)).collect(Collectors.toList());
        Assertions.assertEquals(expectedList, actualList);

        // scard：获取集合长度
        int size = this.redisTemplate.opsForSet().size(key).intValue();
        Assertions.assertEquals(total, size);

        // sismember：查看元素是否在集合中，在则返回1，不在集合中或者集合不存在则返回0
        Boolean isMember = this.redisTemplate.opsForSet().isMember(key, String.valueOf(1000000));
        Assertions.assertFalse(isMember);
        isMember = this.redisTemplate.opsForSet().isMember(key, String.valueOf(20));
        Assertions.assertTrue(isMember);

        // srem：删除元素
        long count = this.redisTemplate.opsForSet().remove(key, String.valueOf(20));
        Assertions.assertEquals(1, count);
        isMember = this.redisTemplate.opsForSet().isMember(key, String.valueOf(20));
        Assertions.assertFalse(isMember);

        // srandmember：随机从集合中获取一个元素，不删除元素
        Object randomMember = this.redisTemplate.opsForSet().randomMember(key);
        isMember = this.redisTemplate.opsForSet().isMember(key, randomMember);
        Assertions.assertTrue(expectedList.contains(randomMember));
        Assertions.assertTrue(isMember);

        // spop：从集合中移除并返回随机数量的元素
        Object pop = this.redisTemplate.opsForSet().pop(key);
        isMember = this.redisTemplate.opsForSet().isMember(key, pop);
        Assertions.assertTrue(expectedList.contains(pop));
        Assertions.assertFalse(isMember);

        //region 测试set和get

        for (int i = 0; i <= 15; i++) {
            this.redisTemplate.opsForValue().set("key" + i, "value" + i);
            String value = (String) this.redisTemplate.opsForValue().get("key" + i);
            Assertions.assertEquals("value" + i, value);
        }

        //endregion

        // region 用于协助测试set Integer类型值是否会报告错误

        String uuidStr = UUID.randomUUID().toString();
        redisTemplate.opsForValue().set(uuidStr, Integer.valueOf(1));

        // endregion
    }
}
