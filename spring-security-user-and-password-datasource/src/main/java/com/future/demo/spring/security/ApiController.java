package com.future.demo.spring.security;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/api/v1")
public class ApiController {
    @GetMapping("test1")
    public String test1() {
        return "Hello xxx";
    }
}
