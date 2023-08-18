package com.future.demo.java.samples;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.util.concurrent.TimeUnit;

// https://blog.csdn.net/wyaoyao93/article/details/115727005

@Warmup(iterations = 2, time = 1, timeUnit = TimeUnit.SECONDS)
// 进行3次基准测试，每次基准测试持续3秒
@Measurement(iterations = 2, time = 1, timeUnit = TimeUnit.SECONDS)
// 提供报告结果的默认时间单位
@OutputTimeUnit(TimeUnit.SECONDS)
@State(Scope.Benchmark)
public class JMHSample_Setup_Teardown {
    // Trial：Setup和TearDown默认的配置，该套件方法会在每一个基准测试方法的所有批次执行的前后被执行。
    // Iteration：由于我们可以设置Warmup和Measurement，因此每一个基准测试方法都会被执行若干个批次，如果想要在每一个基准测试批次执行的前后调用套件方法，则可以将Level设置为Iteration。
    // Invocation：将Level设置为Invocation意味着在每一个批次的度量过程中，每一次对基准方法的调用前后都会执行套件方法。
    @Setup(Level.Trial)
    public void setup() {
        // System.out.println("setup...");
    }

    @TearDown(Level.Iteration)
    public void teardown() {
        // System.out.println("teardown..");
    }

    @Benchmark
    public void benchmark1() {

    }

    @Benchmark
    public void benchmark2() {

    }

    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(JMHSample_Setup_Teardown.class.getSimpleName())
                // 断点调试时fork=0
                .forks(1)
                // 发生错误停止测试
                .shouldFailOnError(true)
                .build();

        new Runner(opt).run();
    }
}
