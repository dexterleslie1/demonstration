#!/usr/bin/python
# https://github.com/google/python-fire/blob/master/docs/guide.md#version-2-firefiredict
# python multiple-commands-version-1.py add 10 20
# python multiple-commands-version-2.py multiply 10 20

import fire

def add(x, y):
    return x+y

def multiply(x, y):
    return x*y

if __name__ == "__main__":
    fire.Fire({
        "add_alias": add,
        "multiply_alias": multiply
    })