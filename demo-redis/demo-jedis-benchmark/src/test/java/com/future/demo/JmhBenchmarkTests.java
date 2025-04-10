package com.future.demo;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.io.ClassPathResource;
import org.springframework.util.StreamUtils;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisCluster;
import redis.clients.jedis.JedisPool;

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

    ApplicationContext context;
    JedisPool jedisPool;
    JedisCluster jedisCluster;

    int keyLength = 300;
    String script;

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
        try {
            jedisPool = context.getBean(JedisPool.class);
        } catch (NoSuchBeanDefinitionException ignored) {
        }
        try {
            jedisCluster = context.getBean(JedisCluster.class);
        } catch (NoSuchBeanDefinitionException ignored) {
        }

        ClassPathResource classPathResource = new ClassPathResource("test.lua");
        script = StreamUtils.copyToString(classPathResource.getInputStream(), StandardCharsets.UTF_8);
        classPathResource.getInputStream().close();
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
        if (this.jedisPool != null) {
            Jedis jedis = null;
            try {
                jedis = JedisPoolUtil.getJedis(this.jedisPool);
                jedis.set(key, key);
            } finally {
                JedisPoolUtil.returnJedis(jedis);
            }
        } else {
            this.jedisCluster.set(key, key);
        }
    }

    @Benchmark
    public void testLuaScript(Blackhole blackhole) {
        int randInt = RANDOM.nextInt(keyLength);
        String key = String.valueOf(randInt);
        if (this.jedisPool != null) {
            Jedis jedis = null;
            try {
                jedis = JedisPoolUtil.getJedis(this.jedisPool);
                key = (String) jedis.eval(script, Collections.singletonList(key), Collections.singletonList(key));
            } finally {
                JedisPoolUtil.returnJedis(jedis);
            }
        } else {
            key = (String) this.jedisCluster.eval(script, Collections.singletonList(key), Collections.singletonList(key));
        }
        blackhole.consume(key);
    }
}
