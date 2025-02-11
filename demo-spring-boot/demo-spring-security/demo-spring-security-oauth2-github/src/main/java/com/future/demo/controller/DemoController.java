package com.future.demo.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
public class DemoController {
    @GetMapping("/")
    public String index(Principal principal) {
        return "Hello, " + principal.getName();
    }
}
