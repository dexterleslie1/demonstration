package com.xy.demo.redisson.lock;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import org.redisson.Redisson;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@BenchmarkMode(Mode.Throughput)
@State(Scope.Benchmark) //使用的SpringBoot容器，都是无状态单例Bean，无安全问题，可以直接使用基准作用域BenchMark
@OutputTimeUnit(TimeUnit.SECONDS)
@Fork(1)  //整体平均执行1次
@Warmup(iterations = 3,time = 5,timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 3,time = 5,timeUnit = TimeUnit.SECONDS) //测试也是1s、五遍
@Threads(16)
public class RedissonLockPerformanceTests {
    private RedissonClient redisson = null;
    private Random random = new Random();

    @Benchmark
    public void test1() throws InterruptedException {
        String key = UUID.randomUUID().toString();
        RLock mylock = redisson.getLock(key);
        boolean isLocked = false;
        try {
            isLocked = mylock.tryLock(1, 10000, TimeUnit.MILLISECONDS);
        } finally {
            if(isLocked){
                mylock.unlock();
            }
        }
    }

    @Setup(Level.Iteration)
    public void setup(){
        String host = MyConfig.Host;
        int port = MyConfig.Port;
        String password = MyConfig.Password;

        Config config = new Config();
        config.useSingleServer().setAddress("redis://" + host + ":" + port).setPassword(password);
        redisson = Redisson.create(config);
    }

    @TearDown(Level.Iteration)
    public void teardown(){
        if(redisson != null){
            redisson.shutdown();
        }
    }

    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(RedissonLockPerformanceTests.class.getSimpleName())
                .forks(1)
                .build();

        new Runner(opt).run();
    }
}
