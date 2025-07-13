# JavaScript



## 模板字符串

JavaScript 的**模板字符串**（Template Literals）是 ES6（ECMAScript 2015）引入的一种新特性，用于更方便地创建和操作字符串。它通过反引号（```）包裹字符串，并提供了一些强大的功能，比如字符串插值、多行字符串和嵌套表达式。

### 模板字符串的基本语法

1. 使用反引号：
   - 模板字符串使用反引号（```）而不是单引号（`'`）或双引号（`"`）来定义字符串。
2. 字符串插值：
   - 在模板字符串中，可以通过 `${expression}` 的形式嵌入表达式，表达式会被计算并替换为结果。

### 示例代码

```javascript
// 基本用法
const name = "Alice";
const greeting = `Hello, ${name}!`;
console.log(greeting); // 输出: Hello, Alice!
 
// 嵌入表达式
const a = 5;
const b = 10;
console.log(`The sum of ${a} and ${b} is ${a + b}.`); // 输出: The sum of 5 and 10 is 15.
 
// 多行字符串
const multiLineString = `This is a
multi-line
string.`;
console.log(multiLineString);
/*
输出:
This is a
multi-line
string.
*/
 
// 嵌套模板字符串
const firstName = "Bob";
const lastName = "Smith";
const fullName = `${firstName} ${lastName}`;
const message = `Hello, my name is ${fullName}.`;
console.log(message); // 输出: Hello, my name is Bob Smith.
```

### 模板字符串的优势

1. 可读性：
   - 模板字符串使字符串插值和多行字符串的书写更加直观和易读。
2. 灵活性：
   - 可以在字符串中嵌入任意有效的 JavaScript 表达式，而不仅仅是变量。
3. 避免转义：
   - 在模板字符串中，不需要像传统字符串那样使用反斜杠（`\`）来转义换行符或引号。

### 注意事项

- 模板字符串中的表达式会被计算并替换为结果，因此表达式中不应包含会引发副作用的代码（如修改全局变量），除非这是你期望的行为。
- 模板字符串本身并不会自动进行 HTML 转义。如果你需要在模板字符串中插入用户输入的内容以防止 XSS 攻击，需要手动进行转义。

模板字符串是现代 JavaScript 开发中非常实用的一个特性，它大大简化了字符串的创建和操作，提高了代码的可读性和可维护性。



## `prototype`

>详细用法请参考本站 [示例](https://gitee.com/dexterleslie/demonstration/tree/main/front-end/html+js+css/js-prototype)

特点如下：

- 任何函数都有 prototype 属性，并且 prototype 是 Object 类型的一个实例
- prototype 对象属性 constructor 指向该函数本身
- 给 prototype 对象添加属性/方法，函数所有实例对象自动拥有 prototype 中的属性/方法

示例：

```html
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Document</title>
    <script type="text/javascript">
        // 任何函数都有 prototype 属性，并且 prototype 是 Object 类型的一个实例
        console.log(`typeof Date: ${typeof Date}`)
        console.log(`typeof Date.prototype: ${typeof Date.prototype}`)

        function MyFunction() {

        }
        console.log(`typeof MyFunction: ${typeof MyFunction}`)
        console.log(`typeof MyFunction.prototype: ${typeof MyFunction.prototype}`)

        // constructor
        // prototype 对象属性 constructor 指向该函数本身
        console.log(`Date.prototype.constructor===Date: ${Date.prototype.constructor===Date}`)
        console.log(`MyFunction.prototype.constructor===MyFunction: ${MyFunction.prototype.constructor===MyFunction}`)
    
        // prototype添加方法
        // 给 prototype 对象添加属性/方法，函数所有实例对象自动拥有 prototype 中的属性/方法
        MyFunction.prototype.sayHello = function() {
            console.log('Hello Dexterleslie')
        }
        let myFunction = new MyFunction()
        myFunction.sayHello()
    </script>
</head>
<body>
    
