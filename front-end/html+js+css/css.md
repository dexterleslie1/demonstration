## Flex 布局

>提醒：为父级盒子设置 flex 布局后，子元素的 float、clear 和 vertical-align 属性将失效。



### 介绍

CSS Flexbox（Flexible Box）布局是一种一维布局模型，旨在提供一种更有效的方式来布局、对齐和分配容器内项目的空间，即使它们的大小未知或动态变化。Flexbox 非常适合用于创建复杂的页面布局，特别是在响应式设计中。

以下是一些关于 Flexbox 布局的基本概念和使用方法：

1. Flex 容器（Flex Container）

要使用 Flexbox，首先需要将一个元素的 `display` 属性设置为 `flex` 或 `inline-flex`。

```css
.container {
  display: flex; /* 或 inline-flex */
}
```

2. Flex 项目（Flex Items）

成为 Flex 容器的直接子元素将自动成为 Flex 项目。

```html
<div class="container">
  <div class="item">Item 1</div>
  <div class="item">Item 2</div>
  <div class="item">Item 3</div>
</div>
```

3. 主轴（Main Axis）和交叉轴（Cross Axis）

Flexbox 布局定义了两个轴：

- **主轴（Main Axis）**：Flex 项目排列的轴。默认是水平方向（从左到右）。
- **交叉轴（Cross Axis）**：与主轴垂直的轴。默认是垂直方向（从上到下）。

4. Flex 容器属性

`flex-direction`

定义主轴的方向。

```css
.container {
  flex-direction: row; /* 默认，水平方向 */
  /* 其他值：row-reverse, column, column-reverse */
}
```

`flex-wrap`

定义 Flex 项目是否换行。

```css
.container {
  flex-wrap: nowrap; /* 默认，不换行 */
  /* 其他值：wrap, wrap-reverse */
}
```

`flex-flow`

`flex-direction` 和 `flex-wrap` 的简写。

```css
.container {
  flex-flow: row wrap;
}
```

`justify-content`

定义 Flex 项目在主轴上的对齐方式。

```css
.container {
  justify-content: flex-start; /* 默认，起点对齐 */
  /* 其他值：flex-end, center, space-between, space-around, space-evenly */
}
```

`align-items`

定义 Flex 项目在交叉轴上的对齐方式。

```css
.container {
  align-items: stretch; /* 默认，拉伸以填充容器 */
  /* 其他值：flex-start, flex-end, center, baseline */
}
```

`align-content`

定义多行 Flex 项目在交叉轴上的对齐方式（当 `flex-wrap` 为 `wrap` 时有效）。

```css
.container {
  align-content: stretch; /* 默认，拉伸以填充容器 */
  /* 其他值：flex-start, flex-end, center, space-between, space-around, space-evenly */
}
```

5. Flex 项目属性

`order`

定义 Flex 项目的排列顺序（默认是 0）。

```css
.item {
  order: 1; /* 值越小，排列越靠前 */
}
```

`flex-grow`

定义 Flex 项目的放大比例（默认是 0，即如果存在剩余空间，也不放大）。

```css
.item {
  flex-grow: 1; /* 如果有剩余空间，则放大 */
}
```

`flex-shrink`

定义 Flex 项目的缩小比例（默认是 1，即如果空间不足，该项目将缩小）。

```css
.item {
  flex-shrink: 1; /* 如果空间不足，则缩小 */
}
```

`flex-basis`

定义在分配多余空间之前，项目占据的主轴空间（默认是 `auto`）。

```css
.item {
  flex-basis: auto; /* 或指定具体值，如 200px */
}
```

`flex`

`flex-grow`、`flex-shrink` 和 `flex-basis` 的简写。

```css
.item {
  flex: 1 1 auto; /* 默认值 */
  /* 其他示例：flex: 2; (等同于 flex: 2 1 0%) */
}
```

`align-self`

允许单个 Flex 项目在交叉轴上覆盖 `align-items` 属性。

```css
.item {
  align-self: auto; /* 默认，与 align-items 一致 */
  /* 其他值：flex-start, flex-end, center, baseline, stretch */
}
```

示例

```html
<!DOCTYPE html>
<html lang="en">
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<title>Flexbox Example</title>
<style>
  .container {
    display: flex;
    flex-direction: row;
    justify-content: space-between;
    align-items: center;
    height: 100vh;
    border: 1px solid #ccc;
  }
  .item {
    background-color: lightblue;
    padding: 20px;
    margin: 10px;
    border: 1px solid #000;
    flex: 1;
    text-align: center;
  }
</style>
</head>
<body>
  <div class="container">
    <div class="item">Item 1</div>
    <div class="item">Item 2</div>
    <div class="item">Item 3</div>
  </div>
</body>
</html>
```

在这个示例中，`.container` 是一个 Flex 容器，包含三个 Flex 项目。这些项目在主轴（水平方向）上均匀分布，并且在交叉轴（垂直方向）上居中对齐。每个项目都根据可用空间进行缩放。

通过掌握这些基本概念和属性，你可以创建出灵活且响应迅速的布局。



### flex-direction

设置 flex 布局主轴方向，子元素会按照设定的主轴方向**排列**。

取值如下：

- flex-direction=row（默认值）时，子元素按照 x 轴（水平方向）从左到右排列。
- flex-direction=row-reverse 时，子元素按照 x 轴从（水平反方向）右到左反方向排列。
- flex-direction=column 时，子元素按照 y 轴（垂直方向）从上往下排列。
- flex-direction=column-reverse 时，子元素按照 y 轴（垂直反方向）从下往上反方向排列。

