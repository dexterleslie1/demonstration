package com.future.demo.unify.gateway.password;

import org.springframework.security.authentication.AbstractAuthenticationToken;

/**
 * 密码登录AuthenticationToken
 */
public class CustomizePasswordAuthenticationToken extends AbstractAuthenticationToken {

    /**
     * 登录的用户名
     */
    private final Object principal;
    private final Object credentials;

    /**
     * 构建一个没有鉴权的 CustomizePasswordAuthenticationToken
     */
    public CustomizePasswordAuthenticationToken(Object principal, Object credentials) {
        super(null);
        this.principal = principal;
        this.credentials = credentials;
        setAuthenticated(false);
    }

    @Override
    public Object getCredentials() {
        return this.credentials;
    }

    @Override
    public Object getPrincipal() {
        return this.principal;
    }

    @Override
    public void setAuthenticated(boolean isAuthenticated) throws IllegalArgumentException {
        if (isAuthenticated) {
            throw new IllegalArgumentException(
                    "Cannot set this token to trusted - use constructor which takes a GrantedAuthority list instead");
        }

        super.setAuthenticated(false);
    }

    @Override
    public void eraseCredentials() {
        super.eraseCredentials();
    }
}
