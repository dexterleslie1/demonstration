package com.future.demo.unify.common;

import com.future.demo.unify.email.UnifyEmailAuthenticationToken;
import com.future.demo.unify.password.UnifyPasswordAuthenticationToken;
import com.future.demo.unify.sms.UnifySmsCaptchaAuthenticationToken;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class UnifyAuthenticationFilter extends AbstractAuthenticationProcessingFilter {

    public UnifyAuthenticationFilter() {
        // 拦截登录请求filter
        super(new AntPathRequestMatcher("/api/v1/auth/login" , "POST"));
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException, IOException, ServletException {
        if (!request.getMethod().equals("POST")) {
            throw new AuthenticationServiceException(
                    "Authentication method not supported: " + request.getMethod());
        }

        String type = request.getParameter("type");
        if (type == null) {
            type = StringUtils.EMPTY;
        }
        AbstractAuthenticationToken authRequest = null;
        if (type.equals("password")) {
            // 密码+验证码
            String principal = request.getParameter("principal");
            String credentials = request.getParameter("credentials");
            // 连续登录失败第5次后需要提供验签验证码参数
            String captchaVerifyParam = request.getParameter("captchaVerifyParam");
            authRequest = new UnifyPasswordAuthenticationToken(principal, credentials, captchaVerifyParam);
        } else if (type.equals("sms")) {
            // 短信
            String principal = request.getParameter("principal");
            String captchaVerifyParam = request.getParameter("captchaVerifyParam");
            authRequest = new UnifySmsCaptchaAuthenticationToken(principal, captchaVerifyParam);
        } else if (type.equals("email")) {
            // 邮箱
            String email = request.getParameter("email");
            String captchaVerifyParam = request.getParameter("captchaVerifyParam");
            authRequest = new UnifyEmailAuthenticationToken(email, captchaVerifyParam);
        } else if (type.equals("站内扫码")) {
            // 站内扫码
        } else if (type.equals("微信扫码")) {
            // 微信扫码
        } else if (type.equals("认证器App")) {
            // 认证器App
//            String username = request.getParameter("username");
//            String captchaVerifyParam = request.getParameter("captchaVerifyParam");
//            authenticationToken = new CustomizePasswordAuthenticationToken(username, captchaVerifyParam);
        } else {
            throw new AuthenticationServiceException("不支持的登录方式");
        }

        return this.getAuthenticationManager().authenticate(authRequest);
    }
}
