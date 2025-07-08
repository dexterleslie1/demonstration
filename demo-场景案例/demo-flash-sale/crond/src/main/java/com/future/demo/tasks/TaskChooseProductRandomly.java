package com.future.demo.tasks;

import cn.hutool.core.util.RandomUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.future.demo.entity.ProductModel;
import com.future.demo.mapper.ProductMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

import static com.future.demo.constant.Const.KeyProductIdAndStockAmountInRedisZSet;
import static com.future.demo.constant.Const.TopicPublishChooseProductRandomlyForOrdering;

/**
 * 随机抽取下单商品并通过 kafka 发布给 api 服务。
 */
@Component
@Slf4j
public class TaskChooseProductRandomly {
    @Autowired
    StringRedisTemplate redisTemplate;
    @Resource
    ProductMapper productMapper;
    @Resource
    ObjectMapper objectMapper;
    @Resource
    KafkaTemplate kafkaTemplate;

    @Scheduled(cron = "0/30 * * * * ?")
    public void execute() throws JsonProcessingException, ExecutionException, InterruptedException {
        // 获取 zset 元素总数
        Long totalCount = redisTemplate.opsForZSet().zCard(KeyProductIdAndStockAmountInRedisZSet);
        if (totalCount == null || totalCount <= 0) {
            if (log.isWarnEnabled())
                log.warn("redis zset {} 中没有商品信息", KeyProductIdAndStockAmountInRedisZSet);
            return;
        }
        if (log.isDebugEnabled())
            log.debug("redis zset {} 中共有 {} 个商品", KeyProductIdAndStockAmountInRedisZSet, totalCount);

        // 随机抽取开始位置
        long start = RandomUtil.randomLong(0, totalCount);
        // 一次最多抽取1024个商品
        long end = start + 1024;
        Set<String> productIdSet = redisTemplate.opsForZSet().range(KeyProductIdAndStockAmountInRedisZSet, start, end);
        if (productIdSet == null || productIdSet.size() <= 0) {
            if (log.isDebugEnabled())
                log.debug("随机抽取到 {} 个商品 {}", productIdSet == null ? 0 : productIdSet.size(), productIdSet);
            return;
        }

        List<Long> productIdList = productIdSet.stream().map(Long::parseLong).collect(Collectors.toList());
        List<ProductModel> modelList = productMapper.list(productIdList);

        // 过滤去除库存为0的普通商品
        List<ProductModel> ordinaryModelList = modelList.stream().filter(o -> !o.isFlashSale() && o.getStock() > 0).collect(Collectors.toList());
        if (log.isDebugEnabled())
            log.debug("过滤去除库存为0的普通商品总数为{} {}", ordinaryModelList.size(), ordinaryModelList);

        // 过滤去除秒杀结束或者秒杀未开始的秒杀商品
        List<ProductModel> flashSaleModelList = modelList.stream().filter(o -> {
            if (!o.isFlashSale()) {
                return true;
            }

            LocalDateTime localDateTimeNow = LocalDateTime.now();
            LocalDateTime flashSaleStartTime = o.getFlashSaleStartTime();
            LocalDateTime flashSaleEndTime = o.getFlashSaleEndTime();
            if (localDateTimeNow.isAfter(flashSaleStartTime) && localDateTimeNow.isBefore(flashSaleEndTime)) {
                return true;
            }
            return false;
        }).collect(Collectors.toList());
        if (log.isDebugEnabled())
            log.debug("过滤去除秒杀结束或者秒杀未开始的秒杀商品总数为{} {}", flashSaleModelList.size(), flashSaleModelList);

        String JSON = objectMapper.writeValueAsString(ordinaryModelList);
        kafkaTemplate.send(TopicPublishChooseProductRandomlyForOrdering, JSON).get();
        if (log.isDebugEnabled())
            log.debug("成功发布消息 - 随机抽取的普通商品到 api 服务以便下单时随机抽取普通商品 {}", JSON);
        JSON = objectMapper.writeValueAsString(flashSaleModelList);
        kafkaTemplate.send(TopicPublishChooseProductRandomlyForOrdering, JSON).get();
        if (log.isDebugEnabled())
            log.debug("成功发布消息 - 随机抽取的秒杀商品到 api 服务以便下单时随机抽取秒杀商品 {}", JSON);
    }
}