</body>
</html>
```



## `var` 和 `const` 区别

在 JavaScript 中，`var` 和 `const` 都是用于声明变量的关键字，但它们在作用域、可变性、提升行为等方面有显著差异。以下是核心区别的详细对比：


### 1. **作用域（Scope）**
- **`var`**：函数作用域（Function-scoped）。  
  变量在声明它的函数内有效，若在全局作用域声明则属于全局对象（如 `window`）。  
  示例：  
  ```javascript
  function example() {
    if (true) {
      var x = 10; // 函数作用域，x 在整个函数内有效
    }
    console.log(x); // 输出 10（可以访问）
  }
  ```

- **`const`**：块级作用域（Block-scoped）。  
  变量仅在声明它的块（`{}` 包裹的区域，如 `if`、`for`、`while` 等）内有效。  
  示例：  
  ```javascript
  function example() {
    if (true) {
      const y = 20; // 块级作用域，仅 if 块内有效
    }
    console.log(y); // 报错：ReferenceError: y is not defined
  }
  ```


### 2. **变量提升（Hoisting）**
- **`var`**：支持变量提升，但仅提升声明，赋值保留在原处。  
  声明前访问会得到 `undefined`（不会报错）。  
  示例：  
  ```javascript
  console.log(a); // 输出 undefined（声明被提升到顶部）
  var a = 1;
  ```

- **`const`**：同样存在提升（所有声明都会提升），但由于**必须初始化**且存在“暂时性死区”（Temporal Dead Zone, TDZ），声明前访问会直接报错。  
  示例：  
  ```javascript
  console.log(b); // 报错：ReferenceError（TDZ 内访问）
  const b = 2;
  ```


### 3. **重复声明（Redeclaration）**
- **`var`**：允许重复声明同一变量（后声明的覆盖先声明的），不会报错。  
  示例：  
  ```javascript
  var c = 3;
  var c = 4; // 不报错，c 最终为 4
  ```

- **`const`**：禁止重复声明同一变量（同一作用域内），否则报错。  
  示例：  
  ```javascript
  const d = 5;
  const d = 6; // 报错：SyntaxError（重复声明）
  ```


### 4. **可变性（Immutability）**
- **`var`**：声明的变量可以**重新赋值**或**修改值**（无限制）。  
  示例：  
  ```javascript
  var e = 10;
  e = 20; // 允许重新赋值
  e.name = "test"; // 若 e 是对象，可修改属性（允许）
  ```

- **`const`**：声明的变量**不能重新赋值**（绑定不可变），但如果是对象/数组，其内部属性/元素可以修改（值可变）。  
  示例：  
  ```javascript
  const f = 30;
  f = 40; // 报错：TypeError（不能重新赋值）
  
  const obj = { name: "Alice" };
  obj.name = "Bob"; // 允许修改对象属性
  obj = { age: 18 }; // 报错：不能重新赋值绑定
  
  const arr = [1, 2];
  arr.push(3); // 允许修改数组内容
  arr = [4, 5]; // 报错：不能重新赋值绑定
  ```


### 5. **初始化要求**
- **`var`**：声明时**可选初始化**（未初始化时默认值为 `undefined`）。  
  示例：  
  ```javascript
  var g; // 合法，g 初始值为 undefined
  ```

- **`const`**：声明时**必须初始化**（否则语法错误）。  
  示例：  
  ```javascript
  const h; // 报错：SyntaxError（缺少初始化）
  ```


### 6. **全局作用域行为**
- **`var`**：在全局作用域声明的变量会成为 `window` 对象的属性（浏览器环境）。  
  示例：  
  ```javascript
  var globalVar = "hello";
  console.log(window.globalVar); // 输出 "hello"
  ```

- **`const`**：在全局作用域声明的变量**不会成为 `window` 对象的属性**（更安全，避免全局污染）。  
  示例：  
  ```javascript
  const globalConst = "world";
  console.log(window.globalConst); // 输出 undefined（非严格模式）
  ```


### 总结：如何选择？
- 优先使用 `const`：当变量不需要重新赋值时（如固定配置、对象/数组引用），用 `const` 声明，确保不可变性，减少意外修改。  
- 其次使用 `let`：当需要变量重新赋值时（如循环计数器），用 `let` 声明（块级作用域更安全）。  
- 避免使用 `var`：除非需要兼容旧代码或明确需要函数作用域（现代 JS 中已很少需要）。

**一句话记忆**：`const` 是“常量绑定”（绑定不可变），`var` 是“函数作用域变量”（灵活但易出错）。



## `viewport`、`document`、`window` 区别

在 Web 开发中，`viewport`（视口）、`window`（窗口）和 `document`（文档）是三个核心概念，分别对应不同的层级和功能。以下是它们的详细区别与关联：


### **1. 定义与核心定位**
#### **`viewport`（视口）**  
- **定义**：指浏览器中**实际显示网页内容的区域**（即用户肉眼可见的部分），不包含浏览器的工具栏、标签栏、地址栏等浏览器自身界面。  
- **核心定位**：属于 **BOM（Browser Object Model，浏览器对象模型）** 的范畴，描述浏览器窗口的“显示区域”。  


#### **`window`（窗口）**  
- **定义**：代表浏览器的一个**标签页或窗口**，是 BOM 的核心对象。它管理浏览器窗口的所有行为（如打开/关闭窗口、调整尺寸、滚动等），并包含当前页面的所有资源（如 `document`、`location`、`navigator` 等）。  
- **核心定位**：BOM 的顶层对象，是浏览器窗口的“控制器”。  


#### **`document`（文档）**  
- **定义**：代表浏览器中**加载的 HTML 文档**，是 DOM（Document Object Model，文档对象模型）的根节点。它包含页面的所有内容（如元素、文本、样式等），并通过 DOM API 提供对页面结构的操作能力。  
- **核心定位**：DOM 的入口，负责描述和操作页面的“结构和内容”。  


### **2. 层级关系与关联**
三者的关系可以用“**窗口 → 视口 → 文档**”来概括：  
- `window` 是浏览器窗口的全局对象，包含 `document`（`window.document`）和 `viewport`（`window.innerWidth` 等属性）。  
- `document` 是 `window` 的子对象，描述页面的 HTML 结构。  
- `viewport` 是 `window` 中用于显示 `document` 的区域，其尺寸直接影响 `document` 的渲染效果（如响应式布局）。  


### **3. 关键属性与方法对比**
| **维度**          | **`viewport`**                                               | **`window`**                                                 | **`document`**                                               |
| ----------------- | ------------------------------------------------------------ | ------------------------------------------------------------ | ------------------------------------------------------------ |
| **所属模型**      | BOM（浏览器对象模型）                                        | BOM（浏览器对象模型）                                        | DOM（文档对象模型）                                          |
| **核心作用**      | 描述浏览器“显示区域”的尺寸和特性                             | 控制浏览器窗口的行为（如打开/关闭、事件）                    | 操作 HTML 文档的结构、内容和样式                             |
| **典型属性/方法** | `innerWidth`/`innerHeight`（视口尺寸）<br>`devicePixelRatio`（设备像素比） | `open()`（打开新窗口）<br>`resizeTo()`（调整窗口尺寸）<br>`scrollX`/`scrollY`（滚动位置） | `documentElement`（HTML 根元素）<br>`getElementById()`（查询元素）<br>`body`（页面主体） |
| **示例**          | `console.log(window.innerWidth);`（输出视口宽度）            | `window.location.href = "https://example.com";`（跳转页面）  | `document.getElementById("app");`（获取元素）                |


### **4. 常见场景说明**
#### **（1）视口（`viewport`）的应用**  
- 响应式设计：通过 `vw`/`vh`（基于视口宽高）或 `min-width/max-width` 媒体查询适配不同屏幕。  
- 移动端适配：通过 `<meta name="viewport" content="width=device-width">` 强制浏览器按设备宽度渲染，避免缩放。  


#### **（2）窗口（`window`）的应用**  
- 窗口控制：`window.open()` 打开新标签页，`window.close()` 关闭当前窗口（需用户触发）。  
- 事件监听：`window.onload`（页面完全加载）、`window.resize`（窗口尺寸变化）、`window.scroll`（滚动事件）。  


#### **（3）文档（`document`）的应用**  
- DOM 操作：`document.createElement()` 创建元素，`document.querySelector()` 查询元素，`document.body.innerHTML` 修改页面内容。  
- 资源加载：`document.write()` 动态写入内容（不推荐，会覆盖现有文档）。  


### **5. 总结**
- **`viewport`**：用户“看到”的区域，决定页面内容的可见范围（BOM 视角）。  
- **`window`**：浏览器窗口的“控制器”，管理窗口行为并包含页面所有资源（BOM 核心）。  
- **`document`**：页面的“结构说明书”，通过 DOM API 操作 HTML 内容（DOM 入口）。  

三者协同工作：`window` 提供窗口能力，`document` 定义页面结构，`viewport` 决定结构如何显示。



## 获取 `viewport` 高度和宽度



### 概念

`window.innerWidth` 和 `window.innerHeight` 是浏览器 BOM（浏览器对象模型）中的两个属性，用于获取**当前浏览器视口（Viewport）的宽度和高度**（即用户肉眼可见的网页显示区域的尺寸）。以下是详细解析：


#### **核心定义**
- **`window.innerWidth`**：返回当前视口的**宽度**（单位：像素），包含视口内的滚动条（如果存在）。  
- **`window.innerHeight`**：返回当前视口的**高度**（单位：像素），同样包含滚动条（如果存在）。  


#### **关键特性**
##### 1. **基于视口（而非窗口或屏幕）**  
视口是浏览器中实际渲染网页的区域，不包含浏览器的工具栏、标签栏、地址栏等浏览器自身界面。例如：  
- 当浏览器窗口最大化时，视口尺寸接近屏幕可用区域（但可能略小，因浏览器UI占用部分空间）。  
- 当用户手动调整窗口大小时，`innerWidth` 和 `innerHeight` 会实时更新。  


##### 2. **包含滚动条（若存在）**  
如果页面内容超过视口高度，浏览器会显示垂直滚动条。此时：  
- `innerHeight` 包含滚动条的宽度（例如，若滚动条占 17px，则 `innerHeight` 是视口可用高度 + 17px）。  
- 若需获取**不包含滚动条的视口尺寸**，可使用 `document.documentElement.clientWidth`（对应宽度）和 `document.documentElement.clientHeight`（对应高度）。  


##### 3. **动态更新**  
视口尺寸会随以下操作实时变化，因此这两个属性是“动态值”：  
- 用户手动调整浏览器窗口大小（如拖拽窗口边缘）。  
- 设备旋转（如手机从竖屏切换为横屏）。  
- 浏览器缩放（如按 `Ctrl+ +` 放大页面，此时视口实际像素尺寸会缩小，但 `innerWidth` 会按缩放后的逻辑像素返回）。  


#### **与其他属性的对比**
为避免混淆，以下是相关属性的区别：

| **属性**                                | **描述**                                           | **是否包含滚动条** | **典型用途**                       |
| --------------------------------------- | -------------------------------------------------- | ------------------ | ---------------------------------- |
| `window.innerWidth`                     | 视口宽度（包含滚动条）                             | 是                 | 响应式布局、动态计算元素尺寸       |
| `window.innerHeight`                    | 视口高度（包含滚动条）                             | 是                 | 同上                               |
| `document.documentElement.clientWidth`  | 文档根元素（`<html>`）的客户端宽度（不包含滚动条） | 否                 | 计算页面内容区的实际可用空间       |
| `document.documentElement.clientHeight` | 文档根元素的客户端高度（不包含滚动条）             | 否                 | 同上                               |
| `screen.width`                          | 屏幕的总物理宽度（所有显示器总宽度，多屏时为总和） | -                  | 获取设备屏幕硬件尺寸（非网页视口） |


#### **示例说明**
假设当前浏览器视口（可见区域）为：  
- 宽度 `1200px`（无滚动条）  
- 高度 `800px`（有垂直滚动条，占 `17px`）  

则：  
```javascript
console.log(window.innerWidth);   // 输出 1200（无滚动条，宽度即视口宽度）  
console.log(window.innerHeight);  // 输出 817（包含滚动条的 17px）  

