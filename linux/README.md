# linux相关

## shell相关

### bash shell设置自动补全

参考
https://www.pix-art.be/post/bash-history-completion

STEP 1: Create .inputrc
vim ~/.inputrc

STEP 2: Paste the following code
"\e[A": history-search-backward
"\e[B": history-search-forward
set show-all-if-ambiguous on
set completion-ignore-case on

STEP 3: Open a new window

STEP4: Testing
10 commands ago I executed this command "vim ~/.inputrc". Now I would like to repeat it but don't want to look it up in my history or type it all out again. I can now type "vi" and press Arrow Up. And it will auto complete with the last known command that started with "vi" in my case "vim ~/.inputrc".

### shell快捷键

ctr+a 快速回到行首

ctrl+e 快速回到行尾

command+t 打开新的tab

command+w 关闭当前tab

ctrl+tab tab之间切换



## 操作系统命令

### ln命令

**硬链接使用**

https://www.cnblogs.com/su-root/p/9949807.html

硬链接应用：

- 文件备份：为了防止重要的文件被误删，文件备份是一种好的办法，但拷贝文件会带来磁盘空间的消耗。硬链接能不占用磁盘空间实现文件备份。
- 文件共享：多人共同维护同一份文件时，可以通过硬链接的方式，在私人目录里创建硬链接，每个人的修改都能同步到源文件，但又避免某个人误删就丢掉了文件的问题。
- 文件分类：不同的文件资源需要分类，比如某个电影即是的分类是外国、悬疑，那我们可以在外国的文件夹和悬疑的文件夹里分别创建硬链接，这样可以避免重复拷贝电影浪费磁盘空间。有人可能说，使用软链接不也可以吗？是的，但不太好。因为一旦源文件移动位置或者重命名，软链接就失效了。

```shell
# 硬链接指向的是节点(inode)
# 创建普通文件
echo "hello" > 1.txt
# 创建硬链接
ln 1.txt 1.txt.hard
# 查看文件硬链接数，源文件和硬链接的硬链接数为2
ls -l
# 查看文件的inode，硬链接对应的inode和源文件一样
ls -li
# mv源文件，文件移动和重命名不会影响硬链接，因为硬链接指向的inode没有发生变化
mv 1.txt 2.txt
# 删除源文件，源文件删除不会导致硬链接断开，只会导致硬链接书减少1
rm 1.txt
# 硬链接只能够链接文件，不能链接目录
# 硬链接只能够链接相同的文件系统，因为不同的文件系统有不同的inode table
```

**符号链接使用**

符号链接应用：

- 快捷方式：对于路径很深的文件，查找起来不太方便。利用软链接在桌面创建快捷方式，可以迅速打开并编辑文件。
- 灵活切换程序版本：对于机器上同时存在多个版本的程序，可以通过更改软链接的指向，从而迅速切换程序版本。这里提到了python版本的切换可以这么做。
- 动态库版本管理：不是很懂，具体可以看这里。

```shell
# 软链接指向的是路径(path)
# 创建普通文件
echo "hello" > 1.txt
# 创建符号链接
ln -s 1.txt 1.txt.soft
# 查看符号链接执行的源文件
ls -l
# 查看文件的inode，符号链接和源文件的inode不一样
ls -li
# 删除和移动源文件，会导致符号链接失效，因为符号链接指向文件路径的
# 符号链接能够链接文件和目录
# 符号链接能够链接不同的文件系统，因为符号链接指向文件的路径
```

**符号链接与硬链接的区别**

https://www.php.cn/linux-491726.html

