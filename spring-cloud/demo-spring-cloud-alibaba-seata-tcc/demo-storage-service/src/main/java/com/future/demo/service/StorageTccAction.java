package com.future.demo.service;

import com.future.common.exception.BusinessException;
import io.seata.rm.tcc.api.BusinessActionContext;
import io.seata.rm.tcc.api.BusinessActionContextParameter;
import io.seata.rm.tcc.api.LocalTCC;
import io.seata.rm.tcc.api.TwoPhaseBusinessAction;

@LocalTCC
public interface StorageTccAction {
    /**
     * @param productId
     * @param count
     * @throws BusinessException
     */
    @TwoPhaseBusinessAction(
            name = "deduct",
            commitMethod = "confirm",
            rollbackMethod = "cancel",
            // 启用tcc防护（避免幂等、空回滚、悬挂）
            useTCCFence = true)
    void deduct(@BusinessActionContextParameter(paramName = "productId") Long productId,
                @BusinessActionContextParameter(paramName = "count") int count) throws BusinessException;

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