// 对比不包含滚动条的尺寸：
console.log(document.documentElement.clientWidth);   // 输出 1200（与 innerWidth 相同，因无滚动条）  
console.log(document.documentElement.clientHeight);  // 输出 800（不包含滚动条的真实内容高度）  
```


#### **实际应用场景**
`window.innerWidth` 和 `window.innerHeight` 主要用于**响应式设计**和**动态布局计算**，例如：  
- 根据视口宽度调整元素字体大小（如 `font-size: ${window.innerWidth / 100}px`）。  
- 判断设备方向（横屏/竖屏）：  
  ```javascript
  const isLandscape = window.innerWidth > window.innerHeight;
  ```
- 动态加载不同尺寸的图片（根据视口宽度选择 `srcset`）。  


#### **注意事项**
- **浏览器兼容性**：现代浏览器（Chrome、Firefox、Edge、Safari 等）均支持，IE9+ 也支持（IE8 及以下使用 `document.documentElement.clientWidth` 替代）。  
- **缩放影响**：浏览器缩放（如 125% 缩放）会改变逻辑像素与物理像素的比例，但 `innerWidth` 返回的是逻辑像素值（符合 CSS 像素标准）。  
- **移动端适配**：移动端浏览器通常默认隐藏滚动条，因此 `innerWidth` 和 `clientWidth` 数值可能一致（需通过视口元标签 `<meta name="viewport">` 优化）。  

**总结**：`window.innerWidth` 和 `window.innerHeight` 是获取**当前视口（可见区域）尺寸**的核心属性，广泛用于响应式设计和动态布局计算。



### 实验

>详细用法请参考本站 [示例](https://gitee.com/dexterleslie/demonstration/tree/main/front-end/html+js+css/demo-js-viewport-window-document)

```html
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Document</title>
</head>
<body>
    <button onclick="handleOnclick()">点击我查看viewport、window、document区别</button>

    <style>
        html, body {
            height: 800px;
        }
    </style>
    <script>
        function handleOnclick() {
            // 通过 window 对象获取 viewport 宽度和高度
            // https://sabe.io/blog/javascript-width-height-browser-viewport
            var viewportWidth = window.innerWidth
            var viewportHeight = window.innerHeight
            console.log(`viewportWidth=${viewportWidth},viewportHeight=${viewportHeight}`)

            // var documentWidth = document.body.clientWidth
            // var documentHeight = document.body.clientHeight
            // console.log(`documentInnerWidth=${documentWidth},documenInnerHeight=${documentHeight}`)
        }
    </script>
</body>
</html>
```



## 获取 `<body>` 的高度和宽度



### 概念

`document.body.clientWidth` 和 `document.body.clientHeight` 是用于获取 HTML 文档中 `<body>` 元素**内容区域（含内边距但不含边框、外边距和滚动条）的尺寸**的属性。以下是详细解析：


#### **核心定义**
- **`document.body.clientWidth`**：返回 `<body>` 元素的**内容宽度**（单位：像素），计算规则为：  
  `内容宽度 + 左内边距(padding-left) + 右内边距(padding-right)`（不包含边框、外边距或滚动条）。  

- **`document.body.clientHeight`**：返回 `<body>` 元素的**内容高度**（单位：像素），计算规则为：  
  `内容高度 + 上内边距(padding-top) + 下内边距(padding-bottom)`（同样不包含边框、外边距或滚动条）。  


#### **关键特性**
##### 1. **基于 `<body>` 元素的内容区域**  
`<body>` 是 HTML 文档的主体容器，包含页面可见的所有内容（如文本、图片、div 等）。`clientWidth` 和 `clientHeight` 仅关注 `<body>` 元素自身的内容区域，不涉及其子元素的尺寸或溢出情况。  


##### 2. **包含内边距（Padding），但不包含边框、外边距或滚动条**  
- **内边距（Padding）**：会被计入 `clientWidth`/`clientHeight`。例如，若 `<body>` 设置了 `padding: 20px`，则这 20px 会被加到内容宽度/高度中。  
- **边框（Border）**：不会被计入。即使 `<body>` 有 `border: 1px solid`，也不会影响这两个属性的值。  
- **外边距（Margin）**：不会被计入。`<body>` 的 `margin` 是其与其他元素（如父元素 `<html>`）的间距，不影响自身内容区域尺寸。  
- **滚动条**：如果 `<body>` 内容溢出导致出现滚动条，滚动条会**占据内容区域的空间**，因此 `clientWidth`/`clientHeight` 会**减去滚动条的宽度/高度**（例如，垂直滚动条通常占 17px，会导致 `clientWidth` 减少 17px）。  


##### 3. **受文档模式（DOCTYPE）和样式影响**  
- **标准模式（Standard Mode）**：当文档声明了标准 DOCTYPE（如 `<!DOCTYPE html>`）时，`<html>` 元素的高度由 `<body>` 内容决定，`<body>` 的尺寸直接反映页面可见内容的区域。  
- **怪异模式（Quirks Mode）**：若未声明 DOCTYPE 或使用旧版 IE 的怪异模式，`<body>` 的尺寸计算可能受浏览器默认样式影响（如默认 `margin: 8px`），导致 `clientWidth`/`clientHeight` 包含额外的空白。  


#### **与其他属性的对比**
为避免混淆，以下是相关属性的区别（以 `<body>` 为例）：

| **属性**                               | **描述**                                                     | **是否包含内边距** | **是否包含滚动条**     | **典型用途**                         |
| -------------------------------------- | ------------------------------------------------------------ | ------------------ | ---------------------- | ------------------------------------ |
| `document.body.clientWidth`            | `<body>` 内容宽度 + 内边距                                   | 是                 | 否（滚动条会挤占空间） | 计算 `<body>` 可见内容区的宽度       |
| `document.body.clientHeight`           | `<body>` 内容高度 + 内边距                                   | 是                 | 否（滚动条会挤占空间） | 计算 `<body>` 可见内容区的高度       |
| `document.body.offsetWidth`            | `<body>` 内容宽度 + 内边距 + 边框                            | 是                 | 是（包含滚动条）       | 计算 `<body>` 占用的总空间（含边框） |
| `document.documentElement.clientWidth` | `<html>` 元素的内容宽度 + 内边距（标准模式下与视口宽度一致） | 是                 | 否（滚动条挤占空间）   | 替代方案（更稳定）                   |


#### **示例说明**
假设 `<body>` 的 CSS 样式为：  
```css
body {
  margin: 0;          /* 无外边距 */
  padding: 20px;      /* 上下左右内边距各 20px */
  border: 5px solid;  /* 边框 5px（不影响 client 尺寸） */
  overflow: auto;     /* 内容溢出时显示滚动条 */
}
```
且页面内容足够多，导致 `<body>` 出现垂直滚动条（占 17px）和水平滚动条（占 17px）：  

- **`clientWidth`**：内容宽度 + 左内边距（20px） + 右内边距（20px） - 水平滚动条宽度（17px）。  
  例如，若内容实际宽度为 `1000px`，则 `clientWidth = 1000 + 20 + 20 - 17 = 1023px`。  

- **`clientHeight`**：内容高度 + 上内边距（20px） + 下内边距（20px） - 垂直滚动条高度（17px）。  
  例如，若内容实际高度为 `800px`，则 `clientHeight = 800 + 20 + 20 - 17 = 823px`。  


#### **实际应用场景**
`document.body.clientWidth` 和 `clientHeight` 主要用于**获取 `<body>` 元素自身的可见内容区域尺寸**，常见用途包括：  

##### 1. **计算页面可用空间**  
当需要动态调整 `<body>` 内元素的位置或大小时（如弹出层、浮动菜单），可通过这两个属性获取 `<body>` 的实际可用区域（排除滚动条和边框）。  

##### 2. **判断内容是否溢出**  
结合 `scrollWidth` 和 `scrollHeight`（`<body>` 内容的总宽度和高度，含溢出部分），可判断内容是否超出 `<body>` 的可见区域：  
```javascript
const isContentOverflowing = 
  document.body.scrollWidth > document.body.clientWidth || 
  document.body.scrollHeight > document.body.clientHeight;
