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





