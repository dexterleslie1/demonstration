# Electron



## 创建空白的 Electron 项目

>[参考链接](https://www.electronjs.org/zh/docs/latest/tutorial/quick-start)
>
>2025/04/11 下面实验失败，错误信息如下：
>
>```bash
>~/workspace-git/temp/test1 » npm install electron --dev
>npm WARN config dev Please use --include=dev instead.
>npm WARN deprecated boolean@3.2.0: Package no longer supported. Contact Support at https://www.npmjs.com/support for more info.
>npm ERR! code 1
>npm ERR! path /home/dexterleslie/workspace-git/temp/test1/node_modules/electron
>npm ERR! command failed
>npm ERR! command sh -c node install.js
>npm ERR! RequestError: connect ECONNREFUSED 20.205.243.166:443
>npm ERR!     at ClientRequest.<anonymous> (/home/dexterleslie/workspace-git/temp/test1/node_modules/got/dist/source/core/index.js:970:111)
>npm ERR!     at Object.onceWrapper (node:events:633:26)
>npm ERR!     at ClientRequest.emit (node:events:530:35)
>npm ERR!     at origin.emit (/home/dexterleslie/workspace-git/temp/test1/node_modules/@szmarczak/http-timer/dist/source/index.js:43:20)
>npm ERR!     at TLSSocket.socketErrorListener (node:_http_client:500:9)
>npm ERR!     at TLSSocket.emit (node:events:518:28)
>npm ERR!     at emitErrorNT (node:internal/streams/destroy:169:8)
>npm ERR!     at emitErrorCloseNT (node:internal/streams/destroy:128:3)
>npm ERR!     at process.processTicksAndRejections (node:internal/process/task_queues:82:21)
>npm ERR!     at TCPConnectWrap.afterConnect [as oncomplete] (node:net:1605:16)
>
>npm ERR! A complete log of this run can be found in: /home/dexterleslie/.npm/_logs/2025-04-11T00_51_15_332Z-debug-0.log
>
>```
>
>

创建项目目录

```bash
mkdir test1
```

初始化项目

```bash
cd test1
npm init
```

将 electron 包安装到应用的开发依赖中

```bash
npm install electron --dev
```

新增以下内容到 package.json

```json
{  "scripts": {    "start": "electron ."  }}
```

新建 main.js 内容如下：

```javascript
const { app, BrowserWindow } = require('electron')

function createWindow () {
  const win = new BrowserWindow({
    width: 800,
    height: 600
  })

  win.loadFile('index.html')
}

app.whenReady().then(() => {
  createWindow()

  app.on('activate', function () {
    if (BrowserWindow.getAllWindows().length === 0) createWindow()
  })
})

app.on('window-all-closed', function () {
  if (process.platform !== 'darwin') app.quit()
})
```

新建 index.html 内容如下：

```html
<!DOCTYPE html>
<html>
  <head>
    <meta charset="UTF-8">
    <!-- https://developer.mozilla.org/en-US/docs/Web/HTTP/CSP -->
    <meta http-equiv="Content-Security-Policy" content="default-src 'self'; script-src 'self'">
    <meta http-equiv="X-Content-Security-Policy" content="default-src 'self'; script-src 'self'">
    <title>Hello World!</title>
  </head>
  <body>
    <h1>Hello World!</h1>
    We are using Node.js <span id="node-version"></span>,
    Chromium <span id="chrome-version"></span>,
    and Electron <span id="electron-version"></span>.
  </body>
</html>
```

启动应用

```bash
npm run start
```



## 创建 Electron+Vue2+Element-UI 项目

>详细用法请参考本站 [示例](https://gitee.com/dexterleslie/demonstration/tree/main/front-end/electron/demo-electron-vue)
>
>[参考链接](https://juejin.cn/post/7058637067718246431)
>
>[参考链接](https://juejin.cn/post/6997402283222761480)

安装 NodeJS v16.20.0：参考本站 <a href="/nodejs/README.html#ubuntu安装nodejs" target="_blank">链接</a>

安装 vue-cli

```bash
sudo npm install -g @vue/cli --registry=https://registry.npmmirror.com 
```

创建一个 Vue2 基础项目(取消 babel 和 eslint 选择)

```bash
vue create demo-electron-vue
```

通过 .npmrc 配置项目 npm 源：参考本站 <a href="/nodejs/README.html#通过项目-npmrc-配置-npm-yarn-源" target="_blank">链接</a>

添加 element-ui 依赖

```bash
cd demo-electron-vue
npm install element-ui@^2.15.6
```

添加 el-button 到 src/App.vue 中，代码如下：

```vue
<template>
  <img alt="Vue logo" src="./assets/logo.png">
  <HelloWorld msg="Welcome to Your Vue.js App"/>
  <el-button type="primary">主要按钮</el-button>
</template>
```

安装 vue-cli-plugin-electron-builder 插件，选择 electron 13.0.0 版本

```bash
vue add electron-builder
```

修改 main.js 添加以下内容：

```javascript
import ElementUI from 'element-ui'
import 'element-ui/lib/theme-chalk/index.css'

Vue.use(ElementUI)
```

启动 Electron+Vue 项目，注意：启动过程中会拉取 **vue-devtools** 的浏览器调试插件，这个时候你如果没有使用科学的方式上网将会出现，这时候如果你可以使用科学的方式来下载那更好，毕竟做开发还是要会的，如果暂时不方便就`src/background.js`中的`await installExtension(VUEJS_DEVTOOLS)`暂时注释掉并将项目重新启动一次。

```bash
npm run electron:serve
```

- 提醒：如果启动卡顿，注释 src/background.js 中下面代码

  ```javascript
  // try {
  //   await installExtension(VUEJS_DEVTOOLS)
  // } catch (e) {
  //   console.error('Vue Devtools failed to install:', e.toString())
  // }
  ```



## 创建 Electron+Vue3+Element-Plus 项目

>详细用法请参考本站 [示例](https://gitee.com/dexterleslie/demonstration/tree/main/front-end/electron/demo-electron-vue3)
>
>[参考链接](https://blog.csdn.net/m0_37518156/article/details/116904437)

安装 NodeJS v16.20.0：参考本站 <a href="/nodejs/README.html#ubuntu安装nodejs" target="_blank">链接</a>

安装 vue-cli

```bash
sudo npm install -g @vue/cli --registry=https://registry.npmmirror.com 
```

创建一个 Vue3 基础项目(取消 babel 和 eslint 选择)

```bash
vue create demo-electron-vue3
```

通过 .npmrc 配置项目 npm 源：参考本站 <a href="/nodejs/README.html#通过项目-npmrc-配置-npm-yarn-源" target="_blank">链接</a>

增加 element-plus 依赖

```bash
cd demo-electron-vue3
npm install element-plus
```

添加 el-button 到 src/App.vue 中，代码如下：

```vue
<template>
  <img alt="Vue logo" src="./assets/logo.png">
  <HelloWorld msg="Welcome to Your Vue.js App"/>
  <el-button type="primary">Primary</el-button>
</template>
```

使用 vue-cli 添加并配置 vue 项目为 electron 项目，选择 electron 13.0.0 版本

```bash
vue add electron-builder
```

开发者模式运行 electron 项目

```bash
npm run electron:serve
```

- 提醒：如果启动卡顿，注释 src/background.js 中下面代码

  ```javascript
  // try {
  //   await installExtension(VUEJS3_DEVTOOLS)
  // } catch (e) {
  //   console.error('Vue Devtools failed to install:', e.toString())
  // }
  ```



## BrowserWindow



### 全屏显示新窗口

> 详细用法请参考本站 [示例](https://gitee.com/dexterleslie/demonstration/tree/main/front-end/electron/electron-browserwindow)

```javascript
const remote = require('electron').remote
const BrowserWindow = remote.BrowserWindow
const globalShortcut = remote.globalShortcut

let win;

document.getElementById("btnFullScreen").onclick = function() {
    if(!win) {
        // 获取所有屏幕
        let displays = remote.screen.getAllDisplays()

        win = new BrowserWindow({
            // 设置全屏窗口
            x: displays[0].bounds.x,
            y: displays[0].bounds.y,
            width: displays[0].bounds.width,
            height: displays[0].bounds.height,

            // 不自动显示窗口，需要调用show方法显示窗口
            show: false,
            
            // 无边
            frame: false,
            fullscreen: undefined,
            transparent: true,
            movable: false,
            resizable: false,
            hasShadow: false,
            enableLargerThanScreen: true,
        })
        win.loadURL('https://www.baidu.com')

        win.setAlwaysOnTop(true, 'screen-saver');
        win.setSkipTaskbar(true)

        // 显示窗口
        win.show()
    }
}

globalShortcut.register('Esc', function() {
    if(win) {
        win.destroy()
        win = null
    }
})
```



## 快捷键

### 注册和取消全局快捷键

>详细用法请参考本站 [示例](https://gitee.com/dexterleslie/demonstration/tree/main/front-end/electron/electron-shortcut)

```javascript
const { app, BrowserWindow, globalShortcut } = require('electron')

app.whenReady().then(() => {
  createWindow()

  app.on('activate', function () {
    if (BrowserWindow.getAllWindows().length === 0) createWindow()
  })

  // 参考文档
  // https://www.electronjs.org/zh/docs/latest/api/accelerator
  // https://www.electronjs.org/zh/docs/latest/api/global-shortcut

  // 注册ctrl+shift+a快捷键
  // macOS按下command+shift+a键触发
  let result = globalShortcut.register('CommandOrControl+shift+a', function() {
    console.log(`按下CommandOrControl+shift+a快捷键`);
  });
  if(!result) {
    console.log('注册快捷键ctrl+shift+a失败');
  } else {
    console.log('注册快捷键ctrl+shift+a成功');
  }
})

app.on('will-quit', function() {
  // 注销ctrl+shift+a快捷键
  globalShortcut.unregister('CommandOrControl+shift+a');

  // 注销所有快捷键
  globalShortcut.unregisterAll();
})

app.on('window-all-closed', function () {
  if (process.platform !== 'darwin') app.quit()
})

```

