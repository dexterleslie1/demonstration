## 移动端`Android`、`iOS`、各种小程序的跨平台应用开发主流解决方案有哪些呢？

移动端Android、iOS及各种小程序的跨平台应用开发主流解决方案主要包括以下几种：

1. React Native：
   - **特点**：使用JavaScript和React编写代码，生成原生Android和iOS组件。React Native拥有丰富的生态系统和大量的第三方库，社区活跃，开发者众多。
   - **适用场景**：适合有JavaScript基础的团队，能快速迭代开发，适用于社交App等需要快速实现界面布局与交互逻辑的应用。
2. Flutter：
   - **特点**：使用Dart语言，由Google开发的跨平台框架，可以生成Android和iOS的应用。Flutter高度自定义UI，无需依赖平台组件，性能接近原生，适合复杂动画和高帧率应用。
   - **适用场景**：适合需要快速构建跨平台移动应用程序，尤其是注重UI体验和动画效果的应用，如游戏化学习App。
3. Taro：
   - **特点**：一个开放式跨端跨框架解决方案，支持使用React、Vue、Nerv等框架来开发微信、京东、百度、支付宝、字节跳动、QQ小程序、H5、React Native等应用。Taro提供了丰富的API和工具，可以访问底层操作系统的功能。
   - **适用场景**：适合熟悉React、Vue等前端框架的开发者，需要开发多端应用，尤其是小程序和H5应用。
4. uni-app：
   - **特点**：使用Vue.js开发，开发者编写一套代码，可发布到iOS、Android、Web（响应式）、以及各种小程序（微信、支付宝、百度、头条、飞书、QQ、快手、钉钉、淘宝）、快应用等多个平台。uni-app在跨平台的过程中，不牺牲平台特色，可优雅地调用平台专有能力。
   - **适用场景**：适合需要同时支持多种平台，尤其是小程序和Web平台的应用开发。
5. Xamarin：
   - **特点**：微软推出的跨平台开发框架，使用C#和.NET，适用于Android、iOS和Windows。Xamarin共享大量代码，特别是逻辑层和数据层，完整支持原生API，便于与原生代码交互。
   - **适用场景**：适合已经精通.NET技术栈的团队，需要开发高性能的跨平台移动应用。
6. 小程序跨平台框架（如FinClip、Taro、kbone等）：
   - **特点**：这些框架允许开发者使用一套代码基于小程序框架构建应用，并在不同的平台上运行。它们提供了相应的开发工具和框架，开发者可以使用统一的开发语言（如JavaScript）和技术栈进行开发。
   - **适用场景**：适合需要开发小程序并希望在不同平台上运行的应用。

## uni-app和uni-app x区别

**uni-app x 是 uni-app 的升级版、强化版，是一个技术代际的飞跃**。

它们之间的关系可以类比为：

- **uni-app** 类似于 **JavaScript**
- **uni-app x** 类似于 **TypeScript**（在语法和性能上更激进）

下面我们从几个核心维度进行详细对比。

------

### 核心区别一览表

| 特性维度     | uni-app (传统uni-app)             | uni-app x (新一代)                               | 说明                                                         |
| ------------ | --------------------------------- | ------------------------------------------------ | ------------------------------------------------------------ |
| **开发语言** | **JavaScript/TypeScript**         | **UTS**                                          | 最根本的区别。JS是动态语言，UTS是静态类型语言。              |
| **渲染引擎** | **Webview渲染** 和 **小程序逻辑** | **原生渲染**                                     | uni-app x 抛弃了Webview，性能体验与原生App无异。             |
| **性能体验** | 接近小程序，有Webview性能天花板   | **极致性能**，与原生App（如Java、Swift开发）一致 | uni-app x 的渲染和逻辑执行效率都远高于Webview。              |
| **平台支持** | **全端**（App、H5、各家小程序）   | **目前主要支持 App**，后续逐步支持小程序         | uni-app 生态更成熟，uni-app x 是未来方向，先从App突破。      |
| **语法风格** | 类Vue 2/3 选项式或组合式API       | **类Vue 3 组合式API**，**强类型**                | uni-app x 强制使用TypeScript风格的强类型，开发更严谨。       |
| **DOM/BOM**  | **支持**                          | **不支持**                                       | uni-app x 没有浏览器环境，不能使用`document`、`window`等对象。 |
| **发行版**   | 稳定版，已大规模应用              | **Alpha/Beta测试版**，不断迭代中                 | uni-app 可用于正式生产，uni-app x 建议尝新和未来项目准备。   |

------

### 详细解析

#### 1. 开发语言：JavaScript/TS vs UTS

- **uni-app**：使用标准的JavaScript或TypeScript进行开发。开发者非常熟悉，生态庞大，但JS是动态脚本语言，在性能和类型安全上存在固有短板。
- **uni-app x**：使用 **UTS** 语言。UTS 是一种静态类型语言，它的语法类似 TypeScript，但最终会被编译成不同平台上的原生语言。 在 Android 上，UTS 编译为 **Kotlin**。 在 iOS 上，UTS 编译为 **Swift**。 这意味着你的代码不再是运行在一个解释器里，而是直接变成了原生代码，从而获得了极高的运行效率。

**示例代码对比：**

```
// uni-app (JavaScript)
export default {
  data() {
    return {
      message: 'Hello World',
      count: 0
    }
  },
  methods: {
    addCount() {
      this.count++
      console.log(this.message + this.count)
    }
  }
}
// uni-app x (UTS - 类Vue 3组合式API)
<script setup lang="uts">
import { ref } from 'vue'

const message = ref<string>('Hello World') // 必须声明类型 string
const count = ref<number>(0) // 必须声明类型 number

const addCount = () => {
  count.value++ // 注意需要使用 .value 访问ref的值
  console.log(message.value + count.value.toString())
}
</script>
```

从代码可以看出，uni-app x 强制类型声明，这减少了运行时错误，提高了代码质量。

#### 2. 渲染引擎：Webview vs 原生渲染

- **uni-app**：在App端，默认使用**Webview渲染**。即界面是由系统浏览器内核渲染的，类似于一个内置的Chrome浏览器。这带来了跨平台一致性，但性能有上限，体验与原生界面有可感知的差异（尤其是在动画复杂度高的场景）。
- **uni-app x**：在App端，使用**原生渲染**。编译器会将Vue模板直接编译为原生的UI组件（如Android的TextView、iOS的UILabel）。因此，其渲染性能、动画流畅度和用户体验与用Kotlin或Swift直接开发的应用完全一致。

#### 3. 生态与平台支持

- **uni-app**：非常成熟，支持发布到iOS、Android、Web（H5）、以及微信、支付宝、百度等所有主流小程序平台。拥有庞大的插件市场。
- **uni-app x**：**当前阶段主要专注于App**。它的目标是未来支持所有平台，但目前小程序和Web的支持还在规划或开发中。它的插件生态也在逐步建设中。

### 如何选择？

| 场景                           | 推荐技术                   | 理由                                                 |
| ------------------------------ | -------------------------- | ---------------------------------------------------- |
| **需要发布到H5或小程序**       | **uni-app**                | uni-app x 目前尚不支持，uni-app 是唯一且成熟的选择。 |
| **开发追求极致性能的App**      | **uni-app x**              | 原生渲染和原生语言编译带来的性能优势是决定性的。     |
| **已有uni-app项目维护**        | **继续使用 uni-app**       | 稳定第一，无需迁移。                                 |
| **新项目，主要目标是App**      | **强烈建议评估 uni-app x** | 这是技术趋势，能获得更好的性能和长期支持。           |
| **团队熟悉JS/TS，快速上手**    | **uni-app**                | 学习成本更低，生态更完善。                           |
| **团队追求代码质量和类型安全** | **uni-app x**              | 强类型和现代Vue 3语法有助于构建大型、可维护的应用。  |

### 总结

**uni-app** 是一个优秀的**跨端框架**，平衡了开发效率和性能，尤其在小程序和H5领域是事实标准。

**uni-app x** 是DCloud公司的一次重大技术革新，它不仅仅是一个框架升级，更是一次**架构革命**。它旨在解决uni-app在App端性能天花板的问题，让开发者使用一套代码就能开发出与原生应用性能无异的应用程序。

可以理解为：**uni-app x 是 uni-app 在 App 端的终极形态和未来方向。** 但目前由于平台支持度的差异，两者会根据项目需求并存很长一段时间。

## `Android`打包和运行原理

uni-app 在 Android 平台的打包和运行原理主要涉及代码封装、WebView 容器加载以及原生功能调用，具体如下：

### 打包原理

- **代码封装与资源生成**：开发者使用 HBuilderX 编写 uni-app 代码后，通过内置打包工具或命令行工具将代码打包成静态资源文件。在打包过程中，会处理项目中的页面、组件、样式、配置文件等，生成可在 Android 设备上运行的资源。例如，项目中的 `pages` 目录下的页面文件、`static` 目录下的静态资源等都会被整合到打包资源中。
- **证书与包名配置**：在打包 Android 应用时，需要配置 Android 包名、证书别名、证书私钥密码等信息。这些信息用于生成数字证书对 APK 文件进行签名，以表明开发者身份。可以从 DCloud 开发者中心生成 Android 云端证书，获取包名、证书密码、别名等，也可自行使用 JRE 环境中的 keytool 命令生成证书。
- **离线 SDK 与资源替换**：若采用本地打包方式，需下载 Android 离线 SDK 并解压。在 HBuilderX 中生成本地打包资源后，将这些资源复制到 SDK 的特定目录下，替换相关文件。同时，要修改关键配置文件，如 `dcloud_control.xml`（替换 AppID）、`AndroidManifest.xml`（修改应用名称、权限配置等）、`build.gradle`（填写 applicationId，需与证书一致，设置 versionCode 和 versionName 与 HBuilderX 项目版本一致）。

### 运行原理

- **原生 WebView 容器加载**：uni-app 在 Android 平台上借助原生 WebView 容器实现跨平台开发。Android 系统提供了 WebView 组件，uni-app 将前端代码打包成的静态资源文件通过 WebView 加载，从而实现应用程序的展示和交互。例如，用户在手机上打开 uni-app 开发的 Android 应用时，应用会启动 WebView 容器，加载打包好的静态资源，呈现出应用的界面。
- **逻辑层与视图层分离**：逻辑层主要负责储存数据和执行业务逻辑，视图层则负责页面渲染。当页面加载时，联网和逻辑运算在逻辑层进行，然后将数据传递给视图层进行渲染。这种分离的设计使得 uni-app 能够在不同的平台上实现高效的运行，但数据传递和事件处理过程中会存在一定的通信损耗，开发者需注意减少不必要的数据传递和事件处理，以提高应用程序的性能和用户体验。
- **原生功能调用**：uni-app 通过原生引擎提供的接口来调用硬件设备和系统功能。例如，调用设备的摄像头、传感器等硬件功能，或者访问系统的文件系统、通知等功能。开发者可以使用 uni-app 提供的丰富 API 和插件来方便地调用这些原生功能，进一步提高了应用程序的性能和功能。



## `iOS`打包和运行原理

uni-app 在 iOS 平台的打包和运行原理涉及代码转换、原生容器适配、证书签名及资源整合，以下是具体说明：

### 打包原理

1. 代码转换与资源整合
   - uni-app 基于 Vue.js 开发，通过 HBuilderX 工具将项目代码转换为 iOS 可识别的格式。打包过程中，项目中的页面、组件、样式、配置文件等会被整合成静态资源文件。例如，`pages` 目录下的页面文件、`static` 目录下的静态资源等都会被处理并打包。
   - 同时，uni-app 会利用 Web 标准技术（HTML、CSS 和 JavaScript）构建应用的界面和逻辑，并借助原生插件扩展功能，如调用摄像头、获取地理位置等。
2. 证书与配置文件准备
   - 打包 iOS 应用需要注册苹果开发者账号，并获取相应的证书和描述文件。开发者需在苹果开发者官网申请开发者证书和描述文件，用于开发测试真机调试和发布。
   - 在 uni-app 项目中，需配置 `manifest.json` 文件，设置应用名称、包名（Bundle ID）、版本号、图标等信息，确保与苹果开发者账号中的配置一致。
3. 编译与打包
   - 使用 HBuilderX 工具选择 iOS 平台进行打包。HBuilderX 会调用 Xcode 的编译工具，将整合后的静态资源文件编译成 iOS 可执行文件。
   - 在编译过程中，会将 uni-app 的代码转换为 iOS 平台能够理解和运行的代码，并适配 iOS 的运行时环境。
   - 编译完成后，会生成 `.ipa` 安装包文件，该文件包含了应用的所有代码和资源，可用于在 iOS 设备上安装和运行。

