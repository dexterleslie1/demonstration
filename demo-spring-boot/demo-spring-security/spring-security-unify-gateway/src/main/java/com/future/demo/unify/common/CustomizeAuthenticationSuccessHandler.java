package com.future.demo.unify.common;

import cn.hutool.extra.servlet.ServletUtil;
import cn.hutool.json.JSONConfig;
import cn.hutool.json.JSONObject;
import com.future.common.http.HttpUtil;
import com.future.common.http.ObjectResponse;
import com.future.common.http.ResponseUtils;
import com.future.demo.unify.password.UnifyPasswordAuthenticationToken;
import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
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
        if (authentication instanceof UnifyPasswordAuthenticationToken) {
            String ip = HttpUtil.getIpAddress(request);
            this.cacheLoginFailureCount.remove(ip);
        }

        CustomizeUser user = (CustomizeUser) authentication.getPrincipal();

        String token = UUID.randomUUID().toString();
        this.tokenStore.store(token, user);

        JSONObject userJsonObject = new JSONObject();
        userJsonObject.put("username", authentication.getName());
        userJsonObject.put("token", token);
        response.setStatus(HttpServletResponse.SC_OK);
        ObjectResponse<JSONObject> objectResponse = ResponseUtils.successObject(userJsonObject);
        String json = cn.hutool.json.JSONUtil.toJsonStr(objectResponse, JSONConfig.create().setIgnoreNullValue(false));
        ServletUtil.write(response, json, MediaType.APPLICATION_JSON_UTF8_VALUE);
    }
}
