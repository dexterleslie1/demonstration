package com.future.demo.service;

import com.future.common.exception.BusinessException;
import com.future.demo.entity.Order;
import io.seata.rm.tcc.api.BusinessActionContext;
import io.seata.rm.tcc.api.BusinessActionContextParameter;
import io.seata.rm.tcc.api.LocalTCC;
import io.seata.rm.tcc.api.TwoPhaseBusinessAction;

@LocalTCC
public interface OrderTccAction {
    /**
     * @param order
     * @param throwExceptionWhenDeductBalance 扣减余额时是否抛出异常以模拟余额扣减失败
     * @throws BusinessException
     */
    @TwoPhaseBusinessAction(
            name = "createOrder",
            commitMethod = "confirm",
            rollbackMethod = "cancel",
            // 启用tcc防护（避免幂等、空回滚、悬挂）
            useTCCFence = true)
    Long createOrder(@BusinessActionContextParameter(paramName = "order") Order order,
                     boolean throwExceptionWhenDeductBalance) throws BusinessException;

    /**
     * 提交
     *
     * @param context
     * @return
     */
    boolean confirm(BusinessActionContext context);

    /**
     * 回滚
     *
     * @param context
     * @return
     */
    boolean cancel(BusinessActionContext context);
}
