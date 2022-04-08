# input和raw_input的区别
# https://blog.csdn.net/salove_y/article/details/78823838

# NOTE: python3已经将raw_input命名为input
# python2中raw_input函数返回字符串类型，input函数返回为用户输入对应的数据类型，例如：输入'你好'时，返回字符串类型，输入90时，返回int类型

# NOTE: python3报告raw_input函数没有定义错误
# varRawInput = raw_input("使用raw_input读取输入数据，任何输入数据都返回为字符串类型：")
# print(type(varRawInput))

varInput = input("python3时使用input读取输入数据，任何输入数据都返回字符串类型：")
print(type(varInput))

# input默认值
varInput = input("输入一个数值，不输入则使用默认值：") or "默认值"
print(varInput)

# 提示输入密码
# https://stackoverflow.com/questions/9202224/getting-a-hidden-password-input
import getpass
varInput = getpass.getpass("输入SSH密码：")
print("输入的SSH密码是：" + varInput)