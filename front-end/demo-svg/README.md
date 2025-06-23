# `svg`



## 概念

SVG（Scalable Vector Graphics，可缩放矢量图形）是一种基于 XML 的二维矢量图形格式，广泛用于网页设计、图标、数据可视化等场景。其核心优势是**矢量特性**（无限缩放不失真）、**可编辑性**（直接修改代码或通过工具调整）和**轻量性**（适合网络传输）。以下是 SVG 绘图的核心知识点和实践指南：


### **一、SVG 基础结构**
SVG 图形通过 XML 标签描述，最外层是 `<svg>` 根元素，定义画布的大小和命名空间：
```xml
<!-- 基础 SVG 结构 -->
<svg 
  xmlns="http://www.w3.org/2000/svg"  <!-- 必须的命名空间 -->
  width="800"                        <!-- 画布宽度（像素或百分比） -->
  height="600"                       <!-- 画布高度 -->
  viewBox="0 0 800 600"              <!-- 视口范围（定义坐标系） -->
  style="border: 1px solid #ccc;">   <!-- 可选样式 -->
  
  <!-- 图形元素（如矩形、圆形等） -->
  
</svg>
```
- **`viewBox`**：关键属性，格式为 `min-x min-y width height`，定义画布的逻辑坐标系。例如 `viewBox="0 0 800 600"` 表示逻辑上画布左上角是 `(0,0)`，右下角是 `(800,600)`，无论物理尺寸如何缩放，图形都会按比例适配。


### **二、常用绘图元素**
SVG 提供了多种基础形状元素，用于绘制简单或复杂图形：

#### 1. **矩形 `<rect>`**
```xml
<rect 
  x="50"    <!-- 左上角 x 坐标 -->
  y="50"    <!-- 左上角 y 坐标 -->
  width="200"  <!-- 宽度 -->
  height="100" <!-- 高度 -->
  fill="blue"  <!-- 填充色 -->
  stroke="red" <!-- 描边色 -->
  stroke-width="2" <!-- 描边宽度 -->
  rx="10"     <!-- 圆角水平半径 -->
  ry="10"     <!-- 圆角垂直半径（通常与 rx 相同） -->
/>
```

#### 2. **圆形 `<circle>`**
```xml
<circle 
  cx="400"  <!-- 圆心 x 坐标 -->
  cy="300"  <!-- 圆心 y 坐标 -->
  r="80"    <!-- 半径 -->
  fill="none" 
  stroke="green" 
  stroke-width="5"
/>
```

#### 3. **椭圆 `<ellipse>`**
```xml
<ellipse 
  cx="400"  <!-- 椭圆中心 x -->
  cy="300"  <!-- 椭圆中心 y -->
  rx="120"  <!-- 水平半轴 -->
  ry="60"   <!-- 垂直半轴 -->
  fill="orange"
/>
```

#### 4. **线条 `<line>`**
```xml
<line 
  x1="100"  <!-- 起点 x -->
  y1="200"  <!-- 起点 y -->
  x2="700"  <!-- 终点 x -->
  y2="400"  <!-- 终点 y -->
  stroke="purple" 
  stroke-width="3"
  stroke-linecap="round" <!-- 线端样式（butt/square/round） -->
/>
```

#### 5. **折线 `<polyline>`**
绘制由多个线段连接的折线（点坐标列表）：
```xml
<polyline 
  points="100,100 200,150 300,100 400,150" <!-- 点坐标对（x1,y1 x2,y2...） -->
  fill="none" 
  stroke="black" 
  stroke-width="2"
/>
```

#### 6. **多边形 `<polygon>`**
绘制闭合的多边形（点坐标列表，自动闭合）：
```xml
<polygon 
  points="500,100 600,200 700,100 650,50" <!-- 点坐标 -->
  fill="yellow" 
  stroke="black" 
  stroke-width="2"
/>
```


