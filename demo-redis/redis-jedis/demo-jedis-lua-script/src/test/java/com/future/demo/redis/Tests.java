package com.future.demo.redis;

import org.junit.Assert;
import org.junit.Test;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.Transaction;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class Tests {
    @Test
    public void test() {
        try (Jedis jedis = JedisUtil.getInstance().getJedis()) {
            jedis.flushDB();

            String script = "return \"hello \" .. KEYS[1] .. \" \" .. ARGV[1]";
            Object object = jedis.eval(script, Collections.singletonList("redis"), Collections.singletonList("world"));
            Assert.assertEquals("hello redis world", object.toString());
        }
    }
}
