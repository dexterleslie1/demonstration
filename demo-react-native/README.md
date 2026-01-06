## 概念

**React Native（简称 RN）是一个让你能够使用 JavaScript 和 React 来构建真正原生移动应用的框架。**

它的核心理念是：**“Learn once, write anywhere”**（学习一次，到处编写）。

---

### 一、核心本质：它不是 WebView，而是“原生组件”

要理解 React Native，首先要把它和其他的跨端方案区分开。

1.  **传统 WebView 方案（如 Cordova/PhoneGap）：**
    *   **原理：** 将网站（HTML、CSS、JS）打包到一个 App 的“浏览器外壳”（WebView）中运行。
    *   **缺点：** 性能较差，用户体验无法和原生 App 媲美，因为所有渲染都在一个内嵌浏览器中完成。

2.  **React Native 方案：**
    *   **原理：** JavaScript 代码编写的业务逻辑在独立的线程（JS线程）中运行。当需要更新界面时，RN 会将虚拟 DOM 的变更描述（我们称之为“影子树”）通过一个**桥接（Bridge）** 通信机制传递给主线程（UI线程）。
    *   **关键：** 主线程接收到指令后，会调用平台**原生的 UI 组件**（iOS 的 `UIButton`，Android 的 `Button`）进行渲染。
    *   **结果：** 你最终看到和交互的，是真正的原生按钮、原生列表、原生视图，而不是一个模仿原生样式的网页元素。这带来了近乎原生的性能和体验。

---

### 二、与 uni-app 的对比

为了让你更好地理解，我们来对比一下你刚了解的 uni-app：

| 特性             | React Native                                  | uni-app                                                      |
| :--------------- | :-------------------------------------------- | :----------------------------------------------------------- |
| **核心语法**     | **React** + JavaScript/TypeScript             | **Vue** + JavaScript/TypeScript                              |
| **UI 组件**      | 编译为**原生组件**（iOS/Android）             | 编译为**原生组件（App端）** / **小程序组件** / **Web标签**   |
| **技术背景**     | Facebook（现 Meta）开源                       | 中国数字天堂（DCloud）推出                                   |
| **主要目标平台** | **iOS 和 Android**                            | **全端**（App、H5、小程序，尤其是国内小程序）                |
| **渲染方式**     | JS 线程与原生 UI 线程通过 **Bridge 异步通信** | App 端使用自研的**原生渲染引擎**，小程序端直接编译为目标平台代码 |
| **生态与社区**   | **全球社区**，非常活跃，大量第三方库          | **中文社区**为主，与国内生态（如微信小程序）结合紧密         |
| **开发体验**     | 更贴近原生开发，需要处理不同平台的细微差异    | 更强调**一致性**，一套代码尽量覆盖多端，条件编译处理差异     |

---

### 三、工作原理简图

```
你的 JavaScript 代码 (React 组件)
         |
         V
React Native 框架 (JS 运行时环境)
         |
         V
    "桥接 (Bridge)"
    (异步序列化通信)
         |
         V
原生平台 (iOS/Android)
         |
         V
渲染为真正的原生 UI 组件
```

**注意：** 这个“桥接”机制虽然是 RN 强大之处，但也曾是性能瓶颈（例如快速滚动时 JS 线程和原生线程通信可能成为瓶颈）。新架构（Fabric）致力于通过允许同步通信和更直接的控制来解决这个问题。

---

### 四、优势和劣势

#### 优势：
1.  **原生体验与性能：** 由于使用原生组件，其观感和流畅度远胜于 WebView 应用。
2.  **热重载与快速迭代：** 修改代码后可以立即看到效果，无需重新编译整个应用，大大提升了开发效率。
3.  **跨平台共享代码：** 大部分业务逻辑可以共享，显著降低开发和维护两个独立原生应用的成本。
4.  **强大的社区和生态：** 拥有海量的第三方库和解决方案，遇到问题容易找到答案。
5.  **背靠 Facebook：** 被 Meta 用于其核心应用（如 Facebook、Instagram），有长期支持和持续发展保障。

#### 劣势：
1.  **调试复杂度：** 有时需要深入原生层面进行调试，对开发者的要求更高（需要了解一些原生知识）。
2.  **包体积较大：** 即使是一个简单的 App，也会包含整个 RN 运行时，导致安装包比纯原生应用大。
3.  **“桥接”的历史瓶颈：** 在老架构下，复杂的 UI 交互有时会因通信延迟而卡顿（新架构已大幅改善）。
4.  **版本升级可能带来挑战：** 不同版本间可能存在破坏性变更，升级时需要工作量。

### 总结

