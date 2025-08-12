## `Cocoapods`

Cocoapods 是 **iOS/macOS 开发中最主流的依赖管理工具**，主要用于简化第三方库（框架）的集成、依赖管理和版本控制。它的出现彻底改变了早期 iOS 开发中手动管理依赖的混乱局面，大幅提升了开发效率。


### **核心作用：依赖管理**

在没有 Cocoapods 的时代，开发者集成第三方库需要手动完成以下操作：

- 到 GitHub 或其他平台下载源码；
- 将源码拖入项目；
- 处理依赖的子库（比如 A 库可能依赖 B 库，需重复上述步骤）；
- 解决版本冲突（比如两个库要求不同版本的同一依赖）；
- 配置编译设置（如头文件搜索路径、链接器标志等）。  
  这些操作繁琐且容易出错，而 **Cocoapods 可以自动化完成所有步骤**。


### **关键概念与工作流程**

#### 1. **Podfile：依赖配置文件**  

使用时，开发者只需创建一个名为 `Podfile` 的文本文件（类似“购物清单”），在其中声明项目需要的第三方库及版本要求。例如：  

```ruby
platform :ios, '12.0'  # 指定最低系统版本
target 'MyApp' do      # 目标项目名称
  use_frameworks!
  pod 'AFNetworking', '~> 4.0'  # 引入 AFNetworking（版本 4.0 及以上，但不超过 5.0）
  pod 'SDWebImage'               # 引入 SDWebImage（最新稳定版）
end
```

#### 2. **Podspec：库的“说明书”**  

每个通过 Cocoapods 发布的第三方库都附带一个 `.podspec` 文件，用于描述该库的基本信息（如名称、版本、作者）、依赖的其他库、源码地址（Git 仓库）、编译配置（如需要链接的系统框架）等。Cocoapods 通过这个文件识别并集成库。

#### 3. **安装与集成**  

- 安装 Cocoapods：通过 RubyGems 安装命令 `sudo gem install cocoapods`。  
- 初始化项目：在项目根目录运行 `pod init`，自动生成 `Podfile`。  
- 编辑 `Podfile` 并执行 `pod install`：Cocoapods 会根据配置下载所有依赖库，生成一个 `Pods` 项目，并创建一个 `*.xcworkspace` 工作空间（**此后必须通过此工作空间打开项目**）。  


### **核心优势**

- **自动化依赖解析**：自动处理库之间的依赖关系（比如 A 依赖 B，C 依赖 B 的旧版本），选择兼容的版本组合。  
- **版本控制**：支持灵活的版本约束（如 `~> 3.0` 表示 3.0 ≤ 版本 < 4.0，`>= 2.0` 表示最低 2.0），避免版本冲突。  
- **社区生态丰富**：拥有全球最大的 iOS 开源库社区（https://cocoapods.org/），涵盖网络、UI、工具等各类场景（如 AFNetworking、SDWebImage、RxSwift 等）。  
- **简化协作**：通过 `Podfile.lock` 锁定所有依赖的具体版本，确保团队成员或 CI 环境使用完全一致的依赖版本，避免“在我电脑上正常”的问题。  


### **适用场景**

- 集成第三方功能（如网络请求、图片加载、数据解析）。  
- 管理多个内部共享库（通过私有 CocoaPods 仓库）。  
- 统一团队项目的依赖版本，降低维护成本。  


### **注意事项**

- **工作空间（.xcworkspace）**：集成后必须通过 `.xcworkspace` 打开项目，而非原有的 `.xcodeproj`，否则无法识别 Pods 中的库。  
- **性能优化**：大型项目依赖较多时，首次 `pod install` 或 `pod update` 可能耗时较长（可通过 `pod repo update` 更新本地索引加速）。  
- **版本兼容性**：部分老旧库可能不支持最新 CocoaPods 版本，需注意版本匹配。  


总之，Cocoapods 是 iOS 开发中**不可或缺的工具**，熟练使用它能大幅提升开发效率，尤其适合中大型项目或需要频繁集成第三方库的场景。



## `Cocoapods` 和 `Objective-C` 关系

>参考链接：https://www.cnblogs.com/wi100sh/p/5258916.html

`iPhone` 开发用的编程语言不是 `c`、`c++`、`java` 而是 `Objective-C`。虽然很多地方与 `c/c++` 相似，却也有很多地方与其不一样。如果你还分不清楚 `Objective-C` 与 `Cocoapods` 的关系，大致可以这样去想：`Objective-C` 只是一个单纯的语言，而 `Cocoapods` 则是用 `Objective-C` 写成的 `iPhone` 基础类库与框架。如果把 `Objective-C` 比作 `c++`， 那么 `Cocoapods` 大概可以比作 `MFC`。



## 使用 `ruby` 安装

>提示：`2025/08/12` 安装 `cocoapods` 后，运行 `pod --version` 命令报错，所以推荐使用 `homebrew`方式安装。
>
>`https://www.cnblogs.com/chuancheng/p/8443677.html`

实验环境：`macOS 13.0.1`、`ruby 2.6.10p210`，使用 `ruby -version` 命令查看 `ruby` 版本信息。

