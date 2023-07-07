package com.future.demo;

import com.yyd.common.http.ResponseUtils;
import com.yyd.common.http.response.ObjectResponse;
import com.yyd.common.json.JSONUtil;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Collections;

public class CustomizeAuthenticationFilter extends OncePerRequestFilter {

    public CustomizeAuthenticationFilter() {

    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        return false;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        CustomizeHttpServletRequestWrapper myRequest = new CustomizeHttpServletRequestWrapper(request);
        try {
            String token = obtainBearerToken(request);
            if (StringUtils.hasText(token)) {
                long userId = 11L;
                CustomizeUser user = new CustomizeUser(userId, Collections.emptyList());
                CustomizeAuthentication authentication = new CustomizeAuthentication(user);
                authentication.setAuthenticated(true);
                SecurityContextHolder.getContext().setAuthentication(authentication);
                myRequest.addParameter("contextUserId", userId);
            }
            filterChain.doFilter(myRequest, response);
        } catch (Exception ex) {
            onErrorResponse(request, response, 50000, ex.getMessage());
        }
    }

    String obtainBearerToken(HttpServletRequest request) {
        String bearerStr = request.getHeader(HttpHeaders.AUTHORIZATION);
        if(!StringUtils.hasText(bearerStr)) {
            return bearerStr;
        }

        return bearerStr.replace("Bearer ", "");
    }

    void onErrorResponse(HttpServletRequest request,
                         HttpServletResponse response,
                         int errorCode,
                         String errorMessage) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        ObjectResponse<String> responseO = ResponseUtils.failObject(errorCode, errorMessage);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.getWriter().write(JSONUtil.ObjectMapperInstance.writeValueAsString(responseO));
    }

}
