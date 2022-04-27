package com.future.demo.jquery.ajax;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
public class DemoNavController {
    @RequestMapping("/")
    public String index() {
        return "index";
    }
}
