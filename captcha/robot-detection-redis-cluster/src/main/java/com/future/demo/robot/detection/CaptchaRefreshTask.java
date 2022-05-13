package com.future.demo.robot.detection;

import org.springframework.beans.factory.annotation.Autowired;

public class CaptchaRefreshTask {
    @Autowired
    CaptchaCacheService captchaCacheService;

    public void process() {
        this.captchaCacheService.refresh();
    }
}
