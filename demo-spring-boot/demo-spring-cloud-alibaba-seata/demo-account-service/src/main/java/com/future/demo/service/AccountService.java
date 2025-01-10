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

    public void deduct(Long userId, BigDecimal amount) throws BusinessException {
        int count = accountMapper.deduct(userId, amount);
        if (count <= 0) {
            throw new BusinessException("扣款失败");
        }

        boolean b = true;
        if (b) {
            throw new BusinessException("扣款失败");
        }
    }
}
