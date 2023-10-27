## 演示idea用法

### 远程开发

> 下载idea2021之后的版本才有这个特性。

```shell
# 当前测试的idea版本为2022.3.3

# centOS8设置: 上传代码目录路径为 /root/workspace-git/ecommerce-all
# centOS8设置: 安装git命令，yum install git -y
# centOS8设置: 使用dcli程序安装docker、maven

# idea设置: 在idea中根据提示新建remote development信息，NOTE: 选择project directory为/root/workspace-git/ecommerce-all/ecommerce-parent，点击确认后需要等待idea下载相关组件并自动配置remote development环境
# idea设置: 使用Project Structure设置工程jdk目录
# idea设置: 使用Settings设置maven home centOS8的maven路径
# idea设置: 显示Git Commit tab，打开Settings>Version Control>Commit>Use non-modal commit interface
```

