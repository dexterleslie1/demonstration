package com.future.demo.unify.gateway.password;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.future.demo.unify.gateway.common.MyUser;
import com.future.demo.unify.gateway.common.TokenStore;
import com.yyd.common.http.HttpUtil;
import com.yyd.common.json.JSONUtil;
import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.UUID;

@Component
public class CustomizePasswordAuthenticationSuccessHandler implements AuthenticationSuccessHandler {
    @Autowired
    CacheManager cacheManager;
    Cache cacheLoginFailureCount;

    @PostConstruct
    public void init1() {
        this.cacheLoginFailureCount = this.cacheManager.getCache("cacheLoginFailureCount");
    }

    @Autowired
    TokenStore tokenStore;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {
        String ip = HttpUtil.getIpAddress(request);
        this.cacheLoginFailureCount.remove(ip);

        MyUser user = (MyUser)authentication.getPrincipal();

        String token = UUID.randomUUID().toString();
        this.tokenStore.store(token, user);
        user.setToken(token);

        ObjectNode userObjectNode = JSONUtil.ObjectMapperInstance.createObjectNode();
        userObjectNode.put("username", authentication.getName());
        userObjectNode.put("loginType", user.getLoginType());
        userObjectNode.put("token", token);
        HttpUtil.response(response, userObjectNode);
    }
}