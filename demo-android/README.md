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



### `Ubuntu22.04.5`

>提示：`AS 2024.2.1` 在 `Ubuntu20.04.3` 系统编辑 `Layout` 文件崩溃，所以显然这最终可能是 `glib` 的问题。`AS` 的最低要求是 `Ladybug` 至少需要 `2.31` 版本。但是，只要将 `Ubuntu` 升级到 `22` 版，`Ladybug` 就能正常工作了。升级后，我终于可以编辑布局了。
>
>参考链接：https://stackoverflow.com/questions/79127278/android-studio-crashes-when-opening-layout-xml-file

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



## `Android Studio` - 创建各种项目

>`todo`



## `Android Studio` - 运行旧版项目

>说明：使用 `Android Studio Ladybug|2024.2.1` 运行旧版本项目需要升级 `Gradle` 和 `Android Gradle Plugin(AGP)`。
>
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



## `Android Studio` - 下载`gradle`慢

~~关闭`android studio`并到官网`https://gradle.org/releases`下载完整版的`gradle`，例如：`gradle-8.9-all.zip`，不是 `gradle-8.9-bin.zip`~~

~~复制下载的`gradle zip`文件到目录`/Users/macos/.gradle/wrapper/dists/gradle-3.3-all/55gk2rcmfc6p2dg9u9ohc3hw9`~~

~~重新启动`android studio`~~

`File` > `Settings` > `搜索proxy功能` 配置 `HTTP Proxy`，选中 `Manual proxy configuration`：

- 选中 `HTTP`
- `Host name` 填写 `192.168.235.128`
- `Port number` 填写 `1080`
- `No proxy for` 填写 `*.aliyun.com,*.aliyuncs.com,dl.google.com`

重启 `Android Studio` 后下载 `gradle` 会自动使用代理。





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

项目`build.gradle`中的`repositories`添加配置：

```groovy
maven { url 'https://maven.aliyun.com/nexus/content/groups/public/' }
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



## `Application` - 概念

您可以这样理解：
*   **Android应用程序**：指整个**房子**。
*   **`android.app.Application``**：指房子的**地基和主框架**。每个房子都必须有地基，但它本身不是房子。

---

### 核心定义

`android.app.Application` 是一个**基类**，用于维护应用程序的**全局状态**。它是你的Android应用启动时，系统创建的**第一个单例对象**，并且在整个应用进程存续期间都会一直存在。

它是一个“应用程序级别”的上下文，而不是“某个界面”的上下文。

### 它的主要角色和职责

1.  **应用的入口点**：
    虽然它没有`main()`方法（Android系统的zygote进程处理了真正的入口），但`Application`对象的创建标志着你的应用代码开始执行。它是你应用内所有组件的起点。

2.  **全局状态容器**：
    因为它是最早创建、最后销毁的对象，并且在整个应用中只有一个实例，所以它是存放**全局变量**和**需要全局访问的数据**的理想场所。
    *   例如：你初始化了一个全局的网络请求客户端（如OkHttpClient）、图片加载库（如Glide）或者数据库帮助类，你可以把它们放在这里，这样应用中的所有Activity和Service都能访问到同一个实例。

3.  **提供应用级别的上下文**：
    它提供了一个`Context`（上下文），这个上下文与整个应用的生命周期绑定，而不是与某个Activity绑定。当你需要一个生命周期与应用一样长的`Context`时（例如在`Service`中或后台任务中），就应该使用`Application Context`，而不是`Activity Context`，这样可以避免内存泄漏。

### 如何使用它？

你通常不会直接使用`Application`类，而是创建一个它的**子类**。

**步骤示例：**

1.  **创建自定义Application类**：
    在你的项目中创建一个类，继承自`android.app.Application`。

    ```kotlin
    // Kotlin 示例
    class MyCustomApplication : Application() {
    
        // 声明一个全局变量
        lateinit var globalHttpClient: OkHttpClient
    
        override fun onCreate() {
            super.onCreate() // 务必先调用父类方法
    
            // 这是初始化全局资源的最佳地点
            globalHttpClient = OkHttpClient.Builder()
                .connectTimeout(20, TimeUnit.SECONDS)
                .build()
    
            // 也可以在这里初始化第三方SDK，如Crash reporting, Analytics等
            Firebase.initialize(this)
            Timber.plant(Timber.DebugTree()) // 初始化日志库
        }
    }
    ```

2.  **在AndroidManifest.xml中注册**：
    你必须告诉Android系统使用你这个自定义的Application类，而不是默认的。在`<application>`标签中指定它的`name`属性。

    ```xml
    <manifest ...>
        <application
            android:name=".MyCustomApplication" <!-- 这里指向你的自定义类 -->
            android:icon="@mipmap/ic_launcher"
            android:label="@string/app_name"
            ... >
            <activity ...>
                ...
            </activity>
        </application>
    </manifest>
    ```

3.  **在其他地方获取Application实例**：
    你可以在Activity、Service或任何有Context的地方轻松获取到你的Application实例。

    ```kotlin
    // 在某个Activity中
    class MainActivity : AppCompatActivity() {
    
        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
    
            // 获取Application实例
            val myApp = application as MyCustomApplication
    
            // 使用Application中的全局对象
            val client = myApp.globalHttpClient
            // 现在可以用这个client进行网络请求了
        }
    }
    ```

### 与 Activity 的区别

| 特性         | `android.app.Application`          | `android.app.Activity`                 |
| :----------- | :--------------------------------- | :------------------------------------- |
| **数量**     | **一个应用只有一个实例**           | **一个应用可以有多个实例**（多个界面） |
| **生命周期** | 从应用启动到结束                   | 从界面创建到销毁（会频繁创建和销毁）   |
| **用途**     | 维护**全局**、**持久**的状态和资源 | 管理**一个界面**的显示和用户交互       |
| **上下文**   | 提供**应用级别**的Context          | 提供**界面级别**的Context              |

### 总结

**`android.app.Application`** 是Android应用的**基石**。它是一个单例类，代表的是**应用程序本身**，而不是应用程序中的某个界面。它的主要作用是：

*   **作为应用的启动入口**
*   **存放和管理全局资源与状态**
*   **提供应用级别的上下文**

正确使用`Application`类对于编写高效、可维护且没有内存泄漏的Android应用至关重要。



## `Application` - 基本用法

