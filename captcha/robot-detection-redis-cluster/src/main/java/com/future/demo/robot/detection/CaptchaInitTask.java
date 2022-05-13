package com.future.demo.robot.detection;

import org.springframework.beans.factory.annotation.Autowired;

public class CaptchaInitTask{
    @Autowired
    CaptchaCacheService captchaCacheService;

    public void process() {
        this.captchaCacheService.init();
    }
}
