package com.future.demo.unify.email;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
public class UnifyEmailAuthenticationProvider implements AuthenticationProvider {

    @Autowired
    private UnifyEmailUserDetailsService userDetailsService;

    @Autowired
    CacheManager cacheManager;
    Cache cacheSmsCaptcha;

    @PostConstruct
    void init() {
        this.cacheSmsCaptcha = this.cacheManager.getCache("cacheSmsCaptcha");
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        UnifyEmailAuthenticationToken authenticationToken = (UnifyEmailAuthenticationToken) authentication;

        String email = (String) authenticationToken.getPrincipal();
        String captchaVerifyParam = (String) authenticationToken.getCredentials();

        if (StringUtils.isBlank(email)) {
            throw new BadCredentialsException("没有提供邮箱");
        }
        if (StringUtils.isBlank(captchaVerifyParam)) {
            throw new BadCredentialsException("没有提供邮箱验证码");
        }

        UserDetails userDetails = userDetailsService.loadUserByUsername(email);

        if (!userDetails.getPassword().equals(captchaVerifyParam)) {
            throw new BadCredentialsException("提供的邮箱验证码错误");
        }

        // 此时鉴权成功后，应当重新 new 一个拥有鉴权的 authenticationResult 返回
        UnifyEmailAuthenticationToken authenticationResult = new UnifyEmailAuthenticationToken(userDetails, userDetails.getAuthorities());
        authenticationResult.setDetails(authenticationToken.getDetails());
        return authenticationResult;
    }

    @Override
    public boolean supports(Class<?> authentication) {
        // 判断 authentication 是不是 UnifySmsCaptchaAuthenticationToken 的子类或子接口
        return UnifyEmailAuthenticationToken.class.isAssignableFrom(authentication);
    }
}
