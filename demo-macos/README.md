## 删除隔离文件权限

> [参考链接](https://malcontentboffin.com/2019/12/macOS-Error-bin-bash-bad-interpreter-Operation-not-permitted.html)

显示文件隔离属性

```sh
xattr -l my-file.sh
```

删除文件隔离属性

```sh
xattr -d com.apple.quarantine my-file.sh
```

递归删除文件和目录的隔离属性

```sh
xattr -r -d com.apple.quarantine Kap.app/
```



## 远程桌面管理方案

> 提示：没有找到相关资料在 `macOS` 中安装 `rdp` 服务。没有尝试内置的远程管理功能(`ARD`)，因为 `windows` 没有 `ARD` 客户端软件。

启用内置的屏幕共享和文件共享功能，其中文件共享功能用于解决 `realvnc viewer` 无法复制粘贴内容时使用。

下载最新版本的 `realvnc viewer` 连接到服务器。



## 安装 `macOS` 虚拟机

> 提示：尝试使用 `virtualbox` 运行``macOS11``和 `macOS12` 未能够成功，提示 `“amd-v is not available”` 错误可能是因为使用 `amd cpu` 虚拟机化时需要特殊配置，没有做实验证明。



### `vmware workstation17` 安装 `macOS11` 和 `macOS12`

> 注意：`macOS` 虚拟机不能启用 `CPU虚拟化IOMMU(IO内存管理单元)` 特性，否则在启动过程中崩溃。

下载 `macOS11` 和 `macOS12` 操作系统 `iso`，`https://archive.org/details/macos_iso`

上传 `macOS11` 和 `macOS12` 操作系统 `iso` 到 `esxi7` 存储中

从 `https://github.com/DrDonk/unlocker` 下载最新版本 `unlocker427.zip` 并执行其中的 `windows/unlock.exe`，注意：执行 `unlock.exe` 时先关闭运行中的 `vmware workstation`。

重启 `windows` 操作系统后创建虚拟机，注意：固件类型选择 `UEFI`，否则无法从 `iso` 引导安装；操作系统类型选择苹果系统的相应版本。

针对 `AMD cpu` 需要编辑虚拟机对应的 `xxx.vmx` 文件并加入下面内容才能够启动 `macOS` 虚拟机，否则虚拟机一直停顿在苹果 `logo` 界面

> `AMD CPU` 安装 `macOS` 需要修改 `vmx` 文件，参考链接 `https://www.nakivo.com/blog/run-mac-os-on-vmware-esxi/`、`https://stackoverflow.com/questions/67025805/vmware-macos-bigsur-in-win10`

```properties
cpuid.0.eax = "0000:0000:0000:0000:0000:0000:0000:1011"
cpuid.0.ebx = "0111:0101:0110:1110:0110:0101:0100:0111"
cpuid.0.ecx = "0110:1100:0110:0101:0111:0100:0110:1110"
cpuid.0.edx = "0100:1001:0110:0101:0110:1110:0110:1001"
cpuid.1.eax = "0000:0000:0000:0001:0000:0110:0111:0001"
cpuid.1.ebx = "0000:0010:0000:0001:0000:1000:0000:0000"
cpuid.1.ecx = "1000:0010:1001:1000:0010:0010:0000:0011"
cpuid.1.edx = "0000:0111:1000:1011:1111:1011:1111:1111"
```

`vmware workstation17` 破解序列号

> [序列号参考链接](https://gist.github.com/PurpleVibe32/30a802c3c8ec902e1487024cdea26251)

```
MC60H-DWHD5-H80U9-6V85M-8280D
```





### `esxi7 update3` 安装 `macOS11` 和 `macOS12`

> 参考链接 `https://vmscrub.com/installing-macos-12-monterey-on-vmware-esxi-7-update-3/`

下载 `macOS11` 和 `macOS12` 操作系统 `iso`，`https://archive.org/details/macos_iso`

上传 `macOS11` 和 `macOS12` 操作系统 `iso` 到 `esxi7` 存储中

从 `https://github.com/erickdimalanta/esxi-unlocker` 下载 `esxi-unlocker-master.zip`

上传 `esxi-unlocker-master.zip` 到 `esxi7` 存储后，`ssh exsi7` 主机解压 `zip`

```sh
unzip esxi-unlocker-master.zip
```

设置 `esxi-unlocker-master` 权限

```sh
chmod 775 -R esxi-unlocker-301/
```

检查是否已经安装过 `unlocker`，如果没有安装过 `unlocker` 下面命令会输出 `smcPresent = false`

```sh
./esxi-smctest.sh
```

安装 `unlocker`

```sh
./esxi-install.sh
```

重启 `esxi7` 服务器

```sh
reboot
```

重启成功后再次检查 `unlocker` 是否成功安装，如果成功安装下面命令会输出 `smcPresent = true`

```sh
./esxi-smctest.sh
```

针对 `AMD cpu` 需要编辑虚拟机对应的 `xxx.vmx` 文件并加入下面内容才能够启动 `macOS` 虚拟机，否则虚拟机一直停顿在苹果 `logo` 界面

> `AMD CPU` 安装 `macOS` 需要修改 `vmx` 文件，参考链接 `https://www.nakivo.com/blog/run-mac-os-on-vmware-esxi/`、`https://stackoverflow.com/questions/67025805/vmware-macos-bigsur-in-win10`

```properties
cpuid.0.eax = "0000:0000:0000:0000:0000:0000:0000:1011"
cpuid.0.ebx = "0111:0101:0110:1110:0110:0101:0100:0111"
cpuid.0.ecx = "0110:1100:0110:0101:0111:0100:0110:1110"
cpuid.0.edx = "0100:1001:0110:0101:0110:1110:0110:1001"
cpuid.1.eax = "0000:0000:0000:0001:0000:0110:0111:0001"
cpuid.1.ebx = "0000:0010:0000:0001:0000:1000:0000:0000"
cpuid.1.ecx = "1000:0010:1001:1000:0010:0010:0000:0011"
cpuid.1.edx = "0000:0111:1000:1011:1111:1011:1111:1111"
```



## `macOS` 虚拟机和宿主机共享文件夹

>`https://blog.csdn.net/qq_30386941/article/details/126457834`

登录到宿主机

导航到`虚拟机设置`>`选项`>`共享文件夹`功能，设置信息如下：

- 文件夹共享选择`总是启用`
- 文件夹添加宿主机要共享的文件夹

重启 `macOS` 虚拟机

登录到 `macOS` 虚拟机

导航到 `访达` > `偏好设置` 功能，设置信息如下：

- 已连接的服务器 `勾选` 状态

此时桌面就会出现挂载的共享目录



## `Xcode` 和 `macOS` 兼容阵列

>`https://developer.apple.com/support/xcode/`

| Xcode Version   | Minimum OS Required                                          |
| --------------- | ------------------------------------------------------------ |
| Xcode 16.2 beta | macOS Sonoma 14.5                                            |
| Xcode 16.1      | macOS Sonoma 14.5                                            |
| Xcode 16        | macOS Sonoma 14.5                                            |
| Xcode 15.4      | macOS Sonoma 14                                              |
| Xcode 15.3      | macOS Sonoma 14                                              |
| Xcode 15.2      | macOS Ventura 13.5                                           |
| Xcode 15.1**    | macOS Ventura 13.5                                           |
| Xcode 15 beta 8 | macOS Ventura 13.4                                           |
| Xcode 15.0.x    | macOS Ventura 13.5                                           |
| Xcode 14.3.1    | macOS Ventura 13                                             |
| Xcode 14.3*     | macOS Ventura 13                                             |
| Xcode 14.2      | macOS Monterey 12.5                                          |
| Xcode 14.1      | macOS Monterey 12.5                                          |
| Xcode 14.0.x    | macOS Monterey 12.5                                          |
| Xcode 13.4      | macOS Monterey 12                                            |
| Xcode 13.3      | macOS Monterey 12                                            |
| Xcode 13.2      | macOS Big Sur 11.3                                           |
| Xcode 13.1      | macOS Big Sur 11.3                                           |
| Xcode 13        | macOS Big Sur 11.3                                           |
| Xcode 12.5.1    | macOS Big Sur 11                                             |
| Xcode 12.5      | macOS Big Sur 11                                             |
| Xcode 12.4      | macOS Catalina 10.15.4 (Intel-based Mac)、macOS Big Sur 11(Apple silicon Mac) |
| Xcode 12.3      | macOS Catalina 10.15.4 (Intel-based Mac)、macOS Big Sur 11(Apple silicon Mac) |
| Xcode 12.2      | macOS Catalina 10.15.4 (Intel-based Mac)、macOS Big Sur 11(Apple silicon Mac) |
| Xcode 12.1      | macOS Catalina 10.15.4 (Intel-based Mac)、macOS Big Sur 11(Apple silicon Mac) |
| Xcode 12        | macOS Catalina 10.15.4 (Intel-based Mac)                     |
| Xcode 11.7      | macOS Catalina 10.15.2                                       |
| Xcode 11.6      | macOS Catalina 10.15.2                                       |
| Xcode 11.5      | macOS Catalina 10.15.2                                       |
| Xcode 11.4.x    | macOS Catalina 10.15.2                                       |
| Xcode 11.3.x    | macOS Mojave 10.14.4                                         |
| Xcode 11.2.x    | macOS Mojave 10.14.4                                         |
| Xcode 11.1      | macOS Mojave 10.14.4                                         |
| Xcode 11        | macOS Mojave 10.14.4                                         |
| Xcode 10.3      | macOS Mojave 10.14.3                                         |
| Xcode 10.2.x    | macOS Mojave 10.14.3                                         |



## 安装 `Xcode`

访问 `https://developer.apple.com/downloads/` 使用开发者帐号登录网站

`macOS 12.6.1` 下载对应的 `Xcode 14.2`（文件后缀是 `xib`）

双击 `xib` 文件安装 `Xcode`（安装过程需要解压 `xib` 文件过程比较耗时），把解压后的 `Xcode` 目录托放至 `Applications` 分类中。



## 安装 `sourcetree`

> 提醒：下载最新的 `sourcetree macOS` 客户端会自动支持 `git-lfs`

下载 `https://www.sourcetreeapp.com/`

解压 `sourcetree` 后，拖动 `sourcetree` 到 `Applications` 分类中



## 安装搜狗拼音输入法

访问搜狗拼音输入法官方网站 `https://shurufa.sogou.com/`，下载 `macOS` 版本输入法的 `xxx.zip` 包后，双击运行再根据提示安装输入法即可。

使用 `ctrl+空格` 或者 `shift` 快捷键切换中文和英文输入状态。



## `Swift` 

Swift 是苹果公司开发的一种通用、多范式的编程语言，主要用于构建苹果生态系统（iOS、macOS、watchOS、tvOS 等）的应用程序。自 2014 年在 WWDC（苹果全球开发者大会）上首次发布以来，Swift 凭借其**安全、简洁、高效**的特性，迅速成为苹果开发者的首选语言，并逐步扩展到服务器端、脚本等领域。


### **一、Swift 的核心特点**
1. **安全优先**  
   Swift 设计时重点考虑了代码的安全性，例如通过「可选类型（Optionals）」强制处理空值（避免 Objective-C 中常见的「空指针崩溃」）；类型推断（Type Inference）减少手动声明类型的冗余，同时避免类型错误；严格的编译检查提前捕获潜在问题。

2. **简洁易读**  
   语法设计接近自然语言（如使用 `let`/`var` 声明常量/变量、`if let` 处理可选值），代码量通常比 Objective-C 更少。例如：
   
   ```swift
   // 传统 Objective-C 实现数组遍历
   NSArray *array = @[@"A", @"B", @"C"];
   for (NSString *str in array) {
       NSLog(@"%@", str);
   }
   
   // Swift 实现（更简洁）
   let array = ["A", "B", "C"]
   for str in array {
       print(str)
   }
   ```
   
3. **高性能**  
   Swift 基于 LLVM 编译器框架优化，运行效率接近 C/C++，部分场景性能优于 Objective-C。其支持值类型（如结构体、枚举）和引用类型（类）的灵活选择，通过值语义减少内存引用开销。

4. **多范式支持**  
   支持面向对象（OOP）、函数式（Functional）、协议导向（Protocol-Oriented）等多种编程范式。例如，协议扩展（Protocol Extensions）允许为协议添加默认实现，推动「组合优于继承」的设计思想。

5. **互操作性**  
   完全兼容 Objective-C（可混合编程），支持直接调用 Objective-C 代码或框架（如 UIKit、Core Data），降低了从 Objective-C 迁移的成本。


### **二、主要应用场景**
Swift 的核心生态围绕苹果平台，但也逐渐扩展到其他领域：
- **苹果应用开发**：iOS 应用（如微信、抖音国际版）、macOS 桌面软件（如 Final Cut Pro）、watchOS 手表应用、tvOS 电视应用等。
- **服务器端开发**：借助开源框架（如 Vapor、Kitura），Swift 可用于构建高性能后端服务（如 API 接口、微服务）。
- **脚本与工具**：通过命令行工具（`swift脚本`）或集成到 Xcode 的 Build Phases，实现自动化任务。
- **教育与研究**：因其语法友好，常被用作编程入门语言（如斯坦福大学课程）。


### **三、学习资源推荐**
- **官方文档**：苹果提供免费的《The Swift Programming Language》电子书（https://developer.apple.com/swift/），覆盖语法、标准库、最佳实践。
- **Swift Playgrounds**：苹果推出的交互式学习工具（支持 iPad 和 Mac），通过游戏化任务快速掌握基础。
- **实战项目**：通过开发 iOS 应用（如待办清单、天气 App）或参与开源项目（GitHub 上搜索 Swift 标签）积累经验。
- **社区与教程**：Ray Wenderlich、Hacking with Swift 等网站提供详细教程；Stack Overflow 可解决开发中的常见问题。


### **四、发展前景**
随着苹果生态的持续扩张（如 Vision Pro 等新设备）和 SwiftUI（声明式 UI 框架）的普及，Swift 的重要性进一步提升。苹果明确表示将长期投入 Swift 的优化（如性能提升、跨平台支持），使其成为连接移动端、桌面端、服务器端的「全栈语言」。对于希望进入苹果开发领域的开发者，Swift 是必备技能。

总结来说，Swift 是一门兼顾安全性、简洁性和高性能的现代编程语言，深度绑定苹果生态，同时在拓展新领域（如服务器端）方面展现出潜力，是当前及未来苹果开发的核心语言。



## `Swift Playground`

**Swift Playgrounds（ Swift 游乐场）** 是苹果（Apple）推出的一款**交互式编程学习与创作工具**，专为简化编程学习、激发创造力而设计。它支持 iPad 和 macOS 平台，通过「代码+实时可视化反馈」的融合模式，让用户（尤其是初学者）能更直观地理解代码逻辑，并快速验证创意。


### **核心定位**  
Swift Playgrounds 的核心是「**交互式探索**」——它不仅是编写代码的工具，更是一个「可运行的实验场」。用户编写的代码会立即以可视化形式呈现结果（如动画、图形、传感器数据变化等），降低了「编写→运行→调试」传统流程的学习成本。


### **关键特性**  
#### 1. **实时预览与交互反馈**  
   - 代码编写时，右侧预览窗口会**即时更新结果**（无需手动点击「运行」）。例如：  
     - 写一行 `print("Hello")`，预览窗口直接显示文字；  
     - 用 SwiftUI 绘制一个圆形，屏幕上立刻出现动态变化的圆；  
     - 调用 iPad 的陀螺仪传感器，倾斜设备时预览中的小球会随角度滚动。  
   - 这种「代码即效果」的反馈机制，让抽象逻辑变得直观可感。


#### 2. **教育导向的设计**  
   - **内置课程体系**：苹果官方提供了从基础语法（变量、循环）到进阶主题（SwiftUI、机器学习）的「游乐场课程」（Playground Books），适合编程零基础者系统学习。  
   - **引导式探索**：许多课程通过「问题→尝试→提示→解答」的流程，鼓励用户主动思考，而非被动阅读文档。  
   - **适合教学场景**：教师可用它设计编程课，学生通过实时反馈更易理解复杂概念（如递归、物理模拟）。


#### 3. **多平台与跨设备支持**  
   - **iPad 版**：深度整合 iPad 的硬件能力（如触控、传感器、Apple Pencil），支持手写输入代码（配合 Apple Pencil）、拖拽图形化元素（如颜色选择器），操作更贴近移动端习惯。  
   - **macOS 版**：功能更全面，支持连接外接设备（如相机、MIDI 键盘），适合需要更多计算资源的创作场景。  


#### 4. **灵活的创作能力**  
   - 支持**Swift 语言全特性**（从基础语法到 SwiftUI、Combine 等框架），可直接编写可发布的 iOS/macOS 应用原型。  
   - 允许导入资源（图片、音频、3D 模型），甚至集成机器学习模型（通过 Create ML），扩展创作边界。  
   - 支持「多页游乐场」（`.playgroundbook` 格式），适合制作分步骤的教程或项目文档。  


#### 5. **低门槛与趣味性**  
   - 无需配置复杂的开发环境（如 Xcode 的项目模板），打开即写，适合快速验证想法。  
   - 内置大量「趣味示例」（如游戏开发、数据可视化、音乐生成），激发用户探索欲。  


### **与传统开发工具的区别**  
| **维度**     | **Swift Playgrounds**          | **Xcode**（传统 IDE）            |
| ------------ | ------------------------------ | -------------------------------- |
| **目标用户** | 初学者、教育场景、快速创意探索 | 专业开发者、大型项目开发         |
| **交互方式** | 实时预览+可视化反馈            | 编译运行+调试器                  |
| **学习成本** | 极低（代码即效果）             | 较高（需熟悉项目结构、编译流程） |
| **功能侧重** | 探索、教育、轻量创作           | 生产级开发、性能优化、发布       |


### **典型使用场景**  
- **编程启蒙**：儿童或零基础学习者通过可视化反馈理解循环、条件判断等逻辑。  
- **创意实验**：快速验证 UI 设计（SwiftUI）、动画效果或小游戏原型。  
- **教学辅助**：教师用内置课程或自定义游乐场开展编程课。  
- **传感器应用**：利用 iPad 的摄像头、麦克风或陀螺仪实现互动项目（如手势控制动画）。  


### **总结**  
Swift Playgrounds 不是传统意义上的「代码编辑器」或「IDE」，而是一个**为「学习」和「探索」优化的编程生态**。它通过「所见即所得」的交互设计，让编程从「抽象的逻辑操作」变为「直观的创作过程」，无论是初学者入门还是开发者快速验证想法，都是高效的工具。



### `Xcode` 创建空白的 `Playground`

打开 `Xcode`，使用 `File` > `New` > `Playground` 选择 `Blank` 类型创建一个空白的 `Playground`，可以使用 `Playground` 调试 `Swift` 编程语言的语法

```swift
import UIKit

var greeting = "Hello, playground"
print(greeting)
```



## `Swift` 标记属性 - `@main`

在 Swift 中，`@main` 是一个**标记属性（Attribute）**，用于显式指定一个类型作为程序的**入口点（Entry Point）**。它是 Swift 5.3 及以上版本引入的特性，旨在统一不同类型程序（如命令行工具、iOS/macOS 应用）的入口定义方式，替代了早期依赖框架特定宏（如 `@UIApplicationMain`、`@NSApplicationMain`）的传统方式。


### **一、`@main` 的核心作用**
`@main` 的本质是告诉编译器：“被标记的这个类型是程序的起点，运行时可执行文件会从这里开始执行”。具体来说：

- 对于**命令行工具**（Command Line Tool）：`@main` 类型需提供一个静态的 `main()` 方法作为入口；
- 对于**SwiftUI 应用**（iOS 14+/macOS 11+）：`@main` 类型需遵循 `App` 协议，Xcode 会自动生成入口逻辑；
- 对于传统 UIKit/AppKit 应用：仍可使用 `@UIApplicationMain` 或 `@NSApplicationMain`，但 `@main` 提供了更通用的替代方案。


### **二、使用 `@main` 的两种典型场景**

#### **场景 1：命令行工具（纯 Swift 脚本或可执行文件）**  
对于不依赖 UIKit/AppKit 的命令行工具（如简单的计算工具、数据处理脚本），`@main` 类型需实现一个**静态的 `main()` 方法**，作为程序执行的起点。

**示例代码**：
```swift
// 一个计算斐波那契数列第 n 项的命令行工具
@main
struct FibonacciCalculator {
    // 静态 main() 方法是程序入口（必须无参数、无返回值）
    static func main() {
        let args = CommandLine.arguments // 获取命令行参数（第一个参数是程序名）
        guard args.count >= 2, let n = Int(args[1]) else {
            print("用法: \(args[0]) <n>")
            return
        }
        
        let result = fibonacci(n)
        print("斐波那契数列第 \(n) 项是: \(result)")
    }
    
    // 辅助函数：计算斐波那契数列
    private static func fibonacci(_ n: Int) -> Int {
        guard n > 0 else { return 0 }
        guard n > 1 else { return 1 }
        var a = 0, b = 1, temp: Int
        for _ in 2...n {
            temp = a + b
            a = b
            b = temp
        }
        return b
    }
}
```

**运行方式**：  
编译后执行 `./FibonacciCalculator 10`，输出 `斐波那契数列第 10 项是: 55`。


#### **场景 2：SwiftUI 应用（iOS/macOS 等苹果平台）**  
在 SwiftUI 中，`@main` 通常用于标记一个遵循 `App` 协议的类型，作为整个应用的入口。此时无需显式编写 `main()` 方法，Xcode 会自动生成底层逻辑，将应用生命周期与 SwiftUI 视图关联。

**示例代码**（iOS 14+）：
```swift
import SwiftUI

// @main 标记该类型为应用入口，遵循 App 协议
@main
struct MyApp: App {
    // 可选：@StateObject 管理全局状态（如数据模型）
    @StateObject private var appState = AppState()
    
    var body: some Scene {
        // 主窗口场景（iOS 中对应主界面）
        WindowGroup {
            ContentView()
                .environmentObject(appState) // 向子视图传递全局状态
        }
        
        // 可选：其他场景（如 macOS 的菜单栏、iPad 分屏）
        #if os(macOS)
        MenuBarExtra("我的应用", systemImage: "star") {
            SettingsView()
        }
        #endif
    }
}

// 全局状态管理类（示例）
class AppState: ObservableObject {
    @Published var user: User? // 用户信息（@Published 触发视图更新）
}

// 主视图（示例）
struct ContentView: View {
    @EnvironmentObject var appState: AppState
    
    var body: some View {
        NavigationView {
            List {
                if let user = appState.user {
                    Text("欢迎，\(user.name)")
                } else {
                    Button("登录") {
                        appState.user = User(name: "示例用户")
                    }
                }
            }
            .navigationTitle("首页")
        }
    }
}

// 用户模型（示例）
struct User: Identifiable {
    let id = UUID()
    var name: String
}
```

**关键说明**：  
- `@main` 标记的 `MyApp` 类型遵循 `App` 协议（定义在 `SwiftUI` 模块中）；  
- `body` 属性返回 `some Scene`，描述应用的所有场景（如主窗口、菜单栏等）；  
- 应用生命周期事件（如启动、进入后台）可通过 `onLaunch`、`onOpenURL` 等修饰符处理（需结合 `App` 协议的扩展）。


### **三、`@main` 与传统入口方式的对比**
在 Swift 5.3 之前，不同平台的入口点定义方式差异较大：
| 平台/框架         | 传统入口方式                                                 | 特点                                                         |
| ----------------- | ------------------------------------------------------------ | ------------------------------------------------------------ |
| iOS/macOS UI 应用 | `@UIApplicationMain`（iOS）<br>`@NSApplicationMain`（macOS） | 必须关联 `AppDelegate` 类，处理应用生命周期事件（如 `application(_:didFinishLaunchingWithOptions:)`） |
| 命令行工具        | 无显式标记，隐式查找 `main.swift` 文件                       | 需在 `main.swift` 中直接编写顶层代码（无法使用结构体/类的 `main()` 方法） |

而 `@main` 的优势在于：
- **统一语法**：无论平台（命令行、iOS、macOS 等），入口点均通过 `@main` 标记；  
- **灵活性**：支持自定义类型（结构体、类、枚举）作为入口，无需强制依赖 `AppDelegate`；  
- **清晰性**：显式声明入口类型，代码结构更易维护。


### **四、注意事项**
1. **唯一性**：一个程序中**只能有一个** `@main` 标记的类型，否则编译报错；  
2. **顶层类型**：`@main` 类型必须是**顶层类型**（不能是嵌套在其他类型内部的类型）；  
3. **`App` 协议的隐式入口**：在 SwiftUI 中，遵循 `App` 协议的类型无需显式编写 `static func main()`，编译器会自动生成入口逻辑；  
4. **命令行工具的 `main.swift`**：若不使用 `@main`，命令行工具的入口代码需直接写在 `main.swift` 文件中（顶层代码），但 `@main` 更推荐用于结构化的代码组织。


### **总结**  
`@main` 是 Swift 中定义程序入口点的标准化方式，适用于命令行工具、SwiftUI 应用等多种场景。通过 `@main`，开发者可以更清晰地组织代码，避免传统入口方式的隐式依赖，同时享受 Swift 语言的简洁性和类型安全特性。对于 SwiftUI 开发者，`@main` 结合 `App` 协议已成为现代苹果应用开发的标准实践。



## `Swift` 标记属性 - `@available`

在 Swift 中，`@available` 是一个**版本可用性标记属性（Availability Attribute）**，用于明确声明一段代码（如类、结构体、方法、属性等）**支持的最低平台版本或系统版本**。它的核心作用是帮助编译器和开发者管理**版本兼容性**，避免在旧版本系统或平台上使用不存在的 API，从而减少运行时崩溃的风险。


### **一、`@available` 的核心语法**
`@available` 的完整语法格式如下：

```swift
@available(<平台1> <版本1>, <平台2> <版本2>, ..., *)
```
其中：
- `<平台>`：指定支持的平台（如 `iOS`、`macOS`、`watchOS`、`tvOS`、`swift` 等）；
- `<版本>`：该平台支持的最低版本（如 `iOS 14.0`、`macOS 11.0`）；
- `*`（通配符）：可选参数，表示“其他未显式列出的平台”（需放在最后）。


### **二、`@available` 的核心作用**
#### 1. **声明代码的最低支持版本**  
通过 `@available`，开发者可以明确告知编译器：“这段代码仅在指定平台/版本的系统中可用”。如果代码被用在**低于指定版本**的环境中（如旧系统或未支持的平台），编译器会直接报错，阻止编译通过。

**示例**：  
假设你开发了一个仅在 iOS 14+ 可用的视图组件 `ModernButton`，可以用 `@available` 标记：
```swift
// 仅在 iOS 14.0+、macOS 11.0+ 及以上系统可用
@available(iOS 14.0, macOS 11.0, *)
struct ModernButton: View {
    var body: some View {
        Text("新按钮")
            .padding()
            .background(.blue)
            .foregroundColor(.white)
    }
}
```
如果尝试在 iOS 13 的项目中使用 `ModernButton`，编译器会报错：  
`'ModernButton' is only available in iOS 14.0 or newer`（`ModernButton` 仅在 iOS 14.0 或更高版本可用）。


#### 2. **配合编译器检查版本兼容性**  
Swift 编译器会根据 `@available` 标记，在代码使用场景中自动检查当前环境是否满足版本要求。例如：
- 如果在 iOS 13 的项目中调用 `ModernButton`，编译器会直接报错，避免运行时崩溃；
- 如果在 iOS 14+ 的项目中使用，代码正常编译，运行时不会出现问题。


#### 3. **支持多平台兼容性声明**  
当代码需要支持多个平台（如同时支持 iOS、macOS、watchOS），可以通过 `@available` 列出所有支持的平台及其最低版本，未列出的平台用 `*` 通配符表示“任意版本”。

**示例**：  
一个跨平台的工具类 `DataParser`，要求 iOS 15+、macOS 12+、watchOS 8+ 可用：
```swift
@available(iOS 15.0, macOS 12.0, watchOS 8.0, *)
class DataParser {
    static func parse(data: Data) -> String {
        // 解析逻辑（仅在指定版本以上可用）
        return String(data: data, encoding: .utf8) ?? ""
    }
}
```


### **三、`@available` 的典型使用场景**
#### 1. **适配新 API 或新特性**  
当使用苹果在某个系统版本中引入的新 API（如 iOS 14 的 `SwiftUI` 导航视图、iOS 15 的 `Async/Await`）时，必须用 `@available` 标记该 API 的可用版本，否则编译器会警告或报错（取决于 Xcode 的严格程度）。

**示例**（iOS 15 引入的 `NavigationStack`）：  
```swift
// 仅在 iOS 16.0+ 可用（假设导航栈在 iOS 16 更新）
@available(iOS 16.0, *)
struct ContentView: View {
    var body: some View {
        NavigationStack { // iOS 16 新组件
            Text("Hello, iOS 16!")
        }
    }
}
```


#### 2. **维护旧项目的向后兼容**  
如果项目需要支持多个系统版本（如同时兼容 iOS 13 和 iOS 16），可以通过 `@available` 区分新旧代码路径。例如：
```swift
// 旧版本兼容逻辑（iOS 13+）
func oldStyleButton() -> some View {
    Button("旧按钮") { /* ... */ }
}

// 新版本优化逻辑（iOS 14+）
@available(iOS 14.0, *)
func newStyleButton() -> some View {
    ModernButton() // 使用 iOS 14+ 的新组件
}
```


#### 3. **标记实验性或内部 API**  
对于苹果提供的实验性 API（如未正式发布的私有 API）或团队内部尚未稳定的功能，可以用 `@available` 限制其仅在特定版本或内部环境中使用，避免意外暴露。


### **四、`@available` vs `#available`**
新手容易混淆 `@available` 和 `#available`，它们的核心区别如下：

| **特性**     | **`@available`**                         | **`#available`**                                  |
| ------------ | ---------------------------------------- | ------------------------------------------------- |
| **作用位置** | 标记在代码声明（如类、方法、属性）的顶部 | 用于条件编译块（`if #available(...)`）            |
| **检查时机** | 编译阶段（编译器检查版本兼容性）         | 运行阶段（运行时检查系统版本）                    |
| **核心目的** | 声明代码的可用版本，阻止不兼容环境编译   | 在运行时根据系统版本执行不同代码逻辑              |
| **语法示例** | `@available(iOS 14.0, *)`                | `if #available(iOS 14.0, *) { ... } else { ... }` |


### **五、注意事项**
1. **通配符 `*` 的位置**：`*` 必须放在所有具体平台参数的最后，且至少需要有一个具体的平台版本（如 `@available(iOS 14.0, *)` 合法，但 `@available(*, iOS 14.0)` 不合法）。  
2. **多平台顺序无关**：平台参数的顺序不影响（如 `@available(macOS 11.0, iOS 14.0, *)` 和 `@available(iOS 14.0, macOS 11.0, *)` 等价）。  
3. **继承与重写**：子类重写父类方法时，`@available` 标记的版本不能低于父类方法的版本（否则编译器报错）。  
4. **与 `@available` 冲突的处理**：如果代码被多个 `@available` 标记（如扩展中重新标记），需确保所有标记的版本兼容（取交集）。  


### **总结**  
`@available` 是 Swift 中管理**版本兼容性**的核心工具，通过在代码声明处标记支持的最低平台/系统版本，帮助编译器提前拦截不兼容的代码使用，避免运行时崩溃。它是现代跨平台开发（尤其是苹果生态）中保证应用稳定性的重要手段，熟练使用 `@available` 是开发者进阶的关键技能之一。



### 示例

在 `SwiftUI` 库中 `public protocol App {` 声明为 `@available(iOS 14.0, macOS 11.0, tvOS 14.0, watchOS 7.0, *)`。

```swift
/// A type that represents the structure and behavior of an app.
///
/// Create an app by declaring a structure that conforms to the `App` protocol.
/// Implement the required ``SwiftUI/App/body-swift.property`` computed property
/// to define the app's content:
///
///     @main
///     struct MyApp: App {
///         var body: some Scene {
///             WindowGroup {
///                 Text("Hello, world!")
///             }
///         }
///     }
///
/// Precede the structure's declaration with the
/// [@main](https://docs.swift.org/swift-book/ReferenceManual/Attributes.html#ID626)
/// attribute to indicate that your custom `App` protocol conformer provides the
/// entry point into your app. The protocol provides a default implementation of
/// the ``SwiftUI/App/main()`` method that the system calls to launch your app.
/// You can have exactly one entry point among all of your app's files.
///
/// Compose the app's body from instances that conform to the ``SwiftUI/Scene``
/// protocol. Each scene contains the root view of a view hierarchy and has a
/// life cycle managed by the system. SwiftUI provides some concrete scene types
/// to handle common scenarios, like for displaying documents or settings. You
/// can also create custom scenes.
///
///     @main
///     struct Mail: App {
///         var body: some Scene {
///             WindowGroup {
///                 MailViewer()
///             }
///             Settings {
///                 SettingsView()
///             }
///         }
///     }
///
/// You can declare state in your app to share across all of its scenes. For
/// example, you can use the ``SwiftUI/StateObject`` attribute to initialize a
/// data model, and then provide that model on a view input as an
/// ``SwiftUI/ObservedObject`` or through the environment as an
/// ``SwiftUI/EnvironmentObject`` to scenes in the app:
///
///     @main
///     struct Mail: App {
///         @StateObject private var model = MailModel()
///
///         var body: some Scene {
///             WindowGroup {
///                 MailViewer()
///                     .environmentObject(model) // Passed through the environment.
///             }
///             Settings {
///                 SettingsView(model: model) // Passed as an observed object.
///             }
///         }
///     }
///
@available(iOS 14.0, macOS 11.0, tvOS 14.0, watchOS 7.0, *)
public protocol App {

    /// The type of scene representing the content of the app.
    ///
    /// When you create a custom app, Swift infers this type from your
    /// implementation of the required ``SwiftUI/App/body-swift.property``
    /// property.
    associatedtype Body : Scene

    /// The content and behavior of the app.
    ///
    /// For any app that you create, provide a computed `body` property that
    /// defines your app's scenes, which are instances that conform to the
    /// ``SwiftUI/Scene`` protocol. For example, you can create a simple app
    /// with a single scene containing a single view:
    ///
    ///     @main
    ///     struct MyApp: App {
    ///         var body: some Scene {
    ///             WindowGroup {
    ///                 Text("Hello, world!")
    ///             }
    ///         }
    ///     }
    ///
    /// Swift infers the app's ``SwiftUI/App/Body-swift.associatedtype``
    /// associated type based on the scene provided by the `body` property.
    @SceneBuilder @MainActor var body: Self.Body { get }

    /// Creates an instance of the app using the body that you define for its
    /// content.
    ///
    /// Swift synthesizes a default initializer for structures that don't
    /// provide one. You typically rely on the default initializer for
    /// your app.
    @MainActor init()
}
```



## `Swift protocol`

在 Swift 中，`protocol`（协议）是一种**定义行为规范的类型**，它规定了“哪些功能必须被实现”，但不提供具体实现（除非通过扩展补充）。协议是 Swift 面向协议编程（Protocol-Oriented Programming, POP）的核心工具，用于替代或补充传统面向对象编程（OOP）中的继承机制，强调“组合优于继承”的设计思想。


### **一、协议的核心作用**
协议的本质是**“契约”**：它要求遵循该协议的类型（类、结构体、枚举）必须实现特定的功能（方法、属性、下标等）。通过协议，开发者可以：
- **规范行为**：统一不同类型的行为标准；
- **实现多态**：通过协议类型引用不同实例，调用相同方法但得到不同结果；
- **灵活组合功能**：通过协议组合（Protocol Composition）聚合多个小功能；
- **扩展默认实现**：通过协议扩展（Protocol Extension）为协议添加通用逻辑；
- **解耦代码**：降低类型间的直接依赖，提升可维护性。


### **二、协议的基础语法与使用**
#### 1. **定义协议**  
协议的声明以 `protocol` 关键字开头，后跟协议名（通常首字母大写）。协议内部可以定义：
- **属性**（必须指定是 `get`/`set` 还是仅 `get`）；
- **方法**（实例方法、静态方法）；
- **下标**（Subscript）；
- **关联类型**（Associated Type，用于泛型约束）；
- **初始化器**（Initializer）。

**示例：定义一个简单的视图协议**  
```swift
protocol View {
    // 要求遵循者必须有一个 body 属性（类型为 some View）
    var body: some View { get }
}
```


#### 2. **遵循协议**  
类型（类、结构体、枚举）通过 `:` 符号声明遵循某个协议，并实现协议要求的所有成员。

**示例：结构体遵循 `View` 协议**  
```swift
struct ContentView: View {
    var body: some View {
        Text("Hello, Protocol!") // 实现协议的 body 属性
    }
}
```


### **三、协议的核心特性与实际应用**

#### **特性 1：规范行为（强制实现要求）**  
协议的核心是“契约”：遵循协议的类型必须实现所有声明的成员（属性、方法等），否则编译器会报错。这确保了不同类型在行为上的一致性。

**示例：定义一个可比较的协议 `ComparableItem`**  
```swift
protocol ComparableItem {
    var id: Int { get } // 必须实现 id 属性
    func compareTo(_ other: ComparableItem) -> Bool // 必须实现比较方法
}

// 结构体遵循协议并实现成员
struct Product: ComparableItem {
    let id: Int
    let name: String
    
    func compareTo(_ other: ComparableItem) -> Bool {
        return self.id < other.id // 按 id 升序比较
    }
}

// 枚举遵循协议
enum UserAction: ComparableItem {
    case login
    case logout
    
    var id: Int { // 实现 id 属性
        switch self {
        case .login: return 1
        case .logout: return 2
        }
    }
    
    func compareTo(_ other: ComparableItem) -> Bool {
        return self.id < other.id // 按枚举关联值比较
    }
}
```


#### **特性 2：作为类型使用（多态）**  
协议可以作为类型（称为“协议类型”）来引用遵循它的实例，从而实现多态：无论实例具体属于哪个类型，只要遵循同一协议，就可以用统一的方式调用协议方法。

**示例：用协议类型统一处理不同对象**  
```swift
// 定义协议
protocol SoundMaker {
    func makeSound()
}

// 狗遵循协议
struct Dog: SoundMaker {
    func makeSound() {
        print("汪汪！")
    }
}

// 猫遵循协议
struct Cat: SoundMaker {
    func makeSound() {
        print("喵喵～")
    }
}

// 函数接收协议类型参数，统一处理
func playSound(_ maker: SoundMaker) {
    maker.makeSound() // 调用协议方法，实际执行具体类型的实现
}

// 使用示例
let dog = Dog()
let cat = Cat()

playSound(dog) // 输出：汪汪！
playSound(cat) // 输出：喵喵～
```


#### **特性 3：协议扩展（添加默认实现）**  
Swift 允许通过 `extension` 为协议添加**默认实现**（Default Implementation），这样遵循协议的类型可以选择是否覆盖默认逻辑。这是 Swift 协议区别于其他语言接口的核心特性，极大提升了灵活性。

**示例：为 `SoundMaker` 添加默认叫声**  
```swift
extension SoundMaker {
    func makeSound() {
        print("...（默认无声）") // 默认实现
    }
}

// 鸟遵循协议但不实现 makeSound，使用默认实现
struct Bird: SoundMaker {}

// 鸭子遵循协议并覆盖默认实现
struct Duck: SoundMaker {
    func makeSound() {
        print("嘎嘎～")
    }
}

// 使用示例
let bird = Bird()
bird.makeSound() // 输出：...（默认无声）

let duck = Duck()
duck.makeSound() // 输出：嘎嘎～
```


#### **特性 4：协议组合（Protocol Composition）**  
通过 `&` 符号可以将多个协议组合成一个新的“复合协议”，要求类型同时遵循所有组合的协议。这避免了为每个功能组合创建新协议，提升了代码复用性。

**示例：组合 `SoundMaker` 和 `Movable` 协议**  
```swift
protocol Movable {
    func move()
}

// 组合协议：要求同时遵循 SoundMaker 和 Movable
typealias Animal = SoundMaker & Movable

// 狗遵循组合协议
struct Dog: Animal {
    func makeSound() { print("汪汪！") }
    func move() { print("狗在奔跑...") }
}

// 使用组合协议类型
func animalAction(_ animal: Animal) {
    animal.makeSound()
    animal.move()
}

let dog = Dog()
animalAction(dog) 
// 输出：
// 汪汪！
// 狗在奔跑...
```


#### **特性 5：关联类型（Associated Type）**  
关联类型允许协议定义“占位符类型”，具体类型由遵循协议的类型决定。它让协议更具通用性，适用于需要泛型的场景（如集合、算法）。

**示例：定义泛型容器协议 `Container`**  
```swift
protocol Container {
    associatedtype Item // 关联类型（占位符）
    var items: [Item] { get set } // 存储 Item 类型的数组
    mutating func append(_ item: Item) // 添加元素的方法
}

// 数组遵循 Container 协议（自动推断 Item 为 Int）
extension Array: Container where Element == Int {
    var items: [Int] { self }
    mutating func append(_ item: Int) {
        self.append(item)
    }
}

// 自定义栈结构遵循 Container 协议（Item 为 String）
struct StringStack: Container {
    typealias Item = String // 显式指定关联类型
    var items: [String] = []
    
    mutating func append(_ item: String) {
        items.append(item)
    }
}
```


### **四、协议 vs 继承（Inheritance）**
传统面向对象编程中，继承（Inheritance）通过“父类-子类”关系复用代码，但存在以下问题：
- 多层继承导致“脆弱基类”（Base Class Fragility）：修改父类可能影响所有子类；
- 类型耦合：子类必须与父类绑定，无法灵活组合功能；
- 单继承限制：多数语言（如 Swift）不支持多继承。

协议通过“行为规范”替代继承，解决了这些问题：
- **解耦**：类型只需遵循协议，无需依赖具体父类；
- **灵活组合**：通过协议组合实现多功能聚合；
- **无层级限制**：一个类型可以遵循多个协议，无继承层级限制。


### **五、典型应用场景**
协议在 Swift 开发中无处不在，以下是常见场景：

#### 1. **UI 开发（SwiftUI）**  
SwiftUI 的核心视图 `View` 就是一个协议，要求遵循者实现 `body` 属性。通过协议，SwiftUI 可以统一处理所有视图类型（如 `Text`、`Button`、自定义视图），并提供一致的渲染逻辑。

**示例（SwiftUI 视图）**：  
```swift
import SwiftUI

// 自定义视图遵循 View 协议
struct MyCustomView: View {
    var body: some View {
        HStack {
            Text("协议驱动 UI")
            Image(systemName: "star")
        }
    }
}
```


#### 2. **委托模式（Delegate Pattern）**  
iOS/macOS 开发中常用的委托模式（如 `UITableViewDelegate`）通过协议定义回调接口，解耦视图控制器与具体逻辑。

**示例（UITableView 委托）**：  
```swift
// 定义委托协议
protocol TableViewDelegate: AnyObject {
    func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath)
}

// 视图控制器遵循委托协议
class MyViewController: UIViewController, UITableViewDelegate {
    func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath) {
        print("选中第 \(indexPath.row) 行")
    }
}
```


#### 3. **数据模型与序列化**  
协议可用于定义数据模型的通用行为（如 `Equatable` 用于判断相等，`Codable` 用于 JSON 序列化）。

**示例（Codable 协议）**：  
```swift
// 遵循 Codable 协议，自动获得 JSON 序列化能力
struct User: Codable {
    let id: Int
    let name: String
    let email: String
}

// 使用示例
let user = User(id: 1, name: "Alice", email: "alice@example.com")
let encoder = JSONEncoder()
let data = try encoder.encode(user) // 自动序列化为 JSON 数据
```


#### 4. **算法与泛型**  
协议（尤其是带关联类型的协议）可用于定义算法的通用接口，支持不同数据类型的实现。

**示例（排序算法协议）**：  
```swift
protocol Sortable {
    associatedtype Element: Comparable // 要求元素可比较
    func sort(_ array: inout [Element])
}

// 冒泡排序实现
struct BubbleSort: Sortable {
    func sort(_ array: inout [Element]) {
        for i in 0..<array.count {
            for j in 0..<array.count - i - 1 {
                if array[j] > array[j+1] {
                    array.swapAt(j, j+1)
                }
            }
        }
    }
}

// 使用示例
var numbers = [3, 1, 4, 2]
var sorter = BubbleSort()
sorter.sort(&numbers) // 排序后：[1, 2, 3, 4]
```


### **六、总结**
`protocol` 是 Swift 中**定义行为规范的核心工具**，通过协议，开发者可以实现：
- 类型行为的统一约束；
- 多态与代码复用；
- 灵活的功能组合（协议扩展、协议组合）；
- 解耦与低耦合设计。

在现代 Swift 开发（尤其是 SwiftUI、算法、架构设计）中，协议的重要性远超传统继承。掌握协议的使用是成为 Swift 高级开发者的关键技能之一。



## `Swift struct`

在 Swift 中，`struct`（结构体）是一种**值类型（Value Type）**的基础数据类型，用于封装**数据**和**操作数据的方法**。它是 Swift 面向对象编程（OOP）和函数式编程（FP）融合的核心工具之一，尤其在 SwiftUI、值语义设计中被广泛使用。


### **一、结构体的核心特性**
#### 1. **值类型语义**  
结构体是**值类型**，这意味着：
- 当结构体实例被赋值给新变量、作为参数传递，或存入集合（如数组）时，会**生成一个独立的副本**（深拷贝），修改副本不会影响原实例。  
- 对比**引用类型**（如类 `class`）：引用类型赋值时仅复制内存地址（浅拷贝），多个变量共享同一个实例，修改其中一个会影响所有关联变量。

**示例对比**：
```swift
// 结构体（值类型）
struct Person {
    var name: String
    var age: Int
}

var personA = Person(name: "Alice", age: 25)
var personB = personA // 赋值时生成副本
personB.age = 30      // 修改副本的 age

print(personA.age) // 输出 25（原实例未改变）
print(personB.age) // 输出 30（副本被修改）

// 类（引用类型）
class Animal {
    var species: String
    init(species: String) {
        self.species = species
    }
}

var animalX = Animal(species: "Cat")
var animalY = animalX // 赋值时复制引用（指向同一实例）
animalY.species = "Dog"

print(animalX.species) // 输出 "Dog"（原实例被修改）
print(animalY.species) // 输出 "Dog"
```


#### 2. **轻量级数据封装**  
结构体适合封装**简单、独立的数据模型**（如坐标、用户信息、配置项等），通过定义属性（存储数据）和方法（操作数据）实现功能。  
结构体的属性可以是任意类型（包括其他结构体、枚举、类等），方法可以是实例方法或静态方法。

**示例：定义一个表示“点”的结构体**  
```swift
struct Point {
    var x: Double
    var y: Double
    
    // 实例方法：计算到另一个点的距离
    func distance(to other: Point) -> Double {
        let dx = x - other.x
        let dy = y - other.y
        return sqrt(dx*dx + dy*dy)
    }
    
    // 静态方法：创建原点（0,0）
    static let origin = Point(x: 0, y: 0)
}

// 使用结构体
let p1 = Point(x: 3, y: 4)
let p2 = Point.origin
let distance = p1.distance(to: p2) // 计算 p1 到原点的距离（结果为 5）
```


#### 3. **默认初始化器**  
Swift 会为结构体**自动生成成员初始化器（Memberwise Initializer）**，无需手动编写。初始化器参数是结构体的所有属性，顺序与定义一致。  
如果自定义了初始化器，默认初始化器会被覆盖，但可以通过扩展重新添加。

**示例：结构体的初始化器**  

```swift
struct Rectangle {
    var width: Double
    var height: Double
    var area: Double { width * height } // 计算属性（依赖 width 和 height）
}

// 自动生成的成员初始化器
let rect = Rectangle(width: 10, height: 5)
print(rect.area) // 输出 50

// 自定义初始化器（覆盖默认初始化器）
extension Rectangle {
    init(side: Double) { // 正方形初始化器
        self.width = side
        self.height = side
    }
}

let square = Rectangle(side: 4)
print(square.area) // 输出 16
```


#### 4. **不可变性（Immutable）**  
结构体默认是**可变的**（通过 `var` 声明），但如果用 `let` 声明结构体实例，则其所有属性都不可修改（类似“冻结”）。这是 Swift 推荐的安全实践，避免意外修改。

**示例：用 `let` 声明的结构体不可变**  
```swift
let fixedPoint = Point(x: 1, y: 2)
fixedPoint.x = 3 // 编译错误！let 声明的结构体属性不可修改
```


### **二、结构体 vs 类（Class）**
结构体和类是 Swift 中两种主要的类型，但设计目标和适用场景不同。以下是核心区别：

| **特性**               | **结构体（Struct）**                       | **类（Class）**                              |
| ---------------------- | ------------------------------------------ | -------------------------------------------- |
| **类型**               | 值类型（赋值时复制）                       | 引用类型（赋值时共享实例）                   |
| **继承**               | 不支持继承（无法通过 `class` 继承结构体）  | 支持单继承（可通过 `class Sub: Super` 继承） |
| **初始化器**           | 自动生成成员初始化器（可自定义）           | 需手动编写初始化器（除非使用便利初始化器）   |
| **析构函数（deinit）** | 不支持（值类型生命周期由作用域自动管理）   | 支持（用于释放资源，如释放文件句柄）         |
| **多态**               | 不支持（无继承则无多态）                   | 支持（通过继承和重写实现多态）               |
| **内存管理**           | 由编译器自动管理（栈或堆，无需手动干预）   | 由引用计数（ARC）管理（需注意循环引用）      |
| **适用场景**           | 轻量级数据模型、值语义需求、无需继承的场景 | 复杂对象、需要共享状态、依赖继承/多态的场景  |


### **三、结构体的典型应用场景**
结构体在 Swift 开发中应用广泛，尤其在以下场景：

#### 1. **数据模型（Data Model）**  
结构体适合表示“不可变或轻量级的独立数据”，例如用户信息、商品属性、配置项等。  
**示例（用户模型）**：
```swift
struct User {
    let id: UUID       // 不可变 ID（用 let 保证安全）
    var username: String
    var email: String
    var isPremium: Bool = false // 默认非会员
}
```


#### 2. **SwiftUI 视图（View）**  
SwiftUI 的核心设计基于“值语义”，推荐用结构体定义视图（遵循 `View` 协议）。因为结构体是值类型，修改视图属性时会生成新实例，触发界面自动更新（无需手动操作）。  
**示例（SwiftUI 视图）**：
```swift
import SwiftUI

struct ProfileView: View {
    let user: User  // 值类型属性（不可变）
    @State private var isEditing = false // 可变状态（用 @State 标记）
    
    var body: some View {
        VStack {
            Text(user.username)
                .font(.title)
            Button("编辑资料") {
                isEditing = true
            }
        }
    }
}
```


#### 3. **函数式编程（FP）**  
结构体的值语义天然支持函数式编程中的“不可变数据”和“纯函数”设计。例如，通过结构体的 `map`、`filter` 等方法处理集合时，不会修改原数据，而是返回新实例。


#### 4. **性能敏感场景**  
值类型（结构体）通常存储在栈上（或堆上的小对象），访问速度更快；而引用类型（类）需通过指针访问堆内存，可能引入额外开销。对于高频使用的小数据（如坐标、颜色），结构体更高效。


### **四、结构体的限制**
尽管结构体强大，但也有局限性：
- **不支持继承**：无法通过结构体实现多态（但可通过协议 `Protocol` 实现类似效果，如协议扩展）；  
- **无法使用析构函数**：无法手动释放资源（如文件句柄、网络连接）；  
- **复杂状态管理**：若需多个实例共享状态（如全局计数器），引用类型（类）更合适。


### **总结**  
`struct` 是 Swift 中**值类型的核心容器**，适合封装轻量级数据、实现值语义和函数式编程。它与类（`class`）互补，前者强调安全和不可变性，后者强调共享和继承。在 SwiftUI、数据模型设计等场景中，结构体是 Swift 开发者的首选工具。



## `Swift` 计算属性

在 Swift 中，**计算属性（Computed Property）** 是结构体（`struct`）、类（`class`）或枚举（`enum`）中一种特殊的属性形式。它**不直接存储值**，而是通过 `get`（读取时）和可选的 `set`（写入时）方法动态计算或修改值。其核心作用是封装逻辑，提供对其他属性的便捷访问或派生值。


### 一、计算属性的核心特点
- **无存储功能**：计算属性本身不占用内存存储值，每次访问时通过 `get` 方法实时计算。
- **依赖其他属性**：计算逻辑通常基于结构体的其他存储属性（Stored Property）或其他计算属性。
- **封装逻辑**：将值的计算或验证逻辑封装在属性中，使代码更简洁、数据更一致。


### 二、基本语法与 `get`/`set` 方法
计算属性的定义语法如下，必须包含 `get` 方法（可省略写法），可选包含 `set` 方法：

```swift
struct YourStruct {
    // 存储属性（直接存储值）
    var storedProperty: Int
    
    // 计算属性（通过 get/set 动态计算）
    var computedProperty: Int {
        get {
            // 读取时执行的逻辑（必须返回与属性类型匹配的值）
            return storedProperty * 2  // 示例：返回存储属性的 2 倍
        }
        set(newValue) {  // set 方法可选（若不需要修改其他属性，可省略）
            // 写入时执行的逻辑（通常用于更新其他存储属性）
            storedProperty = newValue / 2  // 示例：根据新值反推存储属性
        }
    }
}
```


### 三、关键细节与使用场景

#### 1. 只读计算属性（省略 `get`）
如果计算属性**仅需要读取**（无需修改），可以省略 `get` 关键字，直接用闭包体返回值（语法糖）：

```swift
struct Circle {
    var radius: Double  // 存储属性：半径
    
    // 只读计算属性：直径（无需 set）
    var diameter: Double {
        radius * 2  // 等价于 get { return radius * 2 }
    }
}

let circle = Circle(radius: 5)
print(circle.diameter)  // 输出 10（自动调用 get 方法）
// circle.diameter = 20  ❌ 报错：只读属性无法赋值
```


#### 2. `set` 方法的作用
`set` 方法允许通过设置计算属性的值，间接修改其他存储属性。此时 `set` 的参数默认名为 `newValue`（也可自定义名称）：

```swift
struct Rectangle {
    var width: Double
    var height: Double
    
    // 计算属性：面积（可读可写）
    var area: Double {
        get {
            width * height  // 读取时计算面积
        }
        set(newArea) {  // 自定义参数名（也可用 newValue）
            // 假设宽度不变，通过面积反推高度
            height = newArea / width
        }
    }
}

var rect = Rectangle(width: 3, height: 4)
print(rect.area)  // 输出 12（调用 get）
rect.area = 30    // 调用 set，height 被修改为 30 / 3 = 10
print(rect.height)  // 输出 10
```


#### 3. 计算属性的“实时性”
计算属性的值**实时依赖其他存储属性**。当依赖的存储属性变化时，计算属性的值会自动更新：

```swift
struct Person {
    var age: Int
    var isAdult: Bool {  // 依赖 age 的计算属性
        age >= 18
    }
}

var person = Person(age: 16)
print(person.isAdult)  // 输出 false
person.age = 18
print(person.isAdult)  // 输出 true（自动更新）
```


#### 4. 无副作用原则
计算属性的 `get` 方法应**避免副作用**（如修改其他属性、打印日志、网络请求等）。因为 `get` 可能在任意场景被调用（如打印、条件判断），副作用会导致不可预测的行为。


### 四、计算属性 vs 存储属性 vs 方法
| 特性           | 存储属性（Stored）                       | 计算属性（Computed）                     | 方法（Method）                        |
| -------------- | ---------------------------------------- | ---------------------------------------- | ------------------------------------- |
| **是否存储值** | 是（内存中分配空间）                     | 否（动态计算）                           | 否                                    |
| **核心作用**   | 存储数据                                 | 封装派生值或逻辑                         | 封装复杂操作                          |
| **可变性**     | 可声明为 `var`（可变）或 `let`（不可变） | 可声明为 `var`（可读写）或 `let`（只读） | 可声明为 `mutating`（修改自身）或普通 |
| **调用方式**   | 直接访问（如 `obj.age`）                 | 直接访问（如 `obj.area`）                | 函数调用（如 `obj.doSomething()`）    |


### 五、实际应用场景举例
#### 场景 1：单位转换
将摄氏温度转换为华氏温度，通过计算属性封装转换逻辑：

```swift
struct Temperature {
    var celsius: Double  // 摄氏温度（存储属性）
    
    // 华氏温度（计算属性）
    var fahrenheit: Double {
        get {
            celsius * 9/5 + 32
        }
        set(fTemp) {
            celsius = (fTemp - 32) * 5/9
        }
    }
}

var temp = Temperature(celsius: 25)
print(temp.fahrenheit)  // 输出 77.0
temp.fahrenheit = 86
print(temp.celsius)     // 输出 30.0（自动反推）
```


#### 场景 2：数据验证
确保存储的字符串长度不超过限制，通过计算属性提供安全的访问接口：

```swift
struct User {
    private var _username: String  // 私有存储属性（避免直接修改）
    
    var username: String {
        get {
            _username
        }
        set {
            // 限制用户名长度不超过 20 字符
            _username = String(newValue.prefix(20))
        }
    }
}

var user = User()
user.username = "ThisIsAVeryLongUsername"
print(user.username)  // 输出 "ThisIsAVeryLongUserna"（自动截断）
```


### 总结
计算属性是 Swift 中非常强大的工具，核心价值在于**将值的计算逻辑与数据本身绑定**，使代码更简洁、数据更一致。它适用于需要动态派生值、封装转换逻辑或控制属性读写的场景。使用时需注意：计算属性无存储功能，且 `get` 方法应保持无副作用。



### `struct` 示例

```swift
import UIKit

// 测试 struct 计算属性
struct ComputedProperty {
    var value: Double = 0.0
    
    var valueShadow: Double {
       get {
           return value * 2
       }
       
       set(newValue) {
           value = newValue + 1
       }
    }
}

var computedProperty = ComputedProperty(value: 10)
assert(computedProperty.value==10, "value不等于10")

computedProperty.valueShadow = 12
assert(computedProperty.value==13, "value不等于13")

var value = computedProperty.valueShadow
assert(value==13*2, "value不等于26")

```



### `protocol` 示例有 `get`、`set` 时

>强制 `struct` 必须实现 `protocol` 中计算属性的 `get`、`set` 方法。

```swift
import UIKit

// 演示 protocol 计算属性
protocol Person {
    // 强制 struct name 计算属性必须实现 get、set 方法
    var name: String { get set }
}

struct Student: Person {
    var _name: String = ""
    
    var name: String {
        get {
            return self._name
        }

        set(name) {
            self._name = name
        }
    }
}

var student1 = Student()
student1.name = "Dexter"
assert(student1.name == "Dexter")

```



### `protocol` 示例只有 `get` 时

>提示：使用精简的写法实现 `get` 方法。

```swift
import UIKit

// 演示 protocol 计算属性
protocol Person {
    // 强制 struct name 计算属性必须实现 get 方法
    var name: String { get }
}

struct Student: Person {
    var _name: String = ""
    
    var name: String {
        // 正常写法
        // get {
        //    return self._name
        // }
        
        // 精简写法，模仿下面 body 计算属性的写法
        // @main
        // struct demoApp: App {
        //     var body: some Scene {
        //         WindowGroup {
        //             ContentView()
        //         }
        //     }
        // }
        self._name
    }
}

var student1 = Student(_name: "Dexter")
assert(student1.name == "Dexter")

```



## `Swift var body: some Scene {` 中的 `some`

在 SwiftUI 中，`var body: some Scene { ... }` 里的 `some` 是一个**存在类型修饰符（Existential Type Modifier）**，用于明确告诉编译器：“`body` 返回的是一个**具体类型**（Concrete Type），该类型满足 `Scene` 协议的要求”。它的核心作用是**平衡类型安全与灵活性**，同时避免使用“存在类型”（Existential Type）带来的性能开销。


### **一、背景：协议与存在类型（Existential Type）**
在 Swift 中，协议本身不能直接作为具体类型使用（除非协议没有关联类型）。当协议包含**关联类型（Associated Type）**时（如 `Scene` 协议），直接使用协议类型（如 `Scene`）会变成“存在类型”（用 `any Scene` 表示），此时编译器无法在编译期确定具体类型，只能通过动态分发（Dynamic Dispatch）调用方法，可能带来性能损失，且类型安全检查会被削弱。


### **二、`some` 的核心作用**
`some` 的本质是**“存在某个具体类型，该类型满足协议要求”**。它允许你在不暴露具体类型的情况下，告诉编译器“返回值是一个符合协议的具体类型”，从而：
1. **保证类型安全**：编译器会检查返回的具体类型是否完全满足协议的所有要求（包括关联类型、方法、属性等）；
2. **避免动态分发开销**：编译器在编译期已知具体类型，可直接使用静态分发（Static Dispatch），提升性能；
3. **支持协议扩展的默认实现**：如果协议通过扩展提供了默认方法，`some` 允许编译器选择具体类型的实现或默认实现。


### **三、`some` 在 `Scene` 协议中的具体应用**
`Scene` 是 SwiftUI 中定义应用场景的核心协议（如主窗口、菜单栏等），它包含关联类型 `Body`（要求返回 `some Scene`）。在 `App` 协议中，`body` 属性必须返回 `some Scene`，以确保返回的是一个具体、符合要求的场景类型。

#### **示例：`App` 协议的定义**
```swift
// SwiftUI 框架中的 App 协议（简化版）
protocol App {
    associatedtype Body: Scene // 关联类型 Body 必须是 Scene 的子类型
    var body: some Scene { get } // 返回具体类型的 Scene
}
```

#### **示例：自定义场景类型**
假设你定义了一个自定义场景 `SettingsScene`，它遵循 `Scene` 协议：
```swift
struct SettingsScene: Scene {
    var body: some Scene {
        // 具体场景内容（如设置窗口）
        WindowGroup {
            SettingsView()
        }
    }
}
```

#### **在 `App` 中使用 `some Scene`**
当实现 `App` 协议时，`body` 必须返回一个具体类型（如 `SettingsScene` 或系统提供的 `WindowGroup`），并用 `some Scene` 修饰：
```swift
@main
struct MyApp: App {
    var body: some Scene {
        WindowGroup { // WindowGroup 是系统提供的 Scene 子类型
            ContentView()
        }
        // 或自定义场景
        // SettingsScene()
    }
}
```


### **四、`some` vs `any`**
在 Swift 中，`any` 和 `some` 都用于修饰协议类型，但行为完全不同：

| **特性**                 | **`some T`**                              | **`any T`**                        |
| ------------------------ | ----------------------------------------- | ---------------------------------- |
| **类型含义**             | 存在一个**具体类型**满足 `T` 协议         | 任意类型（可能是 `T` 或其子类型）  |
| **类型检查**             | 编译期严格检查具体类型是否符合协议        | 运行时动态检查（可能崩溃）         |
| **性能**                 | 静态分发（高效）                          | 动态分发（可能有性能损耗）         |
| **关联类型支持**         | 要求关联类型有明确约束（如 `where` 子句） | 允许关联类型无约束（但可能不安全） |
| **SwiftUI 中的使用场景** | 视图、场景等需要严格类型安全的场景        | 极少使用（除非明确需要动态类型）   |


### **五、为什么 SwiftUI 选择 `some Scene`？**
在 SwiftUI 中，视图和场景的类型安全至关重要：
- **视图层级验证**：SwiftUI 的编译器需要验证视图的层级结构（如 `VStack` 只能包含 `View` 子类型），`some Scene` 确保返回的场景类型完全符合 `Scene` 协议的要求；
- **性能优化**：静态分发避免了动态查找方法的开销，提升应用流畅度；
- **避免运行时崩溃**：如果返回的类型不满足 `Scene` 协议（如遗漏必需的属性或方法），编译器会在编译期报错，而非运行时崩溃。


### **六、总结**
`var body: some Scene { ... }` 中的 `some` 是 Swift 中用于**存在类型的修饰符**，它的核心作用是：
- 确保返回值是一个**具体类型**，且完全满足 `Scene` 协议的所有要求；
- 提供编译期类型安全检查，避免运行时崩溃；
- 优化性能，通过静态分发替代动态分发。

在 SwiftUI 开发中，`some` 是保证视图和场景类型安全的关键工具，开发者需要始终使用 `some` 来修饰遵循协议的返回值（如 `some View`、`some Scene`），以确保代码的正确性和高效性。



## `Swift` 闭包

在 Swift 中，**闭包（Closure）** 是一种**自包含的、可传递的代码块**，它可以捕获并存储其所在上下文中的变量和常量（即“捕获值”），从而在后续执行时使用这些值。闭包的本质是「可调用的代码单元」，类似于函数，但更灵活——它不需要命名（匿名），且能动态捕获周围环境的上下文。


### **核心特性**  
闭包的核心特点可以概括为三点：  
1. **匿名性**：闭包通常没有显式的名称（但也可以赋值给变量/常量后间接使用名称）。  
2. **捕获上下文**：能访问并修改其定义时所在的变量、常量或其他闭包（即“捕获值”）。  
3. **可传递性**：可以作为参数传递给函数，或作为函数的返回值。  


### **闭包的语法形式**  
Swift 中闭包的语法经过优化，支持多种简化写法，核心结构如下：  

```swift
{ (参数列表) -> 返回值类型 in
    // 闭包体（执行的代码）
}
```

- `参数列表`：闭包接收的输入参数（类似函数参数）。  
- `-> 返回值类型`：可选，闭包执行后的返回值类型（若省略，Swift 会自动推断）。  
- `in`：分隔符，标志参数和返回值声明结束，闭包体开始。  


### **闭包的典型使用场景**  
闭包在 Swift 中广泛用于需要「传递行为」的场景，例如作为函数的回调、集合的排序规则、异步操作的完成处理等。以下是几个常见示例：  


#### **1. 作为函数参数（回调）**  
许多 Swift 标准库函数（如 `sorted(by:)`、`map(_:)`、`filter(_:)`）接受闭包作为参数，用于自定义行为。  

**示例：用闭包实现数组排序**  
```swift
let numbers = [3, 1, 4, 1, 5, 9]
// 闭包作为参数，定义排序规则（升序）
let sortedNumbers = numbers.sorted(by: { a, b in
    return a < b
})
print(sortedNumbers) // 输出：[1, 1, 3, 4, 5, 9]
```


#### **2. 捕获上下文中的变量**  
闭包可以「捕获」其定义时所在作用域中的变量或常量，并在后续执行时使用这些值（即使原作用域已结束）。  

**示例：闭包捕获外部变量**  
```swift
var count = 0
// 定义一个闭包，捕获外部的 count 变量
let incrementCount = {
    count += 1
    print("当前计数：\(count)")
}

incrementCount() // 输出：当前计数：1
incrementCount() // 输出：当前计数：2（count 被闭包保留并修改）
```


#### **3. 尾随闭包（Trailing Closure）**  
当闭包是函数的**最后一个参数**时，Swift 允许省略参数括号，并将闭包写在函数调用的大括号外（尾随闭包语法），使代码更易读。  

**示例：尾随闭包优化**  
```swift
// 函数定义（接受闭包作为最后一个参数）
func execute(after delay: TimeInterval, action: () -> Void) {
    DispatchQueue.main.asyncAfter(deadline: .now() + delay) {
        action()
    }
}

// 使用尾随闭包调用（省略 action 参数的括号）
execute(after: 2.0) {
    print("2秒后执行")
}
```


#### **4. 自动闭包（@autoclosure）**  
通过 `@autoclosure` 属性，Swift 可以自动将表达式包装为闭包，实现**延迟求值**（表达式在闭包执行时才会计算）。它通常用于需要「按需执行」的场景（如条件判断中的延迟计算）。  

**示例：自动闭包实现延迟求值**  
```swift
var shouldLog = true
func log(_ message: @autoclosure () -> String) {
    if shouldLog {
        print(message()) // 闭包在此处执行，message 表达式延迟计算
    }
}

log("这是一条日志") // 等价于传递闭包 { "这是一条日志" }
shouldLog = false
log("这条日志不会被打印") // 闭包被创建但不会执行
```


### **闭包的捕获行为**  
闭包捕获外部变量时，遵循以下规则：  
- **值类型（如 Int、结构体）**：闭包会复制一份值的副本（拷贝语义），后续对原变量的修改不影响闭包内的副本（除非闭包显式声明为 `inout` 参数）。  
- **引用类型（如类实例、数组）**：闭包会捕获引用（引用语义），闭包和原作用域共享同一实例，修改会影响双方。  


#### **避免循环引用**  
当闭包捕获 `self`（如类的实例方法中的闭包）时，若闭包被 `self` 持有（如作为属性），会导致**循环引用**（`self` 引用闭包，闭包又引用 `self`），造成内存泄漏。此时需用 `[weak self]` 或 `[unowned self]` 显式声明弱引用或无主引用。  

**示例：解决循环引用**  
```swift
class DataDownloader {
    var data: Data?
    var completion: (() -> Void)?
    
    func startDownload() {
        // 错误写法（循环引用）：
        // URLSession.shared.dataTask(with: url) { data in
        //     self.data = data // self 引用闭包，闭包引用 self → 循环引用
        // }
        
        // 正确写法（弱引用）：
        URLSession.shared.dataTask(with: url) { [weak self] data in
            self?.data = data // 弱引用避免循环引用
        }.resume()
    }
}
```


### **闭包与函数的关系**  
闭包和函数在 Swift 中都属于「可调用类型（Callable Type）」，语法和行为高度相似。本质上，**函数是特殊的闭包**（具名的闭包）。两者的主要区别是：  
- 函数有显式名称，闭包通常匿名（但可赋值给变量后间接命名）。  
- 闭包可以捕获上下文，而函数默认不捕获（除非通过参数传递上下文）。  


### **总结**  
Swift 闭包是一种灵活的「可传递代码块」，核心能力是**捕获上下文**和**作为参数传递行为**。它广泛应用于排序、回调、异步操作等场景，结合尾随闭包、自动闭包等语法优化，能写出简洁高效的代码。理解闭包的捕获规则（尤其是循环引用）是掌握 Swift 内存管理的关键。



### 尾随闭包

> 当闭包是函数的**最后一个参数**时，Swift 允许省略参数括号，并将闭包写在函数调用的大括号外（尾随闭包语法），使代码更易读。

```swift
import UIKit

func execute(callback: () -> Int) -> Int {
    return callback()
}

// 尾随闭包语法
var result = execute() {
    return 10*2
}
assert(result == 20)

```



## `Swift` 断言

在 Swift 中，**断言（Assertion）** 是一种调试工具，用于在代码运行时验证某个条件是否为真。如果条件不满足（即结果为 `false`），断言会立即终止程序并输出错误信息，帮助开发者快速定位问题。断言主要用于开发和测试阶段，**默认在发布（Release）模式中会被禁用**（避免影响性能）。


### 一、基本语法
Swift 的断言语法如下：
```swift
assert(condition: Bool, message: String? = nil)
```
或更简洁的形式（直接推断消息类型）：
```swift
assert(condition, message)
```
- `condition`：需要验证的布尔条件。若为 `false`，断言触发。
- `message`：可选的错误描述（字符串），断言触发时会随系统信息一起输出（可省略，默认会生成包含条件的位置信息）。


### 二、使用示例
#### 示例 1：基础断言
```swift
let age = -5
assert(age >= 0, "年龄不能为负数")  // 条件不满足，触发断言
```
输出（调试模式下）：
```
Fatal error: 年龄不能为负数: file YourFileName.swift, line X
```


#### 示例 2：检查数组索引有效性
```swift
let array = [10, 20, 30]
let index = 5

// 检查索引是否越界
assert(index < array.count, "索引 \(index) 超出数组范围（数组长度 \(array.count)）")

// 若 index >= array.count（如 5 >= 3），断言触发
```


#### 示例 3：省略消息（使用默认提示）
```swift
let value = 100
assert(value <= 50)  // 条件不满足，触发断言
```
输出（调试模式下）：
```
Fatal error: Assertion failed: value <= 50, file YourFileName.swift, line X
```


### 三、关键注意事项
1. **仅在调试阶段生效**  
   断言默认在调试构建（Debug）中启用，发布构建（Release）中会被编译器移除（条件检查不会执行）。因此，**不要用断言替代必要的运行时检查**（如用户输入验证）。

2. **条件应为“不可能失败”的情况**  
   断言用于捕捉代码中的逻辑错误（如“理论上不会发生的错误”），而不是处理预期可能发生的错误（如网络请求失败）。例如：
   ```swift
   // 正确用法：检查内部逻辑是否正确
   func calculateScore(points: Int) -> Int {
       assert(points >= 0, "分数不能为负数")  // 内部逻辑应保证 points 非负
       return points * 2
   }
   
   // 错误用法：用断言处理用户输入（不可靠）
   func processUserInput(input: Int) {
       assert(input > 0, "输入必须为正数")  // 用户可能输入负数，断言会被绕过
   }
   ```

3. **与 `fatalError` 的区别**  
   `fatalError(_:)` 是立即终止程序的函数（无论是否在调试模式），而断言仅在条件不满足且处于调试模式时触发。例如：
   ```swift
   // 断言：调试时触发，发布时忽略
   assert(false, "调试错误")
   
   // fatalError：始终触发
   fatalError("致命错误")
   ```


### 四、调试时查看断言信息
在 Xcode 中，断言触发时会停在断言所在的代码行，并在控制台输出详细信息（包括文件名、行号和自定义消息），方便快速定位问题。


### 总结
Swift 的断言是开发者的“自我检查工具”，用于确保代码逻辑在预期范围内运行。合理使用断言可以提高代码的健壮性，但需注意其仅在调试阶段生效，且不应替代必要的运行时错误处理。



## `Swift var` 和 `let` 区别

在 Swift 中，`var` 和 `let` 是用于声明变量的关键字，核心区别在于**可变性**（是否允许修改值）。以下是具体区别和使用场景的详细说明：


### 1. **核心区别：可变性**
- **`var`（变量）**：声明后可修改其值（重新赋值）。  
  适用于需要动态变化的数据（如计数器、用户输入等）。  
  ```swift
  var age = 20
  age = 21  // 合法：修改值
  ```

- **`let`（常量）**：声明时必须初始化，且之后**不可修改值**（重新赋值会报错）。  
  适用于固定不变的数据（如数学常数、配置参数等），强制保证数据的稳定性。  
  ```swift
  let pi = 3.14159
  pi = 3.14  // 报错：Cannot assign to value: 'pi' is a let constant
  ```


### 2. **初始化要求**
两者都**必须在首次使用前初始化**（即分配初始值），但后续行为不同：  
- `var` 初始化后可重新赋值。  
- `let` 初始化后不可重新赋值（但如果是引用类型，可修改其属性）。  


### 3. **对引用类型的特殊行为**
如果声明的是**引用类型**（如类 `Class` 的实例），`let` 常量仍允许修改其属性，但不能让常量指向另一个实例：  
```swift
class Person {
    var name: String
    init(name: String) {
        self.name = name
    }
}

// 用 let 声明一个类实例（引用类型）
let person = Person(name: "Alice")

// 允许修改实例的属性（因为常量指向的对象未变）
person.name = "Bob"  // 合法

// 不允许让常量指向新实例（因为会改变常量的绑定）
person = Person(name: "Charlie")  // 报错：Cannot assign to value: 'person' is a let constant
```


### 4. **类型推断与显式类型**
两者都支持 Swift 的**类型推断**（根据初始值自动推导类型），也可显式指定类型：  
```swift
var score = 95       // 推断为 Int 类型
var score: Int = 95  // 显式指定 Int 类型（效果相同）

let maxCount = 100   // 推断为 Int 类型
let maxCount: Int = 100  // 显式指定 Int 类型
```


### 5. **可选类型（Optional）的处理**
- `var` 可声明为可选类型（`Type?`），后续可赋值为 `nil` 或解包后的值：  
  ```swift
  var username: String? = nil  // 初始为 nil
  username = "Alice"           // 合法：赋值为非 nil
  ```

- `let` 若声明为可选类型，初始化后不可修改其值（包括不能从 `nil` 改为非 `nil`）：  
  ```swift
  let userId: String? = nil     // 初始为 nil
  userId = "123"                // 报错：Cannot assign to value: 'userId' is a let constant
  ```


### 6. **最佳实践**
Swift 社区强烈推荐**优先使用 `let`**，仅在需要修改值时使用 `var`。这能：  
- 减少意外修改的风险，提高代码可维护性。  
- 让编译器进行更多优化（如内联常量值）。  
- 明确标识“不变量”，使代码意图更清晰。  


### 总结表格
| 特性             | `var`（变量）              | `let`（常量）                |
| ---------------- | -------------------------- | ---------------------------- |
| 可变性           | 允许修改值                 | 初始化后不可修改值           |
| 初始化要求       | 必须初始化，后续可重新赋值 | 必须初始化，后续不可重新赋值 |
| 引用类型修改属性 | 允许                       | 允许（但不可指向新实例）     |
| 最佳实践         | 仅当需要修改时使用         | 优先使用（固定值）           |

通过合理使用 `var` 和 `let`，可以让代码更安全、简洁且易于理解。



### `let` 声明的存储属性

示例：

```swift
import UIKit

protocol Person {
    var name: String {
        get
    }
}

struct Student: Person {
    let name: String
}

var student1 = Student(name: "Dexter")
assert(student1.name == "Dexter")
```

为何 `Student` 不需要实现 `get` 访问器呢？

- 存储属性的 `let` 声明表示它是只读的存储属性（值初始化后不可修改）。
- 但即使是只读的存储属性，`Swift` 也会自动生成默认的 `get` 访问器，用于读取其当前值。因此，`Student` 的 `name` 属性虽然用 `let` 声明，但已经隐式包含了 `get` 访问器，完全满足协议 `Person` 对 `name` 的要求（只需可读）。
