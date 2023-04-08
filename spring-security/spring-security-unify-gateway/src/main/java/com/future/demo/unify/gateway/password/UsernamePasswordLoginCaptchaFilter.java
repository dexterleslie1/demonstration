package com.future.demo.unify.gateway.password;

import com.yyd.common.http.HttpUtil;
import lombok.extern.slf4j.Slf4j;
import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
public class UsernamePasswordLoginCaptchaFilter extends OncePerRequestFilter {
    CacheManager cacheManager;
    Cache cachePasswordLoginCaptcha;
    Cache cacheLoginFailureCount;

    public void setCacheManager(CacheManager cacheManager) {
        this.cacheManager = cacheManager;
        this.cachePasswordLoginCaptcha = this.cacheManager.getCache("cachePasswordLoginCaptcha");
        this.cacheLoginFailureCount = this.cacheManager.getCache("cacheLoginFailureCount");
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String ip = HttpUtil.getIpAddress(request);
        Element element = this.cacheLoginFailureCount.get(ip);
        int failureCount = 0;
        if(element!=null) {
            failureCount = (Integer)element.getObjectValue();
        }
        // 单个ip登录失败次数大于等于5
        if(failureCount>=5) {
            String clientId = request.getParameter("clientId");
            if(StringUtils.isBlank(clientId)) {
                HttpUtil.responseWithError(response, HttpStatus.BAD_REQUEST, 50001, "没有提供clientId参数");
                return;
            }

            String captcha = request.getParameter("captcha");
            if(StringUtils.isBlank(captcha)) {
                HttpUtil.responseWithError(response, HttpStatus.BAD_REQUEST, 50001, "没有提供登录验证码参数");
                return;
            }

            element = this.cachePasswordLoginCaptcha.get(clientId);
            if(element==null) {
                HttpUtil.responseWithError(response, HttpStatus.BAD_REQUEST, 50001, "登录验证码错误");
                return;
            }

            String captchaStore = (String)element.getObjectValue();
            if(!captcha.equals(captchaStore)) {
                HttpUtil.responseWithError(response, HttpStatus.BAD_REQUEST, 50001, "登录验证码错误");
                return;
            }
        }
        filterChain.doFilter(request, response);
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        String path = request.getServletPath();
        return !path.equals("/api/v1/password/login");
    }
}
