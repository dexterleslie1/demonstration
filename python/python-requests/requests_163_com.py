# 接口地址
# https://www.kancloud.cn/lizhixuan/free_api/1165106

# 创建临时目录.tmp
import os

varTemporaryDirectory = ".tmp"

try:
    os.mkdir(varTemporaryDirectory)
except:
    pass

import requests
import time
import traceback

varUserAgent = "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/97.0.4692.99 Safari/537.36"

varUrl = "https://api.apiopen.top/getWangYiNews"
for varPage in range(1, 500):
    varParams = {"page": str(varPage)}
    varHeaders = {"User-Agent": varUserAgent}
    varResponse = requests.post(varUrl, headers=varHeaders, params=varParams)
    assert varResponse.status_code == 200
    for entry in varResponse.json()["result"]:
        varPath = entry["path"]
        varFilename = os.path.basename(varPath)

        if varFilename.strip() == "":
            continue

        # 已下载文件跳过
        if os.path.exists(varTemporaryDirectory + "/" + varFilename):
            continue

        varHeaders = {"User-Agent": varUserAgent}
        varResponse = requests.get(varPath, headers=varHeaders)
        if varResponse.status_code == 200:
            varFile = None
            try:
                varFile = open(varTemporaryDirectory + "/" + varFilename, "w")
                varFile.write(varResponse.text)
            except:
                traceback.print_exc()
            finally:
                if varFile is not None:
                    varFile.close()
            print("第{0}页数据，成功下载 path={1}".format(varPage, varPath))
        else:
            print("第{0}页数据，下载失败 path={1}，原因：{2}".format(varPage, varPath, varResponse.text))