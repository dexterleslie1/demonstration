-- 演示逗号分隔字符串
-- https://gist.github.com/akornatskyy/63100a3e6a971fd13456b6db104fb65b

local str = "888, 999, 000"
local fields = {}
for field in str:gmatch('([^,]+)') do
	fields[#fields+1] = field
end
print("last element:" .. fields[#fields])

-- 演示替换字符串中的空格
-- https://stackoverflow.com/questions/10460126/how-to-remove-spaces-from-a-string-in-lua

str = str:gsub("%s+", "")
print("被替换空格后的字符串:" .. str)
