# 创建字典
varDict = {"name": "jack", "age": 30}
print(varDict)

# 使用dict内置函数创建字典
varDict = dict(name = "jack", age = 30)
print(varDict)

# 使用[]读取key值
varDict = {"name": "jack", "age": 30}
print(varDict["name"])

# 使用[]读取不存在key值，抛出KeyError错误
# varDict = {"name": "jack", "age": 30}
# print(varDict["name1"])

# 使用get读取key值
varDict = {"name": "jack", "age": 30}
print(varDict.get("name"))

# 使用get读取不存在key值
varDict = {"name": "jack", "age": 30}
print(varDict.get("name1"))

# 使用get读取key不存在返回默认值
varDict = {"name": "jack", "age": 30}
print(varDict.get("name1", "Dexter"))

# 判断key是否存在
varDict = {"name": "jack", "age": 30}
print("name" in varDict)
print("name" not in varDict)

# 删除指定key
varDict = {"name": "jack", "age": 30}
del varDict["name"]
print(varDict)

# 清空
varDict = {"name": "jack", "age": 30}
varDict.clear()
print(varDict)

# 新增元素
varDict = {"name": "jack", "age": 30}
varDict["sex"] = "男"
print(varDict)

# 修改元素
varDict = {"name": "jack", "age": 30}
varDict["name"] = "Dexter"
print(varDict)

# keys函数
varDict = {"name": "jack", "age": 30}
varKeys = varDict.keys()
print(list(varKeys))

# values函数
varDict = {"name": "jack", "age": 30}
varValues = varDict.values()
print(list(varValues))

# items函数
varDict = {"name": "jack", "age": 30}
varItems = varDict.items()
print(list(varItems))

# 字典遍历
varDict = {"name": "jack", "age": 30}
for varKey in varDict:
    print(varKey + "=" + str(varDict.get(varKey)))