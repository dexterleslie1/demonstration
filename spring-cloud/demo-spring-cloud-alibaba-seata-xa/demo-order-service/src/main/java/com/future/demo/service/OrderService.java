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
     * @param throwExceptionWhenDeductBalance 扣减余额时是否抛出异常以模拟余额扣减失败
     * @throws BusinessException
     */
    @GlobalTransactional(name = "order-create", rollbackFor = Exception.class)
    public Long createOrder(Order order, boolean throwExceptionWhenDeductBalance) throws BusinessException {
        // 获取 Seata 当前全局事务 xid
        String xid = RootContext.getXID();
        try {
            if (log.isDebugEnabled()) {
                log.debug("创建订单，XID：{}", xid);
            }

            order.setStatus(1);
            int count = this.orderMapper.insert(order);
            if (count > 0) {
                // 注意：由于 MySQL XA 协议机制，在这里根据 id 查询订单会返回 null，所以不适宜在这里根据订单 id 查询订单信息
                // order = this.orderMapper.findById(order.getId());
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
                response = this.accountClient.deduct(order.getUserId(), order.getMoney(), throwExceptionWhenDeductBalance);
                FeignUtil.throwBizExceptionIfResponseFailed(response);
                if (log.isDebugEnabled()) {
                    log.debug("账户余额扣减成功，订单信息：{}", order);
                }

                // 注意：由于 MySQL XA 协议机制，在这里更新订单会等待 insert 排他锁直到超时，所以不适宜在这里更新订单信息
                /*// 更新订单状态
                order.setStatus(1);
                count = this.orderMapper.update(order);
                if (count > 0) {
                    log.debug("订单状态更新成功，订单信息：{}", order);
                } else {
                    log.debug("订单状态更新失败，订单信息：{}", order);
                }*/

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
