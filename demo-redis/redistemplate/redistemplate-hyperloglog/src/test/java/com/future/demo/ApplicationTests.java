package com.future.demo;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {Application.class})
@Slf4j
public class ApplicationTests {

    @Autowired
    private StringRedisTemplate redisTemplate = null;

    // UV 统计场景，统计有误差但可以忍受，100 万数据占用很少的内存空间
    @Test
    public void test() throws InterruptedException {
        String key = UUID.randomUUID().toString();

        ExecutorService executor = Executors.newCachedThreadPool();
        int currentThreads = 2;
        int loopCount = 1000000;
        int batchSize = 1000;
        for (int i = 0; i < currentThreads; i++) {
            executor.execute(() -> {
                List<String> list = new ArrayList<>();
                for (int j = 0; j < loopCount; j++) {
                    if ((j + 1) % batchSize == 0 || j + 1 >= loopCount) {
                        redisTemplate.opsForHyperLogLog().add(key, list.toArray(new String[0]));
                        list.clear();
                    } else {
                        list.add(String.valueOf(j));
                    }
                }
            });
        }
        executor.shutdown();
        while (!executor.awaitTermination(10, TimeUnit.MILLISECONDS)) ;

        long count = redisTemplate.opsForHyperLogLog().size(key);
        log.debug("count: {}", count);
    }
}
