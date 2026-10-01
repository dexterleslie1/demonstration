package com.future.demo;

import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.common.typeinfo.Types;
import org.apache.flink.api.connector.source.util.ratelimit.RateLimiterStrategy;
import org.apache.flink.api.java.tuple.Tuple3;
import org.apache.flink.configuration.CheckpointingOptions;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.connector.datagen.source.DataGeneratorSource;
import org.apache.flink.contrib.streaming.state.EmbeddedRocksDBStateBackend;
import org.apache.flink.core.execution.JobClient;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.CheckpointConfig;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

import java.nio.file.Paths;

/**
 * 演示单表同步（无 JOIN）不会因业务逻辑产生状态膨胀。
 * <p>
 * 与 {@link StatebackendParentAndChildTableJoinTests} 对比：Regular Join 会为两侧历史记录
 * 保留 ListState 并持续增长；单表同步仅做 map 等无状态转换，RocksDB 目录体积基本稳定。
 * <p>
 * 本示例模拟单表 CDC 同步：高速写入单表数据，经简单字段映射后输出，不保留历史记录。
 * 作业运行约 60 秒后自动 cancel。如需从头开始，删除 {@link #CHECKPOINT_DIR} 与
 * {@link #ROCKSDB_DIR} 目录后重启。
 */
public class StatebackendSingleTableTests {

    /** 单表 id 上限，与 Join 示例子表 dj_id 范围一致，便于对比数据量 */
    private static final int ROW_ID_MAX = 5000;

    private static final String CHECKPOINT_DIR = Paths.get(
            System.getProperty("user.dir"), ".checkpoint", "single-table-sync-demo"
    ).toUri().toString();

    private static final String ROCKSDB_DIR = Paths.get(
            System.getProperty("user.dir"), ".rocksdb", "single-table-sync-demo"
    ).toString();

    private static final long RUN_DURATION_MS = 60_000;

    public static void main(String[] args) throws Exception {
        Configuration config = new Configuration();
        config.set(CheckpointingOptions.CHECKPOINTS_DIRECTORY, CHECKPOINT_DIR);
        config.set(CheckpointingOptions.MAX_RETAINED_CHECKPOINTS, 1);

        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment(config);
        env.setParallelism(1);
        env.enableCheckpointing(3000);
        env.getCheckpointConfig().configure(config);
        env.getCheckpointConfig().setMinPauseBetweenCheckpoints(500);
        env.getCheckpointConfig().setExternalizedCheckpointCleanup(
                CheckpointConfig.ExternalizedCheckpointCleanup.RETAIN_ON_CANCELLATION
        );

        EmbeddedRocksDBStateBackend stateBackend = new EmbeddedRocksDBStateBackend(true);
        stateBackend.setDbStoragePaths(ROCKSDB_DIR);
        env.setStateBackend(stateBackend);

        System.out.println("=== 单表同步（无 JOIN）状态演示 ===");
        System.out.println("单表 id 范围: 1 ~ " + ROW_ID_MAX + "，约 3000 条/秒");
        System.out.println("无 JOIN、无窗口聚合，业务算子不保留历史记录");
        System.out.println("RocksDB 目录: " + ROCKSDB_DIR);
        System.out.println("Checkpoint 目录: " + CHECKPOINT_DIR);
        System.out.println("可与 Join 示例对比: .rocksdb/large-state-join-demo");

        DataGeneratorSource<Tuple3<Long, Long, String>> tableSource = new DataGeneratorSource<>(
                index -> Tuple3.of(index, (index % ROW_ID_MAX) + 1, "row-" + index),
                Long.MAX_VALUE,
                RateLimiterStrategy.perSecond(3000),
                Types.TUPLE(Types.LONG, Types.LONG, Types.STRING)
        );

        DataStream<Tuple3<Long, Long, String>> tableStream = env.fromSource(
                tableSource,
                WatermarkStrategy.noWatermarks(),
                "single-table"
        );

        tableStream
                .map((MapFunction<Tuple3<Long, Long, String>, String>) row ->
                        "id=" + row.f0 + ", biz_id=" + row.f1 + ", content=" + row.f2)
                .print();

        System.out.println("作业运行 " + RUN_DURATION_MS / 1000 + " 秒后自动 cancel...");
        System.out.println("观察 RocksDB 目录体积应基本稳定，不会像 Join 那样持续增长");
        JobClient jobClient = env.executeAsync("Single Table Sync State Demo");
        Thread.sleep(RUN_DURATION_MS);
        jobClient.cancel().get();
        System.out.println("作业已 cancel。请检查 " + ROCKSDB_DIR + " 目录大小，应与 Join 示例形成对比");
    }
}
