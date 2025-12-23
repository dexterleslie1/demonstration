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

## uView概念

>官方介绍：https://uviewui.com/components/intro.html

uView是uni-app生态专用的UI框架，uni-app 是一个使用 Vue.js 开发所有前端应用的框架，开发者编写一套代码， 可发布到iOS、Android、H5、以及各种小程序(微信/支付宝/百度/头条/QQ/钉钉)等多个平台(引言自uni-app网)。但目前除微信小程序，其它小程序平台的兼容可能存在一些问题，后续会针对这方面持续优化。

## uView集成到uni-app

>提示：创建uni-app项目时选择Vue版本为2。
>
>详细用法请参考本站示例：https://gitee.com/dexterleslie/demonstration/tree/main/demo-uni-app/demo-uview-getting-started

安装uView依赖

```sh
npm install uview-ui@2.0.37
```

main.js中注册uView到Vue中

```javascript
// 注册uView组件到Vue中
import uView from 'uview-ui'
Vue.use(uView)
```

pages.json配置uView的easycom

```json
{
	"easycom": {
		"autoscan": true,
		"custom": {
			"^u-(.*)": "uview-ui/components/u-$1/u-$1.vue"
		}
	},
    ...
}
```

uni.scss配置uView UI主题变量

```scss
/* uView UI 主题变量 */
@import 'uview-ui/theme.scss';
```

index.vue中调用uView组件

```vue
<template>
	<u-calendar :show="show" :mode="mode" @confirm="confirm" @close="()=>{show = false}"></u-calendar>
</template>

<script>
	export default {
		data() {
			return {
				show: true,
				mode: 'range'
			}
		},
		methods: {
			confirm(e) {
				console.log(e);
				this.show = false
			}
		}
	}
</script>

```

## uView - Calendar日历

>官方文档：https://uviewui.com/components/calendar.html
>
>详细用法请参考本站示例：https://gitee.com/dexterleslie/demonstration/tree/main/demo-uni-app/demo-uview-calendar

