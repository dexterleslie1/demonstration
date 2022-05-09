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

            String value = jedis.get(key);
            Assert.assertEquals(key, value);

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
