package com.future.demo.gateway.config;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.yyd.common.jwt.JwtUtil;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.server.context.ServerSecurityContextRepository;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class SecurityRepository implements ServerSecurityContextRepository {

    @Value("${publicKey}")
    String publicKey;

    @Override
    public Mono<Void> save(ServerWebExchange serverWebExchange, SecurityContext securityContext) {
        return Mono.empty();
    }

    @Override
    public Mono<SecurityContext> load(ServerWebExchange exchange) {
        String token = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        if (token != null) {
            try {
                token = token.replace("Bearer ", "");
                SecurityContext emptyContext = SecurityContextHolder.createEmptyContext();

                DecodedJWT decodedJWT = JwtUtil.verifyWithPublicKey(publicKey, token);
                String userId = String.valueOf(decodedJWT.getClaim("userId").asLong());
                List<String> permissionList = decodedJWT.getClaim("permissionList").asList(String.class);
                List<GrantedAuthority> authorities = permissionList == null ? new ArrayList<>() : permissionList.stream().map(SimpleGrantedAuthority::new).collect(Collectors.toList());
                List<String> roleList = decodedJWT.getClaim("roleList").asList(String.class);
                if (roleList != null && roleList.size() > 0) {
                    authorities.addAll(roleList.stream().map(SimpleGrantedAuthority::new).collect(Collectors.toList()));
                }
                User user = new User(userId, StringUtils.EMPTY, authorities);
                Authentication authentication = new MyAuthentication(user);
                authentication.setAuthenticated(true);
                emptyContext.setAuthentication(authentication);
                return Mono.just(emptyContext);
            } catch (Exception e) {
                return Mono.empty();
            }
        }
        return Mono.empty();
    }

}
