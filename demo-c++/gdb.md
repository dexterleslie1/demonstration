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

b 23 if argc >= 2 条件断点，当argc >= 2时

info b 显示所有断点信息

run/r 运行程序

run/r arg1 arg2 提供参数运行程序

next/n 单步到程序源代码的下一行，不进入函数。

step/s 单步到下一个不同的源代码行（包括进入函数）。

print/p variablename 打印变量值

continue/c 继续执行程序，如果有下一个断点会在断点处停下

start 从程序第一行代码开始执行，不需要设置断点

finish 相当于step out，退出当前函数执行

ptype variablename 显示变量类型

display variablename 每执行一条指令都i显示一次变量值

undisplay seqnumber 不再显示指定变量值

quit 退出gdb调试



## gdb找出段错误

```shell
# 编译段错误程序
gcc gdbdebugsegment.c -g -o test

# 使用gdb调试test程序
gdb test

# 在gdb中输入run运行程序后自动在段错误位置停止运行s
```



## gdb显示栈帧变量

调试过程中你在子调用里面时，此时通过 print 打印父级调用变量时会提示无法在当前上下问找到符号。此时通过 backtrace 显示调用栈，再通过frame traceseqnumber切换栈帧后再 print 变量即可。

```shell
# 调用gdb调试程序
gdb test

# 设置断点到子调用add函数中
b 15

# 运行程序
run

# 此时 print argc 会报告错误 No symbol "argc" in current context.

# 使用backtrace 显示调用栈
backtrace
#0  add (a=11, b=10) at gdbdebug.c:15
#1  0x00005555555551ce in main (argc=1, argv=0x7fffffffdec8) at gdbdebug.c:28

# 使用frame切换argc所在的栈帧main #1
frame 1

# 使用print argc显示变量值
print argc
```

