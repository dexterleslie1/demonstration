package com.future.demo.servlet;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Random;
import java.util.UUID;

/**
 * 通过BCryptPasswordEncoder产生cpu消耗，辅助cc测试
 */
public class CpuConsumptionServlet extends HttpServlet {

    private static final long serialVersionUID = -4751096228274971485L;
    private final static PasswordEncoder PASSWORD_ENCODER = new BCryptPasswordEncoder();

//    private final static Random RANDOM = new Random();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String uuid = UUID.randomUUID().toString();
        String encoded = PASSWORD_ENCODER.encode(uuid);
//        int randomInt = RANDOM.nextInt(10000);
//        if(randomInt <= 0) {
//            randomInt = 5;
//        }
//        String uuid = RandomStringUtils.random(randomInt);
//        String encoded = DigestUtils.md5Hex(uuid);
        resp.setContentType("text/plain;charset=utf-8");
        resp.getWriter().write("encoded=" + encoded);
    }
}