```

##### 3. **兼容旧版浏览器**  
在某些旧版浏览器（如 IE）中，`document.documentElement.clientWidth` 可能无法正确反映视口尺寸，此时 `document.body.clientWidth` 可作为备用方案（需结合文档模式判断）。  


#### **注意事项**
- **默认样式的影响**：浏览器默认会给 `<body>` 添加 `margin: 8px`（不同浏览器可能略有差异），这会导致 `<body>` 与 `<html>` 之间出现空白。若需精确计算，建议通过 CSS 重置 `<body>` 的 `margin` 和 `padding`（如 `body { margin: 0; padding: 0; }`）。  
- **滚动条的动态变化**：当内容溢出状态改变（如窗口缩放导致滚动条出现/消失）时，`clientWidth`/`clientHeight` 会实时更新，因此需在需要时重新获取值。  
- **与视口的区别**：`document.body.clientWidth` 是 `<body>` 元素自身的内容区域尺寸，而 `window.innerWidth` 是浏览器视口的尺寸（可能包含 `<body>` 外的滚动条）。例如，若 `<body>` 有水平滚动条，`window.innerWidth` 会比 `document.body.clientWidth` 小（因为滚动条占用了视口空间）。  

**总结**：`document.body.clientWidth` 和 `clientHeight` 是获取 `<body>` 元素**内容区域（含内边距）尺寸**的核心属性，适用于需要精确计算 `<body>` 自身可见空间的场景。



### 实验

>详细用法请参考本站 [示例](https://gitee.com/dexterleslie/demonstration/tree/main/front-end/html+js+css/demo-js-viewport-window-document)

```html
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Document</title>
</head>
<body>
    <button onclick="handleOnclick()">点击我查看viewport、window、document区别</button>

    <style>
        body {
            height: 800px;
            /*
                body 默认 margin 为 8px，设置 margin: 0; 后，window.innerWidth = document.body.clientWidth，否则会相差 16px。
                因为 window.innerWidth 表示浏览器​​视口（Viewport）的可见区域宽度​​（用户肉眼可见的网页区域），包含视口内的滚动条（若存在）。例如：若视口实际宽度为 1200px，且有垂直滚动条（占 17px），则 window.innerWidth = 1200px（滚动条属于视口的一部分）。
                document.body.clientWidth 表示 <body> 元素​​内容区域的宽度​​（即 <body> 内部内容的实际可用空间），计算规则为：内容宽度 + 左内边距(padding-left) + 右内边距(padding-right)（不包含边框、外边距或滚动条）。
                https://stackoverflow.com/questions/13127887/how-wide-is-the-default-body-margin
            */
            margin: 0;
        }
    </style>
    <script>
        function handleOnclick() {
            // 通过 window 对象获取 viewport 宽度和高度
            // https://sabe.io/blog/javascript-width-height-browser-viewport
            var viewportWidth = window.innerWidth
            var viewportHeight = window.innerHeight
            console.log(`viewportWidth=${viewportWidth},viewportHeight=${viewportHeight}`)

            var documentWidth = document.body.clientWidth
            var documentHeight = document.body.clientHeight
            console.log(`documentInnerWidth=${documentWidth},documenInnerHeight=${documentHeight}`)
        }
    </script>
