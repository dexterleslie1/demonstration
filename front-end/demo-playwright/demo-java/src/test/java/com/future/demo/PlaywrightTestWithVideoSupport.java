package com.future.demo;

import com.microsoft.playwright.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestInfo;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Playwright 测试基类，支持边测试边录制视频。
 * 通过系统属性 {@code playwright.record.video=true} 开启录制，视频输出到 {@code target/playwright-videos/}，
 * 文件名为「测试类名_测试方法名_yyyyMMddHHmmss.webm」，便于与测试用例对应且避免重名。
 * <p>
 * 注意：Playwright 自带的录制仅捕获<strong>视口内的页面内容</strong>，不包含浏览器外壳（地址栏、标签栏等）。
 * 若需录制整个浏览器窗口（含地址栏），需使用系统级或第三方录屏工具（如 OBS、FFmpeg 窗口捕获等）。
 * </p>
 * 运行示例：{@code mvn test -Dplaywright.record.video=true}
 */
@SpringBootTest(classes = Application.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
abstract class PlaywrightTestWithVideoSupport {

    @LocalServerPort
    protected int port;

    protected Playwright playwright;
    protected Browser browser;
    protected BrowserContext context;
    protected Page page;

    /** 测试开始时间，用于视频文件名中的 yyyyMMddHHmmss */
    private LocalDateTime testStartTime;

    protected static boolean isRecordVideoEnabled() {
        return "true".equalsIgnoreCase(System.getProperty("playwright.record.video", "false"));
    }

    @BeforeEach
    void before() {
        testStartTime = LocalDateTime.now();
        playwright = Playwright.create();
        browser = playwright.chromium().launch(new BrowserType.LaunchOptions().setHeadless(false));
        if (isRecordVideoEnabled()) {
            // 开启录制时指定视频输出目录，实际文件名在 after 中按测试名保存并删除默认 hash 文件
            context = browser.newContext(new Browser.NewContextOptions()
                    .setRecordVideoDir(Paths.get("target/playwright-videos")));
        } else {
            context = browser.newContext();
        }
        page = context.newPage();
    }

    @AfterEach
    void after(TestInfo testInfo) {
        if (context != null) {
            // 若开启录制，关闭 context 时 Playwright 会在此路径下生成一份 hash 命名的视频，用于后续删除避免重复
            Path defaultVideoPath = null;
            if (isRecordVideoEnabled() && page != null) {
                Video video = page.video();
                if (video != null) {
                    // 先拿到默认视频路径（context 关闭时会写入该文件）
                    defaultVideoPath = video.path();
                    page.close();
                    // 按「测试类名_测试方法名_测试开始时间」保存视频，便于与用例对应
                    String videoFileName = videoFileNameFromTestInfo(testInfo, testStartTime);
                    Path videoDir = Paths.get("target/playwright-videos");
                    try {
                        Files.createDirectories(videoDir);
                        video.saveAs(videoDir.resolve(videoFileName + ".webm"));
                    } catch (IOException e) {
                        throw new RuntimeException("保存视频失败: " + videoFileName, e);
                    } finally {
                        // 删除 Playwright 自动生成的 hash 命名视频，仅保留上面保存的测试命名视频
                        if (defaultVideoPath != null) {
                            try {
                                Files.deleteIfExists(defaultVideoPath);
                            } catch (IOException ignored) {
                            }
                        }
                    }
                } else {
                    page.close();
                }
            }
            context.close();
        }
        if (playwright != null) {
            playwright.close();
        }
    }

    /**
     * 根据当前测试信息及测试开始时间生成视频文件名（不含扩展名），格式：测试类名_测试方法名_yyyyMMddHHmmss，非法文件名字符替换为下划线。
     */
    private static String videoFileNameFromTestInfo(TestInfo testInfo, LocalDateTime startTime) {
        String className = testInfo.getTestClass().map(Class::getSimpleName).orElse("Test");
        String methodName = testInfo.getTestMethod().map(m -> m.getName()).orElse("unknown");
        String timestamp = startTime.format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        String base = className + "_" + methodName + "_" + timestamp;
        return base.replaceAll("[\\\\/:*?\"<>|\\s]+", "_");
    }
}
