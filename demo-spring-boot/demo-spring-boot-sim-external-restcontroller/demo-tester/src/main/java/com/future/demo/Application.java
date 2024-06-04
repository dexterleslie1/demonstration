package com.future.demo;

import com.xx.thrid.party.config.EnableXxxThirdParty;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
// 启用第三方库（实例化相关controller并注入到spring容器中）
@EnableXxxThirdParty
public class Application {
    public static void main(String []args){
        SpringApplication.run(Application.class, args);
    }
}
