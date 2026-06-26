package com.future.demo;

import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.table.api.TableResult;
import org.apache.flink.table.api.bridge.java.StreamTableEnvironment;

/**
 * 使用 Flink SQL JDBC 连接器读写 MySQL
 */
public class FlinkSQLConnectorJdbcTests {
    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(1);

        StreamTableEnvironment tableEnv = StreamTableEnvironment.create(env);

        // 第一步：定义 MySQL 源表（JDBC 连接器，需先 docker-compose up 启动 MySQL）
        tableEnv.executeSql(
                "CREATE TABLE auth_source (\n" +
                "    id BIGINT,\n" +
                "    account STRING,\n" +
                "    `password` STRING,\n" +
                "    create_time TIMESTAMP(3)\n" +
                ") WITH (\n" +
                "    'connector' = 'jdbc',\n" +
                "    'url' = 'jdbc:mysql://localhost:3306/demo?useSSL=false&serverTimezone=Asia/Shanghai',\n" +
                "    'table-name' = 'auth',\n" +
                "    'username' = 'root',\n" +
                "    'password' = '123456'\n" +
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

        // 第三步：从 MySQL 读取 auth 表数据并打印到控制台
        TableResult result = tableEnv.executeSql(
                "INSERT INTO auth_print\n" +
                "SELECT id, account, `password`, create_time FROM auth_source"
        );
        result.await();
    }
}
