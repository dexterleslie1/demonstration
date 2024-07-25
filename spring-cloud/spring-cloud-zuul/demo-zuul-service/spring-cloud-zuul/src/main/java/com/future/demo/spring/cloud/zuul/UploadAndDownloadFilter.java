package com.future.demo.spring.cloud.zuul;

import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;

import javax.servlet.http.HttpServletRequest;
import java.util.UUID;

public class UploadAndDownloadFilter extends ZuulFilter {

    @Override
    public String filterType() {
        return "pre"; // 在请求被路由之前执行
    }

    @Override
    public int filterOrder() {
        return 1; // Filter的执行顺序
    }

    @Override
    public boolean shouldFilter() {
        RequestContext ctx = RequestContext.getCurrentContext();
        HttpServletRequest request = ctx.getRequest();
        // 基于请求URL或其他信息决定是否应用此Filter
        String requestURI = request.getRequestURI();
        // 仅对上传和下载接口应用此filter
        return requestURI.startsWith("/api/v1/upload") || requestURI.startsWith("/api/v1/download");
    }

    @Override
    public Object run() {
        RequestContext ctx = RequestContext.getCurrentContext();
        HttpServletRequest request = ctx.getRequest();

        // 添加一个名为"token"
        ctx.addZuulRequestHeader("token", UUID.randomUUID().toString());

        return null;
    }
}
