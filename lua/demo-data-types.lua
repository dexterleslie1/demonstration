-- 演示不同数据类型用法

-- 演示table数据类型用法
-- https://www.runoob.com/lua/lua-tables.html
-- 简单的 table
mytable = {}
print("mytable 的类型是 ",type(mytable))

mytable[1]= "Lua"
mytable["wow"] = "修改前"
print("mytable 索引为 1 的元素是 ", mytable[1])
print("mytable 索引为 wow 的元素是 ", mytable["wow"])

-- alternatetable和mytable的是指同一个 table
alternatetable = mytable

print("alternatetable 索引为 1 的元素是 ", alternatetable[1])
print("alternatetable 索引为 wow 的元素是 ", alternatetable["wow"])

alternatetable["wow"] = "修改后"

print("mytable 索引为 wow 的元素是 ", mytable["wow"])

-- 释放变量
alternatetable = nil
print("alternatetable 是 ", alternatetable)

-- mytable 仍然可以访问
print("mytable 索引为 wow 的元素是 ", mytable["wow"])

mytable = nil
print("mytable 是 ", mytable)

-------------------------------------------------------------------------------------------------------------------
-- 演示table数据类型作为数组的用法
-- https://www.lua.org/pil/11.1.html
-- https://www.lua.org/pil/19.1.html

-- table.getn方法在lua5.1之后被弃用，使用#获取array长度
-- https://stackoverflow.com/questions/31452871/table-getn-is-deprecated-how-can-i-get-the-length-of-an-array

fruits = {"banana","orange","apple"}

-- 在末尾插入
table.insert(fruits,"mango")
print("索引为 4 的元素为 ",fruits[4])

-- array中插入元素
-- https://stackoverflow.com/questions/27434142/how-do-i-append-to-a-table-in-lua
-- 在索引为 2 的键处插入
table.insert(fruits,2,"grapes")
print("索引为 2 的元素为 ",fruits[2])

print("最后一个元素为 ",fruits[5])
table.remove(fruits)
print("移除后最后一个元素为 ",fruits[5])
assert(#fruits == 4, "varArray长度不等于4")

-------------------------------------------------------------------------------------------------------------------
-- 演示table数据类型作为字典的用法
local dict = {k1="v1", k2="v2"}
dict["k3"] = "v3"
print("dict[\"k1\"]=" .. dict["k1"])
print("dict[\"k3\"]=" .. dict["k3"])
