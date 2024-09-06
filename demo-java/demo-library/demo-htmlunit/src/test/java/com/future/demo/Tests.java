package com.future.demo;

import cn.hutool.http.HttpUtil;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class Tests {
    @Test
    public void test() throws IOException {
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
}
