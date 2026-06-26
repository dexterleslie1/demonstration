package com.future.demo;

import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.table.api.TableResult;
import org.apache.flink.table.api.bridge.java.StreamTableEnvironment;

/**
 * 主子表 LEFT JOIN 演示：子表数据先到，主表数据延迟到达
 * 测试结论：子表数据先到会被存储起来，主表数据后到会left join先到的子表数据并输出到sink表
 */
public class FlinkSQLParentAndChildTableLeftJoinParentDatumDelayTests {
    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(1);

        StreamTableEnvironment tableEnv = StreamTableEnvironment.create(env);

        // 定义主表（模拟延迟：1 条/秒，慢于子表）
        tableEnv.executeSql(
                "CREATE TABLE parent_table (\n" +
                "    id BIGINT,\n" +
                "    company_id BIGINT,\n" +
                "    event_time TIMESTAMP(3),\n" +
                "    WATERMARK FOR event_time AS event_time - INTERVAL '5' SECOND\n" +
                ") WITH (\n" +
                "    'connector' = 'datagen',\n" +
                "    'rows-per-second' = '1',\n" +
                "    'fields.id.kind' = 'sequence',\n" +
                "    'fields.id.start' = '1',\n" +
                "    'fields.id.end' = '20',\n" +
                "    'fields.company_id.min' = '1',\n" +
                "    'fields.company_id.max' = '5',\n" +
                "    'fields.event_time.max-past' = '60'\n" +
                ")"
        );

        // 定义子表（模拟先到：10 条/秒，dj_id 与主表 id 对齐）
        tableEnv.executeSql(
                "CREATE TABLE child_table (\n" +
                "    id BIGINT,\n" +
                "    dj_id BIGINT,\n" +
                "    content STRING\n" +
                ") WITH (\n" +
                "    'connector' = 'datagen',\n" +
                "    'rows-per-second' = '10',\n" +
                "    'fields.id.min' = '1',\n" +
                "    'fields.id.max' = '100',\n" +
                "    'fields.dj_id.kind' = 'sequence',\n" +
                "    'fields.dj_id.start' = '1',\n" +
                "    'fields.dj_id.end' = '21',\n" +
                "    'fields.content.length' = '10'\n" +
                ")"
        );

        // 定义 print sink 表，将结果打印到控制台
        tableEnv.executeSql(
                "CREATE TABLE print_sink (\n" +
                "    parent_id BIGINT,\n" +
                "    company_id BIGINT,\n" +
                "    child_id BIGINT,\n" +
                "    content STRING,\n" +
                "    event_time TIMESTAMP(3)\n" +
                ") WITH (\n" +
                "    'connector' = 'print'\n" +
                ")"
        );

        // 主子表 INNER JOIN：子表先到时不输出，主表到达后才会关联出结果
        TableResult result = tableEnv.executeSql(
                "INSERT INTO print_sink\n" +
                "SELECT parent.id AS parent_id,\n" +
                "       parent.company_id,\n" +
                "       child.id AS child_id,\n" +
                "       child.content,\n" +
                "       parent.event_time\n" +
                "FROM parent_table parent\n" +
                "LEFT JOIN child_table child ON child.dj_id = parent.id"
        );
        result.await();
    }
}
