package com.future.demo.java;

import org.apache.commons.lang3.RandomStringUtils;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

// https://blog.csdn.net/a23452/article/details/126680840
@BenchmarkMode(Mode.AverageTime)
@State(Scope.Benchmark) //使用的SpringBoot容器，都是无状态单例Bean，无安全问题，可以直接使用基准作用域BenchMark
@OutputTimeUnit(TimeUnit.MICROSECONDS)
@Fork(1)  //整体平均执行1次
@Warmup(iterations = 5, time = 5, timeUnit = TimeUnit.SECONDS) //预热1s
@Measurement(iterations = 5, time = 10, timeUnit = TimeUnit.SECONDS) //测试也是1s、五遍
// 指定并发执行线程数
// https://stackoverflow.com/questions/39644383/jmh-run-benchmark-concurrently
@Threads(-1)
public class StringContainsAndIndexOfBenchmarkTests {

    Random random = new Random();
    List<String> stringList = new ArrayList<>();
    List<String> stringList1 = new ArrayList<>();

    public static void main(String[] args) throws RunnerException {
        //使用注解之后只需要配置一下include即可，fork和warmup、measurement都是注解
        Options opt = new OptionsBuilder()
                .include(StringContainsAndIndexOfBenchmarkTests.class.getSimpleName())
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
    @Setup
    public void setup() {
        for (int i = 0; i < 10240; i++) {
            int randomLength = random.nextInt(10240);
            if (randomLength <= 0)
                randomLength = 1;
            stringList.add(RandomStringUtils.randomAlphanumeric(randomLength));

            randomLength = random.nextInt(1024);
            if (randomLength <= 0)
                randomLength = 1;
            stringList1.add(RandomStringUtils.randomAlphanumeric(randomLength));
        }
    }

    /**
     * 测试的后处理操作，关闭容器，资源清理
     */
    @TearDown
    public void teardown() {
    }

    @Benchmark
    public void test_contains(Blackhole blackhole) {
        int randomIndex = random.nextInt(stringList.size());
        String string1 = stringList.get(randomIndex);
        randomIndex = random.nextInt(stringList1.size());
        String string2 = stringList1.get(randomIndex);
        boolean b = string1.contains(string2);
        blackhole.consume(b);
    }

    @Benchmark
    public void test_indexOf(Blackhole blackhole) {
        int randomIndex = random.nextInt(stringList.size());
        String string1 = stringList.get(randomIndex);
        randomIndex = random.nextInt(stringList1.size());
        String string2 = stringList1.get(randomIndex);
        int index = string1.indexOf(string2);
        blackhole.consume(index);
    }

}
