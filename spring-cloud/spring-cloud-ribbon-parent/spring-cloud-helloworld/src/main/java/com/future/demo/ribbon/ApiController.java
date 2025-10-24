package com.future.demo.ribbon;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.TimeUnit;

/**
 *
 */
@RestController
public class ApiController {
    @Value("${server.port}")
    private int port;

    /**
     * @param name
     * @return
     */
    @RequestMapping(value = "/api/v1/sayHello", method = RequestMethod.POST)
    public String sayHello(@RequestParam(value = "name", defaultValue = "") String name,
                           @RequestParam(value = "timeoutInMillis", defaultValue = "0") int timeoutInMillis) throws InterruptedException {
        // 如果 timeoutInMillis > 0，则模拟超时
        if (timeoutInMillis > 0) {
            TimeUnit.MILLISECONDS.sleep(timeoutInMillis);
        }

        return "Hello " + name + "!!(Ribbon) port=" + port;
    }
}
