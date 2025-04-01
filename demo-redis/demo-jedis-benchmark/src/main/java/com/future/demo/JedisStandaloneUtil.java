package com.future.demo;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

/**
 * Jedis 工具类
 */
@ConditionalOnProperty(value = "cluster", havingValue = "false", matchIfMissing = true)
@Component
public class JedisStandaloneUtil implements InitializingBean {
    @Value("${spring.redis.host:localhost}")
    String host;
    @Value("${spring.redis.port:6379}")
    int port;
    @Value("${spring.redis.password:123456}")
    String password;

    private JedisPool jedisPool = null;

    /**
     * 从jedis连接池中获取获取jedis对象
     *
     * @return
     */
    public Jedis getJedis() {
        return jedisPool.getResource();
    }

    /**
     * 回收jedis(放到finally中)
     *
     * @param jedis
     */
    public void returnJedis(Jedis jedis) {
        if (null != jedis) {
            jedis.close();
        }
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        int maxTotal = 600;
        int maxIdle = 300;
        int maxWaitMillis = 1000;
        boolean testOnBorrow = false;
        int timeout = 10000;

        JedisPoolConfig config = new JedisPoolConfig();
        //控制一个pool可分配多少个jedis实例，通过pool.getResource()来获取；
        //如果赋值为-1，则表示不限制；如果pool已经分配了maxActive个jedis实例，则此时pool的状态为exhausted(耗尽)。
        config.setMaxTotal(maxTotal);
        //控制一个pool最多有多少个状态为idle(空闲的)的jedis实例。
        config.setMaxIdle(maxIdle);
        //表示当borrow(引入)一个jedis实例时，最大的等待时间，如果超过等待时间，则直接抛出JedisConnectionException；
        config.setMaxWaitMillis(maxWaitMillis);
        //在borrow一个jedis实例时，是否提前进行validate操作；如果为true，则得到的jedis实例均是可用的；
        config.setTestOnBorrow(testOnBorrow);

        jedisPool = new JedisPool(config, host, port, timeout, password);
    }
}

