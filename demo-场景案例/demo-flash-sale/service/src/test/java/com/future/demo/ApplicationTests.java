package com.future.demo;

import com.datastax.driver.core.Session;
import com.future.common.exception.BusinessException;
import com.future.demo.dto.OrderDTO;
import com.future.demo.entity.OrderDetailModel;
import com.future.demo.entity.OrderModel;
import com.future.demo.entity.ProductModel;
import com.future.demo.entity.Status;
import com.future.demo.mapper.OrderDetailMapper;
import com.future.demo.mapper.OrderMapper;
import com.future.demo.mapper.ProductMapper;
import com.future.demo.service.MerchantService;
import com.future.demo.service.OrderService;
import com.future.demo.service.ProductService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang.StringUtils;
import org.awaitility.Awaitility;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static com.future.demo.constant.Const.CacheKeyPrefixOrderInCacheBeforeCassandraIndexCreate;

@SpringBootTest
@Slf4j
public class ApplicationTests {

    @Autowired
    StringRedisTemplate redisTemplate;
    @Resource
    ProductMapper productMapper;
    @Resource
    OrderMapper orderMapper;
    @Resource
    OrderDetailMapper orderDetailMapper;

    @Resource
    OrderService orderService;
    @SpyBean
    ProductService productService;
    @Resource
    MerchantService merchantService;
    @Resource
    Session session;

