#!/usr/bin/python
# https://github.com/google/python-fire/blob/master/docs/guide.md#version-2-firefirefn
# python hello-world-version-2.py World

import fire

def hello(name):
    return "Hello {name}!".format(name=name)

if __name__ == "__main__":
    fire.Fire(hello)