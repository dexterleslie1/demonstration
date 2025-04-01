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

### 孤儿进程和僵尸进程

参考  demo-process-orphan.c和 demo-process-zombie.c

https://blog.csdn.net/a745233700/article/details/120715371

僵尸进程：一个进程使用 fork 创建子进程，如果子进程退出，而父进程并没有调用 wait 或 waitpid 获取子进程的状态信息，那么子进程的进程描述符仍然保存在系统中，这种进程称之为僵死进程。

孤儿进程：一个父进程退出，而它的一个或多个子进程还在运行，那么这些子进程将成为孤儿进程。孤儿进程将被 init 进程(进程号为1)所收养，并由 init 进程对它们完成状态收集工作。

僵尸进程的危害：僵尸进程虽然不占有任何内存空间，但如果父进程不调用 wait() / waitpid() 的话，那么保留的信息就不会释放，其进程号就会一直被占用，而系统所能使用的进程号是有限的，如果大量的产生僵死进程，将因为没有可用的进程号而导致系统不能产生新的进程，此即为僵尸进程的危害。

孤儿进程的危害：孤儿进程是没有父进程的进程，孤儿进程这个重任就落到了 init 进程身上，init 进程就好像是一个民政局，专门负责处理孤儿进程的善后工作。每当出现一个孤儿进程的时候，内核就把孤儿进程的父进程设置为 init，而 init 进程会循环地 wait() 它的已经退出的子进程。这样，当一个孤儿进程凄凉地结束了其生命周期的时候，init 进程就会出面处理它的一切善后工作。因此孤儿进程并不会有什么危害。

### 守护进程

参考 demo-process-daemon.c

### 使用wait和waitpid回收fork子进程PCB等相关资源

参考 demo-process-wait.c

函数作用：把僵尸进程PCB信息等相关资源及时回收

一次wait或者waitpid调用只回收一个子进程

```c
// 等待到所有子进程退出wile循环才退出
pid_t pid;
while((pid = waitpid(-1, NULL, WNOHANG)) != -1) {
    if(pid == 0) {
        sleep(1);
    } else if(pid > 0) {
        printf("等待到子进程pid=%d结束\n", pid);
    }
}
```

### 进程间通讯IPC(InterProcess Communication)

- 匿名（pipe）和命名（fifo）管道（使用最简单）
- 信号（开销最小）
- 共享内存映射区（无血缘关系）
- 本地套接字（最稳定）

#### 匿名（pipe）和命名（fifo）管道

参考 demo-process-ipc-pipe.c、demo-process-ipc-fifo-write-end.c、demo-process-ip-fifo-read-end.c

匿名管道解决有血缘关系的进程间通讯

命名管道解决没有血缘关系的进程间通讯

#### 共享内存映射区

参考 demo-process-ipc-mmap*.c

#### 信号

参考 demo-signal-*.c

## 线程

### 线程基本使用

参考  demo-pthread.c

### 线程互斥

参考 demo-pthread-mutex.c

### 线程条件变量

参考 demo-pthread-mutex-condition.c