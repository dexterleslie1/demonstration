-- https://www.lua.org/pil/11.1.html
-- https://www.lua.org/pil/19.1.html

-- table.getn方法在lua5.1之后被弃用，使用#获取array长度
-- https://stackoverflow.com/questions/31452871/table-getn-is-deprecated-how-can-i-get-the-length-of-an-array

local varArray = {}

-- array中插入元素
-- https://stackoverflow.com/questions/27434142/how-do-i-append-to-a-table-in-lua
table.insert(varArray, 1)

assert(#varArray == 1, "varArray长度不等于1")