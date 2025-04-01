'''
计算1-100之间偶数和
'''

varCounter = 1
varSum = 0
while varCounter<=100:
    if varCounter%2 == 0:
        varSum = varSum + varCounter
    varCounter = varCounter + 1

print("1-100之间偶数和：" + str(varSum))