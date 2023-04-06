package com.future.demo.gateway.config;

import org.springframework.http.HttpHeaders;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.server.context.ServerSecurityContextRepository;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import javax.annotation.Resource;

@Component
public class SecurityRepository implements ServerSecurityContextRepository {

    @Resource
    UserStore userStore;

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
                Authentication authentication = this.userStore.get(token);
                if(authentication == null) {
                    return Mono.empty();
                }
                Authentication authentication1 = new MyAuthentication((SecurityUserDetails)authentication.getPrincipal());
                authentication1.setAuthenticated(true);
                emptyContext.setAuthentication(authentication1);
                return Mono.just(emptyContext);
            } catch (Exception e) {
                return Mono.empty();
            }
        }
        return Mono.empty();
    }

}
