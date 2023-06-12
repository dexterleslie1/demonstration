package com.future.demo;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

/**
 * 总结：1、不会启动超时。2、服务器未启动完毕不能接受请求
 */
@Component
@Slf4j
public class MyInitializingBean implements InitializingBean {

    @Override
    public void afterPropertiesSet() throws InterruptedException {
        /*log.debug("准备休眠30秒，测试服务器是否正常启动");
        TimeUnit.SECONDS.sleep(30);
        log.debug("结束休眠30秒，服务器正常启动");*/
    }

}
