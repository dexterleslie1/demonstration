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

// 选择器测试
@RunWith(SpringRunner.class)
@SpringBootTest(classes = {Application.class}, webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class SelectorTests {
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

    // 测试根据多个 css class 查询元素
    @Test
    public void testFindElementByUsingCssSelectorWithMultipleClassNames() throws InterruptedException {
        driver.get("https://www.baidu.com");
        List<WebElement> elementList = driver.findElements(By.cssSelector("[class=\"mnav c-font-normal c-color-t\"]"));
        elementList.get(0).click();

        TimeUnit.SECONDS.sleep(2);
    }
}
