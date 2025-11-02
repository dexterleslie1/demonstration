package com.future.demo.unify.password;


import com.future.common.regex.RegexUtil;
import com.future.demo.unify.common.CustomizeUser;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Collections;

/**
 * 密码登录用户信息获取
 */
@Component
@Slf4j
public class UnifyPasswordUserDetailsService implements UserDetailsService {

    @Autowired
    PasswordEncoder passwordEncoder;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        if (StringUtils.isBlank(username)) {
            throw new BadCredentialsException("请指定指定用户名/手机号码/邮箱");
        }

        boolean usernameAndPasswordLogin = true;
        // 判断是否手机号码登录
        String phone;
        if (!username.startsWith("+")) {
            phone = "+86" + username;
        } else {
            phone = username;
        }
        try {
            RegexUtil.isMobilePhone(phone);
            usernameAndPasswordLogin = false;
            if (log.isDebugEnabled())
                log.debug("手机号码+密码登录");
        } catch (IllegalArgumentException ignored) {
        }

        // 判断是否email登录
        try {
            RegexUtil.isEmail(username);
            usernameAndPasswordLogin = false;
            if (log.isDebugEnabled())
                log.debug("email+密码登录");
        } catch (IllegalArgumentException ignored) {

        }

        // 否则用户名登录
        if (usernameAndPasswordLogin) {
            if (log.isDebugEnabled())
                log.debug("用户名+密码登录");
        }

        // 这里硬编码密码为123456，实际生产环境密码需要从数据库读取
        return new CustomizeUser(username, passwordEncoder.encode("123456"), Collections.singletonList(new SimpleGrantedAuthority("ROLE_admin")));
    }
}
