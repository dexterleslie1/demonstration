package com.future.demo.robot.detection;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Configuration
public class ConfigRedisson {
    @Value("${redis.host}")
    private String redisHost;

    @Bean(destroyMethod = "shutdown")
    RedissonClient redissonClient() {
        Config config = new Config();
        List<String> stringList = Arrays.stream(this.redisHost.split(",")).map(redisHost -> "redis://" + redisHost).collect(Collectors.toList());
        config.useClusterServers().addNodeAddress(stringList.toArray(new String[] {}));
        return Redisson.create(config);
    }
}