**React Native 是一个利用 Web 技术（React）来驱动原生 UI 渲染的移动应用开发框架。** 它在开发效率、跨平台能力和原生性能之间取得了极佳的平衡。

如果你是一个熟悉 React 的 Web 开发者，并希望进入移动应用开发领域，React Native 是一个非常自然且强大的选择。它与 uni-app 的选择，往往取决于你的技术栈偏好（React vs Vue）和目标平台重心（全球市场的 iOS/Android vs 国内市场的全端尤其是小程序）。



## 运行原理

React Native 的核心思想是：**用 JavaScript 编写业务逻辑，然后控制原生界面进行渲染。**

它的运行架构可以概括为以下几个关键部分和流程：

---

### 一、核心架构：双线程 + 桥接（Bridge）

React Native 应用运行时，主要包含三个部分：

1.  **JavaScript 线程（JS 线程）**：
    *   **职责**：在这里运行你的 JavaScript 代码（包括 React 组件、业务逻辑等）。
    *   **环境**：运行在一个独立的 **JavaScript 引擎** 中（在 iOS 上是自带的 JavaScriptCore，在 Android 上默认也是 JavaScriptCore，也可以配置为 Google 的 V8）。
    *   **任务**：处理业务逻辑、计算布局、发送指令告诉原生端该做什么。

2.  **原生（UI）线程（Main/UI 线程）**：
    *   **职责**：负责原生 UI 的渲染和用户交互。
    *   **任务**：接收来自 JS 线程的指令，创建和渲染真正的原生组件（如 `UIView`、`TextView`）。同时，它也负责监听用户的操作（如触摸、滑动）。

3.  **桥接（Bridge）**：
    *   **职责**：这是 **最核心** 的部分，它是连接 JS 线程和原生线程的**异步通信通道**。
    *   **工作方式**：**异步、批处理、序列化**。JS 线程和原生线程不能直接调用对方的方法，它们只能通过 Bridge 相互发送消息（JSON 格式的序列化数据）。

---

### 二、详细工作流程（以渲染一个界面为例）

让我们通过一个具体的例子，看看 `<Text>Hello, World!</Text>` 是如何显示在屏幕上的。

#### 步骤 1：JS 线程计算布局（Virtual DOM 差异）

1.  你的 React 组件代码在 JS 线程中执行。
2.  React 会创建和维护一个**虚拟 DOM**，这是一个用 JavaScript 对象描述的 UI 结构树。
3.  当状态（state）或属性（props）发生变化时，React 会计算出新的虚拟 DOM 树，并与旧的树进行对比（Diffing），找出需要更新的最小部分。
4.  这个“更新指令”被序列化成一个 JSON 消息，它描述了“需要创建什么视图”、“样式是什么”、“视图层级关系如何”。

#### 步骤 2：通过 Bridge 传递指令

5.  计算出的 UI 更新指令（JSON 数据）通过 **Bridge** 从 JS 线程发送到原生（UI）线程。
    *   **注意**：这个通信是**异步**的。JS 线程发送消息后不会等待原生线程的响应，而是继续执行后面的 JavaScript 代码。这保证了 JS 线程不会被阻塞。

#### 步骤 3：原生线程渲染真实 UI

6.  原生（UI）线程接收到来自 Bridge 的指令。
7.  原生端有一个对应的**阴影树（Shadow Tree）**，它是由**瑜伽（Yoga）布局引擎**维护的。Yoga 会根据 JS 线程传过来的样式信息（如 `flex: 1`）计算出每个视图元素的**精确位置和大小**。
8.  计算好布局后，原生线程会调用 iOS 的 Objective-C/Swift 或 Android 的 Java/Kotlin 的 API，来创建或更新对应的**真实原生视图**（如 iOS 的 `UILabel`，Android 的 `TextView`），并将其显示在屏幕上。

#### 步骤 4：用户交互与事件回馈

9.  当用户触摸屏幕时：
    *   原生（UI）线程首先捕获到这个触摸事件。
    *   原生线程将这个事件信息（如坐标、事件类型）通过 **Bridge** 反向发送给 JS 线程。
10. JS 线程接收到事件信息后，根据坐标判断是哪个 React 组件应该响应，然后执行你在组件中定义的事件处理函数（如 `onPress`）。

这个过程可以总结为下图所示的循环：

```mermaid
flowchart TD
    A[JS 线程<br>计算差异] --> B[通过Bridge<br>（异步）传递指令]
    B --> C[原生线程<br>（Yoga引擎）计算布局]
    C --> D[渲染原生UI<br>（UILabel/TextView）]
    D -- 用户交互 --> E[原生线程捕获事件]
    E --> F[通过Bridge<br>（异步）传递事件]
    F --> A
```

