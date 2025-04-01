-- 演示变量相关知识点：
--      * 局部变量和全局变量

-- 有local定义的变量为局部变量，只能在其作用域中引用
-- 没有local定义的变量为全局变量，能够在这个程序中引用
-- https://www.codecademy.com/resources/docs/lua/variables
count = 2

function addValue (val)
  local count = 1
  return count + val
end

-- 返回6
print("局部变量count:" .. tostring(addValue(5)))
-- 返回2
print("全局变量count:" .. tostring(count))