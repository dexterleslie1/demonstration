package com.future.demo;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/")
public class ApiController {
    @GetMapping("")
    public String test1() {
        return "Hello xxx";
    }
}
