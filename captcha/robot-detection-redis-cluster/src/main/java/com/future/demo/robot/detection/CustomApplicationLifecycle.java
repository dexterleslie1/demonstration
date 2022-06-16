package com.future.demo.robot.detection;

import com.yyd.common.ddos.AbstractCaptchaCacheService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

@Slf4j
public class CustomApplicationLifecycle extends ApplicationLifecycle {
    @Autowired
    AbstractCaptchaCacheService captchaCacheService;

    @Override
    protected void doStartup() throws Exception {
        captchaCacheService.startup();
    }

    @Override
    protected void doShutdown() throws Exception {
        captchaCacheService.shutdown();
    }
}
