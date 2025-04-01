# 变量标识、类型、值
name = "11111"
print(id(name), type(name), name)

# 整数
varInteger1 = 90
varInteger2 = -7
varInteger3 = 0
print(id(varInteger1), type(varInteger1), varInteger1)
print(id(varInteger2), type(varInteger2), varInteger2)
print(id(varInteger3), type(varInteger3), varInteger3);

# 二进制整数
print("二进制整数：", 0b101111)

# 八进制整数
print("八进制整数：", 0o456)

# 十六进制整数
print("十六进制整数：", 0xefa5)

# 浮点数
varFloat1 = 2.1
print(type(varFloat1), varFloat1)

# 浮点数运算误差使用Decimal解决
varFloat1 = 1.1
varFloat2 = 2.2
print(varFloat1 + varFloat2)

from decimal import Decimal
print(Decimal("1.1") + Decimal("2.2"))

# 布尔类型
varBool1 = True
varBool2 = False
print(type(varBool1), varBool1)
print(type(varBool2), varBool2)

# 字符串单引号和双引号等价
varStr1 = "字符串"
varStr2 = '字符串'
print(type(varStr1), varStr1)
print(type(varStr2), varStr2)

# 字符串三引号
varStr3 = """第一行
第二行"""
varStr4 = '''第一行
第二行'''
print(type(varStr3), varStr3)
print(type(varStr4), varStr4)

# str类型转换
print(type(str(90)))
varName = "张三"
varAge = 30
print("我叫" + varName + "，今年" + str(varAge) + "岁")

# int类型转换
print(type(int('9900')))

# float类型转换
print(type(float('1.977777')))

# 判断变量是否已初始化
var_initialized = None
if var_initialized is None:
    print("变量未初始化")

var_initialized = "None"
if var_initialized is not None:
    print("变量已经初始化")