package com.future.demo.config;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RedissonConfig {
    @Bean
    RedissonClient redissonClient() {
        // Redis Standalone 模式配置
        /*Config config = new Config();
        config.useSingleServer()
                .setAddress("redis://localhost:6379")
                .setPassword("123456");*/

        // Redis Replication 模式配置
        /*Config config = new Config();
        config.useReplicatedServers()
                .addNodeAddress(
                        "redis://localhost:6479"
                        , "redis://localhost:6480"
                        , "redis://localhost:6481");*/

        // Redis Sentinel 模式配置
        Config config = new Config();
        config.useSentinelServers()
                .setMasterName("mymaster")
                .addSentinelAddress(
                        "redis://localhost:26381",
                        "redis://localhost:26380",
                        "redis://localhost:26379");

        return Redisson.create(config);
    }
}
