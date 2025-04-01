package com.future.demo.spring.cloud.feign.consumer.config;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;


/**
 * 所有feign调用http头都注入my-header参数
 * https://developer.aliyun.com/article/1058305
 */
@Slf4j
public class MyRequestInterceptor implements RequestInterceptor {
    @Override
    public void apply(RequestTemplate template) {
        template.header("my-header", "my-value");

        // https://stackoverflow.com/questions/559155/how-do-i-get-a-httpservletrequest-in-my-spring-beans
        if (RequestContextHolder.getRequestAttributes() != null) {
            HttpServletRequest request =
                    ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes())
                            .getRequest();
            String contextUserId = request.getParameter("contextUserId");
            if (StringUtils.hasText(contextUserId)) {
                template.query("contextUserId", contextUserId);
                log.debug("feign客户端成功注入上下文参数，contextUserId={}", contextUserId);
            }
        }
    }
}
