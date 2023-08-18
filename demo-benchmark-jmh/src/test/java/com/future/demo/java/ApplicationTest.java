package com.future.demo.java;

import org.mockito.Mockito;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

@SpringBootApplication
public class ApplicationTest {
    public static void main(String []args){
        SpringApplication.run(ApplicationTest.class, args);
    }

    // jmh测试时模拟mock
    // https://stackoverflow.com/questions/42641853/spring-boot-integration-testing-with-mocked-services-components
    // https://stackoverflow.com/questions/41438519/benchmarking-spring-boot-application-with-jmh
    @Bean
    @Primary
    MyService1 myService1() {
        MyService1 myService1 = Mockito.mock(MyService1.class);
        Mockito.doNothing().when(myService1).test1();
        return myService1;
    }
}
