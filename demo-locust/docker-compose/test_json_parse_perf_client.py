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
