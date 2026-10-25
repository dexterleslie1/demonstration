# Spring Cloud Gateway + Sentinel API 分组并发限流

| 分组 | 匹配 | 并发 |
|------|------|------|
| group-a | 精确 `/api/v1/a/exact` **+** 前缀 `/api/v1/a/pattern/**`（并存，共享配额） | 2 |
| group-b | 前缀 `/api/v1/b/**` | 3 |

后端 sleep 10s。两组互不影响；group-a 内精确与前缀路径共用同一并发数。

## 调试 group-a（精确+前缀共享并发=2）

精确、前缀各打若干，合计超过 2 即限流：

```bash
curl http://localhost:8080/api/v1/a/exact &
curl http://localhost:8080/api/v1/a/pattern/demo &
curl http://localhost:8080/api/v1/a/exact &
wait
```

预期：约 2 个成功，1 个返回 `{"errorCode":429,"errorMessage":"被限流了"}`。

## 调试 group-b（并发=3）

```bash
curl http://localhost:8080/api/v1/b/demo &
curl http://localhost:8080/api/v1/b/demo &
curl http://localhost:8080/api/v1/b/demo &
curl http://localhost:8080/api/v1/b/demo &
wait
```

预期：约 3 个成功，其余被限流。
