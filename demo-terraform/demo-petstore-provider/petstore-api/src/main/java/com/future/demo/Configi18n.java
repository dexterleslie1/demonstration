package com.future.demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;

import javax.annotation.PostConstruct;
import java.util.Locale;

/**
 * 配置后端多语言支持
 */
@Configuration
public class Configi18n implements WebMvcConfigurer {

    @Autowired
    MessageSource messageSource;

    @PostConstruct
    void init() {
        if (this.messageSource instanceof ReloadableResourceBundleMessageSource) {
            ReloadableResourceBundleMessageSource reloadableResourceBundleMessageSource =
                    (ReloadableResourceBundleMessageSource) this.messageSource;
            // 配置 i18n 加载本地多语言文件
            reloadableResourceBundleMessageSource.addBasenames("classpath:i18n/messages", "classpath:i18n/messages-ecommerce-common");
        }
    }

    @Bean
    public LocaleResolver localeResolver() {
        SessionLocaleResolver lr = new SessionLocaleResolver();
        lr.setDefaultLocale(Locale.SIMPLIFIED_CHINESE);
        return lr;
    }

    /**
     * 请求时使用lang参数作为当前语言环境
     *
     * @param registry
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        LocaleChangeInterceptor interceptor = new LocaleChangeInterceptor();
        interceptor.setParamName("lang");
        registry.addInterceptor(interceptor);
    }
}
