package com.future.demo;

import org.apache.commons.lang3.RandomUtils;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Vector;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeUnit;

/**
 * 测试集合线程安全性能
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
public class CollectionThreadSafetyProblemBenchmarkTests {

    final static int IntStart = 0;
    final static int IntEnd = 10000;

    List<Integer> arrayList = new ArrayList<>();
    List<Integer> vector = new Vector<>();
    List<Integer> synchronizedList = Collections.synchronizedList(new ArrayList<>());
    List<Integer> copyOnWriteArrayList = new CopyOnWriteArrayList<>();

    public static void main(String[] args) throws RunnerException {
        //使用注解之后只需要配置一下include即可，fork和warmup、measurement都是注解
        Options opt = new OptionsBuilder()
                .include(CollectionThreadSafetyProblemBenchmarkTests.class.getSimpleName())
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

    /**
     * 测试 ArrayList 性能
     */
    @Benchmark
    public void testArrayList(Blackhole blackhole) {
        int randomInt = RandomUtils.nextInt(0, 3);
        if (randomInt == 0) {
            randomInt = RandomUtils.nextInt(IntStart, IntEnd);
            arrayList.add(randomInt);
        } else if (randomInt == 1) {
            randomInt = RandomUtils.nextInt(IntStart, IntEnd);
            try {
                arrayList.remove(randomInt);
            } catch (IndexOutOfBoundsException ignored) {

            }
        } else {
            randomInt = RandomUtils.nextInt(IntStart, IntEnd);
            try {
                blackhole.consume(arrayList.get(randomInt));
            } catch (IndexOutOfBoundsException ignored) {

            }
        }
    }

    /**
     * 测试 Vector 性能
     */
    @Benchmark
    public void testVector(Blackhole blackhole) {
        int randomInt = RandomUtils.nextInt(0, 3);
        if (randomInt == 0) {
            randomInt = RandomUtils.nextInt(IntStart, IntEnd);
            vector.add(randomInt);
        } else if (randomInt == 1) {
            randomInt = RandomUtils.nextInt(IntStart, IntEnd);
            try {
                vector.remove(randomInt);
            } catch (ArrayIndexOutOfBoundsException ignored) {

            }
        } else {
            randomInt = RandomUtils.nextInt(IntStart, IntEnd);
            try {
                blackhole.consume(vector.get(randomInt));
            } catch (ArrayIndexOutOfBoundsException ignored) {

            }
        }
    }

    /**
     * 测试 synchronizedList 性能
     */
    @Benchmark
    public void testSynchronizedList(Blackhole blackhole) {
        int randomInt = RandomUtils.nextInt(0, 3);
        if (randomInt == 0) {
            randomInt = RandomUtils.nextInt(IntStart, IntEnd);
            synchronizedList.add(randomInt);
        } else if (randomInt == 1) {
            randomInt = RandomUtils.nextInt(IntStart, IntEnd);
            try {
                synchronizedList.remove(randomInt);
            } catch (IndexOutOfBoundsException ignored) {

            }
        } else {
            randomInt = RandomUtils.nextInt(IntStart, IntEnd);
            try {
                blackhole.consume(synchronizedList.get(randomInt));
            } catch (IndexOutOfBoundsException | IllegalStateException ignored) {

            }
        }
    }

    /**
     * 测试 copyOnWriteArrayList 性能
     */
    @Benchmark
    public void testCopyOnWriteArrayList(Blackhole blackhole) {
        int randomInt = RandomUtils.nextInt(0, 3);
        if (randomInt == 0) {
            randomInt = RandomUtils.nextInt(IntStart, IntEnd);
            copyOnWriteArrayList.add(randomInt);
        } else if (randomInt == 1) {
            randomInt = RandomUtils.nextInt(IntStart, IntEnd);
            try {
                copyOnWriteArrayList.remove(randomInt);
            } catch (IndexOutOfBoundsException ignored) {

            }
        } else {
            randomInt = RandomUtils.nextInt(IntStart, IntEnd);
            try {
                blackhole.consume(copyOnWriteArrayList.get(randomInt));
            } catch (IndexOutOfBoundsException | IllegalStateException ignored) {

            }
        }
    }
}
