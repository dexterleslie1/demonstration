package com.future.demo.controller;

import com.future.common.http.ObjectResponse;
import com.future.common.http.ResponseUtils;
import com.future.demo.mapper.TestMapper;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.Date;

@RestController
@RequestMapping("/api/v1")
public class TestController {
    @Resource
    TestMapper testMapper;

    @GetMapping("testSelectSleep")
    public ObjectResponse<String> selectSleep() {
        this.testMapper.selectSleep();
        return ResponseUtils.successObject("当前时间为：" + new Date());
    }
}
