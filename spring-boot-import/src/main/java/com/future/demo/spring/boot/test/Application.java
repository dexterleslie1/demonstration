package com.future.demo.spring.boot.test;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

@SpringBootApplication
// 导入AutoConfigTestService配置自动配置 TestService
@Import(value = {
        AutoConfigTestService.class
})
public class Application {
    public static void main(String []args){
        SpringApplication.run(Application.class, args);
    }
}
