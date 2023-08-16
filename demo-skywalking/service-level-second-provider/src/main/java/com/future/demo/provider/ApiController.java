package com.future.demo.provider;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
public class ApiController {

    @GetMapping(value = "/api/v1/test1")
    public ResponseEntity<String> test1(@RequestParam(value = "param1", defaultValue = "") String param1) {
        log.debug("准备调用业务逻辑");
        return ResponseEntity.ok("成功调用/api/v1/test1接口，[param1=" + param1 + "]");
    }

    @GetMapping(value = "/api/v1/test2")
    public ResponseEntity<String> test2(@RequestParam(value = "param1", defaultValue = "") String param1) {
        log.debug("准备调用业务逻辑");
        double result = 1/0;
        return ResponseEntity.ok("成功调用/api/v1/test2接口，[param1=" + param1 + "]");
    }

    @GetMapping(value = "/api/v1/test3")
    public ResponseEntity<String> test3() throws InterruptedException {
        log.debug("准备调用业务逻辑");
        Thread.sleep(1000);
        return ResponseEntity.ok("成功调用/api/v1/test3接口");
    }

}
