package com.future.demo;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import org.apache.commons.beanutils.BeanUtils;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.TimeUnit;

// https://blog.csdn.net/a23452/article/details/126680840
@BenchmarkMode(Mode.Throughput)
@State(Scope.Benchmark) //使用的SpringBoot容器，都是无状态单例Bean，无安全问题，可以直接使用基准作用域BenchMark
@OutputTimeUnit(TimeUnit.SECONDS)
@Fork(1)  //整体平均执行1次
@Warmup(iterations = 3, time = 5, timeUnit = TimeUnit.SECONDS) //预热1s
@Measurement(iterations = 3, time = 5, timeUnit = TimeUnit.SECONDS) //测试也是1s、五遍
// 指定并发执行线程数
// https://stackoverflow.com/questions/39644383/jmh-run-benchmark-concurrently
@Threads(-1)
public class BenchmarkTests {

    public static void main(String[] args) throws RunnerException {
        //使用注解之后只需要配置一下include即可，fork和warmup、measurement都是注解
        Options opt = new OptionsBuilder()
                .include(BenchmarkTests.class.getSimpleName())
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
        Logger beanutils = (Logger) LoggerFactory.getLogger("org.apache.commons.beanutils");
        beanutils.setLevel(Level.WARN);
    }

    /**
     * 测试的后处理操作，关闭容器，资源清理
     */
    @TearDown
    public void teardown() {
    }

    @Benchmark
    public void testSpringBeanUtilsCopyProperties(Blackhole blackhole) {
        SaleCustomerOrderFhslResponseVo saleCustomerOrderFhslResponseVo = new SaleCustomerOrderFhslResponseVo();
        SaleCustomerOrderInfoForListVo saleCustomerOrderInfoForListVo = new SaleCustomerOrderInfoForListVo();
        org.springframework.beans.BeanUtils.copyProperties(saleCustomerOrderFhslResponseVo, saleCustomerOrderInfoForListVo);
        blackhole.consume(saleCustomerOrderInfoForListVo);
    }

    @Benchmark
    public void testApacheCommonsBeanUtilsCopyProperties(Blackhole blackhole) throws InvocationTargetException, IllegalAccessException {
        SaleCustomerOrderFhslResponseVo saleCustomerOrderFhslResponseVo = new SaleCustomerOrderFhslResponseVo();
        SaleCustomerOrderInfoForListVo saleCustomerOrderInfoForListVo = new SaleCustomerOrderInfoForListVo();
        BeanUtils.copyProperties(saleCustomerOrderFhslResponseVo, saleCustomerOrderInfoForListVo);
        blackhole.consume(saleCustomerOrderInfoForListVo);
    }

    @Benchmark
    public void testSetterAndGetter(Blackhole blackhole) {
        SaleCustomerOrderFhslResponseVo saleCustomerOrderFhslResponseVo = new SaleCustomerOrderFhslResponseVo();
        SaleCustomerOrderInfoForListVo saleCustomerOrderInfoForListVo = new SaleCustomerOrderInfoForListVo();
        saleCustomerOrderInfoForListVo.setFhsl(saleCustomerOrderFhslResponseVo.getFhsl());
        saleCustomerOrderInfoForListVo.setFhps(saleCustomerOrderFhslResponseVo.getFhps());
        saleCustomerOrderInfoForListVo.setIsJd(saleCustomerOrderFhslResponseVo.getIsJd());
        saleCustomerOrderInfoForListVo.setJdSj(saleCustomerOrderFhslResponseVo.getJdSj());
        saleCustomerOrderInfoForListVo.setJdrId(saleCustomerOrderFhslResponseVo.getJdrId());
        saleCustomerOrderInfoForListVo.setThsl(saleCustomerOrderFhslResponseVo.getThsl());
        saleCustomerOrderInfoForListVo.setThps(saleCustomerOrderFhslResponseVo.getThps());
        saleCustomerOrderInfoForListVo.setWfsl(saleCustomerOrderFhslResponseVo.getWfsl());
        saleCustomerOrderInfoForListVo.setWfps(saleCustomerOrderFhslResponseVo.getWfps());
        saleCustomerOrderInfoForListVo.setYfPs(saleCustomerOrderFhslResponseVo.getYfPs());
        saleCustomerOrderInfoForListVo.setYfSl(saleCustomerOrderFhslResponseVo.getYfSl());
        saleCustomerOrderInfoForListVo.setYfps(saleCustomerOrderFhslResponseVo.getYfps());
        saleCustomerOrderInfoForListVo.setYfsl(saleCustomerOrderFhslResponseVo.getYfsl());
        saleCustomerOrderInfoForListVo.setDjFhsl(saleCustomerOrderFhslResponseVo.getDjFhsl());
        saleCustomerOrderInfoForListVo.setDjFhps(saleCustomerOrderFhslResponseVo.getDjFhps());
        saleCustomerOrderInfoForListVo.setJd(saleCustomerOrderFhslResponseVo.getJd());
        saleCustomerOrderInfoForListVo.setWphPs(saleCustomerOrderFhslResponseVo.getWphPs());
        saleCustomerOrderInfoForListVo.setWphSl(saleCustomerOrderFhslResponseVo.getWphSl());
        saleCustomerOrderInfoForListVo.setJxzJd(saleCustomerOrderFhslResponseVo.getJxzJd());
        saleCustomerOrderInfoForListVo.setWcl(saleCustomerOrderFhslResponseVo.getWcl());
        blackhole.consume(saleCustomerOrderInfoForListVo);
    }

    @Benchmark
    public void testMapStruct(Blackhole blackhole) {
        SaleCustomerOrderFhslResponseVo saleCustomerOrderFhslResponseVo = new SaleCustomerOrderFhslResponseVo();
        SaleCustomerOrderInfoForListVo saleCustomerOrderInfoForListVo = new SaleCustomerOrderInfoForListVo();
        SaleCustomerOrderFhslToListMapper.INSTANCE.copy(saleCustomerOrderFhslResponseVo, saleCustomerOrderInfoForListVo);
        blackhole.consume(saleCustomerOrderInfoForListVo);
    }
}