### **三、路径 `<path>`：复杂图形的绘制核心**
`<path>` 是 SVG 中最强大的元素，用于绘制任意形状的路径（如曲线、不规则图形）。其核心是 `d` 属性，包含一系列**路径命令**（大写字母表示绝对坐标，小写字母表示相对坐标）。

#### 常用路径命令：
| 命令                                                    | 描述                               | 示例                       |
| ------------------------------------------------------- | ---------------------------------- | -------------------------- |
| `M x,y`                                                 | 移动到起点（Move to）              | `M 100,100`                |
| `L x,y`                                                 | 直线到（Line to）                  | `L 200,200`                |
| `H x`                                                   | 水平直线到（Horizontal Line）      | `H 300`                    |
| `V y`                                                   | 垂直直线到（Vertical Line）        | `V 400`                    |
| `C x1,y1 x2,y2 x,y`                                     | 三次贝塞尔曲线（Cubic Bezier）     | `C 150,50 250,150 300,100` |
| `Q x1,y1 x,y`                                           | 二次贝塞尔曲线（Quadratic Bezier） | `Q 200,50 250,100`         |
| `A rx,ry x-axis-rotation large-arc-flag sweep-flag x,y` | 椭圆弧（Arc）                      | `A 50,50 0 0 1 300,100`    |
| `Z`                                                     | 闭合路径（Close Path）             | `Z`                        |

#### 示例：绘制心形
```xml
<path 
  d="M 400,200 
     C 350,150 250,150 250,250 
     C 250,350 400,450 400,450 
     C 400,450 550,350 550,250 
     C 550,150 450,150 400,200 Z" 
  fill="red" 
  stroke="darkred" 
  stroke-width="2"
/>
```


### **四、样式与高级效果**
SVG 支持丰富的样式和效果，可通过属性或 CSS 控制：

#### 1. **填充与描边**
- `fill`：填充颜色（支持颜色名、十六进制、RGB、`url(#gradient)` 引用渐变）。
- `stroke`：描边颜色。
- `stroke-width`：描边宽度。
- `stroke-linecap`：线端样式（`butt`/`square`/`round`）。
- `stroke-linejoin`：线段连接样式（`miter`/`round`/`bevel`）。
- `stroke-dasharray`：虚线模式（如 `5,5` 表示5px实线+5px间隔）。

#### 2. **渐变（Gradient）**
通过 `<linearGradient>`（线性渐变）或 `<radialGradient>`（径向渐变）定义颜色过渡，需通过 `id` 引用：
```xml
<!-- 定义线性渐变 -->
<defs>
  <linearGradient id="myGradient" x1="0%" y1="0%" x2="100%" y2="0%">
    <stop offset="0%" stop-color="#ff0000" /> <!-- 起点颜色 -->
    <stop offset="100%" stop-color="#0000ff" /> <!-- 终点颜色 -->
  </linearGradient>
</defs>

<!-- 使用渐变填充 -->
<rect 
  x="100" y="100" width="200" height="100" 
  fill="url(#myGradient)" 
/>
```

#### 3. **图案（Pattern）**
通过 `<pattern>` 定义重复的填充图案（如纹理）：
```xml
<defs>
  <pattern id="stripes" patternUnits="userSpaceOnUse" width="20" height="20">
    <rect width="20" height="10" fill="yellow" />
    <rect y="10" width="20" height="10" fill="orange" />
  </pattern>
</defs>

<rect 
  x="300" y="300" width="200" height="100" 
  fill="url(#stripes)" 
/>
```


### **五、动态生成 SVG（JavaScript）**
SVG 是 XML 格式，可通过 JavaScript 动态创建和修改元素，适用于交互式图表或动画。

