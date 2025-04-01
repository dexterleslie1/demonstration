varHost = "localhost"
varPort = 8080

import json

'''
GET请求
'''
import requests

varUrl = "http://{0}:{1}/api/v1/get".format(varHost, varPort)
varHeaders = {"header1": "header1"}
varParams = {"param1": "param1"}
varResponse = requests.get(varUrl, headers = varHeaders, params = varParams)
# 设定解码为utf8 response.text = response.content.decode('utf8')
varResponse.encoding = "utf8"
assert varResponse.encoding == "utf8"
assert varResponse.status_code == 200
varJSONObject = json.loads(varResponse.text)
# 自动将JSON响应转换为python JSON对象
# varJSONObject = varResponse.json()
assert varJSONObject["errorCode"] == 0
assert varJSONObject["data"] == "param1=param1,header1=header1"

# 解码content bytes
varContentStr = varResponse.content.decode("utf8")
varJSONObject = json.loads(varContentStr)
assert varJSONObject["errorCode"] == 0
assert varJSONObject["data"] == "param1=param1,header1=header1"

# 请求头
assert varResponse.request.headers["header1"] == "header1"
# 响应头
assert varResponse.headers["Content-Type"] == "application/json"
# 请求cookie
assert varResponse.request._cookies == {}
# 响应cookie
assert varResponse.cookies == {}

'''
GET请求text/plain
'''
varUrl = "http://{0}:{1}/api/v1/1.txt".format(varHost, varPort)
varHeaders = {"header1": "header1"}
varParams = {"param1": "param1"}
varResponse = requests.get(varUrl, headers = varHeaders, params = varParams)
assert varResponse.status_code == 200
assert varResponse.headers["Content-Type"] == "text/plain;charset=UTF-8"
assert varResponse.text == "param1=param1,header1=header1"

'''
DELETE请求
'''
varUrl = "http://{0}:{1}/api/v1/delete".format(varHost, varPort)
varHeaders = {"header1": "header1"}
varParams = {"param1": "param1"}
varResponse = requests.delete(varUrl, headers = varHeaders, params = varParams)
assert varResponse.status_code == 200
assert varResponse.headers["Content-Type"] == "application/json"
assert varResponse.json()["data"] == "param1=param1,header1=header1"

'''
PUT请求
'''
varUrl = "http://{0}:{1}/api/v1/putWithBody".format(varHost, varPort)
varHeaders = {"header1": "header1", "Content-Type": "application/json"}
varParams = {"param1": "param1"}
varData = {"username": "Dexter", "password": "123456", "verificationCode": "111111"}
varResponse = requests.put(varUrl, headers = varHeaders, params = varParams, data = json.dumps(varData))
assert varResponse.status_code == 200
assert varResponse.headers["Content-Type"] == "application/json"
assert varResponse.json()["data"] == "username=Dexter,password=123456,verificationCode=111111,header1=header1,param1=param1"

'''
POST请求
'''
varUrl = "http://{0}:{1}/api/v1/postWithBody".format(varHost, varPort)
varHeaders = {"header1": "header1", "Content-Type": "application/json"}
varParams = {"param1": "param1"}
varData = {"username": "Dexter", "password": "123456", "verificationCode": "111111"}
varResponse = requests.post(varUrl, headers = varHeaders, params = varParams, data = json.dumps(varData))
assert varResponse.status_code == 200
assert varResponse.headers["Content-Type"] == "application/json"
assert varResponse.json()["data"] == "username=Dexter,password=123456,verificationCode=111111,header1=header1,param1=param1"