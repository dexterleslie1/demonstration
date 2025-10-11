package com.future.demo.service;

import com.future.common.exception.BusinessException;
import com.future.demo.entity.Account;
import com.future.demo.entity.AccountFreeze;
import com.future.demo.entity.TccTransactionState;
import com.future.demo.mapper.AccountFreezeMapper;
import com.future.demo.mapper.AccountMapper;
import com.future.demo.util.Util;
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
public class AccountTccServiceImpl implements AccountTccService {

    @Autowired
    AccountMapper accountMapper;
    @Autowired
    AccountFreezeMapper accountFreezeMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deduct(@BusinessActionContextParameter(paramName = "userId") Long userId,
                       @BusinessActionContextParameter(paramName = "amount") BigDecimal amount,
                       boolean throwExceptionWhenDeductBalance) throws BusinessException {
        // 冻结金额和事务状态记录
        String xid = RootContext.getXID();

        // 避免业务悬挂
        AccountFreeze accountFreeze = accountFreezeMapper.getByXid(xid);
        if (accountFreeze != null) {
            return;
        }

        accountFreeze = new AccountFreeze();
        accountFreeze.setXid(xid);
        accountFreeze.setUserId(userId);
        accountFreeze.setFreezeMoney(amount.intValue());
        accountFreeze.setState(TccTransactionState.Try);
        int affectRow = accountFreezeMapper.insert(accountFreeze);
        // 幂等处理：事务之前没有尝试过锁定资源才扣减余额
        if (affectRow == 1) {
            // 扣减余额
            affectRow = accountMapper.deduct(userId, amount);
            if (affectRow == 0) {
                String errorMessage = String.format("用户 %d 余额不足导致扣款失败！", userId);
                throw new BusinessException(errorMessage);
            }
        }

        if (throwExceptionWhenDeductBalance) {
            throw new BusinessException("预期扣减余额失败！");
        }
    }

    @Override
    public boolean confirm(BusinessActionContext context) {
        String xid = context.getXid();
        // 删除冻结金额和事务状态记录
        int affectRow = accountFreezeMapper.delete(xid);
        return affectRow == 1;
    }

    @Override
    public boolean cancel(BusinessActionContext context) {
        String xid = context.getXid();
        AccountFreeze accountFreeze = accountFreezeMapper.getByXid(xid);

        // 空回滚判断：deduct 中插入 accountFreeze 还没有执行就 Cancel 了
        if (accountFreeze == null) {
            Long userId = context.getActionContext("userId", Long.class);
            accountFreeze = new AccountFreeze();
            accountFreeze.setXid(xid);
            accountFreeze.setUserId(userId);
            accountFreeze.setFreezeMoney(0);
            accountFreeze.setState(TccTransactionState.Cancel);
            int affectRow = accountFreezeMapper.insert(accountFreeze);
            return affectRow == 1;
        }

        // 幂等判断：之前已经 Cancel 过
        if (accountFreeze.getState() == TccTransactionState.Cancel) {
            return true;
        }

        Long userId = accountFreeze.getUserId();
        int amount = accountFreeze.getFreezeMoney();

        // 恢复余额
        accountMapper.refund(userId, BigDecimal.valueOf(amount));

        // 将冻结金额修改为 0 ，事务状态修改为 Cancel
        accountFreeze.setFreezeMoney(0);
        accountFreeze.setState(TccTransactionState.Cancel);
        int affectRow = accountFreezeMapper.update(accountFreeze);
        return affectRow == 1;
    }

    /**
     * 准备性能测试数据
     *
     * @return
     */
    public void preparePerfTestDatum() {
        for (int i = 0; i < Util.PerfTestDatumUserIdTotalCount; i++) {
            long userId = Util.PerfTestDatumUserIdStart + i;
            Account account = new Account();
            account.setUserId(userId);
            account.setTotal(BigDecimal.valueOf(Util.PerfTestDatumUserBalance));
            account.setUsed(BigDecimal.valueOf(0));
            account.setResidue(account.getTotal());
            accountMapper.deleteByUserId(userId);
            accountMapper.insert(account);
        }
    }
}
