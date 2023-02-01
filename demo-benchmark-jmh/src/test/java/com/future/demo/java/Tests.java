package com.future.demo.java;

import org.junit.Assert;
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
@Warmup(iterations = 5,time = 1,timeUnit = TimeUnit.SECONDS) //预热1s
@Measurement(iterations = 5,time = 1,timeUnit = TimeUnit.SECONDS) //测试也是1s、五遍
public class Tests {
    //springBoot容器
    private ApplicationContext context;

    TestService testService;

    /**
     * 初始化，获取springBoot容器，run即可，同时得到相关的测试对象
     */
    @Setup
    public void setup() {
        //容器获取
        context = SpringApplication.run(Application.class);
        //获取对象
        testService = context.getBean(TestService.class);
    }

    /**
     * 测试的后处理操作，关闭容器，资源清理
     */
    @TearDown
    public void teardown() {
        //使用子类ConfigurableApplicationContext关闭
        ((ConfigurableApplicationContext)context).close();
    }

    @Benchmark
    public void test() {
        this.testService.add(1, 2);
    }

    public static void main(String[] args) throws RunnerException {
        //使用注解之后只需要配置一下include即可，fork和warmup、measurement都是注解
        Options opt = new OptionsBuilder()
                .include(Tests.class.getSimpleName())
                .jvmArgs("-Xmx2G")
                .build();
        new Runner(opt).run();
    }
}
