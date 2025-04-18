# `windows`操作系统相关

## 禁用自动更新功能

### `window10`禁用自动更新功能

1. 打开控制面板：
   - 点击任务栏上的“开始”按钮，然后选择“控制面板”图标。
   - 或者，你可以通过搜索栏输入“控制面板”来快速找到并打开它。
2. 进入管理工具：
   - 在控制面板中，找到并点击“管理工具”选项。
3. 打开服务管理器：
   - 在管理工具窗口中，选择“服务”选项。这将打开Windows的服务管理器。
4. 找到Windows Update服务：
   - 在服务管理器中，向下滚动找到名为“Windows Update”的服务。
5. 禁用Windows Update服务：
   - 双击“Windows Update”服务以打开其属性窗口。
   - 在属性窗口中，切换到“常规”选项卡。
   - 找到“启动类型”下拉菜单，将其设置为“禁用”。
   - （可选）点击“停止”按钮以立即停止当前正在运行的Windows Update服务。
   - 点击“应用”和“确定”按钮保存更改。



### `Windows 11`  禁用自动更新功能

停止更新服务：

- 按 Win + R，输入 services.msc，回车。
- 找到 Windows Update 服务，右键选择“停止”。

禁用服务启动：

- 双击 Windows Update，在“启动类型”中选择“禁用”。

恢复选项：

- 在“恢复”选项卡中，将所有失败操作设置为“无操作”。

应用更改：

- 点击“确定”并重启计算机。



## 启用`hyper-v`特性

windows11 启用`hyper-v`特性，`https://www.bdrsuite.com/blog/how-to-enable-hyper-v-in-windows-11-and-windows-server-2022/`



## Cygwin

>提醒：支持和 Ubuntu 一样正常使用 find、grep 等命令。



### 安装

Windows 11 安装 Cygwin

通过 [链接](https://www.cygwin.com/) 下载最新版本 Cygwin，例如：setup-x86_64.exe，运行安装程序并选择通过互联网在线安装，选择阿里云镜像网站和所有组件安装 Cygwin。



### 使用

#### 切换到 c: 盘

```bash
cd /cygdrive/c
pwd
```



#### 在当前目录打开文件管理器

```bash
cygstart .
```
