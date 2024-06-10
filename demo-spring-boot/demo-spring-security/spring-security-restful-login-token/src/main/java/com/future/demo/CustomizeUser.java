package com.future.demo;

import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Collection;

/**
 * 登录后权限用户对象
 */
@Data
public class CustomizeUser extends User {

    /**
     * 当前用户id
     */
    private Long userId;

    /**
     * @param userId
     * @param authorities
     */
    public CustomizeUser(Long userId, Collection<? extends GrantedAuthority> authorities) {
        super(String.valueOf(userId), StringUtils.EMPTY, authorities);
        this.userId = userId;
    }

}
