## 什么是less、sass、postcss

参考

https://segmentfault.com/a/1190000021138000?u_atoken=fe35c5ac-1859-4cbe-82de-ef0feb3a8673&u_asig=LZzhdTVOqM&u_aref=mPzK4cBgG5I2Xqqq%2FlRynWG7mxc%3D

**Less** 2009年开源的一个项目，受Sass的影响较大，但又使用CSS的语法，让大部分开发者和设计师更容易上手。LESS提供了多种方式能平滑的将写好的代码转化成标准的CSS代码。使用JavaScript编译器进行编译Less扩展了 CSS 语言，增加了变量、Mixin、函数等特性，使 CSS 更易维护和扩展。Less 可以运行在 Node 或浏览器端。less-loader:  将Less编译成 CSS。

**Sass** 是一种动态样式语言，Sass语法属于缩排语法，比css比多出好些功能(如变量、嵌套、运算,混入(Mixin)、继承、颜色处理，函数等)，更容易阅读。sass-loader:  加载SASS / SCSS文件并将其编译为CSS。

**Scss** Sass的缩排语法，对于写惯css前端的web开发者来说很不直观，也不能将css代码加入到Sass里面，因此sass语法进行了改良，Sass3就变成了Scss(sassy css)。与原来的语法兼容，只是用{}取代了原来的缩进。

**PostCSS** 主要功能只有两个：第一个就是前面提到的把 CSS 解析成 JavaScript 可以操作的 抽象语法树结构（Abstract Syntax Tree，AST）。第二个就是**调用插件**来处理 AST 并得到结果。PostCSS 一般不单独使用， **而是与已有的构建工具进行集成**。PostCSS 与主流的构建工具，如 Webpack完成集成之后，选择满足功能需求的 PostCSS 插件并进行配置。postcss-loader:  用PostCSS处理CSS。



## less

### 在浏览器开发环境中引入less

参考 demo-less

### less 变量用法

参考 demo-less

### less 引用父选择器

参考 demo-less



## sass/scss

### 在浏览器开发环境中引入scss

需要先把scss文件编译为css文件，然后在html中通过<link rel="stylesheet" href="index.css">引用编译后的css文件。

ubuntu安装sass命令

```sh
sudo npm install -g sass
```

查看是否成功安装sass命令

```sh
sass --version
```

scss源文件被改动会自动编译为css文件

```sh
sass --watch index.scss index.css
```

参考

https://sass-lang.com/install/
https://www.educative.io/answers/how-to-use-sass-in-your-html-code

### scss变量声明和使用

参考 demo-scss

### 嵌套样式(父子样式)

参考 demo-scss