#### 示例：用 JS 绘制动态圆形
```html
<svg id="mySvg" width="400" height="400"></svg>

<script>
  const svg = document.getElementById('mySvg');
  
  // 创建圆形元素
  const circle = document.createElementNS('http://www.w3.org/2000/svg', 'circle');
  circle.setAttribute('cx', '200');
  circle.setAttribute('cy', '200');
  circle.setAttribute('r', '50');
  circle.setAttribute('fill', 'blue');
  
  // 添加点击事件
  circle.addEventListener('click', () => {
    circle.setAttribute('r', Math.random() * 50 + 30); // 随机改变半径
  });
  
  svg.appendChild(circle); // 将圆形添加到 SVG 画布
</script>
```


### **六、SVG 与 Canvas 的对比**
SVG 和 Canvas 都可用于绘图，但适用场景不同：
| 特性       | SVG                              | Canvas                         |
| ---------- | -------------------------------- | ------------------------------ |
| **类型**   | 矢量图形（基于 XML）             | 位图（基于像素）               |
| **缩放**   | 不失真                           | 可能模糊                       |
| **交互**   | 直接操作 DOM 元素（易绑定事件）  | 需手动计算坐标（事件处理复杂） |
| **复杂度** | 适合静态/低频更新图形            | 适合高频动画/游戏（性能更好）  |
| **SEO**    | 友好（内容可直接被搜索引擎解析） | 不友好（需额外处理）           |


### **七、工具推荐**
- **设计工具**：Adobe Illustrator、Figma（可导出 SVG）。
- **调试工具**：浏览器开发者工具（直接查看和编辑 SVG 代码）。
- **库**：D3.js（数据可视化）、Three.js（3D SVG 扩展）、Snap.svg（简化 SVG 操作）。


### **总结**
SVG 是矢量图形的标准，适合需要缩放、交互或动态生成的场景。核心是通过 `<svg>` 根元素和各种形状元素（`<rect>`、`<circle>`、`<path>` 等）描述图形，结合样式和 JavaScript 实现复杂效果。掌握路径命令（`d` 属性）和渐变/图案是绘制高级图形的关键。



## `html` 中使用 `svg`

在 HTML 中使用 SVG 绘图非常灵活，SVG 可以直接嵌入 HTML 文档（内联 SVG），也可以作为外部资源引用。以下是详细的实践指南，涵盖嵌入方式、样式控制、动态操作和常见场景：

### 嵌入 SVG 的方式

直接在 HTML 的 `<body>` 或容器标签（如 `<div>`）中写入 `<svg>` 标签，优点是可以直接通过 HTML/CSS/JS 操作 SVG 元素，适合需要交互或动态修改的场景。

**示例：**

```html
<!DOCTYPE html>
<html>
<head>
    <title>HTML 中嵌入 SVG</title>
</head>
<body>
    <!-- 内联 SVG，直接写在 HTML 中 -->
    <svg 
        xmlns="http://www.w3.org/2000/svg" 
        width="400" 
        height="300" 
        viewBox="0 0 400 300"
        style="border: 1px solid #eee;"
    >
        <!-- 绘制一个红色矩形 -->
        <rect 
            x="50" y="50" 
            width="100" height="80" 
            fill="#ff4444" 
            stroke="#000" 
            stroke-width="2"
        />
        <!-- 绘制一个蓝色圆形 -->
        <circle 
            cx="300" cy="150" 
            r="40" 
            fill="#4444ff"
        />
    </svg>
</body>
</html>
```



### 通过 HTML 属性设置样式

SVG 元素支持直接通过属性设置样式（如 fill、stroke、stroke-width 等），这些属性优先级高于 CSS。

**示例：**

```html
<svg width="200" height="200">
    <rect 
        x="10" y="10" 
        width="180" height="180" 
        fill="lightblue"  <!-- 填充色 -->
        stroke="darkblue"  <!-- 描边色 -->
        stroke-width="5"   <!-- 描边宽度 -->
        rx="20" ry="20"    <!-- 圆角 -->
    />
</svg>
```



### 通过 CSS 控制样式

可以通过内部或外部 CSS 为 SVG 元素添加样式，支持类名（class）或 ID 选择器。

注意：部分 SVG 属性（如 fill）可通过 CSS 覆盖，但需使用 fill 等 CSS 属性名（而非 XML 属性名）。

