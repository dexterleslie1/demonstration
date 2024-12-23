package com.future.demo;

import org.junit.Test;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.remote.RemoteWebDriver;

import java.net.MalformedURLException;
import java.net.URL;
import java.time.Duration;

public class AppTests {
    @Test
    public void test() throws MalformedURLException {
        // 创建 WebDriver 实例
        WebDriver driver = new RemoteWebDriver(new URL("http://localhost:4444/wd/hub"), new FirefoxOptions());

        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(5));
        driver.get("https://www.baidu.com");
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(5));

        // 关闭浏览器
        driver.quit();
    }
}
