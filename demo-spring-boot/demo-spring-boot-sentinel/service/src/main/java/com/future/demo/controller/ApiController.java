package com.future.demo.controller;

import com.alibaba.csp.sentinel.annotation.SentinelResource;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.future.common.constant.ErrorCodeConstant;
import com.future.common.exception.BusinessException;
import com.future.common.http.ObjectResponse;
import com.future.common.http.ResponseUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/api/v1/sentinel")
public class ApiController {

    @GetMapping("test1")
    @SentinelResource(value = "myTest1",
            blockHandler = "blockHandler",
            fallback = "fallbackhandler"/*,
            exceptionsToIgnore = BusinessException.class*/)
    public ObjectResponse<String> test1(@RequestParam(value = "timeoutInMilliseconds", defaultValue = "0") int timeoutInMilliseconds) throws BusinessException, InterruptedException {
        if (timeoutInMilliseconds > 0) {
            TimeUnit.MILLISECONDS.sleep(timeoutInMilliseconds);
        }

        boolean b = true;
        if (b) {
            throw new BusinessException("xxx");
        }

        return ResponseUtils.successObject(UUID.randomUUID().toString());
    }

    public ObjectResponse<String> blockHandler(BlockException ex) {
        return ResponseUtils.failObject(ErrorCodeConstant.ErrorCodeCommon, "block了");
    }

    public ObjectResponse<String> fallbackHandler(Throwable ex) {
        return ResponseUtils.failObject(ErrorCodeConstant.ErrorCodeCommon, "fallback了");
    }
}
