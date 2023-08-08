# git命令相关知识

## 概念

> HEAD：是指向当前分支的最新提交的指针，可以在任意分支进行切换

## git reset

> https://blog.csdn.net/zts_zts/article/details/115220786

```
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

```
# 设置git status命令显示中文
git config --global core.quotepath false
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



## 切换、创建、删除分支

```
# 显示本地所有分支
git branch

# 显示本地和远程分支
# http://gitready.com/intermediate/2009/02/13/list-remote-branches.html
git branch -r

# 切换分支
git checkout <branchname>

# 创建分支
git checkout -b <branchname>
# 把本地分支推送到远程仓库
git push --set-upstream origin kamailio-support

# 删除本地分支
git branch -D <branchname>

# 重命名当前分支为main
https://blog.csdn.net/tiantao2012/article/details/70255603
git branch -M main

# 删除远程分支
# NOTE: 如果删除远程分支提示远程分支不存在不能删除，则先执行命令同步本地和远程参考分支数据git fetch --prune origin
git push origin --delete <branchname>
```



## 初始化非空目录指向远程仓库

> https://stackoverflow.com/questions/3311774/how-to-convert-existing-non-empty-directory-into-a-git-working-directory-and-pus

```
# NOTE: 在git remote add origin使本地仓库指向远程仓库后，需要马上git pull origin main拉去远程提交到本地。在同步到远程最新代码后，再提交本地修改一切就很顺利了。

# 切换到本地非空目录
cd <localdir>

# 初始化本地非空目录为git仓库
git init

# 本地git仓库指向远程仓库
git remote add origin <url>

# 拉去远程代码到本地
git pull origin main

# 查看本地分支名称
git branch

# 重命名本地分支master为main
git branch -m master main

# stage所有本地修复
git add .

# 提交本地所有stage修改
git commit -m 'message'

# 推送本地commit到远程仓库
git push origin main
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

