package com.future.demo.bigkey;

import com.future.demo.Application;
import org.junit.Assert;
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
public class SetBigKeyTests {

    final static Random RANDOM = new Random(System.currentTimeMillis());

    // set 类型最大的元素个数
    @Param(value = {"2", "1024", "2048", (10 * 1024) + "", (20 * 1024) + "", (256 * 1024) + ""})
    Integer maximumLength;

    StringRedisTemplate redisTemplate;

    //springBoot容器
    ApplicationContext context;

    String keyOfSet;
    List<String> valueListOfSet = new ArrayList<>();

    public static void main(String[] args) throws RunnerException {
        //使用注解之后只需要配置一下include即可，fork和warmup、measurement都是注解
        Options opt = new OptionsBuilder()
                .include(SetBigKeyTests.class.getSimpleName())
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
        keyOfSet = UUID.randomUUID().toString();
        for (int i = 0; i < maximumLength; i++) {
            String uuidStr = UUID.randomUUID().toString();
            Long result = this.redisTemplate.opsForSet().add(keyOfSet, uuidStr);
            Assert.assertEquals(Long.valueOf(1L), result);
            this.valueListOfSet.add(uuidStr);
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

    // 测试 set 类型 remove 和 add BigKey 问题
    @Benchmark
    public void testRemoveAndAdd() {
        int randomInt = RANDOM.nextInt(2);
        if (randomInt == 0) {
            // 删除
            int index = RANDOM.nextInt(this.valueListOfSet.size());
            String value = this.valueListOfSet.get(index);
            this.redisTemplate.opsForSet().remove(keyOfSet, value);
        } else {
            // 添加
            int index = RANDOM.nextInt(this.valueListOfSet.size());
            String value = this.valueListOfSet.get(index);
            this.redisTemplate.opsForSet().add(keyOfSet, value);
        }
    }

    // 测试 set 类型 isMember BigKey 问题
    @Benchmark
    public void testIsMember(Blackhole blackhole) {
        int index = RANDOM.nextInt(this.valueListOfSet.size());
        String value = this.valueListOfSet.get(index);
        Boolean result = this.redisTemplate.opsForSet().isMember(keyOfSet, value);
        blackhole.consume(result);
    }
}
