package com.future.demo.service;

import com.future.common.exception.BusinessException;
import com.future.demo.entity.Order;
import com.future.demo.feign.AccountClient;
import com.future.demo.feign.StorageClient;
import com.future.demo.mapper.OrderMapper;
import io.seata.spring.annotation.GlobalTransactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
@Slf4j
public class OrderService {

    @Resource
    OrderMapper orderMapper;
    @Resource
    StorageClient storageClient;
    @Resource
    AccountClient accountClient;
    @Autowired
    OrderTccAction orderTccAction;

    /**
     * @param order
     * @param throwExceptionWhenDeductBalance 扣减余额时是否抛出异常以模拟余额扣减失败
     * @throws BusinessException
     */
    @GlobalTransactional(name = "order-create", rollbackFor = Exception.class)
    public Long createOrder(Order order, boolean throwExceptionWhenDeductBalance) throws BusinessException {
        return orderTccAction.createOrder(order, throwExceptionWhenDeductBalance);
    }

    /**
     * 准备性能测试数据
     *
     * @return
     */
    public void preparePerfTestDatum() throws BusinessException {
        accountClient.preparePerfTestDatum();
        storageClient.preparePerfTestDatum();
        orderMapper.reset();
    }
}
