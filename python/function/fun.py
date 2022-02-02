# 函数定义
def sub(a, b):
    varResult = a - b
    return varResult

# 函数调用
varA = 5
varB = 1;
varResult = sub(varA, varB)
print("%d-%d=%d" % (varA, varB, varResult))

varResult = sub(b = varB, a = varA)
print("%d-%d=%d" % (varA, varB, varResult))

# 函数参数默认值
def sub(a, b = 1):
    varResult = a - b
    return varResult

varResult = sub(varA)
print("%d-%d=%d" % (varA, varB, varResult))

varResult = sub(varA, varB)
print("%d-%d=%d" % (varA, varB, varResult))

varResult = sub(varA, b = varB)
print("%d-%d=%d" % (varA, varB, varResult))

# 个数可变的位置形参，形参类型为元组
def fun1(* args):
    print(args)

fun1(1)
fun1(1, 2)

# 个数可变的关键字形参，形参类型为字典
def fun2(** args):
    print(args)

fun2(p1 = 1)
fun2(p1 = 1, p2 = 2)