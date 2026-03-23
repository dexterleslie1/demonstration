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

    // 用 printf 以十六进制打印 char buf[]
    char buf[] = {0xDE, 0xAD, 0xBE, 0xEF};
    printf("buf (hex) = ");
    for (unsigned i = 0; i < sizeof(buf); ++i) {
        printf("%02x%s", (unsigned char)buf[i], (i + 1 == sizeof(buf)) ? "" : " ");
    }
    printf("\n");

    return 0;
}
