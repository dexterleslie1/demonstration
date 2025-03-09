package com.future.demo;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ApiController {

    @GetMapping(value = "/api/v1/a/b/sayHello", produces = MediaType.TEXT_PLAIN_VALUE)
    public String sayHello() {
        return "Hello Zuul!";
    }
}
