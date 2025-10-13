package com.future.demo.service;

import com.future.common.exception.BusinessException;
import com.future.demo.entity.AccountFreeze;
import com.future.demo.mapper.AccountFreezeMapper;
import com.future.demo.mapper.AccountMapper;
import io.seata.core.context.RootContext;
import io.seata.rm.tcc.api.BusinessActionContext;
import io.seata.rm.tcc.api.BusinessActionContextParameter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@Slf4j
public class AccountTccActionImpl implements AccountTccAction {

    @Autowired
    AccountMapper accountMapper;
    @Autowired
    AccountFreezeMapper accountFreezeMapper;

    @Override
    // Try 方法需保证自身的本地事务实现，如通过 @Transactional 注解修饰。
    // 因为当前分支事务的 Try 操作出现异常，则回滚全局事务，触发其他分支（不包括当前 Try 操作异常的分支）事务的 Cancel 方法；
    @Transactional(rollbackFor = Exception.class)
    public void deduct(@BusinessActionContextParameter(paramName = "userId") Long userId,
                       @BusinessActionContextParameter(paramName = "amount") BigDecimal amount,
                       boolean throwExceptionWhenDeductBalance) throws BusinessException {
        // 冻结金额
        String xid = RootContext.getXID();
        AccountFreeze accountFreeze = new AccountFreeze();
        accountFreeze.setXid(xid);
        accountFreeze.setUserId(userId);
        accountFreeze.setFreezeMoney(amount.intValue());
        accountFreezeMapper.insert(accountFreeze);
        // 扣减余额
        int affectRow = accountMapper.deduct(userId, amount);
        if (affectRow == 0) {
            String errorMessage = String.format("用户 %d 余额不足导致扣款失败！", userId);
            throw new BusinessException(errorMessage);
        }

        if (throwExceptionWhenDeductBalance) {
            throw new BusinessException("预期扣减余额失败！");
        }
    }

    @Override
    public boolean confirm(BusinessActionContext context) {
        String xid = context.getXid();
        // 删除冻结金额
        int affectRow = accountFreezeMapper.delete(xid);
        return affectRow == 1;
    }

    @Override
    public boolean cancel(BusinessActionContext context) {
        String xid = context.getXid();
        AccountFreeze accountFreeze = accountFreezeMapper.getByXid(xid);

        Long userId = accountFreeze.getUserId();
        int amount = accountFreeze.getFreezeMoney();

        // 恢复余额
        accountMapper.refund(userId, BigDecimal.valueOf(amount));

        // 将冻结金额修改为 0
        accountFreeze.setFreezeMoney(0);
        int affectRow = accountFreezeMapper.update(accountFreeze);
        return affectRow == 1;
    }
}
