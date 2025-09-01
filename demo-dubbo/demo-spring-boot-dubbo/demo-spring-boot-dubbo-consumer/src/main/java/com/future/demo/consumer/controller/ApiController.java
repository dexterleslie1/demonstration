package com.future.demo.consumer.controller;

import com.future.common.http.ObjectResponse;
import com.future.common.http.ResponseUtils;
import com.future.demo.common.service.TestDubboPerfAssistService;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@Slf4j
@RestController
@RequestMapping("/api/v1/external/product")
public class ApiController {
    // 通过注解 @Reference 注入远程服务
    @Reference
    TestDubboPerfAssistService testDubboPerfAssistService;

    /**
     * 用于协助 Dubbo 性能测试
     *
     * @return
     */
    @GetMapping("testPerfAssist")
    public ObjectResponse<String> testPerfAssist() {
        return ResponseUtils.successObject(this.testDubboPerfAssistService.getRandomUuid());
    }
}
