package com.future.demo.unify.password;

import lombok.Getter;
import org.springframework.security.authentication.AbstractAuthenticationToken;

/**
 * 密码+验证码
 */
public class UnifyPasswordAuthenticationToken extends AbstractAuthenticationToken {

    /**
     * 用户名/手机号码/邮箱
     */
    private final Object principal;
    /**
     * 密码
     */
    private final Object credentials;
    /**
     * 验证码验签参数
     */
    @Getter
    private final String captchaVerifyParam;

    /**
     * 还未登录的token
     */
    public UnifyPasswordAuthenticationToken(Object principal,
                                            Object credentials,
                                            String captchaVerifyParam) {
        super(null);
        this.principal = principal;
        this.credentials = credentials;
        this.captchaVerifyParam = captchaVerifyParam;
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
