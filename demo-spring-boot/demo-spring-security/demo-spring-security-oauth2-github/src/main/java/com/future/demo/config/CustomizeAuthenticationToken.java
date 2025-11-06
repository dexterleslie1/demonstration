package com.future.demo.config;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Collection;
import java.util.Map;

public class CustomizeAuthenticationToken extends AbstractAuthenticationToken {
    private final Object principal;
    private final String provider;
    private final Map<String, Object> attributes;

    public CustomizeAuthenticationToken(OAuth2User oauth2User,
                                           Collection<? extends GrantedAuthority> authorities,
                                           String authorizedClientRegistrationId) {
        super(authorities);
        this.principal = oauth2User;
        this.provider = authorizedClientRegistrationId;
        this.attributes = oauth2User.getAttributes();
        setAuthenticated(true);
    }

    @Override
    public Object getCredentials() {
        return ""; // OAuth2 通常没有密码凭证
    }

    @Override
    public Object getPrincipal() {
        return principal;
    }

    public String getProvider() {
        return provider;
    }

    public Map<String, Object> getAttributes() {
        return attributes;
    }

    // 可以添加其他自定义方法
    public String getEmail() {
        return (String) attributes.get("email");
    }

    public String getName() {
        return (String) attributes.get("name");
    }
}
