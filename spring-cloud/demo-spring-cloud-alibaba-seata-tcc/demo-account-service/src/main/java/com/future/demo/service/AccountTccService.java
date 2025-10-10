package com.future.demo.service;

import com.future.common.exception.BusinessException;
import io.seata.rm.tcc.api.BusinessActionContext;
import io.seata.rm.tcc.api.BusinessActionContextParameter;
import io.seata.rm.tcc.api.LocalTCC;
import io.seata.rm.tcc.api.TwoPhaseBusinessAction;

import java.math.BigDecimal;

@LocalTCC
public interface AccountTccService {
    /**
     * @param userId
     * @param amount
     * @throws BusinessException
     */
    @TwoPhaseBusinessAction(name = "deduct", commitMethod = "confirm", rollbackMethod = "cancel")
    void deduct(@BusinessActionContextParameter(paramName = "userId") Long userId,
                @BusinessActionContextParameter(paramName = "amount") BigDecimal amount) throws BusinessException;

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
