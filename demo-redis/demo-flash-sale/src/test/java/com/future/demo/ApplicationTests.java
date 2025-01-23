package com.future.demo;

import com.future.demo.entity.OrderModel;
import com.future.demo.entity.ProductModel;
import com.future.demo.mapper.OrderMapper;
import com.future.demo.mapper.ProductMapper;
import com.future.demo.service.OrderService;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@RunWith(SpringRunner.class)
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
        Assert.assertEquals(1, orderModelList.size());
        Assert.assertEquals(userId, orderModelList.get(0).getUserId());
        Assert.assertEquals(productId, orderModelList.get(0).getProductId());
        Assert.assertEquals(amount, orderModelList.get(0).getAmount());

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
        Assert.assertEquals(1, orderModelList.size());
        Assert.assertEquals(userId, orderModelList.get(0).getUserId());
        Assert.assertEquals(productId, orderModelList.get(0).getProductId());
        Assert.assertEquals(amount, orderModelList.get(0).getAmount());

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
        Assert.assertEquals(5, orderModelList.size());
        Assert.assertEquals(0, this.productMapper.getById(productId).getStock().intValue());

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
        Assert.assertEquals(1, orderModelList.size());
        Assert.assertEquals(userId, orderModelList.get(0).getUserId());
        Assert.assertEquals(productId, orderModelList.get(0).getProductId());
        Assert.assertEquals(amount, orderModelList.get(0).getAmount());

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
        Assert.assertEquals(1, orderModelList.size());
        Assert.assertEquals(userId, orderModelList.get(0).getUserId());
        Assert.assertEquals(productId, orderModelList.get(0).getProductId());
        Assert.assertEquals(amount, orderModelList.get(0).getAmount());

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
        Assert.assertEquals(5, orderModelList.size());
        Assert.assertEquals(0, this.productMapper.getById(productId).getStock().intValue());
        Assert.assertEquals(0, Integer.parseInt(Objects.requireNonNull(this.redisTemplate.opsForValue().get(OrderService.KeyProductStockPrefix + productId))));

        // endregion
    }

    @Test
    public void testBasedRedisWithoutLuaScript() throws Exception {
        // 还原测试数据
        this.reset();

        Long userId = 1L;
        Long productId = 1L;
        Integer amount = 2;

        // region 测试成功创建订单
        this.orderService.createOrderBasedRedisWithoutLuaScript(userId, productId, amount);
        TimeUnit.MILLISECONDS.sleep(500);
        List<OrderModel> orderModelList = this.orderMapper.selectAll();
        Assert.assertEquals(1, orderModelList.size());
        Assert.assertEquals(userId, orderModelList.get(0).getUserId());
        Assert.assertEquals(productId, orderModelList.get(0).getProductId());
        Assert.assertEquals(amount, orderModelList.get(0).getAmount());

        // endregion

        // region 测试用户重复下单

        // 还原测试数据
        this.reset();

        ExecutorService executorService = Executors.newCachedThreadPool();
        int concurrentThreads = 128;
        for (int i = 0; i < concurrentThreads; i++) {
            executorService.submit(() -> {
                try {
                    this.orderService.createOrderBasedRedisWithoutLuaScript(userId, productId, amount);
                } catch (Exception e) {
                    //
                }
            });
        }
        executorService.shutdown();
        while (!executorService.awaitTermination(10, TimeUnit.MILLISECONDS)) ;
        TimeUnit.MILLISECONDS.sleep(500);

        orderModelList = this.orderMapper.selectAll();
        Assert.assertEquals(1, orderModelList.size());
        Assert.assertEquals(userId, orderModelList.get(0).getUserId());
        Assert.assertEquals(productId, orderModelList.get(0).getProductId());
        Assert.assertEquals(amount, orderModelList.get(0).getAmount());

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
                    this.orderService.createOrderBasedRedisWithoutLuaScript(userIdT, productId, amount);
                } catch (Exception e) {
                    //
                }
            });
        }
        executorService.shutdown();

        while (!executorService.awaitTermination(10, TimeUnit.MILLISECONDS)) ;
        TimeUnit.MILLISECONDS.sleep(500);

        orderModelList = this.orderMapper.selectAll();
        Assert.assertEquals(5, orderModelList.size());
        Assert.assertEquals(0, this.productMapper.getById(productId).getStock().intValue());
        Assert.assertEquals(0, Integer.parseInt(Objects.requireNonNull(this.redisTemplate.opsForValue().get(OrderService.KeyProductStockPrefix + productId))));

        // endregion
    }

    void reset() {
        long productId = 1L;
        // 还原商品库存
        this.productMapper.updateStock(productId, 10);
        // 删除所有订单
        this.orderMapper.deleteAll();

        // 秒杀前提前加载商品库存信息到 Redis 中
        ProductModel productModel = this.productMapper.getById(productId);
        String keyProductStock = OrderService.KeyProductStockPrefix + productModel.getId();
        this.redisTemplate.opsForValue().set(keyProductStock, productModel.getStock().toString());
        String keyProductPurchaseRecord = "product:purchase:" + productId;
        this.redisTemplate.delete(keyProductPurchaseRecord);
        String key = OrderService.KeyProductSoldOutPrefix + productId;
        this.redisTemplate.delete(key);
    }
}
