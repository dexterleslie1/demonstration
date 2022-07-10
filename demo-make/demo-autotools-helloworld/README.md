# 演示使用autotools编译项目

## 步骤

```
# 创建configure.ac
autoscan
mv configure.scan configure.ac

# 修改configure.ac
AC_INIT(helloworld, 1.0.0, dexterleslie@gmail.com)
AM_INIT_AUTOMAKE(helloworld, 1.0.0)

AC_CONFIG_FILES([Makefile])

# 生成aclocal.m4
aclocal

# 生成config.h.in
autoheader

# 生成configure脚本
autoconf

# 创建Makefile.am
AUTOMARK_OPTIONS = foreign  
bin_PROGRAMS = helloworld
helloworld_SOURCES = helloworld.c

# 使用Makefile.am生成Makefile.in
touch NEWS
touch README
touch AUTHORS
touch ChangeLog
automake --add-missing

# 运行configure脚本，脚本会自动使用Makefile.in生成Makefile
./configure

# 编译生成helloworld程序
make

```
