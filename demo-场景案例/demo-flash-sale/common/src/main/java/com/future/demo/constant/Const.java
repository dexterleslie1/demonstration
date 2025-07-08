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

    /**
     * 商品创建后设置商品ID和库存到redis zset中，协助实现下单时随机抽取商品逻辑
     */
    public final static String TopicAddProductIdAndStockAmountIntoRedisZSetAfterCreation = "topic-add-productId-and-stockAmount-into-redis-zset-after-creation";
    /**
     * 发布随机抽取的商品到 api 服务以便下单时随机抽取商品
     */
    public final static String TopicPublishChooseProductRandomlyForOrdering = "topic-publish-choose-product-randomly-for-ordering";

    /**
     * 协助实现下单时随机抽取商品逻辑
     */
    public final static String KeyProductIdAndStockAmountInRedisZSet = "productIdAndStockAmountZSet";
}
