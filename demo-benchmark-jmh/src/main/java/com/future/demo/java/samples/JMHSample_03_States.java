package com.future.demo.java.samples;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.util.concurrent.TimeUnit;

@Warmup(iterations = 2, time = 1, timeUnit = TimeUnit.SECONDS)
// 进行3次基准测试，每次基准测试持续3秒
@Measurement(iterations = 2, time = 1, timeUnit = TimeUnit.SECONDS)
// 提供报告结果的默认时间单位
@OutputTimeUnit(TimeUnit.SECONDS)
public class JMHSample_03_States {
    // https://blog.csdn.net/m0_37607945/article/details/111479890
    // 基础测试内线程间共享
    @State(Scope.Benchmark)
    // 基准测试内线程内共享
    //@State(Scope.Thread)
    // 基础测试间变量共享
    //@State(Scope.Group)
    public static class MyState {
        public int x = 0;
    }

    @Benchmark
    @Group
    public void benchmark1(MyState state) throws InterruptedException {
        state.x++;
        System.out.println(Thread.currentThread().getName() + " x=" + state.x);
        TimeUnit.MILLISECONDS.sleep(1000);
    }

    @Benchmark
    @Group
    public void benchmark2(MyState state) throws InterruptedException {
        state.x++;
        System.out.println(Thread.currentThread().getName() + " x=" + state.x);
        TimeUnit.MILLISECONDS.sleep(1000);
    }

    /**
     *
     * @param args
     * @throws RunnerException
     */
    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(JMHSample_03_States.class.getSimpleName())
                // 2条并发执行线程
                .threads(2)
                // 断点调试时fork=0
                .forks(1)
                // 发生错误停止测试
                .shouldFailOnError(true)
                .build();

        new Runner(opt).run();
    }
}
