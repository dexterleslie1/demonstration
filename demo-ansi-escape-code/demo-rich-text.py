#!/usr/bin/python
# -*- coding: UTF-8 -*-

import sys
import time
import random

if __name__ == "__main__":
	
    ### 演示文字颜色

    # 红色文字颜色
    # \u001b 表示 unicode 对应的 ESC，ESC[表示ANSI escape code控制指令的开始
    # 31m表示文字前景颜色为红色
    # 0m表示ANSI escape code控制指令结束，重置清除样式
    print("---------------- 前景颜色 ----------------")
    print("\u001b[31mHelloWorld\u001b[0m")
    print("------------------------------------------")
    print()

    # 中途重置清除样式
    print("---------------- 中途重置清除样式 ----------------")
    print("\u001b[31mHello\u001b[0mWorld")
    print("------------------------------------------")
    print()

    # 基本终端8种颜色
    print("---------------- 基本终端8种颜色 ----------------")
    print("\u001b[30m A \u001b[31m B \u001b[32m C \u001b[33m D \u001b[0m")
    print("\u001b[34m E \u001b[35m F \u001b[36m G \u001b[37m H \u001b[0m")
    print("------------------------------------------")
    print()

    # 16色brighter是8色的高亮版本
    # 在8色值后面加;1，例如 30m->30;1m
    print("---------------- 16色brighter是8色的高亮版本 ----------------")
    print("\u001b[30;1m A \u001b[31;1m B \u001b[32;1m C \u001b[33;1m D \u001b[0m")
    print("\u001b[34;1m E \u001b[35;1m F \u001b[36;1m G \u001b[37;1m H \u001b[0m")
    print("------------------------------------------")
    print()

    # 256色
    print("---------------- 256色 ----------------")
    for i in range(0, 16):
        for j in range(0, 16):
            code = str(i * 16 + j)
            sys.stdout.write("\u001b[38;5;" + code + "m " + code.ljust(4))
        print("\u001b[0m")
    print("------------------------------------------")
    print()




    ### 演示背景颜色

    # 背景颜色
    print("---------------- 背景颜色 ----------------")
    print("\u001b[40m A \u001b[41m B \u001b[42m C \u001b[43m D \u001b[0m")
    print("\u001b[44m A \u001b[45m B \u001b[46m C \u001b[47m D \u001b[0m")
    print("\u001b[40;1m A \u001b[41;1m B \u001b[42;1m C \u001b[43;1m D \u001b[0m")
    print("\u001b[44;1m A \u001b[45;1m B \u001b[46;1m C \u001b[47;1m D \u001b[0m")
    print("------------------------------------------")
    print()

    # 背景256色
    print("---------------- 背景256色 ----------------")
    for i in range(0, 16):
        for j in range(0, 16):
            code = str(i * 16 + j)
            sys.stdout.write("\u001b[48;5;" + code + "m " + code.ljust(4))
        print("\u001b[0m")
    print("------------------------------------------")
    print()




    ### 演示文字修饰

    # 粗体、斜体、reversed
    print("---------------- 粗体、斜体、reversed ----------------")
    print("\u001b[1m BOLD \u001b[0m\u001b[4m Underline \u001b[0m\u001b[7m Reversed \u001b[0m")
    print("\u001b[1m\u001b[4m\u001b[7m BOLD Underline Reversed \u001b[0m")
    print("\u001b[1m\u001b[31m Red Bold \u001b[0m")
    print("\u001b[4m\u001b[44m Blue Background Underline \u001b[0m")
    print("------------------------------------------")
    print()
