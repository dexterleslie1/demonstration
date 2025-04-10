package com.future.demo;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

public class JedisPoolUtil {
    /**
     * 从jedis连接池中获取获取jedis对象
     *
     * @return
     */
    public static Jedis getJedis(JedisPool jedisPool) {
        return jedisPool.getResource();
    }

    /**
     * 回收jedis(放到finally中)
     *
     * @param jedis
     */
    public static void returnJedis(Jedis jedis) {
        if (null != jedis) {
            jedis.close();
        }
    }
}

