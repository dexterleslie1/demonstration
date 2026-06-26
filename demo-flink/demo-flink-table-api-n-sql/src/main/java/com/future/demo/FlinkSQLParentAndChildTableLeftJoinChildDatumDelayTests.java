package com.future.demo;

import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.table.api.TableResult;
import org.apache.flink.table.api.bridge.java.StreamTableEnvironment;

/**
 * 主子表 LEFT JOIN 演示：主表数据先到，子表数据延迟到达
 * 测试结论：主表数据先到会形成记录+I[1, 1, null, null, 2026-06-26T10:41:27.059]（没有子表数据）输出到 sink 表，子表数据后到会根据 dj_id 撤回之前子表为 null 的记录并重新输出完整 left join 结果
 */
public class FlinkSQLParentAndChildTableLeftJoinChildDatumDelayTests {
    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(1);

        StreamTableEnvironment tableEnv = StreamTableEnvironment.create(env);

        // 定义主表（模拟先到：10 条/秒，快于子表）
        tableEnv.executeSql(
                "CREATE TABLE parent_table (\n" +
                "    id BIGINT,\n" +
                "    company_id BIGINT,\n" +
                "    event_time TIMESTAMP(3),\n" +
                "    WATERMARK FOR event_time AS event_time - INTERVAL '5' SECOND\n" +
                ") WITH (\n" +
                "    'connector' = 'datagen',\n" +
                "    'rows-per-second' = '10',\n" +
                "    'fields.id.kind' = 'sequence',\n" +
                "    'fields.id.start' = '1',\n" +
                "    'fields.id.end' = '20',\n" +
                "    'fields.company_id.min' = '1',\n" +
                "    'fields.company_id.max' = '5',\n" +
                "    'fields.event_time.max-past' = '60'\n" +
                ")"
        );

        // 定义子表（模拟延迟：1 条/秒，dj_id 与主表 id 对齐）
        tableEnv.executeSql(
                "CREATE TABLE child_table (\n" +
                "    id BIGINT,\n" +
                "    dj_id BIGINT,\n" +
                "    content STRING\n" +
                ") WITH (\n" +
                "    'connector' = 'datagen',\n" +
                "    'rows-per-second' = '1',\n" +
                "    'fields.id.min' = '1',\n" +
                "    'fields.id.max' = '100',\n" +
                "    'fields.dj_id.kind' = 'sequence',\n" +
                "    'fields.dj_id.start' = '1',\n" +
                "    'fields.dj_id.end' = '20',\n" +
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

        // 主子表 LEFT JOIN：主表先到先输出（子表字段为 null），子表到达后补全关联结果
        TableResult result = tableEnv.executeSql(
                "INSERT INTO print_sink\n" +
                "SELECT parent.id AS parent_id,\n" +
                "       parent.company_id,\n" +
                "       child.id AS child_id,\n" +
                "       child.content,\n" +
                "       parent.event_time\n" +
                "FROM parent_table parent\n" +
                "LEFT JOIN child_table child ON parent.id = child.dj_id"
        );
        result.await();
    }
}
