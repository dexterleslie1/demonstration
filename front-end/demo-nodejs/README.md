## nodejs

### nodejs原理

> node.js 原理简介 https://www.cnblogs.com/bingooo/p/6720540.html
>
> node.js 是基于 V8 的 JavaScript 运行时，事件驱动、非阻塞，因此轻量、高效。

### 安装nodejs

#### 注意

npm是和Nodejs一起并存的，只要安装了Nodejs，npm也安装好了，安装好Nodejs之后。打开终端，执行如下命令，检查是否安装成功。

尝试过在ubuntu上安装nvm来管理多版本nodejs，但是因为nvm是通过shell脚本在线访问raw.githubusercontent.com安装，其中raw.githubusercontent.com国内无法访问，所以放弃此方案。

#### macOS 安装 nodejs

到nodejs官网 https://nodejs.org/en/download 下载 pkg 安装包，双击 pkg 安装包根据提示安装

查看node版本

```sh
node -v
```



#### windows 安装 nodejs

到nodejs官网 https://nodejs.org/en/download 下载 msi 安装包，双击 msi 安装包根据提示安装

查看node版本

```sh
node -v
```



#### ubuntu安装nodejs

使用 dcli 安装



### npm和yarn

#### 什么是npm

> npm全称node package manager，即node包管理工具。是nodejs默认的、以javascript编写的软件包管理系统。
>
> 使用npm命令能够把远程仓库的第三方javascript库下载到本地。
>
> 安装nodejs后会默认已经安装好npm(npm是nodejs下的一个小工具)
>
> 官网 https://www.npmjs.com/

#### 配置npm、yarn源

> [npm、yarn换源与nrm](https://juejin.cn/post/6844904165466980359)



##### npmmirror 镜像站

> https://npmmirror.com/ 是一个完整 [npmjs.com](https://www.npmjs.com) 镜像，你可以用此代替官方版本(只读)，我们将尽量与官方服务**实时同步**。
>
> https://mjpclab.site/uncategorized/npm-install-electron-by-mirror
>
> 通过下面配置 ELECTRON_MIRROR
> ELECTRON_MIRROR=https://npmmirror.com/mirrors/electron/
>
> 通过下面配置设置  REGISTRY
> npm config set registry https://registry.npmmirror.com



##### 通过项目 .npmrc 配置 npm/yarn 源

创建项目目录 demo-npmrc

```sh
mkdir demo-npmrc
```

初始化项目，所有选项选择默认值直接 enter 即可

```sh
npm init
```

添加 jquery 依赖到项目中

```sh
npm install --save-dev jquery
```

添加 .npmrc 内容如下

```properties
electron_mirror=https://npmmirror.com/mirrors/electron/
electron-builder-binaries_mirror=https://npmmirror.com/mirrors/electron-builder-binaries/
registry=http://mirrors.cloud.tencent.com/npm/
```

npm install 时通过 --verbose 参数查看依赖下载所使用的源

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
npm config set registry http://mirrors.cloud.tencent.com/npm/

# yarn设置阿里源
yarn config set registry http://mirrors.cloud.tencent.com/npm/

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

#### npm和yarn相关命令

> npm命令使用 https://www.runoob.com/nodejs/nodejs-npm.html
>
> npm、npm run、package.json关系
>
> https://blog.csdn.net/xingmeiok/article/details/90299089
>
> https://www.jianshu.com/p/55320470dec3

**安装yarn**

```shell
npm install yarn -g
```

**查看npm、yarn版本**

```shell
# 查看npm版本
npm -v

# 查看yarn版本
yarn -v
```

**使用npm init初始化一个空项目**

> 实质是在当前目录生成package.json配置文件。

```shell
# 初始化项目生产package.json项目配置文件
# https://www.cnblogs.com/WD-NewDemo/p/11141384.html
npm init

# 使用默认配置项初始化项目
npm init --yes
```

**使用npm和yarn安装远程包**

> npm install命令会在当前目录下创建node_modules用于存放第三方依赖的源代码。

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

**更新依赖**

```shell
# 更新jquery最新次版本号
# https://www.jianshu.com/p/9398a3586ddc
npm update jquery
```

**使用npm list查看已安装哪些依赖**

```shell
# 查看当前系统全局安装哪些npm包
npm list -g

# 查看当前项目安装哪些依赖
npm list

# 查看当前本地安装jquery版本
npm list jquery
```

**卸载**

```shell
# 卸载jquery
npm uninstall jquery
```

**执行package.json里指定目标**

```shell
# 调用package.json配置文件里面的scripts.test命令
npm run test
```

**显示远程依赖的所有版本**

```shell
# 显示远程vue所有版本
npm info vue versions

# 显示远程vue所有版本
yarn info vue versions
```



##### 显示 package.json 依赖树版本

显示整棵依赖树的版本，参考 https://stackoverflow.com/questions/49019022/how-to-check-a-projects-vue-js-version

```sh
npm list -a
```

显示指定组件的依赖版本，参考 https://stackoverflow.com/questions/49019022/how-to-check-a-projects-vue-js-version

```sh
npm list @vue/cli-service
```





## `nvm`

注意：尝试过使用`nvm`管理`nodejs`版本自由切换，但是由于`nvm`安装脚本和`raw.gitcontent.com`通讯和`nvm ls-remote`命令在国内运行卡住没有响应等问题，放弃使用`nvm`管理`nodejs`版本。



## 帐号信息

### npm registry

- 网址：`https://www.npmjs.com/`
- 帐号：`dl`
- 邮箱：`gmail`
- 密码：`SecretX--#||(UpX),.12`



## 发布组件到 npm registry

在本地登录 npm registry

```bash
npm login --registry=https://registry.npmjs.com
```

- 按 Enter 后命令会打开浏览器要求你登录 npm registry，输入帐号和密码即可。

下载用于测试组件发布的项目到本地 `https://gitee.com/dexterleslie/demonstration/tree/main/front-end/demo-vue/demo-vue2-cli-my-component-lib`

发布组件到 npm registry

```bash
cd demo-vue2-cli-my-component-lib
npm publish --registry=https://registry.npmjs.com
```

参考本站 <a href="/vue/脚手架创建项目.html#创建-vue2" target="_blank">链接</a> 创建 Vue2 测试项目

在测试项目中安装自定义组件依赖

```bash
npm install demo-vue2-cli-my-component-lib
```

在测试项目 src/main.js 中注册组件库

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

在测试项目 src/App.vue 中引用组件库中的组件

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

