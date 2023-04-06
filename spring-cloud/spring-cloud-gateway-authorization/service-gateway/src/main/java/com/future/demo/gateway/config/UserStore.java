package com.future.demo.gateway.config;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class UserStore {

    Map<String, Authentication> tokenToAuthenticationMapper = new HashMap<>();

    public void put(String token, Authentication authentication) {
        this.tokenToAuthenticationMapper.put(token, authentication);
    }

    public Authentication get(String token) {
        return this.tokenToAuthenticationMapper.get(token);
    }
}
