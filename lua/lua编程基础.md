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

