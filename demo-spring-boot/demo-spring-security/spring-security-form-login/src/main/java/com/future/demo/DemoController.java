package com.future.demo;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.security.Principal;

@Controller
public class DemoController {

    @RequestMapping(value = "/login", method = RequestMethod.GET)
    public String login(Model model, String error, String logout) {
        model.addAttribute("var1", "Controller设置的值");
        return "login";
    }

    @RequestMapping(value = "/")
    public String index(Model model, Principal principal) {
        model.addAttribute("username", principal.getName());
        return "welcome";
    }

    @RequestMapping(value = "/welcome")
    public String welcome(Model model, Principal principal) {
        model.addAttribute("username", principal.getName());
        return "welcome";
    }
}
