## commonJS说明

> require遵循CommonJS/AMD规范
>
> require() 源码解读 https://www.ruanyifeng.com/blog/2015/05/require.html

### commonJS概述

node 应用由模块组成，采用 CommonJS 模块规范

CommonJS规范规定，每个模块内部，module变量代表当前模块。这个变量是一个对象，它的exports属性（即module.exports）是对外的接口。加载某个模块，其实是加载该模块的module.exports属性。

require方法用于加载模块。

CommonJS模块的特点如下。

* 所有代码都运行在模块作用域，不会污染全局作用域。
* 模块可以多次加载，但是只会在第一次加载时运行一次，然后运行结果就被缓存了，以后再加载，就直接读取缓存结果。要想让模块再次运行，必须清除缓存。
* 模块加载的顺序，按照其在代码中出现的顺序。

### require加载原理

当 Node 遇到 require(X) 时，按下面的顺序处理。

（1）如果 X 是内置模块（比如 require('http'）)
　　a. 返回该模块。
　　b. 不再继续执行。

（2）如果 X 以 "./" 或者 "/" 或者 "../" 开头
　　a. 根据 X 所在的父模块，确定 X 的绝对路径。
　　b. 将 X 当成文件，依次查找下面文件，只要其中有一个存在，就返回该文件，不再继续执行。

* X
* X.js
* X.json
* X.node

　　c. 将 X 当成目录，依次查找下面文件，只要其中有一个存在，就返回该文件，不再继续执行。

* X/package.json（main字段）
* X/index.js
* X/index.json
* X/index.node

（3）如果 X 不带路径
　　a. 根据 X 所在的父模块，确定 X 可能的安装目录。
　　b. 依次在每个目录中，将 X 当成文件名或目录名加载。

（4） 抛出 "not found"

请看一个例子。

当前脚本文件 /home/ry/projects/foo.js 执行了 require('bar') ，这属于上面的第三种情况。Node 内部运行过程如下。

首先，确定 x 的绝对路径可能是下面这些位置，依次搜索每一个目录。

* /home/ry/projects/node_modules/bar
* /home/ry/node_modules/bar
* /home/node_modules/bar
* /node_modules/bar

搜索时，Node 先将 bar 当成文件名，依次尝试加载下面这些文件，只要有一个成功就返回。

* bar
* bar.js
* bar.json
* bar.node

如果都不成功，说明 bar 可能是目录名，于是依次尝试加载下面这些文件。

* bar/package.json（main字段）
* bar/index.js
* bar/index.json
* bar/index.node

如果在所有目录中，都无法找到 bar 对应的文件或目录，就抛出一个错误。

### require源码

require命令的基本功能是，读入并执行一个JavaScript文件，然后返回该模块的exports对象。如果没有发现指定模块，会报错。

require是Module.prototype的一个函数，返回值为module.exports变量，源代码如下:

```
Module.prototype.require = function(path) {
  return Module._load(path, this);
};
```

Module._load = function(request, parent, isMain) {

//  计算绝对路径

var filename = Module._resolveFilename(request, parent);

//  第一步：如果有缓存，取出缓存

var cachedModule = Module._cache[filename];

if (cachedModule) {

```
return cachedModule.exports;
```

// 第二步：是否为内置模块

if (NativeModule.exists(filename)) {

```
return NativeModule.require(filename);
```

}

// 第三步：生成模块实例，存入缓存

var module = new Module(filename, parent);

Module._cache[filename] = module;

// 第四步：加载模块

try {

```
module.load(filename);

hadException = false;
```

} finally {

```
if (hadException) {

  delete Module._cache[filename];

}
```

}

// 第五步：输出模块的exports属性

return module.exports;

};

## 演示目的

> 1、演示commonJS module.exports和require用法
>
> 2、使用module.exports和require模拟实现第三方npm包开发到发布过程

## 目录结构说明

> my-package: 第三方npm包源代码
>
> my-test: 测试调用第三方包项目

## 参考资料

> https://docs.npmjs.com/creating-node-js-modules

## 运行调试

```shell
# 在my-test项目中安装my-package模块
npm install ../my-package

# 运行my-test测试
node index.js
```
