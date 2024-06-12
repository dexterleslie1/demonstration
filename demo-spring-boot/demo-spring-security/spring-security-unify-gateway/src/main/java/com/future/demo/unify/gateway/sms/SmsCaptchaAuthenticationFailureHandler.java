package com.future.demo.unify.gateway.sms;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.future.common.constant.ErrorCodeConstant;
import com.future.common.http.ResponseUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class SmsCaptchaAuthenticationFailureHandler implements AuthenticationFailureHandler {
    @Autowired
    private ObjectMapper objectMapper;

    private Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    public void onAuthenticationFailure(HttpServletRequest request,
                                        HttpServletResponse response,
                                        AuthenticationException exception) throws IOException, ServletException {
        logger.info("登陆失败");
        ResponseUtils.writeFailResponse(response, HttpServletResponse.SC_BAD_REQUEST, ErrorCodeConstant.ErrorCodeCommon, exception.getMessage());
    }
}
