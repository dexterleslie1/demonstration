package com.xy.future.demo.redis;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.*;
import java.util.stream.Collectors;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {Application.class})
public class Tests {

    @Autowired
    private RedisTemplate redisTemplate = null;

    @Test
    public void test() {
        String key = "demo-key-list-type";
        this.redisTemplate.delete(key);

        int total = 100;
        // lpush
        List<String> expectedList = new ArrayList<>();
        for(int i=total-1; i>=0; i--) {
            this.redisTemplate.opsForList().leftPush(key, String.valueOf(i));
            expectedList.add(0, String.valueOf(i));
        }
        List<String> actualList = this.redisTemplate.opsForList().range(key, 0, -1);
        Assert.assertEquals(expectedList, actualList);

        // llen
        long length = this.redisTemplate.opsForList().size(key);
        Assert.assertEquals(total, length);

        // rpop
        List<String> expectedListCopy = expectedList.stream().collect(Collectors.toList());
        for(int i=0; i<total/2; i++) {
            this.redisTemplate.opsForList().rightPop(key);
            expectedListCopy.remove(expectedListCopy.size()-1);
        }
        actualList = this.redisTemplate.opsForList().range(key, 0, -1);
        Assert.assertEquals(expectedListCopy, actualList);

        length = this.redisTemplate.opsForList().size(key);
        Assert.assertEquals(total/2, length);

        // range
        List<String> values = this.redisTemplate.opsForList().range(key, 0, -1);
        Assert.assertEquals(total/2, values.size());

        // ltrim：截取索引10到19之间的元素，包含10和19边界
        int startIndex = 10;
        int endIndex = 19;
        this.redisTemplate.opsForList().trim(key, startIndex, endIndex);
        length = this.redisTemplate.opsForList().size(key);
        Assert.assertEquals(endIndex-startIndex+1, length);
        List<String> list1 = new ArrayList<>();
        for(int i=startIndex; i<=endIndex; i++) {
            list1.add(String.valueOf(i));
        }
        values = this.redisTemplate.opsForList().range(key, 0, -1);
        Assert.assertEquals(list1, values);
    }
}
