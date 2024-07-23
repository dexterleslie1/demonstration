# `lua`编程基础

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

