package com.future.demo.unify.password;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 * 密码登录
 */
@Component
public class UnifyPasswordAuthenticationProvider implements AuthenticationProvider {

    @Autowired
    PasswordEncoder passwordEncoder;
    @Autowired
    UnifyPasswordUserDetailsService userDetailsService;

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
        UnifyPasswordAuthenticationToken authenticationToken = (UnifyPasswordAuthenticationToken) authentication;

        String username = (String) authenticationToken.getPrincipal();
        String password = (String) authenticationToken.getCredentials();

        if (StringUtils.isBlank(username)) {
            throw new BadCredentialsException("没有提供用户名");
        }
        if (StringUtils.isBlank(password)) {
            password = StringUtils.EMPTY;
        }

        // 校验验证码
        /*checkCaptcha(authenticationToken);*/

        UserDetails userDetails = userDetailsService.loadUserByUsername(username);

        if (!this.passwordEncoder.matches(password, userDetails.getPassword())) {
            throw new BadCredentialsException("用户名或者密码错误");
        }

        // 此时鉴权成功后，应当重新 new 一个拥有鉴权的 authenticationResult 返回
        UnifyPasswordAuthenticationToken authenticationResult = new UnifyPasswordAuthenticationToken(userDetails, "" , null);
        authenticationResult.setDetails(authenticationToken.getDetails());
        return authenticationResult;
    }

    /**
     * 校验验证码
     */
    /*private void checkCaptcha(UnifyPasswordAuthenticationToken authentication) {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        String ip = ServletUtil.getClientIP(request);
        Element element = this.cacheLoginFailureCount.get(ip);
        int failureCount = 0;
        if (element != null) {
            failureCount = (Integer) element.getObjectValue();
        }
        // 单个ip登录失败次数>=5
        if (failureCount >= 5) {
            String captchaVerifyParam = authentication.getCaptchaVerifyParam();
            if (StringUtils.isBlank(captchaVerifyParam)) {
                throw new BadCredentialsException("没有提供登录验证码参数");
            }

            element = this.cachePasswordLoginCaptcha.get(captchaVerifyParam);
            if (element == null) {
                throw new BadCredentialsException("登录验证码错误");
            }

            String captchaStore = (String) element.getObjectValue();
            if (!captchaVerifyParam.equals(captchaStore)) {
                throw new BadCredentialsException("登录验证码错误");
            }
        }
    }*/

    @Override
    public boolean supports(Class<?> authentication) {
        // 判断 authentication 是不是 UnifyPasswordAuthenticationToken 的子类或子接口
        return UnifyPasswordAuthenticationToken.class.isAssignableFrom(authentication);
    }
}
