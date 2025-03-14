package com.future.demo;

import org.junit.jupiter.api.Assertions;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;

import java.util.Collections;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@BenchmarkMode(Mode.Throughput)
@State(Scope.Benchmark) //使用的SpringBoot容器，都是无状态单例Bean，无安全问题，可以直接使用基准作用域BenchMark
@OutputTimeUnit(TimeUnit.SECONDS)
@Warmup(iterations = 3, time = 1, timeUnit = TimeUnit.SECONDS) //预热1s
@Measurement(iterations = 3, time = 5, timeUnit = TimeUnit.SECONDS) //测试也是1s、五遍
@Threads(64)
public class PerfTests {

    //springBoot容器
    ApplicationContext context;
    StringRedisTemplate redisTemplate;

    DefaultRedisScript<String> defaultRedisScript;

    public static void main(String[] args) throws RunnerException {
        //使用注解之后只需要配置一下include即可，fork和warmup、measurement都是注解
        Options opt = new OptionsBuilder()
                .include(PerfTests.class.getSimpleName())
                // 断点调试时fork=0
                .forks(1)
                // 发生错误停止测试
                .shouldFailOnError(true)
                .jvmArgs("-Xmx2G",
                        "-server")
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
        redisTemplate = context.getBean(StringRedisTemplate.class);

        defaultRedisScript = new DefaultRedisScript<>();
        defaultRedisScript.setLocation(new ClassPathResource("test.lua"));
        defaultRedisScript.setResultType(String.class);
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
    public void testSet() {
        String uuidStr = UUID.randomUUID().toString();
        this.redisTemplate.opsForValue().set(uuidStr, uuidStr);
    }

    @Benchmark
    public void testGet(Blackhole blackhole) {
        String uuidStr = UUID.randomUUID().toString();
        String uuidStrResult = this.redisTemplate.opsForValue().get(uuidStr);
        blackhole.consume(uuidStrResult);
    }

    @Benchmark
    public void testSetAndGet(Blackhole blackhole) {
        String uuidStr = UUID.randomUUID().toString();
        this.redisTemplate.opsForValue().set(uuidStr, uuidStr);
        String uuidStrResult = this.redisTemplate.opsForValue().get(uuidStr);
        blackhole.consume(uuidStrResult);
    }

    // 注意：ReadFrom.REPLICA_PREFERRED 时不能运行下面测试，因为不能在 slave 节点执行 set 命令
    /*@Benchmark
    public void testSetAndGetByUsingLuaScript(Blackhole blackhole) {
        String uuidStr = UUID.randomUUID().toString();
        String uuidStrResult = this.redisTemplate.execute(defaultRedisScript, Collections.emptyList(), uuidStr);
        blackhole.consume(uuidStrResult);
    }*/
}