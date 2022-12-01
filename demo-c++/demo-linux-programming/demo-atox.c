//
// Created by dexterleslie on 22-11-30.
//

#include <stdio.h>
#include <stdlib.h>

// 演示atoi、atol、atoll函数使用
int main() {
    char *ptrStr1 = "-100aa";
    char *ptrStr2 = "a23";
    char *ptrStr3 = "356";

    int number1 = atoi(ptrStr1);
    int number2 = atoi(ptrStr2);
    int number3 = atoi(ptrStr3);
    printf("%s convert to integer using by atoi result is %d\n", ptrStr1, number1);
    printf("%s convert to integer using by atoi result is %d\n", ptrStr2, number2);
    printf("%s convert to integer using by atoi result is %d\n", ptrStr3, number3);

    return 0;
}