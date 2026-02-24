package com.future.demo;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.TimeUnit;

// 演示 JavascriptExecutor 用法（如 scrollIntoView）
@RunWith(SpringRunner.class)
@SpringBootTest(classes = {Application.class}, webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class JavascriptExecutorTests {
    WebDriver driver;

    @Before
    public void before() throws MalformedURLException {
        driver = new RemoteWebDriver(new URL("http://localhost:4444/wd/hub"), new FirefoxOptions());
        driver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);
    }

    @After
    public void after() {
        if (driver != null) {
            driver.quit();
        }
    }

    // 演示 scrollIntoView：将元素滚动到视口内（true 表示顶部对齐）
    @Test
    public void testScrollIntoView() throws InterruptedException {
        driver.get("http://192.168.1.182:8080/iframe-testing-support");

        WebElement element = driver.findElement(By.id("iframe2"));
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", element);

        TimeUnit.SECONDS.sleep(5);
    }
}
