package com.future.demo.tasks;

import com.future.demo.mapper.ProductMapper;
import com.future.demo.service.OrderService;
import com.future.demo.service.ProductService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Set;

/**
 * 定期从缓存中删除过期的秒杀商品
 */
@Component
@Slf4j
public class TaskRemoveExpiredFlashSaleProductFromCache {
    @Autowired
    StringRedisTemplate redisTemplate;
    @Resource
    ProductMapper productMapper;

    /**
     * todo 并发控制
     */
    @Scheduled(cron = "0/30 * * * * ?")
    public void execute() {
        // 查询过期秒杀商品列表
        LocalDateTime localDateTimeNow = LocalDateTime.now();
        long seconds = localDateTimeNow.toEpochSecond(ZoneOffset.ofHours(8));
        while (true) {
            Set<String> productIdStrSet =
                    redisTemplate.opsForZSet()
                            .reverseRangeByScore(ProductService.KeyFlashSaleProductExpirationCache, -1, seconds, 0, 1024);
            if (productIdStrSet == null || productIdStrSet.isEmpty()) {
                break;
            }

            // 处理过期秒杀商品列表
            if (log.isDebugEnabled())
                log.debug("处理过期秒杀商品列表 {}", productIdStrSet);

            for (String productIdStr : productIdStrSet) {
                // 同步缓存中的秒杀商品库存到数据库中
                String keyProductStockAmount = String.format(ProductService.KeyFlashSaleProductStockAmountWithHashTag, productIdStr);
                String stockAmountStr = redisTemplate.opsForValue().get(keyProductStockAmount);
                int stockAmount = StringUtils.isBlank(stockAmountStr) ? 0 : Integer.parseInt(stockAmountStr);
                long productId = Long.parseLong(productIdStr);
                productMapper.updateStock(productId, stockAmount);
                if (log.isDebugEnabled())
                    log.debug("成功同步缓存中秒杀商品库存到数据库中，商品ID {} 库存 {}", productIdStr, stockAmount);

                // 删除缓存中的秒杀商品信息
                redisTemplate.delete(keyProductStockAmount);
                if (log.isDebugEnabled())
                    log.debug("成功删除缓存中秒杀商品库存信息，商品ID {}", productIdStr);
                String key = String.format(ProductService.KeyFlashSaleProductStartTime, productIdStr);
                redisTemplate.delete(key);
                if (log.isDebugEnabled())
                    log.debug("成功删除缓存中秒杀商品开始时间信息，商品ID {}", productIdStr);
                key = String.format(ProductService.KeyFlashSaleProductEndTime, productIdStr);
                redisTemplate.delete(key);
                if (log.isDebugEnabled())
                    log.debug("成功删除缓存中秒杀商品结束时间信息，商品ID {}", productIdStr);

                // 删除用户重复秒杀标识
                key = String.format(OrderService.KeyProductPurchaseRecordWithHashTag, productIdStr);
                redisTemplate.delete(key);
                if (log.isDebugEnabled())
                    log.debug("成功删除用户重复秒杀标识，商品ID {}", productIdStr);

                // 删除秒杀商品的过期时间缓存
                redisTemplate.opsForZSet().remove(ProductService.KeyFlashSaleProductExpirationCache, productIdStr);
                if (log.isDebugEnabled())
                    log.debug("成功删除秒杀商品的过期时间缓存，商品ID {}", productIdStr);
            }
        }
    }
}
