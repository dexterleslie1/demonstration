# git命令相关知识

## 概念

> HEAD：是指向当前分支的最新提交的指针，可以在任意分支进行切换

## git reset

> https://blog.csdn.net/zts_zts/article/details/115220786

```
# NOTE: git reset 时候需要指定重置到的git log hash id

# git reset --mixed(默认)：回退版本后，之后提交的文件或代码在暂存区不跟踪了，转移到工作区，需要用add命令添加跟踪，然后再commit，用于未push场景
# NOTE: 没有写命令演示

# git reset --soft: 回退版本后，之后的代码或文件转移到暂存区继续跟踪，等待commit，用于未push场景
# NOTE: 没有写命令演示

# git reset --hard: 回退版本后，之后添加的代码或文件文件全部清除，包括你编辑器里新写的代码也删除，用于已push场景
# https://blog.csdn.net/weixin_44709394/article/details/120725395

git reset --hard [提交日志hash]

# 强制推送到远程，会把远程提交日志删除
git push -f
```



## git revert

> git revert [提交日志hash]：逆向指定提交的操作，起到撤销提交的效果



## git reset和git revert区别

> https://baijiahao.baidu.com/s?id=1714298482780121972&wfr=spider&for=pc



## git status

设置git status命令显示中文

```shell
git config --global core.quotepath false
```

无论哪个子目录都显示整个项目的状态

```shell
git status
```

显示当前所在目录的状态

```shell
git status .
```

显示当前所在目录的状态包括untracked文件，可参考 https://michaelheap.com/git-status-untracked/

```shell
git status -u .
```



## git lfs命令使用

### ubuntu安装git-lfs

```shell
sudo apt install git-lfs
```

### 使用

```
# pull指定文件，参考https://sabicalija.github.io/git-lfs-intro/
sudo git lfs pull --include="xxx.pdf"
```



## git clean

### 参考资料

How to remove files that are listed in the .gitignore but still on the repository?
https://stackoverflow.com/questions/13541615/how-to-remove-files-that-are-listed-in-the-gitignore-but-still-on-the-repositor#:~:text=As%20the%20files%20in%20.,clean%20%2Dxdf%20to%20execute%20it.

### 作用

用于删除被 .gitignore 但依旧在远程仓库存在的文件

### 相关操作

只显示将要删除的文件或者目录，不执行实际删除，-x 删除被忽略的文件，-d 删除被忽略的目录，-n 等价为 --dry-run 表示模拟输出不实际执行

```shell
git clean -xdn
```

执行实际删除，-f 强制执行

```shell
git clean -xdf
```



## git stash

> https://www.cnblogs.com/zndxall/archive/2018/09/04/9586088.html
> https://stackoverflow.com/questions/1910082/git-stash-apply-version

显示 stash 列表

```sh
git stash list
```

使用最近的提交日志内容作为暂存标识暂存修改

```sh
git stash
```

指定暂存的名称

```sh
git stash save "test1"
```

应用最近一次暂存

```sh
git stash apply
```

应用指定的暂存，0为暂存的索引

```sh
git stash apply 0
```



## git clone、pull、push时指定用户名和密码

> https://stackoverflow.com/questions/11506124/how-to-enter-command-with-password-for-git-pull

```
# 克隆仓库
git clone http://root:token-string-here123456@localhost/root/demo-devops.git

# stage所有文件
git add .
# 提交
git commit -m 'init commit'

# 推送提交到远程
git push http://root:token-string-here123456@localhost/root/demo-devops.git --all

# 拉取代码
git pull http://root:token-string-here123456@localhost/root/demo-devops.git
```



## git使用http、socks5代理加速

> https://gist.github.com/goncha/4591538

```
# 全局方式配置http.proxy
git config --global http.proxy 'socks5h://192.168.3.29:10080'
git config --global https.proxy 'socks5h://192.168.3.29:10080'
git config --global --unset http.proxy
git config --global --unset https.proxy

# 显示config配置
https://stackoverflow.com/questions/12254076/how-do-i-show-my-global-git-configuration
git config --list

# 每次clone指定http.proxy
# https://stackoverflow.com/questions/15227130/using-a-socks-proxy-with-git-for-the-http-transport
git clone https://github.com/cmu-db/benchbase.git --config 'http.proxy=socks5://192.168.1.55:1080'
```



## fork仓库

### 参考资料

https://docs.github.com/en/pull-requests/collaborating-with-pull-requests/working-with-forks/fork-a-repo

### NOTE

目前工作中暂时没有用到此特性，所有不深入研究。



## git 显示和替换远程地址

显示远程地址

```sh
git remote -v
```

替换远程地址

```sh
git remote set-url origin https://github.com/username/new-repo.git
```
