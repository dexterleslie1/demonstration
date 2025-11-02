package com.future.demo.unify.email;

import com.future.demo.unify.common.CustomizeUser;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.Collections;

@Component
@Slf4j
public class UnifyEmailUserDetailsService implements UserDetailsService {

//    @Autowired
//    CacheManager cacheManager;
//    Cache cacheSmsCaptcha;
//
//    @PostConstruct
//    void init() {
//        this.cacheSmsCaptcha = this.cacheManager.getCache("cacheSmsCaptcha");
//    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
//        Element element = this.cacheSmsCaptcha.get(phone);
//        if(element==null) {
//            throw new BadCredentialsException("短信验证码已过期，请重新获取");
//        }
//
//        String cacheSmsCaptcha = (String)element.getObjectValue();

        // 用户密码字段当作验证码字段使用
        return new CustomizeUser(email, "123456", Collections.singletonList(new SimpleGrantedAuthority("ROLE_admin")));
    }
}
