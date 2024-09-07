package com.future.demo;

import cn.hutool.http.HttpUtil;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class Tests {
    /**
     * 测试使用htmlunit抓取指定url内容
     *
     * @throws IOException
     */
    @Test
    public void testScrapeHtmlAsString() throws IOException {
        // 使用球探体育协助测试htmlunit
        // https://www.titan007.com/
        // 选择一场英超赛事并获取欧洲赔率链接如下所示：
        String url = "https://1x2.titan007.com/oddslist/2590935.htm";
        String content = HttpUtil.get(url, StandardCharsets.UTF_8);

        Document document = Jsoup.parse(content);

        // 因为使用hutool HttpUtil爬取指定url内容后不会执行javascript
        // 所以无法爬取javascript动态生成的内容
        Element elementTable = document.getElementById("oddsList_tab");
        Assert.assertNull(elementTable);

        try (final WebClient webClient = new WebClient()) {
            // 配置WebClient（可选），例如设置浏览器代理、禁用/启用JavaScript等
            // 例如，禁用CSS以加快页面加载速度（但可能导致页面渲染不正确）
            webClient.getOptions().setJavaScriptEnabled(true);
            webClient.getOptions().setCssEnabled(false);

            // 加载网页
            HtmlPage page = webClient.getPage(url);

            content = page.asXml();
            document = Jsoup.parse(content);

            // 使用htmlunit会执行javascript
            // 所以能够抓取javascript渲染的动态内容
            elementTable = document.getElementById("oddsList_tab");
            Assert.assertNotNull(elementTable);
        }
    }

    /**
     * 测试抓取指定url内容直到所有javascript加载完毕动态内容
     */
    @Test
    public void testScrapeHtmlAsStringUntilDynamicContentLoaded() throws IOException {
        // 使用球探体育协助测试htmlunit
        // https://www.titan007.com/
        // 选择一系列英超未来赛程链接如下所示：
        String url = "https://zq.titan007.com/cn/League/36.html";
        try (final WebClient webClient = new WebClient()) {
            webClient.getOptions().setJavaScriptEnabled(true);
            webClient.getOptions().setCssEnabled(false);

            HtmlPage page = webClient.getPage(url);

            String content = page.asXml();
            Document document = Jsoup.parse(content);

            Element elementTable = document.getElementById("Table3");
            Elements elementTrList = elementTable.select(">tbody>tr[id]");
            // javascript未完成加载动态内容，所以没有tr
            Assert.assertTrue(elementTrList.isEmpty());
        }

        try (final WebClient webClient = new WebClient()) {
            webClient.getOptions().setJavaScriptEnabled(true);
            webClient.getOptions().setCssEnabled(false);

            HtmlPage page = webClient.getPage(url);

            // 等待javascript加载动态内容完毕
            // 最大等待5秒
            webClient.waitForBackgroundJavaScript(5000);

            String content = page.asXml();
            Document document = Jsoup.parse(content);

            Element elementTable = document.getElementById("Table3");
            Elements elementTrList = elementTable.select(">tbody>tr[id]");
            // javascript未完成加载动态内容，所以没有tr
            Assert.assertTrue(!elementTrList.isEmpty());
        }
    }
}
