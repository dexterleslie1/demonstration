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
        // 提示：在海量订单数据场景中，需要评估在海量数据情况下根据订单 `ID` 列表查询订单数据的性能。需要寻找一种解决方案支持海量订单 `ID` 存储并支持随机获取指定数据的订单 `ID`，以实现前面提到的根据订单 `ID` 列表查询订单数据的性能评估需求。
        // 测试使用set存储2000万个long类型数值占用的存储空间
        int totalCount = 20000000;
        AtomicInteger count = new AtomicInteger();
        int concurrentThreads = 32;
        ExecutorService threadPool = Executors.newCachedThreadPool();
        String keyPrefix = "keyTest1";
        int keyTotalCount = 128;
        for (int i = 0; i < concurrentThreads; i++) {
            threadPool.submit(() -> {
                try {
                    List<String> longStrList = new ArrayList<>();
                    while (true) {
                        int productId;
                        if ((productId = count.getAndIncrement()) >= totalCount) {
                            break;
                        }

                        longStrList.add(String.valueOf(productId));

                        if (longStrList.size() >= 1024) {
                            List<String> finalLongStrList = longStrList;
                            redisTemplate.executePipelined(new SessionCallback<String>() {
                                @Override
                                public <K, V> String execute(RedisOperations<K, V> operations) throws DataAccessException {
                                    for (String longStr : finalLongStrList) {
                                        RedisOperations<String, String> redisOperations = (RedisOperations<String, String>) operations;
                                        String key = keyPrefix + (Long.parseLong(longStr) % keyTotalCount);
                                        redisOperations.opsForSet().add(key, longStr);
                                    }

                                    return null;
                                }
                            });
                            longStrList = new ArrayList<>();
                        }
                    }
                } catch (Exception ex) {
                    log.error(ex.getMessage(), ex);
                }
            });
        }
        threadPool.shutdown();
        while (!threadPool.awaitTermination(100, TimeUnit.MILLISECONDS)) ;
    }
}
