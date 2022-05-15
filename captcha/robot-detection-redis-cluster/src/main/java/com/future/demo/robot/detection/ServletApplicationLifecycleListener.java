package com.future.demo.robot.detection;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

public class ServletApplicationLifecycleListener implements ServletContextListener{

    @Override
    public void contextDestroyed(ServletContextEvent event) {
        // TODO 测试shutdown是否被调用
        WebApplicationContext applicationContext = WebApplicationContextUtils.getWebApplicationContext(event.getServletContext());
        if(applicationContext!=null){
            ApplicationLifecycle applicationLifecycle = applicationContext.getBean(ApplicationLifecycle.class);
            if(applicationLifecycle != null) {
                applicationLifecycle.shutdown();
            }
        }
    }

    @Override
    public void contextInitialized(ServletContextEvent event) {
        WebApplicationContext applicationContext = WebApplicationContextUtils.getWebApplicationContext(event.getServletContext());
        if(applicationContext!=null){
            ApplicationLifecycle applicationLifecycle = applicationContext.getBean(ApplicationLifecycle.class);
            if(applicationLifecycle != null) {
                applicationLifecycle.startup();
            }
        }
    }

}
