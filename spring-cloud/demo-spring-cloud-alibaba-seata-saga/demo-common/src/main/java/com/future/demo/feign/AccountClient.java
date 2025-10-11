package com.future.demo.feign;

import com.future.common.exception.BusinessException;
import com.future.common.http.ObjectResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;

@FeignClient("demo-account-service")
public interface AccountClient {
    @PutMapping("/account/deduct")
    ObjectResponse<String> deduct(@RequestParam(value = "accountId") Long accountId,
                                  @RequestParam(value = "amount") BigDecimal amount,
                                  @RequestParam(value = "throwBusinessException") boolean throwBusinessException) throws BusinessException;
}
