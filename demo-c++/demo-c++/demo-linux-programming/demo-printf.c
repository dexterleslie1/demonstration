//
// Created by dexterleslie on 22-11-29.
//

#include <stdio.h>

int main() {
    printf("string value is %s\n", "hello world!");

    // 格式化字符串
    char strDest[1024];
    sprintf(strDest, "format string value is %s\n", "hello world!");
    printf("%s", strDest);

    // 将字符串写入到文件描述符中
    fprintf(stdout, "stdout string value is %s\n", "hello world!");

    return 0;
}
