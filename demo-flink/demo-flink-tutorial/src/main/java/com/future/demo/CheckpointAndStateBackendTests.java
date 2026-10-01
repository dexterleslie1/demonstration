package com.future.demo;

import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.api.common.functions.RichMapFunction;
import org.apache.flink.api.common.state.ValueState;
import org.apache.flink.api.common.state.ValueStateDescriptor;
import org.apache.flink.api.common.typeinfo.Types;
import org.apache.flink.api.connector.source.util.ratelimit.RateLimiterStrategy;
import org.apache.flink.configuration.CheckpointingOptions;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.configuration.PipelineOptionsInternal;
import org.apache.flink.connector.datagen.source.DataGeneratorSource;
import org.apache.flink.contrib.streaming.state.EmbeddedRocksDBStateBackend;
import org.apache.flink.core.execution.JobClient;
import org.apache.flink.runtime.jobgraph.RestoreMode;
import org.apache.flink.runtime.jobgraph.SavepointConfigOptions;
import org.apache.flink.runtime.state.hashmap.HashMapStateBackend;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.CheckpointConfig;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;
import java.util.Optional;
import java.util.stream.Stream;

/**
 * 演示 Checkpoint 与两种 State Backend 的用法：
 * <ul>
 *   <li>HashMapStateBackend：状态保存在 TaskManager JVM 堆内存，访问快，受内存限制</li>
 *   <li>EmbeddedRocksDBStateBackend：状态保存在本地 RocksDB，适合大状态，支持增量 Checkpoint</li>
 * </ul>
 * 启动参数：hashmap（默认）或 rocksdb
 * <p>
 * 作业运行约 8 秒后自动 cancel，checkpoint 会写入 {@link #CHECKPOINT_DIR} 并保留（
 * RETAIN_ON_CANCELLATION 仅在 cancel/fail 时保留，有界作业正常结束会删除 checkpoint）。
 * 再次启动会从上次 checkpoint 恢复并继续累加计数。
 * 如需从头开始，删除 {@link #CHECKPOINT_DIR} 与 {@link #ROCKSDB_DIR} 目录后重启。
 */
public class CheckpointAndStateBackendTests {

    private static final String[] USERS = {"alice", "bob", "charlie"};

    private static final String CHECKPOINT_DIR = Paths.get(
            System.getProperty("user.dir"), ".checkpoint", "state-backend-demo"
    ).toUri().toString();

    private static final String ROCKSDB_DIR = Paths.get(
            System.getProperty("user.dir"), ".rocksdb", "state-backend-demo"
    ).toString();

    /** 固定 JobID（32 位十六进制），使多次本地重启复用同一 checkpoint 子目录 */
    private static final String FIXED_JOB_ID = "0000000000000000000000000000be01";

    /** 运行时长（毫秒），需大于 checkpoint 间隔以便产生 checkpoint */
    private static final long RUN_DURATION_MS = 8000;

    public static void main(String[] args) throws Exception {
        String backend = args.length > 0 ? args[0].toLowerCase() : "hashmap";

        Configuration config = new Configuration();
        config.set(CheckpointingOptions.CHECKPOINTS_DIRECTORY, CHECKPOINT_DIR);
        config.set(CheckpointingOptions.MAX_RETAINED_CHECKPOINTS, 1);
        config.set(PipelineOptionsInternal.PIPELINE_FIXED_JOB_ID, FIXED_JOB_ID);

        Optional<Path> latestCheckpoint = resolveLatestCheckpoint(CHECKPOINT_DIR, FIXED_JOB_ID);
        latestCheckpoint.ifPresent(path -> {
            config.set(SavepointConfigOptions.SAVEPOINT_PATH, path.toUri().toString());
            config.set(SavepointConfigOptions.RESTORE_MODE, RestoreMode.CLAIM);
        });
        cleanupCheckpointStorage(CHECKPOINT_DIR, FIXED_JOB_ID, latestCheckpoint.orElse(null));
        if (latestCheckpoint.isPresent()) {
            System.out.println("从 checkpoint 恢复: " + latestCheckpoint.get().toUri());
        } else {
            System.out.println("未发现 checkpoint，首次启动");
        }

        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment(config);
        env.setParallelism(1);
        env.enableCheckpointing(2000);
        env.getCheckpointConfig().configure(config);
        // 上一次 checkpoint 完成后至少等待 500ms 再触发下一次，避免 checkpoint 扎堆重叠
        env.getCheckpointConfig().setMinPauseBetweenCheckpoints(500);
        env.getCheckpointConfig().setExternalizedCheckpointCleanup(
                CheckpointConfig.ExternalizedCheckpointCleanup.RETAIN_ON_CANCELLATION
        );

        if ("rocksdb".equals(backend)) {
            EmbeddedRocksDBStateBackend stateBackend = new EmbeddedRocksDBStateBackend(true);
            stateBackend.setDbStoragePaths(ROCKSDB_DIR);
            env.setStateBackend(stateBackend);
            System.out.println("使用 EmbeddedRocksDBStateBackend，本地目录: " + ROCKSDB_DIR);
        } else {
            env.setStateBackend(new HashMapStateBackend());
            System.out.println("使用 HashMapStateBackend（JVM 堆内存）");
        }
        System.out.println("Checkpoint 目录: " + CHECKPOINT_DIR);

        DataGeneratorSource<String> generatorSource = new DataGeneratorSource<>(
                index -> USERS[(int) (index % USERS.length)],
                Long.MAX_VALUE,
                RateLimiterStrategy.perSecond(2),
                Types.STRING
        );

        DataStream<String> stream = env.fromSource(
                generatorSource,
                WatermarkStrategy.noWatermarks(),
                "user-generator"
        );

        stream.keyBy(user -> user)
                .map(new RichMapFunction<String, String>() {
                    private ValueState<Integer> countState;

                    @Override
                    public void open(Configuration parameters) {
                        countState = getRuntimeContext()
                                .getState(new ValueStateDescriptor<>("user-count", Integer.class));
                    }

                    @Override
                    public String map(String user) throws Exception {
                        Integer current = countState.value();
                        if (current == null) {
                            current = 0;
                        }
                        current += 1;
                        countState.update(current);
                        return user + " -> count=" + current;
                    }
                })
                .print();

        System.out.println("作业运行 " + RUN_DURATION_MS / 1000 + " 秒后自动 cancel，以保留 checkpoint...");
        JobClient jobClient = env.executeAsync("Checkpoint And StateBackend Demo");
        Thread.sleep(RUN_DURATION_MS);
        jobClient.cancel().get();
        System.out.println("作业已 cancel，checkpoint 已保留，可再次启动验证恢复");
    }

