## 测试命令

```sh
time curl -sS -o 1.json "http://localhost:8090/api/v1/bpkc-mxb/random?limit=50000"

time curl -sS -o 1.json "http://localhost:8080/api/v1/external/bpkc-mxb/random?limit=50000"
```

## k6 压测（consumer 外部接口）

```sh
# 默认：16 VU、10m、limit=50000
k6 run k6-bpkc-mxb-random.js

# 可调参数
VUS=5 DURATION=1m LIMIT=50000 BASE_URL=http://localhost:8080 k6 run k6-bpkc-mxb-random.js
```

