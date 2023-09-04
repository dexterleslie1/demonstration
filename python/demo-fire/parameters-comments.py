#!/usr/bin/python
# 演示函数和参数的注释
# https://github.com/google/python-fire/issues/114

import fire


def hello(param1="dexter", param2=None):
    """Hello function for demonstration

    :param param1: parameter 1
    :param param2: parameter 2
    :return: concat string with params
    """

    return "Hello {name}!".format(name=param1)


if __name__ == '__main__':
    fire.Fire()
