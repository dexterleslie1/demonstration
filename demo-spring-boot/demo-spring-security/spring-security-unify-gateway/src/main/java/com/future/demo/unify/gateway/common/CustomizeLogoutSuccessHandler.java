package com.future.demo.unify.gateway.common;

import com.future.common.http.RequestUtils;
import com.future.common.http.ResponseUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 退出成功后处理
 */
@Component
public class CustomizeLogoutSuccessHandler implements LogoutSuccessHandler {
    @Autowired
    TokenStore tokenStore;

    @Override
    public void onLogoutSuccess(HttpServletRequest request,
                                HttpServletResponse response,
                                Authentication authentication) throws IOException, ServletException {
        String token = RequestUtils.ObtainBearerToken(request);
        if (!StringUtils.isBlank(token)) {
            this.tokenStore.remove(token);
        }

        ResponseUtils.writeSuccessResponse(response, "成功退出");
    }
}
