# 使用`docker`运行

>[Running Locust with Docker](https://docs.locust.io/en/1.5.2/running-locust-docker.html)

`docker-compose.yaml`内容如下：

```yaml
version: '3'

# https://docs.locust.io/en/1.5.2/running-locust-docker.html
services:
  master:
    image: locustio/locust
    ports:
     - "8089:8089"
    volumes:
      - ./:/mnt/locust
    command: -f /mnt/locust/test_json_parse_perf_client.py --master -H http://master:8089

  worker:
    image: locustio/locust
    deploy:
      # 多少个worker，和cpu数量相等为宜
      replicas: 6
    volumes:
      - ./:/mnt/locust
    command: -f /mnt/locust/test_json_parse_perf_client.py --worker --master-host master

```

`test_json_parse_perf_client.py`内容如下：

```python
import json

import locust
from locust import HttpUser, task

from locust import User, between, TaskSet, task, events
import time
import uuid

# 测试json解析性能
def test_json_parse_perf():
    uuid4_str = str(uuid.uuid4())
    JSON = "{\"errorCode\":0,\"errorMessage\":null,\"dataObject\":\"你好" + uuid4_str + "\"}"
    json_object = json.loads(JSON)
    error_code = json_object.get("errorCode")
    if error_code > 0:
        register_success = False
    else:
        register_success = True
    # time.sleep(0.5)
    return register_success

# 测试客户端
class TestJsonParsePerformanceClient:

    def __getattr__(self, name):
        def wrapper(*args, **kwargs):
            start_time = time.time()
            try:
                res = test_json_parse_perf()
                # 记录测试样本
                events.request.fire(request_type="mysql",
                                    name=name,
                                    response_time=int((time.time() - start_time) * 1000),
                                    response_length=1,
                                    exception=None,
                                    context={})
            except Exception as e:
                events.request.fire(request_type="mysql",
                                    name=name,
                                    response_time=int((time.time() - start_time) * 1000),
                                    response_length=1,
                                    exception=e,
                                    context={})

                print('error {}'.format(e))

        return wrapper


class TestJsonParsePerfUser(User):
    register_success = False

    def __init__(self, parent):
        super().__init__(parent)
        self.client = TestJsonParsePerformanceClient()

    @task
    def test_json_parse_perf(self):
        register_success = self.client.execute1()

```

启动`locust`集群

```bash
docker compose up -d
```

访问`http://localhost:8089/`启动测试
