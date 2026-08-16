package com.future.demo.benchmark;

import com.future.demo.Application;
import com.future.demo.service.DdService;
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

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Doris demot.dd 单行 JDBC 插入吞吐 JMH（对齐 demo-benchmark-jmh 的 Spring 集成写法）。
 * <p>
 * 需本地 demo-doris FE 9030 可用。dj_type 固定为 jmh_insert，便于压测后清理。
 * </p>
 * <pre>
 * mvn -q -DskipTests package
 * java -jar target/benchmark-doris-dd.jar DdInsertBenchmarkTests
 * </pre>
 */
@BenchmarkMode(Mode.Throughput)
@State(Scope.Benchmark)
@OutputTimeUnit(TimeUnit.SECONDS)
@Fork(1)
@Warmup(iterations = 5, time = 1, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 3, time = 10, timeUnit = TimeUnit.SECONDS)
@Threads(1)
public class DdInsertBenchmarkTests {

    private ApplicationContext context;
    private DdService ddService;
    private final AtomicLong djIdSeq = new AtomicLong(System.currentTimeMillis());

    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(DdInsertBenchmarkTests.class.getSimpleName())
                .forks(1)
                .shouldFailOnError(true)
                .jvmArgs("-Xmx1G", "-server")
                .build();
        new Runner(opt).run();
    }

    @Setup(Level.Trial)
    public void setup() {
        context = SpringApplication.run(Application.class);
        ddService = context.getBean(DdService.class);
    }

    @TearDown(Level.Trial)
    public void teardown() {
        ((ConfigurableApplicationContext) context).close();
    }

    @Benchmark
    public boolean insertOneRow() {
        return ddService.insertRow(DdRandomData.next(djIdSeq));
    }
}
