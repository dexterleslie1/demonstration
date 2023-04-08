package com.future.demo.unify.gateway.password;

import com.yyd.common.http.HttpUtil;
import lombok.extern.slf4j.Slf4j;
import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
@Slf4j
public class UsernamePasswordAuthenticationFailureHandler implements AuthenticationFailureHandler {
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
                                        AuthenticationException exception) throws IOException, ServletException {
        int errorCode = 50000;

        String ip = HttpUtil.getIpAddress(request);
        Element element = this.cacheLoginFailureCount.get(ip);
        if(element==null) {
            element = new Element(ip, 1);
        } else {
            element = new Element(ip, (Integer)element.getObjectValue()+1);
        }
        this.cacheLoginFailureCount.put(element);
        int failureCount = (Integer)element.getObjectValue();
        if(failureCount>=5) {
            log.debug("模拟返回自定义错误，提示客户端尝试登录次数超过服务器限制，需要客户端主动获取并提供登录验证码");
            errorCode = 50001;
        }
        HttpUtil.responseWithError(response, HttpStatus.BAD_REQUEST, errorCode, exception.getMessage());
    }
}
