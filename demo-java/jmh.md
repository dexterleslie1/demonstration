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



## `@Setup`和`@TearDown`

### `@Setup`和`@TearDown`的`Level`

在JMH（Java Microbenchmark Harness）中，`Trial`、`Iteration`和`Invocation`是理解其测试过程和执行机制的关键概念。它们各自代表了不同的测试层级和粒度，共同构成了JMH性能测试的基础。

1. **Trial**

**定义与作用**：

- Trial是JMH性能测试中的最高层级，它代表了一次完整的基准测试过程。
- 在每次Benchmark测试之前或之后，可以定义一些操作作为Trial级别的操作，这些操作会在每次完整的测试过程之前或之后执行。
- Trial级别的操作允许开发者在测试前后进行资源的准备和清理工作，如数据库的初始化、缓存的清理等。

**注意**：

- Trial级别的操作不会直接影响测试结果，但它们是保证测试环境一致性和测试结果可靠性的重要手段。

2. **Iteration**

**定义与作用**：

- Iteration是JMH性能测试中的一个重要层级，它代表了一次测试迭代过程。
- 在预热（Warmup）和测量（Measurement）阶段，都会进行多次Iteration来收集数据。
- 每次Iteration都包含了一组Invocations（即benchmark方法的多次调用）。
- Iteration是JMH进行测试的最小单位，它允许开发者在每次迭代前后执行一些操作，以观察不同迭代之间的性能变化。

**注意**：

- Iteration的次数、时长等参数可以通过JMH的注解进行配置，以满足不同的测试需求。
- 由于JVM的JIT（即时编译器）机制，多次迭代后代码的执行速度可能会发生变化，因此预热阶段是非常重要的。

3. **Invocation**

**定义与作用**：

- Invocation是JMH性能测试中最细粒度的层级，它代表了一次benchmark方法的调用。
- 在每次Iteration中，会进行多次Invocations来收集足够的数据以计算性能指标（如吞吐量、平均时间等）。
- Invocation级别的操作允许开发者在每次方法调用前后执行一些操作，以观察方法调用的具体性能表现。

**注意**：

- Invocation是JMH性能测试中最直接反映代码性能的部分。
- 为了确保测试结果的准确性，需要避免在benchmark方法中进行无用的操作或调用外部资源。

总结

JMH通过`Trial`、`Iteration`和`Invocation`三个层级来组织性能测试过程，它们各自代表了不同的测试粒度和执行阶段。在实际使用中，开发者可以根据具体需求配置这些参数来进行性能测试，并通过分析测试结果来优化代码性能。同时，需要注意的是，为了确保测试结果的准确性和可靠性，需要避免在测试过程中引入不必要的噪音和偏差。

### `@Setup`和`@TearDown`的用法

详细用法请参考 [链接](https://gitee.com/dexterleslie/demonstration/blob/master/demo-java/demo-benchmark-jmh/src/main/java/com/future/demo/java/samples/JMHSample_Setup_Teardown.java)

示例：

```java
package com.future.demo.java.samples;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.util.concurrent.TimeUnit;

// https://blog.csdn.net/wyaoyao93/article/details/115727005

@Warmup(iterations = 3, time = 2, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 2, time = 2, timeUnit = TimeUnit.SECONDS)
// 提供报告结果的默认时间单位
@OutputTimeUnit(TimeUnit.SECONDS)
@State(Scope.Benchmark)
// 断点调试时fork=0
@Fork(value = 1, jvmArgs = {"-Xmx2G", "-server"})
@Threads(1)
public class JMHSample_Setup_Teardown {
    // Trial是JMH性能测试中的最高层级，它代表了一次完整的基准测试过程。
    // 在每次Benchmark测试之前或之后，可以定义一些操作作为Trial级别的操作，这些操作会在每次完整的测试过程之前或之后执行。
    // Trial级别的操作允许开发者在测试前后进行资源的准备和清理工作，如数据库的初始化、缓存的清理等。
    @Setup(Level.Trial)
    public void setupTrial() {
         System.out.println("++++++++ Trial setup");
    }
    @TearDown(Level.Trial)
    public void teardownTrial() {
         System.out.println("++++++++ Trial teardown");
    }

    // Iteration是JMH性能测试中的一个重要层级，它代表了一次测试迭代过程。
    // 在预热（Warmup）和测量（Measurement）阶段，都会进行多次Iteration来收集数据。
    @Setup(Level.Iteration)
    public void setupIteration() {
        System.out.println("++++++++ Iteration setup");
    }
    @TearDown(Level.Iteration)
    public void teardownIteration() {
        System.out.println("++++++++ Iteration teardown");
    }

    // Invocation是JMH性能测试中最细粒度的层级，它代表了一次benchmark方法的调用。
    @Setup(Level.Invocation)
    public void setupInvocation() {
        System.out.println("++++++++ Invocation setup");
    }
    @TearDown(Level.Invocation)
    public void teardownInvocation() {
        System.out.println("++++++++ Invocation teardown");
    }

    @Benchmark
    public void benchmark1() throws InterruptedException {
        TimeUnit.SECONDS.sleep(1);
    }

    @Benchmark
    public void benchmark2() throws InterruptedException {
        TimeUnit.SECONDS.sleep(1);
    }

    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(JMHSample_Setup_Teardown.class.getSimpleName())
                // 发生错误停止测试
                .shouldFailOnError(true)
                .build();

        new Runner(opt).run();
    }
}
```

