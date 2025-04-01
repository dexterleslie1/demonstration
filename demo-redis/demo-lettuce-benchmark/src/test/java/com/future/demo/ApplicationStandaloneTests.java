package com.future.demo;

import io.lettuce.core.RedisClient;
import io.lettuce.core.RedisFuture;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.async.RedisAsyncCommands;
import io.lettuce.core.api.sync.RedisCommands;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

@SpringBootTest(classes = {Application.class})
@Slf4j
public class ApplicationStandaloneTests {

    RedisClient client;
    StatefulRedisConnection<String, String> connection;

    @BeforeEach
    public void before() {
        // 单机版 Redis 连接
        client = RedisClient.create("redis://123456@localhost:6379");
        connection = client.connect();
    }

    @AfterEach
    public void after() {
        if (this.connection != null) {
            this.connection.close();
            this.connection = null;
        }
        if (client != null) {
            client.shutdown();
            client = null;
        }
    }

    @Test
    public void test() throws InterruptedException {
        // 同步阻塞
        RedisCommands<String, String> sync = connection.sync();
        String key = UUID.randomUUID().toString();
        sync.set(key, key);
        String value = sync.get(key);
        Assertions.assertEquals(key, value);

        // 异步非阻塞
        RedisAsyncCommands<String, String> async = connection.async();
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
