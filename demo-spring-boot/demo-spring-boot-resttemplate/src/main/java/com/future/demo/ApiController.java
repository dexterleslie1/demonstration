package com.future.demo;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yyd.common.http.ResponseUtils;
import com.yyd.common.http.response.ObjectResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 *
 */
@RestController
@RequestMapping(value = "/api/v1")
public class ApiController {
    /**
     *
     * @param parameters
     * @return
     * @throws JsonProcessingException
     */
    @PostMapping(value = "postWithBodyMap")
    ResponseEntity<String> postWithBodyMap(
            @RequestBody(required = true) Map<String, Object> parameters) throws JsonProcessingException {
        ObjectMapper OMInstance = new ObjectMapper();
        String json = OMInstance.writeValueAsString(parameters);
        return ResponseEntity.ok("你提交的body参数：" + json);
    }

    /**
     * 不需要提交任何参数
     * @return
     */
    @PostMapping(value = "postWithoutParameters")
    ResponseEntity<String> postWithoutParameters() {
        return ResponseEntity.ok("没有任何参数提交");
    }

    /**
     * 提交headers参数接口
     * @param name
     * @param token
     * @return
     */
    @PostMapping(value = "postWithHeaders")
    ResponseEntity<String> postWithHeaders(
            @RequestParam(value = "name") String name,
            @RequestHeader(value = "token") String token) {
        if(StringUtils.isEmpty(name)) {
            throw new IllegalArgumentException("name must provide!");
        }
        if(StringUtils.isEmpty(token)) {
            throw new IllegalArgumentException("token must provide!");
        }
        String message = "你提交的参数 name=" + name + ",token=" + token;
        return ResponseEntity.ok(message);
    }

    @PostMapping("test1")
    ObjectResponse<Map<String, Object>> test1() {
        Map<String, Object> mapTemp = new HashMap<>();
        mapTemp.put("k1", "v1");
        return ResponseUtils.successObject(mapTemp);
    }
}
