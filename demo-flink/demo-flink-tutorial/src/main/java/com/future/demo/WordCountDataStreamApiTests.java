package com.future.demo;

import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.api.java.functions.KeySelector;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.util.Collector;

public class WordCountDataStreamApiTests {
    public static void main(String[] args) throws Exception {
        // 使用DataStream API实现
        // 创建执行环境
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        DataStream<String> dataStream = env.readTextFile("words.txt");
        DataStream<Tuple2<String, Integer>> flatMapStream = dataStream.flatMap(new FlatMapFunction<String, Tuple2<String, Integer>>() {
            @Override
            public void flatMap(String s, Collector<Tuple2<String, Integer>> collector) throws Exception {
                // s为每行数据，使用空格分割每个word
                String[] split = s.split(" ");
                for (String word : split) {
                    // 创建每个word的二元组
                    collector.collect(Tuple2.of(word, 1));
                }
            }
        });
        // keyBy使用二元组中第一个元素，sum使用二元组中的第二个元素，print打印sum后的结果
        flatMapStream.keyBy(new KeySelector<Tuple2<String, Integer>, String>() {
            @Override
            public String getKey(Tuple2<String, Integer> value) throws Exception {
                return value.f0;
            }
        }).sum(1).print();
        // DataStream API需要触发执行
        env.execute();
    }
}
