## `Android Studio` - 安装

### `windows7、windows11、windows 2016`

注意：用虚拟机安装`android studio`先登陆`vCenter`启用虚拟机虚拟化

下载 `android-studio-ide-171.4408382-windows.exe` 或者最新版本的 `android studio` 双击安装

选中`Android SDK`和`Intel HAXM`安装

打开`android-studio`创建`BasicActivity`类型`helloworld`工程

如果打开`helloworld`工程一直卡在`”Building ‘helloworld’ Gradle project info”`，那么点击`cancel`并且关闭`android studio`取消`gradle`下载
打开目录`C:\Users\john\.gradle\wrapper\dists\gradle-4.1-all\bzyivzo6n839fup2jbap0tjew`复制`gradle-4.1-all.zip`到此目录下，再次打开`android studio`，这次会很快打开`helloworld`项目



### `macOS`

安装方式和`windows`方式没有区别，全部安装和配置都使用`android-studio`配置，无需打开命令行配置。



### `Ubuntu`

在`https://developer.android.com/studio/archive`下载`android-studio-2024.2.1.11-linux.tar.gz`

切换到`root`用户

```bash
sudo -i
```

解压`android-studio-2024.2.1.11-linux.tar.gz`到`/usr/local`目录

```bash
cd /usr/local
tar -xvzf android-studio-2024.2.1.11-linux.tar.gz
```

新建文件`/usr/share/applications/android-studio.desktop`内容如下：

```properties
[Desktop Entry]
Encoding=UTF-8
# https://askubuntu.com/questions/144968/set-variable-in-desktop-file
Name=Android Studio
Exec=sh /usr/local/android-studio/bin/studio.sh
Icon=/usr/local/android-studio/bin/studio.svg
Terminal=false
Type=Application
StartupNotify=true
```

通过`launch`应用程序功能输入`android studio`打开`android studio`

