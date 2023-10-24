## markdown插件安装

> 安装markdown editor(A full-featured WYSIWYG editor for markdown.)



## 配置git

> - 使用vscode自带的git管理工具
>
> - 安装git graph插件用于查看提交日志
>
> - 设置vscode自带git管理工具tree模式查看变动的文件
>   https://stackoverflow.com/questions/51476096/visual-studio-code-group-pending-changes-by-folder
>   Source Control -- pending changes > Source Control > View & Sort > View as Tree
>
> - 设置git自动保存密码，避免每次提交时都要输入密码
>
>   https://stackoverflow.com/questions/34400272/visual-studio-code-is-always-asking-for-git-credentials
>
>   设置git自动保存密码 git config --global credential.helper store
>   查看git自动保存密码是否设置成功 git config --global credential.helper



## vscode设置

> 1、设置zoom为1，Code > Preferences > Settings > Zoom



## vscode远程开发

### 使用remote-ssh插件远程开发

> NOTE: 因为ssh命令不支持连接时提供密码，所以remote-ssh插件不支持保存SSH密码
>
> 远程开发的terminal也是远程的，远程打开git仓库就可以通过vscode git管理仓库了。

```shell
# 通过插件面板安装remote-ssh插件

# 在remote-ssh导航标签中添加主机
# 在弹出提示中输入主机信息: ssh root@192.168.1.200

# 在remote-ssh功能中选择刚刚添加的主机并选择在新窗口打开，输入密码后就会打开一个新的vscode编辑窗口
# 新窗口中提示输入SSH密码并输入密码后，选择克隆或者打开远程主机的目录就可以进行远程开发了。
```

