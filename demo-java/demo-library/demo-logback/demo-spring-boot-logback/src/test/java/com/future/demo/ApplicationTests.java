package com.future.demo;

import com.future.demo.package1.Tester1;
import com.future.demo.package2.Tester2;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {Application.class}, webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class ApplicationTests {
    final static Logger logger = LoggerFactory.getLogger(Application.class);
    final static Logger namedLogger = LoggerFactory.getLogger("namedLogger");

    @Test
    public void test() throws Exception {
        logger.debug("test method is called");

        namedLogger.debug("named logger输出日志");

        Tester1 tester = new Tester1();
        tester.method();

        Tester2 tester2 = new Tester2();
        tester2.method();
    }

}
