package com.future.demo;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;

import java.util.Arrays;
import java.util.Objects;

@SpringBootTest(classes = {Application.class})
public class ApplicationTests {
    @Autowired
    StringRedisTemplate redisTemplate;

    @Test
    public void contextLoads() {
        // region 测试 Lua 脚本文件
        // 清空 db
        Objects.requireNonNull(this.redisTemplate.getConnectionFactory()).getConnection().flushDb();

        DefaultRedisScript<String> script = new DefaultRedisScript<>();
        script.setLocation(new ClassPathResource("1.lua"));
        script.setResultType(String.class);
        String str = this.redisTemplate.execute(script, Arrays.asList("k1", "k2"), "v1", "v2");
        Assertions.assertEquals("k1 k2 v1 v2", str);
        Assertions.assertEquals("v1", this.redisTemplate.opsForValue().get("k1"));
        Assertions.assertEquals("v2", this.redisTemplate.opsForValue().get("k2"));

        // endregion

        // region 测试 Lua 脚本字符串

        // 清空 db
        Objects.requireNonNull(this.redisTemplate.getConnectionFactory()).getConnection().flushDb();

        script = new DefaultRedisScript<>();
        script.setScriptText("redis.call('set', KEYS[1], ARGV[1])\n" +
                "redis.call('set', KEYS[2], ARGV[2])\n" +
                "return KEYS[1] .. ' ' .. KEYS[2] .. ' ' .. ARGV[1] .. ' ' .. ARGV[2]");
        script.setResultType(String.class);
        str = this.redisTemplate.execute(script, Arrays.asList("k1", "k2"), "v1", "v2");
        Assertions.assertEquals("k1 k2 v1 v2", str);
        Assertions.assertEquals("v1", this.redisTemplate.opsForValue().get("k1"));
        Assertions.assertEquals("v2", this.redisTemplate.opsForValue().get("k2"));

        // endregion
    }
}
