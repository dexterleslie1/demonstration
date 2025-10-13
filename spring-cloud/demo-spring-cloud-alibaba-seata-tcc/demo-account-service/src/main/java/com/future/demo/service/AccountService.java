package com.future.demo.service;

import com.future.demo.entity.Account;
import com.future.demo.mapper.AccountFreezeMapper;
import com.future.demo.mapper.AccountMapper;
import com.future.demo.util.Util;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.math.BigDecimal;

@Service
public class AccountService {

    @Autowired
    AccountMapper accountMapper;
    @Autowired
    AccountFreezeMapper accountFreezeMapper;

    /**
     * 准备性能测试数据
     *
     * @return
     */
    @PostConstruct
    public void init() {
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
