package com.future.demo.benchmark;

import com.future.demo.Application;
import com.future.demo.entity.BpkcMxbMergeStatus;
import com.future.demo.service.BpkcMxbMergeStatusService;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.TearDown;
import org.openjdk.jmh.annotations.Threads;
import org.openjdk.jmh.annotations.Warmup;
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
import java.util.concurrent.atomic.AtomicLong;

/**
 * Doris demot.bpkc_mxb_merge_status MyBatis-Plus 批量插入吞吐 JMH。
 * <p>
 * 固定 batchSize=128、16 线程；Score 为「批/秒」，行吞吐 ≈ Score × 128。
 * 需本地 demo-doris FE 9030 可用，且已执行 doris-init.sql 建表。
 * </p>
 * <pre>
 * mvn -q -DskipTests package
 * java -jar target/benchmark-doris-dd.jar BpkcMxbMergeStatusInsertBenchmarkTests
 * </pre>
 */
@BenchmarkMode(Mode.Throughput)
@State(Scope.Benchmark)
@OutputTimeUnit(TimeUnit.SECONDS)
@Fork(1)
@Warmup(iterations = 3, time = 1, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 3, time = 10, timeUnit = TimeUnit.SECONDS)
@Threads(16)
public class BpkcMxbMergeStatusInsertBenchmarkTests {

    private static final int BATCH_SIZE = 128;

    private ApplicationContext context;
    private BpkcMxbMergeStatusService service;
    private final AtomicLong seq = new AtomicLong(System.currentTimeMillis());

    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(BpkcMxbMergeStatusInsertBenchmarkTests.class.getSimpleName())
                .forks(1)
                .shouldFailOnError(true)
                .jvmArgs("-Xmx1G", "-server")
                .build();
        new Runner(opt).run();
    }

    @Setup(Level.Trial)
    public void setup() {
        context = SpringApplication.run(Application.class);
        service = context.getBean(BpkcMxbMergeStatusService.class);
    }

    @TearDown(Level.Trial)
    public void teardown() {
        ((ConfigurableApplicationContext) context).close();
    }

    @Benchmark
    public boolean insertBatch128() {
        List<BpkcMxbMergeStatus> rows = new ArrayList<>(BATCH_SIZE);
        for (int i = 0; i < BATCH_SIZE; i++) {
            rows.add(BpkcMxbMergeStatusRandomData.next(seq));
        }
        return service.insertBatch(rows, BATCH_SIZE);
    }
}
