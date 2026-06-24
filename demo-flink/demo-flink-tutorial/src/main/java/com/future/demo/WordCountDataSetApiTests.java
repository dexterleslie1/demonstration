package com.future.demo;

import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.api.java.DataSet;
import org.apache.flink.api.java.ExecutionEnvironment;
import org.apache.flink.api.java.operators.FlatMapOperator;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.util.Collector;

public class WordCountDataSetApiTests {
    public static void main(String[] args) throws Exception {
        // 使用DataSet API实现
        // 创建执行环境
        ExecutionEnvironment env = ExecutionEnvironment.getExecutionEnvironment();
        DataSet<String> dataSet = env.readTextFile("words.txt");
        FlatMapOperator<String, Tuple2<String, Integer>> flatMapOperator = dataSet.flatMap(new FlatMapFunction<String, Tuple2<String, Integer>>() {
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
        // groupBy使用二元组中第一个元素，sum使用二元组中的第二个元素，print打印sum后的结果
        flatMapOperator.groupBy(0).sum(1).print();
    }
}
