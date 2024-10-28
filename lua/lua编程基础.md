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



### `for`语句

> [参考链接](https://www.tutorialspoint.com/lua/lua_for_loop.htm)

```lua
for i = 1, 3 do 
   print(i)
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



### `table`转换为`JSON string`

>https://stackoverflow.com/questions/24908199/convert-json-string-to-lua-table

安装`liblua5.3-dev`协助`lua-cjson`库编译

```bash
sudo apt install liblua5.3-dev
```

安装`lua-cjson`库

```bash
sudo luarocks install lua-cjson
```

用于测试`JSON`库的`lua`脚本

```lua
local json = require('cjson')
local my_table = {data = {{value1 = "test1", value2 = "test2"}, {value3 = "test3", value4 = "test4"}}}
local json_string = json.encode(my_table)
print(json_string)  -- {"data":[{"value1":"test1","value2":"test2"},{"value4":"test4","value3":"test3"}]}
local tab = json.decode(json_string)
print(tab)
```



## 标准库用法

>示例详细用法请参考 [链接](https://gitee.com/dexterleslie/demonstration/blob/master/lua/demo-standard-libraries.lua)

### `math.random`用法

```lua
-- 演示math.random函数用法
math.randomseed(os.time())
for var=1, 5 do
    -- 返回[2, 5]之间的随机数
    local num = math.random(2, 5)
    print("random number: " ..  num) 
end  
```



## `require`用法

### 多次`require`同一个文件会重复创建变量吗？

当你多次使用`require`函数来加载同一个文件（模块）时，它不会重复创建模块中的变量。Lua的`require`机制设计用来确保每个模块只被加载一次，并在后续的`require`调用中返回相同的模块对象。

Lua通过维护一个`package.loaded`表来跟踪哪些模块已经被加载。当你第一次`require`一个模块时，Lua会查找`package.loaded`表以确认该模块是否已经被加载。如果模块尚未加载，Lua会执行模块文件中的代码，并将模块对象（通常是模块文件返回的最后一个表达式的值）存储在`package.loaded`表中。如果模块已经被加载，Lua会直接返回`package.loaded`表中存储的模块对象，而不会重新执行模块文件中的代码。

因此，模块文件中的变量（包括函数、常量等）只会在模块首次加载时创建一次。这些变量将作为模块对象的一部分被存储，并在后续的`require`调用中通过返回的模块对象进行访问。

这里有一个简单的例子来说明这一点：

```lua
-- 假设我们有一个名为"mymodule.lua"的文件  
-- mymodule.lua  
local my_var = "Hello, World!"  -- 这是一个局部变量，但它是模块对象的一部分  
  
function greet()  
    return my_var  
end  
  
return {  
    greet = greet  -- 返回包含greet函数的表，这个表就是模块对象  
}  
  
-- 在另一个文件中  
local mymodule = require("mymodule")  
print(mymodule.greet())  -- 输出: Hello, World!  
  
-- 再次require同一个模块  
local mymodule2 = require("mymodule")  
print(mymodule2 === mymodule)  -- 输出: true，说明mymodule2和mymodule是同一个对象  
print(mymodule2.greet())  -- 输出: Hello, World!，使用的是相同的变量
```

在这个例子中，无论`require("mymodule")`被调用多少次，`my_var`这个局部变量都只会在`mymodule.lua`文件首次被加载时创建一次。而且，由于`mymodule.lua`返回了一个包含`greet`函数的表，这个表（即模块对象）在后续的`require`调用中会被重用。因此，`mymodule`和`mymodule2`实际上是同一个对象，它们访问的是相同的`greet`函数和`my_var`变量。



## `metatable`用法

>[`metatable`用法请参考](https://www.runoob.com/lua/lua-metatables.html)

### `__index`元方法

> 这是 metatable 最常用的键。当你通过键来访问 table 的时候，如果这个键没有值，那么Lua就会寻找该table的metatable（假定有metatable）中的__index 键。如果__index包含一个表格，Lua会在表格中查找相应的键。

详细用法请参考示例 [链接](https://gitee.com/dexterleslie/demonstration/blob/master/lua/demo-metatable.lua)

```lua
other = { foo = 3 }
myobj = {}
-- myobj没有foo属性，但是会通过__index元方法查找到other对象中的foo属性
t = setmetatable(myobj, { __index = other })
print("t.foo=" .. t.foo)
```



## 面向对象编程

### 面向对象编程继承

>`lua`面向对象编程的继承主要是借助`metatable+setmetatable`实现。

详细用法请参考 [链接1](https://gitee.com/dexterleslie/demonstration/blob/master/lua/demo-oop.lua)、[链接2](https://gitee.com/dexterleslie/demonstration/blob/master/lua/demo-oop-assistant.lua)

参考`https://www.cnblogs.com/huageyiyangdewo/p/17488042.html`学习继承的基本示例：

```lua
local RectAngle = { length, width, area }

-- 创建新的RectAngle对象
function RectAngle:new(length, width)
    o = {
        -- 初始化成员变量
        length = length or 0,
        width = width or 0,
        area = length * width,
    }

    -- 继承RectAngle对象的所有属性和方法
    o = setmetatable(o, { __index = self })

    return o
end

-- RectAngle对象定义get_info方法
function RectAngle:get_info()
    return self.length, self.width, self.area
end

a = RectAngle:new(10, 20)
-- 输出：a:get_info()=   10      20      200
print("a:get_info()=", a:get_info())

b = RectAngle:new(10, 30)
-- 输出：b:get_info()=   10      30      300
print("b:get_info()=", b:get_info())
-- 输出：a:get_info()=   10      20      200
print("a:get_info()=", a:get_info())
```

参考 [lua-resty-limit-traffic开源库](https://github.com/openresty/lua-resty-limit-traffic/blob/master/lib/resty/limit/req.lua) 学习封装一个自定义库`demo-oop-assistant`

`demo-oop-assistant.lua`代码如下：

```lua
local math = require "math"

local _M = {
    _VERSION = '1.0.0'
}


local mt = {
    __index = _M
}

function _M.new(name)
    -- 定义一个局部变量self
    local self = {
        name = name,
    }

    return setmetatable(self, mt)
end

function _M.say_hello(self)
    print("Hello " .. self.name .. "!!!")
end

return _M
```

`demo-oop.lua`代码如下：

```lua
-- 引用demo-oop-assistant库
local mylib = require('demo-oop-assistant')
local myobj = mylib.new("Dexter")
myobj:say_hello()
```

