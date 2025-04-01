package com.future.demo.controller;

import com.future.common.http.ObjectResponse;
import com.future.demo.feign.FeignClientProvider;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
public class DemoController {
    @Resource
    FeignClientProvider feignClientProvider;

    @GetMapping("test1")
    public ObjectResponse<String> test1(@RequestParam(value = "flag", defaultValue = "") String flag) throws Throwable {
        return this.feignClientProvider.test1(flag);
    }
}
