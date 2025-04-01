package com.xx.thrid.party.config;

import com.xx.thrid.party.MyXxxController;
import org.springframework.context.annotation.Bean;

public class MyThirdPartyConfiguration {
    // 手动创建Controller并注入到spring容器中，否则在引用这个库的应用中不能自动扫描并实例化相关Controller
    @Bean
    MyXxxController myXxxController() {
        return new MyXxxController();
    }
}
