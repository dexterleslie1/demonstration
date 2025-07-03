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

