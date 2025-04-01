package com.future.demo.controller;

import com.future.common.http.ObjectResponse;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.security.Principal;

@Controller
@CrossOrigin(origins = "*",
        // 支持 ajax 跨域请求，允许携带 cookie
        // https://cloud.tencent.com/developer/article/1467263
        allowCredentials = "true")
public class DemoController {
    @GetMapping("/")
    public String index(Model model, Principal principal) {
        String username = "游客";
        if (principal != null) {
            username = principal.getName();
        }
        model.addAttribute("username", username);
        return "index";
    }

    @PostMapping("/transfer")
    @ResponseBody
    public ObjectResponse<String> transfer(@RequestParam(value = "targetAccount", defaultValue = "") String targetAccount,
                                           @RequestParam(value = "amount", defaultValue = "0") BigDecimal amount,
                                           Principal principal) {
        String message = principal.getName() + " 转出金额：" + amount.toString() + " 到 " + targetAccount;
        ObjectResponse<String> response = new ObjectResponse<>();
        response.setData(message);
        return response;
    }
}
