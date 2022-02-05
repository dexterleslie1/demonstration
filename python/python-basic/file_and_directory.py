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

varFile = open("tmp1/file_temporary.tmp", "w")
varFile.write("文件内容")
varFile.close()