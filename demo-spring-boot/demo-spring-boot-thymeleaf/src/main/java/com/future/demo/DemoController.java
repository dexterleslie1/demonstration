package com.future.demo;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * @author Dexterleslie.Chan
 */
@Controller
public class DemoController {
    @RequestMapping(value = "/", method = RequestMethod.GET)
    public String index(Model model) {
        // 为 include hello-suffix页面变量设置值
        model.addAttribute("value1", "value11");
        return "index";
    }

    /**
     * 演示redirect到另外一个thymeleaf url
     *
     * @return
     */
    @GetMapping(value = "/redirect")
    public String redirect() {
        return "redirect:/hello-suffix";
    }

    /**
     *
     * @return
     */
    @GetMapping(value = "/accessHttpServletRequest")
    public String accessHttpServletRequest() {
        return "accessHttpServletRequest";
    }
}