在打开项目过程中，如果下载`gradle.zip`过慢，可以参考 [链接](/android/README.html#gradle-android-studio下载gradle慢) 解决此问题。



## `Android Studio` - 查看内置的`JDK`版本

通过 `Help` > `About` 功能查看 `Android Studio` 内置的 `JDK` 版本，如下：

```
Runtime version: 21.0.3+-12282718-b509.11 amd64
VM: OpenJDK 64-Bit Server VM by JetBrains s.r.o.
```

- `OpenJDK` 版本为 `21.0.3`



## 删除已经下载的系统镜像

>说明：使用 `Android Studio` 删除已经下载的系统镜像以留出硬盘空间。

打开 `Settings` 功能搜索 `sdk` 关键词定位到 `Android SDK` 功能，切换到 `SDK Platforms Tab`，勾选 `Hide Obsolete Packages` 和 `Show Package Details`，拖动滚动条取消勾选需要删除的系统镜像后点击 `Apply` 按钮即可删除。



## 运行谷歌的模拟器（`AVD`）慢

>注意：个人电脑需要退出省电模式并接通电源，否则 `CPU` 功率太低导致 `AVD` 很慢。
>
>提示：
>
>- 使用服务器级的 `CPU` 运行 `AVD` 速度更加快。
>- 使用 `Ubuntu20.4` 运行 `AVD` 速度更加快，可能是因为底层使用 `kvm` 虚拟化原因。
>- 首次启动 `AVD` 会慢需要耐心等待。

运行谷歌的模拟器很慢，甚至在启动应用后经常会遇到 `ANR` 错误，这是因为运行了比较新版本的安卓操作系统，此时只需要切换到比较低版本的安卓系统（例如：`x86 images Tab` 中的 `API 24 "Nougat"; Android 7.0 Google APIs x86`）并且把 `Emulated Performance` 中的 `Graphics Acceleration` 选中为 `Hardware` ，`AVD RAM（AVD运行内存）` 设置为 `4GB`，`VM Heap size（AVD中的每个应用运行内存）` 设置为 `256MB`。`Startup` 中的 `Default Boot` 设置为 `Cold`。



## 项目下载远程仓库慢

>参考：https://blog.csdn.net/ygc87/article/details/82857611

`settings.gradle repositories` 添加配置：

```groovy
maven{ url 'http://maven.aliyun.com/nexus/content/groups/public/' }
```



`settings.gradle.kts` 添加阿里云配置：

```kotlin
pluginManagement {
    repositories {
        // 添加阿里云 Maven 插件仓库
        maven { url = uri("https://maven.aliyun.com/repository/gradle-plugin") }
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        // 添加阿里云 Maven 仓库
        maven { url = uri("https://maven.aliyun.com/nexus/content/groups/public/") }
        google()
        mavenCentral()
    }
}

rootProject.name = "My Application"
include(":app")
 
```



## `android ndk`



### 什么是`sdk`？什么是`ndk`？

>https://developer.android.com/ndk/index.html
>
>https://stackoverflow.com/questions/43751485/what-is-the-difference-between-ndk-and-sdk

`Android NDK`是一个工具集，可让您使用`C`和`C++`等语言以原生代码实现应用的各个部分。对于某些类型的应用，这可以帮助您重复使用以这些语言编写的代码库。

`SDK`是`Android`应用程序的主要开发工具包 - 它包含用于`Java`和资源（`png`、`xml`）编译、打包为`apk`文件、在设备、模拟器、文档等上安装、运行和调试它们的工具。



### `Ubuntu`配置`ndk`编译环境

>https://www.jianshu.com/p/9ada3fd9c286

访问`https://developer.android.com/ndk/downloads/older_releases.html`下载`ndk`

使用`unzip`命令解压`ndk`

```bash
unzip android-ndk-r21e-linux-x86_64.zip
```

移动`android-ndk`到`/usr/local`目录

```bash
sudo mv android-ndk-r21e /usr/local/
```

配置`ndk`环境变量文件`/etc/profile.d/android-ndk.sh`内容如下：

```bash
export ANDROID_NDK=/usr/local/android-ndk-r21e
export ANDROID_NDK_ROOT=$ANDROID_NDK
export PATH=$PATH:$ANDROID_NDK
```

临时加载`ndk`环境变量

```bash
source /etc/profile
```

测试`ndk`是否正确配置

```bash
ndk-build --version
```



## `Generate Signed Bundle or APK`时创建`key`

在创建`key`时候会提示错误信息“JKS 密钥库使用专用格式。建议使用..."，此时可以不用理会此报错信息继续创建`apk`。



## 模拟器

### 有哪些模拟器呢？

`MuMu`模拟器：网易开发的模拟器，支持`Windows`和`macOS`，优化了游戏性能。通过 [链接](https://mumu.163.com/download/) 下载最新版模拟器，根据提示安装即可。



## `gradle` - 查看项目中使用的版本

打开项目根目录下的 `gradle/wrapper/gradle-wrapper.properties` 文件。找到 `distributionUrl` 这一行，`URL` 的路径中就包含了 `Gradle` 发行版版本。



## `gradle` - `android studio`下载`gradle`慢

~~关闭`android studio`并到官网`https://gradle.org/releases`下载完整版的`gradle`，例如：`gradle-8.9-all.zip`，不是 `gradle-8.9-bin.zip`~~

~~复制下载的`gradle zip`文件到目录`/Users/macos/.gradle/wrapper/dists/gradle-3.3-all/55gk2rcmfc6p2dg9u9ohc3hw9`~~

~~重新启动`android studio`~~

`File` > `Settings` > `搜索proxy功能` 配置 `HTTP Proxy`，选中 `Manual proxy configuration`：

- 选中 `HTTP`
- `Host name` 填写 `192.168.235.128`
- `Port number` 填写 `1080`
- `No proxy for` 填写 `*.aliyun.com,*.aliyuncs.com,dl.google.com`

重启 `Android Studio` 后下载 `gradle` 会自动使用代理。



## `gradle` - 和`gradle plugin(AGP)`对应版本

> 官方说明：https://developer.android.com/studio/releases/gradle-plugin

项目`build.gradle`

```groovy
buildscript {
    repositories {
        // Gradle 4.1 and higher include support for Google's Maven repo using
        // the google() method. And you need to include this repo to download
        // Android Gradle plugin 3.0.0 or higher.
        google()
        ...
    }
    dependencies {
        // Gradle 插件
        classpath 'com.android.tools.build:gradle:3.4.2'
    }
}
```

`Gradle plugin(AGP:Android Gradle Plugin)`和`Gradle`版本对照表：

```
Plugin version	Required Gradle version
1.0.0 - 1.1.3		2.2.1 - 2.3
1.2.0 - 1.3.1		2.2.1 - 2.9
1.5.0				2.2.1 - 2.13
2.0.0 - 2.1.2		2.10 - 2.13
2.1.3 - 2.2.3		2.14.1+
2.3.0+				3.3+
3.0.0+				4.1+
3.1.0+				4.4+
3.2.0 - 3.2.1		4.6+
3.3.0 - 3.3.2		4.10.1+
3.4.0+				5.1.1+

Plugin version	Minimum required Gradle version
8.13			8.13
8.12			8.13
8.11			8.13
8.10			8.11.1
8.9				8.11.1
8.8				8.10.2
8.7				8.9
8.6				8.7
8.5				8.7
8.4				8.6
8.3				8.4
8.2				8.2
8.1				8.0
8.0				8.0
```

`Android Gradle Plugin`和`Android Studio`兼容性

| Android Studio version             | Required AGP version |
| ---------------------------------- | -------------------- |
| Narwhal 3 Feature Drop \| 2025.1.3 | 4.0-8.13             |
| Narwhal Feature Drop \| 2025.1.2   | 4.0-8.12             |
| Narwhal \| 2025.1.1                | 3.2-8.11             |
| Meerkat Feature Drop \| 2024.3.2   | 3.2-8.10             |
| Meerkat \| 2024.3.1                | 3.2-8.9              |
| Ladybug Feature Drop \| 2024.2.2   | 3.2-8.8              |
| Ladybug \| 2024.2.1                | 3.2-8.7              |
| Koala Feature Drop \| 2024.1.2     | 3.2-8.6              |
| Koala \| 2024.1.1                  | 3.2-8.5              |
| Jellyfish \| 2023.3.1              | 3.2-8.4              |
| Iguana \| 2023.2.1                 | 3.2-8.3              |
| Hedgehog \| 2023.1.1               | 3.2-8.2              |
| Giraffe \| 2022.3.1                | 3.2-8.1              |
| Flamingo \| 2022.2.1               | 3.2-8.0              |



## 使用`Android Studio Ladybug|2024.2.1`运行旧版项目

>详细用法请参考本站 [示例](https://gitee.com/dexterleslie/demonstration/tree/main/demo-android/demo-older-project-gradle-upgrade)

使用`Android Studio 2024.2.1`运行旧版项目需要升级`Gradle`和`AGP`版本，步骤如下：

- 升级`Gradle`版本：编辑`gradle/wrapper/gradle-wrapper.properties`

  ```properties
  # 旧版本gradle
  distributionUrl=https\://services.gradle.org/distributions/gradle-4.1-all.zip
  
  # 升级为新版本gradle
  distributionUrl=https\://services.gradle.org/distributions/gradle-8.9-bin.zip
  ```

- 升级项目`build.gradle`中的`AGP`版本：编辑`build.gradle`

  ```groovy
  // 旧版本的AGP
  buildscript {
      dependencies {
          classpath 'com.android.tools.build:gradle:3.0.0'
      }
  }
  
  // 升级为新版本的AGP
  buildscript {
      dependencies {
          classpath 'com.android.tools.build:gradle:8.7.0'
      }
  }
  ```

- 调整模块`build.gradle`

  ```groovy
  android {
      // 添加 namespace 配置，每个 Android 模块都有一个命名空间，它用作其生成的 R 和 BuildConfig 类的 Kotlin 或 Java 包名称。
      // https://developer.android.com/build/configure-app-module#set-namespace
      namespace "com.future.study.android.activity_lifecycle"
      ...
  }
  
  dependencies {
      ...
      // compile 修改为 implementation
      // compile 'com.android.support:support-annotations:27.1.1'
      implementation 'com.android.support:support-annotations:27.1.1'
  }
  
  ```

- 删除`AndroidMainfest.xml`中`package`配置

  ```xml
  <?xml version="1.0" encoding="utf-8"?>
  <manifest xmlns:android="http://schemas.android.com/apk/res/android">
      <!-- 删除 package 配置，因为新版本的 gradle 在模块 build.gradle 中使用 namespace 配置替代 -->
      <!--package="com.future.study.android.activity_lifecycle"-->
      ...
  </manifest>
  ```

做完以上步骤后即可正常运行项目。



## `Android Studio 2024.2.1`编辑`Layout`文件崩溃

>提示：所以显然这最终可能是 `glib` 的问题。`AS` 的最低要求是 `Ladybug` 至少需要 `2.31` 版本。但是，只要将 `Ubuntu` 升级到 `22` 版，并将 `glib` 升级到 `2.35` 版，`Ladybug` 就能正常工作了。升级后，我终于可以编辑布局了。
>
>参考链接：https://stackoverflow.com/questions/79127278/android-studio-crashes-when-opening-layout-xml-file