**示例：**

```html
<style>
    /* 外部/内部 CSS */
    .custom-rect {
        fill: #ff9999;    /* 覆盖默认填充色 */
        stroke: #660000;  /* 描边色 */
        stroke-dasharray: 5 5; /* 虚线模式 */
    }
    .animated-circle {
        transition: r 0.3s; /* 半径变化动画 */
    }
    .animated-circle:hover {
        r: 50; /* 悬停时放大 */
    }
</style>

<svg width="300" height="200">
    <rect 
        class="custom-rect"  <!-- 使用 CSS 类 -->
        x="20" y="20" 
        width="100" height="80"
    />
    <circle 
        class="animated-circle"
        cx="200" cy="100" 
        r="30" 
        fill="green"
    />
</svg>
```



### 通过 JavaScript 动态操作

内联 SVG 的元素是 DOM 的一部分，可通过 JavaScript 动态创建、修改或删除。

**示例：动态添加图形**

```html
<svg id="mySvg" width="400" height="300"></svg>

<script>
    // 获取 SVG 容器（注意：SVG 命名空间！）
    const svg = document.getElementById('mySvg');

    // 创建一个矩形（必须使用 createElementNS 指定 SVG 命名空间）
    const rect = document.createElementNS('http://www.w3.org/2000/svg', 'rect');
    rect.setAttribute('x', '50');
    rect.setAttribute('y', '50');
    rect.setAttribute('width', '100');
    rect.setAttribute('height', '80');
    rect.setAttribute('fill', 'purple');

    // 添加到 SVG 容器
    svg.appendChild(rect);

    // 点击矩形时改变颜色
    rect.addEventListener('click', () => {
        rect.setAttribute('fill', Math.random() > 0.5 ? 'red' : 'blue');
    });
</script>
```



## `svg viewBox`、`width`、`height` 和 `svg` 内元素的 `width`、`height` 关系

在 SVG 中，`viewBox`、`width`、`height`（`<svg>` 根元素的属性）与内部图形元素的 `width`、`height`（如 `<rect>`、`<circle>` 等）共同决定了图形的**显示尺寸**和**坐标映射关系**。理解它们的关联是掌握 SVG 缩放、布局和响应式设计的关键。


### **一、核心概念定义**
#### 1. `<svg>` 根元素的 `width` 和 `height`
- **作用**：定义 SVG 画布的**物理显示尺寸**（即浏览器中实际占用的空间）。  
- **单位**：可以是像素（如 `width="400"`）、百分比（如 `width="100%"`）或其他 CSS 单位（如 `vw`、`vh`）。  
- **默认值**：无默认值，必须显式设置（或通过 CSS 定义）。  

#### 2. `<svg>` 根元素的 `viewBox`
- **作用**：定义 SVG 的**逻辑坐标系**（虚拟画布的范围），格式为 `min-x min-y width height`（四个数值）。  
  - `min-x/min-y`：逻辑坐标系的原点（左上角）偏移量（通常为 `0 0`）。  
  - `width/height`：逻辑坐标系的宽度和高度（决定了逻辑空间的大小）。  
- **核心功能**：将逻辑坐标系映射到物理显示尺寸（`width`/`height`），控制图形的**缩放**和**裁剪**。  

#### 3. 内部图形元素的 `width` 和 `height`
- **作用**：定义图形在**逻辑坐标系**中的尺寸（如 `<rect>` 的宽高、`<circle>` 的半径 `r` 等）。  
- **单位**：基于 `viewBox` 定义的逻辑坐标系（无单位，纯数值）。  


### **二、关键关系：逻辑坐标 → 物理显示**
SVG 的渲染流程可简化为：  
**逻辑坐标（由 `viewBox` 定义） → 映射到物理尺寸（由 `<svg>` 的 `width`/`height` 定义） → 最终显示在浏览器中**。

