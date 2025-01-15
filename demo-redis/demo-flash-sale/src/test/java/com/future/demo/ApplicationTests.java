package com.future.demo;

import com.future.demo.entity.OrderModel;
import com.future.demo.mapper.OrderMapper;
import com.future.demo.mapper.ProductMapper;
import com.future.demo.service.OrderService;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ApplicationTests {

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
    public void testBasedRedis() {

    }

    void reset() {
        // 还原商品库存
        this.productMapper.updateStock(1L, 10);
        // 删除所有订单
        this.orderMapper.deleteAll();
    }
}
