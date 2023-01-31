package com.future.demo.redis;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisClusterConfiguration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisNode;
import org.springframework.data.redis.connection.RedisSentinelConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;

/**
 *
 */
@Configuration
public class ConfigRedis {
    @Value("${redis.host}")
    public String redisHost;
    /**
     *
     * @return
     */
    @Bean
    public RedisConnectionFactory redisConnectionFactory(){
        RedisClusterConfiguration config = new RedisClusterConfiguration();
//        config.addClusterNode(new RedisNode(redisHost, 6380));
//        config.addClusterNode(new RedisNode(redisHost, 6381));
//        config.addClusterNode(new RedisNode(redisHost, 6382));
        config.addClusterNode(new RedisNode(redisHost, 6383));
        // 连接配置可以只配置部分节点等同于能够连接整个redis集群
//        config.addClusterNode(new RedisNode(redisHost, 6384));
//        config.addClusterNode(new RedisNode(redisHost, 6385));
        return new JedisConnectionFactory(config);
    }

    /**
     *
     * @return
     */
    @Bean
    public RedisTemplate redisTemplate(){
        RedisTemplate redisTemplate = new RedisTemplate();
        redisTemplate.setConnectionFactory(redisConnectionFactory());
        return redisTemplate;
    }
}
