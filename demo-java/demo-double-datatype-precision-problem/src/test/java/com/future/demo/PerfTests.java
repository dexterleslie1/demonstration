package com.future.demo;

import org.junit.jupiter.api.Assertions;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;

import java.math.BigDecimal;
import java.util.concurrent.TimeUnit;

// https://blog.csdn.net/a23452/article/details/126680840
@BenchmarkMode(Mode.Throughput)
@State(Scope.Benchmark) //使用的SpringBoot容器，都是无状态单例Bean，无安全问题，可以直接使用基准作用域BenchMark
@OutputTimeUnit(TimeUnit.SECONDS)
@Warmup(iterations = 3, time = 1, timeUnit = TimeUnit.SECONDS) //预热1s
@Measurement(iterations = 3, time = 5, timeUnit = TimeUnit.SECONDS) //测试也是1s、五遍
// 指定并发执行线程数
// https://stackoverflow.com/questions/39644383/jmh-run-benchmark-concurrently
@Threads(-1)
public class PerfTests {

    //springBoot容器
    private ApplicationContext context;

    public static void main(String[] args) throws RunnerException {
        //使用注解之后只需要配置一下include即可，fork和warmup、measurement都是注解
        Options opt = new OptionsBuilder()
                .include(PerfTests.class.getSimpleName())
                // 断点调试时fork=0
                .forks(1)
                // 发生错误停止测试
                .shouldFailOnError(true)
                .jvmArgs("-Xmx2G", "-server")
                .build();
        new Runner(opt).run();
    }

    /**
     * 初始化，获取springBoot容器，run即可，同时得到相关的测试对象
     */
    @Setup(Level.Trial)
    public void setup() {
        //容器获取
        context = SpringApplication.run(Application.class);
    }

    /**
     * 测试的后处理操作，关闭容器，资源清理
     */
    @TearDown(Level.Trial)
    public void teardown() {
        //使用子类ConfigurableApplicationContext关闭
        ((ConfigurableApplicationContext) context).close();
    }

    @Benchmark
    public void testMultiplyByUsingDoublePrimitiveDataType(Blackhole blackhole) {
        double d1 = 0.1;
        double d2 = 0.2;
        double d3 = d1 * d2;
        Assertions.assertEquals(0.020000000000000004, d3);
        blackhole.consume(d3);
    }

    @Benchmark
    public void testMultiplyByUsingDoubleBoxingDataType(Blackhole blackhole) {
        Double d1 = 0.1;
        Double d2 = 0.2;
        Double d3 = d1 * d2;
        Assertions.assertEquals(0.020000000000000004, d3);
        blackhole.consume(d3);
    }

    @Benchmark
    public void testMultiplyByUsingBigDecimalDataType(Blackhole blackhole) {
        BigDecimal d1 = new BigDecimal("0.1");
        BigDecimal d2 = new BigDecimal("0.2");
        BigDecimal d3 = d1.multiply(d2);
        Assertions.assertEquals(new BigDecimal("0.02"), d3);
        blackhole.consume(d3);
    }
}
