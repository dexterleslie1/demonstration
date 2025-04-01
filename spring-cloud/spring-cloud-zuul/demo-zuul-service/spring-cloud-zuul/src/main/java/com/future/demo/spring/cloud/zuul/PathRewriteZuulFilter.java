package com.future.demo.spring.cloud.zuul;

import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import org.apache.commons.lang.StringUtils;
import org.springframework.cloud.netflix.zuul.filters.pre.PreDecorationFilter;
import org.springframework.stereotype.Component;

import static org.springframework.cloud.netflix.zuul.filters.support.FilterConstants.REQUEST_URI_KEY;

@Component
public class PathRewriteZuulFilter extends ZuulFilter {
    @Override
    public String filterType() {
        return "pre";
    }

    @Override
    public int filterOrder() {
        return PreDecorationFilter.FILTER_ORDER + 1;
    }

    @Override
    public boolean shouldFilter() {
        RequestContext context = RequestContext.getCurrentContext();
        String originalRequestPath = (String) context.get(REQUEST_URI_KEY);
        // 只过滤 /api/v1/hello 开始的请求
        return originalRequestPath.startsWith("/api/v1/hello");
    }

    @Override
    public Object run() {
        RequestContext context = RequestContext.getCurrentContext();
        String originalRequestPath = (String) context.get(REQUEST_URI_KEY);
        String modifiedRequestPath = "/api/v1/a/b" + originalRequestPath.replace("/api/v1/hello", StringUtils.EMPTY);
        context.put(REQUEST_URI_KEY, modifiedRequestPath);
        return null;
    }
}