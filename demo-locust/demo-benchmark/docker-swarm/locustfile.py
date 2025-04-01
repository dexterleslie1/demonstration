import json

from locust import HttpUser, task

from locust import User, between, TaskSet, task, events
import time


# https://docs.locust.io/en/stable/quickstart.html
class HelloWorldUser(HttpUser):
    # https://docs.locust.io/en/stable/writing-a-locustfile.html
    # wait_time = locust.between(1, 2)

    # https://stackoverflow.com/questions/53376427/issue-while-passing-data-from-one-task-function-to-another-in-locust-with-sequen
    register_success = False

    @task
    def test1(self):
        # https://docs.locust.io/en/stable/writing-a-locustfile.html
        with self.client.get("http://192.168.235.165", catch_response=True) as response:
            # NOTE: 需要设置catch_response=True否则response调用success和failure会报告方法不存在错误
            # https://stackoverflow.com/questions/66764483/locust-fastresponses-failure-attribute-doesnt-set-request-as-failed-in-report
            if response.status_code == 200:
                data = response.json()
                response.success()
            else:
                response.failure('预期错误')
