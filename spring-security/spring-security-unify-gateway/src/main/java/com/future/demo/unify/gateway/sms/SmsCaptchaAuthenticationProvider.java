package com.future.demo.unify.gateway.sms;

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
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;

@Component
public class SmsCaptchaAuthenticationProvider implements AuthenticationProvider {

    @Autowired
    private SmsCaptchaUserDetailsService userDetailsService;

    @Autowired
    CacheManager cacheManager;
    Cache cacheSmsCaptcha;

    @PostConstruct
    void init() {
        this.cacheSmsCaptcha = this.cacheManager.getCache("cacheSmsCaptcha");
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        SmsCaptchaAuthenticationToken authenticationToken = (SmsCaptchaAuthenticationToken) authentication;

        String mobile = (String) authenticationToken.getPrincipal();

        checkSmsCode(mobile);

        UserDetails userDetails = userDetailsService.loadUserByUsername(mobile);

        // 此时鉴权成功后，应当重新 new 一个拥有鉴权的 authenticationResult 返回
        SmsCaptchaAuthenticationToken authenticationResult = new SmsCaptchaAuthenticationToken(userDetails, userDetails.getAuthorities());

        authenticationResult.setDetails(authenticationToken.getDetails());

        return authenticationResult;
    }

    private void checkSmsCode(String phone) {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        String smsCaptcha = request.getParameter("smsCaptcha");

        if(StringUtils.isBlank(smsCaptcha)) {
            throw new BadCredentialsException("没有提供短信验证码");
        }

        Element element = this.cacheSmsCaptcha.get(phone);
        if(element==null) {
            throw new BadCredentialsException("短信验证码已过期，请重新获取");
        }

        String cacheSmsCaptcha = (String)element.getObjectValue();
        if(!cacheSmsCaptcha.equals(smsCaptcha)) {
            throw new BadCredentialsException("提供的短信验证码错误");
        }
    }

    @Override
    public boolean supports(Class<?> authentication) {
        // 判断 authentication 是不是 SmsCodeAuthenticationToken 的子类或子接口
        return SmsCaptchaAuthenticationToken.class.isAssignableFrom(authentication);
    }

//    public UserDetailsService getUserDetailsService() {
//        return userDetailsService;
//    }

//    public void setUserDetailsService(UserDetailsService userDetailsService) {
//        this.userDetailsService = userDetailsService;
//    }
//
//    public void setCacheManager(CacheManager cacheManager) {
//        this.cacheManager = cacheManager;
//        this.cacheSmsCaptcha = this.cacheManager.getCache("cacheSmsCaptcha");
//    }
}