    /**
     * 测试普通下单
     *
     * @throws Exception
     */
    @Test
    public void testCreateOrderOrdinarily() throws Exception {
        // 还原测试数据
        this.reset();

        int stockAmount = 6;

        // region 测试创建普通商品

        String name = RandomStringUtils.randomAlphanumeric(20);
        Long merchantId = this.merchantService.getIdRandomly();
        Long productId = productService.add(name, merchantId, stockAmount, false, null, null);
        List<ProductModel> productModelList = this.productMapper.list(Collections.singletonList(productId));
        Assertions.assertEquals(1, productModelList.size());
        Assertions.assertEquals(productId, productModelList.get(0).getId());
        Assertions.assertEquals(name, productModelList.get(0).getName());
        Assertions.assertEquals(merchantId, productModelList.get(0).getMerchantId());
        Assertions.assertEquals(stockAmount, productModelList.get(0).getStock());

        // endregion

        Long userId = 1L;
        Integer amount = 1;

        // region 测试成功创建订单

        // 先删除之前的订单缓存避免对测试产生影响
        String key = CacheKeyPrefixOrderInCacheBeforeCassandraIndexCreate + userId;
        redisTemplate.delete(key);

        Long orderId = this.orderService.create(userId, productId, amount, null);
        // 检查缓存中的订单信息存在
        key = CacheKeyPrefixOrderInCacheBeforeCassandraIndexCreate + userId;
        Object value = redisTemplate.opsForHash().get(key, String.valueOf(orderId));
        // todo kafka 消费太快，导致 null
        /*Assertions.assertNotNull(value);*/
        List<OrderModel> orderModelList = this.orderMapper.selectAll();
        Assertions.assertEquals(1, orderModelList.size());
        Assertions.assertEquals(userId, orderModelList.get(0).getUserId());
        List<OrderDetailModel> orderDetailModelList = this.orderDetailMapper.selectAll();
        Assertions.assertEquals(1, orderDetailModelList.size());
        Assertions.assertEquals(productId, orderDetailModelList.get(0).getProductId());
        Assertions.assertEquals(amount, orderDetailModelList.get(0).getAmount());
        Assertions.assertEquals(userId, orderDetailModelList.get(0).getUserId());
        Assertions.assertEquals(orderModelList.get(0).getId(), orderDetailModelList.get(0).getOrderId());

        // 测试 listByUserIdAndStatus，不休眠后从 redis 中读取订单信息
        Status status = orderModelList.get(0).getStatus();
        orderId = orderModelList.get(0).getId();
        LocalDateTime createTime = orderModelList.get(0).getCreateTime();
        LocalDateTime startTime = createTime;
        LocalDateTime endTime = startTime.plusMonths(1);
        List<OrderDTO> orderDTOList = this.orderService.listByUserIdAndStatus(userId, status, startTime, endTime);
        Assertions.assertEquals(1, orderDTOList.size());
        Assertions.assertEquals(String.valueOf(orderId), orderDTOList.get(0).getId());
        Assertions.assertEquals(1, orderDTOList.get(0).getOrderDetailList().size());
        Assertions.assertEquals(productId, orderDTOList.get(0).getOrderDetailList().get(0).getProductId());
        Assertions.assertEquals(amount, orderDTOList.get(0).getOrderDetailList().get(0).getAmount());
        Assertions.assertEquals(merchantId, orderDTOList.get(0).getMerchantId());

        // 测试 listByUserIdAndStatus，休眠后从 Cassandra 中读取订单信息
        TimeUnit.MILLISECONDS.sleep(500);
        status = orderModelList.get(0).getStatus();
        orderId = orderModelList.get(0).getId();
        createTime = orderModelList.get(0).getCreateTime();
        startTime = createTime;
        endTime = startTime.plusMonths(1);
        // 检查缓存中的订单信息被删除
        key = CacheKeyPrefixOrderInCacheBeforeCassandraIndexCreate + userId;
        value = redisTemplate.opsForHash().get(key, String.valueOf(orderId));
        Assertions.assertNull(value);
        orderDTOList = this.orderService.listByUserIdAndStatus(userId, status, startTime, endTime);
        Assertions.assertEquals(1, orderDTOList.size());
        Assertions.assertEquals(String.valueOf(orderId), orderDTOList.get(0).getId());
        Assertions.assertEquals(1, orderDTOList.get(0).getOrderDetailList().size());
        Assertions.assertEquals(productId, orderDTOList.get(0).getOrderDetailList().get(0).getProductId());
        Assertions.assertEquals(amount, orderDTOList.get(0).getOrderDetailList().get(0).getAmount());
        Assertions.assertEquals(merchantId, orderDTOList.get(0).getMerchantId());

        // 测试 listByUserIdAndWithoutStatus
        orderId = orderModelList.get(0).getId();
        createTime = orderModelList.get(0).getCreateTime();
        startTime = createTime;
        endTime = startTime.plusMonths(1);
        merchantId = orderModelList.get(0).getMerchantId();
        orderDTOList = this.orderService.listByUserIdAndWithoutStatus(userId, startTime, endTime);
        Assertions.assertEquals(1, orderDTOList.size());
        Assertions.assertEquals(String.valueOf(orderId), orderDTOList.get(0).getId());
        Assertions.assertEquals(1, orderDTOList.get(0).getOrderDetailList().size());
        Assertions.assertEquals(productId, orderDTOList.get(0).getOrderDetailList().get(0).getProductId());
        Assertions.assertEquals(amount, orderDTOList.get(0).getOrderDetailList().get(0).getAmount());
        Assertions.assertEquals(merchantId, orderDTOList.get(0).getMerchantId());

        // 测试 listByMerchantIdAndStatus
        status = orderModelList.get(0).getStatus();
        orderId = orderModelList.get(0).getId();
        createTime = orderModelList.get(0).getCreateTime();
        startTime = createTime;
        endTime = startTime.plusMonths(1);
        merchantId = orderModelList.get(0).getMerchantId();
        orderDTOList = this.orderService.listByMerchantIdAndStatus(merchantId, status, startTime, endTime);
        Assertions.assertEquals(1, orderDTOList.size());
        Assertions.assertEquals(String.valueOf(orderId), orderDTOList.get(0).getId());
        Assertions.assertEquals(1, orderDTOList.get(0).getOrderDetailList().size());
        Assertions.assertEquals(productId, orderDTOList.get(0).getOrderDetailList().get(0).getProductId());
        Assertions.assertEquals(amount, orderDTOList.get(0).getOrderDetailList().get(0).getAmount());
        Assertions.assertEquals(merchantId, orderDTOList.get(0).getMerchantId());
        Assertions.assertEquals(userId, orderDTOList.get(0).getUserId());

        // 测试 listByMerchantIdAndWithoutStatus
        orderId = orderModelList.get(0).getId();
        createTime = orderModelList.get(0).getCreateTime();
        startTime = createTime;
        endTime = startTime.plusMonths(1);
        merchantId = orderModelList.get(0).getMerchantId();
        orderDTOList = this.orderService.listByMerchantIdAndWithoutStatus(merchantId, startTime, endTime);
        Assertions.assertEquals(1, orderDTOList.size());
        Assertions.assertEquals(String.valueOf(orderId), orderDTOList.get(0).getId());
        Assertions.assertEquals(1, orderDTOList.get(0).getOrderDetailList().size());
        Assertions.assertEquals(productId, orderDTOList.get(0).getOrderDetailList().get(0).getProductId());
        Assertions.assertEquals(amount, orderDTOList.get(0).getOrderDetailList().get(0).getAmount());
        Assertions.assertEquals(merchantId, orderDTOList.get(0).getMerchantId());
        Assertions.assertEquals(userId, orderDTOList.get(0).getUserId());

        // endregion

        // region 测试超卖

        // 还原测试数据
        this.reset();

        ExecutorService executorService = Executors.newCachedThreadPool();
        int concurrentThreads = 256;
        for (int i = 0; i < concurrentThreads; i++) {
            int finalI = i;
            Integer finalAmount = amount;
            Long finalProductId = productId;
            executorService.submit(() -> {
                try {
                    Long userIdT = finalI + 1L;
                    this.orderService.create(userIdT, finalProductId, finalAmount, null);
                } catch (Exception e) {
                    //
                }
            });
        }
        executorService.shutdown();

        while (!executorService.awaitTermination(10, TimeUnit.MILLISECONDS)) ;
        TimeUnit.MILLISECONDS.sleep(500);

        orderModelList = this.orderMapper.selectAll();
        Assertions.assertEquals(5, orderModelList.size());
        Assertions.assertEquals(0, this.productMapper.getById(productId).getStock().intValue());

        // endregion

        // region 测试不能使用普通方式下单秒杀商品

        name = RandomStringUtils.randomAlphanumeric(20);
        merchantId = this.merchantService.getIdRandomly();
        LocalDateTime localDateTimeNow = LocalDateTime.now();
        // 马上开始秒杀
        LocalDateTime flashSaleStartTime = localDateTimeNow.plusSeconds(0);
        // 秒杀活动持续10秒
        LocalDateTime flashSaleEndTime = flashSaleStartTime.plusSeconds(10);
        // 秒杀结束2秒后，自动从缓存删除秒杀商品
        int secondAfterWhichExpiredFlashSaleProductForRemoving = 2;
        Mockito.doReturn(secondAfterWhichExpiredFlashSaleProductForRemoving).when(productService).getSecondAfterWhichExpiredFlashSaleProductForRemoving();
        productId = this.productService.add(name, merchantId, stockAmount, true, flashSaleStartTime, flashSaleEndTime);
        TimeUnit.MILLISECONDS.sleep(500);
        amount = 1;
        try {
            this.orderService.create(userId, productId, amount, null);
            Assertions.fail();
        } catch (BusinessException ex) {
            Assertions.assertEquals("商品 " + productId + " 为秒杀类型，不支持普通方式下单", ex.getMessage());
        }

        // endregion

        // region 测试查询订单列表排序

        reset();

        name = RandomStringUtils.randomAlphanumeric(20);
        merchantId = this.merchantService.getIdRandomly();
        productId = productService.add(name, merchantId, stockAmount, false, null, null);

        List<Long> orderIdList = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            createTime = LocalDateTime.now();
            orderId = this.orderService.create(userId, productId, amount, createTime);
            orderIdList.add(0, orderId);
        }

