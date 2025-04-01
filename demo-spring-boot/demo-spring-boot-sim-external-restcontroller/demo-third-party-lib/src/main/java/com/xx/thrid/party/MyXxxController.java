package com.xx.thrid.party;

import com.future.common.http.ObjectResponse;
import com.future.common.http.ResponseUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

// 此Controller用于当作第三方库被动态引用测试
@RestController
@RequestMapping("/api/v1")
public class MyXxxController {
    @GetMapping("test1")
    ObjectResponse<String> test1() {
        return ResponseUtils.successObject("Hello world!");
    }
}
