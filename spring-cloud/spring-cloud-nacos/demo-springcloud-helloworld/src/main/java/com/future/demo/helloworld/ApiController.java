package com.future.demo.helloworld;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
// nacos配置修改后会自动刷新
@RefreshScope
public class ApiController {
    @Value("${server.port}")
    private int serverPort;
    @Value("${my.config}")
    private String myConfig;

    @GetMapping(value = "/api/v1/test1")
    public ResponseEntity<String> test1(@RequestParam(value = "param1", defaultValue = "") String param1) {
        return ResponseEntity.ok("你的请求参数param1=" + param1 + "，myConfig配置值:" + myConfig + "，端口: " + serverPort);
    }
}
