package com.future.demo;

import org.junit.jupiter.api.Test;

// 等价于 Selenium WebDriverCreationAndDestroyTests：测试 Playwright 创建和销毁
class PlaywrightCreationAndDestroyTests extends PlaywrightTestWithVideoSupport {

    @Test
    void test() {
        page.navigate("http://localhost:" + port);
        page.waitForTimeout(2000);
    }
}
