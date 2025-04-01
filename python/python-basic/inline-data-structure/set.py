# set创建

# set不能包含重复的元素
varSet = {1, 2, 2, 3}
print(varSet)

# 列表转换为set
varSet = set([1, 2, 3])
print(varSet)

varSet = set({1, 2, 3})
print(varSet)

# range转换为set
varSet = set(range(6))
print(varSet)

# 判断元素是否存在
varSet = {1, 2, 3}
print(1 in varSet)
print(100 in varSet)
print(1 not in varSet)
print(100 not in varSet)

# 新增元素
varSet = {1, 56, 73}
varSet.add(11)
print(varSet)

# 新增多个元素
varSet = {1, 2, 3}
varSet.update({5, 6, 7})
print(varSet)

# 删除指定的元素
varSet = {1, 2, 3}
varSet.remove(2)
print(varSet)
# 删除指定元素如果不存在则抛出KeyError异常
# varSet.remove(100)
# print(varSet)

# 删除指定元素如果不存在也不会抛出异常
varSet = {1, 2, 3}
varSet.discard(100)
print(varSet)

# 清空
varSet = {1, 2, 3}
varSet.clear()
print(varSet)

# set是否相等
varSet = {1, 2, 3}
varSet2 = {3, 2, 1}
print(varSet == varSet2)

# 子集判断
varSet = {1, 2, 3, 4, 5}
varSet2 = {2, 3, 4}
print(varSet2.issubset(varSet))

# 超级判断
varSet = {1, 2, 3, 4, 5}
varSet2 = {2, 3, 4}
print(varSet.issuperset(varSet2))

# 是否没有交集判断
varSet = {1, 2, 3, 4, 5}
varSet2 = {6, 7, 8}
print(varSet.isdisjoint(varSet2))

varSet = {1, 2, 3, 4, 5}
varSet2 = {5, 6, 7, 8}
print(varSet.isdisjoint(varSet2))

# 求交集
varSet = {1, 2, 3, 4, 5}
varSet2 = {3, 4, 5, 6, 7, 8}
print(varSet.intersection(varSet2))
print(varSet & varSet2)

# 求并集
varSet = {1, 2, 3, 4, 5}
varSet2 = {3, 4, 5, 6, 7, 8}
print(varSet.union(varSet2))
print(varSet | varSet2)

# 求差集
varSet = {1, 2, 3, 4, 5}
varSet2 = {3, 4, 5, 6, 7, 8}
print(varSet.difference(varSet2))
print(varSet - varSet2)

# 对称差集
varSet = {1, 2, 3, 4, 5}
varSet2 = {3, 4, 5, 6, 7, 8}
print(varSet.symmetric_difference(varSet2))

