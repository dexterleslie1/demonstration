# 简书参考
# https://www.jianshu.com/p/34e9ad3de584

from functools import wraps

# 简单的装饰器
print()
print("简单的装饰器".center(50, "-"))
def decorator1(funActualP):

    @wraps(funActualP)
    def wrapperFunction(*args, **kwargs):
        print("函数执行前输出，funActualP.__name__={0}".format(funActualP.__name__))
        # TODO: 调用函数时怎么传递args参数和kwargs参数
        # funActualP(args, kwargs = kwargs)
        # print("函数执行后输出")
        return funActualP(*args, **kwargs)

    return wrapperFunction

@decorator1
def funActual(*args, **kwargs):
    print("函数执行逻辑args={0},kwargs={1},".format(args, kwargs))

funActual()
funActual(1)
funActual(1, a = 2)

# 装饰器原理
print()
print("装饰器原理".center(50, "-"))
def decoratorPrinciple(funActualP):

    def wrapperFunction():
        print("函数执行前输出")
        funActualP()
        print("函数执行后输出")

    return wrapperFunction

def funActual():
    print("函数执行逻辑")

varFunction = decoratorPrinciple(funActual)
varFunction()

#