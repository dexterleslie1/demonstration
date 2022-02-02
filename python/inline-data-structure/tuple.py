# 元组实质是不可变的列表

# 元组的创建
varTuple = (10, 20, 30)
print(varTuple)

# 使用内置tuple函数创建
varTuple = tuple((10, 20, 30))
print(varTuple)

# 一个元素的元组
varTuple = (10, )
print(varTuple)

# 空元组
varTuple = ()
print(varTuple)

# 获取元组元素
varTuple = (10, 20, 30)
print(varTuple[0])
print(varTuple[1])

# 元组遍历
varTuple = (10, 20, 30)
for item in varTuple:
    print(item)