---

### 三、旧架构的瓶颈与新架构（Fabric）的革新

上面描述的基于“Bridge”的架构是 RN 长期使用的旧架构，它有两个主要瓶颈：

1.  **异步通信延迟**：由于通信是异步的，快速连续的 UI 更新（如高速滚动列表）可能导致 JS 线程和原生线程不同步，造成卡顿。
2.  **序列化开销**：数据在通过 Bridge 传递前需要被序列化为 JSON，这有性能开销。

为了解决这些问题，Facebook 推出了**新架构**（主要组成部分是 **Fabric** 和 **TurboModules**）：

*   **Fabric（新的渲染系统）**：
    *   **允许同步操作**：UI 管理器现在可以被 JS 线程同步调用，消除了通信延迟，使渲染更快。
    *   **直接挂载**：新的渲染器允许将 Shadow Tree 直接挂载到原生视图上，减少了序列化步骤。
*   **TurboModules（新的原生模块系统）**：
    *   惰性加载原生模块，启动更快，并且提供了更强类型的 JS 到原生的调用。

**新架构的核心目标是让 JS 和原生世界的交互更直接、更高效。**

### 总结

React Native 的运行原理可以精炼为：

*   **双线程模型**：JS 线程负责逻辑，原生线程负责渲染，二者隔离保证了 JS 的卡顿不会导致 UI 掉帧。
*   **桥接通信**：通过一个异步、序列化的 Bridge 进行指令和数据的传递。（旧架构的核心）
*   **原生渲染**：最终渲染的是平台原生的 UI 组件，这是其体验优于 WebView 方案的根本原因。
*   **持续演进**：新架构（Fabric）通过允许同步通信和更紧密的集成，正在解决旧架构的瓶颈，迈向更高的性能巅峰。

理解这个原理，有助于你写出性能更好的 RN 应用，并能更深入地理解其优势和局限性。

## Metro概念

### 一句话概括

**Metro** 是 React Native 的官方 JavaScript 打包器。它的核心作用是**将你的所有 JavaScript 代码、资源（如图片、字体）以及依赖项转换、打包成一个或多个 bundle 文件**，以便在移动设备上运行。

你可以把它理解为 React Native 世界的 **Webpack**（用于 Web 开发），但它是专门为 React Native 的需求而设计和优化的。

------

### 为什么需要 Metro？

在传统的 Web 开发中，浏览器可以直接下载并解析单个 `.js`文件。但在 React Native 中：

1. 

   **没有 DOM/BOM**：React Native 运行在一个完全不同的环境中（JavaScriptCore on iOS, Hermes/JavaScriptCore on Android），不能直接执行标准的浏览器 JavaScript。

2. 

   **代码包必须预先构建**：所有的 JS 逻辑都必须被预先“编译”和“打包”好，然后随 App 一起分发到用户的设备上。

3. 

   **需要特定格式**：打包后的代码需要符合 React Native 运行时的要求。

4. 

   **开发效率**：在开发模式下，需要支持热重载、快速增量更新等特性，以提升开发体验。

Metro 就是为了解决这些问题而生的。

------

### Metro 的核心功能和工作流程

Metro 的工作流程可以简化为以下几个关键步骤：

#### 1. 入口点解析

Metro 从一个指定的入口文件（通常是 `index.js`或 `App.js`）开始，分析你的代码结构。

#### 2. 依赖图构建

它会递归地分析所有 `import`/`require`语句，**构建一个完整的依赖关系图**。这个图包含了你的应用所需的所有模块。

#### 3. 转换

这是 Metro 的核心魔法之一。它对每一个模块进行转换：

- 

  **Babel 转换**：使用 Babel 将现代 JavaScript（如 ES6+、JSX）语法转换为 React Native 运行时能够理解的普通 JavaScript。例如，将 `<View>`转换为 `reactNative.createElement(View, ...)`。

- 

  **处理平台特定代码**：通过 **Platform API** 或文件扩展名（如 `.ios.js`, `.android.js`），Metro 可以为不同平台打包不同的代码，实现原生差异化。

- 

  **处理资源**：将图片等资源引用转换为模块，在运行时提供相应的 URI。

#### 4. 打包

将所有转换后的模块“捆绑”在一起，形成一个或几个大的 JavaScript 文件（Bundle）。在开发模式下，它通常生成一个内存中的 Bundle；在生产模式下，它会输出一个物理文件（如 `index.bundle`）。

#### 5. 启动开发服务器

