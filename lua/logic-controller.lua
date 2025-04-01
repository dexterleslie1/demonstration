-- 演示逻辑控制语句if、if elseif、if elseif else、for

---------------------------------------------------------------------------------------------------------------------
-- 演示if、if elseif、if elseif else
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

---------------------------------------------------------------------------------------------------------------------
-- 演示不等于 ~=
-- https://stackoverflow.com/questions/11658975/not-equal-to-this-or-that-in-lua
if 1~=2 and 1~=3 then
    print("1~=2 and 1~=3")
end

---------------------------------------------------------------------------------------------------------------------
-- 演示for语句
-- https://www.tutorialspoint.com/lua/lua_for_loop.htm
for i = 1, 3 do 
   print(i) 
end
