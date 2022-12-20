#!/bin/bash

# 演示使用read读取终端用户输入
# https://blog.csdn.net/qq_35273918/article/details/120820931

echo "输入varTest变量值："
read varTest
echo "varTest=$varTest"

read -p "输入varTest变量值：" varTest
echo "varTest=$varTest"

read -s -p "输入密码：" varTest
echo "密码为：$varTest"