>参考链接：https://www.cnblogs.com/tiejiangweigaibianercunzai/p/4003357.html
>
>详细用法请参考本站 [示例](https://gitee.com/dexterleslie/demonstration/tree/main/demo-android/demo-application)

创建自定义 `Application`：

```java
package com.future.demo;

import android.app.Application;
import android.util.Log;

/**
 *
 */
public class BaseApplication extends Application{
    private final static String TAG = BaseApplication.class.getSimpleName();

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i(TAG, "调用onCreate()函数");
    }
}

```

`AndroidManifest.xml` 中配置自定义 `Application`

```xml
<?xml version="1.0" encoding="utf-8"?>
<manifest xmlns:android="http://schemas.android.com/apk/res/android">

    <application
        android:allowBackup="true"
        android:icon="@mipmap/ic_launcher"
        android:label="@string/app_name"
        android:roundIcon="@mipmap/ic_launcher_round"
        android:supportsRtl="true"
        android:theme="@style/AppTheme"
        android:name=".BaseApplication">
        ...
    </application>

</manifest>
```



## `Application` - `ActivityLifecycleCallbacks`

>`ActivityLifecycleCallbacks` 使用方法初探：https://blog.csdn.net/tongcpp/article/details/40344871
>
>应用前后台切换监听：http://blog.takwolf.com/2017/06/29/android-application-foreground-and-background-switch-listener/index.html

ActivityLifecycleCallbacks是什么？Application通过此接口提供了一套回调方法，用于让开发者对Activity的生命周期事件进行集中处理。

为什么用ActivityLifecycleCallbacks？以往若需监测Activity的生命周期事件代码，你可能是这样做的，重写每一个Acivity的onResume()，然后作统计和处理,ActivityLifecycleCallbacks接口回调可以简化这一繁琐过程，在一个类中作统一处理。

通过使用本站 [示例]() 研究ActivityLifecycleCallbacks监听器能够监听所有activity start和stop事件，能够很好地实现监听应用是否前台进入后台运行和后台进入前台运行切换动作。



## `Activity` - 概念

### 一、核心概念：一句话概括

**Activity（活动）是 Android 应用中一个单独的、可交互的屏幕界面。**

你可以把它想象成 Web 开发中的一个**网页**，或者桌面应用中的一个**窗口**。一个应用通常由多个 Activity 组成，它们相互协作，但又彼此独立。

---

### 二、为什么需要 Activity？

Android 系统通过 Activity 来管理应用的用户界面和用户体验：

1.  **模块化**：每个界面（如登录页、主页、详情页）都是一个独立的 Activity，便于开发和维护。
2.  **生命周期管理**：系统通过一套明确的“生命周期”回调方法来管理 Activity 的创建、显示、隐藏、销毁等过程。这让开发者能知道界面处于什么状态，从而正确地保存数据、释放资源。
3.  **导航与组合**：不同的 Activity 可以相互启动和传递数据，共同构成一个完整的应用流程。

---

### 三、Activity 的生命周期（最重要的概念）

这是理解 Activity 如何工作的核心。生命周期是一系列回调方法的集合，系统会在 Activity 的不同状态间切换时调用它们。

下图直观地展示了整个生命周期（非常重要）：

!https://developer.android.com/static/images/activity_lifecycle.png

#### 主要生命周期方法：

1.  **onCreate()**：**必须实现**的方法。Activity 第一次创建时调用。在这里进行所有基本的初始化操作，如设置布局 (`setContentView`)、绑定控件、初始化数据。
    *   **状态**：已创建，但还不可见。

2.  **onStart()**：Activity 即将对用户**可见**时调用（但可能还无法交互）。

3.  **onResume()**：Activity 开始与用户**交互**（获得焦点）时调用。这是应用最活跃的时候。

4.  **onPause()**：当 Activity**失去焦点**但还部分可见时调用（例如，屏幕上弹出了一个对话框）。这里要执行一些轻量级的操作，如保存需要持久化的数据、释放系统资源（如摄像头）。

5.  **onStop()**：Activity**完全不可见**时调用。可以执行更耗资源的清理工作。

6.  **onRestart()**：在 Activity 被 `onStop()` 后，**重新**被用户打开时调用（不同于第一次创建）。

7.  **onDestroy()**：Activity 被**销毁**之前调用。这是生命周期中的最后一个回调，用于进行最终的资源清理。

---

### 四、一个简单的 Activity 示例

以下是一个最简单的 Activity 代码：

```kotlin
// MainActivity.kt
class MainActivity : AppCompatActivity() { // 继承自支持库中的Activity基类

    // 1. 重写onCreate方法
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState) // 必须调用父类方法
        // 2. 设置Activity要显示的布局文件（UI）
        setContentView(R.layout.activity_main) 

        // 3. 找到布局中的按钮并设置点击事件
        val myButton: Button = findViewById(R.id.my_button)
        myButton.setOnClickListener {
            // 点击按钮后，跳转到另一个Activity（DetailsActivity）
            val intent = Intent(this, DetailsActivity::class.java)
            startActivity(intent)
        }
    }

    // 通常还会根据需要重写其他生命周期方法，如onPause
    override fun onPause() {
        super.onPause()
        // 保存用户进度等操作
    }
}
```

对应的布局文件 `res/layout/activity_main.xml` 定义了界面上的元素。

---

### 五、Activity 之间的导航（Intent）

你很少会只使用一个 Activity。启动另一个 Activity 需要使用 **Intent**（意图）。

*   **显式 Intent**：明确指定要启动的 Activity 类名。
    ```kotlin
    // 从MainActivity跳转到DetailsActivity
    val intent = Intent(this, DetailsActivity::class.java)
    startActivity(intent)
    ```

*   **隐式 Intent**：声明一个要执行的操作（如查看网页、分享图片），由系统决定哪个应用的哪个 Activity 来处理。
    ```kotlin
    // 打开一个网页
    val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.google.com"))
    startActivity(intent)
    ```

---

### 六、总结与类比

| 概念         | Android       | Web 开发                               | 解释                           |
| :----------- | :------------ | :------------------------------------- | :----------------------------- |
| **单个界面** | **Activity**  | **一个网页**                           | 用户与之交互的一个屏幕         |
| **界面布局** | XML 布局文件  | HTML/CSS 文件                          | 定义界面的样子                 |
| **界面逻辑** | Activity 类   | JavaScript                             | 控制界面的行为                 |
| **界面跳转** | **Intent**    | **超链接 (`<a>`) / `window.location`** | 从一个界面导航到另一个         |
| **应用入口** | Main Activity | Index.html (首页)                      | 用户打开应用时看到的第一个界面 |

### 进阶概念（了解即可）

*   **Fragment**：现在更推荐使用 **Fragment（碎片）** 来构建灵活的界面，尤其是在平板和大屏设备上。一个 Activity 可以包含多个 Fragment，就像一个“容器”。
*   **ViewModel** 和 **LiveData**：与 Activity 配合使用，用于以生命周期感知的方式管理界面相关的数据，即使在配置变更（如屏幕旋转）时也能保留数据，避免 Activity 重建导致数据丢失。
*   **启动模式**：通过 `AndroidManifest.xml` 配置，可以控制 Activity 的启动行为（如是否创建新实例、是否重用已有实例）。

希望这个解释能帮助你彻底理解 Android Activity！它是你构建任何 Android 应用的基石。



## `Activity` - 生命周期

>注意：不能保证 `onDestroy` 方法一定被回调（`onDestroy` 方法在调用 `finish` 和用户按下 `back` 按钮时一定被回调）。
>
>详细用法请参考本站 [示例](https://gitee.com/dexterleslie/demonstration/tree/main/demo-android/demo-activity-lifecycle)
>
>参考链接：https://stackoverflow.com/questions/19608948/is-ondestroy-not-always-called?rq=1、https://blog.csdn.net/javazejian/article/details/51932554

生命周期如图所示：

![image-20250913232611015](image-20250913232611015.png)

`Activity` 状态如下：

- `Active/Running`： Activity处于活动状态，此时Activity处于栈顶，是可见状态，可与用户进行交互。 
- `Paused`： 当Activity失去焦点时，或被一个新的非全屏的Activity，或被一个透明的Activity放置在栈顶时，Activity就转化为Paused状态。但我们需要明白，此时Activity只是失去了与用户交互的能力，其所有的状态信息及其成员变量都还存在，只有在系统内存紧张的情况下，才有可能被系统回收掉。 
- `Stopped`： 当一个Activity被另一个Activity完全覆盖时，被覆盖的Activity就会进入Stopped状态，此时它不再可见，但是跟Paused状态一样保持着其所有状态信息及其成员变量。 
- `Killed`：当Activity被系统回收掉时，Activity就处于Killed状态。 

Activity会在以上四种形态中相互切换，至于如何切换，这因用户的操作不同而异。了解了Activity的4种形态后，我们就来聊聊Activity的生命周期。

`Activity` 生命周期回调方法：

- `onCreate`：该方法是在Activity被创建时回调，它是生命周期第一个调用的方法，我们在创建Activity时一般都需要重写该方法，然后在该方法中做一些初始化的操作，如通过setContentView设置界面布局的资源，初始化所需要的组件信息等。
- `onStart`：此方法被回调时表示Activity正在启动，此时Activity已处于可见状态，只是还没有在前台显示，因此无法与用户进行交互。可以简单理解为Activity已显示而我们无法看见摆了。
- `onResume`：当此方法回调时，则说明Activity已在前台可见，可与用户交互了（处于前面所说的Active/Running形态），onResume方法与onStart的相同点是两者都表示Activity可见，只不过onStart回调时Activity还是后台无法与用户交互，而onResume则已显示在前台，可与用户交互。当然从流程图，我们也可以看出当Activity停止后（onPause方法和onStop方法被调用），重新回到前台时也会调用onResume方法，因此我们也可以在onResume方法中初始化一些资源，比如重新初始化在onPause或者onStop方法中释放的资源。
- `onPause`：此方法被回调时则表示Activity正在停止（Paused形态），一般情况下onStop方法会紧接着被回调。但通过流程图我们还可以看到一种情况是onPause方法执行后直接执行了onResume方法，这属于比较极端的现象了，这可能是用户操作使当前Activity退居后台后又迅速地再回到到当前的Activity，此时onResume方法就会被回调。当然，在onPause方法中我们可以做一些数据存储或者动画停止或者资源回收的操作，但是不能太耗时，因为这可能会影响到新的Activity的显示——onPause方法执行完成后，新Activity的onResume方法才会被执行。 
- `onStop`：一般在onPause方法执行完成直接执行，表示Activity即将停止或者完全被覆盖（Stopped形态），此时Activity不可见，仅在后台运行。同样地，在onStop方法可以做一些资源释放的操作（不能太耗时）。 
- `onRestart`：表示Activity正在重新启动，当Activity由不可见变为可见状态时，该方法被回调。这种情况一般是用户打开了一个新的Activity时，当前的Activity就会被暂停（onPause和onStop被执行了），接着又回到当前Activity页面时，onRestart方法就会被回调。 
- `onDestroy`：此时Activity正在被销毁，也是生命周期最后一个执行的方法，一般我们可以在此方法中做一些回收工作和最终的资源释放。 

情景启动 `app`：`onCreate()` > `onStart()` > `onResume()`

情景按 `home` 键后再回到 `app`：

- 按 `home` 键：`onPause()` > `onStop()`
- 回到 `app`：`onRestart()` > `onStart()` > `onResume()`

情景新 `activity` 覆盖旧 `activity`：

- 弹出第二个 `activity`：`onPause()` > `onStop()`
- 按 `back` 按钮返回 `onRestart()` > `onStart()` > `onResume()`

情景点击 `back` 按钮退出：`onPause()` > `onStop()` > `onDestroy()`

情景横竖屏切换：

- 先销毁 `onPause()` > `onStop()` > `onDestroy()`
- 再创建 `onCreate()` > `onStart()` > `onResume()`

情景锁屏、解锁屏：

- 锁屏 `onPause()` > `onStop()`
- 解锁屏 `onRestart()` > `onStart()` > `onResume()`



## `Activity` - 用法

### 跳转到`Activity`并传递数据

>详细用法请参考本站 [示例](https://gitee.com/dexterleslie/demonstration/tree/main/demo-android/demo-activity-lifecycle)

`MainActivity` 传递数据给 `SecondActivity`：

```java
Button button1 = findViewById(R.id.button1);
button1.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View view) {
        // 跳转到 SecondActivity 并传递数据
        Intent intent = new Intent(MainActivity.this, SecondActivity.class);
        intent.putExtra("message", "Hello from MainActivity!");
        startActivity(intent);
    }
});
```

`SecondActivity` 读取传递的数据：

```java
/**
 *
 */
public class SecondActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.second_activity);

        // 获取从 MainActivity 传递过来的数据
        TextView myTextView = findViewById(R.id.myText);
        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("message")) {
            String message = intent.getStringExtra("message");
            myTextView.setText(message);
        }
    }
}
```



### 跳转到`Activity`并期待返回结果

>详细用法请参考本站 [示例](https://gitee.com/dexterleslie/demonstration/tree/main/demo-android/demo-activity-lifecycle)

`MainActivity` 跳转到 `ThirdActivity` 并期待返回结果

```java
private static final int REQUEST_CODE_THIRD_ACTIVITY = 1;

// 跳转到 ThirdActivity 并期待返回结果
        Button toThirdActivityBtn = findViewById(R.id.button2);
        toThirdActivityBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, ThirdActivity.class);
                startActivityForResult(intent, REQUEST_CODE_THIRD_ACTIVITY);
            }
        });
```

`ThirdActivity` 设置返回结果

```java
public class ThirdActivity extends AppCompatActivity {

    private EditText inputEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.third_activity);

        inputEditText = findViewById(R.id.input_edit_text);
        Button returnBtn = findViewById(R.id.return_btn);

        returnBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 获取用户输入
                String input = inputEditText.getText().toString();

                // 创建返回的 Intent 并设置结果
                Intent resultIntent = new Intent();
                resultIntent.putExtra("result", input);
                setResult(RESULT_OK, resultIntent);

                // 结束当前 Activity
                finish();
            }
        });
    }

}
```

`MainActivity` 处理从 `ThirdActivity` 返回的结果

```java
// 处理从 ThirdActivity 返回的结果
@Override
protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);

    if (requestCode == REQUEST_CODE_THIRD_ACTIVITY) {
        if (resultCode == RESULT_OK) {
            String result = data.getStringExtra("result");
            resultTextView.setText("Received from ThirdActivity: " + result);
        }
    }
}
```



## 布局 - 类型

布局（Layout）是 Android 开发中构建用户界面（UI）的基础。它们是一种特殊的视图组（ViewGroup），负责定义其子视图（View）在屏幕上的排列方式。

Android 提供了多种布局，每种都有其特定的用途和排列规则。以下是主要的核心布局：

---

### 1. LinearLayout（线性布局）
**特点**：将其子视图以**单行**或**单列**的形式线性排列。这是最直观、最常用的布局之一。
*   **核心属性**：`android:orientation`
    *   `vertical`（垂直）：子视图从上到下排成一列。
    *   `horizontal`（水平）：子视图从左到右排成一行。
*   **常用权重属性**：`android:layout_weight`
    *   用于按比例分配剩余空间，非常实用。
*   **示例场景**：登录界面的用户名、密码输入框和登录按钮垂直排列；应用底部几个按钮水平排列。

---

### 2. RelativeLayout（相对布局）
**特点**：子视图的位置是**相对于其他兄弟视图**或**父容器**（RelativeLayout本身）来确定的。非常灵活。
*   **核心属性**：以 `layout_toLeftOf`, `layout_toRightOf`, `layout_below`, `layout_above`, `layout_alignParentTop`, `layout_centerInParent` 等为代表。
*   **优点**：可以减少布局嵌套，但规则稍复杂。
*   **示例场景**：将一个图标放在文本框的左边，将一个按钮放置在父容器的右下角。

---

### 3. ConstraintLayout（约束布局）
**特点**：目前**最强大、最推荐**的布局。它通过为每个子视图添加**约束（Constraint）** 来确定其位置，类似于 RelativeLayout 的升级版，但功能强大得多。
*   **核心概念**：视图的每条边（上下左右）必须与其他视图或父容器的边建立约束关系。
*   **强大功能**：
    *   **百分比定位**：可以轻松实现按屏幕百分比布局。
    *   **屏障（Barrier）**：根据多个视图的动态大小来定位。
    *   **引导线（Guideline）**：一条不可见的参考线，用于辅助对齐。
    *   **链（Chain）**：控制一组视图在水平或垂直方向上的分布方式。
*   **优点**：扁平化布局，极大地减少嵌套，性能好，能轻松应对各种复杂和响应式界面。
*   **示例场景**：**几乎所有复杂界面**的首选，特别是需要适配不同屏幕尺寸的场景。

---

### 4. FrameLayout（帧布局）
**特点**：最简单的布局，所有子视图都会**堆叠**在屏幕的**左上角**。后添加的子视图会盖在先添加的视图上面。
*   **核心属性**：`android:layout_gravity`，用于控制子视图在父容器中的对齐方式（如居中、右下角）。
*   **用途**：通常用于**占位**或**显示单个视图**。常见用法是作为碎片（Fragment）的容器，或者用于实现重叠效果（如Logo浮于图片之上）。
*   **示例场景**：应用内的弹窗（Dialog）、一个单独的图片展示页。

---

### 5. TableLayout（表格布局）
**特点**：将其子视图排列成**行和列**的形式。每一行是一个 `TableRow` 元素，每个 `TableRow` 中的子视图代表一个单元格。
*   **核心属性**：`android:stretchColumns` 和 `android:shrinkColumns`，用于控制哪些列可以拉伸或收缩以填满空间。
*   **用途**：适合需要规整的表格式数据展示，但现在更常使用 `RecyclerView` 来实现更灵活的列表/网格。
*   **示例场景**：简单的数据表单、课程表。

---

### 6. GridLayout（网格布局）
**特点**：将界面划分为**无限细的网格线**，子视图可以指定自己占据哪几行哪几列，从而更灵活地实现网格状布局。
*   **与TableLayout区别**：比 TableLayout 更灵活，子视图可以跨行和跨列。
*   **用途**：适合需要自定义网格排布的场景，例如计算器界面、井字棋游戏棋盘。
*   **示例场景**：计算器应用的按钮布局。

---

### 总结与选择建议

| 布局                       | 特点                         | 适用场景                                               | 推荐度           |
| :------------------------- | :--------------------------- | :----------------------------------------------------- | :--------------- |
| **ConstraintLayout**       | **功能最强，性能好，扁平化** | **几乎所有复杂界面**，响应式设计                       | ⭐⭐⭐⭐⭐ **(首选)** |
| **LinearLayout**           | 简单线性排列                 | 简单的列表或单行布局                                   | ⭐⭐⭐⭐             |
| **RelativeLayout**         | 相对定位                     | 简单重叠或相对位置布局（正逐渐被ConstraintLayout替代） | ⭐⭐               |
| **FrameLayout**            | 视图堆叠                     | 碎片容器、弹窗、单个视图展示                           | ⭐⭐⭐⭐             |
| **TableLayout/GridLayout** | 表格/网格                    | 规整的表格式布局（使用场景较少）                       | ⭐⭐               |

**现代最佳实践**：
对于新项目，**强烈建议将 `ConstraintLayout` 作为默认和主要的布局**。它不仅能实现其他所有布局的效果，还能通过减少视图层级来提升性能，并且能更好地适配各种屏幕尺寸。`LinearLayout` 和 `FrameLayout` 在简单场景中依然非常有用。



## 布局 - `FrameLayout`

### 核心概念

**FrameLayout** 是一个“帧”布局，或者可以想象成一个“相框”。它的行为非常直接：

**所有子视图（View）都会自动被放置在布局的左上角（0,0坐标点），后放入的子视图会覆盖在先前子视图的上面。**

您可以把它想象成一叠扑克牌或一摞纸，每张纸都盖住了下面那张，你只能看到最上面的那一张。

---

### 主要特点和用途

1.  **简单叠加（堆叠）**：这是它最核心的用途。用于将多个视图重叠在一起。
2.  **单一视图容器**：经常用作一个“容器”或“占位符”，只包含一个子视图（例如，作为 `Fragment` 的容器）。
3.  **轻量级**：由于布局逻辑非常简单，它的测量和绘制过程非常高效。

---

### 关键属性

FrameLayout 本身属性很少，但其**子视图**可以使用一些非常重要的属性来控制自己在 FrameLayout 中的位置：

*   `android:layout_gravity`
    *   **这是最重要的属性**。它用于控制子视图在 FrameLayout **内部**的对齐方式，而不是像其他布局那样简单的外边距。
    *   **常用值**：`center`, `center_vertical`, `center_horizontal`, `top`, `bottom`, `left`, `right`, `start`, `end`，以及这些值的组合（如 `bottom|end` 表示右下角）。
    *   **示例**：将一个按钮放在 FrameLayout 的右下角：`android:layout_gravity="bottom|end"`

*   `android:foreground`
    *   **FrameLayout 自身的属性**。用于设置一个绘制在所有子视图**之上**的前景图。
    *   可以是一张图片（`@drawable/...`），也可以是一个颜色（`@color/...`）。
    *   常用于设置一个遮罩效果，比如在图片上加一个半透明的灰色层。

*   `android:foregroundGravity`
    *   控制 `android:foreground` 前景图的对齐方式。

---

### 实际应用场景

1.  **Fragment 的容器**
    这是最常见的用法。在 `Activity` 的布局中，你通常会用一个 FrameLayout 来作为放置 `Fragment` 的“坑”。
    ```xml
    <FrameLayout
        android:id="@+id/fragment_container"
        android:layout_width="match_parent"
        android:layout_height="match_parent" />
    ```
    然后在代码中，你将不同的 Fragment 事务填充到这个 ID 为 `fragment_container` 的 FrameLayout 中。

2.  **图片上叠加文字或图标**
    比如实现一个照片墙，在图片的右下角显示一个收藏图标。
    ```xml
    <FrameLayout
        android:layout_width="wrap_content"
        android:layout_height="wrap_content">
    
        <ImageView
            android:layout_width="200dp"
            android:layout_height="200dp"
            android:scaleType="centerCrop"
            android:src="@drawable/my_image" />
    
        <ImageView
            android:layout_width="30dp"
            android:layout_height="30dp"
            android:layout_gravity="bottom|end" <!-- 关键！定位到右下角 -->
            android:src="@drawable/ic_favorite" />
    
    </FrameLayout>
    ```

3.  **实现遮罩或浮层效果**
    使用 `android:foreground` 属性可以轻松实现点击态或禁用态的遮罩效果。
    ```xml
    <FrameLayout
        android:layout_width="100dp"
        android:layout_height="100dp"
        android:foreground="@drawable/selector_foreground"> <!-- 一个选择器，包含按下和默认状态 -->
    
        <ImageView ... />
    
    </FrameLayout>
    ```

4.  **自定义 ProgressBar 或加载框**
    可以在界面中央覆盖一个半透明的层和一个旋转的进度条。
    ```xml
    <FrameLayout
        android:id="@+id/loading_state"
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        android:visibility="gone"> <!-- 默认隐藏 -->
    
        <View
            android:layout_width="match_parent"
            android:layout_height="match_parent"
            android:background="#80000000" /> <!-- 半透明黑色遮罩 -->
    
        <ProgressBar
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:layout_gravity="center" <!-- 关键！让进度条居中 -->
            android:indeterminate="true" />
    
    </FrameLayout>
    ```

---

### 代码示例

下面是一个典型的 FrameLayout 示例，展示了图片叠加图标和文字的效果：

```xml
<?xml version="1.0" encoding="utf-8"?>
<FrameLayout xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="match_parent"
    android:layout_height="wrap_content">

    <!-- 底层：背景图片 -->
    <ImageView
        android:id="@+id/imageView"
        android:layout_width="match_parent"
        android:layout_height="200dp"
        android:scaleType="centerCrop"
        android:src="@drawable/landscape" />

    <!-- 中层：半透明渐变，让文字更清晰 -->
    <View
        android:layout_width="match_parent"
        android:layout_height="60dp"
        android:layout_gravity="bottom"
        android:background="@drawable/gradient_background" />

    <!-- 上层：文字 -->
    <TextView
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:layout_gravity="bottom|start"
        android:layout_margin="16dp"
        android:text="美丽的风景"
        android:textColor="@color/white"
        android:textSize="18sp" />

    <!-- 最上层：右上角图标 -->
    <ImageView
        android:layout_width="40dp"
        android:layout_height="40dp"
        android:layout_gravity="top|end"
        android:layout_margin="8dp"
        android:src="@drawable/ic_bookmark" />

</FrameLayout>
```

### 总结

| 特性         | 说明                                                         |
| :----------- | :----------------------------------------------------------- |
| **核心行为** | **堆叠子视图**，后添加的覆盖先添加的，默认对齐左上角。       |
| **核心属性** | 子视图使用 `android:layout_gravity` 来定位。                 |
| **优点**     | **非常简单、高效**，是实现重叠效果的**最佳选择**。           |
| **缺点**     | 无法实现复杂的、非重叠的布局。                               |
| **主要用途** | **Fragment容器**、**图片文字叠加**、**浮层/遮罩**、**自定义弹窗**。 |

虽然 `ConstraintLayout` 也能实现视图重叠，但对于简单的堆叠场景，`FrameLayout` 因其极致的简单和清晰语义，仍然是首选方案。

### 示例

>详细用法请参考本站 [示例](https://gitee.com/dexterleslie/demonstration/tree/main/demo-android/demo-framelayout)

```xml
<?xml version="1.0" encoding="utf-8"?>
<FrameLayout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    xmlns:tools="http://schemas.android.com/tools"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    app:layout_behavior="@string/appbar_scrolling_view_behavior"
    tools:context="com.future.demo.MainActivity"
    tools:showIn="@layout/activity_main">
    <ProgressBar
        android:layout_gravity="center"
        android:id="@+id/progressBar"
        style="?android:attr/progressBarStyle"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"/>
    <TextView
        android:layout_gravity="center"
        android:id="@+id/textView5"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:text="%20" />
</FrameLayout>

```



## 布局 - `RelativeLayout`

>参考链接：https://blog.csdn.net/qq_40895460/article/details/88644845

### 核心概念

**RelativeLayout** 是一个通过**相对定位**来排列其子视图的布局。每个子视图的位置都是相对于**兄弟视图（Sibling View）** 或**父容器（RelativeLayout 本身）** 来确定的。

它的理念是：“将这个按钮放在那个文本框的下方”，或者“将这个图标对齐到父布局的右边”。

---

### 主要特点和用途

1.  **减少嵌套**：通过相对定位，有时可以避免使用多个 `LinearLayout` 嵌套来实现的复杂布局，从而使视图层级更扁平。
2.  **灵活性**：可以创建出线性布局难以实现的复杂界面。
3.  **性能权衡**：由于需要测量所有视图的依赖关系，如果使用不当（如依赖关系复杂或循环依赖），测量过程可能会比 `LinearLayout` 更耗时。

---

### 关键属性（相对于父容器）

这些属性是 `android:layout_alignParent` 开头，值为 `true` 或 `false`。

*   `android:layout_alignParentTop="true"` - 与父布局顶部对齐
*   `android:layout_alignParentBottom="true"` - 与父布局底部对齐
*   `android:layout_alignParentLeft="true"` - 与父布局左边对齐
*   `android:layout_alignParentRight="true"` - 与父布局右边对齐
*   `android:layout_alignParentStart="true"` - 与父布局起始边对齐（支持RTL）
*   `android:layout_alignParentEnd="true"` - 与父布局结束边对齐（支持RTL）
*   `android:layout_centerInParent="true"` - 在父布局中居中
*   `android:layout_centerHorizontal="true"` - 在父布局中水平居中
*   `android:layout_centerVertical="true"` - 在父布局中垂直居中

---

### 关键属性（相对于兄弟视图）

这些属性是 `android:layout_` 开头，值需要引用另一个视图的 ID (`@id/view_id`)。

*   `android:layout_above="@id/view_id"` - 位于指定视图的上方
*   `android:layout_below="@id/view_id"` - 位于指定视图的下方
*   `android:layout_toLeftOf="@id/view_id"` - 位于指定视图的左边
*   `android:layout_toRightOf="@id/view_id"` - 位于指定视图的右边
*   `android:layout_toStartOf="@id/view_id"` - 位于指定视图的起始边
*   `android:layout_toEndOf="@id/view_id"` - 位于指定视图的结束边
*   `android:layout_alignTop="@id/view_id"` - 与指定视图的顶部对齐
*   `android:layout_alignBottom="@id/view_id"` - 与指定视图的底部对齐
*   `android:layout_alignLeft="@id/view_id"` - 与指定视图的左边对齐
*   `android:layout_alignRight="@id/view_id"` - 与指定视图的右边对齐
*   `android:layout_alignStart="@id/view_id"` - 与指定视图的起始边对齐
*   `android:layout_alignEnd="@id/view_id"` - 与指定视图的结束边对齐
*   `android:layout_alignBaseline="@id/view_id"` - 与指定视图的文本基线对齐（用于TextView）

---

### 实际应用场景与示例

**场景**：创建一个简单的用户资料头部的布局，包含头像、姓名和简介。

```xml
<?xml version="1.0" encoding="utf-8"?>
<RelativeLayout xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    android:padding="16dp">

    <!-- 头像 (放置在左边) -->
    <ImageView
        android:id="@+id/imageView_avatar"
        android:layout_width="60dp"
        android:layout_height="60dp"
        android:layout_alignParentStart="true"
        android:layout_alignParentTop="true"
        android:src="@drawable/avatar" />

    <!-- 姓名 (放置在头像的右边，与头像顶部对齐) -->
    <TextView
        android:id="@+id/textView_name"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:layout_toEndOf="@id/imageView_avatar"
        android:layout_alignTop="@id/imageView_avatar"
        android:layout_marginStart="16dp"
        android:text="张三"
        android:textSize="18sp"
        android:textStyle="bold" />

    <!-- 简介 (放置在姓名的下方，与头像的右边对齐) -->
    <TextView
        android:id="@+id/textView_bio"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:layout_toEndOf="@id/imageView_avatar"
        android:layout_below="@id/textView_name"
        android:layout_alignStart="@id/textView_name"
        android:layout_marginTop="4dp"
        android:text="这是一个简单的用户简介..."
        android:textColor="#666"
        android:textSize="14sp" />

    <!-- 时间戳 (放置在父布局的右上角) -->
    <TextView
        android:id="@+id/textView_time"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:layout_alignParentEnd="true"
        android:layout_alignParentTop="true"
        android:text="10:30 AM"
        android:textColor="#999"
        android:textSize="12sp" />

</RelativeLayout>
```

**布局效果说明**：
1.  `imageView_avatar`：对齐父布局的左上角。
2.  `textView_name`：位于 `imageView_avatar` 的右边 (`toEndOf`)，并且顶部与 `imageView_avatar` 对齐 (`alignTop`)。
3.  `textView_bio`：位于 `textView_name` 的下方 (`below`)，并且起始边与 `textView_name` 对齐 (`alignStart`)。
4.  `textView_time`：对齐父布局的右上角。

---

### 与 ConstraintLayout 的对比

| 特性       | RelativeLayout                         | ConstraintLayout                     |
| :--------- | :------------------------------------- | :----------------------------------- |
| **理念**   | 相对定位（上下左右）                   | 约束（Constraint），更强大灵活       |
| **性能**   | 测量次数较多，性能一般                 | 测量算法更优，性能更好               |
| **功能**   | 基本相对定位                           | 支持比例、屏障、链、引导线等高级功能 |
| **嵌套**   | 可减少嵌套，但依赖关系复杂时难管理     | **极致的扁平化**，能有效减少嵌套     |
| **推荐度** | ** legacy **，**不推荐在新项目中使用** | **现代首选**，Google 强力推荐        |

---

### 最佳实践和注意事项

1.  **避免循环依赖**：视图A依赖于视图B，视图B又依赖于视图A，这会导致布局错误或性能问题。
2.  **引用已定义的ID**：在引用兄弟视图时 (`@id/...`)，必须确保该视图已经在布局文件中**先被定义**了。否则会出现 `No resource found` 错误。通常需要被引用的视图（如锚点视图）放在前面。
3.  **优先使用 ConstraintLayout**：对于新项目，**强烈建议使用 `ConstraintLayout` 来代替 `RelativeLayout`**。`ConstraintLayout` 几乎能做到 `RelativeLayout` 能做的一切，并且做得更好、更高效。`RelativeLayout` 目前主要用于维护旧的代码库。
4.  **语义清晰**：虽然可以减少嵌套，但过于复杂的相对关系可能会使布局文件难以阅读和维护。

### 总结

| 特性         | 说明                                                         |
| :----------- | :----------------------------------------------------------- |
| **核心行为** | 通过**相对定位**（相对于父容器或兄弟视图）来排列子视图。     |
| **优点**     | **灵活性高**，可以在一定程度上**减少布局嵌套**。             |
| **缺点**     | **性能一般**，依赖关系**复杂时难以管理**，**已过时**。       |
| **历史地位** | 在 `ConstraintLayout` 出现之前，它是实现复杂布局的重要工具。 |
| **当前建议** | ** legacy **，在新项目中应优先使用 **`ConstraintLayout`**。  |

虽然现在不推荐新建项目时使用，但理解 `RelativeLayout` 对于阅读和维护遗留代码仍然非常重要。它的“相对”思想也被 `ConstraintLayout` 所继承和发展。

### 示例

>详细用法请参考本站 [示例](https://gitee.com/dexterleslie/demonstration/tree/main/demo-android/demo-relativelayout)

```xml
<?xml version="1.0" encoding="utf-8"?>
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:orientation="vertical">
    <RelativeLayout
        android:layout_height="0dp"
        android:layout_width="match_parent"
        android:layout_weight="1">
        <Button
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:text="button"
            android:layout_centerInParent="true"/>

        <Button
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:text="右上角"
            android:layout_alignParentRight="true"/>

        <Button
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:text="右下角"
            android:layout_alignParentRight="true"
            android:layout_alignParentBottom="true"/>

        <Button
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:text="左下角"
            android:layout_alignParentLeft="true"
            android:layout_alignParentBottom="true"/>

        <Button
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:text="左上角"
            android:layout_alignParentLeft="true"/>
    </RelativeLayout>

    <RelativeLayout
        android:layout_height="0dp"
        android:layout_width="match_parent"
        android:layout_weight="1">
        <Button
            android:id="@+id/center_button"
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:text="中间"
            android:layout_centerInParent="true"/>

        <Button
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:text="中间左边"
            android:layout_centerVertical="true"
            android:layout_toLeftOf="@id/center_button"/>

        <Button
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:text="中间顶部"
            android:layout_centerHorizontal="true"
            android:layout_above="@id/center_button"/>

        <Button
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:layout_centerVertical="true"
            android:layout_toRightOf="@id/center_button"
            android:text="中间右边" />

        <Button
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:text="中间底部"
            android:layout_centerHorizontal="true"
            android:layout_below="@id/center_button"/>

    </RelativeLayout>
</LinearLayout>
```



## 布局 - `ConstraintLayout`

### 核心概念

**ConstraintLayout** 是一个允许您以灵活且高效的方式创建复杂布局的视图组。它的核心思想是**约束（Constraint）**：通过为视图的每条边（左、上、右、下）与另一个视图的边或父布局的边之间建立“约束”关系，来确定视图的位置。

您可以把它想象成用橡皮筋连接视图的各个边。橡皮筋的强度和长度决定了视图最终的位置。

---

### 为什么推荐使用 ConstraintLayout？

1.  **扁平化视图层级**：大幅减少布局嵌套，替代多种 `LinearLayout` 和 `RelativeLayout` 的组合，从而提升性能。
2.  **强大的布局能力**：支持比例定位、屏障、链、引导线等高级功能，能轻松实现各种复杂和响应式设计。
3.  **出色的可视化工具**：Android Studio 的布局编辑器（Design View）对 ConstraintLayout 提供了极佳的支持，可以直观地拖拽和创建约束，大大提高了开发效率。
4.  **Google 官方推荐**：自 2016 年推出以来，一直是 Google 主推的布局方案。

---

### 核心属性和功能

#### 1. 基本约束 (Fundamental Constraints)
这是最基本且必须的属性。每个视图通常需要至少两个方向（水平和垂直）的约束才能确定位置。

*   `app:layout_constraintLeft_toLeftOf="parent"` - 视图的左边约束到父布局的左边
*   `app:layout_constraintTop_toTopOf="parent"` - 视图的顶部约束到父布局的顶部
*   `app:layout_constraintRight_toRightOf="@id/button"` - 视图的右边约束到 ID 为 button 的视图的右边
*   `app:layout_constraintBottom_toBottomOf="parent"` - 视图的底部约束到父布局的底部
*   `app:layout_constraintStart_toStartOf="parent"` - (支持 RTL)
*   `app:layout_constraintEnd_toEndOf="parent"` - (支持 RTL)

**记忆规律**：`layout_constraint[MySide]_to[TargetSide]Of="[target]"`

#### 2. 边距 (Margins)
和其他布局一样使用标准边距属性：`android:layout_margin`, `android:layout_marginStart`, `android:layout_marginTop` 等。

#### 3. 居中定位
将一个视图的左右两边同时约束到父容器的左右两边，它就会在水平居中；上下同理。
```xml
<Button
    ...
    app:layout_constraintLeft_toLeftOf="parent"
    app:layout_constraintRight_toRightOf="parent" />
```

#### 4. 偏差 (Bias)
当视图的两侧都有约束（如既约束左边又约束右边），可以使用偏差来在可用空间内调整位置。
*   `app:layout_constraintHorizontal_bias="0.3"` - 水平偏差 30%（0.0 是最左，1.0 是最右，默认 0.5）
*   `app:layout_constraintVertical_bias="0.8"` - 垂直偏差 80%

#### 5. 尺寸约束 (Dimension Constraints)
*   `android:layout_width/height`：可设置为具体值、`wrap_content` 或 **`0dp` (即 `MATCH_CONSTRAINT`)**。
    *   **`0dp` (MATCH_CONSTRAINT)**：是 ConstraintLayout 的精髓之一。表示视图会扩展大小以满足其设置的**所有约束**。它会在约束允许的范围内尽可能大地填充空间。

#### 6. 比例尺寸 (Ratio)
可以强制视图的宽高比。需要至少一个维度设置为 `0dp`。
*   `app:layout_constraintDimensionRatio="H,16:9"` - 高:宽 = 16:9
*   `app:layout_constraintDimensionRatio="W,1:1"` - 宽:高 = 1:1 (正方形)
*   也可以直接写 `app:layout_constraintDimensionRatio="16:9"`，系统会根据约束自动判断。

---

### 高级功能 (Tools for Complex Layouts)

#### 1. 链 (Chains)
**链**是一种在单个轴（水平或垂直）上控制一组视图分布的方式。链的头部视图（链中第一个被引用的视图）需要设置 `app:layout_constraintHorizontal_chainStyle` 或 `app:layout_constraintVertical_chainStyle`。

*   **spread** (默认)：视图均匀分布，包括首尾的外边距。
*   **spread_inside**：视图均匀分布，但首尾视图与父布局之间没有外边距。
*   **packed**：视图打包在一起居中。可以结合 `bias` 调整整体位置。

#### 2. 屏障 (Barrier)
**屏障**是一个不可见的辅助线，它会根据所引用的视图集合中最大（或最小）的一边来动态定位自己。非常适合处理不确定尺寸的内容。

*   `app:barrierDirection="end"` - 屏障的方向（end, top, start, bottom等）
*   `app:constraint_referenced_ids="view1, view2, view3"` - 屏障所引用的视图ID

#### 3. 引导线 (Guideline)
**引导线**是一条不可见的垂直或水平参考线，用于辅助对齐。可以按绝对距离或百分比定位。
*   `android:orientation="vertical"` - 垂直引导线
*   `app:layout_constraintGuide_begin="50dp"` - 距离父布局起始位置 50dp
*   `app:layout_constraintGuide_percent="0.3"` - 位于父布局的 30% 位置

---

### 实际代码示例

**场景**：创建一个典型的用户信息卡片。

```xml
<?xml version="1.0" encoding="utf-8"?>
<androidx.constraintlayout.widget.ConstraintLayout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto" <!-- 必须添加此命名空间 -->
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    android:padding="16dp">

    <!-- 头像 (左上角) -->
    <ImageView
        android:id="@+id/imageView_avatar"
        android:layout_width="60dp"
        android:layout_height="60dp"
        app:layout_constraintTop_toTopOf="parent"
        app:layout_constraintStart_toStartOf="parent"
        android:src="@drawable/avatar" />

    <!-- 姓名 (头像右边，顶部与头像对齐) -->
    <TextView
        android:id="@+id/textView_name"
        android:layout_width="0dp" <!-- MATCH_CONSTRAINT，宽度由约束决定 -->
        android:layout_height="wrap_content"
        app:layout_constraintTop_toTopOf="@id/imageView_avatar"
        app:layout_constraintStart_toEndOf="@id/imageView_avatar"
        app:layout_constraintEnd_toStartOf="@id/textView_time" <!-- 约束到时间视图，避免重叠 -->
        android:layout_marginStart="16dp"
        android:text="张三"
        android:textSize="18sp"
        android:textStyle="bold" />

    <!-- 时间 (右上角) -->
    <TextView
        android:id="@+id/textView_time"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        app:layout_constraintTop_toTopOf="parent"
        app:layout_constraintEnd_toEndOf="parent"
        android:text="10:30 AM"
        android:textColor="#999"
        android:textSize="12sp" />

    <!-- 简介 (姓名下方，与姓名左边对齐，宽度与父布局右边对齐) -->
    <TextView
        android:id="@+id/textView_bio"
        android:layout_width="0dp"
        android:layout_height="wrap_content"
        app:layout_constraintTop_toBottomOf="@id/textView_name"
        app:layout_constraintStart_toStartOf="@id/textView_name"
        app:layout_constraintEnd_toEndOf="parent"
        android:layout_marginTop="4dp"
        android:text="这是一个非常长的用户简介，它可能会换行，但宽度会被约束得很好..."
        android:textColor="#666"
        android:textSize="14sp" />

</androidx.constraintlayout.widget.ConstraintLayout>
```

### 在 Android Studio 中的可视化操作

1.  在 `Design` 视图中，可以直接从视图的圆圈拖拽到目标（父布局或其他视图）来创建约束。
2.  清除约束：点击视图后使用工具栏的“清除约束”按钮。
3.  快速推断约束：根据当前视图位置自动创建约束（可能不完美，需手动调整）。

### 总结

| 特性         | 说明                                                         |
| :----------- | :----------------------------------------------------------- |
| **核心理念** | **通过约束（橡皮筋）关系**来确定视图位置。                   |
| **核心属性** | `layout_constraint[MySide]_to[TargetSide]Of`                 |
| **核心尺寸** | **`0dp` (MATCH_CONSTRAINT)**，根据约束扩展。                 |
| **优势**     | **扁平化层级**、**性能优异**、**功能强大**、**工具支持好**。 |
| **定位**     | 使用 `bias` 进行百分比定位。                                 |
| **高级工具** | **链(Chains)**、**屏障(Barrier)**、**引导线(Guideline)**。   |
| **推荐度**   | ⭐⭐⭐⭐⭐ **所有新项目的默认和首选布局**。                       |

**强烈建议**花时间熟练掌握 ConstraintLayout，它是现代 Android UI 开发的必备技能，能极大地提升你的开发效率和应用的性能。

### 示例

>详细用法请参考本站 [示例](https://gitee.com/dexterleslie/demonstration/tree/main/demo-android/demo-constraintlayout)

```xml
<?xml version="1.0" encoding="utf-8"?>
<androidx.constraintlayout.widget.ConstraintLayout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    app:layout_behavior="@string/appbar_scrolling_view_behavior">

    <Button
        android:id="@+id/button6"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:layout_marginStart="64dp"
        android:layout_marginTop="48dp"
        android:text="Button"
        app:layout_constraintStart_toStartOf="parent"
        app:layout_constraintTop_toTopOf="parent" />

    <Button
        android:id="@+id/button8"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:layout_marginStart="32dp"
        android:layout_marginTop="52dp"
        android:text="Button"
        app:layout_constraintStart_toStartOf="@+id/button6"
        app:layout_constraintTop_toBottomOf="@+id/button6" />
</androidx.constraintlayout.widget.ConstraintLayout>

```



## 布局 - `LinearLayout`

### 核心概念

**LinearLayout** 是一个将其所有子视图（View）沿**单个方向**（水平或垂直）依次排列的布局容器。

您可以把它想象成一列士兵（垂直）或者一排桌子（水平），每个元素都紧挨着前一个元素排列。

---

### 主要特点和用途

1.  **简单直观**：排列规则非常简单，易于理解和上手。
2.  **顺序排列**：子视图按照在 XML 中定义的顺序依次显示。
3.  **权重分配**：核心功能，可以按比例分配剩余空间，非常适合实现等分布局。
4.  **常用场景**：适合创建简单的列表式布局（如设置项列表）、表单（如登录界面）、水平或垂直排列的按钮组等。

---

### 关键属性

#### 1. 布局自身属性 (设置在 LinearLayout 标签上)

*   `android:orientation` (**必须属性**)
    *   `vertical`：子视图**垂直**排列（成一列）。
    *   `horizontal`：子视图**水平**排列（成一行）。

*   `android:gravity`
    *   控制所有**子视图**在 LinearLayout **内部**的整体对齐方式。
    *   **常用值**：`center`, `center_vertical`, `center_horizontal`, `left`, `right`, `top`, `bottom`，以及组合（如 `left|center_vertical`）。

*   `android:layout_gravity` (设置在子视图上)
    *   控制**该子视图自身**在 LinearLayout **内部**的对齐方式。
    *   例如，在垂直的 LinearLayout 中，一个按钮设置 `android:layout_gravity="right"`，它会位于行的最右侧。

*   `android:baselineAligned`
    *   默认为 `true`。如果设置为 `false`，会阻止布局对齐其子视图的文本基线（baseline）。在需要精确控制包含 `TextView` 的视图对齐时可能用到。

#### 2. 权重 (Weight) - 最强大的功能

权重是 LinearLayout 的灵魂，通过 `android:layout_weight` 属性设置在**子视图**上。它用于**按比例分配父布局中的剩余空间**。

**使用规则**：

1.  通常需要将对应方向的尺寸（宽或高）设置为 `0dp`（`MATCH_CONSTRAINT`）。
2.  系统会将所有子视图的 `layout_weight` 值相加，然后按每个视图的权重比例分配剩余空间。

**示例**：实现三个水平等分的按钮。
```xml
<LinearLayout
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    android:orientation="horizontal">

    <Button
        android:layout_width="0dp"
        android:layout_height="wrap_content"
        android:layout_weight="1"
        android:text="One" />

    <Button
        android:layout_width="0dp"
        android:layout_height="wrap_content"
        android:layout_weight="1"
        android:text="Two" />

    <Button
        android:layout_width="0dp"
        android:layout_height="wrap_content"
        android:layout_weight="1"
        android:text="Three" />

</LinearLayout>
```
三个按钮的权重都是 1，总和为 3，因此每个按钮分得 `1/3` 的剩余空间，从而实现等分。

**不等分示例**：`layout_weight="2"` 的视图将获得的空间是 `layout_weight="1"` 的视图的两倍。

---

### 实际应用场景与示例

#### 场景 1：垂直排列的登录表单
```xml
<?xml version="1.0" encoding="utf-8"?>
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:orientation="vertical"
    android:gravity="center_vertical"
    android:padding="20dp">

    <EditText
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:hint="用户名/邮箱"
        android:inputType="textEmailAddress" />

    <EditText
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:layout_marginTop="10dp"
        android:hint="密码"
        android:inputType="textPassword" />

    <Button
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:layout_marginTop="20dp"
        android:text="登录" />

    <TextView
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:layout_gravity="center_horizontal"
        android:layout_marginTop="15dp"
        android:text="忘记密码？"
        android:textColor="@color/design_default_color_primary" />

</LinearLayout>
```

#### 场景 2：底部水平等分的标签栏
```xml
<LinearLayout
    android:layout_width="match_parent"
    android:layout_height="56dp"
    android:orientation="horizontal"
    android:background="@android:color/white">

    <ImageView
        android:layout_width="0dp"
        android:layout_height="wrap_content"
        android:layout_weight="1"
        android:src="@drawable/ic_home"
        android:scaleType="centerInside"
        android:contentDescription="首页" />

    <ImageView
        android:layout_width="0dp"
        android:layout_height="wrap_content"
        android:layout_weight="1"
        android:src="@drawable/ic_search"
        android:scaleType="centerInside"
        android:contentDescription="搜索" />

    <ImageView
        android:layout_width="0dp"
        android:layout_height="wrap_content"
        android:layout_weight="1"
        android:src="@drawable/ic_add"
        android:scaleType="centerInside"
        android:contentDescription="添加" />

    <ImageView
        android:layout_width="0dp"
        android:layout_height="wrap_content"
        android:layout_weight="1"
        android:src="@drawable/ic_notifications"
        android:scaleType="centerInside"
        android:contentDescription="通知" />

    <ImageView
        android:layout_width="0dp"
        android:layout_height="wrap_content"
        android:layout_weight="1"
        android:src="@drawable/ic_profile"
        android:scaleType="centerInside"
        android:contentDescription="我" />

</LinearLayout>
```

---

### 与 ConstraintLayout 的对比

| 特性         | LinearLayout                                               | ConstraintLayout                                     |
| :----------- | :--------------------------------------------------------- | :--------------------------------------------------- |
| **理念**     | **线性顺序**排列                                           | **相对约束**定位                                     |
| **复杂度**   | **简单**，易于理解                                         | **强大**，学习曲线稍陡                               |
| **嵌套**     | 容易产生**嵌套**（垂直LinearLayout内包含水平LinearLayout） | **极力避免嵌套**，视图层级扁平                       |
| **性能**     | 简单布局性能好，复杂嵌套布局性能差                         | 复杂布局性能**更优**                                 |
| **核心功能** | **权重（Weight）** 分配                                    | **约束（Constraint）、链（Chain）、屏障（Barrier）** |
| **适用场景** | **简单的列表、表单、等分布局**                             | **几乎所有布局，特别是复杂和响应式界面**             |

---

### 最佳实践和注意事项

1.  **避免过度嵌套**：这是使用 LinearLayout 最大的陷阱。深层嵌套会严重影响测量和绘制性能。如果布局超过 3-4 层嵌套，应考虑使用 `ConstraintLayout` 重构。
2.  **权重性能**：使用 `layout_weight` 会有微小的性能开销，因为系统需要测量两次。但在简单布局中影响可忽略。
3.  `match_parent` 与权重：有时为了特殊效果，也会在尺寸设为 `match_parent` 时使用权重，但这会改变计算方式，需要谨慎使用。
4.  **明确方向**：始终记得设置 `android:orientation` 属性，否则可能出现非预期的布局结果。

### 总结

| 特性         | 说明                                                         |
| :----------- | :----------------------------------------------------------- |
| **核心行为** | 将其子视图按**水平**或**垂直**方向**顺序排列**。             |
| **核心属性** | `android:orientation`, `android:layout_weight`               |
| **优点**     | **简单直观**，**上手快**，**权重系统强大**。                 |
| **缺点**     | 容易导致**视图嵌套过深**，影响性能。                         |
| **适用场景** | **简单的线性排列布局**、**等分布局**、**表单**、**标签栏**。 |
| **地位**     | **经典型布局**，适合简单场景，是 Android UI 的基础组成部分。 |

**建议**：对于简单的、线性的布局，`LinearLayout` 依然是绝佳的选择，因为它写起来又快又清晰。但对于任何复杂的、需要适配多种屏幕的界面，都应优先考虑使用 `ConstraintLayout`。

### 示例

>说明：`LinearLayout` 和 `layout_weight` 的用法。
>
>详细用法请参考本站 [示例](https://gitee.com/dexterleslie/demonstration/tree/main/demo-android/demo-linearlayout)



## 布局 - `TabLayout`

注意：`android.support.design.widget.TabLayout` 是旧版支持库中的组件，Google 已推荐迁移到 `com.google.android.material.tabs.TabLayout` (AndroidX)。不过，如果您仍需使用旧版支持库实现，以下是完整指南。

### 基础实现步骤

#### 1. 添加依赖

在 app 的 build.gradle 文件中添加设计支持库依赖：

```gradle
dependencies {
    implementation 'com.android.support:design:28.0.0' // 使用最新支持库版本
}
```

#### 2. 布局文件 (activity_main.xml)

```xml
<?xml version="1.0" encoding="utf-8"?>
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:orientation="vertical">

    <!-- 旧版支持库的TabLayout -->
    <android.support.design.widget.TabLayout
        android:id="@+id/tabLayout"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        app:tabMode="fixed" <!-- fixed或scrollable -->
        app:tabGravity="fill" <!-- fill或center -->
        app:tabIndicatorColor="@color/colorPrimary"
        app:tabSelectedTextColor="@color/colorPrimary"
        app:tabTextColor="@color/gray" />

    <!-- 旧版ViewPager -->
    <android.support.v4.view.ViewPager
        android:id="@+id/viewPager"
        android:layout_width="match_parent"
        android:layout_height="0dp"
        android:layout_weight="1" />

</LinearLayout>
```

#### 3. 创建 Fragment

创建三个简单的 Fragment 用于 Tab 内容展示：

**HomeFragment.java**

```java
public class HomeFragment extends Fragment {
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        TextView textView = view.findViewById(R.id.text_home);
        textView.setText("首页内容");
        return view;
    }
}
```

**DiscoverFragment.java**
```java
public class DiscoverFragment extends Fragment {
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_discover, container, false);
        TextView textView = view.findViewById(R.id.text_discover);
        textView.setText("发现内容");
        return view;
    }
}
```

**ProfileFragment.java**
```java
public class ProfileFragment extends Fragment {
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        TextView textView = view.findViewById(R.id.text_profile);
        textView.setText("个人中心");
        return view;
    }
}
```

#### 4. 创建 ViewPager 适配器

**ViewPagerAdapter.java**
```java
public class ViewPagerAdapter extends FragmentPagerAdapter {
    private final List<Fragment> fragmentList = new ArrayList<>();
    private final List<String> fragmentTitleList = new ArrayList<>();

    public ViewPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        return fragmentList.get(position);
    }

    @Override
    public int getCount() {
        return fragmentList.size();
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return fragmentTitleList.get(position);
    }

    public void addFragment(Fragment fragment, String title) {
        fragmentList.add(fragment);
        fragmentTitleList.add(title);
    }
}
```

#### 5. 在 Activity 中设置 TabLayout 和 ViewPager

**MainActivity.java**
```java
public class MainActivity extends AppCompatActivity {
    
    private TabLayout tabLayout;
    private ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 初始化视图
        tabLayout = findViewById(R.id.tabLayout);
        viewPager = findViewById(R.id.viewPager);
        
        // 设置适配器
        setupViewPager(viewPager);
        
        // 绑定TabLayout和ViewPager
        tabLayout.setupWithViewPager(viewPager);
        
        // 设置Tab图标
        setupTabIcons();
        
        // 添加标签切换监听
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                // 标签被选中时触发
                Toast.makeText(MainActivity.this, 
                    "选中: " + tab.getText(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                // 标签从选中状态变为未选中
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                // 已选中的标签再次被点击
            }
        });
    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new HomeFragment(), "首页");
        adapter.addFragment(new DiscoverFragment(), "发现");
        adapter.addFragment(new ProfileFragment(), "我的");
        viewPager.setAdapter(adapter);
    }

    private void setupTabIcons() {
        tabLayout.getTabAt(0).setIcon(R.drawable.ic_home);
        tabLayout.getTabAt(1).setIcon(R.drawable.ic_discover);
        tabLayout.getTabAt(2).setIcon(R.drawable.ic_profile);
    }
}
```

### 自定义 Tab 视图

#### 1. 创建自定义布局文件 (custom_tab.xml)

```xml
<?xml version="1.0" encoding="utf-8"?>
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="wrap_content"
    android:layout_height="wrap_content"
    android:orientation="vertical"
    android:gravity="center"
    android:padding="8dp">

    <ImageView
        android:id="@+id/tabIcon"
        android:layout_width="24dp"
        android:layout_height="24dp" />

    <TextView
        android:id="@+id/tabText"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:textSize="12sp"
        android:layout_marginTop="4dp" />

</LinearLayout>
```

#### 2. 修改 Activity 代码

```java
private void setupTabIcons() {
    for (int i = 0; i < tabLayout.getTabCount(); i++) {
        TabLayout.Tab tab = tabLayout.getTabAt(i);
        if (tab != null) {
            View customView = LayoutInflater.from(this).inflate(R.layout.custom_tab, null);
            ImageView icon = customView.findViewById(R.id.tabIcon);
            TextView text = customView.findViewById(R.id.tabText);
            
            switch (i) {
                case 0:
                    icon.setImageResource(R.drawable.ic_home);
                    text.setText("首页");
                    break;
                case 1:
                    icon.setImageResource(R.drawable.ic_discover);
                    text.setText("发现");
                    break;
                case 2:
                    icon.setImageResource(R.drawable.ic_profile);
                    text.setText("我的");
                    break;
            }
            
            tab.setCustomView(customView);
        }
    }
}
```

### 迁移到 AndroidX 的建议

虽然上述代码使用旧版支持库可以正常工作，但 Google 已停止对支持库的更新，强烈建议迁移到 AndroidX：

1. 在 `gradle.properties` 中添加：
   ```properties
   android.useAndroidX=true
   android.enableJetifier=true
   ```

2. 将依赖替换为：
   ```gradle
   implementation 'com.google.android.material:material:1.6.0'
   ```

3. 将布局中的 `android.support.design.widget.TabLayout` 替换为 `com.google.android.material.tabs.TabLayout`

4. 将 `android.support.v4.view.ViewPager` 替换为 `androidx.viewpager2.widget.ViewPager2`

### 常见问题解决

#### 1. TabLayout 不显示标签

**检查**：
- 是否正确调用了 `setupWithViewPager()`
- 适配器是否实现了 `getPageTitle()` 方法
- 是否在 `ViewPager` 设置适配器后才绑定 `TabLayout`

#### 2. 标签点击无反应

**解决方案**：
- 确保 `ViewPager` 的适配器已正确设置
- 检查 `ViewPager` 的 `android:layout_weight` 是否正确
- 确认没有其他视图遮挡了 `TabLayout`

#### 3. 自定义 Tab 视图不生效

**检查**：
- 自定义布局的尺寸是否正确
- 是否在 `setupWithViewPager()` 之后才设置自定义视图
- 自定义视图中各控件 ID 是否正确引用

### 总结

虽然 `android.support.design.widget.TabLayout` 仍然可用，但为了获得更好的支持和更多功能，建议尽快迁移到 AndroidX 的 Material 组件库。迁移过程相对简单，且能带来更好的性能和更多的自定义选项。

### 示例

>详细用法请参考本站 [示例](https://gitee.com/dexterleslie/demonstration/tree/main/demo-android/demo-tablayout)



## 布局 - `GridLayout`

GridLayout 是 Android 4.0 (Ice Cream Sandwich) 引入的一个强大的网格布局管理器，它允许开发者创建复杂的网格布局而无需嵌套多个布局容器。

### GridLayout 基本特性

1. **网格系统**：将子视图排列在行和列组成的网格中
2. **灵活的空间分配**：支持权重分配空间
3. **无嵌套布局**：减少视图层级，提高性能
4. **对齐控制**：支持行列对齐方式设置
5. **跨行跨列**：子视图可以跨越多个行或列

### 基本用法

#### 1. XML 布局中声明 GridLayout

```xml
<GridLayout
    xmlns:android="http://schemas.android.com/apk/res/android"
    android:id="@+id/gridLayout"
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    android:columnCount="4"  <!-- 设置总列数 -->
    android:rowCount="3"     <!-- 设置总行数 -->
    android:alignmentMode="alignMargins"
    android:columnOrderPreserved="false"
    android:useDefaultMargins="true">
    
    <!-- 子视图将在这里添加 -->
</GridLayout>
```

#### 2. 常用属性

| 属性                           | 说明                                |
| ------------------------------ | ----------------------------------- |
| `android:columnCount`          | 最大列数                            |
| `android:rowCount`             | 最大行数                            |
| `android:orientation`          | 排列方向 (horizontal/vertical)      |
| `android:alignmentMode`        | 对齐模式 (alignBounds/alignMargins) |
| `android:columnOrderPreserved` | 是否保持列顺序                      |
| `android:useDefaultMargins`    | 是否使用默认边距                    |

### 子视图布局参数

每个子视图可以使用以下参数控制其在网格中的位置和大小：

```xml
<Button
    android:layout_width="wrap_content"
    android:layout_height="wrap_content"
    android:text="按钮1"
    
    <!-- GridLayout 特有属性 -->
    android:layout_row="0"          <!-- 从第0行开始 -->
    android:layout_column="0"       <!-- 从第0列开始 -->
    android:layout_rowSpan="2"      <!-- 跨2行 -->
    android:layout_columnSpan="3"   <!-- 跨3列 -->
    android:layout_gravity="fill" /> <!-- 填充可用空间 -->
```

### Java 代码动态创建 GridLayout

```java
public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // 创建GridLayout
        GridLayout gridLayout = new GridLayout(this);
        gridLayout.setColumnCount(3);
        gridLayout.setRowCount(3);
        gridLayout.setUseDefaultMargins(true);
        
        // 创建9个按钮添加到网格
        for (int i = 0; i < 9; i++) {
            Button button = new Button(this);
            button.setText("按钮 " + (i + 1));
            
            // 设置布局参数
            GridLayout.LayoutParams params = new GridLayout.LayoutParams();
            params.rowSpec = GridLayout.spec(i / 3);  // 行位置
            params.columnSpec = GridLayout.spec(i % 3); // 列位置
            params.width = 0;  // 0表示使用权重
            params.height = GridLayout.LayoutParams.WRAP_CONTENT;
            params.setGravity(Gravity.FILL);
            params.columnSpec = GridLayout.spec(i % 3, 1f); // 权重为1
            
            button.setLayoutParams(params);
            gridLayout.addView(button);
        }
        
        setContentView(gridLayout);
    }
}
```

### 权重分配示例

使用权重实现等宽列：

```java
GridLayout gridLayout = new GridLayout(this);
gridLayout.setColumnCount(3);
gridLayout.setUseDefaultMargins(true);

// 添加3个等宽按钮
for (int i = 0; i < 3; i++) {
    Button button = new Button(this);
    button.setText("按钮 " + (i + 1));
    
    GridLayout.Spec rowSpec = GridLayout.spec(0); // 都在第0行
    GridLayout.Spec columnSpec = GridLayout.spec(i, 1f); // 权重为1
    
    GridLayout.LayoutParams params = new GridLayout.LayoutParams(rowSpec, columnSpec);
    params.width = 0; // 宽度由权重决定
    params.height = GridLayout.LayoutParams.WRAP_CONTENT;
    params.setGravity(Gravity.FILL);
    
    button.setLayoutParams(params);
    gridLayout.addView(button);
}
```

### 复杂布局示例

创建一个计算器界面：

```java
public class CalculatorActivity extends AppCompatActivity {
    private String[] buttons = {
            "7", "8", "9", "/",
            "4", "5", "6", "*",
            "1", "2", "3", "-",
            "C", "0", "=", "+"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        GridLayout gridLayout = new GridLayout(this);
        gridLayout.setColumnCount(4);
        gridLayout.setUseDefaultMargins(true);
        
        // 添加显示屏
        TextView display = new TextView(this);
        display.setText("0");
        display.setTextSize(24);
        display.setGravity(Gravity.END);
        display.setPadding(8, 8, 8, 8);
        
        GridLayout.LayoutParams displayParams = new GridLayout.LayoutParams();
        displayParams.columnSpec = GridLayout.spec(0, 4); // 跨4列
        displayParams.width = GridLayout.LayoutParams.MATCH_PARENT;
        displayParams.height = GridLayout.LayoutParams.WRAP_CONTENT;
        display.setLayoutParams(displayParams);
        gridLayout.addView(display);
        
        // 添加按钮
        for (int i = 0; i < buttons.length; i++) {
            Button button = new Button(this);
            button.setText(buttons[i]);
            button.setTextSize(18);
            
            GridLayout.LayoutParams params = new GridLayout.LayoutParams();
            params.rowSpec = GridLayout.spec(i / 4 + 1); // 从第1行开始
            params.columnSpec = GridLayout.spec(i % 4);
            params.width = 0; // 使用权重
            params.height = GridLayout.LayoutParams.WRAP_CONTENT;
            params.setGravity(Gravity.FILL);
            params.columnSpec = GridLayout.spec(i % 4, 1f); // 权重为1
            
            button.setLayoutParams(params);
            gridLayout.addView(button);
            
            // 添加点击事件
            button.setOnClickListener(v -> {
                String currentText = display.getText().toString();
                String buttonText = ((Button)v).getText().toString();
                
                if (buttonText.equals("C")) {
                    display.setText("0");
                } else if (buttonText.equals("=")) {
                    // 计算结果逻辑
                    try {
                        double result = eval(currentText);
                        display.setText(String.valueOf(result));
                    } catch (Exception e) {
                        display.setText("错误");
                    }
                } else {
                    if (currentText.equals("0")) {
                        display.setText(buttonText);
                    } else {
                        display.setText(currentText + buttonText);
                    }
                }
            });
        }
        
        setContentView(gridLayout);
    }
    
    // 简单的表达式计算函数
    private double eval(String expression) {
        return new Object() {
            int pos = -1, ch;
            
            void nextChar() {
                ch = (++pos < expression.length()) ? expression.charAt(pos) : -1;
            }
            
            boolean eat(int charToEat) {
                while (ch == ' ') nextChar();
                if (ch == charToEat) {
                    nextChar();
                    return true;
                }
                return false;
            }
            
            double parse() {
                nextChar();
                double x = parseExpression();
                if (pos < expression.length()) throw new RuntimeException("意外字符: " + (char)ch);
                return x;
            }
            
            double parseExpression() {
                double x = parseTerm();
                for (;;) {
                    if      (eat('+')) x += parseTerm();
                    else if (eat('-')) x -= parseTerm();
                    else return x;
                }
            }
            
            double parseTerm() {
                double x = parseFactor();
                for (;;) {
                    if      (eat('*')) x *= parseFactor();
                    else if (eat('/')) x /= parseFactor();
                    else return x;
                }
            }
            
            double parseFactor() {
                if (eat('+')) return parseFactor();
                if (eat('-')) return -parseFactor();
                
                double x;
                int startPos = this.pos;
                if (eat('(')) {
                    x = parseExpression();
                    eat(')');
                } else if ((ch >= '0' && ch <= '9') || ch == '.') {
                    while ((ch >= '0' && ch <= '9') || ch == '.') nextChar();
                    x = Double.parseDouble(expression.substring(startPos, this.pos));
                } else {
                    throw new RuntimeException("意外字符: " + (char)ch);
                }
                
                return x;
            }
        }.parse();
    }
}
```

### GridLayout 与 GridView 的区别

| 特性           | GridLayout   | GridView     |
| -------------- | ------------ | ------------ |
| **用途**       | 布局管理器   | 适配器视图   |
| **数据绑定**   | 静态布局     | 动态数据绑定 |
| **子视图数量** | 固定         | 可变         |
| **滚动**       | 不支持       | 支持         |
| **性能**       | 适合少量视图 | 适合大量数据 |
| **灵活性**     | 精确控制位置 | 自动排列     |

GridLayout 适合创建静态的、结构明确的网格界面，而 GridView/RecyclerView 更适合显示动态数据列表。

### 示例

>说明：使用 `GridLayout` 模仿美团 `App` 推荐页的功能导航功能。
>
>详细用法请参考本站 [示例](https://gitee.com/dexterleslie/demonstration/tree/main/demo-android/demo-gridlayout)

```xml
<?xml version="1.0" encoding="utf-8"?>
<GridLayout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    xmlns:tools="http://schemas.android.com/tools"
    android:id="@+id/main"
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    android:columnCount="4"
    android:rowCount="2"
    android:useDefaultMargins="true"
    tools:context=".MainActivity">

    <!-- layout_columnWeight用于指定某个子视图在 GridLayout中所占列的权重，从而实现按比例分配剩余空间。-->
    <!-- layout_marginTop、layout_marginBottom用于指定子元素的top、bottom margin -->
    <Button
        android:layout_width="0dp"
        android:layout_height="wrap_content"
        android:layout_columnWeight="1"
        android:gravity="center_horizontal"
        android:layout_marginTop="5dp"
        android:layout_marginBottom="5dp"
        android:text="功能1"/>
    <Button
        android:layout_width="0dp"
        android:layout_height="wrap_content"
        android:layout_columnWeight="1"
        android:gravity="center_horizontal"
        android:layout_marginTop="5dp"
        android:layout_marginBottom="5dp"
        android:text="功能2" />
    <Button
        android:layout_width="0dp"
        android:layout_height="wrap_content"
        android:layout_columnWeight="1"
        android:gravity="center_horizontal"
        android:layout_marginTop="5dp"
        android:layout_marginBottom="5dp"
        android:text="功能3" />
    <Button
        android:layout_width="0dp"
        android:layout_height="wrap_content"
        android:layout_columnWeight="1"
        android:gravity="center_horizontal"
        android:layout_marginTop="5dp"
        android:layout_marginBottom="5dp"
        android:text="功能4" />
    <Button
        android:layout_width="0dp"
        android:layout_height="wrap_content"
        android:layout_columnWeight="1"
        android:gravity="center_horizontal"
        android:layout_marginTop="5dp"
        android:layout_marginBottom="5dp"
        android:text="功能5" />

    <Button
        android:layout_width="0dp"
        android:layout_height="wrap_content"
        android:layout_columnWeight="1"
        android:gravity="center_horizontal"
        android:layout_marginTop="5dp"
        android:layout_marginBottom="5dp"
        android:text="功能6" />
    <Button
        android:layout_width="0dp"
        android:layout_height="wrap_content"
        android:layout_columnWeight="1"
        android:gravity="center_horizontal"
        android:layout_marginTop="5dp"
        android:layout_marginBottom="5dp"
        android:text="功能7" />
    <Button
        android:layout_width="0dp"
        android:layout_height="wrap_content"
        android:layout_columnWeight="1"
        android:gravity="center_horizontal"
        android:layout_marginTop="5dp"
        android:layout_marginBottom="5dp"
        android:text="功能8" />
    <Button
        android:layout_width="0dp"
        android:layout_height="wrap_content"
        android:layout_columnWeight="1"
        android:gravity="center_horizontal"
        android:layout_marginTop="5dp"
        android:layout_marginBottom="5dp"
        android:text="功能9" />
    <Button
        android:layout_width="0dp"
        android:layout_height="wrap_content"
        android:layout_columnWeight="1"
        android:gravity="center_horizontal"
        android:layout_marginTop="5dp"
        android:layout_marginBottom="5dp"
        android:text="功能10" />

</GridLayout>
```



## 布局 - `gravity`和`layout_gravity`区别

在 Android 布局中，`android:gravity` 和 `android:layout_gravity` 是两个容易混淆但作用完全不同的属性。它们的核心区别在于**作用对象**和**控制范围**的不同。以下是详细对比：

---

### 1. `android:gravity`  
**作用对象**：**当前 View 自身的内容**（如 TextView 的文字、ImageView 的图片、LinearLayout 的子 View 等）。  
**功能**：控制**内容**在**当前 View 内部**的对齐方式。  

#### 适用场景举例：
- 让 `TextView` 的文字居中显示。
- 让 `ImageView` 的图片在控件内靠右显示。
- 让 `LinearLayout` 的所有子 View 在布局内底部对齐。

#### 常用值：
| 值                                  | 说明                   |
| ----------------------------------- | ---------------------- |
| `left` / `start`                    | 内容靠左（起始边）对齐 |
| `right` / `end`                     | 内容靠右（结束边）对齐 |
| `top`                               | 内容顶部对齐           |
| `bottom`                            | 内容底部对齐           |
| `center`                            | 内容水平和垂直居中     |
| `center_vertical`                   | 内容垂直居中           |
| `center_horizontal`                 | 内容水平居中           |
| 组合值（如 `left|center_vertical`） | 同时指定多个方向的对齐 |

#### 示例代码：
```xml
<!-- TextView 文字居中 -->
<TextView
    android:layout_width="200dp"
    android:layout_height="100dp"
    android:gravity="center"  <!-- 文字在 TextView 内部居中 -->
    android:text="Hello World" />

<!-- LinearLayout 的子 View 在布局内底部对齐 -->
<LinearLayout
    android:layout_width="match_parent"
    android:layout_height="300dp"
    android:gravity="bottom"  <!-- 所有子 View 在 LinearLayout 内部底部对齐 -->
    android:orientation="vertical">
    <Button
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:text="Button 1" />
</LinearLayout>
```

---

### 2. `android:layout_gravity`  
**作用对象**：**当前 View 自身**（作为子 View 时）。  
**功能**：控制**当前 View** 在**父容器**中的对齐方式。  

#### 适用场景举例：
- 让 `Button` 在 `LinearLayout` 中靠右显示。
- 让 `TextView` 在 `FrameLayout` 中居中显示。
- 让 `ImageView` 在父布局底部显示。

#### 注意事项：
- 仅在父容器支持自由定位时有效（如 `LinearLayout`、`FrameLayout`），**在 `ConstraintLayout` 中无效**（应使用约束代替）。
- 在 `LinearLayout` 中，方向会影响效果：
  - 垂直方向的 `LinearLayout`：`layout_gravity` 只能控制**水平方向**的对齐（如 `left`/`right`/`center_horizontal`）。
  - 水平方向的 `LinearLayout`：`layout_gravity` 只能控制**垂直方向**的对齐（如 `top`/`bottom`/`center_vertical`）。

#### 示例代码：
```xml
<!-- Button 在 LinearLayout 中靠右显示 -->
<LinearLayout
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    android:orientation="horizontal">
    <Button
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:layout_gravity="end"  <!-- Button 自身在父布局中靠右 -->
        android:text="Button" />
</LinearLayout>

<!-- TextView 在 FrameLayout 中居中 -->
<FrameLayout
    android:layout_width="300dp"
    android:layout_height="300dp">
    <TextView
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:layout_gravity="center"  <!-- TextView 自身在父布局中居中 -->
        android:text="Centered Text" />
</FrameLayout>
```

---

### 对比总结表
| 属性         | `android:gravity`                        | `android:layout_gravity`                             |
| ------------ | ---------------------------------------- | ---------------------------------------------------- |
| **作用对象** | 当前 View 的**内容**                     | 当前 View **自身**                                   |
| **控制范围** | View **内部**                            | 父容器**内**的 View 位置                             |
| **适用容器** | 所有 View（如 TextView、LinearLayout）   | 仅支持自由定位的容器（如 LinearLayout、FrameLayout） |
| **典型用途** | 文字居中、图片对齐、子 View 在布局内对齐 | 控件在父布局中的位置调整                             |

---

### 常见问题解答

#### Q1：为什么 `layout_gravity` 在 LinearLayout 中有时无效？
- **原因**：`LinearLayout` 的方向限制了 `layout_gravity` 的有效方向。
  - 垂直 `LinearLayout`：只能水平对齐（`left`/`right`/`center_horizontal`）。
  - 水平 `LinearLayout`：只能垂直对齐（`top`/`bottom`/`center_vertical`）。
- **解决方案**：改用 `ConstraintLayout` 或 `FrameLayout`。

#### Q2：如何同时控制内容和 View 的位置？
- 组合使用两个属性：
  ```xml
  <TextView
      android:layout_width="200dp"
      android:layout_height="100dp"
      android:gravity="center"          <!-- 文字居中 -->
      android:layout_gravity="end"      <!-- 控件自身在父布局中靠右 -->
      android:text="Hello World" />
  ```

#### Q3：`ConstraintLayout` 中如何替代 `layout_gravity`？
- 使用约束（Constraint）代替：
  ```xml
  <Button
      app:layout_constraintEnd_toEndOf="parent"  <!-- 替代 layout_gravity="end" -->
      app:layout_constraintTop_toTopOf="parent"
      app:layout_constraintBottom_toBottomOf="parent" />
  ```

---

### 记忆技巧
- **gravity** = **"内容"** 的对齐（如文字、图片、子 View 在容器内）。
- **layout_gravity** = **"自己"** 的对齐（作为子 View 时在父容器中的位置）。



## 布局 - `LayoutInflater`

>参考链接：https://www.cnblogs.com/sanjinxiong/articles/2125142.html

### 核心定义

**LayoutInflater**（布局填充器）是 Android 系统中一个用于**将 XML 布局文件动态解析并转换为实际的 View 对象**的工具类。它的本质是一个**将静态的 XML 布局“膨胀”（inflate）为内存中的 View 层级结构**的机制。

您可以把它想象成一个“布局解析工厂”：
- **输入**：XML 布局文件（如 `res/layout/activity_main.xml`）。
- **输出**：对应的 `View` 或 `ViewGroup` 对象树。

---

### 为什么需要 LayoutInflater？

1. **动态加载布局**：并非所有界面都直接在 `Activity.onCreate()` 中用 `setContentView()` 设置，有时需要**运行时动态加载**（如 RecyclerView 的 Item、Dialog 的自定义布局、Fragment 的布局）。
2. **性能优化**：避免在不需要时加载复杂布局，按需动态加载。
3. **复用性**：同一布局文件可被多次解析为不同的 View 实例。

---

### 核心使用场景

#### 1. Activity 的 `setContentView()`
当您调用 `setContentView(R.layout.activity_main)` 时，系统内部实际上是通过 `LayoutInflater` 将 XML 转换为 View 并附加到 Activity 的窗口上。

#### 2. Fragment 的 `onCreateView()`
```java
@Override
public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    // 将 fragment_layout.xml 解析为 View
    return inflater.inflate(R.layout.fragment_layout, container, false);
}
```

#### 3. RecyclerView 的 Adapter
```java
@Override
public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    // 将 item_layout.xml 解析为 View
    View view = LayoutInflater.from(parent.getContext())
            .inflate(R.layout.item_layout, parent, false);
    return new ViewHolder(view);
}
```

#### 4. 自定义 Dialog 或 PopupWindow
```java
Dialog dialog = new Dialog(context);
View dialogView = LayoutInflater.from(context)
        .inflate(R.layout.dialog_custom, null);
dialog.setContentView(dialogView);
```

#### 5. 动态添加 View 到现有布局
```java
ViewGroup parent = findViewById(R.id.container);
View child = LayoutInflater.from(this)
        .inflate(R.layout.child_view, parent, false);
parent.addView(child);
```

---

### 关键方法解析

#### 1. 获取 LayoutInflater 实例
有三种方式：
```java
// 方式1：从系统服务获取（最常用）
LayoutInflater inflater = LayoutInflater.from(context);

// 方式2：通过 Activity 的方法获取
LayoutInflater inflater = getLayoutInflater(); // 仅在 Activity 中可用

// 方式3：从 Context 获取
LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
```

#### 2. 核心 inflate() 方法
```java
public View inflate(@LayoutRes int resource, @Nullable ViewGroup root, boolean attachToRoot)
```

- **参数说明**：
  - `resource`：XML 布局资源 ID（如 `R.layout.my_layout`）。
  - `root`：可选父容器（用于正确生成布局参数 `LayoutParams`）。
  - `attachToRoot`：是否将生成的 View 立即添加到 `root` 中。
    - **`true`**：解析的 View 会自动添加到 `root`，并返回 `root`。
    - **`false`**：仅解析 View，不自动添加（需手动 `addView()`）。

- **返回值**：
  - 如果 `attachToRoot=true`，返回 `root`。
  - 如果 `attachToRoot=false`，返回解析后的 View 根对象。

#### 3. 常用方法重载
```java
// 最简形式（无父容器，无 attachToRoot）
inflater.inflate(R.layout.simple_layout, null);

// 指定父容器但不自动附加
inflater.inflate(R.layout.simple_layout, parent, false);

// 指定父容器并自动附加（等同于 inflate + parent.addView()）
inflater.inflate(R.layout.simple_layout, parent, true);
```

---

### 工作原理（源码简析）

1. **XML 解析**：
   - 使用 `XmlPullParser` 解析 XML 文件。
   - 根据标签名（如 `<TextView>`）通过反射创建对应的 View 实例。

2. **属性处理**：
   - 读取 XML 中的 `android:` 属性（如 `android:layout_width`）。
   - 通过 `AttributeSet` 将属性值设置到 View 对象。

3. **递归构建**：
   - 深度优先遍历 XML 节点树，递归创建所有子 View。
   - 维护父子关系，设置 `LayoutParams`。

4. **性能优化**：
   - 使用 `WeakHashMap` 缓存反射的构造函数（Android 2.3+）。

---

### 注意事项与最佳实践

1. **正确处理父容器和 attachToRoot**：
   - 如果后续需要手动 `addView()`，必须使用 `inflate(..., parent, false)`。
   - 错误示例：
     ```java
     // 错误！会导致重复添加（可能引发 IllegalStateException）
     View view = inflater.inflate(R.layout.child, parent, true);
     parent.addView(view);
     ```

2. **避免传递 null 作为 root**：
   - 如果 `root=null`，生成的 View 的 `LayoutParams` 会丢失（可能导致布局异常）。
   - 正确做法：
     ```java
     // 推荐：即使不立即附加，也传递 parent 以保证 LayoutParams 正确
     View view = inflater.inflate(R.layout.child, parent, false);
     parent.addView(view);
     ```

3. **性能优化**：
   - 避免在滚动列表（如 RecyclerView）中频繁解析复杂布局。
   - 考虑使用 `ViewStub` 延迟加载不立即显示的布局。

4. **自定义 View 的特殊处理**：
   - 自定义 View 的构造函数需支持 `AttributeSet`：
     ```java
     public MyView(Context context, AttributeSet attrs) {
         super(context, attrs);
         // 解析自定义属性
     }
     ```

---

### 常见问题解答

#### Q1：`inflate()` 的 `attachToRoot` 参数到底怎么用？
- **`true`**：适合“一次性”场景（如 Fragment 的 `onCreateView()` 返回的 View 会自动附加到容器）。
- **`false`**：适合需要手动控制添加时机的场景（如 RecyclerView 的 ItemView 由 Adapter 管理添加）。

#### Q2：为什么有时布局参数（如 width/height）失效？
- 通常是因为 `root` 参数传递了 `null`，导致无法生成正确的 `LayoutParams`。务必传递父容器引用。

#### Q3：LayoutInflater 和 ViewBinding/DataBinding 的关系？
- **ViewBinding/DataBinding** 是更高级的封装，底层仍依赖 `LayoutInflater`。
- 它们通过生成绑定类来避免 `findViewById()`，但布局解析过程不变。

---

### 总结

| 特性         | 说明                                                         |
| ------------ | ------------------------------------------------------------ |
| **本质**     | XML 布局 → View 对象的转换器                                 |
| **核心方法** | `inflate(int resource, ViewGroup root, boolean attachToRoot)` |
| **主要场景** | Activity/Fragment/Adapter/Dialog 的布局动态加载              |
| **性能影响** | 反射创建 View 有一定开销，应避免频繁调用                     |
| **关联技术** | `ViewStub`（延迟加载）、`Merge` 标签（优化层级）             |

**一句话记忆**：  
LayoutInflater 是 Android 的“布局解析引擎”，负责将 XML 文件“吹胀”成内存中的 View 树，是动态界面构建的基石。



## `Android Support`库和`AndroidX`库区别

**`android.support` 和 `androidx` 本质上是同一套库，只是 `androidx` 是新的、官方标准的命名和打包方式。**

您可以把它理解为一次大规模的品牌重塑和技术升级：

*   **`android.support`** (Android Support Library)：是 **旧的、已废弃 (deprecated)** 的命名空间。
*   **`androidx`** (AndroidX)：是 **新的、官方当前强制要求** 的命名空间。

它们是谷歌为了更好的管理、维护和标准化支持库而进行的一次**重大重构和重命名**。

---

### 对比表格

| 特性         | `android.support` (旧)                                       | `androidx` (新)                                              |
| :----------- | :----------------------------------------------------------- | :----------------------------------------------------------- |
| **包名**     | `com.android.support:appcompat-v7`<br>`com.android.support:design` | `androidx.appcompat:appcompat`<br>`com.google.android.material:material` |
| **命名空间** | 杂乱，**不统一** (如 `v4`, `v7`...)                          | **统一、简洁**，所有库都以 `androidx` 开头                   |
| **版本管理** | 各个库版本号**独立**，难以同步                               | 所有库版本号**统一**，易于管理                               |
| **发布频率** | 与 Android 平台版本绑定，更新慢                              | **独立于**操作系统发布，更新更快                             |
| **现状**     | **已废弃**，不再维护                                         | **官方现行标准**，所有新项目**必须使用**                     |
| **语义版本** | 不严格遵循                                                   | **严格遵循**                                                 |

---

### 为什么会有这个变化？(从 Support 到 AndroidX)

旧的 `android.support` 库存在很多历史遗留问题，让开发者非常头疼：

1.  **混乱的包结构和版本管理**：
    *   旧库有 `support-v4`, `appcompat-v7`, `recyclerview-v7` 等。这里的 `v7` 原本代表最低支持 API 级别，但早已名不副实，造成极大混淆。
    *   这些库的版本号彼此独立，依赖管理像一场噩梦。比如 `appcompat-v7` 是 `27.1.1`，而 `recyclerview-v7` 可能是 `27.0.2`，非常容易导致冲突。

2.  **与操作系统强绑定**：
    *   旧 Support Library 的发布节奏与 Android 系统版本绑定，无法快速迭代新功能或修复问题。

**AndroidX 就是为了解决这些问题而生的**，它带来了：

*   **统一的包名**：所有库都归到 `androidx` 命名空间下，结构清晰。
    *   `androidx.appcompat`
    *   `androidx.recyclerview`
    *   `androidx.constraintlayout`
*   **严格的语义化版本控制**：版本号 `Major.Minor.Patch` 的变更有了明确约定。
*   **独立发布**：可以随时更新，不受系统版本限制，能更快地提供新功能和修复。

### 如何迁移？

如果您有一个使用旧 Support Library 的项目，强烈建议迁移到 AndroidX。

#### 1. 必要设置
在项目根目录的 `gradle.properties` 文件中，确保有以下两行：
```properties
android.useAndroidX=true
android.enableJetifier=true
```
*   `android.useAndroidX=true`：表示项目在编译时使用 AndroidX 包。
*   `android.enableJetifier=true`：这是一个非常强大的功能，表示 Gradle 会自动将项目依赖的**第三方库**中的旧 Support 包重写为对应的 AndroidX 包。

#### 2. 一键迁移 (推荐)
使用 Android Studio 提供的自动化工具：
1.  菜单栏 -> **Refactor** -> **Migrate to AndroidX...**
2.  Studio 会分析你的项目并提供一份迁移预览。
3.  确认后，它会自动完成绝大部分的**包名**和**导入语句**的替换。

**注意**：迁移后务必进行全面的测试，因为自动迁移可能无法覆盖所有情况（特别是那些通过反射或代码生成的类名）。

---

### 常见库的映射关系

| 旧 Support Library (`com.android.support:`) | 新 AndroidX 库                                               |
| :------------------------------------------ | :----------------------------------------------------------- |
| `appcompat-v7`                              | `androidx.appcompat:appcompat`                               |
| `design`                                    | `com.google.android.material:material`                       |
| `recyclerview-v7`                           | `androidx.recyclerview:recyclerview`                         |
| `cardview-v7`                               | `androidx.cardview:cardview`                                 |
| `support-v4`                                | 被拆分 (如 `androidx.core:core`, `androidx.fragment:fragment`) |
| `constraint-layout`                         | `androidx.constraintlayout:constraintlayout`                 |

您可以在官方的 https://developer.android.com/jetpack/androidx/migrate/class-mappings 中查看完整的对应关系。

### 总结与建议

|              | 建议                                                         |
| :----------- | :----------------------------------------------------------- |
| **新项目**   | **必须、且只能使用 AndroidX**。这是 Android 开发的现行标准和起点。 |
| **老项目**   | **强烈建议安排时间进行迁移**。旧 Support Library 已停止维护，不迁移未来会遇到越来越多的兼容性问题，也无法使用 Jetpack 的新特性。 |
| **第三方库** | 现在绝大多数流行的第三方库都已支持 AndroidX。如果遇到尚未迁移的库，`Jetifier` 工具可以帮你自动转换其二进制依赖。 |

**核心结论**：`androidx` 不是另一个新库，它就是 `android.support` 库的官方升级版和替代品。这是一个不可逆的趋势，所有新的开发工作都必须基于 AndroidX。



## `Fragment` - 概念

Fragment（片段）是 Android 中的一个重要组件，它可以理解为**Activity 中的模块化 UI 片段**，具有自己的生命周期和用户界面。

### 核心概念

Fragment 具有以下关键特性：

1. **模块化设计**：允许将 Activity 界面分解为多个独立、可重用的部分
2. **独立生命周期**：拥有与 Activity 类似但更复杂的生命周期
3. **灵活组合**：可以在运行时动态添加、移除或替换
4. **适配多屏幕**：特别适合平板和手机的不同屏幕尺寸适配

### Fragment 与 Activity 的区别

| 特性         | Fragment                   | Activity     |
| ------------ | -------------------------- | ------------ |
| **生命周期** | 依附于宿主 Activity        | 独立         |
| **UI 组成**  | Activity 的一部分          | 完整的窗口   |
| **启动方式** | 必须嵌入 Activity 中       | 可以独立启动 |
| **复用性**   | 高，可在多个 Activity 重用 | 低           |
| **后台运行** | 不能独立运行               | 可以独立运行 |

### Fragment 生命周期

Fragment 的生命周期比 Activity 更复杂，主要包含以下状态和回调方法：

```
onAttach() → onCreate() → onCreateView() → onActivityCreated() → onStart() → onResume() → onPause() → onStop() → onDestroyView() → onDestroy() → onDetach()
```

### 基本使用示例

#### 1. 创建 Fragment

```java
public class MyFragment extends Fragment {
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // 加载布局文件
        return inflater.inflate(R.layout.fragment_my, container, false);
    }
    
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // 初始化视图组件
        Button button = view.findViewById(R.id.my_button);
        button.setOnClickListener(v -> {
            // 处理点击事件
        });
    }
}
```

#### 2. 在 Activity 中添加 Fragment

##### XML 方式（静态添加）

```xml
<!-- activity_main.xml -->
<FrameLayout
    xmlns:android="http://schemas.android.com/apk/res/android"
    android:id="@+id/fragment_container"
    android:layout_width="match_parent"
    android:layout_height="match_parent">

    <fragment
        android:name="com.example.MyFragment"
        android:id="@+id/my_fragment"
        android:layout_width="match_parent"
        android:layout_height="match_parent" />
</FrameLayout>
```

##### Java 方式（动态添加）

```java
// 在 Activity 中
FragmentManager fragmentManager = getSupportFragmentManager();
FragmentTransaction transaction = fragmentManager.beginTransaction();

// 添加 Fragment
transaction.add(R.id.fragment_container, new MyFragment());

// 或者替换现有 Fragment
// transaction.replace(R.id.fragment_container, new MyFragment());

// 添加到返回栈，以便按返回键能回到前一个 Fragment
transaction.addToBackStack(null);

transaction.commit();
```

### Fragment 通信

#### 1. Fragment 与 Activity 通信

```java
// 在 Fragment 中
public class MyFragment extends Fragment {
    private OnFragmentInteractionListener mListener;

    // 定义接口
    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mListener = (OnFragmentInteractionListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    private void sendDataToActivity() {
        if (mListener != null) {
            mListener.onFragmentInteraction(Uri.parse("content://..."));
        }
    }
}

// 在 Activity 中实现接口
public class MainActivity extends AppCompatActivity 
    implements MyFragment.OnFragmentInteractionListener {
    
    @Override
    public void onFragmentInteraction(Uri uri) {
        // 处理来自 Fragment 的数据
    }
}
```

#### 2. Fragment 之间通信

推荐通过共享的 ViewModel 或通过宿主 Activity 作为中介：

```java
// 使用 ViewModel
public class SharedViewModel extends ViewModel {
    private final MutableLiveData<String> selected = new MutableLiveData<>();

    public void select(String item) {
        selected.setValue(item);
    }

    public LiveData<String> getSelected() {
        return selected;
    }
}

// 在 Fragment 中
SharedViewModel model = ViewModelProviders.of(getActivity()).get(SharedViewModel.class);
model.getSelected().observe(this, item -> {
    // 更新 UI
});
```

### Fragment 类型

1. **UI Fragment**：带有用户界面的 Fragment
2. **Headless Fragment**：没有用户界面，用于后台任务
3. **DialogFragment**：显示对话框的专用 Fragment
4. **PreferenceFragment**：用于显示设置界面

### 最佳实践

1. **避免在 Fragment 中直接引用其他 Fragment**
2. **使用 ViewModel 和 LiveData 进行通信**
3. **考虑使用 Navigation 组件管理 Fragment 导航**
4. **正确处理配置变更（如屏幕旋转）**
5. **合理使用 setRetainInstance(true) 保留 Fragment 实例**

### 常见问题

#### 1. Fragment 重叠问题

**解决方案**：
- 在 onCreate() 中检查 savedInstanceState 是否为 null
- 使用 commitNow() 替代 commit() 立即执行事务

#### 2. getActivity() 返回 null

**原因**：Fragment 未附加到 Activity 或已分离

**解决方案**：
- 在 onAttach() 中保存 Activity 引用
- 使用 isAdded() 检查 Fragment 是否已添加

#### 3. Fragment 事务异步执行

**解决方案**：
- 了解 commit() 是异步的，commitNow() 是同步的
- 使用 executePendingTransactions() 强制立即执行

Fragment 是 Android 开发中非常重要的组件，合理使用可以大大提高应用的模块化程度和用户体验。



## `Fragment` - 用法

>`Fragment` 的生命周期：https://baijiahao.baidu.com/s?id=1616346831006531612&wfr=spider&for=pc

### `xml`方式添加`Fragment`

>参考链接：https://developer.android.com/training/basics/fragments/creating#java
>
>详细用法请参考本站 [示例](https://gitee.com/dexterleslie/demonstration/tree/main/demo-android/demo-fragment-xml)

`res/layout/fragment1_layout.xml`：

```xml
<?xml version="1.0" encoding="utf-8"?>
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    android:orientation="vertical"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:gravity="center">
    <TextView
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:text="fragment1"/>
</LinearLayout>
```

`Fragment1.java`：

```java
/**
 *
 */
public class Fragment1 extends Fragment {
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment1_layout, null);
        return view;
    }
}

```

`res/layout/fragment2_layout.xml`：

```xml
<?xml version="1.0" encoding="utf-8"?>
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    android:orientation="vertical"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:gravity="center">
    <TextView
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:text="fragment2"/>
</LinearLayout>
```

`Fragment2.java`：

```java
/**
 *
 */
public class Fragment2 extends Fragment {
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment2_layout, null);
        return view;
    }
}

```

`res/layout/content_main.xml`：

```xml
<?xml version="1.0" encoding="utf-8"?>
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    xmlns:tools="http://schemas.android.com/tools"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    app:layout_behavior="@string/appbar_scrolling_view_behavior"
    tools:context="com.future.demo.MainActivity"
    tools:showIn="@layout/activity_main"
    android:orientation="horizontal">
    <fragment
        android:id="@+id/fragment1"
        android:name="com.future.demo.Fragment1"
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        android:layout_weight="1"/>
    <fragment
        android:id="@+id/fragment2"
        android:name="com.future.demo.Fragment2"
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        android:layout_weight="1"/>
</LinearLayout>

```



### 编程方式添加`Fragment`

>参考链接：https://guides.codepath.com/android/creating-and-using-fragments
>
>详细用法请参考本站 [示例](https://gitee.com/dexterleslie/demonstration/tree/main/demo-android/demo-fragment-programatically)

`res/layout/fragment1_layout.xml`：

```xml
<?xml version="1.0" encoding="utf-8"?>
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    android:orientation="vertical"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:gravity="center">
    <TextView
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:text="fragment1"/>
</LinearLayout>
```

`Fragment1.java`：

```java
/**
 *
 */
public class Fragment1 extends Fragment {
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment1_layout, null);
        return view;
    }
}
```

`res/layout/content_main.xml`：

```xml
<?xml version="1.0" encoding="utf-8"?>
<android.support.constraint.ConstraintLayout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    xmlns:tools="http://schemas.android.com/tools"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    app:layout_behavior="@string/appbar_scrolling_view_behavior"
    tools:context="com.future.demo.MainActivity"
    tools:showIn="@layout/activity_main">

    <!-- 用于存放 Fragment 的容器 -->
    <FrameLayout
        android:id="@+id/framelayout"
        android:layout_width="match_parent"
        android:layout_height="match_parent"></FrameLayout>

</android.support.constraint.ConstraintLayout>
```

`MainActivity.java`：

```java
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ...

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        Fragment fragment1 = new Fragment1();
        // transaction.add(R.id.framelayout, fragment1, Fragment1.class.getSimpleName());
        // 使用 replace 函数替代容器中的 Fragment
        transaction.replace(R.id.framelayout, fragment1, Fragment1.class.getSimpleName());
        transaction.commit();
    }
	
    ...
}
```



### 显示或隐藏

>参考链接：https://blog.csdn.net/l_201607/article/details/82288978
>
>详细用法请参考本站 [示例](https://gitee.com/dexterleslie/demonstration/tree/main/demo-android/demo-fragment-showorhide)

`Fragment1.java`：

```java
/**
 *
 */
public class Fragment1 extends Fragment {
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment1_layout, null);
        return view;
    }
}
```

`res/layout/fragment1_layout.xml`：

```xml
<?xml version="1.0" encoding="utf-8"?>
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    android:orientation="vertical"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:gravity="center">
    <TextView
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:text="fragment1"/>
</LinearLayout>
```

`Fragment2.java`：

```java
/**
 *
 */
public class Fragment2 extends Fragment {
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment2_layout, null);
        return view;
    }
}
```

`res/layout/fragment2_layout.xml`：

```xml
<?xml version="1.0" encoding="utf-8"?>
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    android:orientation="vertical"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:gravity="center">
    <TextView
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:text="fragment2"/>
</LinearLayout>
```

`res/layout/content_main.xml`：

```xml
<?xml version="1.0" encoding="utf-8"?>
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    xmlns:tools="http://schemas.android.com/tools"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    app:layout_behavior="@string/appbar_scrolling_view_behavior"
    tools:context="com.future.demo.MainActivity"
    tools:showIn="@layout/activity_main"
    android:orientation="vertical">
    <FrameLayout
        android:id="@+id/frameLayout"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:layout_weight="1"></FrameLayout>
    <LinearLayout
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:gravity="center">
        <Button
            android:id="@+id/buttonShowFragment1"
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:text="显示fragment1"/>
        <Button
            android:id="@+id/buttonShowFragment2"
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:text="显示fragment2"/>
    </LinearLayout>
</LinearLayout>
```

`MainActivity.java`：

```java
public class MainActivity extends AppCompatActivity {
    private Fragment1 fragment1 = new Fragment1();
    private Fragment2 fragment2 = new Fragment2();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ...

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.add(R.id.frameLayout, fragment1, Fragment1.class.getSimpleName());
        transaction.add(R.id.frameLayout, fragment2, Fragment1.class.getSimpleName());
        transaction.commit();

        Button button = findViewById(R.id.buttonShowFragment1);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toggleFragmentVisibility(fragment1);
            }
        });
        button = findViewById(R.id.buttonShowFragment2);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toggleFragmentVisibility(fragment2);
            }
        });
    }

    private void toggleFragmentVisibility(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();

        transaction.hide(fragment1);
        transaction.hide(fragment2);

        transaction.show(fragment);

        transaction.commit();
    }
    
    ...
}
```



## `ViewPager`

`android.support.v4.view.ViewPager` 是 Android 支持库（Support Library）中的一个组件，用于实现左右滑动的页面切换效果。以下是详细说明：

### 核心概念
1. **作用**：
   - 提供类似现代应用常见的页面滑动布局（如应用引导页、图片浏览器、Tab切换等）。
   - 允许用户通过左右滑动手势在不同页面间切换。

2. **特点**：
   - 需要配合 **PagerAdapter** 使用（如 `FragmentPagerAdapter` 或 `FragmentStatePagerAdapter`）。
   - 默认不显示页面切换动画，但可通过 `PageTransformer` 自定义动画效果。
   - 支持动态添加/移除页面（需调用 `notifyDataSetChanged()`）。

### 基本用法示例
```java
// 在Activity中使用
ViewPager viewPager = findViewById(R.id.view_pager);
FragmentPagerAdapter adapter = new MyPagerAdapter(getSupportFragmentManager());
viewPager.setAdapter(adapter);

// 简单Adapter示例
class MyPagerAdapter extends FragmentPagerAdapter {
    public MyPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        return MyFragment.newInstance(position);
    }

    @Override
    public int getCount() {
        return 3; // 页面数量
    }
}
```

### 注意事项
1. **迁移到AndroidX**：
   - 新项目应使用AndroidX中的替代类：`androidx.viewpager.widget.ViewPager`
   - 旧项目迁移需替换依赖：
     ```gradle
     implementation 'androidx.viewpager:viewpager:1.0.0'
     ```

2. **与ViewPager2的区别**：
   - **ViewPager2**（推荐）基于RecyclerView重构，支持：
     - 垂直滑动
     - RTL布局
     - 更高效的页面更新
     - 内置差异动画（DiffUtil）

3. **常见问题**：
   - 页面预加载：默认会预加载相邻页面，可通过 `setOffscreenPageLimit()` 调整
   - Fragment状态保存：使用 `FragmentStatePagerAdapter` 可优化内存占用

### 进阶功能
```java
// 添加页面切换动画
viewPager.setPageTransformer(true, new ZoomOutPageTransformer());

// 监听页面变化
viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {}
    
    @Override
    public void onPageSelected(int position) {
        // 页面选中时触发
    }
});
```

建议新项目优先考虑使用 **ViewPager2**，但理解 `ViewPager` 仍有助维护旧代码。

### 示例

>详细用法请参考本站 [示例](https://gitee.com/dexterleslie/demonstration/tree/main/demo-android/demo-viewpager)

定义三个 `Fragment`：

- `MyBaseFragment`：

  `MyBaseFragment.java`：

  ```java
  /**
   *
   */
  public abstract class MyBaseFragment extends Fragment {
      /**
       *
       * @return
       */
      protected abstract String getTitle();
  }
  
  ```

- `FragmentPaying`

  `FragmentPaying.java`

  ```java
  /**
   *
   */
  public class FragmentPaying extends MyBaseFragment {
      @Override
      protected String getTitle() {
          return "支付中账单";
      }
  
      @Nullable
      @Override
      public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
          View view = inflater.inflate(R.layout.layout_paying, null);
          return view;
      }
  }
  ```

  `res/layout/layout_paying.xml`

  ```xml
  <?xml version="1.0" encoding="utf-8"?>
  <LinearLayout
      xmlns:android="http://schemas.android.com/apk/res/android"
      android:layout_width="match_parent"
      android:layout_height="match_parent"
      android:orientation="vertical"
      android:gravity="center">
      <TextView
          android:layout_width="wrap_content"
          android:layout_height="wrap_content"
          android:text="支付中账单"/>
  </LinearLayout>
  ```

- `FragmentPaymentRecord`

  `FragmentPaymentRecord.java`

  ```java
  /**
   *
   */
  public class FragmentPaymentRecord extends MyBaseFragment {
      @Override
      protected String getTitle() {
          return "历史账单";
      }
  
      @Nullable
      @Override
      public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
          View view = inflater.inflate(R.layout.layout_payment_record, null);
          return view;
      }
  }
  ```

  `res/layout/layout_payment_record.xml`

  ```xml
  <?xml version="1.0" encoding="utf-8"?>
  <LinearLayout
      xmlns:android="http://schemas.android.com/apk/res/android"
      android:layout_width="match_parent"
      android:layout_height="match_parent"
      android:orientation="vertical"
      android:gravity="center">
      <TextView
          android:layout_width="wrap_content"
          android:layout_height="wrap_content"
          android:text="历史账单"/>
  </LinearLayout>
  ```

- `FragmentUnpay`

  `FragmentUnpay.java`

  ```java
  /**
   *
   */
  public class FragmentUnpay extends MyBaseFragment {
      @Override
      protected String getTitle() {
          return "未支付账单";
      }
  
      @Nullable
      @Override
      public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
          View view = inflater.inflate(R.layout.layout_unpay, null);
          return view;
      }
  }
  ```

  `res/layout/layout_unpay.xml`

  ```xml
  <?xml version="1.0" encoding="utf-8"?>
  <LinearLayout
      xmlns:android="http://schemas.android.com/apk/res/android"
      android:layout_width="match_parent"
      android:layout_height="match_parent"
      android:orientation="vertical"
      android:gravity="center">
      <TextView
          android:layout_width="wrap_content"
          android:layout_height="wrap_content"
          android:text="未支付账单"/>
  </LinearLayout>
  ```

定义 `MyFragmentPageAdapter`

```java
/**
 *
 */
public class MyFragmentPagerAdapter extends FragmentPagerAdapter {
    private List<MyBaseFragment> fragments = null;

    /**
     *
     * @param fragmentManager
     */
    public MyFragmentPagerAdapter(FragmentManager fragmentManager) {
        super(fragmentManager);
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return fragments.get(position).getTitle();
    }

    @Override
    public Fragment getItem(int position) {
        return fragments.get(position);
    }

    @Override
    public int getCount() {
        return fragments.size();
    }

    /**
     *
     * @param fragments
     */
    public void setFragments(List<MyBaseFragment> fragments) {
        this.fragments = fragments;
    }
}
```

`content_main.xml`

```xml
<?xml version="1.0" encoding="utf-8"?>
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    xmlns:tools="http://schemas.android.com/tools"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    app:layout_behavior="@string/appbar_scrolling_view_behavior"
    tools:context="com.future.demo.MainActivity"
    tools:showIn="@layout/activity_main"
    android:orientation="vertical">
    <android.support.v4.view.ViewPager
        android:id="@+id/viewPager"
        android:layout_width="match_parent"
        android:layout_height="0dp"
        android:layout_weight="1">
    </android.support.v4.view.ViewPager>
</LinearLayout>
```

`MainActivity`

```java
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ...

        // 初始化 ViewPager
        final List<MyBaseFragment> fragments = new ArrayList<>();
        MyBaseFragment fragment = new FragmentUnpay();
        fragments.add(fragment);
        fragment = new FragmentPaying();
        fragments.add(fragment);
        fragment = new FragmentPaymentRecord();
        fragments.add(fragment);

        MyFragmentPagerAdapter fragmentPagerAdapter = new MyFragmentPagerAdapter(getSupportFragmentManager());
        fragmentPagerAdapter.setFragments(fragments);

        ViewPager viewPager = findViewById(R.id.viewPager);
        viewPager.setAdapter(fragmentPagerAdapter);
    }

    ...
}
```



## `UI`组件 - `GridView`

GridView 是 Android 中一个常用的布局组件，它以网格形式展示数据项，适合显示图片、图标或其他需要网格排列的内容。

### 基本特性

- **网格布局**：以行和列的形式排列子视图
- **可滚动**：当内容超出屏幕时支持滚动
- **适配器驱动**：使用 Adapter 提供数据
- **自定义性强**：可以自定义行列数、间距、单元格样式等

### 基本用法

#### 1. XML 布局中声明 GridView

```xml
<GridView
    android:id="@+id/gridView"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:numColumns="3"  <!-- 设置列数 -->
    android:verticalSpacing="10dp"
    android:horizontalSpacing="10dp"
    android:stretchMode="columnWidth" />
```

#### 2. 在 Activity/Fragment 中设置适配器

```java
GridView gridView = findViewById(R.id.gridView);
ArrayAdapter<String> adapter = new ArrayAdapter<>(this, 
        android.R.layout.simple_list_item_1, 
        dataArray);
gridView.setAdapter(adapter);
```

### 常用属性

| 属性                        | 说明                                        |
| --------------------------- | ------------------------------------------- |
| `android:numColumns`        | 设置列数，可设置为"auto_fit"自动适应        |
| `android:verticalSpacing`   | 行间距                                      |
| `android:horizontalSpacing` | 列间距                                      |
| `android:stretchMode`       | 拉伸模式，可选columnWidth/spacingWidth/none |
| `android:columnWidth`       | 指定列宽                                    |
| `android:gravity`           | 内容对齐方式                                |

### 自定义 GridView

通常需要自定义 Adapter 和 Item 布局：

1. 创建自定义 Item 布局 (grid_item.xml)
```xml
<ImageView
    android:id="@+id/grid_item_image"
    android:layout_width="100dp"
    android:layout_height="100dp" />
```

2. 创建自定义 Adapter
```java
public class ImageAdapter extends BaseAdapter {
    private Context context;
    private List<Integer> imageIds;
    
    public ImageAdapter(Context c, List<Integer> ids) {
        context = c;
        imageIds = ids;
    }
    
    public int getCount() {
        return imageIds.size();
    }
    
    public Object getItem(int position) {
        return null;
    }
    
    public long getItemId(int position) {
        return 0;
    }
    
    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView imageView;
        if (convertView == null) {
            imageView = new ImageView(context);
            imageView.setLayoutParams(new GridView.LayoutParams(200, 200));
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        } else {
            imageView = (ImageView) convertView;
        }
        
        imageView.setImageResource(imageIds.get(position));
        return imageView;
    }
}
```

3. 设置自定义 Adapter
```java
GridView gridView = findViewById(R.id.gridView);
gridView.setAdapter(new ImageAdapter(this, imageIdList));
```

### 事件处理

```java
gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
    public void onItemClick(AdapterView<?> parent, View v, 
            int position, long id) {
        Toast.makeText(MainActivity.this, "点击了第" + position + "项", 
                Toast.LENGTH_SHORT).show();
    }
});
```

### 性能优化技巧

1. 使用 ViewHolder 模式减少 findViewById 调用
2. 合理设置图片大小，避免内存溢出
3. 对于大量数据考虑分页加载
4. 使用 convertView 复用已有视图

GridView 适合展示大量需要网格排列的数据，但在复杂场景下可以考虑使用 RecyclerView 替代，后者提供更灵活的布局管理和更好的性能。

### 示例

>详细用法请参考本站 [示例](https://gitee.com/dexterleslie/demonstration/tree/main/demo-android/demo-gridview)



## `UI`组件 - `ListView`

ListView 是 Android 中用于显示可滚动列表项的重要 UI 组件。以下是关于 ListView 的详细介绍：

### 基本概念

ListView 是一个视图组，用于显示可滚动的垂直列表，其中的列表项来自与 ListView 关联的适配器(Adapter)。

### 主要特点

1. **可滚动列表**：自动处理垂直滚动
2. **高效复用**：只创建和显示当前可见的列表项，节省内存
3. **适配器模式**：通过 Adapter 提供数据和创建视图
4. **交互支持**：内置点击、长按等事件处理

### 基本用法

#### XML 布局中声明 ListView

```xml
<ListView
    android:id="@+id/listView"
    android:layout_width="match_parent"
    android:layout_height="match_parent" />
```

#### Java/Kotlin 代码中使用

```java
ListView listView = findViewById(R.id.listView);

// 创建适配器
ArrayAdapter<String> adapter = new ArrayAdapter<>(
    this, 
    android.R.layout.simple_list_item_1, 
    dataArray
);

// 设置适配器
listView.setAdapter(adapter);

// 设置点击事件
listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        // 处理点击事件
    }
});
```

### 适配器类型

1. **ArrayAdapter**：适用于简单的文本列表
2. **SimpleAdapter**：适用于包含多个字段的列表项
3. **BaseAdapter**：自定义适配器的基础类，灵活性最高
4. **CursorAdapter**：用于数据库查询结果的显示

### 自定义 ListView

```java
public class CustomAdapter extends BaseAdapter {
    private Context context;
    private List<Item> items;
    
    public CustomAdapter(Context context, List<Item> items) {
        this.context = context;
        this.items = items;
    }
    
    @Override
    public int getCount() {
        return items.size();
    }
    
    @Override
    public Object getItem(int position) {
        return items.get(position);
    }
    
    @Override
    public long getItemId(int position) {
        return position;
    }
    
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // 视图复用
        if (convertView == null) {
            convertView = LayoutInflater.from(context)
                .inflate(R.layout.list_item_layout, parent, false);
        }
        
        // 绑定数据
        Item item = items.get(position);
        TextView title = convertView.findViewById(R.id.title);
        title.setText(item.getTitle());
        
        return convertView;
    }
}
```

### 优化技巧

1. **视图复用**：利用 convertView 避免重复 inflate
2. **ViewHolder 模式**：减少 findViewById 调用次数
3. **分页加载**：大数据集时实现分批加载
4. **异步加载**：耗时操作放在后台线程

### 替代方案

虽然 ListView 仍然可用，但在现代 Android 开发中，RecyclerView 是更推荐的列表组件，因为它提供了更大的灵活性和更好的性能。

### 常见问题

1. **空列表处理**：使用 setEmptyView() 方法
2. **性能问题**：确保正确实现视图复用
3. **复杂布局**：考虑使用 RecyclerView 替代

ListView 是 Android 开发中的基础组件，理解其工作原理对于构建高效的应用界面非常重要。

### 示例

>详细用法请参考本站 [示例](https://gitee.com/dexterleslie/demonstration/tree/main/demo-android/demo-listview)



## `UI`组件 - `RecyclerView`

RecyclerView 是 Android 5.0 (Lollipop) 引入的一个更先进、更灵活的列表/网格视图组件，用于替代传统的 ListView 和 GridView。它提供了更高的性能和更多的自定义选项。

### 核心特点

1. **视图回收机制**：自动回收不可见的视图以供重用
2. **布局管理器**：支持线性、网格、瀑布流等多种布局
3. **动画支持**：内置项目动画效果
4. **高效更新**：支持局部更新而非全局刷新
5. **强制使用 ViewHolder**：提高滚动性能

### 基本使用步骤

#### 1. 添加依赖 (build.gradle)

```gradle
implementation 'androidx.recyclerview:recyclerview:1.2.1'
```

#### 2. XML 布局中添加 RecyclerView

```xml
<androidx.recyclerview.widget.RecyclerView
    android:id="@+id/recyclerView"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:padding="8dp" />
```

#### 3. 创建 Item 布局 (item_example.xml)

```xml
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    android:orientation="vertical"
    android:padding="16dp">

    <TextView
        android:id="@+id/textViewTitle"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:textSize="18sp" />

    <TextView
        android:id="@+id/textViewDescription"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:textSize="14sp" />
</LinearLayout>
```

#### 4. 创建数据模型类

```java
public class ExampleItem {
    private String title;
    private String description;

    public ExampleItem(String title, String description) {
        this.title = title;
        this.description = description;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }
}
```

#### 5. 创建 Adapter 和 ViewHolder

```java
public class ExampleAdapter extends RecyclerView.Adapter<ExampleAdapter.ExampleViewHolder> {
    private List<ExampleItem> exampleList;

    public static class ExampleViewHolder extends RecyclerView.ViewHolder {
        public TextView textViewTitle;
        public TextView textViewDescription;

        public ExampleViewHolder(View itemView) {
            super(itemView);
            textViewTitle = itemView.findViewById(R.id.textViewTitle);
            textViewDescription = itemView.findViewById(R.id.textViewDescription);
        }
    }

    public ExampleAdapter(List<ExampleItem> exampleList) {
        this.exampleList = exampleList;
    }

    @Override
    public ExampleViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_example, parent, false);
        return new ExampleViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ExampleViewHolder holder, int position) {
        ExampleItem currentItem = exampleList.get(position);
        holder.textViewTitle.setText(currentItem.getTitle());
        holder.textViewDescription.setText(currentItem.getDescription());
    }

    @Override
    public int getItemCount() {
        return exampleList.size();
    }
}
```

#### 6. 在 Activity/Fragment 中设置 RecyclerView

```java
public class MainActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private ExampleAdapter adapter;
    private List<ExampleItem> exampleList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        createExampleList();
        setupRecyclerView();
    }

    private void createExampleList() {
        exampleList = new ArrayList<>();
        exampleList.add(new ExampleItem("标题1", "描述1"));
        exampleList.add(new ExampleItem("标题2", "描述2"));
        exampleList.add(new ExampleItem("标题3", "描述3"));
        // 添加更多数据...
    }

    private void setupRecyclerView() {
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true); // 优化性能，当item大小固定时使用
        
        // 设置布局管理器
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        
        // 创建并设置适配器
        adapter = new ExampleAdapter(exampleList);
        recyclerView.setAdapter(adapter);
        
        // 添加分割线 (需要自定义ItemDecoration或使用第三方库)
        recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
    }
}
```

### 布局管理器 (LayoutManager)

RecyclerView 通过不同的布局管理器实现不同布局：

#### 1. 线性布局 (垂直/水平)

```java
// 垂直列表（默认）
recyclerView.setLayoutManager(new LinearLayoutManager(this));

// 水平列表
recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
```

#### 2. 网格布局

```java
// 2列的网格布局
recyclerView.setLayoutManager(new GridLayoutManager(this, 2));

// 水平滚动的网格布局
recyclerView.setLayoutManager(new GridLayoutManager(this, 2, GridLayoutManager.HORIZONTAL, false));
```

#### 3. 瀑布流布局

```java
// 2列的瀑布流布局
recyclerView.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
```

### 点击事件处理

在 Adapter 中添加点击事件：

```java
public class ExampleAdapter extends RecyclerView.Adapter<ExampleAdapter.ExampleViewHolder> {
    // ... 之前的代码 ...
    
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    @Override
    public ExampleViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_example, parent, false);
        return new ExampleViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ExampleViewHolder holder, int position) {
        ExampleItem currentItem = exampleList.get(position);
        holder.textViewTitle.setText(currentItem.getTitle());
        holder.textViewDescription.setText(currentItem.getDescription());
        
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null && position != RecyclerView.NO_POSITION) {
                    listener.onItemClick(position);
                }
            }
        });
    }
    
    // ... 其他代码 ...
}
```

在 Activity 中使用：

```java
adapter.setOnItemClickListener(new ExampleAdapter.OnItemClickListener() {
    @Override
    public void onItemClick(int position) {
        ExampleItem clickedItem = exampleList.get(position);
        Toast.makeText(MainActivity.this, 
                "点击了: " + clickedItem.getTitle(), 
                Toast.LENGTH_SHORT).show();
    }
});
```

### 数据更新

RecyclerView 提供了更高效的数据更新方式：

```java
// 添加新项
exampleList.add(new ExampleItem("新标题", "新描述"));
adapter.notifyItemInserted(exampleList.size() - 1);

// 删除项
int position = 2; // 要删除的位置
exampleList.remove(position);
adapter.notifyItemRemoved(position);

// 更新项
int position = 1; // 要更新的位置
exampleList.set(position, new ExampleItem("更新标题", "更新描述"));
adapter.notifyItemChanged(position);

// 批量更新
exampleList.clear();
exampleList.addAll(newList); // 新数据集
adapter.notifyDataSetChanged(); // 尽量避免使用，性能较差
```

### 与 ListView/GridView 的主要区别

1. **强制使用 ViewHolder 模式**：提高滚动性能
2. **灵活的布局管理**：通过 LayoutManager 实现不同布局
3. **动画支持**：内置添加/删除/移动动画
4. **更高效的更新机制**：支持局部更新
5. **更复杂的实现**：需要更多代码但提供更大灵活性

RecyclerView 是现代 Android 开发中显示列表数据的首选组件，虽然实现比 ListView/GridView 稍复杂，但提供了更好的性能和更多的自定义选项。
