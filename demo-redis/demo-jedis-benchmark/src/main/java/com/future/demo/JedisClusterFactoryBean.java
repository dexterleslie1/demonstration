package com.future.demo;

import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.stereotype.Component;
import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.JedisCluster;

import java.time.Duration;
import java.util.HashSet;
import java.util.Set;

@Component
//@ConditionalOnProperty(value = "spring.redis.cluster.nodes")
@ConditionalOnExpression("!T(org.springframework.util.StringUtils).isEmpty('${spring.redis.cluster.nodes:}')")
public class JedisClusterFactoryBean implements FactoryBean<JedisCluster>, DisposableBean {
    @Value("${spring.redis.cluster.nodes}")
    String springRedisClusterNodes;

    JedisCluster jedisCluster;

    @Override
    public JedisCluster getObject() {
        int maxTotal = 600;
        int maxIdle = 300;
        int maxWaitMillis = 1000;
        boolean testOnBorrow = false;

//        JedisPoolConfig config = new JedisPoolConfig();
//        //控制一个pool可分配多少个jedis实例，通过pool.getResource()来获取；
//        //如果赋值为-1，则表示不限制；如果pool已经分配了maxActive个jedis实例，则此时pool的状态为exhausted(耗尽)。
//        config.setMaxTotal(maxTotal);
//        //控制一个pool最多有多少个状态为idle(空闲的)的jedis实例。
//        config.setMaxIdle(maxIdle);
//        //表示当borrow(引入)一个jedis实例时，最大的等待时间，如果超过等待时间，则直接抛出JedisConnectionException；
//        config.setMaxWaitMillis(maxWaitMillis);
//        //在borrow一个jedis实例时，是否提前进行validate操作；如果为true，则得到的jedis实例均是可用的；
//        config.setTestOnBorrow(testOnBorrow);

        GenericObjectPoolConfig config = new GenericObjectPoolConfig();
        config.setJmxEnabled(false);
        config.setMaxTotal(maxTotal);
        config.setMaxIdle(maxIdle);
        config.setMaxWait(Duration.ofMillis(maxWaitMillis));
        config.setTestOnBorrow(testOnBorrow);

        Set<HostAndPort> jedisClusterNodes = new HashSet<>();
        String[] split = this.springRedisClusterNodes.split(",");
        for (String str : split) {
            String[] temporarySplit = str.split(":");
            jedisClusterNodes.add(new HostAndPort(temporarySplit[0], Integer.parseInt(temporarySplit[1])));
        }
        jedisCluster = new JedisCluster(jedisClusterNodes, config);
        return jedisCluster;
    }

    @Override
    public Class<?> getObjectType() {
        return JedisCluster.class;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }

    @Override
    public void destroy() {
        if (this.jedisCluster != null) {
            this.jedisCluster.close();
            this.jedisCluster = null;
        }
    }
}
