package com.future.demo.constant;

public class Const {
    /**
     * 向随机 id 选择器添加 id 列表
     */
    public final static String TopicRandomIdPickerAddIdList = "topic-random-id-picker-add-id-list";
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
     * 用户下单后，缓存订单信息到 redis 中，在 Cassandra 索引建立后自动删除此缓存
     */
    public final static String CacheKeyPrefixOrderInCacheBeforeCassandraIndexCreate = "orderInCacheBeforeCassandraIndexCreate:";
    /**
     * 向缓存中添加商品用于下单时随机抽取商品
     */
    public final static String TopicAddProductToCacheForPickupRandomlyWhenPurchasing = "topic-add-product-to-cache-for-pickup-randomly-when-purchasing";
    /**
     * 随机选择商品缓存的普通商品 RSet
     */
    public final static String KeyRSetProductIdOrdinaryForPickupRandomlyWhenPurchasing = "productIdOrdinaryForPickupRandomlyWhenPurchasing";
    /**
     * 随机选择商品缓存的秒杀商品 RSet
     */
    public final static String KeyRSetProductIdFlashSaleForPickupRandomlyWhenPurchasing = "productIdFlashSaleForPickupRandomlyWhenPurchasing";
}
