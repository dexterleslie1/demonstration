package com.future.demo;

import org.junit.After;
import org.junit.Test;
import redis.clients.jedis.JedisCluster;

import java.io.IOException;
import java.util.UUID;

public class Tests {
    @Test
    public void test1() {
        for (int i = 0; i < 100; i++) {
            String key = UUID.randomUUID().toString();
            JedisUtil.getInstance().getJedis().set(key, key);
        }
    }

    @After
    public void teardown() throws IOException {
        JedisCluster jedisCluster = JedisUtil.getInstance().getJedis();
        if (jedisCluster != null) {
            jedisCluster.close();
        }
    }
}
