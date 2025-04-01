package com.future.demo;

import com.future.demo.bean.Status;
import com.future.demo.mapper.EnumMapper;
import org.apache.commons.lang3.RandomUtils;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * 测试枚举类型不同存储方式的查询性能差别
 */
@BenchmarkMode(Mode.Throughput)
@State(Scope.Benchmark) //使用的SpringBoot容器，都是无状态单例Bean，无安全问题，可以直接使用基准作用域BenchMark
@OutputTimeUnit(TimeUnit.SECONDS)
@Warmup(iterations = 3, time = 1, timeUnit = TimeUnit.SECONDS) //预热1s
@Measurement(iterations = 3, time = 5, timeUnit = TimeUnit.SECONDS) //测试也是1s、五遍
@Threads(-1)
public class EnumPerfTests {

    // 订单状态 0-创建，1-支付中，2-支付成功，3-支付失败，4-取消订单
    List<Integer> statusList = new ArrayList<Integer>() {{
        this.add(0);
        this.add(1);
        this.add(2);
        this.add(3);
        this.add(4);
    }};

    int statusLength = statusList.size();

    Status[] statusArr = Status.values();
    int statusArrLength = statusArr.length;

    EnumMapper enumMapper;

    //springBoot容器
    private ApplicationContext context;

    public static void main(String[] args) throws RunnerException {
        //使用注解之后只需要配置一下include即可，fork和warmup、measurement都是注解
        Options opt = new OptionsBuilder()
                .include(EnumPerfTests.class.getSimpleName())
                // 断点调试时fork=0
                .forks(1)
                // 发生错误停止测试
                .shouldFailOnError(true)
                .jvmArgs("-Xmx2G", "-server")
                .build();
        new Runner(opt).run();
    }

    /**
     * 初始化，获取springBoot容器，run即可，同时得到相关的测试对象
     */
    @Setup(Level.Trial)
    public void setup() {
        //容器获取
        context = SpringApplication.run(DemoSpringBootMybatisApplication.class);
        //获取对象
        enumMapper = context.getBean(EnumMapper.class);
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
    public void testEnumStoringAsInt(Blackhole blackhole) {
        int status = statusList.get(RandomUtils.nextInt(0, statusLength));
        int count = this.enumMapper.countEnumStoringAsIntByStatus(status);
        blackhole.consume(count);
    }

    @Benchmark
    public void testEnumStoringAsVarchar(Blackhole blackhole) {
        Status status = statusArr[RandomUtils.nextInt(0, statusArrLength)];
        int count = this.enumMapper.countEnumStoringAsVarcharByStatus(status);
        blackhole.consume(count);
    }

    @Benchmark
    public void testEnumStoringAsEnum(Blackhole blackhole) {
        Status status = statusArr[RandomUtils.nextInt(0, statusArrLength)];
        int count = this.enumMapper.countEnumStoringAsEnumByStatus(status);
        blackhole.consume(count);
    }
}
