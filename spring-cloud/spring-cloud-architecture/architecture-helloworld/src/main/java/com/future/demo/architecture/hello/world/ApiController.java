package com.future.demo.architecture.hello.world;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class ApiController {

    @GetMapping(value = "/api/v1/sayHello", produces = MediaType.TEXT_PLAIN_VALUE)
    public String sayHello(@RequestParam(value = "name", defaultValue = "") String name) {
        return "Hello " + name + "!!(Zuul)";
    }

    @GetMapping(value = "/api/v1/test1", produces = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity<String> test1(@RequestHeader(value = "contextUserId", required = false) Long contextUserId,
                                        // NOTE: 用于测试使用query params方式注入上下文参数报错情况
                                        @RequestParam(value = "page", required = false) int page) {
        return ResponseEntity.ok("成功调用helloworld test1接口，注入上下文参数contextUserId=" + contextUserId);
    }

    @PostMapping(value = "/api/v1/test2", produces = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity<String> test2(@RequestParam(value = "param1", defaultValue = "") String param1) {
        return ResponseEntity.ok("你的请求参数param1=" + param1);
    }
}
