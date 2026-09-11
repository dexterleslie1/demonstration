package com.future.demo.benchmark;

import com.future.demo.doris.DorisStreamLoadConfig;
import com.future.demo.doris.DorisStreamLoadWriter;
import com.future.demo.doris.StreamLoadRecord;
import com.future.demo.entity.BpkcMxbMergeStatus;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
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
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Doris demot.bpkc_mxb_merge_status Stream Load 批量写入吞吐 JMH。
 * <p>
 * 批大小参数化：128 / 256 / 512 / 1024 / 2048 / 4096；16 线程。
 * 使用普通 Stream Load（不开 group_commit）：返回 Success 后数据立即可见。
 * Score 为「批/秒」，行吞吐 ≈ Score × batchSize。
 * </p>
 * <pre>
 * mvn -q -DskipTests package
 * java -jar target/benchmark-doris-dd.jar BpkcMxbMergeStatusStreamLoadBenchmarkTests
 * </pre>
 */
@BenchmarkMode(Mode.Throughput)
@State(Scope.Benchmark)
@OutputTimeUnit(TimeUnit.SECONDS)
@Fork(1)
@Warmup(iterations = 3, time = 1, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 3, time = 5, timeUnit = TimeUnit.SECONDS)
@Threads(16)
public class BpkcMxbMergeStatusStreamLoadBenchmarkTests {

    @Param({"128", "256", "512", "1024", "2048", "4096"})
    public int batchSize;

    private final AtomicLong seq = new AtomicLong(System.currentTimeMillis());
    private final AtomicInteger threadIdSeq = new AtomicInteger();

    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(BpkcMxbMergeStatusStreamLoadBenchmarkTests.class.getSimpleName())
                .forks(1)
                .shouldFailOnError(true)
                .jvmArgs("-Xmx1G", "-server")
                .build();
        new Runner(opt).run();
    }

    /**
     * 每线程独立 writer，避免 Stream Load label 冲突。
     */
    @State(Scope.Thread)
    public static class WriterState {
        DorisStreamLoadWriter<BpkcMxbMergeStatus> writer;

        @Setup(Level.Trial)
        public void setup(BpkcMxbMergeStatusStreamLoadBenchmarkTests parent) {
            int id = parent.threadIdSeq.getAndIncrement();
            writer = new DorisStreamLoadWriter<>(
                    DorisStreamLoadConfig.forTable("demot.bpkc_mxb_merge_status"),
                    "jmh_merge_status_sl_" + id,
                    id,
                    parent.batchSize,
                    2000L);
        }
    }

    @Benchmark
    public void streamLoadBatch(WriterState ws) throws IOException {
        List<StreamLoadRecord<BpkcMxbMergeStatus>> records = new ArrayList<>(batchSize);
        for (int i = 0; i < batchSize; i++) {
            records.add(StreamLoadRecord.upsert(BpkcMxbMergeStatusRandomData.next(seq)));
        }
        ws.writer.load(records);
    }
}
