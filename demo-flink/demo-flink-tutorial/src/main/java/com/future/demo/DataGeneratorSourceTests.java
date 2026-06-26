package com.future.demo;

import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.api.common.typeinfo.Types;
import org.apache.flink.api.connector.source.util.ratelimit.RateLimiterStrategy;
import org.apache.flink.connector.datagen.source.DataGeneratorSource;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

public class DataGeneratorSourceTests {
    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        // 并发读为3
        env.setParallelism(3);

        // 使用 DataGeneratorSource 自动生成指定数量的数据
        DataGeneratorSource<String> generatorSource = new DataGeneratorSource<>(
                index -> "message-" + index,
                10,
                // 每个并发每秒生产2条记录
                RateLimiterStrategy.perSecond(2),
                Types.STRING
        );

        DataStream<String> stream = env.fromSource(
                generatorSource,
                WatermarkStrategy.noWatermarks(),
                "generator-source"
        );

        stream.print();
        env.execute();
    }
}
