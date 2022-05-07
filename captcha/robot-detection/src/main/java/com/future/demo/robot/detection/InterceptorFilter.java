package com.future.demo.robot.detection;

import com.fasterxml.jackson.databind.ObjectMapper;
import net.sf.ehcache.Cache;
import net.sf.ehcache.Element;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class InterceptorFilter implements Filter {
    private final static ObjectMapper OMInstance = new ObjectMapper();

    private Cache cacheEnable = null;
    private Cache cacheWhitelist = null;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        ApplicationContext ctx = WebApplicationContextUtils.getRequiredWebApplicationContext(filterConfig.getServletContext());
        CacheManagera cacheManager = ctx.getBean(CacheManagera.class);
        this.cacheEnable = cacheManager.getCache("cacheEnable");
        this.cacheWhitelist = cacheManager.getCache("cacheWhitelist");
    }

    @Override
    public void doFilter(ServletRequest servletRequest,
                         ServletResponse servletResponse,
                         FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        String clientIp = RequestUtils.getRemoteAddress(request);
        Element element = this.cacheWhitelist.get(clientIp);
        if(element == null) {
            element = this.cacheEnable.get("enabled");
            boolean enabled = false;
            if (element != null) {
                enabled = (Boolean) element.getObjectValue();
            }

            if (enabled) {
                String uri = request.getRequestURI();
                if (uri.endsWith("jsp") || uri.equalsIgnoreCase("/")) {
                    response.sendRedirect("/verify.html");
                    return;
                } else {
                    if (!uri.startsWith("/api/v1/captcha") &&
                        !uri.equalsIgnoreCase("/api/v1/biz/setEnable.do")) {
                        Map<String, String> mapReturn = new HashMap<>();
                        mapReturn.put("location", "/verify.html");
                        AjaxResponse response1 = new AjaxResponse();
                        response1.setDataObject(mapReturn);
                        response1.setErrorCode(50000);
                        String JSON = OMInstance.writeValueAsString(response1);
                        ResponseUtils.write(response, JSON);
                        return;
                    }
                }
            }
        }

        filterChain.doFilter(servletRequest, servletResponse);
    }

    @Override
    public void destroy() {

    }
}
