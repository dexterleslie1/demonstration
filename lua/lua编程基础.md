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

