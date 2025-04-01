package com.future.demo;

import com.future.common.http.ObjectResponse;
import com.future.common.http.ResponseUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/api/auth")
public class ApiController {

    /**
     * 用户登录后才能访问的资源
     *
     * @return
     */
    @GetMapping(value = "a1")
    public ObjectResponse<String> a1(CustomizeAuthentication authentication) {
        return ResponseUtils.successObject("成功调用接口/api/auth/a1，登录用户 " + authentication.getName());
    }

    /**
     * 用户登录后才能访问的资源
     *
     * @return
     */
    @GetMapping(value = "a2")
    public ObjectResponse<String> a2(CustomizeAuthentication authentication) {
        return ResponseUtils.successObject("成功调用接口/api/auth/a2，登录用户 " + authentication.getName());
    }
}