更换`ruby`源

```bash
gem sources --remove https://rubygems.org/ && gem sources --add https://gems.ruby-china.com

# 验证源是否替换成功
gem sources -l
```

在执行`sudo gem install cocoapods`过程中会提示先执行以下命令，否则无法安装`cocoapods`

```bash
sudo gem install drb -v 2.0.6 && sudo gem install zeitwerk -v 2.6.18 && sudo gem install activesupport -v 6.1.7.10
```

安装`cocoapods`

```bash
sudo gem install cocoapods --verbose
```

查看`pod`版本

```bash
pod --version
```

设置`pod`（注意：这个步骤似乎在新版本的`cocoapods`中不在需要）

```bash
pod setup
```

- 注意：此命令因为需要下载`github Specs.git`仓库并设置`~/cocoapods/repos`，所以设置耗时非常长，解决办法是手动下载`https://github.com/CocoaPods/Specs.git`仓库并且设置`~/cocoapods/repos`，命令如下：

  ```bash
  cd ~
  mkdir .cocoapods
  cd .cocoapods
  mkdir repos
  cd repos
  git clone --depth 1 https://github.com/CocoaPods/Specs.git master
  ```

搜索第三方库

```bash
pod search AFNetworking
```



## 使用 `homebrew` 安装

实验环境：`macOS 13.0.1`。

参考本站 <a href="/macos/homebrew.html#homebrew使用" target="_blank">链接</a> 安装 `homebrew`。

安装 `cocoapods`

```sh
brew install cocoapods
```

查看`pod`版本

```bash
pod --version
```

搜索第三方库，提示：首次运行有点慢，需要耐心等待（约 `5` 分钟）

```bash
pod search AFNetworking
```



## `Xcode Objective-C` 项目添加 `AFNetworking` 库

>参考链接：https://github.com/AFNetworking/AFNetworking/wiki/Getting-Started-with-AFNetworking

在 `Xcode` 项目根目录（与 `*.xcodeproj` 文件同目录）下创建 `Podfile` 并添加以下内容

```
project 'demo2'
platform:ios, '7.0'
target 'demo2' do
pod 'AFNetworking', '~>3.1.0'
end
```

- `platform :ios, '7.0'`代表当前`AFNetworking`支持的`iOS`最低版本是`iOS 7.0`
- `project 'demo2'`就是你自己的工程名字，`pod install --verbose`命令会自动在当前目录搜索`demo2.xcodeproj`文件进行`cocoapods`集成生成`demo2.xcworkspace`文件，如果没有`demo2.xcodeproj`文件则不会生成`demo2.xcworkspace`文件
- `pod 'AFNetworking', '~> 3.1.0'`代表要下载的`AFNetworking`版本是`3.1.0`及以上版本，还可以去掉后面的`'~> 3.1.0'`，直接写`pod 'AFNetworking'`，这样代表下载的`AFNetworking`是最新版

下载、编译、生成`demo2.xcworkspace`（以后打开`Xcode`项目使用此文件，其包含`AFNetworking`依赖）

```bash
pod install --verbose
```

命令执行成功后，使用 `Xcode` 打开 `demo2.xcworkspace` 文件



## `pod install` 加速

>`https://www.jianshu.com/p/3086df14ed08`

每次执行`pod install`和`pod update`的时候，`cocoapods`都会默认更新一次`spec`仓库。这是一个比较耗时的操作。在确认`spec`版本库不需要更新时，给这两个命令加一个参数跳过`spec`版本库更新,可以明显提高这两个命令的执行速度。

```bash
pod install --verbose --no-repo-update
pod update --verbose --no-repo-update
```



## `Podfile` 指定版本号

>`https://www.jianshu.com/p/45b6252c3baf`

`pod 'AFNetworking', '~> 1.0'`版本号可以是`1.0`，可以是`1.1`，`1.9`，但必须小于`2`
`pod 'AFNetworking', '1.0'`// 版本号指定为`1.0`
`pod 'AFNetworking'`// 不指定版本号，最新版本
`'> 0.1' Any version higher than 0.1 0.1`以上
`'>= 0.1' Version 0.1 and any higher version 0.1`以上，包括`0.1`
`'< 0.1' Any version lower than 0.1 0.1`以下
`'<= 0.1' Version 0.1 and any lower version 0.1`以下，包括`0.1`
`'~> 0.1.2' Version 0.1.2 and the versions up to 0.2, not including 0.2 and higher 0.2`以下(不含`0.2`)，`0.1.2`以上（含`0.1.2`）
`'~> 0.1' Version 0.1 and the versions up to 1.0, not including 1.0 and higher 1.0`以下(不含`1.0`)，`0.1`以上（含`0.1`）
`'~> 0' Version 0 and higher, this is basically the same as not having it. 0`和以上，等于没有此约束



## 查询本地所有 `pods` 库

> `https://blog.csdn.net/skylin19840101/article/details/71404110`

```bash
pod repo list
```



## 删除本地 `pods` 库

> `https://blog.csdn.net/skylin19840101/article/details/71404110`

```bash
pod repo remove aliyun
```

