package com.future.demo;

import io.lettuce.core.ClientOptions;
import io.lettuce.core.ReadFrom;
import io.lettuce.core.resource.ClientResources;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettucePoolingClientConfiguration;

@Configuration
public class RedisConfiguration {
    @Bean
    public RedisConnectionFactory redisConnectionFactory() {
        GenericObjectPoolConfig poolConfig = new GenericObjectPoolConfig();
        poolConfig.setMaxTotal(256);
        LettuceClientConfiguration clientConfig = LettucePoolingClientConfiguration.builder()
                // 在所有节点负载均衡读取
                // .readFrom(ReadFrom.ANY)
                // 从所有slave节点负载均衡读取
                //.readFrom(ReadFrom.ANY_REPLICA)
                // 随机选择一个slave节点读取
                 .readFrom(ReadFrom.REPLICA)
                // .readFrom(ReadFrom.REPLICA_PREFERRED)
                .poolConfig(poolConfig)
                .clientResources(ClientResources.builder().build())
                .clientOptions(ClientOptions.builder().build())
                .build();

        /*RedisStandaloneConfiguration serverConfig = new RedisStandaloneConfiguration("redis-service", 6379);*/
        // https://docs.spring.io/spring-data/redis/reference/redis/connection-modes.html
        RedisStandaloneConfiguration serverConfig = new RedisStandaloneConfiguration("localhost", 6379);
        /*serverConfig.setPassword("123456");*/
        return new LettuceConnectionFactory(serverConfig, clientConfig);
    }
}
