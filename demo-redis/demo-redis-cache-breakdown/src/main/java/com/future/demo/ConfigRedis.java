package com.future.demo;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.redisson.config.SingleServerConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;

@Configuration
public class ConfigRedis {
    @Value("${spring.redis.host}")
    private String redisHost = null;
    @Value("${spring.redis.port}")
    private int redisPort = 0;
    @Value("${spring.redis.password}")
    private String redisPassword = null;

    @Bean
    RedissonClient redissonClient() {
        Config config = new Config();
        SingleServerConfig singleServerConfig = config
                .useSingleServer()
                .setAddress("redis://" + redisHost + ":" + redisPort);
        if (!StringUtils.isEmpty(redisPassword)) {
            singleServerConfig.setPassword(redisPassword);
        }
        return Redisson.create(config);
    }
}
