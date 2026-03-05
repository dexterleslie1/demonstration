package com.future.demo;

import com.microsoft.playwright.Locator;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

// 等价于 Selenium XPathTests：根据 class 和元素 text 定位元素
class XPathTests extends PlaywrightTestWithVideoSupport {

    @Test
    void testFindElementByUsingXPathWithClassNameAndInnerHTML() {
        page.navigate("http://localhost:" + port);
        Locator element = page.locator("xpath=//span[@class='c-text' and text()='早']");
        assertThat(element).isVisible();
        Assertions.assertNotNull(element.first());
    }
}
