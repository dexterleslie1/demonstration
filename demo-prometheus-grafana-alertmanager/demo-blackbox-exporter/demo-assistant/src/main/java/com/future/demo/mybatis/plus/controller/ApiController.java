package com.future.demo.mybatis.plus.controller;

import com.future.common.exception.BusinessException;
import com.future.common.http.ObjectResponse;
import com.future.common.http.ResponseUtils;
import com.future.demo.mybatis.plus.service.AliveTestingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/alive")
public class ApiController {
    @Autowired
    AliveTestingService aliveTestingService;

    @GetMapping("check")
    public ObjectResponse<String> check(@RequestHeader(value = "mySecret", defaultValue = "") String mySecret) throws BusinessException {
        if (!mySecret.equals("xxxxx")) {
            throw new BusinessException("FAIL");
        }
        this.aliveTestingService.checkAlive();
        return ResponseUtils.successObject("UP");
    }
}