</body>
</html>
```



## 为何 `document.body.clientWidth` 和 `window.innerWidth` 不相等呢？

`document.body.clientWidth` 和 `window.innerWidth` 不相等的核心原因是它们的**计算基准不同**：前者是 `<body>` 元素自身的内容区域尺寸（含内边距），后者是浏览器视口的可见区域尺寸（含滚动条）。以下是具体原因和场景分析：


### **1. 定义差异：计算基准不同**
- **`window.innerWidth`**：表示浏览器**视口（Viewport）的可见区域宽度**（用户肉眼可见的网页区域），包含视口内的滚动条（若存在）。  
  例如：若视口实际宽度为 `1200px`，且有垂直滚动条（占 `17px`），则 `window.innerWidth = 1200px`（滚动条属于视口的一部分）。  

- **`document.body.clientWidth`**：表示 `<body>` 元素**内容区域的宽度**（即 `<body>` 内部内容的实际可用空间），计算规则为：  
  `内容宽度 + 左内边距(padding-left) + 右内边距(padding-right)`（不包含边框、外边距或滚动条）。  


### **2. 关键影响因素**
#### （1）滚动条的存在
当页面内容溢出时，`<body>` 可能出现滚动条（垂直或水平），这会直接影响两者的数值：  
- **`window.innerWidth`**：包含滚动条的宽度（例如，垂直滚动条通常占 `17px`，因此 `window.innerWidth` 会比无滚动条时小 `17px`）。  
- **`document.body.clientWidth`**：滚动条会**挤占 `<body>` 的内容区域**，因此 `clientWidth` 会**减去滚动条的宽度**（例如，若 `<body>` 内容原本宽度为 `1000px`，垂直滚动条占 `17px`，则 `clientWidth = 1000px - 17px = 983px`）。  

**示例**：  
假设视口无滚动条时宽度为 `1200px`，`<body>` 有垂直滚动条（占 `17px`）：  
- `window.innerWidth` = `1200px`（视口总宽度，包含滚动条）。  
- `document.body.clientWidth` = `<body>` 内容宽度 + 内边距 - 滚动条宽度（若内容宽度为 `1200px`，内边距为 `0`，则 `clientWidth = 1200 - 17 = 1183px`）。  


#### （2）`<body>` 的内边距（Padding）
`document.body.clientWidth` 包含 `<body>` 的内边距，而 `window.innerWidth` 不包含：  
- 若 `<body>` 设置了 `padding: 20px`（左右各 `20px`），则 `clientWidth` 会额外增加 `40px`（左+右内边距）。  
- `window.innerWidth` 是视口的总宽度，与 `<body>` 的内边距无关（内边距属于 `<body>` 内部，不影响视口尺寸）。  

**示例**：  
视口宽度为 `1200px`，`<body>` 样式为 `padding: 20px`，且无滚动条：  
- `window.innerWidth` = `1200px`（视口总宽度）。  
- `document.body.clientWidth` = `<body>` 内容宽度 + 左内边距 + 右内边距。若内容宽度为 `1160px`（`1200 - 20*2`），则 `clientWidth = 1160 + 20 + 20 = 1200px`（此时两者相等）。  
  但如果内容宽度超过 `1160px`（如内容宽度为 `1200px`），则 `<body>` 会出现水平滚动条（占 `17px`），此时 `clientWidth = 1200 + 20 + 20 - 17 = 1223px`，而 `window.innerWidth` 仍为 `1200px`（因滚动条挤占了视口空间）。  


#### （3）文档模式（DOCTYPE）的影响
文档模式决定了浏览器如何解析 HTML 和 CSS，默认模式不同会导致 `<html>` 和 `<body>` 的尺寸计算规则变化：  
- **标准模式（Standard Mode）**：声明 `<!DOCTYPE html>` 时，`<html>` 元素的高度由 `<body>` 内容决定，`<body>` 的尺寸与视口高度强相关。此时 `window.innerHeight` 通常等于 `document.documentElement.clientHeight`（而非 `document.body.clientHeight`）。  
- **怪异模式（Quirks Mode）**：未声明 DOCTYPE 或使用旧版 IE 模式时，`<body>` 可能有默认的 `margin: 8px` 和 `padding`，导致 `document.body.clientWidth` 包含额外的空白，与视口尺寸不一致。  


#### （4）`<body>` 的样式设置
`<body>` 的 CSS 样式（如 `width`、`margin`、`overflow`）会直接影响其 `clientWidth`：  
- 若 `<body>` 设置 `width: 100%`，则其内容宽度会尝试填满父元素（即 `<html>`），但 `<html>` 的宽度通常等于视口宽度（`window.innerWidth`）。此时 `document.body.clientWidth` 可能与 `window.innerWidth` 相等（无内边距/滚动条时）。  
- 若 `<body>` 设置 `overflow: hidden`（隐藏滚动条），则内容溢出时不会显示滚动条，`document.body.clientWidth` 可能等于 `window.innerWidth`（无滚动条挤占）。  


### **总结：何时相等？何时不等？**
| **场景**                     | `window.innerWidth` 与 `document.body.clientWidth` 是否相等？ | 原因                                                         |
| ---------------------------- | ------------------------------------------------------------ | ------------------------------------------------------------ |
| 无滚动条、`<body>` 无内边距  | 可能相等（需满足 `<body>` 内容宽度 = 视口宽度 - `<body>` 内边距） | `<body>` 内容区域刚好填满视口，且无滚动条挤占空间。          |
| 有滚动条（垂直或水平）       | 不相等                                                       | 滚动条会挤占 `<body>` 内容区域（`clientWidth` 减去滚动条宽度），但属于视口的一部分（`innerWidth` 包含滚动条）。 |
| `<body>` 有内边距（Padding） | 可能不等                                                     | `clientWidth` 包含内边距，而 `innerWidth` 不包含。           |
| 怪异模式（Quirks Mode）      | 通常不等                                                     | `<body>` 可能有默认 `margin` 或 `padding`，导致尺寸计算偏差。 |


### **示例验证**
假设浏览器视口宽度为 `1200px`，测试以下场景：  

#### 场景 1：无滚动条、`<body>` 无内边距  
```css
body { margin: 0; padding: 0; overflow: hidden; } /* 隐藏滚动条 */
```
- `window.innerWidth` = `1200px`（视口宽度）。  
- `document.body.clientWidth` = `1200px`（内容宽度 + 0 内边距 - 0 滚动条）。  
**结果**：相等。  


#### 场景 2：有垂直滚动条，`<body>` 无内边距  
```css
body { margin: 0; padding: 0; overflow: auto; } /* 内容溢出时显示滚动条 */
```
- 页面内容高度足够大（触发垂直滚动条，占 `17px`）。  
- `window.innerWidth` = `1200px`（视口宽度，包含滚动条）。  
- `document.body.clientWidth` = `1200px - 17px = 1183px`（滚动条挤占内容区域）。  
**结果**：不等。  


#### 场景 3：`<body>` 有内边距，无滚动条  
```css
body { margin: 0; padding: 20px; overflow: hidden; } /* 隐藏滚动条 */
```
- `window.innerWidth` = `1200px`（视口宽度）。  
- `document.body.clientWidth` = `<body>` 内容宽度 + 20px（左内边距） + 20px（右内边距）。  
  若内容宽度为 `1160px`（`1200 - 20*2`），则 `clientWidth = 1160 + 20 + 20 = 1200px`。  
  **结果**：相等（内容宽度刚好填满视口减去内边距）。  


### **结论**
`document.body.clientWidth` 和 `window.innerWidth` 不相等的根本原因是：  
- `window.innerWidth` 是**视口可见区域的总宽度**（含滚动条）；  
- `document.body.clientWidth` 是**`<body>` 元素内容区域的宽度**（含内边距，不含滚动条）。  

滚动条的存在、`<body>` 的内边距样式、文档模式等因素会导致两者数值差异。实际开发中，若需获取视口尺寸，应优先使用 `window.innerWidth`；若需获取 `<body>` 自身内容区域尺寸，则使用 `document.body.clientWidth`。



## `clientWidth`、`clientHeight`、`clientLeft`、`clientTop`

>提醒：不包含 `border` 的尺寸。
>
>详细用法请参考本站 [示例](https://gitee.com/dexterleslie/demonstration/blob/main/front-end/html+js+css/js-clientx-screenx-offsetx-pagex-clientwidth/index.html)

`HTML` 如下：

```html
<hr>
<div>
    演示clientWidth(不包括border的尺寸)、clientHeight、clientLeft、clientTop属性<br>
    不存在垂直滚动条clientWidth=paddingWidth+contentWidth，例子 70px=10px*2+50px<br>
    存在垂直滚动条clientWidth=paddingWidth+contentWidth-scollbarWidth，例子 55px=10px*2+50px-15px<br>
    默认情况下clientLeft和clientTop实际是border的宽度<br>
    如果文字方向从右往左(默认从左往右,通过设置 direction: rtl;)进行排列,且存在垂直滚动条的情况下clientLeft=borderWidth+scrollWidth<br>
    https://blog.csdn.net/yb305/article/details/109990818
