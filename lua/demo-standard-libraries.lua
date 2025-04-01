-- 演示lua标准库用法
-- https://www.lua.org/manual/5.3/manual.html#6

---------------------------------------------------------------------------------------------------------------------
-- 演示math.random函数用法
math.randomseed(os.time())
for var=1, 5 do
    -- 返回[2, 5]之间的随机数
    local num = math.random(2, 5)
    print("random number: " ..  num) 
end  
