if __name__ == "__main__":
    # 创建list两种方式
    varList = ["Hello", "world", 30, "others"]
    print(varList)

    varList = list(["Hello", "world", 30, "others"])
    print(varList)

    # 使用索引读取指定位置元素
    print(varList[1])
    # 索引不存在会抛出IndexError错误
    # print(varList[100])

    # 查找指定元素索引
    print(varList.index("world"))
    # 查找不存在指定元素程序会抛出ValueError错误
    # print(varList.index("world1"))
    # 指定元素查找范围
    # print(varList.index("others", 1, 3))

    # 切片
    varList = [10, 20, 30, 40, 50, 60, 70, 80]
    # start:end:step
    varList2 = varList[1:5:2]
    print(varList2)

    # 判断列表是否包含或者不包含某个元素
    print("列表是否包含或者不包含某个元素".center(30, "-"))
    varList = [10, 20, 30, 40, 50, "Hello1", "Hello2"]
    print(10 in varList)
    print(25 in varList)

    print(10 not in varList)
    print(25 not in varList)
    print("元素 Hello1 是否在列表中", str("Hello1" in varList))

    # 列表元素遍历
    varList = [10, 20, "Hello1", "Hello2"]
    for item in varList:
        print(item)

    # 列表增删改

    # 列表末尾添加一个元素
    varList = [10, 20, 30, 40]
    varList.append(50)
    print(varList)

    # 列表末尾添加多个元素
    varList = [10, 20, 30, 40]
    varList.extend([50, 60])
    print(varList)

    # 列表指定位置插入元素
    varList = [10, 20, 30, 40]
    varList.insert(1, 15)
    print(varList)

    # 指定位置插入多个元素
    varList = [10, 20, 30, 40]
    varList[1:1] = [100, 200, 300, 400, 500]
    print(varList)

    # 列表使用remove删除第一个匹配的元素
    varList = [10, 20, 30, 40]
    varList.remove(30)
    print(varList)

    # 列表使用pop删除指定索引的元素
    varList = [10, 20, 30, 40]
    varList.pop(1)
    print(varList)

    # 列表使用pop删除不存在索引的元素，抛出IndexError异常
    # varList = [10, 20, 30, 40]
    # varList.pop(10)
    # print(varList)

    # pop默认删除列表最后一个元素
    varList = [10, 20, 30, 40]
    varList.pop()
    print(varList)

    # 列表切片
    varList = [10, 20, 30, 40]
    varList = varList[1:3]
    print(varList)

    # 清空列表
    varList = [10, 20, 30, 40]
    varList.clear()
    print(varList)

    # 修改指定索引元素
    varList = [10, 20, 30, 40]
    varList[1] = 100
    print(varList)

    # 修改分片多个元素
    varList = [10, 20, 30, 40]
    varList[1:2] = [100, 200, 300, 500]
    print(varList)

    # 升序排序
    varList = [45, 67, 11, 23, 101, 23]
    varList.sort()
    print(varList)

    # 降序排序
    varList = [45, 67, 11, 23, 101, 23]
    varList.sort(reverse = True)
    print(varList)

    # 内置sorted函数升序排序，会返回一个新的列表
    varList = [45, 67, 11, 23, 101, 23]
    varList = sorted(varList)
    print(varList)

    # 内置sorted函数降序排序，会返回一个新的列表
    varList = [45, 67, 11, 23, 101, 23]
    varList = sorted(varList, reverse = True)
    print(varList)

    # 获取list长度
    varList = [0]*5
    print("[0]*5的长度为：", len(varList))

    # list comprehensions
    # https://docs.python.org/2/tutorial/datastructures.html#list-comprehensions

    print("---------------- 不使用list comprehensions过滤list例子 ----------------")
    source_list = ["a", "b", "c"]
    target_list = []
    for x in source_list:
        if x == "a" or x == "c":
            target_list.append(x.upper())
    print(target_list)
    print("------------------------------------------")
    print()

    # 例子1
    print("---------------- list comprehensions 例子1 ----------------")
    target_list = [x.upper() for x in source_list if x == "a" or x == "c"]
    print(target_list)
    print("------------------------------------------")
    print()

    # 例子2
    print("---------------- list comprehensions 例子2 ----------------")
    target_list = [(x, y) for x in [1,2,3] for y in [3,1,4] if x != y]
    print(target_list)
    print("------------------------------------------")
    print()

    # 例子3
    print("---------------- list comprehensions 例子3 ----------------")
    target_list = [(i, v) for i, v in enumerate(source_list) if v == "a" or v == "c"]
    print(target_list)
    print("------------------------------------------")
    print()
