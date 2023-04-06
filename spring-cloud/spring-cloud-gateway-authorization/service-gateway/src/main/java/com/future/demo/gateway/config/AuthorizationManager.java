package com.future.demo.gateway.config;

import com.future.demo.common.feign.UserFeignIntranet;
import com.future.demo.common.vo.PermissionVo;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.authorization.ReactiveAuthorizationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.server.authorization.AuthorizationContext;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class AuthorizationManager implements ReactiveAuthorizationManager<AuthorizationContext> {

    @Resource
    UserFeignIntranet userFeignIntranet;

    @Override
    public Mono<AuthorizationDecision> check(Mono<Authentication> authentication, AuthorizationContext authorizationContext) {
        return authentication.map(auth -> {
            String path = authorizationContext.getExchange().getRequest().getURI().getPath();

            List<String> authorities = auth.getAuthorities() == null ? null : auth.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.toList());

            List<PermissionVo> permissionVoList = this.userFeignIntranet.listPermission().getData();
            Map<String, PermissionVo> urlToPermissionVoMapper = permissionVoList.stream().collect(Collectors.toMap(PermissionVo::getUrl, o -> o));
            PermissionVo permissionVo = urlToPermissionVoMapper.get(path);
            if (permissionVo != null) {
                boolean valid = true;

                List<String> permissionListAccessing = permissionVo.getPermissionListAccessing();
                if (permissionListAccessing != null) {
                    for (String o : permissionListAccessing) {
                        if (!authorities.contains(o)) {
                            valid = false;
                            break;
                        }
                    }
                }

                List<String> roleListAccessing = permissionVo.getRoleListAccessing();
                if (roleListAccessing != null) {
                    for (String o : roleListAccessing) {
                        if (!authorities.contains(o)) {
                            valid = false;
                            break;
                        }
                    }
                }

                if (valid) {
                    return new AuthorizationDecision(true);
                }
            }

            return new AuthorizationDecision(false);
        }).defaultIfEmpty(new AuthorizationDecision(false));
    }

}