package com.future.demo.zuul.controller;

import com.future.demo.feign.HelloworldClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/zuul")
@RefreshScope
public class ApiController {
    @Value("${my.config}")
    private String myConfig;

    @Autowired
    HelloworldClient helloworldClient;

    @GetMapping(value = "test1")
    public ResponseEntity<String> test1(@RequestParam(value = "param1", defaultValue = "") String param1) {
        return helloworldClient.test1(param1);
    }

    @GetMapping(value = "test2")
    public ResponseEntity<String> test2(@RequestParam(value = "param1", defaultValue = "") String param1) {
        return ResponseEntity.ok("my.config=" + myConfig);
    }
}
