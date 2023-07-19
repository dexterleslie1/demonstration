#!/usr/bin/python
# -*- coding: UTF-8 -*-

# Compare string ignore case
# https://stackoverflow.com/questions/319426/how-do-i-do-a-case-insensitive-string-comparison
var_string = "Y"
if var_string.lower() == "y":
    print("Yes")
else:
    print("No")

# region 字符串是否包含

# https://www.php.cn/faq/420013.html

var_string = '测试字符串1'
assert '字符' in var_string

assert var_string.find('字符') != -1

# endregion
