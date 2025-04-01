package com.future.demo.consul;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 */
@RestController
// 自定从 Consul 配置中心刷新到最新配置
@RefreshScope
public class ApiController {
    @Value("${my.k1:}")
    private String k1;

    /**
     * @param name
     * @return
     */
    @RequestMapping(value = "/api/v1/c/sayHello", method = RequestMethod.POST)
    public String sayHello(@RequestParam(value = "name", defaultValue = "") String name) {
        return "Hello " + name + "!!!，配置k1=" + k1;
    }
}
