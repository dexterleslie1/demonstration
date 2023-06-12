package com.future.demo;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

/**
 * 总结：1、不会启动超时。2、服务器未启动完毕就能够接受请求
 */
@Component
@Slf4j
public class MyApplicationListener implements ApplicationListener<ApplicationStartedEvent> {

    @Override
    public void onApplicationEvent(ApplicationStartedEvent event) {
        /*log.debug("准备休眠30秒，测试服务器是否正常启动");
        try {
            TimeUnit.SECONDS.sleep(30);
        } catch (InterruptedException e) {
            log.error(e.getMessage(), e);
        }
        log.debug("结束休眠30秒，服务器正常启动");*/
    }
}
