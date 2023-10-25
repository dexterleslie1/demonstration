#!/bin/bash

# 演示ANSI escape code用法

# ANSI escape code基本语法
# https://codehs.com/tutorial/andy/ansi-colors

# github gist ANSI code介绍
# https://gist.github.com/fnky/458719343aabd01cfb17a3a4f7296797

# ASCII 码表
# https://www.asciitable.com/

#region 基本语法



# 每个ANSI code都会以ESC（十六进制: \x1b、八进制: \033、unicode: \u001b）开始表示后面紧跟着ANSI escape code
ESC="\x1b"
echo -e "Hello Dexterleslie1!"
# 表示清除屏幕导致上面输出被清除
echo -e "$ESC[2J"
echo -e "Hello Dexterleslie2!"



# 表示使用红色前景色输出文本
echo -e "$ESC[31m.•*•.•*•.•*•."



# 使用不同的前景颜色制作彩虹字效果
RED="$ESC[31m"
YELLOW="$ESC[33m"
GREEN="$ESC[32m"
BLUE="$ESC[34m"
PURPLE="$ESC[35m"
echo -e "$RED•$YELLOW•$GREEN•$BLUE•$PURPLE•"
# [0m表示重置当前所有颜色和样式，否则Cool!!!!会变为紫色
echo -e "$ESC[0mCool!!!!!!"




# 使用彩色背景色
RED="$ESC[41m"
YELLOW="$ESC[43m"
GREEN="$ESC[42m"
BLUE="$ESC[44m"
PURPLE="$ESC[45m"
echo -e "$RED•$YELLOW•$GREEN•$BLUE•$PURPLE•$ESC[0m"





# 多个ANSI code
echo -e "$ESC[35;3mHello $ESC[33;1mDexterleslie$ESC[0m!!!!!"


#endregin

