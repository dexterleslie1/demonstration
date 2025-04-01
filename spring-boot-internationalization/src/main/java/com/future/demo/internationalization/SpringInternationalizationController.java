package com.future.demo.internationalization;

import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.Locale;

/**
 * @author dexterleslie@gmail.com
 */
@Controller
public class SpringInternationalizationController {

    @GetMapping("/")
    public String view() {
        Locale locale = LocaleContextHolder.getLocale();
        System.out.println("当前locale:" + locale);
        return "i18n";
    }

}
