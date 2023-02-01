package com.future.demo;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisCluster;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * @author Dexterleslie.Chan
 */
public class JedisClusterCpuLoadTests {
    private final static Random RANDOM = new Random();
    private final static List<String> Keys = new ArrayList<>();

    @Before
    public void setup() {
        for (int i = 0; i < 500000; i++) {
            String key = "k" + i;
            Keys.add(key);
        }
    }

    @Test
    public void testWrite() throws InterruptedException {
        int looperOutter = 100;
        int looperInner = 20000;
        ExecutorService executorService = Executors.newCachedThreadPool();
        for (int i = 0; i < looperOutter; i++) {
            final int j = i;
            executorService.submit(() -> {
                JedisCluster jedis = null;
                try {
                    jedis = JedisUtil.getInstance().getJedis();
                    for (int i1 = 0; i1 < looperInner; i1++) {
                        int randomInt = RANDOM.nextInt(Keys.size());
                        String key = Keys.get(randomInt);
                        String string1 = String.valueOf(j * looperInner + i1);
                        jedis.set(key, string1);
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            });
        }
        executorService.shutdown();
        while (!executorService.awaitTermination(1, TimeUnit.SECONDS)) ;
        System.out.println("redis缓存数据已准备好，准备进行读效率测试");
    }

    @Test
    public void testRead() throws InterruptedException {
        int looperOutter = 100;
        int looperInner = 20000;
        ExecutorService executorService = Executors.newCachedThreadPool();
        for (int i = 0; i < looperOutter; i++) {
            executorService.submit(() -> {
                JedisCluster jedis = null;
                try {
                    jedis = JedisUtil.getInstance().getJedis();
                    for (int i1 = 0; i1 < looperInner; i1++) {
                        int randomInt = RANDOM.nextInt(Keys.size());
                        String key = Keys.get(randomInt);
                        jedis.get(key);
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            });
        }
        executorService.shutdown();
        while (!executorService.awaitTermination(1, TimeUnit.SECONDS)) ;
    }

    @After
    public void teardown() throws IOException {
        JedisCluster jedisCluster = JedisUtil.getInstance().getJedis();
        if (jedisCluster != null) {
            jedisCluster.close();
        }
    }
}
