-- 演示逗号分隔字符串
-- https://gist.github.com/akornatskyy/63100a3e6a971fd13456b6db104fb65b

local str = "888, 999, 000"
local fields = {}
for field in str:gmatch('([^,]+)') do
	fields[#fields+1] = field
end
print("last element:" .. fields[#fields])

-- 获取第一个元素
local firstEle = nil
for field in str:gmatch('([^,]+)') do
	firstEle = field
	break
end
print("first element:" .. firstEle)

-- 演示替换字符串中的空格
-- https://stackoverflow.com/questions/10460126/how-to-remove-spaces-from-a-string-in-lua

str = str:gsub("%s+", "")
print("被替换空格后的字符串:" .. str)

-- 演示字符串替换函数
-- http://lua-users.org/wiki/StringLibraryTutorial
local strForTesting = "count#888888"
local strForTestingReplaceBefore = strForTesting
strForTesting = strForTesting:gsub("count#", "")
print("替换前：" .. strForTestingReplaceBefore .. "，替换后：" .. strForTesting)

-- 转换布尔类型到字符串类型
-- https://stackoverflow.com/questions/6615572/how-to-format-a-lua-string-with-a-boolean-variable
local booleanVariable = true
print("布尔变量值为： " .. tostring(booleanVariable))
