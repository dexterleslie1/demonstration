package com.future.demo;

import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Collection;

/**
 * 登录认证时用户对象
 */
@Getter
public class CustomizeUserDetails extends User {
    /**
     * 当前用户id
     */
    private Long userId;

    /**
     * @param username
     * @param password
     * @param authorities
     */
    public CustomizeUserDetails(Long userId, String username, String password, Collection<? extends GrantedAuthority> authorities) {
        super(username, password, authorities);
        this.userId = userId;
    }

}
