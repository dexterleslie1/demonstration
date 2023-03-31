package com.future.demo.spring.cloud.feign.consumer;

import com.future.demo.spring.cloud.feign.common.feign.ProductFeign;
import com.future.demo.spring.cloud.feign.common.feign.ProductFeignWithConfig;
import com.future.demo.spring.cloud.feign.common.feign.ProductFeignWithSpecifyUrl;
import com.yyd.common.exception.ExceptionController;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Import;

@SpringBootApplication
@EnableFeignClients(
        clients = {
                ProductFeign.class,
                ProductFeignWithSpecifyUrl.class,
                ProductFeignWithConfig.class
        }
)
@Import(value = {ExceptionController.class})
public class ApplicationConsumer {
    public static void main(String[] args) {
        SpringApplication.run(ApplicationConsumer.class, args);
    }
}
