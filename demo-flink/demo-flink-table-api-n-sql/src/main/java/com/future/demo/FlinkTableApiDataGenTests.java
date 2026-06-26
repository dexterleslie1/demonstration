package com.future.demo;

import org.apache.flink.connector.datagen.table.DataGenConnectorOptions;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.table.api.DataTypes;
import org.apache.flink.table.api.Schema;
import org.apache.flink.table.api.Table;
import org.apache.flink.table.api.TableDescriptor;
import org.apache.flink.table.api.bridge.java.StreamTableEnvironment;

import static org.apache.flink.table.api.Expressions.$;

/**
 * 使用Table API实现DataGen
 */
public class FlinkTableApiDataGenTests {
    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        // 并发读为3
        env.setParallelism(3);

        StreamTableEnvironment tableEnv = StreamTableEnvironment.create(env);

        // 使用 TableDescriptor 定义 DataGen 源表
        TableDescriptor messagesDescriptor = TableDescriptor.forConnector("datagen")
                .schema(Schema.newBuilder()
                        .column("id", DataTypes.BIGINT())
                        .columnByExpression("content", "CONCAT('message-', CAST(id AS STRING))")
                        .column("event_time", DataTypes.TIMESTAMP(3))
                        .watermark("event_time", "event_time - INTERVAL '5' SECOND")
                        .build())
                .option(DataGenConnectorOptions.NUMBER_OF_ROWS, 10L)
                .option(DataGenConnectorOptions.ROWS_PER_SECOND, 2L)
                .option("fields.id.kind", "sequence")
                .option("fields.id.start", "1")
                .option("fields.id.end", "10")
                .option("fields.event_time.max-past", "5")
                .build();

        tableEnv.createTemporaryTable("messages", messagesDescriptor);

        // 使用 Table API 查询
        Table messages = tableEnv.from("messages");
        Table result = messages.select($("id"), $("content"), $("event_time"));

        result.execute().print();
    }
}
