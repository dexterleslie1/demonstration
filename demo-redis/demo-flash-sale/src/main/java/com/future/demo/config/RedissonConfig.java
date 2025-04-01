package com.future.demo.config;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RedissonConfig {

    @Bean
    RedissonClient redisson() {
        Config config = new Config();
        config.useSingleServer().setAddress("redis://localhost:6379").setPassword("123456");

        /*Config config = new Config();
        config.useClusterServers()
                .addNodeAddress("redis://localhost:6380"
                        , "redis://localhost:6381"
                        , "redis://localhost:6382"
                        , "redis://localhost:6383"
                        , "redis://localhost:6384"
                        , "redis://localhost:6385");*/
        return Redisson.create(config);
    }
}
