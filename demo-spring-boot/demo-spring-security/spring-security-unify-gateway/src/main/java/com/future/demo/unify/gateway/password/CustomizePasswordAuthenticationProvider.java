package com.future.demo.unify.gateway.password;

import com.future.common.http.HttpUtil;
import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;

/**
 * 密码登录AuthenticationProvider
 * 模拟用户名、手机号码、邮箱+密码尝试多次登录失败后需要提供登录验证码才能够继续登录系统
 */
@Component
public class CustomizePasswordAuthenticationProvider implements AuthenticationProvider {

    @Autowired
    PasswordEncoder passwordEncoder;
    @Autowired
    CustomizePasswordUserDetailsService userDetailsService;

    @Autowired
    CacheManager cacheManager;
    Cache cachePasswordLoginCaptcha;
    Cache cacheLoginFailureCount;

    @PostConstruct
    void init() {
        this.cachePasswordLoginCaptcha = this.cacheManager.getCache("cachePasswordLoginCaptcha");
        this.cacheLoginFailureCount = this.cacheManager.getCache("cacheLoginFailureCount");
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        CustomizePasswordAuthenticationToken authenticationToken = (CustomizePasswordAuthenticationToken) authentication;

        String username = (String) authenticationToken.getPrincipal();

        // 校验验证码
        checkCaptcha();

        UserDetails userDetails = userDetailsService.loadUserByUsername(username);

        if(!this.passwordEncoder.matches((String)authentication.getCredentials(), userDetails.getPassword())) {
            throw new BadCredentialsException("用户名或者密码错误");
        }

        // 此时鉴权成功后，应当重新 new 一个拥有鉴权的 authenticationResult 返回
        CustomizePasswordAuthenticationToken authenticationResult = new CustomizePasswordAuthenticationToken(userDetails, userDetails.getAuthorities());

        authenticationResult.setDetails(authenticationToken.getDetails());

        return authenticationResult;
    }

    private void checkCaptcha() {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
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
                throw new BadCredentialsException("没有提供clientId参数");
            }

            String captcha = request.getParameter("captcha");
            if(StringUtils.isBlank(captcha)) {
                throw new BadCredentialsException("没有提供登录验证码参数");
            }

            element = this.cachePasswordLoginCaptcha.get(clientId);
            if(element==null) {
                throw new BadCredentialsException("登录验证码错误");
            }

            String captchaStore = (String)element.getObjectValue();
            if(!captcha.equals(captchaStore)) {
                throw new BadCredentialsException("登录验证码错误");
            }
        }
    }

    @Override
    public boolean supports(Class<?> authentication) {
        // 判断 authentication 是不是 UsernamePasswordAuthenticationToken 的子类或子接口
        return CustomizePasswordAuthenticationToken.class.isAssignableFrom(authentication);
    }
}
