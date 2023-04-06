package com.future.demo.gateway.config;

import com.future.demo.common.feign.UserFeignIntranet;
import com.future.demo.common.vo.UserVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@Slf4j
@Component
public class SecurityUserDetailsService implements ReactiveUserDetailsService {

    @Resource
    @Lazy
    UserFeignIntranet userFeignIntranet;

    PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Override
    public Mono<UserDetails> findByUsername(String username) {
        // https://www.cnblogs.com/w84422/p/15519310.html
        CompletableFuture<UserVo> completableFuture = CompletableFuture.supplyAsync(() -> this.userFeignIntranet.getByUsername(username).getData());
        UserVo userVo;
        try {
            userVo = completableFuture.get();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return Mono.empty();
        }

        if (userVo == null) {
            return Mono.empty();
        }

        Collection<GrantedAuthority> authorities = new ArrayList<>();
        List<String> roleList = userVo.getRoleList();
        if (roleList != null) {
            roleList.forEach(o -> {
                authorities.add(new SimpleGrantedAuthority(o));
            });
        }
        List<String> permissionList = userVo.getPermissionList();
        if (permissionList != null) {
            permissionList.forEach(o -> {
                authorities.add(new SimpleGrantedAuthority(o));
            });
        }
        SecurityUserDetails securityUserDetails = new SecurityUserDetails(
                userVo.getUsername(),
                "{bcrypt}" + passwordEncoder.encode(userVo.getPassword()),
                authorities,
                userVo);
        return Mono.just(securityUserDetails);
    }
}
