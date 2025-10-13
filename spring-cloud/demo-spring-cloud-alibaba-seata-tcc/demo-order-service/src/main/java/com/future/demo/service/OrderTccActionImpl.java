package com.future.demo.service;

import com.future.common.exception.BusinessException;
import com.future.common.feign.FeignUtil;
import com.future.common.http.ObjectResponse;
import com.future.demo.entity.Order;
import com.future.demo.feign.AccountClient;
import com.future.demo.feign.StorageClient;
import com.future.demo.mapper.OrderMapper;
import io.seata.core.context.RootContext;
import io.seata.rm.tcc.api.BusinessActionContext;
import io.seata.rm.tcc.api.BusinessActionContextParameter;
import io.seata.rm.tcc.api.BusinessActionContextUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

@Service
@Slf4j
public class OrderTccActionImpl implements OrderTccAction {

    @Resource
    OrderMapper orderMapper;
    @Resource
    StorageClient storageClient;
    @Resource
    AccountClient accountClient;

    @Override
    // Try 方法需保证自身的本地事务实现，如通过 @Transactional 注解修饰。
    // 因为当前分支事务的 Try 操作出现异常，则回滚全局事务，触发其他分支（不包括当前 Try 操作异常的分支）事务的 Cancel 方法；
    @Transactional(rollbackFor = Exception.class)
    public Long createOrder(@BusinessActionContextParameter(paramName = "order") Order order,
                            boolean throwExceptionWhenDeductBalance) throws BusinessException {
        // 获取 Seata 当前全局事务 xid
        String xid = RootContext.getXID();
        try {
            if (log.isDebugEnabled()) {
                log.debug("创建订单，XID：{}", xid);
            }

            order.setStatus(0);
            int count = this.orderMapper.insert(order);
            // 插入订单后，将生成的 orderId 存入上下文，在 confirm 或者 cancel 中能够引用
            BusinessActionContextUtil.addContext("orderId", order.getId());
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
                response = this.accountClient.deduct(order.getUserId(), order.getMoney(), throwExceptionWhenDeductBalance);
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

    @Override
    public boolean confirm(BusinessActionContext context) {
        Long orderId = context.getActionContext("orderId", Long.class);
        if (orderId != null) {
            // 更新 order 状态为 status=2
            Order order = orderMapper.findById(orderId);
            order.setStatus(2);
            orderMapper.update(order);
        }
        return true;
    }

    @Override
    public boolean cancel(BusinessActionContext context) {
        Long orderId = context.getActionContext("orderId", Long.class);
        if (orderId != null) {
            // 删除之前的 order
            orderMapper.deleteById(orderId);
        }
        return true;
    }
}
