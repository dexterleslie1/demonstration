package com.future.demo;

import org.apache.flink.configuration.CheckpointingOptions;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.configuration.PipelineOptionsInternal;
import org.apache.flink.contrib.streaming.state.EmbeddedRocksDBStateBackend;
import org.apache.flink.runtime.jobgraph.RestoreMode;
import org.apache.flink.runtime.jobgraph.SavepointConfigOptions;
import org.apache.flink.streaming.api.environment.CheckpointConfig;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.table.api.StatementSet;
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
 * 主子表宽表 Lookup Join 演示：主/子表分别写入 JDBC 维表，宽表两条 INSERT 各自 Lookup 对侧维表补全字段，无需 Flink JOIN 状态。
 * <p>
 * 与 {@link ParentAndChildTablePartialUpdateTests} 对比：Partial Update 无法分两条 INSERT 更新宽表不同列；
 * Lookup Join 将主/子表持久化到 MySQL，主表流查 child_dim 补 child_id/content，子表流查 parent_dim 补 company_id。
 * <p>
 * Lookup 缓存等算子状态使用 EmbeddedRocksDBStateBackend 落盘；停止后再次启动从 checkpoint 恢复。
 * 如需从头开始，删除 {@link #CHECKPOINT_DIR} 与 {@link #ROCKSDB_DIR} 目录后重启。
 * <p>
 * 运行前需先启动 MySQL（参考 demo-flink-connector 目录 docker-compose up）并执行 db.sql 创建 parent_dim、child_dim 表。
 *
 * insert into parent_dim(id, company_id ) values(10011, 10);
 * insert into child_dim(id, company_id, parent_id) values(20011, 10, 10011);
 * insert into mx_dim(id, company_id, parent_id, child_id) values(30011, 10, 10011, 20011);
 *
 * delete from mx_dim where id = 30011;
 * delete from child_dim where id = 20011;
 * delete from parent_dim where id = 10011;
 *
 * select * from parent_dim;
 * select * from child_dim;
 * select * from mx_dim;
 *
 * 注意：经过测试，三级主子表关系同步到Doris宽表不能使用lookup join方式，因为在mx_dim删除记录时候会出现竞态问题。
 */
public class ParentAndChildTableLookupJoinTests {

    private static final String CHECKPOINT_DIR = Paths.get(
            System.getProperty("user.dir"), ".checkpoint", "lookup-join-parent-child"
    ).toUri().toString();

    private static final String ROCKSDB_DIR = Paths.get(
            System.getProperty("user.dir"), ".rocksdb", "lookup-join-parent-child"
    ).toString();

    /** 固定 JobID（32 位十六进制），使多次本地重启复用同一 checkpoint 子目录，num-retained 才会生效 */
    private static final String FIXED_JOB_ID = "0000000000000000000000000001c001";

    private static final String JDBC_URL =
            "jdbc:mysql://localhost:3306/demo?useSSL=false&serverTimezone=Asia/Shanghai";

    public static void main(String[] args) throws Exception {
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
        EmbeddedRocksDBStateBackend stateBackend = new EmbeddedRocksDBStateBackend(true);
        stateBackend.setDbStoragePaths(ROCKSDB_DIR);
        env.setStateBackend(stateBackend);
        env.setParallelism(1);
        env.enableCheckpointing(5000);
        env.getCheckpointConfig().configure(config);
        env.getCheckpointConfig().setExternalizedCheckpointCleanup(
                CheckpointConfig.ExternalizedCheckpointCleanup.RETAIN_ON_CANCELLATION
        );

        StreamTableEnvironment tableEnv = StreamTableEnvironment.create(env);

        // SinkUpsertMaterializer 是 Flink SQL 在 写入带主键的 Sink 表之前 自动插入的一个算子，用来把 CDC 产生的 changelog 流（+I / -U / +U / -D）整理成 Sink 能正确消费的 Upsert / Delete 语义。
        // 禁用 SinkUpsertMaterializer，否则导致算子状态膨胀
        tableEnv.getConfig().getConfiguration()
                .setString("table.exec.sink.upsert-materialize", "none");

        System.out.println("=== 主子表宽表 Lookup Join（Flink SQL）演示 ===");
        System.out.println("主/子表写入 MySQL 维表，宽表两条 INSERT 各自 Lookup 对侧维表，无 JOIN 算子状态");
        System.out.println("RocksDB State 目录: " + ROCKSDB_DIR);
        System.out.println("Checkpoint 目录: " + CHECKPOINT_DIR);

        // 创建 parent_dim、child_dim、mx_dim mysql-cdc 源表
        tableEnv.executeSql(
                "CREATE TABLE parent_dim (\n" +
                        "    id BIGINT,\n" +
                        "    company_id BIGINT,\n" +
                        "    proc_time AS PROCTIME(),\n" +
                        "    PRIMARY KEY (id) NOT ENFORCED\n" +
                        ") WITH (\n" +
                        "    'connector' = 'mysql-cdc',\n" +
                        "    'hostname' = 'localhost',\n" +
                        "    'port' = '3306',\n" +
                        "    'username' = 'root',\n" +
                        "    'password' = '123456',\n" +
                        "    'database-name' = 'demo',\n" +
                        "    'table-name' = 'parent_dim'\n" +
                        ")"
        );

        tableEnv.executeSql(
                "CREATE TABLE child_dim (\n" +
                        "    id BIGINT,\n" +
                        "    company_id BIGINT,\n" +
                        "    parent_id BIGINT,\n" +
                        "    proc_time AS PROCTIME(),\n" +
                        "    PRIMARY KEY (id) NOT ENFORCED\n" +
                        ") WITH (\n" +
                        "    'connector' = 'mysql-cdc',\n" +
                        "    'hostname' = 'localhost',\n" +
                        "    'port' = '3306',\n" +
                        "    'username' = 'root',\n" +
                        "    'password' = '123456',\n" +
                        "    'database-name' = 'demo',\n" +
                        "    'table-name' = 'child_dim'\n" +
                        ")"
        );

        tableEnv.executeSql(
                "CREATE TABLE mx_dim (\n" +
                        "    id BIGINT,\n" +
                        "    company_id BIGINT,\n" +
                        "    parent_id BIGINT,\n" +
                        "    child_id BIGINT,\n" +
                        "    proc_time AS PROCTIME(),\n" +
                        "    PRIMARY KEY (id) NOT ENFORCED\n" +
                        ") WITH (\n" +
                        "    'connector' = 'mysql-cdc',\n" +
                        "    'hostname' = 'localhost',\n" +
                        "    'port' = '3306',\n" +
                        "    'username' = 'root',\n" +
                        "    'password' = '123456',\n" +
                        "    'database-name' = 'demo',\n" +
                        "    'table-name' = 'mx_dim'\n" +
                        ")"
        );

        // JDBC 维表作为 Lookup Join 右表时，Flink 会在 TaskManager 本地维护查询结果缓存，
        // 避免每条流记录都向 MySQL 发起 SELECT，降低维表数据库压力与 lookup 延迟。
        //
        // lookup.cache.max-rows = '500'
        //   缓存最多保留 500 条维表查询结果（按 lookup 主键去重，每条主键对应一行）。
        //   超过上限时淘汰最久未使用的条目（LRU）；设为 '0' 表示关闭缓存，每次 lookup 都查库。
        //
        // lookup.cache.ttl = '10 min'
        //   单条缓存从写入起最多存活 10 分钟，到期后下次 lookup 会重新查 MySQL 并刷新缓存。
        //   维表在外部被更新（如手动改 parent_dim）时，TTL 内可能读到旧值；TTL 越短数据越新，但查库越频繁。
        //   设为 '0 ms' 表示关闭缓存。
        //
        // 二者配合：在 max-rows 限制内存占用的同时，用 TTL 控制数据新鲜度；本示例维表仅 10 行主键，500 足够。
        tableEnv.executeSql(
                "CREATE TABLE parent_dim_lookup (\n" +
                        "    id BIGINT,\n" +
                        "    company_id BIGINT,\n" +
                        "    PRIMARY KEY (id) NOT ENFORCED\n" +
                        ") WITH (\n" +
                        "    'connector' = 'jdbc',\n" +
                        "    'url' = '" + JDBC_URL + "',\n" +
                        "    'table-name' = 'parent_dim',\n" +
                        "    'username' = 'root',\n" +
                        "    'password' = '123456',\n" +
                        "    'lookup.cache.max-rows' = '500',\n" +
                        "    'lookup.cache.ttl' = '10 min'\n" +
                        ")"
        );

        // child_dim_lookup 同样作为 Lookup 右表，缓存参数含义与 parent_dim_lookup 一致
        tableEnv.executeSql(
                "CREATE TABLE child_dim_lookup (\n" +
                        "    id BIGINT,\n" +
                        "    company_id BIGINT,\n" +
                        "    parent_id BIGINT,\n" +
                        "    PRIMARY KEY (id) NOT ENFORCED\n" +
                        ") WITH (\n" +
                        "    'connector' = 'jdbc',\n" +
                        "    'url' = '" + JDBC_URL + "',\n" +
                        "    'table-name' = 'child_dim',\n" +
                        "    'username' = 'root',\n" +
                        "    'password' = '123456',\n" +
                        "    'lookup.cache.max-rows' = '0',\n" +
                        "    'lookup.cache.ttl' = '0 ms'\n" +
                        ")"
        );

        tableEnv.executeSql(
                "CREATE TABLE mx_dim_lookup (\n" +
                        "    id BIGINT,\n" +
                        "    company_id BIGINT,\n" +
                        "    parent_id BIGINT,\n" +
                        "    child_id BIGINT,\n" +
                        "    PRIMARY KEY (id) NOT ENFORCED\n" +
                        ") WITH (\n" +
                        "    'connector' = 'jdbc',\n" +
                        "    'url' = '" + JDBC_URL + "',\n" +
                        "    'table-name' = 'mx_dim',\n" +
                        "    'username' = 'root',\n" +
                        "    'password' = '123456',\n" +
                        "    'lookup.cache.max-rows' = '0',\n" +
                        "    'lookup.cache.ttl' = '0 ms'\n" +
                        ")"
        );

        tableEnv.executeSql(
                "CREATE TABLE wide_table_sink (\n" +
                        "    parent_id BIGINT,\n" +
                        "    company_id BIGINT,\n" +
                        "    child_id BIGINT,\n" +
                        "    mx_id BIGINT,\n" +
                        "    PRIMARY KEY (company_id, parent_id, child_id, mx_id) NOT ENFORCED\n" +
                        ") WITH (\n" +
                        "    'connector' = 'print'\n" +
                        ")"
        );

        StatementSet statementSet = tableEnv.createStatementSet();

        // 分别注释下面的表SQL测试parent、child、mx lookup join
        // 向MySQL发出SQL: SELECT `id`, `company_id`, `parent_id`, `content` FROM `child_dim` WHERE `company_id` = 1 AND `parent_id` = 10
        statementSet.addInsertSql(
                "INSERT INTO wide_table_sink\n" +
                        "SELECT\n" +
                        "       COALESCE(p.id, CAST(0 AS BIGINT)) AS parent_id,\n" +
                        "       COALESCE(p.company_id, CAST(0 AS BIGINT)) AS company_id,\n" +
                        "       COALESCE(c.id, CAST(0 AS BIGINT)) AS child_id,\n" +
                        "       COALESCE(m.id, CAST(0 AS BIGINT)) AS mx_id\n" +
                        "FROM parent_dim p\n" +
                        "JOIN child_dim_lookup FOR SYSTEM_TIME AS OF p.proc_time AS c\n" +
                        "ON p.company_id = c.company_id AND p.id = c.parent_id\n" +
                        "LEFT JOIN mx_dim_lookup FOR SYSTEM_TIME AS OF p.proc_time AS m\n" +
                        "ON p.company_id = m.company_id AND c.parent_id = m.parent_id AND c.id = m.child_id"
        );
        // 向MySQL发出SQL: SELECT `id`, `company_id`, `parent_id`, `child_id` FROM `mx_dim` WHERE `company_id` = 10 AND `parent_id` = 1001 AND `child_id` = 20011
        statementSet.addInsertSql(
                "INSERT INTO wide_table_sink\n" +
                        "SELECT\n" +
                        "       COALESCE(c.parent_id, CAST(0 AS BIGINT)) AS parent_id,\n" +
                        "       COALESCE(p.company_id, CAST(0 AS BIGINT)) AS company_id,\n" +
                        "       COALESCE(c.id, CAST(0 AS BIGINT)) AS child_id,\n" +
                        "       COALESCE(m.id, CAST(0 AS BIGINT)) AS mx_id\n" +
                        "FROM child_dim c\n" +
                        "JOIN parent_dim_lookup FOR SYSTEM_TIME AS OF c.proc_time AS p\n" +
                        "ON c.company_id = p.company_id AND c.parent_id = p.id\n" +
                        "LEFT JOIN mx_dim_lookup FOR SYSTEM_TIME AS OF c.proc_time AS m\n" +
                        "ON c.company_id = m.company_id AND c.parent_id = m.parent_id AND c.id = m.child_id"
        );

        // mx_dim为主表的lookup join
        // 向MySQL发出SQL: SELECT `id`, `company_id` FROM `parent_dim` WHERE `id` = 1001 AND `company_id` = 10
        // 向MySQL发出SQL: SELECT `id`, `company_id`, `parent_id` FROM `child_dim` WHERE `id` = 20011 AND `company_id` = 10 AND `parent_id` = 1001
        // 注意：这里会因为mx_dim删除指令迟到导致lookup不到parent_dim或者child_dim，进而导致主键错误无法正确删除数据
        statementSet.addInsertSql(
                "INSERT INTO wide_table_sink\n" +
                        "SELECT\n" +
                        "       COALESCE(m.parent_id, CAST(0 AS BIGINT)) AS parent_id,\n" +
                        "       COALESCE(p.company_id, CAST(0 AS BIGINT)) AS company_id,\n" +
                        "       COALESCE(m.child_id, CAST(0 AS BIGINT)) AS child_id,\n" +
                        "       COALESCE(m.id, CAST(0 AS BIGINT)) AS mx_id\n" +
                        "FROM mx_dim m\n" +
                        "LEFT JOIN parent_dim_lookup FOR SYSTEM_TIME AS OF m.proc_time AS p\n" +
                        "ON m.company_id = p.company_id AND m.parent_id = p.id\n" +
                        "LEFT JOIN child_dim_lookup FOR SYSTEM_TIME AS OF m.proc_time AS c\n" +
                        "ON m.company_id = c.company_id AND m.parent_id = c.parent_id AND m.child_id = c.id"
        );

        TableResult result = statementSet.execute();
        result.await();
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
                    .max(Comparator.comparingLong(ParentAndChildTableLookupJoinTests::checkpointId));
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
                    .max(Comparator.comparingLong(ParentAndChildTableLookupJoinTests::checkpointId));
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
