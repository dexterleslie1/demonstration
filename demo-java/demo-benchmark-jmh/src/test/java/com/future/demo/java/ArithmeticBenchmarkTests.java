package com.future.demo.java;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * 测试算术运算性能
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
public class ArithmeticBenchmarkTests {

    Random random = new Random(System.currentTimeMillis());

    public static void main(String[] args) throws RunnerException {
        //使用注解之后只需要配置一下include即可，fork和warmup、measurement都是注解
        Options opt = new OptionsBuilder()
                .include(ArithmeticBenchmarkTests.class.getSimpleName())
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
    }

    /**
     * 测试的后处理操作，关闭容器，资源清理
     */
    @TearDown(Level.Trial)
    public void teardown() {
    }

    final static int TotalAmount = 10000;
    final static int AmountVar = 320;
    final static double DoubleVar = 123.45678966666;

    @Benchmark
    public void testPrimitiveDataType(Blackhole blackhole) {
        double d1 = AmountVar * (DoubleVar - 1);
        double d2 = (d1 - (TotalAmount - AmountVar)) / TotalAmount;
        blackhole.consume(d2);
    }

    final static int Scale = 6;

    @Benchmark
    public void testBigDecimalDataType(Blackhole blackhole) {
        double d1 = AmountVar * (DoubleVar - 1);
        double d2 = (d1 - (TotalAmount - AmountVar)) / TotalAmount;
        BigDecimal bigDecimal1 = new BigDecimal(d2).setScale(Scale, RoundingMode.DOWN);
        blackhole.consume(bigDecimal1.doubleValue());
    }
}
