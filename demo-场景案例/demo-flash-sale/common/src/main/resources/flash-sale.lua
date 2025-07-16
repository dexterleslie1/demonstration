local productId = ARGV[1]
local userId = ARGV[2]
local amount = tonumber(ARGV[3])

-- 判断库存是否充足
local keyStockAmount = "flash-sale-product:stockAmount:{" .. productId .. "}"
local stockAmount = tonumber(redis.call("get", keyStockAmount))
if stockAmount < amount then
    -- 库存不足
    return 1
end

-- 判断用户是否重复下单
local keyPurchaseRecord = "product{" .. productId .. "}:purchase"
local purchaseRecord = redis.call("sismember", keyPurchaseRecord, userId)
if purchaseRecord == 1 then
    -- 重复下单
    return 2
end

redis.call("decrby", keyStockAmount, amount)
redis.call("sadd", keyPurchaseRecord, userId)

return nil
