package com.future.demo.redis;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {Application.class})
public class Tests {

    @Resource
    RedisTemplate<String, String> redisTemplate = null;

    private final static Random RANDOM = new Random();
    private final static List<String> Keys = new ArrayList<>();

    @Before
    public void setup() {
        for(int i=0; i<500000; i++) {
            String key = "k" + i;
            Keys.add(key);
        }
    }

    @Test
    public void test() throws InterruptedException {
        int looperOutter = 10;
        int looperInner = 10000;

        ExecutorService executorService = Executors.newCachedThreadPool();
        for(int i=0;i<looperOutter;i++) {
            final int j = i;
            executorService.submit(() -> {
                try{
                    for(int i1 = 0; i1 <looperInner; i1++) {
                        int randomInt = RANDOM.nextInt(Keys.size());
                        String string1 = String.valueOf(j*looperInner+ i1);
                        String key = Keys.get(randomInt);
                        redisTemplate.opsForValue().set(key, string1);
                    }
                }catch(Exception ex){
                    ex.printStackTrace();
                }
            });
        }
        executorService.shutdown();
        while(!executorService.awaitTermination(1, TimeUnit.SECONDS));
        System.out.println("redis缓存数据已准备好，准备进行读效率测试");
    }

    @Test
    public void testRead() throws InterruptedException {
        int looperOutter = 10;
        int looperInner = 10000;

        ExecutorService executorService = Executors.newCachedThreadPool();
        for(int i=0;i<looperOutter;i++) {
            executorService.submit(() -> {
                try{
                    for(int i1 = 0; i1 <looperInner; i1++) {
                        int randomInt = RANDOM.nextInt(Keys.size());
                        String key = Keys.get(randomInt);
                        redisTemplate.opsForValue().get(key);
                    }
                }catch(Exception ex){
                    ex.printStackTrace();
                }
            });
        }
        executorService.shutdown();
        while(!executorService.awaitTermination(1, TimeUnit.SECONDS));
    }
}
