package com.future.demo;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.StopWatch;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

// 驱动 timeout 选项测试
@RunWith(SpringRunner.class)
@SpringBootTest(classes = {Application.class}, webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class TimeoutTests {
    WebDriver driver;

    @Before
    public void before() throws MalformedURLException {
        // 创建 WebDriver 实例
        driver = new RemoteWebDriver(new URL("http://localhost:4444/wd/hub"), new FirefoxOptions());
    }

    @After
    public void after() {
        if (driver != null) {
            // 关闭浏览器
            driver.quit();
        }
    }

    // 测试 implicitlyWait timeout
    @Test
    public void testImplicitlyWait() throws InterruptedException {
        driver.get("http://localhost:8080");

        // implicitlyWait() 设置一个隐式等待时间。 Selenium 会在查找元素时，自动等待最长指定的时间。 如果元素在等待时间内出现，则测试继续；如果元素在等待时间内未出现，则抛出异常。
        driver.manage().timeouts().implicitlyWait(15, TimeUnit.SECONDS);

        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        try {
            driver.findElement(By.id(UUID.randomUUID().toString()));
            Assert.fail();
        } catch (NoSuchElementException ignored) {

        }
        stopWatch.stop();
        double seconds = stopWatch.getTotalTimeSeconds();
        Assert.assertTrue(seconds >= 15);
    }
}