Metro 会启动一个本地开发服务器，负责向 React Native 应用（通过 Metro Runtime）提供这个 Bundle 文件，并处理后续的 HMR（热模块替换）请求。

------

### Metro 的关键特性

- 

  **极速增量构建**：Metro 的设计初衷就是快。它通过缓存机制，只重新构建发生变化的部分，使得在开发过程中修改代码后，几乎能立即看到效果。

- 

  **可插拔的转换器**：默认使用 Babel，但你也可以集成其他转换器（如 TypeScript、Flow）。

- 

  **HMR**：支持热重载，让你在不丢失应用状态的情况下刷新代码变更，极大提升开发效率。

- 

  **内联资源**：可以将小图片等资源直接转换为 Base64 字符串并内联到 JS Bundle 中，减少网络请求。

- 

  **与 React Native DevTools 深度集成**：为调试工具提供了必要的支持。

------

### 日常开发中与 Metro 的交互

作为 React Native 开发者，你会经常通过以下命令与 Metro 打交道：

- 

  **启动开发服务器**：

  ```
  npx react-native start
  # 或者
  npx react-native start --reset-cache # 当遇到奇怪的缓存问题时，可以重置缓存
  ```

  这个命令就是启动 Metro Bundler。

- 

  **运行应用在模拟器/真机**：

  ```
  npx react-native run-android
  npx react-native run-ios
  ```

  这些命令在背后会先启动 Metro（如果尚未启动），然后编译并安装原生代码，最后从 Metro 服务器获取 JS Bundle 来运行应用。

- 

  **生产环境打包**：

  当你准备发布应用时，会使用如下命令生成离线 Bundle：

  ```
  # 示例命令，具体参数请参考文档
  npx react-native bundle --platform android --dev false --entry-file index.js --bundle-output android/app/src/main/assets/index.android.bundle --assets-dest android/app/src/main/res/
  ```

  这个过程同样由 Metro 完成，但它会生成一个优化过的、用于生产环境的 Bundle。

------

### 常见问题与解决

- 

  **Metro 端口被占用**：错误信息通常为 `Port 8081 already in use`。这通常是因为之前的 Metro 进程没有正确关闭。可以通过 `npx react-native start --port=8088`换一个端口，或者找到并结束占用端口的进程。

- 

  **缓存问题**：有时候代码改了但没生效，可以尝试：

  ```
  # 方法1：在命令行中重置缓存并重启
  npx react-native start --reset-cache
  
  # 方法2：在应用中摇动手机 -> Dev Settings -> 选择 “Dev Settings” -> “Reset Content and Settings”
  # 或者直接删除应用重新安装。
  ```

### 总结

| 方面           | 描述                                                         |
| -------------- | ------------------------------------------------------------ |
| **角色**       | React Native 的 JavaScript 打包器和开发服务器。              |
| **类比**       | 类似于 Webpack、Vite，但是为 RN 定制。                       |
| **核心任务**   | 转换（JSX, ESNext）、打包、服务 Bundle。                     |
| **关键优势**   | 速度快（增量构建）、支持 HMR、与 RN 生态无缝集成。           |
| **开发者接触** | 通过 `react-native start`和 `run-android/run-ios`命令间接使用。 |

简单来说，**只要你在使用 React Native，你就在使用 Metro**。它是连接你的 JavaScript 源代码和最终在手机上运行的程序之间的关键桥梁。

## 开发环境配置

>提示：在 Ubuntu22.04.5或者在Windows11上配置开发环境。
>

安装 `Node v22.21.1`

```sh
# Ubuntu
sudo dcli nodejs install

# Window11到nodejs官网下载msi安装
```

搭建 `Android Studio` 开发环境参考本站 [链接](/android/README.html#ubuntu22-04-5)

创建 `demorn1` 项目

```sh
npx @react-native-community/cli init demorn1 --version 0.72.0
```

- `--version 0.72.0` 表示 `React Native` 版本为 `0.72.0`。

导出 `ANDROID_HOME` 环境变量

```sh
# Ubuntu
export ANDROID_HOME=/home/dexterleslie/Android/Sdk

# Windows11配置如下
ANDROID_HOME=C:\Users\dexterleslie\AppData\Local\Android\Sdk
```

安装JDK17

使用 `Android` 模拟器运行项目

>提示：
>
>- 如果执行命令过程中失败，则重复执行多次。
>- 命令下载完毕react native依赖后，禁用android studio中配置的网络代理，否则运行RN应用会报错Unable to load script...。

```sh
npx react-native run-android

# 上面命令运行成功后，之后启动应用可以使用下面命令而不会弹出命令行窗口
npx react-native start
```

