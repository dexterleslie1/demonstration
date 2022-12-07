#!/usr/bin/python
# https://github.com/google/python-fire/blob/master/docs/guide.md#version-1-firefire
# python hello-world-version-1.py hello World

import fire

def hello(name):
  return "Hello {name}!".format(name=name)

if __name__ == '__main__':
  fire.Fire()