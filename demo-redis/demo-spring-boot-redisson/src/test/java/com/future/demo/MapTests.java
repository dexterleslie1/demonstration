package com.future.demo;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.redisson.api.RMap;
import org.redisson.api.RedissonClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.util.Date;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@RunWith(SpringRunner.class)
@SpringBootTest
@Slf4j
public class MapTests {
    private final static Random random = new Random();
    @Resource
    RedissonClient redisson;

    @Test
    public void test1() throws InterruptedException {
        Date timeStart = new Date();
        int looperOutter = 200;
        final int looperInner = 2000;
        ExecutorService executorService = Executors.newCachedThreadPool();
        for (int i = 0; i < looperOutter; i++) {
            final int j = i;
            executorService.submit(new Runnable() {
                public void run() {
                    try {
                        for (int i = 0; i < looperInner; i++) {
                            String string1 = String.valueOf(j * looperInner + i);
                            RMap<String, String> rMap = redisson.getMap("redissonmap");
                            rMap.put(string1, string1);
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    } finally {
                    }
                }
            });
        }
        executorService.shutdown();
        while (!executorService.awaitTermination(1, TimeUnit.SECONDS)) ;
        System.out.println("redis缓存数据已准备好，准备进行读效率测试");

        final int max = looperOutter * looperInner;
        executorService = Executors.newCachedThreadPool();
        for (int i = 0; i < 250; i++) {
            final int j = i;
            executorService.submit(new Runnable() {
                public void run() {
                    try {
                        for (int i = 0; i < 10000; i++) {
                            int intTemp = random.nextInt(max);
                            RMap<String, String> rMap = redisson.getMap("redissonmap");
                            rMap.get(String.valueOf(intTemp));
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    } finally {
                    }
                }
            });
        }
        executorService.shutdown();
        while (!executorService.awaitTermination(1, TimeUnit.SECONDS)) ;
        Date timeEnd = new Date();
        long milliseconds = timeEnd.getTime() - timeStart.getTime();
        log.info("耗时" + milliseconds + "毫秒");
    }
}
