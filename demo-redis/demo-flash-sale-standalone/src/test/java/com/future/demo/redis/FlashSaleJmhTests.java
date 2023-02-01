package com.future.demo.redis;

import org.junit.Assert;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import redis.clients.jedis.Jedis;

import java.util.Collections;
import java.util.Random;
import java.util.concurrent.TimeUnit;

@BenchmarkMode(Mode.Throughput)
@State(Scope.Benchmark) //使用的SpringBoot容器，都是无状态单例Bean，无安全问题，可以直接使用基准作用域BenchMark
@OutputTimeUnit(TimeUnit.SECONDS)
@Fork(1)  //整体平均执行1次
@Warmup(iterations = 3,time = 5,timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 3,time = 5,timeUnit = TimeUnit.SECONDS) //测试也是1s、五遍
@Threads(16)
public class FlashSaleJmhTests {
    final static Random RANDOM = new Random();
    final static String KeyProductCount = "productCount";
    final static String KeySuccessUser = "successUser";

    @Setup(Level.Iteration)
    public void setup() {
        try(Jedis jedis = JedisUtil.getInstance().getJedis()) {
            jedis.flushDB();

            jedis.set(KeyProductCount, String.valueOf(10));
        }
    }

    @TearDown(Level.Iteration)
    public void teardown() {
        try(Jedis jedis = JedisUtil.getInstance().getJedis()) {
            Assert.assertEquals("0", jedis.get(KeyProductCount));
            Assert.assertEquals(new Long(10), jedis.scard(KeySuccessUser));
        }
    }

    // 模拟秒杀场景
    @Benchmark
    public void test() throws InterruptedException {
        try(Jedis jedis = JedisUtil.getInstance().getJedis()) {
            int userId = RANDOM.nextInt(3000);
            // 使用lua脚本解决秒杀并发和库存遗留问题
            String script = "local userId=KEYS[1]" +
                            "local keyProductCount=\"" + KeyProductCount + "\"" +
                            "local keySuccessUser=\"" + KeySuccessUser + "\"" +
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
            jedis.eval(script, Collections.singletonList(String.valueOf(userId)), Collections.EMPTY_LIST);
        }
    }

    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(FlashSaleJmhTests.class.getSimpleName())
                .forks(1)
                .build();

        new Runner(opt).run();
    }
}
