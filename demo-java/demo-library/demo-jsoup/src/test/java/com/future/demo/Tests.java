package com.future.demo;

import org.apache.commons.io.IOUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.TextNode;
import org.jsoup.select.Elements;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public class Tests {
    @Test
    public void test() throws IOException {
        // 使用jsoup解析HTML字符串
        Document document = Jsoup.parse(getHtml("test.html"));

        // 测试根据id获取元素
        Element elementDiv1 = document.getElementById("div1");
        Assert.assertEquals("div", elementDiv1.tagName());

        // 测试获取table>tbody下有id属性的所有tr
        Element elementTable1 = document.getElementById("table1");
        Elements trElementList = elementTable1.select("tbody > tr[id]");
        Assert.assertEquals(2, trElementList.size());
        trElementList.forEach(o -> {
            Assert.assertEquals("tr", o.tagName());
        });

        // 测试只选择直属元素
        Element elementDiv2 = document.getElementsByClass("div2").first();
        Elements div21ElementList = elementDiv2.select("> .div21");
        Assert.assertEquals(1, div21ElementList.size());
        Assert.assertEquals("<div class=\"div21\">\n" +
                " <div>\n" +
                "  <div class=\"div21\">\n" +
                "  </div>\n" +
                " </div>\n" +
                "</div>", div21ElementList.get(0).toString());
    }

    /**
     * @param resourceName 假设你的HTML文件位于classpath的根目录下，文件名为example.html
     * @return
     * @throws IOException
     */
    String getHtml(String resourceName) throws IOException {
        // 使用类加载器加载资源
        ClassLoader classLoader = Tests.class.getClassLoader();
        InputStream inputStream = classLoader.getResourceAsStream(resourceName);

        try {
            // 将输入流转化为字符串，因为Jsoup.parse()方法通常接受字符串或URL
            // 注意：这里使用了Apache Commons IO库来读取输入流为字符串，你需要添加这个依赖到你的项目中
            // 如果你不想使用外部库，你可以自己编写代码来读取输入流为字符串

            return IOUtils.toString(inputStream, "UTF-8");
        } catch (IOException e) {
            throw e;
        } finally {
            // 关闭输入流
            try {
                inputStream.close();
            } catch (IOException e) {
                throw e;
            }
        }
    }

    /**
     * 获取节点文本
     */
    @Test
    public void testGetNodeText() {
        Document document = Jsoup.parse("<title>百度一下</title>");
        Assert.assertEquals("百度一下", document.select("title").text());
    }

    /**
     * 通过xpath获取节点的文本
     */
    @Test
    public void testXpath() {
        Document document = Jsoup.parse("<title>百度一下</title>");
        List<TextNode> textNodeList = document.selectXpath("//title/text()", TextNode.class);
        String title = textNodeList.get(0).toString();
        Assert.assertEquals("百度一下", title);
    }
}
