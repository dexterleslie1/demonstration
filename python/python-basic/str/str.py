# index和find函数
varStr = "hello,hello"

print(varStr.index("lo"))
print(varStr.find("lo"))

print(varStr.rindex("lo"))
print(varStr.rfind("lo"))

# 使用index查找不存在字符串时会抛出ValueError异常
# print(varStr.index("none"))
# 使用find查找不存在字符串时返回-1
print(varStr.find("none"))

# 把字符串中所有字母转换为大写字母
varStr = "Hello world"
print(varStr.upper())

# 把字符串中所有字母转换为小写字母
varStr = "Hello world"
print(varStr.lower())

# 字符串中大写变小写，小写变大写
varStr = "Hello world"
print(varStr.swapcase())

# 字符串中首字母大写
varStr = "hello world"
print(varStr.capitalize())

# 字符串中单词首字母大写
varStr = "hello world"
print(varStr.title())

# 字符串居中对齐
varStr = "hello world"
print(varStr.center(30, "*"))

# 字符串左对齐
varStr = "hello world"
print(varStr.ljust(30, "*"))

# 字符串右对齐
varStr = "hello world"
print(varStr.rjust(30, "*"))

# zfill() 方法返回指定长度的字符串，原字符串右对齐，前面填充0。
varStr = "hello world"
print(varStr.zfill(30))

# split
varStr = "hello,world,!"
print(varStr.split(","))

varStr = "hello,world,!"
print(varStr.split(",", maxsplit=1))

# rsplit
varStr = "hello,world,!"
print(varStr.rsplit(","))

varStr = "hello,world,!"
print(varStr.rsplit(",", maxsplit=1))

# 判断字符串是否空白字符（space）
varStr = "  "
print(varStr.isspace())

# 判断字符串是否由字母组成
varStr = "Abcdefg"
print(varStr.isalpha())

# 判断字符串是否由字母或者数字组成
varStr = "Abcd99"
print(varStr.isalnum())

# 判断字符串是否由数字组成
varStr = "7899933"
print(varStr.isnumeric())

# 判断字符串是否由十进制数字组成
varStr = "788888"
print(varStr.isdecimal())

varStr = "0x456"
print(varStr.isdecimal())

# replace
varStr = "hello,python,python,python"
print(varStr.replace("python", "java"))

varStr = "hello,python,python,python"
print(varStr.replace("python", "java", 2))

# join
print(",".join(["1", "2", "3"]))

# 字符串比较
varStr = "Python"
varStr1 = "Python"
print(varStr == varStr1)
print(varStr is varStr1)

# 字符串切片
varStr = "hello,python"
print(varStr[:5] + "!" + varStr[6:])

# 字符串格式化
print("我叫%s，年龄%d" % ("Dexter", 30))

print("我叫{0}，年龄{1}，重复我叫{0}".format("Dexter", 30))

# 判断字符串是否为空
# https://blog.csdn.net/fu6543210/article/details/89607717
varStr = " "
assert varStr.strip()==""
assert len(varStr.strip())==0

# 判断字符串是否为空
var_str = " 9 "
assert var_str and var_str.strip()
