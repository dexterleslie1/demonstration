package com.future.demo.unify.gateway.common;

import com.yyd.common.http.response.ObjectResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/user")
public class UserInfoController {
    @GetMapping("info")
    ObjectResponse<String> info(Authentication authentication) {
        ObjectResponse<String> response = new ObjectResponse<>();
        User user = (User)authentication.getPrincipal();
        response.setData(user.getUsername());
        return response;
    }
}
