package com.future.demo.unify.gateway.sms;

import com.future.demo.unify.gateway.common.MyUser;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.ArrayList;

@Component
@Slf4j
public class SmsCaptchaUserDetailsService implements UserDetailsService {
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        log.debug("手机号码+短信验证码登录");
        return new MyUser(username, StringUtils.EMPTY, new ArrayList<>());
    }
}
