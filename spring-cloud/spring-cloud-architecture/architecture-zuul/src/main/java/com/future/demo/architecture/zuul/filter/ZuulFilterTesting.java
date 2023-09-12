package com.future.demo.architecture.zuul.filter;

import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.netflix.zuul.filters.support.FilterConstants;

import javax.servlet.http.HttpServletRequest;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
public class ZuulFilterTesting extends ZuulFilter {
    @Override
    public String filterType() {
        return FilterConstants.PRE_TYPE;
    }

    @Override
    public int filterOrder() {
        return 0;
    }

    @Override
    public boolean shouldFilter() {
        return true;
    }

    @Override
    public Object run() throws ZuulException {
        RequestContext ctx = RequestContext.getCurrentContext();
        HttpServletRequest request = ctx.getRequest();
        String url = request.getRequestURL().toString();
        log.debug("ZuulFilter拦截url=" + url);

        // 传递上下文参数
        Long contextUserId = 100L;

        // NOTE: 如果使用query params方式注入上下文参数会报告如下错误，所以使用http header方式注入上下文参数
        // Optional int parameter 'page' is present but cannot be translated into a null value due to being declared as a primitive type. Consider declaring it as object wrapper for the corresponding primitive type.
//        Map<String, List<String>> params = ctx.getRequestQueryParams();
//        if (params == null) {
//            params = new HashMap<>();
//        }
//        params.remove("contextUserId");
//        params.put("contextUserId", Collections.singletonList(String.valueOf(contextUserId)));
//        ctx.setRequestQueryParams(params);

        ctx.getZuulRequestHeaders().remove("contextUserId");
        ctx.getZuulRequestHeaders().put("contextUserId", String.valueOf(contextUserId));

        return null;
    }
}
