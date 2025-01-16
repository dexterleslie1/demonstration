# Lua 脚本

## 介绍

Redis 的 Lua 脚本是一种强大的功能，允许你在 Redis 服务器上执行复杂的操作，同时保证这些操作的原子性。这意味着一旦 Lua 脚本开始执行，Redis 将确保脚本中的所有命令按顺序执行，而不会被其他客户端的命令打断。这对于实现事务、复杂的逻辑操作非常有用。

以下是一些关于 Redis Lua 脚本的基本信息和示例：

### 基本语法

Redis Lua 脚本通常使用标准的 Lua 语言语法。在 Redis 中，你可以通过 `EVAL` 命令来执行 Lua 脚本。`EVAL` 命令的基本语法如下：

```
EVAL script numkeys key [key ...] arg [arg ...]
```

- `script` 是你要执行的 Lua 脚本。
- `numkeys` 是后续参数中 key 的数量。
- `key` 是 Lua 脚本中会用到的 Redis 键。
- `arg` 是传递给 Lua 脚本的附加参数。

### 示例

#### 示例 1：简单的键值操作

假设你想通过 Lua 脚本设置一个键的值并获取它：

```lua
-- Lua 脚本
local value = redis.call('SET', KEYS[1], ARGV[1])
return redis.call('GET', KEYS[1])
```

在 Redis CLI 中执行这个脚本：

```sh
EVAL "local value = redis.call('SET', KEYS[1], ARGV[1]); return redis.call('GET', KEYS[1]);" 1 mykey myvalue
```

这里，`1` 表示 `KEYS` 数组有一个元素（`mykey`），`myvalue` 是传递给 `ARGV` 数组的第一个参数。

#### 示例 2：实现原子性增加和获取

假设你有一个计数器，想要原子性地增加它的值并获取增加后的值：

```lua
-- Lua 脚本
local current = tonumber(redis.call('GET', KEYS[1]) or "0")
current = current + tonumber(ARGV[1])
redis.call('SET', KEYS[1], tostring(current))
return current
```

在 Redis CLI 中执行这个脚本：

```sh
EVAL "local current = tonumber(redis.call('GET', KEYS[1]) or \"0\"); current = current + tonumber(ARGV[1]); redis.call('SET', KEYS[1], tostring(current)); return current;" 1 mycounter 1
```

这里，`1` 表示 `KEYS` 数组有一个元素（`mycounter`），`1` 是传递给 `ARGV` 数组的第一个参数，表示增加的值。

### 注意事项

1. **错误处理**：在 Lua 脚本中，你可以使用 `redis.call` 或 `redis.pcall` 来执行 Redis 命令。`redis.call` 在命令失败时会抛出错误，而 `redis.pcall` 则会捕获错误并返回错误信息。
2. **脚本缓存**：Redis 会缓存 Lua 脚本，以便在相同的脚本再次执行时，不需要重新编译。脚本的缓存是通过脚本的 SHA1 校验和来识别的。
3. **执行时间**：Lua 脚本的执行时间应该尽可能短，因为长时间的执行会阻塞 Redis 服务器，影响其他客户端的操作。

通过使用 Lua 脚本，你可以充分利用 Redis 的性能和功能，实现复杂且高效的数据操作。



## 使用 redis-cli 测试

```shell
# 使用eval函数执行lua脚本，1表示有1个key参数，redis 对应 KEYS[1], world 对应 ARGV[1]
# https://www.jianshu.com/p/4558689c13be
eval 'return "hello " .. KEYS[1] .. " " .. ARGV[1]' 1 redis world

# 使用redis.call调用redis命令，0 表示没有参数
eval 'return redis.call("set", "k1", "sv1")' 0
get "k1"

# 调用lua脚本文件
# 1.lua脚本文件内容如下：
return 'hello ' .. KEYS[1]
# 调用1.lua脚本文件，返回值hello redis
redis-cli -a 123456 --eval ./1.lua 1 redis
```



## 使用 Jedis 测试

详细用法请参考`https://gitee.com/dexterleslie/demonstration/tree/master/demo-redis/redis-jedis/demo-jedis-lua-script`

```java
public class Tests {
    @Test
    public void test() {
        try (Jedis jedis = JedisUtil.getInstance().getJedis()) {
            jedis.flushDB();

            String script = "return \"hello \" .. KEYS[1] .. \" \" .. ARGV[1]";
            Object object = jedis.eval(script, Collections.singletonList("redis"), Collections.singletonList("world"));
            Assert.assertEquals("hello redis world", object.toString());
        }
    }

    @Test
    public void testLuaScriptError() {
        try (Jedis jedis = JedisUtil.getInstance().getJedis()) {
            jedis.flushDB();

            // 脚本在hset处中断执行
            String script = "redis.call(\"set\", \"k1\", \"v1\")" +
                    "redis.call(\"set\", \"k2\", \"v2\")" +
                    "redis.call(\"hset\", \"k1\", \"v1\")" +
                    "redis.call(\"set\", \"k3\", \"v3\")";
            try {
                jedis.eval(script, Collections.EMPTY_LIST, Collections.EMPTY_LIST);
                Assert.fail("预期异常没有抛出");
            } catch (JedisDataException ignored) {
            }

            // 已经执行的命令依旧生效
            Assert.assertEquals("v1", jedis.get("k1"));
            Assert.assertEquals("v2", jedis.get("k2"));
            Assert.assertFalse(jedis.exists("k3"));
        }
    }
}
```



## 使用 RedisTemplate 测试

>`https://www.cnblogs.com/Howinfun/p/11803747.html`

详细用法请参考示例`https://gitee.com/dexterleslie/demonstration/tree/master/demo-redis/redistemplate/redistemplate-lua-script`

```java
@SpringBootTest(classes = {Application.class})
public class ApplicationTests {
    @Autowired
    StringRedisTemplate redisTemplate;

    @Test
    public void contextLoads() {
        // region 测试 Lua 脚本文件
        // 清空 db
        Objects.requireNonNull(this.redisTemplate.getConnectionFactory()).getConnection().flushDb();

        DefaultRedisScript<String> script = new DefaultRedisScript<>();
        script.setLocation(new ClassPathResource("1.lua"));
        script.setResultType(String.class);
        String str = this.redisTemplate.execute(script, Arrays.asList("k1", "k2"), "v1", "v2");
        Assertions.assertEquals("k1 k2 v1 v2", str);
        Assertions.assertEquals("v1", this.redisTemplate.opsForValue().get("k1"));
        Assertions.assertEquals("v2", this.redisTemplate.opsForValue().get("k2"));

        // endregion

        // region 测试 Lua 脚本字符串

        // 清空 db
        Objects.requireNonNull(this.redisTemplate.getConnectionFactory()).getConnection().flushDb();

        script = new DefaultRedisScript<>();
        script.setScriptText("redis.call('set', KEYS[1], ARGV[1])\n" +
                "redis.call('set', KEYS[2], ARGV[2])\n" +
                "return KEYS[1] .. ' ' .. KEYS[2] .. ' ' .. ARGV[1] .. ' ' .. ARGV[2]");
        script.setResultType(String.class);
        str = this.redisTemplate.execute(script, Arrays.asList("k1", "k2"), "v1", "v2");
        Assertions.assertEquals("k1 k2 v1 v2", str);
        Assertions.assertEquals("v1", this.redisTemplate.opsForValue().get("k1"));
        Assertions.assertEquals("v2", this.redisTemplate.opsForValue().get("k2"));

        // endregion
    }
}
```