详细用法请参考示例 `https://gitee.com/dexterleslie/demonstration/blob/master/front-end/html+js+css/demo-css-flex/flex-direction.html`



### justify-content

设置主轴上的子元素对齐方式。

取值如下：

- justify-content=flex-start（默认值）时，第一个子元素紧贴 x 轴左边或者 y 轴顶端对齐。
- justify-content=flex-end 时，最后一个子元素紧贴 x 轴右边或者 y 轴底端对齐。
- justify-content=flex-center 时，所有子元素中间排列。
- justify-content=space-around 时，所有子元素平均分配主轴剩余空间。
- justify-content=space-between 时，子元素先左右或者上下贴边再平均分配剩余空间。

详细用法请参考示例 `https://gitee.com/dexterleslie/demonstration/blob/master/front-end/html+js+css/demo-css-flex/justify-content.html`



### align-items

在父级元素声明这个属性，用于控制交叉轴方向子元素的对齐方式。注意：这个属性只能够作用于交叉轴方向没有换行的子元素。如果交叉轴方向子元素换行，则使用 align-content 进行控制。

取值如下：

- align-items=flex-start 时，类似 justify-content。
- align-items=flex-end 时，类似 justify-content。
- align-items=center 时，类似 justify-content。
- align-items=stretch 时，子元素沿着交叉轴方向自动拉伸以填满父级元素的高度或者宽度。

详细用法请参考示例 `https://gitee.com/dexterleslie/demonstration/blob/master/front-end/html+js+css/demo-css-flex/align-items.html`



### align-content

用于设置 flex 子项作为一个整体起作用的交叉轴方向上的对齐方式。注意：要让这个属性生效并作用于子元素必须声明父级容器为 display:flex;flex-wrap:wrap; 并且子元素自动换行了。

align-items 和 align-content 的区别：

- flex子项作为一个整体起作用
- flex子项的父级容器已经设置 flex-wrap:wrap



取值如下：

- align-content=flex-start 时，类似 justify-content。
- align-content=flex-end 时，类似 justify-content。
- align-content=center 时，类似 justify-content。
- align-content=stretch 时，类似 justify-content。
- align-content=space-around 时，类似 justify-content。
- align-content=spance-between 时，类似 justify-content。

详细用法请参考示例 `https://gitee.com/dexterleslie/demonstration/blob/master/front-end/html+js+css/demo-css-flex/align-content.html`



### align-self

子元素声明 align-self 属性，用于控制单个子元素和其他子元素交叉轴方向上不同的对齐方式，用于覆盖父级元素声明的 align-items 对单个子元素产生的影响。默认值为 auto，表示继承父级元素 align-items 属性。

详细用法请参考示例 `https://gitee.com/dexterleslie/demonstration/blob/master/front-end/html+js+css/demo-css-flex/align-self.html`



### flex-wrap

在父级元素声明这个属性，用于控制当主轴方向子元素排列空间不足时，子元素排列是否自动换行。

取值如下：

- flex-wrap=nowrap（默认值）时，子元素不自动换行，并且子元素会自动压缩宽度以达到所有子元素的宽度之和不超过父级元素的宽度。
- flex-wrap=wrap 时，子元素自动换行并且子元素的宽度不会变化。

详细用法请参考示例 `https://gitee.com/dexterleslie/demonstration/blob/master/front-end/html+js+css/demo-css-flex/flex-wrap.html`



### flex

子元素声明 flex 属性，用于控制子元素分配父级元素剩余的空间，用来表示占多数份。

详细用法请参考示例 `https://gitee.com/dexterleslie/demonstration/blob/master/front-end/html+js+css/demo-css-flex/flex-attribute.html`



### flex-flow

是flex-direction和flex-wrap的复合属性，例如：flex-flow: row nowrap;



### order

子元素声明order属性，用于控制子元素的排列顺序，数值越小，排列越靠前，默认值为0。

详细用法请参考示例 `https://gitee.com/dexterleslie/demonstration/blob/master/front-end/html+js+css/demo-css-flex/order.html`



## 选择器

>详细用法请参考本站 <a href="https://gitee.com/dexterleslie/demonstration/tree/main/front-end/html+js+css/css-selector" target="_blank">链接</a>



### 介绍

CSS 选择器是用于精准定位 HTML 元素的模式，以下是其完整分类和用法详解：

一、基础选择器

1. **类型选择器**
   直接匹配 HTML 标签名：

   ```css
   p { color: black; } /* 所有段落 */
   div { margin: 10px; } /* 所有 div */
   ```

2. **类选择器**
   以 `.` 开头，匹配 class 属性：

   ```css
   .warning { background: yellow; } /* class="warning" 的元素 */
   ```

3. **ID 选择器**
   以 `#` 开头，匹配 id 属性（唯一性）：

   ```css
   #header { height: 80px; } /* id="header" 的元素 */
   ```

4. **通配符选择器**
   `*` 匹配所有元素：

   ```css
   * { box-sizing: border-box; } /* 全局样式 */
   ```

------

二、属性选择器（重点）

通过 `[]` 匹配元素的属性或属性值：

```css
/* 存在 title 属性 */
[title] { cursor: help; }
 
/* 精确匹配 href 值 */
a[href="https://example.com"] { color: red; }
 
/* 属性值包含 "icon" */
img[alt*="icon"] { border: 2px solid blue; }
 
/* 以 "data-" 开头的自定义属性 */
[data-role="nav"] { display: flex; }
```

------

三、组合器（关系选择器）

