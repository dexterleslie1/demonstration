# `android`



## `android ndk`



### 什么是`sdk`？什么是`ndk`？

>https://developer.android.com/ndk/index.html
>
>https://stackoverflow.com/questions/43751485/what-is-the-difference-between-ndk-and-sdk

`Android NDK`是一个工具集，可让您使用`C`和`C++`等语言以原生代码实现应用的各个部分。对于某些类型的应用，这可以帮助您重复使用以这些语言编写的代码库。

`SDK`是`Android`应用程序的主要开发工具包 - 它包含用于`Java`和资源（`png`、`xml`）编译、打包为`apk`文件、在设备、模拟器、文档等上安装、运行和调试它们的工具。



### `ubuntu`配置`ndk`编译环境

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

MuMu模拟器：网易开发的模拟器，支持Windows和macOS，优化了游戏性能。通过 [链接](https://mumu.163.com/download/) 下载最新版模拟器，根据提示安装即可。

