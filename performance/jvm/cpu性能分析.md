# `cpu`性能分析

## `cpu`占用率过高分析

>[参考链接](https://blog.csdn.net/jwentao01/article/details/115461695)

### 分析方面

- 业务逻辑线程是否陷入死循环或者代码不够优化导致占用较高`cpu`

- 是否存在内存泄露导致频繁`GC`占用较高`cpu`



### 使用`top+jstack`分析

>分析`openjdk docker`容器中的`java`应用，需要在容器中安装`top`命令`apt install procps`，其他步骤和非容器环境一致。

确定占用`cpu`高的`java`进程`id`，`-c`显示进程启动的命令行命令

```sh
top -c
```

确定占用`cpu`高的`java`线程，`-H`表示线程模式，`-p`表示指定进程`id`，能够通过线程名称大概判断什么逻辑在消耗`cpu`资源

```sh
top -H -p [进程id]
```

转换上面获取的线程`id`为16进制显示，因为jstack需要使用线程的16进制`id`

```sh
printf "%x\n" [线程id]
```

使用`jstack`打印指定线程`id`的调用栈快照，`-A`表示`grep`匹配行向下打印30行

```sh
./jstack [进程id] | grep [线程id 16进制] -A 30
```



### 使用`arthas`分析

- 使用`arthas`的`thread`、`monitor`、`trace`命令分析`cpu`高使用率。<a href="/jvm性能/arthas使用.html#arthas安装和运行" target="_blank">具体请参考</a>
- 如果需要分析`docker`容器中的`java`应用，需要在`docker`容器内安装并运行`arthas`
