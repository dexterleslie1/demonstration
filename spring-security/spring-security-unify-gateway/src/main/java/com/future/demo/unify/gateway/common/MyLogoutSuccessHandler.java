package com.future.demo.unify.gateway.common;

import com.yyd.common.http.HttpUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class MyLogoutSuccessHandler implements LogoutSuccessHandler {
    @Autowired
    TokenStore tokenStore;

    @Override
    public void onLogoutSuccess(HttpServletRequest request,
                                HttpServletResponse response,
                                Authentication authentication) throws IOException, ServletException {
        String token = obtainBearerToken(request);
        if(!StringUtils.isBlank(token)) {
            this.tokenStore.remove(token);
        }

        HttpUtil.response(response, "成功退出");
    }

    String obtainBearerToken(HttpServletRequest request) {
        String bearerStr = request.getHeader(HttpHeaders.AUTHORIZATION);
        if(StringUtils.isBlank(bearerStr)) {
            return bearerStr;
        }

        return bearerStr.replace("Bearer ", "");
    }
}
