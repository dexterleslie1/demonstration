package com.future.demo.controller;

import com.future.common.exception.BusinessException;
import com.future.common.http.ObjectResponse;
import com.future.common.http.ResponseUtils;
import com.future.demo.mapper.AccountMapper;
import com.future.demo.service.AccountService;
import com.future.demo.service.AccountTccAction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;

@RestController
public class AccountController {

    @Autowired
    AccountService accountService;
    @Autowired
    AccountTccAction accountTccAction;
    @Autowired
    AccountMapper accountMapper;

    @PutMapping("/account/deduct")
    ObjectResponse<String> deduct(@RequestParam(value = "accountId") Long accountId,
                                  @RequestParam(value = "amount") BigDecimal amount,
                                  @RequestParam(value = "throwExceptionWhenDeductBalance", defaultValue = "false") boolean throwExceptionWhenDeductBalance) throws BusinessException {
        accountTccAction.deduct(accountId, amount, throwExceptionWhenDeductBalance);
        return ResponseUtils.successObject("扣款成功");
    }

    /**
     * 协助测试重置数据
     *
     * @return
     */
    @GetMapping("/account/reset")
    ObjectResponse<String> reset() {
        accountMapper.reset();
        return ResponseUtils.successObject("重置成功");
    }


    /**
     * 准备性能测试数据
     *
     * @return
     */
    @GetMapping("/account/preparePerfTestDatum")
    ObjectResponse<String> preparePerfTestDatum() {
        accountService.preparePerfTestDatum();
        return ResponseUtils.successObject("准备成功");
    }
}
