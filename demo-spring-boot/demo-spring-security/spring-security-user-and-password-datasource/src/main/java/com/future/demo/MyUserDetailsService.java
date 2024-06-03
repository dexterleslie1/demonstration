package com.future.demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class MyUserDetailsService implements UserDetailsService {
    @Autowired
    PasswordEncoder passwordEncoder;

    // 使用UserDetailsService自定义加载用户数据
    // 可用于从数据库自定义加载用户信息
    //
    // 使用正则自动识别手机、邮箱、用户名实现支持三个字段登录
    // https://blog.csdn.net/qq_41589293/article/details/82953674
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return new User("user3", this.passwordEncoder.encode("123456"), AuthorityUtils.commaSeparatedStringToAuthorityList("role3"));
    }
}