### 运行原理

1. 原生容器加载
   - iOS 设备上，uni-app 应用通过原生容器（如 UIWebView 或 WKWebView）加载打包后的静态资源文件。原生容器负责渲染应用的界面，并处理与用户的交互。
   - 当用户打开 uni-app 应用时，原生容器会加载应用的入口文件，然后根据页面路由加载相应的视图和逻辑代码。
2. 逻辑层与视图层交互
   - 逻辑层使用 JavaScript 编写业务逻辑，与视图层进行交互。当用户与界面进行交互时，逻辑层会响应这些事件，并根据需要更新数据和视图。
   - 视图层基于 Vue.js 的模板语法和组件机制构建，根据逻辑层传递的数据进行渲染，展示出用户界面。
3. 原生功能调用
   - uni-app 允许开发者使用原生插件来扩展功能，这些插件可以提供诸如调用摄像头、获取地理位置等原生能力。
   - 在运行时，当应用需要调用原生功能时，会通过原生插件与 iOS 系统的原生 API 进行交互，实现相应的功能。



## 桌面应用打包和运行原理

uni-app 打包桌面应用主要借助 Electron 框架，其原理是将 uni-app 开发的 H5 页面嵌入 Electron 的原生容器中运行，以下是具体说明：

### 打包原理

1. 前期准备
   - **安装相关工具**：在控制台安装 electron 和 electron-packager。例如使用 `cnpm install electron -g` 安装 electron，使用 `cnpm install electron-packager -g` 安装 electron-packager。
   - **修改配置文件**：修改 uni-app 的 `manifest.json` 文件，将运行的基础路径修改为 `./`，避免打包后出现白屏、读取不到资源的问题；去掉启用 https 协议，因为去掉 https 不影响请求后端的 https 协议，否则会出现网络无法加载的情况。
2. H5 打包
   - 通过 HBuilderX 工具，选择“发行”选项，然后依次选择“网站 - H5 手机版”或“网站 - PC 版”，配置相关参数，如域名、端口等，点击“生成”按钮，等待打包完成。
3. 创建配置文件
   - **package.json**：在 H5 文件夹下新建 `package.json` 文件，配置应用的基本信息，如名称、版本号、主文件等。
   - **main.js**：新建 `main.js` 文件，使用 Electron 的 API 创建浏览器窗口，并加载应用的入口文件（如 `index.html`）。
4. 打包桌面应用
   - 使用命令行进入 H5 目录，输入打包命令。例如 `electron-packager . helloWorld --platform=win32 --arch=x64 --icon=computer.ico --out=./out --asar --app-version=1.0.0 --overwrite --ignore=node_modules --electron-version 8.2.1`，各参数有特定含义，如 `helloWorld` 是将要生成的 exe 文件的名称，`--platform=win32` 确定了要构建的平台为 Windows 等。

### 运行原理

- **Electron 容器加载**：打包后的桌面应用通过 Electron 提供的原生容器来运行。Electron 结合了 Chromium 和 Node.js，允许使用 Web 技术（HTML、CSS、JavaScript）来构建跨平台的桌面应用程序。当用户启动应用时，Electron 会创建一个浏览器窗口，并加载应用的入口文件。
- **逻辑层与视图层交互**：应用的逻辑层使用 JavaScript 编写，与视图层进行交互。视图层基于 uni-app 开发的 H5 页面，通过 Electron 的浏览器窗口进行渲染。当用户与界面进行交互时，逻辑层会响应这些事件，并根据需要更新数据和视图。
- **调用原生功能**：Electron 提供了丰富的 API，允许桌面应用调用操作系统的原生功能，如文件系统访问、系统通知、托盘图标等。开发者可以在应用中使用这些 API 来增强应用的功能和用户体验。



## 小程序打包和运行原理

uni-app 小程序打包和运行原理如下：

### 打包原理

1. 代码转换
   - uni-app 编译器（基于 Webpack 或 Vite）负责将开发者编写的 uni-app 代码（如 `.vue` 文件、`.js` 文件、样式文件等）转换为目标平台（微信小程序）原生可识别的代码。
   - **模板编译**：将 `.vue` 文件中的 `<template>` 部分编译为微信小程序的 `.wxml` 文件，uni-app 组件会被映射为微信小程序对应的内置组件。
   - **样式编译**：将 `.vue` 文件中的 `<style>` 部分和独立的样式文件（如 `.css`、`.scss`、`.less` 等，取决于项目配置）编译为微信小程序的 `.wxss` 文件，处理 CSS 预处理器，自动转换尺寸单位（如 px 转换为 rpx，如果配置），处理样式导入等。
   - **脚本编译**：将 `.vue` 文件中的 `<script>` 部分和独立的 `.js` 文件编译为微信小程序的 `.js` 文件，uni-app 的 `uni.*` API 调用会被替换为微信小程序对应的 `wx.*` API 调用，处理模块化（如 CommonJS 或 ES Module），生成小程序可执行的 JS 代码。
2. 配置处理
   - 处理 `pages.json` 和 `manifest.json` 文件，生成微信小程序所需的 `app.json`、页面 `.json` 文件等配置。特别是 `manifest.json` 中的微信小程序配置部分（如 AppID）。
3. 资源处理
   - 复制、压缩、转换静态资源（图片、字体等）。
4. 生成项目结构
   - 编译器在指定的输出目录下生成一个标准的微信小程序项目结构，这个目录包含 `.wxml`、`.wxss`、`.js`、`.json` 文件以及静态资源，可以直接导入到微信开发者工具中。

### 运行原理

1. 双线程架构
   - 微信小程序运行在独特的双线程架构中（逻辑层和视图层），uni-app 打包后的小程序也遵循这一架构。逻辑层负责处理业务逻辑，基于 Vue.js 运行时，但在不同平台有不同实现；视图层负责界面渲染，在不同平台调用不同的 UI 渲染引擎。
2. 通信机制
   - 通信层连接逻辑层和渲染层，处理数据同步和事件传递。页面加载时，联网和逻辑运算在逻辑层进行，然后会传递数据给视图层渲染，这种通信有损耗。同样，在视图层操作时，比如拖动页面，要实时传递事件给逻辑层接收，也是有损耗的。
3. API 调用
   - 平台 API 抽象层统一封装各平台原生 API，提供一致的调用方式。在编译到微信小程序时，uni-app 的 `uni.*` API 会被替换为微信小程序对应的 `wx.*` API 调用，开发者可以通过这些 API 调用微信小程序的各种功能。

## uniapp+App调试方法

如下：

- 把uniapp运行到浏览器Chrome中，这样可以直接使用Chrome开发者工具查看日志、样式、HTML、网络等。
- 参考 https://uniapp.dcloud.net.cn/tutorial/debug/debug-app.html 使用HBuilderX内置的调试器查看日志、Elements、JS断点调试等。
- 使用Chrome开发者工具Remote Debugging功能调试Android手机中的网页 https://blog.jiatool.com/posts/chrome_remote_debug/。

## uniapp+App调试方法实战

借助本站示例 https://gitee.com/dexterleslie/demonstration/tree/main/demo-uni-app/demo-style-scss 辅助实验。

结论如下：

- 把uniapp运行到浏览器Chrome中：能够完整地调试日志、样式、查看元素、监控网络、支持JS断点调试。
- 使用HBuilderX内置的调试器：能够查看日志、不能查看元素（因为只支持nvue元素）、支持JS断点调试、不支持监控网络。
- 使用Chrome开发者工具Remote Debugging：只支持元素和样式调试，不支持日志、网络调试。

## `App`基座

理解了“uni-app基座”才能真正理解uni-app是如何实现跨端的。

简单来说，**uni-app基座（简称App基座）可以理解为一个原生应用程序的“容器”或“引擎”**。你的uni-app代码（Vue组件、JS等）最终需要被这个“基座”加载和解析，才能变成一个真正的、能在手机上运行的App。

---

### 一个生动的比喻

把开发一个App想象成造一辆车：
*   **你的uni-app代码（Vue/JS/CSS）：** 相当于这辆车的**设计图、外壳、内饰和电路规则**。它定义了App长什么样（外壳内饰），有什么功能（电路规则）。
*   **uni-app基座：** 相当于这辆车的**底盘、发动机和变速箱**。它决定了车能不能跑起来、如何驱动、以及基本的行驶能力。

没有基座，你的代码就只是一张设计图，无法真正运行。基座为你的代码提供了最基础的运行环境。

---

### 基座的具体构成

这个“基座”本质上是一个**原生应用程序**，它内部集成了两个最关键的东西：

1.  **JS引擎（如JavaScriptCore或V8）：** 负责解释和执行你用JavaScript/Vue.js编写的所有业务逻辑。
2.  **渲染引擎：** 不是一个完整的WebView，而是一个**优化过的、精简版的渲染器**。它负责将你的Vue组件标签（如 `<view>`, `<text>`）翻译成原生的UI组件（iOS的UIView，Android的android.view）。这正是uni-app性能优于纯WebView方案（如PhoneGap/Cordova）的关键。

**所以，当你写一个 `<view>` 标签时，在App端，它最终不会渲染成浏览器中的HTML Div，而是直接渲染成手机操作系统原生的一个视图控件。**

---

### 基座的三种形态

基座主要分为三种形态，对应不同的开发阶段：

#### 1. 开发阶段的【自定义调试基座】
*   当你用HBuilderX真机运行时，你的代码会被编译并注入到一个特殊的“自定义调试基座”中。
*   这个调试基座包含了所有官方SDK的功能（如地图、支付、推送等），方便你在开发时进行调试。
*   它通常比标准基座大，因为它包含了调试信息和所有原生模块。

#### 2. 发布时的【正式基座】
*   当你开发完成，使用HBuilderX进行**云打包或本地打包**时，你的代码会和一个“正式基座”合并，生成最终的安装包（.apk或.ipa）。
*   这个正式基座是精简过的，移除了调试信息，体积更小，性能更优。

#### 3. 原生插件开发的【SDK基座】
*   如果你需要开发自定义的原生插件，你需要下载App离线打包SDK。这个SDK本质上就是一个不包含业务代码的“空白基座”，你可以在Android Studio或Xcode中引用它，并用自己的原生工程来打包。

---

### 为什么需要基座？它的核心作用总结

1.  **提供跨端能力：** 基座是连接JavaScript框架和原生平台的桥梁。它通过一套统一的JS API，让你可以用同一套代码调用不同平台（iOS、Android）的原生功能。
2.  **保证原生体验和性能：** 如上所述，它的渲染引擎直接将Vue节点映射为原生组件，而不是在WebView里渲染，从而获得了接近原生App的流畅体验。
3.  **集成原生功能：** 基座内部已经预先集成了大量常用的原生模块，如地图、支付、推送、传感器等。你只需要通过JS API简单调用即可，无需自己从头编写原生代码。

### 总结

**uni-app基座是一个由DCloud提供的、用原生语言（Java/OC/C++）编写的运行时环境，它负责解析、渲染和执行你的uni-app项目代码，并将其转换成真正的原生应用程序。** 它是uni-app实现“一套代码，多端发布”这一宏伟目标的工程技术基石。



## `HBuilderX`

### 安装

>提示：在 `Windows11` 中安装 `HBuilderX`。

