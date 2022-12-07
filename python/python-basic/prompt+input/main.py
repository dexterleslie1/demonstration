#!/usr/bin/python
# -*- coding: UTF-8 -*-

import getpass


# python2 use raw_input, python3 use input instead
var_raw_input = raw_input("Enter a raw_input variable: ")
print("raw_input variable: {}".format(var_raw_input))

# Raw input with default value
# https://stackoverflow.com/questions/5403138/how-to-set-a-default-editable-string-for-raw-input
var_raw_input = raw_input("Press enter without inputing and use default value: ") or "abc888"
print("raw_input value is {}".format(var_raw_input))

# enter password without echo
# https://www.geeksforgeeks.org/getpass-and-getuser-in-python-password-without-echo/

# getpass not working in pycharm
# https://stackoverflow.com/questions/28579468/how-to-use-the-python-getpass-getpass-in-pycharm
var_password = getpass.getpass("Enter password: ")
print("Password is {}".format(var_password))
