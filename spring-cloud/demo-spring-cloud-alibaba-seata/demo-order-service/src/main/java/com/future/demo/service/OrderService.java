package com.future.demo.service;

import com.future.common.exception.BusinessException;
import com.future.demo.entity.Order;
import com.future.demo.feign.AccountClient;
import com.future.demo.feign.StorageClient;
import com.future.demo.mapper.OrderMapper;
import io.seata.core.context.RootContext;
import io.seata.spring.annotation.GlobalTransactional;
import lombok.extern.slf4j.Slf4j;
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

    @GlobalTransactional(name = "order-create", rollbackFor = Exception.class)
    public void createOrder(Order order) throws BusinessException {
        String xid = RootContext.getXID();
        try {
            log.debug("创建订单，XID：{}", xid);

            order.setStatus(0);
            int count = this.orderMapper.insert(order);
            if (count > 0) {
                order = this.orderMapper.findById(order.getId());
                log.debug("订单创建成功，订单信息：{}", order);

                // 扣减库存
                this.storageClient.deduct(order.getProductId(), order.getCount());
                log.debug("库存扣减成功，订单信息：{}", order);

                // 扣减账户余额
                this.accountClient.deduct(order.getUserId(), order.getMoney());
                log.debug("账户余额扣减成功，订单信息：{}", order);

                // 更新订单状态
                order.setStatus(1);
                count = this.orderMapper.update(order);
                if (count > 0) {
                    log.debug("订单状态更新成功，订单信息：{}", order);
                } else {
                    log.debug("订单状态更新失败，订单信息：{}", order);
                }
            } else {
                log.debug("订单插入失败，订单信息：{}", order);
                throw new BusinessException("订单创建失败");
            }
        } finally {
            log.debug("订单创建结束，XID：{}", xid);
        }
    }
}
