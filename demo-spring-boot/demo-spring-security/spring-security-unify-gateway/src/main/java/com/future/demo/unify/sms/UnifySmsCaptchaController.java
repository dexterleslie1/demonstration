package com.future.demo.unify.sms;

import com.future.common.http.ObjectResponse;
import com.future.common.http.ResponseUtils;
import com.future.common.phone.PhoneUtil;
import lombok.extern.slf4j.Slf4j;
import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.PostConstruct;
import java.io.UnsupportedEncodingException;

@Slf4j
@RestController
@RequestMapping("/api/v1/auth")
public class UnifySmsCaptchaController {
    @Autowired
    CacheManager cacheManager;
    Cache cacheSmsCaptcha;

    @PostConstruct
    public void init1() {
        this.cacheSmsCaptcha = this.cacheManager.getCache("cacheSmsCaptcha");
    }

    @GetMapping("sendSms")
    public ObjectResponse<String> sendSms(@RequestParam(value = "principal", defaultValue = "") String principal) throws UnsupportedEncodingException {
        Assert.isTrue(!StringUtils.isBlank(principal), "没有指定手机号码参数");

        PhoneUtil.isMobile(principal);

        String captcha = "111111";
        Element element = new Element(principal, captcha);
        element.setTimeToLive(180);
        this.cacheSmsCaptcha.put(element);

        if(log.isDebugEnabled()) {
            log.debug("发送短信验证码到手机号码：" + captcha);
        }

        return ResponseUtils.successObject("短信验证码已发送");
    }
}