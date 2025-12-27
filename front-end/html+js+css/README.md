## 响应式web设计

> https://www.w3schools.com/html/html_responsive.asp



### 设置viewport meta

> 参考 demo-responsive-design/demo-setting-the-viewport



### 响应式图片

> 参考 demo-responsive-design/demo-responsive-image



### 响应式文本

> 参考 demo-responsive-design/demo-responsive-text



### 根据浏览器宽度显示不同的图片

> 参考 demo-responsive-design/demo-show-different-image-depending-on-browser-width



### 自适应和响应式区别

> https://answer.baidu.com/answer/land?params=2pYqV2dFvx1ITq44PzikNEKfsp%2B5%2BHhj%2FesMN9eVgdr5pUNi0t1d87a4dqi4uJGdIGQMev5escaVyMS3IiMTD%2Biv347YglgAXMZx5kK3fb%2FKTvd3r%2BvHBcIgXHL3TJt7qwipUX6VzeB8RMsGVTssnMG0jisEf%2BYsEP1Zo3Kh83%2BgCwExVW86e6BL45uww4v9&from=dqa&lid=c309cf4e00036f62&word=%E8%87%AA%E9%80%82%E5%BA%94%E5%92%8C%E5%93%8D%E5%BA%94%E5%BC%8F%E5%8C%BA%E5%88%AB
>
> 自适应设计通常涉及为不同设备的不同屏幕尺寸设计多个不同的布局。这包括手机端、平板端和电脑端的布局。
>
> 响应式设计则是创建一套页面，并通过媒体查询（media queries）和可能的JavaScript和CSS控制，根据用户的设备和屏幕尺寸动态地调整布局和内容。



## `localStorage` 和 `sessionStorage`



### 区别

- 在使用 `sessionStorage` 方法时，如果关闭了浏览器，这个数据就丢失了，下一次打开浏览器单击"读取数据"按钮时，读取不到任何数据。在使用`localStorage` 方法时，即使浏览器关闭了，下次打开浏览器时仍然能够读取保存的数据。不过，数据保存是按不同的浏览器分别进行保存的，也就是说，打开别的浏览器是读取不到在这个浏览器中保存的数据的。
- 同一个浏览器中，`sessionStorage` 在不同的 `TAB` 页面保存数据是分开互相不影响的，`localStorage` 在不同 `TAB` 页面保存数据时共享的相互影响的。



### 用法

