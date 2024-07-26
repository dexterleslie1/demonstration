-- 演示metatable用法
-- https://www.runoob.com/lua/lua-metatables.html

---------------------------------------------------------------------------------------------------------------------
-- 演示__index元方法用法
-- 当你通过键来访问 table 的时候，如果这个键没有值，那么Lua就会寻找该table的metatable（假定有metatable）中的__index 键。如果__index包含一个表格，Lua会在表格中查找相应的键。
other = { foo = 3 }
myobj = {}
-- myobj没有foo属性，但是会通过__index元方法查找到other对象中的foo属性
t = setmetatable(myobj, { __index = other })
print("t.foo=" .. t.foo)
