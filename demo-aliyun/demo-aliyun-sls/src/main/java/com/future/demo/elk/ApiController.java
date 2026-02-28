package com.future.demo.elk;

import org.apache.commons.lang3.RandomStringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

/**
 *
 */
@RestController
public class ApiController {
    final static Logger logger = LoggerFactory.getLogger(ApiController.class);

    /**
     * @return
     */
    @RequestMapping(value = "/api/v1/test1")
    public ResponseEntity<String> test1() {
        String traceId = UUID.randomUUID().toString();
        String spanId = UUID.randomUUID().toString();

        MDC.put("traceId", traceId);
        MDC.put("spanId", spanId);

        logger.info("测试消息 {}", RandomStringUtils.randomAlphanumeric(8));

        Exception exception = new Exception("测试异常 " + RandomStringUtils.randomAlphanumeric(6));
        logger.error(exception.getMessage(), exception);

        return ResponseEntity.ok("你好世界！");
    }
}
