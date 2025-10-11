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
     * @param throwBusinessException 是否抛出扣款失败业务异常
     * @throws BusinessException
     */
    public void deduct(Long userId, BigDecimal amount, boolean throwBusinessException) throws BusinessException {
        int count = accountMapper.deduct(userId, amount);
        if (count <= 0) {
            throw new BusinessException("扣款失败");
        }

        if (throwBusinessException) {
            throw new BusinessException("扣款失败");
        }
    }
}
