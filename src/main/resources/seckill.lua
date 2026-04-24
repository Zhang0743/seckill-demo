-- KEYS[1]: 库存key (seckill:stock:商品ID)
-- KEYS[2]: 用户购买标记key (seckill:user:用户ID:商品ID)
local stockKey = KEYS[1]
local userKey = KEYS[2]

local stock = tonumber(redis.call('get', stockKey))

if stock == nil or stock <= 0 then
    return 0  -- 库存不足
end

if redis.call('get', userKey) == '1' then
    return -1  -- 重复购买
end

redis.call('decr', stockKey)
redis.call('set', userKey, '1', 'EX', 300)
return 1  -- 成功