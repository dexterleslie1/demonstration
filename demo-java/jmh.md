# `jmh`的用法

## 什么是`jmh`呢？

JMH，全称Java Microbenchmark Harness，是一个用于测量Java和其他JVM（Java虚拟机）语言编写的基准测试的工具。这个工具由Java虚拟机团队开发，旨在提供一种简单且准确的方式来测量代码的运行性能。

支持多种测试模式，包括吞吐量（Throughput）、平均时间（AverageTime）等，用户可以根据需要选择合适的测试模式。

## `jmh`入门

> [入门例子详细用法请参考](https://gitee.com/dexterleslie/demonstration/tree/master/demo-java/demo-benchmark-jmh)

`maven`设置如下：

```xml
<dependency>
    <groupId>org.openjdk.jmh</groupId>
    <artifactId>jmh-core</artifactId>
    <version>1.27</version>
</dependency>
<dependency>
    <groupId>org.openjdk.jmh</groupId>
    <artifactId>jmh-generator-annprocess</artifactId>
    <version>1.27</version>
</dependency>
```

`GettingStartedTests.java`代码如下：

```java

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.util.concurrent.TimeUnit;

// https://blog.csdn.net/tanhongwei1994/article/details/120419321
// 吞吐量模式：每秒多少次调用
@BenchmarkMode(Mode.Throughput)
// 进行2次预热，每次预热持续1秒
@Warmup(iterations = 2, time = 2, timeUnit = TimeUnit.SECONDS)
// 进行3次基准测试，每次基准测试持续3秒
@Measurement(iterations = 3, time = 3, timeUnit = TimeUnit.SECONDS)
// 提供报告结果的默认时间单位
@OutputTimeUnit(TimeUnit.SECONDS)
// 断点调试时fork=0
@Fork(value = 1, jvmArgs = {"-Xmx2G", "-server"})
@Threads(1)
public class GettingStartedTests {

    /**
     *
     */
    @Benchmark
    public void test() throws InterruptedException {
        TimeUnit.SECONDS.sleep(1);
    }

    /**
     * @param args
     * @throws RunnerException
     */
    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                // 指定运行的基准测试类
                .include(GettingStartedTests.class.getSimpleName())
                // 指定不运行的基准测试类
                // .exclude(JMHSample_01_HelloWorld.class.getSimpleName())
                // 发生错误停止测试
                .shouldFailOnError(true)
                .build();

        new Runner(opt).run();
    }
}
```

编译入门例子

```bash
mvn package
```

运行`jmh`测试

```bash
java -jar target/benchmark-jmh.jar
```

或者通过`IDEA`选择测试`GettingStartedTests`点击右键运行测试