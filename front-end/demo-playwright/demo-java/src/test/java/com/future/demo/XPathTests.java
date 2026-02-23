package com.future.demo;

import com.microsoft.playwright.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

// 等价于 Selenium XPathTests：根据 class 和元素 text 定位元素
@SpringBootTest(classes = Application.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class XPathTests {

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
    void testFindElementByUsingXPathWithClassNameAndInnerHTML() {
        page.navigate("http://localhost:" + port);
        Locator element = page.locator("xpath=//span[@class='c-text' and text()='早']");
        assertThat(element).isVisible();
        Assertions.assertNotNull(element.first());
    }
}
