package com.future.demo.controller;

import com.alibaba.csp.sentinel.annotation.SentinelResource;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.future.common.constant.ErrorCodeConstant;
import com.future.common.http.ObjectResponse;
import com.future.common.http.ResponseUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/sentinel")
public class ApiController {

    @GetMapping("test1")
    @SentinelResource(value = "myTest1", blockHandler = "blockHandler")
    public ObjectResponse<String> test1() {
        return ResponseUtils.successObject(UUID.randomUUID().toString());
    }

    public ObjectResponse<String> blockHandler(BlockException ex) {
        return ResponseUtils.failObject(ErrorCodeConstant.ErrorCodeCommon, "block了");
    }
}
