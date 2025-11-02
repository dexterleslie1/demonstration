package com.future.demo.unify.common;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.future.common.http.HttpUtil;
import com.future.common.http.ResponseUtils;
import com.future.common.json.JSONUtil;
import com.future.demo.unify.password.UnifyPasswordAuthenticationToken;
import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.UUID;

/**
 * 登录成功后处理
 */
@Component
public class CustomizeAuthenticationSuccessHandler implements AuthenticationSuccessHandler {
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
                                        Authentication authentication) {
        // 密码登录
        if(authentication instanceof UnifyPasswordAuthenticationToken) {
            String ip = HttpUtil.getIpAddress(request);
            this.cacheLoginFailureCount.remove(ip);
        }

        CustomizeUser user = (CustomizeUser)authentication.getPrincipal();

        String token = UUID.randomUUID().toString();
        this.tokenStore.store(token, user);

        ObjectNode userObjectNode = JSONUtil.ObjectMapperInstance.createObjectNode();
        userObjectNode.put("username", authentication.getName());
        userObjectNode.put("token", token);
        ResponseUtils.writeSuccessResponse(response, userObjectNode);
    }
}
