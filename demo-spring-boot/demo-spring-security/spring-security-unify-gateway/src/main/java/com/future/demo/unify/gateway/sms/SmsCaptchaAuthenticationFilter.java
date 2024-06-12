package com.future.demo.unify.gateway.sms;

import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class SmsCaptchaAuthenticationFilter extends AbstractAuthenticationProcessingFilter {
    /**
     * form表单中手机号码的字段name
     */
    public static final String SPRING_SECURITY_FORM_MOBILE_KEY = "phone";
    public static final String SPRING_SECURITY_FORM_SMS_CAPTCHA = "smsCaptcha";

    public SmsCaptchaAuthenticationFilter() {
        // 短信登录的请求 post 方式的 /api/v1/sms/login
        super(new AntPathRequestMatcher("/api/v1/sms/login", "POST"));
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request,
                                                HttpServletResponse response) throws AuthenticationException {
        if (!request.getMethod().equals("POST")) {
            throw new AuthenticationServiceException(
                    "Authentication method not supported: " + request.getMethod());
        }

        String mobile = obtainMobile(request);
        String smsCaptcha = obtainSmsCaptcha(request);

        if (mobile == null) {
            mobile = "";
        }

        if(smsCaptcha == null) {
            smsCaptcha = "";
        }

        mobile = mobile.trim();

        // 创建没有鉴权的token
        SmsCaptchaAuthenticationToken authRequest = new SmsCaptchaAuthenticationToken(mobile, smsCaptcha);

        setDetails(request, authRequest);

        return this.getAuthenticationManager().authenticate(authRequest);
    }

    protected String obtainMobile(HttpServletRequest request) {
        return request.getParameter(SPRING_SECURITY_FORM_MOBILE_KEY);
    }

    protected String obtainSmsCaptcha(HttpServletRequest request) {
        return request.getParameter(SPRING_SECURITY_FORM_SMS_CAPTCHA);
    }

    protected void setDetails(HttpServletRequest request, SmsCaptchaAuthenticationToken authRequest) {
        authRequest.setDetails(authenticationDetailsSource.buildDetails(request));
    }

}
