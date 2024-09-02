# `jsoup`使用

## 什么是`xpath`呢？

XPath（XML Path Language）是一种在XML文档中定位和选择节点的语言，同时也支持HTML文档的解析。它基于XML的树状结构，提供了在数据结构树中找寻节点的能力。XPath的初衷是作为一个通用的、介于XPointer与XSL间的语法模型，但很快就被开发者广泛采用作为小型查询语言。

**XPath的基本概念**

- **节点（Nodes）**：XML文档的基本构建块，包括元素、属性、文本等。XPath中有七种类型的节点：元素节点、属性节点、文本节点、命名空间节点、处理指令节点、注释节点以及文档节点（或称为根节点）。
- **路径表达式**：用于定位XML或HTML文档中的节点。路径表达式由一系列步骤组成，每个步骤用斜杠`/`分隔。

**XPath的用途**

XPath的主要用途包括：

- **数据抓取**：通过XPath可以轻松地从XML或HTML文档中抓取所需的数据。
- **数据转换**：XPath是XSLT（XML Stylesheet Language Transformations）中的主要元素，用于将XML文档转换为其他格式。
- **数据验证**：XPath可以与其他技术结合使用，对XML文档中的数据进行验证。

**XPath的语法**

XPath使用路径表达式来选取XML或HTML文档中的节点或节点集。路径表达式的基本语法如下：

- `/`：从根节点开始选取。
- `//`：从任意节点开始选取。
- `.`：选取当前节点。
- `..`：选取当前节点的父节点。
- `@`：选取属性。
- `*`：通配符，表示任意节点或属性。

例如，`//title`表示选择文档中的所有`<title>`元素；`//book[@category='fiction']`表示选择所有属性`category`的值为`fiction`的`<book>`元素。

**XPath的高级功能**

XPath还提供了一些高级功能，如：

- **谓语（Predicates）**：用于进一步筛选节点集，如`//book[price>35]`表示选择价格大于35的所有`<book>`元素。
- **轴（Axes）**：定义了相对于当前节点的节点集，如`child`（子元素）、`ancestor`（祖先元素）等。
- **函数**：XPath含有超过100个内建函数，用于处理字符串、数值、日期和时间、节点和QName等。

**XPath的应用场景**

XPath广泛应用于各种需要处理XML或HTML文档的场景中，如网页数据抓取、自动化测试、数据验证等。学会XPath可以显著提高处理这些文档的效率。

总之，XPath是一种强大的语言，它使得在XML和HTML文档中查找和选择节点变得简单而高效。



## `jsoup`和`document css`选择器兼容吗？

>[CSS Selector Reference](https://www.w3schools.com/cssref/css_selectors.php)

**jsoup和Document对象使用的CSS选择器在很大程度上是兼容的**。这主要是因为jsoup是一个Java的HTML解析器，它设计用来解析和操作HTML文档，并提供了类似于jQuery的CSS选择器语法来方便地选择和提取文档中的元素。

**兼容性概述**

1. **选择器语法**：jsoup支持的CSS选择器语法相当广泛，包括标签选择器、类选择器、ID选择器、属性选择器、后代选择器、子选择器、相邻兄弟选择器和通用兄弟选择器等。这些选择器语法与在浏览器中使用的CSS选择器非常相似，因此开发者可以很容易地将他们熟悉的CSS选择器知识应用到jsoup中。
2. **功能限制**：虽然jsoup的CSS选择器功能强大，但它并不是完全按照CSS规范实现的。特别是在处理一些复杂的CSS伪类（如`:hover`、`:active`等）时，jsoup可能无法提供与浏览器完全一致的行为。这是因为jsoup是一个静态的HTML解析器，它不具备浏览器那样的动态交互和渲染能力。
3. **浏览器兼容性**：当讨论jsoup和Document对象（通常指的是在浏览器中通过DOM API获取的文档对象）的CSS选择器兼容性时，我们实际上是在比较两种不同环境下的选择器行为。在浏览器中，CSS选择器由浏览器的CSS引擎解析和执行，而jsoup则在自己的环境中解析和执行选择器。尽管两者在大多数基本选择器上都是兼容的，但在处理复杂选择器和特定浏览器行为时可能会存在差异。

**使用建议**

1. **熟悉选择器语法**：在使用jsoup时，建议开发者首先熟悉CSS选择器的基本语法和用法，以便能够高效地选择和提取HTML文档中的元素。
2. **测试兼容性**：在将jsoup用于生产环境之前，建议开发者针对自己的具体需求进行充分的测试，以确保jsoup的选择器行为符合预期。特别是当涉及到复杂选择器或特定浏览器行为时，更应该注意测试结果的准确性。
3. **查阅文档**：jsoup的官方文档提供了关于CSS选择器语法的详细信息和示例代码，建议开发者在开发过程中经常查阅文档以获取最新的信息和最佳实践。

综上所述，jsoup和Document对象使用的CSS选择器在大多数情况下是兼容的，但在处理复杂选择器和特定浏览器行为时可能会存在差异。因此，在使用jsoup时需要注意选择器语法的正确性和兼容性测试的重要性。



## 什么是`jsoup`呢？

**jsoup 是一款 Java 的 HTML 解析器**，它提供了一套非常省力的 API，可通过 DOM，CSS 以及类似于 jQuery 的操作方法来取出和操作数据。

## `jsoup`的用法

> 下面示例的详细用法请参考 [链接](https://gitee.com/dexterleslie/demonstration/tree/master/demo-java/demo-library/demo-jsoup)

### 如何在`maven`项目中导入`jsoup`依赖呢？

在你的 Maven 项目的 `pom.xml` 文件中，添加 `jsoup` 的依赖。这通常位于 `<dependencies>` 部分：

```xml
<dependencies>  
    <!-- 其他依赖 ... -->  
  
    <!-- 添加 jsoup 依赖 -->  
    <dependency>  
        <groupId>org.jsoup</groupId>  
        <artifactId>jsoup</artifactId>  
        <version>1.17.2</version>
    </dependency>  
</dependencies>
```

### 解析`html`字符串创建`Document`对象

```java
// 使用jsoup解析HTML字符串
Document document = Jsoup.parse(htmlContent);
```

### 根据`id`获取`Element`对象

```java
// 测试根据id获取元素
Element elementDiv1 = document.getElementById("div1");
Assert.assertEquals("div", elementDiv1.tagName());
```

### 获取`table>tbody`下有`id`属性的所有`tr`

```java
// 测试获取table>tbody下有id属性的所有tr
Element elementTable1 = document.getElementById("table1");
Elements trElementList = elementTable1.select("tbody > tr[id]");
Assert.assertEquals(2, trElementList.size());
trElementList.forEach(o -> {
    Assert.assertEquals("tr", o.tagName());
});
```

### 只选择直属元素

```java
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
```



### 获取节点文本

```java
/**
 * 获取节点文本
 */
@Test
public void testGetNodeText() {
    Document document = Jsoup.parse("<title>百度一下</title>");
    Assert.assertEquals("百度一下", document.select("title").text());
}
```



### 通过`xpath`获取节点的文本

```java
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
```

