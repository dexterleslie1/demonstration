package com.future.demo;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

/**
 * 总结：1、和CommandLineRunner一致
 */
@Component
@Slf4j
public class MyApplicationRunner implements ApplicationRunner {

    @Override
    public void run(ApplicationArguments args) throws InterruptedException {
        /*log.debug("准备休眠30秒，测试服务器是否正常启动");
        TimeUnit.SECONDS.sleep(30);
        log.debug("结束休眠30秒，服务器正常启动");*/
    }

}
