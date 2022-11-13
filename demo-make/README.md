# 演示make、autotools、cmake、nmake使用

## gcc

### gcc编译4步骤

- 预处理

  展开宏、头文件、替换条件编译、删除注释、空行、空白，输出hello.i

  ```shell
  gcc -E hello.c -o hello.i
  ```

-  编译

  检查语法规范，消耗时间，系统资源最多，生产汇编代码

  ```shell
  gcc -S hello.c -o hello.s
  ```

- 汇编

  将汇编指令翻译为机器指令

  ```shell
  gcc -c hello.c -o hello.o
  ```

- 链接

  将汇编二进制文件链接生成可执行文件

### gcc编译常用参数

-I 指定头文件目录

-c 只编译，生产.o文件，不进行链接

-g 包含调试信息，用于gdb调试

-O n=0~3 编译优化，n越大优化级别越高，n=0表示不优化

-Wall 提示更多警告信息

-D<DEF> 编译是定义宏，注意-D和<DEF>之间没有空格

-E 生产预处理文件

-M 生成 .c文件与头文件依赖关系以用于Makefile，包括系统库的头文件

-MM 生成 .c 文件与头文件依赖关系以用于Makefile，不包括系统的头文件
