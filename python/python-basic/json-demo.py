# 参考
# https://blog.csdn.net/qq_39247153/article/details/81984524
# https://www.cnblogs.com/miqi1992/p/8081244.html

import json
import datetime

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

# 转换包含datetime对象的JSON对象
# https://blog.csdn.net/t8116189520/article/details/88657533


class DateEncoder(json.JSONEncoder):
    def default(self, obj):
        if isinstance(obj,datetime.datetime):
            return obj.strftime("%Y-%m-%d %H:%M:%S")
        else:
            return json.JSONEncoder.default(self,obj)


timeNow = datetime.datetime.now()
JSONObject = {"datetimeObject": timeNow}
JSON = json.dumps(JSONObject, cls=DateEncoder)
print("包含datetime对象的JSON对象被转换为JSON字符串后 ", JSON);
