package com.future.demo.redis;

import org.junit.Assert;
import org.junit.Test;
import redis.clients.jedis.Jedis;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Tests {
    @Test
    public void test() throws InterruptedException {
        Jedis jedis = null;
        try {
            jedis = JedisUtil.getInstance().getJedis();

            String key = UUID.randomUUID().toString();
            jedis.setex(key, 2, key);

            Thread.sleep(1000);

            String value = jedis.get(key);
            Assert.assertEquals(key, value);

            // ttl函数获取key剩余的ttl秒数
            long seconds = jedis.ttl(key);
            Assert.assertEquals(1, seconds);

            Thread.sleep(2100);

            value = jedis.get(key);
            Assert.assertNull(value);
        } finally {
            if(jedis != null) {
                jedis.close();
                jedis = null;
            }
        }
    }
}
