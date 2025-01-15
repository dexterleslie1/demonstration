package com.future.demo.redis;

import org.junit.Assert;
import org.junit.Test;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.Transaction;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class Tests {
    // 演示成功提交事务
    @Test
    public void testTransactionExec() {
        Transaction transaction = null;
        try (Jedis jedis = JedisUtil.getInstance().getJedis()) {
            jedis.flushDB();

            transaction = jedis.multi();

            transaction.set("k1", "tv1");
            transaction.set("k2", "tv2");

            List<Object> objectList = transaction.exec();
            objectList.forEach(o -> {
                Assert.assertEquals("OK", o);
            });

            Assert.assertEquals("tv1", jedis.get("k1"));
            Assert.assertEquals("tv2", jedis.get("k2"));
        } catch (Exception ex) {
            if (transaction != null) {
                transaction.discard();
            }
            throw ex;
        }
    }

    // 演示部分事务执行失败
    @Test
    public void testTransactionExecWithFailed() {
        Transaction transaction = null;
        try (Jedis jedis = JedisUtil.getInstance().getJedis()) {
            jedis.flushDB();

            transaction = jedis.multi();

            String uuidStr = UUID.randomUUID().toString();
            transaction.set("k2", uuidStr);
            transaction.set("k1", "v1");
            transaction.incr("k1");
            transaction.set("k1", "v2");

            List<Object> objectList = transaction.exec();
            Assert.assertEquals("OK", objectList.get(0));
            Assert.assertTrue(objectList.get(2).toString().contains("ERR value is not an integer or out of range"));

            // redis事务没有回滚概念，已经成功执行的命令不会回滚
            Assert.assertEquals(uuidStr, jedis.get("k2"));
            // 在 Redis 事务中中途有命令执行失败，后续命令还是会继续执行
            Assert.assertEquals("v2", jedis.get("k1"));
        }
    }

    // 演示取消事务执行
    @Test
    public void testTransactionDiscard() {
        Transaction transaction = null;
        try (Jedis jedis = JedisUtil.getInstance().getJedis()) {
            jedis.flushDB();

            transaction = jedis.multi();

            transaction.set("k1", "v1");
            transaction.set("k2", "v2");

            transaction.discard();

            Assert.assertFalse(jedis.exists("k1"));
            Assert.assertFalse(jedis.exists("k2"));
        }
    }

    // jedis事务使用watch乐观锁机制
    @Test
    public void testWatch() throws InterruptedException {
        // 清空 db
        try (Jedis jedis = JedisUtil.getInstance().getJedis()) {
            jedis.flushDB();
        }

        AtomicInteger successIndicator = new AtomicInteger();
        AtomicInteger failIndicator = new AtomicInteger();
        ExecutorService executorService = Executors.newCachedThreadPool();
        for (int i = 0; i < 2; i++) {
            executorService.submit(() -> {
                Jedis jedis = null;
                try {
                    jedis = JedisUtil.getInstance().getJedis();

                    // 对 k1 加乐观锁
                    jedis.watch("k1");

                    Transaction transaction = jedis.multi();

                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        //
                    }

                    String uuidStr = UUID.randomUUID().toString();
                    transaction.sadd("k2", uuidStr);
                    transaction.incr("k3");
                    transaction.set("k1", uuidStr);
                    List<Object> objectList = transaction.exec();
                    if (objectList == null || objectList.size() <= 0) {
                        failIndicator.incrementAndGet();
                    } else {
                        successIndicator.incrementAndGet();
                    }
                } finally {
                    if (jedis != null) {
                        jedis.unwatch();
                    }

                    if (jedis != null) {
                        jedis.close();
                    }
                }
            });
        }
        executorService.shutdown();
        while (!executorService.awaitTermination(1, TimeUnit.SECONDS)) ;

        // 只能有一个线程执行成功
        Assert.assertEquals(1, failIndicator.get());
        Assert.assertEquals(1, successIndicator.get());
        try (Jedis jedis = JedisUtil.getInstance().getJedis()) {
            Assert.assertEquals(Long.valueOf(1), jedis.scard("k2"));
            Assert.assertEquals("1", jedis.get("k3"));
        }
    }
}
