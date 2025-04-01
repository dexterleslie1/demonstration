package com.future.demo.jquery.ajax;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/")
public class DemoDatumController {
    @GetMapping("get")
    public ResponseEntity<Map<String, String>> get(HttpServletRequest request,
                                                   // exception=true 返回状态码为400
                                                   @RequestParam(value = "exception", defaultValue = "false") boolean exception) {
        Map<String, String> params = new HashMap<>();
        params.put("key1", "value1");
        String accessToken = request.getHeader(HttpHeaders.AUTHORIZATION);
        params.put(HttpHeaders.AUTHORIZATION, accessToken);
        if(!exception) {
            return ResponseEntity.ok(params);
        } else {
            return ResponseEntity.badRequest().body(params);
        }
    }

    @PostMapping("post")
    public Map<String, String> post(@RequestParam(value = "param1", defaultValue = "") String param1,
                                    @RequestHeader(value = HttpHeaders.AUTHORIZATION, defaultValue = "") String accessToken) {
        Map<String, String> params = new HashMap<>();
        params.put("key1", "value1");
        params.put("param1", param1);
        params.put(HttpHeaders.AUTHORIZATION, accessToken);
        return params;
    }
}
