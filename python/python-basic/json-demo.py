# 参考
# https://blog.csdn.net/qq_39247153/article/details/81984524
# https://www.cnblogs.com/miqi1992/p/8081244.html

import json

# python对象转换为JSON字符串，dumps函数使用
varDict = {"name": "Dexter", "age": 30}
varJSON = json.dumps(varDict)
varJSONDict = varJSON
assert "{\"name\": \"Dexter\", \"age\": 30}" == varJSON

varList = [1, 2, 3]
varJSON = json.dumps(varList)
varJSONList = varJSON
assert "[1, 2, 3]" == varJSON

varTuple = (1, 2, 3)
varJSON = json.dumps(varList)
varJSONTuple = varJSON
assert "[1, 2, 3]" == varJSON

# JSON转换为python对象，loads函数使用
varDictExpected = {"name": "Dexter", "age": 30}
varDict = json.loads(varJSONDict)
assert varDictExpected == varDict

varListExpected = [1, 2, 3]
varList = json.loads(varJSONList)
assert varListExpected == varList