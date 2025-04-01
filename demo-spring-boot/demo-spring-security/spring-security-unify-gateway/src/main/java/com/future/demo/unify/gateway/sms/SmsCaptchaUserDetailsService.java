package com.future.demo.unify.gateway.sms;

import com.future.demo.unify.gateway.common.CustomizeUser;
import lombok.extern.slf4j.Slf4j;
import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Collections;

@Component
@Slf4j
public class SmsCaptchaUserDetailsService implements UserDetailsService {

    @Autowired
    CacheManager cacheManager;
    Cache cacheSmsCaptcha;

    @PostConstruct
    void init() {
        this.cacheSmsCaptcha = this.cacheManager.getCache("cacheSmsCaptcha");
    }

    @Override
    public UserDetails loadUserByUsername(String phone) throws UsernameNotFoundException {
        log.debug("手机号码+短信验证码登录");

        Element element = this.cacheSmsCaptcha.get(phone);
        if(element==null) {
            throw new BadCredentialsException("短信验证码已过期，请重新获取");
        }

        String cacheSmsCaptcha = (String)element.getObjectValue();

        // 用户密码字段当作验证码字段使用
        return new CustomizeUser(phone, cacheSmsCaptcha, Collections.singletonList(new SimpleGrantedAuthority("ROLE_admin")));
    }
}
