package com.future.demo;

import io.lettuce.core.ClientOptions;
import io.lettuce.core.ReadFrom;
import io.lettuce.core.resource.ClientResources;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.springframework.boot.autoconfigure.data.redis.LettuceClientConfigurationBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisSentinelConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettucePoolingClientConfiguration;
import org.springframework.data.redis.core.RedisTemplate;

@Configuration
public class RedisConfiguration {
    /*@Bean
    public RedisConnectionFactory redisConnectionFactory() {
        GenericObjectPoolConfig poolConfig = new GenericObjectPoolConfig();
        poolConfig.setMaxTotal(256);
        LettuceClientConfiguration clientConfig = LettucePoolingClientConfiguration.builder()
                // 在所有节点负载均衡读取
                // .readFrom(ReadFrom.ANY)
                // 从所有slave节点负载均衡读取
                // .readFrom(ReadFrom.ANY_REPLICA)
                // 随机选择一个slave节点读取
                .readFrom(ReadFrom.REPLICA_PREFERRED)
                .poolConfig(poolConfig)
                .clientResources(ClientResources.builder().build())
                .clientOptions(ClientOptions.builder().build())
                .build();

        RedisSentinelConfiguration sentinelConfig = new RedisSentinelConfiguration()
                .master("mymaster")
                .sentinel("localhost", 26381)
                .sentinel("localhost", 26380)
                .sentinel("localhost", 26379);
        *//*RedisSentinelConfiguration sentinelConfig = new RedisSentinelConfiguration()
                .master("mymaster")
                .sentinel("redis-sentinel-service", 26379);*//*
        return new LettuceConnectionFactory(sentinelConfig, clientConfig);
    }*/

    @Bean
    public LettuceClientConfigurationBuilderCustomizer lettuceClientConfigurationBuilderCustomizer() {
        // ReadFrom.ANY：从所有节点负载均衡读取
        // ReadFrom.MASTER：只从主节点读取
        // ReadFrom.MASTER_PREFERRED：优先从主节点读取，如果主节点不可用则从从节点读取
        // ReadFrom.REPLICA：只从从节点读取
        // ReadFrom.REPLICA_PREFERRED：优先从从节点读取，如果从节点不可用则从主节点读取
        return clientConfigurationBuilder -> clientConfigurationBuilder.readFrom(ReadFrom.REPLICA_PREFERRED);
    }
}
