package com.future.demo.gateway;

import com.yyd.common.exception.WebfluxGlobalExceptionHandlerConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

@SpringBootApplication
@Import(value = {
        WebfluxGlobalExceptionHandlerConfiguration.class
})
public class ApplicationGateway {
    public static void main(String[] args) {
        SpringApplication.run(ApplicationGateway.class, args);
    }
}