</div>
<div style="border-style:solid;border-width:1px;">
    <button onclick="demo1()">点击我</button>

    <div id="div1">
        内容内容内容内容内容内容内容内容内容内容内容内容内容内容内容内容内容内容内容内容内容内容内容内容内容内容内容
    </div>

    <div id="div12">
        内容内容内容内容内容内容内容内容内容内容内容内容内容内容内容内容内容内容内容内容内容内容内容内容内容内容内容
    </div>
</div>
```

`CSS` 如下：

```css
#div1 {
    margin: 10px;
    background-color: antiquewhite;
    width: 50px;
    height: 50px;
    border-style: dashed;
    border-width: 20px;
    border-color: gray;
    padding: 10px;
    overflow: hidden;
}

#div12 {
    margin: 10px;
    background-color: antiquewhite;
    width: 50px;
    height: 50px;
    border-style: dashed;
    border-width: 20px;
    border-color: gray;
    padding: 10px;
    overflow: scroll;
    direction: rtl;
}
```

`JS` 如下：

```javascript
function demo1() {
    // 没有scrollbar时
    var element = document.getElementById('div1')

    var clientWidth = element.clientWidth
    var clientHeight = element.clientHeight

    var clientLeft = element.clientLeft
    var clientTop = element.clientTop

    var str = `没有scrollbar时` +
        `clientWidth=${clientWidth},` +
        `clientHeight=${clientHeight},` +
        `clientLeft=${clientLeft},` +
        `clientTop=${clientTop},`
    alert(str)

    // 有scrollbar时
    var element = document.getElementById('div12')

    var clientWidth = element.clientWidth
    var clientHeight = element.clientHeight

    var clientLeft = element.clientLeft
    var clientTop = element.clientTop

    var str = `有scrollbar时` +
        `clientWidth=${clientWidth},` +
        `clientHeight=${clientHeight},` +
        `clientLeft=${clientLeft},` +
        `clientTop=${clientTop},`
    alert(str)
}
```



## `scrollWidth`、`scrollHeight`

>详细用法请参考本站 [示例](https://gitee.com/dexterleslie/demonstration/blob/main/front-end/html+js+css/js-clientx-screenx-offsetx-pagex-clientwidth/index.html)

`HTML` 如下：

```html
<hr>
<div>
    演示scrollWidth、scrollHeight<br>
    scrollWidth元素的可滚动区域包括溢出不可见部分
</div>
<div>
    <button onclick="demo2()">点击我</button>
    <div id="div2">
        内容内容内容内容内容内容内容内容内容内容内容内容内容内容内容内容内容内容内容内容内容内容内容内容内容内容内容
    </div>
</div>
```

`CSS` 如下：

```css
#div2 {
    margin: 10px;
    background-color: antiquewhite;
    width: 50px;
    height: 50px;
    border-style: dashed;
    border-width: 20px;
    border-color: gray;
    padding: 10px;
    overflow: scroll;
}
```

`JS` 如下：

```javascript
function demo2() {
    var element = document.getElementById('div2')

    var scrollWidth = element.scrollWidth
    var scrollHeight = element.scrollHeight

    alert(`scrollWidth=${scrollWidth},scrollHeight=${scrollHeight}`)
}
```



## `offsetWidth`、`offsetHeight`

>提醒：包含 `border` 的尺寸。
>
>详细用法请参考本站 [示例](https://gitee.com/dexterleslie/demonstration/blob/main/front-end/html+js+css/js-clientx-screenx-offsetx-pagex-clientwidth/index.html)

`HTML` 如下：

```html
<hr>
<div>
    演示offsetWidth、offsetHeight(包括border的尺寸)<br>
    offsetWidth=width+borderWidth*2*padding*2，例子: 110px=50px+20px*2+10px*2<br>
    https://blog.51cto.com/u_15127622/2757550
</div>
<div>
    <button onclick="demo3()">点击我</button>

    <div id="div3">
        内容内容内容内容内容内容内容内容内容内容内容内容内容内容内容内容内容内容内容内容内容内容内容内容内容内容内容
    </div>
</div>
```

`CSS` 如下：

```css
#div3 {
    margin: 10px;
    background-color: antiquewhite;
    width: 50px;
    height: 50px;
    border-style: dashed;
    border-width: 20px;
    border-color: gray;
    padding: 10px;
    overflow: scroll;
}
```

`JS` 如下：

```javascript
function demo3() {
    var element = document.getElementById('div3')

    var offsetWidth = element.offsetWidth
    var offsetHeight = element.offsetHeight

    alert(`offsetWidth=${offsetWidth},offsetHeight=${offsetHeight}`)
}
```



## `offsetLeft`、`offsetTop`

>详细用法请参考本站 [示例](https://gitee.com/dexterleslie/demonstration/blob/main/front-end/html+js+css/js-clientx-screenx-offsetx-pagex-clientwidth/index.html)

`HTML` 如下：

```html
<hr>
<div>
    演示offsetLeft、offsetTop<br>
    offsetLeft=父元素(距离该子元素最近的进行过定位的父元素（position：absolute relative
    fixed），如果其父元素中不存在定位则offsetParent为：body元素)的padding+元素自己的margin<br>
    https://blog.csdn.net/willard_cui/article/details/81712989
</div>
<div>
    <button onclick="demo5()">点击我</button>

    <div id="div51">
        <div id="div52"></div>
    </div>
</div>
```

`CSS` 如下：

```css
#div51 {
    position: relative;
    margin: 10px;
    background-color: antiquewhite;
    width: 250px;
    height: 250px;
    border-style: dashed;
    border-width: 20px;
    border-color: gray;
    padding: 10px;
}

#div52 {
    margin: 10px;
    background-color: aqua;
    width: 100px;
    height: 100px;
    border-style: dashed;
    border-width: 20px;
    border-color: gray;
    padding: 10px;
}
```

`JS` 如下：

```javascript
function demo5() {
    var element = document.getElementById('div52')

    var offsetLeft = element.offsetLeft
    var offsetTop = element.offsetTop

    alert(`offsetLeft=${offsetLeft},offsetTop=${offsetTop}`)
}
```



## `offsetX`、`offsetY`

>详细用法请参考本站 [示例](https://gitee.com/dexterleslie/demonstration/blob/main/front-end/html+js+css/js-clientx-screenx-offsetx-pagex-clientwidth/index.html)

`HTML` 如下：

```html
<hr>
<div>
    offsetX、offsetY: 鼠标相对于事件源元素的X,Y坐标。比如说，我给黄色的盒子定义一个点击事件，则这个offset的坐标原点就在这个黄色盒子的左上角（不包含 border）<br>
    https://blog.csdn.net/qq_44603011/article/details/117768212
