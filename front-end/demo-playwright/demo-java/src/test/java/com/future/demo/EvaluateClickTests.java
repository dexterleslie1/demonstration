package com.future.demo;

import com.microsoft.playwright.*;
import org.junit.Assert;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

/**
 * 按钮演示：button.click() 与 evaluate("el => el.click()") 的区别。
 *
 * <ul>
 *   <li>button.click()：模拟真实用户点击（移动鼠标、派发 pointer/mouse 事件），会受“谁在最上层”影响，
 *       且会做可操作性检查（可见、可接收事件等）。被遮罩挡住时，会点到遮罩或报错。</li>
 *   <li>evaluate("el => el.click()")：在浏览器里直接调用该 DOM 元素的 .click() 方法，
 *       不经过坐标和遮挡判断，不滚动、不派发鼠标序列。被遮罩挡住时仍能触发该按钮的 click 事件。</li>
 * </ul>
 */
@SpringBootTest(classes = Application.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class EvaluateClickTests {

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
        page.setDefaultNavigationTimeout(5000);
        page.setDefaultTimeout(5000);
    }

    @AfterEach
    void after() {
        if (playwright != null) playwright.close();
    }

    @Test
    void testClickCoveredButtonByEvaluate_triggersButtonHandler() {
        page.navigate("http://localhost:" + port + "/button-evaluate-demo");

        Locator coveredBtn = page.locator("#covered-btn");
        // 直接调用该元素的 .click()，不经过“谁在最上层”，故能触发被遮罩挡住的按钮的 click 事件
        coveredBtn.evaluate("el => el.click()");

        page.waitForTimeout(300);
        assertThat(page.locator("#covered-result")).hasText("按钮的 click 被触发");
    }

    @Test
    void testClickCoveredButtonByNormalClick_hitsOverlay() {
        page.navigate("http://localhost:" + port + "/button-evaluate-demo");

        Locator coveredBtn = page.locator("#covered-btn");
        try {
            // 模拟真实点击：会点到视口内最上层的元素（遮罩），不会触发按钮的 click
            coveredBtn.click();
            Assertions.fail("没有抛出预期异常");

            page.waitForTimeout(300);
            assertThat(page.locator("#covered-result")).hasText("点到了遮罩层");
        } catch (TimeoutError ignored) {
            // 因为按钮元素被遮盖，所以无法点击抛出超时错误
        }
    }
}