通过 [链接](https://www.dcloud.io/hbuilderx.html) 下载最新版本 `HBuilderX`，解压后即可运行。



## 创建并运行第一个`App`

步骤如下：

1. 在 `HBuilderX` 中点击 `文件` > `新建` > `项目`，左侧导航栏中选中 `uni-app`，选择 `默认模板` 类型项目，填写项目名称为 `test1`，点击 `创建` 按钮。
2. 参考本站 [链接](/android/README.html#有哪些模拟器呢) 安装并运行 `mumu` 安卓模拟器。
3. 在 `HBuilderX` 中点击 `运行` > `运行到手机或模拟器` > `Android模拟器端口设置` 功能并修改端口为 `7555`。
4. 打开 `App.vue` 文件（作为运行 `App` 的入口），点击 `运行` > `运行到手机或模拟器` > `运行到Android App基座`，稍等一会儿后弹出窗口会自动检查到并显示本地的`mumu`安卓模拟器，点击 `运行` 即可自动安装并运行 `uni-app` 到安卓模拟器中（第一次运行 `HBuilderX` 需要下载相关插件，所以耐心等待）。

## 运行到小程序 - 微信开发者工具

参考本站[链接](/tencent/README.html#小程序-开发环境配置)安装微信小程序开发者工具。

安装完毕后运行HBuilderX并点击`运行`>`运行到小程序模拟器`>`微信开发者工具`功能会自动调用微信小程序开发者模拟器运行小程序。

## uni-app项目依赖安装

>说明：uni-app 不会自动安装 package.json 中的依赖，需要我们手动安装。

```sh
# 在项目的根目录中执行下面命令即可
npm install --registry=https://registry.npmmirror.com
```



## `UI`组件 - `view`

>说明：view组件是 uni-app x 最基本的视图容器，它的作用类似于HTML中的div标签。
>
>[view | uni-app x](https://doc.dcloud.net.cn/uni-app-x/component/view.html)

在 uni-app 中，**`<view>` 是最基础的容器组件**，它相当于 Web 开发中的 `<div>`，但具有更强大的跨平台特性。以下是它的核心特点和工作原理：

---

### 一、`<view>` 的本质：跨平台的 UI 基石
1. **跨平台抽象层**  
   - 你写的同一个 `<view>` 标签，在不同平台会被编译为不同的原生组件：
     - **Web 平台**：渲染为 HTML 的 `<div>` 标签
     - **iOS 平台**：渲染为 `UIView`（原生视图）
     - **Android 平台**：渲染为 `android.view.View`
     - **小程序平台**：编译为对应的小程序标签（如微信小程序的 `<view>`）

2. **设计目的**  
   uni-app 通过 `<view>` 统一了各平台的视图容器差异，让你无需关心底层实现差异。

---

### 二、与 Web 开发 `<div>` 的关键区别
| 特性         | uni-app `<view>`                     | HTML `<div>`                   |
| ------------ | ------------------------------------ | ------------------------------ |
| **渲染方式** | 可能编译为原生组件（App 端）         | 始终是浏览器 DOM 元素          |
| **性能**     | App 端无 DOM 层级，渲染更快          | 受浏览器 DOM 渲染机制限制      |
| **CSS 支持** | 支持大部分样式，但某些属性有平台差异 | 支持所有 CSS 属性              |
| **事件系统** | 使用 `@tap` 等跨平台事件             | 使用 `onclick` 等原生 DOM 事件 |

---

### 三、`<view>` 的核心能力
1. **布局容器**  
   支持所有 Flex 布局模型，示例：
   ```html
   <view style="display: flex; justify-content: center;">
     <text>居中内容</text>
   </view>
   ```

2. **触摸交互**  
   支持跨平台手势事件：
   ```html
   <view @tap="handleTap" @longpress="handleLongPress">
     点击/长按我
   </view>
   ```

3. **滚动容器**  
   可配合 `scroll-view` 实现复杂滚动（但注意 App 端滚动性能优于 WebView）

4. **平台特性扩展**  
   通过条件编译实现平台差异化：
   ```html
   <!-- #ifdef APP-PLATFORM -->
   <view class="app-special-style"></view>
   <!-- #endif -->
   ```

---

### 四、App 端的特殊优化
当运行在 App 平台时：
1. **原生渲染优势**  
   - 没有 WebView 的 DOM 层级限制
   - 内存占用更低（实测比 WebView 减少 30%-50%）
   - 滚动流畅度接近原生（60 FPS）

2. **样式限制注意**  
   - 不支持 `position: fixed`
   - `z-index` 在不同平台表现可能不同
   - 推荐使用 Flex 布局代替浮动布局

---

### 五、开发建议
1. **性能优化**  
   - 避免超过 10 层嵌套（App 端仍有视图树复杂度限制）
   - 大数据列表使用 `v-for` 时务必加 `:key`

2. **兼容性技巧**  
   ```css
   /* 解决 iOS 平台点击高亮问题 */
   view {
     -webkit-tap-highlight-color: transparent;
   }
   ```

3. **调试方法**  
   在 HBuilderX 中使用「调试基座」时，可通过 `uni.getSystemInfo()` 查看当前渲染引擎类型。

---

### 总结
uni-app 的 `<view>` 是一个：
- **跨平台**的 UI 容器
- **高性能**的渲染基础
- **统一**的开发体验接口

它既保留了 Web 开发的灵活性，又通过原生渲染获得了 App 端的性能优势，是 uni-app 跨平台体系的核心组件之一。

### 示例

>详细用法请参考本站 [示例](https://gitee.com/dexterleslie/demonstration/tree/main/demo-uni-app/demo-view)

`index.vue`

```vue
<template>
	<view class="container">
		<view>A</view>
		<view>B</view>
		<view>C</view>
	</view>
</template>

<script>
	export default {
		data() {
			return {
			}
		},
		onLoad() {

		},
		methods: {

		}
	}
</script>

<style>
	.container {
		display: flex;
		flex-direction: row;
		justify-content: space-around;
	}
	
	.container view {
		width: 100px;
		line-height: 100px;
		text-align: center;
	}
	
	.container view:nth-child(1) {
		background-color: red;
	}
	
	.container view:nth-child(2){
		background-color: green;
	}
	
	.container view:nth-child(3) {
		background-color: greenyellow;
	}
</style>

```

## UI组件 - button

>[button | uni-app官网](https://uniapp.dcloud.net.cn/component/button.html)
>
>详细用法请参考本站示例：https://gitee.com/dexterleslie/demonstration/tree/main/demo-uni-app/demo-button

```vue
<template>
	<view class="content">
		<button type="default" @click="handleClick()">点击</button>
	</view>
</template>

<script>
	export default {
		data() {
			return {
				title: 'Hello'
			}
		},
		onLoad() {

		},
		methods: {
			handleClick() {
				console.log('Hello World!')
			}
		}
	}
</script>
```

## API - 模态弹窗modal

>说明：显示模态弹窗，可以只有一个确定按钮，也可以同时有确定和取消按钮。类似于一个API整合了 html 中：alert、confirm。
>
>[uni.showModal(options) | uni-app x](https://doc.dcloud.net.cn/uni-app-x/api/modal.html)
>
>详细用法请参考本站示例：https://gitee.com/dexterleslie/demonstration/tree/main/demo-uni-app/demo-modal

```vue
<template>
	<view class="content">
		<button type="default" @click="handleClick()">un.showModal</button>
		<button type="default" @click="handleClickWithoutCancel()">un.showModal没有cancel按钮</button>
	</view>
</template>

<script>
	export default {
		data() {
			return {
				title: 'Hello'
			}
		},
		onLoad() {

		},
		methods: {
			handleClick() {
				uni.showModal({
					title: '提示',
					content: '提示内容',
				})
			},
			handleClickWithoutCancel() {
				uni.showModal({
					title: '提示',
					content: '提示内容',
					showCancel: false,
				})
			}
		}
	}
</script>
```

## API - 加载框loading

>说明：显示 loading 提示框, 需主动调用 uni.hideLoading 才能关闭提示框。它是一个悬浮弹出的、非组件内嵌的加载中提示。
>
>[uni.showLoading(options) | uni-app x](https://doc.dcloud.net.cn/uni-app-x/api/loading.html)
>
>详细用法请参考本站示例：https://gitee.com/dexterleslie/demonstration/tree/main/demo-uni-app/demo-loading

```vue
<template>
	<view class="content">
		<button type="default" @click="handleClick">uni.showLoading</button>
	</view>
</template>

<script>
	export default {
		data() {
			return {
				title: 'Hello'
			}
		},
		onLoad() {

		},
		methods: {
			handleClick() {
				uni.showLoading({
					title: '加载中...',
					mask: true
				})
				
				setTimeout(()=>{
					uni.hideLoading()
				}, 3000)
			}
		}
	}
</script>
```

## API - 轻提示toast

>说明：显示消息提示框
>
>[uni.showToast(options) | uni-app x | uni-app x](https://doc.dcloud.net.cn/uni-app-x/api/toast.html)
>
>详细用法请参考本站示例：https://gitee.com/dexterleslie/demonstration/tree/main/demo-uni-app/demo-toast

```vue
<template>
	<view class="content">
		<button type="default" @click="handleClickSuccess()">uni.showToast success</button>
		<button type="default" @click="handleClickError()">uni.showToast error</button>
		<button type="default" @click="handleClickFail()">uni.showToast fail</button>
		<button type="default" @click="handleClickException()">uni.showToast exception</button>
		<button type="default" @click="handleClickLoading()">uni.showToast loading</button>
	</view>
</template>

<script>
	export default {
		data() {
			return {
				title: 'Hello'
			}
		},
		onLoad() {

		},
		methods: {
			handleClickSuccess() {
				uni.showToast({
					title: '提示',
					icon: 'success',
					mask: true,
				})
			},
			handleClickError() {
				uni.showToast({
					title: '提示',
					icon: 'error',
					mask: true,
				})
			},
			handleClickFail() {
				uni.showToast({
					title: '提示',
					icon: 'fail',
					mask: true,
				})
			},
			handleClickException() {
				uni.showToast({
					title: '提示',
					icon: 'exception',
					mask: true,
				})
			},
			handleClickLoading() {
				uni.showToast({
					title: '提示',
					icon: 'loading',
					mask: true,
				})
			},
		}
	}
</script>
```

## API - 第三方服务登录 - getUserProfile

>说明：获取用户信息。每次请求都会弹出授权窗口，用户同意后返回 userInfo。
>
>[uni.login(OBJECT) | uni-app官网](https://uniapp.dcloud.net.cn/api/plugins/login.html#getuserprofile)
>
>详细用法请参考本站示例：https://gitee.com/dexterleslie/demonstration/tree/main/demo-uni-app/demo-getuserprofile

```vue
<template>
	<view class="content">
		<button type="default" @click="handleClick()">获取用户信息</button>
	</view>
</template>

<script>
	export default {
		data() {
			return {
				title: 'Hello'
			}
		},
		onLoad() {

		},
		methods: {
			handleClick() {
				uni.getUserProfile({
					desc: '获取用户信息',
					success: (res) => {
						console.log(res)
					},
					fail: (err) => {
						console.error(err)
					}
				})
			}
		}
	}
</script>
```

## 路由概念

### 一、路由系统概述

Uniapp的路由系统是**基于页面栈**的，与原生小程序的导航方式相似，而不是Vue Router的SPA模式。

#### 基本特性

- 

  每个页面都是独立的Vue实例

- 

  支持页面间的**跳转、传参、返回**

- 

  页面路径需要在 `pages.json`中配置

- 

  分为**应用级路由API**和**页面级路由API**

### 二、路由配置文件

#### `pages.json`结构

```
{
  "pages": [
    {
      "path": "pages/index/index",
      "style": {
        "navigationBarTitleText": "首页"
      }
    },
    {
      "path": "pages/detail/detail",
      "style": {
        "navigationBarTitleText": "详情页"
      }
    }
  ],
  "globalStyle": {
    "navigationBarTextStyle": "black",
    "navigationBarTitleText": "UniApp",
    "navigationBarBackgroundColor": "#F8F8F8"
  },
  "tabBar": {
    "color": "#7A7E83",
    "selectedColor": "#3cc51f",
    "list": [
      {
        "pagePath": "pages/index/index",
        "text": "首页"
      },
      {
        "pagePath": "pages/user/user",
        "text": "我的"
      }
    ]
  }
}
```

### 三、核心路由API

#### 1. **uni.navigateTo** - 保留当前页面跳转

```
// 基本跳转
uni.navigateTo({
  url: '/pages/detail/detail'
})

// 带参数跳转
uni.navigateTo({
  url: '/pages/detail/detail?id=1&name=uniapp'
})

// 对象参数（会自动编码为query）
uni.navigateTo({
  url: '/pages/detail/detail',
  success: (res) => {
    console.log('跳转成功', res)
  },
  fail: (err) => {
    console.log('跳转失败', err)
  }
})
```

#### 2. **uni.redirectTo** - 关闭当前页面跳转

```
uni.redirectTo({
  url: '/pages/login/login'
})

// 与 navigateTo 的区别：
// navigateTo: A → B (A在栈中保留)
// redirectTo: A → B (A从栈中移除)
```

#### 3. **uni.reLaunch** - 关闭所有页面跳转

```
uni.reLaunch({
  url: '/pages/index/index'
})

// 应用场景：登录后跳转首页，清空所有历史页面
```

#### 4. **uni.switchTab** - 切换Tab页面

```
uni.switchTab({
  url: '/pages/user/user'
})

// 注意事项：
// 1. 只能跳转到在tabBar配置的页面
// 2. 不能带参数
// 3. 会清空页面栈
```

#### 5. **uni.navigateBack** - 返回上一页

```
// 返回上一页
uni.navigateBack()

// 返回指定页数
uni.navigateBack({
  delta: 2  // 返回2层
})

// 返回首页
uni.navigateBack({
  delta: getCurrentPages().length - 1
})
```

### 四、页面传参与接收

#### 1. **URL传参**

```
// 发送参数
uni.navigateTo({
  url: '/pages/detail/detail?id=123&title=测试标题'
})

// 接收参数（在 detail.vue 中）
export default {
  onLoad(options) {
    // options 包含所有参数
    console.log(options.id)      // "123"
    console.log(options.title)   // "测试标题"
  }
}
```

#### 2. **复杂对象传参**

```
// 发送复杂数据
const product = {
  id: 1,
  name: '商品名称',
  price: 99.9
}

// 方法1：JSON序列化
uni.navigateTo({
  url: `/pages/detail/detail?data=${encodeURIComponent(JSON.stringify(product))}`
})

// 方法2：通过全局数据（适合大数据量）
uni.$globalData = product
uni.navigateTo({
  url: '/pages/detail/detail'
})

// 接收端
onLoad(options) {
  // 方法1接收
  if (options.data) {
    const product = JSON.parse(decodeURIComponent(options.data))
  }
  
  // 方法2接收
  const product = uni.$globalData
}
```

#### 3. **EventBus传参**（推荐用于组件间通信）

```
// 创建eventBus
Vue.prototype.$eventBus = new Vue()

// 发送页面
this.$eventBus.$emit('updateData', { data: 'test' })
uni.navigateTo({
  url: '/pages/detail/detail'
})

// 接收页面
onLoad() {
  this.$eventBus.$on('updateData', (data) => {
    console.log(data)
  })
}

onUnload() {
  this.$eventBus.$off('updateData')
}
```

### 五、页面生命周期与路由钩子

#### 页面生命周期

```
export default {
  // 页面加载
  onLoad(options) {
    console.log('页面加载，参数:', options)
  },
  
  // 页面显示
  onShow() {
    console.log('页面显示')
  },
  
  // 页面初次渲染完成
  onReady() {
    console.log('页面就绪')
  },
  
  // 页面隐藏
  onHide() {
    console.log('页面隐藏')
  },
  
  // 页面卸载
  onUnload() {
    console.log('页面卸载')
  },
  
  // 下拉刷新
  onPullDownRefresh() {
    console.log('下拉刷新')
    setTimeout(() => {
      uni.stopPullDownRefresh()
    }, 1000)
  },
  
  // 触底加载
  onReachBottom() {
    console.log('触底加载')
  },
  
  // 页面滚动
  onPageScroll(e) {
    console.log('页面滚动', e.scrollTop)
  }
}
```

### 六、路由相关方法

#### 1. **获取当前页面栈**

```
// 获取页面栈实例
const pages = getCurrentPages()

// 获取当前页面实例
const currentPage = pages[pages.length - 1]

// 获取当前页面路由
const route = currentPage.route
console.log('当前路由:', route)  // "pages/detail/detail"

// 获取当前页面参数
const options = currentPage.options
console.log('页面参数:', options)
```

#### 2. **获取页面路径和参数工具函数**

```
// utils/router.js
export default {
  // 获取当前页面实例
  getCurrentPage() {
    const pages = getCurrentPages()
    return pages[pages.length - 1]
  },
  
  // 获取当前页面参数
  getCurrentPageOptions() {
    const page = this.getCurrentPage()
    return page.options || {}
  },
  
  // 跳转前检查登录状态
  navigateToWithAuth(options) {
    const token = uni.getStorageSync('token')
    if (!token) {
      uni.navigateTo({
        url: '/pages/login/login'
      })
      return false
    }
    uni.navigateTo(options)
    return true
  },
  
  // 返回并刷新上一页
  navigateBackAndRefresh() {
    const pages = getCurrentPages()
    const prevPage = pages[pages.length - 2]
    if (prevPage && prevPage.$vm) {
      // 调用上一页的刷新方法
      prevPage.$vm.refreshData && prevPage.$vm.refreshData()
    }
    uni.navigateBack()
  }
}
```

### 七、路由守卫与权限控制

#### 1. **全局路由拦截**

```
// main.js
// 保存原始方法
const originalNavigateTo = uni.navigateTo
const originalRedirectTo = uni.redirectTo
const originalReLaunch = uni.reLaunch
const originalSwitchTab = uni.switchTab

// 需要登录的页面路径
const needLoginPages = [
  '/pages/user/user',
  '/pages/order/order'
]

// 重写 navigateTo
uni.navigateTo = function(options) {
  if (needLoginPages.includes(options.url.split('?')[0])) {
    if (!checkLogin()) {
      uni.navigateTo({
        url: '/pages/login/login'
      })
      return
    }
  }
  originalNavigateTo.call(this, options)
}

// 检查登录状态
function checkLogin() {
  return !!uni.getStorageSync('token')
}
```

#### 2. **页面内守卫**

```
// mixins/auth.js
export default {
  data() {
    return {
      requireAuth: false
    }
  },
  
  onLoad(options) {
    if (this.requireAuth && !this.checkAuth()) {
      this.redirectToLogin()
      return
    }
  },
  
  methods: {
    checkAuth() {
      return !!uni.getStorageSync('token')
    },
    
    redirectToLogin() {
      uni.redirectTo({
        url: '/pages/login/login?redirect=' + encodeURIComponent(this.$page.route)
      })
    }
  }
}
```

### 八、路由传参最佳实践

#### 1. **使用统一参数处理器**

```
// utils/params.js
export default {
  // 生成跳转URL
  generateUrl(path, params = {}) {
    if (!params || Object.keys(params).length === 0) {
      return path
    }
    
    const query = Object.keys(params)
      .map(key => {
        if (typeof params[key] === 'object') {
          return `${key}=${encodeURIComponent(JSON.stringify(params[key]))}`
        }
        return `${key}=${encodeURIComponent(params[key])}`
      })
      .join('&')
    
    return `${path}${path.includes('?') ? '&' : '?'}${query}`
  },
  
  // 解析参数
  parseParams(options) {
    const result = {}
    Object.keys(options).forEach(key => {
      const value = options[key]
      try {
        result[key] = JSON.parse(decodeURIComponent(value))
      } catch (e) {
        result[key] = value
      }
    })
    return result
  }
}
```

#### 2. **页面跳转封装**

```
// utils/navigate.js
import params from './params.js'

export default {
  // 带参跳转
  to(path, data = {}) {
    const url = params.generateUrl(path, data)
    uni.navigateTo({ url })
  },
  
  // 替换跳转
  replace(path, data = {}) {
    const url = params.generateUrl(path, data)
    uni.redirectTo({ url })
  },
  
  // 返回并传递数据
  back(data = {}, delta = 1) {
    const pages = getCurrentPages()
    const prevPage = pages[pages.length - 1 - delta]
    
    if (prevPage && prevPage.$vm && prevPage.$vm.onNavigateBack) {
      prevPage.$vm.onNavigateBack(data)
    }
    
    uni.navigateBack({ delta })
  }
}
```

### 九、常见问题与解决方案

#### 1. **URL长度限制**

```
// 问题：URL有长度限制（不同平台不同）
// 解决：使用全局数据或本地存储

// 发送大数据
sendLargeData(data) {
  const key = 'temp_data_' + Date.now()
  uni.setStorageSync(key, data)
  uni.navigateTo({
    url: `/pages/detail/detail?dataKey=${key}`
  })
}

// 接收大数据
onLoad(options) {
  if (options.dataKey) {
    const data = uni.getStorageSync(options.dataKey)
    uni.removeStorageSync(options.dataKey)
  }
}
```

#### 2. **页面刷新问题**

```
// 在需要刷新的页面添加刷新方法
export default {
  data() {
    return {
      needRefresh: false
    }
  },
  
  onShow() {
    if (this.needRefresh) {
      this.loadData()
      this.needRefresh = false
    }
  },
  
  methods: {
    // 供其他页面调用
    refreshData() {
      this.needRefresh = true
    }
  }
}
```

#### 3. **页面栈管理**

```
// 获取页面栈信息
getPageStackInfo() {
  const pages = getCurrentPages()
  return {
    length: pages.length,
    routes: pages.map(page => page.route),
    canGoBack: pages.length > 1
  }
}

// 返回首页
goHome() {
  const pages = getCurrentPages()
  if (pages.length > 1) {
    uni.navigateBack({
      delta: pages.length - 1
    })
  }
}
```

### 十、TypeScript支持

```
// types/router.d.ts
declare namespace UniApp {
  interface NavigateToOptions {
    url: string
    events?: Record<string, Function>
    success?: (res: GeneralCallbackResult) => void
    fail?: (err: any) => void
    complete?: () => void
  }
  
  interface NavigateBackOptions {
    delta?: number
    success?: (res: GeneralCallbackResult) => void
    fail?: (err: any) => void
    complete?: () => void
  }
}

// 页面参数类型定义
interface DetailPageParams {
  id: string | number
  title?: string
  data?: any
}

// 在页面中使用
export default {
  onLoad(options: DetailPageParams) {
    // 现在options有类型提示
    console.log(options.id)
  }
}
```

### 总结要点

1. 

   **路由配置在 `pages.json`** 中，必须先配置后使用

2. 

   **五种跳转方式**各有用途，根据场景选择

3. 

   **参数传递**优先使用URL query，大数据用全局存储

4. 

   **页面栈最大10层**，注意控制层级

5. 

   **Tab页面**使用 `switchTab`跳转，不能带参数

6. 

   **合理使用生命周期**处理页面状态

7. 

   **实现路由守卫**进行权限控制

8. 

   **封装工具函数**提高开发效率和代码可维护性

## 路由 - 获取query参数

>说明：使用options获取query参数。
>
>详细用法请参考本站示例：https://gitee.com/dexterleslie/demonstration/tree/main/demo-uni-app/demo-router

```vue
<template>
	<view class="container">
		<view class="header">
			<text class="title">路由参数演示</text>
		</view>
		
		<view class="content">
			<view class="section">
				<text class="section-title">错误的获取方式 (Vue Router 方式)</text>
				<text class="error-text">{{ errorMessage }}</text>
			</view>
			
			<view class="section">
				<text class="section-title">正确的获取方式 (uni-app 方式)</text>
				<view class="success-info">
					<text>参数 param1: {{ param1 }}</text>
					<text>参数 param2: {{ param2 }}</text>
				</view>
			</view>
			
			<view class="section">
				<text class="section-title">使用 getCurrentPages() 获取参数</text>
				<view class="success-info">
					<text>参数 param1: {{ currentPagesParam1 }}</text>
					<text>参数 param2: {{ currentPagesParam2 }}</text>
				</view>
				<!-- <button class="btn" @tap="useGetCurrentPages">使用 getCurrentPages() 获取参数</button> -->
			</view>
			
			<view class="button-group">
				<button class="btn" @tap="goBack">返回首页</button>
			</view>
		</view>
	</view>
</template>

<script>
export default {
	data() {
		return {
			param1: '',
			param2: '',
			errorMessage: '',
			currentPagesParam1: '',
			currentPagesParam2: ''
		};
	},
	onLoad(options) {
		// 尝试使用错误的方式获取参数 (Vue Router)
		this.tryWrongWay();
		
		// 使用正确的方式获取参数 (uni-app)
		this.useCorrectWay(options);
		
		// 使用 getCurrentPages() 获取参数
		this.useGetCurrentPages();
	},
	methods: {
		// 错误的获取方式：尝试使用 this.$route.query
		tryWrongWay() {
			try {
				// 这是 Vue Router 的用法，在 uni-app 中不适用
				const param1 = this.$route.query.param1;
				this.errorMessage = `错误方式: this.$route.query.param1 = ${param1}`;
			} catch (error) {
				this.errorMessage = `错误信息: ${error.message}`;
			}
		},
		
		// 正确的获取方式：使用 onLoad 生命周期的 options 参数
		useCorrectWay(options) {
			if (options) {
				this.param1 = options.param1 || '未传递';
				this.param2 = options.param2 || '未传递';
			}
		},
		
		// 使用 getCurrentPages() 获取参数
		useGetCurrentPages() {
			// 获取当前页面栈
			const pages = getCurrentPages();
			// 获取当前页面实例 (页面栈的最后一个元素)
			const currentPage = pages[pages.length - 1];
			// 获取页面参数
			const pageOptions = currentPage.options;
			// 存储参数到 data
			this.currentPagesParam1 = pageOptions.param1 || '未传递';
			this.currentPagesParam2 = pageOptions.param2 || '未传递';
		},
		
		// 返回首页
		goBack() {
			uni.navigateBack({
				delta: 1
			});
		}
	}
};
</script>

<style scoped>
.container {
	padding: 20rpx;
	background-color: #f5f5f5;
	min-height: 100vh;
}

.header {
	text-align: center;
	padding: 30rpx 0;
	background-color: #ffffff;
	border-radius: 10rpx;
	margin-bottom: 20rpx;
	box-shadow: 0 2rpx 10rpx rgba(0, 0, 0, 0.1);
}

.title {
	font-size: 36rpx;
	font-weight: bold;
	color: #333333;
}

.content {
	background-color: #ffffff;
	border-radius: 10rpx;
	padding: 20rpx;
	box-shadow: 0 2rpx 10rpx rgba(0, 0, 0, 0.1);
}

.section {
	margin-bottom: 40rpx;
}

.section-title {
	display: block;
	font-size: 32rpx;
	font-weight: bold;
	color: #333333;
	margin-bottom: 20rpx;
	padding-bottom: 10rpx;
	border-bottom: 2rpx solid #e0e0e0;
}

.error-text {
	display: block;
	padding: 20rpx;
	background-color: #ffebee;
	color: #c62828;
	border-radius: 8rpx;
	font-size: 26rpx;
	line-height: 40rpx;
}

.success-info {
	padding: 20rpx;
	background-color: #e8f5e9;
	border-radius: 8rpx;
	font-size: 28rpx;
	color: #2e7d32;
	line-height: 48rpx;
}

.button-group {
	display: flex;
	flex-direction: column;
	gap: 20rpx;
}

.btn {
	padding: 20rpx;
	background-color: #007aff;
	color: #ffffff;
	border: none;
	border-radius: 8rpx;
	font-size: 28rpx;
	cursor: pointer;
}

.btn:active {
	background-color: #0056b3;
}
</style>
```

## 拍照

>详细用法请参考本站示例：https://gitee.com/dexterleslie/demonstration/tree/main/demo-uni-app/demo-take-photo
>
>从本地相册选择图片或使用相机拍照：https://uniapp.dcloud.net.cn/api/media/image.html#chooseimage

调用uni.chooseImage接口拍照

```js
// 拍照方法
takePhoto() {
    uni.chooseImage({
        count: 1, // 最多选择1张图片
        sizeType: ['original', 'compressed'], // 可以指定是原图还是压缩图，默认二者都有
        sourceType: ['camera'], // 使用相机
        success: (res) => {
            // 获取图片路径
            this.photoPath = res.tempFilePaths[0];
            console.log('图片路径:', this.photoPath);
        },
        fail: (err) => {
            console.error('选择图片失败:', err);
            uni.showToast({
                title: '选择图片失败',
                icon: 'none'
            });
        }
    });
},
```

拍照成功后回显到image控件中

```html
<image class="photo" :src="photoPath" mode="aspectFit"></image>
```

保存图片到相册中

```js
// 保存到相册方法
saveToAlbum() {
    if (!this.photoPath) {
        uni.showToast({
            title: '请先拍照',
            icon: 'none'
        });
        return;
    }

    // 检查平台兼容性问题
    // H5平台不支持saveImageToPhotosAlbum，需要使用其他方法
    // #ifdef H5
    uni.showModal({
        title: '提示',
        content: '在H5环境中，图片已经保存在临时路径中，您可以右键保存图片。',
        showCancel: false
    });
    return;
    // #endif

    // 非H5平台，使用标准保存方法
    // #ifndef H5
    uni.saveImageToPhotosAlbum({
        filePath: this.photoPath,
        success: () => {
            uni.showToast({
                title: '保存成功',
                icon: 'success'
            });
        },
        fail: (err) => {
            console.error('保存到相册失败:', err);

            // 如果是权限问题，尝试请求权限
            if (err.errMsg && err.errMsg.includes('auth deny')) {
                uni.showModal({
                    title: '权限请求',
                    content: '需要访问相册权限以保存图片',
                    success: (res) => {
                        if (res.confirm) {
                            // 请求相册权限
                            uni.openSetting({
                                success: (settingRes) => {
                                    // 重新尝试保存
                                    if (settingRes.authSetting['scope.writePhotosAlbum']) {
                                        this.saveToAlbum(); // 递归调用，重新尝试保存
                                    }
                                }
                            });
                        }
                    }
                });
            } else {
                uni.showToast({
                    title: '保存失败',
                    icon: 'none'
                });
            }
        }
    });
    // #endif
},
```

## 图片预览

>详细用法请参考本站示例：https://gitee.com/dexterleslie/demonstration/tree/main/demo-uni-app/demo-图片预览
>
>预览图片：https://uniapp.dcloud.net.cn/api/media/image.html#unipreviewimageobject

从相册选取图片

```js
// 从相册选择图片
chooseImage() {
    uni.chooseImage({
        count: 9, // 最多选择9张图片
        sizeType: ['original', 'compressed'], // 可以指定是原图还是压缩图，默认二者都有
        sourceType: ['album'], // 使用相册
        success: (res) => {
            // 获取图片路径
            this.imagePaths = res.tempFilePaths;
            console.log('图片路径:', this.imagePaths);
        },
        fail: (err) => {
            console.error('选择图片失败:', err);
            uni.showToast({
                title: '选择图片失败',
                icon: 'none'
            });
        }
    });
},
```

调用uni.previewImage图片预览

```js
// 预览图片
previewImage(index = 0) {
    if (this.imagePaths.length === 0) {
        uni.showToast({
            title: '请先选择图片',
            icon: 'none'
        });
        return;
    }

    // 使用 uni.previewImage API 预览图片
    uni.previewImage({
        current: this.imagePaths[index], // 当前显示图片的链接
        urls: this.imagePaths, // 需要预览的图片链接列表
        indicator: 'number', // 图片指示器样式，可取值："default" | "number" | "none"
        loop: true, // 是否可循环预览
        success: () => {
            console.log('预览图片成功');
        },
        fail: (err) => {
            console.error('预览图片失败:', err);
            uni.showToast({
                title: '预览图片失败',
                icon: 'none'
            });
        }
    });
}
```

## 图片上传、下载、使用URL预览

>详细用法请参考本站示例：https://gitee.com/dexterleslie/demonstration/tree/main/demo-uni-app/demo-图片上传和下载
>
>上传和下载：https://uniapp.dcloud.net.cn/api/request/network-file.html

运行 https://gitee.com/dexterleslie/demonstration/tree/main/front-end/axios/axios-api 作为api服务。

上传图片

```js
// 上传图片到服务器
uploadImage() {
    if (!this.selectedImage) {
        this.message = '请先选择图片'
        return
    }

    this.uploading = true
    this.message = '开始上传...'

    // 构建上传参数
    const uploadConfig = {
        url: this.apiBaseUrl + '/postWithFileUpload',
        filePath: this.selectedImage,
        name: 'files',
        formData: {},
        success: (res) => {
            try {
                const response = JSON.parse(res.data)
                if (response.errorCode == 0 && response.data && response.data.length > 0) {
                    // 将上传的文件名添加到列表中
                    this.uploadedFiles.push(...response.data)
                    this.message = '上传成功！文件已保存到服务器'
                    this.selectedImage = '' // 清空选中的图片
                } else {
                    this.message = '上传失败: ' + (response.errorMessage || '服务器返回数据格式错误')
                }
            } catch (e) {
                this.message = '上传失败: 解析服务器响应失败'
                console.error('解析响应失败:', e)
            }
        },
        fail: (err) => {
            console.error('上传失败:', JSON.stringify(err))

            // #ifdef APP-PLUS
            // APP端错误处理
            if (err.errMsg && err.errMsg.includes('uploadFile:fail')) {
                this.message = 'APP上传失败：网络连接问题\n解决方案：\n1. 确保服务器运行在 http://localhost:8080\n2. 检查manifest.json中的域名白名单配置\n3. 确保APP有网络权限'
            } else {
                this.message = 'APP上传失败: ' + (err.errMsg || '未知错误')
            }
            // #endif

            // #ifdef H5
            // H5端错误处理
            this.message = 'H5上传失败: ' + (err.errMsg || '网络错误')
            // #endif
        },
        complete: () => {
            this.uploading = false
        }
    }

    // #ifdef APP-PLUS
    // APP端添加超时和头部配置
    uploadConfig.timeout = 10000
    // #endif

    uni.uploadFile(uploadConfig)
},
```

下载图片

```js
// 下载图片
downloadImage(filename) {
    if (!filename) {
        this.message = '文件名不能为空'
        return
    }

    this.message = '开始下载...'

    const downloadConfig = {
        url: this.apiBaseUrl + '/' + filename,
        success: (res) => {
            if (res.statusCode === 200) {
                // 下载成功，可以保存到相册或显示
                this.message = '下载成功'
                // 可以选择保存到相册
                uni.saveImageToPhotosAlbum({
                    filePath: res.tempFilePath,
                    success: () => {
                        this.message = '下载并保存到相册成功'
                    },
                    fail: (saveErr) => {
                        console.error('保存到相册失败:', saveErr)
                        this.message = '下载成功，但保存到相册失败'
                    }
                })
            } else {
                this.message = '下载失败: HTTP ' + res.statusCode
            }
        },
        fail: (err) => {
            console.error('下载失败:', err)

            // #ifdef APP-PLUS
            // APP端错误处理
            if (err.errMsg && err.errMsg.includes('downloadFile:fail')) {
                this.message = 'APP下载失败：网络连接问题\n解决方案：\n1. 确保服务器运行在 http://localhost:8080\n2. 检查manifest.json中的域名白名单配置\n3. 确保APP有网络权限'
            } else {
                this.message = 'APP下载失败: ' + (err.errMsg || '未知错误')
            }
            // #endif

            // #ifdef H5
            // H5端错误处理
            this.message = 'H5下载失败: ' + (err.errMsg || '网络错误')
            // #endif
        }
    }

    // #ifdef APP-PLUS
    // APP端添加超时配置
    downloadConfig.timeout = 10000
    // #endif

    uni.downloadFile(downloadConfig)
},
```

URL预览图片

```js
// 预览已上传的图片
previewUploadedImage(filename) {
    if (!filename) return

    // 生成预览URL
    const previewUrl = this.apiBaseUrl + '/' + filename
    console.log('预览已上传图片路径:', previewUrl)

    uni.previewImage({
        current: previewUrl,
        urls: [previewUrl]
    })
}
```

## 视频录制、上传、下载、播放

具体用法请参考本站示例：https://gitee.com/dexterleslie/demonstration/tree/main/demo-uni-app/demo-视频录制上传下载播放

使用BlueStacks蓝叠模拟器测试此示例。

## 选择文件

>说明：调用uni.chooseFile在安卓基座运行提示方法没有实现错误。在安卓基座调用plus.io.chooseFile能够弹出文件选择框，但是选中文件后没有回到success函数。最终方案使用xe-upload插件实现选择文件https://ext.dcloud.net.cn/plugin?id=14423。
>
>详细用法请参考本站示例：https://gitee.com/dexterleslie/demonstration/tree/main/demo-uni-app/demo-选择文件

```vue
<template>
	<view class="content">
		<view class="header">
			<text class="title">多文件选择演示</text>
		</view>
		
		<view class="file-select-section">
			<button class="btn-primary" @click="chooseFiles">选择多个文件</button>
			<button class="btn-secondary" @click="clearFiles" :disabled="selectedFiles.length === 0">清空文件列表</button>
		</view>
		
		<xe-upload ref="xeUpload" :options="uploadOptions" @callback="handleUploadCallback"></xe-upload>

		<view class="files-section" v-if="selectedFiles.length > 0">
			<text class="section-title">已选择的文件：</text>
			<view class="file-list">
				<view v-for="(file, index) in selectedFiles" :key="index" class="file-item">
					<view class="file-info">
						<text class="filename">{{ file.name || file }}</text>
						<text class="filesize" v-if="file.size">{{ formatFileSize(file.size) }}</text>
					</view>
					<button class="btn-remove" @click="removeFile(index)">移除</button>
				</view>
			</view>
		</view>

		<view class="message" v-if="message">{{ message }}</view>
	</view>
</template>

<script>
import XeUpload from '@/uni_modules/xe-upload/components/xe-upload/xe-upload.vue'
	
	export default {
		components: {
			XeUpload
		},
		data() {
			return {
				selectedFiles: [], // 选中的文件列表
				message: '', // 消息提示
				uploadOptions: {
					// 不设置url，让组件返回本地文件信息
					name: 'file',
					extension: ['.doc', '.docx', '.pdf', '.txt', '.zip', '.rar', '.xls', '.xlsx', '.ppt', '.pptx', '.jpg', '.jpeg', '.png', '.gif']
				}
			}
		},
		onLoad() {
			// 根据运行环境显示不同的提示
			// #ifdef H5
			this.message = 'H5模式：点击"选择多个文件"按钮选择文件'
			// #endif
			
			// #ifdef APP-PLUS
			this.message = 'APP模式：点击"选择多个文件"按钮选择文件'
			// #endif
			
			// #ifdef MP-WEIXIN
			this.message = '小程序模式：点击"选择多个文件"按钮选择文件'
			// #endif
		},
		methods: {
			// 处理xe-upload组件的回调
			handleUploadCallback(e) {
				const { type, data } = e
				
				switch (type) {
					case 'choose':
						// 处理选择的文件
						console.log('选择了文件:', data)
						// 将组件返回的文件信息转换为我们需要的格式
						const files = data.map(file => ({
							name: file.name || '未知文件',
							size: file.size || 0,
							path: file.tempFilePath || '',
							type: file.type || 'application/octet-stream',
							fileType: file.fileType || 'file'
						}))
						
						this.selectedFiles = [...this.selectedFiles, ...files]
						this.message = `已选择 ${files.length} 个文件，总计 ${this.selectedFiles.length} 个`
						break
						
					case 'success':
						// 上传成功（这里不会触发，因为我们没有设置url）
						console.log('上传成功:', data)
						break
						
					case 'warning':
						// 错误信息
						console.error('选择文件出错:', data)
						this.message = '选择文件失败: ' + (data.message || data.errMsg || '未知错误')
						break
						
					default:
						console.log('未知回调类型:', type, data)
				}
			},

			// 选择多个文件
			chooseFiles() {
				// 使用xe-upload组件选择文件
				// 组件会自动根据平台选择合适的文件选择方式
				this.$refs.xeUpload.upload('file')
			},

			// 移除文件
			removeFile(index) {
				this.selectedFiles.splice(index, 1)
				this.message = `已移除文件，剩余 ${this.selectedFiles.length} 个文件`
			},

			// 清空文件列表
			clearFiles() {
				this.selectedFiles = []
				this.message = '已清空文件列表'
			},

			// 格式化文件大小
			formatFileSize(size) {
				if (size === 0) return '0 B'
				const k = 1024
				const sizes = ['B', 'KB', 'MB', 'GB']
				const i = Math.floor(Math.log(size) / Math.log(k))
				return parseFloat((size / Math.pow(k, i)).toFixed(2)) + ' ' + sizes[i]
			}
		}
	}
</script>

<style>
	.content {
		display: flex;
		flex-direction: column;
		padding: 20rpx;
		background-color: #f5f5f5;
		min-height: 100vh;
	}

	.header {
		text-align: center;
		margin-bottom: 40rpx;
		padding: 20rpx;
		background-color: #fff;
		border-radius: 10rpx;
		box-shadow: 0 2rpx 10rpx rgba(0, 0, 0, 0.1);
	}

	.title {
		font-size: 36rpx;
		font-weight: bold;
		color: #333;
	}

	.file-select-section {
		display: flex;
		flex-direction: column;
		gap: 20rpx;
		margin-bottom: 40rpx;
		padding: 30rpx;
		background-color: #fff;
		border-radius: 10rpx;
		box-shadow: 0 2rpx 10rpx rgba(0, 0, 0, 0.1);
	}

	.btn-primary, .btn-secondary {
		padding: 20rpx 40rpx;
		border-radius: 8rpx;
		font-size: 32rpx;
		font-weight: bold;
		border: none;
		color: #fff;
	}

	.btn-primary {
		background-color: #007aff;
	}

	.btn-primary:hover {
		background-color: #0056d3;
	}

	.btn-secondary {
		background-color: #8e8e93;
	}

	.btn-secondary:hover {
		background-color: #6d6d72;
	}

	.btn-secondary:disabled {
		background-color: #ccc;
		color: #999;
	}

	.files-section {
		margin-bottom: 30rpx;
		padding: 30rpx;
		background-color: #fff;
		border-radius: 10rpx;
		box-shadow: 0 2rpx 10rpx rgba(0, 0, 0, 0.1);
	}

	.section-title {
		font-size: 32rpx;
		font-weight: bold;
		color: #333;
		margin-bottom: 20rpx;
	}

	.file-list {
		display: flex;
		flex-direction: column;
		gap: 20rpx;
	}

	.file-item {
		display: flex;
		justify-content: space-between;
		align-items: center;
		padding: 20rpx;
		background-color: #f8f8f8;
		border-radius: 8rpx;
	}

	.file-info {
		display: flex;
		flex-direction: column;
		flex: 1;
		overflow: hidden;
	}

	.filename {
		font-size: 28rpx;
		color: #333;
		margin-bottom: 10rpx;
		overflow: hidden;
		text-overflow: ellipsis;
		white-space: nowrap;
	}

	.filesize {
		font-size: 24rpx;
		color: #8e8e93;
	}

	.btn-remove {
		padding: 10rpx 20rpx;
		border-radius: 6rpx;
		font-size: 26rpx;
		border: none;
		background-color: #ff3b30;
		color: #fff;
	}

	.message {
		padding: 20rpx;
		text-align: center;
		background-color: #fff;
		border-radius: 10rpx;
		box-shadow: 0 2rpx 10rpx rgba(0, 0, 0, 0.1);
		margin-top: 20rpx;
	}
</style>
```

## 选择文件上传和下载

>详细用法请参考本站示例：https://gitee.com/dexterleslie/demonstration/tree/main/demo-uni-app/demo-选择文件上传和下载

运行 https://gitee.com/dexterleslie/demonstration/tree/main/front-end/axios/axios-api 作为api服务。

```vue
<template>
	<view class="content">
		<view class="header">
			<text class="title">文件上传和下载演示</text>
		</view>
		
		<view class="upload-section">
			<button class="btn-primary" @click="chooseFile">选择文件</button>
			<button class="btn-success" @click="uploadFile" :disabled="!selectedFile || uploading">
				{{ uploading ? '上传中...' : '上传文件' }}
			</button>
		</view>

		<xe-upload ref="xeUpload" :options="uploadOptions" @callback="handleUploadCallback"></xe-upload>
		
		<view class="xe-upload-wrapper">
			<text class="xe-upload-title">跨平台文件选择</text>
			<text class="xe-upload-info">
				本功能使用 xe-upload 组件实现
				支持 H5、APP、微信小程序等平台
				点击上方按钮选择文件
			</text>
		</view>

		<view class="preview-section" v-if="selectedFile">
			<text class="section-title">已选择的文件：</text>
			<view class="file-info">
				<text class="filename">{{ getFileName(selectedFile) }}</text>
				<text class="filesize">{{ formatFileSize(selectedFileSize) }}</text>
			</view>
		</view>

		<view class="uploaded-section" v-if="uploadedFiles.length > 0">
			<text class="section-title">已上传的文件：</text>
			<view class="uploaded-list">
				<view v-for="(file, index) in uploadedFiles" :key="index" class="uploaded-item">
					<text class="filename">{{ file }}</text>
					<button class="btn-download" @click="downloadFile(file)">下载</button>
				</view>
			</view>
		</view>

		<view class="message" v-if="message">{{ message }}</view>
	</view>
</template>

<script>
	import XeUpload from '@/uni_modules/xe-upload/components/xe-upload/xe-upload.vue'
	
	export default {
		components: {
			XeUpload
		},
		data() {
			return {
				title: 'Hello',
				selectedFile: '', // 选中的文件路径
				selectedFileSize: 0, // 文件大小
				uploadedFiles: [], // 已上传的文件列表
				uploading: false, // 上传状态
				message: '', // 消息提示
				apiBaseUrl: '', // API基础地址，将在运行时根据环境设置
				uploadOptions: {
					// 不设置url，让组件返回本地文件信息
					name: 'file',
					extension: ['.doc', '.docx', '.pdf', '.txt', '.zip', '.rar', '.xls', '.xlsx', '.ppt', '.pptx', '.jpg', '.jpeg', '.png', '.gif']
				}
			}
		},
		onLoad() {
			// 根据运行环境设置 API 基础 URL
			// #ifdef H5
			// H5 环境通常可以通过 localhost 或 127.0.0.1 访问
			this.apiBaseUrl = 'http://127.0.0.1:8080/api/v1';
			this.message = 'H5模式：网络连接正常'
			// #endif
			
			// #ifdef APP-PLUS
			// APP 环境可能需要使用实际 IP 地址
			this.apiBaseUrl = 'http://192.168.1.182:8080/api/v1';
			this.message = 'APP模式：如遇上传失败，请确保服务器运行在 http://192.168.1.182:8080'
			// #endif
			
			// #ifdef MP-WEIXIN
			// 小程序环境可能需要使用 https 域名
			this.apiBaseUrl = 'https://your-domain.com/api/v1';
			this.message = '小程序模式：请确保服务器支持 HTTPS 并已配置域名白名单'
			// #endif
		},
		methods: {
			// 处理xe-upload组件的回调
			handleUploadCallback(e) {
				const { type, data } = e
				
				switch (type) {
					case 'choose':
						// 处理选择的文件
						console.log('选择了文件:', data)
						if (data && data.length > 0) {
							const file = data[0] // 获取第一个选择的文件
							this.selectedFile = file.tempFilePath || ''
							this.selectedFileSize = file.size || 0
							this.message = `文件选择成功: ${file.name || '未知文件'} (${this.formatFileSize(file.size || 0)})`
						} else {
							this.message = '没有选择文件'
						}
						break
						
					case 'success':
						// 上传成功（这里不会触发，因为我们没有设置url）
						console.log('上传成功:', data)
						break
						
					case 'warning':
						// 错误信息
						console.error('选择文件出错:', data)
						this.message = '选择文件失败: ' + (data.message || data.errMsg || '未知错误')
						break
						
					default:
						console.log('未知回调类型:', type, data)
				}
			},

			// 选择文件
			chooseFile() {
				// 使用xe-upload组件选择文件
				// 组件会自动根据平台选择合适的文件选择方式
				this.$refs.xeUpload.upload('file')
			},

			// 获取文件名
			getFileName(filePath) {
				if (!filePath) return ''
				const parts = filePath.split('/')
				return parts[parts.length - 1]
			},

			// 格式化文件大小
			formatFileSize(size) {
				if (size === 0) return '0 B'
				const k = 1024
				const sizes = ['B', 'KB', 'MB', 'GB']
				const i = Math.floor(Math.log(size) / Math.log(k))
				return parseFloat((size / Math.pow(k, i)).toFixed(2)) + ' ' + sizes[i]
			},

			// 上传文件到服务器
			uploadFile() {
				if (!this.selectedFile) {
					this.message = '请先选择文件'
					return
				}

				this.uploading = true
				this.message = '开始上传...'

				// 构建上传参数
				const uploadConfig = {
					url: this.apiBaseUrl + '/postWithFileUpload',
					filePath: this.selectedFile,
					name: 'files',
					formData: {},
					success: (res) => {
						try {
							const response = JSON.parse(res.data)
							if (response.errorCode == 0 && response.data && response.data.length > 0) {
								// 将上传的文件名添加到列表中
								this.uploadedFiles.push(...response.data)
								this.message = '上传成功！文件已保存到服务器'
								this.selectedFile = '' // 清空选中的文件
								this.selectedFileSize = 0 // 清空文件大小
							} else {
								this.message = '上传失败: ' + (response.errorMessage || '服务器返回数据格式错误')
							}
						} catch (e) {
							this.message = '上传失败: 解析服务器响应失败'
							console.error('解析响应失败:', e)
						}
					},
					fail: (err) => {
						console.error('上传失败:', JSON.stringify(err))
						
						// #ifdef APP-PLUS
						// APP端错误处理
						if (err.errMsg && err.errMsg.includes('uploadFile:fail')) {
							this.message = 'APP上传失败：网络连接问题\n解决方案：\n1. 确保服务器运行在 http://192.168.1.182:8080\n2. 检查manifest.json中的域名白名单配置\n3. 确保APP有网络权限'
						} else {
							this.message = 'APP上传失败: ' + (err.errMsg || '未知错误')
						}
						// #endif
						
						// #ifdef H5
						// H5端错误处理
						this.message = 'H5上传失败: ' + (err.errMsg || '网络错误')
						// #endif
					},
					complete: () => {
						this.uploading = false
					}
				}

				// #ifdef APP-PLUS
				// APP端添加超时配置
				uploadConfig.timeout = 10000
				// #endif

				uni.uploadFile(uploadConfig)
			},

			// 下载文件
			downloadFile(filename) {
				if (!filename) {
					this.message = '文件名不能为空'
					return
				}

				this.message = '开始下载...'

				const downloadConfig = {
					url: this.apiBaseUrl + '/' + filename,
					success: (res) => {
						if (res.statusCode === 200) {
							// 下载成功
							this.message = '下载成功'
							
							// #ifdef H5
							// H5端保存文件
							const a = document.createElement('a')
							a.href = res.tempFilePath
							a.download = filename
							document.body.appendChild(a)
							a.click()
							document.body.removeChild(a)
							this.message = '下载并保存文件成功'
							// #endif
							
							// #ifdef APP-PLUS
							// APP端下载完成，只显示文件路径
							this.message = `文件下载成功: ${res.tempFilePath}`
							// 询问用户是否需要打开文件
							// this.openWithDefaultApp(res.tempFilePath, filename)
							// #endif
						} else {
							this.message = '下载失败: HTTP ' + res.statusCode
						}
					},
					fail: (err) => {
						console.error('下载失败:', err)
						
						// #ifdef APP-PLUS
						// APP端错误处理
						if (err.errMsg && err.errMsg.includes('downloadFile:fail')) {
							this.message = 'APP下载失败：网络连接问题\n解决方案：\n1. 确保服务器运行在 http://192.168.1.182:8080\n2. 检查manifest.json中的域名白名单配置\n3. 确保APP有网络权限'
						} else {
							this.message = 'APP下载失败: ' + (err.errMsg || '未知错误')
						}
						// #endif
						
						// #ifdef H5
						// H5端错误处理
						this.message = 'H5下载失败: ' + (err.errMsg || '网络错误')
						// #endif
					}
				}

				// #ifdef APP-PLUS
				// APP端添加超时配置
				downloadConfig.timeout = 10000
				// #endif

				uni.downloadFile(downloadConfig)
			},

			// 使用默认应用打开文件
			openWithDefaultApp(filePath, filename) {
				uni.showModal({
					title: '下载完成',
					content: `文件已下载，是否使用默认应用打开？`,
					success: (res) => {
						if (res.confirm) {
							uni.openDocument({
								filePath: filePath,
								success: () => {
									this.message = '文件已打开'
								},
								fail: () => {
									this.message = '无法打开文件，请检查文件格式是否支持'
								}
							})
						} else {
							this.message = '文件已下载，请手动保存'
						}
					}
				})
			}
		}
	}
</script>

<style>
	.content {
		display: flex;
		flex-direction: column;
		padding: 20rpx;
		background-color: #f5f5f5;
		min-height: 100vh;
	}

	.header {
		text-align: center;
		margin-bottom: 40rpx;
		padding: 20rpx;
		background-color: #fff;
		border-radius: 10rpx;
		box-shadow: 0 2rpx 10rpx rgba(0, 0, 0, 0.1);
	}

	.title {
		font-size: 36rpx;
		font-weight: bold;
		color: #333;
	}

	.upload-section {
		display: flex;
		flex-direction: column;
		gap: 20rpx;
		margin-bottom: 40rpx;
		padding: 30rpx;
		background-color: #fff;
		border-radius: 10rpx;
		box-shadow: 0 2rpx 10rpx rgba(0, 0, 0, 0.1);
	}
	
	.xe-upload-wrapper {
		display: flex;
		flex-direction: column;
		align-items: center;
		justify-content: center;
		padding: 20rpx;
		margin-bottom: 20rpx;
		background-color: #f8f9fa;
		border-radius: 8rpx;
		border: 1rpx dashed #dee2e6;
	}
	
	.xe-upload-title {
		font-size: 28rpx;
		color: #6c757d;
		margin-bottom: 15rpx;
	}
	
	.xe-upload-info {
		font-size: 24rpx;
		color: #adb5bd;
		text-align: center;
		line-height: 1.5;
	}

	.btn-primary, .btn-success {
		padding: 20rpx 40rpx;
		border-radius: 8rpx;
		font-size: 32rpx;
		font-weight: bold;
		border: none;
		color: #fff;
	}

	.btn-primary {
		background-color: #007aff;
	}

	.btn-primary:hover {
		background-color: #0056d3;
	}

	.btn-success {
		background-color: #34c759;
	}

	.btn-success:hover {
		background-color: #28a745;
	}

	.btn-success:disabled {
		background-color: #ccc;
		color: #999;
	}

	.preview-section, .uploaded-section {
		margin-bottom: 30rpx;
		padding: 30rpx;
		background-color: #fff;
		border-radius: 10rpx;
		box-shadow: 0 2rpx 10rpx rgba(0, 0, 0, 0.1);
	}

	.section-title {
		font-size: 32rpx;
		font-weight: bold;
		color: #333;
		margin-bottom: 20rpx;
		display: block;
	}

	.file-info {
		display: flex;
		flex-direction: column;
		padding: 20rpx;
		background-color: #f8f9fa;
		border-radius: 8rpx;
		border: 1rpx solid #e9ecef;
	}

	.filename {
		font-size: 28rpx;
		color: #495057;
		margin-bottom: 10rpx;
		word-break: break-all;
	}

	.filesize {
		font-size: 24rpx;
		color: #6c757d;
	}

	.uploaded-list {
		display: flex;
		flex-direction: column;
		gap: 15rpx;
	}

	.uploaded-item {
		display: flex;
		align-items: center;
		justify-content: space-between;
		padding: 20rpx;
		background-color: #f8f9fa;
		border-radius: 8rpx;
		border: 1rpx solid #e9ecef;
	}

	.uploaded-item .filename {
		flex: 1;
		margin-right: 20rpx;
		word-break: break-all;
	}

	.btn-download {
		padding: 10rpx 20rpx;
		border-radius: 6rpx;
		font-size: 24rpx;
		border: none;
		color: #fff;
		background-color: #007aff;
	}

	.message {
		text-align: center;
		padding: 20rpx;
		margin-top: 20rpx;
		border-radius: 8rpx;
		font-size: 28rpx;
		background-color: #e3f2fd;
		color: #1976d2;
		border: 1rpx solid #bbdefb;
	}

	/* 响应式设计 */
	@media (max-width: 750px) {
		.content {
			padding: 10rpx;
		}
		
		.upload-section, .preview-section, .uploaded-section {
			padding: 20rpx;
		}
		
		.uploaded-item {
			flex-direction: column;
			align-items: stretch;
			gap: 15rpx;
		}
		
		.btn-download {
			margin: 0;
		}
	}
</style>

```

## 引用插件xe-upload

访问 https://ext.dcloud.net.cn/plugin?id=14423

导入插件到项目中：点击“下载插件并导入HBuilderX”根据提示导入插件到项目中。

运行插件示例：点击“使用HBuilderX导入示例项目”根据提示导入示例项目到HBuilderX。

## 文件api

>官方参考链接：https://uniapp.dcloud.net.cn/api/file/file.html
>
>详细用法请参考本站示例：https://gitee.com/dexterleslie/demonstration/tree/main/demo-uni-app/demo-文件api
>
>提示：
>
>- 这些api主要是从应用的本地文件系统中，查询并返回由 uni.saveFile或 uni.downloadFile等接口成功保存到本地的所有文件的清单。没有遇到需要使用此api的场景。
>- **应用沙盒**：每个应用独有的私有存储空间，其他应用无法访问。UniApp 的 `uni.saveFile`, `uni.getSavedFileList`操作的都是这个目录下的文件。**这是 UniApp 官方 API 唯一能直接管理的目录。**
>- **公共目录**：如 Downloads (下载), Documents (文档), Pictures (图片) 等，是所有应用共享的空间。要操作这里，需要向系统申请权限并使用原生插件或特定平台的 API。

## 获取公共下载或者公共文档目录路径的api是什么呢？

首先，我需要给出一个核心结论：**在标准的 UniApp 框架下，没有直接获取设备公共目录（如下载目录、文档目录）绝对路径的 API。**

这与 Android 和 iOS 的系统安全机制有关。出于隐私和安全考虑，应用默认只能访问自己沙盒内的文件，不能直接读写其他公共目录。UniApp 作为跨平台框架，为了保持各平台的一致性，并未直接暴露这些底层路径。

但是，我们可以通过一些“曲线救国”的方式来实现类似的功能。下面我将分平台详细解释可用的方案和对应的 API。

------

### 核心概念：沙盒 vs. 公共目录

- 

  **应用沙盒**：每个应用独有的私有存储空间，其他应用无法访问。UniApp 的 `uni.saveFile`, `uni.getSavedFileList`操作的都是这个目录下的文件。**这是 UniApp 官方 API 唯一能直接管理的目录。**

- 

  **公共目录**：如 Downloads (下载), Documents (文档), Pictures (图片) 等，是所有应用共享的空间。要操作这里，需要向系统申请权限并使用原生插件或特定平台的 API。

------

### 各平台解决方案与 API

#### 1. App 端 (iOS & Android)

这是需求最强烈，也是方案最多的平台。主要有两种思路：**使用 Native.js** 或 **使用 UniApp 原生插件**。

##### 方案一：使用 Native.js (仅限 App 端)

Native.js 可以直接调用设备原生 API。通过这种方式，我们可以获取到公共目录的路径。

**关键点：**

- 

  **Android**：通过 `plus.io`模块获取公开路径常量。

- 

  **iOS**：通过 `plus.ios`导入 Foundation Framework 来获取路径。

**示例代码：**

```
// #ifdef APP-PLUS
function getPublicDirectory() {
  // 区分平台
  if (uni.getSystemInfoSync().platform === 'android') {
    // Android 平台
    // 这些是 plus.io 提供的公开目录常量
    const main = plus.android.runtimeMainActivity();
    const Environment = plus.android.importClass('android.os.Environment');
    
    // 获取公共下载目录
    if (Environment.getExternalStorageState() === Environment.MEDIA_MOUNTED) {
      const DOWNLOAD_SERVICE = plus.android.invoke(Environment, 'getExternalStoragePublicDirectory', Environment.DIRECTORY_DOWNLOADS);
      const downloadPath = plus.android.invoke(DOWNLOAD_SERVICE, 'getAbsolutePath');
      console.log('Android 公共下载目录:', downloadPath);
      return downloadPath;
    }
  } else if (uni.getSystemInfoSync().platform === 'ios') {
    // iOS 平台
    const NSFileManager = plus.ios.importClass('NSFileManager');
    const NSDocumentDirectory = plus.ios.importClass('NSSearchPathDirectory').NSDocumentDirectory;
    const NSUserDomainMask = plus.ios.importClass('NSSearchPathDomainMask').NSUserDomainMask;
    
    const fileManager = NSFileManager.defaultManager();
    const paths = fileManager.URLsForDirectoryInDomains(NSDocumentDirectory, NSUserDomainMask);
    const docPath = plus.ios.invoke(paths.firstObject(), 'path');
    console.log('iOS 沙盒文档目录 (非真正公共):', docPath);
    
    // !!! 重要提示 !!!
    // iOS 的沙盒机制极其严格，应用几乎无法在未经用户明确选择的情况下写入真正的公共目录（如 Files app 中的 iCloud Drive）。
    // 上述代码获取的仍然是应用自己的沙盒 Documents 目录。
    // 要与“文件”App 交互，必须使用 UIDocumentPickerViewController，这超出了 Native.js 的简单范畴，通常需要开发原生插件。
    return docPath; // 这里返回的不是公共目录
  }
  return null;
}

getPublicDirectory();
// #endif
```

**重要警告：**

- 

  **Android 权限**：从 Android 6.0 (API 23) 开始，即使你有了路径，要读写公共目录也需要在运行时动态申请 `WRITE_EXTERNAL_STORAGE`或 `READ_EXTERNAL_STORAGE`权限。UniApp 提供了 `uni.authorize`等 API 来申请权限。

- 

  **Android Scoped Storage**：Android 10 (API 29) 引入了分区存储，进一步限制了应用对公共目录的直接访问。上述 `getExternalStoragePublicDirectory`方法在 Target SDK >= 29 的设备上可能受限，行为会发生变化。

- 

  **iOS 限制**：如上所述，iOS 上实现真正的公共写入非常困难，**通常不建议尝试**。如果需要分享文件给用户，应使用 `uni.share`或将文件保存到 `tmp`目录后引导用户用系统分享菜单分享出去。

##### 方案二：使用 UniApp 原生插件

这是更强大、更可靠的方式。你可以寻找现有的插件市场插件，或者自己开发一个原生插件。

- 

  **插件市场**：在 [DCloud 插件市场](https://ext.dcloud.net.cn/)搜索 “文件路径”、“公共目录”、“文件管理” 等关键词，可以找到一些封装好的原生插件，它们通常会提供更简单易用的 API 来获取和操作公共目录。

- 

  **自定义插件**：如果你有原生开发能力（Java/Kotlin for Android, Objective-C/Swift for iOS），可以编写原生插件，将获取公共路径的逻辑封装成 JS 可调用的接口，然后在 UniApp 项目中引用。

#### 2. 小程序端

小程序拥有自己独立的文件系统，**没有公共目录的概念**。所有文件操作都在小程序的私有沙盒内进行。因此，不存在获取公共目录路径的需求和 API。

#### 3. H5 端

H5 端完全运行在浏览器中，受到浏览器同源策略和沙盒环境的严格限制。JS **无法直接获取用户设备上的任意文件路径**。文件操作仅限于用户通过 `<input type="file">`主动选择的文件，或者利用浏览器的下载 API 触发下载，但无法预知或指定下载到哪个具体目录。

------

### 总结与最佳实践建议

| 平台              | 能否获取公共目录路径 | 推荐方案                                                     | 难度 |
| ----------------- | -------------------- | ------------------------------------------------------------ | ---- |
| **App (Android)** | **可以，但有条件**   | 1. **Native.js** + 动态申请存储权限。 2. **UniApp 原生插件**。 | 中等 |
| **App (iOS)**     | **极难，不推荐**     | 1. 仅能获取应用沙盒路径。 2. 如需分享，使用系统分享。        | 高   |
| **小程序**        | **不可以**           | 无。使用小程序自有文件系统。                                 | -    |
| **H5**            | **不可以**           | 无。受浏览器安全限制。                                       | -    |

**给你的实践建议：**

1. 

   **重新评估需求**：首先明确你为什么需要公共目录路径？

   - 

     **如果是想让用户能方便地找到文件**：对于 App，更好的做法是将文件保存在应用沙盒内，然后提供一个“打开文件所在位置”的功能（Android 可用 `plus.runtime.openFile`尝试打开，iOS 不行），或者引导用户使用系统的“分享”功能将文件发送出去。

   - 

     **如果是想下载文件供用户在其他应用中打开**：直接使用 `uni.downloadFile`下载到沙盒，然后用 `uni.openDocument`或 `uni.saveFile`+ `uni.share`来打开或分享，让系统或用户来决定文件的最终去向。

   - 

     **如果是想与其他应用交换数据**：考虑使用 ContentProvider (Android) 或 UIDocumentInteractionController/iCloud (iOS) 等更高级的机制，这通常意味着必须开发原生插件。

2. 

   **优先使用 UniApp 标准 API**：如果业务允许，始终优先使用 `uni.saveFile`、`uni.getSavedFileList`、`uni.openDocument`等官方 API。它们最稳定，兼容性最好。

3. 

   **谨慎使用 Native.js**：如果必须用，请务必做好平台判断 (`#ifdef APP-PLUS`) 和错误处理，并注意 Android 版本的碎片化问题（尤其是存储权限和分区存储）。

总而言之，**“获取公共目录路径”是一个典型的“平台相关”需求，在 UniApp 的统一 API 层面没有提供银弹，需要根据具体平台和场景，采用不同的技术方案来解决。**

## Native.js概念

>参考链接：https://uniapp.dcloud.net.cn/tutorial/native-js.html
>
>具体用法请参考本站示例：https://gitee.com/dexterleslie/demonstration/tree/main/demo-uni-app/demo-nativejs
>
>Native.js示例汇总：https://ask.dcloud.net.cn/article/114

### 一句话概括

**Native.js 是 UniApp 提供的一套桥接方案，它允许你在 JavaScript（Vue）代码中直接调用原生（Android/iOS）平台的 API。**

简单来说，它就是一座连接 JavaScript 世界和原生世界的“桥梁”。

------

### 为什么需要 Native.js？

UniApp 的核心优势在于 **“一套代码，多端运行”**。为了实现这个目标，它抽象出了一个统一的 API 层（如 `uni.request`, `uni.navigateTo`）。但在很多场景下，这个统一的 API 无法满足所有需求：

1. 

   **访问平台特有功能**：比如 Android 的指纹识别、iOS 的 3D Touch、获取设备的 IMEI 号等。

2. 

   **使用成熟的原生 SDK**：比如你想集成一个强大的第三方支付 SDK、地图 SDK 或直播 SDK，而这些 SDK 可能只提供了原生（Java/Object-C/Swift）的版本。

3. 

   **深度定制 UI 或性能优化**：当 H5 或小程序组件无法实现某些特殊的、高性能要求的 UI 效果时。

在这些情况下，你就可以通过 Native.js 来直接“指挥”原生代码去完成这些任务。

------

### Native.js 的工作原理

它的工作原理可以类比为 **“反射”** 或 **“动态执行”**。

1. 

   **编写 JS 代码**：你在 `.njs`文件或 `<script>`标签中编写 Native.js 代码。这些代码看起来很像在写 Java 或 Object-C。

2. 

   **运行时解析与转换**：UniApp 引擎在运行时捕获这些特定的 JS 代码。

3. 

   **桥接调用**：引擎通过 JSBridge 将你的 JS 调用“翻译”成对应的原生方法调用（如调用一个 Java 类的方法或发送一个 iOS 的消息）。

4. 

   **执行并返回结果**：原生代码执行完毕后，将结果再通过 JSBridge 返回给 JavaScript 环境。

**关键点**：Native.js 并不是真正的把 JS 编译成了原生代码，而是在运行时动态地实现了 JS 到原生的通信。

------

### 基本使用示例

假设我们想在 Android 上显示一个原生的 Toast 提示。

#### 1. 判断平台

首先需要确保代码只在 Android 环境下运行。

```
// #ifdef APP-PLUS
// 这段代码只会在 App 平台下编译
// #endif
```

#### 2. 编写 Native.js 代码 (Android)

创建一个 `.js`文件（例如 `native-android.js`）或在页面中编写：

```
// #ifdef APP-PLUS
const main = plus.android.runtimeMainActivity(); // 获取应用主Activity
const Context = plus.android.importClass('android.content.Context'); // 导入Java类
const Toast = plus.android.importClass('android.widget.Toast'); // 导入Toast类

// 调用原生Toast
function showNativeToast(msg) {
  // plus.android.invoke(对象, 方法名, 参数...)
  plus.android.invoke(
    Toast.makeText(main, msg, Toast.LENGTH_SHORT), // 创建Toast对象
    'show' // 调用其show方法
  );
}

export default {
  showNativeToast
};
// #endif
```

#### 3. 在 Vue 组件中使用

```
<template>
  <view>
    <button @click="handleClick">显示原生Toast</button>
  </view>
</template>

<script>
// 引入上面写的模块
import nativeAPI from '@/common/native-android.js';

export default {
  methods: {
    handleClick() {
      // #ifdef APP-PLUS
      nativeAPI.showNativeToast('Hello from Native.js!');
      // #endif
    }
  }
}
</script>
```

对于 iOS，写法类似，但使用的是 Object-C 的类和方法：

```
// #ifdef APP-PLUS
const NSObject = plus.ios.importClass('NSObject');
const UIAlertView = plus.ios.importClass('UIAlertView');
const alert = plus.ios.newObject({
  'cls': 'UIAlertView',
  'supercls': NSObject,
  'init[2]': ['提示', null, '确定']
});
plus.ios.invoke(alert, 'show');
// #endif
```

------

### Native.js 的优缺点

#### 优点：

- 

  **突破限制**：极大地扩展了 UniApp 的能力边界，可以实现非常丰富的原生功能。

- 

  **无需原生开发介入（一定程度）**：前端开发者只需了解基本的原生 API 知识，即可自行实现复杂功能，降低协作成本。

- 

  **灵活性高**：几乎可以调用任何原生 API。

#### 缺点：

- 

  **学习成本高**：你需要同时熟悉 JavaScript 和 Android/iOS 的原生开发知识。

- 

  **兼容性维护麻烦**：不同厂商的 Android 系统、不同版本的 iOS 可能导致原生 API 行为不一致，需要大量适配工作。

- 

  **破坏跨端性**：使用了 Native.js 的代码将**无法运行在 H5 和小程序平台**，必须做好条件编译 `#ifdef APP-PLUS`。这违背了 UniApp “一次编写，到处运行” 的部分理念。

- 

  **性能与稳定性风险**：不当的使用（如内存泄漏、线程阻塞）可能导致 App 崩溃。

- 

  **调试困难**：JS 层和原生层的错误追踪和联调比较复杂。

------

### 最佳实践与替代方案

由于 Native.js 的复杂性和高风险，**官方推荐使用优先级更高的方案**：

1. 

   **优先使用 UniApp 内置 API**：检查 [UniApp API 文档](https://uniapp.dcloud.net.cn/api/)，看是否已经提供了你需要的功能。

2. 

   **使用 uni-app 插件市场**：大量现成的原生插件（如各种 SDK 的封装）已经存在，直接购买或免费使用远比自己写 Native.js 高效可靠。

3. 

   **开发或使用 uni原生插件**：如果插件市场没有，可以找原生开发者开发一个 **uni-app 原生插件**。这种方式将原生代码封装成一个标准插件，在 JS 中调用更简单、更稳定、性能更好，且不影响其他端。

4. 

   **最后选择 Native.js**：只有在以上所有方案都无法满足，且你有能力处理其复杂性和风险时，才考虑使用 Native.js。

### 总结

| 特性       | 描述                                              |
| ---------- | ------------------------------------------------- |
| **是什么** | JS 调用原生 API 的桥接技术                        |
| **目的**   | 弥补跨平台框架在特定原生功能上的不足              |
| **原理**   | 运行时通过 JSBridge 动态调用原生方法              |
| **优点**   | 能力强大，扩展性好                                |
| **缺点**   | 学习成本高、兼容性差、破坏跨端、调试难            |
| **定位**   | **高级、备用方案**，应优先使用内置 API 和插件市场 |

## 使用native.js在android获取公共下载和公共文档目录路径

>具体用法请参考本站示例：https://gitee.com/dexterleslie/demonstration/tree/main/demo-uni-app/demo-nativejs-获取公共下载和文档目录路径

```javascript
getDownloadPath() {
    if (uni.getSystemInfoSync().platform !== 'android') {
        uni.showToast({
            title: '此功能仅支持Android平台',
            icon: 'none'
        });
        return;
    }

    try {
        // 使用native.js获取Android公共下载目录
        const Context = plus.android.importClass('android.content.Context');
        const Environment = plus.android.importClass('android.os.Environment');
        const Build = plus.android.importClass('android.os.Build');
        const activity = plus.android.runtimeMainActivity();

        const DIRECTORY_DOWNLOADS = 'Download';

        let downloadDir;
        // 检查Android版本，Android Q(API 29)及以上版本需要使用新的API
        if (Build.VERSION.SDK_INT >= 29) {
            // Android Q及以上版本获取应用私有下载目录
            downloadDir = activity.getExternalFilesDir(DIRECTORY_DOWNLOADS);
        } else {
            // Android Q以下版本获取公共下载目录
            downloadDir = Environment.getExternalStoragePublicDirectory(DIRECTORY_DOWNLOADS);
        }

        if (downloadDir) {
            // 使用native.js的方式获取路径，兼容不同的Android版本
            this.downloadPath = plus.android.invoke(downloadDir, 'getAbsolutePath');
            uni.showToast({
                title: '获取成功',
                icon: 'success'
            });
        } else {
            throw new Error('无法获取下载目录');
        }
    } catch (e) {
        uni.showToast({
            title: '获取失败：' + e.message,
            icon: 'none'
        });
        console.error('获取下载目录失败：', e);
    }
},

getDocumentPath() {
    if (uni.getSystemInfoSync().platform !== 'android') {
        uni.showToast({
            title: '此功能仅支持Android平台',
            icon: 'none'
        });
        return;
    }

    try {
        // 使用native.js获取Android公共文档目录
        const Context = plus.android.importClass('android.content.Context');
        const Environment = plus.android.importClass('android.os.Environment');
        const Build = plus.android.importClass('android.os.Build');
        const activity = plus.android.runtimeMainActivity();

        const DIRECTORY_DOCUMENTS = 'Document';

        let documentDir;
        // 检查Android版本，Android Q(API 29)及以上版本需要使用新的API
        if (Build.VERSION.SDK_INT >= 29) {
            // Android Q及以上版本获取应用私有文档目录
            documentDir = activity.getExternalFilesDir(DIRECTORY_DOCUMENTS);
        } else {
            // Android Q以下版本获取公共文档目录
            documentDir = Environment.getExternalStoragePublicDirectory(DIRECTORY_DOCUMENTS);
        }

        if (documentDir) {
            // 使用native.js的方式获取路径，兼容不同的Android版本
            this.documentPath = plus.android.invoke(documentDir, 'getAbsolutePath');
            uni.showToast({
                title: '获取成功',
                icon: 'success'
            });
        } else {
            throw new Error('无法获取文档目录');
        }
    } catch (e) {
        uni.showToast({
            title: '获取失败：' + e.message,
            icon: 'none'
        });
        console.error('获取文档目录失败：', e);
    }
}
```

