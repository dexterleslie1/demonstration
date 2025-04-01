package com.future.demo.unify.gateway.common;

import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Collection;

/**
 * 用户信息对象
 */
@Data
public class CustomizeUser extends User {
    public CustomizeUser(String username, String password, Collection<? extends GrantedAuthority> authorities) {
        super(username, password, authorities);
    }
}
