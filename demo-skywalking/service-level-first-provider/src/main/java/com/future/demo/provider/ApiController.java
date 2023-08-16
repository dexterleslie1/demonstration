package com.future.demo.provider;

import com.future.demo.gateway.common.feign.ServiceLevelSecondProviderClient;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;

@RestController
@Slf4j
public class ApiController {

    @Resource
    ServiceLevelSecondProviderClient serviceLevelSecondProviderClient;

    @GetMapping(value = "/api/v1/test1")
    public ResponseEntity<String> test1(@RequestParam(value = "param1", defaultValue = "") String param1) {
        log.debug("准备调用feign");
        ResponseEntity<String> response = this.serviceLevelSecondProviderClient.test1(param1);
        log.debug("结束调用feign");
        return response;
    }

    @GetMapping(value = "/api/v1/test2")
    public ResponseEntity<String> test2(@RequestParam(value = "param1", defaultValue = "") String param1) {
        log.debug("准备调用feign");
        ResponseEntity<String> response = this.serviceLevelSecondProviderClient.test2(param1);
        log.debug("结束调用feign");
        return response;
    }

    @GetMapping(value = "/api/v1/test3")
    public ResponseEntity<String> test3() throws InterruptedException {
        log.debug("准备调用feign");
        Thread.sleep(500);
        ResponseEntity<String> response = this.serviceLevelSecondProviderClient.test3();
        Thread.sleep(200);
        log.debug("结束调用feign");
        return response;
    }
}
