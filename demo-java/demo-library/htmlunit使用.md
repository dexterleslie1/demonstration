# `htmlunit`使用

## 什么是`htmlunit`呢？

HtmlUnit是一款开源的Java页面分析工具，它模拟了浏览器的环境，允许开发者在不需要实际浏览器界面的情况下执行JavaScript、CSS、DOM操作以及HTTP请求。以下是关于HtmlUnit的详细介绍：

一、**基础信息**

- **性质**：Java页面分析工具，无头（headless）浏览器。
- **引擎**：采用Rhinojs引擎来执行JavaScript代码，并使用NekoHTML来解析HTML文档。
- **主要用途**：Web自动化测试、爬虫开发、网页内容解析等领域。

二、**主要特点**

1. **模拟浏览器行为**：HtmlUnit能够模拟浏览器的许多方面，包括DOM操作、CSS选择器、AJAX请求等，使得开发者可以在服务器端或无需图形界面的环境中执行Web应用程序的自动化测试。
2. **执行JavaScript**：由于基于Rhinojs引擎，HtmlUnit能够执行JavaScript代码，从而能够获取一些需要执行JS才能得到的页面内容。
3. **高速运行**：作为一个没有界面的浏览器，HtmlUnit的运行速度非常迅速。
4. **易于集成**：作为Java库，HtmlUnit可以轻松集成到任何Java应用程序中。
5. **开源和免费**：HtmlUnit是开源项目，遵循Apache License 2.0，可以免费使用。
6. **社区支持**：拥有活跃的社区和丰富的文档资源，便于学习和解决问题。

三、**应用场景**

1. **Web自动化测试**：HtmlUnit常被用于进行网页的自动化测试，包括测试网页的交互、表单提交、JavaScript执行等。
2. **爬虫开发**：在爬虫项目中，HtmlUnit可以有效地分析DOM标签，并运行页面上的JavaScript以便获取需要的数据。
3. **网页内容解析**：通过模拟浏览器行为，HtmlUnit可以解析网页内容，提取所需信息。

四、**总结**

HtmlUnit是一个功能强大的无头浏览器，它为Web自动化测试、爬虫开发等领域提供了有力的支持。通过模拟浏览器的行为，HtmlUnit使得开发者能够在无需实际浏览器界面的情况下执行复杂的Web操作。无论是进行自动化测试还是数据抓取，HtmlUnit都是一个值得考虑的工具。



## `htmlunit`使用

>[参考链接](https://www.scrapingbee.com/blog/getting-started-with-htmlunit/)

详细用法请参考 [链接](https://gitee.com/dexterleslie/demonstration/tree/master/demo-java/demo-library/demo-htmlunit)

- 使用`htmlunit`抓取指定`url`内容

  ```java
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
  ```

- 等待`javascript`加载完毕动态内容再抓取

  ```java
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
  ```

  