1. **后代选择器**（空格）
   匹配嵌套元素：

   ```css
   nav a { text-decoration: none; } /* nav 内的所有链接 */
   ```

2. **子选择器**（`>`）
   仅匹配直接子元素：

   ```css
   ul > li { list-style: none; } /* 仅 ul 的直接 li */
   ```

3. **相邻兄弟选择器**（`+`）
   匹配紧接在后的第一个兄弟元素：

   ```css
   h2 + p { margin-top: 5px; } /* h2 后的第一个 p */
   ```

4. **通用兄弟选择器**（`~`）
   匹配之后的所有同级元素：

   ```css
   h1 ~ p { color: gray; } /* h1 之后的所有 p */
   ```

------

四、伪类选择器

1. **状态伪类**

   ```css
   a:hover { text-decoration: underline; } /* 鼠标悬停 */
   input:focus { border-color: blue; } /* 聚焦状态 */
   ```

2. **结构伪类**

   ```css
   li:first-child { font-weight: bold; } /* 第一个 li */
   tr:nth-child(2n) { background: #f5f5f5; } /* 偶数行 */
   ```

3. **逻辑伪类**

   ```css
   p:not(.intro) { font-size: 14px; } /* 排除 class="intro" 的 p */
   div:has(> img) { padding: 10px; } /* 包含直接子 img 的 div（较新语法） */
   ```

------

五、伪元素选择器

以 `::` 开头，操作元素的特定部分：

```css
p::first-line { font-weight: bold; } /* 首行文字 */
div::before { content: "▶ "; } /* 元素前插入内容 */
a::after { content: " →"; } /* 元素后追加箭头 */
```

------

六、优先级与特异性

- **优先级规则**：`!important` > 内联样式 > ID > 类/伪类/属性 > 类型/伪元素 > 通配符

- 特异性计算

  ：通过

   

  ```
  (a, b, c, d)
  ```

   

  四元组比较，例如：

  - `#nav .item` → (0, 1, 1, 0)
  - `div#header` → (0, 1, 0, 1)

------

七、浏览器兼容性注意

- **新版选择器**（如 `:has()`、`:is()`）需确认目标浏览器支持
- **伪元素双冒号**（`::`）是 CSS3 标准，旧版单冒号（`:`）仍兼容但建议更新

掌握这些选择器可精准控制页面样式，建议通过 https://specificity.keegan.st/ 工具调试优先级问题。



### 属性选择器

