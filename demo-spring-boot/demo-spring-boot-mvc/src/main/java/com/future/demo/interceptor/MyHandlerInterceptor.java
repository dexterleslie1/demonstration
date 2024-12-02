package com.future.demo.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.Data;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

@Data
@Component
public class MyHandlerInterceptor implements HandlerInterceptor {

    boolean isPreHandle = false;
    boolean isPostHandle = false;
    boolean isAfterCompletion = false;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        isPreHandle = true;

        String preHandleReturnFalse = request.getParameter("preHandleReturnFalse");
        if (preHandleReturnFalse != null)
            return false;
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        isPostHandle = true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        isAfterCompletion = true;
    }

    public void reset() {
        isPreHandle = false;
        isPostHandle = false;
        isAfterCompletion = false;
    }

}
