package com.future.demo.benchmark;

import com.future.demo.doris.DorisStreamLoadConfig;
import com.future.demo.doris.DorisStreamLoadWriter;
import com.future.demo.doris.StreamLoadRecord;
import com.future.demo.entity.Dd;
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

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Doris demot.dd Stream Load 批量写入吞吐 JMH（对齐 finance DorisStreamLoadWriter）。
 * <p>
 * 需本地 demo-doris FE HTTP 8030 / BE HTTP 8040 可用。
 * 固定 batchSize=100；Score 为「批/秒」，行吞吐 ≈ Score × 100。
 * </p>
 * <pre>
 * mvn -q -DskipTests package
 * java -jar target/benchmark-doris-dd.jar DdStreamLoadBenchmarkTests
 * </pre>
 */
@BenchmarkMode(Mode.Throughput)
@State(Scope.Benchmark)
@OutputTimeUnit(TimeUnit.SECONDS)
@Fork(1)
@Warmup(iterations = 3, time = 1, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 3, time = 10, timeUnit = TimeUnit.SECONDS)
@Threads(1)
public class DdStreamLoadBenchmarkTests {

    private static final int BATCH_SIZE = 100;

    private DorisStreamLoadWriter<Dd> writer;
    private final AtomicLong djIdSeq = new AtomicLong(System.currentTimeMillis());

    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(DdStreamLoadBenchmarkTests.class.getSimpleName())
                .forks(1)
                .shouldFailOnError(true)
                .jvmArgs("-Xmx1G", "-server")
                .build();
        new Runner(opt).run();
    }

    @Setup(Level.Trial)
    public void setup() {
        writer = new DorisStreamLoadWriter<>(
                DorisStreamLoadConfig.demoDefaults(),
                "jmh_dd_stream_load",
                0,
                BATCH_SIZE,
                2000L);
    }

    @TearDown(Level.Trial)
    public void teardown() {
        writer = null;
    }

    @Benchmark
    public void streamLoadBatch() throws IOException {
        List<StreamLoadRecord<Dd>> records = new ArrayList<>(BATCH_SIZE);
        for (int i = 0; i < BATCH_SIZE; i++) {
            records.add(StreamLoadRecord.upsert(DdRandomData.next(djIdSeq)));
        }
        writer.load(records);
    }
}
