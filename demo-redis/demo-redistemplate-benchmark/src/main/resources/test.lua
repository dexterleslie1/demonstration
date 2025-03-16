local uuidStr=ARGV[1]
redis.call("set", uuidStr, uuidStr)
local uuidStrResult=redis.call("get", uuidStr)
return uuidStrResult
