package com.future.demo.redis;

import org.junit.Assert;
import org.junit.Test;
import redis.clients.jedis.Jedis;

import java.util.Collections;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class FlashSaleTests {
    // 模拟秒杀场景
    @Test
    public void test() throws InterruptedException {
        Random random = new Random();
        String keyProductCount = "productCount";
        String keySuccessUser = "successUser";
        try(Jedis jedis = JedisUtil.getInstance().getJedis()) {
            jedis.flushDB();

            jedis.set(keyProductCount, String.valueOf(10));
        }

        int concurrent = 500;
        int eachRequests = 100;
        ExecutorService executorService = Executors.newCachedThreadPool();
        for(int i=0; i<concurrent; i++) {
            executorService.submit(() -> {
                for(int j=0; j<eachRequests; j++) {
                    try(Jedis jedis = JedisUtil.getInstance().getJedis()) {
                        int userId = random.nextInt(3000);
                        // 使用lua脚本解决秒杀并发和库存遗留问题
                        String script = "local userId=KEYS[1]" +
                                        "local keyProductCount=\"" + keyProductCount + "\"" +
                                        "local keySuccessUser=\"" + keySuccessUser + "\"" +
                                        "--[[判断用户是否已在秒杀成功列表中]]" +
                                        "local ifUserExists=tonumber(redis.call(\"sismember\", keySuccessUser, userId))" +
                                        "if ifUserExists==1 then" +
                                        " return \"已经成功秒杀\"" +
                                        "end\n" +
                                        "--[[判断商品是否有库存]]" +
                                        "local count=tonumber(redis.call(\"get\", keyProductCount))" +
                                        "if count<=0 then" +
                                        " return \"秒杀结束\"" +
                                        "end\n" +
                                        "--[[库存-1，新增用户到成功秒杀列表]]" +
                                        "redis.call(\"decr\", keyProductCount)" +
                                        "redis.call(\"sadd\", keySuccessUser, userId)" +
                                        "return \"\"";
                        Object object = jedis.eval(script, Collections.singletonList(String.valueOf(userId)), Collections.EMPTY_LIST);
                        if(object == null || object.toString().equals("")) {
                            object = "成功秒杀";
                        }
//                        System.out.println(object);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            });
        }
        executorService.shutdown();
        while(!executorService.awaitTermination(1, TimeUnit.SECONDS));

        try(Jedis jedis = JedisUtil.getInstance().getJedis()) {
            Assert.assertEquals("0", jedis.get(keyProductCount));
            Assert.assertEquals(new Long(10), jedis.scard(keySuccessUser));
        }
    }
}
