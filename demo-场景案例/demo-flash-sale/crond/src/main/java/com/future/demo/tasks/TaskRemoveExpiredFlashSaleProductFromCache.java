package com.future.demo.tasks;

import com.future.demo.dto.UpdateProductStockAmountReq;
import com.future.demo.service.OrderService;
import com.future.demo.service.ProductService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
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
    ProductService productService;

    /**
     * todo 并发控制
     */
    @Scheduled(cron = "0/30 * * * * ?")
    public void execute() {
        StopWatch stopWatch = null;
        try {
            // 查询过期秒杀商品列表
            LocalDateTime localDateTimeNow = LocalDateTime.now();
            long seconds = localDateTimeNow.toEpochSecond(ZoneOffset.ofHours(8));
            while (true) {
                stopWatch = new StopWatch();
                stopWatch.start();

                Set<String> productIdStrSet =
                        redisTemplate.opsForZSet()
                                .reverseRangeByScore(ProductService.KeyFlashSaleProductExpirationCache, -1, seconds, 0, 1024);
                if (productIdStrSet == null || productIdStrSet.isEmpty()) {
                    if (log.isInfoEnabled()) {
                        log.info("没有需要从缓存中删除的秒杀商品，当前 epoch second {}", seconds);
                    }
                    break;
                }

                // 处理过期秒杀商品列表
                if (log.isDebugEnabled())
                    log.debug("处理过期秒杀商品列表 {}", productIdStrSet);

                // 同步缓存中的秒杀商品库存到数据库中
                List<UpdateProductStockAmountReq> reqList = new ArrayList<>();
                List<String> productStockAmountKeyList = new ArrayList<>();
                for (String productIdStr : productIdStrSet) {
                    String keyProductStockAmount = String.format(ProductService.KeyFlashSaleProductStockAmountWithHashTag, productIdStr);
                    productStockAmountKeyList.add(keyProductStockAmount);
                    UpdateProductStockAmountReq req = new UpdateProductStockAmountReq();
                    long productId = Long.parseLong(productIdStr);
                    req.setProductId(productId);
                    reqList.add(req);
                }
                List<String> stockAmountStrList = redisTemplate.opsForValue().multiGet(productStockAmountKeyList);
                for (int i = 0; i < reqList.size(); i++) {
                    String stockAmountStr = stockAmountStrList == null || stockAmountStrList.size() - 1 < i ? null : stockAmountStrList.get(i);
                    int stockAmount = StringUtils.isBlank(stockAmountStr) ? 0 : Integer.parseInt(stockAmountStr);
                    reqList.get(i).setStockAmount(stockAmount);
                }
                productService.updateStockAmount(reqList);

                // 删除 redis 缓存中的秒杀商品数据
                redisTemplate.executePipelined(new SessionCallback<String>() {
                    @Override
                    public <K, V> String execute(RedisOperations<K, V> operations) throws DataAccessException {
                        RedisOperations<String, String> redisOperations = (RedisOperations<String, String>) operations;
                        for (String productIdStr : productIdStrSet) {
                            // 删除缓存中的秒杀商品信息
                            String keyProductStockAmount = String.format(ProductService.KeyFlashSaleProductStockAmountWithHashTag, productIdStr);
                            redisOperations.delete(keyProductStockAmount);
                            /*if (log.isDebugEnabled())
                                log.debug("成功删除缓存中秒杀商品库存信息，商品ID {}", productIdStr);*/
                            String key = String.format(ProductService.KeyFlashSaleProductStartTime, productIdStr);
                            redisOperations.delete(key);
                            /*if (log.isDebugEnabled())
                                log.debug("成功删除缓存中秒杀商品开始时间信息，商品ID {}", productIdStr);*/
                            key = String.format(ProductService.KeyFlashSaleProductEndTime, productIdStr);
                            redisOperations.delete(key);
                            /*if (log.isDebugEnabled())
                                log.debug("成功删除缓存中秒杀商品结束时间信息，商品ID {}", productIdStr);*/

                            // 删除用户重复秒杀标识
                            key = String.format(OrderService.KeyProductPurchaseRecordWithHashTag, productIdStr);
                            redisOperations.delete(key);
                            /*if (log.isDebugEnabled())
                                log.debug("成功删除用户重复秒杀标识，商品ID {}", productIdStr);*/

                            // 删除秒杀商品的过期时间缓存
                            redisOperations.opsForZSet().remove(ProductService.KeyFlashSaleProductExpirationCache, productIdStr);
                            /*if (log.isDebugEnabled())
                                log.debug("成功删除秒杀商品的过期时间缓存，商品ID {}", productIdStr);*/
                        }

                        return null;
                    }
                });

                stopWatch.stop();
                if (log.isInfoEnabled()) {
                    log.info("从缓存中删除 {} 个秒杀商品耗时 {} 毫秒", productIdStrSet.size(), stopWatch.getTotalTimeMillis());
                }
            }
        } finally {
            if (stopWatch != null) {
                stopWatch.stop();
                stopWatch = null;
            }
        }
    }
}
