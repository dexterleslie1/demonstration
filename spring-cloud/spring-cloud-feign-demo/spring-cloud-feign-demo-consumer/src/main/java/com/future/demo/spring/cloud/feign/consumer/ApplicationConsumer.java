package com.future.demo.spring.cloud.feign.consumer;

import com.future.common.exception.EnableFutureExceptionHandler;
import com.future.demo.spring.cloud.feign.common.feign.ProductFeign;
import com.future.demo.spring.cloud.feign.common.feign.ProductFeignTestSameName;
import com.future.demo.spring.cloud.feign.common.feign.ProductFeignWithConfig;
import com.future.demo.spring.cloud.feign.common.feign.ProductFeignWithSpecifyUrl;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
// 应用程序中启用Feign客户端的支持
@EnableFeignClients(
        clients = {
                ProductFeign.class,
                ProductFeignTestSameName.class,
                ProductFeignWithSpecifyUrl.class,
                ProductFeignWithConfig.class
        }
)
@EnableFutureExceptionHandler
//@EnableDiscoveryClient
public class ApplicationConsumer {
    public static void main(String[] args) {
        SpringApplication.run(ApplicationConsumer.class, args);
    }
}
