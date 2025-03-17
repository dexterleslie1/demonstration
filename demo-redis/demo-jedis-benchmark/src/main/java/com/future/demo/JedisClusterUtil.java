package com.future.demo;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.JedisCluster;
import redis.clients.jedis.JedisPoolConfig;

import java.util.HashSet;
import java.util.Set;

@ConditionalOnProperty(value = "cluster", havingValue = "true")
@Component
public class JedisClusterUtil implements InitializingBean {
    private JedisCluster jedisCluster;

    public JedisCluster getJedisCluster() {
        return this.jedisCluster;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        int maxTotal = 600;
        int maxIdle = 300;
        int maxWaitMillis = 1000;
        boolean testOnBorrow = false;

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

        Set<HostAndPort> jedisClusterNodes = new HashSet<>();
        jedisClusterNodes.add(new HostAndPort("192.168.1.185", 6380));
        jedisClusterNodes.add(new HostAndPort("192.168.1.185", 6381));
        jedisClusterNodes.add(new HostAndPort("192.168.1.185", 6382));
        this.jedisCluster = new JedisCluster(jedisClusterNodes, config);
    }
}

