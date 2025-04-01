#!/usr/bin/python

# -*- coding: UTF-8 -*-

var_name = "Dexter"
if var_name == "Dexter" :
 print("Hello Dexter")
else :
 print("Hello anonymous")

var_num = 3
if var_num == 1 :
 print("var_num=1")
elif var_num == 2 :
 print("var_num=2")
elif var_num == 3 :
 print("var_num=3")
else :
 print("var_num=4")

var_num = 9
if var_num>=0 and var_num<=10 :
 print("var_num is between 0 to 10")

# None and empty list demonstration
x = []
y = None

print("Type of x is " + str(type(x)))
print("Type of y is " + str(type(y)))

if x:
 print("x list is not empty")
else:
 print("x list is empty")

if y:
 print("y variable is not None")
else:
 print("y variable is None")
