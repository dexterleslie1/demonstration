package com.future.demo.unify.gateway.sms;


import com.future.common.http.ObjectResponse;
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
@RequestMapping("/api/v1/sms/captcha")
public class SmsCaptchaController {
    @Autowired
    CacheManager cacheManager;
    Cache cacheSmsCaptcha;

    @PostConstruct
    public void init1() {
        this.cacheSmsCaptcha = this.cacheManager.getCache("cacheSmsCaptcha");
    }

    @GetMapping("send")
    public ObjectResponse<String> send(@RequestParam(value = "phone", defaultValue = "") String phone) throws UnsupportedEncodingException {
        Assert.isTrue(!StringUtils.isBlank(phone), "没有指定手机号码参数");

        PhoneUtil.isMobile(phone);

        String captcha = "111111";
        Element element = new Element(phone, captcha);
        element.setTimeToLive(180);
        this.cacheSmsCaptcha.put(element);
        log.debug("发送短信验证码到手机号码：" + captcha);

        ObjectResponse<String> response = new ObjectResponse<>();
        response.setData("短信验证码已发送");
        return response;
    }
}
