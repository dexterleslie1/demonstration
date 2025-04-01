package com.future.demo.java;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * 测试 double 数据类型格式化性能
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
public class DoubleFormatBenchmarkTests {

    Random random = new Random(System.currentTimeMillis());

    public static void main(String[] args) throws RunnerException {
        //使用注解之后只需要配置一下include即可，fork和warmup、measurement都是注解
        Options opt = new OptionsBuilder()
                .include(DoubleFormatBenchmarkTests.class.getSimpleName())
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

    final static double DoubleVar = 123.45678966666;
    final static String StringFormat = "%.6f";

    @Benchmark
    public void testStringFormatWithDoubleParse(Blackhole blackhole) {
        double d = Double.parseDouble(String.format(StringFormat, DoubleVar));
        blackhole.consume(d);
    }

    final static int Scale = 6;

    @Benchmark
    public void testBigDecimal(Blackhole blackhole) {
        BigDecimal bigDecimal = new BigDecimal(DoubleVar).setScale(Scale, RoundingMode.DOWN);
        double d = bigDecimal.doubleValue();
        blackhole.consume(d);
    }

    final static String DecimalFormatString = "#.######";

    @Benchmark
    public void testDecimalFormat(Blackhole blackhole) {
        DecimalFormat decimalFormat = new DecimalFormat(DecimalFormatString);
        decimalFormat.setRoundingMode(RoundingMode.DOWN);
        double d = Double.parseDouble(decimalFormat.format(DoubleVar));
        blackhole.consume(d);
    }
}
