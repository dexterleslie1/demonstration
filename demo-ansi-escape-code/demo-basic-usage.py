#!/usr/bin/python
# -*- coding: UTF-8 -*-


import sys

# 演示ansi escape code 基本用法
def main(args):
	# github gist ANSI code介绍
    # https://gist.github.com/fnky/458719343aabd01cfb17a3a4f7296797

    #region Erase functions

    

    # 清除整个屏幕（当前terminal可视区域）
    region_start("清除整个屏幕")
    region_end()
    print("\u001b[2J")

    #endregion

    #region Cursor controls



    # 移动光标到home位置
    # NOTE: 需要单独调试，否则影响其他测试结果
    # region_start("移动光标到home位置")
    # print("xxxxxx889999")
    # print("\u001b[H")
    # region_end()

    #endregion

def region_start(title):
    print("---------------- " + title + " ----------------")


def region_end():
    print("------------------------------------------")
    print()


if __name__ == "__main__":
	main(sys.argv)
