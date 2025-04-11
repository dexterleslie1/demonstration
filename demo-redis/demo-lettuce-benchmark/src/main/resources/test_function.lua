#!lua name=mylib

local function my_test1(keys, args)
  local uuidStr=args[1]
  redis.call("set", uuidStr, uuidStr)
  local uuidStrResult=redis.call("get", uuidStr)
  return uuidStrResult
end

redis.register_function('my_test1', my_test1)
