## 原理

> [node.js 原理简介](https://www.cnblogs.com/bingooo/p/6720540.html)

`NodeJS` 是基于 `V8` 的 `JavaScript` 运行时，事件驱动、非阻塞，因此轻量、高效。



## 安装

>提醒：
>
>- `npm` 是和 `NodeJS`一起并存的，只要安装了`NodeJS`，`npm` 也安装好了，安装好 `NodeJS` 之后。打开终端，执行如下命令，检查是否安装成功。
>- 尝试过在 `Ubuntu` 上安装 `nvm` 来管理多版本 `NodeJS`，但是因为 `nvm` 是通过 `Shell` 脚本在线访问 `raw.githubusercontent.com` 安装，其中`raw.githubusercontent.com` 国内无法访问，所以放弃此方案。



### `macOS`

到 `nodejs` 官网 `https://nodejs.org/en/download` 下载 `pkg` 安装包，双击 `pkg` 安装包根据提示安装

查看 `node` 版本

```sh
node -v
```



### `Windows`

到 `nodejs` 官网 https://nodejs.org/en/download 下载 `msi` 安装包，双击 `msi` 安装包根据提示安装

查看 `node` 版本

```sh
node -v
```



### `Ubuntu`

使用 `dcli` 安装。



## `npm` 和 `yarn`

### 什么是 `npm`

>[官网](https://www.npmjs.com/)

`npm` 全称 `node package manager`，即 `node` 包管理工具。是 `nodejs` 默认的、以 `javascript` 编写的软件包管理系统。

使用 `npm` 命令能够把远程仓库的第三方 `javascript` 库下载到本地。

安装 `nodejs` 后会默认已经安装好 `npm`（`npm` 是 `nodejs` 下的一个小工具）



### 配置 `npm`、`yarn` 源

> [`npm`、`yarn` 换源与 `nrm`](https://juejin.cn/post/6844904165466980359)



#### `npmmirror` 镜像站

`https://registry.npmmirror.com/ 是一个完整 [`npmjs.com`](https://www.npmjs.com) 镜像，你可以用此代替官方版本(只读)，我们将尽量与官方服务**实时同步**。

通过下面命令设置 `REGISTRY`

```sh
npm config set registry https://registry.npmmirror.com
```



#### 通过项目 `.npmrc` 配置 `npm/yarn` 源

创建项目目录 `demo-npmrc`

```sh
mkdir demo-npmrc
```

初始化项目，所有选项选择默认值直接 `enter` 即可

```sh
cd demo-npmrc
npm init
```

添加 `jquery` 依赖到项目中

```sh
npm install --save-dev jquery
```

添加 `.npmrc` 内容如下

```properties
electron_mirror=https://npmmirror.com/mirrors/electron/
electron-builder-binaries_mirror=https://npmmirror.com/mirrors/electron-builder-binaries/
registry=https://mirrors.cloud.tencent.com/npm/
```

`npm install` 时通过 `--verbose` 参数查看依赖下载所使用的源

```sh
npm install --verbose
```



**使用npm相关命令配置npm和yarn源**

```shell
# 删除npm源可以通过删除~/.npmrc文件

# 删除yarn源可以通过删除~/.yarnrc文件

# 通过直接修改~/.npmrc文件配置npm源，内容如下
registry=https://mirrors.cloud.tencent.com/npm/
home=https://mirrors.cloud.tencent.com/npm/

# 通过直接修改~/.yarnrc文件配置yarn源，内容如下
# NOTE: todo 似乎淘宝源解析到美国的npm源导致下载速度很慢
registry "https://registry.npm.taobao.org"
chromedriver_cdnurl "https://cdn.npm.taobao.org/dist/chromedriver"
electron_mirror "https://npm.taobao.org/mirrors/electron/"
lastUpdateCheck 1671694163476
phantomjs_cdnurl "http://cnpmjs.org/downloads"
profiler_binary_host_mirror "https://npm.taobao.org/mirrors/node-inspector/"
sass_binary_site "https://npm.taobao.org/mirrors/node-sass/"
sqlite3_binary_host_mirror "https://foxgis.oss-cn-shanghai.aliyuncs.com/"

# 查询当前registry(使用npm install命令安装依赖时下载依赖的网站)
npm config get registry

# yarn查看当前registry(使用yarn add命令安装依赖时下载依赖的网站)
yarn config get registry

# 设置阿里源
npm config set registry https://mirrors.cloud.tencent.com/npm/

# yarn设置阿里源
yarn config set registry https://mirrors.cloud.tencent.com/npm/

# 设置官方源
npm config set registry https://registry.npmjs.org/
yarn config set registry https://registry.npmjs.org/
```

**nrm配置源方法不推荐使用，使用上面方法即可**

```shell
# 安装nrm
npm install nrm -g
# 查看nrm版本
nrm --version
# 查看当前npm/yarn源
nrm ls
# 指定npm/yarn源
nrm use tencent
```



### `npm`、`npm run`、`package.json`关系

>[`npm` 与 `package.json` 详解](https://blog.csdn.net/xingmeiok/article/details/90299089)
>
>[`npm run`](https://www.jianshu.com/p/55320470dec3)



### `npm` 和 `yarn` 相关命令

> [`npm` 命令使用](https://www.runoob.com/nodejs/nodejs-npm.html)

#### 安装 `yarn`

```shell
npm install yarn -g
```



#### 查看 `npm`、`yarn` 版本

```shell
# 查看npm版本
npm -v

# 查看yarn版本
yarn -v
```

#### 使用 `npm init` 初始化一个空项目

> 实质是在当前目录生成 `package.json` 配置文件。

```shell
# 初始化项目生产package.json项目配置文件
# https://www.cnblogs.com/WD-NewDemo/p/11141384.html
npm init

# 使用默认配置项初始化项目
npm init --yes
```



#### 使用 `npm` 和 `yarn` 安装远程包

> `npm install` 命令会在当前目录下创建 `node_modules` 用于存放第三方依赖的源代码。

```shell
# 全局安装jQuery
npm install jquery -g

# 在当前目录安装最新版本jQuery
# 注意：
# 1、需要使用npm init --yes初始化当前目录，否则npm install不会在当前目录生成node_modules子目录
# 2、如果当前目录没有node_modules子目录，命令会往上级目录寻找node_modules目录直到找到，如果上级目录都无法找到此目录，则命令会在当前目录创建一个node_modules子目录
npm install jquery

# 安装指定版本jQuery
npm install jquery@1.1

# 使用yarn add安装指定版本vue
yarn add vue@3.2.20

# 将模块安装到项目目录下，并在package.json文件的dependencies节点写入依赖
# https://www.cnblogs.com/limitcode/p/7906447.html
npm install jquery --save

# 将模块安装到项目目录下，并在package.json文件的devDependencies节点写入依赖
# https://www.cnblogs.com/limitcode/p/7906447.html
npm install jquery --save-dev
```



#### 更新依赖

```shell
# 更新jquery最新次版本号
# https://www.jianshu.com/p/9398a3586ddc
npm update jquery
```



使用 `npm list` 查看已安装哪些依赖

```shell
# 查看当前系统全局安装哪些npm包
npm list -g

# 查看当前项目安装哪些依赖
npm list

# 查看当前本地安装jquery版本
npm list jquery
```



#### 卸载

```shell
# 卸载jquery
npm uninstall jquery
```



#### 执行 `package.json` 里指定目标

```shell
# 调用package.json配置文件里面的scripts.test命令
npm run test
```



#### 显示远程依赖的所有版本

```shell
# 显示远程vue所有版本
npm info vue versions

# 显示远程vue所有版本
yarn info vue versions
```



#### 显示 `package.json` 依赖树版本

显示整棵依赖树的版本，参考 [链接](https://stackoverflow.com/questions/49019022/how-to-check-a-projects-vue-js-version)

```sh
npm list -a
```

显示指定组件的依赖版本，参考 [链接](https://stackoverflow.com/questions/49019022/how-to-check-a-projects-vue-js-version)

```sh
npm list @vue/cli-service
```



## `nvm`

注意：尝试过使用 `nvm` 管理 `nodejs` 版本自由切换，但是由于 `nvm` 安装脚本和 `raw.gitcontent.com` 通讯和 `nvm ls-remote` 命令在国内运行卡住没有响应等问题，放弃使用 `nvm` 管理 `nodejs` 版本。



## 帐号信息

### `npm registry`

- 网址：`https://www.npmjs.com/`
- 帐号：`dl`
- 邮箱：`gmail`
- 密码：`SecretX--#||(UpX),.12`



## 发布组件到 `npm registry`

在本地登录 `npm registry`

```bash
npm login --registry=https://registry.npmjs.com
```

- 按 `Enter` 后命令会打开浏览器要求你登录 `npm registry`，输入帐号和密码即可。

下载用于测试组件发布的项目到本地 `https://gitee.com/dexterleslie/demonstration/tree/main/front-end/demo-vue/demo-vue2-cli-my-component-lib`

发布组件到 `npm registry`

```bash
cd demo-vue2-cli-my-component-lib
npm publish --registry=https://registry.npmjs.com
```

参考本站 <a href="/vue/脚手架创建项目.html#创建-vue2" target="_blank">链接</a> 创建 `Vue2` 测试项目

在测试项目中安装自定义组件依赖

```bash
npm install demo-vue2-cli-my-component-lib
```

在测试项目 `src/main.js` 中注册组件库

```javascript
import Vue from 'vue'
import App from './App.vue'
// 导入组件库
import MyComponentPlugin from 'demo-vue2-cli-my-component-lib'
// 单独引用组件
import { MyComponent1 } from 'demo-vue2-cli-my-component-lib'

Vue.config.productionTip = false
// 注册组件库到 Vue 中，Vue.use 函数会自动调用组件库中的 install 函数
Vue.use(MyComponentPlugin)
// 通过 <my-component-1></my-component-1> 单独引用组件库
Vue.component('my-component-1', MyComponent1)

new Vue({
  render: h => h(App),
}).$mount('#app')
```

在测试项目 `src/App.vue` 中引用组件库中的组件

```vue
<template>
  <div id="app">
    <MyComponent1></MyComponent1>
    <MyComponent2></MyComponent2>
    <my-component-1></my-component-1>
  </div>
</template>

<script>

export default {
  name: 'App',
}
</script>

<style>
#app {
  font-family: Avenir, Helvetica, Arial, sans-serif;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
  text-align: center;
  color: #2c3e50;
  margin-top: 60px;
}
</style>

```



## `package.json scripts` 中的命令添加环境变量

`cross-env` 是一个跨平台设置环境变量的工具，能统一不同系统下的语法差异。安装 `cross-env`（全局或项目内）：

```sh
npm install cross-env --save-dev
```

修改 `package.json` 的 `start` 脚本，将原来的 `NODE_ENV=development electron .` 替换为 `cross-env NODE_ENV=development electron .`：

```json
"scripts": {
  "start": "cross-env NODE_ENV=development electron .",
  "test": "echo \"Error: no test specified\" && exit 1"
}
```



## `import`、`export`

> 注意：使用 `es6 export` 定义模块时，需要在 `package.json` 文件声明 `"type": "module"`，否则在调用模块时会报错。



### 概念

在 Node.js 中，`import` 和 `export` 是 **ECMAScript 模块（ES Modules，简称 ESM）** 的核心语法，用于实现模块的导入与导出。Node.js 早期默认使用 CommonJS（`require`/`module.exports`），但从 v12 开始逐步支持 ESM（v14 起稳定），旨在提供更现代、静态分析友好的模块化方案。

**一、ESM 的核心语法**

ESM 的 `import` 和 `export` 是**静态声明**（编译时确定依赖关系），支持更严格的模块解析规则，且语法更简洁。

**1. 导出（export）**

ESM 提供两种导出方式：**命名导出**（Named Exports）和**默认导出**（Default Export）。

（1）命名导出（多个值导出）

适用于导出多个独立的变量、函数或类，需为每个导出项命名。  
**语法示例：**
```javascript
// utils.js
// 方式1：直接导出（声明时导出）
export const PI = 3.14;
export function add(a, b) {
  return a + b;
}

// 方式2：集中导出（声明后导出）
const MAX = 100;
function log(msg) {
  console.log(msg);
}
export { MAX, log }; // 集中导出已声明的变量/函数

// 方式3：重命名导出（避免命名冲突）
const secret = '123';
export { secret as mySecret }; // 导出时重命名为 mySecret
```

（2）默认导出（单个主值导出）

每个模块最多只能有一个默认导出，通常用于导出模块的“核心值”（如一个类或对象）。  
**语法示例：**
```javascript
// app.js
// 导出一个对象（最常见场景）
export default {
  start() { /* ... */ },
  stop() { /* ... */ }
};

// 或直接导出一个值（如函数）
export default function greet(name) {
  return `Hello, ${name}！`;
}
```

**2. 导入（import）**

根据导出方式的不同，`import` 的语法也不同，支持灵活组合。

（1）导入命名导出

需使用 `{ }` 匹配导出的名称，可重命名以避免冲突。  
**示例：**
```javascript
// 导入 utils.js 中的命名导出
import { PI, add, MAX } from './utils.js'; // 必须与导出名一致

// 重命名导入（避免与现有变量冲突）
import { add as sum } from './utils.js'; // 使用 sum 代替 add

// 导入所有命名导出为一个对象（不推荐，失去静态分析优势）
import * as Utils from './utils.js'; // Utils.PI, Utils.add 等
```

（2）导入默认导出

无需 `{ }`，可自定义名称。  
**示例：**
```javascript
// 导入 app.js 的默认导出
import app from './app.js'; // 名称可自定义（如 app、myApp）

// 混合导入（默认导出 + 命名导出）
import app, { PI } from './app.js'; // 同时导入默认导出和命名导出
```

（3）动态导入（Dynamic Import）

ESM 支持动态导入（返回 Promise），适用于按需加载模块（如条件加载、懒加载）。  
**示例：**
```javascript
// 动态导入（异步）
const loadModule = async () => {
  const module = await import('./utils.js');
  console.log(module.PI); // 3.14
};
loadModule();
```

**二、在 Node.js 中启用 ESM**

Node.js 默认使用 CommonJS，需通过以下方式启用 ESM：

**1. 方式1：`.mjs` 扩展名**

将文件扩展名改为 `.mjs`，Node.js 会自动识别为 ESM。  
**示例：**
```
// 文件：math.mjs
export const add = (a, b) => a + b;

// 运行：node math.mjs（直接执行）
```

**2. 方式2：`package.json` 中声明 `type: "module"`**

在项目的 `package.json` 中添加 `"type": "module"`，则当前目录及子目录下的 `.js` 文件会被视为 ESM。  
**示例：**
```json
{
  "type": "module", // 关键配置
  "main": "app.js"
}
```
此时，`app.js` 作为 `.js` 文件会被识别为 ESM。

**三、ESM 与 CommonJS 的核心区别**

Node.js 中 ESM 和 CommonJS（`require`/`module.exports`）共存，但有以下关键差异：

| **特性**         | **ESM**                                | **CommonJS**                                                 |
| ---------------- | -------------------------------------- | ------------------------------------------------------------ |
| **加载时机**     | 静态加载（编译时确定依赖）             | 动态加载（运行时解析）                                       |
| **模块解析**     | 严格路径检查（无自动 `index.js` 补全） | 自动补全 `index.js`（如 `require('./utils')` 等价于 `require('./utils/index.js')`） |
| **文件扩展名**   | 必须明确（`.js`/`.mjs`）               | 允许省略（`require('./utils')` 自动查找 `.js`/`.json`/`.node`） |
| **循环依赖**     | 处理更友好（返回未完成的导出）         | 可能导致部分导出未初始化                                     |
| **浏览器兼容性** | 原生支持                               | 需打包工具（如 Webpack）转换                                 |
| **语法**         | `import`/`export`                      | `require`/`module.exports`                                   |

**四、常见问题与注意事项**

**1. ESM 中不能直接使用 `require`**

ESM 是静态模块系统，禁止使用 `require`（会抛出 `require is not defined` 错误）。若需加载 CommonJS 模块，可使用以下方式：
- **动态导入**：`const cjsModule = await import('cjs-module');`（返回 `{ default: cjsModule }` 对象）。
- **`createRequire`**（实验性）：`const require = await import('module').then(m => m.createRequire(__filename));`（不推荐，仅兼容旧代码）。

**2. 导出值的不可变性**

ESM 的导出是**实时绑定**（Live Bindings），即导入方会感知导出方的修改（仅适用于对象/数组等引用类型）。  
**示例：**
```javascript
// counter.js
export let count = 0;
export function increment() {
  count++;
}

// app.js
import { count, increment } from './counter.js';
console.log(count); // 0
increment();
console.log(count); // 1（实时更新）
```

**3. 模块作用域**

ESM 中顶层的 `this` 是 `undefined`（CommonJS 中 `this` 指向 `module.exports`），且不能使用 `__dirname`/`__filename`（可用 `import.meta.url` 替代）：
```javascript
// 获取当前文件路径（ESM）
import { fileURLToPath } from 'url';
const __filename = fileURLToPath(import.meta.url);
const __dirname = path.dirname(__filename);
```

**五、总结**

Node.js 中的 `import` 和 `export` 是 ESM 的核心语法，适用于现代 JavaScript 项目。其优势在于：
- **静态分析**：支持 Tree Shaking（移除未使用的代码），减小打包体积。
- **语法简洁**：更接近浏览器原生 JS，降低学习和迁移成本。
- **严格规范**：模块解析和加载行为更可预测，减少潜在错误。

**迁移建议**：新项目优先使用 ESM（通过 `type: "module"`）；旧项目可逐步将 CommonJS 模块转换为 ESM，或通过动态导入兼容两者。



### 实验

>详细用法请参考本站 [示例](https://gitee.com/dexterleslie/demonstration/tree/main/front-end/demo-nodejs/es6-export-and-import)

实验目标：

- 演示 `es6 export` 和 `import` 的用法。
- 使用 `export` 和 `import` 模拟实现第三方 `npm` 包开发到发布过程。



示例的目录结构：

- `my-package`：第三方 `npm` 包源代码。
- `my-test`：测试调用第三方包项目。



调试：

```sh
# 在my-test项目中安装my-package模块
npm install ../my-package

# 运行my-test测试
node index.js
```



### 用法说明



#### 默认导出

```javascript
// 默认导出
// 使用import命令的时候，用户需要知道所要加载的变量名或函数名，否则无法加载。但是，用户肯定希望快速上手，未必愿意阅读文档，去了解模块有哪些属性和方法。
// 为了给用户提供方便，让他们不用阅读文档就能加载模块，就要用到export default命令
export default function(a, b) {
    return a+b
}

// // 或者上面export default function...等价写法
// function add(a, b) {
//     return a+b
// }
// export default add
```



#### 命名导出 - 导出常量

```javascript
// 命名导出

// 导出常量
export const ConstVariable1 = '常量1'
// // 或者上面常量导出的等价写法
// const ConstVariable1 = '常量1'
// export {ConstVariable1}
```



#### 命令导出 - 导出变量

```javascript
// 直接导出变量
export let variable2 = '变量2'
```



#### 命令导出 - 导出函数

```javascript
// 导出函数
export function add(a, b) {
    return a+b;
}
```



#### 命名导出 - 导出构造函数

```javascript
// 导出构造函数
export function Person(name, age) {
    this.name = name
    this.age = age

    this.display = function() {
        console.log(`name=${this.name},age=${this.age}`)
    }
}
```



#### 命名导出 - 导出对象

```javascript
// 导出对象
export let ObjectVariable = {
    prop1: 'prop value1',
    prop2: 'prop value2',
}
```



#### 导入默认导出

```javascript
// 导入默认导出
import MyAddFunction from 'my-package'

console.log(`1+5=${MyAddFunction(1, 5)}`)
```



#### 命名导入

>[`ES6` 导入模块错误 `Cannot use import statement outside a module`](https://www.jianshu.com/p/b802982bad8c)
>
>[`Module` 的语法](https://es6.ruanyifeng.com/#docs/module)
>
>[`export` 用法](https://developer.mozilla.org/en-US/docs/web/javascript/reference/statements/export)

```javascript
import { 
    ConstVariable1,
    variable2,
    add,
    Person,
    ObjectVariable
} from "my-package";

// 所有export一次import加载写法
import * as allExport from 'my-package'

console.log(`ConstVariable1=${ConstVariable1}`)
console.log(`variable2=${variable2}`)
console.log(`1+2=${add(1, 2)}`)
let person = new Person('Dexterleslie', 30)
person.display()
console.log(`ObjectVariable.prop1=${ObjectVariable.prop1},ObjectVariable.prop2=${ObjectVariable.prop2}`)

console.log(`ConstVariable1=${allExport.ConstVariable1}`)
console.log(`variable2=${allExport.variable2}`)
console.log(`1+2=${allExport.add(1, 2)}`)
person = new allExport.Person('Dexterleslie', 30)
person.display()
console.log(`ObjectVariable.prop1=${allExport.ObjectVariable.prop1},ObjectVariable.prop2=${allExport.ObjectVariable.prop2}`)

```



## `commonJS` 的 `exports` 和 `require`

> [`require()` 源码解读](https://www.ruanyifeng.com/blog/2015/05/require.html)

`require` 遵循 `CommonJS/AMD` 规范。



### `commonJS` 概述

`node` 应用由模块组成，采用 `CommonJS` 模块规范。

`CommonJS` 规范规定，每个模块内部，`module` 变量代表当前模块。这个变量是一个对象，它的 `exports` 属性（即 `module.exports`）是对外的接口。加载某个模块，其实是加载该模块的 `module.exports` 属性。

`require` 方法用于加载模块。

`CommonJS` 模块的特点如下：

- 所有代码都运行在模块作用域，不会污染全局作用域。
- 模块可以多次加载，但是只会在第一次加载时运行一次，然后运行结果就被缓存了，以后再加载，就直接读取缓存结果。要想让模块再次运行，必须清除缓存。
- 模块加载的顺序，按照其在代码中出现的顺序。



### `require` 加载原理

当 `Node` 遇到 `require(X)` 时，按下面的顺序处理：

1. 如果 `X` 是内置模块，比如 `require('http'）`：
   - a. 返回该模块。
   - b. 不再继续执行。
2. 如果 `X` 以 `"./"` 或者 `"/"` 或者 `"../"` 开头：
   - a. 根据 `X` 所在的父模块，确定 `X` 的绝对路径。
   - b. 将 `X` 当成文件，依次查找下面文件，只要其中有一个存在，就返回该文件，不再继续执行。
     - `X`
     - `X.js`
     - `X.json`
     - `X.node`
   - c. 将 `X` 当成目录，依次查找下面文件，只要其中有一个存在，就返回该文件，不再继续执行。
     - `X/package.json`（`main` 字段）
     - `X/index.js`
     - `X/index.json`
     - `X/index.node`
3. 如果 `X` 不带路径
   - a. 根据 `X` 所在的父模块，确定 `X` 可能的安装目录。
   - b. 依次在每个目录中，将 `X` 当成文件名或目录名加载。
4. 抛出 `not found`

请看一个例子。

当前脚本文件 `/home/ry/projects/foo.js` 执行了 `require('bar')` ，这属于上面的第三种情况。`Node` 内部运行过程如下：

1. 首先，确定 `bar` 的绝对路径可能是下面这些位置，依次搜索每一个目录。

   * `/home/ry/projects/node_modules/bar`

   * `/home/ry/node_modules/bar`

   * `/home/node_modules/bar`

   * `/node_modules/bar`

2. 搜索时，`Node` 先将 `bar` 当成文件名，依次尝试加载下面这些文件，只要有一个成功就返回。

   * `bar`

   * `bar.js`

   * `bar.json`

   * `bar.node`

3. 如果都不成功，说明 `bar` 可能是目录名，于是依次尝试加载下面这些文件。

   - `bar/package.json`（`main` 字段）
   - `bar/index.js`
   - `bar/index.json`
   - `bar/index.node`

4. 如果在所有目录中，都无法找到 `bar` 对应的文件或目录，就抛出一个错误。



### `require` 源码

`require` 命令的基本功能是，读入并执行一个 `JavaScript` 文件，然后返回该模块的 `exports` 对象。如果没有发现指定模块，会报错。

`require` 是 `Module.prototype` 的一个函数，返回值为 `module.exports`变量，源代码如下：

```javascript
Module.prototype.require = function(path) {
  return Module._load(path, this);
};

Module._load = function(request, parent, isMain) {
    //  计算绝对路径
    var filename = Module._resolveFilename(request, parent);

    //  第一步：如果有缓存，取出缓存
    var cachedModule = Module._cache[filename];
    if (cachedModule) {
        return cachedModule.exports;
    }
    
    // 第二步：是否为内置模块
	if (NativeModule.exists(filename)) {
        return NativeModule.require(filename);
    }
    
    // 第三步：生成模块实例，存入缓存
    var module = new Module(filename, parent);
    Module._cache[filename] = module;

	// 第四步：加载模块
    try {
        module.load(filename);
        hadException = false;
	} finally {
		if (hadException) {
            delete Module._cache[filename];
        }
    }

    // 第五步：输出模块的exports属性
    return module.exports;
};
```



### 实验

>[参考链接](https://docs.npmjs.com/creating-node-js-modules)
>
>详细用法请参考本站 [示例](https://gitee.com/dexterleslie/demonstration/tree/main/front-end/demo-nodejs/commonJS-exports-and-require)

实验目标：

- 演示 `commonJS module.exports` 和 `require` 用法。
- 使用 `module.exports` 和 `require` 模拟实现第三方 `npm` 包开发到发布过程。



示例的目录结构：

- `my-package`：第三方 `npm` 包源代码。
- `my-test`：测试调用第三方包项目。



调试：

```shell
# 在my-test项目中安装my-package模块
npm install ../my-package

# 运行my-test测试
node index.js
```



### 用法说明

>[使用 `module.exports` 导出变量、函数、类](https://www.tutorialsteacher.com/nodejs/nodejs-module-exports)



#### 导出类

```javascript
// 使用module.exports导出变量、函数、类
// https://www.tutorialsteacher.com/nodejs/nodejs-module-exports

// 导出类
module.exports.Person = function(name, age) {
    this.name = name
    this.age = age

    this.display = function() {
        console.log(`name=${this.name},age=${this.age}`)
    }
}
```



#### 导出函数

```javascript
// 导出函数
module.exports.add = function(a, b) {
    return a+b
}
```



#### 导出命名变量

```javascript
// 导出命名变量
module.exports.HelloMessage = 'Hello Dexterleslie'
```



#### 导入

```javascript
const { Person } = require("my-package")

let person = new Person('Dexterleslie', 31)
person.display()

let myPackage = require('my-package')

let a = 1
let b = 5
console.log(`${a}+${b}=${myPackage.add(a, b)}`)

let myAdd = require('my-package').add
a = 5
b = 9
console.log(`${a}+${b}=${myAdd(a, b)}`)

let { add } = require('my-package')
a = 8
b = 9
console.log(`${a}+${b}=${add(a, b)}`)

const HelloMessageObject = require('my-package').HelloMessage

console.log(HelloMessageObject)

const { HelloMessage } = require('my-package')
console.log(HelloMessage)
```

