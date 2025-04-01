package com.future.demo.argument;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.util.UUID;

/**
 * https://blog.csdn.net/JAVA_Coyo/article/details/125584496
 */
@RestController
@RequestMapping(path = "/api/v1")
public class CookieRestfulController {
    /**
     * 请求服务器分配cookie接口
     *
     * @return
     */
    @RequestMapping(path = "assignCookie")
    public ResponseEntity<String> assignCookie(HttpServletResponse response) {
        Cookie cookie = new Cookie("accessToken", UUID.randomUUID().toString());
        // 10秒后过期
        cookie.setMaxAge(10);
        cookie.setPath("/");
        response.addCookie(cookie);
        return ResponseEntity.ok("服务器成功设置cookie");
    }

    /**
     * 查询cookie值
     *
     * @param accessToken
     * @return
     */
    @RequestMapping(path = "getCookie")
    public ResponseEntity<String> getCookie(@CookieValue(value = "accessToken", defaultValue = "") String accessToken) {
        String str = "accessToken=" + accessToken;
        return ResponseEntity.ok("cookie: " + str);
    }

}