</div>
<div>
    <div id="div6">点击我</div>
</div>
```

`CSS` 如下：

```css
#div6 {
    margin: 10px;
    background-color: aqua;
    width: 50px;
    height: 50px;
    border-style: dashed;
    border-width: 20px;
    border-color: gray;
    padding: 10px;
}
```

`JS` 如下：

```javascript
document.addEventListener('DOMContentLoaded', () => {
    var element = document.getElementById('div6')
    element.onmousedown = (event) => {
        alert(`offsetX=${event.offsetX},offsetY=${event.offsetY}`)
    }
})
```



## `clientX`、`clientY`

>详细用法请参考本站 [示例](https://gitee.com/dexterleslie/demonstration/blob/main/front-end/html+js+css/js-clientx-screenx-offsetx-pagex-clientwidth/index.html)

`HTML` 如下：

```html
<hr>
<div>
    clientX、clientY: 鼠标相对于浏览器窗口可视区域的X，Y坐标（窗口坐标），可视区域不包括工具栏和滚动条。<br>
    https://blog.csdn.net/qq_44603011/article/details/117768212
</div>
<div>
    <div id="div7"></div>
</div>
```

`CSS` 如下：

```css
#div7 {
    margin: 10px;
    background-color: aqua;
    width: 50px;
    height: 50px;
    border-style: dashed;
    border-width: 20px;
    border-color: gray;
    padding: 10px;
}
```

`JS` 如下：

```javascript
document.addEventListener('DOMContentLoaded', () => {
    element = document.getElementById('div7')
    element.onmousedown = (event) => {
        alert(`clientX=${event.clientX},clientY=${event.clientY}`)
    }
})
```



## `getBoundingClientRect`

>详细用法请参考本站 [示例1](https://gitee.com/dexterleslie/demonstration/blob/main/front-end/html+js+css/js-clientx-screenx-offsetx-pagex-clientwidth/index.html)
>
>详细用法请参考本站 [示例2](https://gitee.com/dexterleslie/demonstration/blob/main/front-end/html+js+css/js-clientx-screenx-offsetx-pagex-clientwidth/demo-getboundingclientrect-top-and-left.html)

`HTML` 如下：

```html
<hr>
<div>
    getBoundingClientRect(以viewport的left=0、top=0作为参考的定位)<br>
    https://developer.mozilla.org/en-US/docs/Web/API/Element/getBoundingClientRect
</div>
<div>
    <button onclick="demo8()">点击我</button>
    <div id="div8"></div>
</div>
```

`CSS` 如下：

```css
#div8 {
    margin: 10px;
    background-color: aqua;
    width: 50px;
    height: 50px;
    border-style: dashed;
    border-width: 20px;
    border-color: gray;
    padding: 10px;
}
```

`JS` 如下：

```javascript
function demo8() {
    var element = document.getElementById('div8')

    var boundingClientRect = element.getBoundingClientRect()

    alert(`left=${boundingClientRect.left},top=${boundingClientRect.top},
            right=${boundingClientRect.right},bottom=${boundingClientRect.bottom},
            width=${boundingClientRect.width},height=${boundingClientRect.height},
            x=${boundingClientRect.x},y=${boundingClientRect.y}`)
}
```



## `async`、`await`、`Promise` 关系和用法

在 JavaScript 中，`Promise`、`async` 和 `await` 是处理异步操作的核心工具，三者紧密关联但各有分工。其中，**`Promise` 是异步编程的基础解决方案**，而 **`async/await` 是基于 `Promise` 的语法糖**，旨在以更同步的风格编写异步代码。以下从三者的关系、核心用法到实际场景展开详细说明。


---

### 一、三者的关系
- **`Promise`**：ES6 引入的原生对象，用于表示一个**异步操作的最终完成（或失败）及其结果值**。它通过 `then()`、`catch()`、`finally()` 方法链式处理异步结果，解决了传统回调地狱（Callback Hell）问题。
- **`async`**：ES2017 引入的关键字，用于声明一个**异步函数**。`async` 函数的本质是**返回一个 `Promise` 的普通函数**，其内部可通过 `await` 暂停执行并等待异步结果。
- **`await`**：只能在 `async` 函数内部使用的关键字，用于**等待一个 `Promise` 解决（`resolve`）或拒绝（`reject`）**，并返回其结果（或抛出错误）。


**总结关系**：  
`async` 函数是 `Promise` 的语法糖，通过 `await` 简化了 `Promise` 的链式调用（`.then()`）。三者的核心目标是：用更简洁、线性的代码处理异步操作。


---

### 二、核心用法详解

#### 1. Promise：异步操作的基础
`Promise` 构造函数接收一个**执行器函数**（executor），该函数包含两个参数：`resolve`（标记成功）和 `reject`（标记失败）。`Promise` 有三种状态：
- **pending**（进行中）：初始状态。
- **fulfilled**（已成功）：`resolve` 被调用。
- **rejected**（已失败）：`reject` 被调用（或执行器抛出错误）。

##### 基础用法
```javascript
// 创建一个 Promise（模拟异步请求）
const fetchData = new Promise((resolve, reject) => {
  setTimeout(() => {
    const success = Math.random() > 0.5; // 随机成功或失败
    if (success) {
      resolve({ data: "成功获取的数据" }); // 成功时调用 resolve
    } else {
      reject(new Error("请求失败")); // 失败时调用 reject
    }
  }, 1000);
});

// 使用 then/catch 处理结果
fetchData
  .then((result) => {
    console.log("成功:", result.data); // 输出：成功: 成功获取的数据
  })
  .catch((error) => {
    console.error("失败:", error.message); // 若失败则输出：失败: 请求失败
  })
  .finally(() => {
    console.log("无论成功或失败都会执行"); // 最终执行
  });
```

##### 链式调用（解决回调地狱）
传统回调地狱因多层嵌套难以维护，而 `Promise` 支持链式调用，通过返回新的 `Promise` 实现线性结构：
```javascript
// 链式调用示例（获取用户 → 获取订单 → 获取详情）
getUser()
  .then((user) => {
    return getOrders(user.id); // 返回新的 Promise
  })
  .then((orders) => {
    return getOrderDetails(orders[0].id); // 继续返回 Promise
  })
  .then((details) => {
    console.log("最终详情:", details);
  })
  .catch((error) => {
    console.error("任意一步失败:", error);
  });
```


#### 2. async：声明异步函数
`async` 函数是用 `async` 关键字声明的函数，**始终返回一个 `Promise`**。其内部通过 `await` 等待异步操作完成，语法更接近同步代码。

##### 基础结构
```javascript
// 声明 async 函数（函数声明/表达式均可）
async function fetchDataAsync() {
  // 模拟异步操作（如 API 请求）
  const result = await new Promise((resolve) => {
    setTimeout(() => resolve("数据"), 1000);
  });
  return result; // 结果会被包装为 Promise.resolve(result)
}

