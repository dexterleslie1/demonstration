package com.future.demo.service;

import com.future.common.exception.BusinessException;
import com.future.demo.util.Util;
import com.future.demo.entity.Account;
import com.future.demo.mapper.AccountMapper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;

@Service
public class AccountService {
    @Resource
    AccountMapper accountMapper;

    /**
     * @param userId
     * @param amount
     * @param throwExceptionWhenDeductBalance 扣减余额时是否抛出异常以模拟余额扣减失败
     * @throws BusinessException
     */
    public void deduct(Long userId, BigDecimal amount, boolean throwExceptionWhenDeductBalance) throws BusinessException {
        int affectRow = accountMapper.deduct(userId, amount);
        if (affectRow == 0) {
            String errorMessage = String.format("用户 %d 余额不足导致扣款失败！", userId);
            throw new BusinessException(errorMessage);
        }

        if (throwExceptionWhenDeductBalance) {
            throw new BusinessException("预期扣减余额失败！");
        }
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
