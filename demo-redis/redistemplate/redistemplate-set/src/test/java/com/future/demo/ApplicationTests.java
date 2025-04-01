package com.future.demo;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {Application.class})
public class ApplicationTests {

    @Autowired
    private StringRedisTemplate redisTemplate = null;

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
        Assert.assertEquals(expectedList, actualList);

        // scard：获取集合长度
        int size = this.redisTemplate.opsForSet().size(key).intValue();
        Assert.assertEquals(total, size);

        // sismember：查看元素是否在集合中，在则返回1，不在集合中或者集合不存在则返回0
        Boolean isMember = this.redisTemplate.opsForSet().isMember(key, String.valueOf(1000000));
        Assert.assertFalse(isMember);
        isMember = this.redisTemplate.opsForSet().isMember(key, String.valueOf(20));
        Assert.assertTrue(isMember);

        // srem：删除元素
        long count = this.redisTemplate.opsForSet().remove(key, String.valueOf(20));
        Assert.assertEquals(1, count);
        isMember = this.redisTemplate.opsForSet().isMember(key, String.valueOf(20));
        Assert.assertFalse(isMember);

        // srandmember：随机从集合中获取一个元素，不删除元素
        Object randomMember = this.redisTemplate.opsForSet().randomMember(key);
        isMember = this.redisTemplate.opsForSet().isMember(key, randomMember);
        Assert.assertTrue(expectedList.contains(randomMember));
        Assert.assertTrue(isMember);

        // spop：从集合中移除并返回随机数量的元素
        Object pop = this.redisTemplate.opsForSet().pop(key);
        isMember = this.redisTemplate.opsForSet().isMember(key, pop);
        Assert.assertTrue(expectedList.contains(pop));
        Assert.assertFalse(isMember);

        //region 测试set和get

        for (int i = 0; i <= 15; i++) {
            this.redisTemplate.opsForValue().set("key" + i, "value" + i);
            String value = this.redisTemplate.opsForValue().get("key" + i);
            Assert.assertEquals("value" + i, value);
        }

        //endregion
    }
}
