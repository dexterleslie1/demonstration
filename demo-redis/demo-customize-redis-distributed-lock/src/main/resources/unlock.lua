-- 先获取锁对应的 uuid
local lockUuid=redis.call('get', KEYS[1])
-- 判断 Redis 中的 key 是否属于本锁，是则删除该 key，否则不操作
if lockUuid == ARGV[1] then
    redis.call('del', KEYS[1])
end
