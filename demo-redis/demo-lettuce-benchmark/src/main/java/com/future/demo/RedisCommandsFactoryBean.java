package com.future.demo;

import io.lettuce.core.RedisClient;
import io.lettuce.core.RedisURI;
import io.lettuce.core.api.StatefulConnection;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.cluster.RedisClusterClient;
import io.lettuce.core.cluster.api.StatefulRedisClusterConnection;
import io.lettuce.core.cluster.api.sync.RedisClusterCommands;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

@Component
public class RedisCommandsFactoryBean implements FactoryBean<RedisClusterCommands<String, String>>, DisposableBean {
    @Value("${spring.redis.host:}")
    String springRedisHost;
    @Value("${spring.redis.port:6379}")
    int springRedisPort;
    @Value("${spring.redis.password:}")
    String springRedisPassword;

    @Value("${spring.redis.cluster.nodes:}")
    String springRedisClusterNodes;


    StatefulConnection<String, String> connection;
    RedisClusterCommands<String, String> redisCommands;

    @Override
    public RedisClusterCommands<String, String> getObject() {
        if (StringUtils.hasText(this.springRedisHost)) {
            String connectStr = String.format("redis://%s@%s:%d", springRedisPassword, springRedisHost, springRedisPort);
            RedisClient client = RedisClient.create(connectStr);
            connection = client.connect();
            redisCommands = ((StatefulRedisConnection<String, String>) connection).sync();
        } else {
            String[] split = this.springRedisClusterNodes.split(",");
            List<RedisURI> redisURIList = new ArrayList<>();
            for (String str : split) {
                String[] temporarySplit = str.split(":");
                RedisURI node = RedisURI.create(temporarySplit[0], Integer.parseInt(temporarySplit[1]));
                redisURIList.add(node);
            }
            RedisClusterClient client = RedisClusterClient.create(redisURIList);
            connection = client.connect();
            redisCommands = ((StatefulRedisClusterConnection<String, String>) connection).sync();
        }
        return redisCommands;
    }

    @Override
    public Class<?> getObjectType() {
        return RedisClusterCommands.class;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }

    @Override
    public void destroy() {
        if (this.connection != null) {
            this.connection.close();
            this.connection = null;
        }
    }
}
