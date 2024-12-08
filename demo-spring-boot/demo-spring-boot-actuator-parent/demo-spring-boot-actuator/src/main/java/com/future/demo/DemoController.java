package com.future.demo;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
public class DemoController {
    // 用于协助/actuator/logfile测试产生日志
    @GetMapping("/")
    public String index() {
        log.info("Hello World!");
        return "Hello World";
    }

    // 用于协助actuator优雅关闭
    @GetMapping("test1")
    public String test1() throws InterruptedException {
        Thread.sleep(10000);
        return "成功调用";
    }
}
