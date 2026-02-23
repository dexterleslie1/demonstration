package com.future.demo;

import com.microsoft.playwright.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

// 等价于 Selenium SelectorTests：选择器测试（根据多个 css class 查询元素并点击）
@SpringBootTest(classes = Application.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class SelectorTests {

    @LocalServerPort
    private int port;

    private Playwright playwright;
    private Browser browser;
    private Page page;

    @BeforeEach
    void before() {
        playwright = Playwright.create();
        browser = playwright.chromium().launch(new BrowserType.LaunchOptions().setHeadless(false));
        page = browser.newContext().newPage();
        page.setDefaultNavigationTimeout(15000);
    }

    @AfterEach
    void after() {
        if (playwright != null) playwright.close();
    }

    @Test
    void testFindElementByUsingCssSelectorWithMultipleClassNames() {
        page.navigate("https://www.baidu.com");
        Locator navLinks = page.locator("[class=\"mnav c-font-normal c-color-t\"]").first();
        navLinks.click();
        page.waitForTimeout(2000);
    }
}
