# Electron



## 创建空白的 Electron 项目

>提醒：`node` 版本为 `v20.12.2`。
>
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
>- ~~通过设置 `npm` 代理服务器的方式，解决上面 `npm install` 的错误。参考本站 <a href="/nodejs/npm命令.html#设置代理服务器" target="_blank">链接</a>。~~
>- 参考下面说明，通过配置 `.npmrc` 解决上面的 `npm install` 错误。

创建项目目录

```bash
mkdir test1
```

初始化项目

```bash
cd test1
npm init
```

创建 `.npmrc` 文件，否则 `npm install` 命令会执行失败

>[参考链接](https://mjpclab.site/uncategorized/npm-install-electron-by-mirror)

```properties
electron_mirror=https://npmmirror.com/mirrors/electron/
electron-builder-binaries_mirror=https://npmmirror.com/mirrors/electron-builder-binaries/
registry=https://mirrors.cloud.tencent.com/npm/
```

将 electron 包安装到应用的开发依赖中

```bash
npm install electron --dev
```

新增以下内容到 package.json

```json
{  "scripts": {    "start": "electron ."  }}
```

`package.json` 中的 `main` 修改为：

```json
"main": "main.js"
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
    <meta http-equiv="Content-Security-Policy" content="default-src 'self'; script-src 'self'; style-src 'self' 'unsafe-inline'">
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



## 自定义窗口

### 自定义窗口样式

#### 无边框窗口

>[参考官方文档](https://www.electronjs.org/docs/latest/tutorial/custom-window-styles#frameless-windows)

`main.js`

```javascript
const { app, BrowserWindow } = require('electron')

function createWindow () {
  const win = new BrowserWindow({
    width: 800,
    height: 600,
    resizable: false,
    // 无边框窗口
    frame: false,
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

`index.html`

```html
<!DOCTYPE html>
<html>
  <head>
    <meta charset="UTF-8">
    <meta http-equiv="Content-Security-Policy" content="default-src 'self'; script-src 'self'; style-src 'self' 'unsafe-inline'">
    <title>Hello World!</title>
    <style>
      body {
        /* 用于 Electron 或基于 WebKit 的应用中，用来指定某个区域是否可拖动窗口 */
        app-region: drag;
        /* 禁用文本选择 */
        user-select: none;
      }
    </style>
  </head>
  <body>
    <h1>Hello World!</h1>
    We are using Node.js <span id="node-version"></span>,
    Chromium <span id="chrome-version"></span>,
    and Electron <span id="electron-version"></span>.
  </body>
</html>
```



#### 透明窗口

>[参考官方文档](https://www.electronjs.org/docs/latest/tutorial/custom-window-styles#transparent-windows)

`main.js`

```javascript
const { app, BrowserWindow } = require('electron')

function createWindow () {
  const win = new BrowserWindow({
    width: 100,
    height: 100,
    resizable: false,
    frame: false,
    transparent: true
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

`index.html`

```html
<!DOCTYPE html>
<html>
  <head>
    <meta charset="UTF-8">
    <meta http-equiv="Content-Security-Policy" content="default-src 'self'; script-src 'self'; style-src 'self' 'unsafe-inline'">
    <link href="./styles.css" rel="stylesheet">
    <title>Transparent Hello World</title>
  </head>
  <body>
    <div class="white-circle">
        <div>Hello World!</div>
    </div>
  </body>
</html>
```

`styles.css`

```css
body {
    margin: 0;
    padding: 0;
    background-color: rgba(0, 0, 0, 0); /* Transparent background */
}
.white-circle {
    width: 100px;
    height: 100px;
    background-color: white;
    border-radius: 50%;
    display: flex;
    align-items: center;
    justify-content: center;
    /* 用于 Electron 或基于 WebKit 的应用中，用来指定某个区域是否可拖动窗口 */
    app-region: drag;
    /* 禁用文本选择 */
    user-select: none;
}

```



### 自定义标题栏

#### 删除默认标题栏

>[参考官方文档](https://www.electronjs.org/docs/latest/tutorial/custom-title-bar#remove-the-default-title-bar)

在 `Ubuntu20.4` 上测试，删除默认标题栏的效果和无边框窗口的效果一致。

`main.js`

```javascript
const { app, BrowserWindow } = require('electron')

function createWindow () {
  const win = new BrowserWindow({
    // remove the default titlebar
    titleBarStyle: 'hidden'
  })
  win.loadURL('https://example.com')
}

app.whenReady().then(() => {
  createWindow()
})
```



#### 添加原生窗口控制器

>[参考官方文档](https://www.electronjs.org/docs/latest/tutorial/custom-title-bar#add-native-window-controls-windows-linux)

在 macOS 上，设置 titleBarStyle: 'hidden' 会移除标题栏，但窗口的交通灯控件仍保留在左上角。但是在 Windows 和 Linux 上，您需要通过在 BrowserWindow 构造函数中设置 BaseWindowContructorOptions 的 titleBarOverlay 参数，将窗口控件重新添加到 BrowserWindow 中。

`main.js`

```javascript
const { app, BrowserWindow } = require('electron')

function createWindow () {
  const win = new BrowserWindow({
    // remove the default titlebar
    titleBarStyle: 'hidden',
    // expose window controls in Windows/Linux
    ...(process.platform !== 'darwin' ? { titleBarOverlay: true } : {})
  })
  win.loadURL('https://example.com')
}

app.whenReady().then(() => {
  createWindow()
})
```

设置 titleBarOverlay: true 是将窗口控件重新暴露到 BrowserWindow 的最简单方法。



#### 创建自定义标题栏

>[参考官方文档](https://www.electronjs.org/docs/latest/tutorial/custom-title-bar#create-a-custom-title-bar)

`main.js`

```javascript
const { app, BrowserWindow } = require('electron')

function createWindow () {
  const win = new BrowserWindow({
    // remove the default titlebar
    titleBarStyle: 'hidden',
    // expose window controls in Windows/Linux
    ...(process.platform !== 'darwin' ? { titleBarOverlay: true } : {})
  })

  win.loadFile('index.html')
}

app.whenReady().then(() => {
  createWindow()
})
```

`index.html`

```html
<!DOCTYPE html>
<html>
  <head>
    <meta charset="UTF-8">
    <!-- https://developer.mozilla.org/en-US/docs/Web/HTTP/CSP -->
    <meta http-equiv="Content-Security-Policy" content="default-src 'self'; script-src 'self'; style-src 'self' 'unsafe-inline'">
    <link href="./styles.css" rel="stylesheet">
    <title>Custom Titlebar App</title>
  </head>
  <body>
	  <!-- mount your title bar at the top of you application's body tag -->
    <div class="titlebar">Cool titlebar</div>
  </body>
</html>
```

`styles.css`

```css
body {
    margin: 0;
}

.titlebar {
  height: 30px;
  background: blue;
  color: white;
  display: flex;
  justify-content: center;
  align-items: center;
  app-region: drag;
}
```



### 自定义窗口交互

#### 自定义可拖动区域

>[参考官方文档](https://www.electronjs.org/docs/latest/tutorial/custom-window-interactions#custom-draggable-regions)

默认情况下，窗口使用操作系统 Chrome 提供的标题栏进行拖动。移除默认标题栏的应用需要使用 app-region CSS 属性来定义可用于拖动窗口的特定区域。设置 app-region: drag 会将矩形区域标记为可拖动。

需要注意的是，可拖动区域会忽略所有指针事件。例如，与可拖动区域重叠的按钮元素将不会在该重叠区域内触发鼠标点击或鼠标进入/退出事件。设置 app-region: no-drag 可以通过从可拖动区域中排除矩形区域来重新启用指针事件。

为了使整个窗口可拖动，您可以添加 app-region: drag 作为 body 的样式：

```css
body {
  app-region: drag;
}
```

并请注意，如果您已将整个窗口设置为可拖动，则还必须将按钮标记为不可拖动，否则用户将无法点击它们：

```css
button {
  app-region: no-drag;
}
```

如果您仅将自定义标题栏设置为可拖动，则还需要使标题栏中的所有按钮不可拖动。

创建可拖动区域时，拖动行为可能与文本选择冲突。例如，拖动标题栏时，可能会意外选中其中的文本内容。为了避免这种情况，您需要在可拖动区域内禁用文本选择，如下所示：

```css
.titlebar {
  user-select: none;
  app-region: drag;
}
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



## `remote`

>[参考文档](https://wizardforcel.gitbooks.io/electron-doc/content/api/remote.html)

`remote` 模块提供了一种在渲染进程（网页）和主进程之间进行进程间通讯（IPC）的简便途径。



## 启动 `DevTools` 调试器

>详细用法请参考本站 [示例](https://gitee.com/dexterleslie/demonstration/tree/main/front-end/electron/demo-devtools)

```javascript
const { app, BrowserWindow } = require('electron')

function createWindow() {
  const win = new BrowserWindow({
    width: 800,
    height: 600,
  })

  // 开发环境下自动打开 DevTools
  // 在 package.json 中传递 NODE_ENV 环境变量 "start": "NODE_ENV=development electron ."
  if (process.env.NODE_ENV === 'development') {
    win.webContents.openDevTools()
  }

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



## 渲染器进程和主进程通信

>[参考官方文档](https://www.electronjs.org/zh/docs/latest/api/ipc-renderer)
>
>详细用法请参考本站 [示例](https://gitee.com/dexterleslie/demonstration/tree/main/front-end/electron/demo-自定义窗口控制)

`ipcRenderer` 是一个 [EventEmitter](https://nodejs.org/api/events.html#events_class_eventemitter) 的实例。 你可以使用它提供的一些方法从渲染进程 (web 页面) 发送同步或异步的消息到主进程。 也可以接收主进程回复的消息。

`index.html` 代码片段：

```html
<script>
    // 给主进程发送消息
    const { ipcRenderer } = require("electron")
    function onMinimize() {
        ipcRenderer.send("window-minimize")
    }

    function onMaximum() {
        ipcRenderer.send("window-maximum")
    }

    function onExit() {
        ipcRenderer.send("window-exit")
    }
</script>
```

`main.js` 代码片段：

```javascript
function createWindow() {
    ...

    ipcMain.on('window-minimize', () => {
        // 窗口最小化
        win.minimize()
    })

    ipcMain.on('window-maximum', () => {
        if (win.isMaximized()) {
            // 如果最大化则恢复到之前状态
            win.unmaximize()
        } else {
            // 最大化窗口
            win.maximize()
        }
    })

    ipcMain.on('window-exit', () => {
        // 关闭窗口
        win.close()
        // 关闭应用
        app.quit()
    })
}
```

上面的代码中，在渲染进程中通过 `ipcRenderer` 向主进程 `ipcMain` 发送消息通信。



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



## 词汇表

>[参考官方文档](https://www.electronjs.org/docs/latest/glossary)

### `process`

进程是正在执行的计算机程序的一个实例。使用主进程和一个或多个渲染进程的 Electron 应用实际上是同时运行多个程序。

在 Node.js 和 Electron 中，每个正在运行的进程都有一个 process 对象。该对象是一个全局变量，提供有关当前进程的信息并对其进行控制。作为全局变量，它始终可供应用程序使用，无需使用 require()。

### `main process`

主进程（通常名为 main.js）是每个 Electron 应用的入口点。它控制应用的生命周期，从打开到关闭。它还管理原生元素，例如菜单、菜单栏、Dock、托盘等。主进程负责创建应用中每个新的渲染进程。完整的 Node API 已内置。

每个应用程序的主进程文件都在 package.json 的 main 属性中指定。这就是 electron . 如何知道启动时要执行哪个文件的方式。

在 Chromium 中，此进程被称为“浏览器进程”。在 Electron 中，为了避免与渲染器进程混淆，它被重命名。

### `renderer process`

渲染进程是应用中的一个浏览器窗口。与主进程不同，渲染进程可以有多个，每个窗口都在单独的进程中运行。渲染进程也可以被隐藏。



## 主进程模块

>[参考官方文档](https://www.electronjs.org/docs/latest/api/app)

### `app`

控制应用程序的事件生命周期。

以下示例显示如何在关闭最后一个窗口时退出应用程序：

```javascript
const { app } = require('electron')
app.on('window-all-closed', () => {
  app.quit()
})
```

#### 事件

>[参考官方文档](https://www.electronjs.org/docs/latest/api/app#events)

应用程序对象发出以下事件：

##### `will-finish-launching`

应用程序完成基本启动时发出。在 Windows 和 Linux 上，will-finish-launching 事件与 ready 事件相同；在 macOS 上，此事件代表 NSApplication 的 applicationWillFinishLaunching 通知。

大多数情况下，您应该在 ready 事件处理程序中完成所有事情。



## 配置选项

### `nodeIntegration`

在 Electron 中，`webPreferences.nodeIntegration` 是一个关键配置项，用于控制**渲染进程（前端页面）是否能够直接访问 Node.js 的 API 和功能**。以下是其具体作用的详细说明：

**核心作用**

`nodeIntegration` 决定渲染进程中的 JavaScript 是否可以：
- 直接调用 Node.js 内置模块（如 `fs`、`path`、`http` 等）；
- 使用 `require()` 或 `import` 加载本地或第三方 Node.js 模块；
- 访问 Node.js 运行时环境（如 `process`、`Buffer` 等全局对象）。

**具体行为**

- **当 `nodeIntegration: true` 时**：  
  渲染进程的 JavaScript 拥有完整的 Node.js 能力。例如，你可以直接在渲染进程的代码中写：
  ```javascript
  // 直接读取本地文件（危险！）
  const fs = require('fs');
  fs.readFile('/etc/passwd', (err, data) => { ... });
  
  // 使用 Node.js 模块
  const path = require('path');
  console.log(path.join(__dirname, 'file.txt'));
  ```
  这种模式适合需要高度集成 Node.js 能力的场景（如桌面工具类应用），但**存在严重安全风险**（见下文）。

- **当 `nodeIntegration: false` 时（默认值，Electron 12+ 后默认关闭）**：  
  渲染进程的 JavaScript 被隔离在浏览器环境中，无法直接访问 Node.js API。此时，渲染进程只能通过 **IPC（进程间通信）** 与主进程交互，间接调用 Node.js 功能。例如：
  ```javascript
  // 渲染进程（无法直接使用 fs）
  const { ipcRenderer } = require('electron'); // 注意：若 contextIsolation 为 true，此方式也可能受限
  ipcRenderer.invoke('read-file', '/etc/passwd').then(data => { ... });
  ```
  这种模式更安全，适合需要限制前端权限的应用（如 Web 应用封装）。

**安全风险**

`nodeIntegration: true` 虽然方便，但会显著降低应用的安全性，尤其是当渲染进程加载**不可信的外部内容**（如用户上传的网页、第三方网站）时：
- 攻击者可通过 XSS 漏洞直接调用 Node.js API，访问/修改本地文件系统；
- 可能执行系统命令（如通过 `child_process` 模块），导致远程代码执行（RCE）；
- 敏感信息（如用户隐私数据）可能被泄露到本地或网络。

**最佳实践**

- **避免在生产环境中启用 `nodeIntegration: true`**（除非明确需要且内容绝对可信）。
- 推荐使用 **上下文隔离（`contextIsolation: true`）** 配合 **预加载脚本（Preload Script）** 暴露有限的 Node.js 能力，平衡功能与安全。例如：
  ```javascript
  // 预加载脚本（preload.js）
  const { contextBridge, ipcRenderer } = require('electron');
  // 仅暴露必要的 API 给渲染进程
  contextBridge.exposeInMainWorld('api', {
    readFile: (path) => ipcRenderer.invoke('read-file', path)
  });
  ```
  渲染进程通过 `window.api.readFile()` 间接调用 Node.js 功能，避免直接暴露危险接口。

**总结**

`nodeIntegration` 是 Electron 中控制渲染进程权限的核心开关：  
- **`true`**：渲染进程拥有完整 Node.js 能力，方便但危险；  
- **`false`**（推荐）：渲染进程被隔离，需通过 IPC 或预加载脚本安全调用 Node.js 功能。



### `contextIsolation`

在 Electron 中，`webPreferences.contextIsolation` 是控制**渲染进程 JavaScript 上下文隔离级别**的核心配置项，主要用于增强应用安全性。以下是其具体作用的详细说明：

**核心作用**

`contextIsolation` 决定渲染进程的 JavaScript 代码是否运行在一个**独立于主进程全局对象的隔离上下文**中。简单来说：
- **启用（`contextIsolation: true`）**：渲染进程的 JS 上下文与主进程的全局对象（如 `require`、`module`、`process` 等）完全隔离，无法直接访问主进程的原生能力或 Node.js 模块。
- **禁用（`contextIsolation: false`，默认值在 Electron 12 之前）**：渲染进程的 JS 上下文与主进程共享全局对象，可直接访问主进程的 Node.js 能力和全局变量（若未被其他配置限制）。

**具体行为对比**

1. **当 `contextIsolation: false` 时**  

渲染进程的 JS 上下文与主进程“共享”全局环境，这会导致：
- 渲染进程可直接访问主进程的全局对象（如 `require`、`process`、`Buffer` 等），即使 `nodeIntegration` 为 `false`（部分能力仍可能被间接利用）。
- 若同时 `nodeIntegration: true`（如用户代码所示），渲染进程甚至能直接调用 Node.js 模块（如 `fs`、`child_process`），存在**严重安全风险**（例如通过 XSS 攻击执行本地文件读写或系统命令）。

**示例（危险场景）**：  
若渲染进程加载了不可信的网页（如用户输入的 URL），攻击者可通过 XSS 注入代码，直接利用共享的全局对象执行恶意操作：
```javascript
// 渲染进程（contextIsolation: false + nodeIntegration: true）
const fs = require('fs'); // 直接访问 Node.js 模块（因为 context 未隔离）
fs.writeFileSync('/tmp/hacked', '恶意内容'); // 直接写入本地文件
```

2. **当 `contextIsolation: true` 时（推荐）**  

渲染进程的 JS 上下文被隔离到一个独立的沙盒中，与主进程的全局对象完全分离：
- 渲染进程**无法直接访问**主进程的 `require`、`process` 等全局对象，也无法直接调用 Node.js 模块。
- 即使 `nodeIntegration: true`，渲染进程的 JS 上下文仍被隔离，无法直接访问 Node.js 能力（需通过预加载脚本显式暴露）。
- 主进程与渲染进程的通信必须通过 **IPC（进程间通信）** 或 **预加载脚本桥接** 完成，避免直接暴露危险接口。

**示例（安全场景）**：  
通过预加载脚本（`preload.js`）安全暴露有限能力，渲染进程只能通过预定义的接口调用功能：

```javascript
// 预加载脚本（preload.js）
const { contextBridge, ipcRenderer } = require('electron');

// 仅暴露一个安全的文件读取接口（通过 IPC 间接调用）
contextBridge.exposeInMainWorld('api', {
  readFile: (path) => ipcRenderer.invoke('read-file', path)
});

// 渲染进程（contextIsolation: true）
window.api.readFile('/tmp/test.txt') // 只能通过预暴露的 api 调用，无法直接访问 Node.js
  .then(content => console.log(content));
```

**与 `nodeIntegration` 的关系**

`contextIsolation` 和 `nodeIntegration` 共同控制渲染进程的权限，但侧重点不同：
- `nodeIntegration`：直接决定渲染进程是否拥有 Node.js 能力（如 `require`、`fs`）。
- `contextIsolation`：决定渲染进程是否能直接访问主进程的全局对象（如 `process`、`ipcRenderer`）。

**关键结论**：  
即使 `nodeIntegration: false`，若 `contextIsolation: false`，渲染进程仍可能通过其他方式（如直接访问主进程全局对象）间接获取危险能力。因此，**`contextIsolation: true` 是更安全的基础配置**，需配合预加载脚本和 IPC 实现功能。

**最佳实践**

- **生产环境必须启用 `contextIsolation: true`**（Electron 12+ 默认已启用，旧版本需手动设置）。  
- 避免同时启用 `nodeIntegration: true` 和 `contextIsolation: false`（用户代码中的配置存在高风险）。  
- 通过**预加载脚本**（`preload.js`）安全暴露必要功能（如 IPC 通道），而非直接开放 Node.js 或主进程能力。  
- 对于不可信内容（如用户生成的网页），始终使用隔离上下文并严格限制通信接口。

**总结**

`contextIsolation` 是 Electron 中隔离渲染进程与主进程安全边界的核心机制：  
- **`true`**：渲染进程运行在独立沙盒中，无法直接访问主进程全局对象或 Node.js 模块，大幅提升安全性；  
- **`false`**（不推荐）：渲染进程与主进程共享上下文，可能因 XSS 等漏洞导致严重的本地或系统级安全风险。



## 综合技巧

### 圆角外框

>详细用法请参考本站 [示例](https://gitee.com/dexterleslie/demonstration/tree/main/front-end/electron/demo-圆角外框)



## `TODO`

- 怎么使 `electron` 外框自动根据 `viewport` 的高度调整呢？





