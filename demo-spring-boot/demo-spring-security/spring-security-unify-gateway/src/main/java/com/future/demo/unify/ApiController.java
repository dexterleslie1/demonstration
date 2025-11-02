package com.future.demo.unify;

import com.future.common.http.ObjectResponse;
import com.future.common.http.ResponseUtils;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/user")
public class ApiController {

    @GetMapping("info")
    ObjectResponse<String> info(Authentication authentication) {
        ObjectResponse<String> response = new ObjectResponse<>();
        User user = (User)authentication.getPrincipal();
        response.setData(user.getUsername());
        return response;
    }

    @GetMapping("test1")
    ObjectResponse<String> test1() {
        return ResponseUtils.successObject("成功调用接口 /api/v1/user/test1");
    }

    @PreAuthorize("hasRole('admin')")
    @GetMapping("test2")
    ObjectResponse<String> test2() {
        return ResponseUtils.successObject("成功调用接口 /api/v1/user/test2");
    }

    @PreAuthorize("hasAuthority('user:creation')")
    @GetMapping("test3")
    ObjectResponse<String> test3() {
        return ResponseUtils.successObject("成功调用接口 /api/v1/user/test3");
    }

}
