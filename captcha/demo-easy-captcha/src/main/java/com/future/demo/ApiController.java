package com.future.demo;

import com.wf.captcha.ArithmeticCaptcha;
import com.wf.captcha.ChineseCaptcha;
import com.wf.captcha.GifCaptcha;
import com.wf.captcha.SpecCaptcha;
import com.wf.captcha.base.Captcha;
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
        SpecCaptcha captcha = new SpecCaptcha(130, 48, 5);

        // 获取验证码字符
        String code = captcha.text();
        if (log.isDebugEnabled())
            // 输出示例：字符验证码：tEtaR
            log.debug("字符验证码：{}", code);

        request.getSession().setAttribute("captcha", code);

        try (OutputStream out = response.getOutputStream()) {
            captcha.out(out);
        }
    }

    /**
     * 生成算术验证码
     */
    @GetMapping("/getImageMath")
    public void getImageMath(HttpServletRequest request, HttpServletResponse response) throws IOException {
        // 设置响应类型
        response.setContentType("image/gif");
        response.setHeader("Pragma", "No-cache");
        response.setHeader("Cache-Control", "no-cache");
        response.setDateHeader("Expires", 0);

        // 生成算术验证码
        ArithmeticCaptcha captcha = new ArithmeticCaptcha(130, 48);
        captcha.setLen(2); // 几位数运算，默认是两位

        // 获取验证码的结果（计算结果）
        String result = captcha.text();
        if (log.isDebugEnabled())
            // 输出示例：算术验证码结果：3
            log.debug("算术验证码结果：{}", result);

        // 存储到Session
        request.getSession().setAttribute("captcha", result);

        // 输出图片流
        try (OutputStream out = response.getOutputStream()) {
            captcha.out(out);
        }
    }

    /**
     * 自定义验证码
     *
     * @param request
     * @param response
     */
    @GetMapping("/getImageCustomize")
    public void getImageCustomize(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("image/png");
        response.setHeader("Pragma", "No-cache");
        response.setHeader("Cache-Control", "no-cache");
        response.setDateHeader("Expires", 0);

        // 创建GIF验证码
        GifCaptcha captcha = new GifCaptcha(130, 48, 4);

        // 自定义设置
        captcha.setCharType(Captcha.TYPE_NUM_AND_UPPER); // 字符类型：数字+大写字母
        // captcha.setCharType(CharType.ONLY_NUM);    // 纯数字
        // captcha.setCharType(CharType.ONLY_CHAR);   // 纯字母
        // captcha.setCharType(CharType.ONLY_UPPER);  // 纯大写字母
        // captcha.setCharType(CharType.ONLY_LOWER);  // 纯小写字母

        // 获取验证码
        String code = captcha.text();
        request.getSession().setAttribute("captcha", code);

        try (OutputStream out = response.getOutputStream()) {
            captcha.out(out);
        }
    }

    /**
     * 中文验证码
     *
     * @param request
     * @param response
     * @throws IOException
     */
    @GetMapping("/getImageChinese")
    public void getImageChinese(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("image/png");
        response.setHeader("Pragma", "No-cache");
        response.setHeader("Cache-Control", "no-cache");
        response.setDateHeader("Expires", 0);

        ChineseCaptcha captcha = new ChineseCaptcha(130, 48);
        captcha.setLen(3); // 中文字符数量

        String chineseText = captcha.text();
        if (log.isDebugEnabled())
            // 输出示例：中文验证码：片极术
            log.debug("中文验证码：{}", chineseText);

        request.getSession().setAttribute("captcha", chineseText);

        try (OutputStream out = response.getOutputStream()) {
            captcha.out(out);
        }
    }
}
