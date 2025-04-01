package com.future.demo.provider;

import com.yyd.common.http.ResponseUtils;
import com.yyd.common.http.response.ObjectResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/v1")
public class ApiController {

    @GetMapping(value = "admin/test1")
    public ObjectResponse<String> adminTest1(@RequestParam(value = "userId", defaultValue = "0") Long userId) {
        return ResponseUtils.successObject("[userId=" + userId + "]成功调用接口 /api/v1/admin/test1");
    }

    @GetMapping(value = "admin/test2")
    public ObjectResponse<String> adminTest2(@RequestParam(value = "userId", defaultValue = "0") Long userId) {
        return ResponseUtils.successObject("[userId=" + userId + "]成功调用接口 /api/v1/admin/test2");
    }

    @GetMapping(value = "nuser/test1")
    public ObjectResponse<String> nuserTest1(@RequestParam(value = "userId", defaultValue = "0") Long userId) {
        return ResponseUtils.successObject("[userId=" + userId + "]成功调用接口 /api/v1/nuser/test1");
    }

    @GetMapping(value = "nuser/test2")
    public ObjectResponse<String> nuserTest2(@RequestParam(value = "userId", defaultValue = "0") Long userId) {
        return ResponseUtils.successObject("[userId=" + userId + "]成功调用接口 /api/v1/nuser/test2");
    }
}