    private static Optional<Path> resolveLatestCheckpoint(String checkpointDir, String fixedJobId)
            throws IOException {
        Path base = Paths.get(java.net.URI.create(checkpointDir));
        if (!Files.exists(base)) {
            return Optional.empty();
        }

        Path fixedJobDir = base.resolve(fixedJobId);
        Optional<Path> fromFixedJob = findLatestCheckpointInJobDir(fixedJobDir);
        if (fromFixedJob.isPresent()) {
            return fromFixedJob;
        }

        Optional<Path> latest;
        try (Stream<Path> jobDirs = Files.list(base)) {
            latest = jobDirs
                    .filter(Files::isDirectory)
                    .flatMap(jobDir -> {
                        try {
                            return Files.list(jobDir);
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    })
                    .filter(path -> path.getFileName().toString().startsWith("chk-"))
                    .filter(path -> Files.exists(path.resolve("_metadata")))
                    .max(Comparator.comparingLong(CheckpointAndStateBackendTests::checkpointId));
        }

        return latest;
    }

    private static Optional<Path> findLatestCheckpointInJobDir(Path jobDir) throws IOException {
        if (!Files.exists(jobDir)) {
            return Optional.empty();
        }

        try (Stream<Path> checkpoints = Files.list(jobDir)) {
            return checkpoints
                    .filter(path -> path.getFileName().toString().startsWith("chk-"))
                    .filter(path -> Files.exists(path.resolve("_metadata")))
                    .max(Comparator.comparingLong(CheckpointAndStateBackendTests::checkpointId));
        }
    }

    private static void cleanupCheckpointStorage(
            String checkpointDir, String fixedJobId, Path latestCheckpoint) throws IOException {
        Path base = Paths.get(java.net.URI.create(checkpointDir));
        if (!Files.exists(base)) {
            return;
        }

        Path keepForRecoveryJobDir = latestCheckpoint != null ? latestCheckpoint.getParent() : null;
        try (Stream<Path> jobDirs = Files.list(base)) {
            jobDirs.filter(Files::isDirectory).forEach(jobDir -> {
                String jobDirName = jobDir.getFileName().toString();
                if (jobDirName.equals(fixedJobId)
                        || (keepForRecoveryJobDir != null && jobDir.equals(keepForRecoveryJobDir))) {
                    Path keepCheckpoint = jobDir.equals(keepForRecoveryJobDir) ? latestCheckpoint : null;
                    try {
                        cleanupStaleCheckpointsInJobDir(jobDir, keepCheckpoint);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    return;
                }
                deleteRecursively(jobDir);
                System.out.println("清理旧 checkpoint 作业目录: " + jobDir);
            });
        }
    }

    private static void cleanupStaleCheckpointsInJobDir(Path jobDir, Path keepCheckpoint) throws IOException {
        if (!Files.exists(jobDir)) {
            return;
        }
        try (Stream<Path> checkpoints = Files.list(jobDir)) {
            checkpoints
                    .filter(Files::isDirectory)
                    .filter(path -> path.getFileName().toString().startsWith("chk-"))
                    .filter(path -> keepCheckpoint == null || !path.equals(keepCheckpoint))
                    .forEach(path -> {
                        deleteRecursively(path);
                        System.out.println("清理旧 checkpoint: " + path);
                    });
        }
    }

    private static void deleteRecursively(Path path) {
        try (Stream<Path> walk = Files.walk(path)) {
            walk.sorted(Comparator.reverseOrder()).forEach(p -> {
                try {
                    Files.deleteIfExists(p);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static long checkpointId(Path checkpointPath) {
        return Long.parseLong(checkpointPath.getFileName().toString().substring(4));
    }
}
