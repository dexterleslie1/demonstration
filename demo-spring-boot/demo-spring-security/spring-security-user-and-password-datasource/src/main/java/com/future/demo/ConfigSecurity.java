package com.future.demo;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class ConfigSecurity {
    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /*@Bean
    public UserDetailsService users() {
        UserDetails user = User.builder()
                .username("user3")
                .password(this.passwordEncoder().encode("123456"))
                .roles("role3")
                .build();
        return new InMemoryUserDetailsManager(user);
    }*/
}
