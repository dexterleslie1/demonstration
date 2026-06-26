package com.future.demo;

import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.table.api.TableResult;
import org.apache.flink.table.api.bridge.java.StreamTableEnvironment;

/**
 * 使用 Flink SQL 定义源表和结果表
 */
public class FlinkSQLSourceNSinkTableTests {
    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(1);

        StreamTableEnvironment tableEnv = StreamTableEnvironment.create(env);

        // 第一步：定义源表（DataGen 模拟订单数据）
        tableEnv.executeSql(
                "CREATE TABLE orders (\n" +
                "    user_id BIGINT,\n" +
                "    amount DOUBLE,\n" +
                "    order_time TIMESTAMP(3),\n" +
                "    WATERMARK FOR order_time AS order_time - INTERVAL '5' SECOND\n" +
                ") WITH (\n" +
                "    'connector' = 'datagen',\n" +
                "    'number-of-rows' = '20',\n" +
                "    'rows-per-second' = '5',\n" +
                "    'fields.user_id.min' = '1',\n" +
                "    'fields.user_id.max' = '3',\n" +
                "    'fields.amount.min' = '1',\n" +
                "    'fields.amount.max' = '100'\n" +
                ")"
        );

        // 第二步：定义结果表（print 连接器，将结果打印到控制台）
        tableEnv.executeSql(
                "CREATE TABLE order_summary (\n" +
                "    user_id BIGINT,\n" +
                "    total_amount DOUBLE\n" +
                ") WITH (\n" +
                "    'connector' = 'print'\n" +
                ")"
        );

        // 第三步：实时计算并写入结果表
        TableResult result = tableEnv.executeSql(
                "INSERT INTO order_summary\n" +
                "SELECT user_id, SUM(amount) AS total_amount\n" +
                "FROM orders\n" +
                "GROUP BY user_id"
        );
         result.await();
    }
}
