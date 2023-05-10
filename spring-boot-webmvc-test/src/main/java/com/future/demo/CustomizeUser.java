package com.future.demo;

import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Collection;

@Data
public class CustomizeUser extends User {

    private Long userId;

    public CustomizeUser(Long userId, Collection<? extends GrantedAuthority> authorities) {
        super(String.valueOf(userId), StringUtils.EMPTY, authorities);
        this.userId = userId;
    }

}
