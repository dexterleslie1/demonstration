package com.future.demo;

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
import org.springframework.util.StreamUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.Random;
import java.util.concurrent.TimeUnit;

// https://blog.csdn.net/a23452/article/details/126680840
@BenchmarkMode(Mode.Throughput)
@State(Scope.Benchmark) //使用的SpringBoot容器，都是无状态单例Bean，无安全问题，可以直接使用基准作用域BenchMark
@OutputTimeUnit(TimeUnit.SECONDS)
@Warmup(iterations = 3, time = 1, timeUnit = TimeUnit.SECONDS) //预热1s
@Measurement(iterations = 3, time = 5, timeUnit = TimeUnit.SECONDS) //测试也是1s、五遍
// 指定并发执行线程数
// https://stackoverflow.com/questions/39644383/jmh-run-benchmark-concurrently
@Threads(256)
public class JmhBenchmarkTests {
    final static Random RANDOM = new Random(System.currentTimeMillis());
    StringRedisTemplate redisTemplate;

    //springBoot容器
    ApplicationContext context;

    DefaultRedisScript<String> defaultRedisScript;
    int keyLength = 300;

    public static void main(String[] args) throws RunnerException {
        //使用注解之后只需要配置一下include即可，fork和warmup、measurement都是注解
        Options opt = new OptionsBuilder()
                .include(JmhBenchmarkTests.class.getSimpleName())
                // 断点调试时fork=0
                .forks(1)
                // 发生错误停止测试
                .shouldFailOnError(true)
                .jvmArgs("-Xmx4G", "-server")
                .build();
        new Runner(opt).run();
    }

    /**
     * 初始化，获取springBoot容器，run即可，同时得到相关的测试对象
     */
    @Setup(Level.Trial)
    public void setup() throws IOException {
        //容器获取
        context = SpringApplication.run(Application.class);
        //获取对象
        redisTemplate = context.getBean(StringRedisTemplate.class);

        defaultRedisScript = new DefaultRedisScript<>();
        /*defaultRedisScript.setLocation(new ClassPathResource("test.lua"));*/
        ClassPathResource classPathResource = new ClassPathResource("test.lua");
        String script = StreamUtils.copyToString(classPathResource.getInputStream(), StandardCharsets.UTF_8);
        classPathResource.getInputStream().close();
        defaultRedisScript.setScriptText(script);
        defaultRedisScript.setResultType(String.class);

        // 初始化数据协助 get 测试
        for (int i = 0; i < keyLength; i++) {
            String key = String.valueOf(i);
            this.redisTemplate.opsForValue().set(key, key);
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

    @Benchmark
    public void testSet() {
        int randInt = RANDOM.nextInt(keyLength);
        String key = String.valueOf(randInt);
        this.redisTemplate.opsForValue().set(key, key);
    }

    @Benchmark
    public void testLuaScript(Blackhole blackhole) {
        int randInt = RANDOM.nextInt(keyLength);
        String key = String.valueOf(randInt);
        key = this.redisTemplate.execute(defaultRedisScript, Collections.singletonList(key), key);
        blackhole.consume(key);
    }
}
