package com.future.demo.constant;

public class Constant {
    /**
     * 用于测试各个消费者配置独立
     */
    public final static String Topic1 = "my-topic-1";
    /**
     * 用于测试各个消费者配置独立
     */
    public final static String Topic2 = "my-topic-2";
    /**
     * 用于测试高效率发送消息性能
     */
    public final static String TopicTestSendPerf = "topic-test-send-perf";
    /**
     * 协助测试在线修改主题分区数
     */
    public final static String TopicTestAlterPartitionsOnline = "topic-test-alter-partitions-online";

    public final static String KeyConfigOptionAutoOffsetResetCounter = "auto-offset-reset-counter";
}
