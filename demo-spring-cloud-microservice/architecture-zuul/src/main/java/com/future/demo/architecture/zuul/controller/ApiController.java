package com.future.demo.architecture.zuul.controller;

import com.future.demo.architecture.common.feign.HelloworldClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/zuul")
public class ApiController {
    @Autowired
    HelloworldClient helloworldClient;

    @GetMapping(value = "test1", produces = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity<String> test1() {
        return helloworldClient.test1();
    }

}
