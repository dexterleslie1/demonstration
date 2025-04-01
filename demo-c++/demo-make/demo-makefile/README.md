## makefile

### 规则

一个 Makefile 文件由一系列规则组成，一个规则的语法如下：

```makefile
targets: prerequisites
	command
	command
	command
```

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



### 操作符号

参考
makefile-variable-assignment makefile文件

测试 ?= 操作符号

```shell
make -f makefile-variable-assignment test
```

测试普通变量  = 操作符号

```shell
make -f makefile-variable-assignment test1
```



### ifeq else 语句

参考
makefile-ifelse-statement makefile文件

运行测试

```shell
make -f makefile-ifelse-statement test
```



### .PHONY 伪目标的用法

**参考**

https://blog.csdn.net/diaoqu4574/article/details/102222886?app_version=6.2.6&csdn_share_tail=%7B%22type%22%3A%22blog%22%2C%22rType%22%3A%22article%22%2C%22rId%22%3A%22102222886%22%2C%22source%22%3A%22dexterchan%22%7D&utm_source=app

**作用**

PHONY 目标并非实际的文件名：只是在显式请求时执行命令的名字。有两种理由需要使用PHONY 目标：避免和同名文件冲突，改善性能。

所谓的PHONY这个单词就是伪造的意思，makefile中将.PHONY放在一个目标前就是指明这个目标是伪文件目标，如下：
.PHONY: clean，这里clean目标没有依赖文件，如果执行make命令的目录中出现了clean文件，由于其没有依赖文件，所以它永远是最新的，所以根据make的规则clean目标下的命令是不会被执行的。

**演示例子**

Makefile 内容如下：

```makefile
obj = 1.c 2.c 3.c 4.c

all:
	touch $(obj)

clean:
	rm -rf $(obj)
```

执行 make

```shell
make
```

执行 make clean

```shell
make clean
```

这个Makefile中all目标是创建空的1.c 2.c 3.c 和4.c 。 clean目标是删除这些文件，但是如果当前目录中出现了一个clean文件，在执行make clean时就不会在执行clean目标下的命令了而报告 "make: 'clean' is up to date."。

```shell
touch clean
make clean
```

在clean目标前加上.PHONY之后 Makefile 内容如下：

```makefile
obj = 1.c 2.c 3.c 4.c

all:
	touch $(obj)

.PHONY: clean

clean:
	rm -rf $(obj)
```

设置 clean 目标为 .PHONY 后再执行 make clean 命令就不再会报告错误了

```shell
make clean
```



### makefile 中 echo 和 @echo 区别

参考 makefile-echo-and-@echo-diff 或者以下链接
https://blog.csdn.net/linuxweiyh/article/details/84673489

运行测试对比  echo 和 @echo  区别，运行 echo 测试

```shell
make -f makefile-echo-and-@echo-diff echo_test
```

运行 @echo 测试

```shell
make -f makefile-echo-and-@echo-diff echo_test_with_address
```



### 综合实战

#### 模仿 kubebuilder 的 makefile

参考 demo-makefile-kubebuilder demo

运行 demo

```shell
make
```

