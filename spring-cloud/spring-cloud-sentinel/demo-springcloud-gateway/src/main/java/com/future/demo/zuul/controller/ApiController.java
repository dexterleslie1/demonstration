package com.future.demo.zuul.controller;

import com.alibaba.csp.sentinel.annotation.SentinelResource;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.future.common.http.ObjectResponse;
import com.future.demo.zuul.service.CommonService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/api/v1")
public class ApiController {
    @Resource
    CommonService commonService;

    @GetMapping(value = "test1")
    public ResponseEntity<String> test1(@RequestParam(value = "sleepInSeconds", defaultValue = "0") int sleepInSeconds) throws InterruptedException {
        if (sleepInSeconds > 0) {
            TimeUnit.SECONDS.sleep(sleepInSeconds);
        }
        this.commonService.test1();
        return ResponseEntity.ok("/api/v1/test1 " + UUID.randomUUID());
    }

    @GetMapping(value = "test2")
    public ResponseEntity<String> test2(@RequestParam(value = "flag", defaultValue = "") String flag) {
        if ("exception".equals(flag)) {
            throw new RuntimeException("预期异常");
        }
        this.commonService.test1();
        return ResponseEntity.ok("/api/v1/test2 " + UUID.randomUUID());
    }

    @GetMapping(value = "test3")
    @SentinelResource(value = "test3", blockHandler = "blockHandler", fallback = "fallback")
    public ObjectResponse<String> test3(@RequestParam(value = "flag", required = false) String flag,
                                        @RequestParam(value = "p2", required = false) String p2) {
        if ("exception".equals(flag)) {
            throw new RuntimeException("预期异常");
        }
        ObjectResponse<String> response = new ObjectResponse<>();
        response.setData("/api/v1/test3 " + UUID.randomUUID());
        return response;
    }

    public ObjectResponse<String> blockHandler(@RequestParam(value = "flag", defaultValue = "") String flag, BlockException ex) {
        ObjectResponse<String> response = new ObjectResponse<>();
        response.setData("被限流了");
        return response;
    }

    public ObjectResponse<String> fallback(@RequestParam(value = "flag", defaultValue = "") String flag, Throwable ex) {
        ObjectResponse<String> response = new ObjectResponse<>();
        response.setData("服务降级了");
        return response;
    }
}
