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



## `Xcode` 和项目版本不兼容

>[参考链接](http://www.10qianwan.com/articledetail/265489.html)

鼠标右击 `.xcodeproj` 文件 > 显示包内容 > 打开 `project.pbxproj` 文件，比较以前的版本号进行修改（比如：把 `objecversion=50` 修改 `objecversion=48` 即可打开工程）。



## 使用 `Xcode+iPad真机` 开发报告 `Could not locate device support files` 错误

### 错误信息

```
The run destination xx的iPad is not valid for Running the scheme 'demo'.

Could not locate device support files You may be able to resolve the issue by installing the latest version of Xcode from the Mac App Store or developer.apple.com.

[missing string: 869a8e318f07f3e2xxxxxxxxxxxxxxxxxx]
```

你遇到的错误是由于 **Xcode 缺少当前连接 iPad 所需的设备支持文件** 导致的。这通常发生在以下场景：你的 iPad 升级到了较新的 iOS 版本，而当前安装的 Xcode 版本过旧，不支持该 iOS 版本的设备支持文件。


### 具体原因分析

Xcode 的设备支持文件存储在路径：  
`/Applications/Xcode.app/Contents/Developer/Platforms/iPhoneOS.platform/DeviceSupport`  
每个 iOS 版本对应一个子文件夹（如 `17.0 (17A360)`）。当你的 iPad 运行的 iOS 版本高于 Xcode 内置支持的最高版本时，Xcode 无法找到对应的设备支持文件，就会报此错误。


### 解决方案（按优先级排序）

#### 1. **升级 Xcode 到最新版本**（推荐）

Xcode 新版本会内置对新 iOS 设备的支持。你的当前环境是 **macOS13.0.1 + Xcode14.2**，而 Xcode14.2 最高仅支持到 **iOS16.4**（根据苹果官方文档）。如果你的 iPad 已升级到 **iOS17 或更高版本**，必须升级 Xcode 到 **Xcode15 或更高** 才能支持。

- **升级方式**：  
  前往 https://apps.apple.com/cn/app/xcode/id497799835 搜索 Xcode，点击「更新」；或从 https://developer.apple.com/download/all/ 下载最新稳定版 Xcode（需登录开发者账号）。


#### 2. **手动添加缺失的设备支持文件**（若无法立即升级 Xcode）

如果暂时无法升级 Xcode，可以手动从其他设备复制缺失的设备支持文件到本地 Xcode 目录。

- **步骤**：  
  1. 确认 iPad 的 iOS 版本（设置 → 通用 → 关于本机 → 软件版本）。  
  2. 找到一台已安装 **支持该 iOS 版本的 Xcode** 的电脑（或从可靠来源获取对应版本的 `DeviceSupport` 文件夹）。  
  3. 将对应 iOS 版本的文件夹（如 `17.0 (17A360)`）复制到本地 Xcode 的 `DeviceSupport` 目录：  
     `/Applications/Xcode.app/Contents/Developer/Platforms/iPhoneOS.platform/DeviceSupport`  
  4. 重启 Xcode 并重新连接 iPad。


#### 3. **检查 iPad 与 Xcode 的兼容性**

如果你的 iPad 运行的 iOS 版本确实过高（如 iOS17），而 Xcode14.2 完全不支持（苹果通常要求 Xcode 版本 ≥ iOS 版本 - 1），则必须升级 Xcode。例如：  

- iOS17 需要 Xcode15 或更高版本支持。  


#### 4. **其他辅助操作**

- **重启设备与 Xcode**：有时临时缓存问题会导致设备未被识别，重启 iPad 和 Xcode 后重试。  
- **检查 USB 连接**：更换 USB 线或接口，确保 iPad 已信任当前 Mac（首次连接时 iPad 会弹出「信任此电脑」提示，需点击确认）。  


### 总结

最根本的解决方法是 **升级 Xcode 到与 iPad iOS 版本兼容的版本**。Xcode 的版本与 iOS 支持的对应关系可参考苹果官方文档：https://developer.apple.com/documentation/xcode-release-notes。



## `Xcode` 项目的文件和文件夹

>详细用法请参考本站 [示例](https://gitee.com/dexterleslie/demonstration/tree/main/demo-macos/iOS-group-folder)

有 `Group`、`Group without Folder`、`Folder References` 类型文件夹

- `Group`：会对应在文件系统中的目录结构，`build` 后的文件会被复制到项目根目录
- `Group without folder`：只是逻辑上分组目录，在文件系统中没有此目录存在，build后的文件会被复制到项目根目录
- `Folder references`：会对应文件系统中的目录结构，build后的文件会对应在相应的目录中

创建 `group` 分为 `New Group` 和 `New Group without Folder`，`New Group` 会在 `Xcode` 项目目录下创建 `folder`，`New Group without Folder` 不会在项目目录下创建 `folder`。

添加文件 `Copy items if needed` 选项

> https://www.cnblogs.com/wanghuaijun/p/5226685.html

勾选后，会自动复制一份相同的文件到你的工程中，引用的是复制后在工程目录中的位置。若不勾选，文件的引用位置则是文件的原位置（不建议这样做，如果该文件在工程外被删除，工程则无法引用，所以还是复制一份到工程中，这样更利于工程文件的管理）。

新增文件或者文件夹时 `Create Groups` 和 `Create folder references` 区别

> http://www.thomashanning.com/xcode-groups-folder-references/

`Create Groups` 生成文件夹为黄色，这意味着此文件夹不代表文件系统。因此，您可以将一个文件从组移动到另一个组中，而无需在文件系统中移动相应的文件。您可以将文件添加到文件系统的“真实”文件夹中，该文件将不会自动出现在项目导航器中。它只是一个虚拟文件夹。

`Create folder references` 生成文件夹为蓝色，现在该文件夹被一对一地映射到文件系统上的文件夹：如果您从文件系统上的文件夹中删除一个文件，那么它也将从项目导航器中删除。如果您添加一个文件或重命名它，情况也是如此。

示例代码：

```objective-c
- (void)viewDidLoad {
    [super viewDidLoad];
    // Do any additional setup after loading the view.
    // 会对应在文件系统中的目录结构，`build` 后的文件会被复制到项目根目录
    NSString *path = [[NSBundle mainBundle] pathForResource:@"image1" ofType:@"png"];
    NSLog(@"Image path for group %@", path);
    
    // 会对应文件系统中的目录结构，build后的文件会对应在相应的目录中
    path = [[NSBundle mainBundle] pathForResource:@"image1" ofType:@"png" inDirectory:@"FolderReferences"];
    NSLog(@"Image path for folder references %@", path);
}
```



## `Xcode Objective-C` 项目添加 `Frameworks`

>参考链接：http://docs.onemobilesdk.aol.com/ios-ad-sdk/adding-frameworks-xcode.html

从项目窗口左侧的项目导航器中选择项目文件。

在项目设置编辑器中选择您想要添加框架的目标。

选择 `Build Phases` 选项卡，然后单击 `Link Binary With Libraries` 旁边的小三角形以查看应用程序中的所有框架。

要添加框架，请单击框架列表下方的 `+`。

要选择多个框架，请按住命令键，同时使用鼠标单击所需的框架。



## 禁用 `UI` 测试，只运行 `Unit` 测试

使用 `Xcode` 创建名为 `demo2` 的 `Swift App`

打开功能 `Product` > `Scheme` > `Edit Scheme` > `Test`，取消选择 `demo2UITests` 测试目标，只勾选 `demo2Tests` 测试目标。

此时运行测试只会运行 `Unit` 测试。