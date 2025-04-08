package com.future.demo.config;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ConfigRedisson {
    @Bean
    RedissonClient redissonClient() {
        // Redis Standalone 模式配置
        Config config = new Config();
        config.useSingleServer()
                .setAddress("redis://localhost:6379")
                .setPassword("123456");

        // Redis Replication 模式配置
        /*Config config = new Config();
        config.useReplicatedServers()
                .addNodeAddress(
                        "redis://localhost:6479"
                        , "redis://localhost:6480"
                        , "redis://localhost:6481");*/

        // Redis Sentinel 模式配置
        /*Config config = new Config();
        config.useSentinelServers()
                .setMasterName("mymaster")
                // 下面配置 Sentinel 节点
                .addSentinelAddress(
                        "redis://localhost:26579",
                        "redis://localhost:26580",
                        "redis://localhost:26581");*/

        // Redis Cluster 模式配置
        /*Config config = new Config();
        config.useClusterServers()
                .addNodeAddress(
                        "redis://localhost:6679",
                        "redis://localhost:6680",
                        "redis://localhost:6681",
                        "redis://localhost:6682",
                        "redis://localhost:6683",
                        "redis://localhost:6684");*/

        return Redisson.create(config);
    }
}