>[参考链接](https://stackoverflow.com/questions/9271424/how-do-i-target-elements-with-an-attribute-that-has-any-value-in-css)

```html
<!DOCTYPE html>
<html lang="en">

<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Document</title>
    <style>
        /* type=button 的 input */
        .demo1 input[type=button]:hover {
            border: 5px solid red;
        }

        /* 有 disabled 属性的 input */
        .demo1 input[disabled]:hover {
            border: 5px solid green;
        }
    </style>
</head>

<body>
    <!--
        https://stackoverflow.com/questions/9271424/how-do-i-target-elements-with-an-attribute-that-has-any-value-in-css
    -->
    <div>演示根据属性选择元素</div>
    <div class="demo1">
        <input type="button" value="Button">
        <input type="submit" value="Submit">
        <input type="button" disabled value="Button in disabled state">
    </div>
</body>

</html>
```



### 兄弟元素选择器



#### 紧跟其后的兄弟元素选择器

>[参考链接](https://www.w3.org/TR/CSS21/selector.html#adjacent-selectors)

```html
<!DOCTYPE html>
<html lang="en">

<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Document</title>
    <style>
        .div1>div {
            background-color: green;
            margin: 5px;
            width: 40px;
            height: 20px;
        }

        /* 不起作用，因为.a元素在.b元素之前 */
        .div1 .b+.a {
            color: red;
        }

        .div1 .b+.c {
            color: red;
        }
        /* 不起作用，因为.d元素不是紧跟在.b元素之后 */
        .div1 .b+.d {
            color: red;
        }
    </style>
</head>

<body>
    <!--
        https://www.w3.org/TR/CSS21/selector.html#adjacent-selectors
    -->
<div>演示相邻兄弟元素选择器，注意：+选择器之后的元素一定要紧跟在+选择器之前的元素，否则不匹配</div>
<div class="div1">
    <div class="a">a</div>
    <div class="b">b</div>
    <div class="c">c</div>
    <div class="d">d</div>
</div>
<hr>
</body>
</html>
```



#### 随后的兄弟元素选择器

>[选择同级元素的后续所有匹配元素，如例子所示： 选择 .a 元素的同级元素 .b 并且是在 .a 元素之后出现的](https://stackoverflow.com/questions/10782054/what-does-the-tilde-squiggle-twiddle-css-selector-mean)

```html
<!DOCTYPE html>
<html lang="en">

<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Document</title>
    <style>
        .demo2 .a ~ .b {
            background-color: powderblue;
        }
    </style>
</head>

<body>
<!--
        选择同级元素的后续所有匹配元素，如例子所示： 选择 .a 元素的同级元素 .b 并且是在 .a 元素之后出现的
        https://stackoverflow.com/questions/10782054/what-does-the-tilde-squiggle-twiddle-css-selector-mean
    -->
<div>
    演示随后的兄弟元素选择器（subsequent sibling）
</div>
<div class="demo2">
    <ol>
        <li class="b">b</li>
        <li class="a">a</li>
        <li class="x">x</li>
        <li class="b">b</li>
        <li class="b">b</li>
    </ol>
</div>
</body>
</html>
```



### has 选择器

#### 判断有符合条件的兄弟元素

>[参考链接](https://www.zhangxinxu.com/wordpress/2022/08/css-has-pseudo-class/)

```html
<!DOCTYPE html>
<html lang="en">

<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Document</title>
    <style>
        /* 表示当有兄弟元素.b.active时才匹配.x元素 */
        .div1 .x:has(~ .b.active) {
            background-color: green;
        }
    </style>
</head>

<body>
    <!--
        https://www.zhangxinxu.com/wordpress/2022/08/css-has-pseudo-class/

        注意：低版本的firefox、chrome浏览器不支持:has伪类
    -->

    <div>使用has选择兄弟元素</div>
    <div class="div1">
        <ol>
            <li class="b">b</li>
            <li class="a">a</li>
            <li class="x">x</li>
            <li class="b">b</li>
            <li class="b active">b</li>
        </ol>
    </div>
    <hr>
</body>

</html>
```



#### 判断有符合条件的子元素

判断元素有 p 标签类型的子元素

>[参考链接](https://www.freecodecamp.org/news/how-to-use-the-has-selector-in-css/)

```html
<!DOCTYPE html>
<html lang="en">

<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Document</title>
    <style>
        .demo1>div {
            width: 200px;
            height: 100px;
            background-color: green;
            margin: 10px;
        }

        .demo1>div:has(p) {
            background-color: yellowgreen;
        }
    </style>
</head>

<body>
    <!--
        https://www.freecodecamp.org/news/how-to-use-the-has-selector-in-css/
    -->
    <div>有子元素p的div background-color才变色</div>
    <div class="demo1">
        <div>你好</div>
        <div>
            <p>你好</p>
        </div>
    </div>
    <hr>
</body>

</html>
```



子元素 hover 时，父元素 hover 效果失效示例1

>[参考链接](https://stackoverflow.com/questions/17923922/hover-on-child-should-turn-off-hover-effect-on-parent)

```html
<!DOCTYPE html>
<html lang="en">

<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Document</title>
    <style>
        .demo2 .parent {
            width: 300px;
            height: 100px;
            background-color: green;
        }
        
        /* 避免 hover 子元素时父元素也触发 hover 效果 */
        .demo2 .parent:hover:not(:has(.child:hover)) {
            background-color: blue;
        }

        .demo2 .child {
            width: 150px;
            height: 50px;
            background-color: yellowgreen;
        }

        .demo2 .child:hover {
            background-color: aqua;
        }
    </style>
</head>

<body>
    <!--
        https://stackoverflow.com/questions/17923922/hover-on-child-should-turn-off-hover-effect-on-parent
    -->
    <div>使用has关闭父元素hover效果当hover到子元素</div>
    <div class="demo2">
        <div class="parent">
            <div class="child">

            </div>
        </div>
    </div>
    <hr>
</body>

</html>
```



子元素 hover 时，父元素 hover 效果失效示例2

```html
<!DOCTYPE html>
<html lang="en">

<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Document</title>
    <style>
        .demo3 .search-bar {
            background-color: grey;
            display: flex;
            align-items: center;
            border-radius: 5em;
            height: 2.5em;
        }

        .demo3 .search-bar .svg-search {
            height: 100%;
            aspect-ratio: 1/1;
            margin-right: .5em;
            cursor: pointer;
            border-radius: 5em;
            display: flex;
            align-items: center;
            justify-content: center;
            padding: .5em;
        }

        .demo3 .search-bar .svg-search:hover {
            background-color: yellowgreen;
        }

        .demo3 .search-bar input[type=text] {
            border-style: none;
            background-color: transparent;
            outline: none;
            width: 11em;
            font-size: 1em;
        }

        .demo3 .search-bar input[type=text]::placeholder {
            color: black;
        }

        .demo3 .search-bar:hover:not(:has(.svg-search:hover)) {
            background-color: green;
        }

        .demo3 .search-bar:hover:not(:has(.svg-search:hover)) input[type=text]::placeholder {
            color: yellow;
        }
    </style>
</head>

<body>
    <div>使用has关闭父元素hover效果当hover到子元素2</div>
    <div class="demo3">
        <div class="search-bar">
            <div class="svg-search">
                <svg t="1711244811887" viewBox="0 0 1024 1024" version="1.1" xmlns="http://www.w3.org/2000/svg"
                    p-id="63824">
                    <path
                        d="M212.194304 726.972416c33.760256 33.760256 73.08288 60.269568 116.876288 78.792704 45.357056 19.18464 93.518848 28.911616 143.147008 28.911616s97.788928-9.728 143.145984-28.911616c25.648128-10.848256 49.750016-24.457216 72.112128-40.63744l156.345344 156.484608c6.677504 6.683648 15.43168 10.025984 24.18688 10.025984 8.74496 0 17.490944-3.334144 24.1664-10.00448 13.35808-13.345792 13.36832-34.994176 0.021504-48.35328L739.03616 719.985664c30.533632-32.160768 54.736896-69.082112 71.99744-109.889536 19.183616-45.357056 28.911616-93.518848 28.911616-143.147008s-9.728-97.789952-28.911616-143.147008c-18.523136-43.792384-45.033472-83.115008-78.792704-116.876288-33.76128-33.760256-73.083904-60.270592-116.876288-78.793728-45.35808-19.18464-93.518848-28.911616-143.147008-28.911616s-97.789952 9.728-143.147008 28.911616c-43.793408 18.523136-83.116032 45.033472-116.876288 78.793728s-60.269568 73.083904-78.792704 116.876288c-19.183616 45.357056-28.911616 93.518848-28.911616 143.147008s9.728 97.789952 28.911616 143.147008C151.923712 653.888512 178.434048 693.21216 212.194304 726.972416zM260.547584 255.279104c56.539136-56.539136 131.710976-87.676928 211.670016-87.676928 79.958016 0 155.13088 31.137792 211.670016 87.676928s87.675904 131.710976 87.675904 211.670016S740.425728 622.08 683.887616 678.619136c-56.539136 56.539136-131.712 87.676928-211.670016 87.676928-79.95904 0-155.13088-31.136768-211.670016-87.675904s-87.675904-131.712-87.675904-211.670016S204.008448 311.81824 260.547584 255.279104z"
                        fill="#272636" p-id="63825"></path>
                </svg>
            </div>

            <input type="text" placeholder="Search">
        </div>
    </div>
    <hr>
</body>

</html>
```



#### has(:not()) 和 :not(:has()) 区别

>[参考链接](https://www.matuzo.at/blog/2022/100daysof-day50/)

```html
<!DOCTYPE html>
<html lang="en">

<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Document</title>
    <style>
        .demo5 .parent {
            width: 200px;
            height: 100px;
            background-color: green;
            margin: 10px;
        }

        .demo5 .child {
            width: 50%;
            height: 50%;
            background-color: antiquewhite;
        }

        /* 这个表示选择.parent:hover包含任何子元素除了.child:hover。所以会导致此选择器对于第二个div没有作用 */
        /* .demo5 .parent:hover:has(:not(.child:hover)) {
            background-color: yellowgreen;
        } */
        /* 这个表示选择.parent:hover包含没有子元素.child:hover。所以此选择器正是我们想要的 */
        .demo5 .parent:hover:not(:has(.child:hover)) {
            background-color: yellowgreen;
        }
    </style>
</head>

<body>
    <!--
        https://www.matuzo.at/blog/2022/100daysof-day50/
    -->
    <div>has(:not())和:not(:has())区别</div>
    <div class="demo5">
        <div class="parent">
            <div class="child"></div>
        </div>

        <div class="parent">
            <div class="child"></div>
            <div class="child-placeholder"></div>
        </div>
    </div>
    <hr>
</body>

</html>
```



### nth-child 和 nth-of-type 选择器

>[nth-child 选择指定索引的子元素(在不同类型的子元素中)](https://www.freecodecamp.org/news/nth-child-vs-nth-of-type-selector-in-css/)
>
>[nth-of-type 选择指定索引的子元素(在同类型的子元素中)](https://www.freecodecamp.org/news/nth-child-vs-nth-of-type-selector-in-css/)
>
>提醒：索引从 1 开始。

#### 不存在索引为 4 的 p 元素，样式不起作用

```html
<!DOCTYPE html>
<html lang="en">

<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Document</title>
    <style>
        /* nth-child 匹配任何类型的子元素 */
        .div1 p:nth-child(4) {
            color: yellow;
        }
    </style>
</head>

<body>
    <!--
        nth-child 选择指定索引的子元素(在不同类型的子元素中)
        https://www.freecodecamp.org/news/nth-child-vs-nth-of-type-selector-in-css/
    -->

    <div>下面演示不存在索引为 4 的 p 元素，所以样式 color: yellow 不起作用</div>
    <div class="div1">
        <h1>First heading 1 element</h1>
        <p>First paragraph element</p>
        <p>Second paragraph element</p>
        <h2>First heading 2 element</h2>
        <p>Third paragraph element</p>
        <h3>First heading 3 element</h3>
        <p>Fourth paragraph element</p>
        <p>Fifth paragraph element</p>
    </div>
    <hr />
</body>

</html>
```



#### p:nth-child(3)

```html
<!DOCTYPE html>
<html lang="en">

<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Document</title>
    <style>
        .div2 p:nth-child(3) {
            color: red;
        }
    </style>
</head>

<body>
    <div>
        p:nth-child(3) 选择器仅应用于第 4 个 p 节点，因为它是其父元素 section 的第 3 个子元素。
    </div>
    <div class="div2">
        <article>
            <h1>Article's first heading 1 element</h1>
            <p>Article's first paragraph element</p>
            <h2>Article's first heading 2 element</h2>
            <p>Article's second paragraph element</p>
            <section>
                <h3>Article's first heading 3 element</h3>
                <p>Article's third paragraph element</p>
                <p>Article's fourth paragraph element</p>
            </section>
            <h2>Article's second heading 2 element</h2>
            <p>Article's fifth paragraph element</p>
            <p>Article's sixth paragraph element</p>
        </article>

    </div>
    <hr />
</body>

</html>
```



#### p:nth-of-type(3)

样式会应用到第 5 个子元素中，因为它是其父元素下的第 3 个 p 子元素

```html
<!DOCTYPE html>
<html lang="en">

<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Document</title>
    <style>
        .div3 p:nth-of-type(3) {
            color: red;
        }
    </style>
</head>

<body>
    <!--
        nth-of-type 选择指定索引的子元素(在同类型的子元素中)
        https://www.freecodecamp.org/news/nth-child-vs-nth-of-type-selector-in-css/
    -->
    <div>
        样式会应用到第 5 个子元素中，因为它是其父元素下的第 3 个 p 子元素
    </div>
    <div class="div3">
        <h1>First heading 1 element</h1>
        <p>First paragraph element</p>
        <p>Second paragraph element</p>
        <h2>First heading 2 element</h2>
        <p>Third paragraph element - here</p>
        <h3>First heading 3 element</h3>
        <p>Fourth paragraph element</p>
        <p>Fifth paragraph element</p>
    </div>
    <hr />
</body>

</html>
```



样式会被应用到第 8、10 个子元素中，因为它分别是各自其父元素 article、section 的第 3 个 p 子元素

```html
<!DOCTYPE html>
<html lang="en">

<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Document</title>
    <style>
        .div5 p:nth-of-type(3) {
            color: red;
        }
    </style>
</head>

<body>
    <div>
        样式会被应用到第 8、10 个子元素中，因为它分别是各自其父元素 article、section 的第 3 个 p 子元素
    </div>
    <div class="div5">
        <article>
            <h1>Article's first heading 1 element</h1>
            <p> Article's first paragraph element</p>
            <h2>Article's first heading 2 element</h2>
            <p>Article's second paragraph element</p>
            <section>
                <h3>Article's first heading 3 element</h3>
                <p>Article's third paragraph element</p>
                <p>Article's fourth paragraph element</p>
                <p>Article's fourth paragraph element - here</p>
            </section>
            <h2>Article's second heading 2 element</h2>
            <p>Article's fifth paragraph element - here</p>
            <p>Article's sixth paragraph element</p>
        </article>

    </div>
    <hr />
</body>

</html>
```



## 变量

>详细用法请参考本站示例 [链接](https://gitee.com/dexterleslie/demonstration/tree/main/front-end/html+js+css/css-variable)



### 根据条件设置变量的值

>在 body 标签中添加 class="on" 会使用 body.on 中设置的 --my-background-color: yellow 值。

```html
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Document</title>
    <style>
        /* 使用 :root 伪类定义变量的默认值 */
        :root {
            --my-background-color: red;
            --my-color: red;
        }

        /* 当 body 添加 class on 时 css 变量值变为 yellow，否则为 red */
        body.on {
            --my-background-color: yellow;
        }

        .div1 {
            width: 100px;
            height: 100px;
            background-color: var(--my-background-color);
        }

        .demo2>div {
            width: 300px;
            height: 150px;
            background-color: var(--my-color);
        }
    </style>
</head>
<body>
    <!--
        https://www.freecodecamp.org/news/what-are-css-variables-and-how-to-use-them/
    -->
    <div>演示 css 变量的基本用法</div>
    <div class="div1"></div>
    <hr/>

    <div>通过html设置css变量</div>
    <div class="demo2" style="--my-color: green;">
        <div></div>
    </div>
    <hr>
</body>
</html>
```



## 内联样式和内嵌样式的区别

在网页开发中，内联样式（Inline Style）和内嵌样式（Embedded Style）是两种应用CSS的方式，它们的主要区别在于**定义位置、作用域和优先级**。以下是具体对比：

### 1. **定义位置**

- 内联样式

  直接写在HTML标签的 style 属性中，例如：

  ```html
  <p style="color: red; font-size: 16px;">这是一段文字</p>
  ```

  - **特点**：样式与HTML标签紧密耦合，仅作用于当前元素。

- 内嵌样式

  将CSS代码写在HTML文档的 `<head>` 部分，通过 `<style>` 标签包裹，例如：

  ```html
  <head>
    <style>
      p { color: red; font-size: 16px; }
    </style>
  </head>
  ```

  - **特点**：样式与HTML结构部分分离，但仍在同一个文件中。

### 2. **作用域**

- **内联样式**
  仅作用于当前标签，对其他元素无影响。
- **内嵌样式**
  作用于整个HTML文档，所有匹配的标签都会应用该样式。

### 3. **优先级**

- **内联样式** > **内嵌样式** > **外部样式表**
  内联样式优先级最高（除非使用`!important`规则）。

### 4. **可维护性**

- **内联样式**
  难以维护，样式与HTML混合，无法复用。
- **内嵌样式**
  相对集中，可在同一页面复用，但仍不如外部样式表易于管理。

### 5. **使用场景**

- **内联样式**
  临时调整或覆盖其他样式（如动态修改元素样式）。
- **内嵌样式**
  页面内多个元素共享样式，且不需要外部样式表的情况。

### 总结



| **特性**     | **内联样式**          | **内嵌样式**                  |
| ------------ | --------------------- | ----------------------------- |
| **定义位置** | HTML标签的`style`属性 | HTML的`<head>`中`<style>`标签 |
| **作用域**   | 当前标签              | 整个文档                      |
| **优先级**   | 最高                  | 中等                          |
| **可维护性** | 差                    | 一般                          |
| **典型场景** | 临时调整、动态样式    | 页面内复用样式                |



**推荐做法**：

- 优先使用外部样式表（External CSS），便于维护和复用。
- 内嵌样式可作为补充，内联样式慎用（除非必要）。



## display

>详细用法请参考本站 [示例](https://gitee.com/dexterleslie/demonstration/tree/main/front-end/html+js+css/demo-css-display)

### block（块）

>元素能够声明宽度和高度并且独占一行。div 默认是块元素。
>
>注意：在不声明元素宽度时，元素宽度会自动调整至其父级容器宽度，以填满父级容器宽度。

### inline（行内）

>元素的高度和宽度由内容决定，用户无法直接设置其高度和宽度，并且不会独占一行。此外，inline 元素可以设置水平方向的 padding 和 margin，但竖直方向的 padding 和 margin 则无效。
>
>span 默认是行内元素。

### inline-block

>元素能够声明宽度和高度并且不会独占一行。此外，inline 元素可以设置水平和竖直方向的 padding 和 margin。

### table-cell

>表格单元方式排列。
>
>注意：能够声明宽度但是不能够声明高度，高度会自动被拉伸到适合单元格高度。

### none

>元素被隐藏并且不会占据布局空间。visibility: hidden 元素隐藏并且会占据布局空间。



## position

>详细用法请参考本站 [示例](https://gitee.com/dexterleslie/demonstration/tree/main/front-end/html+js+css/demo-css-position)
>
>[参考链接](https://developer.mozilla.org/en-US/docs/Web/CSS/position)

### fixed

>让元素固定在可视区域的固定位置，不受页面的滚动影响。

### static

>该元素根据文档的正常流程定位。 top、right、bottom、left 和 z-index 属性无效。 这是默认值。
>
>postion 的默认值为 static

```html
<!DOCTYPE html>
<html lang="en">

<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Document</title>
    <style>
        .div1>div {
            width: 50px;
            height: 50px;
            margin: 10px;
            background-color: green;
        }

        /* 该元素根据文档的正常流程定位。 top、right、bottom、left 和 z-index 属性无效。 这是默认值。*/
        .div1 .div12 {
            position: static;
            left: 10px;
            top: 200px;
            background-color: red;
            z-index: 1000;
        }
    </style>
</head>

<body>
    <!--
        该元素根据文档的正常流程定位。 top、right、bottom、left 和 z-index 属性无效。 这是默认值。
        
        postion的默认值为static
        
        https://developer.mozilla.org/en-US/docs/Web/CSS/position
    -->
    <div>
        演示position:static用法
    </div>
    <div class="div1">
        <div class="div11"></div>
        <div class="div12"></div>
        <div class="div13"></div>
    </div>
    <hr>
</body>

</html>
```

### relative

>相对自己布局流位置相对定位。被相对定位后，原有的布局流位置依然占据空间。

```html
<!DOCTYPE html>
<html lang="en">

<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Document</title>
    <style>
        .div2 {
            background-color: aqua;
        }

        .div2>div:first-child {
            width: 200px;
            height: 200px;
            background-color: blue;
        }

        .div2>div:nth-child(2) {
            width: 200px;
            height: 200px;
            background-color: blueviolet;
            /* 
                relative特性：
                1、相对自己布局流位置相对定位
                2、被相对定位后，原有的布局流位置依然占据空间
            */
            position: relative;
            top: 150px;
            left: 150px;
        }

        .div2>div:nth-child(3) {
            width: 200px;
            height: 200px;
            background-color: brown;
        }
    </style>
</head>

<body>
    <div>演示position:relative用法</div>
    <div class="div2">
        <div></div>
        <div></div>
        <div></div>
    </div>
    <hr>
</body>

</html>
```

### absolute

>以最近的父级非 static 定位的元素为参考点，定位指定的偏移量。

示例：absolute 的父元素声明为 relative

```html
<!DOCTYPE html>
<html lang="en">

<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Document</title>
    <style>
        .div3 {
            background-color: aqua;
        }

        .div3>div:first-child {
            width: 200px;
            height: 200px;
            background-color: blue;
        }

        .div3>div:nth-child(2) {
            width: 500px;
            height: 500px;
            background-color: blueviolet;
        }

        .div3 .father {
            width: 200px;
            height: 200px;
            background-color: burlywood;

            /* 最近的父级非static定位参考点 */
            position: relative;
            margin: 30px;
        }

        .div3 .son {
            width: 100px;
            height: 100px;
            background-color: chartreuse;

            /* 
                absolute特性：
                1、以最近的父级非static定位的元素为参考点，定位指定的偏移量
            */
            position: absolute;
            top: 100px;
            left: 100px;
        }

        .div3>div:nth-child(3) {
            width: 200px;
            height: 200px;
            background-color: brown;
        }
    </style>
</head>

<body>
    <div>演示position:absolute用法</div>
    <div class="div3">
        <div></div>
        <div>
            <div class="father">
                <div class="son"></div>
            </div>
        </div>
        <div></div>
    </div>
    <hr>
</body>

</html>
```

```html
<!DOCTYPE html>
<html lang="en">

<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Document</title>
    <style>
        .div6 {
            position: relative;
            background-color: green;
            width: 500px;
            height: 500px;
        }

        .div6 .box {
            position: absolute;
            width: 100px;
            height: 100px;
            background-color: yellow;
            border-width: 1px;
            border-color: black;
            border-style: solid;
        }
    </style>
</head>

<body>
    <div>演示position:relative+position:absolute组合定位用法</div>
    <!-- https://www.cnblogs.com/feicheninfo/articles/11004410.html -->
    <div class="div6">
        <div class="box" style="top:10px;left:10px;"></div>
        <div class="box" style="right:10px;bottom:10px;"></div>
    </div>
</body>

</html>
```



示例：absolute 的父元素没有声明为非 static 元素（默认为 static），所以以 body 为参考定位

```html
<!DOCTYPE html>
<html lang="en">

<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Document</title>
    <style>
        .div5 {
            background-color: aqua;
        }

        .div5>div:first-child {
            width: 200px;
            height: 200px;
            background-color: blue;
        }

        .div5>div:nth-child(2) {
            width: 200px;
            height: 200px;
            background-color: blueviolet;

            /* 
                absolute特性：
                1、被定位后，原有的布局流位置不占据布局空间
                2、如果所有父级元素都没有声明非static定位的则以body为参考点
            */
            position: absolute;
            top: 200px;
            left: 200px;
        }

        .div5>div:nth-child(3) {
            width: 200px;
            height: 200px;
            background-color: brown;
        }
    </style>
</head>

<body>
    <div>演示position:absolute因为没有父元素声明为非position:static，所以以body为参考定位</div>
    <div class="div5">
        <div></div>
        <div>这是div5 position:absolute演示</div>
        <div></div>
    </div>
</body>

</html>
```



示例：absolute 居中显示

>position: absolute 的元素居中时需要同时指定 margin: 0 auto;left: 0;right: 0;

```html
<!DOCTYPE html>
<html lang="en">

<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Document</title>
    <style>
        .demo1 .container {
            background-color: green;
            position: relative;
            height: 50px;
        }
        .demo1 .container .widget {
            position: absolute;
            width: 30px;
            height: 30px;
            background-color: yellow;
            /* position: absolute的元素居中时需要同时指定margin: 0 auto;left: 0;right: 0; */
            margin: 0 auto;
            left: 0;
            right: 0;
        }
    </style>
</head>

<body>
    <div>演示position: absolute居中显示</div>
    <div class="demo1">
        <div class="container">
            <div class="widget"></div>
        </div>
    </div>
    <hr>
</body>

</html>
```



## float

>详细用法请参考本站 [示例](https://gitee.com/dexterleslie/demonstration/tree/main/front-end/html+js+css/css-float)



示例 1：

>示例演示 float 特性：
>
>- 将元素排除在普通流之外
>- 元素将不在页面中占据空间
>- 将浮动的元素放置在包含框的左边或者右边
>- 浮动的元素依旧位于包含框之内
>- 浮动元素的外边缘不会超过其父元素的内边缘

```html
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Document</title>
    <style type="text/css">
        /*
        演示float特性：
        1、将元素排除在普通流之外
        2、元素将不在页面中占据空间
        3、将浮动的元素放置在包含框的左边或者右边
        4、浮动的元素依旧位于包含框之内
        5、浮动元素的外边缘不会超过其父元素的内边缘
        */
        #box {
            width: 600px;
            height: 600px;
            background-color: aqua;
        }

        #box > div:first-child {
            width: 200px;
            height: 200px;
            background-color: green;
            float: right;
        }

        #box > div:nth-child(2) {
            width: 200px;
            height: 200px;
            background-color: bisque;
        }

        #box > div:nth-child(3) {
            width: 200px;
            height: 200px;
            background-color: blue;
        }
    </style>
</head>
<body>
    <div id="box">
        <div></div>
        <div></div>
        <div></div>
    </div>
</body>
</html>
```



示例 2：

>示例演示 float 特性：
>
>- 浮动的元素可以向左或者向右移动，直到它的外边缘碰到包含框或者另一个浮动框的边框为止
>- 浮动元素不会互相重叠
>- 父元素的宽度大于等于所有浮动的子元素宽度之和时，浮动元素不会上下浮动，否则最后浮动的元素会换行

```html
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Document</title>
    <style type="text/css">
        /*
        演示float特性：
        1、浮动的元素可以向左或者向右移动，直到它的外边缘碰到包含框或者另一个浮动框的边框为止
        2、浮动元素不会互相重叠
        3、父元素的宽度大于等于所有浮动的子元素宽度之和时，浮动元素不会上下浮动，否则最后浮动的元素会换行
        */
        #box {
            width: 600px;
            height: 510px;
            background-color: aqua;
        }

        #box > div:first-child {
            width: 200px;
            height: 200px;
            background-color: green;
            float: right;
        }

        #box > div:nth-child(2) {
            width: 200px;
            height: 200px;
            background-color: bisque;
            float: right;
        }

        #box > div:nth-child(3) {
            width: 300px;
            height: 300px;
            background-color: blue;
            float: right;
        }
    </style>
</head>
<body>
    <div id="box">
        <div></div>
        <div></div>
        <div></div>
    </div>
</body>
</html>
```



示例 3：

>示例演示 float 特性：
>
>- 任何元素一旦浮动，display 属性将完全失效均可设置高度，并且不会独占一行

```html
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Document</title>
    <style type="text/css">
        /*
        span标签是inline元素，没有宽高并且不会独占一行
        演示float特性：
        1、任何元素一旦浮动，display属性将完全失效均可设置高度，并且不会独占一行
        */
        #box1 {
            float: left;
            width: 200px;
            height: 200px;
            background-color: blueviolet;
        }

        #box2 {
            float: left;
            width: 200px;
            height: 200px;
            background-color: brown;
        }
    </style>
</head>
<body>
    <span id="box1">6666666</span>
    <span id="box2">8888888</span>
</body>
</html>
```



## object-fit

>详细用法请参考本站 [示例](https://gitee.com/dexterleslie/demonstration/tree/main/front-end/html+js+css/css-object-fit)



## 黑暗或明亮模式切换

>dark or light mode.
>
>详细用法请参考本站 [示例](https://gitee.com/dexterleslie/demonstration/tree/main/front-end/html+js+css/demo-css-dark-mode)

```html
<!DOCTYPE html>
<html lang="en" data-theme="dark">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Document</title>
    <style>
        :root {
            --background-color: yellowgreen;
            --color: blue;
        }

        html[data-theme=dark] {
            --background-color: black;
            --color: white;
        }

        .demo1>div {
            width: 200px;
            height: 100px;
            background-color: var(--background-color);
            color: var(--color);
        }
    </style>
</head>
<body>
    <!--
        https://css-tricks.com/dark-modes-with-css/
    -->
    <div>使用css实现高亮和黑暗模式切换</div>
    <div class="demo1">
        <div>Hello world!</div>
    </div>
    <hr>
</body>
</html>
```

