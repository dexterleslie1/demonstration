## gdb安装

```shell
# macOS安装
brew install gdb
```



## gdb使用

```shell
# 编译程序
gcc gdbdebug.c -g -o test

# 使用gdb调试程序
gdb test
```

list/l 显示源代码

list/l number 从第number行开始显示源代码

breakpoint/b linenumber 在第 linenumber 行打断点

info b 显示所有断点信息

run/r 运行程序

next/n 单步到程序源代码的下一行，不进入函数。

step/s 单步到下一个不同的源代码行（包括进入函数）。

print/p variablename 打印变量值

continue/c 继续执行程序，如果有下一个断点会在断点处停下

quit 退出gdb调试