# 参考
# https://www.runoob.com/python/python-exceptions.html

# 异常处理机制try except
import traceback
import sys


def my_except_hook(exctype, value, traceback):
    sys.__excepthook__(exctype, value, traceback)
    pass


if __name__ == "__main__":
    varA = 10
    varB = 1
    varC = "11"
    try:
        varResult = varA / varB

        # 触发ValueError
        int(varC)

    except ZeroDivisionError:
        print("{0}不能被{1}除".format(varA, varB))
        traceback.print_exc()
    except ValueError:
        print("{0}不能转换为int类型".format(varC))
        traceback.print_exc()
    except:
        print("发生异常")
        traceback.print_exc()
    else:
        # 不发生异常这里代码会被执行
        print("{0}/{1}={2}".format(varA, varB, varResult))
    finally:
        print("finally代码发生和不发生异常都会最终被执行")

    # 全局异常处理
    # https://stackoverflow.com/questions/6598053/python-global-exception-handling
    sys.excepthook = my_except_hook
    raise Exception("测试异常")
