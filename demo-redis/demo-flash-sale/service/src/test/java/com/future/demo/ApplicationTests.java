package com.future.demo;

import com.future.demo.entity.OrderDetailModel;
import com.future.demo.entity.OrderModel;
import com.future.demo.entity.ProductModel;
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

    @Test
    public void testBasedDB() throws Exception {
        // 还原测试数据
        this.reset();

        Long userId = 1L;
        Long productId = 1L;
        Integer amount = 2;

        // region 测试成功创建订单
        this.orderService.createOrderBasedDB(userId, productId, amount);
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
                    this.orderService.createOrderBasedDB(userId, productId, amount);
                } catch (Exception e) {
                    //
                }
            });
        }
        executorService.shutdown();
        while (!executorService.awaitTermination(10, TimeUnit.MILLISECONDS)) ;

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
                    this.orderService.createOrderBasedDB(userIdT, productId, amount);
                } catch (Exception e) {
                    //
                }
            });
        }
        executorService.shutdown();
        while (!executorService.awaitTermination(10, TimeUnit.MILLISECONDS)) ;

        orderModelList = this.orderMapper.selectAll();
        Assertions.assertEquals(5, orderModelList.size());
        Assertions.assertEquals(0, this.productMapper.getById(productId).getStock().intValue());

        // endregion
    }

    @Test
    public void testBasedRedisWithLuaScript() throws Exception {
        // 还原测试数据
        this.reset();

        Long userId = 1L;
        Long productId = 1L;
        Integer amount = 2;

        // region 测试成功创建订单
        this.orderService.createOrderBasedRedisWithLuaScript(userId, productId, amount);
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
                    this.orderService.createOrderBasedRedisWithLuaScript(userId, productId, amount);
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
                    this.orderService.createOrderBasedRedisWithLuaScript(userIdT, productId, amount);
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
//
//    @Test
//    public void testBasedRedisWithoutLuaScript() throws Exception {
//        // 还原测试数据
//        this.reset();
//
//        Long userId = 1L;
//        Long productId = 1L;
//        Integer amount = 2;
//
//        // region 测试成功创建订单
//        this.orderService.createOrderBasedRedisWithoutLuaScript(userId, productId, amount);
//        TimeUnit.MILLISECONDS.sleep(500);
//        List<OrderModel> orderModelList = this.orderMapper.selectAll();
//        Assert.assertEquals(1, orderModelList.size());
//        Assert.assertEquals(userId, orderModelList.get(0).getUserId());
//        Assert.assertEquals(productId, orderModelList.get(0).getProductId());
//        Assert.assertEquals(amount, orderModelList.get(0).getAmount());
//
//        // endregion
//
//        // region 测试用户重复下单
//
//        // 还原测试数据
//        this.reset();
//
//        ExecutorService executorService = Executors.newCachedThreadPool();
//        int concurrentThreads = 128;
//        for (int i = 0; i < concurrentThreads; i++) {
//            executorService.submit(() -> {
//                try {
//                    this.orderService.createOrderBasedRedisWithoutLuaScript(userId, productId, amount);
//                } catch (Exception e) {
//                    //
//                }
//            });
//        }
//        executorService.shutdown();
//        while (!executorService.awaitTermination(10, TimeUnit.MILLISECONDS)) ;
//        TimeUnit.MILLISECONDS.sleep(500);
//
//        orderModelList = this.orderMapper.selectAll();
//        Assert.assertEquals(1, orderModelList.size());
//        Assert.assertEquals(userId, orderModelList.get(0).getUserId());
//        Assert.assertEquals(productId, orderModelList.get(0).getProductId());
//        Assert.assertEquals(amount, orderModelList.get(0).getAmount());
//
//        // endregion
//
//        // region 测试超卖
//
//        // 还原测试数据
//        this.reset();
//
//        executorService = Executors.newCachedThreadPool();
//        concurrentThreads = 256;
//        for (int i = 0; i < concurrentThreads; i++) {
//            int finalI = i;
//            executorService.submit(() -> {
//                try {
//                    Long userIdT = finalI + 1L;
//                    this.orderService.createOrderBasedRedisWithoutLuaScript(userIdT, productId, amount);
//                } catch (Exception e) {
//                    //
//                }
//            });
//        }
//        executorService.shutdown();
//
//        while (!executorService.awaitTermination(10, TimeUnit.MILLISECONDS)) ;
//        TimeUnit.MILLISECONDS.sleep(500);
//
//        orderModelList = this.orderMapper.selectAll();
//        Assert.assertEquals(5, orderModelList.size());
//        Assert.assertEquals(0, this.productMapper.getById(productId).getStock().intValue());
//        /*Assert.assertEquals(0, Integer.parseInt(Objects.requireNonNull(this.redisTemplate.opsForValue().get(OrderService.KeyProductStockPrefix + productId))));*/
//
//        // endregion
//    }

    void reset() {
        // 删除所有订单
        this.orderDetailMapper.deleteAll();
        this.orderMapper.deleteAll();

        for (long i = 1; i <= OrderService.ProductCount; i++) {
            // 准备 redis 数据辅助基于缓存的测试
            Integer productStock = 10;
            String keyProductStock = String.format(OrderService.KeyproductStockWithHashTag, i);
            this.redisTemplate.opsForValue().set(keyProductStock, String.valueOf(productStock));
            String keyProductPurchaseRecord = String.format(OrderService.KeyProductPurchaseRecordWithHashTag, i);
            this.redisTemplate.delete(keyProductPurchaseRecord);

            // 准备 db 数据辅助基于数据库的测试
            this.productMapper.delete(i);
            ProductModel productModel = new ProductModel();
            productModel.setId(i);
            productModel.setName("产品" + i);
            productModel.setStock(productStock);
            this.productMapper.insert(productModel);
        }
    }
}
