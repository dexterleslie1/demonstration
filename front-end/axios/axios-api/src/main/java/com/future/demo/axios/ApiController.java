package com.future.demo.axios;

import com.yyd.common.http.response.ObjectResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/v1")
public class ApiController {
    @Value("${spring.application.name}")
    String applicationName;

    // 演示axios put调用
    @PutMapping("putWithBody")
    ObjectResponse<String> putWithBody(
            @RequestBody(required = false) LoginForm loginForm,
            @RequestHeader(value = "header1", defaultValue = "") String header1,
            @RequestParam(value = "param1", defaultValue = "") String param1) {
        Assert.isTrue(loginForm!=null, "没有提供登录参数");
        Assert.isTrue(StringUtils.hasText(loginForm.getUsername()), "没有提供username参数");
        Assert.isTrue(StringUtils.hasText(loginForm.getPassword()), "没有提供password参数");
        Assert.isTrue(StringUtils.hasText(loginForm.getVerificationCode()), "没有提供验证码参数");
        Assert.isTrue(StringUtils.hasText(header1), "没有提供header1参数");
        Assert.isTrue(StringUtils.hasText(param1), "没有提供param1参数");
        log.debug("username={},password={},verificationCode={},header1={},param1={}",
                loginForm.getUsername(),
                loginForm.getPassword(),
                loginForm.getVerificationCode(),
                header1,
                param1);
        ObjectResponse<String> response = new ObjectResponse<>();
        response.setData("username=" + loginForm.getUsername() + ",password=" + loginForm.getPassword()
                + ",verificationCode=" + loginForm.getVerificationCode() + ",header1=" + header1
                + ",param1=" + param1);
        return response;
    }

    // 演示axios post调用
    @PostMapping("postWithBody")
    ObjectResponse<String> postWithBody(
            @RequestBody(required = false) LoginForm loginForm,
            @RequestHeader(value = "header1", defaultValue = "") String header1,
            @RequestParam(value = "param1", defaultValue = "") String param1) {
        Assert.isTrue(loginForm!=null, "没有提供登录参数");
        Assert.isTrue(StringUtils.hasText(loginForm.getUsername()), "没有提供username参数");
        Assert.isTrue(StringUtils.hasText(loginForm.getPassword()), "没有提供password参数");
        Assert.isTrue(StringUtils.hasText(loginForm.getVerificationCode()), "没有提供验证码参数");
        Assert.isTrue(StringUtils.hasText(header1), "没有提供header1参数");
        Assert.isTrue(StringUtils.hasText(param1), "没有提供param1参数");
        log.debug("username={},password={},verificationCode={},header1={},param1={}",
                loginForm.getUsername(),
                loginForm.getPassword(),
                loginForm.getVerificationCode(),
                header1,
                param1);
        ObjectResponse<String> response = new ObjectResponse<>();
        response.setData("username=" + loginForm.getUsername() + ",password=" + loginForm.getPassword()
                + ",verificationCode=" + loginForm.getVerificationCode() + ",header1=" + header1
                + ",param1=" + param1);
        return response;
    }

    // 演示axios delete调用
    @DeleteMapping("delete")
    ObjectResponse<String> delete(
            @RequestParam(value = "param1", defaultValue = "") String param1,
            @RequestHeader(value = "header1", defaultValue = "") String header1) {
        Assert.isTrue(StringUtils.hasText(header1), "没有提供header1参数");
        Assert.isTrue(StringUtils.hasText(param1), "没有提供param1参数");
        ObjectResponse<String> response = new ObjectResponse<>();
        response.setData("param1=" + param1 + ",header1=" + header1);
        return response;
    }

    // 演示axios get调用
    @GetMapping("get")
    ObjectResponse<String> get(
            @RequestParam(value = "param1", defaultValue = "") String param1,
            @RequestHeader(value = "header1", defaultValue = "") String header1) {
        Assert.isTrue(StringUtils.hasText(header1), "没有提供header1参数");
        Assert.isTrue(StringUtils.hasText(param1), "没有提供param1参数");
        ObjectResponse<String> response = new ObjectResponse<>();
        response.setData("param1=" + param1 + ",header1=" + header1);
        return response;
    }

    // 演示axios get返回text/plain
    @GetMapping(value = "1.txt", produces = {MediaType.TEXT_PLAIN_VALUE})
    String getTextPlain(
            @RequestParam(value = "param1", defaultValue = "") String param1,
            @RequestHeader(value = "header1", defaultValue = "") String header1) {
        return "param1=" + param1 + ",header1=" + header1;
    }
}
