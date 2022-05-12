package com.xy.future.demo.captcha;

import com.ramostear.captcha.common.Fonts;
import com.ramostear.captcha.core.Captcha;
import com.ramostear.captcha.support.CaptchaType;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import java.awt.*;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {Application.class})
public class Tests {
    private final static Logger logger = LoggerFactory.getLogger(Tests.class);

    private CaptchaType type = CaptchaType.DEFAULT;
    private Font font = Fonts.getInstance().defaultFont();
    private int width = 160;
    private int height = 50;
    private int length = 10;

    @Autowired
    private RedisTemplate redisTemplate = null;

    @Test
    public void test() throws InterruptedException {
        ExecutorService executorService = Executors.newCachedThreadPool();

        int threads = 10;
        int threadLoop = 10000;
        int batchSize = 200;

        for(int i=1; i<=threads; i++) {
            int finalI = i;
            executorService.submit(() -> {
                Map<String, String> datumMapper = new HashMap<>();
                int batchCount = 0;
                for(int j=1; j<=threadLoop; j++) {
                    Captcha captcha = new Captcha();
                    captcha.setType(this.type);
                    captcha.setWidth(this.width);
                    captcha.setHeight(this.height);
                    captcha.setLength(this.length);
                    captcha.setFont(this.font);
                    String captchaCode = captcha.getCaptchaCode();
                    String imageBase64 = captcha.toBase64();
                    datumMapper.put(captchaCode, imageBase64);
                    if(j%batchSize == 0) {
                        this.redisTemplate.opsForValue().multiSet(datumMapper);
                        batchCount++;
                        logger.info("线程{}第{}批验证码进入redis", finalI, batchCount);
                        datumMapper = new HashMap<>();
                    }
                }
            });
        }

        executorService.shutdown();
        while(!executorService.awaitTermination(1, TimeUnit.SECONDS));
    }
}
