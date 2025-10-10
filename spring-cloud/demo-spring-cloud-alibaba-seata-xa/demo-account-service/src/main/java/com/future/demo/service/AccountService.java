package com.future.demo.service;

import com.future.common.exception.BusinessException;
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
     * @throws BusinessException
     */
    public void deduct(Long userId, BigDecimal amount) throws BusinessException {
        int affectRow = accountMapper.deduct(userId, amount);
        if (affectRow == 0) {
            String errorMessage = String.format("用户 %d 余额不足导致扣款失败！", userId);
            throw new BusinessException(errorMessage);
        }
    }
}
