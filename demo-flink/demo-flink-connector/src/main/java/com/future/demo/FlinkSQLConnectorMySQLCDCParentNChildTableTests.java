package com.future.demo;

import org.apache.flink.configuration.CheckpointingOptions;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.configuration.PipelineOptionsInternal;
import org.apache.flink.contrib.streaming.state.EmbeddedRocksDBStateBackend;
import org.apache.flink.runtime.jobgraph.RestoreMode;
import org.apache.flink.runtime.jobgraph.SavepointConfigOptions;
import org.apache.flink.streaming.api.environment.CheckpointConfig;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.table.api.TableResult;
import org.apache.flink.table.api.bridge.java.StreamTableEnvironment;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;
import java.util.Optional;
import java.util.stream.Stream;

/**
 * 使用 Flink SQL MySQL CDC 连接器读取主子表，LEFT JOIN 后输出到控制台，支持 Checkpoint 断点续读
 * <p>
 * 首次启动：全量快照 + 增量 binlog；停止后再次启动：从上次 checkpoint 记录的 binlog 位点继续读取。
 * 如需从头同步，删除 {@link #CHECKPOINT_DIR} 与 {@link #ROCKSDB_DIR} 目录后重启即可。
 * JOIN State 使用 EmbeddedRocksDBStateBackend 落盘；从 HashMapStateBackend 切换后需删除旧 checkpoint 再启动。
 * <p>
 * 测试建议：先只插入 parent_order，观察子表字段为 null 的输出；再插入 child_item，观察撤回并补全 join 结果。
 */
public class FlinkSQLConnectorMySQLCDCParentNChildTableTests {

    private static final String CHECKPOINT_DIR = Paths.get(
            System.getProperty("user.dir"), ".checkpoint", "mysql-cdc-parent-child"
    ).toUri().toString();

    private static final String ROCKSDB_DIR = Paths.get(
            System.getProperty("user.dir"), ".rocksdb", "mysql-cdc-parent-child"
    ).toString();

    /** 固定 JobID，使多次本地重启复用同一 checkpoint 子目录，num-retained 才会生效 */
    private static final String FIXED_JOB_ID = "000000000000000000000000000cdc01";

    public static void main(String[] args) throws Exception {
        Configuration config = new Configuration();
        config.set(CheckpointingOptions.CHECKPOINTS_DIRECTORY, CHECKPOINT_DIR);
        config.set(CheckpointingOptions.MAX_RETAINED_CHECKPOINTS, 1);
        config.set(PipelineOptionsInternal.PIPELINE_FIXED_JOB_ID, FIXED_JOB_ID);

        Optional<Path> latestCheckpoint = resolveLatestCheckpoint(CHECKPOINT_DIR, FIXED_JOB_ID);
        latestCheckpoint.ifPresent(path -> {
            config.set(SavepointConfigOptions.SAVEPOINT_PATH, path.toUri().toString());
            // CLAIM：Flink 接管恢复来源 checkpoint 的生命周期，num-retained 才能 subsume 旧 chk
            config.set(SavepointConfigOptions.RESTORE_MODE, RestoreMode.CLAIM);
        });
        cleanupCheckpointStorage(CHECKPOINT_DIR, FIXED_JOB_ID, latestCheckpoint.orElse(null));
        if (latestCheckpoint.isPresent()) {
            System.out.println("从 checkpoint 恢复: " + latestCheckpoint.get().toUri());
        } else {
            System.out.println("未发现 checkpoint，首次启动（全量 + 增量）");
        }

        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment(config);
        // 表状态保存在RocksDB中，不配置默认会保存在HashMapStateBackend内存中导致记录越多消耗内存越多
        EmbeddedRocksDBStateBackend stateBackend = new EmbeddedRocksDBStateBackend(true);
        stateBackend.setDbStoragePaths(ROCKSDB_DIR);
        env.setStateBackend(stateBackend);
        System.out.println("RocksDB State 目录: " + ROCKSDB_DIR);
        env.setParallelism(1);
        env.enableCheckpointing(5000);
        env.getCheckpointConfig().configure(config);
        env.getCheckpointConfig().setExternalizedCheckpointCleanup(
                CheckpointConfig.ExternalizedCheckpointCleanup.RETAIN_ON_CANCELLATION
        );

        StreamTableEnvironment tableEnv = StreamTableEnvironment.create(env);

        // 第一步：定义主表 MySQL CDC 源表（需先 docker-compose up 启动 MySQL）
        tableEnv.executeSql(
                "CREATE TABLE parent_source (\n" +
                "    id BIGINT,\n" +
                "    company_id BIGINT,\n" +
                "    event_time TIMESTAMP(3),\n" +
                "    PRIMARY KEY (id) NOT ENFORCED\n" +
                ") WITH (\n" +
                "    'connector' = 'mysql-cdc',\n" +
                "    'hostname' = 'localhost',\n" +
                "    'port' = '3306',\n" +
                "    'username' = 'root',\n" +
                "    'password' = '123456',\n" +
                "    'database-name' = 'demo',\n" +
                "    'table-name' = 'parent_order'\n" +
                ")"
        );

        // 第二步：定义子表 MySQL CDC 源表
        tableEnv.executeSql(
                "CREATE TABLE child_source (\n" +
                "    id BIGINT,\n" +
                "    dj_id BIGINT,\n" +
                "    content STRING,\n" +
                "    PRIMARY KEY (id) NOT ENFORCED\n" +
                ") WITH (\n" +
                "    'connector' = 'mysql-cdc',\n" +
                "    'hostname' = 'localhost',\n" +
                "    'port' = '3306',\n" +
                "    'username' = 'root',\n" +
                "    'password' = '123456',\n" +
                "    'database-name' = 'demo',\n" +
                "    'table-name' = 'child_item'\n" +
                ")"
        );

        // 第三步：定义 print 输出表
        tableEnv.executeSql(
                "CREATE TABLE join_print (\n" +
                "    parent_id BIGINT,\n" +
                "    company_id BIGINT,\n" +
                "    child_id BIGINT,\n" +
                "    content STRING,\n" +
                "    event_time TIMESTAMP(3)\n" +
                ") WITH (\n" +
                "    'connector' = 'print'\n" +
                ")"
        );

        // 第四步：主子表 LEFT JOIN，实时同步到控制台
        TableResult result = tableEnv.executeSql(
                "INSERT INTO join_print\n" +
                "SELECT parent.id AS parent_id,\n" +
                "       parent.company_id,\n" +
                "       child.id AS child_id,\n" +
                "       child.content,\n" +
                "       parent.event_time\n" +
                "FROM parent_source parent\n" +
                "LEFT JOIN child_source child ON parent.id = child.dj_id"
        );
        result.await();
    }

    /**
     * 查找最近一次已完成的外部化 checkpoint 目录。
     * 优先使用固定 JobID 目录下的 checkpoint；若尚未产生则回退到历史目录（迁移旧数据）。
     */
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
                    .max(Comparator.comparingLong(FlinkSQLConnectorMySQLCDCParentNChildTableTests::checkpointId));
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
                    .max(Comparator.comparingLong(FlinkSQLConnectorMySQLCDCParentNChildTableTests::checkpointId));
        }
    }

    /**
     * 清理 checkpoint 存储：删除孤儿 JobID 目录；在保留目录内只留最新 chk（任务被 kill 时 Flink 来不及清理）。
     */
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
