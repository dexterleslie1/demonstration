#!/usr/bin/python

print("Demo #1")
count = 0
while count<9 :
 print("The count is: " + str(count))
 count = count + 1
print("")

print("Demo #2")
var_word = "Python"
for letter in var_word :
 print("Current letter is: " + letter)
print("")

print("Demo #3")
var_fruits = ["banana", "apple", "mango"]
for fruit in var_fruits :
 print("Current fruit is: " + fruit)
print("")


print("Demo #4")
var_fruits = ["banana", "apple", "mango"]
for index in range(len(var_fruits)) :
 print("Current fruit is: " + var_fruits[index])
print("")

print("Demo #5")
for index in range(0, 10) :
 print("Current index is: " + str(index))
print("")
