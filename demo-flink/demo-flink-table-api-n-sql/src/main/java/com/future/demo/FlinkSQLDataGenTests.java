package com.future.demo;

import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.table.api.TableResult;
import org.apache.flink.table.api.bridge.java.StreamTableEnvironment;

/**
 * 使用Flink SQL实现DataGen
 */
public class FlinkSQLDataGenTests {
    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        // 并发读为3
        env.setParallelism(3);

        StreamTableEnvironment tableEnv = StreamTableEnvironment.create(env);

        // 使用 DataGen 连接器自动生成指定数量的测试数据
        tableEnv.executeSql(
                "CREATE TABLE messages (\n" +
                "    id BIGINT,\n" +
                "    dj_id BIGINT,\n" +
                "    content AS CONCAT('message-', CAST(id AS STRING))/*,*/\n" +
                /*"    event_time TIMESTAMP(3),\n" +
                "    WATERMARK FOR event_time AS event_time - INTERVAL '5' SECOND\n" +*/
                ") WITH (\n" +
                "    'connector' = 'datagen',\n" +
                /*"    'number-of-rows' = '10',\n" +*/
                "    'rows-per-second' = '1',\n" +
                "    'fields.id.min' = '1',\n" +
                "    'fields.id.max' = '10',\n" +
                "    'fields.dj_id.kind' = 'sequence',\n" +
                "    'fields.dj_id.start' = '1',\n" +
                "    'fields.dj_id.end' = '10'/*,*/\n" +
                /*"    'fields.event_time.max-past' = '5'\n" +*/
                ")"
        );

        // 定义 print sink 表，将结果打印到控制台
        tableEnv.executeSql(
                "CREATE TABLE print_sink (\n" +
                "    id BIGINT,\n" +
                "    content STRING,\n" +
                "    dj_id BIGINT\n" +
                ") WITH (\n" +
                "    'connector' = 'print'\n" +
                ")"
        );

        TableResult result = tableEnv.executeSql(
                "INSERT INTO print_sink\n" +
                "SELECT id, content, dj_id/*, event_time*/ FROM messages"
        );
        result.await();
    }
}
