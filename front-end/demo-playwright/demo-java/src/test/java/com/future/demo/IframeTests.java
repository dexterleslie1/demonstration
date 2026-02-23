package com.future.demo;

import com.microsoft.playwright.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;

// 等价于 Selenium IframeTests：iframe 测试（切换 iframe 并查找/点击元素）
@SpringBootTest(classes = Application.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class IframeTests {

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
        page.setDefaultNavigationTimeout(30000);
    }

    @AfterEach
    void after() {
        if (playwright != null) playwright.close();
    }

    @Test
    void testSwitchToIframeAndFindElements() {
        page.navigate("http://localhost:" + port + "/iframe-testing-support");
        FrameLocator frame1 = page.frameLocator("#iframe1");
        Locator navLink = frame1.locator("[class=\"ntes-nav-index-title ntes-nav-entry-wide c-fl\"]").first();
        navLink.click();
        page.waitForTimeout(2000);
    }

    @Test
    void testSwitchBetweenTwoIframes() {
        page.navigate("http://localhost:" + port + "/iframe-testing-support");

        FrameLocator frame1 = page.frameLocator("#iframe1");
        FrameLocator frame2 = page.frameLocator("#iframe2");

        frame1.locator("[class=\"ntes-nav-index-title ntes-nav-entry-wide c-fl\"]").first().click();
        page.waitForTimeout(2000);

        frame2.locator("[class=\"ntes-nav-index-title ntes-nav-entry-wide c-fl\"]").first().click();
        page.waitForTimeout(2000);

        frame1.locator("[class=\"ntes-nav-index-title ntes-nav-entry-wide c-fl\"]").first().click();
        page.waitForTimeout(2000);
    }
}
