#!/bin/bash

# https://blog.csdn.net/qq_40707090/article/details/123999254

# 定义变量
name=zhangsan
# 打印变量
echo name=$name

# 定义的变量值中有空格需要使用引号
name="zhang san"
echo name=$name

# 变量名称可以使用下划线开始
_name=lisi
echo _name=$_name

# 使用括号指定变量名称，否则变量名称为namegood
name=xiaohua
echo name=${name}good

# 删除变量
unset name
echo unset variable \$name, name=$name