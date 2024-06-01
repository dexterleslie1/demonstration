package com.future.demo;

import com.future.common.http.ObjectResponse;
import com.future.common.http.ResponseUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Dexterleslie.Chan
 */
@RestController
@RequestMapping(value = "/api/auth")
public class ApiController {

    /**
     * 用户登录后才能访问的资源
     *
     * @return
     */
    @GetMapping(value = "a1")
    public ObjectResponse<String> a1() {
        return ResponseUtils.successObject("成功调用接口/api/auth/a1");
    }

    /**
     * 用户登录后才能访问的资源
     *
     * @return
     */
    @GetMapping(value = "a2")
    public ObjectResponse<String> a2() {
        return ResponseUtils.successObject("成功调用接口/api/auth/a2");
    }
}
