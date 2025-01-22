package com.future.demo;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;

import javax.annotation.Resource;

@Configuration
public class InitConfig implements CommandLineRunner {
    @Resource
    private CanalClient canalClient;

    @Override
    public void run(String... args) throws Exception {
        // todo 导致springboot无法完成启动
        canalClient.handleMessages();
    }
}
