package com.future.demo.bigkey;

import com.future.demo.Application;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.util.Base64Utils;

import java.util.*;
import java.util.concurrent.TimeUnit;

// https://blog.csdn.net/a23452/article/details/126680840
@BenchmarkMode(Mode.Throughput)
@State(Scope.Benchmark) //使用的SpringBoot容器，都是无状态单例Bean，无安全问题，可以直接使用基准作用域BenchMark
@OutputTimeUnit(TimeUnit.SECONDS)
@Warmup(iterations = 3, time = 1, timeUnit = TimeUnit.SECONDS) //预热1s
@Measurement(iterations = 3, time = 5, timeUnit = TimeUnit.SECONDS) //测试也是1s、五遍
// 指定并发执行线程数
// https://stackoverflow.com/questions/39644383/jmh-run-benchmark-concurrently
@Threads(64)
public class StringBigKeyTests {

    final static Random RANDOM = new Random(System.currentTimeMillis());

    // string 类型 value 的最大长度
    @Param(value = {"2", "1024", "2048", "10240"/* 10KB */, "20480"/* 20KB */, "65536"/* 64KB */, "524288" /* 512KB */})
    Integer maximumLength;

    StringRedisTemplate redisTemplate;

    //springBoot容器
    ApplicationContext context;

    List<String> keyList = new ArrayList<>();

    public static void main(String[] args) throws RunnerException {
        //使用注解之后只需要配置一下include即可，fork和warmup、measurement都是注解
        Options opt = new OptionsBuilder()
                .include(StringBigKeyTests.class.getSimpleName())
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

        Objects.requireNonNull(this.redisTemplate.getConnectionFactory()).getConnection().flushDb();
        for (int i = 0; i < 10000; i++) {
            String uuidStr = UUID.randomUUID().toString();
            int length = RANDOM.nextInt(maximumLength);
            byte[] byteArr = new byte[length];
            RANDOM.nextBytes(byteArr);
            String randomStr = Base64Utils.encodeToString(byteArr);
            this.redisTemplate.opsForValue().set(uuidStr, randomStr);
            this.keyList.add(uuidStr);
        }
    }

    /**
     * 测试的后处理操作，关闭容器，资源清理
     */
    @TearDown(Level.Trial)
    public void teardown() {
        //使用子类ConfigurableApplicationContext关闭
        ((ConfigurableApplicationContext) context).close();
    }

    // 测试 string 类型 BigKey 问题
    @Benchmark
    public void test(Blackhole blackhole) {
        int index = RANDOM.nextInt(this.keyList.size());
        String key = this.keyList.get(index);
        String value = this.redisTemplate.opsForValue().get(key);
        blackhole.consume(value);
    }
}
