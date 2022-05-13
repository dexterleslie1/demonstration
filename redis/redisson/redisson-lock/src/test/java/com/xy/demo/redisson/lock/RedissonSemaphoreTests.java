package com.xy.demo.redisson.lock;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.redisson.Redisson;
import org.redisson.api.RSemaphore;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;

import java.util.Date;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class RedissonSemaphoreTests {
    private RedissonClient redisson = null;

    @Before
    public void setup(){
        String host = "localhost";
        int port = 6379;
        String password = "123456";

        Config config = new Config();
        config.useSingleServer().setAddress("redis://" + host + ":" + port).setPassword(password);
        redisson = Redisson.create(config);
    }

    @After
    public void teardown(){
        if(redisson != null){
            redisson.shutdown();
        }
    }

    /**
     * redis redisson 信号量示例（RSemaphore）
     * https://blog.csdn.net/weixin_43931625/article/details/103232670
     * @throws InterruptedException
     */
    @Test
    public void test() throws InterruptedException {
        final Random random = new Random();
        final RSemaphore semaphore = redisson.getSemaphore("testSemaphore");
        semaphore.trySetPermits(5);

        ExecutorService executorService = Executors.newCachedThreadPool();
        for(int i=0; i<10; i++) {
            final int finalI = i;
            executorService.submit(new Runnable() {
                public void run() {
                    try {
                        semaphore.acquire();
                        System.out.println(new Date() + ": i=" + (finalI+1));

                        long randomMilliseconds = random.nextInt(5000);
                        if(randomMilliseconds>0) {
                            Thread.sleep(randomMilliseconds);
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    } finally {
                        semaphore.release();
                        System.out.println("可用信号: " + semaphore.availablePermits());
                    }
                }
            });
        }

        executorService.shutdown();
        while(!executorService.awaitTermination(1000, TimeUnit.MILLISECONDS));
    }
}
