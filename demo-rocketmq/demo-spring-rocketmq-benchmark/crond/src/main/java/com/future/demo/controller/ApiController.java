package com.future.demo.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.future.common.http.ObjectResponse;
import com.future.common.http.ResponseUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

@RestController
@RequestMapping("/api/v1")
public class ApiController {

    @Resource
    AtomicInteger counter;
    @Resource
    Map<Integer, Integer> batchSizeToCountMap;
    @Resource
    ObjectMapper objectMapper;

    @GetMapping("statistics")
    public ObjectResponse<String> statistics() throws Exception {
        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("消息总数", counter.get());
        resultMap.put("batchSizeToCount", batchSizeToCountMap);

        // 计算batchSizeToCountMap中的消息总数
        int totalCountCallback = 0;
        int totalCountMessages = 0;
        for (Integer key : batchSizeToCountMap.keySet()) {
            int count = batchSizeToCountMap.get(key);
            totalCountCallback = totalCountCallback + count;
            totalCountMessages = totalCountMessages + (key * count);
        }
        resultMap.put("监听器回调总数", totalCountCallback);
        resultMap.put("监听器处理消息总数", totalCountMessages);

        String JSON = this.objectMapper.writeValueAsString(resultMap);
        return ResponseUtils.successObject(JSON);
    }
}
