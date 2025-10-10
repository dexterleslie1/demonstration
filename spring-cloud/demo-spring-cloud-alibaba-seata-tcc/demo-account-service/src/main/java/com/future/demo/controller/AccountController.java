package com.future.demo.controller;

import com.future.common.exception.BusinessException;
import com.future.common.http.ObjectResponse;
import com.future.common.http.ResponseUtils;
import com.future.demo.mapper.AccountMapper;
import com.future.demo.service.AccountTccService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.math.BigDecimal;

@RestController
public class AccountController {
    @Resource
    AccountTccService accountTccService;
    @Autowired
    AccountMapper accountMapper;

    @PutMapping("/account/deduct")
    ObjectResponse<String> deduct(@RequestParam(value = "accountId") Long accountId,
                                  @RequestParam(value = "amount") BigDecimal amount) throws BusinessException {
        accountTccService.deduct(accountId, amount);
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
}
