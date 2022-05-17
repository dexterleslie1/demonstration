package com.future.demo.captcha;

import com.ramostear.captcha.common.Fonts;
import com.ramostear.captcha.core.Captcha;
import com.ramostear.captcha.support.CaptchaType;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import net.sf.ehcache.Cache;
import net.sf.ehcache.Element;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.PostConstruct;
import java.awt.*;
import java.util.Date;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@Controller
@RequestMapping(value="/api/v1")
@Slf4j
public class ApiController {
    public final static String CacheKeyCaptchaPrefix = "captcha#";

    @Autowired
    CacheManagera cacheManager;

    Cache cacheCaptcha;

    @PostConstruct
    public void init() {
        String name = "cacheCaptcha";
        Cache cacheTemporary = new Cache(name, 10000000, false, false, 0, 0);
        this.cacheManager.getCacheManager().addCache(cacheTemporary);
        this.cacheCaptcha = this.cacheManager.getCache(name);
    }

    private final CaptchaType type = CaptchaType.DEFAULT;
    private final Font font = Fonts.getInstance().defaultFont();
    private final int width = 160;
    private final int height = 50;
    private final int captchaLength = 4;

    @RequestMapping("test.do")
    public @ResponseBody String test() throws InterruptedException {
        int concurrent = 50;
        int loop = 2000;
        ExecutorService executorService = Executors.newCachedThreadPool();
        for(int i=0; i<concurrent; i++) {
            executorService.submit(() -> {
                for(int j=0; j<loop; j++) {
                    Captcha captcha = new Captcha();
                    captcha.setType(type);
                    captcha.setWidth(width);
                    captcha.setHeight(height);
                    captcha.setLength(captchaLength);
                    captcha.setFont(font);

                    String code = captcha.getCode();
                    String imageBase64 = captcha.toBase64();

                    String captchaId = CacheKeyCaptchaPrefix + UUID.randomUUID().toString();
                    CaptchaEntry entry = new CaptchaEntry();
                    entry.setId(captchaId);
                    entry.setCode(code);
                    entry.setImageBase64(imageBase64);
                    entry.setCreateTime(new Date());

                    Element element = new Element(captchaId, entry);
                    this.cacheCaptcha.put(element);
                }
            });
        }

        executorService.shutdown();
        while(!executorService.awaitTermination(2, TimeUnit.SECONDS)) {
            log.info("ehcache中共有{}个验证码", this.cacheCaptcha.getKeys().size());
        }
        log.info("ehcache中共有{}个验证码", this.cacheCaptcha.getKeys().size());

        return "成功调用";
    }

    @Data
    public static class CaptchaEntry {
        /**
         * 唯一标识
         */
        private String id;
        /**
         * 验证码结果
         */
        private String code;
        /**
         * 验证码图片base64
         */
        private String imageBase64;
        /**
         * 验证码创建时间
         */
        private Date createTime;
    }
}
