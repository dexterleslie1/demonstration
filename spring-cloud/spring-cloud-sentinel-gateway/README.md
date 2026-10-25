# Spring Cloud Gateway + Sentinel API 分组并发限流

| 分组 | 匹配 | 并发 |
|------|------|------|
| group-a | 精确 `/api/v1/a/exact` **+** 前缀 `/api/v1/a/pattern/**` | `p1=v2` → **3**，否则 → **2** |
| group-b | 前缀 `/api/v1/b/**` | 3 |

后端 sleep 10s。group-a 按 URL 参数 `p1` 分阈值，两组互不影响。

## 调试 group-a 默认并发=2（p1 非 v2）

```bash
curl "http://localhost:8080/api/v1/a/exact?p1=v1" &
curl "http://localhost:8080/api/v1/a/exact?p1=v1" &
curl "http://localhost:8080/api/v1/a/exact?p1=v1" &
wait
```

预期：约 2 个成功，1 个被限流。

## 调试 group-a p1=v2 并发=3

```bash
curl "http://localhost:8080/api/v1/a/exact?p1=v2" &
curl "http://localhost:8080/api/v1/a/exact?p1=v2" &
curl "http://localhost:8080/api/v1/a/exact?p1=v2" &
curl "http://localhost:8080/api/v1/a/exact?p1=v2" &
wait
```

预期：约 3 个成功，1 个被限流。

## 调试 group-b（并发=3）

```bash
curl http://localhost:8080/api/v1/b/demo &
curl http://localhost:8080/api/v1/b/demo &
curl http://localhost:8080/api/v1/b/demo &
curl http://localhost:8080/api/v1/b/demo &
wait
```
