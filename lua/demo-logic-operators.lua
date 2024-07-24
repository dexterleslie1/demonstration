-- 演示逻辑运算符and、or、not
-- https://www.lua.org/pil/3.3.html

---------------------------------------------------------------------------------------------------------------------
-- 变量设置初始值为nil，逻辑运算符认为是false
-- local variable1 = nil
-- 设置为false，逻辑运算符认为是false
-- local variable1 = false
-- 非nil和false以外的值，逻辑运算符认为是true
-- local variable1 = true
local result = variable1 or variable2 or '变量1和变量2都没有设置初始值'
print('result: ' .. tostring(result))

---------------------------------------------------------------------------------------------------------------------
-- 演示and用法
if 1~=2 and 1~=3 then
    print("1~=2 and 1~=3")
end
