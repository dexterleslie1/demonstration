package com.future.demo.unify.gateway.sms;

import com.yyd.common.http.HttpUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
public class SmsCaptchaSendFilter extends OncePerRequestFilter {
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String phone = request.getParameter("phone");
        if(StringUtils.isBlank(phone)) {
            HttpUtil.responseWithError(response, HttpStatus.BAD_REQUEST, "没有指定手机号码参数");
            return;
        }

        String captcha = "111111";
        log.info("发送短信验证码到手机号码：" + captcha);
        HttpUtil.response(response, "短信验证码已发送");
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        String path = request.getServletPath();
        return !path.startsWith("/api/v1/captcha/sms/send");
    }
}
