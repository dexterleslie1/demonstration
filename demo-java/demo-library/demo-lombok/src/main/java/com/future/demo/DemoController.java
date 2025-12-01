package com.future.demo;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
@Slf4j
public class DemoController {

    /**
     * 用于协助测试“找不到符号变量log”报错
     * @return
     */
    @GetMapping("test1")
    ResponseEntity<String> test1() {
        log.info("测试日志");
        return ResponseEntity.ok("成功调用");
    }
}
