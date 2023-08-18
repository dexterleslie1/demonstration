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

// https://blog.csdn.net/a23452/article/details/126680840
@BenchmarkMode(Mode.Throughput)
@State(Scope.Benchmark) //使用的SpringBoot容器，都是无状态单例Bean，无安全问题，可以直接使用基准作用域BenchMark
@OutputTimeUnit(TimeUnit.SECONDS)
@Fork(1)  //整体平均执行1次
@Warmup(iterations = 5, time = 1, timeUnit = TimeUnit.SECONDS) //预热1s
@Measurement(iterations = 5, time = 1, timeUnit = TimeUnit.SECONDS) //测试也是1s、五遍
// 指定并发执行线程数
// https://stackoverflow.com/questions/39644383/jmh-run-benchmark-concurrently
@Threads(25)
public class Tests {

    TestService testService;
    MyService1 myService1;

    //springBoot容器
    private ApplicationContext context;

    public static void main(String[] args) throws RunnerException {
        //使用注解之后只需要配置一下include即可，fork和warmup、measurement都是注解
        Options opt = new OptionsBuilder()
                .include(Tests.class.getSimpleName())
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
        //容器获取
        context = SpringApplication.run(ApplicationTest.class);
        //获取对象
        testService = context.getBean(TestService.class);
        this.myService1 = context.getBean(MyService1.class);
    }

    /**
     * 测试的后处理操作，关闭容器，资源清理
     */
    @TearDown
    public void teardown() {
        //使用子类ConfigurableApplicationContext关闭
        ((ConfigurableApplicationContext) context).close();
    }

    // 实现方法的执行顺序
    // https://stackoverflow.com/questions/36261369/control-the-order-of-methods-using-jmh#:~:text=The%20order%20in%20which%20JMH,your%20methods%20in%20that%20order.
    @Benchmark
    public void test_0_case() {
        this.testService.add(1, 2);
        // NOTE: 演示jmh中mock，使用mock屏蔽此方法的抛出异常
        this.myService1.test1();
    }

    @Benchmark
    public void test_1_case() {
        this.testService.sub(2, 1);
    }

    @Benchmark
    public void test_2_case() throws InterruptedException {
        Thread.sleep(100);
    }
}
