#!/usr/bin/python
# -*- coding: UTF-8 -*-

import sys

"""
if __name__ == '__main__'：的作用和原理
https://blog.csdn.net/heqiang525/article/details/89879056
一个python文件通常有两种使用方法，第一是作为脚本直接执行，第二是 import 到其他的 python 脚本中被调用（模块重用）执行。因此 if __name__ == 'main': 的作用就是控制这两种情况执行代码的过程，在 if __name__ == 'main': 下的代码只有在第一种情况下（即文件作为脚本直接执行）才会被执行，而 import 到其他脚本中是不会被执行的
"""
print("__name__={}".format(__name__))


def main(args):
	print("Cli arguments is " + str(args))


if __name__ == "__main__":
	main(sys.argv)
