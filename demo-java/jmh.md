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



## `@State`用法

JMH（Java Microbenchmark Harness）是Java的一个微基准测试工具套件，它允许开发人员对Java代码进行精确的性能测试。在JMH中，`@State`注解是一个非常重要的组成部分，它用于声明一个类作为基准测试的状态对象，并定义这个状态对象的共享范围。以下是`@State`注解用法的详细解析：

`@State`注解的作用

- `@State`注解用于标记一个类，表明这个类是一个状态对象，可以在基准测试方法中被引用。
- 它允许开发者控制状态对象的实例化方式以及在线程间的共享方式。

`@State`注解的参数

`@State`注解接受一个`Scope`枚举作为参数，用于指定状态对象的共享范围。`Scope`主要有以下几种：

1. `Scope.Thread`：
   - 每个测试线程都会有一个该状态对象的独立实例。
   - 这意味着每个线程在访问状态对象时，都是操作自己的私有副本，不会影响到其他线程的状态。
   - 这是默认的作用域。
2. `Scope.Benchmark`：
   - 所有测试线程都会共享同一个状态对象实例。
   - 这适用于测试多线程环境下对共享资源的访问和修改，可以模拟并发访问共享资源的场景。
3. `Scope.Group`：
   - 同一个线程组内的所有线程会共享同一个状态对象实例。
   - 同一线程组每个测试线程都会有一个该状态对象的独立实例。
   - 线程组的概念在JMH中相对较为特殊，它允许开发者将多个基准测试方法分组，并在同一组内共享状态。

使用示例

下面是一个简单的使用`@State`注解的示例：

```java
import org.openjdk.jmh.annotations.Benchmark;  
import org.openjdk.jmh.annotations.Scope;  
import org.openjdk.jmh.annotations.State;  
  
@State(Scope.Benchmark)  
public class SharedState {  
    volatile double x = Math.PI;  
}  
  
@State(Scope.Thread)  
public class UnsharedState {  
    volatile double x = Math.PI;  
}  
  
public class MyBenchmark {  
  
    @Benchmark  
    public void testSharedState(SharedState state) {  
        // 所有线程都会访问并修改同一个SharedState实例的x字段  
        state.x++;  
    }  
  
    @Benchmark  
    public void testUnsharedState(UnsharedState state) {  
        // 每个线程都会访问并修改自己独立的UnsharedState实例的x字段  
        state.x++;  
    }  
}
```

在这个示例中，`SharedState`类被标记为`Scope.Benchmark`，表示所有测试线程都会共享同一个`SharedState`实例。而`UnsharedState`类被标记为`Scope.Thread`，表示每个测试线程都会有一个独立的`UnsharedState`实例。

注意事项

- 在使用`@State`注解时，需要确保状态对象中的字段是线程安全的，特别是在`Scope.Benchmark`和`Scope.Group`作用域下。
- 基准测试方法可以通过参数接收状态对象，JMH会在调用基准测试方法时自动注入相应的状态对象实例。
- 合理使用`@State`注解可以大大提高基准测试的准确性和可靠性，帮助开发者更好地理解和优化Java代码的性能。

### 示例

