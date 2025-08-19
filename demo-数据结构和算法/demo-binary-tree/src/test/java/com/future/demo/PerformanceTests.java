package com.future.demo;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.util.TreeSet;
import java.util.concurrent.TimeUnit;

// https://blog.csdn.net/tanhongwei1994/article/details/120419321
// 吞吐量模式：每秒多少次调用
@BenchmarkMode(Mode.Throughput)
// 进行2次预热，每次预热持续1秒
@Warmup(iterations = 2, time = 3, timeUnit = TimeUnit.SECONDS)
// 进行3次基准测试，每次基准测试持续3秒
@Measurement(iterations = 3, time = 10, timeUnit = TimeUnit.SECONDS)
// 提供报告结果的默认时间单位
@OutputTimeUnit(TimeUnit.SECONDS)
// 断点调试时fork=0
@Fork(value = 1, jvmArgs = {"-Xmx2G", "-server"})
@Threads(16)
@State(Scope.Benchmark)
public class PerformanceTests {
    int total = 1000000;
    TreeSet binaryTree = null;

    int intArray[] = null;

    @Setup
    public void setup() {
        binaryTree = new TreeSet();
        for (int i = 1; i <= total; i++) {
            binaryTree.add(i);
        }

        intArray = new int[total];
        for (int i = 1; i <= total; i++) {
            intArray[i - 1] = i;
        }
    }

    /**
     *
     */
    @Benchmark
    public void testTreeSetContains() {
        binaryTree.contains(total);
    }

    @Benchmark
    public void testIntArraySearch() {
        for (int i = 0; i < intArray.length; i++) {
            if (intArray[i] == total) {
                break;
            }
        }
    }

    /**
     * @param args
     * @throws RunnerException
     */
    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                // 指定运行的基准测试类
                .include(PerformanceTests.class.getSimpleName())
                // 指定不运行的基准测试类
                // .exclude(JMHSample_01_HelloWorld.class.getSimpleName())
                // 发生错误停止测试
                .shouldFailOnError(true)
                .build();

        new Runner(opt).run();
    }
}
