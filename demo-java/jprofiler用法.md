# `jprofiler`用法

## 安装

### `windows`安装`jprofiler`

1. 访问`https://www.ej-technologies.com/download/jprofiler/files`下载`windows`平台最新版本`jprofiler`
2. 使用超级管理员权限安装`jprofiler_windows-x64_14_0_3.exe`

### `ubuntu`安装`jprofiler`

1. 访问`https://www.ej-technologies.com/download/jprofiler/files`下载`linux`平台最新版本`JProfiler for Linux Setup Executable`

2. 为`jprofiler_linux-x64_14_0_3.sh`新增执行权限

   ```bash
   chmod +x jprofiler_linux-x64_14_0_3.sh
   ```

3. 执行`jprofiler`安装程序，根据提示安装`jprofiler`

   ```bash
   sudo ./jprofiler_linux-x64_14_0_3.sh
   ```

4. 在`Show Applications`面板搜索并打开`jprofiler`



## `jprofiler`术语

### `Shallow Size`和`Retained Size`

>[参考链接](https://www.jianshu.com/p/851b5bb0a4d4)

`Shallow Size`是指实例**自身**占用的内存, 可以理解为保存该'数据结构'需要多少内存, 注意**不包括**它引用的其他实例。

`Retained Size`是指, 当实例A被回收时, 可以**同时被回收**的实例的Shallow Size之和

### `outgoing references`和`incoming references`

>[参考链接](https://blog.csdn.net/qq_22222499/article/details/100069126)

outgoing references ，这个对象引用了哪些对象
incoming references ，哪些对象引用了这个对象



