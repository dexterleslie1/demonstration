# `android studio`



## 安装



### `windows7`安装

注意：用虚拟机安装`android studio`先登陆`vCenter`启用虚拟机虚拟化

下载`android-studio-ide-171.4408382-windows.exe`双击安装

选中`Android SDK`和`Intel HAXM`安装

打开`android-studio`创建`BasicActivity`类型`helloworld`工程

如果打开`helloworld`工程一直卡在`”Building ‘helloworld’ Gradle project info”`，那么点击`cancel`并且关闭`android studio`取消`gradle`下载
打开目录`C:\Users\john\.gradle\wrapper\dists\gradle-4.1-all\bzyivzo6n839fup2jbap0tjew`复制`gradle-4.1-all.zip`到此目录下，再次打开`android studio`，这次会很快打开`helloworld`项目



### `macOS`安装

安装方式和`windows`方式没有区别，全部安装和配置都使用`android-studio`配置，无需打开命令行配置。



### `ubuntu`安装

在`https://developer.android.com/studio/archive`下载`android-studio-2021.2.1.5-linux.tar.gz`

切换到`root`用户

```bash
sudo -i
```

解压`android-studio-2021.2.1.5-linux.tar.gz`到`/usr/local`目录

```bash
cd /usr/local
tar -xvzf android-studio-2021.2.1.5-linux.tar.gz
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

在打开项目过程中，如果下载`gradle.zip`过慢，可以参考 <a href="/android/gradle.html#android-studio下载gradle慢的解决办法" target="_blank">链接</a> 解决此问题。



## 问题列表



### `android studio`引用远程仓库`jCenter`慢（`bintray`）

>参考`https://blog.csdn.net/ygc87/article/details/82857611`

仓库替换为`maven{url 'http://maven.aliyun.com/nexus/content/groups/public/'}`

