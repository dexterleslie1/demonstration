package com.future.demo.security.order.controller;

import com.future.common.http.ObjectResponse;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class OrderController {

    /**
     * @param authentication
     * @return
     */
    @GetMapping(value = "/order")
    @PreAuthorize("hasAuthority('sys:admin')")
    public ObjectResponse<String> order(Authentication authentication) {
        ObjectResponse<String> response = new ObjectResponse<>();
        response.setData(authentication.getName() + "下单成功");
        return response;
    }

    /**
     * 用于协助测试权限不足的情况
     *
     * @param authentication
     * @return
     */
    @GetMapping(value = "/order1")
    @PreAuthorize("hasAuthority('sys:nothing')")
    public ObjectResponse<String> order1(Authentication authentication) {
        ObjectResponse<String> response = new ObjectResponse<>();
        response.setData(authentication.getName() + "下单1成功");
        return response;
    }
}
