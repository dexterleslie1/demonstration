import logging


def test_console():
    # format日志格式里的asctime表示输出日志时间
    logging.basicConfig(level=logging.DEBUG, datefmt="%Y-%m-%d %H:%M:%S",
                        format="%(asctime)s %(name)s:%(levelname)s:%(message)s")
    logging.debug("测试debug")


def test_log_exception():
    logging.basicConfig(format="%(asctime)s %(name)s:%(levelname)s:%(message)s", datefmt="%d-%M-%Y %H:%M:%S",
                        level=logging.DEBUG)
    a = 5
    b = 0
    try:
        c = a / b
    except Exception as e:
        # 下面三种方式三选一，推荐使用第一种
        logging.exception("Exception occurred")
        logging.error("Exception occurred", exc_info=True)
        logging.log(level=logging.DEBUG, msg="Exception occurred", exc_info=True)


# https://blog.csdn.net/a2875254060/article/details/122411882
if __name__ == "__main__":
    # test_console()
    test_log_exception()
