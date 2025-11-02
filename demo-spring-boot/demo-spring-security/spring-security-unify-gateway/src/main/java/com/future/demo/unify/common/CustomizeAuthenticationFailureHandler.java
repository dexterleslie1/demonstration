package com.future.demo.unify.common;

import cn.hutool.extra.servlet.ServletUtil;
import cn.hutool.json.JSONUtil;
import com.future.common.constant.ErrorCodeConstant;
import com.future.common.http.HttpUtil;
import com.future.common.http.ObjectResponse;
import com.future.common.http.ResponseUtils;
import lombok.extern.slf4j.Slf4j;
import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
@Slf4j
public class CustomizeAuthenticationFailureHandler implements AuthenticationFailureHandler {
    @Autowired
    CacheManager cacheManager;
    Cache cacheLoginFailureCount;

    @PostConstruct
    public void init1() {
        this.cacheLoginFailureCount = this.cacheManager.getCache("cacheLoginFailureCount");
    }

    @Override
    public void onAuthenticationFailure(HttpServletRequest request,
                                        HttpServletResponse response,
                                        AuthenticationException exception) {
        int errorCode = ErrorCodeConstant.ErrorCodeCommon;
        // 密码登录
        String uri = request.getRequestURI();
        if ("/api/v1/password/login".equalsIgnoreCase(uri)) {
            String ip = HttpUtil.getIpAddress(request);
            Element element = this.cacheLoginFailureCount.get(ip);
            if (element == null) {
                element = new Element(ip, 1);
            } else {
                element = new Element(ip, (Integer) element.getObjectValue() + 1);
            }
            this.cacheLoginFailureCount.put(element);
            int failureCount = (Integer) element.getObjectValue();
            if (failureCount >= 5) {
                log.debug("模拟返回自定义错误，提示客户端尝试登录次数超过服务器限制，需要客户端主动获取并提供登录验证码");
                errorCode = 50001;
            }
        }

        log.info("登陆失败");
        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        ObjectResponse<String> objectResponse = ResponseUtils.failObject(errorCode, exception.getMessage());
        String json = JSONUtil.toJsonStr(objectResponse);
        ServletUtil.write(response, json, MediaType.APPLICATION_JSON_UTF8_VALUE);
    }
}
