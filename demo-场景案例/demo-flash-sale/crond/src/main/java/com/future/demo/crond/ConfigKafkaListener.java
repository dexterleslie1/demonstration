package com.future.demo.crond;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.future.count.CountService;
import com.future.count.IncreaseCountDTO;
import com.future.demo.config.PrometheusCustomMonitor;
import com.future.demo.constant.Const;
import com.future.demo.dto.FlashSaleProductCacheUpdateEventDTO;
import com.future.demo.dto.RandomIdPickerAddIdEventDTO;
import com.future.demo.entity.OrderModel;
import com.future.demo.entity.ProductModel;
import com.future.demo.mapper.CassandraMapper;
import com.future.demo.mapper.CommonMapper;
import com.future.demo.mapper.ProductMapper;
import com.future.demo.service.CommonService;
import com.future.demo.service.OrderService;
import com.future.demo.service.PickupProductRandomlyWhenPurchasingService;
import com.future.demo.service.ProductService;
import com.future.random.id.picker.RandomIdPickerService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.util.concurrent.ListenableFuture;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
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
    KafkaTemplate<String, String> kafkaTemplate;
    @Resource
    ProductService productService;
    @Resource
    RandomIdPickerService randomIdPickerService;
    @Resource
    PrometheusCustomMonitor prometheusCustomMonitor;
    @Resource
    PickupProductRandomlyWhenPurchasingService pickupProductRandomlyWhenPurchasingService;
    @Resource
    CountService countService;

