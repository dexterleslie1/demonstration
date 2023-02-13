package com.future.demo.myrule;

import com.netflix.loadbalancer.IRule;
import com.netflix.loadbalancer.RandomRule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

// 注意：自动IRule一定需要放置到与Application启动类所在的包和子包外，例如：com.future.demo.myrule
// 否则@RibbonClient注解不生效
@Configuration
public class MyRuleRandom {
    @Bean
    public IRule rule() {
        // 随机选择服务负载均衡算法
        return new RandomRule();
    }
}
