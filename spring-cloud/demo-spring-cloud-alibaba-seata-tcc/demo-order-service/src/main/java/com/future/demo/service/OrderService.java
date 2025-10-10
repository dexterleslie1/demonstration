package com.future.demo.service;

import com.future.common.exception.BusinessException;
import com.future.common.feign.FeignUtil;
import com.future.common.http.ObjectResponse;
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

    /**
     * @param order
     * @throws BusinessException
     */
    @GlobalTransactional(name = "order-create", rollbackFor = Exception.class)
    public Long createOrder(Order order) throws BusinessException {
        // 获取 Seata 当前全局事务 xid
        String xid = RootContext.getXID();
        try {
            if (log.isDebugEnabled()) {
                log.debug("创建订单，XID：{}", xid);
            }

            order.setStatus(1);
            int count = this.orderMapper.insert(order);
            if (count > 0) {
                order = this.orderMapper.findById(order.getId());
                if (log.isDebugEnabled()) {
                    log.debug("订单创建成功，订单信息：{}", order);
                }

                // 扣减库存
                ObjectResponse<String> response = this.storageClient.deduct(order.getProductId(), order.getCount());
                FeignUtil.throwBizExceptionIfResponseFailed(response);
                if (log.isDebugEnabled()) {
                    log.debug("库存扣减成功，订单信息：{}", order);
                }

                // 扣减账户余额
                response = this.accountClient.deduct(order.getUserId(), order.getMoney());
                FeignUtil.throwBizExceptionIfResponseFailed(response);
                if (log.isDebugEnabled()) {
                    log.debug("账户余额扣减成功，订单信息：{}", order);
                }

                return order.getId();
            } else {
                if (log.isDebugEnabled()) {
                    log.debug("订单插入失败，订单信息：{}", order);
                }
                throw new BusinessException("订单创建失败");
            }
        } finally {
            if (log.isDebugEnabled()) {
                log.debug("订单创建结束，XID：{}", xid);
            }
        }
    }
}
