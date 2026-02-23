package com.future.demo;

import com.microsoft.playwright.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;

// 等价于 Selenium WebDriverCreationAndDestroyTests：测试 Playwright 创建和销毁
@SpringBootTest(classes = Application.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class PlaywrightCreationAndDestroyTests {

    @LocalServerPort
    private int port;

    private Playwright playwright;
    private Browser browser;
    private Page page;

    @BeforeEach
    void before() {
        playwright = Playwright.create();
        browser = playwright.chromium().launch(new BrowserType.LaunchOptions().setHeadless(false));
        page = browser.newPage();
    }

    @AfterEach
    void after() {
        if (playwright != null) playwright.close();
    }

    @Test
    void test() {
        page.navigate("http://localhost:" + port);
        page.waitForTimeout(2000);
    }
}
