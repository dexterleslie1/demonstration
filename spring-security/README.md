## spring-security组件

> - 启用security配置
>   在ConfigSecurity文件中通过注解@EnableWebSecurity启用spring-security
>
> - 登录成功handler
>   通过实现AuthenticationSuccessHandler自定义登录成功响应
>
> - 登录失败handler
>   通过实现AuthenticationFailureHandle接口自定义登录失败响应
> - 登出成功handler
>   通过实现LogoutSuccessHandler接口自定义登出成功响应
>
> - 登录认证管理器
>   todo AuthorizationManager
>   
>
> - 存储当前登录用户信息的安全上下文
>   https://blog.csdn.net/K_520_W/article/details/110292545
>   通过SecurityContextHolder.getContext().setAuthentication(authentication);方式将用户相关的信息存放到系统的安全上下文中，并且由于 SecurityContextHolder默认是mode_threadlocal模式，那么会将所有登录的用户信息都保存，每个登录的用户都可以通过SecurityContextHolder.getContext().getAuthentication();方式获取
>   当前自己保存的用户信息
>
> - 加载登录用户身份校验信息
>   UserDetailsService和UserDetails
>   通过实现UserDetailsService接口自定义用户身份校验信息加载
>
> - 鉴权管理
>   todo AccessDecisionManager
>
> - 未认证handler
>   通过实现AuthenticationEntryPoint接口自定义用户未认证响应
>
> - 鉴权失败handler
>   通过实现AccessDeniedHandler接口自定义鉴权失败响应

