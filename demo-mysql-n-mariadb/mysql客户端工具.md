# `mysql`客户端工具

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



## `intellij mysql`客户端

`intellij mysql`客户端支持`DELIMITER $$`语法。



## Navicat for MySQL

Upper case keywords 功能会把所有的 SQL 都 Upper case



## HeidiSQL

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



## DBeaver

- 无法连接 MariaDB，因为 MariaDB jdbc 驱动在线下载失败



## SQLyog

使用此工具作为首选的 MySQL GUI 客户端工具，因为其支持精准的 keyword upper case 功能。



### ubuntu 安装

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
