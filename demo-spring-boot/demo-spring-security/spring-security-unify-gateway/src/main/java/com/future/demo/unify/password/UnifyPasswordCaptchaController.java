package com.future.demo.unify.password;

import com.future.common.http.ObjectResponse;
import com.future.common.http.ResponseUtils;
import com.pig4cloud.captcha.ArithmeticCaptcha;
import lombok.extern.slf4j.Slf4j;
import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/v1/auth")
public class UnifyPasswordCaptchaController {
    final static Integer DEFAULT_IMAGE_WIDTH = 100;
    final static Integer DEFAULT_IMAGE_HEIGHT = 40;

    @Autowired
    CacheManager cacheManager;
    Cache cachePasswordLoginCaptcha;

    @PostConstruct
    public void init1() {
        this.cachePasswordLoginCaptcha = this.cacheManager.getCache("cachePasswordLoginCaptcha");
    }

    /**
     * 获取验证码
     *
     */
    @GetMapping("getCaptcha")
    public ObjectResponse<Map<String, String>> get() {
        String clientId = UUID.randomUUID().toString();

        ArithmeticCaptcha captcha = new ArithmeticCaptcha(DEFAULT_IMAGE_WIDTH, DEFAULT_IMAGE_HEIGHT);
        String result = "111111";
        Element element = new Element(clientId, result);
        element.setTimeToLive(180);
        cachePasswordLoginCaptcha.put(element);

        if (log.isDebugEnabled()) {
            log.debug("clientId=" + clientId + "验证码：" + result);
        }

        return ResponseUtils.successObject(new HashMap<String, String>() {{
            put("clientId" , clientId);
            put("image" , captcha.toBase64());
        }});
    }
}