`@State`详细用法示例代码请参考 [链接](https://gitee.com/dexterleslie/demonstration/blob/master/demo-java/demo-benchmark-jmh/src/test/java/com/future/demo/java/JMHSample_03_States_Tests.java)

```java
package com.future.demo.java;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.util.concurrent.TimeUnit;

@Warmup(iterations = 2, time = 1, timeUnit = TimeUnit.SECONDS)
// 进行3次基准测试，每次基准测试持续3秒
@Measurement(iterations = 2, time = 1, timeUnit = TimeUnit.SECONDS)
// 提供报告结果的默认时间单位
@OutputTimeUnit(TimeUnit.SECONDS)
public class JMHSample_03_States_Tests {
    // https://blog.csdn.net/m0_37607945/article/details/111479890
    // https://github.com/melix/jmh-gradle-example/blob/master/src/jmh/java/org/openjdk/jmh/samples/JMHSample_03_States.java
    // 基础测试内线程间共享
    //@State(Scope.Benchmark)
    // 基准测试内线程内共享
    //@State(Scope.Thread)
    // 基础测试间变量共享
    @State(Scope.Group)
    public static class MyState {
        public Object internalObject = new Object();
    }

    @Benchmark
    @Group(value = "myGroup")
    public void benchmark1(MyState state) throws InterruptedException {
        System.out.println(Thread.currentThread().getName() + " ,internalObject=" + state.internalObject);
        TimeUnit.SECONDS.sleep(1);
    }

    @Benchmark
    @Group(value = "myGroup")
    public void benchmark2(MyState state) throws InterruptedException {
        System.out.println(Thread.currentThread().getName() + " ,internalObject=" + state.internalObject);
        TimeUnit.SECONDS.sleep(1);
    }

    @Benchmark
    @Group(value = "myGroup2")
    public void benchmark3(MyState state) throws InterruptedException {
        System.out.println(Thread.currentThread().getName() + " ,internalObject=" + state.internalObject);
        TimeUnit.SECONDS.sleep(1);
    }

    /**
     * @param args
     * @throws RunnerException
     */
    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(JMHSample_03_States_Tests.class.getSimpleName())
                // 2条并发执行线程
                .threads(2)
                // 断点调试时fork=0
                .forks(1)
                // 发生错误停止测试
                .shouldFailOnError(true)
                .build();

        new Runner(opt).run();
    }
}

```



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
@Warmup(iterations = 2, time = 2, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 2, time = 2, timeUnit = TimeUnit.SECONDS)
// 提供报告结果的默认时间单位
@OutputTimeUnit(TimeUnit.SECONDS)
// 断点调试时fork=0
@Fork(value = 1, jvmArgs = {"-Xmx2G", "-server"})
@Threads(2)
public class JMHSample_Setup_Teardown {
    @State(Scope.Benchmark)
    public static class MyStateBenchmark {
        public Object internalObject;

        // Trial是JMH性能测试中的最高层级，它代表了一次完整的基准测试过程。
        // 在每次Benchmark测试之前或之后，可以定义一些操作作为Trial级别的操作，这些操作会在每次完整的测试过程之前或之后执行。
        // Trial级别的操作允许开发者在测试前后进行资源的准备和清理工作，如数据库的初始化、缓存的清理等。
        //@Setup(Level.Trial)
        // Iteration是JMH性能测试中的一个重要层级，它代表了一次测试迭代过程。
        // 在预热（Warmup）和测量（Measurement）阶段，都会进行多次Iteration来收集数据。
        //@Setup(Level.Iteration)
        // Invocation是JMH性能测试中最细粒度的层级，它代表了一次benchmark方法的调用。
        @Setup(Level.Invocation)
        public void setup() {
            this.internalObject = new Object();
            System.out.println("++++++++ benchmark setup, internalObject=" + this.internalObject + ", threadName=" + Thread.currentThread().getName());
        }

        //@TearDown(Level.Trial)
        //@TearDown(Level.Iteration)
        @TearDown(Level.Invocation)
        public void teardown() {
            System.out.println("++++++++ benchmark teardown, internalObject=" + this.internalObject + ", threadName=" + Thread.currentThread().getName());
        }
    }

    @State(Scope.Thread)
    public static class MyStateThread {
        public Object internalObject;

        //@Setup(Level.Trial)
        //@Setup(Level.Iteration)
        @Setup(Level.Invocation)
        public void setup() {
            this.internalObject = new Object();
            System.out.println("++++++++ thread setup, internalObject=" + this.internalObject + ", threadName=" + Thread.currentThread().getName());
        }

        //@TearDown(Level.Trial)
        //@TearDown(Level.Iteration)
        @TearDown(Level.Invocation)
        public void teardown() {
            System.out.println("++++++++ thread teardown, internalObject=" + this.internalObject + ", threadName=" + Thread.currentThread().getName());
        }
    }

    @Benchmark
    public void benchmark1(MyStateBenchmark myStateBenchmark, MyStateThread myStateThread) throws InterruptedException {
        System.out.println("myStateBenchmark.internalObject=" + myStateBenchmark.internalObject + ", myStateThread.internalObject=" + myStateThread.internalObject + ", threadName=" + Thread.currentThread().getName());
        TimeUnit.SECONDS.sleep(1);
    }

    @Benchmark
    public void benchmark2(MyStateBenchmark myStateBenchmark, MyStateThread myStateThread) throws InterruptedException {
        System.out.println("myStateBenchmark.internalObject=" + myStateBenchmark.internalObject + ", myStateThread.internalObject=" + myStateThread.internalObject + ", threadName=" + Thread.currentThread().getName());
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



## `jmh`和`spring`集成

详细配置请参考`https://gitee.com/dexterleslie/demonstration/blob/master/demo-java/demo-benchmark-jmh/src/test/java/com/future/demo/java/SpringIntegrationTests.java`

示例：

```java
package com.future.demo.java;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;

import java.util.concurrent.TimeUnit;

/**
 * jmh和spring集成
 */
@BenchmarkMode(Mode.Throughput)
@State(Scope.Benchmark) //使用的SpringBoot容器，都是无状态单例Bean，无安全问题，可以直接使用基准作用域BenchMark
@OutputTimeUnit(TimeUnit.SECONDS)
@Fork(1)  //整体平均执行1次
@Warmup(iterations = 5, time = 1, timeUnit = TimeUnit.SECONDS) //预热1s
@Measurement(iterations = 3, time = 30, timeUnit = TimeUnit.SECONDS) //测试也是1s、五遍
@Threads(-1)
public class SpringIntegrationTests {

    TestService testService;

    //springBoot容器
    private ApplicationContext context;

    public static void main(String[] args) throws RunnerException {
        //使用注解之后只需要配置一下include即可，fork和warmup、measurement都是注解
        Options opt = new OptionsBuilder()
                .include(SpringIntegrationTests.class.getSimpleName())
                // 断点调试时fork=0
                .forks(1)
                // 发生错误停止测试
                .shouldFailOnError(true)
                .jvmArgs("-Xmx2G",
                        "-server"/*,
                        "-XX:+UseG1GC",
                        "-XX:InitialHeapSize=8g",
                        "-XX:MaxHeapSize=8g",
                        "-XX:MaxGCPauseMillis=500",
                        "-XX:+DisableExplicitGC",
                        "-XX:+UseStringDeduplication",
                        "-XX:+ParallelRefProcEnabled",
                        "-XX:MaxMetaspaceSize=512m",
                        "-XX:MaxTenuringThreshold=1"*/)
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
        //获取对象
        testService = context.getBean(TestService.class);
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
    public void test() {
        this.testService.add(1, 2);
    }
}
```



## 参数化测试

注意：参数化测试中的参数在 @Setup 和 @TearDown 方法中也能够引用。

详细用法请参考示例`https://gitee.com/dexterleslie/demonstration/blob/master/demo-java/demo-benchmark-jmh/src/test/java/com/future/demo/java/ParametersTests.java`
