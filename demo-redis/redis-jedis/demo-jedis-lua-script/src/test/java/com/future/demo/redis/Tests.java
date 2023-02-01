package com.future.demo.redis;

import org.junit.Assert;
import org.junit.Test;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.Transaction;
import redis.clients.jedis.exceptions.JedisDataException;

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

    @Test
    public void testLuaScriptError() {
        try (Jedis jedis = JedisUtil.getInstance().getJedis()) {
            jedis.flushDB();

            // 脚本在hset处中断执行
            String script = "" +
                    "redis.call(\"set\", \"k1\", \"v1\")" +
                    "redis.call(\"set\", \"k2\", \"v2\")" +
                    "redis.call(\"hset\", \"k1\", \"v1\")" +
                    "redis.call(\"set\", \"k3\", \"v3\")";
            try {
                jedis.eval(script, Collections.EMPTY_LIST, Collections.EMPTY_LIST);
                Assert.fail("预期异常没有抛出");
            } catch (JedisDataException ex) {

            }

            // 已经执行的命令依旧生效
            Assert.assertEquals("v1", jedis.get("k1"));
            Assert.assertEquals("v2", jedis.get("k2"));
            Assert.assertFalse(jedis.exists("k3"));
        }
    }
}
