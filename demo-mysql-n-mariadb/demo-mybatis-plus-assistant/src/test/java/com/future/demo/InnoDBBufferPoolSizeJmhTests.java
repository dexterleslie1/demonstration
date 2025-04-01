package com.future.demo;

import com.future.demo.entity.MemoryAssistantEntity;
import com.future.demo.mapper.MemoryAssistantMapper;
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
import java.util.concurrent.TimeUnit;

/**
 * 辅助测试innodb_buffer_pool_size参数
 */
@BenchmarkMode(Mode.Throughput)
@State(Scope.Benchmark) //使用的SpringBoot容器，都是无状态单例Bean，无安全问题，可以直接使用基准作用域BenchMark
@OutputTimeUnit(TimeUnit.SECONDS)
@Fork(1)  //整体平均执行1次
@Warmup(iterations = 5, time = 2, timeUnit = TimeUnit.SECONDS) //预热1s
@Measurement(iterations = 30000, time = 15, timeUnit = TimeUnit.SECONDS) //测试也是1s、五遍
@Threads(256)
public class InnoDBBufferPoolSizeJmhTests {

    MemoryAssistantMapper memoryAssistantMapper;
    List<Long> idList;
    Random R = new Random(System.currentTimeMillis());

    //springBoot容器
    private ApplicationContext context;

    public static void main(String[] args) throws RunnerException {
        //使用注解之后只需要配置一下include即可，fork和warmup、measurement都是注解
        Options opt = new OptionsBuilder()
                .include(InnoDBBufferPoolSizeJmhTests.class.getSimpleName())
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
        this.memoryAssistantMapper = context.getBean(MemoryAssistantMapper.class);

        idList = this.memoryAssistantMapper.selectIds();
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
     * 协助测试场景：
     * 1、innodb buffer设置很小和很大时读数据性能对比
     * 2、长时间运行会随机读取每一个数据，使所有数据加载到innodb buffer pool内存中
     *
     * @param blackhole
     */
    @Benchmark
    public void test(Blackhole blackhole) {
        Long randId = idList.get(R.nextInt(idList.size()));
        /*MemoryAssistantEntity entity = memoryAssistantMapper.selectById(randId);
        blackhole.consume(entity);*/

        long startId = randId;
        int randRange = R.nextInt(2000);
        if (randRange == 0) {
            randRange = 1;
        }
        long endId = startId + randRange;
        int randIndex = R.nextInt(randRange);
        int randLength = R.nextInt(randRange);
        List<MemoryAssistantEntity> entityList = memoryAssistantMapper.list(startId, endId, randIndex, randLength);
        entityList.forEach(blackhole::consume);
    }
}
