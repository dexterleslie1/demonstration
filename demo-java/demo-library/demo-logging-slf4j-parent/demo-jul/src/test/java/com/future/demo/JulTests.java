package com.future.demo;

import org.junit.Test;

import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class JulTests {
    // 测试使用默认配置打印日志
    @Test
    public void test1() {
        // 获取日志记录器对象
        Logger logger = Logger.getLogger(JulTests.class.getName());

        // 日志记录输出
        logger.severe("Hello world!");
        logger.warning("Hello world!");
        logger.info("Hello world!");
        logger.config("Hello world!");
        logger.fine("Hello world!");
        logger.finer("Hello world!");
        logger.finest("Hello world!");

        // 输出指定日志级别的日志记录
        logger.log(Level.WARNING, "Hello world! Warning level!");

        // 占位符
        String name = "John";
        int age = 20;
        logger.log(Level.INFO, "Hello {0}, your age is {1}", new Object[]{name, age});

        // 异常日志记录
        logger.log(Level.SEVERE, "Error occurred!", new Exception("Test exception"));
    }

    // 测试打印所有级别日志
    @Test
    public void test2() {
        // 获取日志记录器对象
        Logger logger = Logger.getLogger(JulTests.class.getName());

        // 关闭系统默认配置
        logger.setUseParentHandlers(false);

        // 定义配置日志级别
        ConsoleHandler handler = new ConsoleHandler();

        // 转换对象
        SimpleFormatter formatter = new SimpleFormatter();

        // 关联
        handler.setFormatter(formatter);
        logger.addHandler(handler);

        // 设置日志级别
        handler.setLevel(Level.ALL);
        logger.setLevel(Level.ALL);

        // 日志记录输出
        logger.severe("Hello world!");
        logger.warning("Hello world!");
        logger.info("Hello world!");
        logger.config("Hello world!");
        logger.fine("Hello world!");
        logger.finer("Hello world!");
        logger.finest("Hello world!");

        // 输出指定日志级别的日志记录
        logger.log(Level.WARNING, "Hello world! Warning level!");

        // 占位符
        String name = "John";
        int age = 20;
        logger.log(Level.INFO, "Hello {0}, your age is {1}", new Object[]{name, age});

        // 异常日志记录
        logger.log(Level.SEVERE, "Error occurred!", new Exception("Test exception"));
    }
}
