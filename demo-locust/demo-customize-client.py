import json

import locust
from locust import HttpUser, task

# from __future__ import absolute_import
# from __future__ import print_function
from locust import User, between, TaskSet, task, events
import time


# def create_conn(conn_string):
#     print("Connecting to MySQL")
#     return create_engine('mysql+pymysql://' + conn_string).connect()

def execute1():
    # _conn = create_conn(conn_string)
    # rs = _conn.execute(query)
    # return rs
    JSON = "{\"errorCode\":0,\"errorMessage\":null,\"dataObject\":\"你好\"}"
    json_object = json.loads(JSON)
    error_code = json_object.get("errorCode")
    if error_code > 0:
        register_success = False
    else:
        register_success = True
    # time.sleep(0.5)
    return register_success


'''
  The MySQL client that wraps the actual query
  https://stackoverflow.com/questions/76016708/locust-events-object-has-no-attribute-request-success
  https://stackoverflow.com/questions/62968986/problem-updating-locust-script-to-1-x-typeerror-init-takes-1-positional
  https://medium.com/analytics-vidhya/create-custom-clients-in-locust-io-to-test-database-performance-fd71235ece6e
'''


class MySqlClient:

    def __getattr__(self, name):
        def wrapper(*args, **kwargs):
            start_time = time.time()
            try:
                res = execute1()
                # print('Result ----------->' + str(res.fetchone()))
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


# https://docs.locust.io/en/stable/quickstart.html
# class HelloWorldUser(HttpUser):
class HelloWorldUser(User):
    # https://docs.locust.io/en/stable/writing-a-locustfile.html
    # wait_time = locust.between(1, 2)

    # https://stackoverflow.com/questions/53376427/issue-while-passing-data-from-one-task-function-to-another-in-locust-with-sequen
    register_success = False

    def __init__(self, parent):
        super().__init__(parent)
        self.client = MySqlClient()

    @task
    def test1(self):
        # JSON = "{\"errorCode\":0,\"errorMessage\":null,\"dataObject\":\"你好\"}"
        # json_object = json.loads(JSON)
        # error_code = json_object.get("errorCode")
        # if error_code > 0:
        #     self.register_success = False
        # else:
        #     self.register_success = True

        # https://docs.locust.io/en/stable/writing-a-locustfile.html
        # with self.client.get("http://127.0.0.1:8080/api/v1/test1", catch_response=True) as response:
        #     # NOTE: 需要设置catch_response=True否则response调用success和failure会报告方法不存在错误
        #     # https://stackoverflow.com/questions/66764483/locust-fastresponses-failure-attribute-doesnt-set-request-as-failed-in-report
        #     if response.status_code == 200:
        #         response.success()
        #     else:
        #         response.failure('预期错误')

        register_success = self.client.execute1()
