package com.future.demo;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 测试锁性能
 */
// https://blog.csdn.net/a23452/article/details/126680840
@BenchmarkMode(Mode.Throughput)
@State(Scope.Benchmark) //使用的SpringBoot容器，都是无状态单例Bean，无安全问题，可以直接使用基准作用域BenchMark
@OutputTimeUnit(TimeUnit.SECONDS)
@Warmup(iterations = 3, time = 1, timeUnit = TimeUnit.SECONDS) //预热1s
@Measurement(iterations = 3, time = 5, timeUnit = TimeUnit.SECONDS) //测试也是1s、五遍
// 指定并发执行线程数
// https://stackoverflow.com/questions/39644383/jmh-run-benchmark-concurrently
@Threads(-1)
public class LockBenchmarkTests {

    MyObject myObject = null;
    ReentrantLock reentrantLockUnfair = new ReentrantLock();
    ReentrantLock reentrantLockFair = new ReentrantLock(true);

    public static void main(String[] args) throws RunnerException {
        //使用注解之后只需要配置一下include即可，fork和warmup、measurement都是注解
        Options opt = new OptionsBuilder()
                .include(LockBenchmarkTests.class.getSimpleName())
                // 断点调试时fork=0
                .forks(1)
                // 发生错误停止测试
                .shouldFailOnError(true)
                .jvmArgs("-Xmx2G")
                .build();
        new Runner(opt).run();
    }

    /**
     * 初始化，获取springBoot容器，run即可，同时得到相关的测试对象
     */
    @Setup(Level.Trial)
    public void setup() {
        myObject = new MyObject();
    }

    /**
     * 测试的后处理操作，关闭容器，资源清理
     */
    @TearDown(Level.Trial)
    public void teardown() {
    }

//    /**
//     * 测试模拟耗时逻辑并且使用 synchronized
//     *
//     * @param blackhole
//     */
//    @Benchmark
//    public void testMethodSimulateLongRunTaskWithSynchronized(Blackhole blackhole) {
//        myObject.methodSimulateLongRunTaskWithSynchronized();
//    }
//
//    /**
//     * 测试模拟耗时逻辑并且不使用 synchronized
//     *
//     * @param blackhole
//     */
//    @Benchmark
//    public void testMethodSimulateLongRunTaskWithoutSynchronized(Blackhole blackhole) {
//        myObject.methodSimulateLongRunTaskWithoutSynchronized();
//    }
//
//    /**
//     * 测试方法 synchronized 性能
//     *
//     * @param blackhole
//     */
//    @Benchmark
//    public void testMethodWithSynchronized(Blackhole blackhole) {
//        blackhole.consume(myObject.methodWithSynchronized());
//    }
//
//    /**
//     * 测试方法没有 synchronized 性能
//     *
//     * @param blackhole
//     */
//    @Benchmark
//    public void testMethodWithoutSynchronized(Blackhole blackhole) {
//        blackhole.consume(myObject.methodWithoutSynchronized());
//    }

    /**
     * 测试非公平锁性能
     */
    @Benchmark
    public void testReentrantLockUnfair() {
        try {
            reentrantLockUnfair.lock();
        } finally {
            reentrantLockUnfair.unlock();
        }
    }

    /**
     * 测试公平锁性能
     */
    @Benchmark
    public void testReentrantLockFair() {
        try {
            reentrantLockFair.lock();
        } finally {
            reentrantLockFair.unlock();
        }
    }

    public static class MyObject {
        public synchronized void methodSimulateLongRunTaskWithSynchronized() {
            try {
                TimeUnit.MILLISECONDS.sleep(5);
            } catch (InterruptedException ignored) {

            }
        }

        public void methodSimulateLongRunTaskWithoutSynchronized() {
            try {
                TimeUnit.MILLISECONDS.sleep(5);
            } catch (InterruptedException ignored) {

            }
        }

        final static int AmountVar = 320;
        final static double DoubleVar = 12.328383984984;

        public synchronized double methodWithSynchronized() {
            return AmountVar * (DoubleVar - 1);
        }

        public double methodWithoutSynchronized() {
            return AmountVar * (DoubleVar - 1);
        }
    }
}
