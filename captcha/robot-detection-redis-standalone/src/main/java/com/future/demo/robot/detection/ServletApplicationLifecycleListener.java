package com.future.demo.robot.detection;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import net.sf.ehcache.Cache;
import net.sf.ehcache.Element;
import org.apache.commons.lang.StringUtils;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

public class ServletApplicationLifecycleListener implements ServletContextListener{

    @Override
    public void contextDestroyed(ServletContextEvent event) {
        WebApplicationContext applicationContext = WebApplicationContextUtils.getWebApplicationContext(event.getServletContext());
        if(applicationContext!=null){

        }
    }

    @Override
    public void contextInitialized(ServletContextEvent event) {
        WebApplicationContext applicationContext = WebApplicationContextUtils.getWebApplicationContext(event.getServletContext());
        if(applicationContext!=null){
            CacheManagera cacheManagera = applicationContext.getBean(CacheManagera.class);
            Cache cacheEnable = cacheManagera.getCache(Const.CahceNameEhcacheEnable);
            JedisPool jedisPool = applicationContext.getBean(JedisPool.class);
            Jedis jedis = null;
            try {
                jedis = jedisPool.getResource();

                String value = jedis.get(Const.CacheKeyEnable);
                boolean enabled = true;
                if(!StringUtils.isEmpty(value)) {
                    try {
                        enabled = Boolean.parseBoolean(value);
                    } catch (Exception ignored) {

                    }
                }
                Element element = new Element(Const.CacheKeyEnable, enabled);
                cacheEnable.put(element);
            } finally {
                if(jedis != null) {
                    jedis.close();
                }
            }
        }
    }

}
