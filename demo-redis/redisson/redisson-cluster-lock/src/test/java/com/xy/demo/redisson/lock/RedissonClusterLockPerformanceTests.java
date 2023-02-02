package com.xy.demo.redisson.lock;

import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Before;
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
@Threads(64)
public class RedissonClusterLockPerformanceTests {
    private final static String Key = UUID.randomUUID().toString();
    private RedissonClient redisson = null;

    @Benchmark
    public void test1() throws InterruptedException {
        RLock mylock = redisson.getLock(Key);
        boolean isAcquired = false;
        try {
            isAcquired = mylock.tryLock(10, 10000, TimeUnit.MILLISECONDS);
        } finally {
            if(isAcquired){
                try {
                    mylock.unlock();
                } catch (Exception ex) {
                    //
                }
            }
        }
    }

    @Setup(Level.Iteration)
    public void setup(){
        Config config = new Config();
        config.useClusterServers().addNodeAddress(
                "redis://127.0.0.1:6380",
                "redis://127.0.0.1:6381",
                "redis://127.0.0.1:6382"
        );
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
                .include(RedissonClusterLockPerformanceTests.class.getSimpleName())
                .forks(1)
                .build();

        new Runner(opt).run();
    }
}
