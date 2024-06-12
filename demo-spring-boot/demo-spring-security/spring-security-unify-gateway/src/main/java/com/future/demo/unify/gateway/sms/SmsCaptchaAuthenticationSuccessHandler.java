package com.future.demo.unify.gateway.sms;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.future.common.http.ResponseUtils;
import com.future.common.json.JSONUtil;
import com.future.demo.unify.gateway.common.CustomizeUser;
import com.future.demo.unify.gateway.common.TokenStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.UUID;

@Component
public class SmsCaptchaAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    @Autowired
    TokenStore tokenStore;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        CustomizeUser user = (CustomizeUser) authentication.getPrincipal();

        String token = UUID.randomUUID().toString();
        this.tokenStore.store(token, user);

        ObjectNode userObjectNode = JSONUtil.ObjectMapperInstance.createObjectNode();
        userObjectNode.put("username", authentication.getName());
        userObjectNode.put("token", token);
        ResponseUtils.writeSuccessResponse(response, userObjectNode);
    }
}
