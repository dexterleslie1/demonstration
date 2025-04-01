# CSS



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

