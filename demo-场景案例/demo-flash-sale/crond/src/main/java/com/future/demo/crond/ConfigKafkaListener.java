package com.future.demo.crond;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.future.demo.config.PrometheusCustomMonitor;
import com.future.demo.dto.FlashSaleProductCacheUpdateEventDTO;
import com.future.demo.dto.IncreaseCountDTO;
import com.future.demo.entity.OrderModel;
import com.future.demo.entity.ProductModel;
import com.future.demo.mapper.CassandraMapper;
import com.future.demo.mapper.CommonMapper;
import com.future.demo.mapper.ProductMapper;
import com.future.demo.service.CommonService;
import com.future.demo.service.OrderService;
import com.future.demo.service.ProductService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.util.backoff.FixedBackOff;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;


@Configuration
@Slf4j
public class ConfigKafkaListener {
    @Resource
    ObjectMapper objectMapper;
    @Autowired
    StringRedisTemplate redisTemplate;
    @Resource
    OrderService orderService;
    @Resource
    PrometheusCustomMonitor monitor;
    @Resource
    CommonMapper commonMapper;
    @Resource
    CommonService commonService;
    @Resource
    ProductMapper productMapper;
    @Resource
    CassandraMapper cassandraMapper;
    @Resource
    KafkaTemplate kafkaTemplate;
    @Resource
    ProductService productService;

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, String> kafkaListenerContainerFactory(
            ConsumerFactory consumerFactory) {
        ConcurrentKafkaListenerContainerFactory<String, String> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory);
        // 启用批量消费
        factory.setBatchListener(true);
        // 设置并发线程数
        factory.setConcurrency(256);
        // 绑定重试错误处理器
        factory.setCommonErrorHandler(retryErrorHandler(kafkaTemplate));
        return factory;
    }

    // 定义重试错误处理器（核心）
    @Bean
    public DefaultErrorHandler retryErrorHandler(KafkaTemplate<Object, Object> template) {
        // 配置重试策略：无限次重试，每次间隔5秒
        // 5000ms间隔，FixedBackOff.UNLIMITED_ATTEMPTS 表示无限次
        FixedBackOff fixedBackOff = new FixedBackOff(5000L, /*FixedBackOff.UNLIMITED_ATTEMPTS*/ 180);

        // 使用RetryTopic的ErrorHandler（自动处理重试和DLQ）
        return new DefaultErrorHandler(
                // 自定义恢复逻辑（可选，当重试耗尽时触发）
                (record, ex) -> {
                    log.error("重试耗尽，消息进入死信队列：{}", record.value());
                },
                fixedBackOff
        );
    }

    private AtomicInteger concurrentCounter = new AtomicInteger();
    private AtomicLong counter = new AtomicLong();

    /**
     * @param messages
     * @throws Exception
     */
    @KafkaListener(topics = com.future.demo.constant.Const.TopicOrderInCacheSyncToDb, concurrency = "1" /* 注意： todo 太大的 concurrency 会拖慢 /actuator/prometheus 接口 */)
    public void receiveMessage(List<String> messages) throws Exception {
        try {
            log.info("concurrent=" + this.concurrentCounter.incrementAndGet() + ",size=" + messages.size() + ",total=" + counter.addAndGet(messages.size()));

            List<OrderModel> orderModelList = new ArrayList<>();
            for (String JSON : messages) {
                OrderModel orderModel = objectMapper.readValue(JSON, OrderModel.class);
                orderModelList.add(orderModel);
            }

            // 普通下单不需要设置merchantId，只有秒杀才需要设置merchantId
            boolean needToSetMerchantId = false;
            for (OrderModel model : orderModelList) {
                if (model.getMerchantId() == null) {
                    needToSetMerchantId = true;
                    break;
                }
            }
            if (needToSetMerchantId)
                orderService.fillupOrderMerchantId(orderModelList);
            this.orderService.insertBatch(orderModelList);

            this.monitor.incrementOrderSyncCount(orderModelList.size());
        } catch (Exception ex) {
            throw ex;
        } finally {
            this.concurrentCounter.decrementAndGet();
        }
    }

    /**
     * 递增 MySQL 或者 Cassandra 计数器
     *
     * @param messages
     * @throws Exception
     */
    @KafkaListener(topics = com.future.demo.constant.Const.TopicIncreaseCount, concurrency = "8")
    public void receiveMessageIncreaseCount(List<String> messages) throws Exception {
        List<IncreaseCountDTO> dtoListMySQL = new ArrayList<>();
        /*List<IncreaseCountDTO> dtoListCassandra = new ArrayList<>();*/
        for (String JSON : messages) {
            IncreaseCountDTO increaseCountDTO = objectMapper.readValue(JSON, IncreaseCountDTO.class);
            /*IncreaseCountDTO.Type type = increaseCountDTO.getType();*/
            /*if (type == IncreaseCountDTO.Type.MySQL) {*/
            dtoListMySQL.add(increaseCountDTO);
            /*} else if (type == IncreaseCountDTO.Type.Cassandra) {
                dtoListCassandra.add(increaseCountDTO);
            }*/
        }

        // MySQL计数器
        // todo 在crond服务关闭重启后select count和计数器不一致
        /*Map<String, Integer> flagToCountMap = new HashMap<>();*/
        for (IncreaseCountDTO dto : dtoListMySQL) {
            String idempotentUuid = dto.getIdempotentUuid();
            String flag = dto.getFlag();
            int count = dto.getCount();
            /*if (!flagToCountMap.containsKey(flag)) {
                flagToCountMap.put(flag, 0);
            }

            if (Boolean.TRUE.equals(redisTemplate.opsForValue().setIfAbsent(idempotentUuid, StringUtils.EMPTY, 300, TimeUnit.SECONDS)))
                flagToCountMap.put(flag, flagToCountMap.get(flag) + count);
            else {
                if (log.isInfoEnabled())
                    log.info("意图重复消费消息 {}", dto);
            }*/

            this.commonService.updateIncreaseCount(idempotentUuid, flag, count);
        }

        /*for (String flag : flagToCountMap.keySet()) {
            int count = flagToCountMap.get(flag);
            if (count > 0)
                this.commonMapper.updateIncreaseCount(flag, count);
        }*/

//        // Cassandra计数器
//        Map<String, Integer> flagToCountMap = new HashMap<>();
//        for (IncreaseCountDTO dto : dtoListCassandra) {
//            String idempotentUuid = dto.getIdempotentUuid();
//            String flag = dto.getFlag();
//            int count = dto.getCount();
//            if (!flagToCountMap.containsKey(flag)) {
//                flagToCountMap.put(flag, 0);
//            }
//
//            if (Boolean.TRUE.equals(redisTemplate.opsForValue().setIfAbsent(idempotentUuid, StringUtils.EMPTY, 300, TimeUnit.SECONDS)))
//                flagToCountMap.put(flag, flagToCountMap.get(flag) + count);
//            else {
//                if (log.isInfoEnabled())
//                    log.info("意图重复消费消息 {}", dto);
//            }
//        }
//
//        for (String flag : flagToCountMap.keySet()) {
//            int count = flagToCountMap.get(flag);
//            if (count > 0)
//                this.cassandraMapper.updateIncreaseCount(flag, count);
//        }
    }

    /**
     * 创建订单 cassandra 索引
     *
     * @param messages
     * @throws Exception
     */
    @KafkaListener(topics = com.future.demo.constant.Const.TopicCreateOrderCassandraIndex, concurrency = "16")
    public void receiveMessageCreateOrderCassandraIndex(List<String> messages) throws Exception {
        try {
            List<OrderModel> modelList = new ArrayList<>();
            for (String message : messages) {
                OrderModel orderModel = this.objectMapper.readValue(message, OrderModel.class);
                modelList.add(orderModel);
            }

            // 普通下单不需要设置merchantId，只有秒杀才需要设置merchantId
            boolean needToSetMerchantId = false;
            for (OrderModel model : modelList) {
                if (model.getMerchantId() == null) {
                    needToSetMerchantId = true;
                    break;
                }
            }
            if (needToSetMerchantId)
                orderService.fillupOrderMerchantId(modelList);

            // 建立 listByUserId Cassandra 索引
            cassandraMapper.insertBatchOrderIndexListByUserId(modelList);
            // 建立 listByMerchantId Cassandra 索引
            cassandraMapper.insertBatchOrderIndexListByMerchantId(modelList);

            // 异步更新 t_count
            if (!modelList.isEmpty()) {
                for (OrderModel model : modelList) {
                    IncreaseCountDTO increaseCountDTO = new IncreaseCountDTO(String.valueOf(model.getId()), "orderListByUserId");
                    /*increaseCountDTO.setType(IncreaseCountDTO.Type.Cassandra);*/
                    increaseCountDTO.setCount(1);
                    String JSON = this.objectMapper.writeValueAsString(increaseCountDTO);
                    kafkaTemplate.send(com.future.demo.constant.Const.TopicIncreaseCount, JSON).get();

                    increaseCountDTO = new IncreaseCountDTO(String.valueOf(model.getId()), "orderListByMerchantId");
                    /*increaseCountDTO.setType(IncreaseCountDTO.Type.Cassandra);*/
                    increaseCountDTO.setCount(1);
                    JSON = this.objectMapper.writeValueAsString(increaseCountDTO);
                    kafkaTemplate.send(com.future.demo.constant.Const.TopicIncreaseCount, JSON).get();
                }
            }

            // Cassandra 成功建立后，从缓存中删除订单信息
            redisTemplate.executePipelined(new SessionCallback<String>() {
                @Override
                public <K, V> String execute(RedisOperations<K, V> operations) throws DataAccessException {
                    RedisOperations<String, String> redisOperations = (RedisOperations<String, String>) operations;
                    for (OrderModel orderModel : modelList) {
                        String userIdStr = String.valueOf(orderModel.getUserId());
                        String orderIdStr = String.valueOf(orderModel.getId());
                        String key = com.future.demo.constant.Const.CacheKeyPrefixOrderInCacheBeforeCassandraIndexCreate + userIdStr;
                        redisOperations.opsForHash().delete(key, orderIdStr);
                    }

                    return null;
                }
            });
        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
            throw ex;
        }
    }

    /**
     * 新增秒杀商品后，设置商品缓存主题
     *
     * @param messages
     * @throws Exception
     */
    @KafkaListener(topics = com.future.demo.constant.Const.TopicSetupProductFlashSaleCache, concurrency = "1")
    public void receiveMessageSetupProductFlashSaleCache(List<String> messages) {
        if (messages == null || messages.isEmpty()) {
            if (log.isWarnEnabled())
                log.warn("意料之外，为何 messages 为空的呢？");
            return;
        }

        // region 设置秒杀商品缓存

        List<FlashSaleProductCacheUpdateEventDTO> dtoList = messages.stream().map(o -> {
            FlashSaleProductCacheUpdateEventDTO dto;
            try {
                dto = objectMapper.readValue(o, FlashSaleProductCacheUpdateEventDTO.class);
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
            return dto;
        }).filter(o -> {
            ProductModel model = o.getProductModel();
            return model.isFlashSale();
        }).collect(Collectors.toList());
        if (log.isDebugEnabled())
            log.debug("messages {} 成功转换为 dtoList {}", messages, dtoList);

        // 设置秒杀商品的库存到缓存中
        Map<String, String> productIdCacheKeyToStockAmountMap = dtoList.stream().collect(
                Collectors.toMap(
                        o -> String.format(ProductService.KeyFlashSaleProductStockAmountWithHashTag, o.getProductModel().getId()),
                        o -> String.valueOf(o.getProductModel().getStock())));
        redisTemplate.opsForValue().multiSet(productIdCacheKeyToStockAmountMap);
        if (log.isDebugEnabled())
            log.debug("成功批量设置秒杀商品的库存到缓存中 {}", productIdCacheKeyToStockAmountMap);
        // 设置秒杀商品的开始时间到缓存中
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        Map<String, String> productIdCacheKeyToStartTimeMap = dtoList.stream().collect(
                Collectors.toMap(
                        o -> String.format(ProductService.KeyFlashSaleProductStartTime, o.getProductModel().getId()),
                        o -> dateTimeFormatter.format(o.getProductModel().getFlashSaleStartTime())));
        redisTemplate.opsForValue().multiSet(productIdCacheKeyToStartTimeMap);
        if (log.isDebugEnabled())
            log.debug("成功批量设置秒杀商品的开始时间到缓存中 {}", productIdCacheKeyToStartTimeMap);
        // 设置秒杀商品的结束时间到缓存中
        Map<String, String> productIdCacheKeyToEndTimeMap = dtoList.stream().collect(
                Collectors.toMap(
                        o -> String.format(ProductService.KeyFlashSaleProductEndTime, o.getProductModel().getId()),
                        o -> dateTimeFormatter.format(o.getProductModel().getFlashSaleEndTime())));
        redisTemplate.opsForValue().multiSet(productIdCacheKeyToEndTimeMap);
        if (log.isDebugEnabled())
            log.debug("成功批量设置秒杀商品的结束时间到缓存中 {}", productIdCacheKeyToEndTimeMap);

        // 设置秒杀商品过期时间缓存
        redisTemplate.executePipelined(new SessionCallback<String>() {
            @Override
            public <K, V> String execute(RedisOperations<K, V> operations) throws DataAccessException {
                RedisOperations<String, String> redisOperations = (RedisOperations<String, String>) operations;
                for (FlashSaleProductCacheUpdateEventDTO dto : dtoList) {
                    String productIdStr = String.valueOf(dto.getProductModel().getId());
                    LocalDateTime flashSaleEndTime = dto.getProductModel().getFlashSaleEndTime();
                    // 在秒杀结束1分钟后自动删除
                    int seconds = dto.getSecondAfterWhichExpiredFlashSaleProductForRemoving();
                    LocalDateTime expirationTime = flashSaleEndTime.plusSeconds(seconds);
                    long epochSecond = expirationTime.atZone(ZoneId.of("Asia/Shanghai")).toEpochSecond();
                    redisOperations.opsForZSet().add(ProductService.KeyFlashSaleProductExpirationCache, productIdStr, epochSecond);
                    if (log.isDebugEnabled())
                        log.debug("成功设置秒杀商品过期时间缓存 {}", dto);
                }
                return null;
            }
        });

        // endregion
    }

    /**
     * 商品创建后设置商品ID和库存到redis zset中，协助实现下单时随机抽取商品逻辑
     *
     * @param messages
     * @throws Exception
     */
    @KafkaListener(topics = com.future.demo.constant.Const.TopicAddProductIdAndStockAmountIntoRedisZSetAfterCreation, concurrency = "1")
    public void receiveMessageAddProductIdAndStockAmountIntoRedisZSetAfterCreation(List<String> messages) {
        if (messages == null || messages.isEmpty()) {
            if (log.isWarnEnabled())
                log.warn("意料之外，为何 messages 为空的呢？");
            return;
        }

        List<ProductModel> modelList = messages.stream().map(o -> {
            try {
                return objectMapper.readValue(o, ProductModel.class);
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        }).collect(Collectors.toList());

        redisTemplate.executePipelined(new SessionCallback<String>() {
            @Override
            public <K, V> String execute(RedisOperations<K, V> operations) throws DataAccessException {
                RedisOperations<String, String> redisOperations = (RedisOperations<String, String>) operations;
                for (ProductModel productModel : modelList) {
                    String productIdStr = String.valueOf(productModel.getId());
                    int stockAmount = productModel.getStock();
                    redisOperations.opsForZSet().add(com.future.demo.constant.Const.KeyProductIdAndStockAmountInRedisZSet, productIdStr, stockAmount);
                }
                return null;
            }
        });
        if (log.isDebugEnabled())
            log.debug("成功设置商品信息到 {} {}", com.future.demo.constant.Const.KeyProductIdAndStockAmountInRedisZSet, modelList);
    }
}
