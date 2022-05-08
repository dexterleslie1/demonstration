package com.future.demo.robot.detection;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang.StringUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class InterceptorFilter implements Filter {
    private final static ObjectMapper OMInstance = new ObjectMapper();

    private JedisPool jedisPool = null;

    @Override
    public void init(FilterConfig filterConfig) {
        ApplicationContext ctx = WebApplicationContextUtils.getRequiredWebApplicationContext(filterConfig.getServletContext());
        jedisPool = ctx.getBean(JedisPool.class);
    }

    @Override
    public void doFilter(ServletRequest servletRequest,
                         ServletResponse servletResponse,
                         FilterChain filterChain) throws IOException, ServletException {
        Jedis jedis = null;
        try {
            jedis = this.jedisPool.getResource();

            HttpServletRequest request = (HttpServletRequest) servletRequest;
            HttpServletResponse response = (HttpServletResponse) servletResponse;

            String clientIp = RequestUtils.getRemoteAddress(request);
            String key = CaptchaController.PrefixWhitelist + clientIp;
            if (!jedis.exists(key)) {
                String str = jedis.get("enabled");
                boolean enabled = false;
                if(!StringUtils.isEmpty(str)) {
                    try {
                        enabled = Boolean.parseBoolean(str);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
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
        } finally {
            if(jedis != null) {
                jedis.close();
                jedis = null;
            }
        }
    }

    @Override
    public void destroy() {

    }
}
