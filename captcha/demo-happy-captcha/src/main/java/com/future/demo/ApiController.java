package com.future.demo;

import com.ramostear.captcha.HappyCaptcha;
import com.ramostear.captcha.support.CaptchaStyle;
import com.ramostear.captcha.support.CaptchaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@RestController
@RequestMapping(value = "/api/v1/captcha")
public class ApiController {
    /**
     * 数字、大小写字母随机组合
     *
     * @param request
     * @param response
     */
    @GetMapping("/default")
    public void captchaDefault(HttpServletRequest request, HttpServletResponse response) {
        HappyCaptcha.require(request, response).build().finish();
    }

    /**
     * 加、减、乘算数运算表达式
     *
     * @param request
     * @param response
     */
    @GetMapping("/arithmetic")
    public void captchaArithmetic(HttpServletRequest request, HttpServletResponse response) {
        HappyCaptcha.require(request, response).type(CaptchaType.ARITHMETIC).build().finish();
    }

    /**
     * 中文简体加、减、乘算数运算表达式描述
     *
     * @param request
     * @param response
     */
    @GetMapping("/arithmeticzh")
    public void captchaArithmeticzh(HttpServletRequest request, HttpServletResponse response) {
        HappyCaptcha.require(request, response).style(CaptchaStyle.ANIM).type(CaptchaType.ARITHMETIC_ZH).build().finish();
    }
}
