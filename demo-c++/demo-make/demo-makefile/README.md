## makefile

### 规则

参考 demo-makefile-basic-rule

```shell
# 编译生成可执行文件test
make

# 清除编译生成的相关文件
make clean
```

规则原则：

- 若想生成目标，检查规则中的依赖条件是否存在，如不存在，则寻找是否有规则用来生成该依赖文件。
- 检查规则中的目标是否需要更新，必须先检查它的所有依赖，依赖中有任一个被更新，则目标必须更新。


### 函数

参考 demo-makefile-function

函数调用语法：$(functionname param1, param2...)

wildcard函数：自动匹配指定模式的文件，例如：wildcard *.c会返回当前目录的 .c 文件

patsubstr函数: 把参数3匹配参数1模式的替换为参数2

### 自动变量

参考 demo-makefile-variable

$@ 在命令中表示目标

$< 在命令中表示第一个依赖

$^ 在命令中表示所有依赖

### 模式规则

参考 demo-makefle-pattern-rule

$< 在模式规则中会自动依次取出依赖并应用模式规则

