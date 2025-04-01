#!/usr/bin/python
# https://github.com/google/python-fire/blob/master/docs/guide.md#version-4-firefireclass
# python multiple-commands-version-1.py add 10 20 --offset=0
# python multiple-commands-version-2.py multiply 10 20 --offset=0

import fire

class Calculator(object):
    def __init__(self, offset=1):
        self._offset = offset

    def add(self, x, y):
        return x+y+self._offset

    def multiply(self, x, y):
        return x*y+self._offset

if __name__ == "__main__":
    fire.Fire(Calculator)