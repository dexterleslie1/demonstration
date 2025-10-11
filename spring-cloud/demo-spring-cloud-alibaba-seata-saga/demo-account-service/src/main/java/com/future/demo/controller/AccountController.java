package com.future.demo.controller;

import com.future.common.exception.BusinessException;
import com.future.common.http.ObjectResponse;
import com.future.demo.service.AccountService;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.math.BigDecimal;

@RestController
public class AccountController {
    @Resource
    AccountService accountService;

    @PutMapping("/account/deduct")
    ObjectResponse<String> deduct(@RequestParam(value = "accountId") Long accountId,
                                  @RequestParam(value = "amount") BigDecimal amount,
                                  @RequestParam(value = "throwBusinessException") boolean throwBusinessException) throws BusinessException {
        this.accountService.deduct(accountId, amount, throwBusinessException);
        ObjectResponse<String> response = new ObjectResponse<>();
        response.setData("扣款成功");
        return response;
    }
}
