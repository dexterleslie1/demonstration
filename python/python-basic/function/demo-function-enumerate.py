# https://www.programiz.com/python-programming/methods/built-in/enumerate
var_list = ["a", "b", "c"]
# 强制转换enumerate为list
var_list_enumerate = list(enumerate(var_list))
print("type(enumerate(var_list))=", type(enumerate(var_list)))
print("enumerate(var_list)=", var_list_enumerate)

# 遍历enumerate
print("---------------- 遍历enumerate1 ----------------")
for item in enumerate(var_list):
    print(item)
print("------------------------------------------")
print()

print("---------------- 遍历enumerate2 ----------------")
for i, item in enumerate(var_list):
    print("var_list[", i, "]=", item)
print("------------------------------------------")
print()
