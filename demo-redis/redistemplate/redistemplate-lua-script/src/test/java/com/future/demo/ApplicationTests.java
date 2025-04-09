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
        // 使用 hash tag {x} 以解决在 redis 集群中 key 不在同一插槽报错问题
        String str = this.redisTemplate.execute(script, Arrays.asList("k1{x}", "k2{x}"), "v1", "v2");
        Assertions.assertEquals("k1{x} k2{x} v1 v2", str);
        Assertions.assertEquals("v1", this.redisTemplate.opsForValue().get("k1{x}"));
        Assertions.assertEquals("v2", this.redisTemplate.opsForValue().get("k2{x}"));

        // endregion

        // region 测试 Lua 脚本字符串

        // 清空 db
        Objects.requireNonNull(this.redisTemplate.getConnectionFactory()).getConnection().flushDb();

        script = new DefaultRedisScript<>();
        script.setScriptText("redis.call('set', KEYS[1], ARGV[1])\n" +
                "redis.call('set', KEYS[2], ARGV[2])\n" +
                "return KEYS[1] .. ' ' .. KEYS[2] .. ' ' .. ARGV[1] .. ' ' .. ARGV[2]");
        script.setResultType(String.class);
        str = this.redisTemplate.execute(script, Arrays.asList("{y}k1", "{y}k2"), "v1", "v2");
        Assertions.assertEquals("{y}k1 {y}k2 v1 v2", str);
        Assertions.assertEquals("v1", this.redisTemplate.opsForValue().get("{y}k1"));
        Assertions.assertEquals("v2", this.redisTemplate.opsForValue().get("{y}k2"));

        // endregion
    }
}
