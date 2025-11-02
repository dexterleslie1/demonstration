package com.future.demo.unify.common;

import cn.hutool.extra.servlet.ServletUtil;
import cn.hutool.json.JSONConfig;
import cn.hutool.json.JSONUtil;
import com.future.common.http.ObjectResponse;
import com.future.common.http.RequestUtils;
import com.future.common.http.ResponseUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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
                                Authentication authentication) {
        String token = RequestUtils.ObtainBearerToken(request);
        if (!StringUtils.isBlank(token)) {
            this.tokenStore.remove(token);
        }

        response.setStatus(HttpServletResponse.SC_OK);
        ObjectResponse<String> objectResponse = ResponseUtils.successObject("成功退出");
        String json = JSONUtil.toJsonStr(objectResponse, JSONConfig.create().setIgnoreNullValue(false));
        ServletUtil.write(response, json, MediaType.APPLICATION_JSON_UTF8_VALUE);
    }
}
