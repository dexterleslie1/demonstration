# 函数定义
def sub(a, b):
    varResult = a - b
    return varResult

# 函数调用
print("位置传参数".center(50, "-"))
varA = 5
varB = 1;
varResult = sub(varA, varB)
print("%d-%d=%d" % (varA, varB, varResult))

print()
print("关键字传参数".center(50, "-"))
varResult = sub(b = varB, a = varA)
print("%d-%d=%d" % (varA, varB, varResult))

# 函数参数默认值
print()
print("函数参数默认值".center(50, "-"))
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
print()
print("个数可变的位置形参".center(50, "-"))
def fun1(a, * args):
    print("参数a={0},args={1}".format(a, args))

fun1(1)
fun1(1, 2)

# 个数可变的关键字形参，形参类型为字典
print()
print("个数可变的关键字形参".center(50, "-"))
def fun2(a, ** args):
    print("参数a={0},args={1}".format(a, args))

fun2(1, p1 = 1)
fun2(1, p1 = 1, p2 = 2)

print()
print("个数可变的位置和关键字形参数同时存在".center(50, "-"))
def fun3(*args, **args1):
    print("参数args={0},args1={1}".format(args, args1))

fun3(1, 2, a = 3, b = 4, c = 5)

#region 返回多个值

print()
print("函数返回多个值".center(50, "-"))

# 使用元组方式返回多个值
def test_fun1():
    return ('val-a', 'val-b')
    pass

(var_a, var_b) = test_fun1()
print('var_a={0},var_b={1}'.format(var_a, var_b))
var_a = test_fun1()[0]
print('var_a={0}'.format(var_a))

#endregion

#region 参数有默认值的函数

def sum(a, b=100) :
 total = a+b
 return total

total = sum(10)
print("10+100=" + str(total))

total = sum(10,1000)
print("10+1000=" + str(total))

#endregion