#### 1. 缩放规则：逻辑坐标与物理尺寸的比例
假设 `<svg>` 的 `width="W"`、`height="H"`，`viewBox="0 0 Vw Vh"`（`Vw` 是逻辑宽度，`Vh` 是逻辑高度），则：  
- **水平缩放比例**：`scaleX = W / Vw`  
- **垂直缩放比例**：`scaleY = H / Vh`  

图形在逻辑坐标系中的每个单位，会被缩放为物理尺寸中的 `scaleX`（水平）或 `scaleY`（垂直）像素。


#### 2. 示例说明
假设 SVG 配置如下：  
```html
<svg 
  width="400"    <!-- 物理宽度：400px -->
  height="300"   <!-- 物理高度：300px -->
  viewBox="0 0 100 100"  <!-- 逻辑坐标系：宽100，高100 -->
>
  <rect 
    x="10" y="10"  <!-- 逻辑坐标：x=10，y=10 -->
    width="80"     <!-- 逻辑宽度：80 -->
    height="80"    <!-- 逻辑高度：80 -->
    fill="blue"
  />
</svg>
```
- **逻辑坐标系**：虚拟画布是 `100×100` 的网格（左上角 `(0,0)`，右下角 `(100,100)`）。  
- **物理显示**：SVG 画布实际占用 `400×300` 像素的屏幕空间。  
- **缩放比例**：  
  - 水平：`400px / 100逻辑单位 = 4px/逻辑单位`（每个逻辑单位对应 4 像素）。  
  - 垂直：`300px / 100逻辑单位 = 3px/逻辑单位`（每个逻辑单位对应 3 像素）。  
- **矩形实际显示尺寸**：  
  - 宽度：`80逻辑单位 × 4px/单位 = 320px`。  
  - 高度：`80逻辑单位 × 3px/单位 = 240px`。  


### **三、`viewBox` 与 `<svg>` 尺寸的交互**
#### 1. 固定 `viewBox`，调整 `<svg>` 的 `width`/`height`  
此时，图形会按比例缩放以适应新的物理尺寸（可能变形，除非 `viewBox` 的宽高比与 `<svg>` 的宽高比一致）。  

**示例**：  
```html
<!-- 原始配置：viewBox="0 0 100 100"（宽高比 1:1） -->
<svg width="200" height="200">...</svg>  <!-- 正常缩放，图形不变形 -->
<svg width="200" height="300">...</svg>  <!-- 垂直拉伸，图形变高（可能变形） -->
```

#### 2. 固定 `<svg>` 的 `width`/`height`，调整 `viewBox`  
此时，逻辑坐标系的范围改变，图形的位置和大小会按比例适配新的逻辑空间（可能裁剪或留白）。  

**示例**：  
```html
<!-- 原始 viewBox="0 0 100 100" -->
<svg width="200" height="200" viewBox="0 0 200 200">...</svg>  <!-- 逻辑空间变大，图形缩小 -->
<svg width="200" height="200" viewBox="0 0 50 50">...</svg>   <!-- 逻辑空间变小，图形放大 -->
```

#### 3. 宽高比不一致时的裁剪与适配（`preserveAspectRatio`）  
默认情况下，若 `viewBox` 的宽高比（`Vw/Vh`）与 `<svg>` 的宽高比（`W/H`）不一致，SVG 会**拉伸图形**以填满画布。  
通过 `preserveAspectRatio` 属性可控制裁剪或适配方式（如居中、保持比例等）。  

**示例**：  
```html
<!-- viewBox="0 0 100 100"（宽高比 1:1），<svg>="300x200"（宽高比 3:2） -->
<svg 
  width="300" 
  height="200" 
  viewBox="0 0 100 100" 
  preserveAspectRatio="xMidYMid meet"  <!-- 保持比例，居中裁剪 -->
>
  <rect x="0" y="0" width="100" height="100" fill="red" />
</svg>
```
- `meet`：保持宽高比，图形完整显示（可能留白）。  
- `slice`：保持宽高比，图形填满画布（可能裁剪）。  
- `xMidYMid`：对齐方式为水平和垂直居中。  


