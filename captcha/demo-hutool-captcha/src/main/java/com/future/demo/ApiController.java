package com.future.demo;

import cn.hutool.captcha.CaptchaUtil;
import cn.hutool.captcha.LineCaptcha;
import cn.hutool.captcha.generator.MathGenerator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;

@RestController
@RequestMapping(value = "/api/v1/captcha")
@Slf4j
public class ApiController {
    /**
     * 生成普通字符验证码
     */
    @GetMapping("/getImageCode")
    public void getImageCode(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("image/png");
        response.setHeader("Pragma", "No-cache");
        response.setHeader("Cache-Control", "no-cache");
        response.setDateHeader("Expires", 0);

        // 生成普通验证码
        LineCaptcha captcha = CaptchaUtil.createLineCaptcha(200, 100, 4, 20);

        // 获取验证码字符
        String code = captcha.getCode();
        if (log.isDebugEnabled())
            // 输出示例：字符验证码：tjE4
            log.debug("字符验证码：{}", code);

        request.getSession().setAttribute("captcha", code);

        try (OutputStream out = response.getOutputStream()) {
            captcha.write(out);
        }
    }

    /**
     * 生成算术运算验证码
     */
    @GetMapping("/getImageMath")
    public void getImageMath(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("image/png");
        response.setHeader("Pragma", "No-cache");
        response.setHeader("Cache-Control", "no-cache");
        response.setDateHeader("Expires", 0);

        // 生成普通验证码
        LineCaptcha captcha = CaptchaUtil.createLineCaptcha(200, 100, 4, 20);
        // 设置算术生成器
        MathGenerator generator = new MathGenerator(1);
        captcha.setGenerator(generator);
        // 生成图片
        captcha.createCode();

        // 获取验证码算术运算式子
        String code = captcha.getCode();
        if (log.isDebugEnabled())
            // 输出示例：算术运算式子：5-5=
            log.debug("算术运算式子：{}", code);

        request.getSession().setAttribute("captcha", code);

        try (OutputStream out = response.getOutputStream()) {
            captcha.write(out);
        }
    }
}
