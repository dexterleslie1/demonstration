-- 参考lua-resty-limit-traffic开源库学习封装一个自定义库
-- https://github.com/openresty/lua-resty-limit-traffic/blob/master/lib/resty/limit/req.lua

local math = require "math"

local _M = {
    _VERSION = '1.0.0'
}


local mt = {
    __index = _M
}

function _M.new(name)
    -- 定义一个局部变量self
    local self = {
        name = name,
    }

    return setmetatable(self, mt)
end

function _M.say_hello(self)
    print("Hello " .. self.name .. "!!!")
end

return _M
