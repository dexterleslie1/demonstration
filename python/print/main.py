# 输出整数、浮点数
print(50)
print(19.46)

# 输出字符串
print("中文字符串")

# 输出含有运算符表达式
print(5+8)

# 输出到文件中
fd = open("./1.txt", "a+")
print("内容1", file = fd)
fd.close()

# 不换行输出
print("这是", "单独", "一行")

# 换行输出
print("第一行\n第二行")
