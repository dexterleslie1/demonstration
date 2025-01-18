-- 判断库存是否充足
local productId = ARGV[1]
local userId = ARGV[2]
local amount = tonumber(ARGV[3])
local keyProductStock = "product:stock:" .. productId
local productStock = tonumber(redis.call("get", keyProductStock))
if productStock < amount then
    -- 库存不足
    return 1
end

-- 判断用户是否重复下单
local keyProductPurchaseRecord = "product:purchase:" .. productId
local purchaseRecord = redis.call("sismember", keyProductPurchaseRecord, userId)
if purchaseRecord == 1 then
    -- 用户重复下单
    return 2
end

redis.call("decrby", keyProductStock, amount)
redis.call("sadd", keyProductPurchaseRecord, userId)
return nil
