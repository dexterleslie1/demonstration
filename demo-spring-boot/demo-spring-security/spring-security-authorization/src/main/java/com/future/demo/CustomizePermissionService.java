package com.future.demo;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.Collection;

/**
 * 自定义权限验证逻辑
 */
@Service
public class CustomizePermissionService {

    public boolean hasPermission(HttpServletRequest request, Authentication authentication) {
        Object objectPrincipal = authentication.getPrincipal();
        if(objectPrincipal instanceof UserDetails) {
            UserDetails userDetails = (UserDetails) objectPrincipal;
            Collection<? extends GrantedAuthority> authorityList = userDetails.getAuthorities();
            String requestUri = request.getRequestURI();
            if(authorityList.contains(new SimpleGrantedAuthority(requestUri))) {
                return true;
            }
        }
        return false;
    }

}