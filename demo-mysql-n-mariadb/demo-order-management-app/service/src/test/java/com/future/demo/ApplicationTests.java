package com.future.demo;

import com.future.demo.dto.OrderDTO;
import com.future.demo.entity.OrderDetailModel;
import com.future.demo.entity.OrderModel;
import com.future.demo.entity.ProductModel;
import com.future.demo.mapper.OrderDetailMapper;
import com.future.demo.mapper.OrderMapper;
import com.future.demo.mapper.ProductMapper;
import com.future.demo.service.OrderService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.math.RandomUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@SpringBootTest
@Slf4j
public class ApplicationTests {

    @Resource
    ProductMapper productMapper;
    @Resource
    OrderMapper orderMapper;
    @Resource
    OrderDetailMapper orderDetailMapper;

    @Resource
    OrderService orderService;

    @Test
    public void test() throws Exception {
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

        // region 测试订单 getById

        orderModelList = this.orderMapper.selectAll();
        Long orderId = orderModelList.get(0).getId();
        OrderDTO orderDTO = this.orderService.getById(orderId);
        Assertions.assertEquals(orderId, orderDTO.getId());
        Assertions.assertEquals(orderModelList.get(0).getUserId(), orderDTO.getUserId());
        Assertions.assertEquals(orderModelList.get(0).getStatus(), orderDTO.getStatus());
        Assertions.assertEquals(orderModelList.get(0).getDeleteStatus(), orderDTO.getDeleteStatus());
        orderDetailModelList = this.orderDetailMapper.list(Collections.singletonList(orderId));
        Assertions.assertEquals(orderDetailModelList.size(), orderDTO.getOrderDetailList().size());
        Assertions.assertEquals(orderDetailModelList.get(0).getId(), orderDTO.getOrderDetailList().get(0).getId());

        // endregion
    }

    void reset() {
        // 删除所有订单
        this.orderDetailMapper.deleteAll();
        this.orderMapper.deleteAll();

        for (long i = 1; i <= OrderService.ProductCount; i++) {
            Integer productStock = 10;

            // 准备 db 数据辅助基于数据库的测试
            this.productMapper.delete(i);
            ProductModel productModel = new ProductModel();
            productModel.setId(i);
            productModel.setName("产品" + i);
            productModel.setStock(productStock);

            long merchantId = RandomUtils.nextInt(OrderService.MerchantCount) + 1;
            productModel.setMerchantId(merchantId);

            this.productMapper.insert(productModel);
        }
    }
}