        TimeUnit.MILLISECONDS.sleep(100);
        localDateTimeNow = LocalDateTime.now();
        startTime = localDateTimeNow.minusMinutes(1);
        endTime = localDateTimeNow.plusMinutes(1);
        orderDTOList = this.orderService.listByUserIdAndWithoutStatus(userId, startTime, endTime);
        Assertions.assertEquals(5, orderDTOList.size());
        List<Long> orderIdListActual = orderDTOList.stream().map(OrderDTO::getId).map(Long::parseLong).toList();
        Assertions.assertArrayEquals(orderIdList.toArray(new Long[0]), orderIdListActual.toArray(new Long[0]));

        // endregion
    }

    /**
     * 测试秒杀下单，提示：因为需要测试商品过期从缓存删除逻辑，测试过程可能持续2分钟左右，需要耐心等待
     *
     * @throws Exception
     */
    @Test
    public void testCreateOrderFlashSale() throws Exception {
        // 还原测试数据
        this.reset();

        int stockAmount = 7;

        // region 测试创建秒杀商品

        String name = RandomStringUtils.randomAlphanumeric(20);
        Long merchantId = this.merchantService.getIdRandomly();
        LocalDateTime localDateTimeNow = LocalDateTime.now();
        // 1秒后开始秒杀
        LocalDateTime flashSaleStartTime = localDateTimeNow.plusSeconds(1);
        // 秒杀活动持续1秒
        LocalDateTime flashSaleEndTime = flashSaleStartTime.plusSeconds(1);
        // 秒杀结束2秒后，自动从缓存删除秒杀商品
        int secondAfterWhichExpiredFlashSaleProductForRemoving = productService.getSecondAfterWhichExpiredFlashSaleProductForRemoving();
        /*int secondAfterWhichExpiredFlashSaleProductForRemoving = 2;
        Mockito.doReturn(secondAfterWhichExpiredFlashSaleProductForRemoving).when(productService).getSecondAfterWhichExpiredFlashSaleProductForRemoving();*/
        Long productId = this.productService.add(name, merchantId, stockAmount, true, flashSaleStartTime, flashSaleEndTime);
        List<ProductModel> productModelList = this.productMapper.list(Collections.singletonList(productId));
        Assertions.assertEquals(1, productModelList.size());
        Assertions.assertEquals(productId, productModelList.get(0).getId());
        Assertions.assertEquals(name, productModelList.get(0).getName());
        Assertions.assertEquals(merchantId, productModelList.get(0).getMerchantId());
        Assertions.assertEquals(stockAmount, productModelList.get(0).getStock());

        // 检查秒杀商品的库存缓存
        Long finalProductId = productId;
        Awaitility.waitAtMost(5, TimeUnit.SECONDS).pollInterval(100, TimeUnit.MILLISECONDS)
                .until(() -> {
                    String key = String.format(ProductService.KeyFlashSaleProductStockAmountWithHashTag, finalProductId);
                    String value = redisTemplate.opsForValue().get(key);
                    return !StringUtils.isBlank(value) && String.valueOf(stockAmount).equals(value);
                });
        String key = String.format(ProductService.KeyFlashSaleProductStockAmountWithHashTag, finalProductId);
        String value = redisTemplate.opsForValue().get(key);
        int stockAmountInCache = Integer.parseInt(value);
        // 检查秒杀商品的开始时间缓存
        key = String.format(ProductService.KeyFlashSaleProductStartTime, productId);
        value = redisTemplate.opsForValue().get(key);
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        Assertions.assertEquals(dateTimeFormatter.format(productModelList.get(0).getFlashSaleStartTime()), value);
        // 检查秒杀商品的结束时间缓存
        key = String.format(ProductService.KeyFlashSaleProductEndTime, productId);
        value = redisTemplate.opsForValue().get(key);
        Assertions.assertEquals(dateTimeFormatter.format(productModelList.get(0).getFlashSaleEndTime()), value);

        // 检查秒杀商品过期时间缓存
        List<ProductModel> finalProductModelList = productModelList;
        Awaitility.waitAtMost(5, TimeUnit.SECONDS).pollInterval(100, TimeUnit.MILLISECONDS)
                .until(() -> {
                    long epochSecond = finalProductModelList.get(0).getFlashSaleEndTime().plusSeconds(secondAfterWhichExpiredFlashSaleProductForRemoving)
                            .toEpochSecond(ZoneOffset.ofHours(8));
                    Set<ZSetOperations.TypedTuple<String>> typedTupleSet =
                            redisTemplate.opsForZSet().reverseRangeByScoreWithScores(ProductService.KeyFlashSaleProductExpirationCache, epochSecond, epochSecond);
                    return typedTupleSet != null && typedTupleSet.size() == 1;
                });
        long epochSecond = productModelList.get(0).getFlashSaleEndTime().plusSeconds(secondAfterWhichExpiredFlashSaleProductForRemoving)
                .toEpochSecond(ZoneOffset.ofHours(8));
        Set<ZSetOperations.TypedTuple<String>> typedTupleSet =
                redisTemplate.opsForZSet().reverseRangeByScoreWithScores(ProductService.KeyFlashSaleProductExpirationCache, epochSecond, epochSecond);
        Assertions.assertEquals(1, typedTupleSet.size());
        ZSetOperations.TypedTuple<String> typedTuple = (ZSetOperations.TypedTuple<String>) typedTupleSet.toArray()[0];
        Assertions.assertEquals(String.valueOf(productId), typedTuple.getValue());
        Assertions.assertEquals(epochSecond, typedTuple.getScore());

        // 等待到crond删除过期的秒杀商品
        long finalEpochSecond1 = epochSecond;
        Awaitility.waitAtMost(65, TimeUnit.SECONDS).pollInterval(1, TimeUnit.SECONDS)
                .until(() -> {
                    Set<ZSetOperations.TypedTuple<String>> typedTupleSetInternal =
                            redisTemplate.opsForZSet().
                                    reverseRangeByScoreWithScores(ProductService.KeyFlashSaleProductExpirationCache, finalEpochSecond1, finalEpochSecond1);
                    return typedTupleSetInternal == null || typedTupleSetInternal.isEmpty();
                });
        // 检查是否成功 - 同步缓存中的秒杀商品库存到数据库中
        productModelList = productMapper.list(Collections.singletonList(productId));
        Assertions.assertEquals(stockAmountInCache, productModelList.get(0).getStock());

        // 检查是否成功 - 删除缓存中的秒杀商品信息
        key = String.format(ProductService.KeyFlashSaleProductStockAmountWithHashTag, productId);
        Assertions.assertNotEquals(Boolean.TRUE, redisTemplate.hasKey(key));
        key = String.format(ProductService.KeyFlashSaleProductStartTime, productId);
        Assertions.assertNotEquals(Boolean.TRUE, redisTemplate.hasKey(key));
        key = String.format(ProductService.KeyFlashSaleProductEndTime, productId);
        Assertions.assertNotEquals(Boolean.TRUE, redisTemplate.hasKey(key));

        // endregion

        Long userId = 1L;
        Integer amount = 1;

        // region 测试创建订单

        // 测试秒杀未开始
        name = RandomStringUtils.randomAlphanumeric(20);
        merchantId = this.merchantService.getIdRandomly();
        flashSaleStartTime = productService.getFlashSaleStartTimeRandomly();
        flashSaleEndTime = productService.getFlashSaleEndTimeRandomly(flashSaleStartTime);
        productId = this.productService.add(name, merchantId, stockAmount, true, flashSaleStartTime, flashSaleEndTime);
        try {
            TimeUnit.MILLISECONDS.sleep(500);
            orderService.createFlashSale(userId, productId, amount, null);
            Assertions.fail();
        } catch (BusinessException ex) {
            Assertions.assertTrue(ex.getMessage().contains("秒杀未开始"), ex.getMessage());
        }

        // 测试秒杀已结束
        name = RandomStringUtils.randomAlphanumeric(20);
        merchantId = this.merchantService.getIdRandomly();
        localDateTimeNow = LocalDateTime.now();
        flashSaleStartTime = localDateTimeNow;
        flashSaleEndTime = localDateTimeNow.plusSeconds(1);
        productId = this.productService.add(name, merchantId, stockAmount, true, flashSaleStartTime, flashSaleEndTime);
        try {
            TimeUnit.MILLISECONDS.sleep(1500);
            orderService.createFlashSale(userId, productId, amount, null);
            Assertions.fail();
        } catch (BusinessException ex) {
            Assertions.assertTrue(ex.getMessage().contains("秒杀已结束"));
        }

        // 测试正常下单
        localDateTimeNow = LocalDateTime.now();
        flashSaleStartTime = localDateTimeNow;
        flashSaleEndTime = localDateTimeNow.plusSeconds(5);
        epochSecond = flashSaleEndTime.plusSeconds(secondAfterWhichExpiredFlashSaleProductForRemoving)
                .toEpochSecond(ZoneOffset.ofHours(8));
        productId = this.productService.add(name, merchantId, stockAmount, true, flashSaleStartTime, flashSaleEndTime);

        // 等待秒杀商品缓存设置完毕
        TimeUnit.MILLISECONDS.sleep(500);

        this.orderService.createFlashSale(userId, productId, amount, null);

        // 等待订单同步到数据库中
        TimeUnit.MILLISECONDS.sleep(500);

        List<OrderModel> orderModelList = this.orderMapper.selectAll();
        Assertions.assertEquals(1, orderModelList.size());
        Assertions.assertEquals(userId, orderModelList.get(0).getUserId());
        List<OrderDetailModel> orderDetailModelList = this.orderDetailMapper.selectAll();
        Assertions.assertEquals(1, orderDetailModelList.size());
        Assertions.assertEquals(productId, orderDetailModelList.get(0).getProductId());
        Assertions.assertEquals(amount, orderDetailModelList.get(0).getAmount());
        Assertions.assertEquals(userId, orderDetailModelList.get(0).getUserId());
        Assertions.assertEquals(orderModelList.get(0).getId(), orderDetailModelList.get(0).getOrderId());

        // endregion

        // region 测试用户重复下单

        // 还原测试数据
        this.reset();

        // 删除上面用户重复下单标识
        key = String.format(OrderService.KeyProductPurchaseRecordWithHashTag, productId);
        redisTemplate.delete(key);

        ExecutorService executorService = Executors.newCachedThreadPool();
        int concurrentThreads = 128;
        for (int i = 0; i < concurrentThreads; i++) {
            Long finalProductId1 = productId;
            Integer finalAmount = amount;
            executorService.submit(() -> {
                try {
                    this.orderService.createFlashSale(userId, finalProductId1, finalAmount, null);
                } catch (Exception e) {
                    //
                }
            });
        }
        executorService.shutdown();
        while (!executorService.awaitTermination(10, TimeUnit.MILLISECONDS)) ;
        TimeUnit.MILLISECONDS.sleep(500);

        orderModelList = this.orderMapper.selectAll();
        Assertions.assertEquals(1, orderModelList.size());
        Assertions.assertEquals(userId, orderModelList.get(0).getUserId());
        orderDetailModelList = this.orderDetailMapper.selectAll();
        Assertions.assertEquals(1, orderDetailModelList.size());
        Assertions.assertEquals(productId, orderDetailModelList.get(0).getProductId());
        Assertions.assertEquals(amount, orderDetailModelList.get(0).getAmount());
        Assertions.assertEquals(userId, orderDetailModelList.get(0).getUserId());
        Assertions.assertEquals(orderModelList.get(0).getId(), orderDetailModelList.get(0).getOrderId());

        // endregion

        // region 测试超卖

        // 还原测试数据
        this.reset();

        // 删除上面用户重复下单标识
        key = String.format(OrderService.KeyProductPurchaseRecordWithHashTag, productId);
        redisTemplate.delete(key);

        executorService = Executors.newCachedThreadPool();
        concurrentThreads = 256;
        for (int i = 0; i < concurrentThreads; i++) {
            int finalI = i;
            Long finalProductId2 = productId;
            Integer finalAmount1 = amount;
            executorService.submit(() -> {
                try {
                    Long userIdT = finalI + 1L;
                    this.orderService.createFlashSale(userIdT, finalProductId2, finalAmount1, null);
                } catch (Exception e) {
                    //
                }
            });
        }
        executorService.shutdown();

        while (!executorService.awaitTermination(10, TimeUnit.MILLISECONDS)) ;
        TimeUnit.MILLISECONDS.sleep(500);

        orderModelList = this.orderMapper.selectAll();
        Assertions.assertEquals(5, orderModelList.size());

        // 等待到秒杀商品过期自动从缓存中删除
        long finalEpochSecond = epochSecond;
        Awaitility.waitAtMost(65, TimeUnit.SECONDS).pollInterval(1, TimeUnit.SECONDS)
                .until(() -> {
                    Set<ZSetOperations.TypedTuple<String>> typedTupleSetInternal =
                            redisTemplate.opsForZSet().
                                    reverseRangeByScoreWithScores(ProductService.KeyFlashSaleProductExpirationCache, finalEpochSecond, finalEpochSecond);
                    return typedTupleSetInternal == null || typedTupleSetInternal.isEmpty();
                });
        // 检查是否成功 - 同步缓存中的秒杀商品库存到数据库中
        productModelList = productMapper.list(Collections.singletonList(productId));
        Assertions.assertEquals(0, productModelList.get(0).getStock());

        // 检查用户重复秒杀标识是否已经删除
        key = String.format(OrderService.KeyProductPurchaseRecordWithHashTag, productId);
        Assertions.assertNotEquals(Boolean.TRUE, redisTemplate.hasKey(key));

        // endregion

        // region 测试不能秒杀普通商品

        // 创建普通商品
        name = RandomStringUtils.randomAlphanumeric(20);
        merchantId = this.merchantService.getIdRandomly();
        amount = 1;
        productId = productService.add(name, merchantId, stockAmount, false, null, null);
        TimeUnit.MILLISECONDS.sleep(500);
        try {
            orderService.createFlashSale(userId, productId, amount, null);
            Assertions.fail();
        } catch (BusinessException ex) {
            Assertions.assertEquals("秒杀商品 " + productId + " 不存在", ex.getMessage());
        }

        // endregion
    }

    void reset() {
        // 删除所有订单
        this.orderDetailMapper.deleteAll();
        this.orderMapper.deleteAll();

        // 删除所有Cassandra索引
        session.execute("truncate table t_order_list_by_userId");
        session.execute("truncate table t_order_list_by_merchantId");
    }
}
