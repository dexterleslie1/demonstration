package com.future.demo.captcha;

import com.ramostear.captcha.HappyCaptcha;
import com.ramostear.captcha.support.CaptchaStyle;
import com.ramostear.captcha.support.CaptchaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@RestController
@RequestMapping(value="/api/v1/captcha")
public class ApiController {
    @GetMapping("/default")
    public void captchaDefault(HttpServletRequest request,HttpServletResponse response){
        HappyCaptcha.require(request,response).build().finish();
    }

    @GetMapping("/arithmetic")
    public void captchaArithmetic(HttpServletRequest request,HttpServletResponse response){
        HappyCaptcha.require(request,response).type(CaptchaType.ARITHMETIC).build().finish();
    }

    @GetMapping("/arithmeticzh")
    public void captchaArithmeticzh(HttpServletRequest request,HttpServletResponse response){
        HappyCaptcha.require(request,response).style(CaptchaStyle.ANIM).type(CaptchaType.ARITHMETIC_ZH).build().finish();
    }
}
