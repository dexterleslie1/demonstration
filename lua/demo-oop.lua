-- 演示lua面向对象编程

---------------------------------------------------------------------------------------------------------------------
-- 面向对象编程入门
-- 使用table数据类型作为对象
-- 下面例子是通过一步步改进lua面向对象编程以引出冒号语法目地
-- https://www.lua.org/pil/16.html
-- https://stackoverflow.com/questions/4911186/difference-between-and-in-lua

-- 定义一个对象，对象中有withdraw方法
Account = {balance = 150}
function Account.withdraw (v)
    Account.balance = Account.balance - v
    return Account.balance
end
print("帐号余额：" .. tostring(Account.withdraw(100)))

-- 上面函数操作全局变量导致函数不通用，例如：如果全局变量Account被修改为nil，则函数就不能正常运行了
-- a = Account; Account = nil
-- a.withdraw(100.00)   -- ERROR!

-- 针对以上问题函数改进（调用函数时传递self参数）如下：
function Account.withdraw (self, v)
    self.balance = self.balance - v
    return self.balance
end
Account.balance = 150
a1 = Account; Account = nil
print("调用函数时传递self参数-帐号余额：" .. tostring(a1.withdraw(a1, 100)))   -- OK

-- 多个对象支持重复使用同一个函数
a2 = {balance=150, withdraw = a1.withdraw}
print("多个对象支持重复使用同一个函数-帐号余额：" .. tostring(a2.withdraw(a2, 100)))

-- 使用冒号语法，注意：定义和调用函数都需要使用冒号语法
Account = {balance = 150}
function Account:withdraw (v)
    self.balance = self.balance - v
    return self.balance
end
a = Account; Account = nil
print("冒号语法-帐号余额：" .. tostring(a:withdraw(100)))

-- 先使用非冒号语法定义函数，再使用冒号语法调用函数
Account = {balance = 150}
function Account.withdraw (self, v)
    self.balance = self.balance - v
    return self.balance
end
a = Account; Account = nil
print("非冒号语法定义，再冒号语法调用-帐号余额：" .. tostring(a:withdraw(100)))

---------------------------------------------------------------------------------------------------------------------
-- 演示面向对象编程的继承
-- lua面向对象编程的继承主要是借助metatable+setmetatable实现
-- https://www.cnblogs.com/huageyiyangdewo/p/17488042.html

local RectAngle = { length, width, area }

-- 创建新的RectAngle对象
function RectAngle:new(length, width)
    o = {
        -- 初始化成员变量
        length = length or 0,
        width = width or 0,
        area = length * width,
    }

    -- 继承RectAngle对象的所有属性和方法
    o = setmetatable(o, { __index = self })

    return o
end

-- RectAngle对象定义get_info方法
function RectAngle:get_info()
    return self.length, self.width, self.area
end

a = RectAngle:new(10, 20)
-- 输出：a:get_info()=   10      20      200
print("a:get_info()=", a:get_info())

b = RectAngle:new(10, 30)
-- 输出：b:get_info()=   10      30      300
print("b:get_info()=", b:get_info())
-- 输出：a:get_info()=   10      20      200
print("a:get_info()=", a:get_info())

-- 演示使用业界标准方法自定义lua库
-- 引用demo-oop-assistant库
local mylib = require('demo-oop-assistant')
local myobj = mylib.new("Dexter")
myobj:say_hello()
