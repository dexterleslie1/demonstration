package com.future.demo.constant;

public class Const {
    /**
     * 向随机 id 选择器添加 id 列表
     */
    public final static String TopicRandomIdPickerAddIdList = "topic-random-id-picker-add-id-list";
    public final static String TopicOrderInCacheSyncToDb = "topic-order-in-cache-sync-to-db";
    /**
     * 快速计数器 count 服务递增
     */
    public final static String TopicIncreaseCountFast = "topic-increase-count-fast";
//    /***
//     * 慢速计数器 count 服务递增
//     */
//    public final static String TopicIncreaseCountSlow = "topic-increase-count-slow";
    /**
     * 秒杀方式下单成功，订阅此消息生成 TopicOrderInCacheSyncToDb、TopicCreateOrderCassandraIndexListByUserId、TopicCreateOrderCassandraIndexListByMerchantId 3条消息以保证数据最终一致
     */
    public final static String TopicCreateFlashSaleOrderSuccessfully = "topic-create-flash-sale-order-successfully";
    /**
     * 创建订单 cassandra 索引 listByUserId
     */
    public final static String TopicCreateOrderCassandraIndexListByUserId = "topic-create-order-cassandra-index-listByUserId";
    /**
     * 创建订单 cassandra 索引 listByMerchantId
     */
    public final static String TopicCreateOrderCassandraIndexListByMerchantId = "topic-create-order-cassandra-index-listByMerchantId";
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
