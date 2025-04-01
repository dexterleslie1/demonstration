redis.call('set', KEYS[1], ARGV[1])
redis.call('set', KEYS[2], ARGV[2])
return KEYS[1] .. ' ' .. KEYS[2] .. ' ' .. ARGV[1] .. ' ' .. ARGV[2]