// 调用 async 函数（返回 Promise）
fetchDataAsync().then((data) => {
  console.log(data); // 输出：数据（1秒后）
});
```

##### 关键特性
- **返回值**：`async` 函数的返回值会被自动包装为 `Promise`。即使返回普通值（非 `Promise`），也会被转为 `resolve(值)`。
  ```javascript
  async function demo() {
    return "hello"; // 等价于 return Promise.resolve("hello")
  }
  demo().then((res) => console.log(res)); // 输出：hello
  ```
- **错误处理**：`async` 函数内部抛出的错误会被包装为 `Promise.reject(错误)`，可通过 `try/catch` 捕获。
  ```javascript
  async function errorDemo() {
    throw new Error("出错了"); // 等价于 return Promise.reject(new Error("出错了"))
  }
  errorDemo().catch((err) => console.error(err.message)); // 输出：出错了
  ```


#### 3. await：等待 Promise 解决
`await` 只能在 `async` 函数内部使用，用于**暂停函数执行，等待右侧的 `Promise` 解决或拒绝**，并返回其结果（或抛出错误）。

##### 基础用法
```javascript
async function fetchUserAndOrder() {
  try {
    // 等待 getUser() 返回的 Promise 解决
    const user = await getUser(); 
    
    // 依赖 user.id，继续等待 getOrders() 返回的 Promise 解决
    const orders = await getOrders(user.id); 
    
    console.log("用户订单:", orders);
    return orders;
  } catch (error) {
    console.error("流程失败:", error);
  }
}
```

##### 核心场景
###### （1）串行执行（依赖前序结果）
若多个异步操作**依赖前一个操作的结果**，需用 `await` 逐个等待（串行执行）：
```javascript
async function sequentialTasks() {
  const a = await taskA(); // 等待 taskA 完成（耗时 1s）
  const b = await taskB(a); // 等待 taskB 完成（依赖 a，耗时 1s）→ 总耗时 2s
  const c = await taskC(b); // 总耗时 3s
  return c;
}
```

###### （2）并行执行（无依赖关系）
若多个异步操作**互不依赖**，可用 `Promise.all()` 并行执行（提升性能）：
```javascript
async function parallelTasks() {
  // 同时启动 taskA 和 taskB（不等待彼此）
  const promiseA = taskA(); // 耗时 1s
  const promiseB = taskB(); // 耗时 1s
  
  // 等待两者都完成（总耗时 1s）
  const [a, b] = await Promise.all([promiseA, promiseB]);
  
  return { a, b };
}
```

###### （3）混合场景（部分并行）
若部分操作依赖前序结果，部分不依赖，可混合使用 `await` 和 `Promise.all()`：
```javascript
async function mixedTasks() {
  const user = await getUser(); // 必须先获取用户（耗时 1s）
  
  // 并行获取用户的帖子和评论（依赖 user.id）
  const [posts, comments] = await Promise.all([
    getPosts(user.id), // 耗时 1s
    getComments(user.id) // 耗时 1s
  ]); // 总耗时 1s（与前序的 1s 串行，整体 2s）
  
  return { user, posts, comments };
}
```


---

### 三、三者的协同工作流程
以“获取用户信息 → 获取用户订单 → 显示结果”为例，三者如何协作：

```javascript
// 1. 定义返回 Promise 的工具函数（模拟异步 API）
function getUser() {
  return new Promise((resolve) => {
    setTimeout(() => resolve({ id: 1, name: "张三" }), 1000);
  });
}

function getOrders(userId) {
  return new Promise((resolve) => {
    setTimeout(() => resolve([{ id: 101, product: "书" }]), 1000);
  });
}

// 2. 用 async/await 编写异步流程
async function main() {
  try {
    // 等待 getUser 完成（1s）
    const user = await getUser();
    
    // 等待 getOrders 完成（依赖 user.id，1s）
    const orders = await getOrders(user.id);
    
    // 显示结果（最终执行）
    console.log(`用户 ${user.name} 的订单：`, orders);
  } catch (error) {
    console.error("流程出错:", error);
  }
}

// 3. 调用 async 函数（返回 Promise）
main();
```

**执行流程**：  
- `main()` 被调用，返回一个 `Promise`。  
- `await getUser()` 暂停 `main` 执行，等待 `getUser` 的 `Promise` 解决（1秒后）。  
- `user` 获取成功后，继续执行 `await getOrders(user.id)`，等待 `getOrders` 的 `Promise` 解决（又1秒）。  
- 最终打印结果，`main` 返回的 `Promise` 被解决。  


---

### 四、常见误区与注意事项

#### 1. `await` 只能在 `async` 函数内使用
在非 `async` 函数中使用 `await` 会直接报语法错误：
```javascript
// 错误示例
function normalFunc() {
  await fetchData(); // 报错：SyntaxError: await is only valid in async functions
}
```

#### 2. `await` 不会阻塞整个事件循环
`await` 仅暂停当前 `async` 函数的执行，但 JavaScript 事件循环仍会处理其他任务（如其他异步操作、UI 渲染）：
```javascript
async function logWithDelay() {
  console.log("开始");
  await new Promise(resolve => setTimeout(resolve, 1000)); // 暂停 1 秒
  console.log("1秒后"); // 1秒后执行
}

console.log("调用前");
logWithDelay();
console.log("调用后");

// 输出顺序：
// 调用前 → 调用后 → （等待 1秒）→ 1秒后
```

#### 3. 避免不必要的 `await`
如果不需要等待异步操作的结果，无需使用 `await`，否则会无意义地阻塞函数执行：
```javascript
// 错误：不必要的 await（阻塞函数）
async function unnecessaryAwait() {
  await fetchData(); // 等待 fetchData 完成，但结果未使用
  doSomethingElse(); // 必须等 fetchData 完成后才执行
}

// 正确：仅在需要结果时使用 await
async function correctUsage() {
  doSomethingElse(); // 先执行其他操作
  const data = await fetchData(); // 需要数据时再等待
  processData(data);
}
```

#### 4. 循环中的 `await` 需谨慎
在 `for...of`、`while` 等循环中使用 `await` 时，每次迭代会等待前一次的异步操作完成（串行执行）。若需并行，需提前收集所有 `Promise`：
```javascript
// 串行执行（总耗时 = 每次耗时之和）
async function processArraySerial(array) {
  for (const item of array) {
    await processItem(item); // 每次等待前一次完成
  }
}

// 并行执行（总耗时 = 最长单次耗时）
async function processArrayParallel(array) {
  const promises = array.map(item => processItem(item)); // 收集所有 Promise
  await Promise.all(promises); // 并行执行
}
```


---

### 五、总结
- **`Promise`**：异步操作的基础，通过 `then/catch` 处理结果，解决回调地狱。  
- **`async`**：声明返回 `Promise` 的函数，内部用 `await` 简化异步逻辑。  
- **`await`**：仅在 `async` 函数内使用，等待 `Promise` 解决并返回结果（或抛出错误）。  

三者的核心价值是：**用同步风格的代码处理异步操作**，提升可读性和可维护性。实际开发中，`async/await` 结合 `Promise.all()` 是处理复杂异步流程的首选方案。