//    private AtomicInteger concurrentCounter = new AtomicInteger();
//    private AtomicLong counter = new AtomicLong();

    /**
     * @param messages
     * @throws Exception
     */
    @KafkaListener(topics = Const.TopicOrderInCacheSyncToDb, concurrency = "2", containerFactory = "defaultKafkaListenerContainerFactory")
    public void receiveMessageOrderInCacheSyncToDB(List<String> messages) throws Exception {
        try {
            /*log.info("concurrent=" + this.concurrentCounter.incrementAndGet() + ",size=" + messages.size() + ",total=" + counter.addAndGet(messages.size()));*/

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
            /*this.concurrentCounter.decrementAndGet();*/
        }
    }

    /**
     * 递增 MySQL 或者 Cassandra 计数器
     *
     * @param messages
     * @throws Exception
     */
    @KafkaListener(topics = Const.TopicIncreaseCount, concurrency = "4", containerFactory = "defaultKafkaListenerContainerFactory")
    public void receiveMessageIncreaseCount(List<String> messages) throws Exception {
        try {
            List<IncreaseCountDTO> increaseCountDTOList = new ArrayList<>();
            for (String JSON : messages) {
                IncreaseCountDTO increaseCountDTO = objectMapper.readValue(JSON, IncreaseCountDTO.class);
                increaseCountDTOList.add(increaseCountDTO);
            }

            // todo 在crond服务关闭重启后select count和计数器不一致
            countService.updateIncreaseCount(increaseCountDTOList);
            prometheusCustomMonitor.getCounterIncreaseCountStatsSuccessfully().increment(increaseCountDTOList.size());
        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
            throw ex;
        }
    }

    /**
     * 创建订单 cassandra 索引 listByUserId
     *
     * @param messages
     * @throws Exception
     */
    @KafkaListener(topics = Const.TopicCreateOrderCassandraIndexListByUserId, containerFactory = "topicCreateOrderCassandraIndexListByUserIdKafkaListenerContainerFactory")
    public void receiveMessageCreateOrderCassandraIndexListByUserId(List<String> messages) throws Exception {
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

            // 异步更新 t_count
            if (!modelList.isEmpty()) {
                List<ListenableFuture<SendResult<String, String>>> futureList = new ArrayList<>();
                for (OrderModel model : modelList) {
                    IncreaseCountDTO increaseCountDTO = new IncreaseCountDTO(String.valueOf(model.getId()), "orderListByUserId");
                    String JSON = this.objectMapper.writeValueAsString(increaseCountDTO);
                    ListenableFuture<SendResult<String, String>> future = kafkaTemplate.send(Const.TopicIncreaseCount, JSON);
                    futureList.add(future);
                }

                for (ListenableFuture<SendResult<String, String>> future : futureList)
                    future.get();
            }

            // Cassandra 成功建立后，从缓存中删除订单信息
            redisTemplate.executePipelined(new SessionCallback<String>() {
                @Override
                public <K, V> String execute(RedisOperations<K, V> operations) throws DataAccessException {
                    RedisOperations<String, String> redisOperations = (RedisOperations<String, String>) operations;
                    for (OrderModel orderModel : modelList) {
                        String userIdStr = String.valueOf(orderModel.getUserId());
                        String orderIdStr = String.valueOf(orderModel.getId());
                        String key = Const.CacheKeyPrefixOrderInCacheBeforeCassandraIndexCreate + userIdStr;
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
     * 创建订单 cassandra 索引 listByMerchantId
     *
     * @param messages
     * @throws Exception
     */
    @KafkaListener(topics = Const.TopicCreateOrderCassandraIndexListByMerchantId, containerFactory = "topicCreateOrderCassandraIndexListByMerchantIdKafkaListenerContainerFactory")
    public void receiveMessageCreateOrderCassandraIndexListByMerchantId(List<String> messages) throws Exception {
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

            // 建立 listByMerchantId Cassandra 索引
            cassandraMapper.insertBatchOrderIndexListByMerchantId(modelList);

            // 异步更新 t_count
            if (!modelList.isEmpty()) {
                List<ListenableFuture<SendResult<String, String>>> futureList = new ArrayList<>();
                for (OrderModel model : modelList) {
                    IncreaseCountDTO increaseCountDTO = new IncreaseCountDTO(String.valueOf(model.getId()), "orderListByMerchantId");
                    String JSON = this.objectMapper.writeValueAsString(increaseCountDTO);
                    ListenableFuture<SendResult<String, String>> future = kafkaTemplate.send(Const.TopicIncreaseCount, JSON);
                    futureList.add(future);
                }

                for (ListenableFuture<SendResult<String, String>> future : futureList)
                    future.get();
            }
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
    @KafkaListener(topics = Const.TopicSetupProductFlashSaleCache, concurrency = "1", containerFactory = "defaultKafkaListenerContainerFactory")
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
     * 向缓存中添加商品用于下单时随机抽取商品
     *
     * @param messages
     * @throws Exception
     */
    @KafkaListener(topics = Const.TopicAddProductToCacheForPickupRandomlyWhenPurchasing, concurrency = "1", containerFactory = "defaultKafkaListenerContainerFactory")
    public void receiveMessageAddProductToCacheForPickupRandomlyWhenPurchasing(List<String> messages) {
        try {
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

            pickupProductRandomlyWhenPurchasingService.setProductList(modelList);
        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
            throw ex;
        }
    }

    /**
     * 向随机 id 选择器添加 id 列表
     *
     * @param messages
     * @throws Exception
     */
    @KafkaListener(topics = Const.TopicRandomIdPickerAddIdList, concurrency = "1", containerFactory = "defaultKafkaListenerContainerFactory")
    public void receiveMessageRandomIdPickerAddIdList(List<String> messages) throws Exception {
        try {
            List<RandomIdPickerAddIdEventDTO> dtoList = new ArrayList<>();
            for (String JSON : messages) {
                RandomIdPickerAddIdEventDTO dto = objectMapper.readValue(JSON, RandomIdPickerAddIdEventDTO.class);
                dtoList.add(dto);
            }
            if (!dtoList.isEmpty()) {
                Map<String, List<Long>> flagToIdListMap = dtoList.stream().collect(
                        Collectors.groupingBy(RandomIdPickerAddIdEventDTO::getFlag,
                                Collectors.mapping(RandomIdPickerAddIdEventDTO::getId, Collectors.toList())));
                for (String flag : flagToIdListMap.keySet())
                    randomIdPickerService.addIdList(flag, flagToIdListMap.get(flag));
            }
        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
            throw ex;
        }
    }
}
