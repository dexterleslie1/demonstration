package com.future.demo.config;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.ClusterServersConfig;
import org.redisson.config.Config;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;

@Configuration
public class ConfigRedisson {
    @Value("${spring.redis.host:}")
    String springRedisHost;
    @Value("${spring.redis.port:6379}")
    int springRedisPort;
    @Value("${spring.redis.password:}")
    String springRedisPassword;

    @Value("${spring.redis.cluster.nodes:}")
    String springRedisClusterNodes;

    @Bean
    RedissonClient redisson() {
        Config config;
        if (StringUtils.hasText(springRedisHost)) {
            config = new Config();
            String address = String.format("redis://%s:%d", this.springRedisHost, this.springRedisPort);
            config.useSingleServer().setAddress(address).setPassword(this.springRedisPassword);
        } else {
            config = new Config();
            ClusterServersConfig clusterServersConfig = config.useClusterServers();
            String[] split = this.springRedisClusterNodes.split(",");
            for (String str : split) {
                String[] temporarySplit = str.split(":");
                String address = String.format("redis://%s:%s", temporarySplit[0], temporarySplit[1]);
                clusterServersConfig.addNodeAddress(address);
            }
        }
        return Redisson.create(config);
    }
}
