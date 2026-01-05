## 总结

如下：

- Windows11首选使用Navicat Premium作为MySQL客户端工具。

## `mysql workbench`

### `ubuntu`安装`mysql workbench`工具

>[Unable to locate package mysql-workbench-community](https://askubuntu.com/questions/1127179/e-unable-to-locate-package-mysql-workbench-community)

`mysql workbench`支持`DELIMITER $$`语法

注意：

- 使用`sudo apt install mysql-workbench`在`ubuntu20.4`中报告包不存在错误
- 使用`sudo snap install mysql-workbench-community`在`unbuntu20.4`中成功安装`mysql-workbench`，但是不能运行`mysql-workbench`，猜测是因为最新版本的`mysql-workbench`只支持`ubuntu22.04`

```bash
# 下载ubuntu20.4对应的deb
https://repo.mysql.com/apt/ubuntu/pool/mysql-tools/m/mysql-workbench-community/

# 安装mysql workbench依赖
sudo apt install ./mysql-workbench-community_8.0.29-1ubuntu20.04_amd64.deb
sudo apt --fix-broken install

# 安装mysql workbench
sudo apt install ./mysql-workbench-community_8.0.29-1ubuntu20.04_amd64.deb

# 使用ubuntu application面板启动workbench
```



### 修改`mysql workbench`的超时设置

1. **打开MySQL Workbench**：首先，启动MySQL Workbench应用程序。
2. **进入偏好设置**：在MySQL Workbench的菜单栏中，选择“Edit”（编辑）菜单，然后点击“Preferences”（偏好设置）选项。这将打开一个新的窗口，其中包含多个配置选项。
3. **定位到SQL编辑器设置**：在偏好设置窗口中，找到“SQL Editor”（SQL编辑器）部分。这通常位于左侧的导航栏中。
4. **调整超时时间**：在SQL编辑器设置下，寻找与超时相关的设置项。对于大多数版本的MySQL Workbench，你需要找到“DBMS connection read time out”（DBMS连接读取超时）或类似的选项。这个选项的单位通常是秒。
5. **修改超时值**：将“DBMS connection read time out”的值从默认的30秒修改为你希望的新值，比如60秒或更高。这个值应该根据你的查询需求和MySQL服务器的性能来设置。
6. **保存并重启**：修改完成后，点击“Apply”（应用）或“OK”（确定）按钮保存更改。然后，你可能需要重启MySQL Workbench以使新的超时设置生效。



## `IntelliJ MySQL`客户端

`intellij mysql`客户端支持`DELIMITER $$`语法。



## Navicat Premium

>下载地址：https://www.navicat.com.cn/download/navicat-premium

说明：

- Upper case keywords 功能会把所有的 SQL 都 Upper case

破解：

>说明：目前使用Navicat Premium17，等试用过期后再尝试破解。
>
>Navicat Premium17破解参考链接：https://www.shujuyr.com/9735.html

1. 到腾讯微云中下载NavicatPremium17Crack.zip。
2. 手动运行”无限试用Navicat.bat“。
3. 使用navicat170_premium_cs_x64.exe安装Navicat Premium。
4. 复制winmm.dll到C:\Program Files\PremiumSoft\Navicat Premium 17中。
5. 直接运行Navicat Premium即可。

## Navicat Premium数据传输

说明：使用Navicat Premium数据传输功能把UAT数据库复制到本地数据库中。

### 情况1

说明：如果数据传输过程中，没有表结构创建SQL脚本错误，则顺利使用此功能复制UAT数据库到本地数据库中。

### 情况2

说明：如果数据传输过程中，有表结构创建SQL脚本语法错误，则不能单纯使用此功能复制UAT数据库到本地数据库中。需要先创建表结构，再使用数据传输功能复制数据。

步骤如下：

1. 在本地数据库中创建表结构：
   - 选中数据库点击右键，在弹出菜单中选择`转储SQL文件`>`仅结构`功能导出创建表SQL脚本。
   - 手动修复表创建失败的SQL语句后在本地数据库中执行此SQL创建相关的表结构。
2. 复制UAT数据库中表数据到本地数据库中
   - 点击`工具`>`数据传输`功能，在弹出框中点击`选项`按钮，在`选项`窗口中取消`创建表`。
   - 根据提示开始复制UAT数据到本地数据库中。

## `HeidiSQL`

- 支持关键词自动 uppercase 功能



### 安装

#### Ubuntu

>[参考链接2](https://snapcraft.io/install/heidisql-wine/ubuntu)
>
>[参考链接1](https://github.com/Kianda/heidisql-snap)

```bash
sudo snap install heidisql-wine
```

通过应用中心输入 Heidi 启动应用。



## `DBeaver`

- 无法连接 MariaDB，因为 MariaDB jdbc 驱动在线下载失败



## `SQLyog`

使用此工具作为首选的 MySQL GUI 客户端工具，因为其支持精准的 keyword upper case 功能。

提示：

- 编写SQL时没有智能提示。



### `Ubuntu`安装

>`https://forum.winehq.org/viewtopic.php?t=33543`

安装 wine

```bash
sudo apt install wine
```

访问 `https://github.com/webyog/sqlyog-community/wiki/Downloads` 下载 SQLyog-13.3.0-0.x64Community.exe 安装程序

使用 wine 运行并安装 SQLyog-13.3.0-0.x64Community.exe，根据安装程序提示安装 SQLyog

```bash
LANG=zh_CN.UTF-8 wine SQLyog-13.3.0-0.x64Community.exe
```

- LANG=zh_CN.UTF-8 使 SQLyog 程序正确显示中文。

安装完毕后重启 ubuntu 操作系统

使用 wine 运行 SQLyog 程序

```bash
LANG=zh_CN.UTF-8 wine ~/.wine/drive_c/Program\ Files/SQLyog\ Community/SQLyogCommunity.exe
```



### `Window11`安装

访问 https://github.com/webyog/sqlyog-community/wiki/Downloads 下载最新版本`SQLyog`并根据提示安装。
