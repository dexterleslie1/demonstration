package com.future.demo;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.annotation.Resource;
import java.util.Collections;

@Configuration
public class ConfigWebSecurity extends WebSecurityConfigurerAdapter {
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                // 禁用 CSRF 保护
                .csrf().disable()

                // 完全禁用服务器端的会话管理，使应用成为无状态（Stateless）
                /*.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)*/

                // 用于对 URL 进行访问权限控制
                /*.and()*/.authorizeRequests()
                // /css/*.css资源允许匿名访问
                .antMatchers("/css/*.css").permitAll()
                // /login资源允许匿名访问
                .antMatchers("/login").permitAll()
                // 用于指定对于任何未明确指定权限要求的请求（即前面未通过 .antMatchers() 等方法明确匹配的请求），都需要用户进行身份验证（即用户必须登录）。
                .anyRequest().authenticated()

                .and()
                // 指定form登录界面 URL
                .formLogin()
                // 自定义登录表单username、password参数名称
                .usernameParameter("usernameDemo")
                .passwordParameter("passwordDemo")
                // 自定义登录页面
                .loginPage("/login")
                // 自定义登录url，此值要和login.html登录表单action一致
                .loginProcessingUrl("/myLogin")
                // 指定form登录成功后跳转的 URL
                .defaultSuccessUrl("/welcome")
                // 自定义登录成功处理器
                /*.successHandler(new AuthenticationSuccessHandler() {
                    @Override
                    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
                        response.sendRedirect("https://www.baidu.com");
                    }
                })*/;
    }

    @Bean
    public UserDetailsService userDetailsService() {
        return new UserDetailService();
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        // 自定义用户信息数据源，提供用户信息给验证框架校验
        auth.userDetailsService(userDetailsService());
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    public static class UserDetailService implements UserDetailsService {
        @Resource
        private PasswordEncoder passwordEncoder;

        @Override
        public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
            if (!"admin".equals(username)) {
                throw new UsernameNotFoundException("用户" + username + "不存在");
            }

            String password = this.passwordEncoder.encode("123456");
            return new User(username, password, Collections.emptyList());
        }
    }
}