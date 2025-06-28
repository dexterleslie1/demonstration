package com.future.demo;

import com.future.common.exception.BusinessException;
import com.future.demo.entity.OrderDetailModel;
import com.future.demo.entity.OrderModel;
import com.future.demo.mapper.OrderDetailMapper;
import com.future.demo.mapper.OrderMapper;
import com.future.demo.mapper.ProductMapper;
import com.future.demo.service.OrderService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;

import javax.annotation.Resource;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

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

    /**
     * 测试 redis 缓存是否正确
     */
    @Test
    public void testRedis() throws Exception {
        // 还原测试数据
        this.reset();

        Long userId = 1L;
        Long productId = 1L;
        Integer amount = 1;

        // 秒杀前商品的数量
        String productKey = String.format(OrderService.KeyproductStockWithHashTag, productId);
        int productStockBefore = Integer.parseInt(this.redisTemplate.opsForValue().get(productKey));

        this.orderService.create(userId, productId, amount);

        // redis缓存中商品数量减1
        int productStockAfter = Integer.parseInt(this.redisTemplate.opsForValue().get(productKey));
        Assertions.assertEquals(productStockBefore - 1, productStockAfter);

        // redis缓存中商品购买有记录
        String keyProductPurchaseRecord = String.format(OrderService.KeyProductPurchaseRecordWithHashTag, productId);
        Boolean member = this.redisTemplate.opsForSet().isMember(keyProductPurchaseRecord, String.valueOf(userId));
        Assertions.assertTrue(member);

        // 测试库存不足
        amount = Integer.MAX_VALUE;
        try {
            this.orderService.create(userId, productId, amount);
            Assertions.fail();
        } catch (BusinessException ex) {
            Assertions.assertEquals("库存不足", ex.getMessage());
        }

        // 测试不能重复购买
        amount = 1;
        try {
            this.orderService.create(userId, productId, amount);
            Assertions.fail();
        } catch (BusinessException ex) {
            Assertions.assertEquals("用户重复下单", ex.getMessage());
        }
    }

    /**
     * 测试普通下单
     *
     * @throws Exception
     */
    @Test
    public void testCreateOrderOrdinarily() throws Exception {
        // 还原测试数据
        this.reset();

        Long userId = 1L;
        Long productId = 1L;
        Integer amount = 1;

        // region 测试成功创建订单

        this.orderService.create(userId, productId, amount);
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

        // region 测试超卖

        // 还原测试数据
        this.reset();

        ExecutorService executorService = Executors.newCachedThreadPool();
        int concurrentThreads = 256;
        for (int i = 0; i < concurrentThreads; i++) {
            int finalI = i;
            executorService.submit(() -> {
                try {
                    Long userIdT = finalI + 1L;
                    this.orderService.create(userIdT, productId, amount);
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
    }

    /**
     * 测试秒杀下单
     *
     * @throws Exception
     */
    @Test
    public void testCreateOrderFlashSale() throws Exception {
        // 还原测试数据
        this.reset();

        Long userId = 1L;
        Long productId = 1L;
        Integer amount = 1;

        // region 测试成功创建订单

        this.orderService.createFlashSale(userId, productId, amount);
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

        ExecutorService executorService = Executors.newCachedThreadPool();
        int concurrentThreads = 128;
        for (int i = 0; i < concurrentThreads; i++) {
            executorService.submit(() -> {
                try {
                    this.orderService.createFlashSale(userId, productId, amount);
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

        executorService = Executors.newCachedThreadPool();
        concurrentThreads = 256;
        for (int i = 0; i < concurrentThreads; i++) {
            int finalI = i;
            executorService.submit(() -> {
                try {
                    Long userIdT = finalI + 1L;
                    this.orderService.createFlashSale(userIdT, productId, amount);
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
        // todo 同步缓存中的库存到数据库中
        /*Assertions.assertEquals(0, this.productMapper.getById(productId).getStock().intValue());*/

        // endregion
    }

    void reset() {
        // 删除所有订单
        this.orderDetailMapper.deleteAll();
        this.orderMapper.deleteAll();

        // 重新初始化商品信息
        this.orderService.initProduct();
    }
}
