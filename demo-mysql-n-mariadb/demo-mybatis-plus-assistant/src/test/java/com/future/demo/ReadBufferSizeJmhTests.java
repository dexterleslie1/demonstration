package com.future.demo;

import com.future.demo.entity.MemoryAssistantMyISAMEntity;
import com.future.demo.mapper.MemoryAssistantMyISAMMapper;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;

import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * 协助测试 read_buffer_size 参数
 */
@BenchmarkMode(Mode.Throughput)
@State(Scope.Benchmark) //使用的SpringBoot容器，都是无状态单例Bean，无安全问题，可以直接使用基准作用域BenchMark
@OutputTimeUnit(TimeUnit.SECONDS)
@Fork(1)  //整体平均执行1次
@Warmup(iterations = 5, time = 2, timeUnit = TimeUnit.SECONDS) //预热1s
@Measurement(iterations = 30000, time = 15, timeUnit = TimeUnit.SECONDS) //测试也是1s、五遍
@Threads(128)
public class ReadBufferSizeJmhTests {

    MemoryAssistantMyISAMMapper memoryAssistantMyISAMMapper;
    List<Long> idList;
    Random R = new Random(System.currentTimeMillis());

    //springBoot容器
    private ApplicationContext context;

    public static void main(String[] args) throws RunnerException {
        //使用注解之后只需要配置一下include即可，fork和warmup、measurement都是注解
        Options opt = new OptionsBuilder()
                .include(ReadBufferSizeJmhTests.class.getSimpleName())
                // 断点调试时fork=0
                .forks(1)
                // 发生错误停止测试
                .shouldFailOnError(true)
                .jvmArgs("-Xmx4G",
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
        this.memoryAssistantMyISAMMapper = context.getBean(MemoryAssistantMyISAMMapper.class);

        idList = this.memoryAssistantMyISAMMapper.selectIds();
        System.out.println("加载id个数为：" + idList.size());
    }

    /**
     * 测试的后处理操作，关闭容器，资源清理
     */
    @TearDown(Level.Trial)
    public void teardown() {
        //使用子类ConfigurableApplicationContext关闭
        ((ConfigurableApplicationContext) context).close();
    }

    /**
     * @param blackhole
     */
    @Benchmark
    public void test(Blackhole blackhole) {
        Long randId = idList.get(R.nextInt(idList.size()));

        long startId = randId;
        int randRange = R.nextInt(2000);
        if (randRange == 0) {
            randRange = 1;
        }
        long endId = startId + randRange;
        List<MemoryAssistantMyISAMEntity> returnList = this.memoryAssistantMyISAMMapper.testReadBufferSize(startId, endId, UUID.randomUUID().toString());
        returnList.forEach(blackhole::consume);
    }
}
