package com.future.demo.unify.gateway.password;

import com.future.demo.unify.gateway.common.MyUser;
import com.yyd.common.regex.RegexUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.ArrayList;

@Component
@Slf4j
public class UsernamePasswordUserDetailsService implements UserDetailsService {
    @Autowired
    PasswordEncoder passwordEncoder;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        if(StringUtils.isBlank(username)) {
            throw new BadCredentialsException("没有指定用户名、手机号码、邮箱至少一项参数");
        }

        int loginType = -1;
        // 判断是否手机号码登录
        String phone;
        if(!username.startsWith("+")) {
            phone = "+86" + username;
        } else {
            phone = username;
        }
        try {
            RegexUtil.isMobilePhone(phone);
            loginType = 2;
        } catch (IllegalArgumentException ex) {
        }

        // 判断是否email登录
        try {
            RegexUtil.isEmail(username);
            loginType = 3;
        } catch (IllegalArgumentException ex) {

        }

        // 否则用户名登录
        if(loginType==-1) {
            loginType = 1;
        }

        if(loginType==1) {
            log.debug("用户名+密码登录");
        } else if(loginType==2) {
            log.debug("手机号码+密码登录");
        } else {
            log.debug("email+密码登录");
        }

        MyUser user = new MyUser(username, passwordEncoder.encode("123456"), new ArrayList<>());
        user.setLoginType(loginType);
        return user;
    }
}
