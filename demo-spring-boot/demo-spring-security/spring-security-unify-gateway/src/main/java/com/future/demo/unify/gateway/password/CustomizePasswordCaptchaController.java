package com.future.demo.unify.gateway.password;

import com.pig4cloud.captcha.ArithmeticCaptcha;
import lombok.extern.slf4j.Slf4j;
import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.util.Assert;
import org.springframework.util.FastByteArrayOutputStream;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
@RestController
@RequestMapping("/api/v1/password/captcha")
public class CustomizePasswordCaptchaController {
    final static Integer DEFAULT_IMAGE_WIDTH = 100;
    final static Integer DEFAULT_IMAGE_HEIGHT = 40;

    @Autowired
    CacheManager cacheManager;
    Cache cachePasswordLoginCaptcha;

    @PostConstruct
    public void init1() {
        this.cachePasswordLoginCaptcha = this.cacheManager.getCache("cachePasswordLoginCaptcha");
    }

    @GetMapping("get")
    public void get(@RequestParam(value = "clientId", defaultValue = "") String clientId,
                    HttpServletResponse response) throws IOException {
        Assert.isTrue(!StringUtils.isBlank(clientId), "没有指定clientId参数");

        ArithmeticCaptcha captcha = new ArithmeticCaptcha(DEFAULT_IMAGE_WIDTH, DEFAULT_IMAGE_HEIGHT);
        String result = "111111";
        Element element = new Element(clientId, result);
        element.setTimeToLive(180);
        cachePasswordLoginCaptcha.put(element);
        log.debug("clientId=" + clientId + "验证码：" + result);

        // 转换流信息写出
        FastByteArrayOutputStream os = new FastByteArrayOutputStream();
        captcha.out(os);

        response.setStatus(HttpStatus.OK.value());
        response.setContentType(MediaType.IMAGE_JPEG_VALUE);
        byte [] bytes = os.toByteArray();
        response.setContentLength(bytes.length);
        response.getOutputStream().write(bytes);
    }
}
