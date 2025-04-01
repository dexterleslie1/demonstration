package com.future.demo.unify.gateway.password;

import org.springframework.lang.Nullable;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 自定义密码登录AuthenticationFilter
 */
public class CustomizePasswordAuthenticationFilter extends AbstractAuthenticationProcessingFilter {

    public static final String SPRING_SECURITY_FORM_USERNAME_KEY = "username";
    public static final String SPRING_SECURITY_FORM_PASSWORD_KEY = "password";

    public CustomizePasswordAuthenticationFilter() {
        // 只处理密码登录url
        super(new AntPathRequestMatcher("/api/v1/password/login", "POST"));
    }

    public Authentication attemptAuthentication(HttpServletRequest request,
                                                HttpServletResponse response) throws AuthenticationException {
        if (!request.getMethod().equals("POST")) {
            throw new AuthenticationServiceException(
                    "Authentication method not supported: " + request.getMethod());
        }

        String username = obtainUsername(request);
        String password = obtainPassword(request);

        if (username == null) {
            username = "";
        }

        if (password == null) {
            password = "";
        }

        username = username.trim();

        // 创建没有鉴权的密码登录token
        CustomizePasswordAuthenticationToken authRequest = new CustomizePasswordAuthenticationToken(
                username, password);

        setDetails(request, authRequest);

        return this.getAuthenticationManager().authenticate(authRequest);
    }

    @Nullable
    protected String obtainPassword(HttpServletRequest request) {
        return request.getParameter(SPRING_SECURITY_FORM_PASSWORD_KEY);
    }

    @Nullable
    protected String obtainUsername(HttpServletRequest request) {
        return request.getParameter(SPRING_SECURITY_FORM_USERNAME_KEY);
    }

    protected void setDetails(HttpServletRequest request,
                              CustomizePasswordAuthenticationToken authRequest) {
        authRequest.setDetails(authenticationDetailsSource.buildDetails(request));
    }
}
