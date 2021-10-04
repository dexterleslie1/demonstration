package com.future.demo.unify.gateway.password;

import com.pig4cloud.captcha.ArithmeticCaptcha;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.util.FastByteArrayOutputStream;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
public class UsernamePasswordLoginCaptchaFilter extends OncePerRequestFilter {
    final static Integer DEFAULT_IMAGE_WIDTH = 100;
    final static Integer DEFAULT_IMAGE_HEIGHT = 40;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        ArithmeticCaptcha captcha = new ArithmeticCaptcha(DEFAULT_IMAGE_WIDTH, DEFAULT_IMAGE_HEIGHT);
        String result = "11111";

        log.info("验证码：" + result);

        // 转换流信息写出
        FastByteArrayOutputStream os = new FastByteArrayOutputStream();
        captcha.out(os);

        response.setStatus(HttpStatus.OK.value());
        response.setContentType(MediaType.IMAGE_JPEG_VALUE);
        byte [] bytes = os.toByteArray();
        response.setContentLength(bytes.length);
        response.getOutputStream().write(bytes);
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        String path = request.getServletPath();
        return !path.startsWith("/api/v1/captcha/get");
    }
}
