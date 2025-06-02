package com.future.demo.config;

import com.future.demo.util.OrderRandomlyUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@Slf4j
public class ConfigMy {
    @Value("${totalCount}")
    Integer totalCount;

    @Bean
    OrderRandomlyUtil orderRandomlyUtil() {
        log.info("totalCount {}", totalCount);
        return new OrderRandomlyUtil(totalCount);
    }
}
