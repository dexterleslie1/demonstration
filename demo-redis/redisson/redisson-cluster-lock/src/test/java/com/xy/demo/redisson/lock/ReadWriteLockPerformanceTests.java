package com.xy.demo.redisson.lock;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import org.redisson.Redisson;
import org.redisson.api.RLock;
import org.redisson.api.RReadWriteLock;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

// 读写锁性能测试
@BenchmarkMode(Mode.Throughput)
@State(Scope.Benchmark) //使用的SpringBoot容器，都是无状态单例Bean，无安全问题，可以直接使用基准作用域BenchMark
@OutputTimeUnit(TimeUnit.SECONDS)
@Fork(1)  //整体平均执行1次
@Warmup(iterations = 3,time = 5, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 3,time = 5, timeUnit = TimeUnit.SECONDS) //测试也是1s、五遍
@Threads(64)
public class ReadWriteLockPerformanceTests {
    private int lockCount = 100;
    private List<String> LockKeys = new ArrayList<>();
    private Random RANDOM = new Random();

    private RedissonClient redisson = null;

    @Benchmark
    public void testReadLock() throws InterruptedException {
        String key = LockKeys.get(RANDOM.nextInt(lockCount));
        RReadWriteLock readWriteLock = redisson.getReadWriteLock(key);
        RLock rLock = null;
        try {
            rLock = readWriteLock.readLock();
            rLock.lock();
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            if (rLock != null && rLock.isHeldByCurrentThread()) {
                rLock.unlock();
            }
        }
    }

    @Benchmark
    public void testWriteLock() throws InterruptedException {
        String key = LockKeys.get(RANDOM.nextInt(lockCount));
        RReadWriteLock readWriteLock = redisson.getReadWriteLock(key);
        RLock rLock = null;
        try {
            rLock = readWriteLock.writeLock();
            rLock.lock();
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            if (rLock != null && rLock.isHeldByCurrentThread()) {
                rLock.unlock();
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

        for(int i=0; i < lockCount; i++) {
            String uuid = UUID.randomUUID().toString();
            LockKeys.add(uuid);
        }
    }

    @TearDown(Level.Iteration)
    public void teardown(){
        if(redisson != null){
            redisson.shutdown();
        }
    }

    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(ReadWriteLockPerformanceTests.class.getSimpleName())
                .forks(1)
                .build();

        new Runner(opt).run();
    }
}
