package com.future.demo.java.samples;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.util.concurrent.TimeUnit;

// https://blog.csdn.net/tanhongwei1994/article/details/120419321
// 吞吐量模式：每秒多少次调用
//@BenchmarkMode(Mode.Throughput)
// 平均调用时间模式：平均一次调用多少秒
//@BenchmarkMode(Mode.AverageTime)
// 调用时间样品分布模式：迭代周期内和基准测试周期内，调用时间的分布
//@BenchmarkMode(Mode.SampleTime)
// 每个迭代周期仅一次调用所需要时间统计，3个迭代周期总共调用wellHelloThere 3次
//@BenchmarkMode(Mode.SingleShotTime)
// 以上所有模式都执行一次
//@BenchmarkMode(Mode.All)
// 进行2次预热，每次预热持续1秒
@Warmup(iterations = 2, time = 2, timeUnit = TimeUnit.SECONDS)
// 进行3次基准测试，每次基准测试持续3秒
@Measurement(iterations = 3, time = 3, timeUnit = TimeUnit.SECONDS)
// 提供报告结果的默认时间单位
@OutputTimeUnit(TimeUnit.SECONDS)
public class JMHSample_01_HelloWorld {
    /**
     *
     */
    @Benchmark
    public void wellHelloThere() throws InterruptedException {
        TimeUnit.SECONDS.sleep(1);
    }

    /**
     *
     * @param args
     * @throws RunnerException
     */
    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                // 指定运行的基准测试类
                .include(JMHSample_01_HelloWorld.class.getSimpleName())
                // 指定不运行的基准测试类
                // .exclude(JMHSample_01_HelloWorld.class.getSimpleName())
                .forks(1)
                .jvmArgs("-Xmx2G")
                .build();

        new Runner(opt).run();
    }
}
