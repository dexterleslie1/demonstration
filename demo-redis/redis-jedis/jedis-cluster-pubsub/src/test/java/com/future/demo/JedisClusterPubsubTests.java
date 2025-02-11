package com.future.demo;

import org.junit.After;
import org.junit.Assert;
import org.junit.Test;
import redis.clients.jedis.JedisCluster;
import redis.clients.jedis.JedisPubSub;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

/**
 * @author Dexterleslie.Chan
 */
public class JedisClusterPubsubTests {
    @Test
    public void test1() throws InterruptedException, TimeoutException {
        final String message1 = "testMessage";
        int count = 300;
        ExecutorService executorService = Executors.newCachedThreadPool();
        final List<JedisPubSub> subscribers = new ArrayList<JedisPubSub>();
        final CountDownLatch countDownLatch = new CountDownLatch(count);
        final String channel = "jedisClusterSubscribeChannel";
        final JedisCluster jedisCluster = JedisUtil.getInstance().getJedis();
        for(int i=0; i<count; i++) {
            executorService.submit(() -> {
                JedisPubSub subscriber = new JedisPubSub() {
                    @Override
                    public void onMessage(String channel1, String message) {
                        Assert.assertEquals(message1, message);
                        countDownLatch.countDown();
                    }
                };
                subscribers.add(subscriber);
                jedisCluster.subscribe(subscriber, channel);
            });
        }

        for(int i=0; i<count; i++) {
            jedisCluster.publish(channel, message1);
        }

        if(!countDownLatch.await(30, TimeUnit.SECONDS)){
            throw new TimeoutException();
        }

        for(JedisPubSub subscriber : subscribers){
            subscriber.unsubscribe();
        }
    }

    @After
    public void teardown() throws IOException {
        JedisCluster jedisCluster = JedisUtil.getInstance().getJedis();
        if(jedisCluster != null){
            jedisCluster.close();
        }
    }
}
