package com.future.demo.controller;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class DemoController {
    @GetMapping("/")
    public String index() {
        return "index";
    }

    /**
     * 登录后才能够访问的页面
     *
     * @param authentication
     * @param model
     * @return
     */
    @GetMapping("/welcome")
    public String welcome(Authentication authentication, Model model) {
        model.addAttribute("username", authentication.getName());
        return "welcome";
    }
}
