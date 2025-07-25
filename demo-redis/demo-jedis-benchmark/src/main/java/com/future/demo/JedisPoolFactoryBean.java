package com.future.demo;

import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.stereotype.Component;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

@Component
//@ConditionalOnProperty(value = "spring.redis.host")
@ConditionalOnExpression("!T(org.springframework.util.StringUtils).isEmpty('${spring.redis.host:}')")
public class JedisPoolFactoryBean implements FactoryBean<JedisPool>, DisposableBean {
    @Value("${spring.redis.host}")
    String springRedisHost;
    @Value("${spring.redis.port}")
    int springRedisPort;
    @Value("${spring.redis.password}")
    String springRedisPassword;

    JedisPool jedisPool;

    @Override
    public JedisPool getObject() {
        int maxTotal = 600;
        int maxIdle = 300;
        int maxWaitMillis = 1000;
        boolean testOnBorrow = false;
        int timeout = 10000;

        JedisPoolConfig config = new JedisPoolConfig();
        config.setJmxEnabled(false);
        //控制一个pool可分配多少个jedis实例，通过pool.getResource()来获取；
        //如果赋值为-1，则表示不限制；如果pool已经分配了maxActive个jedis实例，则此时pool的状态为exhausted(耗尽)。
        config.setMaxTotal(maxTotal);
        //控制一个pool最多有多少个状态为idle(空闲的)的jedis实例。
        config.setMaxIdle(maxIdle);
        //表示当borrow(引入)一个jedis实例时，最大的等待时间，如果超过等待时间，则直接抛出JedisConnectionException；
        config.setMaxWaitMillis(maxWaitMillis);
        //在borrow一个jedis实例时，是否提前进行validate操作；如果为true，则得到的jedis实例均是可用的；
        config.setTestOnBorrow(testOnBorrow);

        jedisPool = new JedisPool(config, this.springRedisHost, this.springRedisPort, timeout, this.springRedisPassword);
        return jedisPool;
    }

    @Override
    public Class<?> getObjectType() {
        return JedisPool.class;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }

    @Override
    public void destroy() {
        if (this.jedisPool != null) {
            this.jedisPool.close();
            this.jedisPool = null;
        }
    }
}
