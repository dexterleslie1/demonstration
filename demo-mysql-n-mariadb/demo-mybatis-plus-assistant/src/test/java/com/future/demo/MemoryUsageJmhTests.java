package com.future.demo;

import com.future.demo.entity.MemoryAssistantEntity;
import com.future.demo.entity.MemoryAssistantMyISAMEntity;
import com.future.demo.mapper.MemoryAssistantMapper;
import com.future.demo.mapper.MemoryAssistantMyISAMMapper;
import com.future.demo.service.JoinQueryService;
import com.future.demo.service.LargeTransactionService;
import com.future.demo.service.SortQueryService;
import com.future.demo.service.TempTableService;
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
 * 协助测试mysql内存使用率
 */
@BenchmarkMode(Mode.Throughput)
@State(Scope.Benchmark) //使用的SpringBoot容器，都是无状态单例Bean，无安全问题，可以直接使用基准作用域BenchMark
@OutputTimeUnit(TimeUnit.SECONDS)
@Fork(1)  //整体平均执行1次
@Warmup(iterations = 5, time = 2, timeUnit = TimeUnit.SECONDS) //预热1s
@Measurement(iterations = 30000, time = 15, timeUnit = TimeUnit.SECONDS) //测试也是1s、五遍
@Threads(8)
public class MemoryUsageJmhTests {

    MemoryAssistantMapper memoryAssistantMapper;
    JoinQueryService joinQueryService;
    LargeTransactionService largeTransactionService;
    MemoryAssistantMyISAMMapper memoryAssistantMyISAMMapper;
    SortQueryService sortQueryService;
    TempTableService tempTableService;
    List<Long> idList;
    List<Long> idListMyisam;
    Random R = new Random(System.currentTimeMillis());

    //springBoot容器
    private ApplicationContext context;

    public static void main(String[] args) throws RunnerException {
        //使用注解之后只需要配置一下include即可，fork和warmup、measurement都是注解
        Options opt = new OptionsBuilder()
                .include(MemoryUsageJmhTests.class.getSimpleName())
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
        this.memoryAssistantMyISAMMapper = context.getBean(MemoryAssistantMyISAMMapper.class);
        this.joinQueryService = context.getBean(JoinQueryService.class);
        this.largeTransactionService = context.getBean(LargeTransactionService.class);
        this.sortQueryService = context.getBean(SortQueryService.class);
        this.tempTableService = context.getBean(TempTableService.class);

        idList = this.memoryAssistantMapper.selectIds();
        idListMyisam = this.memoryAssistantMyISAMMapper.selectIds();
        System.out.println("加载id个数为：" + idList.size() + "，id-myisam个数为：" + idListMyisam.size());
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
        /*// 测试join查询内存利用率
        List<Map<String, Object>> mapList = this.joinQueryService.test(startId, endId, randIndex, randLength);
        mapList.forEach(blackhole::consume);*/
        int randInt = R.nextInt(5);
        if (randInt == 0) {
            this.testInnodbBufferPoolSizeMemoryConsumption(blackhole);
        } else if (randInt == 1) {
            this.testBinlogCacheSizeMemoryConsumption(blackhole);
        } else if (randInt == 2) {
            this.testReadBufferSizeMemoryConsumption(blackhole);
        } else if (randInt == 3) {
            this.testSortBufferSizeMemoryConsumption(blackhole);
        } else {
            this.testTempTableSizeMemoryConsumption(blackhole);
        }
    }

    final static int RandomBound = 2000;

    /**
     * 测试innodb_buffer_pool_size内存利用率
     *
     * @param blackhole
     */
    void testInnodbBufferPoolSizeMemoryConsumption(Blackhole blackhole) {
        Long randId = idList.get(R.nextInt(idList.size()));

        long startId = randId;
        int randRange = R.nextInt(RandomBound);
        if (randRange == 0) {
            randRange = 1;
        }
        long endId = startId + randRange;
        int randIndex = R.nextInt(randRange);
        int randLength = R.nextInt(randRange);

        List<MemoryAssistantEntity> entityList = memoryAssistantMapper.list(startId, endId, randIndex, randLength);
        entityList.forEach(blackhole::consume);
    }

    /**
     * 测试大事务binlog_cache_size内存利用率
     *
     * @param blackhole
     */
    void testBinlogCacheSizeMemoryConsumption(Blackhole blackhole) {
        int statementCount = R.nextInt(64);
        this.largeTransactionService.execute(statementCount);
    }

    /**
     * 测试read_buffer_size内存利用率
     *
     * @param blackhole
     */
    void testReadBufferSizeMemoryConsumption(Blackhole blackhole) {
        Long randId = idList.get(R.nextInt(idList.size()));

        long startId = randId;
        int randRange = R.nextInt(RandomBound);
        if (randRange == 0) {
            randRange = 1;
        }
        long endId = startId + randRange;
        List<MemoryAssistantMyISAMEntity> returnList = this.memoryAssistantMyISAMMapper.testReadBufferSize(startId, endId, UUID.randomUUID().toString());
        returnList.forEach(blackhole::consume);
    }

    /**
     * 测试sort_buffer_size内存利用率
     *
     * @param blackhole
     */
    void testSortBufferSizeMemoryConsumption(Blackhole blackhole) {
        Long randId = idList.get(R.nextInt(idList.size()));

        long startId = randId;
        int randRange = R.nextInt(RandomBound);
        if (randRange == 0) {
            randRange = 1;
        }
        long endId = startId + randRange;
        int randIndex = R.nextInt(randRange);
        int randLength = R.nextInt(randRange);

        List<MemoryAssistantEntity> entityList = this.sortQueryService.test(startId, endId, randIndex, randLength);
        entityList.forEach(blackhole::consume);
    }

    /**
     * 测试tmp_table_size内存利用率
     *
     * @param blackhole
     */
    void testTempTableSizeMemoryConsumption(Blackhole blackhole) {
        Long randId = idList.get(R.nextInt(idList.size()));

        long startId = randId;
        int randRange = R.nextInt(RandomBound);
        if (randRange == 0) {
            randRange = 1;
        }
        long endId = startId + randRange;

        List<MemoryAssistantEntity> entityList = this.tempTableService.test(startId, endId);
        entityList.forEach(blackhole::consume);
    }
}
