package com.future.demo;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/nav")
public class NavController {
    @GetMapping("welcome")
    public String welcome() {
        return "welcome";
    }

    @RequestMapping("redirect")
    public String redirect() {
        return "redirect:welcome";
    }
}
