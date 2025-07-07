package com.future.demo;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {Application.class})
@Slf4j
public class ApplicationTests {
    @Autowired
    StringRedisTemplate redisTemplate;

    @Test
    public void contextLoads() throws InterruptedException {
        // 测试使用zset存储2000万个商品ID和库存占用的存储空间
        int totalCount = 20000000;
        AtomicInteger count = new AtomicInteger();
        int concurrentThreads = 32;
        ExecutorService threadPool = Executors.newCachedThreadPool();
        String keyPrefix = "productAndStockAmount";
        int keyTotalCount = 128;
        for (int i = 0; i < concurrentThreads; i++) {
            threadPool.submit(() -> {
                List<String> productIdStrList = new ArrayList<>();
                while (true) {
                    int productId;
                    if ((productId = count.getAndIncrement()) >= totalCount) {
                        break;
                    }

                    productIdStrList.add(String.valueOf(productId));

                    if (productIdStrList.size() >= 1024) {
                        List<String> finalProductIdStrList = productIdStrList;
                        redisTemplate.executePipelined(new SessionCallback<String>() {
                            @Override
                            public <K, V> String execute(RedisOperations<K, V> operations) throws DataAccessException {
                                for (String productIdStr : finalProductIdStrList) {
                                    RedisOperations<String, String> redisOperations = (RedisOperations<String, String>) operations;
                                    String key = keyPrefix + (Long.parseLong(productIdStr) % keyTotalCount);
                                    redisOperations.opsForZSet().add(key, productIdStr, 300);
                                }

                                return null;
                            }
                        });
                        productIdStrList = new ArrayList<>();
                    }
                }
            });
        }
        threadPool.shutdown();
        while (!threadPool.awaitTermination(100, TimeUnit.MILLISECONDS)) ;
    }
}
