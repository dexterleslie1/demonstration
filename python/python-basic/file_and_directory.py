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

# 获取当前执行的python文件绝对路径
varFile = __file__
varIndex = varFile.rindex("/")
varDirectory = varFile[:varIndex]
varFullPath = os.path.dirname(os.path.realpath(__file__))
assert varDirectory == varFullPath, varDirectory + " 不等于 " + varFullPath

# 获取当前执行python文件的工作目录
# https://stackoverflow.com/questions/5137497/find-the-current-directory-and-files-directory
varCurrentWorkingDirectory = os.getcwd()
assert varCurrentWorkingDirectory == varFullPath, varCurrentWorkingDirectory + " 不等于 " + varFullPath

# 复制文件
# https://stackoverflow.com/questions/123198/how-to-copy-files
import shutil

shutil.copyfile("tmp1/file_temporary.tmp", "tmp1/file_temporary2.tmp")
assert os.path.exists("tmp1/file_temporary2.tmp")
