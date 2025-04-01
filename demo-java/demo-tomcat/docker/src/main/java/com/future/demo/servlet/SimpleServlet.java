package com.future.demo.servlet;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;

/**
 * @author dexterleslie@gmail.com
 */
public class SimpleServlet extends HttpServlet {

    private static final long serialVersionUID = -4751096228274971485L;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("text/plain;charset=utf-8");
        Date date = new Date();
        resp.getWriter().write("系统当前时间:" + date);
    }
}