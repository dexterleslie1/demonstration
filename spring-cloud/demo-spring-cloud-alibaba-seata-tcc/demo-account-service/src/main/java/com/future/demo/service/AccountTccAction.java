package com.future.demo.service;

import com.future.common.exception.BusinessException;
import io.seata.rm.tcc.api.BusinessActionContext;
import io.seata.rm.tcc.api.BusinessActionContextParameter;
import io.seata.rm.tcc.api.LocalTCC;
import io.seata.rm.tcc.api.TwoPhaseBusinessAction;

import java.math.BigDecimal;

@LocalTCC
public interface AccountTccAction {
    /**
     * @param userId
     * @param amount
     * @param throwExceptionWhenDeductBalance 扣减余额时是否抛出异常以模拟余额扣减失败
     * @throws BusinessException
     */
    @TwoPhaseBusinessAction(
            name = "deduct",
            commitMethod = "confirm",
            rollbackMethod = "cancel",
            // 启用tcc防护（避免幂等、空回滚、悬挂）
            useTCCFence = true)
    void deduct(@BusinessActionContextParameter(paramName = "userId") Long userId,
                @BusinessActionContextParameter(paramName = "amount") BigDecimal amount,
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
