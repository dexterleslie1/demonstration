package com.future.demo;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.util.StopWatch;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@SpringBootTest(classes = {Application.class})
@Slf4j
public class ApplicationTests {

    final static Long TotalCount = 100000L;

    @Autowired
    StringRedisTemplate redisTemplate;

    // 测试 pipeline
    // https://blog.csdn.net/yzh_1346983557/article/details/119837981
    @Test
    public void testPipeline() {
        Objects.requireNonNull(this.redisTemplate.getConnectionFactory()).getConnection().flushDb();

        StopWatch stopWatch = new StopWatch();
        stopWatch.start();

        Map<String, String> map = new HashMap<>();
        for (int i = 0; i < TotalCount; i++) {
            String key = "key" + i;
            String value = "value" + i;
            map.put(key, value);

            if ((i + 1) % 1000 == 0) {
                Map<String, String> finalMap = map;
                this.redisTemplate.executePipelined(new SessionCallback<String>() {
                    @Override
                    public <K, V> String execute(RedisOperations<K, V> operations) throws DataAccessException {
                        RedisOperations<String, String> redisOperations = (RedisOperations<String, String>) operations;
                        for (String key : finalMap.keySet()) {
                            redisOperations.opsForValue().set(key, finalMap.get(key));
                        }

                        // 返回null即可，因为返回值会被管道的返回值覆盖，外层取不到这里的返回值
                        return null;
                    }
                });
                map = new HashMap<>();
            }
        }

        stopWatch.stop();
        log.debug("共耗时：" + stopWatch.getTotalTimeSeconds() + "秒");

        Assertions.assertEquals(TotalCount, this.redisTemplate.getConnectionFactory().getConnection().dbSize());
    }
}
