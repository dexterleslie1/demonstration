# `jsoup`的用法

> 下面示例的详细用法请参考 [链接](https://github.com/dexterleslie1/demonstration/tree/master/demo-jsoup)

## 如何在`maven`项目中导入`jsoup`依赖呢？

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

## `jsoup`的详细用法

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

