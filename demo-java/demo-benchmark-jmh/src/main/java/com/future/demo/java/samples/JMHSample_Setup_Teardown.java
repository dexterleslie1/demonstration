package com.future.demo.java.samples;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.util.concurrent.TimeUnit;

// https://blog.csdn.net/wyaoyao93/article/details/115727005

@Warmup(iterations = 3, time = 2, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 2, time = 2, timeUnit = TimeUnit.SECONDS)
// 提供报告结果的默认时间单位
@OutputTimeUnit(TimeUnit.SECONDS)
@State(Scope.Benchmark)
// 断点调试时fork=0
@Fork(value = 1, jvmArgs = {"-Xmx2G", "-server"})
@Threads(1)
public class JMHSample_Setup_Teardown {
    // Trial是JMH性能测试中的最高层级，它代表了一次完整的基准测试过程。
    // 在每次Benchmark测试之前或之后，可以定义一些操作作为Trial级别的操作，这些操作会在每次完整的测试过程之前或之后执行。
    // Trial级别的操作允许开发者在测试前后进行资源的准备和清理工作，如数据库的初始化、缓存的清理等。
    @Setup(Level.Trial)
    public void setupTrial() {
         System.out.println("++++++++ Trial setup");
    }
    @TearDown(Level.Trial)
    public void teardownTrial() {
         System.out.println("++++++++ Trial teardown");
    }

    // Iteration是JMH性能测试中的一个重要层级，它代表了一次测试迭代过程。
    // 在预热（Warmup）和测量（Measurement）阶段，都会进行多次Iteration来收集数据。
    @Setup(Level.Iteration)
    public void setupIteration() {
        System.out.println("++++++++ Iteration setup");
    }
    @TearDown(Level.Iteration)
    public void teardownIteration() {
        System.out.println("++++++++ Iteration teardown");
    }

    // Invocation是JMH性能测试中最细粒度的层级，它代表了一次benchmark方法的调用。
    @Setup(Level.Invocation)
    public void setupInvocation() {
        System.out.println("++++++++ Invocation setup");
    }
    @TearDown(Level.Invocation)
    public void teardownInvocation() {
        System.out.println("++++++++ Invocation teardown");
    }

    @Benchmark
    public void benchmark1() throws InterruptedException {
        TimeUnit.SECONDS.sleep(1);
    }

    @Benchmark
    public void benchmark2() throws InterruptedException {
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