### **四、内部元素的 `width`/`height` 与 `viewBox` 的关系**
内部图形元素的 `width`/`height` 是**基于 `viewBox` 定义的逻辑坐标系**的数值，与 `<svg>` 的物理尺寸无关。它们的实际显示大小由 `viewBox` 和 `<svg>` 的 `width`/`height` 共同决定。  

#### 示例：动态调整 `<svg>` 尺寸，内部元素自动适配
```html
<!-- viewBox="0 0 100 100"（逻辑宽高比 1:1） -->
<svg 
  id="mySvg" 
  width="200" 
  height="200" 
  viewBox="0 0 100 100"
>
  <rect 
    x="10" y="10" 
    width="80"  <!-- 逻辑宽度：80单位 -->
    height="80" <!-- 逻辑高度：80单位 -->
    fill="blue"
  />
</svg>

<script>
  // 动态修改 SVG 的物理尺寸（触发重新渲染）
  const svg = document.getElementById('mySvg');
  svg.setAttribute('width', '400'); // 物理宽度变为 400px
  svg.setAttribute('height', '100'); // 物理高度变为 100px
</script>
```
- 修改后，`viewBox` 仍为 `0 0 100 100`，但 `<svg>` 的宽高比（400:100=4:1）与 `viewBox` 的宽高比（1:1）不一致。  
- 矩形的逻辑尺寸（80×80）会被缩放为：  
  - 水平：`80 × (400/100) = 320px`（占满 400px 宽度的 80%）。  
  - 垂直：`80 × (100/100) = 80px`（占满 100px 高度的 80%）。  


### **五、总结**
| 属性/元素            | 作用域                   | 单位        | 与显示的关系                                                 |
| -------------------- | ------------------------ | ----------- | ------------------------------------------------------------ |
| `<svg>` 的 `width`   | SVG 物理显示尺寸         | 像素/%/vw等 | 定义 SVG 在页面中占用的实际空间。                            |
| `<svg>` 的 `height`  | SVG 物理显示尺寸         | 像素/%/vh等 | 同上。                                                       |
| `<svg>` 的 `viewBox` | SVG 逻辑坐标系           | 无单位数值  | 定义虚拟画布的范围（`min-x min-y width height`），控制逻辑坐标到物理尺寸的映射。 |
| 内部元素的 `width`   | 图形在逻辑坐标系中的尺寸 | 无单位数值  | 基于 `viewBox` 的逻辑坐标系，实际显示大小由 `viewBox` 和 `<svg>` 的 `width`/`height` 共同决定。 |
| 内部元素的 `height`  | 同上                     | 无单位数值  | 同上。                                                       |

**核心原则**：`viewBox` 定义了逻辑坐标系的范围，`<svg>` 的 `width`/`height` 定义了物理显示尺寸，两者的比例决定了图形如何缩放；内部元素的尺寸是逻辑坐标系中的数值，最终显示大小由逻辑坐标到物理尺寸的映射计算得出。



## `svg` 没有指定 `viewBox` 时默认值

在 SVG 中，如果未显式指定 viewBox，其默认值为 0 0 [svg-width] [svg-height]，其中 [svg-width] 和 [svg-height] 是 SVG 元素自身通过 width 和 height 属性定义的物理尺寸。

**示例：**

```html
<svg width="20" height="20">
    <rect x="4" y="9" width="12" height="2" rx="1" fill="#888b99" />
</svg>
```

- 由于未设置 viewBox，其默认值为 viewBox="0 0 20 20"（即 min-x=0, min-y=0, width=20, height=20）。



## 属性

### `stroke`

描边颜色。



### `stroke-width`

描边宽度。



### `fill`

定义了图形元素（如 `<rect>`、`<circle>`、`<path>` 等）的内部填充区域的颜色或图案。对于文本元素（`<text>`），fill 则控制文本的显示颜色。