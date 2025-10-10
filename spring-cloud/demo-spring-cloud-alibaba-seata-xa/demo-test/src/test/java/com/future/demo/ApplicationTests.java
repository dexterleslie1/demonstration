package com.future.demo;

import com.future.common.constant.ErrorCodeConstant;
import com.future.common.http.ObjectResponse;
import com.future.demo.entity.Account;
import com.future.demo.entity.Order;
import com.future.demo.entity.Storage;
import com.future.demo.mapper.account.AccountMapper;
import com.future.demo.mapper.order.OrderMapper;
import com.future.demo.mapper.storage.StorageMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.util.Assert;

import java.math.BigDecimal;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@SpringBootTest(classes = {ApplicationTest.class}, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ApplicationTests {
    @Autowired
    TestRestTemplate restTemplate;
    @Autowired
    OrderMapper orderMapper;
    @Autowired
    StorageMapper storageMapper;
    @Autowired
    AccountMapper accountMapper;

    @Test
    public void contextLoads() throws InterruptedException {
        long productId = 1;
        long userId = 1;
        int count = 2;
        // 购买商品总金额
        int amount = 2000000;

        {
            // region 测试扣款失败时事务的原子性

            String url = "http://localhost:8080/order/reset";
            ResponseEntity<ObjectResponse<String>> responseEntity = restTemplate.exchange(url,
                    HttpMethod.GET, null, new ParameterizedTypeReference<ObjectResponse<String>>() {
                    });
            Assertions.assertEquals(200, responseEntity.getStatusCodeValue());
            Assertions.assertEquals(0, responseEntity.getBody().getErrorCode());
            Assertions.assertNull(responseEntity.getBody().getErrorMessage());
            Assertions.assertEquals("重置成功", responseEntity.getBody().getData());

            int totalPrev = this.orderMapper.selectAll().size();
            Storage storagePrev = this.storageMapper.getByProductId(productId);
            Account accountPrev = this.accountMapper.getByUserId(userId);

            url = "http://localhost:8080/order/create?userId=" + userId + "&productId=" + productId + "&count=" + count + "&amount=" + amount;
            responseEntity = restTemplate.exchange(url,
                    HttpMethod.POST, null, new ParameterizedTypeReference<ObjectResponse<String>>() {
                    });
            // 预期返回 http 200
            Assertions.assertEquals(200, responseEntity.getStatusCodeValue());
            Assertions.assertEquals(ErrorCodeConstant.ErrorCodeCommon, responseEntity.getBody().getErrorCode());
            Assertions.assertEquals("用户 " + userId + " 余额不足导致扣款失败！", responseEntity.getBody().getErrorMessage());
            Assertions.assertNull(responseEntity.getBody().getData());

            // 当前订单总数和调用创建订单接口之前的订单总数一致
            int totalCurrent = this.orderMapper.selectAll().size();
            Assertions.assertEquals(totalPrev, totalCurrent);
            // 当前商品库存信息和调用创建订单接口之前的商品库存信息一致
            Storage storageCurrent = this.storageMapper.getByProductId(productId);
            Assertions.assertEquals(storagePrev.getUsed(), storageCurrent.getUsed());
            Assertions.assertEquals(storagePrev.getResidue(), storageCurrent.getResidue());
            // 当前帐号余额信息和调用创建订单接口之前的帐号余额信息一致
            Account accountCurrent = this.accountMapper.getByUserId(userId);
            Assertions.assertEquals(accountPrev.getUsed(), accountCurrent.getUsed());
            Assertions.assertEquals(accountPrev.getResidue(), accountCurrent.getResidue());

            // endregion
        }

        {
            // region 测试成功创建订单

            amount = 50;

            int totalPrev = this.orderMapper.selectAll().size();
            Storage storagePrev = this.storageMapper.getByProductId(productId);
            Account accountPrev = this.accountMapper.getByUserId(userId);

            String url = "http://localhost:8080/order/create?userId=" + userId + "&productId=" + productId + "&count=" + count + "&amount=" + amount;
            ResponseEntity<ObjectResponse<String>> responseEntity = restTemplate.exchange(url,
                    HttpMethod.POST, null, new ParameterizedTypeReference<ObjectResponse<String>>() {
                    });
            // 预期返回 http 200
            Assertions.assertEquals(200, responseEntity.getStatusCodeValue());
            Assertions.assertEquals(0, responseEntity.getBody().getErrorCode());
            Assertions.assertNotNull(responseEntity.getBody().getData());

            // 校验当前订单总数
            int totalCurrent = this.orderMapper.selectAll().size();
            Assertions.assertEquals(totalPrev + 1, totalCurrent);
            // 校验当前商品库存信息
            Storage storageCurrent = this.storageMapper.getByProductId(productId);
            Assertions.assertEquals(storagePrev.getUsed() + count, storageCurrent.getUsed());
            Assertions.assertEquals(storagePrev.getResidue() - count, storageCurrent.getResidue());
            // 校验当前帐号余额信息
            Account accountCurrent = this.accountMapper.getByUserId(userId);
            Assertions.assertEquals(accountPrev.getUsed().add(BigDecimal.valueOf(amount)), accountCurrent.getUsed());
            Assertions.assertEquals(accountPrev.getResidue().subtract(BigDecimal.valueOf(amount)), accountCurrent.getResidue());

            // endregion
        }

        {
            // region 测试并发事务是否会导致余额超扣问题

            String url = "http://localhost:8080/order/reset";
            ResponseEntity<ObjectResponse<String>> responseEntity = restTemplate.exchange(url,
                    HttpMethod.GET, null, new ParameterizedTypeReference<ObjectResponse<String>>() {
                    });
            Assertions.assertEquals(200, responseEntity.getStatusCodeValue());
            Assertions.assertEquals(0, responseEntity.getBody().getErrorCode());
            Assertions.assertNull(responseEntity.getBody().getErrorMessage());
            Assertions.assertEquals("重置成功", responseEntity.getBody().getData());

            url = "http://localhost:8080/order/create?userId=" + userId + "&productId=" + productId + "&count=" + count + "&amount=" + amount;
            int concurrentThreads = 128;
            ExecutorService threadPool = Executors.newCachedThreadPool();
            for (int i = 0; i < concurrentThreads; i++) {
                String finalUrl = url;
                threadPool.submit(() -> {
                    restTemplate.exchange(finalUrl,
                            HttpMethod.POST, null, new ParameterizedTypeReference<ObjectResponse<String>>() {
                            });
                });
            }
            threadPool.shutdown();
            while (!threadPool.awaitTermination(100, TimeUnit.MILLISECONDS)) ;

            // 预期有 20 笔订单
            List<Order> orderList = this.orderMapper.selectAll();
            Assertions.assertEquals(20, orderList.size());
            // 预期库存已用 40，库存剩余 60
            Storage storageCurrent = this.storageMapper.getByProductId(productId);
            Assertions.assertEquals(40, storageCurrent.getUsed());
            Assertions.assertEquals(60, storageCurrent.getResidue());
            // 预期余额已用 1000，余额剩余 0
            Account accountCurrent = this.accountMapper.getByUserId(userId);
            Assertions.assertEquals(BigDecimal.valueOf(1000), accountCurrent.getUsed());
            Assertions.assertEquals(BigDecimal.valueOf(0), accountCurrent.getResidue());

            // endregion
        }
    }
}
