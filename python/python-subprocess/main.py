import subprocess
import traceback

# call方法使用，执行成功返回0,执行失败返回非0
# call方法不能读取stdout和stderr
varResult = subprocess.call("ls ~", shell=True)
assert isinstance(varResult, int), "varResult类型不为int"
assert varResult == 0, "期望命令执行成功"

varResult = subprocess.call("ls1 ~", shell=True)
assert varResult != 0, "期望命令执行失败"

# run方法使用，返回subprocess.CompletedProcess
# run方法能够读取stdout和stderr
varResult = subprocess.run("ls ~", shell=True, stdout=subprocess.PIPE, stderr=subprocess.PIPE, encoding="utf-8")
assert isinstance(varResult, subprocess.CompletedProcess), "varResult类型不为CompletedProcess"
assert varResult.returncode == 0, "期望命令执行成功"
# print("varResult: ", varResult)

varResult = subprocess.run("ls1 ~", shell=True, stdout=subprocess.PIPE, stderr=subprocess.PIPE, encoding="utf-8")
assert varResult.returncode != 0, "期望命令执行失败"
# print("varResult: ", varResult)

# check=True表示命令执行错误抛出异常
try:
    subprocess.run("ls1 ~", shell=True, check=True, stdout=subprocess.PIPE, stderr=subprocess.PIPE, encoding="utf-8")
except subprocess.CalledProcessError:
    pass
else:
    raise Exception("期望异常没有抛出")

# Popen方法暂时未用到，call和run方法已经够用
