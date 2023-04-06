package com.future.demo.gateway.config;

import com.future.demo.common.vo.UserVo;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.io.Serializable;
import java.util.Collection;

public class SecurityUserDetails extends User implements Serializable {
    private UserVo userVo;

    public SecurityUserDetails(String username, String password, Collection<? extends GrantedAuthority> authorities, UserVo userVo) {
        super(username, password, authorities);
        this.userVo = userVo;
    }

//    public SecurityUserDetails(String username, String password, boolean enabled, boolean accountNonExpired, boolean credentialsNonExpired, boolean accountNonLocked, Collection<? extends GrantedAuthority> authorities, Long userId) {
//        super(username, password, enabled, accountNonExpired, credentialsNonExpired, accountNonLocked, authorities);
//        this.userId = userId;
//    }

    public UserVo getUserVo() {
        return this.userVo;
    }

}
