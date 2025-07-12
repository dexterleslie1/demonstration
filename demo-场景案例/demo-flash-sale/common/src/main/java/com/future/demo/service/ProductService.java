package com.future.demo.service;

import cn.hutool.core.util.RandomUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.future.demo.dto.FlashSaleProductCacheUpdateEventDTO;
import com.future.demo.entity.ProductModel;
import com.future.demo.mapper.ProductMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;

import static com.future.demo.constant.Const.TopicAddProductIdAndStockAmountIntoRedisZSetAfterCreation;
import static com.future.demo.constant.Const.TopicSetupProductFlashSaleCache;

@Service
@Slf4j
public class ProductService {

    public final static String KeyProduct = "product:";
    /**
     * 秒杀商品库存的键
     */
    public final static String KeyFlashSaleProductStockAmountWithHashTag = "flash-sale-product:stockAmount:{%s}";
    /**
     * 秒杀商品秒杀开始时间的键
     */
    public final static String KeyFlashSaleProductStartTime = "flash-sale-product:startTime:%s";
    /**
     * 秒杀商品秒杀结束时间的键
     */
    public final static String KeyFlashSaleProductEndTime = "flash-sale-product:endTime:%s";
    /**
     * 秒杀商品过期时间缓存，用于协助实现秒杀商品过期自动从缓存中删除逻辑
     */
    public final static String KeyFlashSaleProductExpirationCache = "flash-sale-product:expiration";

    public final static int ProductTotalCount = 10000000;

    @Autowired
    StringRedisTemplate redisTemplate;
    @Resource
    ObjectMapper objectMapper;
    @Resource
    ProductMapper productMapper;
    @Resource
    KafkaTemplate<String, String> kafkaTemplate;

    /**
     * 根据商品id列表查询商品列表信息
     *
     * @param idList
     * @return
     */
    public List<ProductModel> listByIds(List<Long> idList) {
        if (idList == null || idList.isEmpty())
            return null;

        return productMapper.list(idList);
    }

    /**
     * 添加商品
     *
     * @param name
     * @param merchantId
     * @param stockAmount
     * @param flashSale
     * @param flashSaleStartTime
     * @param flashSaleEndTime
     * @return 商品ID
     */
    @Transactional(rollbackFor = Exception.class)
    public Long add(String name,
                    Long merchantId,
                    int stockAmount,
                    boolean flashSale,
                    LocalDateTime flashSaleStartTime,
                    LocalDateTime flashSaleEndTime) throws Exception {

        // 不是秒杀商品不需要设置秒杀开始时间和秒杀结束时间
        if (!flashSale) {
            flashSaleStartTime = null;
            flashSaleEndTime = null;
        }

        if (flashSale) {
            // 秒杀商品需要指定秒杀开始和结束时间
            Assert.notNull(flashSaleStartTime, "请指定秒杀开始时间参数flashSaleStartTime");
            Assert.notNull(flashSaleEndTime, "请指定秒杀开始时间参数flashSaleEndTime");

            // 秒杀开始时间需要早于秒杀结束时间
            Assert.isTrue(flashSaleStartTime.isBefore(flashSaleEndTime), "秒杀开始时间 " + flashSaleStartTime + " 必须要早于秒杀结束时间 " + flashSaleEndTime);
        }

        ProductModel model = new ProductModel();
        model.setName(name);
        model.setMerchantId(merchantId);
        model.setStock(stockAmount);
        model.setFlashSale(flashSale);
        model.setFlashSaleStartTime(flashSaleStartTime);
        model.setFlashSaleEndTime(flashSaleEndTime);
        this.productMapper.insert(model);
        if (log.isDebugEnabled())
            log.debug("成功插入商品数据到数据库 {}", model);

        // 秒杀商品需要初始化 redis 缓存
        if (flashSale) {
            int second = getSecondAfterWhichExpiredFlashSaleProductForRemoving();
            FlashSaleProductCacheUpdateEventDTO eventDTO = new FlashSaleProductCacheUpdateEventDTO();
            eventDTO.setProductModel(model);
            eventDTO.setSecondAfterWhichExpiredFlashSaleProductForRemoving(second);
            String JSON = this.objectMapper.writeValueAsString(eventDTO);
            this.kafkaTemplate.send(TopicSetupProductFlashSaleCache, JSON).get();
            if (log.isDebugEnabled())
                log.debug("秒杀商品成功发送设置商品缓存消息 {}", JSON);
        }

        // 商品创建后设置商品ID和库存到redis zset中，协助实现下单时随机抽取商品逻辑
        String JSON = this.objectMapper.writeValueAsString(model);
        kafkaTemplate.send(TopicAddProductIdAndStockAmountIntoRedisZSetAfterCreation, JSON).get();
        if (log.isDebugEnabled())
            log.debug("成功发送商品创建后设置商品ID和库存到redis zset中消息 {}", JSON);

        // 异步更新 t_count
        /*IncreaseCountDTO increaseCountDTO = new IncreaseCountDTO(String.valueOf(model.getId()), "product");
        increaseCountDTO.setType(IncreaseCountDTO.Type.MySQL);
        increaseCountDTO.setCount(1);
        JSON = this.objectMapper.writeValueAsString(increaseCountDTO);
        kafkaTemplate.send(TopicIncreaseCount, JSON).get();*/

        return model.getId();
    }

    /**
     * 随机获取秒杀商品的秒杀开始时间（一个 1-10 分钟内的随机时间）
     *
     * @return
     */
    public LocalDateTime getFlashSaleStartTimeRandomly() {
        LocalDateTime localDateTimeNow = LocalDateTime.now();
        int randomMinute = RandomUtil.randomInt(1, 11);
        return localDateTimeNow.plusMinutes(randomMinute);
    }

    public LocalDateTime getFlashSaleStartTimeRandomly(long futureOffsetSeconds) {
        if (futureOffsetSeconds < 5) {
            futureOffsetSeconds = 5;
        }
        LocalDateTime localDateTimeNow = LocalDateTime.now();
        return localDateTimeNow.plusSeconds(futureOffsetSeconds);
    }

    /**
     * 随机获取秒杀商品的秒杀结束时间（一个 flashSaleStartTime 10-15 分钟内的随机时间）
     *
     * @param flashSaleStartTime
     * @return
     */
    public LocalDateTime getFlashSaleEndTimeRandomly(LocalDateTime flashSaleStartTime) {
        int randomMinute = RandomUtil.randomInt(10, 16);
        return flashSaleStartTime.plusMinutes(randomMinute);
    }

    /**
     * 在秒杀结束多少秒后自动删除缓存中的秒杀商品，默认一分钟后
     *
     * @return
     */
    public int getSecondAfterWhichExpiredFlashSaleProductForRemoving() {
        return 60;
    }
}
