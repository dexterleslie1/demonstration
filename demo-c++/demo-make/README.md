## make、autoconf+automake+autotools、cmake关系

参考
https://zhuanlan.zhihu.com/p/338657327

他们本质都是产生makefile文件的工具。cmake产生的晚，解决了很多autotools工具的问题。autotools是一个工具集具有强大的灵活性，但是因为步骤太多，配置繁琐，产生了很多的替代方案，cmake是其中最优秀的之一。cmake有跨平台特性支持。

## 区分项目使用哪种编译工具make、cmake、autotools

cmake项目包含CMakeLists.txt文件

autotools项目包含configure.scan或者configure.ac、aclocal.m4、config.h.in、Makefile.am、Makefile.in、configure等文件

## makefile 的基础

参考 demo-makefile

## autotools

### 工具集合

autoscan：扫描源代码以搜寻普通的可移植性问题，比如检查编译器，库，头文件等，生成文件configure.scan,它是configure.ac的一个雏形。参考：https://blog.csdn.net/chupaokan7404/article/details/100905223

aclocal：根据已经安装的宏，用户定义宏和acinclude.m4文件中的宏将configure.ac文件所需要的宏集中定义到文件 aclocal.m4中。aclocal是一个perl 脚本程序，它的定义是：“aclocal - create aclocal.m4 by scanning configure.ac”。参考：https://blog.csdn.net/chupaokan7404/article/details/100905223

autoheader：根据configure.ac中的某些宏，比如cpp宏定义，运行m4，声称config.h.in。参考：https://blog.csdn.net/chupaokan7404/article/details/100905223

autoconf：将configure.ac中的宏展开，生成configure脚本。这个过程可能要用到aclocal.m4中定义的宏。参考：https://blog.csdn.net/chupaokan7404/article/details/100905223

automake：automake将Makefile.am中定义的结构建立Makefile.in，然后configure脚本将生成的Makefile.in文件转换为Makefile。如果在configure.ac中定义了一些特殊的宏，比如AC_PROG_LIBTOOL，它会调用libtoolize，否则它会自己产生config.guess和config.sub。参考：https://blog.csdn.net/chupaokan7404/article/details/100905223

### 使用autotools编译curl库

下载源代码

```shell
git clone https://github.com/curl/curl.git
```

安装libtool

```shell
yum install libtool
```

使用autotools自动配置项目

```shell
./buildconf
```

使用configure生成Makefile

```shell
./configure
```

编译

```shell
make
```

运行curl程序

```shell
cd src
./curl --help
```

## cmake用法

参考 demonistration/demo-make/demo-cmake-helloworld demo
