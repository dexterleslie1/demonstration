package com.future.demo;

import com.google.code.kaptcha.Constants;
import com.google.code.kaptcha.impl.DefaultKaptcha;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.imageio.ImageIO;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;

@RestController
@RequestMapping(value = "/api/v1/captcha")
@Slf4j
public class ApiController {

    @Resource(name = "defaultKaptchaCode")
    DefaultKaptcha defaultKaptchaCode;
    @Resource(name = "defaultKaptchaMath")
    DefaultKaptcha defaultKaptchaMath;

    // 1. 获取验证码图片
    @GetMapping("/getImageCode")
    public void getImageCode(HttpServletRequest request, HttpServletResponse response) throws Exception {
        // 禁止缓存，确保每次都是新图片
        response.setDateHeader("Expires", 0);
        response.setHeader("Cache-Control", "no-store, no-cache, must-revalidate");
        response.setHeader("Pragma", "no-cache");
        response.setContentType("image/jpeg");

        // 生成验证码文本
        String capText = defaultKaptchaCode.createText();
        if (log.isDebugEnabled()) {
            // 输出示例：capText=1V60
            log.debug("capText={}", capText);
        }
        // 将验证码文本存入 Session， key 通常固定为 KAPTCHA_SESSION_KEY
        request.getSession().setAttribute(Constants.KAPTCHA_SESSION_KEY, capText);

        // 根据文本生成图片并写入响应流
        BufferedImage bi = defaultKaptchaCode.createImage(capText);
        try (ServletOutputStream out = response.getOutputStream()) {
            ImageIO.write(bi, "jpg", out);
            out.flush();
        }
    }

    @GetMapping("/getImageMath")
    public void getImageCodeMath(HttpServletRequest request, HttpServletResponse response) throws Exception {
        response.setDateHeader("Expires", 0);
        response.setHeader("Cache-Control", "no-store, no-cache, must-revalidate");
        response.setHeader("Pragma", "no-cache");
        response.setContentType("image/jpeg");

        String capTextOriginal = defaultKaptchaMath.createText();
        if (log.isDebugEnabled()) {
            // 输出示例：capText=1*6=?@6，其中 @6 是答案
            log.debug("capText={}", capTextOriginal);
        }

        // 获取算术运算的等式
        String capText = capTextOriginal.substring(0, capTextOriginal.lastIndexOf("@"));
        // 获取算术运算的答案
        String code = capTextOriginal.substring(capText.lastIndexOf("@") + 1);

        request.getSession().setAttribute(Constants.KAPTCHA_SESSION_KEY, code);

        BufferedImage bi = defaultKaptchaMath.createImage(capText);
        try (ServletOutputStream out = response.getOutputStream()) {
            ImageIO.write(bi, "jpg", out);
            out.flush();
        }
    }
}
