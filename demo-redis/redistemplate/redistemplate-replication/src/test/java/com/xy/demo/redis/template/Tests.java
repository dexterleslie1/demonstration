package com.xy.demo.redis.template;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.util.Date;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {Application.class})
public class Tests {
    private final static Random random = new Random();

    @Resource
    private RedisTemplate redisTemplate = null;

    @Test
    public void test() throws InterruptedException {
        int looperOutter = 200;
        int looperInner = 2000;
        ExecutorService executorService= Executors.newCachedThreadPool();
        for(int i=0;i<looperOutter;i++) {
            final int j = i;
            executorService.submit(new Runnable() {
                @Override
                public void run() {
                    try{
                        for(int i=0;i<looperInner;i++) {
                            String string1 = String.valueOf(j*looperInner+i);
                            redisTemplate.opsForValue().set(string1, string1);
                        }
                    }catch(Exception ex){
                        ex.printStackTrace();
                    }finally{
                    }
                }
            });
        }
        executorService.shutdown();
        while(!executorService.awaitTermination(1, TimeUnit.SECONDS));
        System.out.println("redis缓存数据已准备好，准备进行读效率测试");

        final int max = looperOutter*looperInner;
        executorService= Executors.newCachedThreadPool();
        for(int i=0;i<4;i++) {
            executorService.submit(new Runnable() {
                @Override
                public void run() {
                    for(int i=0;i<200000;i++) {
                    try{

                            int intTemp = random.nextInt(max);
                            redisTemplate.opsForValue().get(String.valueOf(intTemp));
//                            TimeUnit.SECONDS.sleep(1);
//                            TimeUnit.MILLISECONDS.sleep(500);
//                            System.out.println(new Date() + "正常读取");

                    }catch(Exception ex){
                        ex.printStackTrace();
                    }finally{
                    }
                    }
                }
            });
        }
        executorService.shutdown();
        while(!executorService.awaitTermination(1, TimeUnit.SECONDS));

        System.out.println("读取性能测试结束");
    }
}