>[`sessionStorage`、`localStorage` 使用](https://www.cnblogs.com/pengc/p/8714475.html)
>
>详细用法请参考本站 [示例](https://gitee.com/dexterleslie/demonstration/tree/main/front-end/html+js+css/demo-h5-storage)

```javascript
window.sessionStorageOnSave = function() {
    let value = document.getElementById("sessionStorageValue").value;
    sessionStorage.setItem("sessionStorageKey", value);
}

window.sessionStorageOnRead = function() {
    let value = sessionStorage.getItem("sessionStorageKey");
    alert("sessionStorage值为：" + value);
}

window.localStorageOnSave = function() {
    let value = document.getElementById("localStorageValue").value;
    localStorage.setItem("localStorageKey", value);
}

window.localStorageOnRead = function() {
    let value = localStorage.getItem("localStorageKey");
    alert("localStorage值为：" + value);
}
```

## 自定义HTML元素

>参考链接：https://developer.mozilla.org/zh-CN/docs/Web/API/Web_components/Using_custom_elements
>
>详细用法请参考本站示例：https://gitee.com/dexterleslie/demonstration/tree/main/front-end/html+js+css/demo-自定义html元素

```html
<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>自定义HTML元素演示</title>
    <style>
        body {
            font-family: Arial, sans-serif;
            max-width: 800px;
            margin: 0 auto;
            padding: 20px;
        }
        .container {
            margin-bottom: 40px;
            padding: 20px;
            border: 1px solid #ddd;
            border-radius: 8px;
        }
        popup-info {
            display: block;
            margin: 10px 0;
        }
    </style>
</head>
<body>
    <h1>自定义HTML元素演示</h1>
    
    <div class="container">
        <h2>1. 独立自定义元素 (popup-info)</h2>
        <p>这是一个独立的自定义元素，继承自 HTMLElement：</p>
        <popup-info img="https://via.placeholder.com/20" text="这是一条重要信息！"></popup-info>
        <popup-info img="https://via.placeholder.com/20" text="自定义元素可以轻松扩展HTML功能。"></popup-info>
    </div>
    
    <div class="container">
        <h2>2. 自定义内置元素 (word-count)</h2>
        <p>这是一个自定义内置元素，继承自 HTMLParagraphElement：</p>
        <p is="word-count">这是一个包含多个单词的段落，用于演示自定义内置元素的功能。</p>
        <p is="word-count">自定义内置元素允许我们扩展现有的HTML元素。</p>
    </div>

    <script>
        // 1. 独立自定义元素: popup-info
        class PopupInfo extends HTMLElement {
            constructor() {
                super();
                
                // 创建影子根
                const shadow = this.attachShadow({ mode: 'open' });
                
                // 创建结构
                const wrapper = document.createElement('span');
                wrapper.setAttribute('class', 'wrapper');
                
                const icon = document.createElement('span');
                icon.setAttribute('class', 'icon');
                icon.setAttribute('tabindex', 0);
                
                const img = document.createElement('img');
                img.src = this.getAttribute('img');
                img.alt = '信息图标';
                
                const info = document.createElement('span');
                info.setAttribute('class', 'info');
                
                // 获取并设置text属性
                const text = this.getAttribute('text');
                info.textContent = text;
                
                // 创建样式
                const style = document.createElement('style');
                style.textContent = `
                    .wrapper {
                        position: relative;
                        display: inline-block;
                    }
                    .icon {
                        cursor: pointer;
                    }
                    .icon img {
                        width: 20px;
                        height: 20px;
                    }
                    .info {
                        position: absolute;
                        bottom: 125%;
                        left: 50%;
                        transform: translateX(-50%);
                        background-color: #333;
                        color: white;
                        padding: 8px 12px;
                        border-radius: 4px;
                        font-size: 14px;
                        white-space: nowrap;
                        opacity: 0;
                        visibility: hidden;
                        transition: opacity 0.3s, visibility 0.3s;
                    }
                    .info::after {
                        content: '';
                        position: absolute;
                        top: 100%;
                        left: 50%;
                        transform: translateX(-50%);
                        border-width: 5px;
                        border-style: solid;
                        border-color: #333 transparent transparent transparent;
                    }
                    .icon:hover + .info,
                    .icon:focus + .info {
                        opacity: 1;
                        visibility: visible;
                    }
                `;
                
                // 组装
                icon.appendChild(img);
                wrapper.appendChild(icon);
                wrapper.appendChild(info);
                shadow.appendChild(style);
                shadow.appendChild(wrapper);
            }
        }
        
        // 注册独立自定义元素
        customElements.define('popup-info', PopupInfo);
        
        // 2. 自定义内置元素: word-count
        class WordCount extends HTMLParagraphElement {
            constructor() {
                super();
            }
            
            connectedCallback() {
                this.updateWordCount();
            }
            
            updateWordCount() {
                const text = this.textContent;
                // 计算单词数量的核心逻辑
                // 1. text.trim() - 移除文本开头和结尾的空白字符
                // 2. split(/\s+/) - 使用正则表达式按一个或多个空白字符（空格、制表符、换行符等）分割文本
                // 3. filter(word => word.length > 0) - 过滤掉分割后产生的空字符串
                // 最终得到一个包含所有实际单词的数组
                const words = text.trim().split(/\s+/).filter(word => word.length > 0);
                const count = words.length;
                
                // 使用dataset API设置元素的data-*属性
                // 1. dataset是HTML5提供的API，用于访问和操作元素的data-*属性
                // 2. wordCount是驼峰命名，会自动转换为连字符命名的data-word-count
                // 3. 这样可以将计算出的单词数量存储到元素的自定义数据属性中
                // 4. 便于在CSS或JavaScript中通过[data-word-count]选择器或element.dataset.wordCount访问
                this.dataset.wordCount = count;
                
                // 如果还没有计数元素，则创建
                if (!this.querySelector('.word-count')) {
                    const countSpan = document.createElement('span');
                    countSpan.className = 'word-count';
                    countSpan.style.color = '#666';
                    countSpan.style.fontSize = '0.8em';
                    countSpan.style.marginLeft = '10px';
                    countSpan.textContent = `(${count} 词)`;
                    this.appendChild(countSpan);
                } else {
                    // 更新现有计数
                    this.querySelector('.word-count').textContent = `(${count} 词)`;
                }
            }
        }
        
        // 注册自定义内置元素
        // customElements.define() 方法用于向浏览器注册自定义元素
        // 第一个参数 'word-count'：自定义元素的名称，必须包含连字符
        // 第二个参数 WordCount：自定义元素的类定义，继承自 HTMLParagraphElement
        // 第三个参数 { extends: 'p' }：配置对象，指定该自定义元素扩展自原生的 p 元素
        // 这种注册方式创建的是自定义内置元素，可以通过 <p is="word-count"> 的方式使用
        customElements.define('word-count', WordCount, { extends: 'p' });
    </script>
</body>
</html>
```

## 什么是`less`、`sass/scss`、`postcss`

> 说明：**现代开发建议**：使用 **SCSS + PostCSS** 组合，既享受预处理器的强大功能，又获得PostCSS的优化优势。
>
> 关于`less`、`sass/scss`、`postcss`解释参考 [链接](https://segmentfault.com/a/1190000021138000?u_atoken=fe35c5ac-1859-4cbe-82de-ef0feb3a8673&u_asig=LZzhdTVOqM&u_aref=mPzK4cBgG5I2Xqqq%2FlRynWG7mxc%3D)

**Less** 2009年开源的一个项目，受Sass的影响较大，但又使用CSS的语法，让大部分开发者和设计师更容易上手。LESS提供了多种方式能平滑的将写好的代码转化成标准的CSS代码。使用JavaScript编译器进行编译Less扩展了 CSS 语言，增加了变量、Mixin、函数等特性，使 CSS 更易维护和扩展。Less 可以运行在 Node 或浏览器端。less-loader:  将Less编译成 CSS。

**Sass** 是一种动态样式语言，Sass语法属于缩排语法，比css比多出好些功能(如变量、嵌套、运算,混入(Mixin)、继承、颜色处理，函数等)，更容易阅读。sass-loader:  加载SASS / SCSS文件并将其编译为CSS。

**Scss** Sass的缩排语法，对于写惯css前端的web开发者来说很不直观，也不能将css代码加入到Sass里面，因此sass语法进行了改良，Sass3就变成了Scss(sassy css)。与原来的语法兼容，只是用{}取代了原来的缩进。

**PostCSS** 主要功能只有两个：第一个就是前面提到的把 CSS 解析成 JavaScript 可以操作的 抽象语法树结构（Abstract Syntax Tree，AST）。第二个就是**调用插件**来处理 AST 并得到结果。PostCSS 一般不单独使用， **而是与已有的构建工具进行集成**。PostCSS 与主流的构建工具，如 Webpack完成集成之后，选择满足功能需求的 PostCSS 插件并进行配置。postcss-loader:  用PostCSS处理CSS。

## css、less、sass、scss、postcss的关系

>说明：**现代开发建议**：使用 **SCSS + PostCSS** 组合，既享受预处理器的强大功能，又获得PostCSS的优化优势。

我来帮你理清这些工具之间的关系，这是一个常见的前端样式技术栈问题：

### 核心关系图

```
原始CSS → 预处理器(SASS/LESS) → 现代工具链(PostCSS) → 最终CSS
```

### 详细解释

#### 1. **CSS (Cascading Style Sheets)**

- 

  **基础**：所有样式技术的最终输出格式

- 

  **作用**：浏览器直接理解的样式语言

- 

  **示例**：`color: red; font-size: 16px;`

#### 2. **预处理器 (SASS/SCSS 和 LESS)**

**共同特点**：

- 

  在CSS基础上添加编程特性（变量、嵌套、混合、函数等）

- 

  需要编译成纯CSS才能在浏览器中使用

- 

  提高CSS的可维护性和开发效率

##### **SASS 和 SCSS 的关系**：

```
SASS家族：
├── SASS (缩进语法，.sass扩展名)
│   └── 不使用大括号和分号，依赖缩进
│   └── 示例：body
│           color: red
│
└── SCSS (Sassy CSS，.scss扩展名)
    └── CSS的超集，完全兼容CSS语法
    └── 示例：body { color: red; }
```

- 

  **SCSS是SASS的主要语法**，现在更常用

- 

  两者功能相同，只是语法不同

##### **LESS**：

- 

  另一个流行的预处理器

- 

  语法与SCSS相似，但实现细节和功能略有不同

- 

  通常与Bootstrap一起使用

#### 3. **PostCSS**

**重要区别**：

- 

  **不是预处理器**，而是一个CSS处理工具

- 

  可以看作"CSS的Babel"

- 

  通过插件系统转换CSS

- 

  可以处理预处理器编译后的CSS

**常见插件**：

- 

  `autoprefixer`：自动添加浏览器前缀

- 

  `cssnano`：CSS压缩

- 

  `postcss-preset-env`：使用未来的CSS特性

### 工作流程示例

#### 传统流程（使用预处理器）：

```
SCSS文件 → SASS编译器 → CSS文件 → 浏览器
```

#### 现代流程（结合使用）：

```
SCSS文件 
    → SASS编译（变量、嵌套等） 
    → 纯CSS 
    → PostCSS处理（加前缀、压缩等） 
    → 最终CSS 
    → 浏览器
```

### 技术栈对比

| 技术      | 类型     | 主要特点                      | 何时使用             |
| --------- | -------- | ----------------------------- | -------------------- |
| CSS       | 基础语言 | 浏览器原生支持                | 总是需要（最终输出） |
| SASS/SCSS | 预处理器 | 变量、嵌套、混合、继承        | 需要高级CSS功能时    |
| LESS      | 预处理器 | 类似SASS，与Bootstrap集成好   | 使用Bootstrap的项目  |
| PostCSS   | 处理工具 | 插件化，自动前缀，CSS未来特性 | 现代工作流必备       |

### 实际项目中的典型配置

```
// package.json 片段
{
  "devDependencies": {
    "sass": "^1.50.0",        // SASS编译器
    "postcss": "^8.4.14",     // PostCSS核心
    "autoprefixer": "^10.4.4", // PostCSS插件
    "cssnano": "^5.1.0"       // PostCSS插件（压缩）
  }
}
// 构建配置示例
module.exports = {
  module: {
    rules: [
      {
        test: /\.scss$/,
        use: [
          'style-loader',
          'css-loader',
          'postcss-loader',  // 处理autoprefixer等
          'sass-loader'       // 编译SASS/SCSS
        ]
      }
    ]
  }
};
```

### 简单总结

1. 

   **CSS是目标**：所有工具最终都产出CSS

2. 

   **SASS/LESS是"增强型CSS"**：提供编程特性，需要编译

3. 

   **SCSS是SASS的现代语法**：现在更常用

4. 

   **PostCSS是"CSS处理流水线"**：处理编译后的CSS，做优化和兼容

**现代开发建议**：使用 **SCSS + PostCSS** 组合，既享受预处理器的强大功能，又获得PostCSS的优化优势。

## less - 浏览器开发环境中配置

> 参考以下链接在浏览器中引用`less` [链接](https://lesscss.org/usage/#using-less-in-the-browser)
>
> 下面例子的具体源码参考 [链接](https://github.com/dexterleslie1/demonstration/tree/master/front-end/html%2Bjs%2Bcss/demo-less-sass-scss-postcss/demo-less)

下载离线的`less.js`文件（用于解析`less`样式文件），[链接](https://github.com/less/less.js/archive/master.zip)

`index.html`引用并配置`less`

```html
<script>
    /* 设置less */
    less = {
    	env: "development"
    };
</script>
<script src="less.js" type="text/javascript" data-env="development"></script>
```

自定义并引用`less`样式，`style.less`样式文件内容如下：

```less
// 定义变量
@link-color: red;
@link-color-hover: darken(@link-color, 10%);

// 使用变量
.demo1 a {
    color: @link-color;
}
```

在`<script ...></script>`之前引用自定义`less`样式文件

```html
<link rel="stylesheet/less" type="text/css" href="styles.less" />
```

`index.html`内容如下：

```html
<!DOCTYPE html>
<html lang="en">

<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Document</title>
    <link rel="stylesheet/less" type="text/css" href="styles.less" />
    <script>
        /* 设置less */
        less = {
            env: "development"
        };
    </script>
    <script src="less.js" type="text/javascript" data-env="development"></script>
</head>

<body>
    <!--
        参考 https://lesscss.org/features/#variables-feature
    -->
    <div>变量使用</div>
    <div class="demo1">
        <a href="#">链接</a>
    </div>
</body>

</html>
```

## less变量 - 基本使用

>变量的使用参考[链接](https://lesscss.org/features/#variables-feature)

`html`如下：

```html
<div class="demo1">
    <a href="#">链接</a>
</div>
```

`less`样式中使用变量

```less
// 定义变量
@link-color: red;
@link-color-hover: darken(@link-color, 10%);

// 使用变量
.demo1 a {
    color: @link-color;
}
```

## less变量 - 在样式选择器中使用

`html`如下：

```html
<div class="demo2">
    <div></div>
</div>
```

`less`样式中使用变量

```less
@my-selector: demo2 div;

// 在选择器中使用变量
.@{my-selector} {
    width: 50px;
    height: 25px;
    background-color: green;
}
```

## scss - 浏览器开发环境中配置

> `html`中使用`scss`参考[链接](https://www.educative.io/answers/how-to-use-sass-in-your-html-code)
>
> 详细用法请参考本站示例：https://github.com/dexterleslie1/demonstration/tree/master/front-end/html%2Bjs%2Bcss/demo-less-sass-scss-postcss/demo-scss

`ubuntu`安装`sass`命令，参考[链接](https://sass-lang.com/install/)

```bash
sudo npm install -g sass
```

查看是否成功安装`sass`命令

```bash
sass --version
```

`scss`源文件被改动会自动编译为`css`文件

```sh
sass --watch index.scss index.css
```

在`html`中通过`<link rel="stylesheet" href="index.css">`引用编译后的`css`文件。

## scss - 变量的使用

> 变量的使用参考[链接](https://sass-lang.com/guide/#variables)
>
> 例子具体代码参考[链接](https://github.com/dexterleslie1/demonstration/blob/master/front-end/html%2Bjs%2Bcss/demo-less-sass-scss-postcss/demo-scss/index.html)

`html`代码如下：

```html
<div class="demo1">
	<div></div>
</div>
```

`scss`代码如下：

```scss
$demo1-background-color: green;

.demo1 div {
    width: 50px;
    height: 25px;
    background-color: $demo1-background-color;
}
```

## scss - 嵌套样式（父子样式）

> 例子具体代码参考[链接](https://github.com/dexterleslie1/demonstration/blob/master/front-end/html%2Bjs%2Bcss/demo-less-sass-scss-postcss/demo-scss/index.html)

`html`代码如下：

```html
<div class="demo2">
    <div class="parent">
        <div class="child"></div>
    </div>
</div>
```

`scss`代码如下：

```scss
.demo2 {
    .parent {
        width: 50px;
        height: 50px;
        background-color: green;

        .child {
            width: 25px;
            height: 25px;
            background-color: yellow;
        }
    }
}
```
