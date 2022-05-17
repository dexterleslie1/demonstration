package com.future.demo.robot.detection;

import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import javax.servlet.*;
import java.io.IOException;

/**
 * 验证客户端是否机器人
 */
public class RobotCheckingFilter implements Filter {
    private AbstractCaptchaCacheService captchaCacheService;

    @Override
    public void init(FilterConfig filterConfig) {
        ApplicationContext ctx = WebApplicationContextUtils.getRequiredWebApplicationContext(filterConfig.getServletContext());
        this.captchaCacheService = ctx.getBean(AbstractCaptchaCacheService.class);
    }

    @Override
    public void doFilter(ServletRequest servletRequest,
                         ServletResponse servletResponse,
                         FilterChain filterChain) throws IOException, ServletException {
        this.captchaCacheService.requestFilter(servletRequest, servletResponse, filterChain);
    }

    @Override
    public void destroy() {

    }
}
