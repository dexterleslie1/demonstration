package com.future.demo;

import io.lettuce.core.RedisFuture;
import io.lettuce.core.RedisURI;
import io.lettuce.core.cluster.RedisClusterClient;
import io.lettuce.core.cluster.api.StatefulRedisClusterConnection;
import io.lettuce.core.cluster.api.async.RedisClusterAsyncCommands;
import io.lettuce.core.cluster.api.sync.RedisClusterCommands;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Arrays;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

@SpringBootTest(classes = {Application.class})
@Slf4j
public class ApplicationClusterTests {

    RedisClusterClient clusterClient;
    StatefulRedisClusterConnection<String, String> clusterConnection;

    @BeforeEach
    public void before() {
        // 集群版 Redis 连接
        RedisURI node1 = RedisURI.create("localhost", 6380);
        RedisURI node2 = RedisURI.create("localhost", 6381);
        RedisURI node3 = RedisURI.create("localhost", 6382);
        clusterClient = RedisClusterClient.create(Arrays.asList(node1, node2, node3));
        clusterConnection = clusterClient.connect();
    }

    @AfterEach
    public void after() {
        if (clusterConnection != null) {
            clusterConnection.close();
            clusterConnection = null;
        }
        if (clusterClient != null) {
            clusterClient.shutdown();
            clusterClient = null;
        }
    }

    @Test
    public void test() throws InterruptedException {
        // 同步阻塞
        RedisClusterCommands<String, String> sync = clusterConnection.sync();
        String key = UUID.randomUUID().toString();
        sync.set(key, key);
        String value = sync.get(key);
        Assertions.assertEquals(key, value);

        // 异步非阻塞
        RedisClusterAsyncCommands<String, String> async = clusterConnection.async();
        key = UUID.randomUUID().toString();
        RedisFuture<String> redisFuture = async.set(key, key);
        String finalKey = key;
        CountDownLatch latch = new CountDownLatch(1);
        redisFuture.whenComplete((data, exception) -> {
            async.get(finalKey).whenComplete((dataInternal, exceptionInternal) -> {
                if (finalKey.equals(dataInternal)) {
                    latch.countDown();
                }
            }).exceptionally(exceptionInternal -> {
                if (exceptionInternal != null) {
                    exceptionInternal.printStackTrace();
                }
                return null;
            });
        }).exceptionally(exception -> {
            exception.printStackTrace();
            return null;
        });
        Assertions.assertTrue(latch.await(1, TimeUnit.SECONDS));
    }
}
