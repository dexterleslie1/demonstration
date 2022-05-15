package com.future.demo.robot.detection;

import com.fasterxml.jackson.databind.ObjectMapper;
import net.sf.ehcache.Cache;
import net.sf.ehcache.Element;
import org.apache.commons.lang.StringUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisCluster;
import redis.clients.jedis.JedisPool;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * 验证客户端是否机器人
 */
public class RobotCheckingFilter implements Filter {
    private final static int TimeoutSeconds = 15*60;
    private final static ObjectMapper OMInstance = new ObjectMapper();

    private Cache cacheEnable = null;
    private Cache cacheWhitelist = null;
    private Cache cacheRequestCounter = null;

    private JedisCluster jedisCluster = null;

    @Override
    public void init(FilterConfig filterConfig) {
        ApplicationContext ctx = WebApplicationContextUtils.getRequiredWebApplicationContext(filterConfig.getServletContext());
        CacheManagera cacheManager = ctx.getBean(CacheManagera.class);
        this.cacheEnable = cacheManager.getCache(Const.CahceNameEhcacheEnable);
        this.cacheWhitelist = cacheManager.getCache(Const.CacheNameEhcacheWhitelist);
        this.cacheRequestCounter = cacheManager.getCache(Const.CacheNameEhcacheRequestCounter);
        jedisCluster = ctx.getBean(JedisCluster.class);
    }

    @Override
    public void doFilter(ServletRequest servletRequest,
                         ServletResponse servletResponse,
                         FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        String clientIp = RequestUtils.getRemoteAddress(request);
        String key = Const.CacheKeyPrefixWhitelist + clientIp;

        // 为了提升性能先判断ehcache是否存在ip白名单
        Element element = this.cacheWhitelist.get(key);
        if(element == null) {
            if (!jedisCluster.exists(key)) {
                boolean enabled = false;
                element = this.cacheEnable.get(Const.CacheKeyEnable);
                if (element != null) {
                    enabled = (Boolean) element.getObjectValue();
                }

                if (enabled) {

                    // 机制启动后，记录ip请求次数
                    Element elementRequestCounter = this.cacheRequestCounter.get(clientIp);
                    if(elementRequestCounter == null) {
                        elementRequestCounter = new Element(clientIp, 0);
                    }
                    int count = (Integer)elementRequestCounter.getObjectValue()+1;
                    elementRequestCounter = new Element(clientIp, count);
                    elementRequestCounter.setTimeToLive(TimeoutSeconds);
                    this.cacheRequestCounter.put(elementRequestCounter);

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
            } else {
                // redis存在此ip白名单时，加载redis中的ip白名单到ehcache以提升性能
                long seconds = jedisCluster.ttl(key);
                if (seconds > 0) {
                    element = new Element(key, StringUtils.EMPTY);
                    element.setTimeToLive((int) seconds);
                    this.cacheWhitelist.put(element);
                }
            }
        }

        filterChain.doFilter(servletRequest, servletResponse);
    }

    @Override
    public void destroy() {

    }
}
