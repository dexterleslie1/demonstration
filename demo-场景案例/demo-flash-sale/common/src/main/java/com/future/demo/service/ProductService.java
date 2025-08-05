package com.future.demo.service;

import cn.hutool.core.util.RandomUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.future.count.IncreaseCountDTO;
import com.future.demo.config.PrometheusCustomMonitor;
import com.future.demo.constant.Const;
import com.future.demo.dto.FlashSaleProductCacheUpdateEventDTO;
import com.future.demo.dto.ProductDTO;
import com.future.demo.dto.UpdateProductStockAmountReq;
import com.future.demo.entity.ProductModel;
import com.future.demo.mapper.ProductMapper;
import com.tencent.devops.leaf.service.SnowflakeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.util.concurrent.ListenableFuture;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ProductService {

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

    @Autowired
    StringRedisTemplate redisTemplate;
    @Resource
    ObjectMapper objectMapper;
    @Resource
    ProductMapper productMapper;
    @Resource
    KafkaTemplate<String, String> kafkaTemplate;
    @Resource
    PrometheusCustomMonitor prometheusCustomMonitor;
    @Resource
    SnowflakeService snowflakeService;

    /**
     * 根据商品id列表查询商品列表信息
     *
     * @param idList
     * @return
     */
    public List<ProductDTO> listByIds(List<Long> idList) {
        if (idList == null || idList.isEmpty())
            return null;

        List<ProductModel> modelList = productMapper.list(idList);
        if (modelList == null || modelList.isEmpty())
            return new ArrayList<>();

        return modelList.stream().map(ProductModel::toDTO).collect(Collectors.toList());
    }

    /**
     * 根据商品id查询商品信息
     *
     * @param id
     * @return
     */
    public ProductDTO getById(Long id) {
        Assert.isTrue(id != null && id > 0, "请指定商品ID");

        ProductModel model = this.productMapper.getById(id);
        if (model == null)
            return null;

        return model.toDTO();
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
    /*@Transactional(rollbackFor = Exception.class)*/
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

        Long id = snowflakeService.getId("product").getId();
        model.setId(id);

        model.setName(name);
        model.setMerchantId(merchantId);
        model.setStock(stockAmount);
        model.setFlashSale(flashSale);
        model.setFlashSaleStartTime(flashSaleStartTime);
        model.setFlashSaleEndTime(flashSaleEndTime);
        model.setCreateTime(LocalDateTime.now());
        this.productMapper.insert(model);
        if (log.isDebugEnabled())
            log.debug("成功插入商品数据到数据库 {}", model);

        List<ListenableFuture<SendResult<String, String>>> futureList = new ArrayList<>();
        // 秒杀商品需要初始化 redis 缓存
        if (flashSale) {
            int second = getSecondAfterWhichExpiredFlashSaleProductForRemoving();
            FlashSaleProductCacheUpdateEventDTO eventDTO = new FlashSaleProductCacheUpdateEventDTO();
            eventDTO.setProductModel(model);
            eventDTO.setSecondAfterWhichExpiredFlashSaleProductForRemoving(second);
            String JSON = this.objectMapper.writeValueAsString(eventDTO);
            futureList.add(this.kafkaTemplate.send(Const.TopicSetupProductFlashSaleCache, JSON));
            if (log.isDebugEnabled())
                log.debug("秒杀商品成功发送设置商品缓存消息 {}", JSON);
        }

        // 向缓存中添加商品用于下单时随机抽取商品
        String JSON = this.objectMapper.writeValueAsString(model);
        futureList.add(kafkaTemplate.send(Const.TopicAddProductToCacheForPickupRandomlyWhenPurchasing, JSON));
        if (log.isDebugEnabled())
            log.debug("成功发送消息“向缓存中添加商品用于下单时随机抽取商品” {}", JSON);

        // 异步更新 t_count
        IncreaseCountDTO increaseCountDTO = new IncreaseCountDTO(model.getId(), "product");
        JSON = this.objectMapper.writeValueAsString(increaseCountDTO);
        futureList.add(kafkaTemplate.send(Const.TopicIncreaseCountSlow, JSON));

        if (!futureList.isEmpty()) {
            int index = -1;
            try {
                for (int i = 0; i < futureList.size(); i++) {
                    index = i;
                    futureList.get(i).get();
                }
            } catch (Exception ex) {
                log.error("发送Kafka消息失败，原因：{}，出错的 futureList 索引为 {}", ex.getMessage(), index, ex);
                throw ex;
            }
        }

        if (flashSale)
            prometheusCustomMonitor.getCounterProductMetricsCreateFlashSaleSuccessfully().increment();
        else
            prometheusCustomMonitor.getCounterProductMetricsCreateOrdinarySuccessfully().increment();

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
     * 在秒杀结束多少秒后自动删除缓存中的秒杀商品，默认30秒后
     *
     * @return
     */
    public int getSecondAfterWhichExpiredFlashSaleProductForRemoving() {
        return 30;
    }

    /**
     * 批量修改商品库存
     *
     * @param reqList
     */
    @Transactional(rollbackFor = Exception.class)
    public void updateStockAmount(List<UpdateProductStockAmountReq> reqList) {
        if (reqList == null || reqList.isEmpty()) {
            return;
        }

        for (UpdateProductStockAmountReq req : reqList) {
            Long productId = req.getProductId();
            Integer stockAmount = req.getStockAmount();
            productMapper.updateStock(productId, stockAmount);
        }
    }
}
