package com.future.demo.zuul.controller;

import com.future.demo.feign.HelloworldClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/zuul")
public class ApiController {
    @Autowired
    HelloworldClient helloworldClient;

    @GetMapping(value = "test1")
    public ResponseEntity<String> test1(@RequestParam(value = "param1", defaultValue = "") String param1) {
        return helloworldClient.test1(param1);
    }
}
