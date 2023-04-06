package com.future.demo.gateway.config;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.future.demo.common.vo.LoginSuccessVo;
import com.yyd.common.http.response.ObjectResponse;
import com.yyd.common.json.JSONUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.server.WebFilterExchange;
import org.springframework.security.web.server.authentication.WebFilterChainServerAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import javax.annotation.Resource;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.UUID;

@Slf4j
@Component
public class AuthenticationSuccessHandler extends WebFilterChainServerAuthenticationSuccessHandler {

    @Resource
    UserStore userStore;

    @Override
    public Mono<Void> onAuthenticationSuccess(WebFilterExchange webFilterExchange, Authentication authentication) {
        ServerHttpResponse response = webFilterExchange.getExchange().getResponse();
        response.setStatusCode(HttpStatus.OK);
        response.getHeaders().add("Content-Type", "application/json; charset=UTF-8");

        Long userId = ((SecurityUserDetails)authentication.getPrincipal()).getUserVo().getId();
        String loginName = ((SecurityUserDetails)authentication.getPrincipal()).getUsername();
        List<String> menuList = ((SecurityUserDetails)authentication.getPrincipal()).getUserVo().getMenuList();
        List<String> permissionList = ((SecurityUserDetails)authentication.getPrincipal()).getUserVo().getPermissionList();
        String token = UUID.randomUUID().toString();
        LoginSuccessVo loginSuccessVo = new LoginSuccessVo();
        loginSuccessVo.setUserId(userId);
        loginSuccessVo.setLoginName(loginName);
        loginSuccessVo.setToken(token);
        loginSuccessVo.setMenuList(menuList);
        loginSuccessVo.setPermissionList(permissionList);
        ObjectResponse<LoginSuccessVo> loginResponse = new ObjectResponse<>();
        loginResponse.setData(loginSuccessVo);

        this.userStore.put(token, authentication);

        String JSON = StringUtils.EMPTY;
        try {
            JSON = JSONUtil.ObjectMapperInstance.writeValueAsString(loginResponse);
        } catch (JsonProcessingException e) {
            log.error(e.getMessage(), e);
        }
        DataBuffer bodyDataBuffer = response.bufferFactory().wrap(JSON.getBytes(StandardCharsets.UTF_8));
        return response.writeWith(Mono.just(bodyDataBuffer));
    }

}
