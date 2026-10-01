package com.future.demo;

import org.apache.flink.configuration.Configuration;
import org.apache.flink.core.execution.JobClient;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.table.api.StatementSet;
import org.apache.flink.table.api.TableResult;
import org.apache.flink.table.api.bridge.java.StreamTableEnvironment;

import java.util.Optional;

/**
 * 结论：这样是不能Partial Update的，如果不想存储状态则使用lookup join实现
 */
public class ParentAndChildTablePartialUpdateTests {

    private static final long RUN_DURATION_MS = 60_000;

    public static void main(String[] args) throws Exception {
        Configuration config = new Configuration();

        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment(config);
        env.setParallelism(1);

        StreamTableEnvironment tableEnv = StreamTableEnvironment.create(env);

        System.out.println("=== 主子表部分更新宽表（Flink SQL）演示 ===");
        System.out.println("两条 INSERT 分别更新主/子字段，无 JOIN 算子");

        tableEnv.executeSql(
                "CREATE TABLE parent_source (\n" +
                        "    id BIGINT,\n" +
                        "    company_id BIGINT\n" +
                        ") WITH (\n" +
                        "    'connector' = 'datagen',\n" +
                        "    'rows-per-second' = '1',\n" +
                        "    'fields.id.kind' = 'sequence',\n" +
                        "    'fields.id.start' = '1',\n" +
                        "    'fields.id.end' = '10',\n" +
                        "    'fields.company_id.min' = '1',\n" +
                        "    'fields.company_id.max' = '5'\n" +
                        ")"
        );

        tableEnv.executeSql(
                "CREATE TABLE child_source (\n" +
                        "    id BIGINT,\n" +
                        "    parent_id BIGINT,\n" +
                        "    content STRING\n" +
                        ") WITH (\n" +
                        "    'connector' = 'datagen',\n" +
                        "    'rows-per-second' = '1',\n" +
                        "    'fields.id.min' = '20',\n" +
                        "    'fields.id.max' = '30',\n" +
                        "    'fields.parent_id.kind' = 'sequence',\n" +
                        "    'fields.parent_id.start' = '1',\n" +
                        "    'fields.parent_id.end' = '10',\n" +
                        "    'fields.content.length' = '10'\n" +
                        ")"
        );

        tableEnv.executeSql(
                "CREATE TABLE wide_table_sink (\n" +
                        "    parent_id BIGINT,\n" +
                        "    company_id BIGINT,\n" +
                        "    child_id BIGINT,\n" +
                        "    content STRING,\n" +
                        "    PRIMARY KEY (parent_id, child_id) NOT ENFORCED\n" +
                        ") WITH (\n" +
                        "    'connector' = 'print'\n" +
                        ")"
        );

        StatementSet statementSet = tableEnv.createStatementSet();

        statementSet.addInsertSql(
                "INSERT INTO wide_table_sink\n" +
                        "SELECT\n" +
                        "       id AS parent_id,\n" +
                        "       company_id,\n" +
                        "       CAST(0 AS BIGINT) AS child_id,\n" +
                        "       CAST(NULL AS STRING) AS content\n" +
                        "FROM parent_source"
        );

        statementSet.addInsertSql(
                "INSERT INTO wide_table_sink\n" +
                        "SELECT\n" +
                        "       parent_id,\n" +
                        "       CAST(0 AS BIGINT) AS company_id,\n" +
                        "       id AS child_id,\n" +
                        "       content\n" +
                        "FROM child_source"
        );

        System.out.println("作业运行 " + RUN_DURATION_MS / 1000 + " 秒后自动 cancel...");
        TableResult result = statementSet.execute();
        Optional<JobClient> jobClient = result.getJobClient();
        if (jobClient.isPresent()) {
            Thread.sleep(RUN_DURATION_MS);
            jobClient.get().cancel().get();
            System.out.println("作业已 cancel。");
        } else {
            result.await();
        }
    }
}
