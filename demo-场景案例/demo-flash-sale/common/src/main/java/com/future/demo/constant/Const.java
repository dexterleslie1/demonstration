package com.future.demo.constant;

public class Const {
    public final static String TopicOrderInCacheSyncToDb = "topic-order-in-cache-sync-to-db";
    /**
     * 递增数据库和Cassandra t_count 主题
     */
    public final static String TopicIncreaseCount = "topic-increase-count";
    /**
     * 创建订单 cassandra 索引消息队列
     */
    public final static String TopicCreateOrderCassandraIndex = "topic-create-order-cassandra-index";
    /**
     * 新增秒杀商品后，设置商品缓存主题
     */
    public final static String TopicSetupProductFlashSaleCache = "topic-setup-product-flashsale-cache";
}
