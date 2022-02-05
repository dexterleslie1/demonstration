# 参考
# https://www.runoob.com/python/python-files-io.html
# https://www.cnblogs.com/mengqingjian/articles/9074077.html

# 创建目录
import os
try:
    os.mkdir("tmp1")
except:
    pass

# 打开和关闭文件
import traceback
varFile = None
try:
    varFile = open("tmp1/file_temporary.tmp", "w")
    varFile.write("文件内容")
except:
    traceback.print_exc()
finally:
    if varFile is not None:
        varFile.close()

# 判断目录是否已存在
assert os.path.exists("tmp1")
assert not os.path.exists("tmp111")

assert os.path.exists("tmp1/file_temporary.tmp")
assert not os.path.exists("tmp1/file_temporary.tmp1")

