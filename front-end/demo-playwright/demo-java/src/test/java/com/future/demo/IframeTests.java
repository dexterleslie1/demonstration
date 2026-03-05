package com.future.demo;

import com.microsoft.playwright.FrameLocator;
import com.microsoft.playwright.Locator;
import org.junit.Before;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

// 等价于 Selenium IframeTests：iframe 测试（切换 iframe 并查找/点击元素）
class IframeTests extends PlaywrightTestWithVideoSupport {

    @BeforeEach
    void setPageTimeout() {
        page.setDefaultNavigationTimeout(30000);
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
