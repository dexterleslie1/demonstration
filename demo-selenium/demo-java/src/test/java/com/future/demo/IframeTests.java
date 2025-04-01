package com.future.demo;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.concurrent.TimeUnit;

// iframe 测试
// https://www.selenium.dev/documentation/webdriver/interactions/frames/
@RunWith(SpringRunner.class)
@SpringBootTest(classes = {Application.class}, webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class IframeTests {
    WebDriver driver;

    @Before
    public void before() throws MalformedURLException {
        // 创建 WebDriver 实例
        driver = new RemoteWebDriver(new URL("http://localhost:4444/wd/hub"), new FirefoxOptions());
        driver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);
    }

    @After
    public void after() {
        if (driver != null) {
            // 关闭浏览器
            driver.quit();
        }
    }

    // 测试切换 iframe 并查找元素
    @Test
    public void testSwitchToIframeAndFindElements() throws InterruptedException {
        driver.get("http://localhost:8080/iframe-testing-support");

        // 切换到 iframe 上下文
        WebElement iframe1 = driver.findElement(By.id("iframe1"));
        driver.switchTo().frame(iframe1);

        // 在 iframe 中查找元素并点击
        List<WebElement> elementList = driver.findElements(By.cssSelector("[class=\"ntes-nav-index-title ntes-nav-entry-wide c-fl\"]"));
        elementList.get(0).click();

        TimeUnit.SECONDS.sleep(5);
    }
}
