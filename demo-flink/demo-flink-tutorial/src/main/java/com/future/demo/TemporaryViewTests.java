package com.future.demo;

import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.table.api.TableResult;
import org.apache.flink.table.api.bridge.java.StreamTableEnvironment;

public class TemporaryViewTests {

    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(1);

        StreamTableEnvironment tableEnv = StreamTableEnvironment.create(env);

        tableEnv.executeSql(
                "CREATE TEMPORARY VIEW parent_source AS\n" +
                        "SELECT\n" +
                        "    id,\n" +
                        "    company_id,\n" +
                        "    PROCTIME() AS proc_time\n" +
                        "FROM (\n" +
                        "    VALUES\n" +
                        "        (CAST(1 AS BIGINT), CAST(11 AS BIGINT)),\n" +
                        "        (CAST(2 AS BIGINT), CAST(22 AS BIGINT)),\n" +
                        "        (CAST(10 AS BIGINT), CAST(19 AS BIGINT))\n" +
                        ") AS t(id, company_id)"
        );

        TableResult result = tableEnv.executeSql("SELECT * FROM parent_source");
        result.print();
        result.await();
    }
}
