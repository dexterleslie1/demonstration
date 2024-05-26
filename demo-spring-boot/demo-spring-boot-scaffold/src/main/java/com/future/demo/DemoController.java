package com.future.demo;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 参考模板controller
 */
@RestController
@RequestMapping(value="/api/v1/demo")
@Slf4j
public class DemoController {
    /**
     * 测试接口
     * @return
     */
    @GetMapping(value="test1")
    public ResponseEntity<String> test1(){
        log.debug("Api for testing is called.");
        return ResponseEntity.ok("Hello ....");
    }
}
