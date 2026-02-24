package com.future.demo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class IndexController {
    @GetMapping("")
    public String index() {
        return "index";
    }

    @GetMapping("iframe-testing-support")
    public String iframeTestingSupport() {
        return "iframe-testing-support";
    }

    @GetMapping("button-evaluate-demo")
    public String buttonEvaluateDemo() {
        return "button-evaluate-demo";
    }
}
