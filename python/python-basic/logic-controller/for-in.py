for item in "Python":
    print(item)

for item in range(3):
    print(item)

# 循环中不使用的变量可以使用 "_" 代替
for _ in range(2):
    print("学好python")

'''
1-100之间偶数求和
'''
varSum = 0
for item in range(1, 100):
    if item%2 == 0:
        varSum = varSum + item

print("1-100之间偶数和：" + str(varSum))