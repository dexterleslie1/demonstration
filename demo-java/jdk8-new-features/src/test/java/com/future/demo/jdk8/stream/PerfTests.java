package com.future.demo.jdk8.stream;

import org.junit.Assert;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;

// 并行和非并行 Stream 性能测试对比
@BenchmarkMode(Mode.Throughput)
@State(Scope.Benchmark) //使用的SpringBoot容器，都是无状态单例Bean，无安全问题，可以直接使用基准作用域BenchMark
@OutputTimeUnit(TimeUnit.SECONDS)
@Warmup(iterations = 3, time = 1, timeUnit = TimeUnit.SECONDS) //预热1s
@Measurement(iterations = 3, time = 5, timeUnit = TimeUnit.SECONDS) //测试也是1s、五遍
@Threads(-1)
public class PerfTests {

    Integer sumTotal = null;
    List<Integer> dataList = null;

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
     * 初始化
     */
    @Setup(Level.Trial)
    public void setup() {
        dataList = new ArrayList<>();
        sumTotal = 0;
        for (int i = 0; i < 1000000; i++) {
            dataList.add(i);
            sumTotal += i;
        }
    }

    /**
     * 测试的后处理操作
     */
    @TearDown(Level.Trial)
    public void teardown() {
    }

    @Benchmark
    public void testNoneParallel(Blackhole blackhole) {
        Stream<Integer> stream = this.dataList.stream();
        Integer sum = stream.collect(Collectors.summingInt(o -> o));
        Assert.assertEquals(sumTotal, sum);
        blackhole.consume(sum);
    }

    @Benchmark
    public void testParallel(Blackhole blackhole) {
        Stream<Integer> stream = this.dataList.parallelStream();
        Integer sum = stream.collect(Collectors.summingInt(o -> o));
        Assert.assertEquals(sumTotal, sum);
        blackhole.consume(sum);
    }
}
