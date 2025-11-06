package com.future.demo;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.security.Principal;

@Controller
public class DemoController {

    @GetMapping(value = "/login")
    public String login(Model model) {
        model.addAttribute("var1", "Controller设置的值");
        return "login";
    }

    @RequestMapping(value = "/")
    public String index(Model model, Principal principal) {
        model.addAttribute("username", principal.getName());
        return "welcome";
    }

    @RequestMapping(value = "/welcome")
    public String welcome(Model model, Authentication authentication) {
        model.addAttribute("username", authentication.getName());
        return "welcome";
    }
}
