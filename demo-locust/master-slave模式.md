## `master、slave`模式

> [Distributed load generation](https://docs.locust.io/en/stable/running-distributed.html)

启动`master`

```bash
locust --master
```

启动`worker`，`--master-host`指定`locust master ip`地址

```bash
locust --worker --master-host=xxx
```

