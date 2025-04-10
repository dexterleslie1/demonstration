package com.future.demo;

import io.lettuce.core.RedisFuture;
import io.lettuce.core.ScriptOutputType;
import io.lettuce.core.cluster.api.async.RedisClusterAsyncCommands;
import io.lettuce.core.cluster.api.sync.RedisClusterCommands;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

@SpringBootTest(classes = {Application.class})
@Slf4j
public class ApplicationTests {
    @Resource
    RedisClusterCommands<String, String> sync;
    @Resource
    RedisClusterAsyncCommands<String, String> async;

    @Test
    public void test() throws InterruptedException {
        // 同步阻塞
        String key = UUID.randomUUID().toString();
        sync.set(key, key);
        String value = sync.get(key);
        Assertions.assertEquals(key, value);

        // 同步阻塞执行 Lua 脚本
        String scriptSha = sync.scriptLoad(Const.LuaScript);
        key = UUID.randomUUID().toString();
        String result = sync.evalsha(scriptSha, ScriptOutputType.STATUS, new String[]{key}, key);
        Assertions.assertEquals(key, result);

        key = UUID.randomUUID().toString();
        result = sync.eval(Const.LuaScript, ScriptOutputType.STATUS, new String[]{key}, key);
        Assertions.assertEquals(key, result);

        // 异步非阻塞
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
