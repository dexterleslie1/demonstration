package com.future.demo;

import com.microsoft.playwright.PlaywrightException;
import org.junit.Before;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;

// 等价于 Selenium TimeoutTests：驱动 timeout 选项测试（隐式等待超时）
class TimeoutTests extends PlaywrightTestWithVideoSupport {

    @BeforeEach
    void setContextTimeout() {
        context.setDefaultTimeout(5000);
    }

    // 等价于 Selenium testImplicitlyWait：设置默认超时后查找不存在的元素，断言等待时间 >= 5 秒
    @Test
    void testImplicitlyWait() {
        page.navigate("http://localhost:" + port);

        // 使用合法且必然不存在的选择器：CSS #id 不能以数字开头，直接用 UUID 会立即报错
        String nonexistentId = "nope-" + UUID.randomUUID();
        long start = System.currentTimeMillis();
        try {
            page.locator("[id='" + nonexistentId + "']").click();
            Assertions.fail("expected timeout");
        } catch (PlaywrightException ignored) {
        }
        long elapsed = System.currentTimeMillis() - start;
        Assertions.assertTrue(elapsed >= 4000, "expected wait >= 4s, was " + elapsed + "ms");
    }
}
