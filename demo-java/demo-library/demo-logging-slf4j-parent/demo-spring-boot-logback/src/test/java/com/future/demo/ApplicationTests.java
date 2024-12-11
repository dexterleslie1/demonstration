package com.future.demo;

import com.future.demo.package1.Tester1;
import com.future.demo.package2.Tester2;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.UUID;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {Application.class}, webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class ApplicationTests {
    final static Logger logger = LoggerFactory.getLogger(Application.class);
    final static Logger consoleAppenderDefaultLogger = LoggerFactory.getLogger("consoleAppenderDefaultLogger");
    final static Logger namedLogger = LoggerFactory.getLogger("namedLogger");
    final static Logger mdcLogger = LoggerFactory.getLogger("mdcLogger");
    final static Logger jsonLogger = LoggerFactory.getLogger("jsonLogger");

    @Test
    public void test() throws Exception {
        logger.debug("Hello world!");
        logger.info("Hello world!");

        consoleAppenderDefaultLogger.debug("consoleAppenderDefaultLogger输出日志");

        namedLogger.debug("named logger输出日志");

        Tester1 tester = new Tester1();
        tester.method();

        Tester2 tester2 = new Tester2();
        tester2.method();

        // slf4j mdc用法
        MDC.put("my-mdc1", UUID.randomUUID().toString());
        mdcLogger.debug("测试slf4j mdc");

        // json encoder测试
        jsonLogger.debug("测试json encoder");
    }

}
