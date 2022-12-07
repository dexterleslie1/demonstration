#!/usr/bin/python

var_int = 100
var_float = 1000.0
var_string = "Dexterleslie"

print(var_int)
print(var_float)
print(var_string)

# List type variable demonstration
var_list = ["Dexter", 100, 7.835, "leslie"]

print("Print all elements: " 		      + str(var_list))
print("Print fist element: " 		      + var_list[0])        # Print list first element
print("Print from index 1 to 2 elements: "    + str(var_list[1:3])) # Print from index 1 to 2 elements
print("Print from index 2 to last elements: " + str(var_list[2:]))  # Print from index 2 to last elements

# Dictionary type variable demonstration
var_dict = {"k1":"v1", "k2":"v2"}
var_dict["key1"] = "Dexter"
var_dict[3] = 4.56

print("Print all dictionary: " + str(var_dict))
print("Print key=key1 value: " + var_dict["key1"])
print("Print key=3 value: " + str(var_dict[3]))
print("Print all keys: " + str(var_dict.keys()))
print("Print all values: " + str(var_dict.values()))
