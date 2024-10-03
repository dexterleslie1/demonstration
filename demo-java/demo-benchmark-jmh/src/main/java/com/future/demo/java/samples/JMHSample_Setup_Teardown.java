package com.future.demo.java.samples;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.util.concurrent.TimeUnit;

// https://blog.csdn.net/wyaoyao93/article/details/115727005
@Warmup(iterations = 2, time = 2, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 2, time = 2, timeUnit = TimeUnit.SECONDS)
// 提供报告结果的默认时间单位
@OutputTimeUnit(TimeUnit.SECONDS)
// 断点调试时fork=0
@Fork(value = 1, jvmArgs = {"-Xmx2G", "-server"})
@Threads(2)
public class JMHSample_Setup_Teardown {
    @State(Scope.Benchmark)
    public static class MyStateBenchmark {
        public Object internalObject;

        // Trial是JMH性能测试中的最高层级，它代表了一次完整的基准测试过程。
        // 在每次Benchmark测试之前或之后，可以定义一些操作作为Trial级别的操作，这些操作会在每次完整的测试过程之前或之后执行。
        // Trial级别的操作允许开发者在测试前后进行资源的准备和清理工作，如数据库的初始化、缓存的清理等。
        //@Setup(Level.Trial)
        // Iteration是JMH性能测试中的一个重要层级，它代表了一次测试迭代过程。
        // 在预热（Warmup）和测量（Measurement）阶段，都会进行多次Iteration来收集数据。
        //@Setup(Level.Iteration)
        // Invocation是JMH性能测试中最细粒度的层级，它代表了一次benchmark方法的调用。
        @Setup(Level.Invocation)
        public void setup() {
            this.internalObject = new Object();
            System.out.println("++++++++ benchmark setup, internalObject=" + this.internalObject + ", threadName=" + Thread.currentThread().getName());
        }

        //@TearDown(Level.Trial)
        //@TearDown(Level.Iteration)
        @TearDown(Level.Invocation)
        public void teardown() {
            System.out.println("++++++++ benchmark teardown, internalObject=" + this.internalObject + ", threadName=" + Thread.currentThread().getName());
        }
    }

    @State(Scope.Thread)
    public static class MyStateThread {
        public Object internalObject;

        //@Setup(Level.Trial)
        //@Setup(Level.Iteration)
        @Setup(Level.Invocation)
        public void setup() {
            this.internalObject = new Object();
            System.out.println("++++++++ thread setup, internalObject=" + this.internalObject + ", threadName=" + Thread.currentThread().getName());
        }

        //@TearDown(Level.Trial)
        //@TearDown(Level.Iteration)
        @TearDown(Level.Invocation)
        public void teardown() {
            System.out.println("++++++++ thread teardown, internalObject=" + this.internalObject + ", threadName=" + Thread.currentThread().getName());
        }
    }

    @Benchmark
    public void benchmark1(MyStateBenchmark myStateBenchmark, MyStateThread myStateThread) throws InterruptedException {
        System.out.println("myStateBenchmark.internalObject=" + myStateBenchmark.internalObject + ", myStateThread.internalObject=" + myStateThread.internalObject + ", threadName=" + Thread.currentThread().getName());
        TimeUnit.SECONDS.sleep(1);
    }

    @Benchmark
    public void benchmark2(MyStateBenchmark myStateBenchmark, MyStateThread myStateThread) throws InterruptedException {
        System.out.println("myStateBenchmark.internalObject=" + myStateBenchmark.internalObject + ", myStateThread.internalObject=" + myStateThread.internalObject + ", threadName=" + Thread.currentThread().getName());
        TimeUnit.SECONDS.sleep(1);
    }

    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(JMHSample_Setup_Teardown.class.getSimpleName())
                // 发生错误停止测试
                .shouldFailOnError(true)
                .build();

        new Runner(opt).run();
    }
}
