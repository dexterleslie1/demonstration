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

import java.math.BigDecimal;
import java.util.List;

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
    public void contextLoads() {
        long productId = 1;
        long userId = 1;
        int count = 2;
        int amount = 20;

        {
            // region 测试抛出业务异常时事务的原子性

            int totalPrev = this.orderMapper.selectAll().size();
            Storage storagePrev = this.storageMapper.getByProductId(productId);
            Account accountPrev = this.accountMapper.getByUserId(userId);

            // throwBusinessException=true 表示抛出业务异常
            String url = "http://localhost:8080/order/create?userId=" + userId + "&productId=" + productId + "&count=" + count + "&amount=" + amount + "&throwBusinessException=true";
            ResponseEntity<ObjectResponse<String>> responseEntity = restTemplate.exchange(url,
                    HttpMethod.POST, null, new ParameterizedTypeReference<ObjectResponse<String>>() {
                    });
            // 预期返回 http 200
            Assertions.assertEquals(200, responseEntity.getStatusCodeValue());
            Assertions.assertEquals(ErrorCodeConstant.ErrorCodeCommon, responseEntity.getBody().getErrorCode());
            Assertions.assertEquals("扣款失败", responseEntity.getBody().getErrorMessage());
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

            int totalPrev = this.orderMapper.selectAll().size();
            Storage storagePrev = this.storageMapper.getByProductId(productId);
            Account accountPrev = this.accountMapper.getByUserId(userId);

            // throwBusinessException=true 表示抛出业务异常
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
    }
}
