package com.future.demo;

import com.yyd.common.http.ResponseUtils;
import com.yyd.common.http.response.ObjectResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/")
public class ApiController {

    @GetMapping("")
    String getClientIp(HttpServletRequest request) {
        return "你的ip地址: " + request.getRemoteAddr();
    }
}
