# `cocoapods`



## 安装

>`https://www.cnblogs.com/chuancheng/p/8443677.html`

实验环境：`macOS 13.0.1`、`ruby 2.6.10p210`

更换`rudy`源

```bash
gem sources --remove https://rubygems.org/ && gem sources --add https://gems.ruby-china.com
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



## 导入第三方库`AFNetworking`

进入项目根目录

```bash
cd /Users/john/Desktop/MyApp
```

编辑`Podfile`内容如下：

```nginx
project 'MyApp'
platform:ios, '7.0'
target 'pod' do
pod 'AFNetworking', '~>3.1.0'
end
```

- `platform :ios, '7.0'`代表当前`AFNetworking`支持的`iOS`最低版本是`iOS 7.0`
-  `project 'MyApp'`就是你自己的工程名字，`pod install --verbose`命令会自动在当前目录搜索`MyApp.xcodeproj`文件进行`cocoapods`集成生成`pod.xcworkspace`文件，如果没有`MyApp.xcodeproj`文件则不会生成`pod.xcworkspace`文件
- `pod 'AFNetworking', '~> 3.1.0'`代表要下载的`AFNetworking`版本是`3.1.0`及以上版本，还可以去掉后面的`'~> 3.1.0'`，直接写`pod 'AFNetworking'`，这样代表下载的`AFNetworking`是最新版

下载、编译、生成`pod.xcworkspace`（以后打开`Xcode`项目使用此文件，其包含`AFNetworking`依赖）

```bash
pod install --verbose
```



## `pod install`加速

>`https://www.jianshu.com/p/3086df14ed08`

每次执行`pod install`和`pod update`的时候，`cocoapods`都会默认更新一次`spec`仓库。这是一个比较耗时的操作。在确认`spec`版本库不需要更新时，给这两个命令加一个参数跳过`spec`版本库更新,可以明显提高这两个命令的执行速度。

```bash
pod install --verbose --no-repo-update
pod update --verbose --no-repo-update
```



## `Podfile`指定版本号

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



## 查询本地所有`pods`库

> `https://blog.csdn.net/skylin19840101/article/details/71404110`

```bash
pod repo list
```



## 删除本地`pods`库

> `https://blog.csdn.net/skylin19840101/article/details/71404110`

```bash
pod repo remove aliyun
```

