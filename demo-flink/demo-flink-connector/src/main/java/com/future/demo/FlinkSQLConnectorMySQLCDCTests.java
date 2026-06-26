package com.future.demo;

import org.apache.flink.configuration.CheckpointingOptions;
import org.apache.flink.configuration.Configuration;
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
 * 使用 Flink SQL MySQL CDC 连接器，支持 Checkpoint 断点续读
 * <p>
 * 首次启动：全量快照 + 增量 binlog；停止后再次启动：从上次 checkpoint 记录的 binlog 位点继续读取。
 * 如需从头同步，删除 {@link #CHECKPOINT_DIR} 目录后重启即可。
 */
public class FlinkSQLConnectorMySQLCDCTests {

    private static final String CHECKPOINT_DIR = Paths.get(
            System.getProperty("user.dir"), ".checkpoint", "mysql-cdc-auth"
    ).toUri().toString();

    public static void main(String[] args) throws Exception {
        Configuration config = new Configuration();
        config.set(CheckpointingOptions.CHECKPOINTS_DIRECTORY, CHECKPOINT_DIR);

        String recoveryPath = resolveLatestCheckpoint(CHECKPOINT_DIR);
        if (recoveryPath != null) {
            config.set(SavepointConfigOptions.SAVEPOINT_PATH, recoveryPath);
            System.out.println("从 checkpoint 恢复: " + recoveryPath);
        } else {
            System.out.println("未发现 checkpoint，首次启动（全量 + 增量）");
        }

        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment(config);
        env.setParallelism(1);
        env.enableCheckpointing(5000);
        env.getCheckpointConfig().setExternalizedCheckpointCleanup(
                CheckpointConfig.ExternalizedCheckpointCleanup.RETAIN_ON_CANCELLATION
        );

        StreamTableEnvironment tableEnv = StreamTableEnvironment.create(env);

        // 第一步：定义 MySQL CDC 源表（需先 docker-compose up 启动 MySQL）
        tableEnv.executeSql(
                "CREATE TABLE auth_source (\n" +
                "    id BIGINT,\n" +
                "    account STRING,\n" +
                "    `password` STRING,\n" +
                "    create_time TIMESTAMP(3),\n" +
                "    PRIMARY KEY (id) NOT ENFORCED\n" +
                ") WITH (\n" +
                "    'connector' = 'mysql-cdc',\n" +
                "    'hostname' = 'localhost',\n" +
                "    'port' = '3306',\n" +
                "    'username' = 'root',\n" +
                "    'password' = '123456',\n" +
                "    'database-name' = 'demo',\n" +
                "    'table-name' = 'auth'\n" +
                ")"
        );

        // 第二步：定义 print 输出表
        tableEnv.executeSql(
                "CREATE TABLE auth_print (\n" +
                "    id BIGINT,\n" +
                "    account STRING,\n" +
                "    `password` STRING,\n" +
                "    create_time TIMESTAMP(3)\n" +
                ") WITH (\n" +
                "    'connector' = 'print'\n" +
                ")"
        );

        // 第三步：实时同步 MySQL auth 表数据到控制台
        TableResult result = tableEnv.executeSql(
                "INSERT INTO auth_print\n" +
                "SELECT id, account, `password`, create_time FROM auth_source"
        );
        result.await();
    }

    /**
     * 查找最近一次已完成的外部化 checkpoint 目录
     */
    private static String resolveLatestCheckpoint(String checkpointDir) throws IOException {
        Path base = Paths.get(java.net.URI.create(checkpointDir));
        if (!Files.exists(base)) {
            return null;
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
                    .max(Comparator.comparingLong(FlinkSQLConnectorMySQLCDCTests::checkpointId));
        }

        return latest.map(path -> path.toUri().toString()).orElse(null);
    }

    private static long checkpointId(Path checkpointPath) {
        return Long.parseLong(checkpointPath.getFileName().toString().substring(4));
    }
}
