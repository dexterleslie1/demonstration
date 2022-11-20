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

### opendir、readdir、closedir实现类似ls命令

参考 demo-file-dir-ls.c

### 使用dup和dup2实现重定向

参考 demo-duplicate.c



## 进程和PCB

### fork、getpid、getppid使用

参考 demo-process-fork.c

### execl、execlp使用

参考 demo-process-exec.c