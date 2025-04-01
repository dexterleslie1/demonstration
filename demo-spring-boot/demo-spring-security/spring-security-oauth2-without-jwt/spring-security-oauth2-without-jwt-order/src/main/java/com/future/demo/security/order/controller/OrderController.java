package com.future.demo.security.order.controller;

import com.future.common.http.ObjectResponse;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
public class OrderController {
    @GetMapping(value = "/order")
    @PreAuthorize("hasAuthority('sys:admin')")
    public ObjectResponse<String> order(Principal principal) {
        ObjectResponse<String> response = new ObjectResponse<>();
        response.setData(principal.getName() + "下单成功");
        return response;
    }
}
