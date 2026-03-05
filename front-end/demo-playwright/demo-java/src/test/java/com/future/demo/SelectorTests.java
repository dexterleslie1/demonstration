package com.future.demo;

import com.microsoft.playwright.Locator;
import org.junit.Before;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

// 等价于 Selenium SelectorTests：选择器测试（根据多个 css class 查询元素并点击）
class SelectorTests extends PlaywrightTestWithVideoSupport {

    @BeforeEach
    void setPageTimeout() {
        page.setDefaultNavigationTimeout(15000);
    }

    @Test
    void testFindElementByUsingCssSelectorWithMultipleClassNames() {
        page.navigate("https://www.baidu.com");
        Locator navLinks = page.locator("[class=\"mnav c-font-normal c-color-t\"]").first();
        navLinks.click();
        page.waitForTimeout(2000);
    }
}
