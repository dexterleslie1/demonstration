# `lua`编程基础

## 变量

### 全局和局部变量

>示例的详细代码请参考 [链接](https://gitee.com/dexterleslie/demonstration/blob/master/lua/demo-variables.lua)
>
>[变量](https://www.codecademy.com/resources/docs/lua/variables)

```lua
count = 2

function addValue (val)
  local count = 1
  return count + val
end

-- 返回6
print("局部变量count:" .. tostring(addValue(5)))
-- 返回2
print("全局变量count:" .. tostring(count))
```



## 逻辑控制语句

>示例的详细代码请参考 [链接](https://gitee.com/dexterleslie/demonstration/blob/master/lua/logic-controller.lua)

### `if`语句

```lua
if 1==1 then
    print("if逻辑控制语句1=1")
end

if 1==0 then
    print("1=0")
elseif 1==1 then
    print("if elseif逻辑控制语句1=1")
end

if 1==0 then
    print("1=0")
elseif 1==2 then
    print("1==2")
else
    print("if elseif else逻辑控制语句1=1")
end
```



## 关系运算符

>示例的详细代码请参考 [链接](https://gitee.com/dexterleslie/demonstration/blob/master/lua/demo-relational-operators.lua)

### `~=`

```lua
if 1~=2 and 1~=3 then
    print("1~=2 and 1~=3")
end
```



## 函数定义和调用

>示例的详细代码请参考 [链接](https://gitee.com/dexterleslie/demonstration/blob/master/lua/%E5%AE%9A%E4%B9%89%E5%87%BD%E6%95%B0.lua)

定义函数

```lua
function sayHello()
    print("Hello Dexterleslie!")
end
```

调用函数

```lua
sayHello()
```



## 数据类型

>下面各个示例的详细代码请参考 [链接](https://gitee.com/dexterleslie/demonstration/blob/master/lua/demo-data-types.lua)
>
>[`table`数据类型用法参考](https://www.runoob.com/lua/lua-tables.html)

### `table`类型

```bash
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
```



### `table`类型当作数组类型

```bash
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
```



### `table`类型当作字典类型

```bash
-- 演示table数据类型作为字典的用法
local dict = {k1="v1", k2="v2"}
dict["k3"] = "v3"
print("dict[\"k1\"]=" .. dict["k1"])
print("dict[\"k3\"]=" .. dict["k3"])
```



## 逻辑运算符（`logic operators`）

>下面各个示例的详细代码请参考 [链接](https://gitee.com/dexterleslie/demonstration/blob/master/lua/demo-logic-operators.lua)
>
>[官方逻辑运算符手册](https://www.lua.org/pil/3.3.html)

### `or`

```lua
-- 变量设置初始值为nil，逻辑运算符认为是false
-- local variable1 = nil
-- 设置为false，逻辑运算符认为是false
-- local variable1 = false
-- 非nil和false以外的值，逻辑运算符认为是true
-- local variable1 = true
local result = variable1 or variable2 or '变量1和变量2都没有设置初始值'
print('result: ' .. tostring(result))
```



### `and`

```lua
-- 演示and用法
if 1~=2 and 1~=3 then
    print("1~=2 and 1~=3")
end
```



## 生成`uuid`

安装`luarocks`，可以使用`LuaRocks`（Lua的包管理器）来安装第三方库，比如`lua-uuid`

```bash
sudo apt install luarocks
```

搜索`uuid`相关包

```bash
sudo luarocks search uuid
```

安装`lua-uuid`第三方库

```bash
sudo luarocks install uuid
```

使用以下脚本获取`uuid`

```lua
local uuid = require('uuid')  
  
-- 生成一个随机的UUID 
local myUuid = uuid()  
print(myUuid)
```

执行脚本

```bash
lua test.lua
```



## 字符串（string）相关

>下面各个示例的详细代码请参考 [链接](https://gitee.com/dexterleslie/demonstration/blob/master/lua/demo-string.lua)

### 布尔类型转换为字符串类型

>[how-to-format-a-lua-string-with-a-boolean-variable](https://stackoverflow.com/questions/6615572/how-to-format-a-lua-string-with-a-boolean-variable)

```lua
local booleanVariable = true
print("布尔变量值为： " .. tostring(booleanVariable))
```

