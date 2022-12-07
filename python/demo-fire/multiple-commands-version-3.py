#!/usr/bin/python
# https://github.com/google/python-fire/blob/master/docs/guide.md#version-3-firefireobject
# python multiple-commands-version-1.py add 10 20
# python multiple-commands-version-2.py multiply 10 20

import fire

class Calculator(object):
    def add(self, x, y):
        return x+y

    def multiply(self, x, y):
        return x*y

if __name__ == "__main__":
    calculator = Calculator()
    fire.Fire(calculator)