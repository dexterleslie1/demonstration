# 演示linux平台编程基础

## 文件open和close

参考 demo-file-open-close.c



## 文件read和write

参考 demo-file-read-write.c



## fcntl使用

参考 demo-fcntl.c

演示使用fcntl函数设置STDIN_FILENO读取为O_NONBLOCK模式



## lseek使用

参考 demo-lseek.c



## stat使用

参考 demo-stat.c



## 文件目录编程

### 判断文件是否存在

参考demo-access.c 使用 access 函数判断文件是否存在，编程手册 man 2 access

### 硬链接link和unlink使用

参考demo-link-unlink.c