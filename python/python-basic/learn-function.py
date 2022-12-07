#!/usr/bin/python

def sum(a, b=100) :
 total = a+b
 return total

total = sum(10)
print("10+100=" + str(total))

total = sum(10,1000)
print("10+1000=" + str(total))
