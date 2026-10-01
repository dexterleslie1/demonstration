package com.future.demo;

import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.api.common.state.ListState;
import org.apache.flink.api.common.state.ListStateDescriptor;
import org.apache.flink.api.common.typeinfo.Types;
import org.apache.flink.api.connector.source.util.ratelimit.RateLimiterStrategy;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.api.java.tuple.Tuple3;
import org.apache.flink.configuration.CheckpointingOptions;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.connector.datagen.source.DataGeneratorSource;
import org.apache.flink.contrib.streaming.state.EmbeddedRocksDBStateBackend;
import org.apache.flink.core.execution.JobClient;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.CheckpointConfig;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.co.KeyedCoProcessFunction;
import org.apache.flink.util.Collector;

import java.nio.file.Paths;

/**
 * 使用主子表 regular join 演示主子表大量数据时会导致状态膨胀。
 * <p>
 * Regular join 会无限期保留两侧未匹配及已匹配的历史记录，状态随数据持续累积。
 * 本示例中：
 * <ul>
 *   <li>主表（parent）：每秒少量记录，id 在 1~{@link #PARENT_ID_MAX} 间循环</li>
 *   <li>子表（child）：高速到达，dj_id 在 1~{@link #CHILD_DJ_ID_MAX} 间循环</li>
 *   <li>dj_id &gt; {@link #PARENT_ID_MAX} 的子表记录永远等不到主表匹配，状态只增不减</li>
 * </ul>
 * 使用 EmbeddedRocksDBStateBackend 将膨胀的状态写入本地 RocksDB（{@link #ROCKSDB_DIR}），
 * 可观察该目录体积持续增长。作业运行约 60 秒后自动 cancel 以保留 checkpoint。
 * 如需从头开始，删除 {@link #CHECKPOINT_DIR} 与 {@link #ROCKSDB_DIR} 目录后重启。
 */
public class StatebackendParentAndChildTableJoinTests {

    /** 主表 id 上限（仅覆盖部分子表 dj_id） */
    private static final int PARENT_ID_MAX = 50;

    /** 子表 dj_id 上限（远大于主表 id，产生大量无法匹配的状态） */
    private static final int CHILD_DJ_ID_MAX = 5000;

    private static final String CHECKPOINT_DIR = Paths.get(
            System.getProperty("user.dir"), ".checkpoint", "large-state-join-demo"
    ).toUri().toString();

    private static final String ROCKSDB_DIR = Paths.get(
            System.getProperty("user.dir"), ".rocksdb", "large-state-join-demo"
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

        System.out.println("=== 主子表 Regular Join 状态膨胀演示 ===");
        System.out.println("主表 id 范围: 1 ~ " + PARENT_ID_MAX + "，约 2 条/秒");
        System.out.println("子表 dj_id 范围: 1 ~ " + CHILD_DJ_ID_MAX + "，约 3000 条/秒");
        System.out.println("dj_id > " + PARENT_ID_MAX + " 的子表记录无法匹配，状态持续膨胀");
        System.out.println("RocksDB 目录: " + ROCKSDB_DIR);
        System.out.println("Checkpoint 目录: " + CHECKPOINT_DIR);

        DataGeneratorSource<Tuple2<Long, Long>> parentSource = new DataGeneratorSource<>(
                index -> Tuple2.of((index % PARENT_ID_MAX) + 1, (index % 5) + 1),
                Long.MAX_VALUE,
                RateLimiterStrategy.perSecond(2),
                Types.TUPLE(Types.LONG, Types.LONG)
        );

        DataGeneratorSource<Tuple3<Long, Long, String>> childSource = new DataGeneratorSource<>(
                index -> Tuple3.of(index, (index % CHILD_DJ_ID_MAX) + 1, "child-" + index),
                Long.MAX_VALUE,
                RateLimiterStrategy.perSecond(3000),
                Types.TUPLE(Types.LONG, Types.LONG, Types.STRING)
        );

        DataStream<Tuple2<Long, Long>> parentStream = env.fromSource(
                parentSource,
                WatermarkStrategy.noWatermarks(),
                "parent-table"
        );

        DataStream<Tuple3<Long, Long, String>> childStream = env.fromSource(
                childSource,
                WatermarkStrategy.noWatermarks(),
                "child-table"
        );

        parentStream.keyBy(parent -> parent.f0)
                .connect(childStream.keyBy(child -> child.f1))
                .process(new RegularJoinCoProcessFunction())
                .print();

        System.out.println("作业运行 " + RUN_DURATION_MS / 1000 + " 秒后自动 cancel...");
        System.out.println("观察 RocksDB 目录体积随时间增长，即为状态膨胀效果");
        JobClient jobClient = env.executeAsync("Large State Regular Join Demo");
        Thread.sleep(RUN_DURATION_MS);
        jobClient.cancel().get();
        System.out.println("作业已 cancel。请检查 " + ROCKSDB_DIR + " 目录大小以验证状态膨胀");
    }

    /**
     * 模拟 Flink SQL Regular Join：两侧到达的记录均持久保留在状态中，
     * 新记录到达时与对侧全部历史记录做笛卡尔匹配并输出。
     */
    private static class RegularJoinCoProcessFunction
            extends KeyedCoProcessFunction<Long, Tuple2<Long, Long>, Tuple3<Long, Long, String>, String> {

        private ListState<Tuple2<Long, Long>> parentState;
        private ListState<Tuple3<Long, Long, String>> childState;

        @Override
        public void open(Configuration parameters) {
            parentState = getRuntimeContext().getListState(
                    new ListStateDescriptor<>("parent-records", Types.TUPLE(Types.LONG, Types.LONG)));
            childState = getRuntimeContext().getListState(
                    new ListStateDescriptor<>("child-records", Types.TUPLE(Types.LONG, Types.LONG, Types.STRING)));
        }

        @Override
        public void processElement1(
                Tuple2<Long, Long> parent,
                Context ctx,
                Collector<String> out) throws Exception {
            parentState.add(parent);
            for (Tuple3<Long, Long, String> child : childState.get()) {
                out.collect(formatJoin(parent, child));
            }
        }

        @Override
        public void processElement2(
                Tuple3<Long, Long, String> child,
                Context ctx,
                Collector<String> out) throws Exception {
            childState.add(child);
            for (Tuple2<Long, Long> parent : parentState.get()) {
                out.collect(formatJoin(parent, child));
            }
        }

        private static String formatJoin(Tuple2<Long, Long> parent, Tuple3<Long, Long, String> child) {
            return "parent_id=" + parent.f0
                    + ", company_id=" + parent.f1
                    + ", child_id=" + child.f0
                    + ", dj_id=" + child.f1
                    + ", content=" + child.f2;
        }
    }
}
