# 导入math模块
print("导入math模块".center(50, "-"))
import math

print("math.pi={0}".format(math.pi))
print("math.pow(2, 3)={0}".format(math.pow(2, 3)))

# 导入别名
print()
print("导入math模块别名".center(50, "-"))
import math as mathlib

print("mathlib.pi={0}".format(mathlib.pi))
print("mathlib.pow(2, 3)={0}".format(mathlib.pow(2, 3)))

# 导入函数、变量、类
print()
print("导入函数、变量、类".center(50, "-"))

# 从math库导入pi常量
from math import pi
# 从math库导入pow函数
from math import pow

print("pi={0}".format(pi))
print("pow(2, 3)={0}".format(pow(2, 3)))

# 导入自定义模块
print()
print("导入自定义模块".center(50, "-"))

import module_self_defined

print("module_self_defined.add(1, 2)={0}".format(module_self_defined.add(1, 2)))
varClassA = module_self_defined.ClassA()
varClassA.method1()